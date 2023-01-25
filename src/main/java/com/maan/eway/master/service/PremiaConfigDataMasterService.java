package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.master.req.PremiaConfigDataMasterGetReq;
import com.maan.eway.master.req.PremiaConfigDataMasterGetallReq;
import com.maan.eway.master.req.PremiaConfigDataMasterSaveReq;
import com.maan.eway.master.res.PremiaConfigDataMasterGetRes;
import com.maan.eway.master.res.PremiaConfigDataMasterGetallRes;
import com.maan.eway.res.SuccessRes;

public interface PremiaConfigDataMasterService {

	List<Error> validatePremiaConfigData(PremiaConfigDataMasterSaveReq req);

	SuccessRes insertPremiaConfigData(PremiaConfigDataMasterSaveReq req);

	PremiaConfigDataMasterGetRes getPremiaConfigData(PremiaConfigDataMasterGetReq req);

	PremiaConfigDataMasterGetallRes getallPremiaConfigData(PremiaConfigDataMasterGetallReq req);

	

}
