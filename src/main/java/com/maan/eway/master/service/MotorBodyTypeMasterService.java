package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.master.req.BodyTypeDropDownReq;
import com.maan.eway.res.DropDownRes;

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