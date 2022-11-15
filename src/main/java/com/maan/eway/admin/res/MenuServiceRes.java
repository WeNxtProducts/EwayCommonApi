package com.maan.eway.admin.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.auth.dto.Menu;

import lombok.Data;

@Data
public class MenuServiceRes {


	@JsonProperty("UserList")
	private List<Menu> userList;
	
	@JsonProperty("AdminList")
	private List<Menu> adminlist;
	
}
