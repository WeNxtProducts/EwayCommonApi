package com.maan.eway.integration.service;

import com.maan.eway.integration.res.IntegrationSaveRes;

public interface FrameReqService {

	//Tanzania
	
	Object pushMotCommDiscountDetail(String policyNo,String companyId);

	Object pushMotDriverDetail(String policyNo,String companyId);

	Object pushYiCoverDetail(String policyNo,String companyId);
	
	Object pushYiChargeDetail(String policyNo,String companyId);

	Object pushYiPolicyDetail(String policyNo,String companyId);

	Object pushCreditLimitDetail(String reqRefNo,String companyId);

	Object pushYiPolicyApproval(String policyNo,String companyId);

	Object pushYiPremCal(String policyNo,String companyId);

	Object pushYiVatDetail(String policyNo,String companyId);

	Object pushYiSectionDetail(String policyNo,String companyId);
	
	Object pushPgitPolRiskAddlInfo(String policyNo,String companyId);

	//Madison

	Object pushPtIntgFlexTran(String policyNo);

	IntegrationSaveRes premiaExternalCall(String policyNo,String companyId);

	IntegrationSaveRes updatePremiaExternalCallStatus(String policyNo, String companyId);

}
