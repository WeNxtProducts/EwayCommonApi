package com.maan.eway.endorsment.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.common.res.CommonCopyRes;
import com.maan.eway.common.service.impl.MotorGridServiceImpl;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;

@Service
public class CopyCommonRaw {

	@Autowired
	private EserviceCommonDetailsRepository eCommonRepo;
	
	@Autowired	 
	private MotorGridServiceImpl numberGenerate ;
	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;
	
	
	public CommonCopyRes copyCommonRaw(Endorsment request) {
		try {
			
			// Risk
			CommonCopyRes  riskRes =  copyCommonRiskTable(request);
			
			
			return riskRes ;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public CommonCopyRes copyCommonRiskTable(Endorsment ent) {
		try {
			List<EserviceCommonDetails> CommonDatas=null;
			Integer count=eCommonRepo.countByOriginalPolicyNoAndRiskId(ent.getPolicyNo(),1);
			String prevPolicyNo=null;
			String prevQuoteNo=null;
			String newRequestNo =null;
			long pendingcount =0;
			if(count>0) {
				List<EserviceCommonDetails> CommonList=eCommonRepo.findByOriginalPolicyNoAndRiskId(ent.getPolicyNo(),1);
				//Compar
				CommonList.sort(new Comparator<EserviceCommonDetails>() {

					@Override
					public int compare(EserviceCommonDetails o1, EserviceCommonDetails o2) {
						// TODO Auto-generated method stub
						return o1.getEndtCount().compareTo(o2.getEndtCount());
					}
				}.reversed());
				
				pendingcount = CommonList.stream().filter(m->m.getEndtStatus().equals("P")).count();
				if(pendingcount>0) {
					 List<EserviceCommonDetails> pendingData = CommonList.stream().filter(m->m.getEndtStatus().equals("P")).collect(Collectors.toList());
					 CommonDatas= pendingData;
					 prevPolicyNo=CommonDatas.get(0).getEndtPrevPolicyNo();
					 prevQuoteNo=CommonDatas.get(0).getEndtPrevQuoteNo();
					 newRequestNo=CommonDatas.get(0).getRequestReferenceNo();
					 count--;
				}else {
					CommonDatas=CommonList;
					
					if(CommonList.size()>1) {
						prevPolicyNo=CommonList.get(1).getPolicyNo();
						prevQuoteNo =CommonList.get(1).getQuoteNo();
					}else {
						prevPolicyNo=ent.getPolicyNo();
						prevQuoteNo =CommonDatas.get(0).getEndtPrevQuoteNo();
					}
				}
				
			}else {
				CommonDatas=eCommonRepo.findByPolicyNoAndStatus(ent.getPolicyNo(),"P");
				prevPolicyNo=ent.getPolicyNo();
				prevQuoteNo =CommonDatas.get(0).getQuoteNo();
			}
			if(pendingcount==0)
				newRequestNo=numberGenerate.generateRequestNo(ent.getCompanyId(), ent.getBranchCode(), String.valueOf(ent.getProductId()));
			
			EndtTypeMaster entMaster=endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeId(ent.getCompanyId(), ent.getProductId().intValue(), "Y",Integer.parseInt(ent.getEndtType()));
			List<EserviceCommonDetails> CommonList=eCommonRepo.findByQuoteNoOrderByRiskIdAsc(prevQuoteNo);
			List<EserviceCommonDetails> newCommonList=new ArrayList<EserviceCommonDetails>();
			++count;
			for(EserviceCommonDetails m :CommonList) {
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				EserviceCommonDetails newObject = dozerMapper.map(m , EserviceCommonDetails.class);
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
				newCommonList.add(newObject);
			}
			eCommonRepo.saveAllAndFlush(newCommonList);
			
			// Response 
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			CommonCopyRes res = dozerMapper.map(newCommonList.get(0) , CommonCopyRes.class);
			
			List<EserviceCommonDetails> prevDatas = eCommonRepo.findByOriginalPolicyNoAndRiskId(prevPolicyNo , 1 );
			res.setOldRequestReferenceNo(prevDatas.get(0).getRequestReferenceNo() );
			
			return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
