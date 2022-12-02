package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maan.eway.bean.FactorRateMaster;
import com.maan.eway.bean.FactorTypeDetails;
import com.maan.eway.bean.MsCommonDetails;
import com.maan.eway.bean.MsCustomerDetails;
import com.maan.eway.bean.MsVehicleDetails;
import com.maan.eway.bean.RatingFieldMaster;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.RatingInfo;
import com.maan.eway.upgrade.criteria.CriteriaService;
import com.maan.eway.upgrade.criteria.SpecCriteria;
@Component
public class CommonCalculator {
	@Autowired
	protected CriteriaService crservice;
	
	protected SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy")  ;
	
	protected MathContext round=new MathContext(3, RoundingMode.HALF_UP);
	protected CalcEngine engine;
	
	protected String cdRefno;
	protected String vdRefno;
	
	protected List<Tuple> result=null;	
	protected List<Tuple> vehicles=null;
	protected List<Tuple> customers =null;
	protected List<Cover> calculatedcover=null;
	protected List<Tuple> prorata=null;
	 

	/*public void setEngine(CalcEngine engine,List<Cover> c) {
		this.engine = engine;
		this.calculatedcover=c;
	}
	*/
	public void setEngine(CalcEngine engine,List<Cover> c,List<Tuple> result,List<Tuple> vehicles,List<Tuple> customers,List<Tuple> prorata) {
		this.engine = engine;
		this.calculatedcover=c;
		this.result=result;
		this.vehicles=vehicles;
		this.customers=customers;
		this.prorata=prorata;
	}
	
	
	

