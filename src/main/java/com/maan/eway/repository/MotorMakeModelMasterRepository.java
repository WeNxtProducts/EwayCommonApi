package com.maan.eway.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.maan.eway.bean.MotorMakeModelMaster;
import com.maan.eway.bean.MotorMakeModelMasterId;

public interface MotorMakeModelMasterRepository extends JpaRepository<MotorMakeModelMaster, MotorMakeModelMasterId>,
		JpaSpecificationExecutor<MotorMakeModelMaster> {
	
}
