package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.master.req.BodyTypeChangeStatusReq;
import com.maan.eway.master.req.BodyTypeDropDownReq;
import com.maan.eway.master.req.MotorBodySaveReq;
import com.maan.eway.master.req.MotorBodyTypeGetAllReq;
import com.maan.eway.master.req.MotorBodyTypeGetReq;
import com.maan.eway.master.req.MotorMakeGetAllReq;
import com.maan.eway.master.res.MotorBodyTypeGetRes;
import com.maan.eway.master.res.MotorMakeGetRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

public interface MotorBodyTypeMasterService {

	List<Error> validateMakeMotor(MotorBodySaveReq req);

	SuccessRes saveMakeMotor(MotorBodySaveReq req);

	MotorBodyTypeGetRes getMotorBody(MotorBodyTypeGetReq req);

	List<MotorBodyTypeGetRes> getallMotorBody(MotorBodyTypeGetAllReq req);

	List<MotorBodyTypeGetRes> getactiveMotorBody(MotorBodyTypeGetAllReq req);

	SuccessRes changeStatusOfBodyType(BodyTypeChangeStatusReq req);

	List<DropDownRes> getBodyTypeMasterDropdown(BodyTypeDropDownReq req);

	List<DropDownRes> getInduvidualBodyTypeMasterDropdown();


}