package com.maan.eway.notification.service;

import java.io.File;
import java.util.Date;
import java.util.Properties;
import java.util.function.Consumer;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.maan.eway.notification.bean.MailDataDetails;
import com.maan.eway.notification.repository.MailDataDetailsRepository;
import com.maan.eway.notification.req.Mail;

public class MailJob implements Consumer<Mail> {

	 
	
	/*@Autowired
	private MailDataDetailsRepository mailRepo;
	*/
	
	 
	public void pushMail(Mail m) {
		   /*
		String statusResponse=null;
		try {
			Properties prop = new Properties();
			prop.put("mail.smtp.host", m.getCredential().getHost());
			prop.put("mail.smtp.port", m.getCredential().getPort());
			if(m.getCredential().getIsSSL()) {
				prop.put("mail.smtp.auth", "true");
				prop.put("mail.smtp.starttls.enable", "true"); // TLS
			}else {
				prop.put("mail.smtp.auth", "false");
				prop.put("mail.smtp.starttls.enable", "false"); // TLS
			}
			
			Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(m.getCredential().getUsername(), m.getCredential().getPassword());
				}
			});
			MimeMessage mimeMessage = new MimeMessage(session);

			mimeMessage.setFrom(new InternetAddress(m.getCredential().getUsername()));
			
			InternetAddress	to = new InternetAddress(m.getMailTo());
			mimeMessage.addRecipient(Message.RecipientType.TO, to);
			// Mail Cc
			InternetAddress[] addressCc=null;
			if (m.getMailcc() != null && m.getMailcc().size()>0 ) {
				 addressCc = new InternetAddress[m.getMailcc().size()];
				for (int i = 0; i < m.getMailcc().size(); i++) {
					if (StringUtils.isNotBlank( m.getMailcc().get(i))) {
						addressCc[i] = new InternetAddress( m.getMailcc().get(i)); 
						mimeMessage.addRecipient(Message.RecipientType.CC, addressCc[i]); 
					}
				} 
			}
			 
			mimeMessage.setSubject(m.getMailSubject());
			mimeMessage.setContent(m.getMailBody(), "text/html");
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			helper.setSubject(m.getMailSubject());
			helper.setText(m.getMailBody(), true);
			if(m.getAttachments()!=null && StringUtils.isNotBlank(m.getAttachments())) {
				for (String attachPath : m.getAttachments().split(";")) {
					File file=loadFilesFromPath(attachPath);
					if (file != null && file.exists())
						helper.addAttachment(file.getName(), file);
				}
			}
			
			
			
			Transport.send(mimeMessage);
		}catch (Exception e) {
			e.printStackTrace();
			statusResponse=e.getLocalizedMessage();
		}
		
		
		MailDataDetails mdd=MailDataDetails.builder()
				.fromEmail(m.getCredential().getUsername())
				.mailBody(m.getMailBody())
				.mailRegards(m.getMailRegards())
				.mailResponse(statusResponse)
				.mailSubject(m.getMailSubject())
				.mailTranId(null)
				.pushedEntryDate(new Date())
				.status(statusResponse==null?"S":"F")
				.toEmail(m.getMailTo())
				//.notifNo(m.getNotifNo())
				.build();
		mailRepo.save(mdd);
		 */
	}
	@Override
	public void accept(Mail t) {
		pushMail(t);
	}
	

	private File loadFilesFromPath(String attachPath) {
		
		try {
			return new File(attachPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
