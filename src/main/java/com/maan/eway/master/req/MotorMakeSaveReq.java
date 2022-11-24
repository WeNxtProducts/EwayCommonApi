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
@NoArgsConstructor
@AllArgsConstructor
public class MotorMakeSaveReq {

	@JsonProperty("MakeId")
	private String makeId;
	
	@JsonProperty("MakeNameEn")
	private String makeNameEn;
		
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("ColorDesc")
	private String colorDesc;
		
	@JsonProperty("Status")
	private String status;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;


	
}
