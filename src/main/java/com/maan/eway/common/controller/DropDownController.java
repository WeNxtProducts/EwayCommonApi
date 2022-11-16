package com.maan.eway.common.controller;

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

import com.maan.eway.common.req.NcdDetailsGetReq;
import com.maan.eway.common.service.DropDownService;
import com.maan.eway.master.dropdown.req.CityDropDownReq;
import com.maan.eway.master.dropdown.req.RegionDropDownReq;
import com.maan.eway.master.dropdown.req.StateDropDownReq;
import com.maan.eway.req.SubUserTypeReq;
import com.maan.eway.res.CommonRes;
import com.maan.eway.res.DropDownRes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/dropdown")
@Api(tags = "MASTER : Drop Down Controller", description = "API's")

public class DropDownController {

	@Autowired
	private DropDownService dropDownService;


	@GetMapping("/insurancetype")
	@ApiOperation(value = "This method is to Cover Note Drop Down")
	public ResponseEntity<CommonRes> insuranceType() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.insuranceType();
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
	
	@GetMapping("/gettabledetails")
	@ApiOperation(value = "This method is to Table Details Drop Down")
	public ResponseEntity<CommonRes> getTableDetails() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.getTableDetails();
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
	
	@GetMapping("/citylimit")
	@ApiOperation(value = "This method is to Cover Note Drop Down")
	public ResponseEntity<CommonRes> cityLimit() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.cityLimit();
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
	@GetMapping("/borrowertype")
	@ApiOperation(value = "This method is to Cover Note Drop Down")
	public ResponseEntity<CommonRes> borrowerType() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.borrowerType();
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

	@GetMapping("/title")
	@ApiOperation(value = "This method is to Cover Note Drop Down")
	public ResponseEntity<CommonRes> title() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.title();
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
	
	@GetMapping("/insuranceclass")
	@ApiOperation(value = "This method is to Cover Note Drop Down")
	public ResponseEntity<CommonRes> insuranceClass() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.insuranceClass();
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
	
	@GetMapping("/covernotetype")
	@ApiOperation(value = "This method is to Cover Note Drop Down")
	public ResponseEntity<CommonRes> coverNoteType() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.coverNoteType();
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

	@GetMapping("/paymentmode")
	@ApiOperation(value = "This method is to Payment Mode Drop Down")
	public ResponseEntity<CommonRes> paymentmode() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.paymentmode();
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

	@GetMapping("/endorsementtype")
	@ApiOperation(value = "This method is to Endorsement Type Drop Down")
	public ResponseEntity<CommonRes> endorsementtype() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.endorsementtype();
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

	@GetMapping("/discounttypeoffered")
	@ApiOperation(value = "This method is to Discount Type Offered Drop Down")
	public ResponseEntity<CommonRes> discounttypeoffered() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.discounttypeoffered();
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

	@GetMapping("/istaxexcempted")
	@ApiOperation(value = "This method is to Tax Excepmted Drop Down")
	public ResponseEntity<CommonRes> taxexcempted() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.taxexcempted();
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

	@GetMapping("/taxexcemptiontype")
	@ApiOperation(value = "This method is to Tax Excepmtion Type Drop Down")
	public ResponseEntity<CommonRes> taxexcemptiontype() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.taxexcemptiontype();
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

	@GetMapping("/policyholdertype")
	@ApiOperation(value = "This method is to Policy Holder Type Drop Down")
	public ResponseEntity<CommonRes> policyholdertype() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.policyholdertype();
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

	@GetMapping("/policyholderidtype")
	@ApiOperation(value = "This method is to Policy Holder ID Type Drop Down")
	public ResponseEntity<CommonRes> policyholderidtype() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.policyholderidtype();
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

	@GetMapping("/policyholdergender")
	@ApiOperation(value = "This method is to Policy Holder Gender Drop Down")
	public ResponseEntity<CommonRes> policyholdergender() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.policyholdergender();
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

	@GetMapping("/nametitle")
	@ApiOperation(value = "This method is to Name Title Drop Down")
	public ResponseEntity<CommonRes> nametitle() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.nametitle();
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

