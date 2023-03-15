package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.Tuple;

import org.springframework.stereotype.Component;

import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.CoverException;
import com.maan.eway.res.calc.Endorsement;

@Component
public class EndtCoverCalculator  extends CommonCalculator implements Consumer<Cover> {
	
	
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
				 if(prorata!=null  && "Y".equals(t.getProRataYn())) {
					 BigDecimal percenat=prorata.get(0).get("percent")==null?BigDecimal.ZERO:new BigDecimal(prorata.get(0).get("percent").toString());	
					 t.setProRata(percenat.divide(new BigDecimal("100")));
				 }
				 
				 BigDecimal si=vehicles.get(0).get(t.getCoverBasedOn())==null?BigDecimal.ZERO:new BigDecimal(vehicles.get(0).get(t.getCoverBasedOn()).toString());
				 if("Y".equals(t.getDependentCoveryn())) {
					 if(calculatedcover!=null) {
						Cover ct = calculatedcover.stream().filter(c->c.getCoverId().equals(t.getDependentCoverId())).findAny().orElse(null);
						si=ct!=null?ct.getPremiumExcluedTax():BigDecimal.ZERO;
					}
				 }				 
				 t.setSumInsured(si);
			
				 
				 
				 BigDecimal domath = domath(t.getCalcType(), t.getRate(), si,t.getExchangeRate());
				 t.setPremiumBeforeDiscount(domath);
				 t.setPremiumBeforeDiscountLC(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate()).round(round)) ;
			 
				 
				 Double totaldiscount=0D;
				 if(t.getDiscounts()!=null && t.getDiscounts().size()>0) {
					 AdminDiscountCalculator dcal=new AdminDiscountCalculator(t.getPremiumBeforeDiscount(),t.getExchangeRate(),this);					 
					 t.getDiscounts().stream().forEach(dcal);
					 totaldiscount= t.getDiscounts().stream().mapToDouble(i->i.getDiscountAmount().doubleValue()).sum();
				 }
				 
				 
				 Double totalloading=0D;
				 if(t.getLoadings()!=null && t.getLoadings().size()>0) {
					 AdminLoadingCalculator dcal=new AdminLoadingCalculator(t.getPremiumBeforeDiscount(),t.getExchangeRate(),this);					 
					 t.getLoadings().stream().forEach(dcal);
					 totalloading= t.getLoadings().stream().mapToDouble(i->i.getLoadingAmount().doubleValue()).sum();
				 }
				 
				 
				 
				 t.setPremiumAfterDiscount( t.getPremiumBeforeDiscount().subtract(new BigDecimal(totaldiscount)).add(new BigDecimal(totalloading)).multiply(t.getProRata()).setScale(round.getPrecision(),RoundingMode.HALF_UP));
				 t.setPremiumAfterDiscountLC(t.getPremiumAfterDiscount().multiply(t.getExchangeRate()).multiply(t.getProRata()).setScale(round.getPrecision(),RoundingMode.HALF_UP));
				 
				 t.setPremiumExcluedTax(t.getPremiumAfterDiscount());
				 t.setPremiumExcluedTaxLC(t.getPremiumExcluedTax().multiply(t.getExchangeRate()).setScale(round.getPrecision(),RoundingMode.HALF_UP));
				 
				 // Minimium Premium setup.
				 t.setMinimumPremiumYn("N");
				 if(t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0) {
					 t.setPremiumExcluedTax(t.getMinimumPremium().divide(t.getExchangeRate()).round(round)); 
					 t.setPremiumExcluedTaxLC(t.getMinimumPremium());
					 t.setMinimumPremiumYn("Y");
				 }
				 
				 Double totaltax=0D;
				 if(t.getTaxes()!=null && t.getTaxes().size()>0) {
					 TaxCalculator tcal=new TaxCalculator(t.getPremiumExcluedTax(),t.getExchangeRate(),this,customers.get(0));
					 t.getTaxes().stream().forEach(tcal);
					 totaltax = t.getTaxes().stream().mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
				 }
				 
				 t.setPremiumIncludedTax(t.getPremiumExcluedTax().add(new BigDecimal(totaltax)));				 
				 t.setPremiumIncludedTaxLC(t.getPremiumIncludedTax().multiply(t.getExchangeRate()).setScale(round.getPrecision(),RoundingMode.HALF_UP));
				 
				 
				 if(t.getEndorsements()!=null && t.getEndorsements().size()>0) {
					 t.getEndorsements().sort(new Comparator<Endorsement>() {
						@Override
						public int compare(Endorsement o1, Endorsement o2) {
							// TODO Auto-generated method stub
							return o1.getEndtCount().compareTo(o2.getEndtCount());
						}
						 
					});
					 //new premium-old prem
					 Endorsement endorsement = t.getEndorsements().get(0);
					 endorsement.setPremiumAfterDiscountLC(t.getPremiumAfterDiscountLC().subtract(endorsement.getPremiumAfterDiscountLC()));
					 endorsement.setPremiumAfterDiscount(t.getPremiumAfterDiscount().subtract(endorsement.getPremiumAfterDiscount()));
					 
					 endorsement.setPremiumBeforeDiscountLC(t.getPremiumBeforeDiscountLC().subtract(endorsement.getPremiumBeforeDiscountLC()));
					 endorsement.setPremiumBeforeDiscount(t.getPremiumBeforeDiscount().subtract(endorsement.getPremiumBeforeDiscount()));
					 
					 endorsement.setPremiumExcluedTax(t.getPremiumExcluedTax().subtract(endorsement.getPremiumExcluedTax()));
					 endorsement.setPremiumExcluedTaxLC(t.getPremiumExcluedTaxLC().subtract(endorsement.getPremiumExcluedTaxLC()));
					 
					 
					 totaltax=0D;
					 if(endorsement.getTaxes()!=null && endorsement.getTaxes().size()>0) {
						 TaxCalculator tcal=new TaxCalculator(endorsement.getPremiumExcluedTax(),t.getExchangeRate(),this,customers.get(0));
						 endorsement.getTaxes().stream().forEach(tcal);
						 totaltax = endorsement.getTaxes().stream().mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
					 }
					/* t.setPremiumIncludedTax(t.getPremiumExcluedTax().add(new BigDecimal(totaltax,round)));				 
					 t.setPremiumIncludedTaxLC(t.getPremiumIncludedTax().multiply(t.getExchangeRate()).round(round));
					*/
					 BigDecimal totalWithTax=endorsement.getPremiumExcluedTax().add(new BigDecimal(totaltax,round));
					 BigDecimal totalWithTaxLC=totalWithTax.multiply(t.getExchangeRate()).round(round);
					 
					 endorsement.setPremiumIncludedTax(totalWithTax);
					 endorsement.setPremiumIncludedTaxLC(totalWithTaxLC);
				 }
				 
				 
			 }
			 
			
			 
		 } catch (Exception e) {
			 System.out.println("CoverID:"+t.getCoverId()+"<Desc>:"+t.getCoverDesc()+",subcoverId:"+t.getSubCoverId());
			 e.printStackTrace();
			 CoverException build = CoverException.builder().isError(true).message(e.getMessage() +" "+"CoverID:"+t.getCoverId()+"<Desc>:"+t.getCoverDesc()+",subcoverId:"+t.getSubCoverId()).build();
			 
			 t.setError(build);
		 }
		
	}
	
	

}

