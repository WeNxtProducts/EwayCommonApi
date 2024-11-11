package com.maan.eway.jasper.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JasperDocumentReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("EndorsementType")
	private String endorsementType;
	
	@JsonProperty("BrokerQuoteYn")
	private String brokerQuoteYn;
	
	@JsonProperty("StrickerYn")
	private String strickerYn;
	
	@JsonProperty("VehicleId")
	private String vehicleId;
	
}
