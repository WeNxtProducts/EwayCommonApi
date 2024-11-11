package com.maan.eway.admin.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdditionalInfoReq {

	// Personal Details
	@JsonProperty("UserName")
    private String     userName     ;
	@JsonProperty("UserMobile")
    private String     userMobile   ;
	@JsonProperty("UserMail")
    private String     userMail     ;

	@JsonProperty("CompanyName")
    private String    companyName ;
	
	@JsonProperty("BrokerCompanyYn")
    private String    brokerCompanyYn ;
    
    @JsonProperty("Address1")
    private String    address1 ;
    
    @JsonProperty("Address2")
    private String    address2 ;
    
    @JsonProperty("Address3")
    private String    address3 ;
    
    @JsonProperty("CityCode")
    private String    cityCode ;
    
    @JsonProperty("StateCode")
    private String    stateCode ;
    
    @JsonProperty("CountryCode")
    private String    countryCode ;
    
    @JsonProperty("Pobox")
    private String    pobox ;
    
    @JsonProperty("Fax")
    private String    fax ;
   
	@JsonProperty("RegulatoryCode")
    private String    regulatoryCode ;
	
    @JsonProperty("Remarks")
    private String    remarks ;
    
    @JsonProperty("MissippiId")
    private String    missippiId ;
    
    @JsonProperty("ApprovedPreparedBy")
    private String    approvedPreparedBy ;
    
    @JsonProperty("CoreAppBrokerCode")
    private String    coreAppBrokerCode ;
    
    @JsonProperty("AcExecutiveId")
    private String    acExecutiveId ;
    
    @JsonProperty("CustConfirmYn")
    private String    custConfirmYn ;
    
    @JsonProperty("CommissionVatYn")
    private String    commissionVatYn ;
    
    @JsonProperty("VatRegNo")
    private String    vatRegNo ;
    
    @JsonProperty("CheckerYn")
    private String    checkerYn ;
    
    @JsonProperty("MakerYn")
    private String    makerYn ;
    
    @JsonProperty("ContactPersonName")
    private String    contactPersonName ;
    
    @JsonFormat(pattern ="dd/MM/yyyy")
    @JsonProperty("EffectiveDateStart")
    private Date effectiveDateStart ;
    
    @JsonProperty("MobileCode")
    private String    mobileCode ;
    
    @JsonProperty("Designation")
    private String    designation ;
    
    @JsonProperty("WhatsappCode")
    private String    whatsappCode ;
    
    @JsonProperty("WhatsappNo")
    private String    whatsappNo ;
    
    @JsonProperty("CityName")
    private String    cityName ;
    
	@JsonProperty("TaxExemptedYn")
    private String    taxExemptedYn ;
	
	@JsonProperty("CreditLimit")
    private String    creditLimit ;
	
	@JsonProperty("CustomerCode")
	private String     customerCode     ;

}
