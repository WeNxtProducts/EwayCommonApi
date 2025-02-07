package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.ClaimIntimation;
import com.maan.eway.bean.ClaimIntimationId;



public interface ClaimIntimationRepository extends JpaRepository<ClaimIntimation,ClaimIntimationId > , JpaSpecificationExecutor<ClaimIntimation>{

}
