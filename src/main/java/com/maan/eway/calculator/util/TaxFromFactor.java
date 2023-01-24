package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.function.Function;

import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.res.calc.Tax;

public class TaxFromFactor  implements Function<FactorRateRequestDetails,Tax>{

	@Override
	public Tax apply(FactorRateRequestDetails t) {
		try {
			Tax d=Tax.builder()
				 	.isTaxExempted(null)
				 	.taxAmount(BigDecimal.ZERO)
				 	.taxDesc(t.getTaxDesc())
				 	.taxExemptCode(null)
				 	.taxExemptType(null)
				 	.taxId(t.getTaxId()==null?"":t.getTaxId().toString())
				 	.taxRate(t.getRate()==null?0D: t.getRate().doubleValue() )				 	
				 	.calcType(t.getCalcType()==null?"":t.getCalcType())
				 	.build();
			return d;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

 

}
