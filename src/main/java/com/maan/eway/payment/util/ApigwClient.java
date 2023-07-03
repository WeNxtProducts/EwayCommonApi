package com.maan.eway.payment.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;

public class ApigwClient {

    String baseUrl;
    String apiKey;
    String apiSecret;

    public  ApigwClient(String baseUrl, String apiKey, String apiSecret){
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;

    }
    public  HttpHeaders computeHeader( JsonObject dataMap) {
        
        String encodekey = Base64.getEncoder().encodeToString((apiKey).getBytes());
        String authToken = "SELCOM "+ encodekey;
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date now = new Date();
        String timestamp = sdfDate.format(now);
        
        String message = "", signed_fields = "";
    
        List<String> keys = new ArrayList<String>();
        List<String> serializedJson = new ArrayList<String>();
        serializedJson.add("timestamp="+timestamp);
    
        for (Object key : dataMap.keySet()) {
            String keyStr = (String)key;
            String keyvalue = dataMap.get(keyStr).getAsString();
            serializedJson.add(keyStr+"="+keyvalue);
            keys.add(keyStr);
    
        }
    
        message =  String.join("&", serializedJson);
        signed_fields =  String.join(",", keys);
    
        try {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(apiSecret.getBytes(), "HmacSHA256");
        
            sha256_HMAC.init(secret_key);
       
    
        String digest = new String(Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(message.getBytes())));
        
        HttpHeaders header=new HttpHeaders();
    
        header.setContentType(MediaType.APPLICATION_JSON);
        header.set("Authorization", authToken);
        header.set("Digest-Method", "HS256");
        header.set("Digest", digest);
        header.set("Timestamp", timestamp);
        header.set("Signed-Fields", signed_fields);
    
        return header;
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            return null;
            
        }
    
    }
    
    public  JsonObject postFunc(String path, JsonObject jsonData){
    	HttpHeaders header = computeHeader(jsonData);
        String url = this.baseUrl + path;
       

        try {
        	RestTemplate   temp=new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5)).setReadTimeout(Duration.ofSeconds(5)).build();
       		HttpEntity<?> requestent = new HttpEntity<>(jsonData, header);  
       		
			System.out.println( new Date()+" Start "+ url);
			ResponseEntity<JsonObject> postForEntity = temp.exchange(url,HttpMethod.POST, requestent, new ParameterizedTypeReference<JsonObject>() {} );
			System.out.println( new Date()+" End "+ url);
       		
			System.out.println(  "Status Code"+ postForEntity.getStatusCode());
            return postForEntity.getBody();
        } catch (Exception ex) {
            JsonObject err = new JsonObject();
            err.addProperty("error", ex.getMessage());
            return err;
        } 
    }
    
    public  JsonObject getFunc(String path, JsonObject jsonData){
    	HttpHeaders header = computeHeader(jsonData);
        String url = this.baseUrl + path;    

        try {
        	RestTemplate   temp=new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5)).setReadTimeout(Duration.ofSeconds(5)).build();
        	 List<String> qstring_list = new ArrayList<String>();
        	for (String key : jsonData.keySet()) {
                qstring_list.add(key.toString() + "="+jsonData.get(key).getAsString());
                
            }
        	url=url+"?" + String.join("&", qstring_list);
       		HttpEntity<?> requestent = new HttpEntity<>(header);       		
			System.out.println( new Date()+" Start "+ url);
			ResponseEntity<JsonObject> postForEntity = temp.exchange(url,HttpMethod.GET, requestent, new ParameterizedTypeReference<JsonObject>() {} );
			System.out.println( new Date()+" End "+ url);       		
			System.out.println(  "Status Code"+ postForEntity.getStatusCode());
            return postForEntity.getBody();
        } catch (Exception ex) {
            JsonObject err = new JsonObject();
            err.addProperty("error", ex.getMessage());
            return err;
        } 
    }

/*
    public  JsonObject getFunc(String path, JsonObject jsonData){
        Map<String, Object> header = computeHeader(jsonData);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        List<String> qstring_list = new ArrayList<String>();
        
        for (String key : jsonData.keySet()) {
            qstring_list.add(key.toString() + "="+jsonData.get(key).getAsString());
            
        }

        try {
            String url = this.baseUrl + path + "?" + String.join("&", qstring_list);
            HttpGet request = new HttpGet(url);

            for (Object key : header.keySet()) {
                request.addHeader(key.toString(), header.get(key).toString());
            }

            HttpResponse hresp  = httpClient.execute(request);

            HttpEntity httpEntity = hresp.getEntity();
            String apiOutput = EntityUtils.toString(httpEntity);
        
            

            return new Gson().fromJson(apiOutput, JsonObject.class);
        } catch (Exception ex) {
            JsonObject err = new JsonObject();
            err.addProperty("error", ex.getMessage());
            return err;
        }  
  

    }

    public JsonObject deleteFunc(String path, JsonObject jsonData){
        Map<String,Object> header = computeHeader(jsonData);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        List<String> qstring_list = new ArrayList<String>();

        for (String key : jsonData.keySet()) {
            qstring_list.add(key.toString() + "="+jsonData.get(key).getAsString());
        }

        try {
            String url = this.baseUrl + path + "?" + String.join("&", qstring_list);
            HttpDelete request = new HttpDelete(url);

            for (Object key : header.keySet()) {
                request.addHeader(key.toString(), header.get(key).toString());
            }

            HttpResponse hresp  = httpClient.execute(request);

            HttpEntity httpEntity = hresp.getEntity();
            String apiOutput = EntityUtils.toString(httpEntity);
            return new Gson().fromJson(apiOutput, JsonObject.class);
        } catch (Exception ex) {
            JsonObject err = new JsonObject();
            err.addProperty("error", ex.getMessage());
            return err;
        } 

        
  

    }
    */

}
