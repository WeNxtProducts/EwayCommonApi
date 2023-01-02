package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.BuildingDetails;
import com.maan.eway.bean.BuildingDetailsId;

public interface BuildingDetailsRepository extends JpaRepository<BuildingDetails, BuildingDetailsId>, JpaSpecificationExecutor<BuildingDetails> {

	BuildingDetails findByRequestReferenceNoAndRiskIdAndSectionId(String requestReferenceNo, Integer valueOf,
			String sectionId);

	BuildingDetails findByRequestReferenceNoAndRiskId(String requestReferenceNo, Integer valueOf);

	List<BuildingDetails> findByRequestReferenceNo(String requestReferenceNo);

}
