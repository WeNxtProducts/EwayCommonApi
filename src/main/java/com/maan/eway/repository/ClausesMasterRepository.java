package com.maan.eway.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.ClausesMaster;
import com.maan.eway.bean.ClausesMasterId;

public interface ClausesMasterRepository  extends JpaRepository<ClausesMaster,ClausesMasterId>, JpaSpecificationExecutor<ClausesMaster>{

	ClausesMaster findTopByClausesIdOrderByAmendIdDesc(Integer integer);

	
	List<ClausesMaster> findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanEqualOrderByClausesIdAscAmendIdDesc(
			String companyId, String branchCode, String productId, String sectionId, Date date);


	List<ClausesMaster> findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndTypeIdAndEffectiveDateStartLessThanEqualOrderByClausesIdAscAmendIdDesc(
			String companyId, String branchCode, String productId, String sectionId, String string, Date date);

}
