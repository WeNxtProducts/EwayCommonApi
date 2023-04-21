package com.maan.eway.common.res;

import java.math.BigDecimal;
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

public class SearchPaymentInfoRes {
	
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

	@JsonProperty("EmiYn")
	private String emiYn;
	@JsonProperty("InstallmentMonth")
	private String installmentMonth;

	@JsonProperty("InstallmentPeriod")
	private String installmentPeriod;

	@JsonProperty("Premium")
	private String premium;
	
    @JsonProperty("PaymentTypedesc")
    private String  paymentTypedesc ;
    
    @JsonProperty("BankName")
    private String     bankName ;
    
    @JsonProperty("ChequeNo")
    private String     chequeNo;

    @JsonFormat(pattern="dd/MM/yyyy")
    @JsonProperty("ChequeDate")
    private Date       chequeDate ;

   

}
