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
@Table(name="payment_master")
@IdClass(PaymentMasterId.class)
public class PaymentMaster {

	@Id
	@Column(name="PAYMENT_MASTER_ID",nullable=false)
	private Integer paymentMasterId;
	
	@Id
	@Column(name="BRANCH_CODE",length=20, nullable=false)
	private String branchCode;
	
	@Id
	@Column(name="COMPANY_ID",length=20, nullable=false)
	private String companyId;

	@Id
	@Column(name="PRODUCT_ID",length=20, nullable=false)
	private Integer productId;
	
	@Id
	@Column(name="AMEND_ID",nullable=false)
	private Integer amendId;

	@Id
	@Column(name="USER_TYPE",length=20)
	private String userType;
	
	@Id
	@Column(name="SUB_USER_TYPE",length=20)
	private String subUserType;
	
	@Column(name="CASH_YN",length=20)
	private String cashYn;
	
	@Column(name="CREDIT_YN",length=20)
	private String creditYn;
	
	
	@Column(name="CHEQUE_YN",length=20)
	private String chequeYn;
	
	
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
	
	
	@Column(name="STATUS",length=1)
	private String status;
	
	@Column(name="CREATED_BY",length=100)
	private String createdBy;
	
	@Column(name="UPDATED_BY",length=100)
	private String updatedBy;
	
	
}
