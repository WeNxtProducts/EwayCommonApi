package com.maan.eway.endorsment.util;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.common.service.impl.MotorGridServiceImpl;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;

@Service
public class CopyRawTable  {

	@Autowired
	private EServiceMotorDetailsRepository emotorRepo;
	
	@Autowired	 
	private MotorGridServiceImpl numberGenerate ;
	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;
	 
	
	
	public EserviceMotorDetails copyMotorRaw(Endorsment ent) {
		try {
			EserviceMotorDetails motor=null;
			Integer count=emotorRepo.countByOriginalPolicyNo(ent.getPolicyNo());
			String prevPolicyNo=null;
			String prevQuoteNo=null;
			String newRequestNo =null;
			long pendingcount =0;
			if(count>0) {
				List<EserviceMotorDetails> motors=emotorRepo.findByOriginalPolicyNo(ent.getPolicyNo());
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
					 motor= pendingData.get(0);
					 prevPolicyNo=motor.getEndtPrevPolicyNo();
					 prevQuoteNo=motor.getEndtPrevQuoteNo();
					 newRequestNo=motor.getRequestReferenceNo();
					 count--;
				}else {
					motor=motors.get(0);
					
					if(motors.size()>1) {
						prevPolicyNo=motors.get(1).getPolicyNo();
						prevQuoteNo =motors.get(1).getQuoteNo();
					}else {
						prevPolicyNo=ent.getPolicyNo();
						prevQuoteNo =motor.getEndtPrevQuoteNo();
					}
				}
				
			}else {
				motor=emotorRepo.findByPolicyNoAndStatus(ent.getPolicyNo(),"P");
				prevPolicyNo=ent.getPolicyNo();
				prevQuoteNo =motor.getQuoteNo();
			}
			if(pendingcount==0)
				newRequestNo=numberGenerate.generateRequestNo(ent.getCompanyId(), ent.getBranchCode(), String.valueOf(ent.getProductId()));
			
			EndtTypeMaster entMaster=endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeId(ent.getCompanyId(), ent.getProductId().intValue(), "Y",Integer.parseInt(ent.getEndtType()));
			
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			EserviceMotorDetails newObject = dozerMapper.map(motor, EserviceMotorDetails.class);
			newObject.setRequestReferenceNo(newRequestNo);
			newObject.setOriginalPolicyNo(ent.getPolicyNo());
			newObject.setEndorsementDate(new Date());
			newObject.setEndorsementRemarks(ent.getEndtRemarks());
			newObject.setEndorsementEffdate(ent.getEndtEffectiveDate());
			newObject.setEndtPrevPolicyNo(prevPolicyNo);
			newObject.setEndtPrevQuoteNo(prevQuoteNo);
			newObject.setEndtCount(new BigDecimal(++count));
			newObject.setEndtStatus("P");
			newObject.setIsFinaceYn(entMaster.getEndtTypeCategoryId()==2?"Y":"N");
			newObject.setEndtCategDesc(entMaster.getEndtTypeCategory());
			newObject.setEndorsementType(Integer.parseInt(ent.getEndtType()));
			newObject.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
			EserviceMotorDetails save = emotorRepo.save(newObject);
			return save;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
 

}
