package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.CurrencyMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.EserviceTravelGroupDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MasterReferralDetails;
import com.maan.eway.bean.SeqCustid;
import com.maan.eway.bean.SeqQuoteno;
import com.maan.eway.bean.UwQuestionsDetails;
import com.maan.eway.common.req.CoverIdsReq;
import com.maan.eway.common.req.IndividualReferalReq;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.req.QuoteThreadReq;
import com.maan.eway.common.req.VehicleIdsReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.NewQuoteRes;
import com.maan.eway.common.res.ProductThreadRes;
import com.maan.eway.common.res.QuoteThreadRes;
import com.maan.eway.common.service.QuoteThreadService;
import com.maan.eway.error.Error;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.CoverDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EservicePersonalAccidentDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.EserviceTravelGroupDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.MasterReferralDetailsRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PersonalAccidentRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.SeqCustidRepository;
import com.maan.eway.repository.SeqCustrefnoRepository;
import com.maan.eway.repository.SeqQuotenoRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.repository.TravelPassengerHistoryRepository;
import com.maan.eway.repository.UwQuestionsDetailsRepository;
import com.maan.eway.res.ReferalResponse;
import com.maan.eway.res.referal.MasterReferal;
import com.maan.eway.thread.MyTaskList;

@Service
@Transactional
public class QuoteThreadServiceImpl implements QuoteThreadService {

	
	private Logger log = LogManager.getLogger(QuoteThreadServiceImpl.class);
	

	@Value(value = "${motor.productId}")
	private String motorProductId;
	
	@Value(value = "${travel.productId}")
	private String travelProductId;
	
	@Value(value = "${building.productId}")
	private String buildingProductId;
	
	@Value(value = "${personalaccident.productId}")
	private String personalAccidentProductId;
	
	@Value(value = "${workmencompensation.productId}")
	private String workmenCompensationProductId;
	
	@Value(value = "${employeesliability.productId}")
	private String employeesliabilityProductId;

	Gson json = new Gson();
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private EserviceCustomerDetailsRepository eserCustRepo ;
	
	@Autowired
	private EServiceMotorDetailsRepository eserMotRepo ;
	
	@Autowired
	private EserviceTravelDetailsRepository eserTraRepo ;
	
	@Autowired
	private FactorRateRequestDetailsRepository facRateRepo ;
	
	@Autowired
	private SeqCustidRepository custIdRepo ;
	
	@Autowired
	private SeqQuotenoRepository quoteNoRepo ;
	
	@Autowired
	private PersonalInfoRepository perInfoRepo ;
	
	@Autowired
	private MotorDataDetailsRepository motorRepo ;
	
	@Autowired
	private CoverDetailsRepository coverRepo ;
	
	@Autowired
	private HomePositionMasterRepository homeRepo ;
	
	@Autowired
	private LoginMasterRepository loginRepo ;
	
	@Autowired
	private UwQuestionsDetailsRepository uwRepo ;
	
	@Autowired
	private EserviceTravelDetailsRepository eserRepo ;
	
	@Autowired
	private EserviceTravelGroupDetailsRepository eserGroupRepo ;
	
	@Autowired
	private TravelPassengerDetailsRepository traPassRepo  ;
	
	@Autowired
	private TravelPassengerHistoryRepository traPassHisRepo  ;
	
	@Autowired
	private EserviceBuildingDetailsRepository eserBuildRepo  ;
	
	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo  ;
	
	@Autowired
	private MasterReferralDetailsRepository masReferralRepo ;
	
	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo;
	
	@Autowired
	private CommonDataDetailsRepository commonDataRepo;
	
