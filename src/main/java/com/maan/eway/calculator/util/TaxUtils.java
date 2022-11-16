package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.function.Function;

import javax.persistence.Tuple;

import com.maan.eway.res.calc.Tax;

public class TaxUtils  implements Function<Tuple,Tax>{

	@Override
	public Tax apply(Tuple t) {
		try {
			Tax d=Tax.builder()
				 	.isTaxExempted(null)
				 	.taxAmount(BigDecimal.ZERO)
				 	.taxDesc(t.get("taxName")==null?"":t.get("taxName").toString())
				 	.taxExemptCode(null)
				 	.taxExemptType(null)
				 	.taxId(t.get("taxId")==null?"":t.get("taxId").toString())
				 	.taxRate(t.get("value")==null?0D:Double.parseDouble(t.get("value").toString()))
				 	.calcType(t.get("calcType")==null?"":t.get("calcType").toString())
				 	.build();
			return d;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

 

}
