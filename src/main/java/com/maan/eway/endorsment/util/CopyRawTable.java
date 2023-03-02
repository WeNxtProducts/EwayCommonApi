package com.maan.eway.endorsment.util;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

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
				
				long pendingcount = motors.stream().filter(m->m.getEndtStatus().equals("P")).count();
				if(pendingcount>0) {
					 List<EserviceMotorDetails> pendingData = motors.stream().filter(m->m.getEndtStatus().equals("P")).collect(Collectors.toList());
					 motor= pendingData.get(0);
					 prevPolicyNo=motor.getEndtPrevPolicyNo();
					 prevQuoteNo=motor.getEndtPrevQuoteNo();
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
				prevQuoteNo =motor.getEndtPrevQuoteNo();
			}
			
			String newRequestNo = numberGenerate.generateRequestNo(ent.getCompanyId(), ent.getBranchCode(), String.valueOf(ent.getProductId()));
			
			EndtTypeMaster entMaster=endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeId(ent.getCompanyId(), ent.getProductId().intValue(), "Y",Integer.parseInt(ent.getEndtType()));
			
			motor.setRequestReferenceNo(newRequestNo);
			motor.setOriginalPolicyNo(ent.getPolicyNo());
			motor.setEndorsementDate(new Date());
			motor.setEndorsementRemarks(ent.getEndtRemarks());
			motor.setEndorsementEffdate(ent.getEndtEffectiveDate());
			motor.setEndtPrevPolicyNo(prevPolicyNo);
			motor.setEndtPrevQuoteNo(prevQuoteNo);
			motor.setEndtCount(new BigDecimal(count++));
			motor.setEndtStatus("P");
			motor.setIsFinaceYn(entMaster.getEndtTypeCategoryId()==2?"Y":"N");
			motor.setEndtCategDesc(entMaster.getEndtTypeCategory());
			EserviceMotorDetails save = emotorRepo.save(motor);
			return save;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
 

}
