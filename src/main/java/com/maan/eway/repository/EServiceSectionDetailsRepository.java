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

import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceSectionDetailsId;

 
 
public interface EServiceSectionDetailsRepository  extends JpaRepository<EserviceSectionDetails,EserviceSectionDetailsId > , JpaSpecificationExecutor<EserviceSectionDetails> {

	List<EserviceSectionDetails> findByRequestReferenceNoAndRiskIdAndProductIdOrderBySectionIdAsc(
			String requestReferenceNo, Integer locationId, String productId);

	Long countByRequestReferenceNoAndRiskId(String requestReferenceNo, Integer locationId);

	@Transactional
	void deleteByRequestReferenceNoAndRiskId(String requestReferenceNo, Integer locationId);

	List<EserviceSectionDetails> findByRequestReferenceNoOrderByRiskIdAsc(String requestReferenceNo);

	List<EserviceSectionDetails> findByQuoteNoOrderByRiskIdAsc(String quoteNo);

	List<EserviceSectionDetails> findByRequestReferenceNoOrderBySectionIdAsc(String oldReqRefNo);

	List<EserviceSectionDetails> findByQuoteNo(String quoteNo);

	List<EserviceSectionDetails> findByRequestReferenceNoOrderBySectionNameAsc(String requestReferenceNo);

	List<EserviceSectionDetails> findByRequestReferenceNoAndStatus(String requestReferenceNo, String string);

	List<EserviceSectionDetails> findByRequestReferenceNoAndStatusIn(String requestReferenceNo, List<String> status);

	List<EserviceSectionDetails> findByRequestReferenceNoAndStatusNot(String requestReferenceNo, String string);

	List<EserviceSectionDetails> findByRequestReferenceNoAndProductIdOrderBySectionIdAsc(String requestReferenceNo,
			String productId);

	List<EserviceSectionDetails> findByQuoteNoAndStatusNotOrderByRiskIdAsc(String endtPrevQuoteNo, String string);

	List<EserviceSectionDetails> findByRequestReferenceNo(String requestRefNo);

	List<EserviceSectionDetails> findByQuoteNoAndUserOpt(String requestRefNo,String useropt);

	EserviceSectionDetails findByRequestReferenceNoAndRiskIdAndSectionIdAndLocationId(String requestReferenceNo,
			Integer vehicleId, String sectionId, Integer locationId);

	List<EserviceSectionDetails> findByRequestReferenceNoAndRiskIdAndProductIdAndLocationIdOrderBySectionIdAsc(
			String requestReferenceNo, Integer valueOf, String productId, Integer valueOf2);
	

}
