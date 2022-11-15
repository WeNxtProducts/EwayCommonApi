package com.maan.eway.repository;

import java.math.BigDecimal;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.MenuMaster;


public interface MenuMasterRepository  extends JpaRepository<MenuMaster, Integer>, JpaSpecificationExecutor<MenuMaster> {


                                                                              
}
