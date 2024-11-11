/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2023-01-03 ( Date ISO 2023-01-03 - Time 15:28:40 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2023-01-03 ( 15:28:40 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.bean;


import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import java.util.Date;
import jakarta.persistence.*;




/**
* Domain class for entity "OmPaymentDetail"
*
* @author Telosys Tools Generator
*
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@IdClass(PaymentDetailId.class)
@Table(name="payment_detail")


public class PaymentDetail implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="QUOTE_NO", nullable=false, length=50)
    private String     quoteNo ;

    @Id
    @Column(name="PAYMENT_ID", nullable=false)
    private String     paymentId ;

    @Id
    @Column(name="MERCHANT_REFERENCE", length=100)
    private String     merchantReference ;

    //--- ENTITY DATA FIELDS 
    @Column(name="PAYMENT_TYPE")
    private String  paymentType ;

    @Column(name="PAYMENT_TYPEDESC")
    private String  paymentTypedesc ;

    @Column(name="PAYMENT_STATUS", length=50)
    private String     paymentStatus;

    
    @Column(name="PREMIUM")
    private BigDecimal     premium ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="REQUEST_TIME")
    private Date       requestTime ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="RESPONSE_TIME")
    private Date       responseTime ;

    @Column(name="RESPONSE_MESSAGE", length=500)
    private String     responseMessage ;

    @Column(name="RESPONSE_STATUS", length=500)
    private String     responseStatus ;

    @Column(name="VALIDITY_DATE")
    private Date       validityDate ;

    @Column(name="SHORTERN_URL", length=1000)
    private String     shorternUrl ;
  
    @Column(name="CUSTOMER_EMAIL", length=500)
    private String     customerEmail ;

    @Column(name="CUSTOMER_NAME", length=500)
    private String     customerName ;

    @Column(name="REQ_CARD_NUMBER", length=500)
    private String     reqCardNumber ;

    @Column(name="REQ_SIGNATURE", length=500)
    private String     reqSignature ;

    @Column(name="AUTH_TRANS_REF_NO", length=500)
    private String     authTransRefNo ;

    @Column(name="REQ_BILL_TO_SURNAME", length=500)
    private String     reqBillToSurname ;

    @Column(name="REQ_BILL_TO_ADDRESS_CITY", length=500)
    private String     reqBillToAddressCity ;

    @Column(name="REQ_CARD_EXPIRY_DATE", length=500)
    private String     reqCardExpiryDate ;

    @Column(name="REQ_BILL_TO_ADDR_POSTAL_CODE", length=500)
    private String     reqBillToAddrPostalCode ;

    @Column(name="REQ_BILL_TO_PHONE", length=500)
    private String     reqBillToPhone ;

    @Column(name="AUTH_AMOUNT", length=500)
    private String     authAmount ;

    @Column(name="AUTH_RESPONSE", length=500)
    private String     authResponse ;

    @Column(name="REQ_BILL_TO_FORENAME", length=500)
    private String     reqBillToForename ;

    @Column(name="REQUEST_TOKEN", length=500)
    private String     requestToken ;

    @Column(name="AUTH_TIME", length=500)
    private String     authTime ;

    @Column(name="REQ_BILL_TO_EMAIL", length=500)
    private String     reqBillToEmail ;

    @Column(name="REQ_BILL_TO_COMPANY_NAME", length=500)
    private String     reqBillToCompanyName ;

    @Column(name="REQ_TRANSACTION_TYPE", length=500)
    private String     reqTransactionType ;

    @Column(name="REQ_REFERENCE_NUMBER", length=500)
    private String     reqReferenceNumber ;

    @Column(name="REQ_BILL_TO_ADDRESS_STATE", length=500)
    private String     reqBillToAddressState ;

    @Column(name="REQ_BILL_TO_ADDRESS_LINE2", length=500)
    private String     reqBillToAddressLine2 ;

    @Column(name="REQ_BILL_TO_ADDRESS_LINE1", length=500)
    private String     reqBillToAddressLine1 ;

   
    @Column(name="REQUEST_REFERENCE_NO", length=100)
    private String    requestReferenceNo;
    
    @Column(name="CUSTOMER_ID", length=500)
    private String    customerId;
        
  
    @Column(name="BANK_CODE", length=100)
    private String     bankCode ;
    
    @Column(name="BANK_NAME", length=100)
    private String     bankName ;

    @Column(name="BRANCH_CODE", length=50)
    private String     branchCode;

    @Column(name="BRANCH_NAME", length=100)
    private String     branchName ;

    @Column(name="RES_SIGNATURE", length=100)
    private String     resSignature ;

    @Column(name="HIT_COUNT")
    private Double     hitCount ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;

    @Column(name="CREATED_BY", length=100)
    private String     createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATED_DATE")
    private Date       updatedDate ;

    @Column(name="UPDATED_BY", length=100)
    private String     updatedBy;

    @Column(name="EMI_YN", length=100)
    private String     emiYn ;
    
    @Column(name="INSTALLMENT_MONTH", length=20)
    private String     installmentMonth;
    
    @Column(name="INSTALLMENT_PERIOD", length=20)
    private String     installmentPeriod;

    @Column(name="EXCHANGE_RATE")
    private BigDecimal     exchangeRate;
    
    @Column(name="PREMIUM_FC")
    private BigDecimal     premiumFc;
    
    @Column(name="PREMIUM_LC")
    private BigDecimal     premiumLc;
    
    @Column(name="CURRENCY_ID", length=100)
    private String     currencyId;
    
    @Column(name="CHEQUE_NO", length=20)
    private String     chequeNo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CHEQUE_DATE")
    private Date       chequeDate ;
    
    @Column(name="PAYMENTS")
    private String       payments ;
    
    @Column(name="ACCOUNT_NUMBER")
    private String       accountNumber ;
    
    @Column(name="IBAN_NUMBER")
    private String       ibanNumber ;
    
    
    @Column(name="MICRNO")
    private String micrNo;
    @Column(name="PAYEENAME")
    private String payeeName;
    
    
    @Column(name="REQ_BILL_TO_COUNTRY")
    private String reqBillToCountry;
    @Column(name="COMPANY_ID")
    private String companyId;

    @Column(name="CHANNEL")
    private String channel;
    
    @Column(name="REFERENCE")
    private String reference;
    
    @Column(name="MSISDN")
    private String msisdn;

    @Column(name="CBC_NO", length=100)
    private String     cbcNo;
    
    @Column(name="WHATSAPP_CODE")
    private String whatsappCode;
    @Column(name="WHATSAPP_NO")
    private String whatsappNo;
    
}



