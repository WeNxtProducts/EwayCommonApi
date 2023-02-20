package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.SectionDetails;

import lombok.Data;

@Data
public class PaccGetRes {

	@JsonProperty("Suminsured")
    private String     suminsured ;
	
	@JsonProperty("OccupationType")
    private String    occupationType;

	@JsonProperty("OccupationTypeDesc")
    private String    occupationTypeDesc;

	@JsonProperty("CategoryId")
    private String    categoryId;
	
	@JsonProperty("RiskId")
    private String    riskId;
	@JsonProperty("DocumentsTitle")
    private String    documentsTitle;

	@JsonProperty("SectionDetails")
    private List<SectionDetails>    sectionDetails;
}
