package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.common.req.TermsAndConditionListReq;
import com.maan.eway.error.Error;
import lombok.Data;

@Data
public class TermsAndConditionListRes {



	@JsonProperty("SubId")
	private String subId;
	
	@JsonProperty("SubIdDesc")
	private String subIdDesc;
		
	@JsonProperty("DocRefNo")
	private String docRefNo;

}
