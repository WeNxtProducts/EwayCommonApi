package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.WarrantyMaster;
import com.maan.eway.bean.WarrantyMasterId;

public interface WarrantyMasterRepository  extends JpaRepository<WarrantyMaster,WarrantyMasterId>, JpaSpecificationExecutor<WarrantyMaster>{

	
	List<WarrantyMaster> findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(String companyId,
			String branchCode, String productId, String sectionId);

}
