package com.maan.eway.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BuildingSumInsuredDetails {

	
	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("BuildingSuminsured")
    private String     buildingSuminsured ;
	
	@JsonProperty("AllriskSumInsured")
    private String     allriskSuminsured ;
	
//	@JsonProperty("OccupationDetails")
//    private List<OccupationReqClass>     occupationDetails ;
	
	@JsonProperty("OccupationType")
    private String    occupationType;

	
	@JsonProperty("PersonalAccSuminsured")
    private String    personalAccSuminsured;

	@JsonProperty("Count")
    private String    count;
//	@JsonProperty("PaTotaldisabilitySumInsured")
//    private String     paTotaldisabilitySumInsured ;
//	
//	@JsonProperty("PaPermanentdisablementSuminsured")
//    private String     paPermanentdisablementSuminsured ;
//	
//	@JsonProperty("PaMedicalSuminsured")
//    private String     paMedicalSuminsured ;
	@JsonProperty("ContentSuminsured")
    private String     contentSuminsured ;
	@JsonProperty("PersonalIntermediarySuminsured")
    private String     personalIntermediarySuminsured ;
	
	@JsonProperty("SectionId")
	private List<String> sectionId;
	
	@JsonProperty("WorkmenCompSuminsured")
    private String    workmenCompSuminsured;
	
	@JsonProperty("ElecEquipSuminsured")
    private String     elecEquipSuminsured ;
		

	@JsonProperty("MoneySinglecarrySuminsured")
    private String   moneySinglecarrySuminsured ;
		
	@JsonProperty("MoneyAnnualcarrySuminsured")
    private String   moneyAnnualcarrySuminsured ;
	
	@JsonProperty("MoneyInsafeSuminsured")
    private String   moneyInsafeSuminsured ;
	
	@JsonProperty("FidelityAnyoccuSuminsured")
    private String   fidelityAnyoccuSuminsured ;
	
	@JsonProperty("FidelityAnnualSuminsured")
    private String   fidelityAnnualSuminsured ;
	
	@JsonProperty("TpliabilityAnyoccuSuminsured")
    private String tpliabilityAnyoccuSuminsured ;
	
	@JsonProperty("EmpliabilityAnnualSuminsured")
    private String empliabilityAnnualSuminsured ;
	
	@JsonProperty("EmpliabilityExcessSuminsured")
    private String empliabilityExcessSuminsured ;

	@JsonProperty("GoodsSinglecarrySuminsured")
    private String   goodsSinglecarrySuminsured ;

	@JsonProperty("GoodsTurnoverSuminsured")
    private String   goodsTurnoverSuminsured ;
	
}
