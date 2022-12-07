package com.maan.eway.master.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AcExecutiveProductDropdownRes {

	@JsonProperty("Code")
	private String code;
	
	@JsonProperty("CodeDesc")
	private String codeDesc;
	}
