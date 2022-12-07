package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.function.Consumer;

import com.maan.eway.res.calc.Tax;

public class TaxCalculator   implements Consumer<Tax> {

	private BigDecimal premium;
	private BigDecimal exchangeRate;
	
	public TaxCalculator(BigDecimal premium, BigDecimal exchangeRate, CommonCalculator calc) {
		super();
		this.premium = premium;
		this.exchangeRate = exchangeRate;
		this.calc = calc;
	}




	private CommonCalculator calc;
	

 
	
	@Override
	public void accept(Tax t) {
	 try {
		 String calctype= t.getCalcType();
		 
		 BigDecimal domath = calc.domath(calctype, t.getTaxRate(), premium,exchangeRate);
		 t.setTaxAmount(domath);
		  
	 }catch (Exception e) {
		 e.printStackTrace();
	 }
		
	}

}
