package com.maan.eway.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@DynamicInsert
@DynamicUpdate
@IdClass(ErrorDescMasterId.class)
@Entity
@Table(name ="error_desc_master")
public class ErrorDescMaster implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="ERROR_CODE")
	private String errorCode;
	
	@Column(name="ERROR_FIELD")
	private String errorField;

	@Column(name="ERROR_DESC")
	private String errorDesc;
	
	@Id
	@Column(name="AMEND_ID")
	private Integer amendId;
	
	@Column(name="EFFECTIVE_DATE_END")
	private Date effectiveDateEnd;
	
	@Column(name="EFFECTIVE_DATE_START")
	private Date effectiveDateStart;
	
	@Column(name="STATUS")
	private String status;
	
	@Column(name="REMARKS")
	private String remarks;
	
	@Column(name="ENTRY_DATE")
	private Date entryDate;
	
	@Id
	@Column(name="PRODUCT_ID")
	private String productId;
	
	@Column(name="CREATED_BY")
	private String createdBy;
	
	@Column(name="UPDATED_BY")
	private String updatedBy;
	
	@Column(name="UPDATED_DATE")
	private Date updatedDate;
	
}
