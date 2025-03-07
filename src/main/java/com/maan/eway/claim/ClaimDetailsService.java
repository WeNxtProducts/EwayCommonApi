package com.maan.eway.claim;

import java.util.List;

import com.maan.eway.common.res.ViewQuoteRes;

public interface ClaimDetailsService {

	List<PolicyDetailsResponseDto> policydetailsbyregno(PolicyDetailsReq req);

	ViewQuoteRes claimViewQuoteDetails(PolicyDetailsReq req);



}
