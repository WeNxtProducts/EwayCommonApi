/*
 * Java domain class for entity "FactorRateRequestDetails" 
 * Created on 2022-11-08 ( Date ISO 2022-11-08 - Time 16:28:23 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */
 
 /*
 * Created on 2022-11-08 ( 16:28:23 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.repository;

import java.math.BigDecimal;
import java.util.List;

import jakarta.transaction.Transactional;

import com.maan.eway.bean.FactorRateRequestDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.maan.eway.bean.FactorRateRequestDetailsId;
/**
 * <h2>FactorRateRequestDetailsRepository</h2>
 *
 * createdAt : 2022-11-08 - Time 16:28:23
 * <p>
 * Description: "FactorRateRequestDetails" Repository
 */
 
 
 
public interface FactorRateRequestDetailsRepository  extends JpaRepository<FactorRateRequestDetails,FactorRateRequestDetailsId > , JpaSpecificationExecutor<FactorRateRequestDetails> {

	void deleteByRequestReferenceNoAndVehicleId(String requestReferenceNo, Integer vehicleId);

	Long countByRequestReferenceNoAndVehicleId(String requestReferenceNo, Integer vehicleId);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndVehicleIdOrderByCoverIdAsc(String requestReferenceNo, Integer vehicleId);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndDiscLoadIdOrderByVehicleIdAsc(String requestReferenceNo,
			int i);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndVehicleIdOrderByVehicleIdAsc(String requestReferenceNo,
			Integer vehicleId);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndDiscLoadIdAndVehicleIdOrderByVehicleIdAsc(
			String requestReferenceNo, int i, Integer vehicleId);

	List<FactorRateRequestDetails> findByRequestReferenceNoOrderByVehicleIdAsc(String requestReferenceNo);

	
	List<FactorRateRequestDetails> findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndUserOptOrderByVehicleIdAsc(
			String requestReferenceNo, int i, int j, String string);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdOrderByCoverIdAsc(
			String requestReferenceNo, Integer valueOf, String insuranceId, Integer valueOf2, Integer valueOf3);
	@Transactional
	void deleteByRequestReferenceNoAndVehicleIdAndCompanyIdAndProductIdAndSectionId(String requestReferenceNo,
			Integer valueOf, String insuranceId, Integer valueOf2, Integer valueOf3);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndProductIdAndSectionIdAndDiscLoadIdAndVehicleIdOrderByVehicleIdAsc(
			String requestReferenceNo, Integer valueOf, Integer valueOf2, int i, Integer groupId);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndVehicleIdAndSectionIdOrderByVehicleIdAsc(
			String requestReferenceNo, Integer vehicleId, Integer valueOf);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndProductIdAndSectionIdAndVehicleIdOrderByVehicleIdAsc(
			String requestReferenceNo, Integer valueOf, Integer valueOf2, Integer vehicleId);


	Long countByRequestReferenceNoAndVehicleIdAndCompanyIdAndProductIdAndSectionId(String requestReferenceNo,
			Integer valueOf, String insuranceId, Integer valueOf2, Integer valueOf3);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndDiscLoadIdAndVehicleIdAndProductIdAndSectionIdOrderByVehicleIdAsc(
			String requestReferenceNo, int i, Integer vehicleId, Integer valueOf, Integer valueOf2);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndDiscLoadIdAndUserOptOrderByVehicleIdAsc(
			String requestReferenceNo, int i, String string);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndVehicleIdAndProductIdAndSectionIdAndStatusOrderByVehicleIdAsc(
			String requestReferenceNo, int i, int j, Integer vehicleId, Integer valueOf, Integer valueOf2,String status);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndUserOptAndVehicleIdAndProductIdAndSectionIdOrderByVehicleIdAsc(
			String requestReferenceNo, int i, int j, String string, Integer vehicleId, Integer valueOf,
			Integer valueOf2);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndVehicleIdAndProductIdAndSectionIdNotOrderByVehicleIdAsc(
			String requestReferenceNo, int i, int j, Integer groupId, Integer valueOf, int k);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndVehicleIdAndProductIdAndSectionIdOrderByCoverIdAsc(
			String requestReferenceNo, Integer vehicleId, Integer productId, Integer sectionId);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndDiscLoadIdAndTaxIdOrderByVehicleIdAsc(
			String requestReferenceNo, int i, int j);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndProductIdAndSectionIdOrderByVehicleIdAsc(
			String requestReferenceNo, int i, int j, Integer valueOf, Integer valueOf2);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndStatusOrderByVehicleIdAsc(
			String requestReferenceNo, int i, int j, String string);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndProductIdAndSectionIdAndStatusOrderByVehicleIdAsc(
			String requestReferenceNo, int i, int j, Integer valueOf, Integer valueOf2, String string);

