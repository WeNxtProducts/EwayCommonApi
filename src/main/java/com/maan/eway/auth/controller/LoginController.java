package com.maan.eway.auth.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.auth.dto.CommonLoginRes;
import com.maan.eway.auth.dto.LoginRequest;
import com.maan.eway.auth.dto.LogoutRequest;
import com.maan.eway.auth.service.AuthendicationService;
import com.maan.eway.auth.service.LoginValidatedService;
import com.maan.eway.auth.token.EncryDecryService;
import com.maan.eway.auth.token.passwordEnc;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.error.Error;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(  tags="LOGIN : Login Token Creation", description = "API.")
@RequestMapping("/authentication")
public class LoginController {
	
	@Autowired
	private AuthendicationService authservice;
	@Autowired
	private LoginValidatedService loginValidationComponent;
	@Autowired
	private PrintReqService reqPrinter;
	@Autowired
	private LoginMasterRepository loginRepo;
	
	@PostMapping("/login")
	@ApiOperation(value="This method is to Create Token For Access Other Apis")
	public ResponseEntity<CommonLoginRes> getloginToken(@RequestBody LoginRequest mslogin, HttpServletRequest http)  {
		CommonLoginRes res = new CommonLoginRes();
		reqPrinter.reqPrint(mslogin);
		res =loginValidationComponent.loginInputValidation(mslogin); 
		if(res.getErrorMessage()!=null &&  res.getErrorMessage().size()>0 ) {
			return new ResponseEntity<CommonLoginRes>(res, HttpStatus.OK);
		} 
		
		res = authservice.checkUserLogin(mslogin,http);
		if(res.getCommonResponse() !=null) {
			return new ResponseEntity<CommonLoginRes>(res, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
	}

	@PostMapping("/logout")
	@ApiOperation(value="This method is used to Logout From Screen")
	public CommonLoginRes logout(@RequestBody LogoutRequest mslogin)  {		
		return authservice.logout(mslogin);
	}
	
	
	
	
	@PostMapping("/doauth")
	@ApiOperation(value="This method is to Create Token For Access Other Apis")
	public ResponseEntity<CommonLoginRes> getloginTokenEncrypt(@RequestBody LoginRequest msloginx, HttpServletRequest http)  {
		
		Map<String,Object> encValue=new HashMap<String,Object>();
		try {
			
			String decrypt = EncryDecryService.decrypt(URLDecoder.decode(msloginx.getEncryptionkey(), "UTF-8"));
			if (StringUtils.isNotBlank(decrypt) && decrypt.indexOf(",") != -1) {
				String[] split = decrypt.replaceAll("\\{", "").replaceAll("\\}", "").split(",");
				if (split.length > 0) {
					for (int i = 0; i < split.length; i++) {
						String text=split[i].replaceAll("\n", "").replaceAll("\r", "").replaceAll("\"", "");
						String[] keyValuePair = text.split(":");
						encValue.put(keyValuePair[0].trim(), keyValuePair[1].trim());
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		List<Error> errors=null;
		if(encValue.get("TinyUrlId")!=null) {
			
			errors=loginValidationComponent.validateTinyUrlId(encValue.get("TinyUrlId"));
			if(errors==null || errors.size()==0 ) {
				LoginRequest mslogin=new LoginRequest();
				LoginMaster login = loginRepo.findByLoginId(encValue.get("LoginId").toString());
				mslogin.setLoginId(encValue.get("LoginId").toString());
				mslogin.setPassword(login.getPassword());
				mslogin.setReLoginKey("Y");
				ResponseEntity<CommonLoginRes> getloginToken = getloginToken(mslogin,http);
				CommonLoginRes body = getloginToken.getBody();
				body.setAdditionalInfo(encValue);
				return getloginToken;
			}else {
				CommonLoginRes body=new CommonLoginRes();
				body.setErrorMessage(errors);
				body.setIsError(true);
				return new ResponseEntity<CommonLoginRes>(body, HttpStatus.OK);
			}
				
		 
		}
		CommonLoginRes body=new CommonLoginRes();
		Error err=new Error();
		err.setCode("9844");
		err.setField("TinyUrl");
		err.setMessage("Tiny Url is Not Valid");
		errors=new ArrayList<Error>();
		errors.add(err);
		body.setErrorMessage(errors);
		body.setIsError(true);
		return new ResponseEntity<CommonLoginRes>(body, HttpStatus.OK);
		
	}
	
}
