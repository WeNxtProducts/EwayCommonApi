package com.maan.eway.master.res;

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
public class MotorBodyTypeGetRes {

	@JsonProperty("BodyId")
	private Integer bodyId;
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("BodyNameEn")
	private String bodyNameEn;
	
	@JsonFormat(pattern="dd/MM/YYYY")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern="dd/MM/YYYY")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

	
	@JsonProperty("AmendId")
	private Integer amendId;
	
	@JsonProperty("Status")
	private String status;

	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonFormat(pattern="dd/MM/YYYY")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("SeatingCapacity")
	private Integer seatingCapacity;

	@JsonProperty("Tonnage")
	private Integer tonnage;
		  

	@JsonProperty("Cylinders")
	private Integer cylinders;
		    

}
