package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class QuoteInformationDTO {
	@JsonProperty("EnquiryId") 
    private String enquiryId;
	
	@JsonProperty("QuoteNo") 
    private String quoteNo;
	
	@JsonProperty("QuotationDescription") 
    private String quotationDescription;
	
	@JsonProperty("SumInsured") 
    private Double sumInsured;
	
	@JsonProperty("PremiumRate") 
    private Double premiumRate;
	
	@JsonProperty("PremiumAmount") 
    private Double premiumAmount;
	
	@JsonProperty("TechnicalDiscount") 
    private Double technicalDiscount;
	
	@JsonProperty("AdditionalDiscount") 
    private Double additionalDiscount;
	
	@JsonProperty("QuoteStatus") 
    private String quoteStatus;
	
	@JsonProperty("QuoteRemarks") 
    private String quoteRemarks;
}
