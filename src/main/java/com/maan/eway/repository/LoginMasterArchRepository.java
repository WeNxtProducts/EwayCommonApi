/*
 * Java domain class for entity "LoginMasterArch" 
 * Created on 2022-08-24 ( Date ISO 2022-08-24 - Time 12:58:27 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */
 
 /*
 * Created on 2022-08-24 ( 12:58:27 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.repository;

import java.math.BigDecimal;

import com.maan.eway.bean.LoginBranchMasterArch;
import com.maan.eway.bean.LoginBranchMasterArchId;
import com.maan.eway.bean.LoginMasterArch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.maan.eway.bean.LoginMasterArchId;
/**
 * <h2>LoginMasterArchRepository</h2>
 *
 * createdAt : 2022-08-24 - Time 12:58:27
 * <p>
 * Description: "LoginMasterArch" Repository
 */
 
 
 
public interface LoginMasterArchRepository  extends JpaRepository<LoginMasterArch,LoginMasterArchId > , JpaSpecificationExecutor<LoginMasterArch> {

}
