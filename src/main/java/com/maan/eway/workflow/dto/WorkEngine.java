package com.maan.eway.workflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class WorkEngine {
		@JsonProperty("CompanyId")
		private String companyId;
		@JsonProperty("ProductId")
		private String productId;
		
		
}
