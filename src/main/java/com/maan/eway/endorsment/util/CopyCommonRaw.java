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
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.common.res.CommonCopyRes;
import com.maan.eway.common.res.EndorsementCriteriaRes;
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
	
	
	public EserviceCommonDetails copyCommonRaw(Endorsment request) {
		try {
			
			// Risk
			CommonCopyRes  riskRes =  copyCommonRiskTable(request);
			
			EserviceCommonDetails commonData = eCommonRepo.findByRequestReferenceNoAndRiskId(riskRes.getRequestReferenceNo(), 1 ); 
			
			return commonData ;
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
			
			EndtTypeMaster entMaster=endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(ent.getCompanyId(), ent.getProductId().intValue(), "Y",Integer.parseInt(ent.getEndtType()), new Date(), new Date());
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
			
			List<EserviceCommonDetails> prevDatas = eCommonRepo.findByPolicyNoAndRiskId(prevPolicyNo , 1 );
			res.setOldRequestReferenceNo(prevDatas.get(0).getRequestReferenceNo() );
			
			return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@PersistenceContext
	private EntityManager em;

	public List<EndorsementCriteriaRes> endorsementCommonGrid(Endorsment request) {
		 try {

			 
				// Get Datas
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<EndorsementCriteriaRes> query = cb.createQuery(EndorsementCriteriaRes.class);

				// Find All
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
				Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				// Select
				query.multiselect(//cb.literal(Long.parseLong("1")).alias("idsCount"),
						// Customer Info
						c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
						c.get("clientName").alias("clientName"),
						// Vehicle Info
						m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
						m.get("branchCode").alias("branchCode"), m.get("requestReferenceNo").alias("requestReferenceNo"),
						cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise(m.get("quoteNo"))
								.alias("quoteNo"),
						cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId"))
								.otherwise(m.get("customerId")).alias("customerId"),
						m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
						m.get("endorsementType").alias("endorsementTypeId"),
						m.get("endorsementTypeDesc").alias("endorsementDesc"),
						m.get("endtCategDesc").alias("endorsementCategoryDesc"),
						m.get("endorsementEffdate").alias("effectiveDate"),
						m.get("endtStatus").alias("endorsementStatus"),
						m.get("policyNo").alias("policyNo"),
						m.get("endorsementRemarks").alias("endorsementRemarks"),
						//Home Position Master
						h.get("overallPremiumLc").alias("overallPremiumLc"), h.get("overallPremiumFc").alias("overallPremiumFc"),
						h.get("endtPremium").alias("endtPremium"), h.get("currency").alias("currency")
						
						);
			 
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("endorsementDate")));

				// Where
				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), request.getCompanyId());
				Predicate n3 = cb.equal(m.get("productId"), request.getProductId());
				Predicate n4 = cb.in(m.get("status")).value(Arrays.asList("E","P"));  // m.get("status").in("E","P"));
				Predicate n5 = cb.like(m.get("originalPolicyNo"), request.getPolicyNo());
				Predicate n7 = cb.like(h.get("quoteNo"), m.get("quoteNo"));
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

				query.where(n1, n2, n3, n4, n5,n7)
						/*.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
								m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
								m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"))*/
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
