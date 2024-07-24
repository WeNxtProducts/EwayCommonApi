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
@Table(name = "eway_enquiry_details")
@Data
public class EnquiryDetails implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENQUIRY_ID", length = 20, nullable = false)
	private String enquiryId;
	
	@Column(name ="LEAD_ID")
	private String leadId;
	
	@Column(name ="ENQUIRY_DESCRIPTION")
	private String enquiryDescription;
	
	@Column(name ="LOB_ID")
	private String lobId;
	
	@Column(name ="PRODUCT_ID")
	private String productId;
	
	@Column(name ="SUM_INSURED")
	private Double sumInsured;
	
	@Column(name ="SUGGEST_PREMIUM")
	private Double suggestPremium;
	
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
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REJECTED_DATE")
	private Date rejectedDate;
	
	@Column(name = "REJECTED_REASON")
	private String rejectedReason;
	
	@Column(name ="STATUS")
	private String status;
	
	@Column(name ="QUOTE_NO")
	private String quoteNo;
	
}
