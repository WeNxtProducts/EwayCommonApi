package com.maan.eway.renewal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.renewal.req.GetPolicyBySourceReq;
import com.maan.eway.renewal.req.RenewalTrackReq;
import com.maan.eway.renewal.res.BranchForRenewalTrack;
import com.maan.eway.renewal.res.GetPolicyBySourceRes;
import com.maan.eway.renewal.res.ProductByBranch;
import com.maan.eway.renewal.service.RenewalTrackingService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/renewaltrack")
@Api(tags = "Track the renewal policy")
public class RenewalTrackingController {

	@Autowired
	private RenewalTrackingService service;
	
	    @PostMapping("/getdivisionbycompany")
	    public ResponseEntity<?> getByCompany(@RequestBody RenewalTrackReq req) {
	        if (req.getCompanyId() != null && !req.getCompanyId().trim().isEmpty()) {
	            return ResponseEntity.ok(service.RenewalTrackGetBranch(req));
	        } else {
	            return ResponseEntity.badRequest().body("CompanyId is required");
	        }
	    }

	    @PostMapping("/getproductsbycompanyanddivision")
	    public ResponseEntity<?> getByCompanyAndDivision(@RequestBody RenewalTrackReq req) {
	        if (req.getCompanyId() != null && !req.getCompanyId().trim().isEmpty()
	                && req.getDivisionCode() != null && !req.getDivisionCode().trim().isEmpty()) {
	            return ResponseEntity.ok(service.GetRenewalDetailsByDivsion2(req));
	        } else {
	            return ResponseEntity.badRequest().body("CompanyId and DivisionCode are required");
	        }
	    }

	    @PostMapping("/getsourcesbyproduct")
	    public ResponseEntity<?> getByCompanyDivisionProduct(@RequestBody RenewalTrackReq req) {
	        if (req.getCompanyId() != null && !req.getCompanyId().trim().isEmpty()
	                && req.getDivisionCode() != null && !req.getDivisionCode().trim().isEmpty()
	                && req.getProductCode() != null && !req.getProductCode().trim().isEmpty()) {
	            return ResponseEntity.ok(service.RenewalTrackAgentRes2(req));
	        } else {
	            return ResponseEntity.badRequest().body("CompanyId, DivisionCode, and ProductCode are required");
	        }
	    }

	    @PostMapping("/getpolicydetails")
	    public ResponseEntity<?> getByAllParams(@RequestBody RenewalTrackReq req) {
	        if (req.getCompanyId() != null && !req.getCompanyId().trim().isEmpty()
	                && req.getDivisionCode() != null && !req.getDivisionCode().trim().isEmpty()
	                && req.getProductCode() != null && !req.getProductCode().trim().isEmpty()
	                && req.getSourceCode() != null && !req.getSourceCode().trim().isEmpty()) {
	            return ResponseEntity.ok(service.RenewalTrackPolicyDetailsBySource(req));
	        } else {
	            return ResponseEntity.badRequest().body("CompanyId, DivisionCode, ProductCode, and SourceCode are required");
	        }
	    }
	    
	    @PostMapping("/getproductsbysource")
	   public ResponseEntity<?> getProductsBySource(@RequestBody RenewalTrackReq req) {
	    	 if (req.getCompanyId() != null && !req.getCompanyId().trim().isEmpty()
		                && req.getDivisionCode() != null && !req.getDivisionCode().trim().isEmpty()
		                && req.getSourceCode() != null && !req.getSourceCode().trim().isEmpty()) {
		            return ResponseEntity.ok(service.getProductsBySource(req));
		        } else {
		            return ResponseEntity.badRequest().body("CompanyId, DivisionCode and SourceCode are required");
		        }
	    }
	    
}
