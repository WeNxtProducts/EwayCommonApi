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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.ListItemValueId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
/**
 * <h2>ListItemValueRepository</h2>
 *
 * createdAt : 2022-08-24 - Time 12:58:27
 * <p>
 * Description: "ListItemValue" Repository
 */
 
 
 
public interface ListItemValueRepository  extends JpaRepository<ListItemValue,ListItemValueId > , JpaSpecificationExecutor<ListItemValue> {

//	List<ListItemValue> findByItemTypeAndStatusOrderByItemCodeAsc(String string, String string2);

	ListItemValue findByItemTypeAndItemCodeAndStatus(String string, String productIconId, String string2);

//	List<ListItemValue> findByStatusOrderByItemIdAsc(String string);

//	List<ListItemValue> findByItemTypeAndStatus(String string, String string2);

//	List<ListItemValue> findByItemTypeAndStatusOrderByItemIdAsc(String string, String string2);

//	List<ListItemValue> findByItemTypeAndStatusOrderByItemCodeDesc(String string, String string2);

	ListItemValue findByItemTypeAndItemCode(String string, String gender);

//	List<ListItemValue> findByItemTypeAndStatusOrderByParam2Asc(String userType, String string);

//	List<ListItemValue> findByItemTypeAndStatusAndCompanyIdOrderByItemCodeAsc(String string, String string2,
//			String insuranceId);

	ListItemValue findByItemTypeAndItemCodeAndCompanyId(String string, String categoryId, String companyId);

	List<ListItemValue> findByItemTypeAndStatusAndCompanyIdOrderByItemCodeDesc(String string, String string2,
			String companyId);

	@Query(value = "SELECT ITEM_CODE,ITEM_VALUE FROM eway_list_item_value WHERE ITEM_TYPE='DOMESTIC_KEY_FACTS' AND ITEM_CODE=?1",nativeQuery = true)
	List<Map<String, Object>> getDomesticKeyFactor(String itemCode);

	List<ListItemValue> findByItemTypeAndStatusOrderByItemCodeDesc(String string, String string2);




}
