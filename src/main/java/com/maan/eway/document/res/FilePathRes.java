package com.maan.eway.document.res;

import java.util.Date;

import jakarta.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FilePathRes {

	@JsonProperty("UniqueId")
	private String uniqueId;


	@JsonProperty("FilePathOriginal")
	private String filePathOriginal;

	
	@JsonProperty("DocumentName")
	private String documentName;
	
	@JsonProperty("ProductName")
	private String producttName;
	
	
	@JsonFormat(pattern = "dd/MM/YYYY")
	@JsonProperty("UploadedTime")
	private Date uploadedTime;
	
	@JsonProperty("IdType")
	private String idType ;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("SectionName")
	private String sectionName;
	
	@JsonProperty("CompanyName")
	private String companyName;
	
	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("LocationId")
	private String locationId;
	
	@JsonProperty("LocationName")
	private String locationName;
	
	@JsonProperty("ProductType")
	private String productType;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("FileName")
	private String fileName;

	@JsonProperty("OriginalFileName")
	private String originalFileName;

	@JsonProperty("UploadedBy")
	private String uploadedBy;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@JsonProperty("QuoteNo")
	private String quoteNo;

	@JsonProperty("FilePathName")
	private String filepathname ;
	@JsonProperty("UploadedTime")
	private String uploadedtime;
	
	@JsonProperty("Status")
	private String status;
	@JsonProperty("FileName")
	private String filename;
	
	@JsonProperty("DocumentId")
	private String documentId;
	
	@JsonProperty("DocumentDesc")
	private String documentDesc;
	
	@JsonProperty("DocApplicable")
	private String docApplicable;
	@JsonProperty("DocApplicableId")
	private String docApplicableId;
	
	@JsonProperty("CommonFilePath")
	private String commonfilepath ;
	
	@JsonProperty("Errorres")
	private String errorres ;
	


	@JsonProperty("Id")
	private String id;

	@Column(name = "FILE_PATH_ORGINAL")
	private String filePathOrginal;
	
	@JsonProperty("DocDesc")
	private String docDesc;
	
	
	@JsonProperty("DocumentType")
	private String documentType;
	
	@JsonProperty("DocumentTypeDesc")
	private String documentTypeDesc;
	
	@JsonFormat(pattern = "dd/MM/YYYY")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("OrginalFileName")
	private String orginalFileName;
	
	@JsonProperty("ImgUrl")
	private String imgurl;
	
	
}
