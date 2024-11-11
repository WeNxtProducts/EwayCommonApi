package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import jakarta.persistence.Tuple;

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
			 DecimalFormat dcf=	 (DecimalFormat) this.decimalFormat.clone();
			 if("Y".equals( t.getIsSubCover())) {
				 //this.setEngine(engine);
				 t.getSubcovers().stream().forEach(this);
				 t.getSubcovers().removeIf(ll -> (ll.isNotsutable()));
			 }else {

			//	 loadOnetimetable(engine);
				 boolean discountLoading=true;
				 System.out.println(t.getCoverId()+ "--- "+t.getCoverName());
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
						 List<String> dependentIds = Arrays.asList(t.getDependentCoverId().split(","));
						 
						 si = calculatedcover.stream().sorted(Comparator.comparing(Cover::getPremiumExcluedTax).reversed()).filter(c->{					        
					        return dependentIds.contains(c.getCoverId());
						}).map(x-> x.getPremiumExcluedTax()).reduce((a, b) -> a.subtract(b)).orElse(BigDecimal.ZERO);
								//findAny().orElse(null);
						//si=ct!=null?ct.getPremiumExcluedTax():BigDecimal.ZERO;
					}
				 }		
				 si=si.subtract(t.getFreeCoverLimit());
				 si=si.compareTo(BigDecimal.ZERO)>0?si:BigDecimal.ZERO;
				 t.setSumInsured(si);
				 t.setSumInsuredLc(si.multiply(exchangeRate,MathContext.DECIMAL64));
				 //t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0
				 if(t.getSumInsured().compareTo(t.getCoverageLimit())>0) {
					 t.setIsReferral("Y");
					 t.setReferalDescription("CoverageLimit Referral Limits Upto "+t.getCoverageLimit().toPlainString());
					 t.setPremiumBeforeDiscount(BigDecimal.ZERO);					 
					 t.setPremiumBeforeDiscountLC(BigDecimal.ZERO);
					 t.setCalcType("P");
				 }else if(t.getSumInsured().compareTo(t.getMinSumInsured())<0) {
					    discountLoading=false;
						CoverException build = CoverException.builder().message(t.getCoverName()+ "Min SumInsured is:"+t.getMinSumInsured().toPlainString()+ " & SumInsured:"+t.getSumInsured().toPlainString())
						.isError(true).build();
						t.setError(build);
						t.setNotsutable(true);
						throw build;
				 } if("F".equals(t.getCalcType()) || "FD".equals(t.getCalcType())) {
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
						if("B".equals(t.getCoverageType())) {
							 t.setIsReferral("Y");
							 t.setReferalDescription("No factor found "+t.getCoverDesc() +" Referral" );
							 t.setPremiumBeforeDiscount(BigDecimal.ZERO);
							 t.setRate(0D);

							 t.setMinimumPremium(BigDecimal.ZERO);
							 t.setPremiumBeforeDiscountLC(BigDecimal.ZERO);
							 t.setCalcType("A");
							 t.setRegulatoryCode("NA");
						}else {
							CoverException build = CoverException.builder().message("No factor found "+t.getCoverDesc())
							.isError(true).build();
							 t.setError(build);
							 t.setNotsutable(true);
							 throw build;
						}
						 /*t.setIsReferral("Y");
						 t.setReferalDescription("No factor found Referral for "+t.getCoverDesc());
						 t.setPremiumBeforeDiscount(BigDecimal.ZERO);					 
						 t.setPremiumBeforeDiscountLC(BigDecimal.ZERO);*/
					}
					 if("FD".equals(t.getCalcType())){
						 PerilCalculator calc=new PerilCalculator(crservice, engine, result, vehicles, customers,this,factors,this.drivers);
						 calc.perilCalculator(t);
						 t.setPremiumBeforeDiscountLC((BigDecimal) dcf.parse(dcf.format(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate())))) ;
						 t.getLoadings().clear();
						 t.setMinimumPremium(tuple.get("minPremium")==null?BigDecimal.ZERO:new BigDecimal(tuple.get("minPremium").toString())/*.divide(t.getExchangeRate(),round)*/);
						 t.setExcessAmount(tuple.get("excessAmount")==null?BigDecimal.ZERO:new BigDecimal(tuple.get("excessAmount").toString()));
						 t.setExcessDesc(tuple.get("excessDesc")==null?"":tuple.get("excessDesc").toString());
						 t.setExcessPercent(tuple.get("excessPercent")==null?BigDecimal.ZERO:new BigDecimal(tuple.get("excessPercent").toString()));
						 //t.getDiscounts().clear();
						 discountLoading=false;
					 }else if(tuple!=null) {
						 String calctype=tuple.get("calcType").toString();
						 String rate=tuple.get("rate")==null?"0":tuple.get("rate").toString();
						 String minrate=tuple.get("minimumRate")==null?"0":tuple.get("minimumRate").toString();
						 String regulatoryCode=tuple.get("regulatoryCode")==null?"N/A":tuple.get("regulatoryCode").toString();

						 t.setRate((Double) ((Double.parseDouble(rate)*Double.parseDouble(rateFor))));
						 t.setMinrate((Double) ((Double.parseDouble(minrate)*Double.parseDouble(rateFor))));
						 
						 t.setMinimumPremium(tuple.get("minPremium")==null?BigDecimal.ZERO:new BigDecimal(tuple.get("minPremium").toString())/*.divide(t.getExchangeRate(),round)*/);
						 //System.out.println(t.getCoverDesc()+ "<--->"+t.getRate() +"---"+si);
						 
						 BigDecimal domath = domath(calctype, t.getRate(), si,t.getExchangeRate());
						// System.out.println(t.getCoverDesc()+ "<--->"+domath);
						 t.setPremiumBeforeDiscount(domath);

						 t.setPremiumBeforeDiscountLC((BigDecimal) dcf.parse(dcf.format(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate())))) ;
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
				 }/*else if("FD".equals(t.getCalcType())){
					 PerilCalculator calc=new PerilCalculator(crservice, engine, result, vehicles, customers,this);
					 calc.perilCalculator(t);
					 t.setPremiumBeforeDiscountLC((BigDecimal) dcf.parse(dcf.format(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate())))) ;
					 t.getLoadings().clear();
					 //t.getDiscounts().clear();
					 discountLoading=false;
				 }*/else {
					 t.setRate((t.getRate()*Double.parseDouble(rateFor)));
					 CommonCalculator calcul=new CommonCalculator();
					 calcul.setEngine(engine, calculatedcover, result, vehicles, customers, prorata, crservice, dcf,drivers,this.customerChoiceTaxes);
					// System.out.println(t.getCoverDesc()+ "---"+t.getRate() +"---"+si);
					 BigDecimal domath = calcul.domath(t.getCalcType(), t.getRate(), si,t.getExchangeRate());
					// System.out.println(t.getCoverDesc()+ "---"+domath);
					 t.setPremiumBeforeDiscount(domath);					 
					 t.setPremiumBeforeDiscountLC((BigDecimal) dcf.parse(dcf.format(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate())))) ;
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
				 
				 
				 t.setPremiumAfterDiscount((BigDecimal) dcf.parse(dcf.format(t.getPremiumBeforeDiscount().subtract(new BigDecimal(totaldiscount)).add(new BigDecimal(totalloading)).multiply(t.getProRata()))) );
 
				 t.setPremiumAfterDiscountLC((BigDecimal) dcf.parse(dcf.format(t.getPremiumAfterDiscount().multiply(t.getExchangeRate()))));
				 //.multiply(t.getProRata())				 
				 t.setPremiumExcluedTax(t.getPremiumAfterDiscount());				 
				 t.setPremiumExcluedTaxLC((BigDecimal) dcf.parse(dcf.format(t.getPremiumExcluedTax().multiply(t.getExchangeRate()))));
				 
				 // Minimium Premium setup.
				 if(t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0 && !"Y".equals(t.getIsReferral())) {
					 
					 t.setPremiumExcluedTax((BigDecimal) dcf.parse(dcf.format(t.getMinimumPremium().divide(t.getExchangeRate(),MathContext.DECIMAL64)))); 
					 t.setPremiumExcluedTaxLC(t.getMinimumPremium());
					 t.setMinimumPremiumYn("Y");
				 }
				 
				 Double totaltax=0D;
				 if(t.getTaxes()!=null && t.getTaxes().size()>0 && customers!=null && customers.get(0)!=null ) {
					 TaxCalculator tcal=new TaxCalculator(t.getPremiumExcluedTax(),t.getExchangeRate(),this,customers.get(0),this.customerChoiceTaxes);
					 t.getTaxes().stream().filter(f -> "N".equals(f.getDependentYn())).forEach(tcal);
					 Double totaltax_N = t.getTaxes().stream().filter(f -> "N".equals(f.getDependentYn())).mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
					 
					 
					 tcal=new TaxCalculator(t.getPremiumExcluedTax().add(new BigDecimal(totaltax_N)),t.getExchangeRate(),this,customers.get(0),customerChoiceTaxes);
					 t.getTaxes().stream().filter(f -> "Y".equals(f.getDependentYn())).forEach(tcal);
					 Double totaltax_Y = t.getTaxes().stream().filter(f -> "Y".equals(f.getDependentYn())).mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
					 
					 totaltax=totaltax_N+totaltax_Y;
				 }
				 
				 t.setPremiumIncludedTax((BigDecimal) dcf.parse(dcf.format(t.getPremiumExcluedTax().add(new BigDecimal(totaltax)))));				 
				 t.setPremiumIncludedTaxLC((BigDecimal) dcf.parse(dcf.format(t.getPremiumIncludedTax().multiply(t.getExchangeRate()))));
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

