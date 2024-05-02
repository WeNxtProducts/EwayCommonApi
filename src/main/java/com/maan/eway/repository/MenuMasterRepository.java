package com.maan.eway.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.google.common.base.Optional;
import com.maan.eway.bean.MenuMaster;
import com.maan.eway.bean.MenuMasterId;


public interface MenuMasterRepository  extends JpaRepository<MenuMaster, MenuMasterId>, JpaSpecificationExecutor<MenuMaster> {

	Optional<MenuMaster> findFirstByOrderByMenuIdDesc();
    Optional<MenuMaster> findByMenuId(int nextMenuId);
	List<MenuMaster> findByParentMenuAndUsertypeAndStatusAndDisplayYnAndCompanyId(String id,String UserType ,String Status ,String Display,String cmp_id);
	
	List<MenuMaster>  findByMenuIdAndCompanyId(Integer id,String comp_id);
	
	
                                                                              
}
