/*
 * Created on 2022-11-21 ( 15:20:11 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */
package com.maan.eway.bean;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;



/**
 * Composite primary key for entity "LoginMasterArch" ( stored in table "login_master_arch" )
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
public class LoginMasterArchId implements Serializable {

    private static final long serialVersionUID = 1L;

    //--- ENTITY KEY ATTRIBUTES 
    private String     archId ;
    
    private String     loginId ;
    
    private String     userType ;
    
    private String     subUserType ;
    
     
}
