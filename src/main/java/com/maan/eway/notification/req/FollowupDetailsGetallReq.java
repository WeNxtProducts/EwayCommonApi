package com.maan.eway.notification.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FollowupDetailsGetallReq {

	
	
	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("ProductId")
	private String productId;

//	@JsonProperty("Status")
//	private String status;

	@JsonProperty("LoginId")
	private String loginId;    
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;    
	
}
