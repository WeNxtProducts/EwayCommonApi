/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2023-01-04 ( Date ISO 2023-01-04 - Time 19:00:39 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2023-01-04 ( 19:00:39 )
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

import jakarta.persistence.*;




/**
* Domain class for entity "SeqQuoteno"
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
@Table(name="seq_custid")


public class SeqCustid implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="CUST_ID", nullable=false)
    private Long       custId ;

    //--- ENTITY DATA FIELDS 

    //--- ENTITY LINKS ( RELATIONSHIP )


}



