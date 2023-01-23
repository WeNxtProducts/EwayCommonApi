package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.WarRateMaster;
import com.maan.eway.bean.WarRateMasterId;

public interface WarRateMasterRepository  extends JpaRepository<WarRateMaster,WarRateMasterId>, JpaSpecificationExecutor<WarRateMaster>{

	
	
	List<WarRateMaster> findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(String companyId,
			String branchCode, String productId, String sectionId);

}
