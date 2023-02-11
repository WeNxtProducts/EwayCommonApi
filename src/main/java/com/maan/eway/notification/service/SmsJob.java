package com.maan.eway.notification.service;

import java.util.Date;
import java.util.Properties;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.SmsDataDetails;
import com.maan.eway.notification.req.Sms;
import com.maan.eway.repository.SmsDataDetailsRepository;

@Service
public class SmsJob implements Consumer<Sms> {

	@Autowired
	private SmsDataDetailsRepository smsRepo;

	public void pushSms(Sms m) {

		String statusResponse = null;
		try {
			Properties prop = new Properties();
			prop.put("MobileNo", m.getSmsTo());
			prop.put("SmsContent", m.getSmsBody());
			prop.put("SmsRegards", m.getSmsRegards());
			prop.put("SmsSubject", m.getSmsSubject());

		} catch (Exception e) {
			e.printStackTrace();
			statusResponse = e.getLocalizedMessage();
		}

		SmsDataDetails savedata = new SmsDataDetails();

		Long sno = smsRepo.count();
		savedata.setMobileNo(m.getSmsTo());
		savedata.setSmsFrom(m.getSmsFrom());		
		savedata.setSmsType(m.getSmsSubject());
		savedata.setSmsContent(m.getSmsBody());
		savedata.setEntryDate(new Date());
		savedata.setSNo(sno.toString());
		savedata.setResMessage("SMS Pushed Successfully");
		savedata.setResStatus("OK");
		savedata.setReqTime(new Date());
		savedata.setResTime(new Date());
		smsRepo.save(savedata);

	}

	@Override
	public void accept(Sms t) {
		pushSms(t);

	}

}
