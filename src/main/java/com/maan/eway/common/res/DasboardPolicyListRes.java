package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DasboardPolicyListRes {

	@JsonProperty("PolicyNo")
    private String    policyNo     ;
	@JsonProperty("RequestReferencNo")
    private String     requestReferencNo     ;
	@JsonProperty("QuoteNo")
    private String     quoteNo     ;
	@JsonProperty("CustomerName")
    private String     customerName     ;
	//@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyStartDate")
	private String policyStartDate;
//@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("PolicyEndtDate")
	private String policyEndDate;
	@JsonProperty("Status")
    private String     status     ;
		
}
