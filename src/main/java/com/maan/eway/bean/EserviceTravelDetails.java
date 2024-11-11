/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2022-11-30 ( Date ISO 2022-11-30 - Time 17:06:34 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2022-11-30 ( 17:06:34 )
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
* Domain class for entity "EserviceTravelDetails"
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
@IdClass(EserviceTravelDetailsId.class)
@Table(name="eservice_travel_details")


public class EserviceTravelDetails implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="REQUEST_REFERENCE_NO", nullable=false, length=20)
    private String     requestReferenceNo ;

    @Id
    @Column(name="CUSTOMER_REFERENCE_NO", nullable=false, length=20)
    private String     customerReferenceNo ;

    @Id
    @Column(name="RISK_ID", nullable=false)
    private Integer    riskId ;

    @Id
    @Column(name="COMPANY_ID", nullable=false, length=20)
    private String     companyId ;

    @Id
    @Column(name="BRANCH_CODE", nullable=false, length=20)
    private String     branchCode ;
    
    
    @Column(name = "LOCATION_ID", length = 20)
	private String locationId;

    //--- ENTITY DATA FIELDS 
    @Column(name="TRAVEL_COVER_ID")
    private Integer    travelCoverId ;

    @Column(name="TRAVEL_COVER_DESC", length=250)
    private String     travelCoverDesc ;
    
    @Column(name="SECTION_ID", nullable=false, length=20)
    private String     sectionId ;

    @Column(name="PRODUCT_ID", nullable=false, length=20)
    private String     productId ;

    @Column(name="POLICY_NO", length=100)
    private String     policyNo;

    
    @Column(name="SOURCE_COUNTRY", length=50)
    private String     sourceCountry ;

    @Column(name="DESTINATION_COUNTRY", length=50)
    private String     destinationCountry ;

    @Column(name="SPORTS_COVER_YN", length=20)
    private String     sportsCoverYn ;

    @Column(name="TERRORISM_COVER_YN", length=20)
    private String     terrorismCoverYn ;

    @Column(name="PLAN_TYPE_ID")
    private Integer    planTypeId ;
    
    @Column(name="CURRENCY")
    private String    currency ;

    @Column(name="EXCHANGE_RATE")
    private BigDecimal     exchangeRate ;
    
    @Column(name="PLAN_TYPE_DESC", length=50)
    private String     planTypeDesc ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="TRAVEL_START_DATE")
    private Date       travelStartDate ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="TRAVEL_END_DATE")
    private Date       travelEndDate ;

    @Column(name="TRAVEL_COVER_DURATION")
    private Integer    travelCoverDuration ;

    @Column(name="TOTAL_PASSENGERS")
    private Integer    totalPassengers ;

    @Column(name="TOTAL_PREMIUM")
    private BigDecimal     totalPremium ;

    @Column(name="AGE")
    private Integer    age ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE")
    private Date       effectiveDate ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;

    @Column(name="CREATED_BY", length=100)
    private String     createdBy ;

    @Column(name="STATUS", length=1)
    private String     status ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATED_DATE")
    private Date       updatedDate ;

    @Column(name="UPDATED_BY", length=100)
    private String     updatedBy ;

    @Column(name="REMARKS", length=100)
    private String     remarks ;

    @Column(name="HAVEPROMOCODE", length=10)
    private String     havepromocode ;

    @Column(name="PROMOCODE", length=100)
    private String     promocode ;

    @Column(name="COVID_COVER_YN", length=20)
    private String     covidCoverYn ;

    @Column(name="AC_EXECUTIVE_ID")
    private Integer    acExecutiveId ;

    @Column(name="APPLICATION_ID", length=20)
    private String     applicationId ;

    @Column(name="BROKER_CODE", length=20)
    private String     brokerCode ;

    @Column(name="SUB_USER_TYPE", length=20)
    private String     subUserType ;

    @Column(name="LOGIN_ID", length=100)
    private String     loginId ;

    @Column(name="CUSTOMER_ID", length=20)
    private String     customerId ;

    @Column(name="QUOTE_NO", length=20)
    private String     quoteNo ;

    @Column(name="ADMIN_LOGIN_ID", length=100)
    private String     adminLoginId ;

    @Column(name="ADMIN_REMARKS", length=1000)
    private String     adminRemarks ;

    @Column(name="REJECT_REASON", length=1000)
    private String     rejectReason ;

    @Column(name="REFERAL_REMARKS", length=1000)
    private String     referalRemarks ;

    @Column(name="BDM_CODE", length=20)
    private String     bdmCode ;

    @Column(name="SOURCE_TYPE", length=20)
    private String     sourceType ;

    @Column(name="CUSTOMER_CODE", length=100)
    private String     customerCode ;


    @Column(name="BROKER_BRANCH_NAME", length=20)
    private String     brokerBranchName ;

    @Column(name="BROKER_BRANCH_CODE", length=20)
    private String     brokerBranchCode ;


    @Column(name="COMPANY_NAME", length=100)
    private String     companyName ;

    @Column(name="PRODUCT_NAME", length=100)
    private String     productName ;
    
    @Column(name="SECTION_NAME", length=100)
    private String     sectionName ;
    
    @Column(name="COMMISSION_TYPE", length=20)
    private String     commissionType ;
    
    @Column(name="COMMISSION_TYPE_DESC", length=20)
    private String     commissionTypeDesc ;
    
    
    @Column(name="SOURCE_COUNTRY_DESC", length=20)
    private String     sourceCountryDesc ;
    
    @Column(name="DESTINATION_COUNTRY_DESC", length=20)
    private String     destinationCountryDesc ;
    
    @Column(name="ACTUAL_PREMIUM_LC")
    private BigDecimal     actualPremiumLc ;

    @Column(name="ACTUAL_PREMIUM_FC")
    private BigDecimal     actualPremiumFc ;
    
    @Column(name="OVERALL_PREMIUM_LC")
    private BigDecimal     overallPremiumLc ;

    @Column(name="OVERALL_PREMIUM_FC")
    private BigDecimal     overallPremiumFc ;
    
    @Column(name="OLD_REQ_REF_NO", length=20)
    private String     oldReqRefNo ;


    @Column(name="BANK_CODE", length=100)
    private String   bankCode;
    
    @Column(name="MANUAL_REFERAL_YN", length=100)
    private String  manualReferalYn;
    
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
    
    
    @Column(name="ENDT_CATEG_DESC", length=10)
    private String     endtCategDesc ;

    @Column(name="ENDT_PREMIUM")
    private Double       endtPremium ;
    
    @Column(name="COMMISSION_PERCENTAGE")
    private BigDecimal commissionPercentage;
    
    @Column(name="VAT_COMMISSION")
    private BigDecimal vatCommission;
    
    @Column(name="TIRA_COVER_NOTE_NO")
    private String tiraCoverNoteNo;
    
    @Column(name="VD_REFNO")
    private Integer     vdRefNo ;
    
    @Column(name="CD_REFNO")
    private Integer     cdRefno;
    
    @Column(name="MS_REFNO")
    private Integer     msRefno ;
    
    
    @Column(name="CUSTOMER_NAME")
    private String     customerName ;
    
    @Column(name="AGENCY_CODE")
    private String     agencyCode ;
    
    @Column(name="BRANCH_NAME ")
    private String     branchName  ;

    @Column(name="SALE_POINT_CODE", length=200)
    private String    salePointCode;

    
    @Column(name="FINALIZE_YN")
    private String finalizeYn;
    
    @Column(name="BROKER_TIRA_CODE")
    private String brokerTiraCode;
    
    @Column(name="SOURCE_TYPE_ID")
    private String sourceTypeId;

    
    @Column(name="EMI_YN", length=20)
    private String     emiYn;

    @Column(name="INSTALLMENT_PERIOD")
    private Integer     installmentPeriod ;
    
    @Column(name="NO_OF_INSTALLMENT")
    private Integer     noOfInstallment ;

    @Column(name="EMI_PREMIUM")
    private BigDecimal     emiPremium ;

    
    
    @Column(name="VAT_PREMIUM")
    private BigDecimal vatPremium;
    
    @Column(name="ENDT_VAT_PREMIUM")
    private BigDecimal endtVatPremium;
    
    

    @Column(name="RENEWAL_DATE_YN", length=20)
    private String renewalDateYn;
}



