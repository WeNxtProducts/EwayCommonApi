package com.maan.eway.notif.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

import lombok.Data;

@Data
public class NotifTemplateMasterGetReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("InsuranceId")
    private String companyId;
   
	@JsonProperty("NotificationApplicable")
	private String notificationApplicable;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
    
}
