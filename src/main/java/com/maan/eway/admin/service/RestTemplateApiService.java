package com.maan.eway.admin.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.maan.eway.admin.req.PolicyTypeMasterGetReq;
import com.maan.eway.common.res.DropdownCommonRes;


@Service
public class RestTemplateApiService {

	  private final RestTemplate restTemplate;

	    public RestTemplateApiService(RestTemplate restTemplate) {
	        this.restTemplate = restTemplate;
	    }

	    public DropdownCommonRes callSecondApi(String url, PolicyTypeMasterGetReq policyTypeMasterGetReq,  String token) {
	       

	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", "Bearer " + token); 
	        headers.setContentType(MediaType.APPLICATION_JSON); 

	        HttpEntity<PolicyTypeMasterGetReq> requestEntity = new HttpEntity<>(policyTypeMasterGetReq, headers);
	        ResponseEntity<DropdownCommonRes> responseEntity = 
	                restTemplate.postForEntity(url, requestEntity, DropdownCommonRes.class);
	        if (responseEntity.getStatusCode().is2xxSuccessful()) {
	            return responseEntity.getBody(); 
	        } else {
	            return null; 
	        }
	    }
}
