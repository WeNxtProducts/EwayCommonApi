package com.maan.eway.endorsment.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.DocumentTransactionDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.ProductEmployeeDetails;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.req.ChangeEndoStatusReq;
import com.maan.eway.common.res.CommonCopyRes;
import com.maan.eway.common.res.EndorsementCriteriaRes;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.common.service.impl.MotorGridServiceImpl;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.ProductEmployeesDetailsRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.bean.CommonDataDetails;
@Service
public class CopyCommonRaw {

	@Autowired
	private EserviceCommonDetailsRepository eCommonRepo;
	
	@Autowired	 
	private MotorGridServiceImpl numberGenerate ;
	
	@Autowired
	private SectionDataDetailsRepository sectionDataRepo;
	
	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo;

	@Autowired
	private HomePositionMasterRepository homePosistionRepo;
	
	@Autowired
	private PersonalInfoRepository personalInforepo;

	@Autowired
	private ProductEmployeesDetailsRepository proEmplyeeRepo;
	
	@Autowired
	private CommonDataDetailsRepository commonDataRepo;
	
	@Autowired
	private DocumentTransactionDetailsRepository coverDocUploadDetails;
	private Logger log = LogManager.getLogger(CopyCommonRaw.class);


	@Autowired 
	private RatingFactorsUtil ratingutil;
	
