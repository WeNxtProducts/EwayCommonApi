package com.maan.eway.notif.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.UIDFolder;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.MailMaster;
import com.maan.eway.bean.NotifTemplateMaster;
import com.maan.eway.bean.Sms;
import com.maan.eway.bean.SmsConfigMaster;
import com.maan.eway.bean.SmsDataDetails;
import com.maan.eway.notif.req.GetMailTemplateReq;
import com.maan.eway.notif.req.MailTriggerReq;
import com.maan.eway.notif.req.ReadMailReq;
import com.maan.eway.notif.req.ReplayMailTriggerReq;
import com.maan.eway.notif.req.SmsGetReq;
import com.maan.eway.notif.req.SmsReq;
import com.maan.eway.notif.req.SmsSaveReq;
import com.maan.eway.notif.res.NotifTemplateRes;
import com.maan.eway.notif.res.ReadMailRes;
import com.maan.eway.notif.res.SmsGetRes;
import com.maan.eway.notif.service.NotificationService;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.MailMasterRepository;
import com.maan.eway.repository.NotifTemplateMasterRepository;
import com.maan.eway.repository.SmsConfigMasterRepository;
import com.maan.eway.repository.SmsDataDetailsRepository;
import com.maan.eway.res.SuccessRes;

@Service
public class NotificationServiceImpl implements NotificationService {

	private Logger log = LogManager.getLogger(NotificationServiceImpl.class);
	/*
	@Value(value = "${kafka.producer.job2.link}")
	private String kafkaproducerlink2;
	*/
	@Autowired
	private MailMasterRepository mailRepo;
	
	@Autowired
	private NotifTemplateMasterRepository notifRepo;
	
	@Autowired
	private InsuranceCompanyMasterRepository insRepo;
	
	@Autowired
	private SmsConfigMasterRepository smsm;
	
	@Autowired
	private SmsDataDetailsRepository smsdatarepo;

	@Autowired
	private MailDetailsServiceImpl mailservice;
	
	@Autowired
	private SmsServiceImplementation smsservice;
	
	@Override
	public List<ReadMailRes> ReadMail(ReadMailReq req) {

		List<ReadMailRes> reslist = new ArrayList<ReadMailRes>();
		
		MailMaster mailCred =  mailRepo.findByCompanyId(req.getInsuranceId());
		
		Properties props = new Properties();
		
		props.setProperty("mail.store.protocol", "imaps");
		
		props.put("mail.smtp.host", mailCred.getSmtpHost());
		props.put("mail.smtp.port", mailCred.getSmtpPort()==null?"":mailCred.getSmtpPort().toString());
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		
		try {
			Session session = Session.getInstance(props, null);
			Store store = session.getStore();
			store.connect(mailCred.getSmtpHost(), mailCred.getSmtpUser(), mailCred.getSmtpPwd());
			session.setDebug(true);
			
			//Get All Mail From INBOX
			Folder inbox = store.getFolder("INBOX");
			UIDFolder uf = (UIDFolder)inbox;
			inbox.open(Folder.READ_ONLY);
			//Filter By Sender and Subject 
			SearchTerm sender = new FromTerm(new InternetAddress(req.getFromEmail()));
			SearchTerm subject = new SubjectTerm(req.getSubject());
			SearchTerm SenderAndSub = new AndTerm(sender, subject);
			
			//Filter By last 10 Days Mail
			long DAY_IN_MS = 1000 * 60 * 60 * 24;
			Date somePastDate = new Date(System.currentTimeMillis() - (10 * DAY_IN_MS));
			SearchTerm olderThan = new ReceivedDateTerm(ComparisonTerm.LT, new Date(System.currentTimeMillis() + (1 * DAY_IN_MS)));
			SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, somePastDate);
			SearchTerm Dateand = new AndTerm(olderThan, newerThan);
			SearchTerm And = new AndTerm(Dateand, SenderAndSub);
			
			Message[] messages = inbox.search(And);

			System.out.println(messages.length);
			for (int i = 0; i < messages.length; i++) {

				Message message = messages[i];
				
				//Set Response
				ReadMailRes res = new ReadMailRes();
				res.setSubject(message.getSubject());
				res.setFrom(message.getFrom()[0].toString());
				res.setMailBody(getTextFromMimeMultipart(message.getContent()));
				res.setReceivedDate(message.getReceivedDate());
				res.setMessageId(uf.getUID(message));
				System.out.println("MailId   " + uf.getUID(messages[i]));
				reslist.add(res);
			}
			store.close();

		} catch (Exception mex) {
			mex.printStackTrace();
		}
		return reslist;
	}