	@Override
	public CommonRes call_OT_Insert(NewQuoteReq req) {
		CommonRes commonRes = new CommonRes();
		NewQuoteRes response = new NewQuoteRes();
		List<Error> errors = new ArrayList<Error>();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhmmssSS");
		try {
		boolean referal = false ;
		if( StringUtils.isBlank(req.getAdminLoginId())) {
			
			// Refral Checking Method
			commonRes = RefferalChecking(req);
			ReferalResponse res = (ReferalResponse) commonRes.getCommonResponse();
			if(res!=null && StringUtils.isNotBlank( res.getReferral())  && res.getReferral().equalsIgnoreCase("true") ) {
				referal = true ;	
			}	
		}
		
		// Referal Returning Block
		if( referal == true || (commonRes.getIsError()!=null && commonRes.getIsError()==true) ) {
			return  commonRes ;
	
		} else {
			// Thread Call Setup
			List<Callable<Object>> queue = new ArrayList<Callable<Object>>();
			
			MyTaskList taskList = new MyTaskList(queue);
			
			CommonRes frameQuoteReq = setQuoteThreadReq(req );
            if( frameQuoteReq.getErrorMessage() !=null && frameQuoteReq.getErrorMessage().size()>0 ) {
            	commonRes = frameQuoteReq ;
            	return commonRes ; 
            }
            
            // Frame Request 
            QuoteThreadReq request = (QuoteThreadReq) frameQuoteReq.getCommonResponse() ;
           
            // Delete Same Quote Old Records
            commonRes =  oldQuoteRecordsDeleteThreadCall(request);
            if( commonRes.getErrorMessage() !=null && commonRes.getErrorMessage().size()>0 ) {
             	return commonRes ; 
             }
            
            // Customer Save Thread Call
            QuoteThreadCall customerSave = new QuoteThreadCall("CustomerSave" , request , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo 
            		, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,motorProductId , travelProductId,buildingProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo);
            queue.add(customerSave);
            
            int threadCount = 1 ;
            int success = 0;
            
         // Product Wise Thread Call
            commonRes = productWiseThreadCall( req , request ) ;
        	 if( commonRes.getErrorMessage() !=null && commonRes.getErrorMessage().size()>0 ) {
             	commonRes = frameQuoteReq ;
             	return commonRes ; 
             }
            
        	 ProductThreadRes productThreads = (ProductThreadRes) commonRes.getCommonResponse();
        	 threadCount = threadCount + productThreads.getThreadCount();
        	 queue.addAll(productThreads.getQueue());
        	 ForkJoinPool forkjoin = new ForkJoinPool(threadCount); 
             ConcurrentLinkedQueue<Future<Object>> invoke  = (ConcurrentLinkedQueue<Future<Object>>) forkjoin.invoke(taskList) ;
             
        	 Map<String,Object> custRes = new HashMap<String,Object>() ;
			List<Map<String,Object>> motRes =  new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> covRes =  new ArrayList<Map<String,Object>>() ;
			List<Map<String,Object>> traRes =  new ArrayList<Map<String,Object>>();
			
			for (Future<Object> callable : invoke) {

				log.info(callable.getClass() + "," + callable.isDone());

				if (callable.isDone()) {
					Map<String, Object> map = (Map<String, Object>) callable.get();

					for (Entry<String, Object> future : map.entrySet()) {
						
						if ("CustomerSave".equalsIgnoreCase(future.getKey())) {
							custRes = (Map<String,Object>) future.getValue();
						} else if ("MotorSave".equalsIgnoreCase(future.getKey())) {
							motRes.add((Map<String,Object>) future.getValue());
						} else if ("CoverSave".equalsIgnoreCase(future.getKey())) {
							covRes.add((Map<String,Object>) future.getValue());
						} else if ("TravelSave".equalsIgnoreCase(future.getKey())) {
							traRes.add((Map<String,Object>) future.getValue());
						}
					}

					success++;
				}
			}
	
			// Cust Res
			if( custRes.get("Response")!=null && custRes.get("Response").toString().equals("Failed") ) {
				errors.add(new Error("01","Customer Save",custRes.get("Errors").toString()));
				commonRes.setCommonResponse(null);
				commonRes.setIsError(true);
				commonRes.setErrorMessage(errors);
				commonRes.setMessage("Failed");
				return commonRes ; 
				
			} else {
				
				// Motor Res
				for (Map<String,Object> mot : motRes) {
					if( mot.get("Response")!=null && mot.get("Response").toString().equals("Failed") ) {
						errors.add(new Error("01","Motor Save",mot.get("Errors").toString()));
						commonRes.setCommonResponse(null);
						commonRes.setIsError(true);
						commonRes.setErrorMessage(errors);
						commonRes.setMessage("Failed");
						return commonRes ; 
					}
				}
				
				// Travel Res
				for (Map<String,Object> tra : traRes) {
					if( tra.get("Response")!=null && tra.get("Response").toString().equals("Failed") ) {
						errors.add(new Error("01","Travel Save",tra.get("Errors").toString()));
						commonRes.setCommonResponse(null);
						commonRes.setIsError(true);
						commonRes.setErrorMessage(errors);
						commonRes.setMessage("Failed");
						return commonRes ; 
					}
				}
				
				// Cover Res
				for (Map<String,Object> cov : covRes) {
					if( cov.get("Response")!=null && cov.get("Response").toString().equals("Failed") ) {
						errors.add(new Error("01","Cover Save",cov.get("Errors").toString()));
						commonRes.setCommonResponse(null);
						commonRes.setIsError(true);
						commonRes.setErrorMessage(errors);
						commonRes.setMessage("Failed");
						return commonRes ; 
					}
				}
			}
			
			// Response 
			if ( errors !=null && errors.size()>0 ) {
				commonRes.setCommonResponse(null);
				commonRes.setIsError(true);
				commonRes.setErrorMessage(errors);
				commonRes.setMessage("Failed");
				
			} else {
				
				//Quote Save Thread Call
				List<Callable<Object>> queue2 = new ArrayList<Callable<Object>>();
				MyTaskList taskList2 = new MyTaskList(queue2);
				request.setVehicleId(req.getVehicleIdsList().get(0).getVehicleId());
				QuoteThreadCall quoteSave = new QuoteThreadCall("QuoteSave" , request , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo  
	            		, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo,motorProductId , travelProductId,buildingProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo);
	            
				queue2.add(quoteSave);
				
				 ForkJoinPool forkjoin2 = new ForkJoinPool(threadCount);
				ConcurrentLinkedQueue<Future<Object>> invoke2 = (ConcurrentLinkedQueue<Future<Object>>) forkjoin2.invoke(taskList2);
				
				QuoteThreadRes quoteRes = new QuoteThreadRes(); 
				
				for (Future<Object> callable : invoke2) {
	
					log.info(callable.getClass() + "," + callable.isDone());
	
					if (callable.isDone()) {
						Map<String, Object> map = (Map<String, Object>) callable.get();
	
						for (Entry<String, Object> future : map.entrySet()) {
							if ("QuoteSave".equalsIgnoreCase(future.getKey())) {
	
								quoteRes = (QuoteThreadRes) future.getValue();
	
							} 
						}
						success++;
					}
				}

//				
//				
//				QuoteThreadRes quoteRes = call_QuoteSave(request);

				
				response.setQuoteNo(quoteRes.getQuoteNo());
				response.setRequestReferenceNo(quoteRes.getRequestReferenceNo());
				response.setCustomerId(quoteRes.getCustomerId());
				response.setResponse("Saved SuccessFully");
				 
				// Response 
				if ( errors !=null && errors.size()>0 ) {
					commonRes.setCommonResponse(null);
					commonRes.setIsError(true);
					commonRes.setErrorMessage(errors);
					commonRes.setMessage("Failed");
					
				} else {
					commonRes.setCommonResponse(response);
					commonRes.setIsError(false);
					commonRes.setErrorMessage(Collections.emptyList());
					commonRes.setMessage("Success");
				}
			}

			
		}	
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " +  e.getMessage());
			errors.add(new Error("01","Common Error",e.getMessage()));
			commonRes.setCommonResponse(null);
			commonRes.setIsError(true);
			commonRes.setErrorMessage(errors);
			commonRes.setMessage("Failed");	
		}
		return commonRes ;
	}
	
