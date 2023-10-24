package com.maan.eway.common.service;

import java.util.Date;
import java.util.List;

import com.maan.eway.common.req.ExistingBrokerUserListReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.res.GetExistingBrokerListRes;
import com.maan.eway.common.res.GetRejectedQuoteDetailsRes;
import com.maan.eway.common.res.QuoteCriteriaResponse;

public interface LifeGridService {

	QuoteCriteriaResponse getLifeExistingQuoteDetails(ExistingQuoteReq req, Date before30, Date today, int limit,
			int offset);

	List<GetExistingBrokerListRes> getLifeExistingDropdown(ExistingBrokerUserListReq req, Date today, Date before30);

	List<GetExistingBrokerListRes> getBrokerUserListLapsedLife(ExistingBrokerUserListReq req, Date today,
			Date before30);

	QuoteCriteriaResponse getLifeLapsedQuoteDetails(ExistingQuoteReq req, Date before30, int limit, int offset);

	GetRejectedQuoteDetailsRes getLifeRejectedQuoteDetails(ExistingQuoteReq req, Date before30, Date today, int limit,
			int offset);

	List<GetExistingBrokerListRes> getBrokerUserListLifeRejected(ExistingBrokerUserListReq req, Date today,
			Date before30);

}
