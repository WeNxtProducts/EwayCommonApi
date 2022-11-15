package com.maan.eway.auth.service;

import java.util.List;

import com.maan.eway.auth.dto.ChangePasswordReq;
import com.maan.eway.auth.dto.CommonLoginRes;
import com.maan.eway.auth.dto.LoginRequest;
import com.maan.eway.error.Error;

public interface LoginValidatedService {

	CommonLoginRes loginInputValidation(LoginRequest mslogin);

/*	List<Error> LoginChangePwdValidation(ChangePasswordReq req);

	List<Error> InsertLoginValidation(InsertLoginMasterReq req); */

	List<Error> LoginChangePasswordValidation(ChangePasswordReq req); 

}
