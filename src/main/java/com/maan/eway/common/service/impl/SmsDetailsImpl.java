package com.maan.eway.common.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.NotifTemplateMaster;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.SmsConfigMaster;
import com.maan.eway.bean.SmsDetails;
import com.maan.eway.common.req.SendSmsReq;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.NotifTemplateMasterRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.SmsConfigMasterRepository;
import com.maan.eway.repository.SmsDetailsRepository;
import com.maan.eway.res.SuccessRes;


@Service
@Transactional

public class SmsDetailsImpl {

	private Logger log=LogManager.getLogger(SmsDetailsImpl.class);

	
	@Autowired
	private HomePositionMasterRepository homerepo;
	
	@Autowired
	private SmsConfigMasterRepository smsConfigRepo;
	
	@Autowired
	private SmsDetailsRepository smsDetailRepo;
	
	@Autowired
	private NotifTemplateMasterRepository notifRepo;
	
	@Autowired
	private PersonalInfoRepository personalRepo;
	
	@Autowired
	private com.maan.eway.service.PrintReqService reqPrinter;
	
	
	@Autowired
	private GenerateSeqNoServiceImpl genSeqNoService ; 

	
	public SuccessRes sendSms(SendSmsReq req) {
		reqPrinter.reqPrint(req);

		SuccessRes res = new SuccessRes();
		SmsDetails savedata = new SmsDetails();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Long sno = smsDetailRepo.count();
			sno=sno+1L;
			String refNo = "";

			Random rand = new Random();
			int random = rand.nextInt(90) + 10;
			refNo = "SMS"+"-"  + genSeqNoService.generateRefNo() ; 
	
			
			List<HomePositionMaster> homeposition = homerepo.findByRequestReferenceNo(req.getRequestReferenceNo());
			List<SmsConfigMaster> smsconfig = smsConfigRepo.findByCompanyIdAndBranchCode(req.getCompanyId(),req.getBranchCode());
			PersonalInfo customerdata = personalRepo.findByCustomerId(req.getCustomerId());
			mapper.map(req,SmsDetails.class);

			// Customer Personal Info 
			
			savedata.setCustomerReferenceNo(customerdata.getCustomerReferenceNo());
			savedata.setCustomerName(customerdata.getClientName());
			savedata.setMobileNoDesc(req.getMobileNoDesc());
			savedata.setMobileNo(req.getMobileNo());

			// Product Details
			savedata.setCompanyId(req.getCompanyId());
			savedata.setBranchCode(req.getBranchCode());
			savedata.setProductId(req.getProductId());
			savedata.setSectionId(req.getSectionId());
			savedata.setCustomerReferenceNo(homeposition.get(0).getCustomerId());
			savedata.setQuoteNo(req.getQuoteNo());
			savedata.setPolicyNo(req.getPolicyNo());
			savedata.setSmsType(req.getSmsSubject());
			savedata.setSmsContent(req.getSmsBody());
			savedata.setEntryDate(new Date());
			savedata.setCreatedBy(req.getLoginId());
			savedata.setStatus("Y");
			savedata.setResSuccess("SMS Sent Successfully");
			savedata.setSmsRefNo(refNo);
			savedata.setSNo(sno.toString());
			savedata.setSenderId(smsconfig.get(0).getSenderId());
			
			
			res.setResponse("SMS Sent Successfully");
			res.setSuccessId(refNo);
			reqPrinter.reqPrint(res);
			
			smsDetailRepo.save(savedata);
			}
		
		catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details", e.getMessage());
			return null;
		}
		return res;
	}
		
}