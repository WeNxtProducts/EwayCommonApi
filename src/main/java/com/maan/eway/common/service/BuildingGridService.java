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

public interface BuildingGridService {
	
	List<QuoteCriteriaRes> getBuildingExistingQuoteDetails(ExistingQuoteReq req ,List<String> branches,  Date startDate ,Date  endDate , Integer limit , Integer offset );

	List<QuoteCriteriaRes> getBuildingLapsedQuoteDetails(ExistingQuoteReq req, List<String> branches ,Date before30, int limit,int offset);

	List<RejectCriteriaRes> getBuildingRejectedQuoteDetails(ExistingQuoteReq req, List<String> branches ,int limit, int offset);

	List<QuoteCriteriaRes> getBuildingReferalPendingDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<QuoteCriteriaRes> getBuildingReferalApprovedDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<RejectCriteriaRes> getBuildingReferalRejectedDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<QuoteCriteriaRes> getBuildingAdminReferalPendings(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<QuoteCriteriaRes> getBuildingAdminReferalApproved(ExistingQuoteReq req, List<String> branches, int limit,int offset);

	List<RejectCriteriaRes> getBuildingAdminReferalRejected(ExistingQuoteReq req, List<String> branches, int limit,int offset);
	
	List<Tuple> searchBuildingQuote(CopyQuoteReq req, List<String> branches);

	SuccessRes buildingCopyQuote(CopyQuoteReq req, List<String> branches);

	List<ListItemValue> geBuildingCoptyQuotetListItem(CopyQuoteDropDownReq req,String itemType);
}
