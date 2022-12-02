package com.maan.eway.common.service;

import java.util.Date;
import java.util.List;

import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.res.QuoteCriteriaRes;

public interface TravelGridService {

	List<QuoteCriteriaRes> getTravelExistingQuoteDetails(ExistingQuoteReq req ,List<String> branches,  Date startDate ,Date  endDate , Integer limit , Integer offset );

	List<QuoteCriteriaRes> getTravelLapsedQuoteDetails(ExistingQuoteReq req, List<String> branches ,Date before30, int limit,int offset);

	List<QuoteCriteriaRes> getMotorRejectedQuoteDetails(ExistingQuoteReq req, List<String> branches ,int limit, int offset);

	List<QuoteCriteriaRes> getMotorReferalPendingDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<QuoteCriteriaRes> getMotorReferalApprovedDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<QuoteCriteriaRes> getMotorReferalRejectedDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<QuoteCriteriaRes> getMotorAdminReferalPendings(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<QuoteCriteriaRes> getMotorAdminReferalApproved(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<QuoteCriteriaRes> getMotorAdminReferalRejected(ExistingQuoteReq req, List<String> branches, int limit,int offset);

}
