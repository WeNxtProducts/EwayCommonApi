/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2022-10-27 ( Date ISO 2022-10-27 - Time 12:39:24 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2022-10-27 ( 12:39:24 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.bean;


import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import java.util.Date;
import jakarta.persistence.*;




/**
* Domain class for entity "MsVehicleDetails"
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
@IdClass(PolicyDiscountLoadingCoversId.class)
@Table(name="policy_discount_loading_covers")


public class PolicyDiscountLoadingCovers implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="REQUEST_REFERENCE_NO", nullable=false, length=10)
    private String     requestReferenceNo ;

    @Id
    @Column(name="PD_REFNO", nullable=false)
    private Long       pdRefno ;

  
    @Column(name="ENDT_TYPE_ID")
    private Integer    endtTypeId ;
   
    @Column(name="ENDT_CATEGORY_ID")
    private String    endtCategoryId ;
    
    @Column(name="PERIOD_OF_INSURANCE", nullable=false, length=10)
    private String     periodOfInsurance ;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE", nullable=false)
    private Date       entryDate ;

   @Column(name="NO_OF_VEHICLES", nullable=false)
    private Integer    noOfVehicles ;

    @Column(name="GROUP_COUNT")
    private Integer groupCount;
    
    @Column(name="HAVEPROMOCODE", length=10)
    private String     havepromocode ;

    @Column(name="PROMOCODE", length=100)
    private String     promocode ;
    
    @Column(name="STATUS", length=10)
    private String     status ;
    
    @Column(name="CURRENCY", nullable=false)
    private String    currency ;

    @Column(name="EXCHANGE_RATE", nullable=false)
    private BigDecimal     exchangeRate ;
   
}



