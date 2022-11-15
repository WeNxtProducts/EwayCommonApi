package com.maan.eway.auth.service;

import javax.servlet.http.HttpServletRequest;

import com.maan.eway.auth.dto.ChangePasswordReq;
import com.maan.eway.auth.dto.CommonLoginRes;
import com.maan.eway.auth.dto.LoginRequest;

public interface AuthendicationService {

	CommonLoginRes checkUserLogin(LoginRequest mslogin, HttpServletRequest http);
	
	String LoginChangePassword(ChangePasswordReq req);


/*	CommonCrmRes LoginChangePassword(ChangePasswordReq req);

	CommonCrmRes logout(LogoutRequest mslogin);

	SuccessRes InsertLogin(InsertLoginMasterReq req);

	LoginEncryptResponse getLoginEncryptResponse(PaymentResUrlReq request, HttpServletRequest http);

	


	LoginGetRes getloginid(LoginGetReq req);

	List<LoginDetailsGetRes> getLogintDetails(LoginDetailsGetReq req); */


}
