package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.res.DropDownRes;

public interface MotorColorMasterService {
	
	List<DropDownRes> getColorMasterDropdown();
/*
	List<Error> validateColorMotor(MotorColorSaveReq req);

	SuccessRes saveColor(MotorColorSaveReq req);

	MotorColorGetRes getMotorColor(MotorColorGetReq req);

	List<MotorColorGetRes> getallMotorColor(MotorColorGetAllReq req);

	List<MotorColorGetRes> getactiveMotorColor(MotorColorGetAllReq req);

	SuccessRes changeStatusOfColor(ColorChangeStatusReq req);
*/
}