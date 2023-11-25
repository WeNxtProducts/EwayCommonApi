package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data

public class PaymentCashRes {
	
	@JsonProperty("PayeeName")
	 private String payeeName;
   

}
