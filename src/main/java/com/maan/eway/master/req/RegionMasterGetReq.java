package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RegionMasterGetReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("RegionCode")
    private String     regionCode     ;
    
	@JsonProperty("CountryId")
    private String countryId    ;
    
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
}
