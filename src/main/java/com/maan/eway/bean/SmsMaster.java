package com.maan.eway.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(SmsMasterId.class)
@Table(name="SMS_CONFIG_MASTER")
public class SmsMaster  implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="S_NO",nullable =false)
	private Integer sNo;
	
	@Id
	@Column(name="COMPANY_ID",length=20, nullable=false)
	private String companyId;
	
	@Id
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EFFECTIVE_DATE_START",nullable=false)
	private Date effectiveDateStart;
	
	@Id
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EFFECTIVE_DATE_END",nullable=false)
	private Date effectiveDateEnd;
	
	@Column(name="STATUS",length=6)
	private String Status;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ENTRY_DATE")
	private Date entryDate;
	
	
	@Column(name="SENDER_ID",length=60)
	private String senderId;
	
	@Column(name="REMARKS",length=900)
	private String remarks;
	
	@Column(name="SMS_USER_PASS",length = 150)
	private String smsUserPass;
	
	@Column(name="SMS_USER_NAME",length=150)
	private String smsUserName;
	
	@Column(name="SMS_PARTY_URL",length=300)
	private String smsPartyUrl;
	
	@Column(name="SECURE_YN", length=60)
	private String secureYn;
	
	@Column(name="CORE_APP_CODE",length=20)
	private String coreAppCode;
	
	@Column(name="REGULATORY_CODE",length=20,nullable=false)
	private String regulatoryCode;
	
	@Column(name="AmendId")
	private Integer amendId;
	
	@Column(name="CREATED_BY",length=100)
	private String createdBy;
}
