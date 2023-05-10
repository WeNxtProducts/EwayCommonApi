package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EndtTypeMasterDto {
	@JsonProperty("Endttypeid")
    private Integer    endtTypeId   ;
	@JsonProperty("Endttypecategoryid")
    private Integer    endtTypeCategoryId ;
	@JsonProperty("Status")
    private String     status       ;
	@JsonProperty("Productid")
    private Integer    productId    ;
	@JsonProperty("Companyid")
    private String     companyId    ;

    //----------------------------------------------------------------------
    // ENTITY DATA FIELDS 
    //----------------------------------------------------------------------    
	@JsonProperty("Endttype")
    private String     endtType     ;
	@JsonProperty("Endttypedesc")
    private String     endtTypeDesc ;
	@JsonProperty("Endttypecategory")
    private String     endtTypeCategory ;
	@JsonProperty("Priority")
    private Integer    priority     ;
	@JsonProperty("Endtdependantfields")
    private String     endtDependantFields ;
	@JsonProperty("Coreappcode")
    private BigDecimal coreAppCode  ;
	@JsonProperty("Endtfeeyn")
    private String     endtFeeYn    ;
	@JsonProperty("Endtfeepercent")
    private String     endtFeePercent ;
}
