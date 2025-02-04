/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2022-11-21 ( Date ISO 2022-11-21 - Time 15:19:50 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2022-11-21 ( 15:19:50 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.bean;


import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

import java.util.Date;
import jakarta.persistence.*;




/**
* Domain class for entity "BankMaster"
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
@IdClass(PremiaConfigDataMasterId.class)
@Table(name="eway_premia_config_data_master")


public class PremiaConfigDataMaster implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="PREMIA_ID", nullable=false)
    private Integer premiaId;

    @Id
    @Column(name="COLUMN_ID", nullable=false)
    private Integer columnId;    
    
    @Id
    @Column(name="COMPANY_ID", nullable=false, length=20)
    private String     companyId ;

    @Id
    @Column(name="PRODUCT_ID", nullable=false, length=20)
    private String     productId ;

    @Id
    @Column(name="SECTION_ID", nullable=false, length=20)
    private String    sectionId ;

    @Id
    @Column(name="BRANCH_CODE", nullable=false, length=20)
    private String     branchCode ;
        
    
    
    //--- ENTITY DATA FIELDS 

    
    @Column(name="COLUMN_NAME", length=100)
    private String   columnName ;

    @Column(name="DEFAULT_YN", length=1)
    private String defaultYn;

    @Column(name="DEFAULT_VALUE", length=100)
    private String defaultValue;
    
    @Column(name="CASE_CONDITION_YN", length=1)
    private String caseConditionYn;

    @Column(name="CASE_CONDITION", length=100)
    private String caseCondition;
        
    @Column(name="INPUT_COLUMN", length=100)
    private String    inputColumn;

    @Column(name="DATA_TYPE_ID", length=1)
    private String dataTypeId;

    @Column(name="DATA_TYPE_DESC", length=100)
    private String dataTypeDesc;
    
    @Column(name="DATE_FORMAT_TYPE", length=100)
    private String dataFormatType;
    
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;

    @Column(name="STATUS", length=1)
    private String     status ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_START", nullable=false)
    private Date       effectiveDateStart ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_END", nullable=false)
    private Date       effectiveDateEnd ;

    @Column(name="REMARKS", length=100)
    private String     remarks ;

    @Column(name="CREATED_BY", length=100)
    private String     createdBy ;

    @Column(name="UPDATED_BY", length=100)
    private String     updatedBy ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATED_DATE")
    private Date       updatedDate ;

    @Column(name="AMEND_ID", nullable=false)
    private Integer    amendId ;


}



