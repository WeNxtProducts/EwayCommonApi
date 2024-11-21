package com.maan.eway.integration.service;

import com.maan.eway.integration.req.ValuationDetailsReq;
import com.maan.eway.integration.req.ValuationReq;
import com.maan.eway.integration.req.ValuationStatusReq;
import com.maan.eway.integration.res.PremiaResponse;

public interface ValuationService {

	PremiaResponse pushValuation(ValuationReq req);

	PremiaResponse getStatus(ValuationStatusReq req);

	PremiaResponse getDetails(ValuationDetailsReq req);

	

	
}