/*
	public SuccessRes SendAndSaveMail(MailTriggerReq req) {
		SuccessRes res = new SuccessRes();
		try {

			MailDetailsSaveReq mailreq = new MailDetailsSaveReq();
			mailreq.setMailBody(req.getMailBody());
			mailreq.setInsCompanyId(req.getInsuranceId());
			mailreq.setMailSubject(req.getMailSubject());
			mailreq.setMailSubject(req.getMailSubject());
			mailreq.setType(req.getType());
			mailreq.setBranchCode(req.getBranchCode());
			mailreq.setCreatedBy(req.getCreatedBy());
			mailreq.setType(req.getType());
			mailreq.setUserType(req.getSearchValue());
			
			
			mailservice.maildetails(mailreq);
			SendMail(req);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details " + e.getMessage());
			return null;
			
		}
		return res;
	}
	
	*/
	@Override
	public SuccessRes  SendMail(MailTriggerReq req ) {
		
		SuccessRes res = new SuccessRes();
		
		MailMaster mailCred =  mailRepo.findByCompanyId(req.getInsuranceId());
		
 		Properties prop = new Properties();
		prop.put("mail.smtp.host", mailCred.getSmtpHost());
		prop.put("mail.smtp.port", mailCred.getSmtpPort()==null?"":mailCred.getSmtpPort().toString());
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true"); // TLS

		Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mailCred.getSmtpUser(), mailCred.getSmtpPwd());
			}
		});

		try {
			
			// Mail Framed Req 
			List<String> mailCc      =  req.getMailCc();  
			List<String> mailTo      =  req.getMailTo();
			List<String> mailBcc      =  req.getMailBcc();
			
			if(mailTo!=null && mailTo.size()>0 ) {
				InternetAddress[] addressToo = null;
				InternetAddress[] addressCc = null;

				MimeMessage mimeMessage = new MimeMessage(session);

				mimeMessage.setFrom(new InternetAddress(mailCred.getSmtpUser()));
				
				// Mail To
				addressToo = new InternetAddress[mailTo.size()];
				for (int i = 0; i < mailTo.size(); i++) {
					if (StringUtils.isNotBlank(mailTo.get(i))) { 
						addressToo[i] = new InternetAddress(mailTo.get(i)); 
						mimeMessage.addRecipient(Message.RecipientType.TO, addressToo[i]);
					}
				}

				// Mail Cc
				if (mailCc != null && mailCc.size()>0 ) {
					addressCc = new InternetAddress[mailCc.size()];
					for (int i = 0; i < mailCc.size(); i++) {
						if (StringUtils.isNotBlank(mailCc.get(i))) {
							addressCc[i] = new InternetAddress(mailCc.get(i)); 
							mimeMessage.addRecipient(Message.RecipientType.CC, addressCc[i]); 
						}
					}
				}
				
				// Mail BCc
				if (mailBcc != null && mailBcc.size()>0 ) {
					InternetAddress[] addressBCc = new InternetAddress[mailBcc.size()];
					for (int i = 0; i < mailBcc.size(); i++) {
						if (StringUtils.isNotBlank(mailBcc.get(i))) {
							addressBCc[i] = new InternetAddress(mailBcc.get(i)); 
							mimeMessage.addRecipient(Message.RecipientType.BCC, addressBCc[i]); 
						}
					}
				}
				
				mimeMessage.setSubject(req.getMailSubject());
				mimeMessage.setContent(frameMailTemplate(req.getMailBody(),req.getMailSubject() , req.getInsuranceId(), req.getMailRegards()), "text/html");
				
				
				Transport.send(mimeMessage);
				res.setResponse("Mail successfully sent");
				
				
				
			}
		} catch (MessagingException mex) {
			mex.printStackTrace();
			res.setResponse("Mail Sent Failed");
		}	
		return res;
	}

	@Override
	public NotifTemplateRes getGetMailTemplate(GetMailTemplateReq req) {
		
		NotifTemplateRes res = new NotifTemplateRes();
		
		try {
			
			List<NotifTemplateMaster>  notifDetails = notifRepo.findBySnoAndStatusAndNotificationApplicableAndInsIdAndEffectiveDateStartLessThanEqualOrderByEntryDateDesc(req.getSno(),"Y",req.getNotificationApplicable(),req.getInsuranceId(), new Date()); 
			ModelMapper mapper = new ModelMapper();
			res = mapper.map(notifDetails.get(0), NotifTemplateRes.class);
			
		}catch (Exception e) {
			log.error(e);
		}

		return res;
	}
	
	
	private String frameMailTemplate(String mailContent, String subject, String insuranceId,String Regards) {

		try {
			InsuranceCompanyMaster insData = insRepo.findByCompanyId(insuranceId);

			StringBuilder html = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/email.html")));

			String val;
			while ((val = br.readLine()) != null) {
				html.append(val);
			}
			String result = html.toString();
			result = result.replace("{MailLogo}", insData.getCompanyLogo());
			result = result.replace("{MailSub}", subject);
			result = result.replace("{MailBody}", mailContent);
			result = result.replace("{MailFooter}", Regards);
			result = result.replace("{MailAddress}", insData.getCompanyAddress());

			br.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getTextFromMimeMultipart(Object content)  throws MessagingException, IOException{
	    
		String result = "";
		if (content instanceof String)  {
			result = (String)content;  			
		}else if (content instanceof Multipart)  { 
			
		    MimeMultipart mimeMultipart = (MimeMultipart)content;  

		    int count = mimeMultipart.getCount();
		    for (int i = 0; i < count; i++) {
		        BodyPart bodyPart = mimeMultipart.getBodyPart(i);
		        if (bodyPart.isMimeType("text/plain")) {
		            result = result + "\n" + bodyPart.getContent();
		            break; // without break same text appears twice in my tests
		        } else if (bodyPart.isMimeType("text/html")) {
		            String html = (String) bodyPart.getContent();
		            result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
		        } else if (bodyPart.getContent() instanceof MimeMultipart){
		            result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
		        }
		    }
		    
		}

		return result;
	}

	@Override
	public List<ReadMailRes> ReadSentMail(ReadMailReq req) {

		List<ReadMailRes> reslist = new ArrayList<ReadMailRes>();
		
		MailMaster mailCred =  mailRepo.findByCompanyId(req.getInsuranceId());
		
		Properties props = new Properties();
		
		props.setProperty("mail.store.protocol", "imaps");
		
		props.put("mail.smtp.host", mailCred.getSmtpHost());
		props.put("mail.smtp.port", mailCred.getSmtpPort()==null?"":mailCred.getSmtpPort().toString());
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		
		try {
			Session session = Session.getInstance(props, null);
			Store store = session.getStore();
			store.connect(mailCred.getSmtpHost(), mailCred.getSmtpUser(), mailCred.getSmtpPwd());
			session.setDebug(true);
			
			Folder inbox = store.getFolder("[Gmail]/Sent Mail");
			UIDFolder uf = (UIDFolder)inbox;
			inbox.open(Folder.READ_ONLY);
			
			//Filter By Sender and Subject 
			
			SearchTerm sender = new RecipientStringTerm(RecipientType.TO, req.getFromEmail());
			SearchTerm subject = new SubjectTerm(req.getSubject());
			SearchTerm SenderAndSub = new AndTerm(sender, subject);
			
			//Filter By last 10 Days Mail
			long DAY_IN_MS = 1000 * 60 * 60 * 24;
			Date somePastDate = new Date(System.currentTimeMillis() - (10 * DAY_IN_MS));
			SearchTerm olderThan = new ReceivedDateTerm(ComparisonTerm.LT, new Date(System.currentTimeMillis() + (1 * DAY_IN_MS)));
			SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, somePastDate);
			SearchTerm Dateand = new AndTerm(olderThan, newerThan);
			SearchTerm And = new AndTerm(Dateand, SenderAndSub);
		    
			Message[] messages = inbox.search(And);

			System.out.println(messages.length);
			for (int i = 0; i < messages.length; i++) {

				Message message = messages[i];
				
				//Set Response
				ReadMailRes res = new ReadMailRes();
				res.setSubject(message.getSubject());
				res.setFrom(message.getFrom()[0].toString());
				res.setMailBody(getTextFromMimeMultipart(message.getContent()));
				res.setReceivedDate(message.getReceivedDate());
				res.setMessageId(uf.getUID(message));
				System.out.println("MailId   " + uf.getUID(messages[i]));
				
				reslist.add(res);
			}
			store.close();

		} catch (Exception mex) {
			mex.printStackTrace();
		}
		return reslist;
	}

	
	public SuccessRes SaveAndSendSms(SmsReq req) {
	ModelMapper mapper = new ModelMapper();
		SuccessRes res = new SuccessRes();
		try {
			
			SmsSaveReq smsreq = new SmsSaveReq();
			mapper.map(req,smsreq);
			smsservice.sms(smsreq);
			SendSms(req);
			}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public SuccessRes SendSms(SmsReq req) {

		SuccessRes res = new SuccessRes();
		
		ModelMapper mapper = new ModelMapper();
		Optional<SmsConfigMaster> master = smsm.findByCompanyId(req.getInsuranceid());
		SmsConfigMaster rep=mapper.map(master.get(), SmsConfigMaster.class);
		
		Sms sms=new Sms();
		sms.setMobileCode(req.getMobileCode());
		sms.setMobileNo(req.getMobileNo());
		sms.setSmsContent(req.getSmsContent());
		sms.setSmsSubject(req.getSmsSubject());
		sms.setSmsRegards(req.getSmsRegards());
		sms.setMaster(rep);
		
		/// Table Insert
		SmsDataDetails data = new SmsDataDetails();
		
		//id
		String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
		String milliSecond =  String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND));
		String smsId = year.substring(2,4) + milliSecond+(int)(Math.random()*100);
		
		//Save
		data.setSNo(Integer.valueOf(smsId));
		data.setCustName(req.getCustName());
		data.setInsId(req.getInsuranceid());
		data.setMobileNo(req.getMobileNo());
		data.setSmsRefNo(smsId);
		data.setSmsContent(req.getSmsContent());
		data.setCreatedBy(req.getCreatedBy());
		smsdatarepo.save(data);	 
		
		/*
		PushIntoKafka job = new PushIntoKafka(sms, kafkaproducerlink2);
		FutureTask<Object> futureTask = new FutureTask<>(job);
		Thread thread = new Thread(futureTask);
		thread.start();
		*/
		res.setResponse("SMS Pushed Sucessfully");
		
		return res;
	}
	
	@Override
	public SuccessRes replayMail(ReplayMailTriggerReq req) {

		SuccessRes res = new SuccessRes();
		
		MailMaster mailCred =  mailRepo.findByCompanyId(req.getInsuranceId());
		
		Properties props = new Properties();
		props.setProperty("mail.store.protocol", "imaps");
		
		props.put("mail.smtp.host", mailCred.getSmtpHost());
		props.put("mail.smtp.port", mailCred.getSmtpPort()==null?"":mailCred.getSmtpPort().toString());
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		
		try {
			Session session = Session.getInstance(props, null);
			Store store = session.getStore();
			store.connect(mailCred.getSmtpHost(), mailCred.getSmtpUser(), mailCred.getSmtpPwd());
			session.setDebug(true);
			
			//Get All Mail From INBOX
			Folder f = store.getFolder("INBOX");
			UIDFolder uf = (UIDFolder)f;
			f.open(Folder.READ_ONLY);
			Message message = uf.getMessageByUID(req.getUuid());
			
			String to = InternetAddress.toString(message.getRecipients(Message.RecipientType.TO));
			
			Message replyMessage = new MimeMessage(session);
			replyMessage = (MimeMessage) message.reply(false);
			replyMessage.setFrom(new InternetAddress(to));
			replyMessage.setContent(frameMailTemplate(req.getMailBody(),replyMessage.getSubject() , req.getInsuranceId(), req.getMailRegards()), "text/html");
			replyMessage.setReplyTo(message.getReplyTo());

			Transport t = session.getTransport("smtp");
			try {

				t.connect(mailCred.getSmtpUser(), mailCred.getSmtpPwd());
				t.sendMessage(replyMessage, replyMessage.getAllRecipients());

			} finally {
				t.close();
			}
			System.out.println("message replied successfully ....");
			res.setResponse("message replied successfully ....");

			f.close(false);
			store.close();
			
			store.close();

		} catch (Exception mex) {
			mex.printStackTrace();
			res.setResponse("Replied Message Failed....");
		}
		return res;
	}
	@Override
	public List<SmsGetRes> getSms(SmsGetReq req) {
		ModelMapper mapper = new ModelMapper();
		List<SmsGetRes> resList = new ArrayList<SmsGetRes>();
		try {
			List<SmsDataDetails> smsdatas = smsdatarepo.findByMobileNoOrderByReqTimeDesc(req.getMobileNo());
			for(SmsDataDetails data : smsdatas) {
				SmsGetRes res = new SmsGetRes();	
			mapper.map(data,res);
			resList.add(res);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return resList;
	}
	

}
