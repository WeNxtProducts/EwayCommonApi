package com.maan.eway.res.calc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cover implements Serializable{
	 @JsonProperty("CoverId") 
	    public String coverId;
	    @JsonProperty("CalcType") 
	    public String calcType;
	    @JsonProperty("CoverName") 
	    public String coverName;
	    @JsonProperty("CoverDesc") 
	    public String coverDesc;
	    @JsonProperty("MinimumPremium") 
	    public BigDecimal minimumPremium;
	    @JsonProperty("CoverToolTip") 
	    public String coverToolTip;
	    @JsonProperty("IsSubCover") 
	    public String isSubCover;
	    @JsonProperty("SumInsured") 
	    public BigDecimal sumInsured;
	    @JsonProperty("Rate") 
	    public Double rate;
	    @JsonProperty("SubCoverId") 
	    public String subCoverId;
	    @JsonProperty("SubCoverDesc") 
	    public String subCoverDesc;
	    @JsonProperty("SubCoverName") 
	    public String subCoverName;
	    @JsonProperty("SectionId")
		private String sectionId;
	    @JsonProperty("Discounts") 
	    public List<Discount> discounts;
	    @JsonProperty("Taxes") 
	    public List<Tax> taxes;
	    
	    @JsonProperty("SubCovers") 
	    public List<Cover> subcovers;
	    
	    @JsonProperty("FactorTypeId")
	    private String factorTypeId;
	    
	    @JsonProperty("DependentCoverYN")
	    private String dependentCoveryn;

	    @JsonProperty("DependentCoverId")
	    private String dependentCoverId;
	    
	    @JsonProperty("Exception")
	    private CoverException error;

	    @JsonProperty("Loadings") 
	    public List<Loading> loadings;
	    @JsonProperty("CoverageType") 
	    private String coverageType;
	    @JsonProperty("isSelected") 
	    private String isselected;
	    
	    @JsonProperty("Notsutable") 
	    private boolean notsutable;
	    
	    
	    @JsonProperty("PremiumBeforeDiscountLC") 
	    private BigDecimal premiumBeforeDiscountLC;
	    @JsonProperty("PremiumAfterDiscountLC") 
	    private BigDecimal premiumAfterDiscountLC;
	    @JsonProperty("PremiumExcluedTaxLC") 
	    private BigDecimal premiumExcluedTaxLC;
	    @JsonProperty("PremiumIncludedTaxLC") 
	    private BigDecimal premiumIncludedTaxLC;
	    
	    @JsonProperty("PremiumBeforeDiscount") 
	    private BigDecimal premiumBeforeDiscount;
	    @JsonProperty("PremiumAfterDiscount") 
	    private BigDecimal premiumAfterDiscount;
	    @JsonProperty("PremiumExcluedTax") 
	    private BigDecimal premiumExcluedTax;
	    @JsonProperty("PremiumIncludedTax") 
	    private BigDecimal premiumIncludedTax;
	    
	    @JsonProperty("ExchangeRate")
	    private BigDecimal exchangeRate;
	    @JsonProperty("Currency")
	    private String currency;
	    
	    @JsonProperty("isReferal")
	    private String isReferral;
	    
	    @JsonProperty("ReferalDescription")
	    private String referalDescription;
	    @JsonProperty("ProRata")
	    private BigDecimal proRata;
	    
	    
	    @JsonProperty("RegulatorSumInsured") 
	    private BigDecimal tiraSumInsured;
	    @JsonProperty("RegulatorRate") 
	    private Double tiraRate;

	    @JsonProperty("UserOpt")
	    private String     userOpt ;
	    
	    @JsonProperty("CoverBasedOn")
	    private String coverBasedOn;

	    @JsonProperty("RegulatoryCode")
	    private String  regulatoryCode ;
	    
	    @JsonProperty("InsuranceId") 
		private String insuranceId;
		@JsonProperty("BranchCode") 
		private String branchCode;
		@JsonProperty("AgencyCode") 
		private String agencyCode;
		
		 @JsonProperty("ProductId") 
		 private String productId;
		 
		 @JsonProperty("MSRefNo") 
		 private String msrefno;
		 @JsonProperty("VehicleId") 
		 private String vehicleId;
		 
		 
		 @JsonProperty("CdRefNo")
			private String cdRefNo;

			@JsonProperty("VdRefNo")
			private String vdRefNo;
			@JsonProperty("CreatedBy")
			private String createdBy;
			
			
			@JsonProperty("RequestReferenceNo")
			private String requestReferenceNo;
			
			 @JsonProperty("MultiSelectYn") 
			 private String multiSelectYn;

			 @JsonProperty("SectionName") 
			 private String sectionName;
			 
			 @JsonProperty("ExcessPercent") 
			 private BigDecimal excessPercent;
			 @JsonProperty("ExcessAmount") 
			 private BigDecimal excessAmount;
			 @JsonProperty("ExcessDesc") 
			 private String excessDesc;
			 
			 @JsonProperty("MinimumPremiumYn") 
			 private String minimumPremiumYn;
			 @JsonProperty("ProRataApplicable")
			 private String proRataYn;
			 
			 @JsonProperty("Endorsements")
			 private List<Endorsement> endorsements;
			 
			 @JsonProperty("EndtCount")
			 private BigDecimal endtCount;
}


