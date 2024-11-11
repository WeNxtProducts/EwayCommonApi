/*
 * Created on 2022-11-21 ( 15:20:07 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */
package com.maan.eway.bean;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;



/**
 * Composite primary key for entity "FactorRateMaster" ( stored in table "factor_rate_master" )
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
public class FactorRateMasterId implements Serializable {

    private static final long serialVersionUID = 1L;

    //--- ENTITY KEY ATTRIBUTES 
    private Integer    factorTypeId ;
    
    private Integer    sNo ;
    
    private Long     companyId ;
    
    private Integer    productId ;
    
    private String     branchCode ;
    
    private Long     agencyCode ;
    
    private Integer    sectionId ;
    
    private Integer    coverId ;
    
    private Integer    subCoverId ;
    
    private Integer    amendId ;

}
