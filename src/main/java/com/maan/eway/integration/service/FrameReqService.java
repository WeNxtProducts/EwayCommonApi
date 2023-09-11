package com.maan.eway.integration.service;

import java.util.List;

public interface FrameReqService {

	Object pushMotCommDiscountDetail(String policyNo);

	Object pushMotDriverDetail(String policyNo);

	Object pushYiCoverDetail(String policyNo);

	Object pushYiChargeDetail(String policyNo);

	Object pushYiPolicyDetail(String policyNo);

//	Object pushPgitPolRiskAddlInfo(String policyNo);

}
