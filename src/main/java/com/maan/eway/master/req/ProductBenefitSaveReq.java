package com.maan.eway.master.req;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductBenefitSaveReq {

	@JsonProperty("BenefitId")
	private String benefitId;
	
	@JsonProperty("Description")
	private String description;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;

	@JsonProperty("TypeId")
	private String typeId;
	
	@JsonProperty("DisplayOrder")
	private String displayOrder;

//	@JsonProperty("ImageFile")
//	private Object imageFile;


}
