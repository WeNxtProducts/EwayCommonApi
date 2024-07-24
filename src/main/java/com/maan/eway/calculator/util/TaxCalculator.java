package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Consumer;

import jakarta.persistence.Tuple;

import com.maan.eway.res.calc.Tax;

public class TaxCalculator   implements Consumer<Tax> {

	private BigDecimal premium;
	private BigDecimal exchangeRate;
	
	protected Tuple customer =null;
	
	public TaxCalculator(BigDecimal premium, BigDecimal exchangeRate, CommonCalculator calc, Tuple customer) {
		super();
		this.premium = premium;
		this.exchangeRate = exchangeRate;
		this.calc = calc;
		this.customer=customer;
	}




	private CommonCalculator calc;
	

 
	
	@Override
	public void accept(Tax t) {
	 try {
		 String calctype= t.getCalcType();
		 
		 String isTaxExempted=customer.get("isTaxExempted")==null?"N":customer.get("isTaxExempted").toString();
		 String taxExemptedId=customer.get("taxExemptedId")==null?"":customer.get("taxExemptedId").toString();
		 
		 t.setIsTaxExempted(isTaxExempted);
		 t.setTaxExemptCode(taxExemptedId);
		 
		 BigDecimal domath_Fc = BigDecimal.ZERO;
		 t.setTaxAmount(BigDecimal.ZERO);
		 t.setTaxAmountLc(BigDecimal.ZERO);
		 if( ("Y".equals(t.getTaxExemptedAllowed()) && t.getIsTaxExempted().equals("N")) || t.getTaxExemptedAllowed().equals("N") ) {
			 domath_Fc= calc.domath(calctype, t.getTaxRate(), premium,exchangeRate); 
			 BigDecimal domath_Lc = domath_Fc.multiply(exchangeRate);
			 t.setTaxAmount(domath_Fc);
			 t.setTaxAmountLc(domath_Lc);
			 t.setMinimumTaxAmount(t.getMinimumTaxAmountLc().multiply(exchangeRate));
			 if(domath_Lc.compareTo(t.getMinimumTaxAmountLc())<0) {
				 t.setTaxAmount(t.getMinimumTaxAmount());
				 t.setTaxAmountLc(t.getMinimumTaxAmountLc());
			 }
			 
		 }
		 
		  
	 }catch (Exception e) {
		 e.printStackTrace();
	 }
		
	}

}

