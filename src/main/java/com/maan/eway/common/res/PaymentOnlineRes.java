package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data

public class PaymentOnlineRes {
	
	@JsonProperty("RequestTime")
    private Date       requestTime  ;
	@JsonProperty("ResponseTime")
    private Date       responseTime ;
	@JsonProperty("ResponseMessage")
    private String     responseMessage ;
	@JsonProperty("ResponseStatus")
    private String     responseStatus ;
	@JsonProperty("AuthTransRefNo")
    private String     authTransRefNo ;
	@JsonProperty("AuthAmount")
    private String     authAmount   ;
	@JsonProperty("AuthResponse")
    private String     authResponse ;
	@JsonProperty("Channel")
    private String     channel ;
	@JsonProperty("Reference")
    private String     reference ;
	@JsonProperty("Msisdn")
    private String     msisdn ;
   

}
