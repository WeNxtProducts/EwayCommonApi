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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.CompanyTaxSetup;
import com.maan.eway.bean.MsCommonDetails;
import com.maan.eway.bean.MsCustomerDetails;
import com.maan.eway.bean.MsVehicleDetails;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.calculator.util.CoverCalculator;
import com.maan.eway.calculator.util.SplitDiscountUtils;
import com.maan.eway.calculator.util.SplitLoadingUtils;
import com.maan.eway.calculator.util.SplitSubCoverUtil;
import com.maan.eway.calculator.util.SubCoverCreationUtil;
import com.maan.eway.calculator.util.TaxUtils;
import com.maan.eway.repository.MsVehicleDetailsRepository;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.CoverException;
import com.maan.eway.res.calc.Discount;
import com.maan.eway.res.calc.Loading;
import com.maan.eway.res.calc.Tax;
import com.maan.eway.service.CalculatorEngine;
import com.maan.eway.upgrade.criteria.CriteriaService;
import com.maan.eway.upgrade.criteria.SpecCriteria;

@Service
public class CalculatorEngineService implements CalculatorEngine{

	 // 1.Section
	 // 2.Cover
	
	@Autowired
	private CriteriaService crservice;
	@Autowired
	private CoverCalculator calc;
	
	protected List<Tuple> commontbl=null;	
	protected List<Tuple> vehicles=null;
	protected List<Tuple> customers =null;
	protected List<Cover> calculatedcover=null;
	
	
	@Autowired
	private MsVehicleDetailsRepository msvech;
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
	
	public List<Tuple> LoadTax(CalcEngine engine) {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(CompanyTaxSetup.class, search, "taxId"); 
			
			result=crservice.getResult(criteria, 0, 50);

			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Cover>  calculator(CalcEngine engine) {
		List<Cover> retc=new ArrayList<Cover>();
		try {
			
			loadOnetimetable(engine);
			if((commontbl==null || commontbl.size()==0) || (vehicles==null || vehicles.size()==0) || (customers==null || customers.size()==0)) {
				System.out.println("::: Exception ");
				throw new Exception();
				
				 /*throw CoverException.builder().message("Exception :: onetime table not inserted")
				 .isError(true).build();*/
			}
			
			List<Tuple> taxes = LoadTax(engine);
			TaxUtils tzx=new TaxUtils(); 
			
			List<String> dependedcovers=new ArrayList<String>();
			dependedcovers.add("N");
			dependedcovers.add("Y");
			
			List<Tuple> totalcoverstuple = LoadCover(engine);
			
			
		 	for (String dependcover : dependedcovers) {
		 		List<Cover> totalcovers=new ArrayList<Cover>();
		 		List<Tuple> covers = totalcoverstuple.stream().filter(t-> dependcover.equals(t.get("dependentCoverYn").toString())).collect(Collectors.toList());
		 			 
				SplitDiscountUtils discountUtil=new  SplitDiscountUtils();				
				List<Discount> discounts = covers.stream().map(discountUtil).filter(d->d!=null).collect(Collectors.toList());
				
				SplitLoadingUtils loadingtuils=new SplitLoadingUtils();
				List<Loading> loadings = covers.stream().map(loadingtuils).filter(d->d!=null).collect(Collectors.toList());
				
				
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
				 Map<String, List<Cover>> subcovers = covers.stream().map(splitsub).filter(d->d!=null).collect(Collectors.groupingBy(Cover::getIsSubCover));
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
				 
				 
				 calc.setEngine(engine,retc,commontbl,vehicles,customers);
				 
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
		return retc;
	}
	
	public void loadOnetimetable(CalcEngine engine) {
		///One time table record
		try {
			SpecCriteria criteria =null;
			MsVehicleDetails findByVdRefno = msvech.findByVdRefno(Long.parseLong(engine.getVdRefNo()));
			System.out.println("findByVdRefno"+findByVdRefno.getChassisNumber());
			vehicles=null;
			while(vehicles==null) {
			String search="vdRefno:"+engine.getVdRefNo()+";vehicleId:"+engine.getVehicleId();
			  criteria = crservice.createCriteria(MsVehicleDetails.class, search, "vdRefno");
			  
			  vehicles = crservice.getResult(criteria, 0, 50);
			 
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
				 search="vdRefno:"+vdRefno+";vehicleId:"+engine.getVehicleId();
				  criteria = crservice.createCriteria(MsVehicleDetails.class, search, "vdRefno");
				  
				  vehicles = crservice.getResult(criteria, 0, 50);
				 
				  System.out.println("Vehicle record "+vdRefno+", vehicles is "+((vehicles==null || vehicles.isEmpty())?"empty":"Not an empty"));
				}
			
		//	if(customers==null) {
			search="cdRefno:"+cdRefno+";";
			 criteria = crservice.createCriteria(MsCustomerDetails.class, search, "cdRefno");
			  customers = crservice.getResult(criteria, 0, 50);
		//	}
			
		}
		
		}catch(Exception e) {e.printStackTrace();}
		
	}
	
	
	
}
