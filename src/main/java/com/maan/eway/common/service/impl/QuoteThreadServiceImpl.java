package com.maan.eway.common.service.impl;

import java.awt.image.RescaleOp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.EserviceTravelGroupDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.bean.UwQuestionsDetails;
import com.maan.eway.common.req.CoverIdsReq;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.req.QuoteThreadReq;
import com.maan.eway.common.req.VehicleIdsReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.NewQuoteRes;
import com.maan.eway.common.res.ProductThreadRes;
import com.maan.eway.common.res.QuoteThreadRes;
import com.maan.eway.common.res.ThreadCountRes;
import com.maan.eway.common.service.QuoteThreadService;
import com.maan.eway.error.Error;
import com.maan.eway.repository.CoverDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.EserviceTravelGroupDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.repository.UwQuestionsDetailsRepository;
import com.maan.eway.res.ReferalResponse;
import com.maan.eway.thread.MyTaskList;

@Service
@Transactional
public class QuoteThreadServiceImpl implements QuoteThreadService {

	
	private Logger log = LogManager.getLogger(QuoteThreadServiceImpl.class);
	

	@Value(value = "${motor.productId}")
	private String motorProductId;
	
	@Value(value = "${travel.productId}")
	private String travelProductId;
	
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
	
	
	
	@Override
	@Transactional
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
		
