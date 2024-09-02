package com.maan.eway.auth.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductDropDownRes {

	@JsonProperty("ProductId")
	private String productId ;
	
	@JsonProperty("OldProductName")
	private String oldProductName ;
	
	@JsonProperty("ProductName")
	private String newProductName;
	

	@JsonProperty("ProductIconId")
	private String productIconId ;
	
	@JsonProperty("ProductIconName")
	private String productIconName;
	
	@JsonProperty("PackageYn")
	private String packageYn;
	
	@JsonProperty("DisplayOrder")
	private Integer displayOrder;
	
	@JsonProperty("ProductNameLocal")
	private String newProductNameLocal;
	
}
