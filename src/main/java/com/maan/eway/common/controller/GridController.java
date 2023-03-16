package com.maan.eway.common.controller;

import java.util.Collections;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.IssuerQuoteReq;
import com.maan.eway.common.req.UpdateLapsedQuoteReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.EserviceCustomerDetailsRes;
import com.maan.eway.common.res.GetAllMotorDetailsRes;
import com.maan.eway.common.res.PortfolioCustomerDetailsRes;
import com.maan.eway.common.res.UpdateLapsedQuoteRes;
import com.maan.eway.common.service.GridService;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.res.CopyQuoteSuccessRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/api")
@Api(tags = "GRID DETAILS", description = "API's")
public class GridController {

	@Autowired
	private PrintReqService reqPrinter;
	
	@Autowired
	private  GridService entityService;
	
	// Quote Grids
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/existingquotedetails")
	public ResponseEntity<CommonRes> getallExistingQuoteDetails(@RequestBody  ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<EserviceCustomerDetailsRes> res = entityService.getallExistingQuoteDetails(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/lapsedquotedetails")
	public ResponseEntity<CommonRes> getallLapsedQuoteDetails(@RequestBody  ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<EserviceCustomerDetailsRes> res = entityService.getallLapsedQuoteDetails(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/rejectedquotedetails")
	public ResponseEntity<CommonRes> getallRejectedQuoteDetails(@RequestBody  ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<EserviceCustomerDetailsRes> res = entityService.getallRejectedQuoteDetails(req);
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
	
	
	// Referral Grids
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/referralpending")
	public ResponseEntity<CommonRes> getallReferralPendingDetails(@RequestBody  ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<EserviceCustomerDetailsRes> res = entityService.getallReferralPendingDetails(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/referralapproved")
	public ResponseEntity<CommonRes> getallReferralApprovedDetails(@RequestBody  ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<EserviceCustomerDetailsRes> res = entityService.getallReferralApprovedDetails(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/referralrejected")
	public ResponseEntity<CommonRes> getallReferralRejectedDetails(@RequestBody  ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<EserviceCustomerDetailsRes> res = entityService.getallReferralRejectedDetails(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/referralrequote")
	public ResponseEntity<CommonRes> getallReferralRequoteDetails(@RequestBody  ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<EserviceCustomerDetailsRes> res = entityService.getallReferralRequoteDetails(req);
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
	// Admin Referrral Grids
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/adminreferralpending")
	public ResponseEntity<CommonRes> getallAdminReferralPendings(@RequestBody  ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<EserviceCustomerDetailsRes> res = entityService.getallAdminReferralPendings(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/adminreferralapproved")
	public ResponseEntity<CommonRes> getallAdminReferralApproved(@RequestBody  ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<EserviceCustomerDetailsRes> res = entityService.getallAdminReferralApproved(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/adminreferralrejected")
	public ResponseEntity<CommonRes> getallAdminReferralRejecteds(@RequestBody  ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<EserviceCustomerDetailsRes> res = entityService.getallAdminReferralRejected(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/adminreferralrequote")
	public ResponseEntity<CommonRes> getallAdminReferralRequote(@RequestBody  ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		List<EserviceCustomerDetailsRes> res = entityService.getallAdminReferralRequote(req);
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

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping("/copyquote")
		public ResponseEntity<CommonRes> copyQuote(@RequestBody CopyQuoteReq req) {
			reqPrinter.reqPrint(req);
			CommonRes data = new CommonRes();
			List<Error> validation = entityService.validateQuotoNo(req);
			// validation
			if (validation != null && validation.size() != 0) {
				data.setCommonResponse(null);
				data.setIsError(true);
				data.setErrorMessage(validation);
				data.setMessage("Failed");
				return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

			} else {
				CopyQuoteSuccessRes res = entityService.copyQuote(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping("/searchmotordata")
		public ResponseEntity<CommonRes> getbyReqRefNo(@RequestBody CopyQuoteReq req) {
			CommonRes data = new CommonRes();
			reqPrinter.reqPrint(req);
			List<GetAllMotorDetailsRes> res = entityService.getbyReqRefNo(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping("/dropdown/copyquoteby")
		public ResponseEntity<CommonRes> copyQuoteByDropdown(@RequestBody CopyQuoteDropDownReq req) {
			CommonRes data = new CommonRes();
			List<DropDownRes> res = entityService.copyQuoteByDropdown(req);
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
		
		
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping("/updatelapsedquote")
		public ResponseEntity<CommonRes> updateLapsedQuoteDetails(@RequestBody  UpdateLapsedQuoteReq req) {
			reqPrinter.reqPrint(req);
			CommonRes data = new CommonRes();
			UpdateLapsedQuoteRes res = entityService.updateLapsedQuoteDetails(req);
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
		
			// Portfolio
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
			@PostMapping("/portfolio/active")
			public ResponseEntity<CommonRes> getallPortfolioActive(@RequestBody ExistingQuoteReq req) {
				reqPrinter.reqPrint(req);
				CommonRes data = new CommonRes();
				List<PortfolioCustomerDetailsRes> res = entityService.getallPortfolioActive(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
			@PostMapping("/portfolio/pending")
			public ResponseEntity<CommonRes> getallPortfolioPending(@RequestBody ExistingQuoteReq req) {
				reqPrinter.reqPrint(req);
				CommonRes data = new CommonRes();
				List<PortfolioCustomerDetailsRes> res = entityService.getallPortfolioPending(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
			@PostMapping("/portfolio/cancelled")
			public ResponseEntity<CommonRes> getallPortfolioCancelled(@RequestBody ExistingQuoteReq req) {
				reqPrinter.reqPrint(req);
				CommonRes data = new CommonRes();
				List<PortfolioCustomerDetailsRes> res = entityService.getallPortfolioCancelled(req);

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

			// Quote Grids
			@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
			@PostMapping("dropdown/issuerquotedetails")
			public ResponseEntity<CommonRes> getallIssuerQuoteDetails(@RequestBody IssuerQuoteReq req) {
				reqPrinter.reqPrint(req);
				CommonRes data = new CommonRes();
				List<DropDownRes> res = entityService.getallIssuerQuoteDetails(req);
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
