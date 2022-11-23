package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.Tuple;

import org.springframework.stereotype.Component;

import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.CoverException;

@Component
public class AdminCoverCalculator  extends CommonCalculator implements Consumer<Cover> {
	
	
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
					 t.setProRata(percenat.divide(new BigDecimal("100")));
				 }
				 
				 BigDecimal si=vehicles.get(0).get("sumInsured")==null?BigDecimal.ZERO:new BigDecimal(vehicles.get(0).get("sumInsured").toString());
				 if("Y".equals(t.getDependentCoveryn())) {
					 if(calculatedcover!=null) {
						Cover ct = calculatedcover.stream().filter(c->c.getCoverId().equals(t.getDependentCoverId())).findAny().orElse(null);
						si=ct!=null?ct.getPremiumExcluedTax():BigDecimal.ZERO;
					}
				 }				 
				 t.setSumInsured(si);
			
				 
				 
				 BigDecimal domath = domath(t.getCalcType(), t.getRate(), si);
				 t.setPremiumBeforeDiscount(domath);
				 t.setPremiumBeforeDiscountLC(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate()).round(round)) ;
			 
				 
				 Double totaldiscount=0D;
				 if(t.getDiscounts()!=null && t.getDiscounts().size()>0) {
					 AdminDiscountCalculator dcal=new AdminDiscountCalculator(t.getPremiumBeforeDiscount(),this);					 
					 t.getDiscounts().stream().forEach(dcal);
					 totaldiscount= t.getDiscounts().stream().mapToDouble(i->i.getDiscountAmount().doubleValue()).sum();
				 }
				 
				 
				 Double totalloading=0D;
				 if(t.getLoadings()!=null && t.getLoadings().size()>0) {
					 AdminLoadingCalculator dcal=new AdminLoadingCalculator(t.getPremiumBeforeDiscount(),this);					 
					 t.getLoadings().stream().forEach(dcal);
					 totalloading= t.getLoadings().stream().mapToDouble(i->i.getLoadingAmount().doubleValue()).sum();
				 }
				 
				 
				 
				 t.setPremiumAfterDiscount( t.getPremiumBeforeDiscount().subtract(new BigDecimal(totaldiscount,round)).add(new BigDecimal(totalloading,round)).multiply(t.getProRata()).round(round));
				 t.setPremiumAfterDiscountLC(t.getPremiumAfterDiscount().multiply(t.getExchangeRate()).multiply(t.getProRata()).round(round));
				 
				 t.setPremiumExcluedTax(t.getPremiumAfterDiscount());
				 t.setPremiumExcluedTaxLC(t.getPremiumExcluedTax().multiply(t.getExchangeRate()).round(round));
				 
				 // Minimium Premium setup.
				 if(t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0) {
					 t.setPremiumExcluedTax(t.getMinimumPremium().divide(t.getExchangeRate()).round(round)); 
					 t.setPremiumExcluedTaxLC(t.getMinimumPremium());
				 }
				 
				 Double totaltax=0D;
				 if(t.getTaxes()!=null && t.getTaxes().size()>0) {
					 TaxCalculator tcal=new TaxCalculator(t.getPremiumExcluedTax(),this);
					 t.getTaxes().stream().forEach(tcal);
					 totaltax = t.getTaxes().stream().mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
				 }
				 
				 t.setPremiumIncludedTax(t.getPremiumExcluedTax().add(new BigDecimal(totaltax,round)));				 
				 t.setPremiumIncludedTaxLC(t.getPremiumIncludedTax().multiply(t.getExchangeRate()).round(round));
			 }
			 
			
			 
		 } catch (Exception e) {
			 System.out.println("CoverID:"+t.getCoverId()+"<Desc>:"+t.getCoverDesc()+",subcoverId:"+t.getSubCoverId());
			 e.printStackTrace();
			 CoverException build = CoverException.builder().isError(true).message(e.getMessage() +" "+"CoverID:"+t.getCoverId()+"<Desc>:"+t.getCoverDesc()+",subcoverId:"+t.getSubCoverId()).build();
			 
			 t.setError(build);
		 }
		
	}
	
	

}

