package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.EaglePremiaIntegration;
import com.maan.eway.bean.EaglePremiaIntegrationId;

public interface EaglePremiaIntegrationRepository  extends JpaRepository<EaglePremiaIntegration,EaglePremiaIntegrationId > , JpaSpecificationExecutor<EaglePremiaIntegration> {
 
	//EaglePremiaIntegration findBycompanyIdAnditemTypeAnditemId(Integer company,String itemtype,Integer itemid);
	EaglePremiaIntegration findByCompanyIdAndItemTypeAndItemId(Integer companyId, String itemType, Integer itemId);

}
