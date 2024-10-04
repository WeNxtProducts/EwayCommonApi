package com.maan.eway.workflow.dto;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.JsonElement;
import com.maan.eway.bean.FlowFieldDetails;

import jakarta.persistence.Column;
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
@ToString
@Builder
public class JsonField  {

	private BigDecimal companyId ;

	private BigDecimal productId ;

	private BigDecimal keyId ;

	private String     jsonKey ;

	private String     isHeader ;

	private BigDecimal     headerKeyid ;

	private String     isarray ;

	private String     datatype ;

	private String     pattern ;

	private String     defaultYn ;

	private String     defaultValue ;

	private String     status ;
	
	private BigDecimal queryId ;
	 
	private String queryCol ;
	private String queryAlias;
	private List<JsonField> childField;
	

	 
}
