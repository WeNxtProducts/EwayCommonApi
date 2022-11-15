package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.SmsMaster;
import com.maan.eway.bean.SmsMasterId;

public interface SmsMasterRepository  extends JpaRepository<SmsMaster, SmsMasterId>, JpaSpecificationExecutor<SmsMaster>{

}
