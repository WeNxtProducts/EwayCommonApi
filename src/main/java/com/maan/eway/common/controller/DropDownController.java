package com.maan.eway.common.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.NcdDetailsGetReq;
import com.maan.eway.common.service.DropDownService;
import com.maan.eway.master.req.BuildingUsageDropDownReq;
import com.maan.eway.master.req.CityDropDownReq;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.master.req.LovPolicyDropDownReq;
import com.maan.eway.master.req.RegionDropDownReq;
import com.maan.eway.master.req.RelationDropDownReq;
import com.maan.eway.master.req.StateDropDownReq;
import com.maan.eway.res.ColummnDropRes;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.res.DropDownRes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/dropdown")
@Api(tags = "MASTER : Drop Down Controller", description = "API's")

public class DropDownController {

	@Autowired
	private DropDownService dropDownService;


//	@PostMapping("/insurancetype")
//	@ApiOperation(value = "This method is to Cover Note Drop Down")
//	public ResponseEntity<CommonRes> insuranceType(@RequestBody LovDropDownReq req) {
//		CommonRes data = new CommonRes();
//
//		List<DropDownRes> res = dropDownService.insuranceType(req);
//		data.setCommonResponse(res);
//		data.setIsError(false);
//		data.setErrorMessage(Collections.emptyList());
//		data.setMessage("Success");
//
//		if (res != null) {
//			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
//		} else {
//			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//		}
//
//	}
//
//	@PostMapping("/gettabledetails")
//	@ApiOperation(value = "This method is to Table Details Drop Down")
//	public ResponseEntity<CommonRes> getTableDetails(@RequestBody LovDropDownReq req) {
//		CommonRes data = new CommonRes();
//
//		List<ColummnDropRes> res = dropDownService.getTableDetails(req);
//		data.setCommonResponse(res);
//		data.setIsError(false);
//		data.setErrorMessage(Collections.emptyList());
//		data.setMessage("Success");
//
//		if (res != null) {
//			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
//		} else {
//			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//		}
//
//	}
	
