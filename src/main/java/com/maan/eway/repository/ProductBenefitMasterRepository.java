package com.maan.eway.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.ExclusionMaster;
import com.maan.eway.bean.ExclusionMasterId;
import com.maan.eway.bean.ProductBenefitMaster;
import com.maan.eway.bean.ProductBenefitMasterId;


public interface ProductBenefitMasterRepository extends JpaRepository<ProductBenefitMaster, ProductBenefitMasterId>, JpaSpecificationExecutor<ProductBenefitMaster>{

	
	
}
