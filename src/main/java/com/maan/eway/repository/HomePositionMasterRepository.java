/*
 * Java domain class for entity "HomePositionMaster" 
 * Created on 2022-09-06 ( Date ISO 2022-09-06 - Time 11:31:27 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */
 
 /*
 * Created on 2022-09-06 ( 11:31:27 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.repository;

import java.math.BigDecimal;
import java.util.List;

import jakarta.transaction.Transactional;

import com.maan.eway.bean.HomePositionMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.maan.eway.bean.HomePositionMasterId;
/**
 * <h2>HomePositionMasterRepository</h2>
 *
 * createdAt : 2022-09-06 - Time 11:31:27
 * <p>
 * Description: "HomePositionMaster" Repository
 */
 
 
@Transactional
public interface HomePositionMasterRepository  extends JpaRepository<HomePositionMaster,HomePositionMasterId > , JpaSpecificationExecutor<HomePositionMaster> {

	HomePositionMaster findByQuoteNo(String quoteNo);

	Long countByQuoteNo(String quoteNo);

	@Transactional
	void deleteByQuoteNo(String quoteNo);

	Long countByRequestReferenceNo(String requestReferenceNo);

	List<HomePositionMaster> findByRequestReferenceNo(String requestReferenceNo);

	HomePositionMaster findByPolicyNoAndStatusAndCompanyIdAndProductId(String policyNo, String status, String companyId,Integer productId);

	List<HomePositionMaster> findByQuoteNoAndProductId(String quoteNo, Integer productId);

	List<HomePositionMaster> findByRequestReferenceNoAndProductId(String requestReferenceNo, Integer productId);

	HomePositionMaster findByPolicyNo(String policyNo);

	HomePositionMaster findByPolicyNoAndStatus(String policyNo, String string);
	
	Long countByQuoteNoNotAndEndtCountAndOriginalPolicyNo(String quoteNo, Integer bigDecimal,
			String originalPolicyNo);

	@Transactional
	void deleteByQuoteNoNotAndEndtCountAndOriginalPolicyNo(String quoteNo, Integer bigDecimal,
			String originalPolicyNo);
	
	HomePositionMaster findTop1ByPolicyNo(String policyNo);

	List<HomePositionMaster> findAllByOrderByEntryDateDesc();


}
