package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ColummnDropRes {

	@JsonProperty("DisplayName")
	private String dispalyName;
	@JsonProperty("ColumnName")
	private String columnName;
	@JsonProperty("FieldName")
	private String fieldName;
	
}
