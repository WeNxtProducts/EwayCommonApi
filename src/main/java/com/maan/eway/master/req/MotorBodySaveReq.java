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
public class MotorBodySaveReq {

	@JsonProperty("BodyId")
	private String bodyId;
	@JsonProperty("SectionId")
	private String sectionId;
	@JsonProperty("BodyNameEn")
	private String bodyNameEn;
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	
	@JsonProperty("Remarks")
	private String remarks;
	@JsonProperty("SeatingCapacity")
	private Integer seatingCapacity	;
	@JsonProperty("Tonnage")
	private Integer tonnage	;
	@JsonProperty("Cylinders")
	private Integer cylinders	;
	@JsonProperty("Status")
	private String status;
	
}                                      
  
   