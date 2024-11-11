package com.maan.eway.integration.service;

public interface FrameReqService {

	//Tanzania
	
	Object pushMotCommDiscountDetail(String policyNo);

	Object pushMotDriverDetail(String policyNo);

	Object pushYiCoverDetail(String policyNo);

	Object pushYiChargeDetail(String policyNo);

	Object pushYiPolicyDetail(String policyNo);

	Object pushCreditLimitDetail(String reqRefNo);

	Object pushYiPolicyApproval(String policyNo);

	Object pushYiPremCal(String policyNo);

	Object pushYiVatDetail(String policyNo);

	Object pushYiSectionDetail(String policyNo);
	
	Object pushPgitPolRiskAddlInfo(String policyNo);

	//Madison

	Object pushPtIntgFlexTran(String policyNo);

}
