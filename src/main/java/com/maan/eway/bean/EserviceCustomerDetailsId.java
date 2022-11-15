package com.maan.eway.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Data;

@Data
public class EserviceCustomerDetailsId implements Serializable {
	private static final long serialVersionUID = 1L;
	 
	private String     customerReferenceNo;

	private String     companyId ;
	
	private Integer     productId ;
}