	@PostMapping("/citylimit")
	@ApiOperation(value = "This method is to Cover Note Drop Down")
	public ResponseEntity<CommonRes> cityLimit(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.cityLimit(req);
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
	@PostMapping("/borrowertype")
	@ApiOperation(value = "This method is to Cover Note Drop Down")
	public ResponseEntity<CommonRes> borrowerType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.borrowerType(req);
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

	@PostMapping("/title")
	@ApiOperation(value = "This method is to Cover Note Drop Down")
	public ResponseEntity<CommonRes> title(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.title(req);
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
	
//	@PostMapping("/insuranceclass")
//	@ApiOperation(value = "This method is to Cover Note Drop Down")
//	public ResponseEntity<CommonRes> insuranceClass(@RequestBody LovDropDownReq req) {
//		CommonRes data = new CommonRes();
//
//		List<DropDownRes> res = dropDownService.insuranceClass();
//		data.setCommonResponse(res);
//		data.setIsError(false);
//		data.setErrorMessage(Collections.emptyList());
//		data.setMessage("Success");
//
//		if (res != null) {
//			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
//		} else {
//			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//		}
//
//	}
	
	@PostMapping("/contructtype")
	@ApiOperation(value = "This method is to Construct Type Drop Down")
	public ResponseEntity<CommonRes> constructType(@RequestBody LovDropDownReq req ) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.constructType(req);
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
	
	@PostMapping("/consecutivedays")
	@ApiOperation(value = "This method is to Construct Type Drop Down")
	public ResponseEntity<CommonRes> consecutiveDays(@RequestBody LovDropDownReq req ) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.consecutiveDays(req);
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
	
	@PostMapping("/buildingtype")
	@ApiOperation(value = "This method is to Construct Type Drop Down")
	public ResponseEntity<CommonRes> buildingType(@RequestBody LovDropDownReq req ) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.buildingType(req);
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
	
	@PostMapping("/covernotetype")
	@ApiOperation(value = "This method is to Cover Note Drop Down")
	public ResponseEntity<CommonRes> coverNoteType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.coverNoteType(req);
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

	@PostMapping("/paymentmode")
	@ApiOperation(value = "This method is to Payment Mode Drop Down")
	public ResponseEntity<CommonRes> paymentmode(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.paymentmode(req);
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

	@PostMapping("/endorsementtype")
	@ApiOperation(value = "This method is to Endorsement Type Drop Down")
	public ResponseEntity<CommonRes> endorsementtype(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.endorsementtype(req);
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

	@PostMapping("/discounttypeoffered")
	@ApiOperation(value = "This method is to Discount Type Offered Drop Down")
	public ResponseEntity<CommonRes> discounttypeoffered(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.discounttypeoffered(req);
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

	@PostMapping("/istaxexcempted")
	@ApiOperation(value = "This method is to Tax Excepmted Drop Down")
	public ResponseEntity<CommonRes> taxexcempted(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.taxexcempted(req);
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

	@PostMapping("/taxexcemptiontype")
	@ApiOperation(value = "This method is to Tax Excepmtion Type Drop Down")
	public ResponseEntity<CommonRes> taxexcemptiontype(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.taxexcemptiontype(req);
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

	@PostMapping("/policyholdertype")
	@ApiOperation(value = "This method is to Policy Holder Type Drop Down")
	public ResponseEntity<CommonRes> policyholdertype(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.policyholdertype(req);
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

	@PostMapping("/policyholderidtype")
	@ApiOperation(value = "This method is to Policy Holder ID Type Drop Down")
	public ResponseEntity<CommonRes> policyholderidtype(@RequestBody LovPolicyDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.policyholderidtype(req);
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

	@PostMapping("/policyholdergender")
	@ApiOperation(value = "This method is to Policy Holder Gender Drop Down")
	public ResponseEntity<CommonRes> policyholdergender(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.policyholdergender(req);
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

	@PostMapping("/nametitle")
	@ApiOperation(value = "This method is to Name Title Drop Down")
	public ResponseEntity<CommonRes> nametitle(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.nametitle(req);
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

	@PostMapping("/notificationtype")
	@ApiOperation(value = "This method is to Notification Type Drop Down")
	public ResponseEntity<CommonRes> notificationtype(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.notificationtype(req);
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


	@PostMapping("/motorcategory")
	@ApiOperation(value = "This method is get all Motor Category Drop Down")
	public ResponseEntity<CommonRes> getMotorCategory(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getMotorCategory(req);
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

	@PostMapping("/motortype")
	@ApiOperation(value = "This method is get all Motor Type Drop Down")
	public ResponseEntity<CommonRes> getMotorType(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getMotorType(req);
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

//	@PostMapping("/motorusage")
//	@ApiOperation(value = "This method is get all Motor Usage Drop Down")
//	public ResponseEntity<CommonRes> getMotorUsage(@RequestBody LovDropDownReq req) {
//
//		CommonRes data = new CommonRes();
//
//		// Save
//		List<DropDownRes> res = dropDownService.getMotorUsage(req);
//		data.setCommonResponse(res);
//		data.setIsError(false);
//		data.setErrorMessage(Collections.emptyList());
//		data.setMessage("Success");
//
//		if (res != null) {
//			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
//		} else {
//			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//		}
//	}
	
	@PostMapping("/ownercategory")
	@ApiOperation(value = "This method is get all Owner Category Drop Down")
	public ResponseEntity<CommonRes> ownerCategory(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.ownerCategory(req);
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
	
	@PostMapping("/fleettype")
	@ApiOperation(value = "This method is get all Fleet Type Drop Down")
	public ResponseEntity<CommonRes> fleetType(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.fleetType(req);
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
	
	
	@PostMapping("/reinsurancecategory")
	@ApiOperation(value = "This method is get all Reinsurance Category Drop Down")
	public ResponseEntity<CommonRes> reinsuranceCategory(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.reinsuranceCategory(req);
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
	
	@PostMapping("/participanttype")
	@ApiOperation(value = "This method is get all Participant Type Drop Down")
	public ResponseEntity<CommonRes> participantType(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.participantType(req);
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
	
	
	
	@PostMapping("/reinsuranceform")
	@ApiOperation(value = "This method is get all Reinsurance Form Drop Down")
	public ResponseEntity<CommonRes> reinsuranceForm(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.reinsuranceForm(req);
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
	
	
	@PostMapping("/reinsurancetype")
	@ApiOperation(value = "This method is get all Reinsurance Type Drop Down")
	public ResponseEntity<CommonRes> reinsuranceType(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.reinsuranceType(req);
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
	
	@PostMapping("/claimformdullyfilled")
	@ApiOperation(value = "This method is get all Claim Form Dully Filled Drop Down")
	public ResponseEntity<CommonRes> claimformdullyfilled(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.claimformdullyfilled(req);
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
	
	
	@PostMapping("/lostassessmentoption")
	@ApiOperation(value = "This method is get all Lost Assessment Option Drop Down")
	public ResponseEntity<CommonRes> lostassessmentoption(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.lostassessmentoption(req);
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

	@PostMapping("/assessoridtype")
	@ApiOperation(value = "This method is get all Assessor Id Type Drop Down")
	public ResponseEntity<CommonRes> assessoridtype(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.assessoridtype(req);
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

	@PostMapping("/claimantcategory")
	@ApiOperation(value = "This method is get all Calimant Category Drop Down")
	public ResponseEntity<CommonRes> claimantCategory(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.claimantCategory(req);
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

	

	@PostMapping("/claimanttype")
	@ApiOperation(value = "This method is get all Calimant Type Drop Down")
	public ResponseEntity<CommonRes> claimantType(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.claimantType(req);
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
	

	@PostMapping("/claimantidtype")
	@ApiOperation(value = "This method is get all Calimant Id Type Drop Down")
	public ResponseEntity<CommonRes> claimantIdType(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.claimantIdType(req);
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
	

	@PostMapping("/isreassessment")
	@ApiOperation(value = "This method is get all Is Reassessment Drop Down")
	public ResponseEntity<CommonRes> isreassessment(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.isreassessment(req);
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
	
	@PostMapping("/offeraccepted")
	@ApiOperation(value = "This method is get all Offer Accepted Drop Down")
	public ResponseEntity<CommonRes> offerAccepted(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.offerAccepted(req);
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
	
	
	@PostMapping("/partiesnotified")
	@ApiOperation(value = "This method is get all Parties Notified Drop Down")
	public ResponseEntity<CommonRes> partiesNotified(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.partiesNotified(req);
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
	
	@PostMapping("/claimresultedlitigation")
	@ApiOperation(value = "This method is get all Claim Resulted Litigation Drop Down")
	public ResponseEntity<CommonRes> claimResultedLitigation(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.claimResultedLitigation(req);
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
	
	@PostMapping("/tonnage")
	@ApiOperation(value = "This method is get all Tonnage Drop Down")
	public ResponseEntity<CommonRes> tonnage(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.tonnage(req);
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
	
	@PostMapping("/language")
	public ResponseEntity<CommonRes> getLanguage(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getLanguage(req);
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
	@PostMapping("/fueltype")
	public ResponseEntity<CommonRes> getFuelType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getFuelType(req);
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
	
	@PostMapping("/plantype")
	public ResponseEntity<CommonRes> getPlanType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getPlanType(req);
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

	@PostMapping("/relationtype")
	public ResponseEntity<CommonRes> getRelationType(@RequestBody RelationDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getRelationType(req);
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
	
	
	@PostMapping("/buildingpurpose")
	public ResponseEntity<CommonRes> getBuildingPurpose(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getBuildingPurpose(req);
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
	
	@PostMapping("/buildingusage")
	public ResponseEntity<CommonRes> getBuildingUsage(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getBuildingUsage(req);
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
