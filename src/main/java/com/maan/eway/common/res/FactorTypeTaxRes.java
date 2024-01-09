package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FactorTypeTaxRes {
	
	@JsonProperty("TaxId")
	private Integer taxId;
	
	@JsonProperty("TaxRate")
	private Long taxRate;
	
	@JsonProperty("TaxAmount")
	private Long taxAmount;
	
	@JsonProperty("taxDesc")
	private String taxDesc;
	
	@JsonProperty("TaxCalcType")
	private String taxCalcType;
	
	@JsonProperty("IsTax")
	private String isTax;
	
	@JsonProperty("TaxExemptType")
	private String taxExemptType;
	
	@JsonProperty("TaxExemptCode")
	private String taxExemptCode;

	@JsonProperty("TaxAmountLc")
	private Long taxAmountLc;
	
	@JsonFormat(pattern = "dd/mm/yyyy")
	@JsonProperty("PolicyEndDate")
	private Date policyEndDate;	
	
	@JsonFormat(pattern = "dd/mm/yyyy")
	@JsonProperty("EffectiveDate")
	private Date effectiveDate;	

}
