package com.maan.eway.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PassengerSectionDetails {

	@JsonProperty("SectionId")
	private  String sectionId;	
	
	@JsonProperty("SectionName")
	private  String sectionName;	
	
	@JsonProperty("PassengerId")
	private  String passengerId;	
	
	@JsonProperty("PassengerName")
	private  String passengerName;	
	
	@JsonProperty("GroupDesc")
	private  String groupDesc;
	
	@JsonProperty("GroupId")
	private  String groupId;
	
	@JsonProperty("Covers")
	private  List<CoverRes> covers ;
}
