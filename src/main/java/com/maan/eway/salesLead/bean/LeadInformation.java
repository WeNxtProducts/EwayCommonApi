package com.maan.eway.salesLead.bean;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity
@DynamicUpdate
@Data
@Table(name = "lead_information")
public class LeadInformation  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "LEAD_ID", length = 20, nullable = false)
	private String leadId;
	
	@Column(name = "CLIENT_NAME")
	private String clientName;
	
	@Column(name = "CLIENT_CODE")
	private String clientCode;
	
	@Column(name = "ADDRESS1")
	private String address1;
	
	@Column(name = "ADDRESS2")
	private String address2;
	
	@Column(name = "STATE")
	private String state;
	
	@Column(name = "CITY")
	private String city;
	
	@Column(name = "PINCODE")
	private String pincode;
	
	@Column(name = "PHONE")
	private String phone;
	
	@Column(name = "GST_IDENTIFICATION_NO")
	private String gstIdentificationNo;
	
	@Column(name = "BRANCH_CODE")
	private String branchCode;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ENTRY_DATE")
	private Date entryDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LEAD_CREATED_DATE")
	private Date leadCreatedDate;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "UPDATED_BY")
	private String updatedBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED_DATE")
	private Date updatedDate;
	
	@Column(name = "INTERMEDIATE_ID")
	private String intermediateId;
	
	@Column(name = "INTERMEDIATE_NAME")
	private String intermediateName;
	
	@Column(name = "CHANNEL_ID")
	private String channelId;
	
	@Column(name = "CHANNEL_DESC")
	private String channelDesc;
	
	@Column(name = "SECTION_TYPE_ID")
	private String sectionTypeId;
	
	@Column(name = "SECTION_TYPE_DESC")
	private String sectionTypeDesc;
	
	@Column(name = "PROPOBABILITY_OF_SUCCESS_ID")
	private String propobabilityOfSuccessId;
	
	@Column(name = "PROPOBABILITY_OF_SUCCESS_DESC")
	private String propobabilityOfSuccessDesc;
	
	@Column(name = "TYPE_OF_BUSINESS_ID")
	private String typeOfBusinessId;
	
	@Column(name = "TYPE_OF_BUSINESS_DESC")
	private String typeOfBusinessDesc;

	@Column(name = "CURRENT_INSURER")
	private String currentInsurer;
	
	@Column(name = "COMPANY_ID")
	private String companyId;
	
	@Column(name = "PRODUCT_ID")
	private String productId;
	
}
