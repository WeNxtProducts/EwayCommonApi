package com.maan.eway.notif.service;

import java.util.List;

import com.maan.eway.notif.req.GetMailTemplateReq;
import com.maan.eway.notif.req.MailTriggerReq;
import com.maan.eway.notif.req.ReadMailReq;
import com.maan.eway.notif.req.ReplayMailTriggerReq;
import com.maan.eway.notif.req.SmsGetReq;
import com.maan.eway.notif.req.SmsReq;
import com.maan.eway.notif.res.NotifTemplateRes;
import com.maan.eway.notif.res.ReadMailRes;
import com.maan.eway.notif.res.SmsGetRes;
import com.maan.eway.res.SuccessRes;

public interface NotificationService {

	public SuccessRes SendMail(MailTriggerReq req);

	public NotifTemplateRes getGetMailTemplate(GetMailTemplateReq req);

	public List<ReadMailRes> ReadMail(ReadMailReq req);

	public SuccessRes SendSms(SmsReq req);

	public List<ReadMailRes> ReadSentMail(ReadMailReq req);

	public SuccessRes replayMail(ReplayMailTriggerReq req);

//	public SuccessRes SendAndSaveMail(MailTriggerReq req);

	public SuccessRes SaveAndSendSms(SmsReq req);

	public List<SmsGetRes> getSms(SmsGetReq req);


}
