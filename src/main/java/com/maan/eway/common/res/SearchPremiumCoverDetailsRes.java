package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.SubCoverRes;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.CoverException;
import com.maan.eway.res.calc.Discount;
import com.maan.eway.res.calc.Endorsement;
import com.maan.eway.res.calc.Loading;
import com.maan.eway.res.calc.Tax;

import lombok.Data;

@Data
public class SearchPremiumCoverDetailsRes {

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
   
    @JsonProperty("CoverageType") 
    private String coverageType;
    
    @JsonProperty("PremiumExcluedTaxLC") 
    private BigDecimal premiumExcluedTaxLC;
    @JsonProperty("PremiumIncludedTaxLC") 
    private BigDecimal premiumIncludedTaxLC;
    
    @JsonProperty("PremiumExcluedTax") 
    private BigDecimal premiumExcluedTax;
    @JsonProperty("PremiumIncludedTax") 
    private BigDecimal premiumIncludedTax;
    

//    @JsonProperty("TaxRate")
//    private BigDecimal   taxRate ;
}