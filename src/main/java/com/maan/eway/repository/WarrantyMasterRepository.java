package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.WarrantyMaster;
import com.maan.eway.bean.WarrantyMasterId;

public interface WarrantyMasterRepository  extends JpaRepository<WarrantyMaster,WarrantyMasterId>, JpaSpecificationExecutor<WarrantyMaster>{

}
