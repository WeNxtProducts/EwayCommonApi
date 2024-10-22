package com.maan.eway.workflow.util;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

import com.maan.eway.bean.ApiIntegMaster;
import com.maan.eway.upgrade.criteria.CriteriaService;
import com.maan.eway.upgrade.criteria.SpecCriteria;
import com.maan.eway.workflow.dto.WorkEngine;

import jakarta.persistence.Tuple;

@Service
public class AzentoApiService {

	private static String azentoToken;
	
	@Autowired
	private CriteriaService crservice;
	
	 	
	 
	private String getAzentoToken(WorkEngine engine) {
		if(StringUtils.isNotBlank(azentoToken)) {
			return azentoToken;
		}else {
			try {
				String search4 = "companyId:" + engine.getCompanyId() + ";productId:" + engine.getProductId()+";status:{Y,R};apiType:AUTH";
				SpecCriteria commonCriteria = crservice.createCriteria(ApiIntegMaster.class, search4, "productId");
				List<Tuple> commonResult = crservice.getResult(commonCriteria, 0, 50);				
				String url=commonResult.get(0).get("apiUrl").toString();
				 TrustManager[] trustAllCerts = new TrustManager[]{
				            new X509TrustManager() {
				                public java.security.cert.X509Certificate[] getAcceptedIssuers() {return null;}
				                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType){}
				                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType){}
				            }
				        };

				        SSLContext sc = SSLContext.getInstance("SSL");
				        sc.init(null, trustAllCerts, new java.security.SecureRandom());
				        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
							
							@Override
							public boolean verify(String hostname, SSLSession session) {
								// TODO Auto-generated method stub
								return true;
							}
						});
				        		

				RestTemplate restTemplate = new RestTemplate();
		   		HttpHeaders headers = new HttpHeaders();
		   		headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		   		headers.setContentType(MediaType.APPLICATION_JSON);
		   		 //headers.set("Authorization",authHeader);
		   		Map<String,Object> req=new HashMap<String,Object>();
		   		req.put("username", "azentio");
		   		req.put("password", "azentio");
		   		HttpEntity<Object> entityReq = new HttpEntity<Object>(req, headers);
		   		ResponseEntity<Map> response = restTemplate.postForEntity(url, entityReq, Map.class);
		   		azentoToken=(String)response.getBody().get("jwt");
			}catch(Exception e) {
				e.printStackTrace();
			}
			return azentoToken;
		}
	}

	public Map<String, Object> createQuote(WorkEngine engine, Map<String, Object> request) {
		try {
			String token = getAzentoToken(engine);
			
			try {
				String search4 = "companyId:" + engine.getCompanyId() + ";productId:" + engine.getProductId()+";status:{Y,R};apiType:CREATEQUOTE";
				SpecCriteria commonCriteria = crservice.createCriteria(ApiIntegMaster.class, search4, "productId");
				List<Tuple> commonResult = crservice.getResult(commonCriteria, 0, 50);				
				String url=commonResult.get(0).get("apiUrl").toString();
				RestTemplate restTemplate = new RestTemplate();
		   		HttpHeaders headers = new HttpHeaders();
		   		headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		   		headers.setContentType(MediaType.APPLICATION_JSON);
		   		 headers.set("Authorization","Bearer "+token);		   		
		   		HttpEntity<Object> entityReq = new HttpEntity<Object>(request, headers);
		   		ResponseEntity<Map> response = restTemplate.postForEntity(url, entityReq, Map.class);
		   		return response.getBody();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
