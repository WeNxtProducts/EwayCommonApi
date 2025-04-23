package com.maan.eway.common.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.QuoteInformationDTO;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.QuoteInformationService;
import com.maan.eway.error.Error;

@RestController
@RequestMapping("/api/salesQuote")
public class QuoteInformationController {

    @Autowired
    private QuoteInformationService service;

    @PostMapping("saveQuote")
	public ResponseEntity<?> saveOrUpdate(@RequestBody QuoteInformationDTO dto) {
		QuoteInformationDTO que = service.saveOrUpdate(dto);
		if (que != null) {
			CommonRes res = new CommonRes();
			res.setCommonResponse(que);
			res.setErroCode(0);
			res.setErrorMessage(null);
			return new ResponseEntity<CommonRes>(res, HttpStatus.ACCEPTED);
		} else {
			CommonRes res = new CommonRes();
			res.setCommonResponse(null);
			res.setErroCode(1);
            res.setErrorMessage(Arrays.asList(new Error("E001", "Save Failed", "Unable to save quote")));
			return new ResponseEntity<CommonRes>(res, HttpStatus.ACCEPTED);
		}
	}

    @GetMapping("getAllQuotes")
    public ResponseEntity<?> getAll() {
    	List<QuoteInformationDTO> que = service.findAll();
    	 if (!que.isEmpty()) {
			CommonRes res = new CommonRes();
			res.setCommonResponse(que);
			res.setErroCode(0);
			res.setErrorMessage(null);
			return new ResponseEntity<CommonRes>(res, HttpStatus.ACCEPTED);
		} else {
			CommonRes res = new CommonRes();
			res.setCommonResponse(null);
			res.setErroCode(0);
            res.setErrorMessage(Arrays.asList(new Error("E002", "No Data", "No quote records found")));
			return new ResponseEntity<CommonRes>(res, HttpStatus.NO_CONTENT);
		}
    }

    @GetMapping("/{enquiryId}/{quoteNo}")
    public ResponseEntity<?> getById(@PathVariable String enquiryId, @PathVariable String quoteNo) {
    	Optional<QuoteInformationDTO> que = service.findById(enquiryId, quoteNo);
    	if (que.isPresent()) {
			CommonRes res = new CommonRes();
			res.setCommonResponse(que);
			res.setErroCode(0);
			res.setErrorMessage(null);
			return new ResponseEntity<CommonRes>(res, HttpStatus.ACCEPTED);
		} else {
			CommonRes res = new CommonRes();
			res.setCommonResponse(null);
			res.setErroCode(0);
            res.setErrorMessage(Arrays.asList(new Error("E003", "Not Found", "No quote found for the given ID")));
			return new ResponseEntity<CommonRes>(res, HttpStatus.NO_CONTENT);
		}
       
    }

    @DeleteMapping("/{enquiryId}/{quoteNo}")
    public ResponseEntity<CommonRes> delete(@PathVariable String enquiryId, @PathVariable String quoteNo) {
        service.delete(enquiryId, quoteNo);

        CommonRes res = new CommonRes();
        res.setCommonResponse("Deleted Successfully");
        res.setErroCode(0);
        res.setErrorMessage(null);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
