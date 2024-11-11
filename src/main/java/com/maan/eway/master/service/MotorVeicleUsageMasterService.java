/*
*  Copyright (c) 2019. All right reserved
* Created on 2022-10-11 ( Date ISO 2022-10-11 - Time 15:28:59 )
* Generated by Telosys Tools Generator ( version 3.3.0 )
*/
package com.maan.eway.master.service;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import com.maan.eway.common.res.MotorVehicleUsageMasterGetRes;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.MotorVehicleUsageChangeStatusReq;
import com.maan.eway.master.req.MotorVehicleUsageMasterGetReq;
import com.maan.eway.master.req.MotorVehicleUsageMasterGetallReq;
import com.maan.eway.master.req.MotorVehicleUsageMasterSaveReq;
import com.maan.eway.master.req.UsageDropDownReq;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

/**
* <h2>PersonalInfoServiceimpl</h2>
*/
public interface MotorVeicleUsageMasterService  {

	List<String> validateMotorVehicleUsageDetails(MotorVehicleUsageMasterSaveReq req);

	SuccessRes saveMotorVehicleUsageDetails(MotorVehicleUsageMasterSaveReq req);

	MotorVehicleUsageMasterGetRes getMotorVehicleDetails(MotorVehicleUsageMasterGetReq req);

	List<MotorVehicleUsageMasterGetRes> getallMotorVehicleDetails(MotorVehicleUsageMasterGetallReq req);

	List<MotorVehicleUsageMasterGetRes> getactiveMotorVehicleDetails(MotorVehicleUsageMasterGetallReq req);

	
	SuccessRes changeStatusOfVehicleUsage(MotorVehicleUsageChangeStatusReq req);



	List<DropDownRes> getInduvidualVehicleUsageDropdown( UsageDropDownReq req);

	List<DropDownRes> getVehicleUsageDropdown(UsageDropDownReq req);

	
	
}
