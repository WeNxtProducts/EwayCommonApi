/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2022-11-21 ( Date ISO 2022-11-21 - Time 15:20:07 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2022-11-21 ( 15:20:07 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.bean;


import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import java.util.Date;
import jakarta.persistence.*;




/**
* Domain class for entity "FactorRateMaster"
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
@IdClass(FactorRateMasterId.class)
@Table(name="factor_rate_master" ,indexes = {
       /* @Index(name = "INDX_CONDITION1" , columnList = "factorTypeId,companyId,productId,branchCode,agencyCode,sectionId,coverId,effectiveDateStart,effectiveDateEnd,status"),
        @Index(name = "INDX_CONDITION2" , columnList = "factorTypeId,companyId,productId,branchCode,agencyCode,sectionId,coverId,subCoverId,effectiveDateStart,effectiveDateEnd,status"),
        @Index(name = "INDX_DISCRETE1" , columnList = "factorTypeId,param9,param10,param11,param12"),
        @Index(name = "INDX_DISCRETE2" , columnList = "param13,param14,param15,param16,param17,param18,param19,param20"),
        @Index(name = "Indx_eagle" , columnList = "factorTypeId,companyId,productId,coverId,subCoverId,effectiveDateStart,effectiveDateEnd,param1,param2,param3,param4,param5,param6"),
        @Index(name = "Indx_eagle_1" , columnList = "factorTypeId,companyId,productId,agencyCode,sectionId,coverId,subCoverId,effectiveDateStart,effectiveDateEnd,param1,param2,param3,param4,param5,param6,param9"),
        @Index(name = "INDX_EFFDATE" , columnList = "effectiveDateStart"),
        @Index(name = "INDX_EFFDATE1" , columnList = "effectiveDateEnd"),
        @Index(name = "INDX_PARAM" , columnList = "param1,param2,param3,param4,param5,param6,param9,param10,param11,param12"),
        @Index(name = "INDX_PARAM2" , columnList = "param13,param14,param15,param16,param17,param18,param19,param20,param21,param22,param23,param24,param25,param26,param27,param28"),
        @Index(name = "Indx_sanlam_ivory" , columnList = "companyId,productId,agencyCode,sectionId,coverId,subCoverId,effectiveDateStart,effectiveDateEnd,status,param3,param4,param10,param12,param15,factorTypeId"),
        @Index(name = "Indx_sanlam_ivory2" , columnList = "factorTypeId,companyId,productId,agencyCode,sectionId,coverId,subCoverId,effectiveDateStart,effectiveDateEnd,status,param3,param4"),
        @Index(name = "Indx_sanlam_ivory3" , columnList = "factorTypeId,companyId,productId,sectionId,coverId,subCoverId,effectiveDateStart,effectiveDateEnd,status,param3,param4"),
        @Index(name = "INDX_STATUS" , columnList = "status"),       */ 
        @Index(name = "trubleshoot_1" , columnList = "companyId,productId,sectionId,status,coverId,subCoverId,effectiveDateStart,effectiveDateEnd,factorTypeId,param5,param6,param1,param2,param3,param4"),
        @Index(name="_1",columnList = "companyId,productId,sectionId,status,coverId,subCoverId,effectiveDateStart,effectiveDateEnd,param9"),
        @Index(name="_2",columnList = "companyId,productId,sectionId,status,coverId,subCoverId,effectiveDateStart,effectiveDateEnd,param1,param2"),
        @Index(name="_3",columnList = "companyId,productId,sectionId,status,coverId,subCoverId,effectiveDateStart,effectiveDateEnd,param21,param22"),
        @Index(name="_4",columnList = "companyId,productId,sectionId,status,coverId,subCoverId,effectiveDateStart,effectiveDateEnd,param9,param10"),
} )


