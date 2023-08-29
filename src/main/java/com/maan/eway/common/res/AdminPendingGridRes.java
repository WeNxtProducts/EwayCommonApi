package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminPendingGridRes {

	
	@JsonProperty("Count")
	private Long   count ;
	

    @JsonProperty("PendingGrid")
    private List<AdminPendingGridListRes>   adminPendingGridListRes ;
    
   
}
