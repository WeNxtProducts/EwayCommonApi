/*
 * Java domain class for entity "BankMaster" 
 * Created on 2022-08-24 ( Date ISO 2022-08-24 - Time 12:58:26 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */
 
 /*
 * Created on 2022-08-24 ( 12:58:26 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.repository;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.UWReferralDetails;
import com.maan.eway.bean.UWReferralDetailsId;
import com.maan.eway.bean.UWReferralHistoryId;
import com.maan.eway.bean.UWRefferralHistory;

 
public interface UWReferralHistoryRepository  extends JpaRepository<UWRefferralHistory,UWReferralHistoryId > , JpaSpecificationExecutor<UWRefferralHistory> {

	
}
