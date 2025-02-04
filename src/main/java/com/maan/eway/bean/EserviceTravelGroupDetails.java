/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2022-11-19 ( Date ISO 2022-11-19 - Time 13:30:10 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2022-11-19 ( 13:30:10 )
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
* Domain class for entity "EserviceBuildingDetails"
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
@IdClass(EserviceTravelGroupDetailsId.class)
@Table(name="eservice_travel_group_details")


public class EserviceTravelGroupDetails implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="REQUEST_REFERENCE_NO", nullable=false, length=20)
    private String     requestReferenceNo ;

    @Id
    @Column(name="TRAVEL_ID", nullable=false)
    private Integer    travelId ;

    @Id
    @Column(name="GROUP_ID")
    private Integer    groupId ;

    @Column(name="POLICY_NO", length=100)
    private String     policyNo;

    
    @Column(name="GROUP_MEMBERS")
    private Integer    grouppMembers ;

    @Column(name="COMPANY_ID", length=20)
    private String     companyId ;

    @Column(name="BRANCH_CODE", length=20)
    private String     branchCode ;

    @Column(name="PRODUCT_ID", length=20)
    private String     productId ;
    
    @Column(name="GROUP_DESC", length=20)
    private String    groupDesc ;

    @Column(name="STARTT")
    private Integer    startt ;

    @Column(name="END")
    private Integer    end;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;

    @Column(name="CREATED_BY", length=100)
    private String     createdBy ;

    @Column(name="STATUS", length=2)
    private String     status ;

    @Column(name="ACTUAL_PREMIUM_LC")
    private BigDecimal     actualPremiumLc ;

    @Column(name="ACTUAL_PREMIUM_FC")
    private BigDecimal     actualPremiumFc ;
    
    @Column(name="OVERALL_PREMIUM_LC")
    private BigDecimal     overallPremiumLc ;

    @Column(name="OVERALL_PREMIUM_FC")
    private BigDecimal     overallPremiumFc ;

    @Column(name="CUSTOMER_ID", length=20)
    private String     customerId ;
    
    

    @Column(name="QUOTE_NO", length=20)
    private String     quoteNo ;
    
    @Column(name="ENDORSEMENT_TYPE")
   private Integer    endorsementType ;

   @Column(name="ENDORSEMENT_TYPE_DESC", length=100)
   private String     endorsementTypeDesc ;

 @Temporal(TemporalType.TIMESTAMP)
   @Column(name="ENDORSEMENT_DATE")
   private Date       endorsementDate ;

   @Column(name="ENDORSEMENT_REMARKS", length=500)
   private String     endorsementRemarks ;

   @Temporal(TemporalType.TIMESTAMP)
   @Column(name="ENDORSEMENT_EFFDATE")
   private Date       endorsementEffdate ;

   @Column(name="ORIGINAL_POLICY_NO", length=500)
   private String     originalPolicyNo ;

   @Column(name="ENDT_PREV_POLICY_NO", length=500)
   private String     endtPrevPolicyNo ;

   @Column(name="ENDT_PREV_QUOTE_NO", length=500)
   private String     endtPrevQuoteNo ;

   @Column(name="ENDT_COUNT")
   private BigDecimal endtCount ;

   @Column(name="ENDT_STATUS", length=10)
   private String     endtStatus ;
   
   @Column(name="IS_FINYN", length=10)
   private String     isFinaceYn ;
   
   
   @Column(name="ENDT_CATEG_DESC", length=100)
   private String     endtCategDesc ;


   @Column(name="RISK_ID")
   private Integer    riskId ;
   
   @Column(name="SECTION_ID")
   private Integer    sectionId;
   
   @Column(name="SECTION_DESC")
   private String    sectionDesc ;
   
   @Column(name="RATING_RELATION_ID")
   private String ratingRelationId;

   @Column(name="RATING_RELATION_DESC")
   private String ratingRelationDesc;
   
   @Column(name="NICK_NAME")
   private String nickName;


}



