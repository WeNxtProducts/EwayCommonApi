package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.bean.ErrorDescMaster;

@Repository
public interface ErrorDescMasterRepository extends JpaRepository<ErrorDescMaster, Long> {

}
