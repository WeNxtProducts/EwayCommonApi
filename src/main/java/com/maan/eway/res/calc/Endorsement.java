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
    
    @JsonProperty("EndtCount") 
    private BigDecimal endtCount;
    
    @JsonProperty("Taxes")
    private List<Tax> taxes;
}
