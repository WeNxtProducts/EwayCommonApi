package com.maan.eway.master.dropdown.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.service.DropDownService;
import com.maan.eway.master.dropdown.req.CityDropDownReq;
import com.maan.eway.master.dropdown.req.ColumnNameDropDownlReq;
import com.maan.eway.master.dropdown.req.RegionDropDownReq;
import com.maan.eway.master.dropdown.req.StateDropDownReq;
import com.maan.eway.master.dropdown.service.OneTimeTableDetailsService;
import com.maan.eway.req.SubUserTypeReq;
import com.maan.eway.res.CommonRes;
import com.maan.eway.res.DropDownRes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/dropdown")
@Api(tags = "MASTER : Drop Down Controller", description = "API's")

public class OneTimeTableDetailsController {

	@Autowired
	private OneTimeTableDetailsService service;

	@GetMapping("/tablename")
	@ApiOperation(value = "This method is to Table Name Drop Down")
	public ResponseEntity<CommonRes> tableName() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.tableName();
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
	
	
	@PostMapping("/columnname")
	@ApiOperation(value = "This method is to Column Name Drop Down")
	public ResponseEntity<CommonRes> columnName(@RequestBody ColumnNameDropDownlReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = service.columnName(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
}
