package com.maan.eway.crm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.auth.dto.LoginRequest;
import com.maan.eway.auth.dto.ProductDropDownRes;
import com.maan.eway.crm.bean.UserLoginResponseData;
import com.maan.eway.crm.service.CrmService;

import io.swagger.annotations.Api;

@RestController
@Api(tags = "CRM : Controller", description = "API.")
@RequestMapping("/crm")
public class CrmController {
	@Autowired
	private CrmService crmService;
	

	
	@PostMapping("/authentication/validateToken")
	public UserLoginResponseData validateToken(@RequestParam("token") String token) {

			  UserLoginResponseData validateTokenForCRM = crmService.validateTokenForCRM(token);
			  return validateTokenForCRM;
	}


	@PostMapping("/productListByUserId")
	public List<ProductDropDownRes> productListByUserId(@RequestBody LoginRequest userData) {

		return crmService.getProductDetailByLoginId(userData.getLoginId(), userData.getCompanyId());
	}
}