	public List<Tuple> LoadFactorRates(CalcEngine engine,String coverId,String factorid,String vehicleId) {
		Map<String,List<String>> vloop=new HashMap<String, List<String>>();
		try {
			
			
			
				List<RatingInfo> rateInfos = LoadRatingType(engine, factorid);
				
				
				

				Tuple tuple = vehicles.get(0);
				
				 

				List<String> condtions=new ArrayList<String>();
				for (RatingInfo r : rateInfos) {
					if("MS_CUSTOMER_DETAILS".equalsIgnoreCase(r.getInputTableName())) {
						r.setInputColumValue(customers.get(0).get(r.getInputColumName()).toString());
					}else if("MS_Vehicle_DETAILS".equalsIgnoreCase(r.getInputTableName())) {
						r.setInputColumValue(tuple.get(r.getInputColumName()).toString());
					}else if("MS_Common_DETAILS".equalsIgnoreCase(r.getInputTableName())) {
						r.setInputColumValue(result.get(0).get(r.getInputColumName()).toString());
					}
					String condtion=r.getDiscretCol()+":"+r.getInputColumValue()+"";
					if("Y".equals(r.getFactorRangeYn())) {
						condtion=""+r.getInputColumValue()+"~"+r.getRangeFromCol()+"&"+r.getRangeToCol();  
					} 
					condtions.add(condtion);   
				}

				vloop.put(vehicleId, condtions);


				List<Tuple> loopfactorrates = loopfactorrates(vloop,coverId);

				if(loopfactorrates==null || loopfactorrates.size()==0) {
					condtions.clear(); 
					vloop.clear();
		
					for(int i=0;i<rateInfos.size();i++) {							
						RatingInfo r = rateInfos.get(i);
						if("N".equals(r.getFactorRangeYn())) {
							String condtion=r.getDiscretCol()+":"+r.getInputColumValue()+";";
							if(condtions.size()>0)
								condtion=condtion.concat(StringUtils.join(condtions,';'));
							List<Tuple> onlyquery = loadfactorOnlyquery(condtion, coverId);
							Long count=0L;
							if(onlyquery==null || onlyquery.size()==0) {
								r.setInputColumValue("99999");
							} 	
						}
						String condtion=r.getDiscretCol()+":"+r.getInputColumValue()+"";
						if("Y".equals(r.getFactorRangeYn())) {
							condtion=""+r.getInputColumValue()+"~"+r.getRangeFromCol()+"&"+r.getRangeToCol();  
						} 
						condtions.add(condtion);  
					}						 
					vloop.put(vehicleId, condtions);
					loopfactorrates = loopfactorrates(vloop,coverId); 

				}
			
			return loopfactorrates;  
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	
	protected List<Tuple> loopfactorrates(Map<String, List<String>> vloop, String coverId) {

		for (Entry<String, List<String>> entry : vloop.entrySet()) {
			List<String> condtions = entry.getValue();
			String condtion=StringUtils.join(condtions,';');

			List<Tuple> loadfactorOnlyquery = loadfactorOnlyquery( condtion, coverId);
			
			return loadfactorOnlyquery;

		} 
		return null;
	}
	
	
	protected List<Tuple> loadfactorOnlyquery(String condtion,String coverId) {
		try{
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+
					engine.getSectionId()+";status:{Y,R};coverId:"+coverId+";"
					+todayInString+"~effectiveDateStart&effectiveDateEnd;agencyCode:"+engine.getAgencyCode()
					+";branchCode:"+engine.getBranchCode()+";"+condtion;


			String search2="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+
					engine.getSectionId()+";status:{Y,R};coverId:"+coverId+";"
					+todayInString+"~effectiveDateStart&effectiveDateEnd;agencyCode:"+engine.getAgencyCode()
					+";branchCode:99999;"+condtion;

			String search3="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+
					engine.getSectionId()+";status:{Y,R};coverId:"+coverId+";"
					+todayInString+"~effectiveDateStart&effectiveDateEnd;agencyCode:99999"
					+";branchCode:"+engine.getBranchCode()+";"+condtion;

			String search4="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+
					engine.getSectionId()+";status:{Y,R};coverId:"+coverId+";"
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
				String todayInString = DD_MM_YYYY.format(new Date());
				String search="productId:"+engine.getProductId()+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;ratingId:"+info.getRatingFieldId()+";";
				List<Tuple> result=null;
				SpecCriteria criteria = crservice.createCriteria(RatingFieldMaster.class, search, "ratingId"); 
				result=crservice.getResult(criteria, 0, 50);
				if(result!=null && result.size()>0) {
					
					Tuple t = result.get(0);
					info.setRatingFieldId(t.get("ratingId")==null?"":t.get("ratingId").toString());
					info.setRatingField(t.get("ratingField")==null?"":t.get("ratingField").toString());
					info.setInputTableName(t.get("inputTableName")==null?"":t.get("inputTableName").toString());
					info.setInputColumName(t.get("inputColumnName")==null?"":t.get("inputColumnName").toString());
					 
				}			
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return infos;
	}
	
	public  List<RatingInfo> LoadRatingType(CalcEngine engine,String factorTypeId){
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;factorTypeId:"+factorTypeId+";";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(FactorTypeDetails.class, search, "factorTypeId"); 
			result=crservice.getResult(criteria, 0, 50);
			if(result!=null && result.size()>0) {
				
				RatingTypeUtil rate=new RatingTypeUtil();
				List<RatingInfo> collect = result.stream().map(rate).filter(d->d!=null).collect(Collectors.toList());
				 
				LoadRatingField(engine, collect);
				 return collect;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	protected BigDecimal domath(String calctype, Double rate,BigDecimal si) {
		BigDecimal d=BigDecimal.ZERO;
		if("P".equals(calctype)) {
			d = si.multiply(new BigDecimal(rate/100), round);			
		 }else if("A".equals(calctype)) {
			d=(new BigDecimal(rate));			
		 }else if("M".equals(calctype)) {
			 d = si.multiply(new BigDecimal(rate/1000), round);			
		 }
		return d;
	}
	
	protected BigDecimal domathTira(String calctype, Double rate,BigDecimal premium) {
		BigDecimal d=BigDecimal.ZERO;
		//(3500/4)*100
		if("P".equals(calctype)) {
			d = premium.divide(new BigDecimal(rate) , round).multiply(new BigDecimal(100), round); ///multiply(new BigDecimal(rate/100), round);			
		 }else if("A".equals(calctype)) {
			d=(new BigDecimal(rate));			
		 }else if("M".equals(calctype)) {
			 d = premium.divide(new BigDecimal(rate), round).multiply(new BigDecimal(1000), round);			
		 }
		return d;
	}
}
