package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.res.DropDownRes;

public interface PolicyTypeMasterService {
/*
	List<Error> validatePolicyType(PolicyTypeMasterSaveReq req);

	SuccessRes insertPolicyType(PolicyTypeMasterSaveReq req);

	PolicyTypeMasterGetRes getPolicyType(PolicyTypeMasterGetReq req);

	List<PolicyTypeMasterGetRes> getallPolicyType(PolicyTypeMasterGetAllReq req);

	List<PolicyTypeMasterGetRes> getallactivePolicyType(PolicyTypeMasterGetAllReq req);
*/
	List<DropDownRes> getPolicyTypeMasterDropdown();

}
