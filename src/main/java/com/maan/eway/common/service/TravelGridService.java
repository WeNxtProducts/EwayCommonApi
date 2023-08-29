package com.maan.eway.common.service;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import com.maan.eway.admin.res.ReferalCriteriaRes;
import com.maan.eway.admin.res.ReferalGridCriteriaAdminRes;
import com.maan.eway.admin.res.ReferalGridCriteriaRes;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.GetallPolicyReportsReq;
import com.maan.eway.common.req.RevertGridReq;
import com.maan.eway.common.res.PortfolioPendingGridCriteriaRes;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.res.CopyQuoteSuccessRes;
import com.maan.eway.res.SuccessRes;

public interface TravelGridService {

	List<QuoteCriteriaRes> getTravelExistingQuoteDetails(ExistingQuoteReq req ,List<String> branches,  Date startDate ,Date  endDate , Integer limit , Integer offset );

	List<QuoteCriteriaRes> getTravelLapsedQuoteDetails(ExistingQuoteReq req, List<String> branches ,Date before30, int limit,int offset);

	List<RejectCriteriaRes> getTravelRejectedQuoteDetails(ExistingQuoteReq req, List<String> branches ,int limit, int offset);

	List<ReferalGridCriteriaRes> getTravelReferalDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset , String Status);

	List<ReferalGridCriteriaRes> getTravelAdminReferalDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset , String Status);

	List<ListItemValue> getTravelCoptyQuotetListItem(CopyQuoteDropDownReq req, String itemType);

	List<Tuple> searchTravelQuote(CopyQuoteReq req, List<String> branches);

	CopyQuoteSuccessRes travelCopyQuote(CopyQuoteReq req, List<String> branches,String loginId);

	CopyQuoteSuccessRes travelEndt(CopyQuoteReq req, List<String> branches, String loginId);

	List<Tuple> getTravelReportDetails(GetallPolicyReportsReq req);

	List<PortfolioPendingGridCriteriaRes> getTravelProtfolioPending(ExistingQuoteReq req, List<String> branches,
			Date today, int limit, int offset, String string);

	List<ReferalGridCriteriaAdminRes> getTravelAdminReferalPendingDetails(RevertGridReq req, int limit, int offset,
			String string);

	List<ReferalGridCriteriaAdminRes> getTravelAdminReferalPendingDetailsCount(RevertGridReq req, String string);

}
