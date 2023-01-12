package com.maan.eway.notification.req;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.notification.req.statealgo.NotificationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
	@JsonProperty("Companyname")
	private String     companyName  ;
	@JsonProperty("Productname")
	private String     productName  ;
	@JsonProperty("Sectionname")
	private String     sectionName  ;
	@JsonProperty("Statusmessage")
	private String     statusMessage ;
	@JsonProperty("Otp")
	private Integer    otp;
	@JsonProperty("Policyno")
	private String policyNo     ;
	@JsonProperty("Quoteno")
	private String quoteNo      ;
	@JsonProperty("Notifdescription")
	private String     notifDescription ;
	@JsonProperty("Notifcationdate")
	private Date       notifcationDate ;	
	@JsonProperty("Notifpriority")
	private Integer    notifPriority ;
	@JsonProperty("Tinyurl")
	private String     tinyUrl      ;
	@JsonProperty("Notiftemplatename")
    private String     notifTemplatename ;
	@JsonProperty("Notifpushedstatus")
    private NotificationStatus notifPushedStatus ;
	
	@JsonProperty("Customer")
	private Customer customer;
	@JsonProperty("Broker")
	private Broker broker;
	@JsonProperty("UnderWriter")
	private UnderWriter underwriter;

}

