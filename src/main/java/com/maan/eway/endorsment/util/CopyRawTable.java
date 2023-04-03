package com.maan.eway.endorsment.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.CoverDocumentUploadDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.UwQuestionsDetails;
import com.maan.eway.common.req.ChangeEndoStatusReq;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.common.service.impl.MotorGridServiceImpl;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.notification.repository.CoverDocumentUploadDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.SeqCustidRepository;
import com.maan.eway.repository.SeqQuotenoRepository;
import com.maan.eway.repository.SeqRefnoRepository;
import com.maan.eway.repository.UwQuestionsDetailsRepository;
import com.maan.eway.res.CopyQuoteSuccessRes;

@Service
public class CopyRawTable  {

	@Autowired
	private EServiceMotorDetailsRepository emotorRepo;
	
	@Autowired	 
	private MotorGridServiceImpl numberGenerate ;
/*	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;
	 */
	@Autowired
	private HomePositionMasterRepository homePosistionRepo;
	
	@Autowired
	private PersonalInfoRepository personalInforepo;
	
	@Autowired
	private MotorDataDetailsRepository motorDataDetepo;
	
	@Autowired
	private EServiceMotorDetailsRepository repo;
	
	@Autowired
	private EserviceCustomerDetailsRepository custRepo ;
	
	@Autowired
	private MotorDriverDetailsRepository motordrivDetepo;
	
	@Autowired
	private CoverDocumentUploadDetailsRepository coverDocUploadDetails;
	
	@Autowired
	private UwQuestionsDetailsRepository uwquestionRepo;

	
	private Logger log = LogManager.getLogger(MotorGridServiceImpl.class);
	
