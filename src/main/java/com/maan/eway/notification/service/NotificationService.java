package com.maan.eway.notification.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.notification.repository.NotifTransactionDetailsRepository;
import com.maan.eway.notification.req.Notification;

public class NotificationService {
	 
	private NotifTransactionDetailsRepository notifTrans;
	
	@Autowired
	private NotificationValidation vad;
	
	public CommonRes pushNotification(Notification n) {
		
		
		List<Error> validation = vad.pushValidation(n);
		CommonRes c=new CommonRes();
		
		if(validation.isEmpty()) {
			NotifTransactionDetails nt = NotifTransactionDetails.builder()
					.brokerCompanyName(n.getBroker().getBrokerCompanyName())
					.brokerMailId(n.getBroker().getBrokerMailId())
					.brokerMessengerCode(n.getBroker().getBrokerMessengerCode())
					.brokerMessengerPhone(n.getBroker().getBrokerMessengerPhone())
					.brokerPhoneCode(n.getBroker().getBrokerPhoneCode())
					.brokerPhoneNo(n.getBroker().getBrokerPhoneNo())
					.companyName(n.getCompanyName())
					.customerMailid(n.getCustomer().getCustomerMailid())
					.customerPhoneCode(n.getCustomer().getCustomerPhoneCode())
					.customerPhoneNo(n.getCustomer().getCustomerPhoneNo())
					.customerMessengerCode(n.getCustomer().getCustomerMessengerCode())
					.customerMessengerPhone(n.getCustomer().getCustomerMessengerPhone())
					.entryDate(new Date())
					.notifcationDate(n.getNotifcationDate())
					.notifDescription(n.getNotifDescription())
					.notifNo(null)
					.notifPriority(n.getNotifPriority())
					.notifPushedStatus("P")
					.notifTemplatename(n.getNotifTemplatename())
					.otp(n.getOtp())
					.policyNo(n.getPolicyNo())
					.quoteNo(n.getQuoteNo()) 
					.uwMailid(n.getUnderwriter().getUwMailid())
					.uwMessengerCode(n.getUnderwriter().getUwMessengerCode())
					.uwMessengerPhone(n.getUnderwriter().getUwMessengerPhone())
					.uwName(n.getUnderwriter().getUwName())
					.uwPhonecode(n.getUnderwriter().getUwPhonecode())
					.uwPhoneNo(n.getUnderwriter().getUwPhoneNo())
					.productName(n.getProductName())
					.sectionName(n.getSectionName())
					.statusMessage(n.getStatusMessage())
					.tinyUrl(n.getTinyUrl())
					.notifPushedStatus(n.getNotifPushedStatus().toString())
					.build();	
			notifTrans.save(nt);
			c.setIsError(Boolean.FALSE);
			c.setErroCode(100);
			c.setIsError(null);
			c.setMessage("Pushed Successfuly");
			c.setCommonResponse(null);
		}else {
			c.setErroCode(101);
			c.setErrorMessage(validation);
			c.setIsError(Boolean.TRUE);
			c.setMessage("Have Validation");
		}
		
		return  c;
		
		
 	}
}
