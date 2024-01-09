package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FactorRateRequestCommonRes {
	
	@JsonProperty("CoverId")
	private Integer coverId;
	
	@JsonProperty("CalcType")
	private String calcType;
	
	@JsonProperty("CoverName")
	private String coverName;
	
	@JsonProperty("CoverDesc")
	private String coverDesc;
	
	@JsonProperty("MinPrem")
	private Long minPrem;
	
	@JsonProperty("SubCoverYn")
	private String subCoverYn;
	
	@JsonProperty("SumInsured")
	private Long sumInsured;
	
	@JsonProperty("SumInsuredFc")
	private Long sumInsuredFc;
	
	@JsonProperty("Rate")
	private Long rate;
	

	@JsonProperty("SubCoverName")
	private String subCoverName;
	
	@JsonProperty("SubCoverDesc")
	private String subCoverDesc;
	

	@JsonProperty("SubCoverId")
	private Integer subCoverId;
	
	@JsonProperty("SectionId")
	private Integer sectionId;
	
	@JsonProperty("Discounts")
	private List<FactorRateRequestRes> discounts;
	
	@JsonProperty("Tax")
	private List<FactorTypeTaxRes> tax;
	
	@JsonProperty("Loading")
	private List<FactorrateRequestLoadingRes> loading;
	
	@JsonProperty("FactorTypeId")
	private Long factorTypeId;
	
	@JsonProperty("DepentCoverYn")
	private String depentCoverYn;
	
	@JsonProperty("CoverType")
	private String coverType;
	
	@JsonProperty("IsSelected")
	private String isSelected;
	
	@JsonProperty("PreBeforeDis")
	private Long preBeforeDis;
	
	@JsonProperty("PreBeforeDisLc")
	private Long preBeforeDisLc;
	
	@JsonProperty("PreAfterDis")
	private Long preAfterDis;
	
	@JsonProperty("PreAfterDisLc")
	private Long preAfterDisLc;
	
	@JsonProperty("PreExcludedDis")
	private Long preExcludedDis;
	
	@JsonProperty("PreExcludedDisLc")
	private Long preExcludedDisLc;
	
	@JsonProperty("PreIxcludedDis")
	private Long preIxcludedDis;
	
	@JsonProperty("PreIxcludedDisLc")
	private Long preIxcludedDisLc;
	
	@JsonProperty("ExRate")
	private Long exRate;
	
	@JsonProperty("Currency")
	private String currency;

	@JsonProperty("IsRefferal")
	private String isRefferal;
	
	@JsonProperty("ProRataYn")
	private String proRataYn;
	
	@JsonProperty("ProRataPercent")
	private Long proRataPercent;
	
	@JsonProperty("RegulatoryRate")
	private Long regulatoryRate;
	
	@JsonProperty("RegulatorySI")
	private Long regulatorySI;
	
	@JsonProperty("UserOtp")
	private String userOtp;
	
	@JsonProperty("CoverBasedOn")
	private String coverBasedOn;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonProperty("ProductId")
	private Integer productId;
	
	@JsonProperty("VehicleId")
	private Integer vehicleId;
	
	
	@JsonProperty("CdRefNo")
	private String cdRefNo;
	
	@JsonProperty("VdRefNo")
	private String vdRefNo;
	
	@JsonProperty("MsRefNo")
	private String msRefNo;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("MultiSelectYn")
	private String multiSelectYn;
	
	@JsonProperty("ExcessAmount")
	private Long excessAmount;
	
	@JsonProperty("ExcessPercent")
	private Long excessPercent;
	
	@JsonProperty("ExcessDesc")
	private String excessDesc;
	
	@JsonProperty("MinimunPreYn")
	private String minimunPreYn;
	
	@JsonProperty("EndtCount")
	private Long endtCount;
	
	@JsonFormat(pattern = "dd/mm/yyyy")
	@JsonProperty("EffectiveDate")
	private Date effectiveDate;	
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("DiffPreIncludeFc")
	private Long diffPreIncludeFc;
	
	@JsonProperty("DiffPreIncludeLc")
	private Long diffPreIncludeLc;
	
	@JsonProperty("CoverageLimit")
	private Long coverageLimit;
	
	@JsonProperty("MinPreFc")
	private Long minPreFc;

	@JsonFormat(pattern = "dd/mm/yyyy")
	@JsonProperty("PolicyEndDate")
	private Date policyEndDate;	
	
}
