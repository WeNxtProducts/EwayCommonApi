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
	
	
	@JsonProperty("ProductId")
	private Integer productId;
	
	@JsonProperty("PaymentId")
	private String paymentId;
	
    @JsonProperty("PaymentTypedesc")
    private String  paymentTypedesc ;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;

	
	@JsonProperty("PaymentStatus")
	private String paymentStatus;

	@JsonProperty("EmiYn")
	private String emiYn;
	
	@JsonProperty("InstallmentMonth")
	private String installmentMonth;

	@JsonProperty("InstallmentPeriod")
	private String installmentPeriod;



    
    @JsonProperty("BankName")
    private String     bankName ;
    
    @JsonProperty("ChequeNo")
    private String     chequeNo;

    @JsonFormat(pattern="dd/MM/yyyy")
    @JsonProperty("ChequeDate")
    private Date       chequeDate ;
//
//	@JsonProperty("PaymentReferenceNo")
//    private String     paymentReferenceNo ;
	@JsonProperty("ExchangeRate")
    private Double     exchangeRate      ;
	
	@JsonProperty("PremiumFc")
    private Double     premiumFc      ;
	
	@JsonProperty("PremiumLc")
    private Double     premiumLc      ;
	
	@JsonProperty("CurrencyId") 
    private String  currencyId   ;
	
	@JsonProperty("Premium")
    private Double     premium      ;
	@JsonProperty("RequestTime")
    private Date       requestTime  ;
	@JsonProperty("ResponseTime")
    private Date       responseTime ;
	@JsonProperty("ResponseMessage")
    private String     responseMessage ;
	@JsonProperty("ResponseStatus")
    private String     responseStatus ;
	@JsonProperty("MerchantReference")
    private String     merchantReference ;
	@JsonProperty("CustomerEmail")
    private String     customerEmail ;
	@JsonProperty("CustomerName")
    private String     customerName ;
	@JsonProperty("ReqCardNumber")
    private String     reqCardNumber ;
	@JsonProperty("ReqSignature")
    private String     reqSignature ;
	@JsonProperty("AuthTransRefNo")
    private String     authTransRefNo ;
	@JsonProperty("ReqBillToSurname")
    private String     reqBillToSurname ;
	@JsonProperty("ReqBillToAddressCity")
    private String     reqBillToAddressCity ;
	@JsonProperty("ReqCardExpiryDate")
    private String     reqCardExpiryDate ;
	@JsonProperty("ReqBillToAddrPostalCode")
    private String     reqBillToAddrPostalCode ;
	@JsonProperty("ReqBillToPhone")
    private String     reqBillToPhone ;
	@JsonProperty("ReasonCode")
    private String     reasonCode   ;
	@JsonProperty("AuthAmount")
    private String     authAmount   ;
	@JsonProperty("AuthResponse")
    private String     authResponse ;
	@JsonProperty("BillTransRefno")
    private String     billTransRefNo ;
	@JsonProperty("ReqBillToForename")
    private String     reqBillToForename ;
	@JsonProperty("RequestToken")
    private String     requestToken ;
	@JsonProperty("AuthTime")
    private String     authTime     ;
	@JsonProperty("ReqBilltoEmail")
    private String     reqBillToEmail ;
	@JsonProperty("ReqBillToCompanyname")
    private String     reqBillToCompanyName ;
	@JsonProperty("ReqTransactionType")
    private String     reqTransactionType ;
	@JsonProperty("ReqReferenceNumber")
    private String     reqReferenceNumber ;
	@JsonProperty("ReqBillToAddressState")
    private String     reqBillToAddressState ;
	@JsonProperty("ReqBillToAddressLine2")
    private String     reqBillToAddressLine2 ;
	@JsonProperty("ReqBillToAddressLine1")
    private String     reqBillToAddressLine1 ;
	@JsonProperty("Result")
    private String     Result    ;
	@JsonProperty("Transid")
    private String    Transid   ;
	@JsonProperty("Amount")
    private String    amount   ;
	@JsonProperty("Trackid")
    private String    Trackid   ;
	@JsonProperty("Referenceid")
    private String    referenceid   ;
	@JsonProperty("Customerid")
    private String   customerid   ;
	
	@JsonProperty("OthProductId")
    private String     othProductId ;
	@JsonProperty("PosCardType")
    private String     posCardType  ;
	@JsonProperty("PosApprovedId")
    private String     posApprovedId ;
	@JsonProperty("ReqPaymentType")
    private String     reqPaymentType ;
	@JsonProperty("PosRefNumber")
    private String     posRefNumber ;
	@JsonProperty("ReasonCodeDesc")
    private String     reasonCodeDesc ;
	@JsonProperty("CurrentStageCode")
    private String     currentStageCode ;
	@JsonProperty("CurrentStatusCode")
    private String     currentStatusCode ;
	
	@JsonProperty("BranchName")
    private String     branchName   ;
	@JsonProperty("DateOfCollection")
    private Date       dateOfCollection ;
	@JsonProperty("TransactionId")
    private String     transactionId ;
	@JsonProperty("TransactionDate")
    private Date       transactionDate ;
	@JsonProperty("ResSignature")
    private String     resSignature ;
	@JsonProperty("HitCount")
    private Double     hitCount     ;
	@JsonProperty("BranchCode")
    private String     branchCode;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
    private Date   entryDate   ;
	  
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
    private Date   updatedDate   ;

	
	@JsonProperty("CreatedBy")
    private String     createdBy;

	@JsonProperty("UpdatedBy")
    private String     updatedBy;

   

}
