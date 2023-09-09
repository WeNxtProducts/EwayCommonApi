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
import com.maan.eway.common.res.GetCommonReferalDetailsRes;
import com.maan.eway.common.res.GetRejectedQuoteDetailsRes;
import com.maan.eway.common.res.PortfolioPendingGridCriteriaRes;
import com.maan.eway.common.res.QuoteCriteriaResponse;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.res.CopyQuoteSuccessRes;

public interface CommonGridService {
	
	QuoteCriteriaResponse getCommonExistingQuoteDetails(ExistingQuoteReq req , Date startDate ,Date  endDate , Integer limit , Integer offset );

	QuoteCriteriaResponse getCommonLapsedQuoteDetails(ExistingQuoteReq req, Date before30, int limit,int offset);

	GetRejectedQuoteDetailsRes getCommonRejectedQuoteDetails(ExistingQuoteReq req, Date startDate ,Date  endDate ,int limit, int offset);

	GetCommonReferalDetailsRes getCommonReferalDetails(ExistingQuoteReq req, int limit,int offset , String Status);

	GetCommonReferalDetailsRes getCommonAdminReferalDetails(ExistingQuoteReq req,  int limit,int offset , String Status);
	
	List<Tuple> searchCommonQuote(CopyQuoteReq req, List<String> branches);

	CopyQuoteSuccessRes commonCopyQuote(CopyQuoteReq req, List<String> branches);


	List<Tuple> validateCommonEndt(String quoteNo);



	List<PortfolioPendingGridCriteriaRes> getCommonProtfolioPending(ExistingQuoteReq req, List<String> branches,Date startDate,  int limit,
			int offset, String string);



	CopyQuoteSuccessRes commonEndt(CopyQuoteReq req, List<String> branches, String loginId);

	GetBuildingAdminReferalPendingDetailsRes getCommonAdminReferalPendingDetails(RevertGridReq req, int limit, int offset,
			String string);

	List<ReferalGridCriteriaAdminRes> getCommonAdminReferalPendingDetailsCount(RevertGridReq req, String string);

	List<ListItemValue> getCommonCoptyQuotetListItem(CopyQuoteDropDownReq req, String itemType);
}
