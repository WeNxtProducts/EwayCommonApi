package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.Tuple;

import org.apache.commons.lang3.StringUtils;

import com.maan.eway.res.calc.Discount;

public class DiscountCalculator   implements Consumer<Discount> {

	private BigDecimal premium;
	private BigDecimal exchangeRate;
	public DiscountCalculator(BigDecimal premium, BigDecimal exchangeRate, CommonCalculator calc) {
		super();
		this.premium = premium;
		this.exchangeRate = exchangeRate;
		this.calc = calc;
	}



	private CommonCalculator calc;
	 
 
	
	@Override
	public void accept(Discount t) {
	 try {
		 String calctype= t.getDiscountCalcType();
		 if("F".equals(t.getDiscountCalcType())) {
			 List<Tuple> factors = calc.LoadFactorRates(calc.engine, t.getDiscountId(),t.getFactorTypeId(),calc.engine.getVehicleId(),StringUtils.isBlank(t.getSubCoverId())?"0":t.getSubCoverId());
			 Tuple tuple = factors.get(0);
			 calctype=tuple.get("calcType").toString();
			 String rate=tuple.get("rate")==null?"0":tuple.get("rate").toString();
			 String minPremium=tuple.get("minPremium")==null?"0":tuple.get("minPremium").toString();
			 t.setDiscountRate(rate);
			 t.setMaxAmount(new BigDecimal(minPremium));
			 String regulatoryCode=tuple.get("regulatoryCode")==null?"N/A":tuple.get("regulatoryCode").toString();
			 t.setRegulatoryCode(regulatoryCode);
		 }
		 BigDecimal domath = calc.domath(calctype, Double.parseDouble(t.getDiscountRate()), premium,exchangeRate);
		 t.setDiscountAmount(domath);
		 if(t.getDiscountAmount().compareTo(t.getMaxAmount())==1) {
			 t.setDiscountAmount(t.getMaxAmount());
		 }
		 
	 }catch (Exception e) {
		// t.setDiscountAmount(BigDecimal.ZERO);
		 e.printStackTrace();
	 }
		
	}

}
