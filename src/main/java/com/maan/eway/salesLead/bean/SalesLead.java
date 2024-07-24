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
@Table(name = "eway_sales_lead")
public class SalesLead  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "LEAD_ID", length = 20, nullable = false)
	private String leadId;
	
	@Column(name = "FIRST_NAME")
	private String firstName;
	
	@Column(name = "LAST_NAME")
	private String lastName;
	
	@Column(name = "ADDRESS")
	private String address;
	
	@Column(name = "EMAIL")
	private String email;
	
	@Column(name = "MOBILE")
	private String mobile;
	
	@Column(name = "BRANCH_CODE")
	private String branchCode;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ENTRY_DATE")
	private Date entryDate;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED_DATE")
	private Date updatedDate;
	
	@Column(name = "UPDATED_BY")
	private String updatedBy;
	
	@Column(name = "INTERMEDIATE_ID")
	private String intermediateId;
	
	@Column(name = "INTERMEDIATE_NAME")
	private String intermediateName;
	
	@Column(name = "CHANNEL_ID")
	private String channelId;
	
	@Column(name = "CHANNEL_DESC")
	private String channelDesc;
	
	@Column(name = "PROPOBABILITY_OF_SUCCESS_ID")
	private String propobabilityOfSuccessId;
					
	@Column(name = "PROPOBABILITY_OF_SUCCESS")
	private String propobabilityOfSuccess;
	
	@Column(name = "TYPE_OF_BUSINESS_ID")
	private String typeOfBusinessId;
	
	@Column(name = "TYPE_OF_BUSINESS")
	private String typeOfBusiness;
	
	@Column(name = "CURRENT_INSURER")
	private String currentInsurer;
	
}
