/* 


*  
*  
*  Copyright (c) 2019. All right reserved
 * Created on 2022-11-21 ( Date ISO 2022-11-21 - Time 15:20:05 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2022-11-21 ( 15:20:05 )
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
* Domain class for entity "ExchangeMaster"
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
@IdClass(ExchangeMasterId.class)
@Table(name="eway_exchange_master")


public class ExchangeMaster implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="S_NO", nullable=false)
    private Integer    sNo ;

    @Id
    @Column(name="EXCHANGE_ID", nullable=false)
    private Integer    exchangeId ;

    @Id
    @Column(name="COMPANY_ID", nullable=false, length=20)
    private String     companyId ;

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
    @Column(name="EXCHANGE_RATE")
    private Double     exchangeRate ;

    @Column(name="CURRENCY_ID", length=20)
    private String     currencyId ;

    @Column(name="CURRENCY_NAME", length=100)
    private String     currencyName;

    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;

    @Column(name="REMARKS", length=100)
    private String     remarks ;

    @Column(name="STATUS", length=1)
    private String     status ;


    @Column(name="CREATED_BY", length=100)
    private String     createdBy;

    @Column(name="UPDATED_BY", length=100)
    private String     updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATED_DATE")
    private Date       updatedDate ;


    @Column(name="CORE_APP_CODE", length=20)
    private String  coreAppCode;
    
    
    @Column(name="CURRENCY_NAME_LOCAL", length=100)
    private String  currencyNameLocal;

}



