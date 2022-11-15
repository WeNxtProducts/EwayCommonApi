package com.maan.eway.master.dropdown.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.master.dropdown.req.MotorMakeChangeStatusReq;
import com.maan.eway.master.dropdown.req.MotorMakeGetAllReq;
import com.maan.eway.master.dropdown.req.MotorMakeGetReq;
import com.maan.eway.master.dropdown.req.MotorMakeSaveReq;
import com.maan.eway.master.dropdown.res.MotorMakeGetRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

public interface MotorMakeMasterService {
	List<DropDownRes> getMotorMakeDropdown();
/*
	List<Error> validateMakeMotor(MotorMakeSaveReq req);

	SuccessRes saveMakeMotor(MotorMakeSaveReq req);

	MotorMakeGetRes getMakeId(MotorMakeGetReq req);

	List<MotorMakeGetRes> getallMotorMake(MotorMakeGetAllReq req);

	List<MotorMakeGetRes> getactiveMotorMake(MotorMakeGetAllReq req);

	SuccessRes changeStatusOfMotorMake(MotorMakeChangeStatusReq req);

*/

}