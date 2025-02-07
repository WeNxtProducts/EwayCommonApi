package com.maan.eway.bean;

import java.io.Serializable;
import java.util.Date;

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
@ToString
@Builder
public class ClaimIntimationId implements Serializable {
	 private static final long serialVersionUID = 1L;

	    //--- ENTITY KEY ATTRIBUTES 
	    private Integer claimReferenceNo ;
	    
	    private String companyId;
	    
	    private String productId;
	    
		private Integer amendId;
}
