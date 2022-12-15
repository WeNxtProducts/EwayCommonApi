package com.maan.eway.common.service;

import java.util.Date;
import java.util.List;

import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.RejectCriteriaRes;

public interface TravelGridService {

	List<QuoteCriteriaRes> getTravelExistingQuoteDetails(ExistingQuoteReq req ,List<String> branches,  Date startDate ,Date  endDate , Integer limit , Integer offset );

	List<QuoteCriteriaRes> getTravelLapsedQuoteDetails(ExistingQuoteReq req, List<String> branches ,Date before30, int limit,int offset);

	List<RejectCriteriaRes> getTravelRejectedQuoteDetails(ExistingQuoteReq req, List<String> branches ,int limit, int offset);

	List<QuoteCriteriaRes> getTravelReferalPendingDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<QuoteCriteriaRes> getTravelReferalApprovedDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<RejectCriteriaRes> getTravelReferalRejectedDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<QuoteCriteriaRes> getTravelAdminReferalPendings(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<QuoteCriteriaRes> getTravelAdminReferalApproved(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<RejectCriteriaRes> getTravelAdminReferalRejected(ExistingQuoteReq req, List<String> branches, int limit,int offset);

}
