package com.maan.eway.notif.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

import lombok.Data;

@Data
public class MailMasterGetReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("Sno")
    private String sNo;
    
    @JsonProperty("InsuranceId")
    private String companyId;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
   
    
}
