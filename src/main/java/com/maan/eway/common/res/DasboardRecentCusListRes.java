package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DasboardRecentCusListRes {

	@JsonProperty("TitleDesc")
    private String    titleDesc     ;
	@JsonProperty("ClientName")
    private String     clientName     ;
	@JsonProperty("CustomerReferenceNo")
    private String     customerReferenceNo     ;
	@JsonProperty("InsuranceId")
    private String     companyId     ;
	@JsonProperty("BranchCode")
	private String branchCode;
	@JsonProperty("GenderDesc")
	private String genderDesc;
	@JsonProperty("OccupationDesc")
    private String     occupationDesc     ;
	@JsonProperty("MobileNo1")
    private String     mobileNo1     ;
	@JsonProperty("Email1")
    private String     email1     ;
	@JsonProperty("EntryDate")
    private String     entryDate     ;
}
