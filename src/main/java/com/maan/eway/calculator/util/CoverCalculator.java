package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.Tuple;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.CoverException;

@Component
public class CoverCalculator extends CommonCalculator implements Consumer<Cover> {
	
	
	/*@Autowired
	private CoverCalculator calc;*/
	
	@Override
	public void accept(Cover t) {
		 try {
			
			 if("Y".equals( t.getIsSubCover())) {
				 //this.setEngine(engine);
				 t.getSubcovers().stream().forEach(this);
			 }else {

			//	 loadOnetimetable(engine);
				 boolean discountLoading=true;
				 
				 BigDecimal exchangeRate= new BigDecimal(vehicles.get(0).get("exchangeRate")==null?"1":vehicles.get(0).get("exchangeRate").toString());
				 t.setExchangeRate(exchangeRate);
				 String currecy=vehicles.get(0).get("currency")==null?"N/A":vehicles.get(0).get("currency").toString();
				 t.setCurrency(currecy);
				 
				 t.setProRata(new BigDecimal("1"));
				 if(prorata!=null && prorata.size()>0 && "Y".equals(t.getProRataYn())) {
					 BigDecimal percenat=prorata.get(0).get("percent")==null?BigDecimal.ZERO:new BigDecimal(prorata.get(0).get("percent").toString());	
					 t.setProRata(percenat.divide(new BigDecimal("100"),MathContext.DECIMAL32));
				 }else if("D".equals(t.getProRataYn())) {
					String periodOfInsurance =vehicles.get(0).get("periodOfInsurance") == null ? "365": vehicles.get(0).get("periodOfInsurance").toString();
					t.setPolicyPeriod(new BigDecimal(periodOfInsurance));
					t.setProRata(t.getPolicyPeriod().divide(new BigDecimal("365") ,MathContext.DECIMAL32));
				 }
				 
				 /// this particular variable is for is rate defined for Single
				 String rateFor=vehicles.get(0).get("groupCount")==null?"1":vehicles.get(0).get("groupCount").toString();
				 
				 
				 
				 
				 
				 BigDecimal si=BigDecimal.ZERO;
				 if(!"A".equals(t.getCalcType()))
					 si=vehicles.get(0).get(t.getCoverBasedOn())==null?BigDecimal.ZERO:new BigDecimal(vehicles.get(0).get(t.getCoverBasedOn()).toString());
				 
				 if("Y".equals(t.getDependentCoveryn())) {
					 if(calculatedcover!=null) {
						Cover ct = calculatedcover.stream().filter(c->c.getCoverId().equals(t.getDependentCoverId())).findAny().orElse(null);
						si=ct!=null?ct.getPremiumExcluedTax():BigDecimal.ZERO;
					}
				 }		
				 si=si.subtract(t.getFreeCoverLimit());
				 si=si.compareTo(BigDecimal.ZERO)>0?si:BigDecimal.ZERO;
				 t.setSumInsured(si);
				 t.setSumInsuredLc(si.multiply(exchangeRate,MathContext.DECIMAL64));
				 //t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0
				 if(t.getSumInsured().compareTo(t.getCoverageLimit())>0) {
					 t.setIsReferral("Y");
					 t.setReferalDescription("CoverageLimit Referral Limits Upto"+t.getCoverageLimit());
					 t.setPremiumBeforeDiscount(BigDecimal.ZERO);					 
					 t.setPremiumBeforeDiscountLC(BigDecimal.ZERO);
					 t.setCalcType("P");
				 }else if(t.getSumInsured().compareTo(t.getMinSumInsured())<0) {
					    discountLoading=false;
						CoverException build = CoverException.builder().message(t.getCoverName()+ "Min SumInsured is:"+t.getMinSumInsured()+ " & SumInsured:"+t.getSumInsured())
						.isError(true).build();
						t.setError(build);
						t.setNotsutable(true);
						throw build;
				 } if("F".equals(t.getCalcType())) {
					 // Tuple vehicle,Tuple customer,Tuple common
					 List<Tuple> factors = LoadFactorRates(engine, t.getCoverId(),t.getFactorTypeId(),engine.getVehicleId(),StringUtils.isBlank(t.getSubCoverId())?"0":t.getSubCoverId());
					 
					 /*if(factors==null || factors.size()==0) 
						 throw CoverException.builder().message("Not Found Result "+"CoverID:"+t.getCoverId()+"<Desc>:"+t.getCoverDesc()+",subcoverId:"+t.getSubCoverId())
					 .isError(true).build();*/
						 
					 Tuple tuple = null;
					 try {
						 tuple=factors.get(0);
					 }catch (Exception e) {
						// TODO: handle exception
						 discountLoading=false;
						CoverException build = CoverException.builder().message("No factor found")
						 .isError(true).build();
						 t.setError(build);
						 t.setNotsutable(true);
						 throw build;
						 /*t.setIsReferral("Y");
						 t.setReferalDescription("No factor found Referral for "+t.getCoverDesc());
						 t.setPremiumBeforeDiscount(BigDecimal.ZERO);					 
						 t.setPremiumBeforeDiscountLC(BigDecimal.ZERO);*/
					}
					 if(tuple!=null) {
						 String calctype=tuple.get("calcType").toString();
						 String rate=tuple.get("rate")==null?"0":tuple.get("rate").toString();
						 String regulatoryCode=tuple.get("regulatoryCode")==null?"N/A":tuple.get("regulatoryCode").toString();

						 t.setRate((Double) ((Double.parseDouble(rate)*Double.parseDouble(rateFor))));

						 t.setMinimumPremium(tuple.get("minPremium")==null?BigDecimal.ZERO:new BigDecimal(tuple.get("minPremium").toString())/*.divide(t.getExchangeRate(),round)*/);
						 BigDecimal domath = domath(calctype, t.getRate(), si,t.getExchangeRate());
						 t.setPremiumBeforeDiscount(domath);

						 t.setPremiumBeforeDiscountLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate())))) ;
						 t.setCalcType(calctype);
						 t.setRegulatoryCode(regulatoryCode);
						 /// Referal
						 t.setIsReferral((tuple.get("status")==null?"N":tuple.get("status").toString()).equals("R")?"Y":"N");
						 if("Y".equals(t.getIsReferral())){
							 t.setReferalDescription(t.getCoverDesc() +" Referral" );
							
						 }
						 t.setExcessAmount(tuple.get("excessAmount")==null?BigDecimal.ZERO:new BigDecimal(tuple.get("excessAmount").toString()));
						 t.setExcessDesc(tuple.get("excessDesc")==null?"":tuple.get("excessDesc").toString());
						 t.setExcessPercent(tuple.get("excessPercent")==null?BigDecimal.ZERO:new BigDecimal(tuple.get("excessPercent").toString()));
					 }
				 }else if("FD".equals(t.getCalcType())){
					 PerilCalculator calc=new PerilCalculator(crservice, engine, result, vehicles, customers);
					 calc.perilCalculator(t);
					 t.setPremiumBeforeDiscountLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate())))) ;
					 t.getLoadings().clear();
					 //t.getDiscounts().clear();
					 discountLoading=false;
				 }else {
					 t.setRate((t.getRate()*Double.parseDouble(rateFor)));
					 
					 BigDecimal domath = domath(t.getCalcType(), t.getRate(), si,t.getExchangeRate());
					 t.setPremiumBeforeDiscount(domath);					 
					 t.setPremiumBeforeDiscountLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate())))) ;
				 }
				 
				 
				 //BigDecimal domathTira = domathTira(t.getCalcType(),t.getRate(),t.getSumInsured(),t.getExchangeRate()); Tira Calculation only for referral
				 t.setTiraSumInsured(si);
				 if(!"A".equals(t.getCalcType()))
					 t.setTiraRate(t.getRate());
				 Double totaldiscount=0D;
				 Double totalloading=0D;
				 if(discountLoading) {
					
					 if(t.getDiscounts()!=null && t.getDiscounts().size()>0) {
						 DiscountCalculator dcal=new DiscountCalculator(t.getPremiumBeforeDiscount(),t.getExchangeRate(),this);					 
						 t.getDiscounts().stream().forEach(dcal);
						 totaldiscount= t.getDiscounts().stream().mapToDouble(i->i.getDiscountAmount().doubleValue()).sum();
					 }


					 
					 if(t.getLoadings()!=null && t.getLoadings().size()>0) {
						 LoadingCalculator dcal=new LoadingCalculator(t.getPremiumBeforeDiscount(),t.getExchangeRate(),this);					 
						 t.getLoadings().stream().forEach(dcal);
						 totalloading= t.getLoadings().stream().mapToDouble(i->i.getLoadingAmount().doubleValue()).sum();
					 }
				 }
				 
				 
				 t.setPremiumAfterDiscount((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumBeforeDiscount().subtract(new BigDecimal(totaldiscount)).add(new BigDecimal(totalloading)).multiply(t.getProRata()))) );
 
				 t.setPremiumAfterDiscountLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumAfterDiscount().multiply(t.getExchangeRate()))));
				 //.multiply(t.getProRata())				 
				 t.setPremiumExcluedTax(t.getPremiumAfterDiscount());				 
				 t.setPremiumExcluedTaxLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumExcluedTax().multiply(t.getExchangeRate()))));
				 
				 // Minimium Premium setup.
				 if(t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0 && !"Y".equals(t.getIsReferral())) {
					 
					 t.setPremiumExcluedTax((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getMinimumPremium().divide(t.getExchangeRate(),MathContext.DECIMAL64)))); 
					 t.setPremiumExcluedTaxLC(t.getMinimumPremium());
					 t.setMinimumPremiumYn("Y");
				 }
				 
				 Double totaltax=0D;
				 if(t.getTaxes()!=null && t.getTaxes().size()>0 && customers!=null && customers.get(0)!=null ) {
					 TaxCalculator tcal=new TaxCalculator(t.getPremiumExcluedTax(),t.getExchangeRate(),this,customers.get(0));
					 t.getTaxes().stream().filter(f -> "N".equals(f.getDependentYn())).forEach(tcal);
					 Double totaltax_N = t.getTaxes().stream().filter(f -> "N".equals(f.getDependentYn())).mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
					 
					 
					 tcal=new TaxCalculator(t.getPremiumExcluedTax().add(new BigDecimal(totaltax_N)),t.getExchangeRate(),this,customers.get(0));
					 t.getTaxes().stream().filter(f -> "Y".equals(f.getDependentYn())).forEach(tcal);
					 Double totaltax_Y = t.getTaxes().stream().filter(f -> "Y".equals(f.getDependentYn())).mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
					 
					 totaltax=totaltax_N+totaltax_Y;
				 }
				 
				 t.setPremiumIncludedTax((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumExcluedTax().add(new BigDecimal(totaltax)))));				 
				 t.setPremiumIncludedTaxLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumIncludedTax().multiply(t.getExchangeRate()))));
			 }
			 
			
			 
		 }catch(CoverException ex) {
			 ex.printStackTrace();
		 }catch (Exception e) {
			 System.out.println("CoverID:"+t.getCoverId()+"<Desc>:"+t.getCoverDesc()+",subcoverId:"+t.getSubCoverId());
			 e.printStackTrace();
			 CoverException build = CoverException.builder().isError(true).message(e.getMessage() +" "+"CoverID:"+t.getCoverId()+"<Desc>:"+t.getCoverDesc()+",subcoverId:"+t.getSubCoverId()).build();
			 
			 t.setError(build);
		 }
		
	}
	
	

}

