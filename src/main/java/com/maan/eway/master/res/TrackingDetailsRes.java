/*
 * Java domain class for entity "TrackingDetails" 
 * Created on 2021-10-09 ( Date ISO 2021-10-09 - Time 15:04:43 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */
package com.maan.eway.master.res;

import java.io.Serializable;

import lombok.*;
import java.math.BigDecimal;
import java.util.Date;


import java.math.BigDecimal;
import java.util.Date;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * Domain class for entity "TrackingDetails"
 *
 * @author Telosys Tools Generator
 *
 */
 
 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrackingDetailsRes {


    
	@JsonProperty("Trackingid")
    private String     trackingId   ;  
	@JsonProperty("RequestReferenceNo")
    private String     requestReferenceNo      ;
	@JsonProperty("QuoteNo")
    private String     quoteNo   ;
	@JsonProperty("PolicyNo")
    private String     policyNo     ;
	@JsonProperty("Status")
    private String     status       ;
	@JsonProperty("Statusdescription")
    private String     statusDesc ;
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("Entrydate")
    private Date       entryDate    ;
	@JsonProperty("CreatedBy")
    private String     createdby    ;
	@JsonProperty("Remarks")
    private String     remarks      ;
	@JsonProperty("InsuranceId")
    private String companyId ;
	@JsonProperty("BranchCode")
    private String branchCode   ;
	@JsonProperty("ProductId")
    private String productId   ;


	  
	  
}
