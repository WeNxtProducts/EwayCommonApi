package com.maan.eway.notif.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.notif.controller.NotificationController;
import com.maan.eway.notif.req.MailFramingReq;
import com.maan.eway.notif.req.MailTriggerReq;
import com.maan.eway.notif.service.MailFramingService;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;

@Service
public class MailFramingServiceImpl implements MailFramingService {
	
	@Autowired
	private InsuranceCompanyMasterRepository insRepo;

	@Autowired
	private NotificationController mailTrigger;
	
	@Autowired
	private MailCriteriaServiceImpl mailCriteriaService;
	
	private Logger log = LogManager.getLogger(MailThreadServiceImpl.class);
	
	@Override
	public String frameMail(MailFramingReq mReq , String mailBody, String mailSubject) {
		String res = "" ;
		try {
			List<Map<String, Object>> contents = new ArrayList<Map<String, Object>>();//oracle.getListFromQuery(queryKey, mReq.getKeys());	
			if(mReq.getNotifTemplateId().equalsIgnoreCase("PRODUCT") ) {
				contents = mailCriteriaService.productCreatedCreateria(mReq);
			}
			else if(mReq.getNotifTemplateId().equalsIgnoreCase("SECTION")) {
				contents = mailCriteriaService.sectionCreatedCriteria(mReq);
			}
			else if(mReq.getNotifTemplateId().equalsIgnoreCase("COVER")) {
				contents = mailCriteriaService.coverCreatedCriteria(mReq);
			}
		
			
			if(contents !=null && contents.size()>0 ) {
				
				Map<String, Object> content = contents.get(0);
				mailSubject = frameContentMail(mReq ,mailSubject , content) ; 	
				mailBody = frameContentMail(mReq ,mailBody , content) ; 		
				
				MailTriggerReq mTReq = new MailTriggerReq(); 
				mTReq.setInsuranceId(mReq.getInsId());
				mTReq.setMailBody(mailBody);
				mTReq.setMailSubject(mailSubject);
				mTReq.setMailCc(mReq.getMailCc() );
				mTReq.setMailTo(mReq.getMailTo());
				mTReq.setMailRegards(mReq.getMailRegards());
				
				// Trigger Mail
				mailTrigger.sendMails(mTReq);
				log.info("{ Mail Sent Successfully }");
				res = "Mail Sent Successfully";
			}
		return res;
		}catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		
	}
	
	
	
	
	private String frameContentMail(MailFramingReq mReq  ,String body ,Map<String, Object> content) {
		try {
			
			//String body=;
			if(content!=null && !content.isEmpty()){
				for (Entry<String, Object> entry : content.entrySet()) {
					body=body.replaceAll("\\{"+entry.getKey()+"\\}", entry.getValue()==null?"":entry.getValue().toString()); //entry.getKey()
				}
			}
			
			return body;
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	private String frameMailTemplate(MailFramingReq mReq ,String mailContent , String subject, String insuranceId) {
	
	try {
		InsuranceCompanyMaster insData =  insRepo.findByCompanyId(mReq.getInsId() );
		
		String companyLogo =  insData.getCompanyLogo();
		String companyAddress =  insData.getCompanyAddress();
		String companyRegards = insData.getRegards();
		
	 //	<img src="https://alliance.co.tz/wp-content/themes/alliance/img/logo.png" width="150">
		StringBuilder html = new StringBuilder();			
		BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/email.html")));
		 
		String val;
		while ((val = br.readLine()) != null) {
			html.append(val);
		}
		String result = html.toString();
		result = result.replace("{MailLogo}", companyLogo);
		result = result.replace("{MailSub}", subject);
		result = result.replace("{MailBody}", mailContent);
		result = result.replace("{MailFooter}", companyRegards);
		result = result.replace("{MailAddress}", companyAddress);
		
		br.close();
		return result;
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	}
}

}
