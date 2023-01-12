package com.maan.eway.notification.req;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Broker{
	@JsonProperty("Brokername")
    private String     brokerName   ;
	@JsonProperty("Brokercompanyname")
    private String     brokerCompanyName ;
	@JsonProperty("Brokermailid")
    private String     brokerMailId ;
	@JsonProperty("Brokerphoneno")
    private BigDecimal brokerPhoneNo ;
	@JsonProperty("Brokerphonecode")
    private Integer    brokerPhoneCode ;
	@JsonProperty("Brokermessengercode")
    private Integer    brokerMessengerCode ;
	@JsonProperty("Brokermessengerphone")
    private BigDecimal brokerMessengerPhone ;
}