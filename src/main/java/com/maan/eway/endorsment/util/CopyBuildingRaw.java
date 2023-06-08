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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.BuildingDetails;
import com.maan.eway.bean.ContentAndRisk;
import com.maan.eway.bean.CoverDocumentUploadDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.PersonalAccident;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.common.req.ChangeEndoStatusReq;
import com.maan.eway.common.res.BuildingCopyRes;
import com.maan.eway.common.res.EndorsementCriteriaRes;
import com.maan.eway.common.service.impl.MotorGridServiceImpl;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.notification.repository.CoverDocumentUploadDetailsRepository;
import com.maan.eway.repository.BuildingDetailsRepository;
import com.maan.eway.repository.ContentAndRiskRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PersonalAccidentRepository;
import com.maan.eway.repository.PersonalInfoRepository;

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
	private HomePositionMasterRepository homePosistionRepo;
	
	@Autowired
	private PersonalInfoRepository personalInforepo;
	
	@Autowired
	private BuildingDetailsRepository buildingRepo;
	
	@Autowired
	private EserviceCustomerDetailsRepository custRepo ;
	
	@Autowired
	private PersonalAccidentRepository paRepo;
	
	@Autowired
	private ContentAndRiskRepository contentAndRiskRepo;
	
	@Autowired
	private CoverDocumentUploadDetailsRepository coverDocUploadDetails;

	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo;

	private Logger log = LogManager.getLogger(MotorGridServiceImpl.class);
	
	public EserviceBuildingDetails copyBuildingRaw(Endorsment request) {
		try {
			
			
			// Risk Copy
			BuildingCopyRes  riskRes =  copyBuildingRiskTable(request);
			
			// Section Copy
			List<String> sectionIds = copyBuildingSections( riskRes ) ;
			riskRes.setSectionId(sectionIds);
			riskRes.setLocationId(riskRes.getLocationId());
			
			// Personal Accident Copy
			String res = copyPersonalAccident (riskRes.getRequestReferenceNo() ,	riskRes.getOldRequestReferenceNo() ,sectionIds ,  riskRes  ) ;
			
			EserviceBuildingDetails buildData = eBuildingRepo.findByRequestReferenceNoAndRiskId(riskRes.getRequestReferenceNo() , 1 ); 
			
			
			return buildData ;
			
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
					BuildingCopyRes res = dozerMapper.map(BuildingList.get(0) , BuildingCopyRes.class);
					
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
			if(pendingcount==0)
				newRequestNo=numberGenerate.generateRequestNo(ent.getCompanyId(), ent.getBranchCode(), String.valueOf(ent.getProductId()));
			
			EndtTypeMaster entMaster=endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(ent.getCompanyId(), ent.getProductId().intValue(), "Y",Integer.parseInt(ent.getEndtType()),new Date(), new Date());
			List<EserviceBuildingDetails> BuildingList=eBuildingRepo.findByQuoteNoOrderByRiskIdAsc(prevQuoteNo);
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
				newBuildingList.add(newObject);
			}
			eBuildingRepo.saveAllAndFlush(newBuildingList);
			
			
			BuildingCopyRes res = dozerMapper.map(newBuildingList.get(0) , BuildingCopyRes.class);
			
			List<EserviceBuildingDetails> prevDatas = eBuildingRepo.findByPolicyNoAndRiskId(prevPolicyNo , 1 );
			res.setOldRequestReferenceNo(prevDatas.get(0).getRequestReferenceNo() );
			res.setPolicyNo(ent.getPolicyNo()+"-"+count) ;
			 
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
			List<EserviceSectionDetails>  oldSecDatas = eserSecRepo.findByQuoteNoOrderByRiskIdAsc(buildingData.getEndtPrevQuoteNo()) ;
			
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
	
	public String  copyPersonalAccident(String newReqRefNo , String  oldReqRefNo , List<String> sectionIds ,  BuildingCopyRes buildingData ) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		String res = "" ;
		try {
			List<EserviceCommonDetails> oldAccData = eserCommonRepo.findByQuoteNoOrderByRiskIdAsc(buildingData.getEndtPrevQuoteNo()); 
			
			if( oldAccData !=null && oldAccData.size() > 0) {
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

					dozerMapper.map(oldAccData.get(0) , accdata);
					accdata.setRequestReferenceNo(newReqRefNo );
					accdata.setPolicyNo(buildingData.getPolicyNo());
					accdata.setQuoteNo(null);
					eserCommonRepo.save(accdata);
					res = "Saved Succefully" ;
				}
				res = "Not Data Available" ;
					
				
				}
			}
			
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
			//	Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
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
				Predicate n5 = cb.or(cb.like(m.get("originalPolicyNo"), request.getPolicyNo()),cb.like(m.get("policyNo"), request.getPolicyNo()));


			 
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

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;
	}
	
	private PersonalAccident personalAccident(ChangeEndoStatusReq req) {
		PersonalAccident savedata = new PersonalAccident();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<PersonalAccident> pa = paRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
			if (pa.size() > 0) {
				for (PersonalAccident data : pa) {
					savedata = dozerMapper.map(data, PersonalAccident.class);
					savedata.setEndtStatus("C");
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

	private BuildingDetails buildingDetailsEndtStatus(ChangeEndoStatusReq req) {
		BuildingDetails savedata = new BuildingDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<BuildingDetails> buildData = buildingRepo.findByQuoteNo(req.getQuoteNo());
			if (buildData.size() > 0) {
				for (BuildingDetails data : buildData) {
					savedata = dozerMapper.map(data, BuildingDetails.class);
					savedata.setEndtStatus("C");
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
				homePosistionRepo.saveAndFlush(savedata);
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
