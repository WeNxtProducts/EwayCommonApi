package com.maan.eway.notification.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;

import org.apache.commons.lang3.ArrayUtils;

import com.maan.eway.bean.MailMaster;
import com.maan.eway.bean.NotifTemplateMaster;
import com.maan.eway.notification.req.JobCredentials;
import com.maan.eway.notification.req.Mail;
import com.maan.eway.notification.req.Messenger;
import com.maan.eway.notification.req.Sms;

public class PushedStateChange implements  Function<Tuple,List<Object>>{


	private NotifTemplateMaster master;
	 private MailMaster mailMaster;	 
	 

	public PushedStateChange(NotifTemplateMaster master, MailMaster mailMaster) {
		this.master=master;
		this.mailMaster=mailMaster;
	}

	@Override
	public List<Object> apply(Tuple t) {
		try {
			//clazz = t.getClass();
			
			List<Object> a=new ArrayList<Object>();
			if(master.getWhatsappRequired().equals("Y") ) {
				Messenger m=Messenger.builder()
						.messengerBody((String) getContentFrame(t, master.getWhatsappBodyEn()))
						.messengerRegards((String) getContentFrame(t, master.getWhatsappRegards()))
						.messengerSubject((String) getContentFrame(t, master.getWhatsappSubject()))
						.messengerTo((String) getValue(t,master.getToMessengerno()))
						.build();
				a.add(m);
			}
			if(master.getSmsRequired().equals("Y") ) {
				Sms s=Sms.builder()
						.smsBody((String) getContentFrame(t, master.getSmsBodyEn()))
						//.smsRegards((String) getContentFrame(t, master.getSms))
						.smsSubject((String) getContentFrame(t, master.getSmsSubject()))
						.smsTo((String) getValue(t,master.getToSmsno()))					
						.build();
				a.add(s);
			}
			if(master.getMailRequired().equals("Y") ) {
				
				String tomailds=(String) getValue(t,master.getToEmail());
				String tomailid=tomailds;
				List<String> mailcc=null;
				if(tomailds.indexOf(",")!=1) {
					tomailid=tomailds.split(",")[0];
				    String[] mailcsc = tomailid.split(",");
				    List<String> asList = Arrays.asList(mailcsc);
				    mailcc= (asList.size()>5)?asList.subList(0, 5):asList;
				}
				Mail ml=Mail.builder()
						.mailBody((String) getContentFrame(t, master.getMailBody()))
						.mailRegards((String) getContentFrame(t, master.getMailRegards()))
						.mailSubject((String) getContentFrame(t, master.getMailSubject()))
						.mailTo(tomailid)
						.mailcc(mailcc)
						.credential(JobCredentials.builder().host(mailMaster.getSmtpHost()).isSSL(true).password(mailMaster.getSmtpPwd()).port(Long.parseLong(mailMaster.getSmtpPort().toString())).username(mailMaster.getSmtpUser()).build())
						.build();
				a.add(ml);
			}
			return a;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	private Object getValue(Tuple t, String fieldNameString) {
		 try {
			Object o=(Object) t.get(fieldNameString);
			if (o instanceof BigDecimal) {
				return o.toString();
			}
			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

	 
	private Object getContentFrame(Tuple t ,String messageTemplate) {
		try {
			 
		  
			StringBuffer b=new StringBuffer(messageTemplate);
			while (b.indexOf("{")!=-1 && b.indexOf("}")!=-1) {
				 String tx = b.substring(b.indexOf("{")+1, b.indexOf("}"));
				 b.replace(b.indexOf("{"), b.indexOf("}")+1, String.valueOf(t.get(tx)));
			} 
			return b.toString();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	} 
	 
}
