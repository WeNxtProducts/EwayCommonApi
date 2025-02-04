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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCommonDetailsId;

import jakarta.transaction.Transactional;
/**
 * <h2>BankMasterRepository</h2>
 *
 * createdAt : 2022-08-24 - Time 12:58:26
 * <p>
 * Description: "BankMaster" Repository
 */
 
 
 
public interface EserviceCommonDetailsRepository  extends JpaRepository<EserviceCommonDetails,EserviceCommonDetailsId > , JpaSpecificationExecutor<EserviceCommonDetails> {

	EserviceCommonDetails findByRequestReferenceNoAndRiskId(String requestReferenceNo , Integer riskId);

	Long countByRequestReferenceNo(String refNo);

	@Transactional
	void deleteByRequestReferenceNo(String refNo);

	List<EserviceCommonDetails> findByRequestReferenceNoOrderByRiskIdAsc(String requestReferenceNo);

	EserviceCommonDetails findByRequestReferenceNoAndQuoteNoAndProductIdAndCompanyId(String requestReferenceNo,
			String quoteNo, String productId, String companyId);

	List<EserviceCommonDetails> findByRequestReferenceNoAndProductId(String requestReferenceNo, String productId);

	Integer countByOriginalPolicyNoAndRiskId(String policyNo, int i);

	List<EserviceCommonDetails> findByOriginalPolicyNoAndRiskId(String policyNo, int i);

	List<EserviceCommonDetails> findByPolicyNoAndStatus(String policyNo, String string);

	List<EserviceCommonDetails> findByQuoteNoOrderByRiskIdAsc(String prevQuoteNo);

	List<EserviceCommonDetails> findByPolicyNoAndRiskId(String prevPolicyNo, int i);

		List<EserviceCommonDetails> findByRequestReferenceNo(String requestReferenceNo);

		List<EserviceCommonDetails> findByQuoteNo(String quoteNo);

		List<EserviceCommonDetails> findByCustomerId(String customerId);

		List<EserviceCommonDetails> findByOriginalPolicyNo(String policyNo);

		List<EserviceCommonDetails> findByRequestReferenceNoOrderBySectionNameAsc(String requestReferenceNo);

		List<EserviceCommonDetails> findByRequestReferenceNoAndSectionId(String requestReferenceNo, String sectionId);

		List<EserviceCommonDetails> findByPolicyNo(String prevPolicyNo);

		EserviceCommonDetails findByRequestReferenceNoOrderByRiskIdDesc(String requestReferenceNo);

		List<EserviceCommonDetails> findByQuoteNoAndSectionIdAndProductIdOrderByRiskIdAsc(String quoteNo, String string,
				String string2);

		EserviceCommonDetails findByRequestReferenceNoAndRiskIdAndSectionId(String requestReferenceNo,
				Integer vehicleId, String sectionId);

		List<EserviceCommonDetails> findByQuoteNoAndStatusNotOrderByRiskIdAsc(String endtPrevQuoteNo, String string);

		List<EserviceCommonDetails> findByRequestReferenceNoAndEndorsementType(String newReqRefNo, Integer endorsementType);

		List<EserviceCommonDetails> findByRequestReferenceNoAndStatusNotAndRiskIdInOrderByRiskIdAsc(
				String requestReferenceNo, String string, List<Integer> vehicleIds);

		List<EserviceCommonDetails> findByRequestReferenceNoAndStatus(String requestReferenceNo, String string);

		void deleteByPolicyNo(String string);

		List<EserviceCommonDetails> findByOriginalPolicyNoAndStatusNot(String policyNo, String string);

		EserviceCommonDetails findAllByRequestReferenceNoAndSectionId(String requestReferenceNo, String sectionId);

		EserviceCommonDetails findByRequestReferenceNoAndOriginalRiskIdAndSectionId(String requestReferenceNo,
				Integer vehicleId, String sectionid);

		List<EserviceCommonDetails> findByRequestReferenceNoAndStatusNotAndOriginalRiskIdInOrderByOriginalRiskIdAsc(
				String requestReferenceNo, String string, List<Integer> vehicleIds);

		EserviceCommonDetails findByRequestReferenceNoAndRiskIdAndSectionIdAndLocationId(String requestReferenceNo,
				Integer vehicleId, String sectionId, Integer valueOf);

		List<EserviceCommonDetails> findByRequestReferenceNoAndLocationId(String requestReferenceNo, Integer data);






}
