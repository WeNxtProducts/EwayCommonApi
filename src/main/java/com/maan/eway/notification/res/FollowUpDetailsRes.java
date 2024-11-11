package com.maan.eway.notification.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class FollowUpDetailsRes {


	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("BranchCode")
	private String branchCode;

	@JsonProperty("ProductId")
	private String productId;

	
	@JsonProperty("FollowupId")
	private Integer followupId;

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("LoginId")
	private String loginId;

	@JsonProperty("FollowupDesc")
	private String followupDesc;

    
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("StartDate")
    private String startDate;
	
	@JsonProperty("StartTime")
    private String startTime;
    
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EndDate")
    private String endDate;
	
	@JsonProperty("EndTime")
    private String endTime;
    
	
	@JsonProperty("Remarks")
    private String remarks;
    
    
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
    private String entryDate;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
    private String updatedDate;
	
	@JsonProperty("Status")
    private String status;
    
	@JsonProperty("StatusDesc")
    private String statusDesc;
    
}
