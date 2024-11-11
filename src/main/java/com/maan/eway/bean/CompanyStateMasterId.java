/*
 * Created on 2022-11-21 ( 15:19:55 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */
package com.maan.eway.bean;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;


import java.util.Date;

/**
 * Composite primary key for entity "CompanyStateMaster" ( stored in table "company_state_master" )
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
public class CompanyStateMasterId implements Serializable {

    private static final long serialVersionUID = 1L;

    //--- ENTITY KEY ATTRIBUTES 
    private Integer    stateId ;
    
    private String     stateShortCode ;
    
    private String     countryId ;
    
    private String     companyId ;
    
    private String     regionCode ;
    
    private Date       effectiveDateStart ;
    
    private Date       effectiveDateEnd ;
    
     
}
