/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2022-11-21 ( Date ISO 2022-11-21 - Time 15:20:20 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2022-11-21 ( 15:20:20 )
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
* Domain class for entity "MsCommonDetails"
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
@IdClass(MsCommonDetailsId.class)
@Table(name="ms_common_details")


public class MsCommonDetails implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="INSURANCE_ID", nullable=false, length=20)
    private String     insuranceId ;

    @Id
    @Column(name="BRANCH_CODE", nullable=false, length=100)
    private String     branchCode ;

    @Id
    @Column(name="AGENCY_CODE", nullable=false, length=20)
    private String     agencyCode ;

    @Id
    @Column(name="SECTION_ID", nullable=false)
    private Integer    sectionId ;

    @Id
    @Column(name="PRODUCT_ID", nullable=false)
    private Integer    productId ;

    @Id
    @Column(name="CD_REFNO", nullable=false)
    private Long       cdRefno ;

    @Id
    @Column(name="MS_REFNO", nullable=false)
    private Long       msRefno ;

    @Id
    @Column(name="REQUESTREFERENCENO", nullable=false, length=20)
    private String     requestreferenceno ;

    @Id
    @Column(name="STATUS", nullable=false, length=2)
    private String     status ;

    @Id
    @Column(name="VD_REFNO", nullable=false)
    private Long       vdRefno ;

    //--- ENTITY DATA FIELDS 
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;

    @Column(name="DD_REFNO")
    private Long       ddRefno ;
    //--- ENTITY LINKS ( RELATIONSHIP )


}



