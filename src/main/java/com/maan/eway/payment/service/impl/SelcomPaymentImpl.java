package com.maan.eway.payment.service.impl;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.maan.eway.bean.PaymentDetail;  
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PaymentVendorMaster;
import com.maan.eway.common.req.PaymentDetailsSaveReq;
import com.maan.eway.common.service.PaymentService;
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
	@Override
	public JsonObject createOrderForPayment(String merchantRefernceNo) {
		try {
			PaymentDetail payment = paymentDetailRepo.findByMerchantReferenceAndPaymentTypeAndPaymentStatus(merchantRefernceNo,"4","PENDING");
			
			if(payment!=null ) {
				List<PaymentVendorMaster> paymentId= paymentVendorRepo.findByCompanyIdAndStatusAndVendorIdOrderByAmendIdDesc(payment.getCompanyId(),"Y","1");
				PaymentVendorMaster vendor = paymentId.get(0);
				String apiKey = null;
				String apiSecret = null;
				String baseUrl = null;
				String orderPath =null;
				String vendorCode=null;
				String redirect_url=null;
				String cancel_url=null;
				String webHookUrl=null;
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
					}
					
					// data
					JsonObject orderDict = new JsonObject();
					orderDict.addProperty("vendor",vendorCode);
					orderDict.addProperty("order_id",payment.getMerchantReference());
					orderDict.addProperty("buyer_email", payment.getCustomerEmail());
					orderDict.addProperty("buyer_name", payment.getCustomerName());
					orderDict.addProperty("buyer_userid", "");
					orderDict.addProperty("buyer_phone", payment.getReqBillToPhone());
					orderDict.addProperty("gateway_buyer_uuid", "");
					/*InsuranceCompanyMaster insInfo = insuranceRepo.findByCompanyId(payment.getCompanyId());
					if(insInfo.getCurrencyId().equals(payment.getCurrencyId()))	{					
						orderDict.addProperty("amount",  payment.getPremiumLc());
					else
						orderDict.addProperty("amount",  payment.getPremiumFc());*/ 
					orderDict.addProperty("amount",100);
					//orderDict.addProperty("currency",payment.getCurrencyId()); 
					orderDict.addProperty("currency","TZS");
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
					orderDict.addProperty("billing.postcode_or_pobox" , payment.getReqBillToAddrPostalCode());  
					orderDict.addProperty("billing.country" , payment.getReqBillToCountry());  
					orderDict.addProperty("billing.phone" , payment.getReqBillToPhone());
					orderDict.addProperty("shipping.firstname" ,  payment.getReqBillToForename());
					orderDict.addProperty("shipping.lastname" ,  payment.getReqBillToSurname());
					//orderDict.addProperty("shipping.address_1" , payment.getReqBillToAddressLine1());
					orderDict.addProperty("shipping.address_2" , payment.getReqBillToAddressLine2());
					orderDict.addProperty("shipping.city" , payment.getReqBillToAddressCity());
					orderDict.addProperty("shipping.state_or_region" , payment.getReqBillToAddressState());  
					orderDict.addProperty("shipping.postcode_or_pobox" , payment.getReqBillToAddrPostalCode());  
					orderDict.addProperty("shipping.country" ,  payment.getReqBillToCountry()); 
					orderDict.addProperty("shipping.phone" , payment.getReqBillToPhone());
					orderDict.addProperty("buyer_remarks","None");
					orderDict.addProperty("merchant_remarks","None");
					orderDict.addProperty("no_of_items",  1);
					
					// initalize a new Client instace with values of the base url, api key and api secret
					ApigwClient client = new ApigwClient(baseUrl,apiKey,apiSecret);
					//post data
					JsonObject response = client.postFunc(orderPath ,orderDict); 
					return response;
			}else {
				log.info(merchantRefernceNo +" No Record Found") ;
			}
  		
		}catch (Exception e) {
			e.printStackTrace();
		}
 		return null;
	}
	@Override
	public JsonObject methodWebhook(JsonObject jsonData) {
		try {
			log.info("WEBHOOK START");
			 for (String key : jsonData.keySet()) {
				 log.info(key.toString() + "="+jsonData.get(key).getAsString());
		            
		        }
			 log.info("WEBHOOK END");
			String orderId=jsonData.get("order_id").toString();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public JsonObject orderStatus(String orderId,String token) {
		 try {
			 PaymentDetail payment = paymentDetailRepo.findByMerchantReference(orderId);
				
				if(payment!=null ) {
					List<PaymentVendorMaster> paymentId= paymentVendorRepo.findByCompanyIdAndStatusAndVendorIdOrderByAmendIdDesc(payment.getCompanyId(),"Y","1");
					PaymentVendorMaster vendor = paymentId.get(0);				
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
						orderStatusDict.addProperty("order_id",orderId);
						//get order status
						JsonObject response = client.getFunc(checkstatusLink ,orderStatusDict);
							if("COMPLETED".equals(response.get("payment_status").toString())) {
								payment.setPaymentStatus("ACCEPTED");
								payment.setAuthTransRefNo(response.get("transid").toString());
								payment.setChannel(response.get("channel").toString());
								payment.setReference(response.get("reference").toString());
								payment.setMsisdn(response.get("msisdn").toString());
							}else if("PENDING".equals(response.get("payment_status").toString()))
								payment.setPaymentStatus("PENDING");
							else if("INPROGRESS".equals(response.get("payment_status").toString()))
								payment.setPaymentStatus("PENDING");
							else
								payment.setPaymentStatus("FAILED");
							
							payment.setAuthResponse(response.get("payment_status").toString());
							payment.setResponseTime(new Date());
							payment.setAuthAmount(response.get("amount").toString());
							paymentDetailRepo.save(payment);
							
							PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentIdAndMerchantReference(payment.getQuoteNo(), payment.getPaymentId(),orderId);
							paymentInfo.setPaymentStatus(payment.getPaymentStatus());
							paymentinforepo.save(paymentInfo);
							if("COMPLETED".equals(response.get("payment_status").toString())) {
								PaymentDetailsSaveReq req=new PaymentDetailsSaveReq();
								req.setQuoteNo(payment.getQuoteNo());
								req.setCreatedBy(payment.getUpdatedBy());
								req.setPaymentType(payment.getPaymentType());
								paymentService.generatePolicy(paymentInfo,req,payment,token);
							}
 						return response;
				}
		 }catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace();
		}
		return null;
	}

}
