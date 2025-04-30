package com.maan.eway.payment.service.impl; 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maan.eway.auth.dto.ClaimLoginResponse;
import com.maan.eway.auth.dto.CommonLoginRes;
import com.maan.eway.auth.dto.LoginRequest;
import com.maan.eway.auth.service.AuthendicationService;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PaymentVendorMaster;
import com.maan.eway.bean.RSTAPushDetails;
import com.maan.eway.common.req.PaymentDetailsSaveReq;
import com.maan.eway.common.req.TiraFrameReqCall;
import com.maan.eway.common.service.PaymentService;
import com.maan.eway.common.service.impl.TiraIntegerationServiceImpl;
import com.maan.eway.payment.service.MpesaPaymentService;
import com.maan.eway.payment.service.SelcomPaymentService;
import com.maan.eway.payment.util.ApigwClient;
import com.maan.eway.payment.util.CyberSouceIntegration;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PaymentInfoRepository;
import com.maan.eway.repository.PaymentVendorMasterRepository;
import com.maan.eway.repository.RSTAPushDetailsRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;


@Service
public class SelcomPaymentImpl implements SelcomPaymentService {

	@Autowired
	private PaymentVendorMasterRepository paymentVendorRepo;	

	@Autowired
	private PaymentDetailRepository paymentDetailRepo;

	@Autowired
	private InsuranceCompanyMasterRepository insuranceRepo;
	
	@Autowired
	private RSTAPushDetailsRepository rstaPushDetailsRepo;

	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private PaymentInfoRepository paymentinforepo;
	
	@Autowired
	private ListItemValueRepository itemValueRepo;

	private Logger log = LogManager.getLogger(SelcomPaymentImpl.class);

	private JsonArray payments;
	
	@Autowired
	private AuthendicationService authservice;
	@Autowired
	private  TiraIntegerationServiceImpl tiraService;

	
	@PersistenceContext
	private EntityManager em;
	
	@Value("${whatsapp.post.url}")
	private String whatsappUrl;
	
	@Autowired
	private MpesaPaymentService mpesaPaymentService;
	
