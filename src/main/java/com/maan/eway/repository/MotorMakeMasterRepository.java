package com.maan.eway.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.maan.eway.bean.MotorMakeMaster;
import com.maan.eway.bean.MotorMakeMasterId;

public interface MotorMakeMasterRepository extends JpaRepository<MotorMakeMaster, MotorMakeMasterId> , JpaSpecificationExecutor<MotorMakeMaster>{

	List<MotorMakeMaster> findByCompanyIdAndCoreAppCodeOrderByAmendIdDesc(String companyId, String makeId);




}
