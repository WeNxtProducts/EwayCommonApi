package com.maan.eway.notif.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.NotifTemplateMaster;
import com.maan.eway.notif.req.MailFramingReq;
import com.maan.eway.notif.service.MailFramingService;
import com.maan.eway.repository.NotifTemplateMasterRepository;
import com.maan.eway.service.PrintReqService;

@Service
public class MailThreadServiceImpl {

	@Autowired
	private  PrintReqService printReq;
	
	@Autowired
	private  NotifTemplateMasterRepository notifRepo;
	
	private Logger log = LogManager.getLogger(MailThreadServiceImpl.class);
	
	@Autowired
	private MailFramingService mailFrameService;
	
	
	// Thread Call
	@Transactional
	@Async
	public CompletableFuture<Object> threadToSendMail(MailFramingReq mReq) throws InterruptedException {
		Thread.sleep(100);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss"); 
		Object res = new Object (); 
		Date today = new Date(); 
		printReq.reqPrint(mReq);
		log.info("Thread Start Time ---> " + sdf.format(new Date()) );
		try {
			// Notification Master
			List<NotifTemplateMaster>  notifDetails = notifRepo.findByStatusAndNotificationApplicableAndEffectiveDateStartLessThanEqualOrderByEntryDateDesc("Y", mReq.getNotifTemplateId() , today); 
			
			if(notifDetails !=null && notifDetails.size()>0 ) {		
				NotifTemplateMaster notifData = notifDetails.get(0) ;
				String mailBody    =  notifData.getMailBody() ;
				String mailSubject =  notifData.getMailSubject() ;
				
				if(mReq.getKeys() != null && mReq.getKeys().size()>0) {
					// Mail Trigger
					mailFrameService.frameMail(mReq , mailBody ,mailSubject ) ;
				}
				
					
			}	
			log.info("Thread End Time ---> " + sdf.format(new Date()));
			return CompletableFuture.completedFuture(res);
		}catch(Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
			
		return null;	
	}
	
	
		
	
	
		

}
