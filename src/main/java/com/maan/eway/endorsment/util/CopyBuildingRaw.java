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
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelGroupDetails;
import com.maan.eway.bean.OccupationMaster;
import com.maan.eway.common.res.BuildingCopyRes;
import com.maan.eway.common.res.EserviceBuildingSaveRes;
import com.maan.eway.common.res.EserviceSaveRes;
import com.maan.eway.common.res.TravelGroupGetRes;
import com.maan.eway.common.service.impl.MotorGridServiceImpl;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;

@Service
public class CopyBuildingRaw {

	@Autowired
	private EserviceBuildingDetailsRepository eBuildingRepo;
	
	@Autowired	 
	private MotorGridServiceImpl numberGenerate ;
	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;
	
	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo;


	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo;

	
	public List<BuildingCopyRes> copyBuildingRaw(Endorsment request) {
		try {
			
			// Risk Copy
			BuildingCopyRes  riskRes =  copyBuildingRiskTable(request);
			
			// Section Copy
			List<BuildingCopyRes>  secRiskList = copyBuildingSections(riskRes.getRequestReferenceNo() ,
					riskRes.getOldRequestReferenceNo() , riskRes ) ;

			List<String> sectionIds = secRiskList.stream().map(BuildingCopyRes :: getSectionId  ).collect(Collectors.toList() ) ;
			
			// Personal Accident Copy
			String res = copyPersonalAccident (riskRes.getRequestReferenceNo() ,	riskRes.getOldRequestReferenceNo() ,sectionIds ,  riskRes  ) ;
			
			
			return secRiskList ;
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public BuildingCopyRes copyBuildingRiskTable(Endorsment ent) {
		try {
			List<EserviceBuildingDetails> BuildingDatas=null;
			Integer count=eBuildingRepo.countByOriginalPolicyNoAndRiskId(ent.getPolicyNo(),1);
			String prevPolicyNo=null;
			String prevQuoteNo=null;
			String newRequestNo =null;
			long pendingcount =0;
			if(count>0) {
				List<EserviceBuildingDetails> BuildingList=eBuildingRepo.findByOriginalPolicyNoAndRiskId(ent.getPolicyNo(),1);
				//Compar
				BuildingList.sort(new Comparator<EserviceBuildingDetails>() {

					@Override
					public int compare(EserviceBuildingDetails o1, EserviceBuildingDetails o2) {
						// TODO Auto-generated method stub
						return o1.getEndtCount().compareTo(o2.getEndtCount());
					}
				}.reversed());
				
				pendingcount = BuildingList.stream().filter(m->m.getEndtStatus().equals("P")).count();
				if(pendingcount>0) {
					 List<EserviceBuildingDetails> pendingData = BuildingList.stream().filter(m->m.getEndtStatus().equals("P")).collect(Collectors.toList());
					 BuildingDatas= pendingData;
					 prevPolicyNo=BuildingDatas.get(0).getEndtPrevPolicyNo();
					 prevQuoteNo=BuildingDatas.get(0).getEndtPrevQuoteNo();
					 newRequestNo=BuildingDatas.get(0).getRequestReferenceNo();
					 count--;
				}else {
					BuildingDatas=BuildingList;
					
					if(BuildingList.size()>1) {
						prevPolicyNo=BuildingList.get(1).getPolicyNo();
						prevQuoteNo =BuildingList.get(1).getQuoteNo();
					}else {
						prevPolicyNo=ent.getPolicyNo();
						prevQuoteNo =BuildingDatas.get(0).getEndtPrevQuoteNo();
					}
				}
				
			}else {
				BuildingDatas=eBuildingRepo.findByPolicyNoAndStatus(ent.getPolicyNo(),"P");
				prevPolicyNo=ent.getPolicyNo();
				prevQuoteNo =BuildingDatas.get(0).getQuoteNo();
			}
			if(pendingcount==0)
				newRequestNo=numberGenerate.generateRequestNo(ent.getCompanyId(), ent.getBranchCode(), String.valueOf(ent.getProductId()));
			
			EndtTypeMaster entMaster=endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeId(ent.getCompanyId(), ent.getProductId().intValue(), "Y",Integer.parseInt(ent.getEndtType()));
			List<EserviceBuildingDetails> BuildingList=eBuildingRepo.findByQuoteNoOrderByRiskIdAsc(prevQuoteNo);
			List<EserviceBuildingDetails> newBuildingList=new ArrayList<EserviceBuildingDetails>();
			++count;
			for(EserviceBuildingDetails m :BuildingList) {
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				EserviceBuildingDetails newObject = dozerMapper.map(m , EserviceBuildingDetails.class);
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
				newBuildingList.add(newObject);
			}
			eBuildingRepo.saveAllAndFlush(newBuildingList);
			
			// Response 
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			BuildingCopyRes res = dozerMapper.map(newBuildingList.get(0) , BuildingCopyRes.class);
			
			List<EserviceBuildingDetails> prevDatas = eBuildingRepo.findByOriginalPolicyNoAndRiskId(prevPolicyNo , 1 );
			res.setOldRequestReferenceNo(prevDatas.get(0).getRequestReferenceNo() );
			res.setPolicyNo(ent.getPolicyNo()+"-"+count) ;
			;
			return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<BuildingCopyRes> copyBuildingSections(String newReqRefNo , String  oldReqRefNo , BuildingCopyRes buildingData ) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<EserviceSectionDetails>  oldSecDatas = eserSecRepo.findByRequestReferenceNoOrderBySectionIdAsc(oldReqRefNo) ;
			
			// Building Section Insert
			Long buildSecCount = eserSecRepo.countByRequestReferenceNoAndRiskId(newReqRefNo, 1);
			if (buildSecCount > 0) {
				eserSecRepo.deleteByRequestReferenceNoAndRiskId(newReqRefNo, 1);
			}

			List<BuildingCopyRes> resList = new ArrayList<BuildingCopyRes>(); 
			for (EserviceSectionDetails section : oldSecDatas) {
				EserviceSectionDetails secData = new EserviceSectionDetails();
			
				dozerMapper.map(section, secData);
				secData.setRequestReferenceNo(newReqRefNo);
				secData.setUserOpt("N");
				secData.setPolicyNo(buildingData.getPolicyNo());
				secData.setQuoteNo(null);
				eserSecRepo.saveAndFlush(secData);
				
				BuildingCopyRes res = new BuildingCopyRes();
				res.setRequestReferenceNo(newReqRefNo);
				res.setCustomerReferenceNo(secData.getCustomerReferenceNo());
				res.setLocationId(secData.getRiskId().toString() );
				res.setInsuranceId(secData.getCompanyId());
				res.setRiskId(secData.getRiskId().toString() );
				res.setCreatedBy(secData.getCreatedBy());
				res.setProductId(secData.getProductId());
				res.setSectionId(secData.getSectionId());
				resList.add(res);
			}
			
			return resList;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String  copyPersonalAccident(String newReqRefNo , String  oldReqRefNo , List<String> sectionIds ,  BuildingCopyRes buildingData ) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		String res = "" ;
		try {
			EserviceCommonDetails oldAccData = eserCommonRepo.findByRequestReferenceNoAndRiskId(oldReqRefNo, 1); 
			
			EserviceCommonDetails accdata = new EserviceCommonDetails();
			
			List<String> sectionids = sectionIds ;
			List<String> result = sectionids.stream().filter(sectionid -> "35".equalsIgnoreCase(sectionid))
					.collect(Collectors.toList());
			if(result!=null&& result.size()>0  ) {

			if (result.get(0).equalsIgnoreCase("35")) {

				Long count = eserCommonRepo.countByRequestReferenceNo(newReqRefNo);
				if (count > 0) {
					eserCommonRepo.deleteByRequestReferenceNo(newReqRefNo);
				}

				dozerMapper.map(oldAccData, accdata);
				accdata.setPolicyNo(buildingData.getPolicyNo());
				accdata.setQuoteNo(null);
				eserCommonRepo.save(accdata);
				res = "Saved Succefully" ;
			}
			res = "Not Data Available" ;
				
			
			}
			return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
