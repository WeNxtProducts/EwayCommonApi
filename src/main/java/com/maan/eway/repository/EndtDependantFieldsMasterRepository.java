package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.EndtDependantFieldMaster;
import com.maan.eway.bean.EndtDependantFieldsMasterId;

public interface EndtDependantFieldsMasterRepository extends JpaRepository<EndtDependantFieldMaster,EndtDependantFieldsMasterId >, JpaSpecificationExecutor<EndtDependantFieldMaster>  {

	
}
