package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DasboardReferalPendingRes {

	@JsonProperty("RequestReferencNo")
    private String     requestReferencNo     ;
	@JsonProperty("CustomerName")
    private String     customerName     ;
	@JsonProperty("PolicyStartDate")
	private String policyStartDate;
	@JsonProperty("PolicyEndtDate")
	private String policyEndDate;
	@JsonProperty("Status")
    private String     status     ;
		
}
