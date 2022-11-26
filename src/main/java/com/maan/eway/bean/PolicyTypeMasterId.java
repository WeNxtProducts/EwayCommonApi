package com.maan.eway.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PolicyTypeMasterId  implements Serializable{

	private static final long serialVersionUID=1L;
	
	private Integer policyTypeId;
	private Integer     productId;
   private String     companyId ;
  
}
