/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2022-11-21 ( Date ISO 2022-11-21 - Time 15:20:26 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2022-11-21 ( 15:20:26 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.bean;


import java.io.Serializable;
import java.math.BigDecimal;
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
* Domain class for entity "ProductSectionMaster"
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
@IdClass(ProductSectionMasterId.class)
@Table(name="product_section_master")


public class ProductSectionMaster implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
	@Id
	@Column(name="SECTION_ID", nullable=false)
	private Integer    sectionId ;
	
	@Id
	@Column(name="PRODUCT_ID", nullable=false)
	private Integer    productId ;
	
	@Id
	@Column(name="COMPANY_ID", nullable=false, length=100)
	private String     companyId ;
	
	@Id
	@Column(name="AMEND_ID", nullable=false)
	private Integer    amendId ;

    //--- ENTITY DATA FIELDS 
    @Column(name="SECTION_NAME", length=100)
    private String     sectionName ;

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

    @Column(name="CORE_APP_CODE", nullable=false, length=20)
    private String     coreAppCode ;

    @Column(name="REMARKS", length=100)
    private String     remarks ;

    @Column(name="CREATED_BY", nullable=false, length=20)
    private String     createdBy ;

    @Column(name="REGULATORY_CODE", nullable=false, length=20)
    private String     regulatoryCode ;

    @Column(name="UPDATED_BY", length=20)
    private String     updatedBy ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATED_DATE")
    private Date       updatedDate ;

    @Column(name="MOTOR_YN", length=1)
    private String     motorYn;

    //--- ENTITY LINKS ( RELATIONSHIP )
    @Column(name="MIN_PREMIUM")
    private BigDecimal minPremium;
    
    @Column(name="SECTION_NAME_LOCAL", length=100)
    private String   sectionNameLocal ;

    
    

}