	@Transactional
	void deleteByCompanyIdAndProductIdAndRequestReferenceNoAndVehicleIdAndSectionIdAndCoverIdNotIn(
			String companyId,Integer productId,String requestRefNo,Integer vehicleId,Integer sectionId,
			List<Integer> coverId);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndProductIdAndVehicleIdAndSectionIdOrderByVehicleIdAsc(
			String requestReferenceNo, int i, int j, Integer vehicleId, Integer valueOf, Integer valueOf2);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndSectionIdAndDiscLoadIdAndTaxIdAndStatusOrderByVehicleIdAsc(
			String requestReferenceNo, Integer valueOf, int i, int j, String string);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndSectionIdAndDiscLoadIdAndTaxIdAndStatusNotOrderByVehicleIdAsc(
			String requestReferenceNo, Integer valueOf, int i, int j, String string);


	Long countByRequestReferenceNoAndSectionIdNotIn(String requestReferenceNo, List<Integer> optedSectionIds);

	@Transactional
	void deleteByRequestReferenceNoAndSectionIdNotIn(String requestReferenceNo, List<Integer> optedSectionIds);

	List<FactorRateRequestDetails> findByCompanyIdAndProductIdAndRequestReferenceNoAndVehicleIdAndSectionIdAndCoverIdNotIn(String companyId,
			Integer valueOf, String requestRef, Integer vehicleId, Integer sectionId, List<Integer> selectedCoverId);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndVehicleIdAndProductIdAndSectionIdAndStatusNotOrderByVehicleIdAsc(
			String requestReferenceNo, Integer vehicleId, Integer valueOf, Integer valueOf2, String string);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndVehicleIdAndProductIdAndSectionIdOrderByVehicleIdAsc(
			String requestReferenceNo, Integer vehicleId, Integer valueOf, Integer valueOf2);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndProductIdAndSectionIdOrderByVehicleIdAsc(
			String requestReferenceNo, Integer valueOf, Integer valueOf2);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndSectionIdOrderByVehicleIdAsc(String requestReferenceNo,
			Integer valueOf);

	Long countByRequestReferenceNoAndVehicleIdAndSectionIdNotIn(String requestReferenceNo, Integer valueOf,
			List<Integer> optedSectionIds);

	@Transactional
	void deleteByRequestReferenceNoAndVehicleIdAndSectionIdNotIn(String requestReferenceNo, Integer valueOf,
			List<Integer> optedSectionIds);

	List<FactorRateRequestDetails> findByRequestReferenceNo(String referenceNo);

	List<FactorRateRequestDetails> findByCompanyIdAndProductIdAndRequestReferenceNoAndVehicleIdAndSectionIdAndLocationIdAndCoverIdNotIn(
			String companyId, Integer valueOf, String requestRef, Integer vehicleId, Integer sectionId,
			Integer valueOf2, List<Integer> selectedCoverId);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndLocationIdOrderByCoverIdAsc(
			String requestReferenceNo, Integer vehicleId, String companyId, Integer valueOf, Integer valueOf2,
			Integer valueOf3);
	
	List<FactorRateRequestDetails> findByRequestReferenceNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdInAndLocationIdOrderByCoverIdAsc(
			String requestReferenceNo, Integer vehicleId, String companyId, Integer valueOf, List<Integer> valueOf2,
			Integer valueOf3);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndSectionIdAndLocationIdOrderByVehicleIdAsc(
			String requestReferenceNo, Integer valueOf, Integer locationId);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndProductIdAndSectionIdAndVehicleIdAndLocationIdOrderByVehicleIdAsc(
			String requestReferenceNo, Integer valueOf, Integer valueOf2, Integer vehicleId, Integer locationId);

	List<FactorRateRequestDetails> findByRequestReferenceNoAndVehicleIdAndProductIdAndSectionIdAndLocationIdOrderByVehicleIdAsc(
			String requestReferenceNo, Integer vehicleId, Integer valueOf, Integer valueOf2, Integer locationId);

	Long countByRequestReferenceNoAndVehicleIdAndSectionIdNotInAndLocationId(String requestReferenceNo, Integer valueOf,
			List<Integer> optedSectionIds, Integer valueOf2);

	void deleteByRequestReferenceNoAndVehicleIdAndSectionIdNotInAndLocationId(String requestReferenceNo,
			Integer valueOf, List<Integer> optedSectionIds, Integer valueOf2);

	Long countByRequestReferenceNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndLocationId(
			String requestReferenceNo, Integer valueOf, String insuranceId, Integer valueOf2, Integer valueOf3,
			Integer valueOf4);

	void deleteByRequestReferenceNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndLocationId(
			String requestReferenceNo, Integer valueOf, String insuranceId, Integer valueOf2, Integer valueOf3,
			Integer valueOf4);

	
}
