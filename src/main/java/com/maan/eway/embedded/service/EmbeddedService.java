package com.maan.eway.embedded.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.maan.eway.auth.token.EncryDecryService;
import com.maan.eway.bean.GroupMedicalDetails;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.embedded.request.ClaimDetailsReq;
import com.maan.eway.embedded.request.Inalipa;
import com.maan.eway.embedded.response.InalipaDetailsRes;
import com.maan.eway.embedded.response.InalipaDetailsRes1;
import com.maan.eway.embedded.response.ResponseForInalipa;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.repository.GroupMedicalDetailsRepository;

@Service
public class EmbeddedService {

	@Autowired
	private GenerateSeqNoServiceImpl genNo;
	@Autowired
	private RatingFactorsUtil ratingutil;
	
	@Autowired
	private GroupMedicalDetailsRepository gmdRepo;
	protected SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy")  ;
	
	@Autowired
	private EmbeddedServiceValidator validator;
	DecimalFormat decimalFormat =new DecimalFormat("#####0.###");
	
	
	@Autowired
	private NotificationService notifcationService;
	
	@Value(value = "${embedded.scheduleUrl}")
	private String scheduleUrl;
	
	@Autowired
	private GroupMedicalDetailsRepository groupMedicalRepo;
	
	public ResponseForInalipa createPolicy(String loginId, Inalipa request) {
		try {
			SimpleDateFormat format=new SimpleDateFormat("YYYY-MM-dd");			
			format.setTimeZone(TimeZone.getTimeZone("EAT"));
			
			String requestReferenceNo=loginId.toUpperCase()+"-"+Calendar.getInstance().getTimeInMillis();
			String encrypt = EncryDecryService.encrypt(requestReferenceNo);
			 CompletableFuture<Tuple> task_1 = CompletableFuture.supplyAsync(()->(ratingutil.collectProductsFromLoginId(loginId)));
			 CompletableFuture<Map<String,Object>> task_2 = CompletableFuture.supplyAsync(()->(ratingutil.collectCommissionDetails(loginId)));
			 CompletableFuture<List<String>> task_3 = CompletableFuture.supplyAsync(()->(validator.validateRequest(request)));
			 CompletableFuture<String> task_4 = CompletableFuture.supplyAsync(()->(notifcationService.getShorternURL(new String(scheduleUrl.replaceAll("<LoginId>", loginId)+encrypt))))
			/*		 .completeOnTimeout(new String(scheduleUrl.replaceAll("<LoginId>", loginId)+encrypt), 1, TimeUnit.SECONDS)*/;
			 
			 List<CompletableFuture<?>> allTask=new ArrayList<CompletableFuture<?>>();
			 allTask.add(task_1);
			 allTask.add(task_2);
			 allTask.add(task_3);
			 allTask.add(task_4);
			 CompletableFuture<Void> allFuture = CompletableFuture.allOf(allTask.toArray(new CompletableFuture[allTask.size()]));
			 CompletableFuture<?> allCompletableFuture = allFuture.thenApply(future -> {
		 
				             return allTask.stream().map(completableFuture -> completableFuture.join())
		  
				                     .collect(Collectors.toList());
				  	         });

			 CompletableFuture<?> completableFuture = allCompletableFuture.toCompletableFuture();
			 String pdfUrl=null;
			 Tuple loginInfo=null;
			 List<String> errorss=null;
			 Map<String,Object> commissionDetails=null;
			 try {
				  
				 
				             List<Object> finalLists = (List<Object>) completableFuture.get();
				             for (Object finalList : finalLists) {
				            	 if(finalList instanceof String)
					            	 	pdfUrl=(String) finalList;
					             else if(finalList instanceof Tuple)
					            	 	loginInfo=(Tuple) finalList;
					             else if(finalList instanceof ArrayList)
					            	 errorss=(List<String>) finalList;
					             else if(finalList instanceof HashMap)
					            	 commissionDetails=( Map<String,Object> ) finalList;
							}
				             
				             
				         } catch (InterruptedException e) {
				  
				             e.printStackTrace();
				  
				         } catch (ExecutionException e) {
				  
				             e.printStackTrace();
				  
				         }
		/*	Tuple loginInfo = ratingutil.collectProductsFromLoginId(loginId);
			Map<String,Object> commissionDetails= ratingutil.collectCommissionDetails(loginId);
			List<String> errorss=validator.validateRequest(request);			
			String pdfUrl=notifcationService.getShorternURL(new String(scheduleUrl.replaceAll("<LoginId>", loginId)+""+EncryDecryService.encrypt(requestReferenceNo)));
			*/
			if(errorss!=null && errorss.size()>0) {
				ResponseForInalipa response=ResponseForInalipa.builder()
						.expiredDate(null)
						.policyNo(null)
						.pdfurl(null)
						.transactionNo(request.getTransactionNo())
						.isError(true)
						.errors(errorss)
						.build();
				return response;
			}	
			
			//Date expiredDate = new Date();//LocalDate.now();
			

			if(loginInfo!=null) {
					String search="companyId:"+loginInfo.get("companyId") +";productId:"+loginInfo.get("productId")+";status:{Y,R};coverId:"+request.getPlanOpted()+";"
					+DD_MM_YYYY.format(new Date())+"~effectiveDateStart&effectiveDateEnd;agencyCode:99999"
					+";branchCode:99999;";
					List<Tuple> rating =null;
							try{ rating=ratingutil.fetchAlipaRating(search); }catch (Exception e) {
								List<String> errors=new ArrayList<String>();
								errors.add("Rating Not Found");
								ResponseForInalipa response=ResponseForInalipa.builder()
										.expiredDate(null)
										.policyNo(null)
										.pdfurl(null)
										.transactionNo(request.getTransactionNo())
										.isError(true)
										.errors(errors)
										.build();
								return response;
							}
					BigDecimal premium=null;
					if(rating!=null && rating.size()>0) {
						Date inceptionDate =null;
						Date expiredDate=null;
						String noofDays=rating.get(0).get("remarks").toString();
						
						
						double sum = rating.stream().filter(t-> t.get("baseRate")!=null).mapToDouble(t-> Double.parseDouble(t.get("baseRate").toString())).sum();
						premium=new BigDecimal(Math.round(sum));
						Double totalTax_percent=(Double) commissionDetails.get("TOTALTAX");
						BigDecimal totalTax = premium.multiply(new BigDecimal((Double) (totalTax_percent/ 100))).setScale(3, RoundingMode.HALF_UP);
						BigDecimal overallPremium = premium.add(totalTax);
						
						Double commissionPercent=(Double) commissionDetails.get("COMMISSION_PERCENTAGE"); 
						BigDecimal totalcommission =premium.multiply(new BigDecimal((Double) (commissionPercent/ 100)));
						
						String policyNo=genNo.generatePolicyNo("1001","100");
						//notifcationService.getShorternURL(pdfUrl+""+policyNo);
						
						if(StringUtils.isNotEmpty(request.getOrderDate())) {
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							sdf.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Africa/Dar_es_Salaam")));
							
							inceptionDate=sdf.parse(request.getOrderDate());
							LocalDate inception_date =inceptionDate.toInstant().atZone(ZoneId.of("Africa/Dar_es_Salaam")).toLocalDate();
							expiredDate=Date.from(inception_date.plusDays(Long.parseLong(noofDays)-2)
									.atTime(23, 59, 59).toInstant(ZoneOffset.ofHours(-18)));
								
						}else {
							expiredDate=Date.from(LocalDate.now(ZoneId.of("Africa/Dar_es_Salaam")).plusDays(Long.parseLong(noofDays)-2)
									.atTime(23, 59, 59).toInstant(ZoneOffset.ofHours(-18)));
							inceptionDate=new Date();

						}
						
						GroupMedicalDetails medical=GroupMedicalDetails.builder()
								.amountPaid(request.getOrderValue())
								.applicationId("1")
								.clientTransactionNo(request.getTransactionNo())							
								.companyId(loginInfo.get("companyId").toString())
								.customerName(request.getInsurerName())
								.entryDate(new Date())
								.expiryDate(expiredDate)
								.inceptionDate(inceptionDate)
								.loginId(loginId)
								.mobileCode(request.getMobileCode())
								.mobileNo(request.getMobileNumber())
								.nidaNo(request.getNIDA_Number())							
								.status("Y")
								.sectionId(Integer.parseInt(request.getPlanOpted()))
								.responsePeriod(new Date())
								.requestReferenceNo(requestReferenceNo) 
								.productId(Integer.parseInt(loginInfo.get("productId").toString()))
								.premium(premium)
								.taxPremium(totalTax)
								.taxPercentage(new BigDecimal(totalTax_percent))
								.commissionAmount(totalcommission)
								.commissionPercentage(new BigDecimal(commissionPercent))
								.overallPremium(overallPremium)		
								.policyNo(policyNo)
								.planOpted(request.getPlanOpted())
								.pdfPath(pdfUrl)											
								.build();

						gmdRepo.save(medical);
						ResponseForInalipa response=ResponseForInalipa.builder()
								.expiredDate(format.format(expiredDate)+"T23:59:59")
								.policyNo(policyNo)
								.pdfurl(pdfUrl)
								.transactionNo(request.getTransactionNo())	
								.isError(false)
								.premium(premium)
								.taxPercent(decimalFormat.format(totalTax_percent))
								.tax(totalTax)
								.totalPremium(overallPremium)								
								.build();
						return response;
					}else {

						List<String> errors=new ArrayList<String>();
						errors.add("Rating Not Found");
						ResponseForInalipa response=ResponseForInalipa.builder()
								.expiredDate(null)
								.policyNo(null)
								.pdfurl(null)
								.transactionNo(request.getTransactionNo())
								.isError(true)
								.errors(errors)
								.build();
						return response;
					}
						
					
				
			}else {
				List<String> errors=new ArrayList<String>();
				errors.add("Account Not Valid");
				ResponseForInalipa response=ResponseForInalipa.builder()
						.expiredDate(null)
						.policyNo(null)
						.pdfurl(null)
						.transactionNo(request.getTransactionNo())
						.isError(true)
						.errors(errors)
						.build();
				return response;
			}
			
			
		}catch(DataIntegrityViolationException e) {
			
			List<String> errors=new ArrayList<String>();
			errors.add("Duplicate Policy Available");
			ResponseForInalipa response=ResponseForInalipa.builder()
					.expiredDate(null)
					.policyNo(null)
					.pdfurl(null)
					.transactionNo(request.getTransactionNo())
					.isError(true)
					.errors(errors)
					.build();
			return response;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public ResponseForInalipa createSchedule(String loginId, String encodedPolicyNo) {
		try {
		 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public InalipaDetailsRes getClaimDetails(ClaimDetailsReq req) {
		Log.info("Enter into getClaimDetails");
		InalipaDetailsRes res = new InalipaDetailsRes();
		List<InalipaDetailsRes1> resultList = new ArrayList<>();
		try {
			List<GroupMedicalDetails> list = groupMedicalRepo.findByMobileNo(req.getMobileNo());
			if(!CollectionUtils.isEmpty(list)) {
				String accDate = new SimpleDateFormat("yyyy-MM-dd").format(DD_MM_YYYY.parse(req.getAccidentDate()));
				List<Map<String,Object>> validCustList = groupMedicalRepo.getCustomerDetails(req.getMobileNo(),accDate,req.getClaimType());
				if(!CollectionUtils.isEmpty(validCustList)) {
					validCustList.forEach(k -> {
						InalipaDetailsRes1 m = InalipaDetailsRes1.builder()
								.policyNo(k.get("POLICY_NO")==null?"":k.get("POLICY_NO").toString())
								.mobileNo(k.get("MOBILE_NO")==null?"":k.get("MOBILE_NO").toString())
								.inceptionDate(k.get("INCEPTION_DATE")==null?"":DD_MM_YYYY.format(k.get("INCEPTION_DATE")))
								.expiryDate(k.get("EXPIRY_DATE")==null?"":DD_MM_YYYY.format(k.get("EXPIRY_DATE")))
								.intimatedDate(DD_MM_YYYY.format(new Date()))
								.claimType(k.get("CLAIM_TYPE")==null?"":k.get("CLAIM_TYPE").toString())
								.build();
						resultList.add(m);
					});
					res.setCommonResponse(resultList);
					res.setMessage("SUCCESS");
					res.setErrorMessage(null);
					res.setError(true);
				}else {
					res.setCommonResponse(null);
					res.setMessage("NO ACTIVE PERIOD");
					res.setErrorMessage("On the date of the accident, your policy was not active.");
					res.setError(true);
				}
			}else {
				res.setCommonResponse(null);
				res.setErrorMessage("You're not a legitimate client.");
				res.setMessage("NO DATA FOUND");
			}
			Log.info("Exit into getClaimDetails");
		}catch(Exception e) {
			Log.info("Error in getClaimDetails");
			e.printStackTrace();
		}
		return res;
	}

}
