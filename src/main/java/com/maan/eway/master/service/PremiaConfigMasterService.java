/*
*  Copyright (c) 2019. All right reserved
* Created on 2022-08-24 ( Date ISO 2022-08-24 - Time 12:58:26 )
* Generated by Telosys Tools Generator ( version 3.3.0 )
*/
package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.master.req.PremiaConfigMasterChangeStatusReq;
import com.maan.eway.master.req.PremiaConfigMasterDropDownReq;
import com.maan.eway.master.req.PremiaConfigMasterGetAllReq;
import com.maan.eway.master.req.PremiaConfigMasterGetReq;
import com.maan.eway.master.req.PremiaConfigMasterSaveReq;
import com.maan.eway.master.req.PremiaTableColumnDropDownReq;
import com.maan.eway.master.res.PremiaConfigMasterRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
/**
* <h2>BankMasterServiceimpl</h2>
*/
public interface PremiaConfigMasterService  {

	List<String> validatePremiaConfig(PremiaConfigMasterSaveReq req);

	SuccessRes insertPremiaConfig(PremiaConfigMasterSaveReq req);

	PremiaConfigMasterRes getPremiaConfig(PremiaConfigMasterGetReq req);

	List<PremiaConfigMasterRes> getallPremiaConfig(PremiaConfigMasterGetAllReq req);

	List<PremiaConfigMasterRes> getactivePremiaConfig(PremiaConfigMasterGetAllReq req);

	SuccessRes changeStatusPremiaConfig(PremiaConfigMasterChangeStatusReq req);

	List<DropDownRes> getPremiaConfigMasterDropdown(PremiaConfigMasterDropDownReq req);

	List<DropDownRes> getPremiaTableDropdown(PremiaTableColumnDropDownReq req);



}
