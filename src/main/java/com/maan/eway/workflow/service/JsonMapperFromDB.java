package com.maan.eway.workflow.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.FieldQueryTablequery;
import com.maan.eway.bean.FlowFieldDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.upgrade.criteria.CriteriaService;
import com.maan.eway.upgrade.criteria.SpecCriteria;
import com.maan.eway.workflow.dto.JsonField;
import com.maan.eway.workflow.dto.WorkEngine;
import com.maan.eway.workflow.util.AzentoApiService;
import com.maan.eway.workflow.util.DownloadDocService;
import com.maan.eway.workflow.util.FieldFromTuple;
import com.maan.eway.workflow.util.FieldToMapConverter;
import com.maan.eway.workflow.util.JsonModules;
import com.maan.eway.workflow.util.SaveResponseToTable;

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
	
	@Autowired
	private HomePositionMasterRepository homePositionRepo;
	
	@Autowired
	private DownloadDocService downloadService;
	
	@Autowired
	private SaveResponseToTable saveResponse;
	public Map<String,Object> createRequest(WorkEngine engine) {
		try {
			String search="companyId:"+engine.getCompanyId()+";productId:"+engine.getProductId()+";status:{Y,R};integType:"+engine.getIntegType()+";";
			SpecCriteria criteria = crservice.createCriteria(FlowFieldDetails.class, search, "keyId");
			List<Tuple> result = crservice.getResult(criteria, 0, 50);
			FieldFromTuple t=new FieldFromTuple();
			
			List<JsonField> data = result.parallelStream().map(t).filter(d-> d!=null).collect(Collectors.toList());
			
			List<BigDecimal> distinctQueryid = data.stream().filter(tq -> tq.getQueryId()!=null && tq.getQueryId().compareTo(BigDecimal.ZERO)!=0)
			.map(tx->tx.getQueryId()).distinct().collect(Collectors.toList());
			Map<String, List<Map<String, Object>>> dynamicQuery=null;
			if( distinctQueryid !=null && !distinctQueryid.isEmpty()) {
				 dynamicQuery = dynamicQuery(distinctQueryid,data,engine);
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

	public Map<String, List<Map<String, Object>>> dynamicQuery(List<BigDecimal> distinctQueryid, List<JsonField> data,WorkEngine engine) {
		try {
			Map<String, List<Map<String, Object>>> hashMap=new HashMap<String, List<Map<String, Object>>>();
			for(BigDecimal id:distinctQueryid) {
				String search="queryId:"+id.toPlainString()+";";
				SpecCriteria criteria = crservice.createCriteria(FieldQueryTablequery.class, search, "queryId");
				List<Tuple> result = crservice.getResult(criteria, 0, 50);	
				
				List<String> collect = data.stream().filter(t-> t.getQueryId().compareTo(id)==0 && t.getQueryCol()!=null && !"".equals(t.getQueryCol()) )
						.map(t-> t.getQueryCol()+" "+t.getQueryAlias()).collect(Collectors.toList());
				
				String sql="SELECT "+(collect.isEmpty()? "*": String.join(",",collect)) +" "+ result.get(0).get("sqlQuery").toString();
				sql=sql.replaceAll("\\{quoteno\\}","'"+ engine.getQuoteNo() +"'")
						.replaceAll("\\{RequestReferenceNo\\}","'"+ engine.getRequestReferenceNo()+"'")
						.replaceAll("\\{PolicyNo\\}","'"+ engine.getPolicyNo()+"'");				
						
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

		try {
 			Map<String, Object> request = createRequest(engine);
			List<Map<String, Object>> a1=(List<Map<String, Object>>)request.get("Data");
			Map<String, Object> a2=(Map<String, Object>)a1.get(0).get("Root");
			Map<String, Object> response = azentoService.createQuote(engine,a2);
			List<Map<String, Object>> retObj=new ArrayList<Map<String,Object>>();
			Map<String,Object> responseMap=new HashMap<String,Object>();
			responseMap.put("Request",a2);
			responseMap.put("Response",response);
			
			if("POL_INTEG".equals(engine.getIntegType())) {
				try {
					
					Map<String, Object> data = (Map<String, Object>) response.get("data");					
					HomePositionMaster hm = homePositionRepo.findByQuoteNo(engine.getQuoteNo());
					if(hm!=null && data!=null && !data.isEmpty()) {
						hm.setIntegrationError((Boolean) data.get("hasError")?"Y":"N");
						hm.setIntegrationStatus((Boolean) data.get("hasError")?"F":"Y");
						hm.setPolicyNo(data.get("policyNumber").toString());
						hm.setCorePolicyNo(data.get("policyNumber").toString());
						hm.setStatus("P");
						hm.setCoreIntgRemarks(response.get("message").toString());
						hm.setCoreQuoteNo(data.get("quotationNumber").toString());
						//hm.setCoreSgsId(data.get("policyId").toString());
						homePositionRepo.save(hm);
					}

				}catch (Exception e) {
					e.printStackTrace();
				}
			}else if("GENDOC_INTEG".equals(engine.getIntegType())) {
				try {					
					List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
					//Generate Doc
					{
						WorkEngine e=new WorkEngine();
						e.setCompanyId(engine.getCompanyId());
						e.setProductId(engine.getProductId());
						e.setQuoteNo(engine.getQuoteNo());
						e.setRequestReferenceNo(engine.getRequestReferenceNo());
						e.setIntegType("DOWNLD_INTEG");
						
						Map<String, Object> request1 = createRequest(e);
						List<Map<String, Object>> aa1=(List<Map<String, Object>>)request1.get("Data");
						Map<String, Object> downloadReq=(Map<String, Object>)aa1.get(0).get("Root");
						downloadService.downloadDocs(data,engine,downloadReq,e);
					}	
				}catch (Exception e) {
					e.printStackTrace();;
				}
			}else if("QUOT_INTEG".equals(engine.getIntegType())){
				try {
					saveResponse.saveIntoFactorRequestTable(response,engine,a2);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			retObj.add(responseMap);
			return retObj;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
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
