package com.maan.eway.payment.service.impl;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
			}

		}catch (Exception e) {
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
						response = array.get(0).getAsJsonObject();

						if("COMPLETED".equals(response.get("payment_status").getAsString())) {
							payment.setPaymentStatus("ACCEPTED");
							payment.setAuthTransRefNo(response.get("transid").getAsString());
							payment.setChannel(response.get("channel").getAsString());
							payment.setReference(response.get("reference").getAsString());
							payment.setMsisdn(response.get("msisdn").getAsString());
							isPaymentdone=true;
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

						if("ACCEPTED".equals(payment.getPaymentStatus())|| "FAILED".equals(payment.getPaymentStatus())) { 
							PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(payment.getQuoteNo(), payment.getPaymentId());
							if(!"ACCEPTED".equals(paymentInfo.getPaymentStatus())) {
								paymentInfo.setPaymentStatus(payment.getPaymentStatus());
								paymentInfo.setUpdatedDate(new Date());
								paymentInfo.setMerchantReference(payment.getMerchantReference());
								paymentinforepo.save(paymentInfo);

								if("COMPLETED".equals(response.get("payment_status").getAsString())) {
									try {
										LoginRequest mslogin=new LoginRequest();
										mslogin.setLoginId("guest");
										mslogin.setPassword("Admin@01");
										mslogin.setReLoginKey("Y");
										CommonLoginRes checkUserLogin = authservice.checkUserLogin(mslogin,null);
										Map<String,Object> commonResponse =(Map<String,Object>) checkUserLogin.getCommonResponse();
										if(commonResponse!=null) {
											String tokeen = commonResponse.get("Token").toString();
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
							}


						}
					}
					j.addProperty("result",isPaymentdone?"COMPLETED":"FAIL");
					j.addProperty("message",responses.toString());		
				} 		



				return j;
			}else {
				JsonObject j=new JsonObject();
				j.addProperty("result","FAIL");
				j.addProperty("message","No Data found");
				return j;

			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
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
		try {

			List<PaymentVendorMaster> paymentId= paymentVendorRepo.findByCompanyIdAndStatusAndVendorIdOrderByAmendIdDesc(payment.getCompanyId(),"Y","1");
			PaymentVendorMaster vendor = paymentId.get(0);		
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
