package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data

public class PaymentMasterDropDownRes {

	
	
	@JsonProperty("Code")
	private String code;

	@JsonProperty("CodeDesc")
	private String codeDesc;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
	
	@JsonProperty("Type")
	private String type;
		
}
