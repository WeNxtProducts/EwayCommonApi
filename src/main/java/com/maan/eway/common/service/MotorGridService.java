package com.maan.eway.common.service;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import com.maan.eway.bean.ListItemValue;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.res.SuccessRes;

public interface MotorGridService {
	
	List<QuoteCriteriaRes> getMotorExistingQuoteDetails(ExistingQuoteReq req ,List<String> branches,  Date startDate ,Date  endDate , Integer limit , Integer offset );

	List<QuoteCriteriaRes> getMotorLapsedQuoteDetails(ExistingQuoteReq req, List<String> branches ,Date before30, int limit,int offset);

	List<RejectCriteriaRes> getMotorRejectedQuoteDetails(ExistingQuoteReq req, List<String> branches ,int limit, int offset);

	List<QuoteCriteriaRes> getMotorReferalPendingDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<QuoteCriteriaRes> getMotorReferalApprovedDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<RejectCriteriaRes> getMotorReferalRejectedDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<QuoteCriteriaRes> getMotorAdminReferalPendings(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<QuoteCriteriaRes> getMotorAdminReferalApproved(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<RejectCriteriaRes> getMotorAdminReferalRejected(ExistingQuoteReq req, List<String> branches, int limit,int offset);
	
	List<Tuple> searchMotorQuote(CopyQuoteReq req, List<String> branches);

	SuccessRes motorCopyQuote(CopyQuoteReq req, List<String> branches);

	List<ListItemValue> geMotorCoptyQuotetListItem(CopyQuoteDropDownReq req,String itemType);
}