		// Referal Returnin Block
		if( referal == true || (commonRes.getIsError()!=null && commonRes.getIsError()==true) ) {
			return  commonRes ;
	
		} else {
			
			List<Callable<Object>> queue = new ArrayList<Callable<Object>>();
			
			MyTaskList taskList = new MyTaskList(queue);
			
			CommonRes frameQuoteReq = setQuoteThreadReq(req );
            if( frameQuoteReq.getErrorMessage() !=null && frameQuoteReq.getErrorMessage().size()>0 ) {
            	commonRes = frameQuoteReq ;
            	return commonRes ; 
            }
            
            // Frame Request 
            QuoteThreadReq request = (QuoteThreadReq) frameQuoteReq.getCommonResponse() ;
           
            // Customer Save Thread Call
            QuoteThreadCall customerSave = new QuoteThreadCall("CustomerSave" , request , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo 
            		, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,motorProductId , travelProductId);
            queue.add(customerSave);
            
            int threadCount = 1 ;
            int success = 0;
            
            commonRes = productWiseThreadCall( req , request ) ;
        	 if( frameQuoteReq.getErrorMessage() !=null && frameQuoteReq.getErrorMessage().size()>0 ) {
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
				// Home Positiom Master Thread Call
				List<Callable<Object>> queue2 = new ArrayList<Callable<Object>>();
				MyTaskList taskList2 = new MyTaskList(queue2);
				request.setVehicleId(req.getVehicleIdsList().get(0).getVehicleId());
				QuoteThreadCall quoteSave = new QuoteThreadCall("QuoteSave" , request , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo  
	            		, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,motorProductId , travelProductId);
	            
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
			//	HomePositionMaster homeData = homeRepo.findByQuoteNo(request.getQuoteNo());
				
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
	
	public CommonRes RefferalChecking(NewQuoteReq req ) {
		CommonRes commonRes = new CommonRes();
		List<Error> errors = new ArrayList<Error>();
		try {
				
			boolean referral = false ;
			String referralRemarks = "" ;
			
			if(StringUtils.isNotBlank(req.getManualReferralYn()) && req.getManualReferralYn().equalsIgnoreCase("Y") ) {
				referral = true ;
				referralRemarks = req.getReferralRemarks();
				
			} else {
				List<FactorRateRequestDetails> userOptCovers = new ArrayList<FactorRateRequestDetails>();
		
				// Covers Referrral Checking
				List<FactorRateRequestDetails> covers = facRateRepo.findByRequestReferenceNoOrderByVehicleIdAsc(req.getRequestReferenceNo()); 
				for (VehicleIdsReq veh : req.getVehicleIdsList()) {
	
					// Cover Referal Checking
					List<CoverIdsReq> coverList = veh.getCoverIdList();
					for (CoverIdsReq cov : coverList) {
						if(StringUtils.isBlank(cov.getSubCoverYn()) || cov.getSubCoverYn().equalsIgnoreCase("N") ) {
							List<FactorRateRequestDetails> filterCovers = covers.stream().filter( o -> o.getCoverId().equals(cov.getCoverId()) ).collect(Collectors.toList());		
							userOptCovers.addAll(filterCovers);
							
							List<FactorRateRequestDetails> filterReferalCovers = filterCovers.stream().filter( o -> o.getCoverId().equals(cov.getCoverId()) &&  o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0) &&  cov.getIsReferal()!=null && cov.getIsReferal().equalsIgnoreCase("Y") ).collect(Collectors.toList());
							if(filterReferalCovers.size()>0 && StringUtils.isBlank(referralRemarks) && referral==false ) { 
								referralRemarks = filterReferalCovers.get(0).getCoverName() ;
								referral = true ;
							}
						
						} else {
							List<FactorRateRequestDetails> filterSubCovers  = covers.stream().filter( o -> o.getCoverId().equals(cov.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(cov.getSubCoverId()))  ).collect(Collectors.toList());
							userOptCovers.addAll(filterSubCovers);
							List<FactorRateRequestDetails> filterReferalSubCovers = filterSubCovers.stream().filter( o -> o.getCoverId().equals(cov.getCoverId()) &&  o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0) &&  cov.getIsReferal()!=null && cov.getIsReferal().equalsIgnoreCase("Y")  ).collect(Collectors.toList());
							if(filterReferalSubCovers.size()>0 && StringUtils.isBlank(referralRemarks) && referral==false) { 
								referralRemarks = filterReferalSubCovers.get(0).getCoverName() ;
								referral = true ;
							}
						}
					}
				}
			
				// Update User Opted Covers 
				for (FactorRateRequestDetails uptCover : userOptCovers ) {
					
					uptCover.setUserOpt("Y");
					facRateRepo.save(uptCover);
				}
				
				// Under Writter Refral Checking
				List<UwQuestionsDetails>  uwQuestions = uwRepo.findByRequestReferenceNo( req.getRequestReferenceNo());
				List<UwQuestionsDetails>  filterUwQuestions = uwQuestions.stream().filter( o -> o.getIsReferral()!=null && o.getIsReferral().equalsIgnoreCase("Y") ).collect(Collectors.toList());
				if(filterUwQuestions.size()>0 ) {
					referral = true ;
					if(StringUtils.isBlank(referralRemarks)) {
						referralRemarks =  filterUwQuestions.get(0).getUwQuestionDesc();
						
					}
				}
			}	
			
			if (  referral == true ) {
					if ( req.getProductId().equalsIgnoreCase(motorProductId)) {
						List<EserviceMotorDetails> motorDatas = eserMotRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
						for (EserviceMotorDetails mot : motorDatas ) {
							mot.setStatus("RP");
							mot.setReferalRemarks(referralRemarks);
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


	//-------------------------------------------------------------Product Wise Multi Thread Call---------------------------------------------------------------------//
	@Transactional
	public CommonRes productWiseThreadCall(NewQuoteReq req , QuoteThreadReq request ) {
		CommonRes commonRes = new CommonRes();
		List<Error> errors = new ArrayList<Error>();
		ProductThreadRes ProductThreadRes = new ProductThreadRes();
		
		try {
			int threadCount = 0 ;
			List<Callable<Object>> queue = new ArrayList<Callable<Object>>();
			 //  // Delete Old Record
			Long coverInfo =  coverRepo.countByQuoteNo(request.getQuoteNo());
 			if (coverInfo >0 ) {
 				//Delete data
 				CriteriaBuilder cb = em.getCriteriaBuilder();
 				CriteriaDelete<PolicyCoverData> delete = cb.createCriteriaDelete(PolicyCoverData.class);

 				Root<PolicyCoverData> pc = delete.from(PolicyCoverData.class);

 				//Where
 				Predicate n1 = cb.equal(pc.get("quoteNo"), request.getQuoteNo());
 				delete.where(n1);
 				em.createQuery(delete).executeUpdate();
 				
 			}
			// Multiple Vehicle Thread Call
			List<Integer> vehicleIds = req.getVehicleIdsList().stream().map(VehicleIdsReq :: getVehicleId  ).collect(Collectors.toList());
	        if (req.getProductId().equalsIgnoreCase(motorProductId) ) {
				 for (Integer vehId :  vehicleIds ) {
		            	threadCount = threadCount +  2 ;
		            	QuoteThreadReq request2 = new QuoteThreadReq();
		            	request2.setCustomerId(request.getCustomerId());
		            	request2.setProductId(request.getProductId());
		            	request2.setQuoteNo(request.getQuoteNo());
		            	request2.setRequestReferenceNo(request.getRequestReferenceNo());
		            	request2.setVehicleIdsList(request.getVehicleIdsList());
		            	request2.setCreatedBy(request.getCreatedBy());
		            	request2.setVehicleId(vehId); 
		            	QuoteThreadCall motorSave = new QuoteThreadCall("MotorSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo  
		                		, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,motorProductId , travelProductId);
			            queue.add(motorSave);
						QuoteThreadCall coverSave = new QuoteThreadCall("CoverSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo 
			            		, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,motorProductId , travelProductId);
						queue.add(coverSave);	
		            }
				 
				 // Delete Old Record
				// // Delete Old Record
				Long motorInfo =  motorRepo.countByQuoteNo(request.getQuoteNo());
				if (motorInfo > 0 ) {
					//Delete data
	 				CriteriaBuilder cb = em.getCriteriaBuilder();
	 				CriteriaDelete<MotorDataDetails> delete = cb.createCriteriaDelete(MotorDataDetails.class);

	 				Root<MotorDataDetails> m = delete.from(MotorDataDetails.class);

	 				//Where
	 				Predicate n1 = cb.equal(m.get("quoteNo"), request.getQuoteNo());
	 				delete.where(n1);
	 				em.createQuery(delete).executeUpdate();
					
				}
				
					
			}else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
	        	
	        	List<EserviceTravelGroupDetails> groupData = eserGroupRepo.findByRequestReferenceNoOrderByGroupIdAsc(request.getRequestReferenceNo() );
	        	
	        	Integer passCount = 0;
	        	// Filte Count
	        	 for (Integer vehId :  vehicleIds ) {
					 List<EserviceTravelGroupDetails> filterGroup = groupData.stream().filter( o -> o.getGroupId().equals(vehId) ).collect(Collectors.toList());				 
					
					 for (int i=0 ; i <= filterGroup.get(0).getGrouppMembers() ; i++) {
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
		            	 QuoteThreadCall travelSave = new QuoteThreadCall("TravelSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo 
		            			 , homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,motorProductId , travelProductId);
			             queue.add(travelSave);
						 QuoteThreadCall coverSave = new QuoteThreadCall("CoverSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo 
								 , homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,motorProductId , travelProductId);
						 queue.add(coverSave);
					 }					 
		         } 
	        	//  // Delete Old Record
				Long travelInfo =  traPassRepo.countByQuoteNo(request.getQuoteNo() );
				if (travelInfo > 0 ) {
					//Delete data
	 				CriteriaBuilder cb = em.getCriteriaBuilder();
	 				CriteriaDelete<TravelPassengerDetails> delete = cb.createCriteriaDelete(TravelPassengerDetails.class);

	 				Root<TravelPassengerDetails> m = delete.from(TravelPassengerDetails.class);

	 				//Where
	 				Predicate n1 = cb.equal(m.get("quoteNo"), request.getQuoteNo());
	 				delete.where(n1);
	 				em.createQuery(delete).executeUpdate();
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
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhmmssSS");
		try {
			// Id Generate
			String customerId = "" ;
			String quoteNo  = "" ;
			String subUserType  = "" ;
			
			// Find Old QuoteNo
			if(req.getProductId().equalsIgnoreCase(motorProductId)) {
				EserviceMotorDetails findMotor =  eserMotRepo.findByRequestReferenceNoAndVehicleId(req.getRequestReferenceNo() , req.getVehicleIdsList().get(0).getVehicleId());
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
				EserviceMotorDetails findMotor =  eserMotRepo.findByRequestReferenceNoAndVehicleId(req.getRequestReferenceNo() , req.getVehicleIdsList().get(0).getVehicleId());
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
}
