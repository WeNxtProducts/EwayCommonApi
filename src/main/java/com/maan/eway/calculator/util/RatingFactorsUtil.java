package com.maan.eway.calculator.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.CompanyProrataMaster;
import com.maan.eway.bean.CompanyTaxSetup;
import com.maan.eway.bean.ConstantTableDetails;
import com.maan.eway.bean.DropdownTableDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.FactorRateMaster;
import com.maan.eway.bean.FactorTypeDetails;
import com.maan.eway.bean.OneTimeTableDetails;
import com.maan.eway.bean.RatingFieldMaster;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.req.referal.ReferralRequest;
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
	
	@Cacheable(cacheNames= {"loadfactorOnlyquery"},keyGenerator  = "loadfactorOnlyqueryKeyGen",value = "loadfactorOnlyquery")
	protected List<Tuple> loadfactorOnlyquery(CalcEngine engine,String condtion,String coverId, String subCoverId) {
		try{
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
				return result;
			}
		}catch (Exception e) {
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
			SpecCriteria criteria = crservice.createCriteria(FactorTypeDetails.class, search, "factorTypeId"); 
			result=crservice.getResult(criteria, 0, 50);
			if(result!=null && result.size()>0) {
				
				RatingTypeUtil rate=new RatingTypeUtil();
				List<RatingInfo> collect = result.stream().map(rate).filter(d->d!=null).collect(Collectors.toList());
				 
				this.LoadRatingField(engine, collect);
				 return collect;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	} 
	
	@Cacheable(cacheNames = {"ProductType"},keyGenerator  = "productTypeKeyGen",value = "ProductType" )
	public synchronized String collectProductType(CalcEngine engine) {
		try{
			String todayInString = DD_MM_YYYY.format(new Date());
			String prodSearch="companyId:"+engine.getInsuranceId()+";productId:"+engine.getProductId()+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";				
			SpecCriteria	criteria = crservice.createCriteria(CompanyProductMaster.class, prodSearch, "companyId");			  
			List<Tuple> product = crservice.getResult(criteria, 0, 1);
			String oneProduct=product.get(0).get("motorYn")==null?"M":product.get(0).get("motorYn").toString();
			return oneProduct;

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	
	@Cacheable(cacheNames = {"loadTax"},keyGenerator  = "loadTaxKeyGen",value = "loadTax" )
	public List<Tuple> LoadTax(CalcEngine engine) {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			//String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:Y;branchCode:{99999,"+engine.getBranchCode()+"};"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
			String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:Y;branchCode:"+engine.getBranchCode()+";"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(CompanyTaxSetup.class, search, "taxId"); 
			
			result=crservice.getResult(criteria, 0, 50);
			if(result.isEmpty()) {
				search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:Y;branchCode:99999;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
				criteria = crservice.createCriteria(CompanyTaxSetup.class, search, "taxId"); 
				result=crservice.getResult(criteria, 0, 50);
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
			return prorata;
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
			//String todayInString = DD_MM_YYYY.format(new Date());
			String prodSearch="itemType:ESERVICE_TABLE;status:Y;displayName:"+engine.getProductId()+";";				
			SpecCriteria	criteria = crservice.createCriteria(OneTimeTableDetails.class, prodSearch, "parentId");			  
			List<Tuple> product = crservice.getResult(criteria, 0, 1);
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
							 			.updatedDate(null).build();
					 return e;
				 }
			
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
		return null;
	}
}
