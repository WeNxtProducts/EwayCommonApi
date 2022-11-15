package com.maan.eway.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "MAIL_DETAILS")
public class MailDetails {

	@Id
	@Column(name = "ID", length = 100, nullable = false)
	private String id;

	@Column(name = "MAIL_SUBJECT", length = 100)
	private String mailSubject;

	@Column(name = "MAIL_BODY", length = 5000)
	private String mailBody;

	@Column(name = "TYPE", length = 100)
	private String type;

	@Column(name = "SENDER", length = 100)
	private String sender;

	@Column(name = "CREATED_BY", length = 100)
	private String createdBy;

	@Column(name = "INS_COMPANY_ID", length = 100)
	private String insCompanyId;

	@Column(name = "BRANCH_CODE", length = 100)
	private String branchCode;

}
