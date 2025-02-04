/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2022-11-21 ( Date ISO 2022-11-21 - Time 15:19:55 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2022-11-21 ( 15:19:55 )
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
* Domain class for entity "CompanyTaxSetup"
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
@IdClass(CompaniesTaxSetupId.class)
@Table(name="companies_tax_setup")


public class CompaniesTaxSetup implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
	@Id
	@Column(name="COUNTRY_ID", nullable=false, length=20)
	private String     countryId ;

    @Id
    @Column(name="COMPANY_ID", nullable=false, length=20)
    private String     companyId ;

    @Id
    @Column(name="TAX_ID", nullable=false)
    private Integer    taxId ;

    @Id
    @Column(name="AMEND_ID", nullable=false)
    private Integer amendId;
    
    @Id
    @Column(name="TAX_FOR", length=20)
    private String   taxFor;
    
    @Id
    @Column(name="BRANCH_CODE", length=20)
    private String   branchCode;
   
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_START", nullable=false)
    private Date       effectiveDateStart ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_END", nullable=false)
    private Date       effectiveDateEnd ;

    //--- ENTITY DATA FIELDS 
    @Column(name="TAX_NAME", length=200)
    private String     taxName ;

    @Column(name="TAX_DESC", length=200)
    private String     taxDesc ;

    @Column(name="CALC_TYPE", length=1)
    private String     calcType ;

    @Column(name="CALC_TYPE_DESC", length=100)
    private String     calcTypeDesc ;

    @Column(name="VALUE")
    private Double     value ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;

    @Column(name="CREATED_BY", length=100)
    private String     createdBy ;

    @Column(name="STATUS", length=1)
    private String     status ;


    @Column(name="REMARKS", length=100)
    private String     remarks ;
   
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATED_DATE")
    private Date       updatedDate ;
    

    @Column(name="UPDATED_BY", length=20)
    private String   updatedBy ;

    @Column(name="TAX_CODE", length=20)
    private String   taxCode;
        
    @Column(name="TAX_FOR_DESC", length=20)
    private String   taxForDesc;
    
    @Column(name="CORE_APP_CODE", length=20)
    private String   coreAppCode;
    
    @Column(name="REGULATORY_CODE", length=20)
    private String   regulatoryCode;
     
    @Column(name="TAX_EXEMPT_ALLOW_YN", length=20)
    private String   taxExemptAllowYn;
    
    @Column(name="PRIORITY", length=20)
    private Integer   priority;
    
    @Column(name="DEPENDENT_YN", length=20)
    private String   dependentYn;    

    @Column(name="MINIMUM_AMOUNT", length=20)
    private BigDecimal   minimumAmount;
}



