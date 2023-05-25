package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.master.req.EndorsementChangeStatusReq;
import com.maan.eway.master.req.EndorsementMasterDropdownReq;
import com.maan.eway.master.req.EndorsementMasterGetReq;
import com.maan.eway.master.req.EndorsementMasterGetallReq;
import com.maan.eway.master.req.EndorsementMasterSaveReq;
import com.maan.eway.master.res.EndorsementMasterGetallRes;
import com.maan.eway.master.res.EndorsementMasterRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

public interface EndorsementMasterService {

	List<Error> validateEndorsement(EndorsementMasterSaveReq req);

	SuccessRes saveEndorsement(EndorsementMasterSaveReq req);

	List<EndorsementMasterGetallRes> getallEndorsement(EndorsementMasterGetallReq req);

	List<EndorsementMasterGetallRes> getActiveEndorsement(EndorsementMasterGetallReq req);

	EndorsementMasterRes getByEndorsementId(EndorsementMasterGetReq req);

	SuccessRes changeStatusOfEndorsement(EndorsementChangeStatusReq req);

	List<DropDownRes> getEndorsementMasterDropdown(EndorsementMasterDropdownReq req);

	List<EndorsementMasterGetallRes> getallBrokerEndorsement(EndorsementMasterGetallReq req);


}
