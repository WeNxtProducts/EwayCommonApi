package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FactorRateRequestRes {
	
	
	@JsonProperty("DiscLoadId")
	private Integer discLoadId;
	
	@JsonProperty("DiscountCoverId")
	private Integer discountCoverId;
	
	@JsonProperty("MaxLoadingAmount")
	private Long maxLoadingAmount;

	@JsonProperty("DisCalcType")
	private String discalcType;
	
	@JsonProperty("FactorTypeId")
	private Long factorTypeId;
	
	@JsonProperty("CoverType")
	private String coverType;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;

	@JsonFormat(pattern = "dd/mm/yyyy")
	@JsonProperty("EffectiveDate")
	private Date effectiveDate;	
	
	@JsonProperty("SubCoverId")
	private Integer subCoverId;

	@JsonFormat(pattern = "dd/mm/yyyy")
	@JsonProperty("PolicyEndDate")
	private Date policyEndDate;	
	

}
