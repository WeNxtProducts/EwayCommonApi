package com.maan.eway.workflow.util;


import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
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
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itextpdf.text.pdf.qrcode.ByteArray;
import com.maan.eway.bean.ApiIntegMaster;
import com.maan.eway.bean.PremiaTransactionLog;
import com.maan.eway.repository.PremiaTransactionLogRepository;
import com.maan.eway.upgrade.criteria.CriteriaService;
import com.maan.eway.upgrade.criteria.SpecCriteria;
import com.maan.eway.workflow.dto.WorkEngine;

import jakarta.persistence.Tuple;

@Service
public class AzentoApiService {

	private String azentoToken;
	
	@Autowired
	private CriteriaService crservice;
	

	@Autowired
	private PremiaTransactionLogRepository transRepo;
	 
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

				PremiaTransactionLog log=new PremiaTransactionLog();
				try {
					Gson gson = new GsonBuilder() .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()) .create();

					log.setEntryDate(new Date());
					log.setQuoteNo(StringUtils.isBlank(engine.getQuoteNo())?engine.getRequestReferenceNo():engine.getQuoteNo());
					log.setRequestTime(LocalDateTime.now());
					log.setGenerateReq(engine.toString());
					log.setEndpoint(url);

					RestTemplate restTemplate = new RestTemplate();
					HttpHeaders headers = new HttpHeaders();
					headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
					headers.setContentType(MediaType.APPLICATION_JSON);
					//headers.set("Authorization",authHeader);
					Map<String,Object> req=new HashMap<String,Object>();
					req.put("username", "azentio");
					req.put("password", "azentio");
					log.setRequest(gson.toJson(req));		
					HttpEntity<Object> entityReq = new HttpEntity<Object>(req, headers);
					ResponseEntity<Map> response = restTemplate.postForEntity(url, entityReq, Map.class);
					log.setResponse(gson.toJson(response.getBody()));
					log.setResponseTime(LocalDateTime.now());
					log.setStatus(response.getBody()!=null ?"Y":"F");
					azentoToken=(String)response.getBody().get("jwt");
				}catch (Exception e) {
					log.setResponseTime(LocalDateTime.now());
					log.setStatus("F");
					log.setErrorMessage(e.getLocalizedMessage());
					e.printStackTrace();
				}finally {
					transRepo.save(log);
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			return azentoToken;
		}
	}

	public Map<String, Object> createQuote(WorkEngine engine, Map<String, Object> request) {
		PremiaTransactionLog log=new PremiaTransactionLog();
		Map<String, Object> isErrormap=new HashMap<String, Object>();
		try {
			String token = null;
			int loop=0,maxLoop=5;
			while(StringUtils.isBlank(token) && loop<maxLoop) {
				token = getAzentoToken(engine);
				loop++;
				if(loop>2) this.azentoToken="";
			}
			
			log.setEntryDate(new Date());
			log.setQuoteNo(StringUtils.isBlank(engine.getQuoteNo())?engine.getRequestReferenceNo():engine.getQuoteNo());
			log.setRequestTime(LocalDateTime.now());
			log.setGenerateReq(engine.toString());
			try {
				 Gson gson = new GsonBuilder() .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()) .create();
				 
				String search4 = "companyId:" + engine.getCompanyId() + ";productId:" + engine.getProductId()+";status:{Y,R};apiType:"+engine.getIntegType()+";";
				SpecCriteria commonCriteria = crservice.createCriteria(ApiIntegMaster.class, search4, "productId");
				List<Tuple> commonResult = crservice.getResult(commonCriteria, 0, 50);				
				String url=commonResult.get(0).get("apiUrl").toString();
				log.setEndpoint(url);
				
		   		
		   		
		   				
		   		
		   		if("DOWNLD_INTEG".equals(engine.getIntegType())) {
		   			
					

		   			RestTemplate restTemplate = new RestTemplate();
		   		/*	List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		   			MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		   			converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
		   			messageConverters.add(converter);
		   			restTemplate.setMessageConverters(messageConverters);*/
		   			restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
		   			HttpHeaders headers = new HttpHeaders();
			   		headers.setAccept(Arrays.asList(MediaType.ALL));
			   		headers.set("Authorization","Bearer "+token);
			   		headers.setContentType(MediaType.APPLICATION_JSON);
			   		HttpEntity<Object> entityReq = new HttpEntity<Object>(request, headers);
		   			
		   			entityReq = new HttpEntity<Object>(request, headers);
		   			log.setRequest(gson.toJson(entityReq.getBody()));		   			
		   			ResponseEntity<byte[]> response = restTemplate.exchange(url,HttpMethod.POST, entityReq,byte[].class);
		   			
		   			log.setResponse(Base64.getEncoder().encodeToString(response.getBody()));
		   			log.setResponseTime(LocalDateTime.now());
		   			log.setStatus(response.getBody()!=null ?"Y":"F");
			   		if(response.getBody()==null) {
			   			log.setErrorMessage("File Not Received");
			   		}else {
			   			log.setErrorMessage("Success");
			   		} 
			   		Map<String, Object> r=new HashMap<>();
			   		r.put("File", response.getBody());
			   		return r;
		   		}else {
		   			RestTemplate restTemplate = new RestTemplate();
		   			HttpHeaders headers = new HttpHeaders();
			   		headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
			   		headers.setContentType(MediaType.APPLICATION_JSON);
			   		headers.set("Authorization","Bearer "+token);		   		
			   		HttpEntity<Object> entityReq = new HttpEntity<Object>(request, headers);
			   		log.setRequest(gson.toJson(entityReq.getBody()));
		   			ResponseEntity<Map> response = restTemplate.postForEntity(url, entityReq, Map.class);
		   			log.setResponse(gson.toJson(response.getBody()));
		   			log.setResponseTime(LocalDateTime.now());
			   		log.setStatus((Boolean) response.getBody().get("hasError") ?"F":"Y");
			   		if((Boolean) response.getBody().get("hasError")) {
			   			Map<String, Object> data=(Map<String, Object>) response.getBody().get("data");
			   			List<Map<String, Object>> errorlist=((List<Map<String, Object>>)data.get("errorDetailsList"));
			   			log.setErrorMessage(errorlist.get(0).get("errorDescription")!=null?errorlist.get(0).get("errorDescription").toString():"Some Error from Core API");
			   		}else {
			   			log.setErrorMessage("Success");
			   		} 
			   		return response.getBody();
		   		}
		   		
			}catch(Exception e) {
				log.setResponseTime(LocalDateTime.now());
				log.setStatus("F");
				log.setErrorMessage(e.getLocalizedMessage());
				e.printStackTrace();
				isErrormap.put("Error", e.getLocalizedMessage());
			}
		}catch(Exception e) {
			log.setResponseTime(LocalDateTime.now());
			e.printStackTrace();
			isErrormap.put("Error", e.getLocalizedMessage());
		}finally {
			System.out.println(log);
			transRepo.save(log);
		}
		return isErrormap;
	}

	public static void saveStringAsPdf(String content, Path destination) throws IOException { // Ensure the directories exist 
		Files.createDirectories(destination.getParent()); // Write the content to the PDF file
		Files.writeString(destination, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING); 
	}
	
}
