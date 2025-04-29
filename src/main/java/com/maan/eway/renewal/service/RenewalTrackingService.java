package com.maan.eway.renewal.service;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import com.maan.eway.renewal.req.RenewalTrackAgentResByProduct2;
import com.maan.eway.renewal.req.RenewalTrackReq;
import com.maan.eway.renewal.res.BranchForRenewalTrack;
import com.maan.eway.renewal.res.PolicyDet;
import com.maan.eway.renewal.res.ProductByBranch;
import com.maan.eway.renewal.res.ProductsBySourceRes;

public interface RenewalTrackingService {



	List<ProductByBranch> GetRenewalDetailsByDivsion2(String divisionCode, String companyId);


	BranchForRenewalTrack RenewalTrackGetBranch(String companyId);

	List<RenewalTrackAgentResByProduct2> RenewalTrackAgentRes2(String divisionCode, String companyId,
			String productCode);

	ProductsBySourceRes getProductsBySource(@RequestBody RenewalTrackReq req );
	
	List<PolicyDet> RenewalTrackPolicyDetailsBySource(String divisionCode, String companyId, String productCode,
			String brokerCode);
	
	

}
