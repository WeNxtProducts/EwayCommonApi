package com.maan.eway.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SectionDetails {

	
	@JsonProperty("SectionId")
	private  String sectionId;	
	
	@JsonProperty("SectionName")
	private  String sectionName;	
	
	@JsonProperty("Covers")
	private  List<CoverRes> covers ;
}
