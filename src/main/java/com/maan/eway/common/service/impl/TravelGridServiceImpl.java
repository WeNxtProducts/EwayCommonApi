package com.maan.eway.common.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.service.TravelGridService;

import io.swagger.v3.oas.annotations.servers.Server;

@Transactional
@Service
public class TravelGridServiceImpl implements  TravelGridService {

	@Override
	public List<QuoteCriteriaRes> getTravelExistingQuoteDetails(ExistingQuoteReq req, List<String> branches,
			Date startDate, Date endDate, Integer limit, Integer offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QuoteCriteriaRes> getTravelLapsedQuoteDetails(ExistingQuoteReq req, List<String> branches,
			Date before30, int limit, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QuoteCriteriaRes> getMotorRejectedQuoteDetails(ExistingQuoteReq req, List<String> branches, int limit,
			int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QuoteCriteriaRes> getMotorReferalPendingDetails(ExistingQuoteReq req, List<String> branches, int limit,
			int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QuoteCriteriaRes> getMotorReferalApprovedDetails(ExistingQuoteReq req, List<String> branches, int limit,
			int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QuoteCriteriaRes> getMotorReferalRejectedDetails(ExistingQuoteReq req, List<String> branches, int limit,
			int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QuoteCriteriaRes> getMotorAdminReferalPendings(ExistingQuoteReq req, List<String> branches, int limit,
			int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QuoteCriteriaRes> getMotorAdminReferalApproved(ExistingQuoteReq req, List<String> branches, int limit,
			int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<QuoteCriteriaRes> getMotorAdminReferalRejected(ExistingQuoteReq req, List<String> branches, int limit,
			int offset) {
		// TODO Auto-generated method stub
		return null;
	}

}
