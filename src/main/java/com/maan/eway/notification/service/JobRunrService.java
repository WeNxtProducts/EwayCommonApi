package com.maan.eway.notification.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.jobrunr.jobs.annotations.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.MailMaster;
import com.maan.eway.bean.NotifTemplateMaster;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.notification.repository.NotifTransactionDetailsRepository;
import com.maan.eway.notification.req.Mail;
import com.maan.eway.notification.req.Messenger;
import com.maan.eway.notification.req.Sms;
import com.maan.eway.repository.MailMasterRepository;
import com.maan.eway.repository.NotifTemplateMasterRepository;

@Service
public class JobRunrService {


	@Autowired
	private NotifTemplateMasterRepository masterRepo;

	@Autowired
	private MailMasterRepository mailRepo;
	

	@Autowired
	private RatingFactorsUtil rat;
	
	@Autowired
	private NotifTransactionDetailsRepository notRepo;
	@Autowired
	MailJob job;
	
	@Job(name = "The sample job with variable %0", retries = 2)
	public void jobProcess() {


		
		Pageable pages = PageRequest.of(0, 50);

		List<List<Object>> collect =null;
		Date d=new Date();
		List<NotifTransactionDetails> transDetails= notRepo.findByNotifPushedStatusAndNotifcationPushDateLessThanEqualAndNotifcationEndDateGreaterThanEqualOrderByNotifPriorityDesc("P",d,d,pages);
		if(transDetails.size()>0) {		
			try {
			List<Tuple> ne = rat.loadNotificationPending();
			transDetails.stream().forEach(tr-> tr.setNotifPushedStatus("Y"));
			notRepo.saveAll(transDetails);

			Map<String, Map<Integer, Map<String, List<NotifTransactionDetails>>>> groups = transDetails.stream().collect(Collectors.groupingBy(NotifTransactionDetails::getCompanyid,
					Collectors.groupingBy(NotifTransactionDetails::getProductid,
							Collectors.groupingBy(NotifTransactionDetails::getNotifTemplatename))));



			synchronized (transDetails) {

				for (Entry<String, Map<Integer, Map<String, List<NotifTransactionDetails>>>> g : groups.entrySet()){
					Map<Integer, Map<String, List<NotifTransactionDetails>>> h = g.getValue();
					for (Entry<Integer, Map<String, List<NotifTransactionDetails>>> h1 : h.entrySet()) {
						Map<String, List<NotifTransactionDetails>> h2 = h1.getValue();
						for (Entry<String, List<NotifTransactionDetails>> h3 : h2.entrySet()) {

							List<NotifTransactionDetails> n=h3.getValue();
							List<NotifTemplateMaster> templat = masterRepo.findByCompanyIdAndProductIdAndStatusAndNotifTemplatenameIgnoreCaseOrderByAmendIdDesc(n.get(0).getCompanyid(),Long.valueOf(n.get(0).getProductid()),"Y",n.get(0).getNotifTemplatename());
							List<MailMaster> mailc = mailRepo.findByCompanyIdAndBranchCodeAndStatusOrderByAmendIdDesc(n.get(0).getCompanyid(),"99999","Y");						
							PushedStateChange p=new PushedStateChange(templat.get(0),mailc.get(0));					
							collect = ne.stream().map(p).filter(dd->dd!=null).collect(Collectors.toList());					
							List<Mail> totalMailJob=new ArrayList<Mail>();
							List<Sms> totalSmSJob=new ArrayList<Sms>();
							List<Messenger> totalMessnJob=new ArrayList<Messenger>();

							if(!collect.isEmpty()) {
								for (List<Object> list : collect) {
									//totalJob.addAll(list);
									for (Object o:list) {

										if(o instanceof Mail) {
											totalMailJob.add((Mail) o);
										}else if(o instanceof Sms) {
											totalSmSJob.add((Sms) o);
										}else if(o instanceof Messenger) {
											totalMessnJob.add((Messenger) o);
										}

									}
								}
								if(!totalMailJob.isEmpty()) {

									totalMailJob.stream().forEach(job);									
								}


							}
						}
					}
				}


			}

			transDetails.stream().forEach(tr-> tr.setNotifPushedStatus("C"));
			notRepo.saveAll(transDetails);
			}catch (Exception e) {
				e.printStackTrace();
				transDetails.stream().forEach(tr-> tr.setNotifPushedStatus("E"));
				notRepo.saveAll(transDetails);
			}

		}

		

	}
}
