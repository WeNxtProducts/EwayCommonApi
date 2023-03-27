package com.maan.eway.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.PersonalAccident;
import com.maan.eway.bean.PersonalAccidentId;

public interface PersonalAccidentRepository
		extends JpaRepository<PersonalAccident, PersonalAccidentId>, JpaSpecificationExecutor<PersonalAccident> {

	PersonalAccident findByRequestReferenceNoAndRiskIdAndSectionId(String requestReferenceNo, Integer valueOf,
			String sectionId);

	PersonalAccident findByRequestReferenceNoAndRiskIdAndPersonIdAndSectionId(String requestReferenceNo,
			Integer valueOf, String personId, String sectionId);

	List<PersonalAccident> findByRequestReferenceNoAndSectionIdOrderByPersonId(String requestReferenceNo,
			String sectionId);

	Long countByQuoteNo(String quoteNo);

	@Transactional
	void deleteByQuoteNo(String quoteNo);

	List<PersonalAccident> findByQuoteNoOrderByRiskIdAsc(String quoteNo);

	List<PersonalAccident> findByQuoteNo(String quoteNo);

}
