/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2022-10-19 ( Date ISO 2022-10-19 - Time 17:10:34 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2022-10-19 ( 17:10:34 )
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
* Domain class for entity "MsCustomerDetails"
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
@IdClass(MsCustomerDetailsId.class)
@Table(name="ms_customer_details")


public class MsCustomerDetails implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="POLICY_HOLDER_TYPEID", nullable=false, length=100)
    private String     policyHolderTypeid ;

    @Id
    @Column(name="ID_TYPE", nullable=false, length=2)
    private String     idType ;

    @Id
    @Column(name="ID_NUMBER", nullable=false, length=100)
    private String     idNumber ;

    @Id
    @Column(name="AGE", nullable=false)
    private Integer    age ;

    @Id
    @Column(name="GENDER", nullable=false, length=1)
    private String     gender ;

    @Id
    @Column(name="OCCUPATION", nullable=false, length=1)
    private String     occupation ;

    @Column(name="BUSINESS_TYPE", length=1)
    private String     businessType ;

    @Id
    @Column(name="REGION_CODE", nullable=false, length=20)
    private String     regionCode ;

    @Column(name="CITY_CODE", length=20)
    private String     cityCode ;

    @Id
    @Column(name="CD_REFNO", nullable=false)
    private Long       cdRefno ;

    @Id
    @Column(name="IS_TAX_EXEMPTED", nullable=false, length=1)
    private String     isTaxExempted ;

    //--- ENTITY DATA FIELDS 
    @Column(name="POLICY_HOLDER_TYPE", length=2)
    private String     policyHolderType ;

    @Column(name="ID_TYPE_DESC", length=100)
    private String     idTypeDesc ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DOB_OR_REG_DATE")
    private Date       dobOrRegDate ;

    @Column(name="NATIONALITY", length=20)
    private String     nationality ;

    @Column(name="PLACE_OF_BIRTH", length=100)
    private String     placeOfBirth ;

    @Column(name="GENDER_DESC", length=20)
    private String     genderDesc ;

    @Column(name="OCCUPATION_DESC", length=20)
    private String     occupationDesc ;

    @Column(name="BUSINESS_TYPE_DESC", length=20)
    private String     businessTypeDesc ;

    @Column(name="STATE_CODE", length=20)
    private String     stateCode ;

    @Column(name="STATE_NAME", length=100)
    private String     stateName ;

    @Column(name="CITY_NAME", length=100)
    private String     cityName ;

    @Column(name="TAX_EXEMPTED_ID", length=20)
    private String     taxExemptedId ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;

    @Column(name="STATUS", length=1)
    private String     status ;

    @Column(name="CREATED_BY", length=100)
    private String     createdBy ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATED_DATE")
    private Date       updatedDate ;

    @Column(name="UPDATED_BY", length=100)
    private String     updatedBy ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="LICENSE_ISSUED_DATE")
    private Date       licenseIssuedDate ;
    
    @Column(name="LICENSE_DURATION", length=100)
    private Integer licenseDuration ;
    
    @Column(name="AREA_GROUP", length=100)
    private String areaGroup ;
   
    @Column(name="AREA_CLASIFICATION", length=100)
    private String areaClasification ;
    


    @Column(name="MARITAL_STATUS", length=100)
    private String maritalStatus ;

}



