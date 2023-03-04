package com.maan.eway.notification.service;

import java.util.List;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.notification.req.NotifTemplateGetReq;
import com.maan.eway.notification.req.TemplatesDropDownReq;
import com.maan.eway.notification.res.MailTemplateRes;
import com.maan.eway.notification.res.SmsTemplateRes;
import com.maan.eway.res.DropDownRes;

public interface NotifTemplateService {

	List<DropDownRes> getTemplatesDropDown(TemplatesDropDownReq req);

	CommonRes getMailTemplate(NotifTemplateGetReq req);

	CommonRes getSmsTemplate(NotifTemplateGetReq req);

}
