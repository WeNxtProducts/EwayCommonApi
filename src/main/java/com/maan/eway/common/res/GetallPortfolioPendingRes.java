package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetallPortfolioPendingRes {
	   @JsonProperty("Count")
	   private Long     Count ;
	   @JsonProperty("PortfolioPendingList")
	   private List<PortfolioCustomerDetailsRes>     pendingList ;
}
