/*
 * Java domain class for entity "DocumentMaster" 
 * Created on 2022-08-24 ( Date ISO 2022-08-24 - Time 12:58:27 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */
 
 /*
 * Created on 2022-08-24 ( 12:58:27 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.DocumentTransactionDetails;
import com.maan.eway.bean.DocumentTransactionDetailsId;

import jakarta.transaction.Transactional;

 
public interface DocumentTransactionDetailsRepository  extends JpaRepository<DocumentTransactionDetails,DocumentTransactionDetailsId > , JpaSpecificationExecutor<DocumentTransactionDetails> {

	List<DocumentTransactionDetails> findByQuoteNo(String quoteNo);

	@Transactional
	void deleteByQuoteNoAndUniqueIdAndId(String quoteNo, Integer valueOf, String id);

	DocumentTransactionDetails findByQuoteNoAndUniqueIdAndId(String quoteNo, Integer valueOf, String id);

	Long countByQuoteNo(String quoteNo);

	Long countByQuoteNoAndProductIdAndSectionId(String quoteNo, Integer valueOf, Integer valueOf2);

	List<DocumentTransactionDetails> findByQuoteNoAndProductIdAndSectionId(String oldQuoteNo, Integer valueOf,Integer valueOf2);

	List<DocumentTransactionDetails> findByRequestReferenceNo( String requestReferenceNo);

	List<DocumentTransactionDetails> findByQuoteNoAndSectionId(String quoteNo, Integer secId);

	List<DocumentTransactionDetails> findByQuoteNoAndIdAndIdTypeAndRiskId(String quoteNo, String id, String idType,
			Integer valueOf);

	List<DocumentTransactionDetails> findByQuoteNoAndProductId(String quoteNo, Integer valueOf);

	Long countByQuoteNoNotAndEndtCountAndOriginalPolicyNo(String quoteNo, BigDecimal bigDecimal,
			String originalPolicyNo);

	@Transactional
	void deleteByQuoteNoNotAndEndtCountAndOriginalPolicyNo(String quoteNo, BigDecimal bigDecimal,
			String originalPolicyNo);

	List<DocumentTransactionDetails> findByQuoteNoAndInstallmentPeriodAndNoOfInstallment(String quoteNo,
			String installmentPeriod, String noOfInstallment);

}
