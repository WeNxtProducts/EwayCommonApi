package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.master.req.BrokerEndtSetupMasterChangeStatusReq;
import com.maan.eway.master.req.BrokerEndtSetupMasterGetReq;
import com.maan.eway.master.req.BrokerEndtSetupMasterGetallReq;
import com.maan.eway.master.req.BrokerEndtSetupMasterSaveReq;
import com.maan.eway.master.res.BrokerEndtSetupMasterGetallRes;
import com.maan.eway.res.SuccessRes;

public interface BrokerEndtSetupMasterService {

	List<Error> validateBrokerEndtSetup(BrokerEndtSetupMasterSaveReq req);

	SuccessRes saveBrokerEndtSetup(BrokerEndtSetupMasterSaveReq req);

	List<BrokerEndtSetupMasterGetallRes> getallBrokerEndtSetup(BrokerEndtSetupMasterGetallReq req);

	List<BrokerEndtSetupMasterGetallRes> getActiveBrokerEndtSetup(BrokerEndtSetupMasterGetallReq req);

	BrokerEndtSetupMasterGetallRes getBybrokerEndtSetupid(BrokerEndtSetupMasterGetReq req);

	SuccessRes changeStatusOfBrokerEndtSetup(BrokerEndtSetupMasterChangeStatusReq req);

}
