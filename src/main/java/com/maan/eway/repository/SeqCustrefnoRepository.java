/*
 * Java domain class for entity "SeqQuoteno" 
 * Created on 2023-01-04 ( Date ISO 2023-01-04 - Time 19:00:39 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */
 
 /*
 * Created on 2023-01-04 ( 19:00:39 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.SeqCustid;
import com.maan.eway.bean.SeqCustrefno;
/**
 * <h2>SeqQuotenoRepository</h2>
 *
 * createdAt : 2023-01-04 - Time 19:00:39
 * <p>
 * Description: "SeqQuoteno" Repository
 */
 
 
 
public interface SeqCustrefnoRepository  extends JpaRepository<SeqCustrefno,Long > , JpaSpecificationExecutor<SeqCustrefno> {

}