	//-------------------------------------------------------------Refrral Checking Block ---------------------------------------------------------------------//
	@Transactional
	public CommonRes RefferalChecking(NewQuoteReq req ) {
		CommonRes commonRes = new CommonRes();
		List<Error> errors = new ArrayList<Error>();
		try {
			List<MasterReferralDetails> findMasterRefrals = masReferralRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
			List<IndividualReferalReq>  induRefs = new ArrayList<IndividualReferalReq>(); 
			
			boolean referral = false ;
			String referralRemarks = "" ;
			
			if(StringUtils.isNotBlank(req.getManualReferralYn()) && req.getManualReferralYn().equalsIgnoreCase("Y") ) {
				referral = true ;
				referralRemarks = req.getReferralRemarks();
				
			}
			
			
			// OTher Referals
			{
				List<FactorRateRequestDetails> userOptCovers = new ArrayList<FactorRateRequestDetails>();
				
				//UPDATE
				CriteriaBuilder cb = em.getCriteriaBuilder();
				// create update
				CriteriaUpdate<FactorRateRequestDetails> update = cb.createCriteriaUpdate(FactorRateRequestDetails.class);
				// set the root class
				Root<FactorRateRequestDetails> m = update.from(FactorRateRequestDetails.class);
				// set update and where clause
				update.set("userOpt", "N");
				
				Predicate n3 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo() );
				Predicate n4 = cb.equal(m.get("productId"),req.getProductId());
				update.where(n3,n4);
				// perform update
				em.createQuery(update).executeUpdate();
				// Covers Referrral Checking
				List<FactorRateRequestDetails> covers = facRateRepo.findByRequestReferenceNoOrderByVehicleIdAsc(req.getRequestReferenceNo()); 
				for (VehicleIdsReq veh : req.getVehicleIdsList()) {
					
					String referrals = "" ;
					// Cover Referal Checking
					List<CoverIdsReq> coverList = veh.getCoverIdList();
					for (CoverIdsReq cov : coverList) {
						
						if(StringUtils.isBlank(cov.getSubCoverYn()) || cov.getSubCoverYn().equalsIgnoreCase("N") ) {
							List<FactorRateRequestDetails> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(veh.getVehicleId()) && o.getSectionId().equals(Integer.valueOf(veh.getSectionId())) &&  o.getCoverId().equals(cov.getCoverId()) ).collect(Collectors.toList());	
							userOptCovers.addAll(filterCovers);
							
							List<FactorRateRequestDetails> filterReferalCovers = filterCovers.stream().filter( o ->  o.getVehicleId().equals(veh.getVehicleId()) && o.getSectionId().equals(Integer.valueOf(veh.getSectionId())) && o.getCoverId().equals(cov.getCoverId()) &&  o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0) &&  cov.getIsReferal()!=null && cov.getIsReferal().equalsIgnoreCase("Y") ).collect(Collectors.toList());
							if(filterReferalCovers.size()>0 ) { 
							
								referrals = StringUtils.isBlank(referrals)? filterReferalCovers.get(0).getCoverName() : referrals +"~" +filterReferalCovers.get(0).getCoverName() ;
								referral = true ;
							}
						
						} else {
							List<FactorRateRequestDetails> filterSubCovers  = covers.stream().filter( o -> o.getVehicleId().equals(veh.getVehicleId()) &&  o.getSectionId().equals(Integer.valueOf(veh.getSectionId())) && o.getCoverId().equals(cov.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(cov.getSubCoverId()))  ).collect(Collectors.toList());
							userOptCovers.addAll(filterSubCovers);
							List<FactorRateRequestDetails> filterReferalSubCovers = filterSubCovers.stream().filter( o ->  o.getVehicleId().equals(veh.getVehicleId()) &&  o.getCoverId().equals(cov.getCoverId()) &&  o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0) &&  cov.getIsReferal()!=null && cov.getIsReferal().equalsIgnoreCase("Y")  ).collect(Collectors.toList());
							if(filterReferalSubCovers.size()>0  ) { 
								referrals = StringUtils.isBlank(referrals)? filterReferalSubCovers.get(0).getCoverName() : referrals +"~" +filterReferalSubCovers.get(0).getCoverName() ;
								referral = true ;
							}
						}
					}
					
