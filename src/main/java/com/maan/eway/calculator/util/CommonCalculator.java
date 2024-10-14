package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.RatingInfo;

import jakarta.persistence.Tuple;
//@Component
//@CacheConfig(cacheNames = {"RatingType"})
public class CommonCalculator {
 
	protected RatingFactorsUtil crservice;
	
	protected SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy")  ;
	
	//protected MathContext round=new MathContext(3, RoundingMode.HALF_UP);
	protected CalcEngine engine;
	
	protected String cdRefno;
	protected String vdRefno;
	
	protected List<Tuple> result=null;	
	protected List<Tuple> vehicles=null;
	protected List<Tuple> customers =null;
	protected List<Cover> calculatedcover=null;
	protected List<Tuple> prorata=null;
	protected List<Tuple> drivers=null;
	protected DecimalFormat decimalFormat = null;
	protected  List<Tuple> customerChoiceTaxes;

	/*public void setEngine(CalcEngine engine,List<Cover> c) {
		this.engine = engine;
		this.calculatedcover=c;
	}
	*/
	public void setEngine(CalcEngine engine,List<Cover> c,List<Tuple> result,List<Tuple> vehicles,List<Tuple> customers,List<Tuple> prorata, RatingFactorsUtil crservice,DecimalFormat decimalFormat, List<Tuple> drivers, List<Tuple> customerChoiceTaxes) {
		this.engine = engine;
		this.calculatedcover=c;
		this.result=result;
		this.vehicles=vehicles;
		this.customers=customers;
		this.prorata=prorata;
		this.crservice=crservice;
		this.decimalFormat=decimalFormat;
		this.decimalFormat.setParseBigDecimal(true);		
		this.drivers=drivers;
		this.customerChoiceTaxes=customerChoiceTaxes;
		
	}
	
	public List<Tuple> LoadFactorRates(CalcEngine engine,String coverId,String factorid,String vehicleId, String subCoverId){
		return LoadFactorRates(engine, coverId, factorid, vehicleId, vehicles.get(0), customers.get(0), result.get(0),subCoverId,(drivers==null || drivers.isEmpty())?null:drivers.get(0));
	}
	
