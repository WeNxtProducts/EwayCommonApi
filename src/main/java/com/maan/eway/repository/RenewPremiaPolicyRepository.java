package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.RenewPremiaPolicy;
import com.maan.eway.bean.RenewPremiaPolicyId;

 
 
public interface RenewPremiaPolicyRepository  extends JpaRepository<RenewPremiaPolicy,RenewPremiaPolicyId > , JpaSpecificationExecutor<RenewPremiaPolicy> {

	List<RenewPremiaPolicy> findByStatusAndTransactionId(String string, String tranId);

	List<RenewPremiaPolicy> findAllByInsuredMobile(String insuredMobile);

	List<RenewPremiaPolicy> findByPolSrcCode(String coreAppBrokerCode);



	


	

}
