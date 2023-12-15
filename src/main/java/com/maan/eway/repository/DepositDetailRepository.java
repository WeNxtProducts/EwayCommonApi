package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.DepositDetail;

public interface DepositDetailRepository extends JpaRepository<DepositDetail, Long>{

	int countByProductIdAndQuoteNoAndPremiumAmountAndStatus(String productId, String quoteNo,
			Double valueOf, String string);

	List<DepositDetail> findByStatus(String string);

	List<DepositDetail> findByCbcNo(String cbcNo);

	int countByQuoteNoAndPremiumAmountAndStatus(String quoteNo, Double valueOf, String string);

}
