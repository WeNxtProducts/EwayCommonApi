package com.maan.eway.renewal.service;

import java.util.List;

import com.maan.eway.renewal.req.GetCustomersByBrokerReq;
import com.maan.eway.renewal.req.RenewalTrackingInReq;
import com.maan.eway.renewal.req.RtGetProductsReq;
import com.maan.eway.renewal.res.RenewalTrackByProductRes;
import com.maan.eway.renewal.res.RenewalTrackingDetails;
import com.maan.eway.renewal.res.RenewalTrackingInRes;

public interface RenewalTrackingService {

	RenewalTrackingInRes renewTrackByApprover(RenewalTrackingInReq req);

	List<RenewalTrackingDetails> getBrokersCustomerList(GetCustomersByBrokerReq req);

	RenewalTrackByProductRes renewTrackForProductPerf(RenewalTrackingInReq req);

	List<RenewalTrackingDetails> getAllByProductCode(RtGetProductsReq req);

}
