package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.MultiplePolicyDrCrDetail;
import com.maan.eway.bean.MultiplePolicyDrCrDetailId;

public interface MultiplePolicyDrCrDetailRepository extends JpaRepository<MultiplePolicyDrCrDetail, MultiplePolicyDrCrDetailId> {

	List<MultiplePolicyDrCrDetail> findByQuoteNoAndRiskId(String quoteNo, int vehicleId);

}
