package com.maan.eway.admin.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserDetailsGetReq {

	@JsonProperty("LoginId")
	private String loginId ;
}
