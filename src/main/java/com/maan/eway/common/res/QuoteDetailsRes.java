package com.maan.eway.common.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class QuoteDetailsRes {

	@JsonProperty("QuoteNo")
	private String   quoteNo;
	
	@JsonProperty("RequestReferenceNo")
	private String   requestReferenceNo;
	
	@JsonProperty("CustomerId")
	private String   customerId;
	
	@JsonProperty("CompanyId")
	private String   companyId;
	
	@JsonProperty("BranchCode")
	private String   branchCode;
	
	@JsonProperty("ProductId")
	private String   productId;
	
	@JsonProperty("SectionId")
	private String   sectionId;
	
	@JsonProperty("AmendId")
    private String   amendId;
	
	@JsonProperty("LoginId")
	private String   loginId;
	
	@JsonProperty("ApplicationId")
	private String   applicationId;
	
	@JsonProperty("ApplicationNo")
	private String   applicationNo;
	
	@JsonProperty("AgencyCode")
	private String   agencyCode;
	
	@JsonProperty("AcExecutiveId")
	private String   acExecutiveId; 
	
	@JsonProperty("BrokerCode")
	private String   brokerCode;
	
	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDate")
	private Date effectiveDate;
	
	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("ExpiryDate")
	private Date expiryDate;
	
	@JsonProperty("Status")
	private String status ;
	
	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("QuoteCreatedDate")
	private Date quoteCreatedDate ;
	
	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate ;
	
	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("InceptionDate")
	private Date   inceptionDate;
	
	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("LapsedDate")
	private Date lapsedDate ; 
	
	@JsonProperty("LapsedRemarks")
	private String lapsedRemakrs ;
	
	@JsonProperty("LapsedUpdatedBy")
	private String lapsedUpdatedBy ;
	
	@JsonProperty("Currency")
	private String   currency;
	
	@JsonProperty("Remarks")
	private String   remarks;
	
	@JsonProperty("VehicleNo")
    private String   vehicleNo;
	
	@JsonProperty("ExchangeRate")
	private String   exchangeRate;
	
	// No OF Vehicles
	@JsonProperty("NoOfVehicles")
	private String noOfVehicles ;
	
	@JsonProperty("PremiumFc")
	private String  premiumFc ;
	
	@JsonProperty("OverallPremiumFc")
	private String   overAllPremiumFc ;
	@JsonProperty("VatPremiumFc")
	private String   vatPremiumFc;
	@JsonProperty("VatPercent")
	private String  vatPercent;
	@JsonProperty("PremiumLc")
	private String   premiumLc ;
	@JsonProperty("OverallPremiumLc")
	private String   overAllPremiumLc ;
	@JsonProperty("VatPremiumLc")
	private String  vatPremiumLc ;
	@JsonProperty("FinalizeYn")
	private String  finalizeYn ;
	@JsonProperty("Tax1")
	private String  tax1 ;
	@JsonProperty("Tax2")
	private String  tax2;
	@JsonProperty("Tax3")
	private String  tax3;
	/*
	@JsonProperty("ExcessSign(null);
	@JsonProperty("ExcessPremium(null);
	@JsonProperty("DiscountPremium(null);
	@JsonProperty("PolicyFee(null);
	@JsonProperty("OtherFee(null);
	@JsonProperty("Commission(null);
	@JsonProperty("CommissionPercentage(null);
	@JsonProperty("VatCommission(nll);
	@JsonProperty("CalcPremium(null);
	@JsonProperty("AdminReferralStatus(null);
	@JsonProperty("AdminReferralStatus(null);
	@JsonProperty("ReferralDescription(null);
	@JsonProperty("ApprovedBy(null);
	@JsonProperty("ApprCanBy(null);"
 */
}
