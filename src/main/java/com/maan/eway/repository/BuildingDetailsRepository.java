package com.maan.eway.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.maan.eway.bean.BuildingDetails;
import com.maan.eway.bean.BuildingDetailsId;

public interface BuildingDetailsRepository extends JpaRepository<BuildingDetails, BuildingDetailsId>, JpaSpecificationExecutor<BuildingDetails> {

	BuildingDetails findByRequestReferenceNoAndRiskIdAndSectionId(String requestReferenceNo, Integer valueOf,
			String sectionId);

	BuildingDetails findByRequestReferenceNoAndRiskId(String requestReferenceNo, Integer valueOf);

	List<BuildingDetails> findByRequestReferenceNo(String requestReferenceNo);

	List<BuildingDetails> findByQuoteNoOrderByRiskIdAsc(String quoteNo);

	Long countByQuoteNo(String newQuoteNo);

	List<BuildingDetails> findByQuoteNo(String quoteNo);

	List<BuildingDetails> findByRequestReferenceNoOrderByRiskIdAsc(String requestReferenceNo);

	@Query(value = "SELECT PD.COVER_DESC, BD.LOCATION_NAME, ( SELECT CONTENT_RISK_DESC FROM content_and_risk WHERE REQUEST_REFERENCE_NO = PD.REQUEST_REFERENCE_NO AND RISK_ID = PD.VEHICLE_ID AND SECTION_ID = PD.SECTION_ID ) AS DESCRIPTION, PD.RATE, PD.SUM_INSURED,PD.PREMIUM_EXCLUDED_TAX_LC FROM POLICY_COVER_DATA PD, building_details BD WHERE PD.TAX_ID = '0' AND PD.DISC_LOAD_ID = '0' AND PD.SUB_COVER_ID = '0' AND PD.VEHICLE_ID = BD.RISK_ID AND PD.Request_reference_no = BD.Request_reference_no AND PD.QUOTE_NO = ?1",nativeQuery = true)
	List<Map<String, Object>> getSectionDetails(String quoteNo);
	


}
