/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2022-11-21 ( Date ISO 2022-11-21 - Time 15:20:31 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2022-11-21 ( 15:20:31 )
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
* Domain class for entity "SmsConfigMaster"
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
@IdClass(SmsConfigMasterId.class)
@Table(name="eway_sms_config_master")


public class SmsConfigMaster implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="S_NO", nullable=false)
    private Integer    sNo ;

    @Id
    @Column(name="COMPANY_ID", nullable=false, length=20)
    private String     companyId ;
    
    @Id
    @Column(name="BRANCH_CODE", nullable=false, length=20)
    private String     branchCode ;
    
    @Id
    @Column(name="AMEND_ID")
    private Integer    amendId ;

    //--- ENTITY DATA FIELDS 
    @Column(name="STATUS", length=6)
    private String     status ;

    @Temporal(TemporalType.DATE)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;

    @Column(name="SENDER_ID", length=60)
    private String     senderId ;

    @Column(name="REMARKS", length=900)
    private String     remarks ;

    @Column(name="SMS_USER_PASS", length=150)
    private String     smsUserPass ;

    @Column(name="SMS_USER_NAME", length=150)
    private String     smsUserName ;

    @Column(name="SMS_PARTY_URL", length=300)
    private String     smsPartyUrl ;

    @Column(name="SECURE_YN", length=60)
    private String     secureYn ;

    @Column(name="CORE_APP_CODE", length=20)
    private String     coreAppCode ;

    @Column(name="REGULATORY_CODE", length=20)
    private String     regulatoryCode ;

    @Column(name="CREATED_BY", length=100)
    private String     createdBy ;

    @Column(name="UPDATED_BY", length=100)
    private String     updatedBy ;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATED_DATE")
    private Date       updatedDate ;
    	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_START")
    private Date       effectiveDateStart ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_END")
    private Date       effectiveDateEnd ;
    //--- ENTITY LINKS ( RELATIONSHIP )


}



