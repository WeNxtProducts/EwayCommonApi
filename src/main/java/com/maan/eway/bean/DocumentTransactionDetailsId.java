/*
 * Created on 2022-11-21 ( 15:19:57 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */
package com.maan.eway.bean;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;



/**
 * Composite primary key for entity "CoverDocumentUploadDetails" ( stored in table "cover_document_upload_details" )
 *
 * @author Telosys
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DocumentTransactionDetailsId implements Serializable {

    private static final long serialVersionUID = 1L;

    //--- ENTITY KEY ATTRIBUTES 
    private String     quoteNo ;

    private String     requestReferenceNo ;
    
    private Integer    productId ;

    private String     companyId ;

    private Integer    sectionId ;

    private Integer locationId ;
    
    private Integer riskId ;

    private String id ;
	
    private String idType ;
	
    private Integer uniqueId ;
    
     
}
