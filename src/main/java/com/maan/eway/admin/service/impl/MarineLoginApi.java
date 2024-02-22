package com.maan.eway.admin.service.impl;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.maan.eway.bean.LoginUserInfo;

@Service
public class MarineLoginApi {

	@Value(value="${marine.auth.login}")
	private String authLoginLink;
	@Value(value="${marine.auth.createbroker}")
	private String createbrokerlink;
	@Value(value="${marine.auth.createproduct}")
	private String createProductLink;
	private String createLogin() {
		try {
			Map<String,Object > mainRequest=new HashMap<String, Object>();
			mainRequest.put("UserId", "guest");
			mainRequest.put("Password","Admin@01");
			mainRequest.put("LoginType","Admin");
			mainRequest.put("RegionCode","01");
			mainRequest.put("BranchCode","01");
			
			RestTemplate   temp=new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5)).setReadTimeout(Duration.ofSeconds(5)).build();
			
			
			HttpHeaders header=new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			//header.setCharset("UTF-8");
			//header.setBearerAuth(request.getTokenl());
			 


			HttpEntity<?> requestent = 
					new HttpEntity<>(mainRequest, header);

			System.out.println( new Date()+" Start "+ authLoginLink);
			ResponseEntity<Map<String, Object>> postForEntity = temp.exchange(authLoginLink,HttpMethod.POST, requestent,new ParameterizedTypeReference<Map<String, Object>>(){} );
			System.out.println( new Date()+" End "+ authLoginLink);
			Map<String,String> object =(Map<String,String>) postForEntity.getBody().get("LoginResponse");
			return object.get("Token");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "";
		
	}
	
	public void createMarineBroker(LoginUserInfo userInfo) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 			
			Map<String,Object > mainRequest=new HashMap<String, Object>();
			mainRequest.put("Address1",userInfo.getAddress1());
			mainRequest.put("Address2",userInfo.getAddress2());
			mainRequest.put("AgencyCode",userInfo.getAgencyCode());
			mainRequest.put("Approvedby",userInfo.getUpdatedBy());
			mainRequest.put("AttachedBranchInfo",null);
			mainRequest.put("AttachedRegionInfo",null);
			mainRequest.put("BorkerOrganization",userInfo.getCustomerName());
			mainRequest.put("BranchCode",null);
			mainRequest.put("BrokerCode",userInfo.getCustomerCode());
			mainRequest.put("City","0");
			mainRequest.put("Country","1");
			mainRequest.put("CustFirstName",userInfo.getCustomerName());
			mainRequest.put("CustLastName","");
			mainRequest.put("CustomerId","");
			mainRequest.put("DateOfBirth","");
			mainRequest.put("EffectiveDate",sdf.format(userInfo.getEntryDate()));
			mainRequest.put("Email",userInfo.getUserMail());
			mainRequest.put("EmergencyFee","");
			mainRequest.put("EmergencyFund","");
			mainRequest.put("Executive","5");
			mainRequest.put("Fax","0");
			mainRequest.put("Gender","");
			mainRequest.put("GovetFee","");
			mainRequest.put("GovtFeeStatus","");
			mainRequest.put("LoginId",userInfo.getLoginId());
			mainRequest.put("MissippiId","");
			mainRequest.put("MobileNo",userInfo.getUserMobile());
			mainRequest.put("Mode","");
			mainRequest.put("Nationality","1");
			mainRequest.put("Occupation",userInfo.getDesignation());
			mainRequest.put("OneOffCommission","");
			mainRequest.put("OpenCoverCommission","");
			mainRequest.put("Password","Admin@01");
			mainRequest.put("PoBox",userInfo.getPobox());
			mainRequest.put("PolicyFee","");
			mainRequest.put("PolicyFeeStatus","");
			mainRequest.put("RePassword","Admin@01");
			mainRequest.put("RegionCode","02");
			mainRequest.put("Status",userInfo.getStatus());
			mainRequest.put("SubBranchCode","");
			mainRequest.put("TaxApplicable","");
			mainRequest.put("TelephoneNo","");
			mainRequest.put("Title","");
			mainRequest.put("ValidNcheck",""); 
			
			String token = createLogin();
			RestTemplate   temp=new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5)).setReadTimeout(Duration.ofSeconds(5)).build();
			
			
			HttpHeaders header=new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			//header.setCharset("UTF-8");
			header.setBearerAuth(token);
			 


			HttpEntity<?> requestent = 
					new HttpEntity<>(mainRequest, header);

			System.out.println( new Date()+" Start "+ createbrokerlink);
			ResponseEntity<Map<String, Object>> postForEntity = temp.exchange(createbrokerlink,HttpMethod.POST, requestent,new ParameterizedTypeReference<Map<String, Object>>(){} );
			System.out.println( new Date()+" End "+ createbrokerlink);
			System.out.println("response"+postForEntity.getBody());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void createProductLink() {
		try {
			Map<String,Object > mainRequest=new HashMap<String, Object>();
			mainRequest.put("AgencyCode","");
			mainRequest.put("BackDateAllowed","");
			mainRequest.put("Commission","");
			mainRequest.put("CustomerId","");
			mainRequest.put("CustomerName","");
			mainRequest.put("DiscountPremium","");
			mainRequest.put("Freight","N");
			mainRequest.put("InsuranceEndLimit","10000000000000");
			mainRequest.put("LoadingPremium",null);
			mainRequest.put("MinPremiumAmount","1000");
			mainRequest.put("PayReceip","N");
			mainRequest.put("ProductId","");
			mainRequest.put("Provision","N");
			mainRequest.put("Remarks","N");
			mainRequest.put("UserId","");			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
