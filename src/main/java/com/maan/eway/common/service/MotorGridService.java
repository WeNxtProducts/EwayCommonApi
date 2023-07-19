package com.maan.eway.common.service;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import com.maan.eway.admin.res.MotorGridCriteriaRes;
import com.maan.eway.admin.res.PortfolioGridCriteriaRes;
import com.maan.eway.admin.res.ReferalCriteriaRes;
import com.maan.eway.admin.res.ReferalGridCriteriaRes;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.GetallPolicyReportsReq;
import com.maan.eway.common.req.IssuerQuoteReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.res.CopyQuoteSuccessRes;
import com.maan.eway.res.SuccessRes;

public interface MotorGridService {
	
	List<QuoteCriteriaRes> getMotorExistingQuoteDetails(ExistingQuoteReq req ,List<String> branches,  Date startDate ,Date  endDate , Integer limit , Integer offset );

	List<QuoteCriteriaRes> getMotorLapsedQuoteDetails(ExistingQuoteReq req, List<String> branches ,Date before30, int limit,int offset);

	List<RejectCriteriaRes> getMotorRejectedQuoteDetails(ExistingQuoteReq req, List<String> branches ,int limit, int offset);

	List<MotorGridCriteriaRes> getMotorReferalDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset , String Status);

	List<MotorGridCriteriaRes> getMotorAdminReferalDetails(ExistingQuoteReq req, List<String> branches, int limit,int offset , String Status);
	
	List<Tuple> searchMotorQuote(CopyQuoteReq req, List<String> branches);

	CopyQuoteSuccessRes motorCopyQuote(CopyQuoteReq req, List<String> branches,String loginId);

	List<ListItemValue> geMotorCoptyQuotetListItem(CopyQuoteDropDownReq req,String itemType);

	CopyQuoteSuccessRes motorEndt(CopyQuoteReq req, List<String> branches,String loginId);

	List<Tuple> validateMotorEndt(String quoteNo);

	List<PortfolioGridCriteriaRes> getMotorProtfolioActive(ExistingQuoteReq req, List<String> branches, Date startDate,int limit,
			int offset, String string);

	List<PortfolioGridCriteriaRes> getMotorProtfolioPending(ExistingQuoteReq req, List<String> branches,Date startDate,  int limit,
			int offset, String string);

	List<PortfolioGridCriteriaRes> getMotorPortfolioCancelled(ExistingQuoteReq req, List<String> branches,Date startDate,  int limit,
			int offset, String string);

	List<Tuple> getMotorIssuerQuoteDetails(IssuerQuoteReq req, Date startDate, Date endDate);

	List<Tuple> getMotorReportDetails(GetallPolicyReportsReq req);
	
}
