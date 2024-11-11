package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.res.calc.Tax;

public class TaxFromFactor  implements Function<FactorRateRequestDetails,Tax>{

	@Override
	public Tax apply(FactorRateRequestDetails t) {
		try {
			Tax d=Tax.builder()
					.isTaxExempted(t.getIsTaxExtempted())
					.taxAmount(t.getTaxAmount())
					.taxDesc(t.getTaxDesc())
					.taxExemptCode(t.getTaxExemptCode())
					.taxExemptType(t.getTaxExemptType())
					.taxId(t.getTaxId()==null?"":t.getTaxId().toString())
					.taxRate(t.getTaxRate()==null?0D: t.getTaxRate().doubleValue() )				 	
					.calcType(t.getTaxCalcType()==null?"":t.getTaxCalcType())
					.endtTypeId(t.getDiscLoadId().toString())
					.endtTypeCount(t.getEndtCount())							 
 					.regulatoryCode(t.getRegulatoryCode())
					.endtTypeCount(t.getEndtCount())
					.dependentYn(t.getDependentCoverYn())
					.taxExemptedAllowed(t.getIsTaxExtempted())
					.minimumTaxAmountLc(BigDecimal.ZERO)
					.minimumTaxAmount(BigDecimal.ZERO)
					.taxAmountLc(t.getTaxAmountLc())
					.taxFor("")					
					.build();
			return d;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

 

}
