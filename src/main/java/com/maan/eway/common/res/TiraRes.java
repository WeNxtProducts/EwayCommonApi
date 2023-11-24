package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TiraRes {

	@JsonProperty("RequestId")
	private String requestId;

	@JsonProperty("QuoteNo")
	private String policyNo;

	@JsonProperty("TiraTrackingId")
	private BigDecimal tiraTrackingId;

	@JsonProperty("ResponseId")
	private String responseId;

	@JsonProperty("MethodName")
	private String methodName;

	@JsonProperty("HitCount")
	private Integer hitCount;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("AcknowledgementId")
	private String acknowledgementId;

	@JsonProperty("StatusCode")
	private String statusCode;

	@JsonProperty("StatusDesc")
	private String statusDesc;

	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;

	@JsonProperty("ChassisNo")
	private String chassisNo;

	@JsonProperty("RequestFilePath")
	private String requestFilePath;

	@JsonProperty("ResponseFilePath")
	private String responseFilePath;
    
		
}
