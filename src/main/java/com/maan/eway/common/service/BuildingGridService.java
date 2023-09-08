package com.maan.eway.common.service;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import com.maan.eway.admin.res.GetBuildingAdminReferalPendingDetailsRes;
import com.maan.eway.admin.res.ReferalGridCriteriaAdminRes;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.RevertGridReq;
import com.maan.eway.common.res.GetRejectedQuoteDetailsRes;
import com.maan.eway.common.res.GetTravelReferalDetailsRes;
import com.maan.eway.common.res.PortfolioPendingGridCriteriaRes;
import com.maan.eway.common.res.QuoteCriteriaResponse;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.res.CopyQuoteSuccessRes;

public interface BuildingGridService {
	
	QuoteCriteriaResponse getBuildingExistingQuoteDetails(ExistingQuoteReq req , Date startDate ,Date  endDate , Integer limit , Integer offset );

	QuoteCriteriaResponse getBuildingLapsedQuoteDetails(ExistingQuoteReq req, Date before30, int limit,int offset);

	GetRejectedQuoteDetailsRes getBuildingRejectedQuoteDetails(ExistingQuoteReq req,Date startDate, Date endDate, int limit, int offset);

	GetTravelReferalDetailsRes getBuildingReferalDetails(ExistingQuoteReq req,  int limit,int offset , String Status);

	GetTravelReferalDetailsRes getBuildingAdminReferalDetails(ExistingQuoteReq req,  int limit,int offset , String Status);
	
	List<Tuple> searchBuildingQuote(CopyQuoteReq req, List<String> branches);

	CopyQuoteSuccessRes buildingCopyQuote(CopyQuoteReq req, List<String> branches,String loginId);

	List<ListItemValue> geBuildingCoptyQuotetListItem(CopyQuoteDropDownReq req,String itemType);

	CopyQuoteSuccessRes buildingEndt(CopyQuoteReq req, List<String> branches, String loginId);

	List<PortfolioPendingGridCriteriaRes> getBuildingProtfolioPending(ExistingQuoteReq req, List<String> branches,
			Date today, int limit, int offset, String string);

	GetBuildingAdminReferalPendingDetailsRes getBuildingAdminReferalPendingDetails(RevertGridReq req, int limit, int offset,
			String string);

	List<ReferalGridCriteriaAdminRes> getBuildingAdminReferalPendingDetailsCount(RevertGridReq req, String string);


}
