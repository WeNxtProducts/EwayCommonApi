package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductGroupMasterRes {
	
	 @JsonProperty("GroupId")
	    private Integer    groupId ;

	  @JsonProperty("ProductId")
	    private Integer    productId ;

	  @JsonProperty("BranchCode")
	    private String     branchCode ;

	  @JsonProperty("CompanyId")
	    private String     companyId ;

	  @JsonProperty("GroupDesc")
	    private String     groupDesc ;

	  @JsonProperty("BandDesc")
	    private String     bandDesc ;
	    
	  @JsonProperty("GroupFrom")
	    private Integer    groupFrom ;

	  @JsonProperty("GroupTo")
	    private Integer    groupTo ;

	  @JsonProperty("ProductName")
	    private String     productName ;
	  @JsonFormat(pattern="dd/MM/yyyy")
	  @JsonProperty("EffectiveDateStart") 
	    private Date       effectiveDateStart ;
	

	  @JsonProperty("Status")
	    private String     status ;

	  @JsonProperty("CoreAppCode")
	    private String     coreAppCode ;

	  @JsonProperty("CreatedBy")
	    private String     createdBy ;

	  @JsonProperty("Remarks")
	    private String     remarks ;

}