					// Master Referals 
					List<MasterReferralDetails> filterMasterReferals =  findMasterRefrals.stream().filter(o -> o.getRiskId().equals(veh.getVehicleId()) 
							&& o.getProductId().equals(Integer.valueOf(req.getProductId())) 
							&& o.getSectionId().equals(Integer.valueOf(veh.getSectionId())) ).collect(Collectors.toList());
							
					if (  filterMasterReferals!=null && filterMasterReferals.size()>0 ) {
						for ( MasterReferralDetails masRef : filterMasterReferals) {
							referrals = StringUtils.isBlank(referrals)? masRef.getReferralDesc() : referrals +"~" +masRef.getReferralDesc() ;
							referral = true ;
						}
					}
					IndividualReferalReq indu = new IndividualReferalReq();
					indu.setProductId(req.getProductId());
					indu.setRiskId(veh.getVehicleId());
					indu.setSectionId(req.getSectionId());	
					indu.setReferals(referrals);
					induRefs.add(indu);
					
					
				}
			
				// Under Writter Refral Checking
				List<UwQuestionsDetails>  uwQuestions = uwRepo.findByRequestReferenceNo( req.getRequestReferenceNo());
				List<UwQuestionsDetails>  filterUwQuestions = uwQuestions.stream().filter( o -> o.getIsReferral()!=null && o.getIsReferral().equalsIgnoreCase("Y") ).collect(Collectors.toList());
				if(filterUwQuestions.size()>0 ) {
					referral = true ;
					
					referralRemarks = StringUtils.isBlank(referralRemarks)?  filterUwQuestions.get(0).getUwQuestionDesc() : referralRemarks +"~" + filterUwQuestions.get(0).getUwQuestionDesc() ;
					
				}
				
				// Update User Opted Covers 
				for (FactorRateRequestDetails uptCover : userOptCovers ) {
					
					uptCover.setUserOpt("Y");
					facRateRepo.save(uptCover);
				}
			}	
			
			
			
			
			if (  referral == true ) {
					if ( req.getProductId().equalsIgnoreCase(motorProductId)) {
						List<EserviceMotorDetails> motorDatas = eserMotRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
						for (EserviceMotorDetails mot : motorDatas ) {
							mot.setStatus("RP");
							List<IndividualReferalReq> filterInduRef = induRefs.stream().filter( o -> o.getRiskId().equals(mot.getRiskId()) ).collect(Collectors.toList()) ;
							String induRefDesc  = filterInduRef.size()> 0 ?  filterInduRef.get(0).getReferals() : "" ;
							mot.setReferalRemarks(StringUtils.isBlank(referralRemarks) ? induRefDesc : referralRemarks + ( StringUtils.isNotBlank(induRefDesc) ?  "~" +induRefDesc :"") ) ;
							mot.setUpdatedDate(new Date());
							mot.setQuoteNo("");
							mot.setCustomerId("");
							eserMotRepo.save(mot);
						}
					} else if ( req.getProductId().equalsIgnoreCase(travelProductId)) {
						EserviceTravelDetails travelData = eserTraRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
						
						travelData.setStatus("RP");
						travelData.setReferalRemarks(referralRemarks);
						travelData.setUpdatedDate(new Date());
						travelData.setQuoteNo("");
						travelData.setCustomerId("");
						eserTraRepo.save(travelData);
						
					} else if ( req.getProductId().equalsIgnoreCase(buildingProductId)) {
						List<EserviceBuildingDetails> buildingDatas = eserBuildRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
						for (EserviceBuildingDetails build : buildingDatas ) {
							build.setStatus("RP");
							build.setReferalRemarks(referralRemarks);
							build.setUpdatedDate(new Date());
							build.setQuoteNo("");
							build.setCustomerId("");
							eserBuildRepo.save(build);
						}
					} else {
						List<EserviceCommonDetails> commonDatas = eserCommonRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
						for (EserviceCommonDetails commonData : commonDatas ) {
							List<IndividualReferalReq> filterInduRef = induRefs.stream().filter( o -> o.getRiskId().equals(commonData.getRiskId()) ).collect(Collectors.toList()) ;
							String induRefDesc  = filterInduRef.size()> 0 ?  filterInduRef.get(0).getReferals() : "" ;
							commonData.setStatus("RP");
							commonData.setReferalRemarks(StringUtils.isBlank(referralRemarks) ? induRefDesc : referralRemarks + ( StringUtils.isNotBlank(induRefDesc) ?  "~" +induRefDesc :"") ) ;
							commonData.setUpdatedDate(new Date());
							commonData.setQuoteNo("");
							commonData.setCustomerId("");
							eserCommonRepo.save(commonData);
						}
					}
					
					ReferalResponse res = new ReferalResponse();
					res.setReferalRemarks(referralRemarks);
					res.setRequestReferenceNo(req.getRequestReferenceNo());
					res.setResponse("Referral Pending");
					res.setStatus("RP");
					res.setQuoteNo(null);
					res.setReferral("true");
					
					commonRes.setCommonResponse(res);
					commonRes.setIsError(false);
					commonRes.setErrorMessage(Collections.emptyList());
				 	commonRes.setMessage("Success");
				 	
			} else {
				
				ReferalResponse res = new ReferalResponse();
				res.setReferalRemarks("");
				res.setRequestReferenceNo(req.getRequestReferenceNo());
				res.setResponse("");
				res.setStatus("");
				res.setQuoteNo(null);
				res.setReferral("false");
				commonRes.setCommonResponse(null);
				commonRes.setIsError(false);
				commonRes.setErrorMessage(Collections.emptyList());
			 	commonRes.setMessage("Success");	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " +  e.getMessage());
			errors.add(new Error("01","Common Error",e.getMessage()));
			commonRes.setCommonResponse(null);
			commonRes.setIsError(true);
			commonRes.setErrorMessage(errors);
			commonRes.setMessage("Failed");	
		}
		return commonRes ;
	}


	public CommonRes oldQuoteRecordsDeleteThreadCall( QuoteThreadReq request ) {
		CommonRes commonRes = new CommonRes();
		List<Error> errors = new ArrayList<Error>();
		String res = "" ;
		try {
			int threadCount = 1 ;
			List<Callable<Object>> queue = new ArrayList<Callable<Object>>();
			
			MyTaskList taskList = new MyTaskList(queue);
			
        	QuoteThreadCall deleteOldRecords = new QuoteThreadCall("DeleteOldRecords" , request , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo  
            		, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo,motorProductId , travelProductId,buildingProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo);
          
        	queue.add(deleteOldRecords);
        	res = "Success";
        	int success = 0;
        	 
        	 Map<String,Object> deleteRes = new HashMap<String,Object>() ;
 			
 			 ForkJoinPool forkjoin = new ForkJoinPool(threadCount); 
             ConcurrentLinkedQueue<Future<Object>> invoke  = (ConcurrentLinkedQueue<Future<Object>>) forkjoin.invoke(taskList) ;
             
 			for (Future<Object> callable : invoke) {

 				log.info(callable.getClass() + "," + callable.isDone());

 				if (callable.isDone()) {
 					Map<String, Object> map = (Map<String, Object>) callable.get();

 					for (Entry<String, Object> future : map.entrySet()) {
 						
 						if ("DeleteOldQuote".equalsIgnoreCase(future.getKey())) {
 							deleteRes = (Map<String,Object>) future.getValue();
 						}
 					}

 					success++;
 				}
 			}
 	
 			// Cust Res
 			if( deleteRes.get("Response")!=null && deleteRes.get("Response").toString().equals("Failed") ) {
 				errors.add(new Error("01","Customer Save",deleteRes.get("Errors").toString()));
 				commonRes.setCommonResponse(null);
 				commonRes.setIsError(true);
 				commonRes.setErrorMessage(errors);
 				commonRes.setMessage("Failed");
 				return commonRes ; 
 				
 			} 
 			
	        commonRes.setCommonResponse(res);
			commonRes.setIsError(false);
			commonRes.setErrorMessage(Collections.emptyList());
		 	commonRes.setMessage("Success");
		 	
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " +  e.getMessage());
			errors.add(new Error("01","Common Error",e.getMessage()));
			commonRes.setCommonResponse(null);
			commonRes.setIsError(true);
			commonRes.setErrorMessage(errors);
			commonRes.setMessage("Failed");	
		}
		return commonRes ;
	}
	//-------------------------------------------------------------Product Wise Multi Thread Call---------------------------------------------------------------------//
	public CommonRes productWiseThreadCall(NewQuoteReq req , QuoteThreadReq request ) {
		CommonRes commonRes = new CommonRes();
		List<Error> errors = new ArrayList<Error>();
		ProductThreadRes ProductThreadRes = new ProductThreadRes();
		
		try {
			int threadCount = 0 ;
			List<Callable<Object>> queue = new ArrayList<Callable<Object>>();
			
			// Multiple Vehicle Thread Call
			List<Integer> vehicleIds = req.getVehicleIdsList().stream().map(VehicleIdsReq :: getVehicleId  ).collect(Collectors.toList());
	        if (req.getProductId().equalsIgnoreCase(motorProductId) ) {
				 for (Integer vehId :  vehicleIds ) {
		            	threadCount = threadCount +  2 ;
		            	List<String> sectionId = req.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(vehId)).map(VehicleIdsReq :: getSectionId   ).collect(Collectors.toList());
		            	
		            	QuoteThreadReq request2 = new QuoteThreadReq();
		            	request2.setCustomerId(request.getCustomerId());
		            	request2.setProductId(request.getProductId());
		            	request2.setQuoteNo(request.getQuoteNo());
		            	request2.setRequestReferenceNo(request.getRequestReferenceNo());
		            	request2.setVehicleIdsList(request.getVehicleIdsList());
		            	request2.setCreatedBy(request.getCreatedBy());
		            	request2.setVehicleId(vehId);
		            	request2.setSectionId(sectionId.get(0));
		            	
		            	QuoteThreadCall motorSave = new QuoteThreadCall("MotorSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo  
		                		, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo,motorProductId , travelProductId,buildingProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo);
			            queue.add(motorSave);
						QuoteThreadCall coverSave = new QuoteThreadCall("CoverSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo 
			            		, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo,motorProductId , travelProductId,buildingProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo);
						queue.add(coverSave);	
		            }
					
			// Bulding Thread Call	 
			} else if (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
				 for (Integer vehId :  vehicleIds ) {
		            	threadCount = threadCount +  2 ;
		            	List<String> sectionId = req.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(vehId)).map(VehicleIdsReq :: getSectionId   ).collect(Collectors.toList());
		            	
		            	for ( String sec : sectionId) {
		            		QuoteThreadReq request2 = new QuoteThreadReq();
			            	request2.setCustomerId(request.getCustomerId());
			            	request2.setProductId(request.getProductId());
			            	request2.setQuoteNo(request.getQuoteNo());
			            	request2.setRequestReferenceNo(request.getRequestReferenceNo());
			            	request2.setVehicleIdsList(request.getVehicleIdsList());
			            	request2.setCreatedBy(request.getCreatedBy());
			            	request2.setVehicleId(vehId);
			            	request2.setSectionId(sec);	 
			            	QuoteThreadCall buildingSave = new QuoteThreadCall("BuildingSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo  
			                		, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo,motorProductId , travelProductId,buildingProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo);
				            queue.add(buildingSave);
							QuoteThreadCall coverSave = new QuoteThreadCall("CoverSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo 
				            		, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo,motorProductId , travelProductId,buildingProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo);
							queue.add(coverSave);	
		            	}
		            	
		            	
		            }
					
			// Multiple Travel Thread Call	 
			}else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
	        	
	        	List<EserviceTravelGroupDetails> groupData = eserGroupRepo.findByRequestReferenceNoOrderByGroupIdAsc(request.getRequestReferenceNo() );
	        	
	        	Integer passCount = 0;
	        	List<VehicleIdsReq>  filterAdult  = req.getVehicleIdsList().stream().filter( o ->  o.getVehicleId().equals(2) ).collect(Collectors.toList());
	        	List<VehicleIdsReq>  filterOthers =req.getVehicleIdsList().stream().filter( o -> ! o.getVehicleId().equals(2) ).collect(Collectors.toList());
	        	List<VehicleIdsReq>  totalGroup  = new ArrayList<VehicleIdsReq>();
	        	totalGroup.addAll(filterAdult)	;
	        	totalGroup.addAll(filterOthers);
	        	List<Integer> groupIds = totalGroup.stream().map(VehicleIdsReq :: getVehicleId  ).collect(Collectors.toList());
	        	// Filte Count
	        	 for (Integer vehId :  groupIds ) {
					 List<EserviceTravelGroupDetails> filterGroup = groupData.stream().filter( o -> o.getGroupId().equals(vehId) ).collect(Collectors.toList());				 
					 List<String> sectionId = req.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(vehId)).map(VehicleIdsReq :: getSectionId   ).collect(Collectors.toList());	
					 for (int i=0 ; i < filterGroup.get(0).getGrouppMembers() ; i++) {
						 passCount = passCount + 1 ;
						 threadCount = threadCount +  2 ;
						
		            	 QuoteThreadReq request2 = new QuoteThreadReq();
		            	 request2.setVehicleId(passCount);
		            	 request2.setCustomerId(request.getCustomerId());
		            	 request2.setProductId(request.getProductId());
		            	 request2.setQuoteNo(request.getQuoteNo());
		            	 request2.setRequestReferenceNo(request.getRequestReferenceNo());
		            	 request2.setVehicleIdsList(request.getVehicleIdsList());
		            	 request2.setCreatedBy(request.getCreatedBy());
		            	 request2.setGroupId(filterGroup.get(0).getGroupId());
		            	 request2.setGroupCount(filterGroup.get(0).getGrouppMembers());
		            	 request2.setSectionId(sectionId.get(0));
		            	 
		            	 QuoteThreadCall travelSave = new QuoteThreadCall("TravelSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo 
		            			 , homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo,motorProductId , travelProductId,buildingProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo);
			             queue.add(travelSave);
						 QuoteThreadCall coverSave = new QuoteThreadCall("CoverSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo 
								 , homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo,motorProductId , travelProductId,buildingProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo);
						 queue.add(coverSave);
					 }					 
		         } 
					// Multiple Personal Accident Thread Call
				} else  {
					for (Integer vehId : vehicleIds) {
						threadCount = threadCount + 2;
						List<String> sectionId = req.getVehicleIdsList().stream().filter(o -> o.getVehicleId().equals(vehId)).map(VehicleIdsReq::getSectionId).collect(Collectors.toList());

						for (String sec : sectionId) {
							QuoteThreadReq request2 = new QuoteThreadReq();
							request2.setCustomerId(request.getCustomerId());
							request2.setProductId(request.getProductId());
							request2.setQuoteNo(request.getQuoteNo());
							request2.setRequestReferenceNo(request.getRequestReferenceNo());
							request2.setVehicleIdsList(request.getVehicleIdsList());
							request2.setCreatedBy(request.getCreatedBy());
							request2.setVehicleId(vehId);
							request2.setSectionId(sec);
							QuoteThreadCall commonDataSave = new QuoteThreadCall("CommonDataSave", request2, em,
									eserCustRepo, eserMotRepo, facRateRepo, perInfoRepo, motorRepo, coverRepo, homeRepo,
									eserRepo, eserGroupRepo, traPassRepo, traPassHisRepo, motorProductId,
									travelProductId, buildingProductId, eserBuildRepo, eserSecRepo,eserCommonRepo,commonDataRepo);
							queue.add(commonDataSave);
							QuoteThreadCall coverSave = new QuoteThreadCall("CoverSave", request2, em, eserCustRepo,
									eserMotRepo, facRateRepo, perInfoRepo, motorRepo, coverRepo, homeRepo, eserRepo,
									eserGroupRepo, traPassRepo, traPassHisRepo, motorProductId, travelProductId,
									buildingProductId, eserBuildRepo, eserSecRepo,eserCommonRepo,commonDataRepo);
							queue.add(coverSave);
						}
					}
				}
	        // Response 
	        ProductThreadRes.setQueue(queue);
	        ProductThreadRes.setThreadCount(threadCount);	
	        commonRes.setCommonResponse(ProductThreadRes);
			commonRes.setIsError(false);
			commonRes.setErrorMessage(Collections.emptyList());
		 	commonRes.setMessage("Success");
		 	
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " +  e.getMessage());
			errors.add(new Error("01","Common Error",e.getMessage()));
			commonRes.setCommonResponse(null);
			commonRes.setIsError(true);
			commonRes.setErrorMessage(errors);
			commonRes.setMessage("Failed");	
		}
		return commonRes ;
	}
	
	
	public CommonRes setQuoteThreadReq(NewQuoteReq req ) {
		CommonRes commonRes = new CommonRes();
		List<Error> errors = new ArrayList<Error>();
	//	SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhmmssSS");
		try {
			// Id Generate
			String customerId = "" ;
			String quoteNo  = "" ;
			String subUserType  = "" ;
			
			// Find Old QuoteNo
			if(req.getProductId().equalsIgnoreCase(motorProductId)) {
				EserviceMotorDetails findMotor =  eserMotRepo.findByRequestReferenceNoAndRiskId(req.getRequestReferenceNo() , req.getVehicleIdsList().get(0).getVehicleId());
				customerId = findMotor.getCustomerId()==null?"":findMotor.getCustomerId();
				quoteNo    = findMotor.getQuoteNo()==null?"":findMotor.getQuoteNo();
				subUserType = findMotor.getSubUserType()==null?"":findMotor.getSubUserType() ;
			
			} else if(req.getProductId().equalsIgnoreCase(travelProductId)) {
				EserviceTravelDetails findTravel =  eserTraRepo.findByRequestReferenceNo(req.getRequestReferenceNo() );
				customerId = findTravel.getCustomerId()==null?"":findTravel.getCustomerId();
				quoteNo    = findTravel.getQuoteNo()==null?"":findTravel.getQuoteNo();
				subUserType = findTravel.getSubUserType()==null?"":findTravel.getSubUserType() ;
			
			}else if(req.getProductId().equalsIgnoreCase(buildingProductId)) {
				List<EserviceBuildingDetails> findBuilds =  eserBuildRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo() );
				customerId = findBuilds.get(0).getCustomerId()==null?"": findBuilds.get(0).getCustomerId();
				quoteNo    =  findBuilds.get(0).getQuoteNo()==null?"": findBuilds.get(0).getQuoteNo();
				subUserType =  findBuilds.get(0).getSubUserType()==null?"": findBuilds.get(0).getSubUserType() ;
			
			}
			
			
			// Quote No Generate
			if(StringUtils.isNotBlank( quoteNo) && (subUserType.equalsIgnoreCase("b2c")) ) {
			//	Random rand = new Random();
	       //     int random=rand.nextInt(90)+10; 
	        	customerId = "C-" + generateCustId();// idf.format(new Date()) + random ;
	            quoteNo  = "Q"+ generateQuoteNo();// idf.format(new Date()) + random ;
	        } else if (StringUtils.isBlank( quoteNo)  ) {
	       // 	Random rand = new Random();
	       //     int random=rand.nextInt(90)+10; 
	        	customerId = "C-" + generateCustId();// idf.format(new Date()) + random ;
	            quoteNo  = "Q"+ generateQuoteNo();// idf.format(new Date()) + random ;
	        } 

			QuoteThreadReq request = new QuoteThreadReq();
            request.setCustomerId(customerId);
            request.setQuoteNo(quoteNo);
            request.setRequestReferenceNo(req.getRequestReferenceNo());
            request.setVehicleIdsList(req.getVehicleIdsList());
            request.setProductId(req.getProductId());
            request.setCreatedBy(req.getCreatedBy());
            
			commonRes.setCommonResponse(request);
			commonRes.setIsError(false);
			commonRes.setErrorMessage(null);
			commonRes.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> "   );
			errors.add(new Error("01", "Common Error", e.getMessage()));
			commonRes.setCommonResponse(null);
			commonRes.setIsError(true);
			commonRes.setErrorMessage(errors);
			commonRes.setMessage("Failed");
		}
		return commonRes ;
}
	
	public CommonRes vehicleMultiThreadCall(NewQuoteReq req ) {
		CommonRes commonRes = new CommonRes();
		List<Error> errors = new ArrayList<Error>();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhmmssSS");
		try {
			// Id Generate
			String customerId = "" ;
			String quoteNo  = "" ;
			String subUserType  = "" ;
			
			// Find Old QuoteNo
			if(req.getProductId().equalsIgnoreCase(motorProductId)) {
				EserviceMotorDetails findMotor =  eserMotRepo.findByRequestReferenceNoAndRiskId(req.getRequestReferenceNo() , req.getVehicleIdsList().get(0).getVehicleId());
				customerId = findMotor.getCustomerId()==null?"":findMotor.getCustomerId();
				quoteNo    = findMotor.getQuoteNo()==null?"":findMotor.getQuoteNo();
				subUserType = findMotor.getSubUserType()==null?"":findMotor.getSubUserType() ;
			
			} else if(req.getProductId().equalsIgnoreCase(travelProductId)) {
				EserviceTravelDetails findTravel =  eserTraRepo.findByRequestReferenceNo(req.getRequestReferenceNo() );
				customerId = findTravel.getCustomerId()==null?"":findTravel.getCustomerId();
				quoteNo    = findTravel.getQuoteNo()==null?"":findTravel.getQuoteNo();
				subUserType = findTravel.getSubUserType()==null?"":findTravel.getSubUserType() ;
			
			}
			
			
			// Quote No Generate
			if(StringUtils.isNotBlank( quoteNo) && (subUserType.equalsIgnoreCase("b2c")) ) {
				Random rand = new Random();
	            int random=rand.nextInt(90)+10; 
	        	customerId = "C-" + idf.format(new Date()) + random ;
	            quoteNo  = "Q"+ idf.format(new Date()) + random ;
	        } else if (StringUtils.isBlank( quoteNo)  ) {
	        	Random rand = new Random();
	            int random=rand.nextInt(90)+10; 
	        	customerId = "C-" + idf.format(new Date()) + random ;
	            quoteNo  = "Q"+ idf.format(new Date()) + random ;
	        } 

			QuoteThreadReq request = new QuoteThreadReq();
            request.setCustomerId(customerId);
            request.setQuoteNo(quoteNo);
            request.setRequestReferenceNo(req.getRequestReferenceNo());
            request.setVehicleIdsList(req.getVehicleIdsList());
            request.setProductId(req.getProductId());
            request.setCreatedBy(req.getCreatedBy());
            
			commonRes.setCommonResponse(request);
			commonRes.setIsError(false);
			commonRes.setErrorMessage(null);
			commonRes.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> "   );
			errors.add(new Error("01", "Common Error", e.getMessage()));
			commonRes.setCommonResponse(null);
			commonRes.setIsError(true);
			commonRes.setErrorMessage(errors);
			commonRes.setMessage("Failed");
		}
		return commonRes ;
}
	
	
	
	
	public synchronized String generateQuoteNo() {
	       try {
	    	   SeqQuoteno entity;
	            entity = quoteNoRepo.save(new SeqQuoteno());          
	            return String.format("%05d",entity.getQuoteNo()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 }
	
	 public synchronized String generateCustId() {
	       try {
	    	   SeqCustid entity;
	            entity = custIdRepo.save(new SeqCustid());          
	            return String.format("%05d",entity.getCustId()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 }
}
