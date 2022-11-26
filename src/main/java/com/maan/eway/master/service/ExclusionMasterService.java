package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.common.req.ExclusionMasterDropdownReq;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.ExclusionChangeStatusReq;
import com.maan.eway.master.req.ExclusionMasterGetReq;
import com.maan.eway.master.req.ExclusionMasterGetallReq;
import com.maan.eway.master.req.ExclusionMasterSaveReq;
import com.maan.eway.master.res.ExclusionMasterRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

public interface ExclusionMasterService {

	List<Error> validateExclusion(ExclusionMasterSaveReq req);

	SuccessRes saveExclusion(ExclusionMasterSaveReq req);

	List<ExclusionMasterRes> getallExclusion(ExclusionMasterGetallReq req);

	List<ExclusionMasterRes> getActiveExclusion(ExclusionMasterGetallReq req);

	ExclusionMasterRes getByExclusionId(ExclusionMasterGetReq req);

	SuccessRes changeStatusOfExclusion(ExclusionChangeStatusReq req);
	List<DropDownRes> getExclusionMasterDropdown(ExclusionMasterDropdownReq req);

}
