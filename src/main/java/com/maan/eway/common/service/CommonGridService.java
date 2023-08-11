package com.maan.eway.common.service;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import com.maan.eway.admin.res.PortfolioGridCriteriaRes;
import com.maan.eway.admin.res.ReferalCommonCriteriaRes;
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

public interface CommonGridService {
	
	List<QuoteCriteriaRes> getCommonExistingQuoteDetails(ExistingQuoteReq req ,List<String> branches,  Date startDate ,Date  endDate , Integer limit , Integer offset );

	List<QuoteCriteriaRes> getCommonLapsedQuoteDetails(ExistingQuoteReq req, List<String> branches ,Date before30, int limit,int offset);

	List<RejectCriteriaRes> getCommonRejectedQuoteDetails(ExistingQuoteReq req, List<String> branches ,int limit, int offset);

	List<ReferalCommonCriteriaRes> getCommonReferalDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset , String Status);

	List<ReferalCommonCriteriaRes> getCommonAdminReferalDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset , String Status);
	
	List<Tuple> searchCommonQuote(CopyQuoteReq req, List<String> branches);

	CopyQuoteSuccessRes commonCopyQuote(CopyQuoteReq req, List<String> branches);

	List<ListItemValue> geCommonCoptyQuotetListItem(CopyQuoteDropDownReq req,String itemType);

	List<Tuple> validateCommonEndt(String quoteNo);

	List<PortfolioGridCriteriaRes> getCommonProtfolioActive(ExistingQuoteReq req, List<String> branches, Date startDate,int limit,
			int offset, String string);

	List<PortfolioPendingGridCriteriaRes> getCommonProtfolioPending(ExistingQuoteReq req, List<String> branches,Date startDate,  int limit,
			int offset, String string);

	List<PortfolioGridCriteriaRes> getCommonPortfolioCancelled(ExistingQuoteReq req, List<String> branches,Date startDate,  int limit,
			int offset, String string);

	CopyQuoteSuccessRes commonEndt(CopyQuoteReq req, List<String> branches, String loginId);
}
