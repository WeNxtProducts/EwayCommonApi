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
public class MotorColorSaveReq {

	@JsonProperty("ColorId")
	private String colorId;
	@JsonProperty("ColorCode")
	private String colorCode;
	@JsonProperty("ColorDesc")
	private String colorDesc;
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	@JsonProperty("Remarks")
	private String remarks;
	@JsonProperty("Status")
	private String status;
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	
}                                      
  
   