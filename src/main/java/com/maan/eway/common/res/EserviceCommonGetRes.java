package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.SectionDetails;

import lombok.Data;

@Data
public class EserviceCommonGetRes {

	 @JsonProperty("RiskId")
	private  String riskId;	

	@JsonProperty("SalaryPerAnnum")
    private String     salaryPerAnnum ;
	
	@JsonProperty("BenefitCoverMonth")
    private String     benefitCoverMonth;
	
	@JsonProperty("SumInsured")
    private String     sumInsured ;
		
	@JsonProperty("OccupationType")
    private String    occupationType;

	@JsonProperty("OccupationTypeDesc")
    private String    occupationTypeDesc;

	@JsonProperty("CategoryId")
    private String    categoryId;
	
	@JsonProperty("CustomerName")
    private String    customerName;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("Dob")
    private Date    dob;

	@JsonProperty("JobJoiningMonth")
    private String    jobJoiningMonth;
	
	@JsonProperty("BetweenDiscontinued")
    private String    betweenDiscontinued;
	
	@JsonProperty("EthicalWorkInvolved")
    private String    ethicalWorkInvolved;
	
	    
	@JsonProperty("SectionDetails")
	private List<SectionDetails>    sectionDetails;

	
}
