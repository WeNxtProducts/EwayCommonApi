package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EserviceCustomerDetailsRes {


    @JsonProperty("CustomerReferenceNo")
    private String   customerReferenceNo ;
    
    @JsonProperty("RequestReferenceNo")
    private String   requestReferenceNo ;
	
    @JsonProperty("ClientName")
	private String clientName;

	
	
	@JsonProperty("IdNumber")
	private String idNumber;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyStartDate")
	private Date policyStartDate;
	

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
	private Date policyEndDate;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	  
	@JsonProperty("CustomerId")
	private String customerId;
	
	@JsonProperty("RejectReason")
	private String rejectReason;
	
	@JsonProperty("Count")
	private String   count ;
	
	@JsonProperty("AdminRemarks")
	private String adminRemarks;
	
	@JsonProperty("ReferalRemarks")
	private String referalRemarks;
	
	

	

	
	
}
