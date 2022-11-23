package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.function.Function;

import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.res.calc.Loading;

public class LoadingFromFactor  implements Function<FactorRateRequestDetails,Loading>{

	@Override
	public Loading apply(FactorRateRequestDetails t) {
		try {
			 if("L".equalsIgnoreCase(t.getCoverageType())) {
				 String calctype=t.getCalcType()==null?"":t.getCalcType();
				 Loading d=Loading.builder()
						 	.loadingDesc(t.getCoverName()==null?"":t.getCoverName())
						 	.loadingId(t.getCoverId()==null?"":t.getCoverId().toString())
						 	.loadingRate("F".equals(calctype)?"0": t.getRate()==null?"0":t.getRate().toString())
						 	.loadingCalcType(calctype)
						 	.loadingforId(t.getDiscLoadId()==null?"":t.getDiscLoadId().toString())
						 	.maxAmount(t.getMinimumPremium()==null?BigDecimal.ZERO:new BigDecimal(t.getMinimumPremium()))
						 	.factorTypeId(t.getFactorTypeId()==null?"":t.getFactorTypeId().toString())
						 	.build();
				 return d;
			 }
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

 

}
