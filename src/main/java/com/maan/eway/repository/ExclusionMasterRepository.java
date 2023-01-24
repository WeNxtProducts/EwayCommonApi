package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.ExclusionMaster;
import com.maan.eway.bean.ExclusionMasterId;

public interface ExclusionMasterRepository extends JpaRepository<ExclusionMaster, ExclusionMasterId>, JpaSpecificationExecutor<ExclusionMaster>{

	
	
	List<ExclusionMaster> findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(String companyId,
			String branchCode, String productId, String sectionId);

	ExclusionMaster findTopByExclusionIdOrderByAmendIdDesc(Integer integer);

}
