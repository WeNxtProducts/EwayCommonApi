package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Tuple;

import java.util.Set;

import com.maan.eway.bean.EwayFactorDetails;
import com.maan.eway.bean.EwayMotorMakemodelMaster;
import com.maan.eway.bean.EwayVehicleMakemodelMasterDetail;
import com.maan.eway.bean.FactorRateMaster;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;

public class PerilCalculator {
	
	protected RatingFactorsUtil crservice;
	protected CalcEngine engine;
	
	protected List<Tuple> result=null;	
	protected List<Tuple> vehicles=null;
	protected List<Tuple> customers =null;
	
	
	
	public PerilCalculator(RatingFactorsUtil crservice, CalcEngine engine, List<Tuple> result, List<Tuple> vehicles,
			List<Tuple> customers) {
		super();
		this.crservice = crservice;
		this.engine = engine;
		this.result = result;
		this.vehicles = vehicles;
		this.customers = customers;
	}

	protected SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy")  ;
	
	public void perilCalculator(Cover t) {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			//agencyCode:"+engine.getAgencyCode()+";branchCode:"+engine.getBranchCode()+";"
			
			EwayVehicleMakemodelMasterDetail vmaster=crservice.collectMakeModelDetails(engine,vehicles);
			
			String VehicleClass="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+56+";"+"param9:"+vehicles.get(0).get("vehicleClass").toString()+";";
			
			String CoverType="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+23+";param9:"+vehicles.get(0).get("insuranceClass").toString()+";";
		
			String SumInsured="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+101+";"+vehicles.get(0).get("sumInsured").toString()+"~param1&param2;";
			
			/*String ThirdPartyLiabilityLimit="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+56+";";
			*/
			String NumberOfVehicles="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+96+";"+vehicles.get(0).get("noOfVehicles").toString()+"~param1&param2;";
			
			String VehicleAge="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+105+";"+vehicles.get(0).get("manufactureAge").toString()+"~param1&param2;";
			
			String PolicyDuration="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+41+";"+vehicles.get(0).get("periodOfInsurance").toString()+"~param1&param2;";
			
			String LicenseDuration="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+43+";"+customers.get(0).get("licenseDuration").toString()+"~param1&param2;";;
			
			String PowerMassRatio="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+38+";"+vmaster.getPowerKw()+"~param1&param2;";
			
			
			String VehBodyType="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+54+";param9:"+vmaster.getBodyId()+";";
			 Long countBody =crservice.getCountFromRating(VehBodyType);
			 if(countBody<=0) {
				 VehBodyType="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
					+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+54+";param9:99999;";
			 }
			
			
			String FuelType="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+19+";param9:"+(vmaster.getFueltype().equalsIgnoreCase("Petrol")?"2":"1")+";";
			
			
			String VehicleGroup="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+67+";"+vmaster.getVehiclegroup()+"~param21&param22;";
			
			String VehicleUse="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+73+";param9:"+vehicles.get(0).get("motorUsage").toString()+";";
			
			String DriveClaimNum_12m_0m="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+4+";param9:"+vehicles.get(0).get("claimNum12m0m").toString()+";";
			
			countBody =crservice.getCountFromRating(DriveClaimNum_12m_0m);
			 if(countBody<=0) {
				 DriveClaimNum_12m_0m="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
					+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+4+";param9:99999;";
			 }
			 
			String DriveClaimNum_24m_12m="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+11+";param9:"+vehicles.get(0).get("claimNum24m12m").toString()+";";
			countBody =crservice.getCountFromRating(DriveClaimNum_24m_12m);
			 if(countBody<=0) {
				 DriveClaimNum_24m_12m= "companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
					+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+11+";param9:99999;";
			 }
			String DriveClaimNum_36m_24m="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+12+";param9:"+vehicles.get(0).get("claimNum36m24m").toString()+";";
			
			countBody =crservice.getCountFromRating(DriveClaimNum_36m_24m);
			 if(countBody<=0) {
				 DriveClaimNum_36m_24m="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
					+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+12+";param9:99999;";
			 }
			 
			String DriverAge="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+40+";param9:"+customers.get(0).get("gender").toString()+";param10:"+customers.get(0).get("age").toString()+";";
						
			String PaymentFreq="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+29+";param9:"+vehicles.get(0).get("paymentFrequency").toString()+";";
			
			String EngineSize="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+100+";"+vmaster.getEnginesizeCc()+"~param1&param2;";
			
			String MaritalStatus="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+10+";param9:"+customers.get(0).get("maritalStatus").toString()+";";
			
			String AreaGroup="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+68+";"+customers.get(0).get("areaGroup").toString()+"~param21&param22;";
			//  23
			
			Map<String,String> queries=new HashMap<String,String>();
			queries.put("VehicleGroup",VehicleGroup);
			queries.put("DriverAgeXGender",DriverAge);
			queries.put("EngineSize",EngineSize);
			queries.put("PowerMassRatio",PowerMassRatio);
			queries.put("PolicyDuration",PolicyDuration);
			queries.put("PaymentFreq",PaymentFreq);
			queries.put("VehicleUse",VehicleUse);
			queries.put("VehicleAge",VehicleAge);
			queries.put("Vehicle Class",VehicleClass);
			queries.put("MaritalStatus",MaritalStatus);
			queries.put("LicenseDuration",LicenseDuration);
			queries.put("NoClaims0to1",DriveClaimNum_12m_0m);
			queries.put("NoClaims1to2",DriveClaimNum_24m_12m);
			queries.put("NoClaims2to3",DriveClaimNum_36m_24m); 
			queries.put("FuelType",FuelType);
			queries.put("VehBodyType",VehBodyType);
			queries.put("AreaGroup",AreaGroup);
			queries.put("NumberOfVehicles",NumberOfVehicles);
			queries.put("CoverType",CoverType);
			queries.put("Base",SumInsured);
			
			for(Entry<String, String> entrySet : queries.entrySet()) {
				String key = entrySet.getKey();
				String dataquery = entrySet.getValue();
				
				 Long count =crservice.getCountFromRating(dataquery+"agencyCode:"+engine.getAgencyCode()+";");
				 if(count>0) {
					 queries.put(key, dataquery+"agencyCode:"+engine.getAgencyCode()+";"); 
				 }else {
					 queries.put(key, dataquery+"agencyCode:99999;"); 
				 }
				
			}
			Map<String,List<Tuple>> queriesResult=new HashMap<String, List<Tuple>>();
			for(Entry<String, String> entrySet : queries.entrySet()) {
				String key = entrySet.getKey();
				String dataquery = entrySet.getValue();
				List<Tuple> queryResult = crservice.getResult(dataquery);
				if(queryResult!=null && queryResult.size()>0) {
					queriesResult.put(key, queryResult);	
				}else {
					System.out.println("No Factor Found for "+key +"\nquery:"+dataquery);
				}
			}
			
			List<EwayFactorDetails> data = crservice.saveFactorDetails(queriesResult,engine,result,vehicles,customers,t);
			
			// for(EwayFactorDetails f :data)
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
