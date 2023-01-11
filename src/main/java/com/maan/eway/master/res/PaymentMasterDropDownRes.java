package com.maan.eway.master.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data

public class PaymentMasterDropDownRes {

	
	
	@JsonProperty("CashYn")
	private String cashYn;

	@JsonProperty("ChequeYn")
	private String chequeYn;
	
	@JsonProperty("CreditYn")
	private String creditYn;
	
		
}
