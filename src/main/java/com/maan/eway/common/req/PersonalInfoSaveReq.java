/*
 * Java domain class for entity "PersonalInfo" 
 * Created on 2022-10-11 ( Date ISO 2022-10-11 - Time 15:28:59 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */
package com.maan.eway.common.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Domain class for entity "PersonalInfo"
 *
 * @author Telosys Tools Generator
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PersonalInfoSaveReq implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("PolicyHolderTypeid")
	private String policyHolderTypeid;

	@JsonProperty("IdType")
	private String idType;

	@JsonProperty("IdNumber")
	private String idNumber;

	@JsonProperty("Age")
	private String age;

	@JsonProperty("Gender")
	private String gender;

	@JsonProperty("Occupation")
	private String occupation;

	@JsonProperty("BusinessType")
	private String businessType;

	@JsonProperty("RegionCode")
	private String regionCode;

	@JsonProperty("IsTaxExempted")
	private String isTaxExempted;

	@JsonProperty("ClientName")
	private String clientName;

	@JsonProperty("Address1")
	private String address1;

	@JsonProperty("Address2")
	private String address2;

	@JsonProperty("Title")
	private String title;

	@JsonProperty("TitleDesc")
	private String titleDesc;

	@JsonProperty("Clientstatus")
	private String clientStatus;

	@JsonProperty("ClientStatusDesc")
	private String clientStatusDesc;

	@JsonProperty("PolicyHolderType")
	private String policyHolderType;

	@JsonProperty("IdTypeDesc")
	private String idTypeDesc;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("DobOrRegDate")
	private Date dobOrRegDate;

	@JsonProperty("Nationality")
	private String nationality;

	@JsonProperty("Placeofbirth")
	private String placeOfBirth;

	@JsonProperty("GenderDesc")
	private String genderDesc;

	@JsonProperty("OccupationDesc")
	private String occupationDesc;

	@JsonProperty("BusinessTypeDesc")
	private String businessTypeDesc;

	@JsonProperty("Vrngst")
	private String vrnGst;

	@JsonProperty("StateCode")
	private String stateCode;

	@JsonProperty("StateName")
	private String stateName;

	@JsonProperty("CityCode")
	private String cityCode;

	@JsonProperty("CityName")
	private String cityName;

	@JsonProperty("Street")
	private String street;

	@JsonProperty("Fax")
	private String fax;

	@JsonProperty("TelephoneNo1")
	private String telephoneNo1;
	@JsonProperty("TelephoneNo2")
	private String telephoneNo2;
	@JsonProperty("TelephoneNo3")
	private String telephoneNo3;
	@JsonProperty("MobileNo1")
	private String mobileNo1;
	@JsonProperty("MobileNo2")
	private String mobileNo2;
	@JsonProperty("MobileNo3")
	private String mobileNo3;
	@JsonProperty("Email1")
	private String email1;
	@JsonProperty("Email2")
	private String email2;
	@JsonProperty("Email3")
	private String email3;
	@JsonProperty("Language")
	private String language;
	@JsonProperty("LanguageDesc")
	private String languageDesc;

	@JsonProperty("TaxExemptedId")
	private String taxExemptedId;

	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("Status")
	private String status;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;

	@JsonProperty("UpdatedBy")
	private String updatedBy;

}
