package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.springframework.stereotype.Component;

import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.CoverException;
import com.maan.eway.res.calc.Endorsement;
import com.maan.eway.res.calc.Tax;

@Component
public class EndtCoverCalculator  extends CommonCalculator implements Consumer<Cover> {
	
	
	/*@Autowired
	private CoverCalculator calc;*/
	private Date effectiveDate;	
	
	public void setEngine(CalcEngine engine,List<Cover> c,List<Tuple> result,List<Tuple> vehicles,List<Tuple> customers
			,List<Tuple> prorata, RatingFactorsUtil crservice,Date effectiveDate,DecimalFormat decimalFormat) {		
		this.setEngine(engine, c, result, vehicles, customers, prorata, crservice,decimalFormat);
		this.effectiveDate=effectiveDate;
	}

	@Override
	public void accept(Cover t) {
		 try {
			
			 if("Y".equals( t.getIsSubCover())) {
				 //this.setEngine(engine);
				 t.getSubcovers().stream().forEach(this);
			 }else {
				 Endorsement endorsement = null;
			//	 loadOnetimetable(engine);
				 if(t.getEndorsements()!=null && t.getEndorsements().size()>0) {
					 t.getEndorsements().sort(new Comparator<Endorsement>() {
						@Override
						public int compare(Endorsement o1, Endorsement o2) {
							// TODO Auto-generated method stub
							return o1.getEndtCount().compareTo(o2.getEndtCount());
						}
						 
					}.reversed());
					 endorsement= t.getEndorsements().get(0);
					 	//isCancellation=endorsement.getEndorsementId().equals("842");
				 }
				 
				 BigDecimal exchangeRate= new BigDecimal(vehicles.get(0).get("exchangeRate")==null?"1":vehicles.get(0).get("exchangeRate").toString());
				 t.setExchangeRate(exchangeRate);
				 String currecy=vehicles.get(0).get("currency")==null?"N/A":vehicles.get(0).get("currency").toString();
				 t.setCurrency(currecy);
				 
				// t.setProRata(new BigDecimal("1"));
				 if("Y".equals(engine.getCoverModification()) && "Y".equals(t.getProRataYn()) && "Y".equals(t.getUserOpt()) && endorsement!=null) {
					 t.setProRata(t.getProRata().divide(new BigDecimal("100")));
				 }else  if(prorata!=null && prorata.size()>0 && "Y".equals(t.getProRataYn()) ) {
					 BigDecimal percenat=prorata.get(0).get("percent")==null?BigDecimal.ZERO:new BigDecimal(prorata.get(0).get("percent").toString());	
					 t.setProRata(percenat.divide(new BigDecimal("100")));
				 }else {
					 t.setProRata(new BigDecimal("1"));
				 }
					 
 				 BigDecimal si=vehicles.get(0).get(t.getCoverBasedOn())==null?BigDecimal.ZERO:new BigDecimal(vehicles.get(0).get(t.getCoverBasedOn()).toString());
				 if("Y".equals(t.getDependentCoveryn())) {
					 if(calculatedcover!=null) {
						Cover ct = calculatedcover.stream().filter(c->c.getCoverId().equals(t.getDependentCoverId())).findAny().orElse(null);
						si=ct!=null?ct.getPremiumExcluedTax():BigDecimal.ZERO;
					}
				 }				 
				 t.setSumInsured(si);
				 t.setSumInsuredLc(si.multiply(exchangeRate,MathContext.DECIMAL64));
				 if(t.getSumInsured().compareTo(t.getCoverageLimit())>0) {
					 t.setIsReferral("Y");
					 t.setReferalDescription("CoverageLimit Referral Limits Upto"+t.getCoverageLimit());
					 t.setPremiumBeforeDiscount(BigDecimal.ZERO);					 
					 t.setPremiumBeforeDiscountLC(BigDecimal.ZERO);
					 //t.setCalcType("P");
				 }
 				 BigDecimal domath = domath(t.getCalcType(), t.getRate(), si,t.getExchangeRate());
				 t.setPremiumBeforeDiscount(domath.multiply(t.getProRata()));				 
				 t.setPremiumBeforeDiscountLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate())))) ;
				 
					 
				 BigDecimal domathTira = domathTira(t.getCalcType(),t.getTiraRate(),t.getPremiumBeforeDiscountLC(),t.getExchangeRate()); //Tira Calculation only for referral
				 t.setTiraSumInsured(domathTira);
				 
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
				 
				 
				 t.setPremiumAfterDiscount((BigDecimal) decimalFormat.parse(decimalFormat.format( t.getPremiumBeforeDiscount().subtract(new BigDecimal(totaldiscount)).add(new BigDecimal(totalloading)).multiply(t.getProRata()))));
				 t.setPremiumAfterDiscountLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumAfterDiscount().multiply(t.getExchangeRate()))));
				  
				 t.setPremiumExcluedTax(t.getPremiumAfterDiscount());				 
				 t.setPremiumExcluedTaxLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumExcluedTax().multiply(t.getExchangeRate()))));
				 // Minimium Premium setup.
				 
				 boolean isCancellation=false;
				 
				 t.setMinimumPremiumYn("N");
				 if(t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0 /*&& !isCancellation*/) {
					 
					 t.setPremiumExcluedTax((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getMinimumPremium().divide(t.getExchangeRate())))); 
					 t.setPremiumExcluedTaxLC(t.getMinimumPremium());
					 t.setMinimumPremiumYn("Y");
				 }		 
				 Double totaltax=0D;
				 if(t.getTaxes()!=null && t.getTaxes().size()>0) {
					 TaxCalculator tcal=new TaxCalculator(t.getPremiumExcluedTax(),t.getExchangeRate(),this,customers.get(0));
					 t.getTaxes().stream().forEach(tcal);
					 totaltax = t.getTaxes().stream().mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
				 }		 
				 
				 t.setPremiumIncludedTax((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumExcluedTax().add(new BigDecimal(totaltax)))));	
				 t.setPremiumIncludedTaxLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumIncludedTax().multiply(t.getExchangeRate()))));
				 
				 
				 if(t.getEndorsements()!=null && t.getEndorsements().size()>0) {
					/* t.getEndorsements().sort(new Comparator<Endorsement>() {
						@Override
						public int compare(Endorsement o1, Endorsement o2) {
							// TODO Auto-generated method stub
							return o1.getEndtCount().compareTo(o2.getEndtCount());
						}
						 
					}.reversed());*/
					 //new premium-old prem
					  endorsement = t.getEndorsements().get(0);
					

			 		
			 		 
					 	if("Y".equals(engine.getCoverModification())) {
					 		
					 		 if("Y".equals(t.getProRataYn()) && "Y".equals(t.getUserOpt())) {
									// Date Differents
										 Date periodStart =  effectiveDate;
											Date periodEnd = t.getPolicyEndDate() ;
										Long diffInMillies = Math.abs(periodEnd.getTime() - periodStart.getTime());
										Long daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) ;
										// Check Leap Year
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
										boolean leapYear = LocalDate.parse(sdf.format(periodEnd) ).isLeapYear();
										String diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );
										
										String periodOfInsurance=(vehicles.get(0).get("periodOfInsurance")==null?"365":vehicles.get(0).get("periodOfInsurance").toString());
										//Removal Logic
										diff= String.valueOf(Integer.parseInt(periodOfInsurance)-Integer.parseInt(diff));
										if(Integer.parseInt(diff)<0) diff="0";
										List<Tuple> prorata =  crservice.loadProRataData(engine, diff);
										
										endorsement.setProRataYn("Y");
										if(prorata.size()>0) {
										 BigDecimal percenat=prorata.get(0).get("percent")==null?BigDecimal.ZERO:new BigDecimal(prorata.get(0).get("percent").toString());
										 BigDecimal p=new BigDecimal("100").subtract(percenat).divide(new BigDecimal("100"));
										 t.setProRata(p);
										 endorsement.setProRata(p);
										}else {
											 BigDecimal p=new BigDecimal("1");
											t.setProRata(p);
											 endorsement.setProRata(p);
										}
										
					 		 }else if("Y".equals(t.getProRataYn()) && !"Y".equals(t.getUserOpt())) {
					 			 // Date Differents
					 			 Date periodStart =  effectiveDate;
					 			 Date periodEnd = t.getPolicyEndDate() ;
					 			 Long diffInMillies = Math.abs(periodEnd.getTime() - periodStart.getTime());
					 			 Long daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) ;						
					 			 // Check Leap Year
					 			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
					 			 boolean leapYear = LocalDate.parse(sdf.format(periodEnd) ).isLeapYear();
					 			 String diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );
					 			 if(Integer.parseInt(diff)<0) diff="0";
					 			 List<Tuple> prorata =  crservice.loadProRataData(engine, diff);
					 			 if(prorata.size()>0) {
					 				 BigDecimal percenat=prorata.get(0).get("percent")==null?BigDecimal.ZERO:new BigDecimal(prorata.get(0).get("percent").toString());
					 				 BigDecimal p=new BigDecimal("100").subtract(percenat).divide(new BigDecimal("100"));
					 				 t.setProRata(p);
					 				 endorsement.setProRata(p);
					 			 }else {
					 				 BigDecimal p=new BigDecimal("1");
					 				 t.setProRata(p);
					 				 endorsement.setProRata(p);
					 			 }

					 		 }
					 		 
					 		 if(t.getProRata().doubleValue()==0D) 
								 t.setProRata(new BigDecimal("1"));
					 		 
					 		 //endorsement.setPremiumAfterDiscountLC(t.getPremiumAfterDiscountLC().multiply(t.getProRata()).setScale(round.getPrecision(),RoundingMode.HALF_UP));
							 //endorsement.setPremiumAfterDiscount(t.getPremiumAfterDiscount().multiply(t.getProRata()).setScale(round.getPrecision(),RoundingMode.HALF_UP));
					 	
					 		 // Temp Prev Premium
							 endorsement.setPremiumAfterDiscountLC(endorsement.getPremiumExcluedTaxLC());
							 endorsement.setPremiumAfterDiscount(endorsement.getPremiumExcluedTax());
							 
							 endorsement.setPremiumBeforeDiscountLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumBeforeDiscountLC().multiply(t.getProRata()))));
							 endorsement.setPremiumBeforeDiscount((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumBeforeDiscount().multiply(t.getProRata()))));
							
							 
					 		 endorsement.setPremiumExcluedTax((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumExcluedTax().multiply(t.getProRata()))));					 		
							 endorsement.setPremiumExcluedTaxLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumExcluedTaxLC().multiply(t.getProRata()))));
							 
							 t.setDiffPremiumIncludedTax(BigDecimal.ZERO);
							 t.setDiffPremiumIncludedTaxLC(BigDecimal.ZERO);
							 
					 		 
					 	}else {
					 		// endorsement.setPremiumAfterDiscountLC(t.getPremiumAfterDiscountLC().subtract(endorsement.getPremiumAfterDiscountLC()));
							// endorsement.setPremiumAfterDiscount(t.getPremiumAfterDiscount().subtract(endorsement.getPremiumAfterDiscount()));
							 
					 	/*	 if("Y".equals(t.getProRataYn()) && "Y".equals(t.getUserOpt())) {
									// Date Differents
										 Date periodStart =  effectiveDate;
											Date periodEnd = t.getPolicyEndDate() ;
										Long diffInMillies = Math.abs(periodEnd.getTime() - periodStart.getTime());
										Long daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) ;
										// Check Leap Year
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
										boolean leapYear = LocalDate.parse(sdf.format(periodEnd) ).isLeapYear();
										String diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );
										
										String periodOfInsurance=(vehicles.get(0).get("periodOfInsurance")==null?"365":vehicles.get(0).get("periodOfInsurance").toString());
										//Removal Logic
										diff= String.valueOf(Integer.parseInt(periodOfInsurance)-Integer.parseInt(diff));
										
										List<Tuple> prorata =  crservice.loadProRataData(engine, diff);
										
										endorsement.setProRataYn("Y");
										if(prorata.size()>0) {
										 BigDecimal percenat=prorata.get(0).get("percent")==null?BigDecimal.ZERO:new BigDecimal(prorata.get(0).get("percent").toString());
										 BigDecimal p=new BigDecimal("100").subtract(percenat).divide(new BigDecimal("100"));
										 t.setProRata(p);
										 endorsement.setProRata(p);
										}else {
											 BigDecimal p=new BigDecimal("1");
											t.setProRata(p);
											 endorsement.setProRata(p);
										}
										
					 		 }else if("Y".equals(t.getProRataYn()) && !"Y".equals(t.getUserOpt()))*/{
					 			 // Date Differents
					 			 Date periodStart =  effectiveDate;
					 			 Date periodEnd = t.getPolicyEndDate() ;
					 			 Long diffInMillies = Math.abs(periodEnd.getTime() - periodStart.getTime());
					 			 Long daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) ;						
					 			 // Check Leap Year
					 			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
					 			 boolean leapYear = LocalDate.parse(sdf.format(periodEnd) ).isLeapYear();
					 			 if(daysBetween>366) daysBetween=365L;
					 			 String diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );
					 			 List<Tuple> prorata =  crservice.loadProRataData(engine, diff);
					 			 if(prorata.size()>0) {
					 				 BigDecimal percenat=prorata.get(0).get("percent")==null?BigDecimal.ZERO:new BigDecimal(prorata.get(0).get("percent").toString());
					 				 BigDecimal p=new BigDecimal("100").subtract(percenat).divide(new BigDecimal("100"));
					 				 t.setProRata(p);
					 				 endorsement.setProRata(p);
					 			 }else {
					 				 BigDecimal p=new BigDecimal("1");
					 				 t.setProRata(p);
					 				 endorsement.setProRata(p);
					 			 }

					 		 }
					 		 
					 		 if(t.getProRata().doubleValue()==0D) {
								 t.setProRata(BigDecimal.ONE);
								 endorsement.setProRata(BigDecimal.ONE);
					 		 }
					 		 
							// Temp Prev Premium
					 		
							 endorsement.setPremiumAfterDiscountLC(endorsement.getPremiumExcluedTaxLC());
							 endorsement.setPremiumAfterDiscount(endorsement.getPremiumExcluedTax());
							 endorsement.setExchangeRate(exchangeRate);
							 endorsement.setEndorsementsumInsured(t.getSumInsured().subtract(endorsement.getEndorsementsumInsured()));
							 endorsement.setEndorsementsumInsuredLc(endorsement.getEndorsementsumInsured().multiply(exchangeRate,MathContext.DECIMAL64));
							 
							 //t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0
							 domath = domath(endorsement.getEndorsementCalcType(), endorsement.getEndorsementRate(), endorsement.getEndorsementsumInsured(),endorsement.getExchangeRate());
							 endorsement.setPremiumBeforeDiscountLC(domath.multiply(t.getProRata()));
							 endorsement.setPremiumBeforeDiscount((BigDecimal) decimalFormat.parse(decimalFormat.format(endorsement.getPremiumBeforeDiscount().multiply(t.getExchangeRate()))));
							 
							 endorsement.setPremiumExcluedTax((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumExcluedTax().subtract(endorsement.getPremiumExcluedTax()).multiply(t.getProRata()))));
							 endorsement.setPremiumExcluedTaxLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumExcluedTaxLC().subtract(endorsement.getPremiumExcluedTaxLC()).multiply(t.getProRata()))));
							 						 
							 
							 //endorsement.setProRata(t.getProRata());

					 	}
					 
					
					 
					 totaltax=0D;
					 if(endorsement.getTaxes()!=null && endorsement.getTaxes().size()>0) {
						 
						 
						 String endtTypeId=vehicles.get(0).get("endtTypeId")==null?"":vehicles.get(0).get("endtTypeId").toString();
						 
						 List<Tax> notendtfees=endorsement.getTaxes().stream().filter(v -> !v.getTaxId().equals(endtTypeId)).collect(Collectors.toList());
						 List<Tax> inendtfees=endorsement.getTaxes().stream().filter(v -> v.getTaxId().equals(endtTypeId)).collect(Collectors.toList());
						 Double endtFee=0D;
						 if(inendtfees.size()>0) {
							 TaxCalculator tcal=new TaxCalculator(endorsement.getPremiumExcluedTax(),t.getExchangeRate(),this,customers.get(0));
							 inendtfees.stream().forEach(tcal);
							 endtFee= inendtfees.stream().mapToDouble(o -> o.getTaxAmount().doubleValue()).sum();
						 }
						 
						 
						 TaxCalculator tcal=new TaxCalculator(endorsement.getPremiumExcluedTax().add(new BigDecimal(endtFee)),t.getExchangeRate(),this,customers.get(0));  
						 notendtfees.stream().filter(f -> "N".equals(f.getDependentYn())).forEach(tcal);
						 Double totaltax_N = notendtfees.stream().filter(f -> "N".equals(f.getDependentYn())).mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
						 
						 
						 tcal=new TaxCalculator(t.getPremiumExcluedTax().add(new BigDecimal(endtFee)).add(new BigDecimal(totaltax_N)),t.getExchangeRate(),this,customers.get(0));
						 notendtfees.stream().filter(f -> "Y".equals(f.getDependentYn())).forEach(tcal);
						 Double totaltax_Y = notendtfees.stream().filter(f -> "Y".equals(f.getDependentYn())).mapToDouble(i->i.getTaxAmount().doubleValue()).sum();						 
						 totaltax=totaltax_N+totaltax_Y;  
					 
					 
					 }
					/* t.setPremiumIncludedTax(t.getPremiumExcluedTax().add(new BigDecimal(totaltax,round)));				 
					 t.setPremiumIncludedTaxLC(t.getPremiumIncludedTax().multiply(t.getExchangeRate()).round(round));
					*/
					 BigDecimal totalWithTax=endorsement.getPremiumExcluedTax().add(new BigDecimal(totaltax));					 
					 BigDecimal totalWithTaxLC=(BigDecimal) decimalFormat.parse(decimalFormat.format(totalWithTax.multiply(t.getExchangeRate())));
					 
					 
					
					 
					 
					 endorsement.setPremiumIncludedTax(totalWithTax);
					 endorsement.setPremiumIncludedTaxLC(totalWithTaxLC);
					 if("Y".equals(engine.getCoverModification()) && "Y".equals(t.getProRataYn()) && "Y".equals(t.getUserOpt())) {
						 
					 endorsement.setPremiumIncludedTax(totalWithTax.multiply(new BigDecimal("-1")));
						 endorsement.setPremiumIncludedTaxLC(totalWithTaxLC.multiply(new BigDecimal("-1")));
				 		 
						// endorsement.setPremiumIncludedTax(BigDecimal.ZERO);
						// endorsement.setPremiumIncludedTaxLC(BigDecimal.ZERO);
						 t.setDiffPremiumIncludedTax(totalWithTax.multiply(new BigDecimal("-1")));
						 t.setDiffPremiumIncludedTaxLC(totalWithTaxLC.multiply(new BigDecimal("-1")));
					 }
					 
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

