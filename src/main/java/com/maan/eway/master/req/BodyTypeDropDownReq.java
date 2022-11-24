package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BodyTypeDropDownReq {

	@JsonProperty("SectionId")
	private String sectionId ;
}
