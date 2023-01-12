package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.Tuple;

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
				 
				 BigDecimal exchangeRate= new BigDecimal(vehicles.get(0).get("exchangeRate")==null?"1":vehicles.get(0).get("exchangeRate").toString());
				 t.setExchangeRate(exchangeRate);
				 String currecy=vehicles.get(0).get("currency")==null?"N/A":vehicles.get(0).get("currency").toString();
				 t.setCurrency(currecy);
				 
				 t.setProRata(new BigDecimal("1"));
				 if(prorata!=null) {
					 BigDecimal percenat=prorata.get(0).get("percent")==null?BigDecimal.ZERO:new BigDecimal(prorata.get(0).get("percent").toString());	
					 t.setProRata(percenat.divide(new BigDecimal("100")).setScale(round.getPrecision(),RoundingMode.HALF_UP));
				 }
				 
				 /// this particular variable is for is rate defined for Single
				 String rateFor=vehicles.get(0).get("groupCount")==null?"1":vehicles.get(0).get("groupCount").toString();
				 
				 
				 
				 
				 
				 BigDecimal si=vehicles.get(0).get(t.getCoverBasedOn())==null?BigDecimal.ZERO:new BigDecimal(vehicles.get(0).get(t.getCoverBasedOn()).toString());
				 if("Y".equals(t.getDependentCoveryn())) {
					 if(calculatedcover!=null) {
						Cover ct = calculatedcover.stream().filter(c->c.getCoverId().equals(t.getDependentCoverId())).findAny().orElse(null);
						si=ct!=null?ct.getPremiumExcluedTax():BigDecimal.ZERO;
					}
				 }				 
				 t.setSumInsured(si);
				 
				 if("F".equals(t.getCalcType())) {
					 // Tuple vehicle,Tuple customer,Tuple common
					 List<Tuple> factors = LoadFactorRates(engine, t.getCoverId(),t.getFactorTypeId(),engine.getVehicleId());
					 
					 /*if(factors==null || factors.size()==0) 
						 throw CoverException.builder().message("Not Found Result "+"CoverID:"+t.getCoverId()+"<Desc>:"+t.getCoverDesc()+",subcoverId:"+t.getSubCoverId())
					 .isError(true).build();*/
						 
					 Tuple tuple = null;
					 try {
						 tuple=factors.get(0);
					 }catch (Exception e) {
						// TODO: handle exception
						 CoverException build = CoverException.builder().message("No factor found")
						 .isError(true).build();
						 t.setError(build);
						 t.setNotsutable(true);
						 throw build;
					}
					 
					 String calctype=tuple.get("calcType").toString();
					 String rate=tuple.get("rate")==null?"0":tuple.get("rate").toString();
					 
					 t.setRate((Double) ((Double.parseDouble(rate)*Double.parseDouble(rateFor))));
					 
					 t.setMinimumPremium(tuple.get("minPremium")==null?BigDecimal.ZERO:new BigDecimal(tuple.get("minPremium").toString())/*.divide(t.getExchangeRate(),round)*/);
					 BigDecimal domath = domath(calctype, t.getRate(), si,t.getExchangeRate());
					 t.setPremiumBeforeDiscount(domath);
					 t.setPremiumBeforeDiscountLC(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate()).setScale(round.getPrecision(),RoundingMode.HALF_UP)) ;
					 t.setCalcType(calctype);
					 /// Referal
					 t.setIsReferral((tuple.get("status")==null?"N":tuple.get("status").toString()).equals("R")?"Y":"N");
				 }else {
					 t.setRate((t.getRate()*Double.parseDouble(rateFor)));
					 
					 BigDecimal domath = domath(t.getCalcType(), t.getRate(), si,t.getExchangeRate());
					 t.setPremiumBeforeDiscount(domath);
					 t.setPremiumBeforeDiscountLC(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate()).setScale(round.getPrecision(),RoundingMode.HALF_UP)) ;
				 }
				 
				 
				 BigDecimal domathTira = domathTira(t.getCalcType(),t.getRate(),t.getPremiumBeforeDiscountLC(),t.getExchangeRate());
				 t.setTiraSumInsured(domathTira);
				 
				 
				Double totaldiscount=0D;
				 if(t.getDiscounts()!=null && t.getDiscounts().size()>0) {
					 DiscountCalculator dcal=new DiscountCalculator(t.getPremiumBeforeDiscount(),t.getExchangeRate(),this);					 
					 t.getDiscounts().stream().forEach(dcal);
					 totaldiscount= t.getDiscounts().stream().mapToDouble(i->i.getDiscountAmount().doubleValue()).sum();
				 }
				 
				 
				 Double totalloading=0D;
				 if(t.getLoadings()!=null && t.getLoadings().size()>0) {
					 LoadingCalculator dcal=new LoadingCalculator(t.getPremiumBeforeDiscount(),t.getExchangeRate(),this);					 
					 t.getLoadings().stream().forEach(dcal);
					 totalloading= t.getLoadings().stream().mapToDouble(i->i.getLoadingAmount().doubleValue()).sum();
				 }
				 
				 
				 
				 t.setPremiumAfterDiscount( t.getPremiumBeforeDiscount().subtract(new BigDecimal(totaldiscount)).add(new BigDecimal(totalloading)).multiply(t.getProRata()).setScale(round.getPrecision(),RoundingMode.HALF_UP));
				 t.setPremiumAfterDiscountLC(t.getPremiumAfterDiscount().multiply(t.getExchangeRate()).multiply(t.getProRata()).setScale(round.getPrecision(),RoundingMode.HALF_UP));
				 
				 t.setPremiumExcluedTax(t.getPremiumAfterDiscount());
				 t.setPremiumExcluedTaxLC(t.getPremiumExcluedTax().multiply(t.getExchangeRate()).setScale(round.getPrecision(),RoundingMode.HALF_UP));
				 
				 // Minimium Premium setup.
				 if(t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0) {
					 t.setPremiumExcluedTax(t.getMinimumPremium().divide(t.getExchangeRate()).setScale(round.getPrecision(),RoundingMode.HALF_UP)); 
					 t.setPremiumExcluedTaxLC(t.getMinimumPremium());
				 }
				 
				 Double totaltax=0D;
				 if(t.getTaxes()!=null && t.getTaxes().size()>0 && customers!=null && customers.get(0)!=null ) {
					 TaxCalculator tcal=new TaxCalculator(t.getPremiumExcluedTax(),t.getExchangeRate(),this,customers.get(0));
					 t.getTaxes().stream().forEach(tcal);
					 totaltax = t.getTaxes().stream().mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
				 }
				 
				 t.setPremiumIncludedTax(t.getPremiumExcluedTax().add(new BigDecimal(totaltax)));				 
				 t.setPremiumIncludedTaxLC(t.getPremiumIncludedTax().multiply(t.getExchangeRate()).setScale(round.getPrecision(),RoundingMode.HALF_UP));
			 }
			 
			
			 
		 }catch(CoverException e) {
			 //e.printStackTrace();
			// t.setError(e);
		 }catch (Exception e) {
			 System.out.println("CoverID:"+t.getCoverId()+"<Desc>:"+t.getCoverDesc()+",subcoverId:"+t.getSubCoverId());
			 e.printStackTrace();
			 CoverException build = CoverException.builder().isError(true).message(e.getMessage() +" "+"CoverID:"+t.getCoverId()+"<Desc>:"+t.getCoverDesc()+",subcoverId:"+t.getSubCoverId()).build();
			 
			 t.setError(build);
		 }
		
	}
	
	

}

