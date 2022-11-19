package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.UwQuestionsDetails;
import com.maan.eway.bean.UwQuestionsDetailsId;
public interface UwQuestionsDetailsRepository  extends JpaRepository<UwQuestionsDetails,UwQuestionsDetailsId > , JpaSpecificationExecutor<UwQuestionsDetails> {


	List<UwQuestionsDetails> findByCompanyIdAndProductIdAndRequestReferenceNoAndVehicleId(String companyId, Integer valueOf,
			String requestReferenceNo, Integer valueOf2);



}
