/*
 * Java domain class for entity "CustomerDetails" 
 * Created on 2022-09-02 ( Date ISO 2022-09-02 - Time 18:14:52 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */
 
 /*
 * Created on 2022-09-02 ( 18:14:52 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import com.maan.eway.bean.CustomerDetails;
import com.maan.eway.bean.CustomerDetailsId;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceMotorDetailsId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
/**
 * <h2>CustomerDetailsRepository</h2>
 *
 * createdAt : 2022-09-02 - Time 18:14:52
 * <p>
 * Description: "CustomerDetails" Repository
 */
 
 @Transactional
public interface EServiceMotorDetailsRepository  extends JpaRepository<EserviceMotorDetails,EserviceMotorDetailsId > , JpaSpecificationExecutor<EserviceMotorDetails> {
  

 
	List<EserviceMotorDetails> findByRequestReferenceNoAndIdNumber(String requestReferenceNo, String idNumber);

	List<EserviceMotorDetails> findByRequestReferenceNoAndIdNumberOrderByRiskIdDesc(String requestReferenceNo,
			String idNumber);

	List<EserviceMotorDetails> findByRequestReferenceNo(String requestReferenceNo);

	List<EserviceMotorDetails> findByRequestReferenceNoAndIdNumberAndRiskId(String requestReferenceNo,
			String idNumber, Integer valueOf);

	EserviceMotorDetails findByRequestReferenceNoAndRiskId(String requestReferenceNo, Integer vehId);

	List<EserviceMotorDetails> findByRequestReferenceNoInOrderByUpdatedDateDesc(List<String> reqRefNos);

	List<EserviceMotorDetails> findByRequestReferenceNoOrderByRiskIdAsc(String requestReferenceNo);

	EserviceMotorDetails findByRequestReferenceNoAndRiskIdOrderByRiskIdAsc(String requestReferenceNo,
			Integer RiskId);
	EserviceMotorDetails findByRequestReferenceNoAndQuoteNoAndProductIdAndCompanyId(String requestReferenceNo,
			String quoteNo, String productId, String companyId);

	@Transactional
	List<EserviceMotorDetails> findByRequestReferenceNoAndProductId(String requestReferenceNo, String productId);

	List<EserviceMotorDetails> findByPolicyNoAndStatus(String policyNo, String string);
	@Transactional
	Integer countByOriginalPolicyNo(String policyNo);
	@Transactional
	List<EserviceMotorDetails> findByOriginalPolicyNo(String policyNo);
	@Transactional
	List<EserviceMotorDetails> findByQuoteNoOrderByRiskIdAsc(String prevQuoteNo);
	  

	//List<EserviceMotorDetails> findByQuoteNoAndStatusNotInOrderByRiskIdAsc(String prevQuoteNo, String string);

	List<EserviceMotorDetails> findByQuoteNoAndStatusOrderByRiskIdAsc(String prevQuoteNo, String string);



	List<EserviceMotorDetails> findByRequestReferenceNoAndStatusOrderByRiskIdAsc(String requestReferenceNo,
			String string);

	List<EserviceMotorDetails> findByQuoteNoAndStatusNotOrderByRiskIdAsc(String quoteNo, String string);

	List<EserviceMotorDetails> findByCustomerId(String customerId);

	List<EserviceMotorDetails> findByRequestReferenceNoAndStatusNotOrderByRiskIdAsc(String requestReferenceNo,
			String string);

	EserviceMotorDetails findByQuoteNoAndChassisNumber(String quoteNo, String chassisNumber);

	List<EserviceMotorDetails> findByRequestReferenceNoOrderBySectionNameAsc(String requestReferenceNo);

	List<EserviceMotorDetails> findByRequestReferenceNoAndRiskIdInAndStatusNotOrderByRiskIdAsc(
			String requestReferenceNo, List<Integer> vehicleIds, String string);

	EserviceMotorDetails findByRequestReferenceNoAndRiskIdAndLocationId(String requestReferenceNo, Integer vehicleId,
			Integer valueOf);





	



}