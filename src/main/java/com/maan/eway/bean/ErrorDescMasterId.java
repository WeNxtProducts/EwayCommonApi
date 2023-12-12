package com.maan.eway.bean;

import java.io.Serializable;

import io.swagger.models.parameters.SerializableParameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ErrorDescMasterId implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String errorCode;
	
	private Integer amendId;
	
	private String productId;

}
