package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.function.Function;

import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.res.calc.Discount;

public class DiscountFromFactor  implements Function<FactorRateRequestDetails,Discount>{

	@Override
	public Discount apply(FactorRateRequestDetails t) {
		try {
			 if(t.getCoverageType()!=null && "D".equalsIgnoreCase(t.getCoverageType())) {
				 String calctype=t.getCalcType()==null?"":t.getCalcType();
				 Discount d=Discount.builder()
						 	.discountDesc(t.getCoverName()==null?"":t.getCoverName())
						 	.discountId(t.getCoverId()==null?"":t.getCoverId().toString())
						 	.discountRate("F".equals(calctype)?"0": t.getRate()==null?"0":t.getRate().toString())
						 	.discountCalcType(calctype)
						 	.discountforId(t.getDiscLoadId()==null?"":t.getDiscLoadId().toString())
						 	.maxAmount(t.getMinimumPremium()==null?BigDecimal.ZERO:new BigDecimal(t.getMinimumPremium()))
						 	.factorTypeId(t.getFactorTypeId()==null?"":t.getFactorTypeId().toString())
						 	.regulatoryCode(t.getRegulatoryCode()==null?"N/A":t.getRegulatoryCode())
						 	.build();
				 return d;
			 }
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

 

}
