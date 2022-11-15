package com.maan.eway.admin.controller;

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

import com.maan.eway.admin.service.AdminDropDownService;
import com.maan.eway.common.service.DropDownService;
import com.maan.eway.req.SubUserTypeReq;
import com.maan.eway.res.CommonRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SubUserTypeDropDownRes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/dropdown")
@Api(tags = "MASTER : Drop Down Controller", description = "API's")

public class AdminDropDownController {

	@Autowired
	private  AdminDropDownService dropDownService;
	

	// Gender

	@GetMapping("/gender")
	@ApiOperation(value = "This method is to Gender Types Drop Down")
	public ResponseEntity<CommonRes> getgender() {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getgender();
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
	
	@GetMapping("/mobilecodes")
	@ApiOperation(value = "This method is to Gender Types Drop Down")
	public ResponseEntity<CommonRes> getMobileCodes() {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getMobileCodes();
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
	
	@GetMapping("/usertype")
	@ApiOperation(value = "This method is to UserType  Drop Down")
	public ResponseEntity<CommonRes> getUserType() {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getUserType();
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

	@PostMapping("/subusertype")
	@ApiOperation(value = "This method is to SubUserType  Drop Down")
	public ResponseEntity<CommonRes> getUserType(@RequestBody SubUserTypeReq req) {
		CommonRes data = new CommonRes();

		// Save
		List<SubUserTypeDropDownRes> res = dropDownService.getSubUserType(req);
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
	@GetMapping("/constmaterial")
	@ApiOperation(value = "This method is to ConstMaterial Drop Down")
	public ResponseEntity<CommonRes> getConstMaterial() {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getConstMaterial();
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
	
	@GetMapping("/outbuildingconst")
	@ApiOperation(value = "This method is to OutbuildingConst  Drop Down")
	public ResponseEntity<CommonRes> getOutbuildingConst() {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getOutbuildingConst();
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
	@GetMapping("/aboutbuilding")
	@ApiOperation(value = "This method is to AboutBuilding  Drop Down")
	public ResponseEntity<CommonRes> getAboutBuilding() {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getAboutBuilding();
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
	
	@GetMapping("/stateextent")
	@ApiOperation(value = "This method is to StateExtent Drop Down")
	public ResponseEntity<CommonRes> getStateExtent() {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getStateExtent();
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
	
	@GetMapping("/contentname")
	@ApiOperation(value = "This method is to Content Name Drop Down")
	public ResponseEntity<CommonRes> getContentName() {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getContentName();
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
	
	
	@GetMapping("/propertyname")
	@ApiOperation(value = "This method is to Property Name Drop Down")
	public ResponseEntity<CommonRes> getPropertyName() {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getPropertyName();
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
	
	@GetMapping("/producticons")
	@ApiOperation(value = "This method is to UserType  Drop Down")
	public ResponseEntity<CommonRes> getProductIcons() {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getProductIcons();
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
	
	@GetMapping("/calctypes")
	@ApiOperation(value = "This method is to CalcType  Drop Down")
	public ResponseEntity<CommonRes> getCalcTypes() {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getCalcTypes();
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
	
	@GetMapping("/coveragetypes")
	@ApiOperation(value = "This method is to CoverageType  Drop Down")
	public ResponseEntity<CommonRes> getCoverageTypes() {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getCoverageTypes();
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
	
	@GetMapping("/rangeparams")
	@ApiOperation(value = "This method is to CoverageType  Drop Down")
	public ResponseEntity<CommonRes> getRangeParams() {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getRangeParams();
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
	
	
	@GetMapping("/discreteparams")
	@ApiOperation(value = "This method is to CoverageType  Drop Down")
	public ResponseEntity<CommonRes> getDiscreteParams() {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getDiscreteParams();
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
	
	@GetMapping("/businesstype")
	@ApiOperation(value = "This method is to Business Type  Drop Down")
	public ResponseEntity<CommonRes> getBusinessType() {
		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getBusinessType();
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
