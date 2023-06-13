package com.maan.eway.common.service.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.maan.eway.common.req.TiraFrameReqCall;
import com.maan.eway.master.service.impl.ClausesMasterServiceImpl;
import com.maan.eway.res.SuccessRes;

@Service
public class TiraIntegerationServiceImpl {

	private Logger log = LogManager.getLogger(ClausesMasterServiceImpl.class);
	
	@Value(value = "${TiraIntegReqFrameLink}")
	private String tiraIntegReqFrameLink;
	
	@Value(value = "${TiraIntegPushLink}")
	private String tiraIntegPushLink;
	
	public SuccessRes callTiraIntegeration(TiraFrameReqCall tiraReq, String token) {
		SuccessRes res = new SuccessRes();
		try {
			// Tira Request Frame
			Object tiraFramedReq = TiraReqFrame(tiraReq, token);

			// Tira Integ Push
			Object tiraIntegPushRes = TiraIntegPush(tiraFramedReq , token);

			res.setResponse("Success");
			res.setSuccessId("");
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}
	
	
	 public Object TiraReqFrame (TiraFrameReqCall tiraReq , String token ) {
		 	Object TiraFramedReq = new Object();
		try {
			// Frame Tira Req

			RestTemplate temp = new RestTemplate();
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			// header.setCharset("UTF-8");
			header.setBearerAuth(token);
			String url = tiraIntegReqFrameLink;
			HttpEntity<?> requestent = new HttpEntity<>(tiraReq, header);

			System.out.println(new Date() + " Start " + url);
			TiraFramedReq = temp.exchange(url, HttpMethod.POST, requestent,new ParameterizedTypeReference<Object>() {}).getBody() ;
			System.out.println(new Date() + " End " + url);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return TiraFramedReq;
	}
	 
		 
	 public Object TiraIntegPush(Object pushReq , String token  ) {
		 	Object postForEntity = new Object();
		try {
			// Frame Tira Req

			RestTemplate temp = new RestTemplate();
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_XML);
			// header.setCharset("UTF-8");
			header.setBearerAuth(token);
			String url = tiraIntegPushLink;
			HttpEntity<?> requestent = new HttpEntity<>(pushReq , header);

			System.out.println(new Date() + " Start " + url);
			postForEntity = temp.exchange(url, HttpMethod.POST, requestent,new ParameterizedTypeReference<Object>() {}).getBody() ;
			System.out.println(new Date() + " End " + url);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return postForEntity;
	}
		 
		 
			
}
