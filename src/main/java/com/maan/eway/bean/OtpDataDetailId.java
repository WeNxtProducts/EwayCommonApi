/*
 * Created on 2023-07-06 ( 17:58:04 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */
package com.maan.eway.bean;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;


import java.math.BigDecimal;

/**
 * Composite primary key for entity "OtpDataDetail" ( stored in table "otp_data_detail" )
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
public class OtpDataDetailId implements Serializable {

    private static final long serialVersionUID = 1L;

    //--- ENTITY KEY ATTRIBUTES 
    private BigDecimal sNo ;
    
    private String     companyId ;
    
     
}
