package com.maan.eway.endorsment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.endorsment.request.EndtMaster;
import com.maan.eway.endorsment.service.EndorsementService;

import io.swagger.annotations.Api;

@RestController
@Api(tags = "NOTIFIACTION : Notifiaction ", description = "API's")
@RequestMapping("/endorsment/")
public class EndorsementController {
	
	@Autowired
	private EndorsementService eservice;
	
	@PostMapping("/cancellation")
	public ResponseEntity<CommonRes> cancelPolicy(@RequestBody Endorsment request) {
	 	CommonRes data = eservice.cancelPolicy(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/endorsementTypes")
	public ResponseEntity<EndtMaster> endorsementTypes(@RequestBody Endorsment request) {
		EndtMaster data = eservice.getEndorsementTypes(request);
	 	if (data != null) {
			return new ResponseEntity<EndtMaster>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/datas")
	public ResponseEntity<List<QuoteCriteriaRes>> endorsementPendingData(@RequestBody Endorsment request) {
		List<QuoteCriteriaRes> data = eservice.endorsementPendingData(request);
	 	if (data != null) {
			return new ResponseEntity<List<QuoteCriteriaRes>>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
}
