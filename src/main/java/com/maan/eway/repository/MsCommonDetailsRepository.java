/*
 * Java domain class for entity "MsCommonDetails" 
 * Created on 2022-10-19 ( Date ISO 2022-10-19 - Time 17:10:34 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */
 
 /*
 * Created on 2022-10-19 ( 17:10:34 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.repository;

import java.math.BigDecimal;

import com.maan.eway.bean.MsCommonDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.maan.eway.bean.MsCommonDetailsId;
/**
 * <h2>MsCommonDetailsRepository</h2>
 *
 * createdAt : 2022-10-19 - Time 17:10:34
 * <p>
 * Description: "MsCommonDetails" Repository
 */
 
 
 
public interface MsCommonDetailsRepository  extends JpaRepository<MsCommonDetails,MsCommonDetailsId > , JpaSpecificationExecutor<MsCommonDetails> {

}
