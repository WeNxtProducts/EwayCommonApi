package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.common.req.AdminReferalStatusReq;
import com.maan.eway.common.req.DeleteOldQuoteReq;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.req.SectionSumInsuredGetReq;
import com.maan.eway.common.req.TracesRemovedReq;
import com.maan.eway.common.req.UpdateQuoteStatusReq;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.QuoteUpdateRes;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.error.Error;
import com.maan.eway.res.SectionWiseSumInsuredRes;
import com.maan.eway.res.SuccessRes;


public interface QuoteService {

	CommonRes generateNewQuote(NewQuoteReq req);

	ViewQuoteRes viewQuoteDetails(ViewQuoteReq req);

	List<Error> validateReferralStatus(AdminReferalStatusReq req);

	QuoteUpdateRes updateReferralStatus(AdminReferalStatusReq req);

	SuccessRes deleteOldQuoteRecord(DeleteOldQuoteReq req);

	SectionWiseSumInsuredRes sectionWiseSuminsuredDetails(SectionSumInsuredGetReq req);

	List<Error> validateQuoteStatus(UpdateQuoteStatusReq req);

	QuoteUpdateRes updateQuoteStatus(UpdateQuoteStatusReq req);

	List<Error> validateNewQuoteDetails(NewQuoteReq req);

	SuccessRes tracesRemoved(TracesRemovedReq req);



}
