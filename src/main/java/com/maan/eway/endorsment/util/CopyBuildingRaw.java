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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.BuildingDetails;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.ContentAndRisk;
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
import com.maan.eway.bean.ProductMaster;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.req.ChangeEndoStatusReq;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.BuildingCopyRes;
import com.maan.eway.common.res.EndorsementCriteriaRes;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.common.service.impl.MotorGridServiceImpl;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.endorsment.service.EndorsementService;
import com.maan.eway.repository.BuildingDetailsRepository;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.ContentAndRiskRepository;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.ProductEmployeesDetailsRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;

@Service
public class CopyBuildingRaw {

	@Autowired
	private CommonDataDetailsRepository commonDataRepo;
	@Autowired
	private ProductEmployeesDetailsRepository proEmplyeeRepo;
	@Autowired
	private EserviceBuildingDetailsRepository eBuildingRepo;
	@Autowired
	private BuildingRiskDetailsRepository buildRiskRepo;
	@Autowired
	private SectionDataDetailsRepository sectionDataRepo;
	@Autowired	 
	private MotorGridServiceImpl numberGenerate ;
	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;
	
	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo;

	@Autowired
	private HomePositionMasterRepository homePosistionRepo;
	
	@Autowired
	private PersonalInfoRepository personalInforepo;
	
	@Autowired
	private BuildingDetailsRepository buildingRepo;
	
	@Autowired
	private EserviceCustomerDetailsRepository custRepo ;
	
	@Autowired
	private ProductEmployeesDetailsRepository paRepo;
	
	@Autowired
	private ContentAndRiskRepository contentAndRiskRepo;
	
	@Autowired
	private DocumentTransactionDetailsRepository coverDocUploadDetails;

	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo;

	@Autowired 
	private RatingFactorsUtil ratingutil;
	
	@Autowired
	private GenerateSeqNoServiceImpl genSeqNoService ; 

	 

	private Logger log = LogManager.getLogger(MotorGridServiceImpl.class);
	
