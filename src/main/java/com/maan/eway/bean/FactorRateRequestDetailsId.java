/*
 * Created on 2022-11-21 ( 15:20:07 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */
package com.maan.eway.bean;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;



/**
 * Composite primary key for entity "FactorRateRequestDetails" ( stored in table "factor_rate_request_details" )
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
public class FactorRateRequestDetailsId implements Serializable {

    private static final long serialVersionUID = 1L;

    //--- ENTITY KEY ATTRIBUTES 
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
    
    private Integer    discountCoverId;
    
    private BigDecimal     endtCount ;
    
    private Integer     locationId ;
    
     
}
