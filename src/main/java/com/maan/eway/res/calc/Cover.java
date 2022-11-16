package com.maan.eway.res.calc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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
	    public BigDecimal premiumBeforeDiscountLC;
	    @JsonProperty("PremiumAfterDiscountLC") 
	    public BigDecimal premiumAfterDiscountLC;
	    @JsonProperty("PremiumExcluedTaxLC") 
	    public BigDecimal premiumExcluedTaxLC;
	    @JsonProperty("PremiumIncludedTaxLC") 
	    public BigDecimal premiumIncludedTaxLC;
	    
	    @JsonProperty("PremiumBeforeDiscount") 
	    public BigDecimal premiumBeforeDiscount;
	    @JsonProperty("PremiumAfterDiscount") 
	    public BigDecimal premiumAfterDiscount;
	    @JsonProperty("PremiumExcluedTax") 
	    public BigDecimal premiumExcluedTax;
	    @JsonProperty("PremiumIncludedTax") 
	    public BigDecimal premiumIncludedTax;
	    
	    @JsonProperty("ExchangeRate")
	    public BigDecimal exchangeRate;
	    @JsonProperty("Currency")
	    public String currency;
	    
	    @JsonProperty("isReferal")
	    private String isReferral;
	    
	    @JsonProperty("ReferalDescription")
	    private String referalDescription;

}
