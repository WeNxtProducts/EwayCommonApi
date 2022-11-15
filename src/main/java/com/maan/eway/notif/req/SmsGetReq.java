package com.maan.eway.notif.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SmsGetReq {

	@JsonProperty("SNo")
	private String sNo;
	
	
	@JsonProperty("InsuranceId")
	private String companyId;
	

	@JsonProperty("MobileNo")
	private String mobileNo;
	
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
}
