package com.maan.eway.master.dropdown.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class StateMasterChangeStatusReq {
	 
	 @JsonProperty("StateId")
	 private String stateId;
	 
	 @JsonProperty("CountryId")
	 private String countryId;
	 
	 @JsonProperty("Status")
	 private String status;
	 
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
}
