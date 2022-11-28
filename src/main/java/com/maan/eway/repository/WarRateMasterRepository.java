package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.WarRateMaster;
import com.maan.eway.bean.WarRateMasterId;

public interface WarRateMasterRepository  extends JpaRepository<WarRateMaster,WarRateMasterId>, JpaSpecificationExecutor<WarRateMaster>{

}
