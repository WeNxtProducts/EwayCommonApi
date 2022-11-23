package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.res.EserviceCustomerDetailsRes;

public interface GridService {

	List<EserviceCustomerDetailsRes> getallExistingQuoteDetails(ExistingQuoteReq req);

	List<EserviceCustomerDetailsRes> getallLapsedQuoteDetails(ExistingQuoteReq req);

	List<EserviceCustomerDetailsRes> getallRejectedQuoteDetails(ExistingQuoteReq req);

	List<EserviceCustomerDetailsRes> getallReferralPendingDetails(ExistingQuoteReq req);

	List<EserviceCustomerDetailsRes> getallReferralApprovedDetails(ExistingQuoteReq req);

	List<EserviceCustomerDetailsRes> getallReferralRejectedDetails(ExistingQuoteReq req);

	List<EserviceCustomerDetailsRes> getallAdminReferralPendings(ExistingQuoteReq req);

	List<EserviceCustomerDetailsRes> getallAdminReferralApproved(ExistingQuoteReq req);

	List<EserviceCustomerDetailsRes> getallAdminReferralRejected(ExistingQuoteReq req);

}
