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
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.EserviceTravelGroupDetails;
import com.maan.eway.common.res.EserviceSaveRes;
import com.maan.eway.common.res.TravelCopyRes;
import com.maan.eway.common.res.TravelGroupGetRes;
import com.maan.eway.common.service.impl.MotorGridServiceImpl;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.EserviceTravelGroupDetailsRepository;

@Service
public class CopyTravelRaw {



	@Autowired
	private EserviceTravelDetailsRepository etravelRepo;
	
	@Autowired	 
	private MotorGridServiceImpl numberGenerate ;
	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;
	
	@Autowired
	private EserviceTravelGroupDetailsRepository groupRepo ;
	
	public TravelCopyRes copyTravelRaw(Endorsment request) {
		try {
			
			// Risk
			TravelCopyRes  riskRes =  copyTravelRiskTable(request);
			
			// Group
			List<TravelGroupGetRes> travelGroupList = copyTravelRiskGroup(riskRes.getRequestReferenceNo() , riskRes.getOldRequestReferenceNo() );
			riskRes.setGroupDetails(travelGroupList);
			
			return riskRes ;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public TravelCopyRes copyTravelRiskTable(Endorsment ent) {
		try {
			List<EserviceTravelDetails> travelDatas=null;
			Integer count=etravelRepo.countByOriginalPolicyNoAndRiskId(ent.getPolicyNo(),1);
			String prevPolicyNo=null;
			String prevQuoteNo=null;
			String newRequestNo =null;
			long pendingcount =0;
			if(count>0) {
				List<EserviceTravelDetails> travelList=etravelRepo.findByOriginalPolicyNoAndRiskId(ent.getPolicyNo(),1);
				//Compar
				travelList.sort(new Comparator<EserviceTravelDetails>() {

					@Override
					public int compare(EserviceTravelDetails o1, EserviceTravelDetails o2) {
						// TODO Auto-generated method stub
						return o1.getEndtCount().compareTo(o2.getEndtCount());
					}
				}.reversed());
				
				pendingcount = travelList.stream().filter(m->m.getEndtStatus().equals("P")).count();
				if(pendingcount>0) {
					 List<EserviceTravelDetails> pendingData = travelList.stream().filter(m->m.getEndtStatus().equals("P")).collect(Collectors.toList());
					 travelDatas= pendingData;
					 prevPolicyNo=travelDatas.get(0).getEndtPrevPolicyNo();
					 prevQuoteNo=travelDatas.get(0).getEndtPrevQuoteNo();
					 newRequestNo=travelDatas.get(0).getRequestReferenceNo();
					 count--;
				}else {
					travelDatas=travelList;
					
					if(travelList.size()>1) {
						prevPolicyNo=travelList.get(1).getPolicyNo();
						prevQuoteNo =travelList.get(1).getQuoteNo();
					}else {
						prevPolicyNo=ent.getPolicyNo();
						prevQuoteNo =travelDatas.get(0).getEndtPrevQuoteNo();
					}
				}
				
			}else {
				travelDatas=etravelRepo.findByPolicyNoAndStatus(ent.getPolicyNo(),"P");
				prevPolicyNo=ent.getPolicyNo();
				prevQuoteNo =travelDatas.get(0).getQuoteNo();
			}
			if(pendingcount==0)
				newRequestNo=numberGenerate.generateRequestNo(ent.getCompanyId(), ent.getBranchCode(), String.valueOf(ent.getProductId()));
			
			EndtTypeMaster entMaster=endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeId(ent.getCompanyId(), ent.getProductId().intValue(), "Y",Integer.parseInt(ent.getEndtType()));
			List<EserviceTravelDetails> travelList=etravelRepo.findByQuoteNoOrderByRiskIdAsc(prevQuoteNo);
			List<EserviceTravelDetails> newtravelList=new ArrayList<EserviceTravelDetails>();
			++count;
			for(EserviceTravelDetails m :travelList) {
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				EserviceTravelDetails newObject = dozerMapper.map(m , EserviceTravelDetails.class);
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
				newtravelList.add(newObject);
			}
			etravelRepo.saveAllAndFlush(newtravelList);
			
			// Response 
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			TravelCopyRes res = dozerMapper.map(newtravelList.get(0) , TravelCopyRes.class);
			
			List<EserviceTravelDetails> prevDatas = etravelRepo.findByOriginalPolicyNoAndRiskId(prevPolicyNo , 1 );
			res.setOldRequestReferenceNo(prevDatas.get(0).getRequestReferenceNo() );
			
			return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public List<TravelGroupGetRes> copyTravelRiskGroup(String newReqRefNo , String  oldReqRefNo) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<EserviceTravelGroupDetails>  oldGroupDatas = groupRepo.findByRequestReferenceNoOrderByGroupIdAsc(oldReqRefNo) ;
			
			// Delete NEw Tavel Group Details
			long groupCount =  groupRepo.countByRequestReferenceNo(newReqRefNo);
			if(groupCount > 0 ) {
				groupRepo.deleteByRequestReferenceNo(newReqRefNo);
			}
			
			// Insert Tavel Group Details
			List<TravelGroupGetRes> travelGroupList = new ArrayList<TravelGroupGetRes>();
			List<EserviceTravelGroupDetails> saveNewGroups = new ArrayList<EserviceTravelGroupDetails>();
			for (EserviceTravelGroupDetails data : oldGroupDatas) {
				EserviceTravelGroupDetails saveGroup = new EserviceTravelGroupDetails();
				
				// Save
				dozerMapper.map(data, saveGroup);
				saveGroup.setRequestReferenceNo(newReqRefNo);	
				saveGroup.setEntryDate(new Date());
				saveGroup.setStatus("Y");
				saveNewGroups.add(saveGroup);
				
				// Group Res
				TravelGroupGetRes groupRes = new TravelGroupGetRes(); 
				groupRes.setGroupId(saveGroup.getGroupId().toString());
				groupRes.setGroupMembers(saveGroup.getGrouppMembers().toString());
				groupRes.setTravelId(saveGroup.getTravelId().toString());
				travelGroupList.add(groupRes);
				
			}
			
			groupRepo.saveAllAndFlush(saveNewGroups);
			
			return travelGroupList;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
