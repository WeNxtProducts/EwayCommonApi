package com.maan.eway.crm.service;

import java.util.List;

import com.maan.eway.auth.dto.ChangePasswordReq;
import com.maan.eway.auth.dto.ProductDropDownRes;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.crm.bean.UserLoginResponseData;

public interface CrmService {

	UserLoginResponseData validateTokenForCRM(String token);

	List<ProductDropDownRes> getProductDetailByLoginId(String loginId, String companyId);

	void updatePassword(ChangePasswordReq req, String url);

	String getEnqiryDetail(Long enquiryId, String token);

	List<EserviceCustomerDetails> getCustomerDetailByLeadseqNo(Long leadSeqNo, String companyId, String token);


}
