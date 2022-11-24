package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BankMasterSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("BankCode")
    private String     bankCode     ;
    
	@JsonFormat(pattern ="dd/MM/yyyy")
	@JsonProperty("EffectiveDate")
	private Date effectiveDate;

	@JsonProperty("BankShortName")
    private String     bankShortName ;
	
	@JsonProperty("BankFullName")
    private String     bankFullName ;
	
	@JsonProperty("Status")
    private String     status ;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;

	@JsonProperty("AmendId")
	private Integer amendId;

	@JsonProperty("Remarks")
	private String remarks;

}
