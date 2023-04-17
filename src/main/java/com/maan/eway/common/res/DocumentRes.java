package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocumentRes {
	
	@JsonProperty("DocumentType")
	private String documentType ;
	
	@JsonProperty("DocumentFie")
	private String orginalFileName ;
	
	@JsonProperty("DocumentDescription")
	private String documentDesc ;
	
	@JsonProperty("DocumentReferenceNumber")
	private Integer documentReferenceNo;
	
	@JsonProperty("Originalpath")
	private String filePathOrginal;

	@JsonProperty("Compressed")
	private String filePathBackup;
	
	@JsonProperty("DocumentTypeDesc")
	private String documentTypeDesc;
	
	@JsonProperty("DocumentId")
	private String documentId;
	
	@JsonProperty("Id")
	private String id;
}
