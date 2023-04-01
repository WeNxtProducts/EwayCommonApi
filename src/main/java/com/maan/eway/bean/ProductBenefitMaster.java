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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="product_benefit_master")
@IdClass(ProductBenefitMasterId.class)
public class ProductBenefitMaster {

	@Id
	@Column(name="BENEFIT_ID",nullable=false)
	private Integer benefitId;
	
	@Id
	@Column(name="TYPE_ID",length=20, nullable=false)
	private Integer typeId;
	
	@Id
	@Column(name="COMPANY_ID",length=20, nullable=false)
	private String companyId;

	@Id
	@Column(name="PRODUCT_ID",length=20, nullable=false)
	private String productId;

	@Id
	@Column(name="SECTION_ID",length=20, nullable=false)
	private String sectionId;

	@Id
	@Column(name="AMEND_ID",nullable=false)
	private Integer amendId;		
	
	@Column(name="DESCRIPTION",length=500)
	private String description;
	
	@Column(name="TYPE_DESC",length=500)
	private String typeDesc;
	
	@Column(name="ICON_PATH",length=500)
	private String iconPath;
	
	@Column(name="ORIGINAL_IMAGE_PATH",length=500)
	private String originalImagePath;
	
	@Column(name="COMPANY_NAME",length=500)
	private String companyName;
	
	@Column(name="PRODUCT_DESC",length=500)
	private String productDesc;
	
	@Column(name="SECTION_DESC",length=500)
	private String sectionDesc;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EFFECTIVE_DATE_START",nullable=false)
	private Date effectiveDateStart;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EFFECTIVE_DATE_END",nullable=false)
	private Date effectiveDateEnd;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATED_DATE")
	private Date updatedDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="ENTRY_DATE")
	private Date entryDate;
	
	@Column(name="REMARKS",length=100)
	private String remarks;
	
	@Column(name="STATUS",length=1)
	private String status;
	
	@Column(name="CREATED_BY",length=100)
	private String createdBy;
	
	@Column(name="UPDATED_BY",length=100)
	private String updatedBy;
	
	@Column(name="CORE_APP_CODE",length=20)
	private String coreAppCode;
	
	@Column(name="REGULATORY_CODE",length=20)
	private String regulatoryCode;
	
	@Column(name="DISPLAY_ORDER",length=20)
	private Integer displayOrder;

}
