package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class GetMenuTypeReq {
	

	@JsonProperty("UserType")
	private String Usertype;
	
}
