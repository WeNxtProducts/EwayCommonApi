package com.maan.eway.admin.service;

import java.util.List;

import com.maan.eway.admin.req.AttachBrokerBranchReq;
import com.maan.eway.admin.req.AttachCompaniesReq;
import com.maan.eway.admin.req.AttachIssuerBrannchReq;
import com.maan.eway.admin.req.BrokerBranchGetReq;
import com.maan.eway.admin.req.GetAllBrokerBranchReq;
import com.maan.eway.admin.req.GetBrokerBranchReq;
import com.maan.eway.admin.req.IssuerBranchGetReq;
import com.maan.eway.admin.res.BrokerCompanyGetRes;
import com.maan.eway.admin.res.GetBrokerBranchRes;
import com.maan.eway.admin.res.IssuerCompanyGetRes;
import com.maan.eway.admin.res.LoginCreationRes;

public interface LoginBranchService {

	LoginCreationRes attachBrokerBranches(AttachCompaniesReq req);
	LoginCreationRes attachIssuerBranches(AttachIssuerBrannchReq req);
	
	List<BrokerCompanyGetRes> getBrokerBranches(BrokerBranchGetReq req);
	List<IssuerCompanyGetRes> getIssuerBranches(IssuerBranchGetReq req);
	LoginCreationRes attachBrokerCompanyBranch(AttachBrokerBranchReq req);
	GetBrokerBranchRes getBrokerCompanyBranch(GetBrokerBranchReq req);
	List<GetBrokerBranchRes> getallBrokerCompanyBranch(GetAllBrokerBranchReq req);

}
