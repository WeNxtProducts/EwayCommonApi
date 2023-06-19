package com.maan.eway.endorsment.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.EserviceTravelGroupDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.res.EndorsementCriteriaRes;
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

	@Autowired 
	private RatingFactorsUtil ratingutil;
	
	public EserviceTravelDetails copyTravelRaw(Endorsment request) {
		try {
			
			// Risk
			TravelCopyRes  riskRes =  copyTravelRiskTable(request);
			
			// Group
			List<TravelGroupGetRes> travelGroupList = copyTravelRiskGroup(riskRes.getRequestReferenceNo() , riskRes.getOldRequestReferenceNo() );
			riskRes.setGroupDetails(travelGroupList);
			
			EserviceTravelDetails travelData = etravelRepo.findByRequestReferenceNo(riskRes.getRequestReferenceNo() ); 
			
			return travelData  ;
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
			
			EndtTypeMaster entMaster=ratingutil.getEndtMasterData(ent.getCompanyId(),ent.getProductId().toPlainString(),ent.getEndtType());

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
			
			List<EserviceTravelDetails> prevDatas = etravelRepo.findByPolicyNoAndRiskId(prevPolicyNo , 1 );
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

	@PersistenceContext
	private EntityManager em;
	
	public List<EndorsementCriteriaRes> endorsementTravelGrid(Endorsment request) {
		 try {

			 
				// Get Datas
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<EndorsementCriteriaRes> query = cb.createQuery(EndorsementCriteriaRes.class);

				// Find All
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
				// Select
				query.multiselect(//cb.literal(Long.parseLong("1")).alias("idsCount"),
						// Customer Info
						cb.max(c.get("customerReferenceNo")).alias("customerReferenceNo"), cb.max(c.get("idNumber")).alias("idNumber"),
						cb.max(c.get("clientName")).alias("clientName"),
						// Vehicle Info
						cb.max(m.get("companyId")).alias("companyId"), cb.max(m.get("productId")).alias("productId"),
						cb.max(m.get("branchCode")).alias("branchCode"), cb.max(m.get("requestReferenceNo")).alias("requestReferenceNo"),
						cb.selectCase().when(cb.max(m.get("quoteNo")).isNotNull(), cb.max(m.get("quoteNo"))).otherwise(cb.max(m.get("quoteNo")))
								.alias("quoteNo"),
						cb.selectCase().when(cb.max(m.get("customerId")).isNotNull(), cb.max(m.get("customerId")))
								.otherwise(cb.max(m.get("customerId"))).alias("customerId"),
						cb.max(m.get("policyStartDate")).alias("policyStartDate"), cb.max(m.get("policyEndDate")).alias("policyEndDate"),
						cb.max(m.get("endorsementType")).alias("endorsementTypeId"),
						cb.max(m.get("endorsementTypeDesc")).alias("endorsementDesc"),
						cb.max(m.get("endtCategDesc")).alias("endorsementCategoryDesc"),
						cb.max(m.get("endorsementEffdate")).alias("effectiveDate"),
						cb.max(m.get("endtStatus")).alias("endorsementStatus"),
						cb.max(m.get("policyNo")).alias("policyNo"),
						cb.max(m.get("endorsementRemarks")).alias("endorsementRemarks"),
						cb.max(m.get("endorsementDate")).alias("endorsementDate"),
						//Home Position Master
						cb.sum(m.get("overallPremiumLc")).alias("overallPremiumLc"), cb.sum(m.get("overallPremiumFc")).alias("overallPremiumFc"),
						cb.sum(m.get("endtPremium")).alias("endtPremium"),cb.max( m.get("currency")).alias("currency")
						
						);
			 
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(cb.max(m.get("endorsementDate"))));

				// Where
				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), request.getCompanyId());
				Predicate n3 = cb.equal(m.get("productId"), request.getProductId());
				Predicate n4 = cb.in(m.get("status")).value(Arrays.asList("E","P","D"));  // m.get("status").in("E","P"));
				Predicate n5 = cb.like(m.get("originalPolicyNo"), request.getPolicyNo());
				//Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
				//Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);

			/*	Predicate n7 = null;
				if (req.getApplicationId().equalsIgnoreCase("1")) {
					n7 = cb.equal(m.get("loginId"), req.getLoginId());
				} else {
					n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
				}*/

				/*Predicate n8 = null;
				if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
					Expression<String> e0 = m.get("brokerBranchCode");
					n8 = e0.in(branches);
				} else {
					Expression<String> e0 = m.get("branchCode");
					n8 = e0.in(branches);
				}*/

				query.where(n1, n2, n3, n4, n5)
						.groupBy(/*c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
								m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
								m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate")*/m.get("policyNo"))
						.orderBy(orderList);

				// Get Result
				TypedQuery<EndorsementCriteriaRes> result = em.createQuery(query);
				////result.setFirstResult(500);
				//result.setMaxResults(500);
				  List<EndorsementCriteriaRes> grids = result.getResultList();
				  
				  return grids;
			
		 }catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}
}
