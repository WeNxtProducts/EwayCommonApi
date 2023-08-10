package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DasboardCountRes {

	@JsonProperty("Position")
    private String     position     ;
	@JsonProperty("Status")
    private String     status     ;
	@JsonProperty("TotalCount")
    private String     count     ;
		
}
