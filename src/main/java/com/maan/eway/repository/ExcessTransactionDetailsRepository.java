package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.ExcessTransactionDetails;
import com.maan.eway.bean.ExcessTransactionDetailsId;

public interface ExcessTransactionDetailsRepository extends JpaRepository<ExcessTransactionDetails, ExcessTransactionDetailsId>{

	List<ExcessTransactionDetails> findByRequestReferenceNo(String requestReferenceNo);

}