	public EserviceBuildingDetails copyBuildingRaw(Endorsment request) {
		try {
			
			
			// Risk Copy
			BuildingCopyRes  riskRes =  copyBuildingRiskTable(request);
			
			// Section Copy
			List<String> sectionIds = copyBuildingSections( riskRes ) ;
			riskRes.setSectionIds(sectionIds);
			riskRes.setLocationId(riskRes.getLocationId());
			
			// Personal Accident Copy
			String res = copyPersonalAccident (riskRes.getRequestReferenceNo() ,	riskRes.getOldRequestReferenceNo() ,sectionIds ,  riskRes ,request  ) ;
			
		//	EserviceBuildingDetails buildData = eBuildingRepo.findByRequestReferenceNoAndRiskId(riskRes.getRequestReferenceNo() , 1 );
			List<EserviceBuildingDetails> buildData = eBuildingRepo.findByRequestReferenceNo(riskRes.getRequestReferenceNo());
			buildData = buildData.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo()))).collect(Collectors.toList());
			EserviceBuildingDetails buildData1=buildData.get(0);
			return buildData1 ;
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	public BuildingCopyRes copyBuildingRiskTable(Endorsment ent) {
		try {
			List<EserviceBuildingDetails> BuildingDatas=null;
			Integer count=eBuildingRepo.countByOriginalPolicyNoAndRiskIdAndSectionId(ent.getPolicyNo(),1 ,"0");
			String prevPolicyNo=null;
			String prevQuoteNo=null;
			String newRequestNo =null;
			long pendingcount =0;
			// Response 
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
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
				if(BuildingList.stream().filter(m->(m.getEndtStatus().equals("P") && (Integer.parseInt(ent.getEndtType())==m.getEndorsementType()))).count()>0) {
					List<EserviceBuildingDetails> pendingList = BuildingList.stream().filter(m->(m.getEndtStatus().equals("P") && (Integer.parseInt(ent.getEndtType())==m.getEndorsementType()))).collect(Collectors.toList());
					BuildingCopyRes res = dozerMapper.map(pendingList.get(0) , BuildingCopyRes.class);
					
					//List<EserviceBuildingDetails> prevDatas = eBuildingRepo.findByPolicyNoAndRiskId(prevPolicyNo , 1 );
					//res.setOldRequestReferenceNo(prevDatas.get(0).getRequestReferenceNo() );
					//res.setPolicyNo(ent.getPolicyNo()+"-"+count) ;
					return res;
				}
				if(pendingcount>0) {
					 List<EserviceBuildingDetails> pendingData = BuildingList.stream().filter(m->m.getEndtStatus().equals("P")).collect(Collectors.toList());
					 BuildingDatas= pendingData;
					 prevPolicyNo=BuildingDatas.get(0).getEndtPrevPolicyNo();
					 prevQuoteNo=BuildingDatas.get(0).getEndtPrevQuoteNo();
					 newRequestNo=BuildingDatas.get(0).getRequestReferenceNo();
					 String prevRequestRefNo=BuildingDatas.get(0).getRequestReferenceNo();
					 List<EserviceBuildingDetails> rows = eBuildingRepo.findByRequestReferenceNoAndProductId(prevRequestRefNo,ent.getProductId().toPlainString());
					 eBuildingRepo.deleteAllInBatch(rows);
					 eBuildingRepo.flush();
					 count--;
				}else {
					BuildingDatas=BuildingList;
					
					if(BuildingList.size()>1) {
						prevPolicyNo=BuildingList.get(0).getPolicyNo();
						prevQuoteNo =BuildingList.get(0).getQuoteNo();
					}else {
						prevPolicyNo=BuildingList.get(0).getPolicyNo();
						prevQuoteNo =BuildingDatas.get(0).getQuoteNo();
					}
				}
				
			}else {
				BuildingDatas=eBuildingRepo.findByPolicyNoAndStatus(ent.getPolicyNo(),"P");
				prevPolicyNo=ent.getPolicyNo();
				prevQuoteNo =BuildingDatas.get(0).getQuoteNo();
			}
			if(pendingcount==0) {
				//newRequestNo=numberGenerate.generateRequestNo(ent.getCompanyId(), ent.getBranchCode(), String.valueOf(ent.getProductId()));
				// Generate Seq
	 			SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
	 		 	generateSeqReq.setInsuranceId(ent.getCompanyId());  
	 		 	generateSeqReq.setProductId(ent.getProductId().toString());
	 		 	generateSeqReq.setType("2");
	 		 	generateSeqReq.setTypeDesc("REQUEST_REFERENCE_NO");
	 		 	newRequestNo =  genSeqNoService.generateSeqCall(generateSeqReq);
			}
				
			
			EndtTypeMaster entMaster=ratingutil.getEndtMasterData(ent.getCompanyId(),ent.getProductId().toPlainString(),ent.getEndtType());
					/*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(ent.getCompanyId(), ent.getProductId().intValue(), "Y",Integer.parseInt(ent.getEndtType()),new Date(), new Date());*/
			List<EserviceBuildingDetails> BuildingList=eBuildingRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(prevQuoteNo,"D");
			List<EserviceBuildingDetails> newBuildingList=new ArrayList<EserviceBuildingDetails>();
			++count;
			for(EserviceBuildingDetails m :BuildingList) {
				 
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
				newObject.setApplicationId(ent.getApplicationId());
				if(ent.getLoginId()==null || StringUtils.isBlank(ent.getLoginId())) {
					newObject.setLoginId(m.getLoginId());
				}else {
					newObject.setLoginId(ent.getLoginId());
				}
				// Source Type Condtion
				String sourceType = newObject.getSourceType() ;
				if(StringUtils.isNotBlank(newObject.getApplicationId()) && ! "1".equalsIgnoreCase(newObject.getApplicationId()) ) {
					List<ListItemValue> sourcerTypes = genSeqNoService.getSourceTypeDropdown(newObject.getCompanyId() , newObject.getBranchCode() ,"SOURCE_TYPE"); 
					List<ListItemValue> acitveSourcerTypes = sourcerTypes.stream().filter( o -> "Y".equalsIgnoreCase(o.getStatus()) 
							&& o.getItemValue().contains(sourceType) ).collect(Collectors.toList()); 							
					newObject.setSourceType(acitveSourcerTypes.size() > 0 ? acitveSourcerTypes.get(0).getItemValue()	: 	newObject.getSourceType());			
					newObject.setSourceTypeId(acitveSourcerTypes.size() > 0 ? acitveSourcerTypes.get(0).getItemCode()	: 	newObject.getSourceTypeId());
				}
				newObject.setSubUserType(ent.getSubUserType());
				newBuildingList.add(newObject);
			}
			eBuildingRepo.saveAllAndFlush(newBuildingList);
			
			
			BuildingCopyRes res = dozerMapper.map(newBuildingList.get(0) , BuildingCopyRes.class);
			
			//List<EserviceBuildingDetails> prevDatas = eBuildingRepo.findByPolicyNoAndRiskId(prevPolicyNo , 1 );
			List<EserviceBuildingDetails> prevDatas = eBuildingRepo.findByPolicyNo(prevPolicyNo);
			res.setOldRequestReferenceNo(prevDatas.get(0).getRequestReferenceNo() );
			res.setPolicyNo(ent.getPolicyNo()+"-"+count) ;
			res.setEndtPrevPolicyNo(prevDatas.get(0).getPolicyNo());
			res.setEndtCount(new BigDecimal(count));
			res.setOriginalPolicyNo(newBuildingList.get(0).getOriginalPolicyNo());
			res.setEndtStatus(newBuildingList.get(0).getEndtStatus());
			res.setIsFinanceYn(newBuildingList.get(0).getIsFinaceYn());
			res.setEndtCategoryDesc(newBuildingList.get(0).getEndtCategDesc());
			res.setEndTypeDesc(newBuildingList.get(0).getEndorsementTypeDesc());
			res.setApplicationId(newBuildingList.get(0).getApplicationId());
			res.setLoginId(newBuildingList.get(0).getLoginId());
			res.setSubUserType(newBuildingList.get(0).getSubUserType());
			return res;
		}catch(ObjectOptimisticLockingFailureException ex ) {
			return copyBuildingRiskTable(ent);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<String> copyBuildingSections( BuildingCopyRes buildingData ) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			String newReqRefNo=buildingData.getRequestReferenceNo() ;
		//	String  oldReqRefNo=buildingData.getOldRequestReferenceNo() ;
			List<EserviceSectionDetails>  oldSecDatas = eserSecRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(buildingData.getEndtPrevQuoteNo(),"D") ;
			
			// Building Section Insert
			Long buildSecCount = eserSecRepo.countByRequestReferenceNoAndRiskId(newReqRefNo, 1);
			if (buildSecCount > 0) {
				eserSecRepo.deleteByRequestReferenceNoAndRiskId(newReqRefNo, 1);
			}

			List<String> secList = new ArrayList<String>(); 
			for (EserviceSectionDetails section : oldSecDatas) {
				EserviceSectionDetails secData = new EserviceSectionDetails();
			
				dozerMapper.map(section, secData);
				secData.setRequestReferenceNo(newReqRefNo);
				secData.setUserOpt("N");
				secData.setPolicyNo(buildingData.getPolicyNo());
				secData.setQuoteNo(null);
				eserSecRepo.saveAndFlush(secData);
				secList.add(secData.getSectionId());
			}
			
			return secList;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String  copyPersonalAccident(String newReqRefNo , String  oldReqRefNo , List<String> sectionIds ,  BuildingCopyRes buildingData ,Endorsment request ) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		String res = "" ;
		try {
			List<EserviceCommonDetails> endtData  = eserCommonRepo.findByRequestReferenceNo(newReqRefNo);
			boolean newInsert = false ; 
			
			if(endtData.size() > 0 ) {
				EserviceCommonDetails data = endtData.get(0)   ;
				Integer endtType  = data.getEndorsementType() == null ? 0 : data.getEndorsementType() ;
				if( endtType.equals(Integer.valueOf(request.getEndtType())) ) {
					newInsert = false ;
				} else {
					newInsert = true ;
					eserCommonRepo.deleteAll(endtData)	;
				}
			} else {
				newInsert = true ;
			}
			
			if(newInsert == true) {
				List<EserviceCommonDetails> oldAccData = eserCommonRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(buildingData.getEndtPrevQuoteNo() , "D");
				if(oldAccData.size() >0) {
					if( oldAccData !=null && oldAccData.size() > 0) {
						List<EserviceCommonDetails> humanList = new ArrayList<EserviceCommonDetails>();
						for (EserviceCommonDetails old : oldAccData) {
							EserviceCommonDetails accdata = new EserviceCommonDetails();
							
								dozerMapper.map(old , accdata);
								accdata.setRequestReferenceNo(newReqRefNo);
								accdata.setOriginalPolicyNo(buildingData.getOriginalPolicyNo());
								accdata.setEndorsementDate(new Date());
								accdata.setEndorsementRemarks(request.getEndtRemarks());
								accdata.setEndorsementEffdate(request.getEndtEffectiveDate());
								accdata.setEndtPrevPolicyNo(buildingData.getEndtPrevPolicyNo());
								accdata.setEndtPrevQuoteNo(buildingData.getEndtPrevQuoteNo());
								accdata.setEndtCount(buildingData.getEndtCount());
								accdata.setEndtStatus(buildingData.getEndtStatus());
								accdata.setIsFinaceYn(buildingData.getIsFinanceYn());
								accdata.setEndtCategDesc(buildingData.getEndtCategoryDesc());
								accdata.setEndorsementType(Integer.parseInt(request.getEndtType()));
								accdata.setEndorsementTypeDesc(buildingData.getEndTypeDesc());
								accdata.setStatus("E");
								accdata.setPolicyNo(buildingData.getPolicyNo());
								accdata.setQuoteNo(null);
								accdata.setApplicationId(request.getApplicationId());
								accdata.setLoginId(request.getLoginId()==null?buildingData.getLoginId():(request.getLoginId()));
								accdata.setSubUserType(request.getSubUserType());
								humanList.add(accdata);							
						}
							eserCommonRepo.saveAll(humanList);
						
					}
				}
			}
			res = "Saved Succefully" ;
			
			return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@PersistenceContext
	private EntityManager em;

	public List<EndorsementCriteriaRes> endorsementBuildingGrid(Endorsment request) {

		 try {

			 
				// Get Datas
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<EndorsementCriteriaRes> query = cb.createQuery(EndorsementCriteriaRes.class);

				// Find All
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
				
				Subquery<Long> endtPre = query.subquery(Long.class);
				Root<HomePositionMaster> h = endtPre.from(HomePositionMaster.class);
				endtPre.select(cb.sum(h.get("endtPremium")) ) ;
				Predicate pm1 = cb.equal(h.get("companyId"), m.get("companyId"));
				Predicate pm2 = cb.equal(h.get("productId"), m.get("productId"));
				Predicate pm3   = cb.like(h.get("policyNo"), m.get("policyNo"));
				endtPre.where(pm1,pm2,pm3);
		
				// Over All Premium Fc
				Subquery<Long> overAllPremiumFc = query.subquery(Long.class);
				Root<HomePositionMaster> ocpm1 = overAllPremiumFc.from(HomePositionMaster.class);
				overAllPremiumFc.select(cb.sum(ocpm1.get("overallPremiumFc")));
				Predicate a1 = cb.equal(m.get("quoteNo"),ocpm1.get("quoteNo") );
				overAllPremiumFc.where(a1);
				
				// Over All Premium Lc
				Subquery<Long> overAllPremiumLc = query.subquery(Long.class);
				Root<HomePositionMaster> ocpm2 = overAllPremiumLc.from(HomePositionMaster.class);
				overAllPremiumLc.select(cb.sum(ocpm2.get("overallPremiumLc")));
				Predicate a2 = cb.equal(m.get("quoteNo"),ocpm2.get("quoteNo") );
				overAllPremiumLc.where(a2);
				
				Subquery<Long> debitNoteNo = query.subquery(Long.class);
				Root<HomePositionMaster> h2 = debitNoteNo.from(HomePositionMaster.class);
				debitNoteNo.select(cb.max(h2.get("debitNoteNo"))) ;
				Predicate pm4 = cb.equal(h2.get("companyId"), m.get("companyId"));
				Predicate pm5 = cb.equal(h2.get("productId"), m.get("productId"));
				Predicate pm6   = cb.equal(h2.get("policyNo"), m.get("policyNo"));
				debitNoteNo.where(pm4,pm5,pm6);
				
				Subquery<Long> creditNo = query.subquery(Long.class);
				Root<HomePositionMaster> h3 = creditNo.from(HomePositionMaster.class);
				creditNo.select(cb.max(h3.get("creditNo"))) ;
				Predicate pm7 = cb.equal(h3.get("companyId"), m.get("companyId"));
				Predicate pm8 = cb.equal(h3.get("productId"), m.get("productId"));
				Predicate pm9   = cb.equal(h3.get("policyNo"), m.get("policyNo"));
				creditNo.where(pm7,pm8,pm9);
				
				Subquery<Long> endtPreTax = query.subquery(Long.class);
				Root<HomePositionMaster> h4 = endtPreTax.from(HomePositionMaster.class);
				endtPreTax.select(cb.sum(h4.get("endtPremiumTax") ) ) ;
				Predicate pm10 = cb.equal(h4.get("companyId"), m.get("companyId"));
				Predicate pm11 = cb.equal(h4.get("productId"), m.get("productId"));
				Predicate pm12   = cb.equal(h4.get("policyNo"), m.get("policyNo"));
				endtPreTax.where(pm10,pm11,pm12);
		
				
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
						cb.sum(overAllPremiumLc).alias("overallPremiumLc"), cb.sum(overAllPremiumFc).alias("overallPremiumFc"),
						cb.sum(endtPre,endtPreTax).alias("endtPremium"),cb.max( m.get("currency")).alias("currency"),
						debitNoteNo.alias("debitNoteNo") ,creditNo.alias("creditNo")
						
						);
			 
				// Order By
//				List<Order> orderList = new ArrayList<Order>();
//				orderList.add(cb.desc(cb.max(m.get("endorsementDate"))));
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc((m.get("policyNo"))));

			
				// Where
				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), request.getCompanyId());
				Predicate n3 = cb.equal(m.get("productId"), request.getProductId());
			//	 Predicate n4 = cb.notEqual(m.get("status"),"D");
				//Predicate n4 = cb.in(m.get("status")).value(Arrays.asList("E","P","D"));  
				// m.get("status").in("E","P"));
				Predicate n5 = cb.or(cb.like(m.get("originalPolicyNo"), request.getPolicyNo()),cb.like(m.get("policyNo"), request.getPolicyNo()));
				//Predicate n6 = cb.equal(h.get("quoteNo"), m.get("quoteNo"));
				Predicate n6 = cb.equal(m.get("sectionId"),"0");

				query.where(n1, n2, n3, n5,n6)
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

	public EserviceBuildingDetails buildingRawEndtStatus(ChangeEndoStatusReq req) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		EserviceBuildingDetails savedata = new EserviceBuildingDetails();
		try {
			//Motor 
			List<EserviceBuildingDetails> build = eBuildingRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());

			if (build.size() > 0) {
				for (EserviceBuildingDetails data : build) {
					savedata = dozerMapper.map(data, EserviceBuildingDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					eBuildingRepo.saveAndFlush(savedata);
				}

			}
			// Update EndT Status
			homeEndtStatus(req);
			personolInfoEndtStatus(req);
			contentAndRiskEndtStatus(req);
			buildingDetailsEndtStatus(req);
			coverDocumentUploadDetailsEndtStatus(req);
			//eserviceCustDetailsChangeStatus(req);
			personalAccident(req);
			eserviceCommon(req);
			sectionDataDetails(req);
			eserviceSectionDetails(req);
			//productEmployee(req);
			commonDataDetails(req);
			buildingRiskDetailsEndtStatus(req);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;
	}
	
	private BuildingRiskDetails buildingRiskDetailsEndtStatus(ChangeEndoStatusReq req) {
		BuildingRiskDetails savedata=null;
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<BuildingRiskDetails> commonData = buildRiskRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
			
			if (commonData != null&& commonData.size()>0) {
				for(BuildingRiskDetails data: commonData ) {
					 savedata = new BuildingRiskDetails();
					savedata = dozerMapper.map(data, BuildingRiskDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					buildRiskRepo.saveAndFlush(savedata);
				}
				
			}

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
				for(CommonDataDetails data:commonData) {
				savedata = dozerMapper.map(data, CommonDataDetails.class);
				savedata.setEndtStatus("C");
				savedata.setStatus("P");
				commonDataRepo.saveAndFlush(savedata);
				}
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
					savedata.setStatus("P");
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
					savedata.setStatus("P");
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
	private ProductEmployeeDetails personalAccident(ChangeEndoStatusReq req) {
		ProductEmployeeDetails savedata = new ProductEmployeeDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<ProductEmployeeDetails> pa = paRepo.findByQuoteNo(req.getQuoteNo());
			if (pa.size() > 0) {
				for (ProductEmployeeDetails data : pa) {
					savedata = dozerMapper.map(data, ProductEmployeeDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					paRepo.saveAndFlush(savedata);
				}
			}
		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

		
	}

	private EserviceCommonDetails eserviceCommon(ChangeEndoStatusReq req) {
		EserviceCommonDetails savedata = new EserviceCommonDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<EserviceCommonDetails> common = eserCommonRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
			if (common.size() > 0) {
				for (EserviceCommonDetails data : common) {
					savedata = dozerMapper.map(data, EserviceCommonDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					eserCommonRepo.saveAndFlush(savedata);
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
					savedata.setStatus("P");
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

	private BuildingDetails buildingDetailsEndtStatus(ChangeEndoStatusReq req) {
		BuildingDetails savedata = new BuildingDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<BuildingDetails> buildData = buildingRepo.findByQuoteNo(req.getQuoteNo());
			if (buildData.size() > 0) {
				for (BuildingDetails data : buildData) {
					savedata = dozerMapper.map(data, BuildingDetails.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					buildingRepo.saveAndFlush(savedata);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

		
	}

	private ContentAndRisk contentAndRiskEndtStatus(ChangeEndoStatusReq req) {
		ContentAndRisk savedata = new ContentAndRisk();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<ContentAndRisk> content=contentAndRiskRepo.findByQuoteNo(req.getQuoteNo());
			if (content.size() > 0) {
				for (ContentAndRisk data : content) {
					savedata = dozerMapper.map(data, ContentAndRisk.class);
					savedata.setEndtStatus("C");
					savedata.setStatus("P");
					contentAndRiskRepo.saveAndFlush(savedata);
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
			savedata.setStatus("P");
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
				savedata.setStatus("P");
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
	private ProductEmployeeDetails productEmployee(ChangeEndoStatusReq req) {
		ProductEmployeeDetails savedata = new ProductEmployeeDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<ProductEmployeeDetails> proEmpList = proEmplyeeRepo.findByQuoteNo(req.getQuoteNo());
			if (proEmpList != null&& proEmpList.size()>0) {
				savedata = dozerMapper.map(proEmpList, ProductEmployeeDetails.class);
				savedata.setEndtStatus("C");
				savedata.setStatus("P");
				proEmplyeeRepo.saveAndFlush(savedata);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;

	}
	/*private EserviceCustomerDetails eserviceCustDetailsChangeStatus(ChangeEndoStatusReq req) {
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

		
	}*/


}
