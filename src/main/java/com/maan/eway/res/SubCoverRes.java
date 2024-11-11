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
public class SubCoverRes {

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
	    @JsonProperty("SubCoverNameLocal") 
	    public String subCoverNameLocal;
	   
	    
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
	    
	    @JsonProperty("CoverBasedOn")
	    private String coverBasedOn;
		 
	    @JsonProperty("RegulatoryCode")
	    private String  regulatoryCode ;
	    

		 @JsonProperty("MultiSelectYn") 
		 private String multiSelectYn;

		 @JsonProperty("SectionName") 
		 private String sectionName;
		 
		 @JsonProperty("MinimumPremiumYn") 
		 private String minimumPremiumYn;

}
