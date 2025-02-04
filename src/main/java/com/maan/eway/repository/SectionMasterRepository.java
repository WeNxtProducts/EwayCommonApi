/*
 * Java domain class for entity "SectionMaster" 
 * Created on 2022-09-02 ( Date ISO 2022-09-02 - Time 18:14:54 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */
 
 /*
 * Created on 2022-09-02 ( 18:14:54 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.repository;

import java.math.BigDecimal;
import java.util.List;

import com.maan.eway.bean.SectionMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.maan.eway.bean.SectionMasterId;
/**
 * <h2>SectionMasterRepository</h2>
 *
 * createdAt : 2022-09-02 - Time 18:14:54
 * <p>
 * Description: "SectionMaster" Repository
 */
 
 
 
public interface SectionMasterRepository  extends JpaRepository<SectionMaster,SectionMasterId > , JpaSpecificationExecutor<SectionMaster> {

	List<SectionMaster> findTopBySectionIdOrderByAmendIdDesc(Integer valueOf);

	List<SectionMaster> findBySectionId(Integer valueOf);


}