public class FactorRateMaster implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="FACTOR_TYPE_ID", nullable=false)
    private Integer    factorTypeId ;

    @Id
    @Column(name="S_NO", nullable=false)
    private Integer    sNo ;

    @Id
    @Column(name="COMPANY_ID", nullable=false, length=100)
    private Long     companyId ;

    @Id
    @Column(name="PRODUCT_ID", nullable=false)
    private Integer    productId ;

    @Id
    @Column(name="BRANCH_CODE", nullable=false, length=20)
    private String     branchCode ;

    @Id
    @Column(name="AGENCY_CODE", nullable=false, length=20)
    private Long     agencyCode ;

    @Id
    @Column(name="SECTION_ID", nullable=false)
    private Integer    sectionId ;

    @Id
    @Column(name="COVER_ID", nullable=false)
    private Integer    coverId ;

    @Id
    @Column(name="SUB_COVER_ID", nullable=false)
    private Integer    subCoverId ;

    @Id
    @Column(name="AMEND_ID", nullable=false)
    private Integer    amendId ;

    //--- ENTITY DATA FIELDS 
    @Column(name="FACTOR_TYPE_NAME", length=100)
    private String     factorTypeName ;

    @Column(name="FACTOR_TYPE_DESC", length=200)
    private String     factorTypeDesc ;

    @Column(name="CREATED_BY", length=100)
    private String     createdBy ;

    @Column(name="COVER_NAME", length=100)
    private String     coverName ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;

    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_START", nullable=false)
    private Date       effectiveDateStart ;

    @Column(name="COVER_DESC", length=200)
    private String     coverDesc ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_END", nullable=false)
    private Date       effectiveDateEnd ;

    @Column(name="SUB_COVER_NAME", length=100)
    private String     subCoverName ;

    @Column(name="SUB_COVER_DESC", length=200)
    private String     subCoverDesc ;

    @Column(name="STATUS", length=1)
    private String     status ;

    @Column(name="REMARKS", length=100)
    private String     remarks ;

    @Column(name="PARAM_1")
    private BigDecimal     param1 ;

    @Column(name="PARAM_2")
    private BigDecimal     param2 ;

    @Column(name="PARAM_3")
    private BigDecimal     param3 ;

    @Column(name="PARAM_4")
    private BigDecimal     param4 ;

    @Column(name="PARAM_5")
    private BigDecimal     param5 ;

    @Column(name="PARAM_6")
    private BigDecimal     param6 ;

    @Column(name="PARAM_7")
    private BigDecimal     param7 ;

    @Column(name="PARAM_8")
    private BigDecimal     param8 ;

    @Column(name="PARAM_9", length=100)
    private String     param9 ;

    @Column(name="PARAM_10", length=100)
    private String     param10 ;

    @Column(name="PARAM_11", length=100)
    private String     param11 ;

    @Column(name="PARAM_12", length=100)
    private String     param12 ;

        
    @Column(name="UPDATED_BY", length=100)
    private String     updatedBy ;
   
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATED_DATE")
    private Date updatedDate ;
 
        
    @Column(name="RATE")
    private BigDecimal     rate ;

    @Column(name="CALC_TYPE", length=1)
    private String     calcType ;

    @Column(name="CALC_TYPE_DESC", length=100)
    private String     calcTypeDesc ;

    @Column(name="MIN_PREMIUM")
    private BigDecimal     minPremium ;

    @Column(name="REGULATORY_CODE", length=20)
    private String     regulatoryCode ;

    @Column(name="CORE_APP_CODE", length=20)
    private String     coreAppCode ;

    @Column(name="MASTER_YN", length=20)
    private String     masterYn;
    
    @Column(name="API_URL", length=20)
    private String     apiUrl;
    
    @Column(name="PARAM_13", length=100)
    private String     param13 ;
    
    @Column(name="PARAM_14", length=100)
    private String     param14 ;
    
    @Column(name="PARAM_15", length=100)
    private String     param15 ;
    
    @Column(name="PARAM_16", length=100)
    private String     param16 ;
    
    @Column(name="PARAM_17", length=100)
    private String     param17 ;
    
    @Column(name="PARAM_18", length=100)
    private String     param18 ;
    
    @Column(name="PARAM_19", length=100)
    private String     param19 ;
    
    @Column(name="PARAM_20", length=100)
    private String     param20 ;
    
    @Column(name="PARAM_21")
    private BigDecimal     param21 ;
    
    @Column(name="PARAM_22")
    private BigDecimal     param22 ;
    
    @Column(name="PARAM_23")
    private BigDecimal     param23 ;
    
    @Column(name="PARAM_24")
    private BigDecimal     param24 ;
    
    @Column(name="PARAM_25")
    private BigDecimal     param25 ;
    
    @Column(name="PARAM_26")
    private BigDecimal     param26 ;
    
    @Column(name="PARAM_27")
    private BigDecimal     param27;
   
    @Column(name="PARAM_28")
    private BigDecimal     param28;
   
    @Column(name="EXCESS_AMOUNT")
    private BigDecimal     excessAmount ;
    
    @Column(name="EXCESS_PERCENT")
    private BigDecimal     excessPercent ;
    
    @Column(name="EXCESS_DESC")
    private String     excessDesc ;
    
    @Column(name="MINIMUM_RATE")
    private BigDecimal     minimumRate ;
}



