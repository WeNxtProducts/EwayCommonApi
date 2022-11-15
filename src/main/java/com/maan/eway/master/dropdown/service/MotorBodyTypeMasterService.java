package com.maan.eway.master.dropdown.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.master.dropdown.req.BodyTypeChangeStatusReq;
import com.maan.eway.master.dropdown.req.BodyTypeDropDownReq;
import com.maan.eway.master.dropdown.req.MotorBodySaveReq;
import com.maan.eway.master.dropdown.req.MotorBodyTypeGetAllReq;
import com.maan.eway.master.dropdown.req.MotorBodyTypeGetReq;
import com.maan.eway.master.dropdown.req.MotorMakeGetAllReq;
import com.maan.eway.master.dropdown.res.MotorBodyTypeGetRes;
import com.maan.eway.master.dropdown.res.MotorMakeGetRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

public interface MotorBodyTypeMasterService {
/*
	List<Error> validateMakeMotor(MotorBodySaveReq req);

	SuccessRes saveMakeMotor(MotorBodySaveReq req);

	MotorBodyTypeGetRes getMotorBody(MotorBodyTypeGetReq req);

	List<MotorBodyTypeGetRes> getallMotorBody(MotorBodyTypeGetAllReq req);

	List<MotorBodyTypeGetRes> getactiveMotorBody(MotorBodyTypeGetAllReq req);

	SuccessRes changeStatusOfBodyType(BodyTypeChangeStatusReq req);
*/
	List<DropDownRes> getBodyTypeMasterDropdown(BodyTypeDropDownReq req);

	List<DropDownRes> getInduvidualBodyTypeMasterDropdown();


}