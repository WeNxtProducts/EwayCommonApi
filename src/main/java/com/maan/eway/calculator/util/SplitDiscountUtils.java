package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.function.Function;

import javax.persistence.Tuple;

import com.maan.eway.res.calc.Discount;

public class SplitDiscountUtils  implements Function<Tuple,Discount>{

	@Override
	public Discount apply(Tuple t) {
		try {
			 if(t.get("coverageType")!=null && ("D".equalsIgnoreCase(t.get("coverageType").toString()) || "P".equalsIgnoreCase(t.get("coverageType").toString())  )) {
				 String calctype=t.get("calcType")==null?"":t.get("calcType").toString();
				 Discount d=Discount.builder()
						 	.discountDesc(t.get("coverName")==null?"":t.get("coverName").toString())
						 	.discountId(t.get("coverId")==null?"":t.get("coverId").toString())
						 	.discountRate("F".equals(calctype)?"0": t.get("baseRate")==null?"0":t.get("baseRate").toString())
						 	.discountCalcType(calctype)
						 	.discountforId(t.get("discountCoverId")==null?"":t.get("discountCoverId").toString())
						 	.maxAmount(t.get("minPremium")==null?BigDecimal.ZERO:new BigDecimal(t.get("minPremium").toString()))
						 	.factorTypeId(t.get("factorTypeId")==null?"":t.get("factorTypeId").toString())
						 	.coverAgeType(t.get("coverageType").toString())
						 	.build();
				 return d;
			 }
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

 

}
