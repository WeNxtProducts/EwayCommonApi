package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.RenewQuotePolicy;
import com.maan.eway.bean.RenewQuotePolicyId;
/**
 * <h2>BankMasterRepository</h2>
 *
 * createdAt : 2022-08-24 - Time 12:58:26
 * <p>
 * Description: "BankMaster" Repository
 */
 
 
 
public interface RenewQuotePolicyRepository  extends JpaRepository<RenewQuotePolicy,RenewQuotePolicyId > , JpaSpecificationExecutor<RenewQuotePolicy> {


	List<RenewQuotePolicy> findByCurrentStatusCodeAndTranId(String string, String tranId);

	List<RenewQuotePolicy> findByCurrentStatusCodeInAndTranIdIn(List<String> status, List<String> tranId);

	List<RenewQuotePolicy> findByOldpolicyNo(String policyNo);
	
	List<RenewQuotePolicy> findByTranId(String tranId);

	List<RenewQuotePolicy> findByTranIdAndCurrentStatusCode(String tranId, String string);

	List<RenewQuotePolicy> findByTranIdAndCurrentStatusCodeNotIn(String tranId, List<String> asList);

	


	

}
