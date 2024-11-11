/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2024-02-01 ( Date ISO 2024-02-01 - Time 11:55:32 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2024-02-01 ( 11:55:32 )
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
* Domain class for entity "EwayVehicleMakemodelMasterDetail"
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
@IdClass(EwayVehicleMakemodelMasterDetailId.class)
@Table(name="eway_vehicle_makemodel_master_detail")


public class EwayVehicleMakemodelMasterDetail implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="VEHICLEID", nullable=false, length=50)
    private String     vehicleid ;

    @Id
    @Column(name="PRODUCT_ID", nullable=false)
    private Integer    productId ;

    @Id
    @Column(name="COMPANY_ID", nullable=false, length=20)
    private String     companyId ;

    @Id
    @Column(name="SECTION_ID", nullable=false)
    private Integer    sectionId ;

    @Id
    @Column(name="MAKE_ID", nullable=false)
    private Integer    makeId ;

    @Id
    @Column(name="MODELGROUP_ID", nullable=false)
    private Integer    modelgroupId ;

    @Id
    @Column(name="AMEND_ID", nullable=false)
    private Integer    amendId ;

    @Id
    @Column(name="BODY_ID", nullable=false)
    private Integer    bodyId ;

    //--- ENTITY DATA FIELDS 
    @Column(name="SOURCEVEHICLEID", length=500)
    private String     sourcevehicleid ;

    @Column(name="FILESOURCE", length=500)
    private String     filesource ;

    @Column(name="MAKE", length=100)
    private String     make ;

    @Column(name="MODELGROUP", length=200)
    private String     modelgroup ;

    @Column(name="MODEL", length=100)
    private String     model ;

    @Column(name="MAKEMODEL", length=500)
    private String     makemodel ;

    @Column(name="BODYTYPE", length=100)
    private String     bodytype ;

    @Column(name="ENGINESIZE_CC", length=500)
    private String     enginesizeCc ;

    @Column(name="WEIGHT_KG", length=500)
    private String     weightKg ;

    @Column(name="POWER_KW", length=500)
    private String     powerKw ;

    @Column(name="FUELTYPE", length=500)
    private String     fueltype ;

    @Column(name="TRANSMISSIONTYPE", length=500)
    private String     transmissiontype ;

    @Column(name="ISSELECTABLE", length=500)
    private String     isselectable ;

    @Column(name="VEHICLEGROUP", length=500)
    private String     vehiclegroup ;

    @Column(name="MODEL_ID")
    private Integer    modelId ;

    @Column(name="STATUS", length=2)
    private String     status ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_START")
    private Date       effectiveDateStart ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFFECTIVE_DATE_END")
    private Date       effectiveDateEnd ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;

    @Column(name="CREATED_BY", length=100)
    private String     createdBy ;

    @Column(name="UPDATED_BY", length=100)
    private String     updatedBy ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATED_DATE")
    private Date       updatedDate ;


    //--- ENTITY LINKS ( RELATIONSHIP )


}



