package com.maan.eway.renewal.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public  class GetAllBranchBySource {
	@JsonProperty("DivisionCode")
	private String divisionCode;
	@JsonProperty("DivisionName")
	private String divisionName;
	@JsonProperty("NoOfProducts")
	private String noOfProducts;
	@JsonProperty("ProductList")
	private List<GetProductBySource> prodList;
}
