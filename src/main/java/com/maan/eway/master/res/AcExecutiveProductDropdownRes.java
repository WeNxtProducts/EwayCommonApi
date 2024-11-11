package com.maan.eway.master.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AcExecutiveProductDropdownRes {

	@JsonProperty("AcExecutiveId")
	private String acExecutiveId;
	
	@JsonProperty("AcExecutiveName")
	private String acExecutiveName;
	
	@JsonProperty("BankCode")
	private String bankCode;
	
	@JsonProperty("BankName")
	private String bankName;
	
	
	}
