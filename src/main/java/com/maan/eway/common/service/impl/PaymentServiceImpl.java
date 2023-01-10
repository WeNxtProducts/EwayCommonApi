package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.SeqPaymentid;
import com.maan.eway.common.req.MakePaymentRes;
import com.maan.eway.common.req.MakePaymentSaveReq;
import com.maan.eway.common.req.MakePaymentUpdateReq;
import com.maan.eway.common.req.PaymentDetailsGetReq;
import com.maan.eway.common.req.PaymentDetailsGetallReq;
import com.maan.eway.common.req.PaymentDetailsSaveReq;
import com.maan.eway.common.req.PaymentDetailsSaveRes;
import com.maan.eway.common.req.PaymentInfoGetAllReq;
import com.maan.eway.common.req.PaymentInfoGetReq;
import com.maan.eway.common.res.PaymentDetailGetRes;
import com.maan.eway.common.res.PaymentInfoGetRes;
import com.maan.eway.common.service.PaymentService;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.CompanyBranchReq;
import com.maan.eway.master.service.impl.ClausesMasterServiceImpl;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PaymentInfoRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.SeqPaymentidRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

@Service
@Transactional

public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private PaymentDetailRepository paymentdetailrepo;
	
	@Autowired
	private PaymentInfoRepository paymentinforepo;
	
	@Autowired
	private HomePositionMasterRepository homerepo;
	
	@Autowired
	private ListItemValueRepository listrepo;
	
	@Autowired
	private PersonalInfoRepository personalrepo;
	
	@Autowired
	private SeqPaymentidRepository seqPayIdrepo;
	
	
	
	@PersistenceContext
	private EntityManager em;
	

	@Autowired
	private LoginBranchMasterRepository lbranchRepo ;
	
	private Logger log = LogManager.getLogger(ClausesMasterServiceImpl.class);

	Gson json = new Gson();
	@Override
	public List<Error> validatemakepayment(MakePaymentSaveReq req) {
		List<Error> error = new ArrayList<Error>();

		try {
			
			if(StringUtils.isBlank(req.getQuoteNo())){
				error.add(new Error("01","Quote No","Please Enter Quote No"));
			}
			if(StringUtils.isBlank(req.getEmiYn())) {
				error.add(new Error("01","EmiYn","Please select Emi Yes/No"));
			} else if(req.getEmiYn().equalsIgnoreCase("Y") ) {
				
				if(StringUtils.isBlank(req.getInstallmentMonth())) {
					error.add(new Error("01","InstallmentMonth","Please Enter InstallmentMonth"));
				}
				if(StringUtils.isBlank(req.getInstallmentPeriod())) {
					error.add(new Error("01","InstallmentPeriod","Please Enter InstallmentPeriod"));
				}
			}
			
			if(StringUtils.isBlank(req.getPremium())) {
				error.add(new Error("01","Premium","Please Enter Premium"));
			}
			
			if(StringUtils.isBlank(req.getCreatedBy())) {
				error.add(new Error("01","CreatedBy","Please Enter CreatedBy"));
			}
			if(StringUtils.isBlank(req.getUserType())) {
				error.add(new Error("01","UserType","Please Enter UserType"));
			}
			if(StringUtils.isBlank(req.getSubUserType())) {
				error.add(new Error("01","SubUserType","Please Enter SubUserType"));
			}
			if(StringUtils.isBlank(req.getRemarks())) {
				error.add(new Error("01","Remarks","Please Enter Remarks"));
			}
			if(StringUtils.isBlank(req.getInsuranceId())) {
				error.add(new Error("01","InsuranceId","Please Enter InsuranceId"));
			}
			
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return error;
	}
	
	
	@Override
	public synchronized MakePaymentRes savemakepayment(MakePaymentSaveReq req) {
		// TODO Auto-generated method stub
		MakePaymentRes res = new MakePaymentRes();
		try {
			//Find data from home Position Master
			HomePositionMaster data = homerepo.findByQuoteNo(req.getQuoteNo());
			PersonalInfo personaldata = personalrepo.findByCustomerId(data.getCustomerId());
			String productName =   getCompanyProductMasterDropdown(data.getCompanyId() , data.getProductId().toString()); //productRepo.findByProductIdOrderByAmendIdDesc(Integer.valueOf(req.getProductId()));
			String companyName =  getInscompanyMasterDropdown(data.getCompanyId()) ; // companyRepo.findByCompanyIdOrderByAmendIdDesc(req.getCompanyId());
			String branchName = getCompanyBranchMasterDropdown(data.getCompanyId() , data.getBranchCode());
			
			String paymentId = generatePaymentid();
			
			// Save Paymetn Info
			PaymentInfo paymentinfo = new PaymentInfo();
			paymentinfo.setAddress1(personaldata.getAddress1());
			paymentinfo.setAmentId(0);
			paymentinfo.setBranchCode(data.getBranchCode());
			paymentinfo.setBranchName(branchName);
			paymentinfo.setCompanyId(data.getCompanyId());
			paymentinfo.setCompanyName(companyName);
			paymentinfo.setCreatedBy(req.getCreatedBy());
			paymentinfo.setCustomerCity(personaldata.getCityName());
			paymentinfo.setCustomerName(personaldata.getClientName() );
			paymentinfo.setEmailId(personaldata.getEmail1());
			paymentinfo.setEmiYn(req.getEmiYn());
			paymentinfo.setEntryDate(new Date());
			paymentinfo.setLoginId(req.getCreatedBy()); 
			paymentinfo.setMerchantReference("");
			paymentinfo.setMobileNo(personaldata.getMobileNo1());
			paymentinfo.setPaymentId(paymentId);
			paymentinfo.setPaymentStatus("PENDING");			
			paymentinfo.setPolicyEndDate(data.getExpiryDate());
			paymentinfo.setPolicyStartDate(data.getInceptionDate() );
			paymentinfo.setPremium(new BigDecimal(req.getPremium()));
			paymentinfo.setProductId(data.getProductId());
			paymentinfo.setProductDesc(productName);
			paymentinfo.setQuoteNo(req.getQuoteNo());
			paymentinfo.setRemarks(req.getRemarks());
			paymentinfo.setShorternUrl("");
			paymentinfo.setStatus("Y");			;
			paymentinfo.setSubUserType(req.getSubUserType());
			paymentinfo.setUpdatedBy(req.getCreatedBy());
			paymentinfo.setUpdatedDate(new Date());
			paymentinfo.setUserType(req.getUserType());
//			Integer validateHour = Integer.valueOf(getListItem (data.getCompanyId() , data.getBranchCode() ,"PAYMENT_VALIDATE_HOUR"));
//			Integer validateMinutes = Integer.valueOf(getListItem (data.getCompanyId() , data.getBranchCode() ,"PAYMENT_VALIDATE_MINUTES"));
//			Date today  = new Date();
//			Calendar cal = new GregorianCalendar(); 
//			cal.setTime(today);
//			cal.set(Calendar.HOUR_OF_DAY, +validateHour);
//			cal.set(Calendar.MINUTE, +validateMinutes);
//			Date validateDate = cal.getTime();
//			
//			paymentinfo.setValidityDate(validateDate);
			
			paymentinforepo.save(paymentinfo);
			
			log.info("Saved Details " + json.toJson(paymentinfo));
			res.setPaymentId(paymentinfo.getPaymentId().toString());
			res.setQuoteNo(req.getQuoteNo());
			res.setResponse("Saved Successful");
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return res;
	}

	 public synchronized String generatePaymentid() {
	       try {
	    	   SeqPaymentid entity;
	            entity = seqPayIdrepo.save(new SeqPaymentid());          
	            return String.format("%05d",entity.getPaymentId()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 }
	 
	 public synchronized String getListItem(String insuranceId , String branchCode, String itemType) {
			String itemDesc = "" ;
			List<ListItemValue> list = new ArrayList<ListItemValue>();
			try {
				Date today = new Date();
				Calendar cal = new GregorianCalendar();
				cal.setTime(today);
				today = cal.getTime();
				Date todayEnd = cal.getTime();
				
				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ListItemValue> query=  cb.createQuery(ListItemValue.class);
				// Find All
				Root<ListItemValue> c = query.from(ListItemValue.class);
				
				//Select
				query.select(c);
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(c.get("branchCode")));
				
				
				// Effective Date Start Max Filter
				Subquery<Long> effectiveDate = query.subquery(Long.class);
				Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
				effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
				Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				effectiveDate.where(a1,a2);
				// Effective Date End Max Filter
				Subquery<Long> effectiveDate2 = query.subquery(Long.class);
				Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
				effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
				Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
				Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
				effectiveDate2.where(a3,a4);
							
				// Where
				Predicate n1 = cb.equal(c.get("status"),"Y");
				Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
				Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
				Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
				Predicate n5 = cb.equal(c.get("companyId"), "99999");
				Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
				Predicate n7 = cb.equal(c.get("branchCode"), "99999");
				Predicate n8 = cb.or(n4,n5);
				Predicate n9 = cb.or(n6,n7);
				Predicate n10 = cb.equal(c.get("itemType"),itemType );
			//	Predicate n11 = cb.equal(c.get("itemCode"), itemCode);
				query.where(n1,n2,n3,n8,n9,n10).orderBy(orderList);
				// Get Result
				TypedQuery<ListItemValue> result = em.createQuery(query);
				list = result.getResultList();
				
				itemDesc = list.size() > 0 ? list.get(0).getItemValue() : "" ; 
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return itemDesc ;
		}
	 
	 public String getCompanyBranchMasterDropdown(String companyId , String branchCode ) {
			String branchName = "" ;
			try {
				Date today  = new Date();
				Calendar cal = new GregorianCalendar(); 
				cal.setTime(today);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 1);
				today   = cal.getTime();
				
				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<BranchMaster> query = cb.createQuery(BranchMaster.class);
				List<BranchMaster> list = new ArrayList<BranchMaster>();
				
				// Find All
				Root<BranchMaster>    c = query.from(BranchMaster.class);		
				
				// Select
				query.select(c );
				
			
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(c.get("branchName")));
				
				// Effective Date Max Filter
				Subquery<Long> effectiveDate = query.subquery(Long.class);
				Root<BranchMaster> ocpm1 = effectiveDate.from(BranchMaster.class);
				effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
				Predicate a1 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode") );
				Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId") );
				Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				effectiveDate.where(a1,a2,a3);
				
			    // Where	
				Predicate n1 = cb.equal(c.get("status"), "Y");
				Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
				Predicate n3 = cb.equal(c.get("companyId"),companyId );
				Predicate n4 = cb.equal(c.get("branchCode"),branchCode );
				
				query.where(n1,n2,n3,n4).orderBy(orderList);
				
				// Get Result
				TypedQuery<BranchMaster> result = em.createQuery(query);
				list = result.getResultList();
				branchName  = list.size()> 0 ? list.get(0).getBranchName() : "";	
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return branchName;
		}
	 
	public String getInscompanyMasterDropdown(String companyId ) {
		String companyName = "" ;
		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<InsuranceCompanyMaster> query = cb.createQuery(InsuranceCompanyMaster.class);
			List<InsuranceCompanyMaster> list = new ArrayList<InsuranceCompanyMaster>();
			
			// Find All
			Root<InsuranceCompanyMaster>    c = query.from(InsuranceCompanyMaster.class);		
			
			// Select
			query.select(c );
			
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("companyName")));
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<InsuranceCompanyMaster> ocpm1 = effectiveDate.from(InsuranceCompanyMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			javax.persistence.criteria.Predicate a1 = cb.equal(c.get("companyId"),ocpm1.get("companyId") );
			javax.persistence.criteria.Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			
			// Effective Date End
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<InsuranceCompanyMaster> ocpm2 = effectiveDate2.from(InsuranceCompanyMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			javax.persistence.criteria.Predicate a3 = cb.equal(c.get("companyId"),ocpm2.get("companyId") );
			javax.persistence.criteria.Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4);
			
		    // Where	
			javax.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			javax.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			javax.persistence.criteria.Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), companyId);
			
			query.where(n1,n2,n3,n4).orderBy(orderList);
	
			// Get Result
			TypedQuery<InsuranceCompanyMaster> result = em.createQuery(query);
			list = result.getResultList();
			companyName  = list.size()> 0 ? list.get(0).getCompanyName() : "";	
				
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return companyName;
	}
	
	public String getCompanyProductMasterDropdown(String companyId , String productId) {
		String productName = "";
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);;
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyProductMaster> query=  cb.createQuery(CompanyProductMaster.class);
			List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
			// Find All
			Root<CompanyProductMaster> c = query.from(CompanyProductMaster.class);
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productName")));
			
			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2,a3);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a4 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			Predicate a5 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a4,a5,a6);
			
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"),companyId);
			Predicate n5 = cb.equal(c.get("productId"),productId);
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			productName  = list.size()> 0 ? list.get(0).getProductName() : "";	
		}
			catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
				}
			return productName;
		}
	
	@Override
	public SuccessRes updatemakepayment(MakePaymentUpdateReq req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozermappper = new DozerBeanMapper();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddmmssSSS");
		PaymentDetail paymentdetail = new PaymentDetail();
		PaymentInfo paymentinfo = new PaymentInfo();
		String refNo = "";

		try {
			Date today = new Date();

			
			//Reference No Generation
			Random rand = new Random();
			int random = rand.nextInt(90) + 10;
			refNo = "EWAY" +"-" + idf.format(new Date()) + random ; 
			
			//Payment Id Count
			Long count  = paymentdetailrepo.count();
			Long paymentid = 200001+count;
			
			//Find data from home Position Master
			List<PaymentDetail> data = paymentdetailrepo.findByQuoteNoOrderByEntryDateDesc(req.getQuoteNo());

			//For Status Expired
			if(req.getStatus().equalsIgnoreCase("EXPIRED"))
			{
			//Payment Detail Save
			paymentdetail=dozermappper.map(data.get(0), PaymentDetail.class);
			paymentdetail.setPaymentStatus(req.getStatus());
			paymentdetail.setPaymentId(data.get(0).getPaymentId());
		//	paymentdetail.setPaymentReferenceNo(refNo);
			paymentdetail.setEntryDate(data.get(0).getEntryDate());
			paymentdetail.setUpdatedDate(today);

			paymentdetailrepo.save(paymentdetail);

			//Payment Info Save
			paymentinfo = dozermappper.map(data.get(0), PaymentInfo.class);			
			paymentinfo.setPaymentStatus(req.getStatus());
		//	paymentinfo.setPaymentId(data.get(0).getPaymentId());
		//	paymentinfo.setPaymentReferenceNo(refNo);
			paymentinfo.setEntryDate(data.get(0).getEntryDate());
			paymentinfo.setUpdatedDate(today);
		//	paymentinfo.setOthPaymentMode(data.get(0).getPaymentTypeDesc());
			
			paymentinforepo.save(paymentinfo);
			}

			
			//For Status Rejected
			else if(req.getStatus().equalsIgnoreCase("REJECTED"))
			{
			//Payment Detail Save
			paymentdetail=dozermappper.map(data.get(0), PaymentDetail.class);
			paymentdetail.setPaymentStatus(req.getStatus());
			paymentdetail.setPaymentId(data.get(0).getPaymentId());
		//	paymentdetail.setPaymentReferenceNo(refNo);
			paymentdetail.setEntryDate(data.get(0).getEntryDate());
			paymentdetail.setUpdatedDate(today);

			paymentdetailrepo.save(paymentdetail);

			//Payment Info Save
			paymentinfo = dozermappper.map(data.get(0), PaymentInfo.class);			
			paymentinfo.setPaymentStatus(req.getStatus());
		//	paymentinfo.setPaymentId(data.get(0).getPaymentId());
		//	paymentinfo.setPaymentReferenceNo(refNo);
			paymentinfo.setEntryDate(data.get(0).getEntryDate());
			paymentinfo.setUpdatedDate(today);
		//	paymentinfo.setOthPaymentMode(data.get(0).getPaymentTypeDesc());

			paymentinforepo.save(paymentinfo);
			}

			
			//For Status Accept
			if(req.getStatus().equalsIgnoreCase("ACCEPTED"))
			{
			List<PaymentDetail> datas = paymentdetailrepo.findByQuoteNoAndPaymentStatusOrderByEntryDateDesc(req.getQuoteNo(),"PENDING");
					
			if(datas.size()>0) {
			//Payment Detail Save
			paymentdetail=dozermappper.map(datas.get(0), PaymentDetail.class);
			paymentdetail.setPaymentStatus(req.getStatus());
			paymentdetail.setPaymentId(datas.get(0).getPaymentId());
		//	paymentdetail.setPaymentReferenceNo(datas.get(0).getPaymentReferenceNo());
			paymentdetail.setEntryDate(datas.get(0).getEntryDate());
			paymentdetail.setUpdatedDate(today);

			paymentdetailrepo.save(paymentdetail);

			//Payment Info Save
			paymentinfo = dozermappper.map(datas.get(0), PaymentInfo.class);			
			paymentinfo.setPaymentStatus(req.getStatus());
		//	paymentinfo.setPaymentId(datas.get(0).getPaymentId());
		//	paymentinfo.setPaymentReferenceNo(datas.get(0).getPaymentReferenceNo());
			paymentinfo.setEntryDate(datas.get(0).getEntryDate());
			paymentinfo.setUpdatedDate(today);
		//	paymentinfo.setOthPaymentMode(datas.get(0).getPaymentTypeDesc());
			
			paymentinforepo.save(paymentinfo);
			}
			
			}
			else {
				return res;
			}
			
			res.setSuccessId(req.getQuoteNo());
			res.setResponse("Updated Successful");
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public PaymentDetailGetRes getpaymentdetails(PaymentDetailsGetReq req) {
		// TODO Auto-generated method stub
		DozerBeanMapper dozermappper = new DozerBeanMapper();
		PaymentDetailGetRes res = new PaymentDetailGetRes();
		try {
		
		//	PaymentDetail data = paymentdetailrepo.findByQuoteNoAndPaymentIdAndPaymentReferenceNo(req.getQuoteNo(),Double.valueOf(req.getPaymentId()),req.getPaymentReferenceNo());
			
		//	res = dozermappper.map(data, PaymentDetailGetRes.class);
		//	res.setPaymentId(String.valueOf(Math.round(data.getPaymentId())));				

		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return res;
	}
	

	@Override
	public List<PaymentDetailGetRes> getallpaymentdetails(PaymentDetailsGetallReq req) {
		// TODO Auto-generated method stub
		DozerBeanMapper dozermappper = new DozerBeanMapper();
		List<PaymentDetailGetRes> resList = new ArrayList<PaymentDetailGetRes>();
		try {
			List<PaymentDetail> datas = paymentdetailrepo.findByQuoteNo(req.getQuoteNo());
			for(PaymentDetail data : datas) {
				PaymentDetailGetRes res = new PaymentDetailGetRes();
				res = dozermappper.map(data, PaymentDetailGetRes.class);
				res.setPaymentId(String.valueOf(Math.round(data.getPaymentId())));				
				resList.add(res);
				}
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public PaymentInfoGetRes getPaymentInfo(PaymentInfoGetReq req) {
		PaymentInfoGetRes res = new PaymentInfoGetRes();
		DozerBeanMapper dozermappper = new DozerBeanMapper();
		try {
			PaymentInfo data = paymentinforepo.findByQuoteNoAndPaymentId(req.getQuoteNo() , req.getPaymentId() );
			res = dozermappper.map(data, PaymentInfoGetRes.class);
			
			
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return res;
	}


	@Override
	public List<PaymentInfoGetRes> viewPaymentInfo(PaymentInfoGetAllReq req) {
		List<PaymentInfoGetRes> resList = new ArrayList<PaymentInfoGetRes>();
		DozerBeanMapper dozermappper = new DozerBeanMapper();
		try {
			List<PaymentInfo> datas = paymentinforepo.findByQuoteNoOrderByEntryDateDesc(req.getQuoteNo());
			for(PaymentInfo data : datas) {
				PaymentInfoGetRes res = new PaymentInfoGetRes();
				res = dozermappper.map(data, PaymentInfoGetRes.class);
				resList.add(res);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<Error> validatePaymentInsert(PaymentDetailsSaveReq req) {
		List<Error> error = new ArrayList<Error>();

		try {
			
			if(StringUtils.isBlank(req.getQuoteNo())){
				error.add(new Error("01","Quote No","Please Enter Quote No"));
			}
			
			if(StringUtils.isBlank(req.getCreatedBy())) {
				error.add(new Error("01","CreatedBy","Please Enter CreatedBy"));
			}
			if(StringUtils.isBlank(req.getUserType())) {
				error.add(new Error("01","UserType","Please Enter UserType"));
			}
			if(StringUtils.isBlank(req.getSubUserType())) {
				error.add(new Error("01","SubUserType","Please Enter SubUserType"));
			}
//			if(StringUtils.isBlank(req.getRemarks())) {
//				error.add(new Error("01","Remarks","Please Enter Remarks"));
//			}
			if(StringUtils.isBlank(req.getInsuranceId())) {
				error.add(new Error("01","InsuranceId","Please Enter InsuranceId"));
			}
			
			if (StringUtils.isNotBlank(req.getQuoteNo()) && StringUtils.isNotBlank(req.getPaymentId()) ) {
				PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(req.getQuoteNo(), req.getPaymentId());
				
				if(  paymentInfo.getPaymentStatus().equalsIgnoreCase("Accepted") ) {
					error.add(new Error("01","Accepted","This Payment Already Accepted From Bank Side"));
					
				} else if(  paymentInfo.getPaymentStatus().equalsIgnoreCase("Rejected") ) {
					error.add(new Error("01","Rejected","This Payment Already Rejected From Bank Side"));
					
				} else if(  paymentInfo.getPaymentStatus().equalsIgnoreCase("Accepted") ) {
					error.add(new Error("01","Cancelled","This Payment Already Cancelled"));
					
				} else if(  paymentInfo.getPaymentStatus().equalsIgnoreCase("Pending") )  {
					error.add(new Error("01","Cancelled","This Payment Already Cancelled"));
				}
				
			}
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return error;
	}


	@Override
	public PaymentDetailsSaveRes savePaymentDetails(PaymentDetailsSaveReq req) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}