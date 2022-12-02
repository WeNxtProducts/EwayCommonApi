package com.maan.eway.service.impl.referal;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.req.referal.ReferralRequest;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.referal.MasterReferal;

public class ThreadReferralCall implements Callable<Object> {
	
	private ReferralRequest request;
	
	public ThreadReferralCall(ReferralRequest request) {
		super();
		this.request = request;
	}

	@Override
	public MasterReferal call() throws Exception {
		MasterReferal referal=null;
		try {
			
			RestTemplate temp=new RestTemplate();
			HttpHeaders header=new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<String> requestent = 
				      new HttpEntity<String>(this.request.getApiRequest(), header);
			
			
			ResponseEntity<CommonRes> postForEntity = temp.postForEntity(this.request.getApiLink(), requestent, CommonRes.class);
			
			if(postForEntity.getStatusCode().is2xxSuccessful()) {
				List<DropDownRes> response=(List<DropDownRes>)postForEntity.getBody().getCommonResponse();
				if(response!=null && !response.isEmpty()) {
					List<DropDownRes> collect = response.stream().filter(t-> (t.getStatus().equals("R") && t.getCode().equals(request.getPrimaryId()))).collect(Collectors.toList());
					if(collect!=null && !collect.isEmpty()) {
						referal=MasterReferal.builder().isreferral(true).referralDesc("Master Referral for "+collect.get(0).getCodeDesc()).build();
					}else
						referal=MasterReferal.builder().isreferral(false).build();
				}else {
					referal=MasterReferal.builder().isreferral(true).referralDesc("No response from api").build();
				}
			}else {
				referal=MasterReferal.builder().isreferral(true).referralDesc("Api is Not Up").build();
			}			
		}catch (Exception e) {
			e.printStackTrace();
			referal=MasterReferal.builder().isreferral(true).referralDesc("Some Exception In referral").build();					
		}
		return referal;
	}

}
