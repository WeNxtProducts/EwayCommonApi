package com.maan.eway.auth.service;

import javax.servlet.http.HttpServletRequest;

import com.maan.eway.auth.dto.ChangePasswordReq;
import com.maan.eway.auth.dto.CommonLoginRes;
import com.maan.eway.auth.dto.ForgetPasswordReq;
import com.maan.eway.auth.dto.LoginRequest;
import com.maan.eway.auth.dto.LogoutRequest;
import com.maan.eway.res.SuccessRes;

public interface AuthendicationService {

	CommonLoginRes checkUserLogin(LoginRequest mslogin, HttpServletRequest http);
	
	String LoginChangePassword(ChangePasswordReq req);

	CommonLoginRes logout(LogoutRequest mslogin);

	SuccessRes LoginForgetPassword(ForgetPasswordReq req);


/*	CommonCrmRes LoginChangePassword(ChangePasswordReq req);

	CommonCrmRes logout(LogoutRequest mslogin);

	SuccessRes InsertLogin(InsertLoginMasterReq req);

	LoginEncryptResponse getLoginEncryptResponse(PaymentResUrlReq request, HttpServletRequest http);

	


	LoginGetRes getloginid(LoginGetReq req);

	List<LoginDetailsGetRes> getLogintDetails(LoginDetailsGetReq req); */


}