	@Override
	public JsonObject createOrderForPayment(String merchantRefernceNo) {
		try {
			PaymentDetail payment = paymentDetailRepo.findByMerchantReferenceAndPaymentStatus(merchantRefernceNo,"PENDING");
			if(payment!=null)
				return createOrderForPayment(payment);
			else {
				log.info(merchantRefernceNo +" No Record Found") ;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public JsonObject createOrderForPayment(PaymentDetail payment) {
		try {

			if(payment!=null ) {
				PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(payment.getQuoteNo(), payment.getPaymentId());

				String userytype="b2b";
				if(paymentInfo.getSubUserType().equalsIgnoreCase("b2c")) {
					userytype="b2c";
				}

				List<PaymentVendorMaster> paymentId= paymentVendorRepo.findByCompanyIdAndStatusAndVendorIdAndUserTypeAndProductIdOrderByAmendIdDesc(payment.getCompanyId(),"Y","1",userytype,paymentInfo.getProductId());
				PaymentVendorMaster vendor =null;
				if(paymentId!=null && paymentId.size()>0) {						 
					vendor = paymentId.get(0);
				}else {
					paymentId=paymentVendorRepo.findByCompanyIdAndStatusAndVendorIdAndUserTypeAndProductIdOrderByAmendIdDesc(payment.getCompanyId(),"Y","1",userytype,99999);
					vendor = paymentId.get(0);
				}
				
				JsonObject response = null;
 
				if("lipila".equals(vendor.getVendorName())) {
					return lipila(vendor,payment);
				}else if("ipayafrica".equals(vendor.getVendorName())) {
					return ipayafrica(vendor,payment);
				}else if("pesapal".equals(vendor.getVendorName())){
					return pesapal(vendor,payment);
				}else if("peach".equals(vendor.getVendorName())){
					return peach(vendor,payment);
				}
//				else if("mpesa".equals(vendor.getVendorName())){
//					return mpesa(vendor,payment);
//				}
				
				else if ("mpesa".equals(vendor.getVendorName())) {
					JsonObject responsempesa = mPesaPayment(vendor, payment);
					JsonObject innerResponse = new JsonObject();
					innerResponse.addProperty("payment_gateway_url", "Dummy"); //dXJsIG5vdCBhdmFpbGFibGU=
					JsonArray asJsonArray = new JsonArray(1);
					asJsonArray.add(innerResponse);
					responsempesa.add("data", asJsonArray);
					return responsempesa;
				}
				else if("cybersource".equals(vendor.getVendorName())){
					return cybersource(vendor,payment);
				}else {
					return selcomPayment(vendor,payment);
				}
				
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private JsonObject mPesaPayment(PaymentVendorMaster vendor, PaymentDetail payment) {

		JsonObject outputResponse = new JsonObject();

		outputResponse.addProperty("result", "SUCCESS");
		outputResponse.addProperty("Message", "Payment Initiated Successfully");

		return outputResponse;
	}
	
	
	private JsonObject cybersource(PaymentVendorMaster vendor, PaymentDetail payment) {
		List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
		String castAmountValue;
		if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId()))						
			castAmountValue =  payment.getPremiumLc().toPlainString();
		else
			castAmountValue=  payment.getPremiumFc().toPlainString();
		return new CyberSouceIntegration().createPay(vendor,payment,castAmountValue) ;
	}
//	private JsonObject mpesa(PaymentVendorMaster vendor, PaymentDetail payment) {
//		try {
//			JsonObject resp =new JsonObject();
//			resp.addProperty("result", "SUCCESS");
//			JsonObject innerResponse=new JsonObject();
//			innerResponse.addProperty("payment_gateway_url", "www.dummyurl.com");
//			JsonArray asJsonArray =new JsonArray(1);
//			asJsonArray.add(innerResponse);
//			resp.add("data", asJsonArray);
//			return resp;
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	private JsonObject peach(PaymentVendorMaster vendor, PaymentDetail payment) {
		DecimalFormat df = new DecimalFormat("#####");
		String signature ="";
		String castAmountValue="0";
		List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
		if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId()))						
			castAmountValue = df.format( payment.getPremiumLc().doubleValue());
		else
			castAmountValue=  df.format( payment.getPremiumLc().doubleValue());
		 
		/*try {
			Map<String, String> params = new HashMap<>();
			params.put("authentication.entityId", vendor.getApiKey());
			params.put("amount", castAmountValue);
			params.put("currency", "ZAR");
			params.put("merchantTransactionId",payment.getMerchantReference());
			params.put("nonce", payment.getMerchantReference());
			params.put("paymentType", "DB");
			params.put("shopperResultUrl",vendor.getReturnUrlLink().replaceAll("<QuoteNo>", payment.getQuoteNo()));
					signature = peachGenerateSignature(params, vendor.getApiSecretKey());	
		}catch (Exception e) {
			e.printStackTrace();
		}*/
		JsonObject jsonResponse = new JsonObject();
		try {
			Map<String, String> params = new LinkedHashMap<>();			 
			params.put("amount", castAmountValue);
			params.put("authentication.entityId", vendor.getApiKey());
			params.put("currency", payment.getCurrencyId());//payment.getCurrencyId());
			params.put("merchantTransactionId", payment.getMerchantReference());
			params.put("nonce", payment.getMerchantReference());
			params.put("paymentType", "DB");
			params.put("shopperResultUrl", vendor.getReturnUrlLink().replaceAll("<QuoteNo>", payment.getQuoteNo()));//URLEncoder.encode(, StandardCharsets.UTF_8.toString()) );			 
			
			params.put("customer.merchantCustomerId", payment.getCustomerId());
			params.put("customer.givenName", payment.getCustomerName());			
			params.put("customer.mobile", payment.getReqBillToPhone());
			params.put("customer.email",StringUtils.isBlank(payment.getCustomerEmail())?"":payment.getCustomerEmail());			
			params.put("customer.phone", payment.getReqBillToPhone());
		 
			params.put("billing.city", payment.getReqBillToAddressCity());
			params.put("billing.company", payment.getReqBillToCompanyName());
			params.put("billing.country","SZL".equals(payment.getReqBillToCountry())?"SZ":payment.getReqBillToCountry());			
			params.put("billing.state", payment.getReqBillToAddressState());
			params.put("billing.postcode", payment.getReqBillToAddrPostalCode());
			params.put("cancelUrl", vendor.getCancelUrlLink().replaceAll("<QuoteNo>", payment.getQuoteNo()));
			params.put("notificationUrl", vendor.getWebhookUrlLink());
			//params.put("forceDefaultMethod","true");
			//params.put("defaultPaymentMethod","CARD");
			signature = peachGenerateSignature(params, vendor.getApiSecretKey());	
			params.put("signature", signature);
			params.put("shopperResultUrl",vendor.getReturnUrlLink().replaceAll("<QuoteNo>", payment.getQuoteNo()));//URLEncoder.encode(, StandardCharsets.UTF_8.toString()) );	
			params.put("cancelUrl", vendor.getCancelUrlLink().replaceAll("<QuoteNo>", payment.getQuoteNo()));
			params.put("notificationUrl", vendor.getWebhookUrlLink());
			/*String requestBody = params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
					.collect(Collectors.joining("&"));
			System.out.println(" checkOut Request Body: " + requestBody);
			 */
			try (CloseableHttpClient client = HttpClients.createDefault()) {
				HttpPost httpPost = new HttpPost(vendor.getPaymentUrlLink());
				httpPost.setHeader("Content-Type", "application/json");
				ObjectMapper objectMapper = new ObjectMapper();
	            String json = objectMapper.writeValueAsString(params);
				httpPost.setEntity(new StringEntity(json));

				try (CloseableHttpResponse response = client.execute(httpPost)) {
					org.apache.http.HttpEntity entity = response.getEntity();
					String responseString = EntityUtils.toString(entity);
					System.out.println("Response: " + responseString);
					JsonObject responseJson = JsonParser.parseString(responseString).getAsJsonObject();
					if (responseJson.has("redirectUrl")) {
	                    jsonResponse.addProperty("redirectUrl", responseJson.get("redirectUrl").getAsString());
	                    
	                    jsonResponse.addProperty("result", "SUCCESS");				
	    				JsonObject innerResponse=new JsonObject();
	    				innerResponse.addProperty("payment_gateway_url",responseJson.get("redirectUrl").getAsString());
	    				JsonArray asJsonArray =new JsonArray(1);
	    				asJsonArray.add(innerResponse);
	    				jsonResponse.add("data", asJsonArray);
	                    
	                } else {
	                    jsonResponse.addProperty("status", "error");
	                    jsonResponse.addProperty("message", "redirectUrl not found in response");
	                    jsonResponse.addProperty("result", "ERROR");
	                }					
						
				}
			}catch (Exception e) {
				 e.printStackTrace();
				 
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonResponse.addProperty("status", "error");
			jsonResponse.addProperty("message", "Failed to initiate checkout");
		}
		return jsonResponse;
	}
	
	public static String peachGenerateSignature(Map<String, String> body, String secret) {
		Map<String, String> sortedParams = new TreeMap<>(body);
		StringBuilder result = new StringBuilder();

		for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
			String key = entry.getKey().trim();
			String value = entry.getValue() != null ? entry.getValue().trim() : "";

			if ("signature".equals(key)) {
				continue;
			}

			result.append(key).append(value);
		}
		 
		System.out.println("Signature String (before hashing): [" + result.toString() + "]");
		return pesapalHmacSha256(result.toString(), secret);
	}
	
	private static String pesapalHmacSha256(String data, String secret) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
			mac.init(secretKey);
			byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				hexString.append(String.format("%02x", b));

			}

			return hexString.toString();
		} catch (Exception e) {
			return "Error generating HMAC-SHA256 signature";
		}
	}
	
	private JsonObject lipila(PaymentVendorMaster vendor, PaymentDetail payment) {
		try {
			String apisecrectkey=null;
			String apibaseURL = null;
			String redirect_url=null;
			CloseableHttpClient httpClient = null;
			if(vendor!=null) {
				apibaseURL=vendor.getRemarks();
				apisecrectkey=vendor.getApiSecretKey();
				redirect_url=vendor.getReturnUrlLink();
				redirect_url=redirect_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
			}
			try {
				JsonObject request=new JsonObject();
				request.addProperty("currency",payment.getCurrencyId());
				request.addProperty("amount",payment.getPremium());
				request.addProperty("email", payment.getCustomerEmail());
				request.addProperty("phoneNumber", payment.getReqBillToPhone());
				request.addProperty("customerFirstName", payment.getCustomerName());
				request.addProperty("customerLastName", "NA");
				request.addProperty("customerCity", payment.getReqBillToAddressCity());
				request.addProperty("customerCountry", payment.getReqBillToCountry());
				request.addProperty("customerAddress", payment.getReqBillToAddressLine1()+payment.getReqBillToAddressLine2());
				request.addProperty("customerZip", 0);
				request.addProperty("externalId", payment.getMerchantReference());
				request.addProperty("narration", payment.getMerchantReference());
				request.addProperty("redirectUrl", redirect_url);

				httpClient= HttpClientBuilder.create().build();
				HttpPost postRequest = new HttpPost(apibaseURL);
				postRequest.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
				postRequest.setHeader("Accept",MediaType.APPLICATION_JSON_VALUE);														
				postRequest.setHeader("Authorization","Bearer "+ apisecrectkey);

				StringEntity params = new StringEntity(request.toString());
				postRequest.setEntity(params);
				HttpResponse hresp  = httpClient.execute(postRequest);

				org.apache.http.HttpEntity httpEntity = hresp.getEntity();
				String apiOutput = EntityUtils.toString(httpEntity);
				System.out.println("output"+ apiOutput);
				JsonObject resp = new Gson().fromJson(apiOutput, JsonObject.class);
				resp.addProperty("result", "SUCCESS");
				JsonObject innerResponse=new JsonObject();
				innerResponse.addProperty("payment_gateway_url", resp.get("redirectUrl").getAsString());
				JsonArray asJsonArray =new JsonArray(1);
				asJsonArray.add(innerResponse);
				resp.add("data", asJsonArray);
				return resp;
			}catch(Exception e) {
				e.printStackTrace();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private JsonObject pesapal(PaymentVendorMaster vendor, PaymentDetail payment) {
		try {
			String apiKey = null;
			String apiSecret = null;
			String authUrl = null;
			String orderPath =null;
			String paymentUrl=null;
			String redirect_url=null;
			String cancel_url=null;
			String webHookUrl=null;
			String signedFields="";
			String remarks="";
			if(vendor!=null) {
				apiKey=vendor.getApiKey();
				apiSecret=vendor.getApiSecretKey();
				authUrl=vendor.getApiBaseUrl();
				paymentUrl=vendor.getPaymentUrlLink();
				//vendorCode=vendor.getVendorCode();
				redirect_url=vendor.getReturnUrlLink();
				cancel_url=vendor.getCancelUrlLink();
				webHookUrl=vendor.getWebhookUrlLink();
				redirect_url=redirect_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
				cancel_url=cancel_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
				signedFields=vendor.getSignedFields();
				remarks=vendor.getRemarks();
			}
			
			//Create Auth
			String token="",notificationId="";
			CloseableHttpClient httpClient = null;
			try {
				JsonObject request=new JsonObject();
				request.addProperty("consumer_key",vendor.getApiKey().toString());
				request.addProperty("consumer_secret", vendor.getApiSecretKey().toString());
				
				httpClient= HttpClientBuilder.create().build();
				HttpPost postRequest = new HttpPost(authUrl);
				postRequest.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
				StringEntity params = new StringEntity(request.toString());
				postRequest.setEntity(params);
	            HttpResponse hresp  = httpClient.execute(postRequest);

	            org.apache.http.HttpEntity httpEntity = hresp.getEntity();
	            String apiOutput = EntityUtils.toString(httpEntity);
	            System.out.println("output"+ apiOutput);
	            JsonObject tokResponse = new Gson().fromJson(apiOutput, JsonObject.class);
	            token=tokResponse.get("token").getAsString();
			}catch (Exception e) {
				e.printStackTrace();
			}finally {
				if(httpClient!=null) {
					httpClient.close();
				}
			}
			if(StringUtils.isNotBlank(token)) {
				try {
					JsonObject request=new JsonObject();
					request.addProperty("url",webHookUrl);
					request.addProperty("ipn_notification_type","POST");

					httpClient= HttpClientBuilder.create().build();
					HttpPost postRequest = new HttpPost(remarks);
					postRequest.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
					postRequest.setHeader("Accept",MediaType.APPLICATION_JSON_VALUE);														
					postRequest.setHeader("Authorization","Bearer "+ token);

					StringEntity params = new StringEntity(request.toString());
					postRequest.setEntity(params);
					HttpResponse hresp  = httpClient.execute(postRequest);

					org.apache.http.HttpEntity httpEntity = hresp.getEntity();
					String apiOutput = EntityUtils.toString(httpEntity);
					System.out.println("output"+ apiOutput);
					JsonObject resp = new Gson().fromJson(apiOutput, JsonObject.class);
					notificationId=resp.get("ipn_id").getAsString();
				}catch (Exception e) {
					e.printStackTrace();
				}finally {
					if(httpClient!=null) {
						httpClient.close();
					}
				}



				try {
					JsonObject request=new JsonObject();
					request.addProperty("id",payment.getMerchantReference());
					request.addProperty("currency", payment.getCurrencyId());
					List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
					if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId()))						
						request.addProperty("amount",   payment.getPremiumLc().toPlainString());
					else
						request.addProperty("amount",   payment.getPremiumFc().toPlainString());


					request.addProperty("description", payment.getQuoteNo()+" Payment Request");
					request.addProperty("redirect_mode", "PARENT_WINDOW");
					request.addProperty("callback_url", redirect_url);
					request.addProperty("cancellation_url",cancel_url);
					request.addProperty("notification_id", notificationId);
					request.addProperty("branch", "");

					JsonObject billingAddr=new JsonObject();
					billingAddr.addProperty("phone_number", payment.getReqBillToPhone());
					billingAddr.addProperty("email_address",payment.getReqBillToEmail());
					billingAddr.addProperty("country_code", payment.getReqBillToCountry());
					billingAddr.addProperty("first_name", payment.getReqBillToForename());
					billingAddr.addProperty("middle_name", "");
					billingAddr.addProperty("last_name", payment.getReqBillToSurname());				
					billingAddr.addProperty("line_1", payment.getReqBillToAddressLine1());
					billingAddr.addProperty("line_2", payment.getReqBillToAddressLine2());
					billingAddr.addProperty("city", payment.getReqBillToAddressCity());
					billingAddr.addProperty("state", payment.getReqBillToAddressState());
					billingAddr.addProperty("postal_code", payment.getReqBillToAddrPostalCode());
					billingAddr.addProperty("zip_code", payment.getReqBillToAddrPostalCode());
					request.add("billing_address", billingAddr);

					httpClient= HttpClientBuilder.create().build();
					HttpPost postRequest = new HttpPost(paymentUrl);
					postRequest.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
					postRequest.setHeader("Accept",MediaType.APPLICATION_JSON_VALUE);														
					postRequest.setHeader("Authorization","Bearer "+ token);
					System.out.println(payment.getMerchantReference()+"Payment Request "+ request.toString());
					StringEntity params = new StringEntity(request.toString());
					postRequest.setEntity(params);
					HttpResponse hresp  = httpClient.execute(postRequest);

					org.apache.http.HttpEntity httpEntity = hresp.getEntity();
					String apiOutput = EntityUtils.toString(httpEntity);
					System.out.println("output"+ apiOutput);
					JsonObject resp = new Gson().fromJson(apiOutput, JsonObject.class);



					resp.addProperty("result", "SUCCESS");

					JsonObject innerResponse=new JsonObject();
					innerResponse.addProperty("payment_gateway_url", resp.get("redirect_url").getAsString());
					payment.setResSignature(resp.get("order_tracking_id").getAsString());	
					paymentDetailRepo.save(payment);
					JsonArray asJsonArray =new JsonArray(1);
					asJsonArray.add(innerResponse);
					resp.add("data", asJsonArray);
					return resp;
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private JsonObject ipayafrica(PaymentVendorMaster vendor, PaymentDetail payment) {
		try {
			String apiKey = null;
			String apiSecret = null;
			String baseUrl = null;
			String orderPath =null;
			String vendorCode=null;
			String redirect_url=null;
			String cancel_url=null;
			String webHookUrl=null;
			String signedFields="";
			String remarks="";
			if(vendor!=null) {
				apiKey=vendor.getApiKey();
				apiSecret=vendor.getApiSecretKey();
				baseUrl=vendor.getApiBaseUrl();
				orderPath=vendor.getPaymentUrlLink();
				vendorCode=vendor.getVendorCode();
				redirect_url=vendor.getReturnUrlLink();
				cancel_url=vendor.getCancelUrlLink();
				webHookUrl=vendor.getWebhookUrlLink();

				redirect_url=redirect_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
				cancel_url=cancel_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
				signedFields=vendor.getSignedFields();
				remarks=vendor.getRemarks();
			}
			// Data needed by iPay
			LinkedHashMap<String, String> fields = new LinkedHashMap<>();
			String[] defaultfield = signedFields.split("&");
			for (int i = 0; i < defaultfield.length; i++) {
				String[] keyValue = defaultfield[i].split("=");
				fields.put(keyValue[0], keyValue[1]);
			}
			fields.put("oid", payment.getMerchantReference());
			fields.put("inv", payment.getQuoteNo());
			List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
			if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId()))						
				fields.put("ttl",  payment.getPremiumLc().toPlainString());
			else
				fields.put("ttl",  payment.getPremiumFc().toPlainString());
			fields.put("tel", payment.getReqBillToPhone());
			fields.put("eml", StringUtils.isBlank(payment.getCustomerEmail())?"hoinfo@firstassurance.co.ke":payment.getCustomerEmail());
			fields.put("vid", vendor.getApiKey());
			fields.put("curr", payment.getCurrencyId());
			/*fields.put("p1", "");
			fields.put("p2", "");
			fields.put("p3", "");
			fields.put("p4", "");*/
			fields.put("cbk", webHookUrl);
			//fields.put("lbk", "");
			fields.put("cst", "2");
			fields.put("crl", "0");
			StringBuilder datastring = new StringBuilder();
			fields.forEach((key, value) -> datastring.append(value));
			fields.put("hsh",  ipayafricaHash(datastring.toString().trim(),vendor.getApiSecretKey()));

			List<NameValuePair> nparms=new ArrayList<>();
			for( Entry<String, String> key:fields.entrySet()) {
				nparms.add(new BasicNameValuePair(key.getKey(), key.getValue()));
			}
			String []remarksArray=remarks.split("&");
			for (int i = 0; i < remarksArray.length; i++) {
				String[] keyValue = remarksArray[i].split("=");
				nparms.add(new BasicNameValuePair(keyValue[0], keyValue[1]));
			}
			String url=vendor.getApiBaseUrl()+vendor.getPaymentUrlLink();  
			try {
				URI uri = new URIBuilder(url).addParameters(nparms).build();
				String responseUrl=uri.toURL().toString();
				System.out.println( payment.getMerchantReference()+"-->"+responseUrl);
				JsonObject response = new JsonObject();
				response.addProperty("result", "SUCCESS");				
				JsonObject innerResponse=new JsonObject();
				innerResponse.addProperty("payment_gateway_url",responseUrl);
				JsonArray asJsonArray =new JsonArray(1);
				asJsonArray.add(innerResponse);
				response.add("data", asJsonArray);
				return response;
				
				/*try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
					String responseBody = EntityUtils.toString(response.getEntity());
					System.out.println(responseBody);
				}*/
			} catch (Exception e) {
				e.printStackTrace();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	 public static String ipayafricaHash(String data, String key) throws Exception {
	        Mac sha1Hmac = Mac.getInstance("HmacSHA1");
	        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
	        sha1Hmac.init(secretKey);
	        byte[] hashBytes = sha1Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
	        //return Base64.getEncoder().encodeToString(hashBytes);
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
	    }
	 
	private JsonObject selcomPayment(PaymentVendorMaster vendor,PaymentDetail payment) {
		try {

			String apiKey = null;
			String apiSecret = null;
			String baseUrl = null;
			String orderPath =null;
			String vendorCode=null;
			String redirect_url=null;
			String cancel_url=null;
			String webHookUrl=null;
			String signedFields="";
			if(vendor!=null) {
				apiKey=vendor.getApiKey();
				apiSecret=vendor.getApiSecretKey();
				baseUrl=vendor.getApiBaseUrl();
				orderPath=vendor.getPaymentUrlLink();
				vendorCode=vendor.getVendorCode();
				redirect_url=vendor.getReturnUrlLink();
				cancel_url=vendor.getCancelUrlLink();
				webHookUrl=vendor.getWebhookUrlLink();

				redirect_url=redirect_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
				cancel_url=cancel_url.replaceAll("<QuoteNo>", payment.getQuoteNo());
				signedFields=vendor.getSignedFields();
			}


			// data
			JsonObject orderDict = new JsonObject();
			orderDict.addProperty("vendor",vendorCode);
			orderDict.addProperty("order_id",payment.getMerchantReference());
			orderDict.addProperty("buyer_email", StringUtils.isBlank(payment.getCustomerEmail())?"info@alliance.co.tz":payment.getCustomerEmail() );
			orderDict.addProperty("buyer_name", payment.getCustomerName());
			orderDict.addProperty("buyer_userid", "");
			orderDict.addProperty("buyer_phone", payment.getReqBillToPhone());
			orderDict.addProperty("gateway_buyer_uuid", "");
			List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
			if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId()))						
				orderDict.addProperty("amount",  payment.getPremiumLc().toPlainString());
			else
				orderDict.addProperty("amount",  payment.getPremiumFc().toPlainString());

			orderDict.addProperty("currency",payment.getCurrencyId());

			//orderDict.addProperty("amount",100);					 
			//4orderDict.addProperty("currency","TZS");
			orderDict.addProperty("payment_methods","ALL");
			orderDict.addProperty("redirect_url",StringUtils.isNotBlank(redirect_url)?Base64.getEncoder().encodeToString(redirect_url.getBytes("UTF-8")):"");
			orderDict.addProperty("cancel_url",StringUtils.isNotBlank(cancel_url)?Base64.getEncoder().encodeToString(cancel_url.getBytes("UTF-8")):"");
			orderDict.addProperty("webhook",StringUtils.isNotBlank(webHookUrl)?Base64.getEncoder().encodeToString(webHookUrl.getBytes("UTF-8")):"");
			orderDict.addProperty("billing.firstname" , payment.getReqBillToForename());
			orderDict.addProperty("billing.lastname" , payment.getReqBillToSurname());
			orderDict.addProperty("billing.address_1" , payment.getReqBillToAddressLine1()); 
			orderDict.addProperty("billing.address_2" ,payment.getReqBillToAddressLine2());		
			orderDict.addProperty("billing.city" , payment.getReqBillToAddressCity()); 
			orderDict.addProperty("billing.state_or_region" , payment.getReqBillToAddressState());  
			orderDict.addProperty("billing.postcode_or_pobox" ,StringUtils.isBlank(payment.getReqBillToAddrPostalCode())?"99999":payment.getReqBillToAddrPostalCode());  
			orderDict.addProperty("billing.country" , payment.getReqBillToCountry());  
			orderDict.addProperty("billing.phone" , payment.getReqBillToPhone());
			/*
					 orderDict.addProperty("shipping.firstname" ,  payment.getReqBillToForename());
					 orderDict.addProperty("shipping.lastname" ,  payment.getReqBillToSurname());
			 */
			//orderDict.addProperty("shipping.address_1" , payment.getReqBillToAddressLine1());
			orderDict.addProperty("shipping.address_2" , payment.getReqBillToAddressLine2());
			orderDict.addProperty("shipping.city" , payment.getReqBillToAddressCity());
			orderDict.addProperty("shipping.state_or_region" , payment.getReqBillToAddressState());  
			orderDict.addProperty("shipping.postcode_or_pobox" ,StringUtils.isBlank(payment.getReqBillToAddrPostalCode())?"99999":payment.getReqBillToAddrPostalCode());  
			orderDict.addProperty("shipping.country" ,  payment.getReqBillToCountry()); 
			//orderDict.addProperty("shipping.phone" , payment.getReqBillToPhone());
			orderDict.addProperty("buyer_remarks","None");
			orderDict.addProperty("merchant_remarks","None");
			orderDict.addProperty("no_of_items",  1);
			List<String> signRemove=new LinkedList<String>();
			for (Entry<String, JsonElement> entry : orderDict.entrySet()) {
				if(!signedFields.contains(entry.getKey())) {
					signRemove.add(entry.getKey());
				} 
			}					
			for(String key:signRemove) {
				orderDict.remove(key);
			}
			// initalize a new Client instace with values of the base url, api key and api secret
			ApigwClient client = new ApigwClient(baseUrl,apiKey,apiSecret);
			//post data
			JsonObject response = client.postFunc(orderPath ,orderDict);
			return response;
		}catch(Exception e) {
			e.printStackTrace();			
		}
		return null;
	}
	@Override
	public JsonObject methodWebhook(Map<String,Object> jsonData) {
		try {
			log.info("WEBHOOK START"+jsonData);
			for (Entry<String, Object> key : jsonData.entrySet()) {
				 System.out.println(key.getKey() +"--"+key.getValue());

			}
			log.info("WEBHOOK END");
			String orderId=jsonData.get("order_id").toString();
			PaymentDetail payment = paymentDetailRepo.findByMerchantReferenceAndPaymentStatus(orderId,"PENDING");
			return orderStatus(payment.getQuoteNo(), "");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public JsonObject orderStatus(String orderId,String token) {
		try {
			List<PaymentDetail> payments = paymentDetailRepo.findByQuoteNo(orderId);

			if(payments!=null  && !payments.isEmpty() ) {
				List<PaymentVendorMaster> paymentId= paymentVendorRepo.findByCompanyIdAndStatusAndVendorIdOrderByAmendIdDesc(payments.get(0).getCompanyId(),"Y","1");
				PaymentVendorMaster vendor = paymentId.get(0);		
				JsonObject response =null;
				boolean isPaymentdone=false;
				JsonObject j=new JsonObject();
				for(PaymentDetail payment:payments) {

					JsonObject responses=null;

					if("lipila".equals(vendor.getVendorName())) {
						responses=lipilaOrderStatus(payment,vendor);
					}else if("pesapal".equals(vendor.getVendorName())) {
						responses=pesapalOrderStatus(payment,vendor);
					} else if("peach".equals(vendor.getVendorName())) {
						responses=peachOrderStatus(payment,vendor);
					}
//					else if("mpesa".equals(vendor.getVendorName())){
//						responses=mpesaOrderStatus(payment,vendor);
//					}
					else if ("mpesa".equals(vendor.getVendorName())) {
						System.out.println("Mpesa");
						responses = mpesaOrderStatus(payment, vendor);
					} 
					else {
						responses=selcomOrderStatus(payment,vendor);
					}


					if("ACCEPTED".equals(payment.getPaymentStatus())|| "FAILED".equals(payment.getPaymentStatus())) { 
						PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(payment.getQuoteNo(), payment.getPaymentId());
						
						
						if(!"ACCEPTED".equals(paymentInfo.getPaymentStatus())) {
							paymentInfo.setPaymentStatus(payment.getPaymentStatus());
							paymentInfo.setUpdatedDate(new Date());
							paymentInfo.setMerchantReference(payment.getMerchantReference());
							paymentinforepo.save(paymentInfo);

							if("ACCEPTED".equals(payment.getPaymentStatus())) {
								try {
									LoginRequest mslogin=new LoginRequest();
									mslogin.setLoginId("guest");
									mslogin.setPassword("Admin@01");
									mslogin.setReLoginKey("Y");
									CommonLoginRes checkUserLogin = authservice.checkUserLogin(mslogin,null);
									ClaimLoginResponse commonResponse =(ClaimLoginResponse) checkUserLogin.getCommonResponse();
									if(commonResponse!=null) {
										String tokeen = commonResponse.getToken();
										TiraFrameReqCall tira=new TiraFrameReqCall();
										tira.setQuoteNo(orderId);
										tiraService.callTiraIntegeration(tira, tokeen);
										if(paymentInfo.getProductId() == 5 && Arrays.asList("100046").contains(paymentInfo.getCompanyId())) {
											callRSTAIntegeration(payment.getQuoteNo());
										}
									}
								}catch(Exception e) {
									e.printStackTrace();
								}
								PaymentDetailsSaveReq req=new PaymentDetailsSaveReq();
								req.setQuoteNo(payment.getQuoteNo());
								req.setCreatedBy(payment.getUpdatedBy());
								req.setPaymentType(payment.getPaymentType());

								paymentService.generatePolicy(paymentInfo,req,payment,token);
							}
							isPaymentdone=true;
						}


					}
					j.addProperty("result",isPaymentdone?"COMPLETED":"FAIL");
					j.addProperty("message",responses!=null ?responses.toString():"");
					System.out.println("Push whatsapp call for "+payment.getMerchantReference()+"--"+payment.getPaymentStatus());
					if(payment.getCompanyId()!="100049") {
						if("ACCEPTED".equals(payment.getPaymentStatus())|| "FAILED".equals(payment.getPaymentStatus()))
							postCall(j,payment);
					}
				}
					
			 	

				return j;
			}else {
				JsonObject j=new JsonObject();
				j.addProperty("result","FAIL");
				j.addProperty("message","No Data found");
//				postCall(j,);
				return j;

			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}


	public void callRSTAIntegeration(String quoteNo) {
		Gson gson =new Gson();
		String responseCode="";
		StringBuffer responseAsString = new StringBuffer();
		SimpleDateFormat sdf =new SimpleDateFormat("dd/MM/yyyy");
		List<Map<String,Object>> request_list = new ArrayList<Map<String,Object>>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<MotorDataDetails> mdd = cq.from(MotorDataDetails.class);
			Root<HomePositionMaster> hpm = cq.from(HomePositionMaster.class);
			
			cq.multiselect(mdd.get("registrationNumber").alias("registrationNumber"),mdd.get("chassisNumber").alias("chassisNumber"),
					hpm.get("policyNo").alias("policyNo"),hpm.get("effectiveDate").alias("effectiveDate"),hpm.get("expiryDate").alias("expiryDate"),
					cb.selectCase().when(cb.in(mdd.get("policyType")).value(Arrays.asList("1","2")), "2").otherwise("1").alias("insuranceType"),
					cb.selectCase().when(cb.between(cb.literal(new Date()), hpm.get("inceptionDate"), hpm.get("expiryDate")), "1").otherwise("0").alias("status"),
					hpm.get("companyId").alias("companyId"))
			.where(cb.equal(mdd.get("quoteNo"), quoteNo),cb.equal(mdd.get("quoteNo"), hpm.get("quoteNo")));
			
			TypedQuery<Tuple> query = em.createQuery(cq);
			
			List<Tuple> resultList = query.getResultList();
			
			resultList.forEach(k -> {
				Map<String,Object> request = new HashMap<String,Object>();
				request.put("insuranceType", k.get("insuranceType")==null?"":k.get("insuranceType").toString());
				request.put("status", k.get("status")==null?"":k.get("status").toString());
				request.put("registrationMark", k.get("registrationNumber")==null?"":k.get("registrationNumber").toString());
				request.put("dateFrom", k.get("effectiveDate")==null?"":sdf.format(k.get("effectiveDate")));
				request.put("dateTo",k.get("expiryDate")==null?"":sdf.format(k.get("expiryDate")));
				request.put("insurancePolicyNo", k.get("policyNo")==null?"":k.get("policyNo").toString());
				request.put("chassisNumber", k.get("chassisNumber")==null?"":k.get("chassisNumber").toString());
				request_list.add(request);
			});
			
			log.info("Policy push request :: "+gson.toJson(request_list));
			insertRSTA(quoteNo,resultList.get(0).get("policyNo").toString(),gson.toJson(request_list));
			List<ListItemValue> rstadetails = itemValueRepo.findByItemTypeAndStatusAndCompanyIdOrderByItemCodeDesc("RSTA_PUSH", "Y", resultList.get(0).get("companyId").toString());
			String url = rstadetails.stream().filter(f -> f.getItemValue().equalsIgnoreCase("API_URL")).map(m -> m.getParam1()).findFirst().get();
			String authorization = rstadetails.stream().filter(f -> f.getItemValue().equalsIgnoreCase("API_PASSWORD")).map(m -> m.getParam1()).findFirst().get();
			
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url); 
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("Accept", "*/*");
			httpPost.setHeader("Authorization", authorization);
			StringEntity entity = new StringEntity(gson.toJson(request_list).replaceAll("\"\"", "null"),"UTF-8");
			httpPost.setEntity(entity);
			CloseableHttpResponse response = httpclient.execute(httpPost); 
			if(response.getStatusLine().getStatusCode()<=400 || response.getStatusLine().getStatusCode()==403) {
				BufferedReader rd1 = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));
				String line = "";
				while((line = rd1.readLine()) != null) {
					responseAsString.append(line);
				}
				log.info("Policy push response :: "+gson.toJson(responseAsString));
			}
			responseCode=String.valueOf(response.getStatusLine().getStatusCode());
		}catch (Exception e) {
			e.printStackTrace();
			responseAsString.append(e.getLocalizedMessage());
		}
		updateRSTAResponse(gson.toJson(responseAsString),responseCode,quoteNo);
	}
	
	@Transactional
	private void updateRSTAResponse(String responseJson, String responseCode, String quoteNo) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaUpdate<RSTAPushDetails> cq = cb.createCriteriaUpdate(RSTAPushDetails.class);
			Root<RSTAPushDetails> rpd = cq.from(RSTAPushDetails.class);
			
			cq.set(rpd.get("rstaResponse"), responseJson)
				.set(rpd.get("rstaResponseCode"), responseCode)
				.set(rpd.get("responseTime"), Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
				.where(cb.equal(rpd.get("quoteNo"), quoteNo));
			em.createQuery(cq).executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void insertRSTA(String quoteNo, String policyNo, String requestJson) {
		try {
			RSTAPushDetails m = RSTAPushDetails.builder()
				.sno(RSTAMaxSno())
				.quoteNo(quoteNo)
				.rstaRequest(requestJson)
				.requestTime(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
				.entryDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
				.build();
			rstaPushDetailsRepo.save(m);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private BigDecimal RSTAMaxSno() {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BigDecimal> cq = cb.createQuery(BigDecimal.class);
			Root<RSTAPushDetails> lpRoot = cq.from(RSTAPushDetails.class);
			cq.select(cb.coalesce(cb.sum(cb.max(lpRoot.get("sno")),BigDecimal.ONE), BigDecimal.ONE));
			BigDecimal value = em.createQuery(cq).getSingleResult();
			return value;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	private JsonObject mpesaOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
		return mpesaPaymentService.orderStatus(payment, vendor);
	}

//	private JsonObject mpesaOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
//		try { 
//			MPesaIntegration mpesaOrderStatus=new MPesaIntegration();
//			APIResponse jsonObject = mpesaOrderStatus.mpesaOrderStatus(payment, vendor);
//			
//				if("INS-0".equalsIgnoreCase(jsonObject.getParameter("output_ResponseCode"))) {
//					payment.setPaymentStatus("ACCEPTED");
//					payment.setAuthTransRefNo(jsonObject.getParameter("output_TransactionID"));
//					payment.setChannel(jsonObject.getParameter("output_ConversationID"));
//					
//					payment.setMsisdn(jsonObject.getParameter("output_ThirdPartyReference"));
//					///isPaymentdone=true;
//				}else if("INS-0".equalsIgnoreCase(jsonObject.getParameter("output_ResponseCode")) 
//						&& ("Cancelled".equals(jsonObject.getParameter("output_ResponseTransactionStatus"))
//								|| "Expired".equals(jsonObject.getParameter("output_ResponseTransactionStatus")) ))
//					payment.setPaymentStatus("FAILED");
//				else 
//					payment.setPaymentStatus("PENDING");
//				
//
//				payment.setAuthResponse(jsonObject.getParameter("output_ResponseCode"));
//				payment.setResponseMessage(jsonObject.getParameter("output_ResponseDesc"));
//				payment.setResponseTime(new Date());
//				paymentDetailRepo.save(payment);
//				JsonObject resp=new JsonObject();
//				for(Map.Entry<String, String> entry: jsonObject.getParameters().entrySet()){					
//					resp.addProperty(entry.getKey(), jsonObject.getParameter(entry.getKey()));
//				}
//			return resp;
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	private JsonObject peachOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
		try (CloseableHttpClient client = HttpClients.createDefault()) {  
			Map<String, String> params = new HashMap<>();
			params.put("authentication.entityId", vendor.getApiKey());
			params.put("merchantTransactionId", payment.getMerchantReference());
			String signaturestatus = peachGenerateSignature(params, vendor.getApiSecretKey());
			params.put("signature", signaturestatus);
			System.out.println("status signaturestatus->"+signaturestatus); 
			String param = params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
					.collect(Collectors.joining("&"));
			StringBuilder urlString = new StringBuilder(vendor.getCheckStatusUrl().concat("?"+param));

			HttpGet httpGet = new HttpGet(urlString.toString());
			httpGet.setHeader("Content-Type", "application/json");
			httpGet.setHeader("accept", "application/json");

			try (CloseableHttpResponse response = client.execute(httpGet)) {
				org.apache.http.HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity);
				System.out.println("Response: " + responseString);
				JsonObject responseJson = JsonParser.parseString(responseString).getAsJsonObject();
				if( responseJson!=null ) {
					List<String> successCodes=new ArrayList<String>();
					successCodes.add("000.000.000");
					successCodes.add("000.000.100");
					successCodes.add("000.100.110");
					successCodes.add("000.100.111");
					successCodes.add("000.100.112");
					List<String> pendingCodes=new ArrayList<String>();
					pendingCodes.add("000.200.000");
					pendingCodes.add("000.200.001");
					pendingCodes.add("000.200.100");
					pendingCodes.add("000.200.101");
					pendingCodes.add("000.200.102");
					pendingCodes.add("000.200.103");
					pendingCodes.add("000.200.200");
					pendingCodes.add("000.200.201");
					pendingCodes.add("000.200.999");
					
					if(responseJson.get("result.code") !=null && successCodes.contains(responseJson.get("result.code").getAsString()) ) {
						JsonObject redirect_post_data = responseJson;//.get("redirect_post_data").getAsJsonObject(); 

						if(successCodes.contains(redirect_post_data.get("result.code").getAsString())) {
							String amountStr=redirect_post_data.get("amount").getAsString();
							BigDecimal OurPremium=BigDecimal.ZERO;
							List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
							if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId())) {
								OurPremium = payment.getPremiumLc();
							}else {
								OurPremium = payment.getPremiumFc();
							}

							if(OurPremium.setScale(0, RoundingMode.UP).compareTo(new BigDecimal(amountStr))>=0) {
								payment.setPaymentStatus("ACCEPTED");
								payment.setAuthTransRefNo(redirect_post_data.get("recon.rrn").getAsString());
								payment.setChannel(redirect_post_data.get("recon.authCode").getAsString());
								payment.setReference(redirect_post_data.get("recon.rrn").getAsString());
								payment.setMsisdn(redirect_post_data.get("recon.stan").getAsString());
								payment.setAccountNumber(redirect_post_data.get("card.last4Digits").getAsString());
								payment.setAuthAmount(redirect_post_data.get("amount").getAsString());
								payment.setAuthResponse(redirect_post_data.get("result.code").getAsString());
								payment.setAuthTime(redirect_post_data.get("timestamp").getAsString());
								payment.setResponseTime(new Date());
								payment.setResponseMessage(redirect_post_data.get("result.description").getAsString());
							}else {
								payment.setPaymentStatus("FAILED");
								payment.setAuthResponse(redirect_post_data.get("result.code") !=null?redirect_post_data.get("result.code").getAsString():"");
								payment.setResponseMessage("Premium Amount is Mismatch ,Customer Paid Only "+amountStr);
								payment.setResponseTime(new Date());
								payment.setAuthAmount(redirect_post_data.get("amount")!=null? redirect_post_data.get("amount").getAsString():"0");
							}
						}
					}else if(responseJson.get("result.code") !=null && pendingCodes.contains(responseJson.get("result.code").getAsString()))
						payment.setPaymentStatus("PENDING");
					else /*if(responseJson.get("status") !=null && ( "cancelled".equals(responseJson.get("status").getAsString()) 
							|| "uncertain".equals(responseJson.get("status").getAsString())  
							))*/							 
						payment.setPaymentStatus("FAILED");

					payment.setAuthResponse(responseJson.get("result.code") !=null?responseJson.get("result.code").getAsString():"");
					payment.setResponseMessage(responseJson.get("result.description")!=null?responseJson.get("result.description").getAsString():"");
					payment.setResponseTime(new Date());
					payment.setAuthAmount(responseJson.get("amount")!=null? responseJson.get("amount").getAsString():"0");

					paymentDetailRepo.save(payment);


				}
				return responseJson;
			}
		}catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	private JsonObject pesapalOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
		CloseableHttpClient httpClient = null;
		String token="",notificationId="";
		
		try {
			JsonObject request=new JsonObject();
			request.addProperty("consumer_key",vendor.getApiKey().toString());
			request.addProperty("consumer_secret", vendor.getApiSecretKey().toString());
			
			httpClient= HttpClientBuilder.create().build();
			HttpPost postRequest = new HttpPost(vendor.getApiBaseUrl());
			postRequest.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
			StringEntity params = new StringEntity(request.toString());
			postRequest.setEntity(params);
            HttpResponse hresp  = httpClient.execute(postRequest);

            org.apache.http.HttpEntity httpEntity = hresp.getEntity();
            String apiOutput = EntityUtils.toString(httpEntity);
            System.out.println("output"+ apiOutput);
            JsonObject tokResponse = new Gson().fromJson(apiOutput, JsonObject.class);
            token=tokResponse.get("token").getAsString();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(httpClient!=null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		try {
			httpClient= HttpClientBuilder.create().build();
			
			String url=vendor.getCheckStatusUrl()+payment.getResSignature();
			
			HttpGet request = new HttpGet(url);
            System.out.println(url);
	        request.addHeader("Authorization", "Bearer "+token);
	        request.setHeader("Content-Type","application/json");
	        request.setHeader("Accept","application/json");
	            HttpResponse hresp  = httpClient.execute(request);

	            org.apache.http.HttpEntity httpEntity = hresp.getEntity();
	            String apiOutput = EntityUtils.toString(httpEntity);
	            System.out.println("output"+ apiOutput.toString());

	            JsonObject fromJson = new Gson().fromJson(apiOutput, JsonObject.class);
	            
	            if( fromJson!=null ) {
					
	            	
					if(fromJson.get("status_code") !=null && "1".equals(fromJson.get("status_code").getAsString()) ) {
						String amountStr=fromJson.get("amount").getAsString();
						BigDecimal OurPremium=BigDecimal.ZERO;
						List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
						if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId())) {
							OurPremium = payment.getPremiumLc();
						}else {
							OurPremium = payment.getPremiumFc();
						}
							
						if(OurPremium.setScale(0, RoundingMode.UP).compareTo(new BigDecimal(amountStr))>=0) {

							payment.setPaymentStatus("ACCEPTED");
							payment.setAuthTransRefNo(fromJson.get("confirmation_code").getAsString());
							payment.setChannel(fromJson.get("confirmation_code").getAsString());
							payment.setReference(fromJson.get("confirmation_code").getAsString());
							payment.setMsisdn(fromJson.get("confirmation_code").getAsString());
							payment.setAccountNumber(fromJson.get("payment_account").getAsString());
							payment.setAuthAmount(fromJson.get("amount").getAsString());
							payment.setAuthResponse(fromJson.get("payment_status_description").getAsString());
							payment.setAuthTime(fromJson.get("created_date").getAsString());
							payment.setResponseTime(new Date());
							payment.setResponseMessage(fromJson.get("description").getAsString());

						}else {
							payment.setPaymentStatus("FAILED");
							payment.setAuthResponse(fromJson.get("payment_status_description") !=null?fromJson.get("payment_status_description").getAsString():"");
							payment.setResponseMessage("Premium Amount is Mismatch ,Customer Paid Only "+amountStr);
							payment.setResponseTime(new Date());
							payment.setAuthAmount(fromJson.get("amount")!=null? fromJson.get("amount").getAsString():"0");
						}
						///isPaymentdone=true;
					}else if(fromJson.get("status_code") !=null &&  "0".equals(fromJson.get("status_code").getAsString()))
						payment.setPaymentStatus("PENDING");
					else if(fromJson.get("status_code") !=null &&  "3".equals(fromJson.get("status_code").getAsString()))
						payment.setPaymentStatus("PENDING");
					else
						payment.setPaymentStatus("FAILED");

					payment.setAuthResponse(fromJson.get("payment_status_description") !=null?fromJson.get("payment_status_description").getAsString():"");
					payment.setResponseMessage(fromJson.get("description")!=null?fromJson.get("description").getAsString():"");
					payment.setResponseTime(new Date());
					payment.setAuthAmount(fromJson.get("amount")!=null? fromJson.get("amount").getAsString():"0");
					
					paymentDetailRepo.save(payment);
				}
		
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
        	if(httpClient!=null)
				try {
					httpClient.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} 
	return null;
		
	}
	private JsonObject lipilaOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
		  CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		try {
			
			String url=vendor.getCheckStatusUrl()+payment.getMerchantReference();
			
			HttpGet request = new HttpGet(url);
            System.out.println(url);
	        request.addHeader("Authorization", "Bearer "+vendor.getApiSecretKey());
	        request.setHeader("Content-Type","application/json");

	            HttpResponse hresp  = httpClient.execute(request);

	            org.apache.http.HttpEntity httpEntity = hresp.getEntity();
	            String apiOutput = EntityUtils.toString(httpEntity);
	            System.out.println("output"+ apiOutput.toString());

	            JsonObject fromJson = new Gson().fromJson(apiOutput, JsonObject.class);
	            
	            if( fromJson!=null ) {
					
					if(fromJson.get("status") !=null && "Successful".equals(fromJson.get("status").getAsString())) {
						payment.setPaymentStatus("ACCEPTED");
						payment.setAuthTransRefNo(fromJson.get("transactionId").getAsString());
						payment.setChannel(fromJson.get("transactionId").getAsString());
						payment.setReference(fromJson.get("transactionId").getAsString());
						payment.setMsisdn(fromJson.get("externalId").getAsString());
						
						///isPaymentdone=true;
					}else if(fromJson.get("status") !=null &&  "Pending".equals(fromJson.get("status").getAsString()))
						payment.setPaymentStatus("PENDING");
					else if(fromJson.get("status") !=null &&  "INPROGRESS".equals(fromJson.get("status").getAsString()))
						payment.setPaymentStatus("PENDING");
					else
						payment.setPaymentStatus("FAILED");

					payment.setAuthResponse(fromJson.get("status") !=null?fromJson.get("status").getAsString():"");
					payment.setResponseMessage(fromJson.get("message")!=null?fromJson.get("message").getAsString():"");
					payment.setResponseTime(new Date());
					payment.setAuthAmount(fromJson.get("amount")!=null? fromJson.get("amount").getAsString():"0");
					paymentDetailRepo.save(payment);
				}
		
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
        	if(httpClient!=null)
				try {
					httpClient.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} 
	return null;
	}
	private JsonObject selcomOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor) {
		try {
			String apiKey = null;
			String apiSecret = null;
			String baseUrl = null;					
			String checkstatusLink=null;
			if(vendor!=null) {
				apiKey=vendor.getApiKey();
				apiSecret=vendor.getApiSecretKey();
				baseUrl=vendor.getApiBaseUrl();
				checkstatusLink=vendor.getCheckStatusUrl();					
			}
			// initalize a new Client instace with values of the base url, api key and api secret
			ApigwClient client = new ApigwClient(baseUrl,apiKey,apiSecret);
			JsonObject orderStatusDict = new JsonObject();
			orderStatusDict.addProperty("order_id",payment.getMerchantReference());
			//get order status
			JsonObject	responses= client.getFunc(checkstatusLink ,orderStatusDict);
			
			
			System.out.println("PAY ::"+payment.getMerchantReference()+" "+responses );
			  
			if("SUCCESS".equalsIgnoreCase(responses.get("result").getAsString()) ) {
				JsonArray array = responses.get("data").getAsJsonArray();
				JsonObject response = array.get(0).getAsJsonObject();

				if("COMPLETED".equals(response.get("payment_status").getAsString())) {
					payment.setPaymentStatus("ACCEPTED");
					payment.setAuthTransRefNo(response.get("transid").getAsString());
					payment.setChannel(response.get("channel").getAsString());
					payment.setReference(response.get("reference").getAsString());
					payment.setMsisdn(response.get("msisdn").getAsString());
					///isPaymentdone=true;
				}else if("PENDING".equals(response.get("payment_status").getAsString()))
					payment.setPaymentStatus("PENDING");
				else if("INPROGRESS".equals(response.get("payment_status").getAsString()))
					payment.setPaymentStatus("PENDING");
				else
					payment.setPaymentStatus("FAILED");

				payment.setAuthResponse(response.get("payment_status").getAsString());
				payment.setResponseMessage(responses.get("message").getAsString());
				payment.setResponseTime(new Date());
				payment.setAuthAmount(response.get("amount").getAsString());
				paymentDetailRepo.save(payment);
			}
			return responses;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private void postCall(JsonObject j, PaymentDetail payment) {
		try {
			
			Map<String,Object> request=new HashMap<String, Object>();
			request.put("whatsapp_no",payment.getWhatsappNo() );
			request.put("message_type", "Text");
			request.put("whatsapp_code", payment.getWhatsappCode());
			request.put("payment_response", j.toString());
			
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
			headers.setContentType(MediaType.APPLICATION_JSON);
			//headers.set("Authorization", "Basic dmlzaW9uOnZpc2lvbkAxMjMj");
			HttpEntity<Object> entityReq = new HttpEntity<>(request, headers);
			System.out.println(entityReq.getBody());
			 ResponseEntity<Object> response = restTemplate.postForEntity(whatsappUrl, entityReq, Object.class);
			System.out.println(response.getBody());
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	@PersistenceContext
    private EntityManager em;

	private List<PaymentDetail> getPendingPaymentDetails(String quoteNo){
		try {
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
			Root<PaymentDetail> paymentDetailRoot = criteriaQuery.from(PaymentDetail.class);
			// SELECT clause
			criteriaQuery.multiselect(
			    paymentDetailRoot.get("QUOTE_NO"),
			    paymentDetailRoot.get("PAYMENT_ID"),
			    paymentDetailRoot.get("MERCHANT_REFERENCE"),
			    paymentDetailRoot.get("SHORTERN_URL"),
			    paymentDetailRoot.get("PREMIUM"),
			    paymentDetailRoot.get("CUSTOMER_NAME"),
			    paymentDetailRoot.get("CUSTOMER_EMAIL"),
			    paymentDetailRoot.get("REQ_BILL_TO_PHONE"),
			    paymentDetailRoot.get("REQ_BILL_TO_COMPANY_NAME"),
			    paymentDetailRoot.get("AUTH_TRANS_REF_NO"),
			    paymentDetailRoot.get("PAYMENT_STATUS")
			);

			// Define the subquery
			Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
			Root<PaymentDetail> subqueryRoot = subquery.from(PaymentDetail.class);
			Expression<Long> oneLiteral = criteriaBuilder.literal(1L);

			subquery.select(oneLiteral);
			subquery.where(
			    criteriaBuilder.equal(
			        criteriaBuilder.upper(subqueryRoot.get("PAYMENT_STATUS")),
			        criteriaBuilder.upper(criteriaBuilder.literal("ACCEPTED"))
			    )
			);

			// Main query WHERE clause
			Predicate mainWhereClause = criteriaBuilder.and(
			    criteriaBuilder.equal(paymentDetailRoot.get("QUOTE_NO"), "Q00222"),
			    criteriaBuilder.not(criteriaBuilder.exists(subquery)),
			    criteriaBuilder.lessThan(
			        criteriaBuilder.sum(
			            criteriaBuilder.function(
			                "interval",
			                Integer.class,
			                paymentDetailRoot.get("ENTRY_DATE"),
			                criteriaBuilder.parameter(Integer.class, "interval"),
			                criteriaBuilder.literal("SECOND")
			            ),
			            criteriaBuilder.literal(1)
			        ),
			        criteriaBuilder.currentTimestamp()
			    ),
			    criteriaBuilder.between(
			        criteriaBuilder.currentTimestamp(),
			        criteriaBuilder.function(
			            "interval",
			            java.sql.Timestamp.class,
			            paymentDetailRoot.get("ENTRY_DATE"),
			            criteriaBuilder.parameter(Integer.class, "displayTime"),
			            criteriaBuilder.literal("MINUTE")
			        ),
			        criteriaBuilder.currentTimestamp()
			    ),
			    criteriaBuilder.equal(
			        criteriaBuilder.upper(paymentDetailRoot.get("PAYMENT_STATUS")),
			        criteriaBuilder.upper(criteriaBuilder.literal("PENDING"))
			    )
			);

			criteriaQuery.where(mainWhereClause);
			criteriaQuery.orderBy(criteriaBuilder.desc(paymentDetailRoot.get("ENTRY_DATE")));

			// Execute the query
			TypedQuery<Object[]> typedQuery = em.createQuery(criteriaQuery);
			typedQuery.setParameter("interval", 1); // Set the interval parameter
			typedQuery.setParameter("displayTime", 1); // Set the displayTime parameter
			List<Object[]> result = typedQuery.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}*/


	public JsonObject createOrderMinimal(PaymentDetail payment) {
		List<PaymentVendorMaster> paymentId= paymentVendorRepo.findByCompanyIdAndStatusAndVendorIdOrderByAmendIdDesc(payment.getCompanyId(),"Y","1");
		PaymentVendorMaster vendor = paymentId.get(0);
		
		if("lipila".equals(vendor.getVendorName())){
			return lipilaOrderMinimal(payment, vendor);
		}
//		else if("mpesa".equals(vendor.getVendorName())){
//			return mpesaOrderMinimal(payment,vendor);
//		}
		
		else if ("mpesa".equals(vendor.getVendorName())) {
			System.out.println("IN MPESA");
			return mPesaOrderMinimal(payment, vendor);
		}
		else {
			return selcomOrderMinimal(payment,vendor);
		}
			
	}
	
	private JsonObject mPesaOrderMinimal(PaymentDetail payment, PaymentVendorMaster vendor) {
		return mpesaPaymentService.payment(payment, vendor);
	}
	
//	private JsonObject mpesaOrderMinimal(PaymentDetail payment, PaymentVendorMaster vendor) {
//		try {
//			List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
//			
//			MPesaIntegration mpesa=new MPesaIntegration();
//			APIResponse pushMobile = mpesa.pushMobile(payment,vendor,insInfo.get(0));
//			 JsonObject json=new JsonObject();
//			if(pushMobile != null) {	             
// 	            for(Map.Entry<String, String> entry: pushMobile.getParameters().entrySet()){	                
//	                json.addProperty(entry.getKey(), pushMobile.getParameter(entry.getKey()));
//	            }
// 	          payment.setReference(pushMobile.getParameter("output_TransactionID"));
// 	          paymentDetailRepo.save(payment);
// 	           return json;
// 	        }	
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	private JsonObject lipilaOrderMinimal(PaymentDetail payment, PaymentVendorMaster vendor) {
		try {

	        
	        String url = vendor.getApiBaseUrl();
	        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

	        try {
	        	JsonObject orderDict = new JsonObject();
				orderDict.addProperty("currency",payment.getCurrencyId());
				List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
				
				if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId()))						
					orderDict.addProperty("amount",  payment.getPremiumLc());
				else
					orderDict.addProperty("amount",  payment.getPremiumFc());
				
				orderDict.addProperty("accountNumber",payment.getReqBillToPhone());
				orderDict.addProperty("fullName",payment.getCustomerName());
				orderDict.addProperty("phoneNumber",payment.getReqBillToPhone());
				if(StringUtils.isNotBlank(payment.getReqBillToEmail()))
					orderDict.addProperty("email",payment.getReqBillToEmail());
				orderDict.addProperty("externalId",payment.getMerchantReference());
				orderDict.addProperty("narration",payment.getQuoteNo() +" Payment Request");
				
				
	            Gson gson = new Gson();
	            HttpPost request = new HttpPost(url);
	            StringEntity params = new StringEntity(orderDict.toString());

	            System.out.println(url);
	            System.out.println( orderDict.toString());
	            
	             request.addHeader("Authorization", "Bearer "+vendor.getApiSecretKey());
	             request.setHeader("Content-Type","application/json");

	            request.setEntity(params);
	            HttpResponse hresp  = httpClient.execute(request);

	            org.apache.http.HttpEntity httpEntity = hresp.getEntity();
	            String apiOutput = EntityUtils.toString(httpEntity);
	            System.out.println("output"+ apiOutput.toString());

	            return new Gson().fromJson(apiOutput, JsonObject.class);
	        } catch (Exception ex) {
	            JsonObject err = new JsonObject();
	            err.addProperty("error", ex.getMessage());
	            return err;
	        }finally {
	        	if(httpClient!=null)
					try {
						httpClient.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			} 
	    
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	private JsonObject selcomOrderMinimal(PaymentDetail payment, PaymentVendorMaster vendor) {

		try {

					
			JsonObject response =null;
			boolean isPaymentdone=false;
			JsonObject j=new JsonObject();


			String apiKey = null;
			String apiSecret = null;
			String baseUrl = null;					
			String checkstatusLink=null;
			if(vendor!=null) {
				apiKey=vendor.getApiKey();
				apiSecret=vendor.getApiSecretKey();
				baseUrl=vendor.getApiBaseUrl();
				checkstatusLink=vendor.getCheckStatusUrl();					
			}

			// initalize a new Client instace with values of the base url, api key and api secret
			ApigwClient client = new ApigwClient(baseUrl,apiKey,apiSecret);
			// path relatiive to base url
			//String orderPath = "/v1/wallet/pushussd";//"/v1/checkout/create-order-minimal";
			String orderPath ="/v1/checkout/wallet-payment";
			// data
			JsonObject orderDict = new JsonObject();
			orderDict.addProperty("transid","MOBI"+Instant.now().toEpochMilli());			
			orderDict.addProperty("order_id",payment.getMerchantReference());
			//orderDict.addProperty("vendor","151662");//vendor.getVendorCode());
			orderDict.addProperty("msisdn",payment.getReqBillToPhone());
			/*orderDict.addProperty("vendor",vendor.getVendorCode());
			orderDict.addProperty("order_id",payment.getMerchantReference());
			orderDict.addProperty("buyer_email", payment.getReqBillToEmail());
			orderDict.addProperty("buyer_name", payment.getCustomerName());
			orderDict.addProperty("buyer_phone", payment.getReqBillToPhone());

			List<InsuranceCompanyMaster> insInfo = insuranceRepo.findByCompanyIdAndStatusAndEffectiveDateStartBeforeAndEffectiveDateEndAfter(payment.getCompanyId(),"Y",new Date(),new Date());
			if(insInfo.get(0).getCurrencyId().equals(payment.getCurrencyId()))						
				orderDict.addProperty("amount",  payment.getPremiumLc().toPlainString());
			else
				orderDict.addProperty("amount",  payment.getPremiumFc().toPlainString());


			orderDict.addProperty("currency",payment.getCurrencyId());
			orderDict.addProperty("buyer_remarks","None");
			orderDict.addProperty("merchant_remarks","None");
			orderDict.addProperty("no_of_items", 1 );
*/
			//post data
			JsonObject resp = client.postFunc(orderPath ,orderDict);
			log.info("Mobile Payment Response:"+resp);
			System.out.println("Mobile Payment Response:"+resp);
			return resp;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	
	}
	@Override
	public JsonObject createOrderMinimal(String merchantRefernceNo) {
		try {
			PaymentDetail payment = paymentDetailRepo.findByMerchantReferenceAndPaymentStatus(merchantRefernceNo,"PENDING");
			if(payment!=null) {
			 return createOrderMinimal(payment);				
			}else {
				log.info("No Records");
			} 
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
}
