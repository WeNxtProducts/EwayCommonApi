package com.maan.eway.service.impl; 
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.MsAssetDetails;
import com.maan.eway.bean.MsCommonDetails;
import com.maan.eway.bean.MsCustomerDetails;
import com.maan.eway.bean.MsHumanDetails;
import com.maan.eway.bean.MsVehicleDetails;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.calculator.util.AdminCoverCalculator;
import com.maan.eway.calculator.util.CoverCalculator;
import com.maan.eway.calculator.util.CoverFromFactor;
import com.maan.eway.calculator.util.DiscountFromFactor;
import com.maan.eway.calculator.util.EndtCoverCalculator;
import com.maan.eway.calculator.util.EndtFromFactor;
import com.maan.eway.calculator.util.LoadingFromFactor;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.calculator.util.SplitDiscountUtils;
import com.maan.eway.calculator.util.SplitLoadingUtils;
import com.maan.eway.calculator.util.SplitSubCoverUtil;
import com.maan.eway.calculator.util.SubCoverCreationUtil;
import com.maan.eway.calculator.util.TaxUtils;
import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.common.service.QuoteService;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.endorsment.util.CoverFromPolicy;
import com.maan.eway.endorsment.util.DiscountFromPolicy;
import com.maan.eway.endorsment.util.EndtFromPolicy;
import com.maan.eway.endorsment.util.LoadingFromPolicy;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.LoginProductMasterRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.req.calcengine.CalcCommission;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.DebitAndCredit;
import com.maan.eway.res.calc.Discount;
import com.maan.eway.res.calc.Endorsement;
import com.maan.eway.res.calc.Loading;
import com.maan.eway.res.calc.Tax;
import com.maan.eway.res.calc.UWReferrals;
import com.maan.eway.res.referal.MasterReferal;
import com.maan.eway.service.CalculatorEngine;
import com.maan.eway.service.FactorRateRequestDetailsService;
import com.maan.eway.service.PolicyDrcrDetailService;
import com.maan.eway.service.impl.referal.ReferalServiceImpl;
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
	private FactorRateRequestDetailsRepository repository;
	
	
	@Autowired
	private ReferalServiceImpl referal;
	
	@Autowired
	private QuoteService quoteservice;
	
	private SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy")  ;
	
	@Autowired
	private LoginProductMasterRepository loginProductrepo;
	
	@Autowired
	private PolicyDrcrDetailService crdrservice;
	
	@Autowired
	private GenerateSeqNoServiceImpl genNo;
	
	
	@Autowired
	private PolicyCoverDataRepository coverDataRepo;
	/*public void LoadSection(CalcEngine engine) {
	
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
	}*/
	
	
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
	
	
	
	public synchronized EserviceMotorDetailsSaveRes  calculator(CalcEngine engine,String token) {
		// Referal Checking.
		
		List<UWReferrals> referr = referal.underwriterReferral(engine);
		
		List<MasterReferal> masterreferral=null;
		try {
			masterreferral = referal.masterreferral(engine, token);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
		 	try {
		 		String endtTypeId=vehicles.get(0).get("endtTypeId")==null?"":vehicles.get(0).get("endtTypeId").toString();
		 		if(StringUtils.isNotBlank(endtTypeId) && !"0".equals(endtTypeId)) {
		 			loadAndRemoveCoversForEndt(engine,retc);
		 		}		 	
		 	}catch (Exception e) {
		 		e.printStackTrace();
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
			response.setReferals(masterreferral);
			fservice.saveFactorRateRequestDetails(response);
			
			//Update Premium,referral
			

			/// Endoresment calculation
			try {
		 		String endtTypeId=vehicles.get(0).get("endtTypeId")==null?"":vehicles.get(0).get("endtTypeId").toString();
		 		if(StringUtils.isNotBlank(endtTypeId) && !"0".equals(endtTypeId)) {
		 			// referalCalculator = referalCalculator(engine);
		 			endorsementCalculator(engine);
		 			
		 		}		 	
		 	}catch (Exception e) {
		 		e.printStackTrace();
			}
				
			
			return  response ;
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		 
		
		return null;
	}
	



	
	private void loadAndRemoveCoversForEndt(CalcEngine engine, List<Cover> retc) {
		try {
			
			String requestRefercenNo=vehicles.get(0).get("requestReferenceNo").toString();
			String rawtable = ratingutil.getProductIdBasedRawTable(engine);
			
			
			if(StringUtils.isNotBlank(rawtable)) {
				 String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()+";riskId:"+engine.getVehicleId()+";status:E;requestReferenceNo:"+requestRefercenNo+";";
				//String search="riskId:"+engine.getVehicleId()+";requestReferenceNo:"+requestRefercenNo+";";
				List<Tuple> result=null;
				SpecCriteria criteria = crservice.createCriteria(Class.forName(rawtable), search, "requestReferenceNo"); 
				result=crservice.getResult(criteria, 0, 50);
				
				
				//String endtPrevPolicyNo=result.get(0).get("endtPrevPolicyNo").toString();
				String endtPrevQuoteNo=result.get(0).get("endtPrevQuoteNo").toString();
				
				String endtDesc=result.get(0).get("endorsementTypeDesc").toString();
				String endtTypeId=result.get(0).get("endorsementType").toString();
				BigDecimal endtCount=new BigDecimal(result.get(0).get("endtCount").toString());
				
				
				
				 
				 
				//find Prev Quote Data
					List<PolicyCoverData> oldPolicyCovers = coverDataRepo.findByQuoteNoAndVehicleIdOrderByCoverIdAsc(endtPrevQuoteNo,Integer.parseInt(engine.getVehicleId()));
					List<Tuple> taxes = ratingutil.LoadTax(engine);
					TaxUtils tzx=new TaxUtils(); 
					
					
					//CoverFromPolicy
					List<PolicyCoverData> basecovers=oldPolicyCovers.stream().filter( d -> !("T".equals(d.getCoverageType()) || "D".equals(d.getCoverageType()) || "L".equals(d.getCoverageType()) || "E".equals(d.getCoverageType())))
					.collect(Collectors.toList());
				for (PolicyCoverData d : basecovers) {
					List<Cover> operatedList=new ArrayList<Cover>();
					
					DiscountFromPolicy discountUtil=new DiscountFromPolicy();
					List<Discount> discounts = oldPolicyCovers.stream().filter(r -> d.getCoverId()==r.getCoverId())
							.map(discountUtil).filter(dx->dx!=null).collect(Collectors.toList());
					
					LoadingFromPolicy loadingUtil=new LoadingFromPolicy();
					List<Loading> loadings = oldPolicyCovers.stream().filter(r -> d.getCoverId()==r.getCoverId()).map(loadingUtil).filter(dx->dx!=null).collect(Collectors.toList());
					
					
					EndtFromPolicy endtUtils=new EndtFromPolicy();
					List<Endorsement> endorsements = oldPolicyCovers.stream().filter(r -> d.getCoverId()==r.getCoverId()).map(endtUtils).filter(dx->dx!=null).collect(Collectors.toList());
					
					
					 //CurrentEndorsement
					 Endorsement currentEndt=Endorsement.builder()
							 	.endorsementDesc(d.getCoverDesc()+" "+endtDesc+" "+endtCount.intValue())
							 	.endorsementId(endtTypeId)
							 	.endorsementRate("0")
							 	.endorsementCalcType("A")
							 	.endorsementforId(String.valueOf( d.getCoverId()))
							 	.maxAmount(BigDecimal.ZERO)
							 	.factorTypeId(null)
							 	.regulatoryCode("N/A")	
							 	.endtCount(endtCount)
							 	.premiumAfterDiscount(d.getPremiumAfterDiscountFc())
							    .premiumAfterDiscountLC(d.getPremiumAfterDiscountLc())
							    .premiumBeforeDiscount(d.getPremiumBeforeDiscountFc())
							    .premiumBeforeDiscountLC(d.getPremiumBeforeDiscountLc())
							    .premiumExcluedTax(d.getPremiumExcludedTaxFc())
							    .premiumExcluedTaxLC(d.getPremiumExcludedTaxLc())
							    .premiumIncludedTax(d.getPremiumIncludedTaxFc())
							    .premiumIncludedTaxLC(d.getPremiumIncludedTaxLc())
							 	.build();
					 endorsements.add(currentEndt);
					
					
					CoverFromPolicy coverUtil=new CoverFromPolicy("");
					List<Cover> covers = oldPolicyCovers.stream().filter(r -> d.getCoverId()==r.getCoverId()).map(coverUtil).filter(dx->dx!=null).collect(Collectors.toList());
					List<Cover> oldTax = covers.stream().filter(c -> "T".equals(c.getCoverageType())).collect(Collectors.toList());
					covers.removeAll(oldTax);
					
					List<Tax> taxey = taxes.stream().map(tzx).filter(t->t!=null).collect(Collectors.toList());
					covers.forEach(c -> c.setTaxes(taxey));
					
					covers.forEach(c ->c.setEndorsements(endorsements));// Existing Endorsement
					covers.forEach(c -> c.setDiscounts(discounts));
					covers.forEach(c -> c.setLoadings(loadings));
					
					
					
					retc.stream().filter(r -> d.getCoverId()==Integer.parseInt(r.getCoverId())).forEach(item -> {		
						operatedList.add(item);
					});
					retc.removeAll(operatedList);	
					retc.addAll(covers); 
				}
			}
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private EserviceMotorDetailsSaveRes endorsementCalculator(CalcEngine request) {
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
				
				List<FactorRateRequestDetails> factors = repository.findByRequestReferenceNoAndVehicleIdAndProductIdAndSectionIdOrderByCoverIdAsc(request.getRequestReferenceNo(), Integer.valueOf(request.getVehicleId()),Integer.valueOf(request.getProductId()),Integer.valueOf(request.getSectionId()));
				
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
					EndtFromFactor endtUtil=new EndtFromFactor();
					List<Endorsement> endorsements = covers.stream().map(endtUtil).filter(d->d!=null).collect(Collectors.toList());
					
					
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
						 
						 if(!endorsements.isEmpty() && !noncovers.isEmpty()) {
							 for(Cover c:noncovers) {
								 List<Endorsement> ds = endorsements.stream().filter(d-> d.getEndorsementforId().equals(c.getCoverId())).collect(Collectors.toList());
								 ds.stream().forEach(dss->dss.setSubCoverId(c.getSubCoverId()));
								 //List<Tax> taxey = taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
								 c.setEndorsements(ds);
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
						 
						 if(!endorsements.isEmpty() && !noncovers.isEmpty()) {
							 for(Cover c:noncovers) {
								 List<Endorsement> ds = endorsements.stream().filter(d-> d.getEndorsementforId().equals(c.getCoverId())).collect(Collectors.toList());
								 ds.stream().forEach(dss->dss.setSubCoverId(c.getSubCoverId()));
								 List<Endorsement> dss=ds.stream().map(dx-> SerializationUtils.clone(dx)).collect(Collectors.toList());
								 c.setEndorsements(dss);
								 //c.setTaxes(taxey);
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
					 
					 
					 EndtCoverCalculator calc=new EndtCoverCalculator();
					 calc.setEngine(request,retc,commontbl,vehicles,customers,prorata,ratingutil);
					 
					 totalcovers.stream().forEach(calc);
					 //remove error records
					 totalcovers.removeIf(ll-> (ll.isNotsutable()));
					 retc.addAll(totalcovers);
					Comparator<Cover> comp=Comparator.comparing(Cover::getCoverageType); 
					 retc.sort(comp);
					 
				//	 x
					 
					 
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


	public void loadOnetimetable(CalcEngine engine) {
		///One time table record
		try {
			SpecCriteria criteria =null;
			/*
			MsVehicleDetails findByVdRefno = msvech.findByVdRefno(Long.parseLong(engine.getVdRefNo()));
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
				
				List<FactorRateRequestDetails> factors = repository.findByRequestReferenceNoAndVehicleIdAndProductIdAndSectionIdOrderByCoverIdAsc(request.getRequestReferenceNo(), Integer.valueOf(request.getVehicleId()),Integer.valueOf(request.getProductId()),Integer.valueOf(request.getSectionId()));
				
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
					 
					// CoverCalculator calc=new CoverCalculator();
					 AdminCoverCalculator calc=new AdminCoverCalculator();
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


	@Override
	public  List<DebitAndCredit> commissionCalc(CalcCommission request) {
		 try {
			
			 ViewQuoteReq q=new ViewQuoteReq();
			 q.setQuoteNo(request.getQuoteno());
			 ViewQuoteRes v = quoteservice.viewQuoteDetails(q);
			
			 
			 List<LoginProductMaster> lp = loginProductrepo.findByLoginIdAndCompanyIdAndProductIdAndStatusOrderByEntryDateDesc(v.getQuoteDetails().getLoginId(),request.getInsuranceId() , Integer.parseInt(request.getProductId()), "Y");
			 
			 
			 Integer commissionPercent = lp.get(0).getCommissionPercent()==null?0:lp.get(0).getCommissionPercent();
			 String commissionVatYn = lp.get(0).getCommissionVatYn()==null?"N": lp.get(0).getCommissionVatYn();
			 String premiumFc = v.getQuoteDetails().getPremiumFc();
			 String vatPremiumFc =	v.getQuoteDetails().getVatPremiumFc();
			 BigDecimal commission=	new BigDecimal(premiumFc)
					 				.multiply(new BigDecimal(commissionPercent))
			 						.divide(BigDecimal.valueOf(100D))
			 						.setScale(new MathContext(3, RoundingMode.HALF_UP)
			 						.getPrecision(),RoundingMode.HALF_UP);
			 v.getQuoteDetails().getVatPercent();
			 BigDecimal commissionVat=BigDecimal.ZERO;
			 List<Map<String,Object>> rules=new ArrayList<Map<String,Object>>();
					 
			 // Setup
			 Map<String,Object> setup=new HashMap<String, Object>();
				 
			 List<Map<String,Object>> csubsets=new ArrayList<Map<String,Object>>();
			 {
				 Map<String,Object> subset=new HashMap<String, Object>();
				 subset.put("CHARGE_CODE", "1001");
				 subset.put("CHARGE_CODE_DESC", "Premium");
				 subset.put("CHARGE_CODE_VALUE",premiumFc);
				 csubsets.add(subset);
			 }
			 {
				 Map<String,Object> subset=new HashMap<String, Object>();
				 subset.put("CHARGE_CODE", "1012");
				 subset.put("CHARGE_CODE_DESC", "VAT");
				 subset.put("CHARGE_CODE_VALUE",vatPremiumFc);
				 csubsets.add(subset);
			 }
			
			 
			 
			 List<Map<String,Object>> bsubsets=new ArrayList<Map<String,Object>>();
			 {
				 Map<String,Object> subset=new HashMap<String, Object>();
				 subset.put("CHARGE_CODE", "1005");
				 subset.put("CHARGE_CODE_DESC", "Commission");
				 subset.put("CHARGE_CODE_VALUE",commission);
				 bsubsets.add(subset);
			 }
			  
			 {
				 Map<String,Object> subset=new HashMap<String, Object>();
				 subset.put("CHARGE_CODE", "1007");
				 subset.put("CHARGE_CODE_DESC", "Commission%");
				 subset.put("CHARGE_CODE_VALUE",commissionPercent);
				 bsubsets.add(subset);
			 }
			  
			 if(commissionVatYn.equals("Y"))
			 {
				 commissionVat=commission
			 				.multiply(new BigDecimal(v.getQuoteDetails().getVatPercent()))
	 						.divide(BigDecimal.valueOf(100D))
	 						.setScale(new MathContext(3, RoundingMode.HALF_UP)
	 						.getPrecision(),RoundingMode.HALF_UP);
	 
				 
				 Map<String,Object> subset=new HashMap<String, Object>();
				 subset.put("CHARGE_CODE", "1012");
				 subset.put("CHARGE_CODE_DESC", "COMMISSON_VAT");
				 subset.put("CHARGE_CODE_VALUE",commissionVat);
				 bsubsets.add(subset);
			 }
			  
			 
			 setup.put("<CUSTOMER>", csubsets);
			 setup.put("<BROKER>", bsubsets);
			 
			 //-----------------------------
			 
			 
			 //Rule 
			 Map<String,Object> rule1=new HashMap<String, Object>();
			 rule1.put("DEBIT", "<CUSTOMER>");
			 rule1.put("CREDIT","<BROKER>");			 	 
			 rules.add(rule1);

			 String crnumber= "CN-"+genNo.generateCreditNo(); //ThreadLocalRandom.current().ints(1001, 4999).distinct().limit(5).findAny().toString();
			 String drnumber= "DN-"+genNo.generateDebitNo(); //ThreadLocalRandom.current().ints(4999, 9999).distinct().limit(5).findAny().toString();
			 String policyNo= genNo.generatePolicyNo();
			 request.setPolicyNo(policyNo);
			 int rownum=1;
			 
			 List<DebitAndCredit> result=new ArrayList<DebitAndCredit>();
			 
			 
			 for (Map<String, Object> map : rules) {
				for (Entry<String, Object> m : map.entrySet()) {
					
					 List<Map<String,Object>> dd=( List<Map<String,Object>>) setup.get(m.getValue());
					 
					 for (Map<String, Object> s : dd) {
						 			
						 String doctype=m.getValue().equals("<CUSTOMER>")?"C":"B";
						 
						 		DebitAndCredit dc = DebitAndCredit.builder()
						 			.amountFc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()))
						 			.amountLc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()))
						 			.branchCode(request.getBranchCode())
									.chargeCode(new BigDecimal(s.get("CHARGE_CODE").toString()))
									.chgId(new BigDecimal(rownum++))
									.companyId(request.getInsuranceId())
									.docId(doctype.equals("C")?v.getCustomerDetails().getCustomerId():v.getQuoteDetails().getLoginId())
									.docNo(m.getKey().equals("DEBIT")?drnumber:crnumber)
									.docType(doctype)
									.drcrFlag(m.getKey().equals("DEBIT")?"DR":"CR")
									.entryDate(new Date())
									.policyNo(request.getPolicyNo())
									.productId(request.getProductId())
									.quoteNo(request.getQuoteno())
									.status("Y")
									.quoteInfo(v)
									.build();
						 		result.add(dc);
					 }   	
				}
			 }
			 crdrservice.insertDRCR(result, request.getQuoteno());
			 return result;
			 
			 
			 
			 
		 }catch (Exception e) {
			 e.printStackTrace();
		 }
		return null;
	}
	
	
	
}
