package com.maan.eway.calculator.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.BrokerCommissionDetails;
import com.maan.eway.bean.CompanyProrataMaster;
import com.maan.eway.bean.CompanyTaxSetup;
import com.maan.eway.bean.ConstantTableDetails;
import com.maan.eway.bean.CurrencyMaster;
import com.maan.eway.bean.DropdownTableDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.FactorRateMaster;
import com.maan.eway.bean.FactorTypeDetails;
import com.maan.eway.bean.LifePolicytermsMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.OneTimeTableDetails;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.ProductTaxSetup;
import com.maan.eway.bean.RatingFieldMaster;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.bean.TinyurlMaster;
import com.maan.eway.bean.TinyurlRequestDetail;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.req.referal.ReferralRequest;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.calc.RatingInfo;
import com.maan.eway.upgrade.criteria.CriteriaService;
import com.maan.eway.upgrade.criteria.SpecCriteria;

@Component

public class RatingFactorsUtil {
	@Autowired
	protected CriteriaService crservice;
	
	protected SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy")  ;
	
	
	@Cacheable(cacheNames = {"LoadConstant"},keyGenerator  = "loadConstantKeyGen",value = "LoadConstant" )
	public List<ReferralRequest> LoadConstant(CalcEngine engine) {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:{Y,R};"+todayInString+"~effectiveDateStart&effectiveDateEnd;branchCode:{99999,"+engine.getBranchCode()+"};";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(ConstantTableDetails.class, search, "itemId"); 
			result=crservice.getResult(criteria, 0, 50);
			List<ReferralRequest> refreqs=null;
			if(result!=null && result.size()>0) {
				
				refreqs=new ArrayList<ReferralRequest>();
				
				
				
				for(Tuple r :result) {
					String itemId=r.get("itemId")==null?"":r.get("itemId").toString();
					
					ReferralRequest req=ReferralRequest.builder().apiLink(r.get("apiUrl")==null?"":r.get("apiUrl").toString())
							.primaryTable(r.get("keyTable")==null?"":r.get("keyTable").toString())
							.primaryKey(r.get("keyName")==null?"":r.get("keyName").toString())
							.build();
					
					
					List<Tuple> loadDropdown = loadDropdown(engine, itemId);
					List<Map<String,String>> mps=null;
					if(loadDropdown!=null && loadDropdown.size()>0) {
						mps=new ArrayList<Map<String,String>>();	
						for(Tuple l :loadDropdown) {
							Map<String,String> mp=new HashMap<String,String>();
							mp.put("JsonKey", l.get("requestJsonKey")==null?"":l.get("requestJsonKey").toString());
							mp.put("JsonColum", l.get("requestColumn")==null?"":l.get("requestColumn").toString());
							mp.put("JsonTable", l.get("requestTable")==null?"":l.get("requestTable").toString());
							mps.add(mp);
						}
						req.setMp(mps);
					}
					refreqs.add(req);
				}
				
			}
			
			return refreqs;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	public List<Tuple> loadDropdown(CalcEngine engine,String itemId){
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:{Y,R};"+todayInString+"~effectiveDateStart&effectiveDateEnd;branchCode:{99999,"+engine.getBranchCode()+"};itemId:"+itemId+";";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(DropdownTableDetails.class, search, "requestId"); 
			result=crservice.getResult(criteria, 0, 50);

			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	protected List<Tuple> loopfactorrates(CalcEngine engine,Map<String, List<String>> vloop, String coverId, String subCoverId) {

		for (Entry<String, List<String>> entry : vloop.entrySet()) {
			List<String> condtions = entry.getValue();
			String condtion=StringUtils.join(condtions,';');

			List<Tuple> loadfactorOnlyquery = loadfactorOnlyquery(engine,condtion, coverId,subCoverId);
			
			return loadfactorOnlyquery;

		} 
		return null;
	}
	
	private Map<Integer,String>  commonQueries(CalcEngine engine,String condtion,String coverId, String subCoverId) {
		String todayInString = DD_MM_YYYY.format(new Date());
		String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+
				engine.getSectionId()+";status:{Y,R};coverId:"+coverId+";subCoverId:"+subCoverId+";"
				+todayInString+"~effectiveDateStart&effectiveDateEnd;agencyCode:"+engine.getAgencyCode()
				+";branchCode:"+engine.getBranchCode()+";"+condtion;


		String search2="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+
				engine.getSectionId()+";status:{Y,R};coverId:"+coverId+";subCoverId:"+subCoverId+";"
				+todayInString+"~effectiveDateStart&effectiveDateEnd;agencyCode:"+engine.getAgencyCode()
				+";branchCode:99999;"+condtion;

		String search3="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+
				engine.getSectionId()+";status:{Y,R};coverId:"+coverId+";subCoverId:"+subCoverId+";"
				+todayInString+"~effectiveDateStart&effectiveDateEnd;agencyCode:99999"
				+";branchCode:"+engine.getBranchCode()+";"+condtion;

		String search4="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+
				engine.getSectionId()+";status:{Y,R};coverId:"+coverId+";subCoverId:"+subCoverId+";"
				+todayInString+"~effectiveDateStart&effectiveDateEnd;agencyCode:99999"
				+";branchCode:99999;"+condtion;

		Map<Integer,String> hsmap=new TreeMap<Integer,String>();
		hsmap.put(1, search);
		hsmap.put(2, search2);
		hsmap.put(3, search3);
		hsmap.put(4, search4);
		return hsmap;
	}
	@Cacheable(cacheNames= {"loadfactorOnlyquery"},keyGenerator  = "loadfactorOnlyqueryKeyGen",value = "loadfactorOnlyquery")
	public List<Tuple> loadfactorOnlyquery(CalcEngine engine,String condtion,String coverId, String subCoverId) {
		try{
			Map<Integer, String> hsmap = commonQueries(engine, condtion, coverId, subCoverId);
			//1.Priorty both s pecifi agencycode & branchcode
			//2.priorty both specifi agencycode
			//3.priorty both specifi branchcode
			//4.priorty both common
			SpecCriteria criteria = null;

			for(int i=1;i<=hsmap.size();i++) {
				String dataquery = hsmap.get(i);


				criteria = crservice.createCriteria(FactorRateMaster.class, dataquery, "factorTypeId"); 

				List<Long> count = crservice.getCount(criteria, 0, 50);
				if(!count.isEmpty()) { 
					Long countrec = count.get(0);				
					if(countrec>0) 
						break;
				}

			}

			if(criteria!=null) {
				List<Tuple> result=null;
				result=crservice.getResult(criteria, 0, 50);
				return result.size()>0?result:null;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Cacheable(cacheNames= {"countfactorOnlyquery"},keyGenerator  = "countfactorOnlyqueryKeyGen",value = "countfactorOnlyquery")
	public List<Long> countfactorOnlyquery(CalcEngine engine,String condtion,String coverId, String subCoverId) {
		String dataquery = null;
		try{
			Map<Integer, String> hsmap = commonQueries(engine, condtion, coverId, subCoverId);
			//1.Priorty both s pecifi agencycode & branchcode
			//2.priorty both specifi agencycode
			//3.priorty both specifi branchcode
			//4.priorty both common
			SpecCriteria criteria = null;
			List<Long> count=null;
			for(int i=1;i<=hsmap.size();i++) {
				dataquery = hsmap.get(i);


				criteria = crservice.createCriteria(FactorRateMaster.class, dataquery, "factorTypeId"); 

				  count = crservice.getCount(criteria, 0, 50);
				if(!count.isEmpty()) { 
					Long countrec = count.get(0);				
					if(countrec>0) 
						break;
				}

			}

			 
			return count;
		}catch (Exception e) {
			System.out.println("Factor Id"+dataquery);
			e.printStackTrace();
			
		}
		return null;
	}
	
	
	public List<RatingInfo> LoadRatingField(CalcEngine engine,List<RatingInfo> infos){
		
		
		for (RatingInfo info : infos) {
			try {
				List<Tuple> result=this.getCachedRatingFields(engine,info);
				if(result!=null && result.size()>0) {					
					Tuple t = result.get(0);
					info.setRatingFieldId(t.get("ratingId")==null?"":t.get("ratingId").toString());
					info.setRatingField(t.get("ratingField")==null?"":t.get("ratingField").toString());
					info.setInputTableName(t.get("inputTable")==null?"":t.get("inputTable").toString());
					info.setInputColumName(t.get("inputColumnName")==null?"":t.get("inputColumnName").toString());
					 
				}			
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return infos;
	}
	@Cacheable(cacheNames = {"getCachedRatingFields"},keyGenerator  = "getCachedRatingFieldsKeyGen",value = "getCachedRatingFields" )
	public List<Tuple> getCachedRatingFields(CalcEngine engine,RatingInfo info){
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="productId:"+engine.getProductId()+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;ratingId:"+info.getRatingFieldId()+";";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(RatingFieldMaster.class, search, "ratingId"); 
			result=crservice.getResult(criteria, 0, 50);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Cacheable(cacheNames = {"RatingType"},keyGenerator  = "ratingTypeKeyGen",value = "RatingType" )
	public synchronized   List<RatingInfo> LoadRatingType(CalcEngine engine,String factorTypeId){
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;factorTypeId:"+factorTypeId+";";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(FactorTypeDetails.class, search, "ratingFieldId"); 
			result=crservice.getResult(criteria, 0, 50);
			if(result!=null && result.size()>0) {
				
				RatingTypeUtil rate=new RatingTypeUtil();
				List<RatingInfo> collect = result.stream().map(rate).filter(d->d!=null).collect(Collectors.toList());
				collect.sort(new Comparator<RatingInfo>() {

					@Override
					public int compare(RatingInfo o1, RatingInfo o2) {
						// TODO Auto-generated method stub
						return  o1.getRatingFieldId().compareTo(o2.getRatingFieldId());
					}
					
				});
				this.LoadRatingField(engine, collect);
				 return collect;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	} 
	
	@Cacheable(cacheNames = {"ProductType"},keyGenerator  = "productTypeKeyGen",value = "ProductType" )
	public synchronized List<Tuple> collectProductType(CalcEngine engine) {
		try{
			
			if(engine.getSectionId()=="") {
				String todayInString = DD_MM_YYYY.format(new Date());
				String prodSearch="companyId:"+engine.getInsuranceId()+";productId:"+engine.getProductId()+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";				
				SpecCriteria	criteria = crservice.createCriteria(ProductSectionMaster.class, prodSearch, "companyId");			  
				List<Tuple> product = crservice.getResult(criteria, 0, 1);
				return product;					
			}else {
				String todayInString = DD_MM_YYYY.format(new Date());
				String prodSearch="companyId:"+engine.getInsuranceId()+";productId:"+engine.getProductId()+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;sectionId:"+engine.getSectionId()+";";				
				SpecCriteria	criteria = crservice.createCriteria(ProductSectionMaster.class, prodSearch, "companyId");			  
				List<Tuple> product = crservice.getResult(criteria, 0, 1);
				return product;
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	
	@Cacheable(cacheNames = {"loadTax"},keyGenerator  = "loadTaxKeyGen",value = "loadTax" )
	public List<Tuple> LoadTax(CalcEngine engine,List<String> taxFor) {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			//String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:Y;branchCode:{99999,"+engine.getBranchCode()+"};"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
			String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:Y;branchCode:"+engine.getBranchCode()+";"+todayInString+"~effectiveDateStart&effectiveDateEnd;taxFor:{"+StringUtils.join(taxFor,',')+"};";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(ProductTaxSetup.class, search, "taxId"); 
			
			result=crservice.getResult(criteria, 0, 50);
			if(result.isEmpty()) {
				search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:Y;branchCode:99999;"+todayInString+"~effectiveDateStart&effectiveDateEnd;taxFor:{"+StringUtils.join(taxFor,',')+"};";
				criteria = crservice.createCriteria(ProductTaxSetup.class, search, "taxId"); 
				result=crservice.getResult(criteria, 0, 50);
				return result.size()>0?result:null;
			}
			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Cacheable(cacheNames = {"loadProRata"},keyGenerator  = "loadProRataKeyGen",value = "loadProRata" )
	public List<Tuple> loadProRataData(CalcEngine engine,String periodOfInsurance){
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="insuranceid:"+engine.getInsuranceId()+";productid:"+engine.getProductId()+";status:Y;"+periodOfInsurance+"~startfrom&endto;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
			SpecCriteria criteria = crservice.createCriteria(CompanyProrataMaster.class, search, "sno");
			List<Tuple> prorata = crservice.getResult(criteria, 0, 50);
			return prorata.size()>0?prorata:null;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	public List<Tuple> loadNotificationPending() {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="notifPushedStatus:P;"+todayInString+"~notifcationPushDate&notifcationEndDate";
			SpecCriteria criteria = crservice.createCriteria(NotifTransactionDetails.class, search, "notifPriority");
			List<Tuple> prorata = crservice.getResult(criteria, 0, 50);
			return prorata;
		}catch (Exception e) {
			e.printStackTrace();	
		}
		return null;
		
	}
	
	@Cacheable(cacheNames = {"ProductToRawtable"},keyGenerator  = "getProductIdBasedRawTable",value = "ProductToRawtable" )
	public synchronized String getProductIdBasedRawTable(CalcEngine engine) {
		try{
			
			String prodSearch="itemType:ESERVICE_TABLE;status:Y;displayName:"+engine.getProductId()+";itemValue:"+engine.getSectionId()+";companyId:"+engine.getInsuranceId()+";";
			SpecCriteria	criteria = crservice.createCriteria(OneTimeTableDetails.class, prodSearch, "parentId");
			List<Tuple> product =null;
			List<Long> count = crservice.getCount(criteria, 0, 1);
			if(!count.isEmpty()) { 
				Long countrec = count.get(0);
				if(countrec<=0) {				
					 prodSearch="itemType:ESERVICE_TABLE;status:Y;displayName:"+engine.getProductId()+";companyId:"+engine.getInsuranceId()+";";
					 criteria = crservice.createCriteria(OneTimeTableDetails.class, prodSearch, "parentId");					
				}
				product= crservice.getResult(criteria, 0, 1);
			}	
			
			String rawTable=product.get(0).get("itemCode")==null?"0":product.get(0).get("itemCode").toString();
			return "com.maan.eway.bean."+rawTable;

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	@Cacheable(cacheNames= {"EndtMasterData"},keyGenerator  = "getEndtMasterData",value = "EndtMasterData")
	public EndtTypeMaster getEndtMasterData(String insuranceId, String productId, String endtTypeId) {
		 try {

				String todayInString = DD_MM_YYYY.format(new Date());
				String search="companyId:"+insuranceId+";productId:"+productId+";status=Y;endtTypeId:"+endtTypeId+";"+todayInString+"effectiveDateStart&effectiveDateEnd;";
				SpecCriteria criteria = crservice.createCriteria(EndtTypeMaster.class, search, "endtTypeId");
				List<Tuple> prorata = crservice.getResult(criteria, 0, 50);
				 if(prorata!=null && prorata.size()>0) {
					 Tuple t = prorata.get(0);
					 EndtTypeMaster e=EndtTypeMaster.builder()
							 			.amendId(t.get("amendId")==null?0:Integer.parseInt(t.get("amendId").toString()))
							 			.calcTypeId(t.get("calcTypeId")==null?"P":t.get("calcTypeId").toString())
							 			.companyId(t.get("companyId")==null?"":t.get("companyId").toString())
							 			.coreAppCode(t.get("coreAppCode")==null?"":t.get("coreAppCode").toString())
							 			.createdBy(t.get("createdBy")==null?"":t.get("createdBy").toString())
							 			.effectiveDateEnd(new Date())
							 			.effectiveDateStart(new Date())
							 			.endtDependantFields(t.get("endtDependantFields")==null?"":t.get("endtDependantFields").toString())
							 			.endtDependantIds(t.get("endtDependantIds")==null?"":t.get("endtDependantIds").toString())
							 			.endtFeePercent(t.get("endtFeePercent")==null?"":t.get("endtFeePercent").toString())
							 			.endtFeeYn(t.get("endtFeeYn")==null?"":t.get("endtFeeYn").toString())
							 			.endtType(t.get("endtType")==null?"":t.get("endtType").toString())
							 			.endtTypeCategory(t.get("endtTypeCategory")==null?"":t.get("endtTypeCategory").toString())
							 			.endtTypeDesc(t.get("endtTypeDesc")==null?"":t.get("endtTypeDesc").toString())
							 			.endtTypeId(t.get("endtTypeId")==null?0:Integer.parseInt(t.get("endtTypeId").toString()))
							 			.entryDate(new Date())
							 			.priority(t.get("priority")==null?0:Integer.parseInt(t.get("priority").toString()))
							 			.productId(t.get("productId")==null?0:Integer.parseInt(t.get("productId").toString()))
							 			.regulatoryCode(t.get("regulatoryCode")==null?"":t.get("regulatoryCode").toString())
							 			.remarks(t.get("remarks")==null?"":t.get("remarks").toString())
							 			.status(t.get("status")==null?"":t.get("status").toString())
							 			.updatedBy(t.get("updatedBy")==null?"":t.get("updatedBy").toString())
							 			.endtTypeCategoryId(t.get("endtTypeCategoryId")==null?0:Integer.parseInt(t.get("endtTypeCategoryId").toString()))
							 			.isCoverendt(t.get("isCoverendt")==null?"N":t.get("isCoverendt").toString())
							 			.updatedDate(null).build();
					 return e;
				 }
			
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
		return null;
	}
	@Cacheable(cacheNames= {"currencyDecimalFormat"},keyGenerator  = "currencyDecimalFormatKeyGen",value = "currencyDecimalFormat")
	public String currencyDecimalFormat(String insuranceId, String currencyId) {

		String decimalFormat = "0" ;
		try {
			
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+insuranceId+";currencyId:"+currencyId+";status=Y;"+todayInString+"effectiveDateStart&effectiveDateEnd;";
			SpecCriteria criteria = crservice.createCriteria(CurrencyMaster.class, search, "amendId");
			List<Tuple> currencies = crservice.getResult(criteria, 0, 50);
			if(currencies!=null && currencies.size()>0) {
				decimalFormat = currencies.get(0).get("decimalDigit")==null?"0":currencies.get(0).get("decimalDigit").toString();
			}		 		
			
		} catch (Exception e) {
			e.printStackTrace(); 			
		}
		return decimalFormat;
	
	}

	public  List<Tuple> loadTinyUrl(String companyid, Integer productid, String notifTemplatename) {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			//String search="companyId:"+ companyid +";productId:"+productid+";status:{Y,R};"+todayInString+"~effectiveDateStart&effectiveDateEnd;branchCode:{99999};notifYn:Y;type:"+notifTemplatename.toUpperCase().trim()+";";
			String search="type:"+notifTemplatename.toUpperCase().trim()+";"+"companyId:"+ companyid +";productId:"+productid+";status:{Y,R};notifYn:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;branchCode:99999";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(TinyurlMaster.class, search, "sno"); 
			result=crservice.getResult(criteria, 0, 50);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public  List<Tuple> loadTinyUrl(String companyid, Integer productid, String notifTemplatename,String notifYn) {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			//String search="companyId:"+ companyid +";productId:"+productid+";status:{Y,R};"+todayInString+"~effectiveDateStart&effectiveDateEnd;branchCode:{99999};notifYn:Y;type:"+notifTemplatename.toUpperCase().trim()+";";
			String search="type:"+notifTemplatename.toUpperCase().trim()+";"+"companyId:"+ companyid +";productId:"+productid+";status:{Y,R};notifYn:"+notifYn+";"+todayInString+"~effectiveDateStart&effectiveDateEnd;branchCode:99999";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(TinyurlMaster.class, search, "sno"); 
			result=crservice.getResult(criteria, 0, 50);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public List<Tuple> loadTinyUrlRequest(String companyid, Integer productid, String notifTemplatename,String itemId){
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+ companyid +";productId:"+productid+";status:{Y,R};"+todayInString+"~effectiveDateStart&effectiveDateEnd;branchCode:99999;tinyId:"+itemId+";";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(TinyurlRequestDetail.class, search, "itemId"); 
			result=crservice.getResult(criteria, 0, 50);

			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<EndtTypeMaster> getEndtMasterDatas(String companyId, String productId) {
		try {

			List<EndtTypeMaster> result=new ArrayList<EndtTypeMaster>();
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+companyId+";productId:"+productId+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
			SpecCriteria criteria = crservice.createCriteria(EndtTypeMaster.class, search, "endtTypeId");
			List<Tuple> prorata = crservice.getResult(criteria, 0, 50);
			 if(prorata!=null && prorata.size()>0) {
				 for(int i=0;i<prorata.size();i++) {
				 Tuple t = prorata.get(i);
				 EndtTypeMaster e=EndtTypeMaster.builder()
						 			.amendId(t.get("amendId")==null?0:Integer.parseInt(t.get("amendId").toString()))
						 			.calcTypeId(t.get("calcTypeId")==null?"P":t.get("calcTypeId").toString())
						 			.companyId(t.get("companyId")==null?"":t.get("companyId").toString())
						 			.coreAppCode(t.get("coreAppCode")==null?"":t.get("coreAppCode").toString())
						 			.createdBy(t.get("createdBy")==null?"":t.get("createdBy").toString())
						 			.effectiveDateEnd(new Date())
						 			.effectiveDateStart(new Date())
						 			.endtDependantFields(t.get("endtDependantFields")==null?"":t.get("endtDependantFields").toString())
						 			.endtDependantIds(t.get("endtDependantIds")==null?"":t.get("endtDependantIds").toString())
						 			.endtFeePercent(t.get("endtFeePercent")==null?"":t.get("endtFeePercent").toString())
						 			.endtFeeYn(t.get("endtFeeYn")==null?"":t.get("endtFeeYn").toString())
						 			.endtType(t.get("endtType")==null?"":t.get("endtType").toString())
						 			.endtTypeCategory(t.get("endtTypeCategory")==null?"":t.get("endtTypeCategory").toString())
						 			.endtTypeDesc(t.get("endtTypeDesc")==null?"":t.get("endtTypeDesc").toString())
						 			.endtTypeId(t.get("endtTypeId")==null?0:Integer.parseInt(t.get("endtTypeId").toString()))
						 			.entryDate(new Date())
						 			.priority(t.get("priority")==null?0:Integer.parseInt(t.get("priority").toString()))
						 			.productId(t.get("productId")==null?0:Integer.parseInt(t.get("productId").toString()))
						 			.regulatoryCode(t.get("regulatoryCode")==null?"":t.get("regulatoryCode").toString())
						 			.remarks(t.get("remarks")==null?"":t.get("remarks").toString())
						 			.status(t.get("status")==null?"":t.get("status").toString())
						 			.updatedBy(t.get("updatedBy")==null?"":t.get("updatedBy").toString())
						 			.endtTypeCategoryId(t.get("endtTypeCategoryId")==null?0:Integer.parseInt(t.get("endtTypeCategoryId").toString()))
						 			.sectionModificationYn(t.get("sectionModificationYn")==null ? "" : t.get("sectionModificationYn").toString())
						 			.sectionModificationType(t.get("sectionModificationType")==null ? "" : t.get("sectionModificationType").toString())
						 			.isCoverendt(t.get("isCoverendt")==null?"N":t.get("isCoverendt").toString())
						 			.updatedDate(null).build();
				 result.add(e);
				 }
 			 }
		
			 return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Cacheable(cacheNames= {"collectProductsFromLoginId"},keyGenerator  = "collectProductsFromLoginIdKeyGen",value = "collectProductsFromLoginId")
	public Tuple collectProductsFromLoginId(String loginId) {
		try {			
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="loginId:"+loginId+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
			SpecCriteria criteria = crservice.createCriteria(LoginProductMaster.class, search, "loginId");
			List<Tuple> data = crservice.getResult(criteria, 0, 50);
			if(data.size()>0)
				return data.get(0);
		}catch (Exception e) {
			e.printStackTrace();	
		}
		return null;
		
	}
	@Cacheable(cacheNames= {"fetchAlipaRating"},keyGenerator  = "fetchAlipaRatingKeyGen",value = "fetchAlipaRating")
	public List<Tuple> fetchAlipaRating(String search) {
		try {
			SpecCriteria criteria = crservice.createCriteria(SectionCoverMaster.class, search, "coverId");
			List<Tuple> data = crservice.getResult(criteria, 0, 50);
			return data;
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	@Cacheable(cacheNames= {"collectCommissionDetails"},keyGenerator  = "collectCommissionDetailsKeyGen",value = "collectCommissionDetails")
	public Map<String, Object> collectCommissionDetails(String loginId) {
		 try {
			 String todayInString = DD_MM_YYYY.format(new Date());
			 String search="loginId:"+loginId+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;policyType:99999;";
			 SpecCriteria criteria = crservice.createCriteria(BrokerCommissionDetails.class, search, "amendId");
			 List<Tuple> data = crservice.getResult(criteria, 0, 50);
			 if(data.size()>0) {
				 Map<String, Object> r=new HashMap<String, Object>(); 
				 r.put("COMMISSION_PERCENTAGE", data.get(0).get("commissionPercentage"));
				 r.put("COMMISSION_VAT_YN", data.get(0).get("commissionVatYn"));
				 r.put("COMMISSION_VAT_PERCENT", data.get(0).get("commissionVatPercent"));
				 
				 CalcEngine e=new CalcEngine();
				 e.setAgencyCode("99999");
				 e.setBranchCode("99999");
				 e.setInsuranceId(data.get(0).get("companyId").toString());
				 e.setProductId(data.get(0).get("productId").toString());
				 e.setSectionId("99999");
				  List<Tuple> loadTax = LoadTax(e, Arrays.asList("B", "N"));
				  Double totalTax=loadTax==null?0D:loadTax.stream().mapToDouble(t->t.get("value")==null?0D:Double.parseDouble(t.get("value").toString())).sum();				  
				  r.put("TOTALTAX", totalTax);
				  
				 return r;
			 }
		 }catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}
	
	@PersistenceContext
	private EntityManager em;

	public List<PolicyCoverData> findDataForTravel(String endtPrevQuoteNo, Integer vehicleId, String insuranceId,
			Integer productId, Integer sectionId, String status) {

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PolicyCoverData> createQuery = cb.createQuery(PolicyCoverData.class);
			Root<PolicyCoverData> from = createQuery.from(PolicyCoverData.class);
			Predicate n1 = cb.equal(from.get("quoteNo"),endtPrevQuoteNo);
			Predicate n2 = cb.equal(from.get("vehicleId"),vehicleId);
			Predicate n3 = cb.equal(from.get("companyId"),insuranceId);			
			Predicate n4 = cb.equal(from.get("productId"),productId);
			Predicate n5 = cb.equal(from.get("sectionId"),sectionId);
			Predicate n6 = cb.equal(from.get("status"),status);
			Field[] declaredFields = PolicyCoverData.class.getDeclaredFields();
			//declaredFields
			List<Selection<?>> colselct=new ArrayList<Selection<?>>();
			
			for(Field  field:declaredFields) {
				try {
					if(!"serialVersionUID".equals(field.getName()))
					{
						Selection<Object> alias = from.get(field.getName()).alias(field.getName());
						colselct.add(alias);
					}
				}catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
			
			
			createQuery.where(n1,n2,n3,n4,n5,n6).groupBy(from.get(""));
			createQuery.multiselect(colselct);
		}catch (Exception e) {
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}
	@Cacheable(cacheNames= {"collectSectionMaster"},keyGenerator  = "collectSectionMasterKeyGen",value = "collectSectionMaster")
	public List<ProductSectionMaster> collectSectionMaster(String companyId, String productId, String sectionId) {
		try {

			List<ProductSectionMaster> result=new ArrayList<ProductSectionMaster>();
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+companyId+";productId:"+productId+";sectionId:"+sectionId+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
			SpecCriteria criteria = crservice.createCriteria(ProductSectionMaster.class, search, "sectionId");
			List<Tuple> prorata = crservice.getResult(criteria, 0, 50);
			 if(prorata!=null && prorata.size()>0) {
				 for(int i=0;i<prorata.size();i++) {
					 Tuple t = prorata.get(i);
					 ProductSectionMaster section= new ProductSectionMaster();
					 section.setCoreAppCode(t.get("coreAppCode")==null?"":t.get("coreAppCode").toString());
					 section.setSectionName(t.get("sectionName")==null?"":t.get("sectionName").toString());
					 section.setSectionId(t.get("sectionId")==null?0:Integer.parseInt(t.get("sectionId").toString()));
					 result.add(section);
					 
				 }
			 }
			 return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Cacheable(cacheNames= {"collectBranchMaster"},keyGenerator  = "collectBranchMasterKeyGen",value = "collectBranchMaster")
	public List<BranchMaster> collectBranchMaster(String companyId, String branchCode) {

		try {

			List<BranchMaster> result=new ArrayList<BranchMaster>();
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+companyId+";branchCode:"+branchCode+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
			SpecCriteria criteria = crservice.createCriteria(BranchMaster.class, search, "branchCode");
			List<Tuple> prorata = crservice.getResult(criteria, 0, 50);
			 if(prorata!=null && prorata.size()>0) {
				 for(int i=0;i<prorata.size();i++) {
					 Tuple t = prorata.get(i);
					 BranchMaster b=new BranchMaster();
					 b.setCoreAppCode(t.get("coreAppCode")==null?"":t.get("coreAppCode").toString());
					 
					 result.add(b);
					 
				 }
			 }
			 return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	
	}

	 
	public List<DropDownRes> getLifePolicyTerms(String companyId,String productId) {
		try {
		String todayInString = DD_MM_YYYY.format(new Date());
		String search="companyId:"+companyId+";productId:"+productId+";sectionId:99999;status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
		SpecCriteria criteria = crservice.createCriteria(LifePolicytermsMaster.class, search, "policyTerms");
		List<Tuple> prorata = crservice.getResult(criteria, 0, 50);
		 List<DropDownRes> result=new LinkedList<DropDownRes>();
		 if(prorata!=null && prorata.size()>0) {
			 for(int i=0;i<prorata.size();i++) {
				 Tuple t = prorata.get(i);
				 DropDownRes b=new DropDownRes();
				 b.setCode(t.get("policyTerms")==null?"":t.get("policyTerms").toString());
				 b.setCodeDesc(t.get("policyTermsDesc")==null?"":t.get("policyTermsDesc").toString());
				 
				 result.add(b);
				 
			 }
		 }
		 return result;
	}catch (Exception e) {
		e.printStackTrace();
	}
		return null;
	}

}
