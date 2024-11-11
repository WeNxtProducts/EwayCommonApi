package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.FirstLossPayee;

public interface FirstLossPayeeRepository extends JpaRepository<FirstLossPayee,Integer> {

	List<FirstLossPayee> findByRequestReferenceNo(String requestReferenceNo);

	List<FirstLossPayee> findByRequestReferenceNoAndLocationId(String requestReferenceNo, Integer valueOf);

}
