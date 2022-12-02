package com.maan.eway.common.service.impl;

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
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.UwQuestionsDetails;
import com.maan.eway.common.req.CoverIdsReq;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.req.QuoteThreadReq;
import com.maan.eway.common.req.VehicleIdsReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.NewQuoteRes;
import com.maan.eway.common.res.QuoteThreadRes;
import com.maan.eway.common.service.QuoteThreadService;
import com.maan.eway.error.Error;
import com.maan.eway.repository.CoverDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.UwQuestionsDetailsRepository;
import com.maan.eway.res.ReferalResponse;
import com.maan.eway.thread.MyTaskList;

@Service
@Transactional
public class QuoteThreadServiceImpl implements QuoteThreadService {

	
	private Logger log = LogManager.getLogger(QuoteThreadServiceImpl.class);
	
	
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private EserviceCustomerDetailsRepository eserCustRepo ;
	
	@Autowired
	private EServiceMotorDetailsRepository eserMotRepo ;
	
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
	
	@Override
	@Transactional
	public CommonRes call_OT_Insert(NewQuoteReq req) {
		CommonRes commonRes = new CommonRes();
		NewQuoteRes response = new NewQuoteRes();
		List<Error> errors = new ArrayList<Error>();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhmmssSS");
		try {
		
			List<UwQuestionsDetails>  uwQuestions = uwRepo.findByRequestReferenceNo( req.getRequestReferenceNo());
			List<FactorRateRequestDetails> covers = facRateRepo.findByRequestReferenceNoOrderByVehicleIdAsc(req.getRequestReferenceNo()); 
			
		boolean referal = false ;
		String referalRemarks = "" ;
		List<FactorRateRequestDetails> userOptCovers = new ArrayList<FactorRateRequestDetails>();
		
		if( StringUtils.isBlank(req.getAdminLoginId())) {
			for (VehicleIdsReq veh : req.getVehicleIdsList() ){
						
				// Cover Referal Checking
				List<CoverIdsReq> coverList = veh.getCoverIdList();
				for (CoverIdsReq cov : coverList  ) {
					if(StringUtils.isBlank(cov.getSubCoverYn()) || cov.getSubCoverYn().equalsIgnoreCase("N") ) {
						List<FactorRateRequestDetails> filterCovers = covers.stream().filter( o -> o.getCoverId().equals(cov.getCoverId()) ).collect(Collectors.toList());		
						userOptCovers.addAll(filterCovers);
						
						List<FactorRateRequestDetails> filterReferalCovers = filterCovers.stream().filter( o -> o.getCoverId().equals(cov.getCoverId()) &&  o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0) &&  cov.getIsReferal()!=null && cov.getIsReferal().equalsIgnoreCase("Y") ).collect(Collectors.toList());
						if(filterReferalCovers.size()>0 && StringUtils.isBlank(referalRemarks) && referal==false ) { 
							referalRemarks = filterReferalCovers.get(0).getCoverName() ;
							referal = true ;
						}
					
					} else {
						List<FactorRateRequestDetails> filterSubCovers  = covers.stream().filter( o -> o.getCoverId().equals(cov.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(cov.getSubCoverId()))  ).collect(Collectors.toList());
						userOptCovers.addAll(filterSubCovers);
						List<FactorRateRequestDetails> filterReferalSubCovers = filterSubCovers.stream().filter( o -> o.getCoverId().equals(cov.getCoverId()) &&  o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0) &&  cov.getIsReferal()!=null && cov.getIsReferal().equalsIgnoreCase("Y")  ).collect(Collectors.toList());
						if(filterReferalSubCovers.size()>0 && StringUtils.isBlank(referalRemarks) && referal==false) { 
							referalRemarks = filterReferalSubCovers.get(0).getCoverName() ;
							referal = true ;
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
			List<UwQuestionsDetails>  filterUwQuestions = uwQuestions.stream().filter( o -> o.getIsReferral()!=null && o.getIsReferral().equalsIgnoreCase("Y") ).collect(Collectors.toList());
			if(filterUwQuestions.size()>0 ) {
				referal = true ;
				if(StringUtils.isBlank(referalRemarks)) {
					referalRemarks =  filterUwQuestions.get(0).getUwQuestionDesc();
					
				}
			}
		}
			
		if (  referal == true ) {
			if ( req.getProductId().equalsIgnoreCase("5")) {
				List<EserviceMotorDetails> motorDatas = eserMotRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
				for (EserviceMotorDetails mot : motorDatas ) {
					mot.setStatus("RP");
					mot.setReferalRemarks(referalRemarks);
					mot.setUpdatedDate(new Date());
					mot.setQuoteNo("");
					mot.setCustomerId("");
					eserMotRepo.save(mot);
				}
			}
			
			ReferalResponse res = new ReferalResponse();
			res.setReferalRemarks(referalRemarks);
			res.setRequestReferenceNo(req.getRequestReferenceNo());
			res.setResponse("Referral Pending");
			res.setStatus("RP");
			res.setQuoteNo(null);
			commonRes.setCommonResponse(res);
			commonRes.setIsError(false);
			commonRes.setErrorMessage(Collections.emptyList());
			commonRes.setMessage("Success");
			
		} else {
			List<Callable<Object>> queue = new ArrayList<Callable<Object>>();
			
			MyTaskList taskList = new MyTaskList(queue);
			
			CommonRes frameQuoteReq = setQuoteThreadReq(req );
            if( frameQuoteReq.getErrorMessage() !=null && frameQuoteReq.getErrorMessage().size()>0 ) {
            	commonRes = frameQuoteReq ;
            	return commonRes ; 
            }
            
            List<Integer> vehicleIds = req.getVehicleIdsList().stream().map(VehicleIdsReq :: getVehicleId  ).toList();
            QuoteThreadReq request = (QuoteThreadReq) frameQuoteReq.getCommonResponse() ;
            
            // Customer Save
            QuoteThreadCall customerSave = new QuoteThreadCall("CustomerSave" , request , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo  , homeRepo);
            queue.add(customerSave);
            
            int threadCount = 1 ;
            int success = 0;
			Map<String,Object> custRes = new HashMap<String,Object>() ;
			List<Map<String,Object>> motRes =  new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> covRes =  new ArrayList<Map<String,Object>>() ;
			
			// Multiple Vehicle Thread Call
			if (req.getProductId().equalsIgnoreCase("5") ) {
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
		            	QuoteThreadCall motorSave = new QuoteThreadCall("MotorSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo  , homeRepo);
			            queue.add(motorSave);
						QuoteThreadCall coverSave = new QuoteThreadCall("CoverSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo  , homeRepo);
						queue.add(coverSave);	
		            } 
			}
           
            
            ForkJoinPool forkjoin = new ForkJoinPool(threadCount); 
            ConcurrentLinkedQueue<Future<Object>> invoke  = (ConcurrentLinkedQueue<Future<Object>>) forkjoin.invoke(taskList) ;
            
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
				QuoteThreadCall quoteSave = new QuoteThreadCall("QuoteSave" , request , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,coverRepo  , homeRepo );
	            
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

	public CommonRes setQuoteThreadReq(NewQuoteReq req ) {
		CommonRes commonRes = new CommonRes();
		List<Error> errors = new ArrayList<Error>();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhmmssSS");
		try {
			// Id Generate
			EserviceMotorDetails findMotor =  eserMotRepo.findByRequestReferenceNoAndVehicleId(req.getRequestReferenceNo() , req.getVehicleIdsList().get(0).getVehicleId());
			
			String customerId = "" ;
			String quoteNo  = "" ;
			
			if(StringUtils.isNotBlank( findMotor.getQuoteNo()) && (findMotor.getSubUserType().equalsIgnoreCase("b2c")) ) {
				Random rand = new Random();
	            int random=rand.nextInt(90)+10; 
	        	customerId = "C-" + idf.format(new Date()) + random ;
	            quoteNo  = "Q"+ idf.format(new Date()) + random ;
	        } else if (StringUtils.isNotBlank( findMotor.getQuoteNo())  ) {
	        	customerId = findMotor.getCustomerId() ;
	            quoteNo  = findMotor.getQuoteNo() ;
	        } else {
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
