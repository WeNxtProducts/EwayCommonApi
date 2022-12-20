package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.EserviceCustomerSearchVrtinReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.res.EserviceCustomerDetailsRes;
import com.maan.eway.common.res.GetAllMotorDetailsRes;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

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



	List<GetAllMotorDetailsRes> getbyReqRefNo(CopyQuoteReq req);

	SuccessRes copyQuote(CopyQuoteReq req);

	List<DropDownRes> copyQuoteByDropdown(CopyQuoteDropDownReq req);

}
