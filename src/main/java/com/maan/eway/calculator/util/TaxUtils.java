package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.function.Function;

import javax.persistence.Tuple;

import com.maan.eway.res.calc.Tax;

public class TaxUtils  implements Function<Tuple,Tax>{
	private BigDecimal endtCount;
	public TaxUtils(BigDecimal endtCount) {
		super();
		this.endtCount = endtCount;
	}
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
					.regulatoryCode(t.get("taxCode")==null?"N/A":t.get("taxCode").toString())
					.endtTypeCount(endtCount)
					.dependentYn(t.get("dependentYn")==null?"N":t.get("dependentYn").toString())
					.taxExemptedAllowed(t.get("taxExemptAllowYn")==null?"Y":t.get("taxExemptAllowYn").toString())
					.minimumTaxAmountLc(t.get("minimumAmount")==null?BigDecimal.ZERO:new BigDecimal(t.get("minimumAmount").toString()))
					.minimumTaxAmount(t.get("minimumAmount")==null?BigDecimal.ZERO:new BigDecimal(t.get("minimumAmount").toString()))
					.taxAmountLc(BigDecimal.ZERO)
					.taxFor(t.get("taxFor")==null?"":t.get("taxFor").toString())
				 	.build();
			return d;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

 

}
