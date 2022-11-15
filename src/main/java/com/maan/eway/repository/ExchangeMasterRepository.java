package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.ExchangeMaster;
import com.maan.eway.bean.ExchangeMasterId;

public interface ExchangeMasterRepository extends JpaRepository<ExchangeMaster, ExchangeMasterId>, JpaSpecificationExecutor<ExchangeMaster> {

}
