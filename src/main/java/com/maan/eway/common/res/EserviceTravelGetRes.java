package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EserviceTravelGetRes {

		@JsonProperty("CustomerReferenceNo")
	    private String   customerReferenceNo ;
	    
	    @JsonProperty("RequestReferenceNo")
	    private String   requestReferenceNo ;
	  
	    @JsonProperty("TravelId")
	    private String    travelId     ;
		@JsonProperty("InsuranceId")
	    private String     companyId    ;
		@JsonProperty("BranchCode")
	    private String     branchCode   ;
		@JsonProperty("ProductId")
	    private String     productId    ;
		@JsonProperty("ProductName")
	    private String     productName    ;
		
		@JsonProperty("SectionId")
	    private String     sectionId    ;
		@JsonProperty("SectionName")
	    private String     sectionName    ;
		@JsonProperty("CompanyName")
	    private String    companyName    ;

		@JsonProperty("TravelCoverId")
	    private String    travelCoverId ;
		@JsonProperty("SourceCountry")
	    private String     sourceCountry ;
		@JsonProperty("DestinationCountry")
	    private String     destinationCountry ;
		@JsonProperty("SportsCoverYn")
	    private String     sportsCoverYn ;
		@JsonProperty("TerrorismCoverYn")
	    private String     terrorismCoverYn ;
		@JsonProperty("PlanTypeId")
	    private String    planTypeId   ;
		
		@JsonFormat(pattern="dd/MM/yyyy")
		@JsonProperty("TravelStartDate")
	    private Date       travelStartDate ;
		
		@JsonFormat(pattern="dd/MM/yyyy")
		@JsonProperty("TravelEndDate")
	    private Date       travelEndDate ;
		
		@JsonProperty("TravelCoverDuration")
	    private String    travelCoverDuration ;
		@JsonProperty("TotalPassengers")
	    private String    totalPassengers ;
		
		@JsonProperty("Age")
	    private String    age          ;
		
		@JsonProperty("CreatedBy")
	    private String     createdBy    ;
		@JsonProperty("Status")
	    private String     status       ;
		
		@JsonProperty("Remarks")
	    private String     remarks      ;
		
		@JsonProperty("HavePromoCode")
	    private String     havepromocode ;
		
		@JsonProperty("PromoCode")
	    private String     promocode    ;
		@JsonProperty("CovidCoverYn")
	    private String     covidCoverYn ;
		
		@JsonProperty("AdminLoginId")
	    private String     adminLoginId ;
		@JsonProperty("AdminRemarks")
	    private String     adminRemarks ;
		@JsonProperty("RejectReason")
	    private String     rejectReason ;
		
		@JsonProperty("BrokerCode")
		private String brokerCode;
		
		@JsonProperty("LoginId")
		private String loginId;
		
		@JsonProperty("AcExecutiveId")
		private String acExecutiveId;
		
		@JsonProperty("UserType")
		private String userType;
		
		@JsonProperty("SubUserType")
		private String subUserType;
		
		@JsonProperty("Currency")
		private String currency;
		
		@JsonProperty("ExchangeRate")
		private String exchangeRate;
		
		@JsonProperty("ApplicationId")
		private String applicationId;
		
		@JsonProperty("BdmCode")
		private String bdmCode;
		
		@JsonProperty("SourceType")
		private String sourceType;
		
		@JsonProperty("CustomerCode")
		private String customerCode;
		
		@JsonFormat(pattern="dd/MM/yyyy")
		@JsonProperty("EntryDate")
	    private Date       entryDate ;
		
		@JsonFormat(pattern="dd/MM/yyyy")
		@JsonProperty("UpdatedDate")
	    private Date       updatedDate ;
		
		
		@JsonProperty("UpdatedBy")
	    private String updatedBy;
		
		@JsonProperty("QuoteNo")
	    private String quoteNo;
		
		@JsonProperty("CustomerId")
	    private String customerId;
		
		@JsonProperty("CommissionType")
		private String commissionType;
		
		@JsonProperty("SourceCountryDesc")
		private String sourceCountryDesc;
		
		@JsonProperty("DestinationCountryDesc")
		private String desctinationCountryDesc;
		
	
		

		
}
