package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.function.Function;

import jakarta.persistence.Tuple;

import com.maan.eway.res.calc.Loading;

public class SplitLoadingUtils  implements Function<Tuple,Loading>{

	private Date effectiveDate;
	private Date policyEndDate;
	
	public SplitLoadingUtils(Date effectiveDate, Date policyEndDate) {
		this.effectiveDate=effectiveDate;
		this.policyEndDate=policyEndDate;
	}

	@Override
	public Loading apply(Tuple t) {
		try {
			 if(t.get("coverageType")!=null && "L".equalsIgnoreCase(t.get("coverageType").toString())) {
				 String calctype=t.get("calcType")==null?"":t.get("calcType").toString();
				 Loading d=Loading.builder()
						 	.loadingDesc(t.get("coverName")==null?"":t.get("coverName").toString())
						 	.loadingId(t.get("coverId")==null?"":t.get("coverId").toString())
						 	.loadingRate("F".equals(calctype)?"0": t.get("baseRate")==null?"0":t.get("baseRate").toString())
						 	.loadingCalcType(calctype)
						 	.loadingforId(t.get("discountCoverId")==null?"":t.get("discountCoverId").toString())
						 	.maxAmount(t.get("minPremium")==null?BigDecimal.ZERO:new BigDecimal(t.get("minPremium").toString()))
						 	.factorTypeId(t.get("factorTypeId")==null?"":t.get("factorTypeId").toString())
							.regulatoryCode(t.get("regulatoryCode")==null?"N/A":t.get("regulatoryCode").toString())
							.effectiveDate(effectiveDate)
							.policyEndDate(policyEndDate)
							.minrate(t.get("minimumRate")==null?0D:Double.parseDouble(t.get("minimumRate").toString()))
						 	.build();
				 return d;
			 }
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

 

}
