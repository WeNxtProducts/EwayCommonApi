package com.maan.eway.chartaccount;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChartParentMasterRepository extends JpaRepository<ChartParentMaster, ChartParentMasterId> {

	List<ChartParentMaster> findByChatParentIdCompanyIdAndStatusIgnoreCase(Integer companyId, String status);

	List<ChartParentMaster> findByChatParentIdCompanyIdAndStatusIgnoreCaseOrderByDisplayOrderAsc(Integer companyId,
			String status);
	
	List<ChartParentMaster> findByChatParentIdCompanyId(Integer companyId);

}
