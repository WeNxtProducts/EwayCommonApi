package com.maan.eway.common.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PortFolioDashBoardRes {

	   @JsonProperty("ProductId")
	   private String     productId;
	   
	   @JsonProperty("ProductName")
	   private String     productName;
	   
	   @JsonProperty("BrokerList")
	   private List<PortfolioBrokerListRes>     brokerList;
	   
	   @JsonProperty("BrokerCount")
	   private Long brokerCount;
	   
	
}
