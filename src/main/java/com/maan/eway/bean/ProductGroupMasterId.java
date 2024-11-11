/*
 * Created on 2022-12-05 ( 13:36:13 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */
package com.maan.eway.bean;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;



/**
 * Composite primary key for entity "ProductGroupMaster" ( stored in table "product_group_master" )
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
public class ProductGroupMasterId implements Serializable {

    private static final long serialVersionUID = 1L;

    //--- ENTITY KEY ATTRIBUTES 
    private Integer    groupId ;
    
    private Integer    productId ;
    
    private String     branchCode ;
    
    private String     companyId ;
    
    private Integer    amendId ;
    
     
}
