package com.maan.eway.req;

import java.math.BigDecimal;
import java.io.Serializable;
//import org.apache.poi.hpsf.Decimal;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
@Validated
public class CoInsuranceInfoReq implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("Sno")
	private int sno ;
	
	@JsonProperty("Insurancecompanyid")
	private int insurancecompanyid ;
	
	@JsonProperty("Insurancecompanyname")
	private String insurancecompanyname ;

	@JsonProperty("Sharedpercentage")
	private BigDecimal  sharedpercentage ;

	
	@JsonProperty("Leaderparticipant")
	private String leaderparticipant ;
	

 }
