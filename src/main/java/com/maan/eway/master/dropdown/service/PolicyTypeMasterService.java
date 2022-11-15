package com.maan.eway.master.dropdown.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.master.dropdown.req.PolicyTypeMasterGetAllReq;
import com.maan.eway.master.dropdown.req.PolicyTypeMasterGetReq;
import com.maan.eway.master.dropdown.req.PolicyTypeMasterSaveReq;
import com.maan.eway.master.dropdown.res.PolicyTypeMasterGetRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

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
