package com.maan.eway.payment.service.impl;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.maan.eway.auth.dto.ClaimLoginResponse;
import com.maan.eway.auth.dto.CommonLoginRes;
import com.maan.eway.auth.dto.LoginRequest;
import com.maan.eway.auth.service.AuthendicationService;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PaymentVendorMaster;
import com.maan.eway.common.req.PaymentDetailsSaveReq;
import com.maan.eway.common.req.TiraFrameReqCall;
import com.maan.eway.common.service.PaymentService;
import com.maan.eway.common.service.impl.TiraIntegerationServiceImpl;
import com.maan.eway.payment.service.SelcomPaymentService;
import com.maan.eway.payment.util.ApigwClient;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PaymentInfoRepository;
import com.maan.eway.repository.PaymentVendorMasterRepository;


@Service
public class SelcomPaymentImpl implements SelcomPaymentService {

	@Autowired
	private PaymentVendorMasterRepository paymentVendorRepo;	

	@Autowired
	private PaymentDetailRepository paymentDetailRepo;

	@Autowired
	private InsuranceCompanyMasterRepository insuranceRepo;

	@Autowired
	private PaymentService paymentService;
	@Autowired
	private PaymentInfoRepository paymentinforepo;

	private Logger log = LogManager.getLogger(SelcomPaymentImpl.class);

	private JsonArray payments;
	
	@Autowired
	private AuthendicationService authservice;
	@Autowired
	private  TiraIntegerationServiceImpl tiraService;
	
	@Value("${whatsapp.post.url}")
	private String whatsappUrl;
	
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
					// Online Payment not available
					response = new JsonObject();
					response.addProperty("result", "SUCCESS");
					
					JsonObject innerResponse=new JsonObject();
					innerResponse.addProperty("payment_gateway_url", "dXJsIG5vdCBhdmFpbGFibGU=");
					JsonArray asJsonArray =new JsonArray(1);
					asJsonArray.add(innerResponse);
					response.add("data", asJsonArray);
					return response;
				}else {
					return selcomPayment(vendor,payment);
				}
				
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
					}else {
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
					if("ACCEPTED".equals(payment.getPaymentStatus())|| "FAILED".equals(payment.getPaymentStatus()))
						postCall(j,payment);
				}
					
			 	

				return j;
			}else {
				JsonObject j=new JsonObject();
				j.addProperty("result","FAIL");
				j.addProperty("message","No Data found");
				//postCall(j,);
				return j;

			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
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
		}else {
			return selcomOrderMinimal(payment,vendor);
		}
			
	}
	
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
