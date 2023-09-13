package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.maan.eway.bean.YiSectionDetail;

public interface YiSectionDetailRepository extends JpaRepositoryImplementation<YiSectionDetail, String>{

	List<com.maan.eway.bean.YiSectionDetail> findByQuotationPolicyNo(String policyNo);

}