	public List<EserviceMotorDetails> copyMotorRaw(Endorsment ent, EndtTypeMaster entMaster) {
		try {
			List<EserviceMotorDetails> motor=null;
			Integer count=emotorRepo.countByOriginalPolicyNoAndRiskId(ent.getPolicyNo(),1);
			String prevPolicyNo=null;
			String prevQuoteNo=null;
			String prevRequestRefNo=null;
			String newRequestNo =null;
			long pendingcount =0;
			if(count>0) {
				List<EserviceMotorDetails> motors=emotorRepo.findByOriginalPolicyNoAndRiskId(ent.getPolicyNo(),1);
				//Compar
				motors.sort(new Comparator<EserviceMotorDetails>() {

					@Override
					public int compare(EserviceMotorDetails o1, EserviceMotorDetails o2) {
						// TODO Auto-generated method stub
						return o1.getEndtCount().compareTo(o2.getEndtCount());
					}
				}.reversed());
				
				pendingcount = motors.stream().filter(m->m.getEndtStatus().equals("P")).count();
				if(pendingcount>0) {
					 List<EserviceMotorDetails> pendingData = motors.stream().filter(m->m.getEndtStatus().equals("P")).collect(Collectors.toList());
					 motor= pendingData;
					 prevPolicyNo=motor.get(0).getEndtPrevPolicyNo();
					 prevQuoteNo=motor.get(0).getEndtPrevQuoteNo();
					 newRequestNo=motor.get(0).getRequestReferenceNo();
					 prevRequestRefNo=motor.get(0).getRequestReferenceNo();
					 count--;
				}else {
					motor=motors.stream().filter(m->m.getEndtStatus().equals("C")).collect(Collectors.toList());
					
					if(motors.size()>1) {
						prevPolicyNo=motors.get(0).getPolicyNo();
						prevQuoteNo =motors.get(0).getQuoteNo();
						prevRequestRefNo=motor.get(0).getRequestReferenceNo();
					}else {
						prevPolicyNo=motor.get(0).getPolicyNo();
						prevQuoteNo =motor.get(0).getQuoteNo();
						prevRequestRefNo=motor.get(0).getRequestReferenceNo();
					}
				}
				
			}else {
				motor=emotorRepo.findByPolicyNoAndStatus(ent.getPolicyNo(),"P");
				prevPolicyNo=ent.getPolicyNo();
				prevQuoteNo =motor.get(0).getQuoteNo();
				prevRequestRefNo=motor.get(0).getRequestReferenceNo();

			}
			if(pendingcount==0)
				newRequestNo=numberGenerate.generateRequestNo(ent.getCompanyId(), ent.getBranchCode(), String.valueOf(ent.getProductId()));
			
			//EndtTypeMaster entMaster=endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartGreaterThanEqualAndEffectiveDateEndLessThanEqual(ent.getCompanyId(), ent.getProductId().intValue(), "Y",Integer.parseInt(ent.getEndtType()), new Date(), new Date());
			List<EserviceMotorDetails> motors=emotorRepo.findByQuoteNoAndStatusOrderByRiskIdAsc(prevQuoteNo,"Y");
			List<EserviceMotorDetails> newMotors=new ArrayList<EserviceMotorDetails>();
			++count;
			for(EserviceMotorDetails m :motors) {
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				EserviceMotorDetails newObject = dozerMapper.map(m , EserviceMotorDetails.class);
				newObject.setRequestReferenceNo(newRequestNo);
				newObject.setOriginalPolicyNo(ent.getPolicyNo());
				newObject.setEndorsementDate(new Date());
				newObject.setEndorsementRemarks(ent.getEndtRemarks());
				newObject.setEndorsementEffdate(ent.getEndtEffectiveDate());
				newObject.setEndtPrevPolicyNo(prevPolicyNo);
				newObject.setEndtPrevQuoteNo(prevQuoteNo);
				newObject.setEndtCount(new BigDecimal(count));
				newObject.setEndtStatus("P");
				newObject.setIsFinaceYn(entMaster.getEndtTypeCategoryId()==2?"Y":"N");
				newObject.setEndtCategDesc(entMaster.getEndtTypeCategory());
				newObject.setEndorsementType(Integer.parseInt(ent.getEndtType()));
				newObject.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
				newObject.setStatus("E");
				newObject.setPolicyNo(ent.getPolicyNo()+"-"+count);
				newObject.setQuoteNo(null);
				newMotors.add(newObject);
				
				List<UwQuestionsDetails> olduwquestion = uwquestionRepo.findByCompanyIdAndProductIdAndRequestReferenceNoAndVehicleId(ent.getCompanyId(),ent.getProductId().intValue(),prevRequestRefNo,newObject.getRiskId());
				List<UwQuestionsDetails> newuwquestions=new ArrayList<UwQuestionsDetails>();
				for (UwQuestionsDetails ouw : olduwquestion) {
					UwQuestionsDetails newuw = dozerMapper.map(ouw , UwQuestionsDetails.class);
					newuw.setRequestReferenceNo(newRequestNo);
					newuwquestions.add(newuw);
				}
				uwquestionRepo.saveAllAndFlush(newuwquestions);
			}
			List<EserviceMotorDetails> save = emotorRepo.saveAllAndFlush(newMotors);
			
					
			return save;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public EserviceMotorDetails eserviceMotorEndtStatus(ChangeEndoStatusReq req) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		EserviceMotorDetails savedata = new EserviceMotorDetails();
		try {
			//Motor 
			List<EserviceMotorDetails> motors = repo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());

			if (motors.size() > 0) {
				for (EserviceMotorDetails data : motors) {
					savedata = dozerMapper.map(data, EserviceMotorDetails.class);
					savedata.setEndtStatus("C");
					repo.saveAndFlush(savedata);
				}

			}
			// Update EndT Status
			homeEndtStatus(req);
			personolInfoEndtStatus(req);
			motorDataDetailsEndtStatus(req);
			motorDriverDetailsEndtStatus(req);
			coverDocumentUploadDetailsEndtStatus(req);
			eserviceCustDetailsChangeStatus(req);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;
	}

	private CoverDocumentUploadDetails coverDocumentUploadDetailsEndtStatus(ChangeEndoStatusReq req) {
		CoverDocumentUploadDetails savedata = new CoverDocumentUploadDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<CoverDocumentUploadDetails> motorData = coverDocUploadDetails.findByQuoteNo(req.getQuoteNo());
			if (motorData.size() > 0) {
				for (CoverDocumentUploadDetails data : motorData) {
					savedata = dozerMapper.map(data, CoverDocumentUploadDetails.class);
					savedata.setEndtStatus("C");
					coverDocUploadDetails.saveAndFlush(savedata);
				}
			}
		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

		
	}

	private MotorDriverDetails motorDriverDetailsEndtStatus(ChangeEndoStatusReq req) {
		MotorDriverDetails savedata = new MotorDriverDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<MotorDriverDetails> motorDriverData = motordrivDetepo.findByQuoteNo(req.getQuoteNo());
			if (motorDriverData.size() > 0) {
				for (MotorDriverDetails data : motorDriverData) {
					savedata = dozerMapper.map(data, MotorDriverDetails.class);
					savedata.setEndtStatus("C");
					motordrivDetepo.saveAndFlush(savedata);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

		
	}

	private MotorDataDetails motorDataDetailsEndtStatus(ChangeEndoStatusReq req) {
		MotorDataDetails savedata = new MotorDataDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<MotorDataDetails> motorData=motorDataDetepo.findByQuoteNo(req.getQuoteNo());
			if (motorData.size() > 0) {
				for (MotorDataDetails data : motorData) {
					savedata = dozerMapper.map(data, MotorDataDetails.class);
					savedata.setEndtStatus("C");
					motorDataDetepo.saveAndFlush(savedata);
				}
			}
	
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

	}

	private PersonalInfo personolInfoEndtStatus(ChangeEndoStatusReq req) {
		PersonalInfo savedata = new PersonalInfo();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(req.getQuoteNo());
			String customerId=homeData.getCustomerId();
			PersonalInfo personalInfoData=personalInforepo.findByCustomerId(customerId);
			savedata = dozerMapper.map(personalInfoData, PersonalInfo.class);
			savedata.setEndtStatus("C");
			personalInforepo.saveAndFlush(savedata);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return savedata;
		
	}

	private HomePositionMaster homeEndtStatus(ChangeEndoStatusReq req) {
		HomePositionMaster savedata = new HomePositionMaster();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			HomePositionMaster homeData = homePosistionRepo.findByQuoteNo(req.getQuoteNo());
			if (homeData != null) {
				savedata = dozerMapper.map(homeData, HomePositionMaster.class);
				savedata.setEndtStatus("C");
				homePosistionRepo.saveAndFlush(savedata);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

	}
	private EserviceCustomerDetails eserviceCustDetailsChangeStatus(ChangeEndoStatusReq req) {
		EserviceCustomerDetails savedata = new EserviceCustomerDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(req.getQuoteNo());
			String olsCustomerId=homeData.getCustomerId();
			
			PersonalInfo personalInfoData=personalInforepo.findByCustomerId(olsCustomerId);
			EserviceCustomerDetails custData = custRepo.findByCustomerReferenceNo(personalInfoData.getCustomerReferenceNo());
			if (custData!=null) 
					savedata = dozerMapper.map(custData, EserviceCustomerDetails.class);
					savedata.setEndtStatus("P");
					custRepo.saveAndFlush(savedata);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

		
	}


}