	@GetMapping("/notificationtype")
	@ApiOperation(value = "This method is to Notification Type Drop Down")
	public ResponseEntity<CommonRes> notificationtype() {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.notificationtype();
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

	@GetMapping("/getallcountries")
	@ApiOperation(value = "This method is get all Countries Drop Down")

	public ResponseEntity<CommonRes> getAllCountries() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getCountryDropdown();
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

	@PostMapping("/getallregion")
	@ApiOperation(value = "This method is get all Region Drop Down")

	public ResponseEntity<CommonRes> getRegionDropdown(@RequestBody RegionDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getRegionDropdown(req);
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

	@PostMapping("/getallstate")
	@ApiOperation(value = "This method is get all State Drop Down")

	public ResponseEntity<CommonRes> getStateDropdown(@RequestBody StateDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getStateDropdown(req);
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
	
	
	@PostMapping("/getallcity")
	@ApiOperation(value = "This method is get all City Drop Down")

	public ResponseEntity<CommonRes> getCityDropdown(@RequestBody CityDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getCityDropdown(req);
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

	@GetMapping("/motorcategory")
	@ApiOperation(value = "This method is get all Motor Category Drop Down")
	public ResponseEntity<CommonRes> getMotorCategory() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getMotorCategory();
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

	@GetMapping("/motortype")
	@ApiOperation(value = "This method is get all Motor Type Drop Down")
	public ResponseEntity<CommonRes> getMotorType() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getMotorType();
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

	@GetMapping("/motorusage")
	@ApiOperation(value = "This method is get all Motor Usage Drop Down")
	public ResponseEntity<CommonRes> getMotorUsage() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getMotorUsage();
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
	
	@GetMapping("/ownercategory")
	@ApiOperation(value = "This method is get all Owner Category Drop Down")
	public ResponseEntity<CommonRes> ownerCategory() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.ownerCategory();
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
	
	@GetMapping("/fleettype")
	@ApiOperation(value = "This method is get all Fleet Type Drop Down")
	public ResponseEntity<CommonRes> fleetType() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.fleetType();
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
	
	
	@GetMapping("/reinsurancecategory")
	@ApiOperation(value = "This method is get all Reinsurance Category Drop Down")
	public ResponseEntity<CommonRes> reinsuranceCategory() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.reinsuranceCategory();
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
	
	@GetMapping("/participanttype")
	@ApiOperation(value = "This method is get all Participant Type Drop Down")
	public ResponseEntity<CommonRes> participantType() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.participantType();
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
	
	
	
	@GetMapping("/reinsuranceform")
	@ApiOperation(value = "This method is get all Reinsurance Form Drop Down")
	public ResponseEntity<CommonRes> reinsuranceForm() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.reinsuranceForm();
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
	
	
	@GetMapping("/reinsurancetype")
	@ApiOperation(value = "This method is get all Reinsurance Type Drop Down")
	public ResponseEntity<CommonRes> reinsuranceType() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.reinsuranceType();
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
	
	@GetMapping("/claimformdullyfilled")
	@ApiOperation(value = "This method is get all Claim Form Dully Filled Drop Down")
	public ResponseEntity<CommonRes> claimformdullyfilled() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.claimformdullyfilled();
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
	
	
	@GetMapping("/lostassessmentoption")
	@ApiOperation(value = "This method is get all Lost Assessment Option Drop Down")
	public ResponseEntity<CommonRes> lostassessmentoption() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.lostassessmentoption();
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

	@GetMapping("/assessoridtype")
	@ApiOperation(value = "This method is get all Assessor Id Type Drop Down")
	public ResponseEntity<CommonRes> assessoridtype() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.assessoridtype();
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

	@GetMapping("/claimantcategory")
	@ApiOperation(value = "This method is get all Calimant Category Drop Down")
	public ResponseEntity<CommonRes> claimantCategory() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.claimantCategory();
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

	

	@GetMapping("/claimanttype")
	@ApiOperation(value = "This method is get all Calimant Type Drop Down")
	public ResponseEntity<CommonRes> claimantType() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.claimantType();
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
	

	@GetMapping("/claimantidtype")
	@ApiOperation(value = "This method is get all Calimant Id Type Drop Down")
	public ResponseEntity<CommonRes> claimantIdType() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.claimantIdType();
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
	

	@GetMapping("/isreassessment")
	@ApiOperation(value = "This method is get all Is Reassessment Drop Down")
	public ResponseEntity<CommonRes> isreassessment() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.isreassessment();
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
	
	@GetMapping("/offeraccepted")
	@ApiOperation(value = "This method is get all Offer Accepted Drop Down")
	public ResponseEntity<CommonRes> offerAccepted() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.offerAccepted();
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
	
	
	@GetMapping("/partiesnotified")
	@ApiOperation(value = "This method is get all Parties Notified Drop Down")
	public ResponseEntity<CommonRes> partiesNotified() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.partiesNotified();
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
	
	@GetMapping("/claimresultedlitigation")
	@ApiOperation(value = "This method is get all Claim Resulted Litigation Drop Down")
	public ResponseEntity<CommonRes> claimResultedLitigation() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.claimResultedLitigation();
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
	
	@GetMapping("/tonnage")
	@ApiOperation(value = "This method is get all Tonnage Drop Down")
	public ResponseEntity<CommonRes> tonnage() {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.tonnage();
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
	
	
	@PostMapping("/getncddetails")
	public ResponseEntity<CommonRes> getNcdDetails(@RequestBody NcdDetailsGetReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getNcdDetails(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/language")
	public ResponseEntity<CommonRes> getLanguage() {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getLanguage();
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	@GetMapping("/fueltype")
	public ResponseEntity<CommonRes> getFuelType() {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getFuelType();
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

}
