package com.maan.eway.workflow.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.FieldQueryTablequery;
import com.maan.eway.bean.FlowFieldDetails;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.upgrade.criteria.CriteriaService;
import com.maan.eway.upgrade.criteria.SpecCriteria;
import com.maan.eway.workflow.dto.JsonField;
import com.maan.eway.workflow.dto.WorkEngine;
import com.maan.eway.workflow.util.AzentoApiService;
import com.maan.eway.workflow.util.FieldFromTuple;
import com.maan.eway.workflow.util.FieldToMapConverter;
import com.maan.eway.workflow.util.JsonModules;

import jakarta.persistence.Tuple;

@Service
public class JsonMapperFromDB {

	@Autowired
	private CriteriaService crservice;
	
	@Autowired
	private JdbcTemplate template;
	
	@Autowired
	private AzentoApiService azentoService;

	@Autowired
	private FactorRateRequestDetailsRepository repository;
	
	public Map<String,Object> createRequest(WorkEngine engine) {
		try {
			String search="companyId:"+engine.getCompanyId()+";productId:"+engine.getProductId()+";status:{Y,R};";
			SpecCriteria criteria = crservice.createCriteria(FlowFieldDetails.class, search, "keyId");
			List<Tuple> result = crservice.getResult(criteria, 0, 50);
			FieldFromTuple t=new FieldFromTuple();
			
			List<JsonField> data = result.parallelStream().map(t).filter(d-> d!=null).collect(Collectors.toList());
			
			List<BigDecimal> distinctQueryid = data.stream().filter(tq -> tq.getQueryId()!=null && tq.getQueryId().compareTo(BigDecimal.ZERO)!=0)
			.map(tx->tx.getQueryId()).distinct().collect(Collectors.toList());
			Map<String, List<Map<String, Object>>> dynamicQuery=null;
			if( distinctQueryid !=null && !distinctQueryid.isEmpty()) {
				 dynamicQuery = dynamicQuery(distinctQueryid,data);
			}
				
			JsonField structure = createStructure(data);
			List<JsonField> finalData=new ArrayList<JsonField>();
			finalData.add(structure);
			FieldToMapConverter convt=new FieldToMapConverter(dynamicQuery);
			//data.stream().forEach(convt);
			List<Map<String, Object>> collect = finalData.stream().map(convt).filter(d -> d != null).collect(Collectors.toList());
			//Map<String,Object> dataObj=frameJsonFromJsonField(data);
			Map<String,Object> obj=new HashMap<String,Object>();
			obj.put("isError", false);
			obj.put("Errors", null);
			obj.put("Data", collect);
			
			
			return obj;
 		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private JsonField createStructure(List<JsonField> data) {
		try {
			List<JsonField> headers = data.stream().filter(f-> "Yes".equals(f.getIsHeader())).collect(Collectors.toList());
			headers.sort(Comparator.comparing(JsonField::getKeyId));
			JsonModules modMapper=new JsonModules(data);
			headers.stream().forEach(modMapper);
			/*modMapper=new JsonModules(headers);
			headers.stream().forEach(modMapper);*/
			Optional<JsonField> first = headers.stream().filter(t-> (t.getKeyId().compareTo(new BigDecimal("701"))==0)).findFirst();
			if(first.get()!=null )
				return first.get();
			/*int size = headers.size();
			for(int i=0;i<size;i++) {
				JsonField header = headers.get(i);
				List<JsonField> child = headers.stream().filter(tx->(tx.getHeaderKeyid().compareTo(header.getKeyId())==0)).collect(Collectors.toList());
				if(child.size()>0)
					headers.removeAll(child);
				header.getChildField().addAll(child);
			}
			return headers;*/ 
		}catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}

	public Map<String, List<Map<String, Object>>> dynamicQuery(List<BigDecimal> distinctQueryid, List<JsonField> data) {
		try {
			Map<String, List<Map<String, Object>>> hashMap=new HashMap<String, List<Map<String, Object>>>();
			for(BigDecimal id:distinctQueryid) {
				String search="queryId:"+id.toPlainString()+";";
				SpecCriteria criteria = crservice.createCriteria(FieldQueryTablequery.class, search, "queryId");
				List<Tuple> result = crservice.getResult(criteria, 0, 50);	
				
				List<String> collect = data.stream().filter(t-> t.getQueryId().compareTo(id)==0 && t.getQueryCol()!=null && !"".equals(t.getQueryCol()) )
						.map(t-> t.getQueryCol()+" "+t.getQueryAlias()).collect(Collectors.toList());
				
				String sql="SELECT "+(collect.isEmpty()? "*": String.join(",",collect)) +" "+ result.get(0).get("sqlQuery").toString();
				
			  List<Map<String, Object>> resultList = template.queryForList(sql);			  
			  hashMap.put(id.toPlainString(), resultList);
			}
			return hashMap;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Map<String, Object>> createQuotation(WorkEngine engine) {
	/*	String result="data->riskInfo->riskDetails->riskDetailsArray->coverages";		
		String[] split = result.split("->");
		List<String> listStr = Arrays.asList(split); 
		Map<String, Object> temp=request;
		for(int i=0;i<listStr.size();i++) {
 			String keyStr = listStr.get(i);
 			if(temp.get(keyStr) instanceof ArrayList) {
 				temp = ((List<Map<String, Object>>) temp.get(keyStr)).get(0);	
 			}else
				temp = (Map<String, Object>) temp.get(keyStr);			
		}		
	 */	
		Map<String, Object> request = createRequest(engine);
		List<Map<String, Object>> a1=(List<Map<String, Object>>)request.get("Data");
		Map<String, Object> a2=(Map<String, Object>)a1.get(0).get("Root");
		Map<String, Object> response = azentoService.createQuote(engine,a2);
		
		List<Map<String, Object>> retObj=new ArrayList<Map<String,Object>>();
		Map<String, Object> data = (Map<String, Object>) response.get("data");
		Map<String, Object> summaryDetails = (Map<String, Object>) data.get("summaryDetails");
		Map<String, Object> quoteInfo = (Map<String, Object>) summaryDetails.get("quoteInfo");
		Map<String, Object> riskInfo = (Map<String, Object>) quoteInfo.get("riskInfo");
		Map<String, Object> riskDetails = (Map<String, Object>) riskInfo.get("riskDetails");
		List<Map<String, Object>> riskDetailsArray = (List<Map<String, Object>>) riskDetails.get("riskDetailsArray");
		Map<String, Object> coverages = (Map<String, Object>) riskDetailsArray.get(0).get("coverages");
		
		Map<String, Object> riskPrimaryInfo = (Map<String, Object>) riskDetailsArray.get(0).get("riskPrimaryInfo");
		
		List<Map<String, Object>> mandatoryCoveragesArray = (List<Map<String, Object>>) coverages.get("mandatoryCoveragesArray");
		
		
		List<Map<String, Object>> brResults = (List<Map<String, Object>>) summaryDetails.get("brResults");
		
		List<FactorRateRequestDetails> saveCoverList = new ArrayList<FactorRateRequestDetails>(); 
		for ( Map<String, Object> coverData  : mandatoryCoveragesArray  ) {
			FactorRateRequestDetails saveCover = new FactorRateRequestDetails(); 
			
			coverData.get(saveCoverList);
			saveCover.setRequestReferenceNo(engine.getRequestReferenceNo());
			saveCover.setSubCoverId(0);
			saveCover.setVehicleId(Integer.parseInt(riskPrimaryInfo.get("riskId").toString()));
			saveCover.setCoverId(Integer.valueOf(coverData.get("coverageCode").toString()));
			saveCover.setCurrency(riskPrimaryInfo.get("riskCurrency").toString());
			saveCover.setLocationId(Integer.valueOf(engine.getLocationId()));
			saveCover.setExchangeRate(new BigDecimal(riskPrimaryInfo.get("riskCurrencyRate").toString()));
			saveCover.setCompanyId(engine.getCompanyId());
			saveCover.setProductId(Integer.valueOf(engine.getProductId()));
			saveCover.setSectionId(Integer.valueOf(engine.getSectionId()));
			saveCover.setSubCoverYn("N");
			saveCover.setCdRefno(engine.getCdRefNo());
			saveCover.setVdRefno(engine.getVdRefNo());
			saveCover.setMsRefno(engine.getMsrefno());	
			saveCover.setDiscLoadId(0);
			saveCover.setTaxId(0);
			saveCover.setEntryDate(new Date());			
			saveCover.setCreatedBy(engine.getCreatedBy());
			saveCover.setStatus("Y");
			
			saveCover.setDependentCoverYn("N");
			saveCover.setDependentCoverId(null);
			saveCover.setIsSelected("D"); // D Default,Y -yes ,N- no
 			saveCover.setPremiumAfterDiscountFc(new BigDecimal(coverData.get("coveragePremium").toString()));
			saveCover.setPremiumBeforeDiscountFc(new BigDecimal(coverData.get("coveragePremium").toString()));
			saveCover.setPremiumExcludedTaxFc(new BigDecimal(coverData.get("coveragePremium").toString()));
			saveCover.setPremiumIncludedTaxFc(new BigDecimal(coverData.get("coveragePremium").toString()));
			saveCover.setPremiumAfterDiscountLc(new BigDecimal(coverData.get("coveragePremium").toString()));
			saveCover.setPremiumBeforeDiscountLc(new BigDecimal(coverData.get("coveragePremium").toString()));
			saveCover.setPremiumExcludedTaxLc(new BigDecimal(coverData.get("coveragePremium").toString()));
			saveCover.setPremiumIncludedTaxLc(new BigDecimal(coverData.get("coveragePremium").toString()));
				saveCover.setIsReferral((brResults==null || brResults.isEmpty())?"N":"Y" );
			saveCover.setReferralDescription((brResults==null || brResults.isEmpty())?"": brResults.get(0).get("message").toString());
			saveCover.setMultiSelectYn("N");
			saveCover.setExcessAmount(null);
			saveCover.setExcessDesc(null);
			saveCover.setExcessPercent(null);
			saveCover.setProRataYn("N");
			String userOpt="Y";
			saveCover.setRegulatoryCode("");
			saveCover.setMinimumPremiumYn("N");
			saveCover.setUserOpt(userOpt);
			saveCover.setActualRate(new BigDecimal(coverData.get("coverBasicRate").toString())); 
				
			saveCover.setCoverBasedOn("sumInsured");
			
			saveCover.setRegulSumInsured(coverData.get("vehicleValue")==null?BigDecimal.ZERO:new BigDecimal(coverData.get("vehicleValue").toString()));
			saveCover.setEndtCount(BigDecimal.ZERO);
			
			saveCover.setCoverPeriodFrom(coverData.get("coverageStartDate")==null?null:convertDate(coverData.get("coverageStartDate").toString()));  //coverageStartDate
			
			saveCover.setCoverPeriodTo(coverData.get("coverageEndDate")==null?null:convertDate(coverData.get("coverageEndDate").toString()));
			saveCover.setSumInsured(coverData.get("vehicleValue")==null?BigDecimal.ZERO:new BigDecimal(coverData.get("vehicleValue").toString()));
			saveCover.setSumInsuredLc(coverData.get("vehicleValue")==null?BigDecimal.ZERO:new BigDecimal(coverData.get("vehicleValue").toString()));;
			saveCover.setProRataPercent(new BigDecimal("100"));
			saveCover.setRegulatorySuminsured(coverData.get("vehicleValue")==null?BigDecimal.ZERO:new BigDecimal(coverData.get("vehicleValue").toString()));
			
			saveCover.setRegulatoryRate(new BigDecimal(coverData.get("coverBasicRate").toString()));
			saveCover.setCoverageLimit(new BigDecimal(coverData.get("coverageLimit").toString()));
			saveCover.setMinCoverageLimit(new BigDecimal(coverData.get("coverageLimit").toString()));
			saveCover.setIsTaxExtempted("N");
			
			saveCover.setCoverNameLocal(StringUtil.isBlank(coverData.get("coverageName").toString())?"":coverData.get("coverageName").toString());
			saveCover.setCoverDescLocal(StringUtil.isBlank(coverData.get("coverageName").toString())?"":coverData.get("coverageName").toString());
			saveCover.setSubCoverDescLocal(StringUtil.isBlank(coverData.get("coverageName").toString())?"":coverData.get("coverageName").toString());
			saveCover.setSubCoverNameLocal(StringUtil.isBlank(coverData.get("coverageName").toString())?"":coverData.get("coverageName").toString());
			saveCover.setMinimumRate(coverData.get("minRate")==null?BigDecimal.ZERO:new BigDecimal(coverData.get("minRate").toString()));
			saveCover.setMinimumRateYn("N");	
					//private BigDecimal     minCoverageLimit;
			// Date Differents
			Date periodStart =  convertDate(coverData.get("coverageStartDate").toString());
			Date periodEnd = convertDate(coverData.get("coverageEndDate").toString()) ;
			String diff = "0";
			BigDecimal NoOfDays = new BigDecimal(0);
			
			if(periodStart!=null && periodEnd!=null && !"D".equals("N")) {
				Long diffInMillies = Math.abs(periodEnd.getTime() - periodStart.getTime());
				Long daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)  + 1 ;
				
				// Check Leap Year
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
				boolean leapYear = LocalDate.parse(sdf.format(periodEnd) ).isLeapYear();
				diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );
				
				NoOfDays = new BigDecimal(diff);
				
			}
			saveCover.setNoOfDays(NoOfDays);
			
//			if(coverData.getTaxes()!=null && coverData.getTaxes().size() > 0 ) {
//				saveCover.setTax1(coverData.getTaxes().get(0).getTaxAmount()==null ? null : Double.valueOf(df.format(coverData.getTaxes().get(0).getTaxAmount())) );
//				if(coverData.getTaxes().size() > 1  ) 
//				saveCover.setTax2( coverData.getTaxes().get(1).getTaxAmount()==null ? null : Double.valueOf(df.format(coverData.getTaxes().get(1).getTaxAmount())) );
//				if(coverData.getTaxes().size() > 2  ) 
//				saveCover.setTax3(coverData.getTaxes().get(2).getTaxAmount()==null ? null : Double.valueOf(df.format(coverData.getTaxes().get(2).getTaxAmount())) );
//				
//			}
			saveCover.setDiscountCoverId(0) ;
			saveCover.setDiffPremiumIncludedTaxFc(BigDecimal.ZERO);
			saveCover.setDiffPremiumIncludedTaxLc(BigDecimal.ZERO);
			saveCover.setFreeCoverLimit(BigDecimal.ZERO);
			saveCoverList.add(saveCover);
			
		}
		repository.saveAllAndFlush(saveCoverList);
		
		retObj.add(coverages);
				return retObj;
	}
	
	private Date convertDate(String dateString) {
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	      
	        try {
	            Date date = formatter.parse(dateString);
	             return date;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    
	}

}
