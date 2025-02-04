package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeMasterGetReq {

	@JsonProperty("ExchangeId")
	private String exchangeId;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
}
