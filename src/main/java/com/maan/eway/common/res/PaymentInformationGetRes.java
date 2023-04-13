package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data

public class PaymentInformationGetRes {
	
	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("ProductId")
	private Integer productId;
	
	@JsonProperty("PaymentId")
	private String paymentId;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("MerchantReference")
	private String merchantReference;
	
	@JsonProperty("PaymentStatus")
	private String paymentStatus;
	
	
	
	
	
	

    

}
