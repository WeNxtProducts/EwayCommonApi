/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2022-11-21 ( Date ISO 2022-11-21 - Time 15:19:59 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2022-11-21 ( 15:19:59 )
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

import java.math.BigDecimal;
import java.util.Date;
import jakarta.persistence.*;




/**
* Domain class for entity "CurrencyMaster"
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
@IdClass(CurrencyMasterId.class)
@Table(name="eway_currency_master")


public class CurrencyMaster implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="CURRENCY_ID", nullable=false)
    private String    currencyId ;

    @Id
    @Column(name="CURRENCY_SHORT_CODE", nullable=false, length=20)
    private String     currencyShortCode ;

    @Id
    @Column(name="AMEND_ID")
    private Integer    amendId ;

    @Id
    @Column(name="COMPANY_ID", length=20)
    private String     companyId;

    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_START", nullable=false)
    private Date       effectiveDateStart ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_END", nullable=false)
    private Date       effectiveDateEnd ;

    //--- ENTITY DATA FIELDS 
    @Column(name="CURRENCY_NAME", length=50)
    private String     currencyName ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;

    @Column(name="REMARKS", length=100)
    private String     remarks ;

    @Column(name="STATUS", length=1)
    private String     status ;

    @Column(name="DISPLAY_ORDER")
    private Integer    displayOrder ;

    @Column(name="SHORT_NAME", length=25)
    private String     shortName ;

    @Column(name="RFACTOR")
    private Integer       rfactor ;

    @Column(name="SUB_CURRENCY", length=10)
    private String     subCurrency ;

    @Column(name="EX_MINLMT")
    private BigDecimal exMinlmt ;

    @Column(name="EX_MAXLMT")
    private BigDecimal exMaxlmt ;

    @Column(name="CORE_APP_CODE", length=20)
    private String     coreAppCode ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATED_DATE", nullable=false)
    private Date       updatedDate;

    @Column(name="CREATED_BY", length=100)
    private String     createdBy;

    @Column(name="UPDATED_BY", length=100)
    private String    updatedBy;


    @Column(name="MIN_DISCOUNT", length=20)
    private String    minDiscount;

    @Column(name="MAX_LOADING", length=20)
    private String    maxLoading;
    
    @Column(name="DECIMAL_DIGIT")
    private Integer    decimalDigit;
    
    @Column(name="REGULATORY_CODE")
    private String regulatoryCode;
    
    @Column(name="CURRENCY_NAME_LOCAL", length=100)
    private String    currencyNameLocal;
    
}



