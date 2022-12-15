package com.maan.eway.common.res;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RejectCriteriaRes {

	// Customer Info
		private Long   idsCount ;
	    private String   customerReferenceNo ;
	    private String idNumber;
		private String clientName;

		// Vehicle Info
		private String     companyId ;
		private String     productId ;
		private String     branchCode ;
		
		private String   requestReferenceNo ;
		private String quoteNo;
		private String customerId;
		private Date policyStartDate;
		private Date policyEndDate;
		private String rejectReason;
}
