package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.CommonDataDetails;

import com.maan.eway.bean.PremiaApiDropdownMaster;
import com.maan.eway.bean.PremiaApiDropdownMasterId;

public interface PremiaApiDropdownMasterRepository  extends JpaRepository<PremiaApiDropdownMaster,PremiaApiDropdownMasterId > , JpaSpecificationExecutor<PremiaApiDropdownMaster> {
 
	//EaglePremiaIntegration findBycompanyIdAnditemTypeAnditemId(Integer company,String itemtype,Integer itemid);
	PremiaApiDropdownMaster findByCompanyIdAndItemTypeAndItemId(Integer companyId, String itemType, Integer itemId);

}
