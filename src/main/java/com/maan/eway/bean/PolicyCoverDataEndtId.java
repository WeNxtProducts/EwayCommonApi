/*
 * Created on 2023-08-22 ( 11:27:54 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */
package com.maan.eway.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;



/**
 * Composite primary key for entity "PolicyCoverDataEndt" ( stored in table "policy_cover_data_endt" )
 *
 * @author Telosys
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PolicyCoverDataEndtId implements Serializable {

    private static final long serialVersionUID = 1L;

    //--- ENTITY KEY ATTRIBUTES 
    private String     policyNo ;
    
    private String     quoteNo ;
    
    private String     requestReferenceNo ;
    
    private Integer    vehicleId ;
    
    private String     companyId ;
    
    private Integer    productId ;
    
    private Integer    sectionId ;
    
    private Integer    coverId ;
    
    private String     subCoverYn ;
    
    private Integer    subCoverId ;
    
    private Integer    discLoadId ;
    
    private Integer    taxId ;
    
    private BigDecimal     endtCount ;
    
    private Integer    discountCoverId ;
    
    private Integer    individualId ;
    
     
}
