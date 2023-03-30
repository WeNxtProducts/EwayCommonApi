package com.maan.eway.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.ExclusionMaster;
import com.maan.eway.bean.ExclusionMasterId;


public interface ExclusionMasterRepository extends JpaRepository<ExclusionMaster, ExclusionMasterId>, JpaSpecificationExecutor<ExclusionMaster>{

	
	
	
	
	ExclusionMaster findTopByExclusionIdOrderByAmendIdDesc(Integer integer);

	
	List<ExclusionMaster> findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanEqualOrderByExclusionIdAscAmendIdDesc(
			String companyId, String branchCode, String productId, String sectionId, Date date);


	List<ExclusionMaster> findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndTypeIdAndEffectiveDateStartLessThanEqualOrderByExclusionIdAscAmendIdDesc(
			String companyId, String branchCode, String productId, String sectionId, String string, Date date);

}
