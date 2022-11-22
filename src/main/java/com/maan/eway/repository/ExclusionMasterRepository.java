package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.ExclusionMaster;
import com.maan.eway.bean.ExclusionMasterId;

public interface ExclusionMasterRepository extends JpaRepository<ExclusionMaster, ExclusionMasterId>, JpaSpecificationExecutor<ExclusionMaster>{

}
