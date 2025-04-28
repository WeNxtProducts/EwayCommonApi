package com.maan.eway.renewal.service;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import com.maan.eway.renewal.req.GetCustomersByBrokerReq;
import com.maan.eway.renewal.req.GetPolicyBySourceReq;
import com.maan.eway.renewal.req.RenewalTrackAgentResByProduct2;
import com.maan.eway.renewal.req.RenewalTrackReq;
import com.maan.eway.renewal.req.RenewalTrackingInReq;
import com.maan.eway.renewal.req.RtGetProductsReq;
import com.maan.eway.renewal.res.BranchForRenewalTrack;
import com.maan.eway.renewal.res.GetPolicyBySourceRes;
import com.maan.eway.renewal.res.ProductByBranch;
import com.maan.eway.renewal.res.ProductsBySourceRes;
import com.maan.eway.renewal.res.RenewalTrackAgentResByProduct;
import com.maan.eway.renewal.res.RenewalTrackAgentResByProduct.PolicyDetail;
import com.maan.eway.renewal.res.RenewalTrackByProductRes;
import com.maan.eway.renewal.res.RenewalTrackByProductResByDivision;
import com.maan.eway.renewal.res.RenewalTrackingDetails;
import com.maan.eway.renewal.res.RenewalTrackingInRes;

public interface RenewalTrackingService {



	List<ProductByBranch> GetRenewalDetailsByDivsion2(String divisionCode, String companyId);


	BranchForRenewalTrack RenewalTrackGetBranch(String companyId);

	List<RenewalTrackAgentResByProduct2> RenewalTrackAgentRes2(String divisionCode, String companyId,
			String productCode);

	ProductsBySourceRes getProductsBySource(@RequestBody RenewalTrackReq req );
	
	List<PolicyDetail> RenewalTrackPolicyDetailsBySource(String divisionCode, String companyId, String productCode,
			String brokerCode);
	
	

}
