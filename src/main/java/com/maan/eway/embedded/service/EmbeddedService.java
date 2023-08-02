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
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.GroupMedicalDetails;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.embedded.request.Inalipa;
import com.maan.eway.embedded.response.ResponseForInalipa;
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
	public ResponseForInalipa createPolicy(String loginId, Inalipa request) {
		try {
			SimpleDateFormat format=new SimpleDateFormat("YYYY-MM-dd");			
			format.setTimeZone(TimeZone.getTimeZone("EAT"));
			
			Tuple loginInfo = ratingutil.collectProductsFromLoginId(loginId);
			Map<String,Object> commissionDetails= ratingutil.collectCommissionDetails(loginId);
			List<String> errorss=validator.validateRequest(request);
			
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
			
			Date expiredDate = new Date();//LocalDate.now();
			
			String pdfUrl="www.maansarovor.com";
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
						String noofDays=rating.get(0).get("remarks").toString();
						
						expiredDate=Date.from(LocalDate.now(ZoneId.of("Africa/Dar_es_Salaam")).plusDays(Long.parseLong(noofDays))
								.atTime(23, 59, 59).toInstant(ZoneOffset.MIN));

						double sum = rating.stream().filter(t-> t.get("baseRate")!=null).mapToDouble(t-> Double.parseDouble(t.get("baseRate").toString())).sum();
						premium=new BigDecimal(Math.round(sum));
						Double totalTax_percent=(Double) commissionDetails.get("TOTALTAX");
						BigDecimal totalTax = premium.multiply(new BigDecimal((Double) (totalTax_percent/ 100))).setScale(3, RoundingMode.HALF_UP);
						BigDecimal overallPremium = premium.add(totalTax);
						
						Double commissionPercent=(Double) commissionDetails.get("COMMISSION_PERCENTAGE"); 
						BigDecimal totalcommission =premium.multiply(new BigDecimal((Double) (commissionPercent/ 100)));
						
						String policyNo=genNo.generatePolicyNo();
						
						GroupMedicalDetails medical=GroupMedicalDetails.builder()
								.amountPaid(request.getOrderValue())
								.applicationId("1")
								.clientTransactionNo(request.getTransactionNo())							
								.companyId(loginInfo.get("companyId").toString())
								.customerName(request.getInsurerName())
								.entryDate(new Date())
								.expiryDate(expiredDate)
								.inceptionDate(new Date())
								.loginId(loginId)
								.mobileCode(request.getMobileCode())
								.mobileNo(request.getMobileNumber())
								.nidaNo(request.getNIDA_Number())							
								.status("Y")
								.sectionId(Integer.parseInt(request.getPlanOpted()))
								.responsePeriod(new Date())
								.requestReferenceNo(loginId.toUpperCase()+"-"+Calendar.getInstance().getTimeInMillis()) 
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

}
