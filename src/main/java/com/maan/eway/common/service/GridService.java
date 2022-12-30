package com.maan.eway.common.service;

import java.util.List;
import com.maan.eway.error.Error;
import com.maan.eway.common.req.CopyQuoteReq;

import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.UpdateLapsedQuoteReq;
import com.maan.eway.common.res.EserviceCustomerDetailsRes;
import com.maan.eway.common.res.GetAllMotorDetailsRes;

import com.maan.eway.common.res.UpdateLapsedQuoteRes;

import com.maan.eway.common.res.PortfolioCustomerDetailsRes;

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

	List<EserviceCustomerDetailsRes> getallReferralRequoteDetails(ExistingQuoteReq req);

	List<EserviceCustomerDetailsRes> getallAdminReferralRequote(ExistingQuoteReq req);

	List<Error> validateQuotoNo(CopyQuoteReq req);


	UpdateLapsedQuoteRes updateLapsedQuoteDetails(UpdateLapsedQuoteReq req);

	List<PortfolioCustomerDetailsRes> getallPortfolioActive(ExistingQuoteReq req);

	List<PortfolioCustomerDetailsRes> getallPortfolioPending(ExistingQuoteReq req);

	List<PortfolioCustomerDetailsRes> getallPortfolioCancelled(ExistingQuoteReq req);



}