	public  List<Tuple> LoadFactorRates(CalcEngine engine,String coverId,String factorid,String vehicleId,Tuple vehicle,Tuple customer,Tuple common,String subCoverId,Tuple drivers) {
		Map<String,List<String>> vloop=new HashMap<String, List<String>>();
		try {
			
			
			
				final List<RatingInfo> rateInfos = crservice.LoadRatingType(engine, factorid);
				
				
					
				 

				//List<String> condtions=new ArrayList<String>();
				for (RatingInfo r : rateInfos) {
					if("MsCustomerDetails".equalsIgnoreCase(r.getInputTableName())) {
						r.setInputColumValue(customer.get(r.getInputColumName()).toString());
					}else if("MsCommonDetails".equalsIgnoreCase(r.getInputTableName())) {
						r.setInputColumValue(common.get(r.getInputColumName()).toString());
					}else if("MsDriverDetails".equalsIgnoreCase(r.getInputTableName())){
						r.setInputColumValue(drivers.get(r.getInputColumName()).toString());
					}else /*if("MS_Vehicle_DETAILS".equalsIgnoreCase(r.getInputTableName()) || "MSVehicleDETAILS".equalsIgnoreCase(r.getInputTableName()) 
							|| "MsHumanDetails".equalsIgnoreCase(r.getInputTableName()) || "MsAssetDetails".equalsIgnoreCase(r.getInputTableName()) )*/ {
						if (vehicle.get(r.getInputColumName()) instanceof BigDecimal) {
							r.setInputColumValue( ((BigDecimal) vehicle.get(r.getInputColumName())).toPlainString());
						}else
							r.setInputColumValue(vehicle.get(r.getInputColumName())!=null?vehicle.get(r.getInputColumName()).toString():"");
					}
					
					
					/*
					String condtion=r.getDiscretCol()+":"+r.getInputColumValue()+"";
					if("Y".equals(r.getFactorRangeYn())) {
						condtion=""+r.getInputColumValue()+"~"+r.getRangeFromCol()+"&"+r.getRangeToCol();  
					} 
					condtions.add(condtion);*/  
				}
			
				//vloop.put(vehicleId, condtions);


				List<Tuple> loopfactorrates = crservice.loopfactorrates(engine,vloop,coverId,subCoverId,rateInfos);

			/*	if(loopfactorrates==null || loopfactorrates.size()==0) {
					condtions.clear(); 
					vloop.clear();
					for(int i=0;i<rateInfos.size();i++) {							
						RatingInfo r = rateInfos.get(i);
						if("Y".equals(r.getFactorRangeYn())) {
							String condtion=""+r.getInputColumValue()+"~"+r.getRangeFromCol()+"&"+r.getRangeToCol(); 
							condtions.add(condtion); 
						}
					}
					//System.out.println("----------------------"+coverId);
					for(int i=0;i<rateInfos.size();i++) {							
						RatingInfo r = rateInfos.get(i);
						if("N".equals(r.getFactorRangeYn())) {
							String condtion=r.getDiscretCol()+":"+r.getInputColumValue()+";";
							if(condtions.size()>0)
								condtion=condtion.concat(StringUtils.join(condtions,';'));
							//List<> onlyquery =null;
							Long count =0L;
							try {
								count =	crservice.countfactorOnlyquery(engine,condtion, coverId,subCoverId);
								//System.out.println(" ----------------------"+coverId+""+count +condtion);
							}catch (Exception e) {
								e.printStackTrace();
							}	
							
							if(count<=0L) {
								r.setInputColumValue("99999");
								condtion=r.getDiscretCol()+":"+r.getInputColumValue()+";";
								condtions.add(condtion); 
							}else {
								condtion=r.getDiscretCol()+":"+r.getInputColumValue()+";";
								condtions.add(condtion);
							}
						}					
						 
					}
					//System.out.println("----------------------"+coverId);
					vloop.put(vehicleId, condtions);
					loopfactorrates =  crservice.loopfactorrates(engine,vloop,coverId,subCoverId); 
			 		 
				}*/
			
			return loopfactorrates;  
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}

	protected BigDecimal domath(String calctype, Double rate,BigDecimal si,BigDecimal exchangeRate) throws ParseException {
		BigDecimal d=BigDecimal.ZERO;
		if("P".equals(calctype)) {
			d = si.multiply(new BigDecimal(rate/100)/*, round*/);			
		 }else if("A".equals(calctype)) {
			d=(new BigDecimal(rate).divide(exchangeRate,3,RoundingMode.HALF_UP));// for foreign currency calculation we have to divide by exchange rate			
		 }else if("M".equals(calctype)) {
			 d = si.multiply(new BigDecimal(rate/1000)/*, round*/);			
		 }else if("X".equals(calctype)) {
			 d = si.multiply(new BigDecimal(rate));
		 }
		d = (BigDecimal) decimalFormat.parse(decimalFormat.format(d));
		return d;
	}
	
	protected BigDecimal domathTira(String calctype, Double rate,BigDecimal premium,BigDecimal exchangeRate) throws ParseException {
		BigDecimal d=BigDecimal.ZERO;
		//(3500/4)*100
		if("P".equals(calctype)) {
			d = premium.divide(new BigDecimal((rate>0D?rate:1D)),3, RoundingMode.HALF_UP ).multiply(new BigDecimal(100)); ///multiply(new BigDecimal(rate/100), round);			
		 }else if("A".equals(calctype)) {
			d=(new BigDecimal(rate));			
		 }else if("M".equals(calctype)) {
			 d = premium.divide(new BigDecimal((rate>0D?rate:1D))).multiply(new BigDecimal(1000));			
		 }else if("X".equals(calctype)) {
			 d = premium.multiply(new BigDecimal(rate));
		 }
		
		d = (BigDecimal) decimalFormat.parse(decimalFormat.format(d));
		return d;
	}
}
