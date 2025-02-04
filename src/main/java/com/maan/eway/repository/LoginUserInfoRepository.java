/*
 * Java domain class for entity "LoginUserInfo" 
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

import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.LoginUserInfoId;
/**
 * <h2>LoginUserInfoRepository</h2>
 *
 * createdAt : 2022-08-24 - Time 12:58:27
 * <p>
 * Description: "LoginUserInfo" Repository
 */
 
 
 
public interface LoginUserInfoRepository  extends JpaRepository<LoginUserInfo,LoginUserInfoId > , JpaSpecificationExecutor<LoginUserInfo> {

	LoginUserInfo findByLoginId(String loginId);



	List<LoginUserInfo> findByCustomerCode(String customerCode);
	
	int countByCompanyNameAndUserMail(String companyName, String userMail);



	int countByCompanyNameAndIdTypeAndIdNumber(String companyName, String idType, String idNumber);

}
