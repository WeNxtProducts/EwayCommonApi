package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.ClausesMaster;
import com.maan.eway.bean.ClausesMasterId;

public interface ClausesMasterRepository  extends JpaRepository<ClausesMaster,ClausesMasterId>, JpaSpecificationExecutor<ClausesMaster>{

}
