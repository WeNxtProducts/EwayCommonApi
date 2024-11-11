/*
 * Java domain class for entity "LoginMaster" 
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

import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginBranchMasterId;
import com.maan.eway.bean.LoginMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.maan.eway.bean.LoginMasterId;
/**
 * <h2>LoginMasterRepository</h2>
 *
 * createdAt : 2022-08-24 - Time 12:58:27
 * <p>
 * Description: "LoginMaster" Repository
 */
 
 
 
public interface LoginBranchMasterRepository  extends JpaRepository<LoginBranchMaster,LoginBranchMasterId > , JpaSpecificationExecutor<LoginBranchMaster> {

	List<LoginBranchMaster> findByLoginId(String loginId);

	LoginBranchMaster findByLoginIdAndBranchCodeAndCompanyId(String loginId, String branchCode,
			String insuranceId);

	List<LoginBranchMaster> findByLoginIdOrderByUpdatedDateDesc(String loginId);

	List<LoginBranchMaster> findByLoginIdAndCompanyIdOrderByBranchCodeAsc(String loginId, String companyId);

	long countByLoginId(String loginId);

	LoginBranchMaster findByBrokerBranchCodeAndLoginIdAndBranchCodeAndCompanyId(String brokerBranchCode, String loginId,
			String branchCode, String companyId);

	LoginBranchMaster findByBrokerBranchCodeAndLoginIdAndCompanyId(String brokerBranchCode, String loginId,
			String insuranceId);

	List<LoginBranchMaster> findByLoginIdAndCompanyIdAndBranchCode(String loginId, String companyId, String branchCode);

	LoginBranchMaster findByLoginIdAndBranchCode(String loginId, String branchCode);

	List<LoginBranchMaster> findByLoginIdAndStatus(String loginId, String string);


	LoginBranchMaster findByLoginIdAndBrokerBranchCodeAndCompanyId(String loginId, String brokerBranchCode,
			String companyId);

	List<LoginBranchMaster> findByLoginIdOrderByBranchCodeAsc(String loginId);

	List<LoginBranchMaster> findByLoginIdAndBranchCodeOrderByBranchCodeAsc(String loginId, String branchCode);

	LoginBranchMaster findByBrokerBranchCodeAndAgencyCodeAndBranchCodeAndCompanyId(String brokerBranchCode,
			Integer valueOf, String branchCode, String companyId);

	List<LoginBranchMaster> findByLoginIdAndCompanyId(String loginId, String companyId);
	
	 List<LoginBranchMaster> findByLoginIdAndCompanyIdAndBranchCodeAndBranchNameNotAndStatus(String login_id, String company_id, String branchCode, String branchName,String status);

	LoginBranchMaster findByBranchCodeAndLoginIdAndCompanyId(String branchCode, String loginId, String companyId);


	



	

}
