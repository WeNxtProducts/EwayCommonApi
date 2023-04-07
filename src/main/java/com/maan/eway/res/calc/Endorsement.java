package com.maan.eway.res.calc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endorsement implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("EndorsementId") 
    private String endorsementId;
    @JsonProperty("EndorsementDesc") 
    private String endorsementDesc;
    @JsonProperty("EndorsementRate") 
    private String endorsementRate;
   // @JsonProperty("EndorsementAmount") 
  //  private BigDecimal endorsementAmount;
    @JsonProperty("EndorsementCalcType") 
    private String endorsementCalcType;
    @JsonProperty("EndorsementForId") 
    private String endorsementforId;
    @JsonProperty("SubCoverId") 
    public String subCoverId;
    @JsonProperty("MaxEndorsementAmount") 
    public BigDecimal maxAmount;
    @JsonProperty("FactorTypeId")
    private String factorTypeId;

    @JsonProperty("RegulatoryCode")
    private String  regulatoryCode ;
    
    
    
    @JsonProperty("PremiumBeforeDiscountLC") 
    private BigDecimal premiumBeforeDiscountLC;
    @JsonProperty("PremiumAfterDiscountLC") 
    private BigDecimal premiumAfterDiscountLC;
    @JsonProperty("premiumExcludedTaxLC") 
    private BigDecimal premiumExcluedTaxLC;
    @JsonProperty("PremiumIncludedTaxLC") 
    private BigDecimal premiumIncludedTaxLC;
    
    @JsonProperty("PremiumBeforeDiscount") 
    private BigDecimal premiumBeforeDiscount;
    @JsonProperty("PremiumAfterDiscount") 
    private BigDecimal premiumAfterDiscount;
    @JsonProperty("PremiumExcludedTax") 
    private BigDecimal premiumExcluedTax;
    @JsonProperty("PremiumIncludedTax") 
    private BigDecimal premiumIncludedTax;
    
    @JsonProperty("EndtCount") 
    private BigDecimal endtCount;
   
    @JsonProperty("ProRata")
    private BigDecimal proRata;
    @JsonProperty("ProRataApplicable")
	 private String proRataYn;
    @JsonProperty("Taxes")
    private List<Tax> taxes;
    

    @JsonProperty("EndorsementFees")
    private List<Tax> endtFees;
   
}
