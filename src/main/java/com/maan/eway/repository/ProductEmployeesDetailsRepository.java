package com.maan.eway.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.BuildingDetails;
import com.maan.eway.bean.BuildingDetailsId;
import com.maan.eway.bean.FidelityEmployeeDetails;
import com.maan.eway.bean.FidelityEmployeeDetailsId;
import com.maan.eway.bean.ProductEmployeeDetails;
import com.maan.eway.bean.ProductEmployeeDetailsId;

public interface ProductEmployeesDetailsRepository extends JpaRepository<ProductEmployeeDetails, ProductEmployeeDetailsId>, JpaSpecificationExecutor<ProductEmployeeDetails> {

	Long countByQuoteNoAndRiskId(String quoteNo, Integer valueOf);

	@Transactional
	void deleteByQuoteNoAndRiskId(String quoteNo, Integer valueOf);

	List<ProductEmployeeDetails> findByQuoteNo(String quoteNo);

	List<ProductEmployeeDetails> findByQuoteNoAndRiskIdOrderByEmployeeIdAsc(String quoteNo, Integer valueOf);

	Long countByQuoteNoAndRiskIdAndEmployeeId(String quoteNo, Integer valueOf, Long valueOf2);

	@Transactional
	void deleteByQuoteNoAndRiskIdAndEmployeeId(String quoteNo, Integer valueOf, Long valueOf2);


}
