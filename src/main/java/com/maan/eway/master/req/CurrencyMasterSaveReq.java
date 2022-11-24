package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CurrencyMasterSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("CurrencyId")
	private String currencyId;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDate")
	private Date effectiveDate;

	@JsonProperty("Sno")
	private String sno;

	@JsonProperty("CurrencyName")
	private String currencyName;

	@JsonProperty("CurrencyShortCode")
	private String currencyShortCode;

	@JsonProperty("CurrencyShortName")
	private String currencyShortName;
	
/*	@JsonProperty("Rfactor")
	private String rfactor; */

	@JsonProperty("SubCurrency")
	private String subCurrency;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("ExMinlmt")
	private String exMinlmt;
	
	@JsonProperty("ExMaxlmt")
	private String exMaxlmt;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;

	@JsonProperty("AmendId")
	private Integer amendId;

	@JsonProperty("Remarks")
	private String remarks;
}
