package com.maan.eway.service.impl; 
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.CompanyProrataMaster;
import com.maan.eway.bean.CompanyTaxSetup;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.MsAssetDetails;
import com.maan.eway.bean.MsCommonDetails;
import com.maan.eway.bean.MsCustomerDetails;
import com.maan.eway.bean.MsHumanDetails;
import com.maan.eway.bean.MsVehicleDetails;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.bean.UwQuestionsDetails;
import com.maan.eway.calculator.util.CoverCalculator;
import com.maan.eway.calculator.util.CoverFromFactor;
import com.maan.eway.calculator.util.DiscountFromFactor;
import com.maan.eway.calculator.util.LoadingFromFactor;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.calculator.util.SplitDiscountUtils;
import com.maan.eway.calculator.util.SplitLoadingUtils;
import com.maan.eway.calculator.util.SplitSubCoverUtil;
import com.maan.eway.calculator.util.SubCoverCreationUtil;
import com.maan.eway.calculator.util.TaxUtils;
import com.maan.eway.calculator.util.UwQuestionUtils;
import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.common.req.UpdateFactorRateReq;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.MsVehicleDetailsRepository;
import com.maan.eway.repository.UwQuestionsDetailsRepository;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.Discount;
import com.maan.eway.res.calc.Loading;
import com.maan.eway.res.calc.Tax;
import com.maan.eway.res.calc.UWReferrals;
import com.maan.eway.service.CalculatorEngine;
import com.maan.eway.service.FactorRateRequestDetailsService;
import com.maan.eway.upgrade.criteria.CriteriaService;
import com.maan.eway.upgrade.criteria.SpecCriteria;

@Service
public class CalculatorEngineService implements CalculatorEngine{

	 // 1.Section
	 // 2.Cover
	
	@Autowired
	private CriteriaService crservice;
	
	@Autowired 
	private RatingFactorsUtil ratingutil;
	/*@Autowired
	private CoverCalculator calc;
	*/
	protected List<Tuple> commontbl=null;	
	protected List<Tuple> vehicles=null;
	protected List<Tuple> customers =null;
	protected List<Cover> calculatedcover=null;
	protected List<Tuple> prorata=null;
	
	@Autowired
	private FactorRateRequestDetailsService fservice;
	
	@Autowired
	private MsVehicleDetailsRepository msvech;
	
	@Autowired
	private UwQuestionsDetailsRepository uwrepo;
	

	@Autowired
	private FactorRateRequestDetailsRepository repository;
	
