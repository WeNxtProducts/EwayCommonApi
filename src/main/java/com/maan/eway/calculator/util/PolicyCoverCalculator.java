package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.persistence.Tuple;

import org.apache.tomcat.util.buf.StringUtils;

import com.maan.eway.chartaccount.ChartAccountRequest;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.CoverException;
import com.maan.eway.res.calc.RatingInfo;

public class PolicyCoverCalculator implements Consumer<Cover> {
	 

 

	public PolicyCoverCalculator(List<Tuple> policy, RatingFactorsUtil crservice, CalcEngine engine,DecimalFormat decimalFormat,List<Tuple> customers) {
		super();
		this.policy = policy;
		this.crservice = crservice;
		this.engine = engine;
		this.decimalFormat = decimalFormat;
		this.customers=customers;
	}
	protected List<Tuple> policy;
	protected RatingFactorsUtil crservice;
	protected CalcEngine engine;
	protected DecimalFormat decimalFormat = null;
	protected List<Tuple> customers =null;
	
	@Override
	public void accept(Cover t) {
		try {
			String chartId = t.getDependentCoverId();
			
			BigDecimal premium =BigDecimal.ZERO;// new BigDecimal(drcr.getCommonResponse().toString());
			
			
     		BigDecimal exchangeRate= new BigDecimal(policy.get(0).get("exchangeRate")==null?"1":policy.get(0).get("exchangeRate").toString());
			 t.setExchangeRate(exchangeRate);
			 String currecy=policy.get(0).get("currency")==null?"N/A":policy.get(0).get("currency").toString();
			 t.setCurrency(currecy);
			 t.setSumInsured(premium);
			 t.setSumInsuredLc(premium.multiply(exchangeRate,MathContext.DECIMAL64));
			 t.setProRata(BigDecimal.ONE);
			 
			 
		
			
			
			 Double totaldiscount=0D;
			 Double totalloading=0D;
			 

				
			 if(t.getDiscounts()!=null && t.getDiscounts().size()>0) {
				 DiscountCalculatorPolicy dcal=new DiscountCalculatorPolicy(t.getPremiumBeforeDiscount(),t.getExchangeRate(),policy,crservice,engine,decimalFormat);					 
				 t.getDiscounts().stream().forEach(dcal);
				 totaldiscount= t.getDiscounts().stream().mapToDouble(i->i.getDiscountAmount().doubleValue()).sum();
			 }


			 
			 if(t.getLoadings()!=null && t.getLoadings().size()>0) {
				 LoadingCalculatorPolicy dcal=new LoadingCalculatorPolicy(t.getPremiumBeforeDiscount(),t.getExchangeRate(),policy,crservice,engine,decimalFormat);					 
				 t.getLoadings().stream().forEach(dcal);
				 totalloading= t.getLoadings().stream().mapToDouble(i->i.getLoadingAmount().doubleValue()).sum();
			 }
		 
			 if(!t.getDiscounts().isEmpty()) {
				 t.setRate(t.getDiscounts().get(0).getMaxAmount().doubleValue());
			 }else if(!t.getLoadings().isEmpty()) {
				 t.setRate(t.getLoadings().get(0).getMaxAmount().doubleValue());
			 }
			 
			 BigDecimal domath = domath(t.getCalcType(), t.getRate(), premium,t.getExchangeRate());
			 t.setPremiumBeforeDiscount(domath);
			 t.setPremiumBeforeDiscountLC(new BigDecimal(decimalFormat.format(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate())))) ;
			 
			 t.setPremiumAfterDiscount(new BigDecimal(decimalFormat.format(t.getPremiumBeforeDiscount().subtract(new BigDecimal(totaldiscount)).add(new BigDecimal(totalloading)).multiply(t.getProRata()))) );

			 t.setPremiumAfterDiscountLC(new BigDecimal(decimalFormat.format(t.getPremiumAfterDiscount().multiply(t.getExchangeRate()))));
			 //.multiply(t.getProRata())				 
			 t.setPremiumExcluedTax(t.getPremiumAfterDiscount());				 
			 t.setPremiumExcluedTaxLC(new BigDecimal(decimalFormat.format(t.getPremiumExcluedTax().multiply(t.getExchangeRate()))));
			 
			 // Minimium Premium setup.
			 if(t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0 && !"Y".equals(t.getIsReferral())) {
				 
				 t.setPremiumExcluedTax(new BigDecimal(decimalFormat.format(t.getMinimumPremium().divide(t.getExchangeRate(),MathContext.DECIMAL64)))); 
				 t.setPremiumExcluedTaxLC(t.getMinimumPremium());
				 t.setMinimumPremiumYn("Y");
			 }
			 
			 Double totaltax=0D;
			if(t.getTaxes()!=null && t.getTaxes().size()>0 ) {
				TaxCalculatorPolicy tcal=new TaxCalculatorPolicy(t.getPremiumExcluedTax(),t.getExchangeRate(),customers.get(0),decimalFormat);
				 t.getTaxes().stream().filter(f -> "N".equals(f.getDependentYn())).forEach(tcal);
				 Double totaltax_N = t.getTaxes().stream().filter(f -> "N".equals(f.getDependentYn())).mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
				 
				 
				 tcal=new TaxCalculatorPolicy(t.getPremiumExcluedTax().add(new BigDecimal(totaltax_N)),t.getExchangeRate(),customers.get(0),decimalFormat);
				 t.getTaxes().stream().filter(f -> "Y".equals(f.getDependentYn())).forEach(tcal);
				 Double totaltax_Y = t.getTaxes().stream().filter(f -> "Y".equals(f.getDependentYn())).mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
				 
				 totaltax=totaltax_N+totaltax_Y;
			 }
			 
			 t.setPremiumIncludedTax(new BigDecimal(decimalFormat.format(t.getPremiumExcluedTax().add(new BigDecimal(totaltax)))));				 
			 t.setPremiumIncludedTaxLC(new BigDecimal(decimalFormat.format(t.getPremiumIncludedTax().multiply(t.getExchangeRate()))));
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public PolicyCoverCalculator(List<Tuple> policy, RatingFactorsUtil crservice, CalcEngine engine) {
		super();
		this.policy = policy;
		this.crservice = crservice;
		this.engine = engine;
	}


	public List<Tuple> LoadFactorRates(CalcEngine engine,String coverId,String factorid){
		return LoadFactorRates(engine, coverId, factorid, policy.get(0));
	}

	protected BigDecimal domath(String calctype, Double rate,BigDecimal si,BigDecimal exchangeRate) throws ParseException {
		BigDecimal d=BigDecimal.ZERO;
		if("P".equals(calctype)) {
			d = si.multiply(new BigDecimal(rate/100)/*, round*/);			
		 }else if("A".equals(calctype)) {
			d=(new BigDecimal(rate).divide(exchangeRate,3,RoundingMode.HALF_UP));// for foreign currency calculation we have to divide by exchange rate			
		 }else if("M".equals(calctype)) {
			 d = si.multiply(new BigDecimal(rate/1000)/*, round*/);			
		 }
		d = new BigDecimal(decimalFormat.format(d));
		return d;
	}
	private List<Tuple> LoadFactorRates(CalcEngine engine, String coverId, String factorid, Tuple tuple) {
		Map<String,List<String>> vloop=new HashMap<String, List<String>>();
		try {
			
			
			
				List<RatingInfo> rateInfos = crservice.LoadRatingType(engine, factorid);
				
				
					
				 

				List<String> condtions=new ArrayList<String>();
				for (RatingInfo r : rateInfos) {
					  if("MsPolicyDetails".equalsIgnoreCase(r.getInputTableName())){
						r.setInputColumValue(tuple.get(r.getInputColumName()).toString());
					}
					
					String condtion=r.getDiscretCol()+":"+r.getInputColumValue()+"";
					if("Y".equals(r.getFactorRangeYn())) {
						condtion=""+r.getInputColumValue()+"~"+r.getRangeFromCol()+"&"+r.getRangeToCol();  
					} 
					condtions.add(condtion);   
				}

				vloop.put("1", condtions);


				List<Tuple> loopfactorrates = crservice.loopfactorrates(engine,vloop,coverId,"0");

				if(loopfactorrates==null || loopfactorrates.size()==0) {
					condtions.clear(); 
					vloop.clear();
		
					for(int i=0;i<rateInfos.size();i++) {							
						RatingInfo r = rateInfos.get(i);
						if("N".equals(r.getFactorRangeYn())) {
							String condtion=r.getDiscretCol()+":"+r.getInputColumValue()+";";
							if(condtions.size()>0)
								condtion=condtion.concat(StringUtils.join(condtions,';'));
							List<Long> onlyquery =null;
							try {
								onlyquery =	crservice.countfactorOnlyquery(engine,condtion, coverId,"0");
							}catch (Exception e) {
								e.printStackTrace();
							}	
							Long count=onlyquery.get(0);
							if(count<=0) {
								r.setInputColumValue("99999");
							} 	
						}
						String condtion=r.getDiscretCol()+":"+r.getInputColumValue()+"";
						if("Y".equals(r.getFactorRangeYn())) {
							condtion=""+r.getInputColumValue()+"~"+r.getRangeFromCol()+"&"+r.getRangeToCol();  
						} 
						condtions.add(condtion);  
					}						 
					vloop.put("1", condtions);
					loopfactorrates =  crservice.loopfactorrates(engine,vloop,coverId,"0"); 

				}
			
			return loopfactorrates;  
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
}
