package com.maan.eway.req.referal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReferralRequest {

	private String apiLink;
	
	private String primaryId;

	private String apiRequest;
	
	
	
}
