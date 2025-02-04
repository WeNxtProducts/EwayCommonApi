/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2022-12-15 ( Date ISO 2022-12-15 - Time 16:37:23 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2022-12-15 ( 16:37:23 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.bean;


import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;




/**
* Domain class for entity "TravelPassengerDetails"
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
@IdClass(TravelPassengerHistoryId.class)
@Table(name="travel_passenger_history")


public class TravelPassengerHistory implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="REQUEST_REFERENCE_NO", nullable=false, length=20)
    private String     requestReferenceNo ;

    @Id
    @Column(name="TRAVEL_ID", nullable=false)
    private Integer    travelId ;

    @Id
    @Column(name="PASSENGER_ID", nullable=false, length=20)
    private Integer     passengerId ;
    
    @Id
    @Column(name="GROUP_ID", nullable=false, length=20)
    private Integer     groupId ;
    
    @Id
    @Column(name="GROUP_COUNT", nullable=false, length=20)
    private Integer     groupCount ;


    @Column(name="GENDER_ID")
    private String    genderId ;

    @Id
    @Column(name="CUSTOMER_REFERENCE_NO", nullable=false, length=20)
    private String     customerReferenceNo ;

    @Id
    @Column(name="COMPANY_ID", nullable=false, length=20)
    private String     companyId ;

    @Id
    @Column(name="BRANCH_CODE", nullable=false, length=20)
    private String     branchCode ;

    @Id
    @Column(name="PRODUCT_ID", nullable=false)
    private Integer    productId ;

    @Id
    @Column(name="SECTION_ID", nullable=false)
    private Integer    sectionId ;
    
    @Id
    @Column(name="CUSTOMER_ID", length=20)
    private String     customerId ;

    @Id
    @Column(name="QUOTE_NO", length=20)
    private String     quoteNo ;

    //--- ENTITY DATA FIELDS 
    @Column(name="NAME_TITLE_DESC", length=10)
    private String     nameTitleDesc ;

    @Column(name="NAME_TITLE_ID")
    private Integer    nameTitleId ;

    @Column(name="PASSENGER_NAME", length=120)
    private String     passengerName ;
    
    @Column(name="PASSENGER_FIRST_NAME", length=120)
    private String     passengerFirstName ;
    
    @Column(name="PASSENGER_LAST_NAME", length=120)
    private String     passengerLastName ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DOB")
    private Date       dob ;

    @Column(name="GENDER_DESC", length=10)
    private String     genderDesc ;

    @Column(name="AGE")
    private Integer    age ;

    @Column(name="RELATION_ID")
    private Integer    relationId ;

    @Column(name="RELATION_DESC", length=30)
    private String     relationDesc ;

    @Column(name="NATIONALITY", length=50)
    private String     nationality ;
    
    @Column(name="NATIONALITY_DESC", length=50)
    private String     nationalityDesc ;

    @Column(name="COVER_TYPE", length=10)
    private String     coverType ;

    @Column(name="PASSPORT_NO", length=20)
    private String     passportNo ;

    @Column(name="CIVIL_ID", length=15)
    private String     civilId ;

    @Column(name="LOGIN_ID", length=100)
    private String     loginId ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;

    @Column(name="STATUS", length=15)
    private String     status ;

    @Column(name="ADDRESS1", length=500)
    private String     address1 ;

    @Column(name="ADDRESS2", length=500)
    private String     address2 ;

    @Column(name="POBOX", length=100)
    private String     pobox ;

   
    @Column(name="TRAVEL_COVER_ID", nullable=false)
    private Integer    travelCoverId ;

    @Column(name="COMPANY_NAME", length=100)
    private String     companyName ;

    @Column(name="PRODUCT_NAME", length=100)
    private String     productName ;

    @Column(name="SECTION_NAME", length=100)
    private String     sectionName ;

    @Column(name="CURRENCY", length=20)
    private String     currency ;

    @Column(name="EXCHANGE_RATE")
    private Double     exchangeRate ;

    @Column(name="TRAVEL_COVER_DESC", length=250)
    private String     travelCoverDesc ;

    @Column(name="SOURCE_COUNTRY", length=50)
    private String     sourceCountry ;

    @Column(name="SOURCE_COUNTRY_DESC", length=200)
    private String     sourceCountryDesc ;

    @Column(name="DESTINATION_COUNTRY", length=50)
    private String     destinationCountry ;

    @Column(name="DESTINATION_COUNTRY_DESC", length=200)
    private String     destinationCountryDesc ;

    @Column(name="SPORTS_COVER_YN", length=20)
    private String     sportsCoverYn ;

    @Column(name="TERRORISM_COVER_YN", length=20)
    private String     terrorismCoverYn ;

    @Column(name="PLAN_TYPE_ID")
    private Integer    planTypeId ;

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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE")
    private Date       effectiveDate ;

    @Column(name="CREATED_BY", length=100)
    private String     createdBy ;

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

    @Column(name="ADMIN_LOGIN_ID", length=100)
    private String     adminLoginId ;

    @Column(name="ADMIN_REMARKS", length=100)
    private String     adminRemarks ;

    @Column(name="REJECT_REASON", length=100)
    private String     rejectReason ;

    @Column(name="REFERAL_REMARKS", length=100)
    private String     referalRemarks ;

    @Column(name="BDM_CODE", length=20)
    private String     bdmCode ;

    @Column(name="SOURCE_TYPE", length=20)
    private String     sourceType ;

    @Column(name="CUSTOMER_CODE", length=100)
    private String     customerCode ;

    @Column(name="BROKER_BRANCH_CODE", length=20)
    private String     brokerBranchCode ;

    @Column(name="BROKER_BRANCH_NAME", length=100)
    private String     brokerBranchName ;

    @Column(name="COMMISSION_TYPE", length=20)
    private String     commissionType ;

    @Column(name="COMMISSION_TYPE_DESC", length=100)
    private String     commissionTypeDesc ;

  
    @Column(name="VD_REFNO", length=20)
    private String     vdRefno ;

    @Column(name="CD_REFNO", length=20)
    private String     cdRefno ;

    @Column(name="MS_REFNO", length=20)
    private String     msRefno ;

    
    @Column(name="LOCATION_ID")
    private Integer    locationId ;
}



