package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.YiConditionDetail;

public interface YiConditionDetailRepository extends JpaRepository<YiConditionDetail,String > , JpaSpecificationExecutor<YiConditionDetail > {

	List<YiConditionDetail> findByQuotationPolicyNo(String policyNo);


}
