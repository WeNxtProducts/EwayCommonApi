package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TinyUrlMasterSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("Sno")
	private String sno;

	@JsonProperty("Type")
	private String type;
	
	@JsonProperty("AppUrl")
	private String appUrl;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;
		
	@JsonProperty("BranchCode")
	private String branchCode;
		
	@JsonProperty("Status")
	private String status;

	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("CreatedBy")
	private String createdBy;


	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;		
		
	@JsonProperty("UpdatedBy")
	private String updatedBy;

}
