/*
 * Java domain class for entity "SectionMaster" 
 * Created on 2022-09-02 ( Date ISO 2022-09-02 - Time 18:14:54 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */
package com.maan.eway.master.req;

import java.io.Serializable;

import lombok.*;
import java.util.Date;


import java.util.Date;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * Domain class for entity "SectionMaster"
 *
 * @author Telosys Tools Generator
 *
 */
 
 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SectionMasterSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;
  
	@JsonProperty("SectionId")
    private String    sectionId    ;
	
	@JsonProperty("SectionName")
	private String sectionName;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
    @JsonProperty("CreatedBy")
    private String    createdBy    ;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("Remarks")
	private String remarks;

}
