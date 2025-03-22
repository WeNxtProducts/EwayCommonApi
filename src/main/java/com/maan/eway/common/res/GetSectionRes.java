package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetSectionRes {

	@JsonProperty("OptedList")
	private List<DropdownResponse> optedSectionList;

	@JsonProperty("UnOptedList")
	private List<DropdownResponse> unOptedSectionList;

}
