package com.maan.eway.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@IdClass(EndtDependantFieldsMasterId.class)
@Table(name="endt_dependant_fields_master")
public class EndtDependantFieldMaster {

	@Id
	@Column(name="DEPENDANT_FIELD_ID", nullable=false)
	private Integer dependantFieldId;
	
	@Id
	@Column(name="COMPANY_ID", length=10, nullable=false)
	private String companyId;
	
	@Id
	@Column(name="PRODUCT_ID", length=10, nullable=false)
	private String productId;
	
	@Column(name="DEPENDANT_FIELD_NAME", length=100)
	private String dependantFieldName;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EFFECTIVE_DATE_START")
	private Date effectiveDateStart;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EFFECTIVE_DATE_END")
	private Date effectiveDateEnd;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ENTRY_DATE")
	private Date entryDate;
	
	@Column(name="CREATED_BY", length=100)
	private String createdBy;
	
	@Column(name="UPDATED_BY", length=100)
	private String updatedBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATED_DATE")
	private Date UpdatedDate;
	
	@Column(name="CORE_APP_CODE", length=10)
	private String coreAppCode;
	
	@Column(name="REGULATORY_CODE", length=10)
	private String regulatoryCode;

	@Column(name="AMEND_ID")
	private Integer amendId;

	@Column(name="STATUS", length=10)
	private String status;


	@Column(name="REMARKS", length=100)
	private String remarks;

}
