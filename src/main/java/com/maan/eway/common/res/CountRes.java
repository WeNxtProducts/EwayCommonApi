package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CountRes {
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("ProductName")
	private String productName;

	@JsonProperty("QuoteTotalCount")
	private Long quotetotalCount;

	@JsonProperty("PolicyTotalCount")
	private Long policytotalCount;

	@JsonProperty("EndtTotalCount")
	private Long endttotalCount;

		
}
