package com.maan.eway.notif.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.notif.req.SmsGetReq;
import com.maan.eway.notif.req.SmsInsertReq;
import com.maan.eway.notif.res.SmsMasterGetRes;
import com.maan.eway.res.SuccessRes;

public interface SmsMasterService {

	List<Error> validatesmsmaster(SmsInsertReq req);

	SuccessRes insertsmsmaster(SmsInsertReq req);

	SmsMasterGetRes getbysmsid(SmsGetReq req);

}
