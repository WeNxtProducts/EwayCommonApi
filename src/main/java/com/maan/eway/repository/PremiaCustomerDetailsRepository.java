/*
 * Java domain class for entity "PremiaCustomerDetails" 
 * Created on 2022-12-05 ( Date ISO 2022-12-05 - Time 18:54:00 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */
 
 /*
 * Created on 2022-12-05 ( 18:54:00 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.repository;

import java.math.BigDecimal;
import java.util.List;

import com.maan.eway.bean.PremiaCustomerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.maan.eway.bean.PremiaCustomerDetailsId;
/**
 * <h2>PremiaCustomerDetailsRepository</h2>
 *
 * createdAt : 2022-12-05 - Time 18:54:00
 * <p>
 * Description: "PremiaCustomerDetails" Repository
 */
 
 
 
public interface PremiaCustomerDetailsRepository  extends JpaRepository<PremiaCustomerDetails,PremiaCustomerDetailsId > , JpaSpecificationExecutor<PremiaCustomerDetails> {

	List<PremiaCustomerDetails> findByCustomerCode(String coustomerCode);

	List<PremiaCustomerDetails> findByCustomerCodeAndCompanyIdAndBranchCodeAndStatus(String customerCode,
			String companyId, String branchCode, String string);

}
