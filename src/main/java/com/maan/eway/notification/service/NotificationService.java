package com.maan.eway.notification.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.notification.repository.NotifTransactionDetailsRepository;
import com.maan.eway.notification.req.Notification;
@Service
public class NotificationService {
	@Autowired 
	private NotifTransactionDetailsRepository notifTrans;
	
	@Autowired
	private NotificationValidation vad;
	
	
	
	
	/*
	@Autowired
	private JobScheduler jobScheduler;
	*/
	public CommonRes pushNotification(Notification n) {
		
		
		List<Error> validation = vad.pushValidation(n);
		CommonRes c=new CommonRes();
		
		if(validation.isEmpty()) {
			Calendar calend = Calendar.getInstance();
			calend.setTime(n.getNotifcationDate()); 
			calend.add(Calendar.DATE, 1); 
			
			
			NotifTransactionDetails nt = NotifTransactionDetails.builder()
					.brokerCompanyName(n.getBroker().getBrokerCompanyName())
					.brokerMailId(n.getBroker().getBrokerMailId())
					.brokerMessengerCode(n.getBroker().getBrokerMessengerCode())
					.brokerMessengerPhone(n.getBroker().getBrokerMessengerPhone())
					.brokerPhoneCode(n.getBroker().getBrokerPhoneCode())
					.brokerPhoneNo(n.getBroker().getBrokerPhoneNo())
					.brokerName(n.getBroker().getBrokerName())
					.companyName(n.getCompanyName())
					.customerMailid(n.getCustomer().getCustomerMailid())
					.customerPhoneCode(n.getCustomer().getCustomerPhoneCode())
					.customerPhoneNo(n.getCustomer().getCustomerPhoneNo())
					.customerMessengerCode(n.getCustomer().getCustomerMessengerCode())
					.customerMessengerPhone(n.getCustomer().getCustomerMessengerPhone())
					.customerName(n.getCustomer().getCustomerName())
					.entryDate(new Date())
					.notifcationPushDate(n.getNotifcationDate())
					.notifcationEndDate(calend.getTime())
					.notifDescription(n.getNotifDescription())
					//.notifNo(null)
					.notifPriority(n.getNotifPriority())
					.notifPushedStatus("P")
					.notifTemplatename(n.getNotifTemplatename())
					.otp(n.getOtp())
					.policyNo(n.getPolicyNo())
					.quoteNo(n.getQuoteNo()) 
					.uwMailid(n.getUnderwriters().get(0).getUwMailid())
					.uwMessengerCode(n.getUnderwriters().get(0).getUwMessengerCode())
					.uwMessengerPhone(n.getUnderwriters().get(0).getUwMessengerPhone())
					.uwName(n.getUnderwriters().get(0).getUwName())
					.uwPhonecode(n.getUnderwriters().get(0).getUwPhonecode())
					.uwPhoneNo(n.getUnderwriters().get(0).getUwPhoneNo())
					.productName(n.getProductName())
					.sectionName(n.getSectionName())
					.statusMessage(n.getStatusMessage())
					.tinyUrl(n.getTinyUrl())
					.notifPushedStatus(n.getNotifPushedStatus().toString())
					.companyid(n.getCompanyid())
					.productid(n.getProductid())					
					.build();	
			NotifTransactionDetails sv = notifTrans.save(nt);
			c.setIsError(Boolean.FALSE);
			c.setErroCode(100);
			c.setIsError(null);
			c.setMessage("Pushed Successfuly");
			c.setCommonResponse(sv);
			
			//jobScheduler.enqueue(()->jobProcess(n,nt));
			
			
		}else {
			c.setErroCode(101);
			c.setErrorMessage(validation);
			c.setIsError(Boolean.TRUE);
			c.setMessage("Have Validation");
		}
		
		return  c;
		
		
 	}
	
}
