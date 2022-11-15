package com.maan.eway.notif.service;

import com.maan.eway.notif.req.MailFramingReq;

public interface MailFramingService {

	String frameMail(MailFramingReq mReq, String mailBody, String mailSubject);


}
