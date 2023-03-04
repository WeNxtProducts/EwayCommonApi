package com.maan.eway.res;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.CoverException;
import com.maan.eway.res.calc.Discount;
import com.maan.eway.res.calc.Loading;
import com.maan.eway.res.calc.Tax;

import lombok.Data;

@Data
public class CoverRes {

	@JsonProperty("CoverId") 
    public String coverId;
	@JsonProperty("Rate") 
    public Double rate;
	
    @JsonProperty("CoverName") 
    public String coverName;
    @JsonProperty("CoverDesc") 
    public String coverDesc;
    @JsonProperty("IsSubCover") 
    public String isSubCover;
    @JsonProperty("SumInsured") 
    public BigDecimal sumInsured;
 
    @JsonProperty("SubCovers") 
    public List<SubCoverRes> subcovers;

    @JsonProperty("DependentCoverYN")
    private String dependentCoveryn;

    @JsonProperty("DependentCoverId")
    private String dependentCoverId;
   
    @JsonProperty("CoverageType") 
    private String coverageType;
    @JsonProperty("isSelected") 
    private String isselected;
    
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
    
    @JsonProperty("RegulatoryCode")
    private String  regulatoryCode ;
    
    @JsonProperty("ExcessAmount")
    private String  excessAmount;
    
    @JsonProperty("ExcessPercent")
    private String  excessPercent;
    
    @JsonProperty("ExcessDesc")
    private String  excessDesc;
   
	 @JsonProperty("MultiSelectYn") 
	 private String multiSelectYn;

	 @JsonProperty("SectionName") 
	 private String sectionName;
	 
	 @JsonProperty("MinimumPremiumYn") 
	 private String minimumPremiumYn;
	
	
}
