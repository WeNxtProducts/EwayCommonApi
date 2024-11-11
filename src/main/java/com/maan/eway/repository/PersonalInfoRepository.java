/*
 * Java domain class for entity "PersonalInfo" 
 * Created on 2022-10-11 ( Date ISO 2022-10-11 - Time 15:28:59 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */
 
 /*
 * Created on 2022-10-11 ( 15:28:59 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.repository;

import java.math.BigDecimal;
import java.util.List;

import jakarta.transaction.Transactional;

import com.maan.eway.bean.PersonalInfo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.maan.eway.bean.PersonalInfoId;
/**
 * <h2>PersonalInfoRepository</h2>
 *
 * createdAt : 2022-10-11 - Time 15:28:59
 * <p>
 * Description: "PersonalInfo" Repository
 */
 

public interface PersonalInfoRepository  extends JpaRepository<PersonalInfo,PersonalInfoId > , JpaSpecificationExecutor<PersonalInfo> {


	Page<PersonalInfo> findByStatus(Pageable paging, String string);

	PersonalInfo findByPolicyHolderTypeidAndPolicyHolderTypeAndIdNumber(String policyHolderTypeid,
			String policyHolderType, String idNumber);

	Long countByCustomerId(String customerId);

	@Transactional
	void deleteByCustomerId(String customerId);

	PersonalInfo findByCustomerId(String customerId);

	List<PersonalInfo> findByCompanyIdAndCustomerId(String companyId, String customerId);

	List<PersonalInfo> findByCustomerReferenceNo(String customerReferenceNo);



}
