package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class EserviceCustomerSearchVrtinReq {
	
	@JsonProperty("VrTinNo")
	private String vrTinNo;
	
}
