package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EmiEndtDetailsReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("PrevPolicyNo")
	private String prevPolicyNo;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("PolicyType")
	private String policyType;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("EmiYn")
	private String emiYn;
	
	@JsonProperty("EndtId")
	private String endtId;
	
	@JsonProperty("Premium")
	private String premium;
	
	@JsonProperty("IsChargeRefund")
	private String isChargeRefund;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	

	
}
