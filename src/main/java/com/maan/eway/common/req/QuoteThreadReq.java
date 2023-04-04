package com.maan.eway.common.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.bean.EserviceMotorDetails;

import lombok.Data;

@Data
public class QuoteThreadReq {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo ;
	
	@JsonProperty("QuoteNo")
	private String    quoteNo ;

	@JsonProperty("EndtPrevQuoteNo")
	private String    endtPrevQuoteNo ;
	
	 @JsonProperty("CustomerId")
	 private String    customerId ;
	 
	 @JsonProperty("ProductId")
	 private String    productId ;
	 
	 @JsonProperty("SectionId")
	 private String    sectionId ;

		
	@JsonProperty("VehicleId")
	private Integer vehicleId ;

	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("AdminLoginId")
	private String adminLoginId;
	
	@JsonProperty("GroupId")
	private Integer groupId;

	@JsonProperty("GroupCount")
	private Integer groupCount;


	@JsonProperty("PolicyStartDate")
	private Date policyStartDate;
	
	@JsonProperty("PolicyEndDate")
	private Date policyEndDate;
	
	@JsonProperty("EffectiveDate")
	private Date effetiveDate;
	
	@JsonProperty("NoOfDays")
	private String noOfDays;
	
	@JsonProperty("Vehicles")
	private List<VehicleIdsReq> VehicleIdsList;

}