	private SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy")  ;
	public void LoadSection(CalcEngine engine) {
	
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
	
			String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()+";status=Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(ProductSectionMaster.class, search, "coverId"); 
			 result=crservice.getResult(criteria, 0, 50);
			 	
			 System.out.println("result"+result.size());
 		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public List<Tuple> LoadCover(CalcEngine engine) {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()+";status:{Y,R};"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(SectionCoverMaster.class, search, "coverId"); 
			result=crservice.getResult(criteria, 0, 50);

			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	
	
	public synchronized EserviceMotorDetailsSaveRes  calculator(CalcEngine engine) {
		// Referal Checking.
		
		List<UWReferrals> referr=null;
		if(StringUtils.isNotBlank(engine.getRequestReferenceNo()) && StringUtils.isNotBlank(engine.getVehicleId())) {
	 
			List<UwQuestionsDetails> uwqs = uwrepo.findByCompanyIdAndProductIdAndRequestReferenceNoAndVehicleId(engine.getInsuranceId(),Integer.valueOf(engine.getProductId()),engine.getRequestReferenceNo(),Integer.valueOf(engine.getVehicleId()));
			if(!uwqs.isEmpty()) {
				List<UwQuestionsDetails> isreferral=uwqs.stream().filter(f-> "Y".equals(f.getIsReferral())).collect(Collectors.toList());
				UwQuestionUtils uts=new UwQuestionUtils();
				referr = isreferral.stream().map(uts).filter(d->d!=null).collect(Collectors.toList());
			}
			
		}
		
		
		List<Cover> retc=new ArrayList<Cover>();
		try {
			
			loadOnetimetable(engine);
			if((commontbl==null || commontbl.size()==0) || (vehicles==null || vehicles.size()==0) || (customers==null || customers.size()==0)) {
				System.out.println("::: Exception :: ");
				throw new Exception();
				
				 /*throw CoverException.builder().message("Exception :: onetime table not inserted")
				 .isError(true).build();*/
			}
			
			List<Tuple> taxes = ratingutil.LoadTax(engine);
			TaxUtils tzx=new TaxUtils(); 
			
			List<String> dependedcovers=new ArrayList<String>();
			dependedcovers.add("N");
			dependedcovers.add("Y");
			
			List<Tuple> totalcoverstuple = LoadCover(engine);
			
			
		 	for (String dependcover : dependedcovers) {
		 		List<Cover> totalcovers=new ArrayList<Cover>();
		 		List<Tuple> covers = totalcoverstuple.stream().filter(t-> dependcover.equals(t.get("dependentCoverYn").toString())).collect(Collectors.toList());
		 		List<Discount> discounts=null;
		 		List<Loading> loadings =null;
		 		if(covers!=null && covers.size()>0) {
		 			SplitDiscountUtils discountUtil=new  SplitDiscountUtils();				
		 			discounts = covers.stream().map(discountUtil).filter(d->d!=null).collect(Collectors.toList());

		 			SplitLoadingUtils loadingtuils=new SplitLoadingUtils();
		 			loadings = covers.stream().map(loadingtuils).filter(d->d!=null).collect(Collectors.toList());
		 		}
				
				 SplitSubCoverUtil splitsub=new SplitSubCoverUtil("N");
				 Map<String, List<Cover>> nonSubcovers = covers.stream().map(splitsub).filter(d->d!=null).collect(Collectors.groupingBy(Cover::getIsSubCover));
				 if(!nonSubcovers.isEmpty()) {
					 List<Cover> noncovers = nonSubcovers.get("N");					 //noncovers
					 if(!discounts.isEmpty() && !noncovers.isEmpty()) {
						 for(Cover c:noncovers) {
							 List<Discount> ds = discounts.stream().filter(d-> d.getDiscountforId().equals(c.getCoverId())).collect(Collectors.toList());
							 ds.stream().forEach(dss->dss.setSubCoverId(c.getSubCoverId()));
							 //List<Tax> taxey = taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							 c.setDiscounts(ds);
							 //c.setTaxes(taxey);
						 }
					 }
					 
					 if(!loadings.isEmpty() && !noncovers.isEmpty()) {
						 for(Cover c:noncovers) {
							 List<Loading> ds = loadings.stream().filter(d-> d.getLoadingforId().equals(c.getCoverId())).collect(Collectors.toList());
							 ds.stream().forEach(dss->dss.setSubCoverId(c.getSubCoverId()));
							 //List<Tax> taxey = taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							 c.setLoadings(ds);
							 //c.setTaxes(taxey);
						 }
					 }
					 
					 if(!noncovers.isEmpty()) {
						 for(Cover c:noncovers) {
							 List<Tax> taxey = taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							 c.setTaxes(taxey);
						 }
					 }
				 }
				
				 
				 
				 
				 splitsub=new SplitSubCoverUtil("Y");
				 Map<String, List<Cover>> subcovers = covers.stream().map(splitsub).filter(d->(d!=null && !"0".equals(d.getSubCoverId()))).collect(Collectors.groupingBy(Cover::getIsSubCover));
				 if(!subcovers.isEmpty()) {
					 List<Cover> noncovers = subcovers.get("Y");					 //noncovers
					 if(!discounts.isEmpty() && !noncovers.isEmpty()) {
						 for(Cover c:noncovers) {
							 List<Discount> ds = discounts.stream().filter(d-> d.getDiscountforId().equals(c.getCoverId())).collect(Collectors.toList());
							 ds.stream().forEach(dss->dss.setSubCoverId(c.getSubCoverId()));

							 List<Discount> dss=ds.stream().map(dx-> SerializationUtils.clone(dx)).collect(Collectors.toList());
							// List<Tax> taxez = taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							 c.setDiscounts(dss);
							// c.setTaxes(taxez);
						 }	
					 }
					 
					 if(!loadings.isEmpty() && !noncovers.isEmpty()) {
						 for(Cover c:noncovers) {
							 List<Loading> ds = loadings.stream().filter(d-> d.getLoadingforId().equals(c.getCoverId())).collect(Collectors.toList());
							 ds.stream().forEach(dss->dss.setSubCoverId(c.getSubCoverId()));

							 List<Loading> dss=ds.stream().map(dx-> SerializationUtils.clone(dx)).collect(Collectors.toList());
							 c.setLoadings(dss);
						 }	
					 }
					 if(!noncovers.isEmpty()) {
						 for(Cover c:noncovers) {
							 List<Tax> taxey = taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							 c.setTaxes(taxey);
						 }
					 }
					 
					 List<Cover> d = noncovers.stream().filter(SubCoverCreationUtil.distinctByKey(Cover::getCoverId)).collect(Collectors.toList());
					 List<Cover> subcov=new ArrayList<Cover>();
						for (Cover cover : d) {
							 List<Cover> subcover = noncovers.stream().filter(cv-> cv.getCoverId().equals(cover.getCoverId())).collect(Collectors.toList());
							 subcover.stream().forEach(s->s.setIsSubCover("N"));
							 //subcover.stream().forEach(s->s.setTaxes(new ArrayList<Tax>(taxez)));
							 Cover newcover=SerializationUtils.clone(cover);
							 newcover.setSubcovers(subcover);
							 newcover.setIsSubCover("Y");
							 newcover.setSubCoverId(null);
							 newcover.setSubCoverDesc(null);
							 newcover.setSubCoverName(null);
							 newcover.setDiscounts(null);
							 newcover.setLoadings(null);
							 newcover.setTaxes(null);
							 subcov.add(newcover);
						}
						subcovers.put("Y", subcov);						
				 }
				 
				 if(!nonSubcovers.isEmpty() && !subcovers.isEmpty() ) {
					 totalcovers=subcovers.get("Y");
					 totalcovers.addAll(nonSubcovers.get("N"));
				 }else if(!nonSubcovers.isEmpty() && subcovers.isEmpty()  ) {
					 totalcovers=nonSubcovers.get("N");
				 }else if( nonSubcovers.isEmpty() && !subcovers.isEmpty()  ) {
					 totalcovers=subcovers.get("Y");
				 }
				 
				 /*if(StringUtils.isNotBlank(engine.getVdRefNo()) && StringUtils.isNotBlank(engine.getCdRefNo())) {
					 //calc.setEngine(engine, retc);
					 
					 
				 } */
				 
				 CoverCalculator calc=new CoverCalculator();
				 calc.setEngine(engine,retc,commontbl,vehicles,customers,prorata,ratingutil);
				 
				 totalcovers.stream().forEach(calc);
				 //remove error records
				 totalcovers.removeIf(ll-> (ll.isNotsutable()));
				 retc.addAll(totalcovers);
				 Comparator<Cover> comp=Comparator.comparing(Cover::getCoverageType); 
				 retc.sort(comp); 
		}
				  
				 
		}/*catch(CoverException e) {
			e.printStackTrace();
		}*/catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			EserviceMotorDetailsSaveRes response=new EserviceMotorDetailsSaveRes();
			response.setCoverList(retc);
			response.setResponse("Saved Successfully");
			response.setRequestReferenceNo(engine.getRequestReferenceNo());
			//response.setCustomerReferenceNo(req.getCustomerReferenceNo());
			response.setVehicleId(engine.getVehicleId()) ;	
			response.setVdRefNo(engine.getVdRefNo());
			response.setCdRefNo(engine.getCdRefNo());
			response.setInsuranceId(engine.getInsuranceId());
			response.setSectionId(engine.getSectionId());
			response.setCreatedBy(engine.getCreatedBy());
			response.setProductId(engine.getProductId()); 
			response.setMsrefno(engine.getMsrefno());
			response.setUpdateas(null);
			response.setUwList(referr);
			
			fservice.saveFactorRateRequestDetails(response);
			
			//Update Premium,referral
			
			return  response ;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void loadOnetimetable(CalcEngine engine) {
		///One time table record
		try {
			SpecCriteria criteria =null;
			/*MsVehicleDetails findByVdRefno = msvech.findByVdRefno(Long.parseLong(engine.getVdRefNo()));
			System.out.println("findByVdRefno"+findByVdRefno.getChassisNumber());
			*/
			String oneProduct= ratingutil.collectProductType(engine);
			 
			vehicles=null;
			while(vehicles==null) {
				
				
				 if(oneProduct.equalsIgnoreCase("M")){
					 String search="vdRefno:"+engine.getVdRefNo()+";vehicleId:"+engine.getVehicleId();
					 criteria = crservice.createCriteria(MsVehicleDetails.class, search, "vdRefno");			  
					 vehicles = crservice.getResult(criteria, 0, 50);
				 }else if(oneProduct.equalsIgnoreCase("H")){
					 String search="vdRefno:"+engine.getVdRefNo()+";humanId:"+engine.getVehicleId();
					 criteria = crservice.createCriteria(MsHumanDetails.class, search, "vdRefno");			  
					 vehicles = crservice.getResult(criteria, 0, 50);
				 }else if(oneProduct.equalsIgnoreCase("A")){
					 String search="vdRefno:"+engine.getVdRefNo()+";locationId:"+engine.getVehicleId();
					 criteria = crservice.createCriteria(MsAssetDetails.class, search, "vdRefno");			  
					 vehicles = crservice.getResult(criteria, 0, 50);
				 }
			 
			  System.out.println("Vehicle record "+engine.getVdRefNo()+", vehicles is "+((vehicles==null || vehicles.isEmpty())?"empty":"Not an empty"));
			}
		
			
			
		String search="msRefno:"+engine.getMsrefno()+";";
		
		
		
		
		//if(result==null) {
			 criteria = crservice.createCriteria(MsCommonDetails.class, search, "msRefno"); 
			commontbl=crservice.getResult(criteria, 0, 50);
		//}
		if(commontbl!=null && commontbl.size()>0) {
			Tuple tuple = commontbl.get(0);
			String vdRefno=tuple.get("vdRefno").toString();
			String cdRefno=tuple.get("cdRefno").toString();
			  vehicles=null;
				while(vehicles==null) {
					
					 if(oneProduct.equals("M")){
						   search="vdRefno:"+engine.getVdRefNo()+";vehicleId:"+engine.getVehicleId();
						 criteria = crservice.createCriteria(MsVehicleDetails.class, search, "vdRefno");			  
						 vehicles = crservice.getResult(criteria, 0, 50);
					 }else if(oneProduct.equals("H")){
						   search="vdRefno:"+engine.getVdRefNo()+";humanId:"+engine.getVehicleId();
						 criteria = crservice.createCriteria(MsHumanDetails.class, search, "vdRefno");			  
						 vehicles = crservice.getResult(criteria, 0, 50);
					 }else if(oneProduct.equalsIgnoreCase("A")){
						 search="vdRefno:"+engine.getVdRefNo()+";locationId:"+engine.getVehicleId();
						 criteria = crservice.createCriteria(MsAssetDetails.class, search, "vdRefno");			  
						 vehicles = crservice.getResult(criteria, 0, 50);
					 }
				 
				 
				 
				  System.out.println("Vehicle record "+vdRefno+", vehicles is "+((vehicles==null || vehicles.isEmpty())?"empty":"Not an empty"));
				}
			
		//	if(customers==null) {
			 search="cdRefno:"+cdRefno+";";
			 criteria = crservice.createCriteria(MsCustomerDetails.class, search, "cdRefno");
			 customers = crservice.getResult(criteria, 0, 50);
		//	}
			
			 
			 if(vehicles!=null) {
				 String periodOfInsurance=(vehicles.get(0).get("periodOfInsurance")==null?"365":vehicles.get(0).get("periodOfInsurance").toString());
				  prorata = ratingutil.loadProRataData(engine, periodOfInsurance);
			  }
		}
		
		
		 
		
		}catch(Exception e) {e.printStackTrace();}
		
	}


	@Override
	public synchronized EserviceMotorDetailsSaveRes referalCalculator(CalcEngine request) {
		 try {
			   List<Cover> retc=new ArrayList<Cover>();
			   
			   	loadOnetimetable(request);
				if((commontbl==null || commontbl.size()==0) || (vehicles==null || vehicles.size()==0) || (customers==null || customers.size()==0)) {
					System.out.println("::: Exception :: ");
					throw new Exception();
					
					 /*throw CoverException.builder().message("Exception :: onetime table not inserted")
					 .isError(true).build();*/
				}
				
			   
			 	List<String> dependedcovers=new ArrayList<String>();
			 	
				dependedcovers.add("N");
				dependedcovers.add("Y");
				
				List<FactorRateRequestDetails> factors = repository.findByRequestReferenceNoAndVehicleIdOrderByCoverIdAsc(request.getRequestReferenceNo(), Integer.valueOf(request.getVehicleId()));
				
				//TaxFromFactor tzx=new TaxFromFactor(); 
				List<Tuple> taxes = ratingutil.LoadTax(request);
				TaxUtils tzx=new TaxUtils(); 
				
				for (String dependcover : dependedcovers) {
					List<Cover> totalcovers=new ArrayList<Cover>();
					List<FactorRateRequestDetails> covers = factors.stream().filter(f -> dependcover.equals(f.getDependentCoverYn())).collect(Collectors.toList());
				 
					DiscountFromFactor discountUtil=new DiscountFromFactor();
					List<Discount> discounts = covers.stream().map(discountUtil).filter(d->d!=null).collect(Collectors.toList());
					LoadingFromFactor loadingtuils=new LoadingFromFactor();
					List<Loading> loadings = covers.stream().map(loadingtuils).filter(d->d!=null).collect(Collectors.toList());
					
					 
					
					CoverFromFactor splitsub=new CoverFromFactor("N");
					Map<String, List<Cover>> nonSubcovers = covers.stream().map(splitsub).filter(d->d!=null).collect(Collectors.groupingBy(Cover::getIsSubCover));
					 if(!nonSubcovers.isEmpty()) {
						 List<Cover> noncovers = nonSubcovers.get("N");					 //noncovers
						 if(!discounts.isEmpty() && !noncovers.isEmpty()) {
							 for(Cover c:noncovers) {
								 List<Discount> ds = discounts.stream().filter(d-> d.getDiscountforId().equals(c.getCoverId())).collect(Collectors.toList());
								 ds.stream().forEach(dss->dss.setSubCoverId(c.getSubCoverId()));
								 //List<Tax> taxey = taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
								 c.setDiscounts(ds);
								 //c.setTaxes(taxey);
							 }
						 }
						 if(!loadings.isEmpty() && !noncovers.isEmpty()) {
							 for(Cover c:noncovers) {
								 List<Loading> ds = loadings.stream().filter(d-> d.getLoadingforId().equals(c.getCoverId())).collect(Collectors.toList());
								 ds.stream().forEach(dss->dss.setSubCoverId(c.getSubCoverId()));
								 //List<Tax> taxey = taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
								 c.setLoadings(ds);
								 //c.setTaxes(taxey);
							 }
						 }
						 if(!noncovers.isEmpty()) {
							 for(Cover c:noncovers) {
								 List<Tax> taxey = taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
								 c.setTaxes(taxey);
							 }
						 }
					 }
					 
					 splitsub=new CoverFromFactor("Y");
					 Map<String, List<Cover>> subcovers = covers.stream().map(splitsub).filter(d->(d!=null && !"0".equals(d.getSubCoverId()))).collect(Collectors.groupingBy(Cover::getIsSubCover));
					 if(!subcovers.isEmpty()) {
						 List<Cover> noncovers = subcovers.get("Y");					 //noncovers
						 if(!discounts.isEmpty() && !noncovers.isEmpty()) {
							 for(Cover c:noncovers) {
								 List<Discount> ds = discounts.stream().filter(d-> d.getDiscountforId().equals(c.getCoverId())).collect(Collectors.toList());
								 ds.stream().forEach(dss->dss.setSubCoverId(c.getSubCoverId()));

								 List<Discount> dss=ds.stream().map(dx-> SerializationUtils.clone(dx)).collect(Collectors.toList());
								// List<Tax> taxez = taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
								 c.setDiscounts(dss);
								// c.setTaxes(taxez);
							 }	
						 }
						 
						 if(!loadings.isEmpty() && !noncovers.isEmpty()) {
							 for(Cover c:noncovers) {
								 List<Loading> ds = loadings.stream().filter(d-> d.getLoadingforId().equals(c.getCoverId())).collect(Collectors.toList());
								 ds.stream().forEach(dss->dss.setSubCoverId(c.getSubCoverId()));

								 List<Loading> dss=ds.stream().map(dx-> SerializationUtils.clone(dx)).collect(Collectors.toList());
								 c.setLoadings(dss);
							 }	
						 }
						 if(!noncovers.isEmpty()) {
							 for(Cover c:noncovers) {
								 List<Tax> taxey = taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
								 c.setTaxes(taxey);
							 }
						 }
						 
						 List<Cover> d = noncovers.stream().filter(SubCoverCreationUtil.distinctByKey(Cover::getCoverId)).collect(Collectors.toList());
						 List<Cover> subcov=new ArrayList<Cover>();
							for (Cover cover : d) {
								 List<Cover> subcover = noncovers.stream().filter(cv-> cv.getCoverId().equals(cover.getCoverId())).collect(Collectors.toList());
								 subcover.stream().forEach(s->s.setIsSubCover("N"));
								 //subcover.stream().forEach(s->s.setTaxes(new ArrayList<Tax>(taxez)));
								 Cover newcover=SerializationUtils.clone(cover);
								 newcover.setSubcovers(subcover);
								 newcover.setIsSubCover("Y");
								 newcover.setSubCoverId(null);
								 newcover.setSubCoverDesc(null);
								 newcover.setSubCoverName(null);
								 newcover.setDiscounts(null);
								 newcover.setLoadings(null);
								 newcover.setTaxes(null);
								 subcov.add(newcover);
							}
							subcovers.put("Y", subcov);						
					 }
					 
					 if(!nonSubcovers.isEmpty() && !subcovers.isEmpty() ) {
						 totalcovers=subcovers.get("Y");
						 totalcovers.addAll(nonSubcovers.get("N"));
					 }else if(!nonSubcovers.isEmpty() && subcovers.isEmpty()  ) {
						 totalcovers=nonSubcovers.get("N");
					 }else if( nonSubcovers.isEmpty() && !subcovers.isEmpty()  ) {
						 totalcovers=subcovers.get("Y");
					 }
					 
					 CoverCalculator calc=new CoverCalculator();
					 calc.setEngine(request,retc,commontbl,vehicles,customers,prorata,ratingutil);
					 
					 totalcovers.stream().forEach(calc);
					 //remove error records
					 totalcovers.removeIf(ll-> (ll.isNotsutable()));
					 retc.addAll(totalcovers);
					 Comparator<Cover> comp=Comparator.comparing(Cover::getCoverageType); 
					 retc.sort(comp); 
				}
				try {
					EserviceMotorDetailsSaveRes response=new EserviceMotorDetailsSaveRes();
					response.setCoverList(retc);
					response.setResponse("Saved Successfully");
					response.setRequestReferenceNo(request.getRequestReferenceNo());
					//response.setCustomerReferenceNo(req.getCustomerReferenceNo());
					response.setVehicleId(request.getVehicleId()) ;	
					response.setVdRefNo(request.getVdRefNo());
					response.setCdRefNo(request.getCdRefNo());
					response.setInsuranceId(request.getInsuranceId());
					response.setSectionId(request.getSectionId());
					response.setCreatedBy(request.getCreatedBy());
					response.setProductId(request.getProductId()); 
					response.setMsrefno(request.getMsrefno());
					response.setUpdateas("admin");
					//response.setUwList(referr);
					
					fservice.saveFactorRateRequestDetails(response);
					
					//Update Premium,referral
					
					return  response ;
				}catch (Exception e) {
					e.printStackTrace();
				}		 
		 }catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}
	
	
	
}
