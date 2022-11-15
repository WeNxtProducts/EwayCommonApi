package com.maan.eway.master.dropdown.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.master.dropdown.req.ColorChangeStatusReq;
import com.maan.eway.master.dropdown.req.MotorColorGetAllReq;
import com.maan.eway.master.dropdown.req.MotorColorGetReq;
import com.maan.eway.master.dropdown.req.MotorColorSaveReq;
import com.maan.eway.master.dropdown.res.MotorColorGetRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

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