package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.CoverRes;

import lombok.Data;

@Data
public class SectionDetailsRes {
	
	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("SectionName")
	private String sectionName;
	
	@JsonProperty("Count")
	private String count;
	
	@JsonProperty("OccupationId")
	private String occupationId;
	
	@JsonProperty("OccupationDesc")
	private String occupationDesc;
	
	@JsonProperty("SumInsured")
	private String sumInsured;

	@JsonProperty("ContentType")
	private String contentType;
	
	@JsonProperty("ContentDesc")
	private String contentDesc;


	@JsonProperty("LocationId")
	private  String locationId;	
	
	@JsonProperty("LocationName")
	private  String locationName;	
	
	@JsonProperty("PremiumAfterDiscount")
	private  String premiumAfterDiscount;	

	@JsonProperty("PremiumAfterDiscountLc")
	private  String premiumAfterDiscountLc;	

	@JsonProperty("PremiumBeforeDiscount")
	private  String premiumBeforeDiscount;	

	@JsonProperty("PremiumBeforeDiscountLc")
	private  String premiumBeforeDiscountLc;	

	@JsonProperty("PremiumExcluedTax")
	private  String premiumExcluedTax;	
	
	@JsonProperty("PremiumExcluedTaxLc")
	private  String premiumExcluedTaxLc;	
	
	@JsonProperty("PremiumIncludedTax")
	private  String premiumIncludedTax;	

	@JsonProperty("PremiumIncludedTaxLc")
	private  String premiumIncludedTaxLc;	
	
	@JsonProperty("Covers")
	private  List<CoverRes> covers ;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
	
	@JsonProperty("MoneySafeLimit")
    private String moneySafeLimit    ;
	
	@JsonProperty("MoneyOutofSafe")
    private String moneyOutofSafe    ;
	
	@JsonProperty("MoneyDirectorResidence")
    private String moneyDirectorResidence    ;
	
	@JsonProperty("MoneyCollector")
    private String moneyCollector    ;
	
	@JsonProperty("MoneyAnnualEstimate")
    private String moneyAnnualEstimate    ;
	
	@JsonProperty("MoneyMajorLoss")
    private String moneyMajorLoss;
	
	@JsonProperty("PremiumLc")
    private Double premiumLc;	

	@JsonProperty("PremiumFc")
    private Double premiumFc;	

	@JsonProperty("OverAllPremiumFc")
    private Double overAllPremiumFc;	

	@JsonProperty("OverAllPremiumLc")
    private Double overAllPremiumLc;	

	@JsonProperty("CommissionAmount")
    private String commissionAmount;	

	@JsonProperty("CommissionPercentage")
    private String commissionPercentage;

	@JsonProperty("VatCommission")
    private String vatCommission;
	
	@JsonProperty("FinalyzeYn")
	private String finalizeYn;
	
	@JsonProperty("OccupationTypeDesc")
    private String    occupationTypeDesc;

	
	
	
}
