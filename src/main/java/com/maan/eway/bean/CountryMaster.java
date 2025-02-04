/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2022-11-21 ( Date ISO 2022-11-21 - Time 15:19:56 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2022-11-21 ( 15:19:56 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.bean;


import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import jakarta.persistence.Table;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

import java.util.Date;
import jakarta.persistence.*;




/**
* Domain class for entity "CountryMaster"
*
* @author Telosys Tools Generator
*
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@IdClass(CountryMasterId.class)
@Table(name="eway_country_master")


public class CountryMaster implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
	@Id
	@Column(name="COMPANY_ID", nullable=false)
	private String    companyId ;
	
    @Id
    @Column(name="COUNTRY_ID", nullable=false)
    private String    countryId ;

    @Id
    @Column(name="COUNTRY_SHORT_CODE", nullable=false, length=20)
    private String     countryShortCode ;

    @Id
    @Column(name="AMEND_ID", nullable=false)
    private Integer    amendId ;

    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_START", nullable=false)
    private Date       effectiveDateStart ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_END", nullable=false)
    private Date       effectiveDateEnd ;

    //--- ENTITY DATA FIELDS 
    @Column(name="COUNTRY_NAME", length=50)
    private String     countryName ;

    @Column(name="MOBILE_CODE", length=100)
    private String     mobileCode ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;

    @Column(name="STATUS", length=1)
    private String     status ;

    @Column(name="CORE_APP_CODE", length=20)
    private String     coreAppCode ;


    @Column(name="REMARKS", length=100)
    private String     remarks ;

    @Column(name="CREATED_BY", nullable=false, length=100)
    private String     createdBy ;

    @Column(name="TIRA_CODE", length=20)
    private String     tiraCode ;

    @Column(name="REGULATORY_CODE", nullable=false, length=20)
    private String     regulatoryCode ;


    @Column(name="UPDATED_BY", length=100)
    private String     updatedBy ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATED_DATE")
    private Date       updatedDate ;

    @Column(name="PLAN_ID", length=100)
    private String     planId;

    @Column(name="PLAN_DESC", length=100)
    private String     planDesc;
    
    @Column(name="NATIONALITY", length=100)
    private String     nationality;
    
    //Local Language 
    
    @Column(name="COUNTRY_NAME_LOCAL", length=100)
    private String   countryNameLocal ;
    
    @Column(name="PLAN_DESC_LOCAL", length=100)
    private String   planDescLocal ;
    
    @Column(name="NATIONALITY_LOCAL", length=100)
    private String   nationalityLocal ;

}



