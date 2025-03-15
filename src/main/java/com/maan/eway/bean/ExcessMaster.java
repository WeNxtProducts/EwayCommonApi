package com.maan.eway.bean;

import java.util.Date;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ExcessMasterId.class)
@Table(name = "Excess_Master")
@Getter
@Setter
public class ExcessMaster {

	@Id
	@Column(name = "EXCESS_ID")
	private Integer excessId;

	@Id
	@Column(name = "COMPANY_ID")
	private String companyId;

	@Id
	@Column(name = "PRODUCT_ID")
	private String productId;

	@Id
	@Column(name = "SECTION_ID")
	private String sectionId;

	@Column(name = "COVER_ID")
	private String coverId;

	@Id
	@Column(name = "AMEND_ID")
	private Integer amendId;

	@Column(name = "EXCESS_PERCENTAGE")
	private Integer excessPercentage;

	@Column(name = "EXCESS_AMOUNT")
	private Double excessAmount;

	@Column(name = "EXCESS_DESCRIPTION")
	private String excessDescription;

	@Column(name = "CURRENCY")
	private String currency;

	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EFFECTIVE_DATE_START")
	private Date effectiveDateStart;

	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EFFECTIVE_DATE_END")
	private Date effectiveDateEnd;

	@Temporal(TemporalType.DATE)
     @Column(name = "ENTRY_DATE")
	private Date entryDate;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "REGULATORY_CODE")
	private String regulatoryCode;

	@Column(name = "CORE_APP_CODE")
	private String coreAppCode;
	
	@Column(name = "BRANCH_CODE")
	private String branchCode;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "COVER_NAME")
	private String coverName;
	

}
