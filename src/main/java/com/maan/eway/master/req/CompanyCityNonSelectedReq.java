package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyCityNonSelectedReq {

	@JsonProperty("CountryId")
    private String countryId;
    
	@JsonProperty("InsuranceId")
    private String companyId;
    
    @JsonProperty("Limit")
    private String limit;
    
    @JsonProperty("Offset")
    private String offset;

    @JsonProperty("RegionId")
    private String regionId;
    
    @JsonProperty("StateId")
    private String stateId;
    
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
    
}
