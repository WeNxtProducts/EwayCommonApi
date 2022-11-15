package com.maan.eway.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.maan.eway.bean.MotorMakeMaster;
import com.maan.eway.bean.MotorMakeMasterId;

public interface MotorMakeMasterRepository extends JpaRepository<MotorMakeMaster, MotorMakeMasterId> , JpaSpecificationExecutor<MotorMakeMaster>{




}