	public EserviceCommonDetails copyCommonRaw(Endorsment request) {
		try {
			
			// Risk
			CommonCopyRes  riskRes =  copyCommonRiskTable(request);
			List<EserviceCommonDetails> commonData = eCommonRepo.findByRequestReferenceNo(riskRes.getRequestReferenceNo());
			commonData = commonData.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo()))).collect(Collectors.toList());
			//EserviceCommonDetails commonData = eCommonRepo.findByRequestReferenceNoOrderByRiskIdDesc(riskRes.getRequestReferenceNo() ); 
			EserviceCommonDetails commonData1=commonData.get(0);
			return commonData1 ;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
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
			
			EndtTypeMaster entMaster=ratingutil.getEndtMasterData(ent.getCompanyId(),ent.getProductId().toPlainString(),ent.getEndtType());
					//endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(ent.getCompanyId(), ent.getProductId().intValue(), "Y",Integer.parseInt(ent.getEndtType()), new Date(), new Date());
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
			
			List<EserviceCommonDetails> prevDatas = eCommonRepo.findByPolicyNo(prevPolicyNo);
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
				// Select
				query.multiselect(// cb.literal(Long.parseLong("1")).alias("idsCount"),
						// Customer Info
						cb.max(c.get("customerReferenceNo")).alias("customerReferenceNo"),
						cb.max(c.get("idNumber")).alias("idNumber"), cb.max(c.get("clientName")).alias("clientName"),
						// Vehicle Info
						cb.max(m.get("companyId")).alias("companyId"), cb.max(m.get("productId")).alias("productId"),
						cb.max(m.get("branchCode")).alias("branchCode"),
						cb.max(m.get("requestReferenceNo")).alias("requestReferenceNo"),
						cb.selectCase().when(cb.max(m.get("quoteNo")).isNotNull(), cb.max(m.get("quoteNo")))
								.otherwise(cb.max(m.get("quoteNo"))).alias("quoteNo"),
						cb.selectCase().when(cb.max(m.get("customerId")).isNotNull(), cb.max(m.get("customerId")))
								.otherwise(cb.max(m.get("customerId"))).alias("customerId"),
						cb.max(m.get("policyStartDate")).alias("policyStartDate"),
						cb.max(m.get("policyEndDate")).alias("policyEndDate"),
						cb.max(m.get("endorsementType")).alias("endorsementTypeId"),
						cb.max(m.get("endorsementTypeDesc")).alias("endorsementDesc"),
						cb.max(m.get("endtCategDesc")).alias("endorsementCategoryDesc"),
						cb.max(m.get("endorsementEffdate")).alias("effectiveDate"),
						cb.max(m.get("endtStatus")).alias("endorsementStatus"),
						cb.max(m.get("policyNo")).alias("policyNo"),
						cb.max(m.get("endorsementRemarks")).alias("endorsementRemarks"),
						cb.max(m.get("endorsementDate")).alias("endorsementDate"),
						// Home Position Master
						cb.sum(m.get("overallPremiumLc")).alias("overallPremiumLc"),
						cb.sum(m.get("overallPremiumFc")).alias("overallPremiumFc"),
						cb.sum(m.get("endtPremium")).alias("endtPremium"), cb.max(m.get("currency")).alias("currency")

				);

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(cb.max(m.get("endorsementDate"))));

				// Where
				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), request.getCompanyId());
				Predicate n3 = cb.equal(m.get("productId"), request.getProductId());
				// Predicate n4 = cb.in(m.get("status")).value(Arrays.asList("E","P","D")); //
				// m.get("status").in("E","P"));
				Predicate n5 = cb.or(cb.like(m.get("originalPolicyNo"), request.getPolicyNo()),
						cb.like(m.get("policyNo"), request.getPolicyNo()));
				// Predicate n6 = cb.equal(m.get("riskId"), "1");
				// Predicate n7 = cb.like(h.get("quoteNo"), m.get("quoteNo"));
				// Predicate n8 = cb.like(m.get("PolicyNo"), request.getPolicyNo());
				// Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
				// Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);

				/*
				 * Predicate n7 = null; if (req.getApplicationId().equalsIgnoreCase("1")) { n7 =
				 * cb.equal(m.get("loginId"), req.getLoginId()); } else { n7 =
				 * cb.equal(m.get("applicationId"), req.getApplicationId()); }
				 */

				/*
				 * Predicate n8 = null; if (req.getUserType().equalsIgnoreCase("Broker") ||
				 * req.getUserType().equalsIgnoreCase("User")) { Expression<String> e0 =
				 * m.get("brokerBranchCode"); n8 = e0.in(branches); } else { Expression<String>
				 * e0 = m.get("branchCode"); n8 = e0.in(branches); }
				 */

				query.where(n1, n2, n3, /* n4, */ n5)
						/*
						 * .groupBy(c.get("customerReferenceNo"), c.get("idNumber"),
						 * c.get("clientName"), m.get("companyId"), m.get("productId"),
						 * m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
						 * m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"))
						 */

						.groupBy(/* m.get("overallPremiumLc"),m.get("overallPremiumFc"), */m.get("policyNo"))
						.orderBy(orderList);

				// Get Result
				TypedQuery<EndorsementCriteriaRes> result = em.createQuery(query);
				//// result.setFirstResult(500);
				// result.setMaxResults(500);
				List<EndorsementCriteriaRes> grids = result.getResultList();
				return grids;
			
		 }catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}

	public EserviceCommonDetails commonRawEndtStatus(ChangeEndoStatusReq req) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		EserviceCommonDetails savedata = new EserviceCommonDetails();
		try {
			//Motor 
			List<EserviceCommonDetails> common = eCommonRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());

			if (common.size() > 0) {
				for (EserviceCommonDetails data : common) {
					savedata = dozerMapper.map(data, EserviceCommonDetails.class);
					savedata.setEndtStatus("C");
					eCommonRepo.saveAndFlush(savedata);
				}

			}
			// Update EndT Status
			homeEndtStatus(req);
			personolInfoEndtStatus(req);
			sectionDataDetails(req);
			eserviceSectionDetails(req);
			productEmployee(req);
			commonDataDetails(req);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;
	}
	private CommonDataDetails commonDataDetails(ChangeEndoStatusReq req) {
		CommonDataDetails savedata = new CommonDataDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<CommonDataDetails> commonData = commonDataRepo.findByQuoteNo(req.getQuoteNo());
			if (commonData != null&& commonData.size()>0) {
				savedata = dozerMapper.map(commonData, CommonDataDetails.class);
				savedata.setEndtStatus("C");
				commonDataRepo.saveAndFlush(savedata);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

	}
	private ProductEmployeeDetails productEmployee(ChangeEndoStatusReq req) {
		ProductEmployeeDetails savedata = new ProductEmployeeDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<ProductEmployeeDetails> proEmpList = proEmplyeeRepo.findByQuoteNo(req.getQuoteNo());
			if (proEmpList != null&& proEmpList.size()>0) {
				savedata = dozerMapper.map(proEmpList, ProductEmployeeDetails.class);
				savedata.setEndtStatus("C");
				proEmplyeeRepo.saveAndFlush(savedata);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

	}
	private SectionDataDetails sectionDataDetails(ChangeEndoStatusReq req) {
		SectionDataDetails savedata = new SectionDataDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<SectionDataDetails> motorData = sectionDataRepo.findByQuoteNoAndStatusNot(req.getQuoteNo(),"D");
			if (motorData.size() > 0) {
				for (SectionDataDetails data : motorData) {
					savedata = dozerMapper.map(data, SectionDataDetails.class);
					savedata.setEndtStatus("C");
				//	savedata.setStatus("P");
					sectionDataRepo.saveAndFlush(savedata);
				}
			}
		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

		
	}
	private EserviceSectionDetails eserviceSectionDetails(ChangeEndoStatusReq req) {
		EserviceSectionDetails savedata = new EserviceSectionDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<EserviceSectionDetails> motorData = eserSecRepo.findByQuoteNo(req.getQuoteNo());
			if (motorData.size() > 0) {
				for (EserviceSectionDetails data : motorData) {
					savedata = dozerMapper.map(data, EserviceSectionDetails.class);
					savedata.setEndtStatus("C");
				//	savedata.setStatus("P");
					eserSecRepo.saveAndFlush(savedata);
				}
			}
		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;
	}
		private DocumentTransactionDetails coverDocumentUploadDetailsEndtStatus(ChangeEndoStatusReq req) {
			DocumentTransactionDetails savedata = new DocumentTransactionDetails();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				List<DocumentTransactionDetails> motorData = coverDocUploadDetails.findByQuoteNo(req.getQuoteNo());
				if (motorData.size() > 0) {
					for (DocumentTransactionDetails data : motorData) {
						savedata = dozerMapper.map(data, DocumentTransactionDetails.class);
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
					savedata.setIntegrationStatus("S");
					savedata.setEffectiveDate(new Date());
					homePosistionRepo.saveAndFlush(savedata);
				}

			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return savedata;

		}

	
	
}
