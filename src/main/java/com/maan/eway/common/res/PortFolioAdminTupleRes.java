package com.maan.eway.common.res;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortFolioAdminTupleRes {

	private Long count ; 
	private BigDecimal overallPremiumLc ;
	private BigDecimal overallPremiumFc ;
	private Integer productId ; 
	private String productName ; 
	private String agencyCode ;
	private String brokerName ;
	private String userType ;
//	private String subUserType ;
	private Integer oaCode ;
	private String customerCode ;
	private String customerName ;
	private String sourceType ;
	private String bdmCode ;
	
}
