package com.maan.eway.admin.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.admin.req.MenuServiceReq;
import com.maan.eway.admin.res.MenuServiceRes;
import com.maan.eway.admin.service.MenuMasterService;
import com.maan.eway.res.CommonRes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "MENU : Menu List", description = "API's")
@RequestMapping("/master")
public class MenuMasterController {

	@Autowired
	private MenuMasterService menuservice;
	
	@PostMapping("/menu")
	@ApiOperation(value="This method is to Display Menu Service")
	public ResponseEntity<CommonRes> menudisplay(@RequestBody MenuServiceReq req){
	CommonRes data = new CommonRes();
	MenuServiceRes res = menuservice.menudisplay(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if(res!=null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	
	}
	
}
