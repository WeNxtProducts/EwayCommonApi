/*
 * Created on 2022-11-21 ( 15:20:28 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */
package com.maan.eway.bean;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;


import java.util.Date;

/**
 * Composite primary key for entity "RegionMaster" ( stored in table "region_master" )
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
public class PtIntgFlexTranId implements Serializable {

    private static final long serialVersionUID = 1L;

    //--- ENTITY KEY ATTRIBUTES 
    private String     piftTranSysId ;
    
    private String     piftPolicyNo ;
    
    private String    piftLevel ;
    
     
}
