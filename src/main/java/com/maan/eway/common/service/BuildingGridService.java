package com.maan.eway.common.service;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import com.maan.eway.admin.res.ReferalCriteriaRes;
import com.maan.eway.admin.res.ReferalGridCriteriaRes;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.res.PortfolioPendingGridCriteriaRes;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.res.CopyQuoteSuccessRes;
import com.maan.eway.res.SuccessRes;

public interface BuildingGridService {
	
	List<QuoteCriteriaRes> getBuildingExistingQuoteDetails(ExistingQuoteReq req ,List<String> branches,  Date startDate ,Date  endDate , Integer limit , Integer offset );

	List<QuoteCriteriaRes> getBuildingLapsedQuoteDetails(ExistingQuoteReq req, List<String> branches ,Date before30, int limit,int offset);

	List<RejectCriteriaRes> getBuildingRejectedQuoteDetails(ExistingQuoteReq req, List<String> branches ,int limit, int offset);

	List<ReferalGridCriteriaRes> getBuildingReferalDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset , String Status);

	List<ReferalGridCriteriaRes> getBuildingAdminReferalDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset , String Status);
	
	List<Tuple> searchBuildingQuote(CopyQuoteReq req, List<String> branches);

	CopyQuoteSuccessRes buildingCopyQuote(CopyQuoteReq req, List<String> branches,String loginId);

	List<ListItemValue> geBuildingCoptyQuotetListItem(CopyQuoteDropDownReq req,String itemType);

	CopyQuoteSuccessRes buildingEndt(CopyQuoteReq req, List<String> branches, String loginId);

	List<PortfolioPendingGridCriteriaRes> getBuildingProtfolioPending(ExistingQuoteReq req, List<String> branches,
			Date today, int limit, int offset, String string);


}
