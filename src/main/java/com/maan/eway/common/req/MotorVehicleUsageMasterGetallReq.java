package com.maan.eway.common.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MotorVehicleUsageMasterGetallReq {
	
	@JsonProperty("Limit")
    private String limit  ;
	@JsonProperty("Offset")
    private String  offset;
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	@JsonProperty("SectionId")
    private String sectionId  ;
}
