package com.maan.eway.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="BROKER_ENDT_SETUP_MASTER")
@IdClass(BrokerEndtSetupMasterId.class)
public class BrokerEndtSetupMaster {

	@Id
	@Column(name="ENDT_SETUP_ID", length=50, nullable=false)
	private String endtSetupId;
	
	@Id
	@Column(name="LOGIN_ID", length=100, nullable=false)
	private String loginId;
	
	@Id
	@Column(name="PRODUCT_ID",length=50, nullable=false)
	private String productId;

	@Id
	@Column(name="COMPANY_ID",length=50, nullable=false)
	private String companyId;

	@Id
	@Column(name="AMEND_ID", nullable=false)
	private Integer amendId;

	@Id
	@Column(name="ENDT_TYPES", length=500, nullable=false)
	private String endtTypes;
	
	@Column(name="AGENCY_CODE")
	private Integer agencyCode;
	
	@Column(name="OA_CODE")
	private Integer oaCode;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ENTRY_DATE")
	private Date entryDate;
	
	@Column(name="STATUS", length=1)
	private String status;
	
	@Column(name="CREATED_BY", length=100)
	private String createdBy;
	
	@Column(name="UPDATED_BY", length=100)
	private String updatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATED_DATE")
	private Date updatedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EFFECTIVE_DATE_START")
	private Date effectiveDateStart;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EFFECTIVE_DATE_END")
	private Date effectiveDateEnd;

	@Column(name="REMARKS", length=100)
	private String remarks;
	
	@Column(name="USER_TYPE", length=100)
	private String userType;

	@Column(name="SUB_USER_TYPE", length=100)
	private String subUserType;


}
