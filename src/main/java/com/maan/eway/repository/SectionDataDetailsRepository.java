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

import java.math.BigDecimal;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.SectionDataDetailsId;

 
 
public interface SectionDataDetailsRepository  extends JpaRepository<SectionDataDetails,SectionDataDetailsId > , JpaSpecificationExecutor<SectionDataDetails> {

	Long countByQuoteNo(String quoteNo);

	@Transactional
	void deleteByQuoteNo(String quoteNo);

	List<SectionDataDetails> findByRequestReferenceNoOrderByRiskIdAsc(String requestReferenceNo);

	List<SectionDataDetails> findBySectionId(String sectionId);

	List<SectionDataDetails> findByQuoteNoOrderByRiskIdAsc(String quoteNo);

	List<SectionDataDetails> findByQuoteNo(String quoteNo);

	List<SectionDataDetails> findByQuoteNoAndStatusOrderByRiskIdAsc(String prevQuoteNo, String string);

	

	List<SectionDataDetails> findByQuoteNoAndStatusNot(String quoteNo, String string);

	Long countByQuoteNoNotAndEndtCountAndOriginalPolicyNo(String quoteNo, BigDecimal bigDecimal,
			String originalPolicyNo);

	@Transactional
	void deleteByQuoteNoNotAndEndtCountAndOriginalPolicyNo(String quoteNo, BigDecimal bigDecimal,
			String originalPolicyNo);
	
	SectionDataDetails findByQuoteNoAndSectionIdAndRiskId(String quoteNo,String sectionId, Integer Riskid);

}
