package com.maan.eway.crm.bean;


import lombok.Data;
@Data
public class CustomerDetail {

	private String loginId;

	public CustomerDetail() {
	}

	public CustomerDetail(String loginId) {
		this.loginId = loginId;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
}
