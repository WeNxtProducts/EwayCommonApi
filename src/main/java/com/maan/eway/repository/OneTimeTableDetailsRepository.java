/*
 * Java domain class for entity "ListItemValue" 
 * Created on 2022-08-24 ( Date ISO 2022-08-24 - Time 12:58:27 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */
 
 /*
 * Created on 2022-08-24 ( 12:58:27 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.OneTimeTableDetails;
import com.maan.eway.bean.OneTimeTableDetailsId;
/**
 * <h2>ListItemValueRepository</h2>
 *
 * createdAt : 2022-08-24 - Time 12:58:27
 * <p>
 * Description: "ListItemValue" Repository
 */
 
 
 
public interface OneTimeTableDetailsRepository  extends JpaRepository<OneTimeTableDetails,OneTimeTableDetailsId > , JpaSpecificationExecutor<OneTimeTableDetails> {


	List<OneTimeTableDetails> findByItemTypeAndStatusOrderByItemCodeAsc(String string, String string2);


	OneTimeTableDetails findByItemTypeAndParentId(String string, Integer valueOf);


	OneTimeTableDetails findByItemTypeAndItemCode(String itemType, String inputColumn);


	List<OneTimeTableDetails> findByItemIdAndStatusOrderByItemCodeAsc(Integer valueOf, String string);


	OneTimeTableDetails findByItemTypeAndItemId(String string, Integer valueOf);



}
