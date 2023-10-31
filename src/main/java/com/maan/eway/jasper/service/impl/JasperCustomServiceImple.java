package com.maan.eway.jasper.service.impl;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.BuildingDetails;
import com.maan.eway.bean.ClausesMaster;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.ContentAndRisk;
import com.maan.eway.bean.CountryMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.ExclusionMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.MotorMakeModelMaster;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.ProductEmployeeDetails;
import com.maan.eway.bean.ProductGroupMaster;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.TermsAndCondition;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.bean.WarrantyMaster;
import com.maan.eway.jasper.res.CreditDataSetOne;
import com.maan.eway.jasper.res.CreditDataSetTwo;
import com.maan.eway.jasper.res.CreditNoteRes;
import com.maan.eway.jasper.res.MotorCoverNoteRes;
import com.maan.eway.jasper.res.MotorPrivateDriverDetails;
import com.maan.eway.jasper.res.MotorPrivateRes;
import com.maan.eway.jasper.res.MotorPrivateVehicleDetails;
import com.maan.eway.jasper.res.TaxDataSetOneRes;
import com.maan.eway.jasper.res.TaxInvoiceRes;
import com.maan.eway.jasper.res.TravelDataSetOneRes;
import com.maan.eway.jasper.res.TravelDataSetTwoRes;
import com.maan.eway.jasper.res.TravelReportRes;
import com.maan.eway.repository.BuildingDetailsRepository;
import com.maan.eway.repository.ContentAndRiskRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;
import com.maan.eway.repository.ProductEmployeesDetailsRepository;

@Component
public class JasperCustomServiceImple {
	
	Logger log = LogManager.getLogger(JasperCustomServiceImple.class);

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private MotorDataDetailsRepository motorRepo;
	
	@Autowired
	private MotorDriverDetailsRepository motordriverRepo;
	
	@Autowired
	private ContentAndRiskRepository conAndRiskRepo;
	
	@Autowired
	private BuildingDetailsRepository buildingDetRepo;
	
	@Autowired
	private ProductEmployeesDetailsRepository productEmpDetRepo;
	
	@Autowired
	private EserviceCommonDetailsRepository eserviceCommonDetRepo;
		
	private String RenewalDate(String Input) {
		DateTimeFormatter inputformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
		LocalDateTime dateTime = LocalDateTime.parse(Input, inputformatter);
		return dateTime.toLocalDate().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}

	public MotorCoverNoteRes getMotorCoverNote(String policyNo) {
	  log.info("Enter into getMotorCoverNote.\nArgument ==> PolicyNo :"+policyNo);
		MotorCoverNoteRes response = new  MotorCoverNoteRes();
  try {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<MotorDataDetails> mddRoot = cq.from(MotorDataDetails.class);
		Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
		Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
		Root<InsuranceCompanyMaster> icmRoot = cq.from(InsuranceCompanyMaster.class);
		Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
		
		Subquery<String> insureName = cq.subquery(String.class);
		Root<LoginUserInfo> SubluiRoot = insureName.from(LoginUserInfo.class);
		insureName.select(cb.upper(SubluiRoot.get("userName"))).where(cb.equal(SubluiRoot.get("loginId"), hpmRoot.get("loginId")));
		
		Subquery<Integer> modelTypeAmd = cq.subquery(Integer.class);
		Root<MotorMakeModelMaster> SubmmAmd = modelTypeAmd.from(MotorMakeModelMaster.class);
		modelTypeAmd.select(cb.max(SubmmAmd.get("amendId"))).where(cb.equal(SubmmAmd.get("vehiclemodelcode"), mddRoot.get("modelNumber")),
				cb.equal(SubmmAmd.get("status"), "Y"),cb.equal(SubmmAmd.get("companyId"), hpmRoot.get("companyId")));
		
		Subquery<String> modelType = cq.subquery(String.class);
		Root<MotorMakeModelMaster> Submm = modelType.from(MotorMakeModelMaster.class);
		modelType.select(Submm.get("modelNameEn")).where(cb.equal(Submm.get("vehiclemodelcode"), mddRoot.get("modelNumber")),
				cb.equal(Submm.get("companyId"), hpmRoot.get("companyId")),cb.equal(Submm.get("status"), "Y"),cb.equal(Submm.get("amendId"), modelTypeAmd));
		
		Subquery<Integer> icmAmd = cq.subquery(Integer.class);
		Root<InsuranceCompanyMaster> SubicmAmd = icmAmd.from(InsuranceCompanyMaster.class);
		icmAmd.select(cb.max(SubicmAmd.get("amendId"))).where(cb.equal(SubicmAmd.get("companyId"), icmRoot.get("companyId")));
		
		cq.multiselect(mddRoot.get("vehicleId").alias("vehicleId"),
				cb.concat(piRoot.get("titleDesc"), cb.concat(".", piRoot.get("clientName"))).alias("customerName"),
				cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")),hpmRoot.get("customerName"))
						.otherwise(insureName).alias("insurerName"),
				hpmRoot.get("policyCovertedDate").alias("paymentDate"),
				hpmRoot.get("inceptionDate").alias("inceptionDate"),
				hpmRoot.get("expiryDate").alias("expiryDate"),
				hpmRoot.get("coverNoteReferenceNo").alias("covernoteNo"),
				hpmRoot.get("stickerNumber").alias("stickerNumber"),
				mddRoot.get("registrationNumber").alias("registrationNumber"),
				mddRoot.get("vehicleTypeDesc").alias("vehicleTypeDesc"),
				cb.selectCase().when(cb.isNotNull(mddRoot.get("vehcileModelDesc")), mddRoot.get("vehcileModelDesc"))
						.otherwise(modelType).alias("modelType"),
				mddRoot.get("colorDesc").alias("colorDesc"),
				mddRoot.get("cubicCapacity").alias("cubicCapacity"),
				mddRoot.get("vehicleMakeDesc").alias("vehicleMakeDesc"),
				mddRoot.get("chassisNumber").alias("chassisNumber"),
				mddRoot.get("seatingCapacity").alias("seatingCapacity"),
				mddRoot.get("engineNumber").alias("engineNumber"),
				mddRoot.get("fuelTypeDesc").alias("fuelType"),
				mddRoot.get("policyTypeDesc").alias("policyTypeDesc"),
				mddRoot.get("manufactureYear").alias("manufactureYear"),
				cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")), piRoot.get("mobileNo1"))
						.otherwise(luiRoot.get("userMobile")).alias("agentMobile"),
				mddRoot.get("motorUsageDesc").alias("motorUsageDesc"),
				hpmRoot.get("companyName").alias("companyName"),
				hpmRoot.get("branchName").alias("branchName"),
				hpmRoot.get("currency").alias("currency"),
				mddRoot.get("sectionName").alias("sectionName"),
				mddRoot.get("vehcileModel").alias("vehcileModel"),
				cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")),hpmRoot.get("premiumLc"))
						.otherwise(hpmRoot.get("vatPremiumFc")).alias("premium"),
				cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")), hpmRoot.get("vatPremiumLc"))
						.otherwise(hpmRoot.get("vatPremiumFc")).alias("vatPremium"),
				cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")), hpmRoot.get("overallPremiumLc"))
						.otherwise(hpmRoot.get("overallPremiumFc")).alias("overallPremium"))
		.where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId")),
				cb.equal(mddRoot.get("quoteNo"), hpmRoot.get("quoteNo")),
				cb.equal(luiRoot.get("loginId"), hpmRoot.get("loginId")),
				cb.equal(hpmRoot.get("currency"), icmRoot.get("currencyId")),
				cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")),
				cb.equal(icmRoot.get("amendId"), icmAmd),
				cb.equal(hpmRoot.get("productId"), "46"),
				cb.equal(hpmRoot.get("status"), "P"),
				cb.equal(hpmRoot.get("policyNo"), policyNo));
		List<Tuple> list = em.createQuery(cq).getResultList();
		if(!CollectionUtils.isEmpty(list)) {
			Tuple map = list.get(0);
			response.setVehicleId(map.get("vehicleId")==null?"":map.get("vehicleId").toString());
			response.setCustomerName(map.get("customerName")==null?"":map.get("customerName").toString());
			response.setInsurerName(map.get("insurerName")==null?"":map.get("insurerName").toString());
			response.setPaymentDate(map.get("paymentDate")==null?"":map.get("paymentDate").toString());
			response.setDateofIssue(map.get("paymentDate")==null?"":map.get("paymentDate").toString());
			response.setStartDate(map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
			response.setEndDate(map.get("expiryDate")==null?"":map.get("expiryDate").toString());
			response.setCovernoteNo(map.get("covernoteNo")==null?"":map.get("covernoteNo").toString());
			response.setStickerNumber(map.get("stickerNumber")==null?"":map.get("stickerNumber").toString());
			response.setRegistrationNumber(map.get("registrationNumber")==null?"":map.get("registrationNumber").toString());
			response.setVehicleTypeDesc(map.get("vehicleTypeDesc")==null?"":map.get("vehicleTypeDesc").toString());
			response.setModelType(map.get("modelType")==null?"":map.get("modelType").toString());
			response.setColorDesc(map.get("colorDesc")==null?"":map.get("colorDesc").toString());
			response.setCubicCapacity(map.get("cubicCapacity")==null?"":map.get("cubicCapacity").toString());
			response.setVehicleMakeDesc(map.get("vehicleMakeDesc")==null?"":map.get("vehicleMakeDesc").toString());
			response.setChassisNumber(map.get("chassisNumber")==null?"":map.get("chassisNumber").toString());
			response.setSeatingCapacity(map.get("seatingCapacity")==null?"":map.get("seatingCapacity").toString());
			response.setEngineNumber(map.get("engineNumber")==null?"":map.get("engineNumber").toString());
			response.setFuelType(map.get("fuelType")==null?"":map.get("fuelType").toString());
			response.setPolicyTypeDesc(map.get("policyTypeDesc")==null?"":map.get("policyTypeDesc").toString());
			response.setManufactureYear(map.get("manufactureYear")==null?"":map.get("manufactureYear").toString());
			response.setAgentMobile(map.get("agentMobile")==null?"":map.get("agentMobile").toString());
			response.setMotorUsageDesc(map.get("motorUsageDesc")==null?"":map.get("motorUsageDesc").toString());
			response.setCompanyName(map.get("companyName")==null?"":map.get("companyName").toString());
			response.setBranchName(map.get("branchName")==null?"":map.get("branchName").toString());
			response.setCurrency(map.get("currency")==null?"":map.get("currency").toString());
			response.setSectionName(map.get("sectionName")==null?"":map.get("sectionName").toString());
			response.setModelNumber(map.get("vehcileModel")==null?"":map.get("vehcileModel").toString());
			response.setPremium(map.get("premium")==null?"":map.get("premium").toString());
			response.setVatPremium(map.get("vatPremium")==null?"":map.get("vatPremium").toString());
			response.setOverallPremium(map.get("overallPremium")==null?"":map.get("overallPremium").toString());
		}
  }catch(Exception e) {
	  log.info("Error in getMotorCoverNote ==> "+e.getMessage());
	  e.printStackTrace();
  }
  	log.info("Exit into getMotorCoverNote");
		return response;
	}

	public TaxInvoiceRes getTaxInvoiceRes(String policyNo) {
		log.info("Enter into getTaxInvoiceRes.\nArgument ==> PolicyNo :"+policyNo);
		TaxInvoiceRes response = new TaxInvoiceRes();
	try {
		List<TaxDataSetOneRes> dataset1Res = new ArrayList<TaxDataSetOneRes>();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
		Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
		Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
		Root<PaymentDetail> pdRoot = cq.from(PaymentDetail.class);
		
		Subquery<Integer> SubcnAmd = cq.subquery(Integer.class);
		Root<CountryMaster> cnAmd = SubcnAmd.from(CountryMaster.class);
		SubcnAmd.select(cb.max(cnAmd.get("amendId"))).where(cb.equal(cnAmd.get("countryId"), piRoot.get("nationality")),
				cb.equal(cnAmd.get("companyId"), hpmRoot.get("companyId")),
				cb.equal(cnAmd.get("status"), "Y"));
		
		Subquery<String> countryName = cq.subquery(String.class);
		Root<CountryMaster> Subcn = countryName.from(CountryMaster.class);
		countryName.select(Subcn.get("countryName")).where(cb.equal(Subcn.get("countryId"), piRoot.get("nationality")),
				cb.equal(Subcn.get("companyId"), hpmRoot.get("companyId")),
				cb.equal(Subcn.get("status"), "Y"),
				cb.equal(Subcn.get("amendId"), SubcnAmd));
				
		Subquery<String> vrnNumber = cq.subquery(String.class);
		Root<InsuranceCompanyMaster> Subvrn = vrnNumber.from(InsuranceCompanyMaster.class);
		vrnNumber.select(Subvrn.get("vrnNumber")).where(cb.equal(Subvrn.get("companyId"), hpmRoot.get("companyId")),
				cb.between(cb.literal(new Date()) , Subvrn.get("effectiveDateStart"), Subvrn.get("effectiveDateEnd")));
		
		Subquery<String> tinNumber = cq.subquery(String.class);
		Root<InsuranceCompanyMaster> Subtin = tinNumber.from(InsuranceCompanyMaster.class);
		tinNumber.select(Subtin.get("tinNumber")).where(cb.equal(Subtin.get("companyId"), hpmRoot.get("companyId")),
				cb.between(cb.literal(new Date()), Subtin.get("effectiveDateStart"), Subtin.get("effectiveDateEnd")));
		
		Subquery<String> brokerName = cq.subquery(String.class);
		Root<LoginUserInfo> SubBn = brokerName.from(LoginUserInfo.class);
		brokerName.select(SubBn.get("userName")).where(cb.equal(SubBn.get("loginId"), hpmRoot.get("loginId")));
		
		Subquery<String> currencyId = cq.subquery(String.class);
		Root<InsuranceCompanyMaster> SubCi = currencyId.from(InsuranceCompanyMaster.class);
		currencyId.select(SubCi.get("currencyId")).where(cb.equal(hpmRoot.get("companyId"), SubCi.get("companyId")));
		
		Subquery<BigDecimal> sumInsured = cq.subquery(BigDecimal.class);
		Root<PolicyCoverData> SubSi = sumInsured.from(PolicyCoverData.class);
		sumInsured.select(cb.sum(SubSi.get("sumInsured"))).where(cb.equal(SubSi.get("quoteNo"), hpmRoot.get("quoteNo")),
				cb.equal(SubSi.get("discLoadId"), "0"),cb.equal(SubSi.get("taxId"), "0"),cb.equal(SubSi.get("dependentCoverYn"), "N"));
		
		cq.multiselect(luiRoot.get("userName").alias("userName"),hpmRoot.get("approvedBy").alias("approvedBy"),hpmRoot.get("agencyCode").alias("agencyCode"),
				cb.concat(piRoot.get("titleDesc"), cb.concat(".", piRoot.get("clientName"))).alias("customerName"),cb.concat(piRoot.get("address1"), cb.concat(",", cb.concat(cb.coalesce(piRoot.get("pinCode"), ""),
				cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("pinCode")), "").when(cb.equal(piRoot.get("pinCode"), ""), "").otherwise(",").as(String.class), cb.concat(piRoot.get("stateName"), cb.concat(",", cb.concat(piRoot.get("cityName"),
						cb.concat(",", countryName)))))))).alias("address"),
				piRoot.get("vrTinNo").alias("vrTinNo"),piRoot.get("idTypeDesc").alias("identificationName"),piRoot.get("idNumber").alias("identificationNo"),hpmRoot.get("customerCode").alias("intermediaryRefNo"),
				hpmRoot.get("policyNo").alias("policyNo"),hpmRoot.get("quoteNo").alias("quoteNo"),hpmRoot.get("inceptionDate").alias("inceptionDate"),
				hpmRoot.get("expiryDate").alias("expiryDate"),hpmRoot.get("currency").alias("currency"),hpmRoot.get("debitNoteNo").alias("debitNoteNo"),
				vrnNumber.alias("vrnNumber"),tinNumber.alias("tinNumber"),cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")),hpmRoot.get("customerName"))
					.otherwise(brokerName).alias("brokerName"),
				cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(currencyId), hpmRoot.get("premiumLc")).otherwise(hpmRoot.get("premiumFc")).alias("premium"),
				cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(currencyId), hpmRoot.get("vatPremiumLc")).otherwise(hpmRoot.get("vatPremiumFc")).alias("vatPremium"),
				cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(currencyId), hpmRoot.get("overallPremiumLc")).otherwise(hpmRoot.get("overallPremiumFc")).alias("overAllPremium"),
				hpmRoot.get("vatPercent").alias("vatPercent"),pdRoot.get("bankName").alias("bankName"),pdRoot.get("accountNumber").alias("accountNumber"),sumInsured.alias("totSumInsured"))
		.where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId")),
				cb.equal(pdRoot.get("quoteNo"), hpmRoot.get("quoteNo")),
				cb.equal(hpmRoot.get("loginId"), luiRoot.get("loginId")),
				cb.equal(hpmRoot.get("policyNo"), policyNo));
		
		List<Tuple> list = em.createQuery(cq).getResultList();
		if(!CollectionUtils.isEmpty(list)) {
			Tuple map = list.get(0);
			CriteriaBuilder cb1 = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq1 = cb1.createQuery(Tuple.class);
			Root<MotorDataDetails> mddRoot = cq1.from(MotorDataDetails.class);
			cq1.multiselect(mddRoot.get("registrationNumber").alias("registrationNumber"),
					mddRoot.get("motorCategoryDesc").alias("motorCategoryDesc")).where(cb.equal(mddRoot.get("quoteNo"), map.get("quoteNo")));
			List<Tuple> dataset1 = em.createQuery(cq1).getResultList();
			dataset1.forEach(k -> {
				TaxDataSetOneRes p = TaxDataSetOneRes.builder()
					.registrationNumber(k.get("registrationNumber")==null?"":k.get("registrationNumber").toString())
					.motorCategoryDesc(k.get("motorCategoryDesc")==null?"":k.get("motorCategoryDesc").toString())
					.build();
				dataset1Res.add(p);
			});
			response.setUserName(map.get("userName")==null?"":map.get("userName").toString());
			response.setApprovedBy(map.get("approvedBy")==null?"":map.get("approvedBy").toString());
			response.setAgencyCode(map.get("agencyCode")==null?"":map.get("agencyCode").toString());
			response.setCustomerName(map.get("customerName")==null?"":map.get("customerName").toString());
			response.setAddress(map.get("address")==null?"":map.get("address").toString());
			response.setVrTinNo(map.get("vrTinNo")==null?"":map.get("vrTinNo").toString());
			response.setIdentificationName(map.get("identificationName")==null?"":map.get("identificationName").toString());
			response.setIdentificationNo(map.get("identificationNo")==null?"":map.get("identificationNo").toString());
			response.setPolicyNo(map.get("policyNo")==null?"":map.get("policyNo").toString());
			response.setInceptionDate(map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
			response.setQuoteNo(map.get("quoteNo")==null?"":map.get("quoteNo").toString());
			response.setExpiryDate(map.get("expiryDate")==null?"":map.get("expiryDate").toString());
			response.setCurrency(map.get("currency")==null?"":map.get("currency").toString());
			response.setDebitNoteNo(map.get("debitNoteNo")==null?"":map.get("debitNoteNo").toString());
			response.setVrnNumber(map.get("vrnNumber")==null?"":map.get("vrnNumber").toString());
			response.setTinNumber(map.get("tinNumber")==null?"":map.get("tinNumber").toString());
			response.setBrokerName(map.get("brokerName")==null?"":map.get("brokerName").toString());
			response.setPremium(map.get("premium")==null?"":map.get("premium").toString());
			response.setVatPremium(map.get("vatPremium")==null?"":map.get("vatPremium").toString());
			response.setVatPercent(map.get("vatPercent")==null?"":map.get("vatPercent").toString());
			response.setOverAllPremium(map.get("overAllPremium")==null?"":map.get("overAllPremium").toString());
			response.setTotSumInsured(map.get("totSumInsured")==null?"":map.get("totSumInsured").toString());
			response.setIntermediaryRefNo(map.get("intermediaryRefNo")==null?"":map.get("intermediaryRefNo").toString());
			response.setDataset1List(dataset1Res);
		}
	}catch(Exception e) {
		log.info("Error in getTaxInvoiceRes ==> "+e.getMessage());
		e.printStackTrace();
	}
	log.info("Exit into getTaxInvoiceRes");
			return response;
			
	}
	
	public CreditNoteRes getCreditNoteRes(String policyNo) {
		log.info("Enter into getCreditNoteRes.\nArgument ==> PolicyNo :"+policyNo);
		CreditNoteRes response = new CreditNoteRes();
	try {
		List<CreditDataSetOne> DataSetOneRes = new ArrayList<CreditDataSetOne>();
		List<CreditDataSetTwo> DataSetTwoRes = new ArrayList<CreditDataSetTwo>();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
		Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
		Root<InsuranceCompanyMaster> icmRoot = cq.from(InsuranceCompanyMaster.class);
		Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
		
		Subquery<Integer> countryNameAmd = cq.subquery(Integer.class);
		Root<CountryMaster> SubCnAd = countryNameAmd.from(CountryMaster.class);
		countryNameAmd.select(cb.max(SubCnAd.get("amendId"))).where(cb.equal(SubCnAd.get("countryId"), piRoot.get("nationality")),
				cb.equal(SubCnAd.get("companyId"), hpmRoot.get("companyId")),cb.equal(SubCnAd.get("status"), "Y"));
		
		Subquery<String> countryName = cq.subquery(String.class);
		Root<CountryMaster> SubCm = countryName.from(CountryMaster.class);
		countryName.select(SubCm.get("countryName")).where(cb.equal(SubCm.get("countryId"), piRoot.get("nationality")),
					cb.equal(SubCm.get("companyId"), hpmRoot.get("companyId")),cb.equal(SubCm.get("status"), "Y"),cb.equal(SubCm.get("amendId"), countryNameAmd));
		
		Subquery<Integer> icmAmd = cq.subquery(Integer.class);
		Root<InsuranceCompanyMaster> SubIcAm = icmAmd.from(InsuranceCompanyMaster.class);
		icmAmd.select(cb.max(SubIcAm.get("amendId"))).where(cb.equal(SubIcAm.get("companyId"), icmRoot.get("companyId")));
		
		cq.multiselect(cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")), hpmRoot.get("customerName"))
				.otherwise(luiRoot.get("userName")).alias("brokerName"),
			cb.concat(piRoot.get("titleDesc"), cb.concat(".", piRoot.get("clientName"))).alias("customerName"),
			cb.concat(piRoot.get("address1"), cb.concat(",", cb.concat(cb.coalesce(piRoot.get("pinCode"), ""), cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("pinCode")), "")
					.when(cb.equal(piRoot.get("pinCode"), ""), "").otherwise(",").as(String.class), cb.concat(piRoot.get("stateName"), 
					cb.concat(",", cb.concat(piRoot.get("cityName"), cb.concat(",", countryName)))))))).alias("address"),
			hpmRoot.get("branchName").alias("branchName"),hpmRoot.get("creditNo").alias("creditNo"),hpmRoot.get("currency").alias("currency"),
			hpmRoot.get("productName").alias("productName"),cb.selectCase().when(cb.equal(hpmRoot.get("endtCount"), "0"), "NEW BUSINESS").otherwise("ENDORSEMENT").alias("business"),
			cb.selectCase().when(cb.isNull(hpmRoot.get("originalPolicyNo")), hpmRoot.get("policyNo")).otherwise(hpmRoot.get("originalPolicyNo")).alias("policyNo"),
			cb.selectCase().when(cb.isNotNull(hpmRoot.get("originalPolicyNo")), hpmRoot.get("policyNo")).alias("endorsementNo"),hpmRoot.get("endtTypeId").alias("endtTypeId"),
			hpmRoot.get("endtTypeDesc").alias("endtTypeDesc"),hpmRoot.get("endorsementRemarks").alias("endorsementRemarks"),hpmRoot.get("inceptionDate").alias("inceptionDate"),
			hpmRoot.get("expiryDate").alias("expiryDate"),hpmRoot.get("agencyCode").alias("agencyCode"),hpmRoot.get("customerId").alias("customerId"),hpmRoot.get("approvedBy").alias("approvedBy"),
			cb.selectCase().when(cb.equal(hpmRoot.get("endtCount"), "0"), hpmRoot.get("commission")).when(cb.isNotNull(hpmRoot.get("creditNo")), hpmRoot.get("commission")).alias("premium"),
			cb.selectCase().when(cb.equal(hpmRoot.get("endtCount"), "0"), cb.quot(cb.prod(hpmRoot.get("commission"), hpmRoot.get("vatPercent")), 100)).when(cb.isNotNull(hpmRoot.get("creditNo")), 
					cb.quot(cb.prod(hpmRoot.get("commission"), hpmRoot.get("vatPercent")), 100)).alias("vatPremiumFc"),
			cb.selectCase().when(cb.equal(hpmRoot.get("endtCount"), "0"), cb.sum(hpmRoot.get("commission"), cb.quot(cb.prod(hpmRoot.get("commission"), hpmRoot.get("vatPercent")), 100)))
				.when(cb.isNotNull(hpmRoot.get("creditNo")), cb.sum(hpmRoot.get("commission"), cb.quot(cb.prod(hpmRoot.get("commission"), hpmRoot.get("vatPercent")), 100))).alias("overAllPremiumFc"),
			hpmRoot.get("vatPercent").alias("vatPercent"),hpmRoot.get("quoteNo").alias("quoteNo"))
		.where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId")),cb.equal(hpmRoot.get("currency"), icmRoot.get("currencyId")),
				cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")),cb.equal(hpmRoot.get("loginId"), luiRoot.get("loginId")),
				cb.equal(icmRoot.get("amendId"), icmAmd),cb.equal(hpmRoot.get("status"), "P"),cb.equal(hpmRoot.get("policyNo"), policyNo))
		.orderBy(cb.desc(hpmRoot.get("entryDate")));
		
		List<Tuple> list = em.createQuery(cq).getResultList();
		if(!CollectionUtils.isEmpty(list)) {
			Tuple map = list.get(0);
			CriteriaBuilder cb1 = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq1 = cb1.createQuery(Tuple.class);
			Root<SectionDataDetails> sddRoot = cq1.from(SectionDataDetails.class);
			cq1.multiselect(sddRoot.get("sectionDesc").alias("sectionDesc")).where(cb.equal(sddRoot.get("quoteNo"), map.get("quoteNo")));
			List<Tuple> SectionList = em.createQuery(cq1).getResultList();
			SectionList.forEach(k -> {
				CreditDataSetOne h = CreditDataSetOne.builder()
					.sectionDesc(k.get("sectionDesc")==null?"":k.get("sectionDesc").toString())
					.build();
				DataSetOneRes.add(h);
			});
			CriteriaBuilder cb2 = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq2 = cb2.createQuery(Tuple.class);
			Root<ProductSectionMaster> psmRoot = cq2.from(ProductSectionMaster.class);
			Root<SectionDataDetails> sddRoot1 = cq2.from(SectionDataDetails.class);
			
			Subquery<Integer> SubSdAm = cq.subquery(Integer.class);
			Root<ProductSectionMaster> SubpsmRoot = SubSdAm.from(ProductSectionMaster.class);
			SubSdAm.select(cb.max(SubpsmRoot.get("amendId"))).where(cb.equal(SubpsmRoot.get("productId"), psmRoot.get("productId")),
					cb.equal(SubpsmRoot.get("companyId"), psmRoot.get("companyId")),cb.equal(SubpsmRoot.get("sectionId"), psmRoot.get("sectionId")),
					cb.equal(SubpsmRoot.get("status"), "Y"));
			
			cq2.multiselect(psmRoot.get("coreAppCode").alias("coreAppCode"))
				.where(cb.equal(psmRoot.get("status"), "Y"),cb.equal(psmRoot.get("productId"), sddRoot1.get("productId")),
						cb.equal(psmRoot.get("companyId"), sddRoot1.get("companyId")),cb.equal(psmRoot.get("sectionId"), sddRoot1.get("sectionId")),
						cb.equal(sddRoot1.get("quoteNo"), map.get("quoteNo")),cb.equal(psmRoot.get("amendId"), SubSdAm)).distinct(true);
			List<Tuple> riskCodeList = em.createQuery(cq2).getResultList();
			riskCodeList.forEach(i -> {
				CreditDataSetTwo q = CreditDataSetTwo.builder()
					.coreAppCode(i.get("coreAppCode")==null?"":i.get("coreAppCode").toString())
					.build();
				DataSetTwoRes.add(q);
			});
			response.setBrokerName(map.get("brokerName")==null?"":map.get("brokerName").toString());
			response.setCustomerName(map.get("customerName")==null?"":map.get("customerName").toString());
			response.setAddress(map.get("address")==null?"":map.get("address").toString());
			response.setBranchName(map.get("branchName")==null?"":map.get("branchName").toString());
			response.setCreditNo(map.get("creditNo")==null?"":map.get("creditNo").toString());
			response.setCurrency(map.get("currency")==null?"":map.get("currency").toString());
			response.setProductName(map.get("productName")==null?"":map.get("productName").toString());
			response.setBusiness(map.get("business")==null?"":map.get("business").toString());
			response.setPolicyNo(map.get("policyNo")==null?"":map.get("policyNo").toString());
			response.setEndtTypeId(map.get("endtTypeId")==null?"":map.get("endtTypeId").toString());
			response.setEndtTypeDesc(map.get("endtTypeDesc")==null?"":map.get("endtTypeDesc").toString());
			response.setEndorsementRemarks(map.get("endorsementRemarks")==null?"":map.get("endorsementRemarks").toString());
			response.setEndorsementNo(map.get("endorsementNo")==null?"":map.get("endorsementNo").toString());
			response.setInceptionDate(map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
			response.setExpiryDate(map.get("expiryDate")==null?"":map.get("expiryDate").toString());
			response.setAgencyCode(map.get("agencyCode")==null?"":map.get("agencyCode").toString());
			response.setCustomerId(map.get("customerId")==null?"":map.get("customerId").toString());
			response.setApprovedBy(map.get("approvedBy")==null?"":map.get("approvedBy").toString());
			response.setPremium(map.get("premium")==null?"":map.get("premium").toString());
			response.setVatPremiumFc(map.get("vatPremiumFc")==null?"":map.get("vatPremiumFc").toString());
			response.setOverAllPremiumFc(map.get("overAllPremiumFc")==null?"":map.get("overAllPremiumFc").toString());
			response.setVatPercent(map.get("vatPercent")==null?"":map.get("vatPercent").toString());
			response.setQuoteNo(map.get("quoteNo")==null?"":map.get("quoteNo").toString());
			response.setSectionDescList(DataSetOneRes);
			response.setRiskCodeList(DataSetTwoRes);
		}
	}catch(Exception e) {
		log.info("Error in getCreditNoteRes ==>" + e.getMessage());
		e.printStackTrace();
	}
	log.info("Exit into getCreditNoteRes");
		return response;
	}

	public MotorPrivateRes getMotorPrivate(String policyNo) {
		log.info("Enter into getMotorPrivate.\nArgument ==> PolicyNo :"+policyNo);
		MotorPrivateRes response = new MotorPrivateRes();
	try {
		List<MotorPrivateVehicleDetails> vehicleDetailsRes = new ArrayList<>();
		List<MotorPrivateDriverDetails> driverDetailsRes = new ArrayList<>();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
		Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
		Root<CompanyProductMaster> cpmRoot = cq.from(CompanyProductMaster.class);
		Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
		Root<MotorDataDetails> mddRoot = cq.from(MotorDataDetails.class);
		
		Subquery<Integer> countryNameAmd = cq.subquery(Integer.class);
		Root<CountryMaster> SubCmAmd = countryNameAmd.from(CountryMaster.class);
		countryNameAmd.select(cb.max(SubCmAmd.get("amendId"))).where(cb.equal(SubCmAmd.get("countryId"), piRoot.get("nationality")),cb.equal(SubCmAmd.get("companyId"), hpmRoot.get("companyId")),
				cb.equal(SubCmAmd.get("status"), "Y"));
		
		Subquery<String> countryName = cq.subquery(String.class);
		Root<CountryMaster> SubCm = countryName.from(CountryMaster.class);
		countryName.select(SubCm.get("countryName")).where(cb.equal(SubCm.get("countryId"), piRoot.get("nationality")),
				cb.equal(SubCm.get("companyId"), hpmRoot.get("companyId")),cb.equal(SubCm.get("status"), "Y"),cb.equal(SubCm.get("amendId"), countryNameAmd));
		
		Subquery<Long> MotorCount = cq.subquery(Long.class);
		Root<MotorDataDetails> SubMCRoot = MotorCount.from(MotorDataDetails.class);
		MotorCount.select(cb.count(SubMCRoot)).where(cb.equal(SubMCRoot.get("policyNo"), hpmRoot.get("policyNo")));
		
		cq.multiselect(cpmRoot.get("companyId").alias("companyId"),cpmRoot.get("effectiveDateStart").alias("effectiveDateStart"),cpmRoot.get("effectiveDateEnd").alias("effectiveDateEnd"),
			hpmRoot.get("policyNo").alias("policyNo"),hpmRoot.get("quoteNo").alias("quoteNo"),cb.concat(piRoot.get("titleDesc"), cb.concat(".", piRoot.get("clientName"))).alias("customerName"),
			hpmRoot.get("debitNoteNo").alias("debitNoteNo"),cb.concat(piRoot.get("address1"), cb.concat(",", cb.concat(cb.coalesce(piRoot.get("pinCode"), ""), 
				cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("pinCode")), "").when(cb.equal(piRoot.get("pinCode"), ""), "").otherwise(",").as(String.class), cb.concat(piRoot.get("stateName"),
				cb.concat(",", cb.concat(piRoot.get("cityName"), cb.concat(",", countryName)))))))).alias("address"),hpmRoot.get("inceptionDate").alias("inceptionDate"),
			hpmRoot.get("expiryDate").alias("expiryDate"),hpmRoot.get("currency").alias("currency"),hpmRoot.get("stickerNumber").alias("stickerNumber"),
			mddRoot.get("insuranceTypeDesc").alias("insuranceTypeDesc"),cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")), hpmRoot.get("premiumLc"))
			.otherwise(hpmRoot.get("premiumFc")).alias("premium"),cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")), hpmRoot.get("vatPremiumLc"))
			.otherwise(hpmRoot.get("vatPremiumFc")).alias("vatPremium"),cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")), hpmRoot.get("overallPremiumLc"))
			.otherwise(hpmRoot.get("overallPremiumFc")).alias("totalPremium"),hpmRoot.get("branchName").alias("branchName"),hpmRoot.get("approvedBy").alias("approvedBy"),
			cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")), hpmRoot.get("customerName"))
			.otherwise(luiRoot.get("userName")).alias("userName"),MotorCount.alias("noOfVehicle"))
		.where(cb.equal(mddRoot.get("policyNo"), hpmRoot.get("policyNo")),cb.equal(piRoot.get("customerId"), hpmRoot.get("customerId")),cb.equal(hpmRoot.get("loginId"), luiRoot.get("loginId")),
				cb.equal(cpmRoot.get("companyId"), hpmRoot.get("companyId")),cb.equal(cpmRoot.get("status"), "Y"),cb.equal(hpmRoot.get("productId"), cpmRoot.get("productId")),
				cb.between(cb.literal(new Date()), cpmRoot.get("effectiveDateStart"), cpmRoot.get("effectiveDateEnd")),cb.equal(hpmRoot.get("policyNo"), policyNo)).distinct(true);
		
		List<Tuple> list = em.createQuery(cq).getResultList();
		if(!CollectionUtils.isEmpty(list)) {
			Tuple map = list.get(0);
			List<MotorDataDetails> vehicleDetails = motorRepo.findByQuoteNoOrderByVehicleIdAsc(map.get("quoteNo").toString());
			vehicleDetails.forEach(k -> {
				MotorPrivateVehicleDetails t = MotorPrivateVehicleDetails.builder()
					.vehicleId(k.getVehicleId()==null?"":k.getVehicleId().toString())
					.registrationNumber(k.getRegistrationNumber()==null?"":k.getRegistrationNumber().toString())
					.vehicleMake(k.getVehicleMake()==null?"":k.getVehicleMake().toString())
					.vehcileModel(k.getVehcileModel()==null?"":k.getVehcileModel().toString())
					.vehicleTypeDesc(k.getVehicleTypeDesc()==null?"":k.getVehicleTypeDesc().toString())
					.cubicCapacity(k.getCubicCapacity()==null?"":k.getCubicCapacity().toString())
					.manufactureYear(k.getManufactureYear()==null?"":k.getManufactureYear().toString())
					.seatingCapacity(k.getSeatingCapacity()==null?"":k.getSeatingCapacity().toString())
					.colorDesc(k.getColorDesc()==null?"":k.getColorDesc().toString())
					.policyTypeDesc(k.getPolicyTypeDesc()==null?"":k.getPolicyTypeDesc().toString())
					.windScreenSumInsuredLc(k.getWindScreenSumInsured()==null?"":k.getWindScreenSumInsured().toString())
					.sumInsured(k.getSumInsured()==null?"":k.getSumInsured().toString())
					.stickerNumber(map.get("stickerNumber")==null?"":map.get("stickerNumber").toString())
					.build();
				vehicleDetailsRes.add(t);
			});
			List<MotorDriverDetails> driverDetails = motordriverRepo.findByQuoteNo(map.get("quoteNo").toString());
			driverDetails.forEach(d -> {
					MotorPrivateDriverDetails y = MotorPrivateDriverDetails.builder()
							.driverId(d.getDriverId()==null?"":d.getDriverId().toString())
							.driverName(d.getDriverName()==null?"":d.getDriverName().toString())
							.driverTypeDesc(d.getDriverTypedesc()==null?"":d.getDriverTypedesc().toString())
							.driverDOB(d.getDriverDob()==null?"":LocalDateTime.parse(d.getDriverDob().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
							.iDNumber(d.getIdNumber()==null?"":d.getIdNumber().toString())
							.chassisNumber(vehicleDetails.get(0).getChassisNumber()==null?"":vehicleDetails.get(0).getChassisNumber().toString())
							.build();
						driverDetailsRes.add(y);
			});
			
			response.setCompanyId(map.get("companyId")==null?"":map.get("companyId").toString());
			response.setEffectiveDateStart(map.get("effectiveDateStart")==null?"":map.get("effectiveDateStart").toString());
			response.setEffectiveDateEnd(map.get("effectiveDateEnd")==null?"":map.get("effectiveDateEnd").toString());
			response.setPolicyNo(map.get("policyNo")==null?"":map.get("policyNo").toString());
			response.setQuoteNo(map.get("quoteNo")==null?"":map.get("quoteNo").toString());
			response.setCustomerName(map.get("customerName")==null?"":map.get("customerName").toString());
			response.setDebitNoteNo(map.get("debitNoteNo")==null?"":map.get("debitNoteNo").toString());
			response.setAddress(map.get("address")==null?"":map.get("address").toString());
			response.setInceptionDate(map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
			response.setExpiryDate(map.get("expiryDate")==null?"":map.get("expiryDate").toString());
			response.setRenewalDate(map.get("expiryDate")==null?"":RenewalDate(map.get("expiryDate").toString()));
			response.setCurrency(map.get("currency")==null?"":map.get("currency").toString());
			response.setStickerNumber(map.get("stickerNumber")==null?"":map.get("stickerNumber").toString());
			response.setInsuranceTypeDesc(map.get("insuranceTypeDesc")==null?"":map.get("insuranceTypeDesc").toString());
			response.setPremium(map.get("premium")==null?"":map.get("premium").toString());
			response.setVatPremium(map.get("vatPremium")==null?"":map.get("vatPremium").toString());
			response.setTotalPremium(map.get("totalPremium")==null?"":map.get("totalPremium").toString());
			response.setBranchName(map.get("branchName")==null?"":map.get("branchName").toString());
			response.setApprovedBy(map.get("approvedBy")==null?"":map.get("approvedBy").toString());
			response.setUserName(map.get("userName")==null?"":map.get("userName").toString());
			response.setNoOfVehicle(map.get("noOfVehicle")==null?"":map.get("noOfVehicle").toString());
			response.setVehicleDetails(vehicleDetailsRes);
			response.setDriverDetails(driverDetailsRes);
		}
	}catch(Exception e) {
		log.info("Error in getMotorPrivate ==>"+e.getMessage());
		e.printStackTrace();
	}
	log.info("Exit into getMotorPrivate");
		return response;
	}

	public TravelReportRes getTravelReport(String policyNo) {
		log.info("Enter into getTravelReport.\nArgument ==> PolicyNo :"+policyNo);
		TravelReportRes response = new TravelReportRes();
	try {
		List<TravelDataSetOneRes> travelDataSetOne = new ArrayList<TravelDataSetOneRes>();
		List<TravelDataSetTwoRes> travelDataSetTwo = new ArrayList<TravelDataSetTwoRes>();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
		Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
		Root<LoginMaster> lmRoot = cq.from(LoginMaster.class);
		
		Subquery<Integer> countryNameAmd = cq.subquery(Integer.class);
		Root<CountryMaster> SubCmAmd = countryNameAmd.from(CountryMaster.class);
		countryNameAmd.select(cb.max(SubCmAmd.get("amendId"))).where(cb.equal(SubCmAmd.get("countryId"), piRoot.get("nationality")),cb.equal(SubCmAmd.get("companyId"), hpmRoot.get("companyId")),
				cb.equal(SubCmAmd.get("status"), "Y"));
		
		Subquery<String> countryName = cq.subquery(String.class);
		Root<CountryMaster> SubCm = countryName.from(CountryMaster.class);
		countryName.select(SubCm.get("countryName")).where(cb.equal(SubCm.get("countryId"), piRoot.get("nationality")),
				cb.equal(SubCm.get("companyId"), hpmRoot.get("companyId")),cb.equal(SubCm.get("status"), "Y"),cb.equal(SubCm.get("amendId"), countryNameAmd));
		
		Subquery<String> currencyId =cq.subquery(String.class);
		Root<InsuranceCompanyMaster> icmRoot = currencyId.from(InsuranceCompanyMaster.class);
		currencyId.select(icmRoot.get("currencyId")).where(cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")));
		
		Subquery<Double> overAllPremiumLc = cq.subquery(Double.class);
		Root<TravelPassengerDetails> SuboverAllPremiumLcRoot = overAllPremiumLc.from(TravelPassengerDetails.class);
		overAllPremiumLc.select(cb.sum(SuboverAllPremiumLcRoot.get("overallPremiumLc")).as(Double.class))
			.where(cb.equal(SuboverAllPremiumLcRoot.get("quoteNo"), hpmRoot.get("quoteNo")));
		
		Subquery<Double> overAllPremiumFc = cq.subquery(Double.class);
		Root<TravelPassengerDetails> SuboverAllPremiumFcRoot = overAllPremiumFc.from(TravelPassengerDetails.class);
		overAllPremiumFc.select(cb.sum(SuboverAllPremiumFcRoot.get("overallPremiumFc")).as(Double.class))
			.where(cb.equal(SuboverAllPremiumFcRoot.get("quoteNo"), hpmRoot.get("quoteNo")));
		
		Subquery<Double> premiumLc = cq.subquery(Double.class);
		Root<TravelPassengerDetails> SubpremiumLcRoot = premiumLc.from(TravelPassengerDetails.class);
		premiumLc.select(cb.sum(SubpremiumLcRoot.get("actualPremiumLc")).as(Double.class))
			.where(cb.equal(SubpremiumLcRoot.get("quoteNo"), hpmRoot.get("quoteNo")));
		
		Subquery<Double> premiumFc = cq.subquery(Double.class);
		Root<TravelPassengerDetails> SubpremiumFcRoot = premiumFc.from(TravelPassengerDetails.class);
		premiumFc.select(cb.sum(SubpremiumFcRoot.get("actualPremiumFc")).as(Double.class))
			.where(cb.equal(SubpremiumFcRoot.get("quoteNo"), hpmRoot.get("quoteNo")));
		
		Subquery<Long> noOfPassanger = cq.subquery(Long.class);
		Root<TravelPassengerDetails> SubnoOfPassanger= noOfPassanger.from(TravelPassengerDetails.class);
		noOfPassanger.select(cb.count(SubnoOfPassanger)).where(cb.equal(SubnoOfPassanger.get("quoteNo"), hpmRoot.get("quoteNo")));
		
		cq.multiselect(hpmRoot.get("quoteNo").alias("quoteNo"),hpmRoot.get("policyNo").alias("policyNo"),cb.upper(cb.concat(piRoot.get("titleDesc"),
				cb.concat(".", piRoot.get("clientName")))).alias("customerName"),cb.concat(piRoot.get("address1"), cb.concat(cb.coalesce(piRoot.get("pinCode"), ""),
						cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("pinCode")), "").when(cb.equal(piRoot.get("pinCode"), ""), "").otherwise(",").as(String.class), cb.concat(piRoot.get("stateName"), 
								cb.concat(",", cb.concat(piRoot.get("cityName"), cb.concat(",", countryName))))))).alias("address"),
				piRoot.get("telephoneNo1").alias("telephoneNo1"),lmRoot.get("agencyCode").alias("agencyCode"),hpmRoot.get("inceptionDate").alias("inceptionDate"),hpmRoot.get("expiryDate").alias("expiryDate"),
				hpmRoot.get("currency").alias("currency"),cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(currencyId), overAllPremiumLc).otherwise(overAllPremiumFc).alias("overAllPremium"),
				cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(currencyId), premiumLc).otherwise(premiumFc).alias("premium"),
				noOfPassanger.alias("noOfPassanger"))
		.where(cb.equal(piRoot.get("customerId"), hpmRoot.get("customerId")),cb.equal(lmRoot.get("loginId"), hpmRoot.get("loginId")),
				cb.equal(hpmRoot.get("productId"), "4"),cb.equal(hpmRoot.get("status"), "P"),cb.equal(hpmRoot.get("policyNo"), policyNo));
		
		List<Tuple> list = em.createQuery(cq).getResultList();
		if(!CollectionUtils.isEmpty(list)) {
			Tuple map = list.get(0);
			CriteriaBuilder cb1 = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq1 = cb1.createQuery(Tuple.class);
			Root<TravelPassengerDetails> tpdRoot1 = cq1.from(TravelPassengerDetails.class);
			cq1.multiselect(cb.upper(tpdRoot1.get("passengerName")).alias("passengerName"),tpdRoot1.get("dob").alias("dob"),tpdRoot1.get("age").alias("age"),
					tpdRoot1.get("relationDesc").alias("relationDesc"),tpdRoot1.get("passportNo").alias("passportNo"),tpdRoot1.get("travelCoverDuration").alias("travelCoverDuration"))
			.where(cb.equal(tpdRoot1.get("quoteNo"), map.get("quoteNo"))).orderBy(cb.asc(tpdRoot1.get("passengerName")));
			
			List<Tuple> passangerDetails = em.createQuery(cq1).getResultList();
					
			for(int i=0;i<passangerDetails.size();i++) {
				TravelDataSetOneRes o = new TravelDataSetOneRes();
				o.setSno(String.valueOf(i+1));
				o.setPassengerName(passangerDetails.get(i).get("passengerName")==null?"":passangerDetails.get(i).get("passengerName").toString());
				o.setDob(passangerDetails.get(i).get("dob")==null?"":passangerDetails.get(i).get("dob").toString());
				o.setAge(passangerDetails.get(i).get("age")==null?"":passangerDetails.get(i).get("age").toString());
				o.setRelationDesc(passangerDetails.get(i).get("relationDesc")==null?"":passangerDetails.get(i).get("relationDesc").toString());
				o.setPassportNo(passangerDetails.get(i).get("passportNo")==null?"":passangerDetails.get(i).get("passportNo").toString());
				o.setTravelCoverDuration(passangerDetails.get(i).get("travelCoverDuration")==null?"":passangerDetails.get(i).get("travelCoverDuration").toString());
				travelDataSetOne.add(o);
			}
			
			CriteriaBuilder cb2 = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq2 = cb2.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot2 = cq2.from(HomePositionMaster.class);
			Root<TravelPassengerDetails> tpdRoot2 = cq2.from(TravelPassengerDetails.class);
			Root<ProductGroupMaster> pgmRoot2 = cq2.from(ProductGroupMaster.class);
			Root<PolicyCoverData> pcdRoot2 = cq2.from(PolicyCoverData.class);
			
			Subquery<Long> sumInsured = cq2.subquery(Long.class);
			Root<PolicyCoverData> SubsumInsured = sumInsured.from(PolicyCoverData.class);
			sumInsured.select(cb.sum(SubsumInsured.get("sumInsured"))).where(cb.equal(SubsumInsured.get("vehicleId"), tpdRoot2.get("travelId")),
					cb.equal(SubsumInsured.get("quoteNo"), hpmRoot2.get("quoteNo")));
			
			Subquery<String> currencyId2 = cq2.subquery(String.class);
			Root<InsuranceCompanyMaster> icmRoot2 = currencyId2.from(InsuranceCompanyMaster.class);
			currencyId2.select(icmRoot2.get("currencyId")).where(cb.equal(pcdRoot2.get("companyId"), icmRoot2.get("companyId")));
			
			cq2.multiselect(pgmRoot2.get("bandDesc").alias("bandDesc"),tpdRoot2.get("planTypeDesc").alias("planTypeDesc"),pcdRoot2.get("coverName").alias("coverName"),
					sumInsured.alias("sumInsured"),pcdRoot2.get("rate").alias("rate"),pcdRoot2.get("currency").alias("currency"),pcdRoot2.get("taxRate").alias("taxRate"),
				cb.selectCase().when(cb.in(pcdRoot2.get("currency")).value(currencyId2), pcdRoot2.get("premiumIncludedTaxLc")).otherwise(pcdRoot2.get("premiumIncludedTaxFc")).alias("premium"))
			.where(cb.equal(tpdRoot2.get("quoteNo"), hpmRoot2.get("quoteNo")),cb.equal(pgmRoot2.get("groupId"),tpdRoot2.get("groupId")),
					cb.equal(hpmRoot2.get("productId"), "4"),cb.equal(hpmRoot2.get("status"), "P"),cb.equal(hpmRoot2.get("quoteNo"), pcdRoot2.get("quoteNo")),cb.equal(pcdRoot2.get("vehicleId"), tpdRoot2.get("groupId")),
					cb.equal(pcdRoot2.get("discLoadId"), "0"),cb.equal(pcdRoot2.get("taxId"), "0"),cb.equal(hpmRoot2.get("policyNo"),policyNo)).distinct(true);
			
			List<Tuple> travelSubReport = em.createQuery(cq2).getResultList();
			travelSubReport.forEach(k -> {
				TravelDataSetTwoRes h = TravelDataSetTwoRes.builder()
					.bandDesc(k.get("bandDesc")==null?"":k.get("bandDesc").toString())
					.planTypeDesc(k.get("planTypeDesc")==null?"":k.get("planTypeDesc").toString())
					.coverName(k.get("coverName")==null?"":k.get("coverName").toString())
					.sumInsured(k.get("sumInsured")==null?"":k.get("sumInsured").toString())
					.taxRate(k.get("taxRate")==null?"":k.get("taxRate").toString())
					.rate(k.get("rate")==null?"":k.get("rate").toString())
					.currency(k.get("currency")==null?"":k.get("currency").toString())
					.premium(k.get("premium")==null?"":k.get("premium").toString())
					.build();
				travelDataSetTwo.add(h);
			});
			response.setQuoteNo(map.get("quoteNo")==null?"":map.get("quoteNo").toString());
			response.setPolicyNo(map.get("policyNo")==null?"":map.get("policyNo").toString());
			response.setCustomerName(map.get("customerName")==null?"":map.get("customerName").toString());
			response.setAddress(map.get("address")==null?"":map.get("address").toString());
			response.setTelephoneNo1(map.get("telephoneNo1")==null?"":map.get("telephoneNo1").toString());
			response.setAgencyCode(map.get("agencyCode")==null?"":map.get("agencyCode").toString());
			response.setInceptionDate(map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
			response.setExpiryDate(map.get("expiryDate")==null?"":map.get("expiryDate").toString());
			response.setCurrency(map.get("currency")==null?"":map.get("currency").toString());
			response.setOverAllPremium(map.get("overAllPremium")==null?"":map.get("overAllPremium").toString());
			response.setPremium(map.get("premium")==null?"":map.get("premium").toString());
			response.setNoOfPassanger(map.get("noOfPassanger")==null?"":map.get("noOfPassanger").toString());
			response.setQuoteNo(map.get("quoteNo")==null?"":map.get("quoteNo").toString());
			response.setPassangerDetails(travelDataSetOne);
			response.setTravelCoverDetails(travelDataSetTwo);
		}
	}catch(Exception e) {
		log.info("Error in getTravelReport ==>"+e.getMessage());
		e.printStackTrace();
	}
		log.info("Exit into getTravelReport");
		return response;
	}

	public Map<String, Object> getMotorEndorsementSchedule(String policyNo) {
		log.info("Enter into getMotorEndorsementSchedule.\nArgument ==> PolicyNo :"+policyNo);
		Map<String, Object> result = new HashMap<String,Object>();
	try {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
		Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
		Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
		
		cq.multiselect(cb.concat(piRoot.get("titleDesc"), cb.concat(".", piRoot.get("clientName"))).alias("customerName"),
			hpmRoot.get("policyNo").alias("EndorsementNo"),hpmRoot.get("originalPolicyNo").alias("originalPolicyNo"),hpmRoot.get("effectiveDate").alias("effectiveDate"),
			hpmRoot.get("expiryDate").alias("expiryDate"),hpmRoot.get("inceptionDate").alias("inceptionDate"),hpmRoot.get("currency").alias("currency"),
			hpmRoot.get("endtPremium").alias("endtPremium"),hpmRoot.get("endtTypeDesc").alias("endtTypeDesc"),hpmRoot.get("endorsementRemarks").alias("endorsementRemarks"),
			hpmRoot.get("companyName").alias("companyName"),hpmRoot.get("branchName").alias("branchName"),cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")),hpmRoot.get("customerName"))
			.otherwise(luiRoot.get("userName")).alias("userName"),hpmRoot.get("quoteNo").alias("quoteNo"))
		.where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId")),cb.equal(luiRoot.get("loginId"), hpmRoot.get("loginId")),
				cb.equal(hpmRoot.get("productId"), "5"),cb.equal(hpmRoot.get("status"), "P"),cb.equal(hpmRoot.get("policyNo"), policyNo))
		.orderBy(cb.asc(hpmRoot.get("entryDate")));
		
		List<Tuple> list = em.createQuery(cq).getResultList();
		if(!CollectionUtils.isEmpty(list)) {
			Tuple map = list.get(0);
			CriteriaBuilder cb1 = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq1 = cb1.createQuery(Tuple.class);
			Root<MotorDataDetails> mddRoot = cq1.from(MotorDataDetails.class);
			
			cq1.multiselect(mddRoot.get("insuranceClassDesc").alias("insuranceClassDesc"),mddRoot.get("registrationNumber").alias("registrationNumber"),
					mddRoot.get("chassisNumber").alias("chassisNumber"))
			.where(cb.equal(mddRoot.get("quoteNo"), map.get("quoteNo")));
			
			List<Tuple> vehicleInfo = em.createQuery(cq1).getResultList();
			List<Map<String,Object>> vehicleList = vehicleInfo.stream().map(k ->{
				LinkedHashMap<String, Object> vMap = new LinkedHashMap<String,Object>();
				vMap.put("InsuranceClassDesc", k.get("insuranceClassDesc")==null?"":k.get("insuranceClassDesc").toString());
				vMap.put("RegistrationNumber", k.get("registrationNumber")==null?"":k.get("registrationNumber").toString());
				vMap.put("ChassisNumber", k.get("chassisNumber")==null?"":k.get("chassisNumber").toString());
				return vMap;
			}).collect(Collectors.toList());
			
			result.put("customerName", map.get("customerName")==null?"":map.get("customerName").toString());
			result.put("EndorsementNo", map.get("EndorsementNo")==null?"":map.get("EndorsementNo").toString());
			result.put("originalPolicyNo", map.get("originalPolicyNo")==null?"":map.get("originalPolicyNo").toString());
			result.put("effectiveDate", map.get("effectiveDate")==null?"":map.get("effectiveDate").toString());
			result.put("expiryDate", map.get("expiryDate")==null?"":map.get("expiryDate").toString());
			result.put("inceptionDate", map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
			result.put("currency", map.get("currency")==null?"":map.get("currency").toString());
			result.put("endtPremium", map.get("endtPremium")==null?"":map.get("endtPremium").toString());
			result.put("endtTypeDesc", map.get("endtTypeDesc")==null?"":map.get("endtTypeDesc").toString());
			result.put("endorsementRemarks", map.get("endorsementRemarks")==null?"":map.get("endorsementRemarks").toString());
			result.put("companyName", map.get("companyName")==null?"":map.get("companyName").toString());
			result.put("branchName", map.get("branchName")==null?"":map.get("branchName").toString());
			result.put("userName", map.get("userName")==null?"":map.get("userName").toString());
			result.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
			result.put("vehicleList", vehicleList);
		}
	}catch(Exception e) {
		log.info("Error in getMotorEndorsementSchedule ==>"+e.getMessage());
		e.printStackTrace();
	}
		log.info("Exit into getMotorEndorsementSchedule");
		return result;
	}

	public Map<String, Object> getCyberInsurance(String policyNo) {
		log.info("Enter into getCyberInsurance.\nArgument ==> PolicyNo :"+policyNo);
		Map<String, Object> result = new HashMap<String,Object>();
	try {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
		Root<BranchMaster> bmRoot = cq.from(BranchMaster.class);
		Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
		Root<EserviceBuildingDetails> ebdRoot = cq.from(EserviceBuildingDetails.class);
		
		Subquery<Integer> countryNameAmd = cq.subquery(Integer.class);
		Root<CountryMaster> SubcountryAmd = countryNameAmd.from(CountryMaster.class);
		countryNameAmd.select(cb.max(SubcountryAmd.get("amendId"))).where(cb.equal(SubcountryAmd.get("countryId"), piRoot.get("nationality")),
				cb.equal(SubcountryAmd.get("companyId"), hpmRoot.get("companyId")),cb.equal(SubcountryAmd.get("status"), "Y"));
		
		Subquery<String> countryName = cq.subquery(String.class);
		Root<CountryMaster> cmRoot = countryName.from(CountryMaster.class);
		countryName.select(cmRoot.get("countryName")).where(cb.equal(cmRoot.get("countryId"), piRoot.get("nationality")),
				cb.equal(cmRoot.get("companyId"), hpmRoot.get("companyId")),cb.equal(cmRoot.get("status"), "Y"),cb.equal(cmRoot.get("amendId"), countryNameAmd));
		
		Subquery<String> currencyId = cq.subquery(String.class);
		Root<InsuranceCompanyMaster> icmRoot = currencyId.from(InsuranceCompanyMaster.class);
		currencyId.select(icmRoot.get("currencyId")).where(cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")));
		
		Subquery<Integer> bmAmd = cq.subquery(Integer.class);
		Root<BranchMaster> SubbmAnd = bmAmd.from(BranchMaster.class);
		bmAmd.select(cb.max(SubbmAnd.get("amendId"))).where(cb.equal(SubbmAnd.get("branchCode"), hpmRoot.get("branchCode")),cb.equal(SubbmAnd.get("status"), "Y"));
		
		cq.multiselect(hpmRoot.get("policyNo").alias("policyNo"),hpmRoot.get("quoteNo").alias("quoteNo"),bmRoot.get("branchName").alias("branchName"),
				hpmRoot.get("entryDate").alias("entryDate"),cb.concat(piRoot.get("titleDesc"), cb.concat(".", piRoot.get("clientName"))).alias("customerName"),
				cb.concat(piRoot.get("address1"), cb.concat(",", cb.concat(cb.coalesce(piRoot.get("pinCode"), ""), cb.concat(cb.selectCase()
					.when(cb.equal(piRoot.get("pinCode"), ""), "").when(cb.isNull(piRoot.get("pinCode")), "").otherwise(",").as(String.class), cb.concat(piRoot.get("stateName"),
							cb.concat(",", cb.concat(piRoot.get("cityName"), cb.concat(",", countryName)))))))).alias("address"),hpmRoot.get("inceptionDate").alias("inceptionDate"),
				hpmRoot.get("expiryDate").alias("expiryDate"),ebdRoot.get("occupationTypeDesc").alias("occupationTypeDesc"),cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(currencyId), hpmRoot.get("overallPremiumLc"))
				.otherwise(hpmRoot.get("overallPremiumFc")).alias("premium"))
		.where(cb.equal(hpmRoot.get("branchCode"), bmRoot.get("branchCode")),cb.equal(hpmRoot.get("companyId"), bmRoot.get("companyId")),
				cb.equal(piRoot.get("customerId"), hpmRoot.get("customerId")),cb.equal(hpmRoot.get("requestReferenceNo"), ebdRoot.get("requestReferenceNo")),
				cb.equal(bmRoot.get("status"), "Y"),cb.between(cb.literal(new Date()), bmRoot.get("effectiveDateStart"), bmRoot.get("effectiveDateEnd")),
				cb.equal(bmRoot.get("amendId"), bmAmd),cb.not(cb.in(ebdRoot.get("sectionId")).value("0")),cb.equal(hpmRoot.get("policyNo"), policyNo));
		
		List<Tuple> list = em.createQuery(cq).getResultList();
		if(!CollectionUtils.isEmpty(list)) {
			Tuple map = list.get(0);
			// SectionList
			CriteriaBuilder cb1 = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq1 = cb1.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot1 = cq1.from(HomePositionMaster.class);
			Root<PolicyCoverData> pcdRoot = cq1.from(PolicyCoverData.class);
			
			Subquery<BigDecimal> excessAmount = cq1.subquery(BigDecimal.class);
			Root<PolicyCoverData> SubexcessAmd = excessAmount.from(PolicyCoverData.class);
			excessAmount.select(SubexcessAmd.get("excessAmount")).where(cb.equal(SubexcessAmd.get("quoteNo"), hpmRoot1.get("quoteNo")),
					cb.equal(SubexcessAmd.get("discLoadId"), "0"),cb.equal(SubexcessAmd.get("taxId"), "0"),cb.equal(SubexcessAmd.get("coverId"), "5"));
			
			cq1.multiselect(pcdRoot.get("coverId").alias("coverId"),pcdRoot.get("coverName").alias("coverName"),pcdRoot.get("coverageLimit").alias("coverageLimit"),
					excessAmount.alias("excessAmount")).where(cb.equal(pcdRoot.get("quoteNo"), hpmRoot1.get("quoteNo")),cb.equal(hpmRoot1.get("policyNo"), map.get("policyNo")),
							cb.equal(pcdRoot.get("discLoadId"), "0"),cb.equal(pcdRoot.get("taxId"), "0"));

			List<Tuple> list1 = em.createQuery(cq1).getResultList();
			List<Map<String,Object>> sectionList = list1.stream().map(k ->{
				LinkedHashMap<String, Object> Smap = new LinkedHashMap<String,Object>();
				Smap.put("coverId", k.get("coverId")==null?"":k.get("coverId").toString());
				Smap.put("coverName", k.get("coverName")==null?"":k.get("coverName").toString());
				Smap.put("coverageLimit", k.get("coverageLimit")==null?"":k.get("coverageLimit").toString());
				Smap.put("excessAmount", k.get("excessAmount")==null?"":k.get("excessAmount").toString());
				return Smap;
			}).collect(Collectors.toList());
			
			// DeviceList
			List<ContentAndRisk> list2 = conAndRiskRepo.findByQuoteNo(map.get("quoteNo").toString());
			List<Map<String,Object>> deviceList = list2.stream().map(d -> {
				LinkedHashMap<String, Object> Dmap = new LinkedHashMap<String,Object>();
				Dmap.put("itemDesc", d.getItemDesc()==null?"":d.getItemDesc().toString());
				Dmap.put("makeAndModel", d.getMakeAndModel()==null?"":d.getMakeAndModel().toString());
				Dmap.put("manufactureYear", d.getManufactureYear()==null?"":d.getManufactureYear().toString());
				Dmap.put("serialNoDesc", d.getSerialNoDesc()==null?"":d.getSerialNoDesc().toString());
				return Dmap;
			}).collect(Collectors.toList());
			
			// CONDITIONS
			List<Map<String,Object>> conditionList = getConditionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),"");

			// EXCLUSION
			List<Map<String,Object>> exclusionList = getExclusionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),"");
			
			result.put("policyNo", map.get("policyNo")==null?"":map.get("policyNo").toString());
			result.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
			result.put("branchName", map.get("branchName")==null?"":map.get("branchName").toString());
			result.put("entryDate", map.get("entryDate")==null?"":map.get("entryDate").toString());
			result.put("customerName", map.get("customerName")==null?"":map.get("customerName").toString());
			result.put("address", map.get("address")==null?"":map.get("address").toString());
			result.put("inceptionDate", map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
			result.put("expiryDate", map.get("expiryDate")==null?"":map.get("expiryDate").toString());
			result.put("occupationTypeDesc", map.get("occupationTypeDesc")==null?"":map.get("occupationTypeDesc").toString());
			result.put("premium", map.get("premium")==null?"":map.get("premium").toString());
			result.put("sectionList", sectionList);
			result.put("deviceList", deviceList);
			result.put("conditionList", conditionList);
			result.put("exclusionList", exclusionList);
		}
	}catch(Exception e) {
		log.info("Error in getCyberInsurance ==> "+e.getMessage());
		e.printStackTrace();
	}
		log.info("Exit into getCyberInsurance");
		return result;
	}
	
	public Map<String,Object> getMotorBrokerQuotation(String QuoteNo){
		log.info("Enter into getMotorBrokerQuotation.\nArgument ==> "+QuoteNo);
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
			Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
			Root<InsuranceCompanyMaster> icmRoot = cq.from(InsuranceCompanyMaster.class);
			Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
			
			Subquery<Integer> countryNameAmd = cq.subquery(Integer.class);
			Root<CountryMaster> SubCnAd = countryNameAmd.from(CountryMaster.class);
			countryNameAmd.select(cb.max(SubCnAd.get("amendId"))).where(cb.equal(SubCnAd.get("countryId"), piRoot.get("nationality")),
					cb.equal(SubCnAd.get("companyId"), hpmRoot.get("companyId")),cb.equal(SubCnAd.get("status"), "Y"));
			
			Subquery<String> countryName = cq.subquery(String.class);
			Root<CountryMaster> SubCm = countryName.from(CountryMaster.class);
			countryName.select(SubCm.get("countryName")).where(cb.equal(SubCm.get("countryId"), piRoot.get("nationality")),
						cb.equal(SubCm.get("companyId"), hpmRoot.get("companyId")),cb.equal(SubCm.get("status"), "Y"),cb.equal(SubCm.get("amendId"), countryNameAmd));
			
			Subquery<Integer> icmAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> icmAmdRoot = icmAmd.from(InsuranceCompanyMaster.class);
			icmAmd.select(cb.max(icmAmdRoot.get("amendId"))).where(cb.equal(icmAmdRoot.get("companyId"), icmRoot.get("companyId")));
			
			cq.multiselect(cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Agent","Premia Direct")), hpmRoot.get("customerName"))
					.otherwise(luiRoot.get("userName")).alias("userName"),hpmRoot.get("agencyCode").alias("agencyCode"),hpmRoot.get("requestReferenceNo").alias("requestReferenceNo"),
					hpmRoot.get("quoteNo").alias("quoteNo"),hpmRoot.get("policyNo").alias("policyNo"),hpmRoot.get("originalPolicyNo").alias("originalPolicyNo"),hpmRoot.get("companyId").alias("companyId"),
					hpmRoot.get("companyName").alias("companyName"),hpmRoot.get("currency").alias("currency"),hpmRoot.get("vatPercent").alias("vatPercent"),
					cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")), hpmRoot.get("premiumLc")).otherwise(hpmRoot.get("premiumFc")).alias("premium"),
					cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")), hpmRoot.get("vatPremiumLc")).otherwise(hpmRoot.get("vatPremiumFc")).alias("vatPremium"),
					cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")), hpmRoot.get("overallPremiumLc")).otherwise(hpmRoot.get("overallPremiumFc")).alias("overAllPremium"),
					hpmRoot.get("commissionPercentage").alias("commissionPercentage"),hpmRoot.get("commission").alias("commission"),hpmRoot.get("branchName").alias("branchName"),hpmRoot.get("inceptionDate").alias("inceptionDate"),
					hpmRoot.get("expiryDate").alias("expiryDate"),hpmRoot.get("paymentType").alias("paymentType"),cb.concat(piRoot.get("titleDesc"), cb.concat(".", piRoot.get("clientName"))).alias("customerName"),
					cb.concat(piRoot.get("address1"), cb.concat(",", cb.concat(cb.coalesce(piRoot.get("pinCode"), ""),cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("pinCode")), "").when(cb.equal(piRoot.get("pinCode"), ""), "")
					.otherwise(",").as(String.class), cb.concat(piRoot.get("stateName"), cb.concat(",", cb.concat(piRoot.get("cityName"),cb.concat(",", countryName)))))))).alias("address"),
					piRoot.get("vrTinNo").alias("vrTinNo"),piRoot.get("email1").alias("email1"),piRoot.get("mobileNo1").alias("mobileNo1"))
			.where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId")),cb.equal(hpmRoot.get("currency"), icmRoot.get("currencyId")),cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")),
					cb.equal(luiRoot.get("loginId"), hpmRoot.get("loginId")),cb.equal(icmRoot.get("amendId"), icmAmd),cb.equal(hpmRoot.get("productId"), "5"),cb.equal(hpmRoot.get("quoteNo"), QuoteNo))
			.orderBy(cb.desc(hpmRoot.get("entryDate")));
			
			List<Tuple> list = em.createQuery(cq).getResultList();
			if(!CollectionUtils.isEmpty(list)) {
				Tuple map = list.get(0);
				List<MotorDataDetails> list1 = motorRepo.findByQuoteNoOrderByVehicleIdAsc(map.get("quoteNo").toString());
				List<Map<String,Object>> vehicleList = list1.stream().map(k -> {
					LinkedHashMap<String,Object> m = new LinkedHashMap<String,Object>();
					m.put("policyTypeDesc", k.getPolicyTypeDesc()==null?"":StringUtils.capitalize(k.getPolicyTypeDesc()));
					m.put("registrationNumber", k.getRegistrationNumber()==null?"":k.getRegistrationNumber());
					m.put("vehicleMakeDesc", k.getVehicleMakeDesc()==null?"":StringUtils.capitalize(k.getVehicleMakeDesc()));
					m.put("vehicleTypeDesc", k.getVehicleTypeDesc()==null?"":StringUtils.capitalize(k.getVehicleTypeDesc()));
					m.put("chassisNumber", k.getChassisNumber()==null?"":k.getChassisNumber());
					m.put("colorDesc", k.getColorDesc()==null?"":StringUtils.capitalize(k.getColorDesc()));
					m.put("manufactureYear", k.getManufactureYear());
					m.put("engineNumber", k.getEngineNumber()==null?"":k.getEngineNumber());
					m.put("vehcileModelDesc", k.getVehcileModelDesc()==null?"":StringUtils.capitalize(k.getVehcileModelDesc()));
					m.put("sumInsured", k.getSumInsured());
					return m;
				}).collect(Collectors.toList());
				
				result.put("userName", map.get("userName")==null?"":map.get("userName").toString());
				result.put("agencyCode", map.get("agencyCode")==null?"":map.get("agencyCode").toString());
				result.put("requestReferenceNo", map.get("requestReferenceNo")==null?"":map.get("requestReferenceNo").toString());
				result.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
				result.put("policyNo", map.get("policyNo")==null?"":map.get("policyNo").toString());
				result.put("originalPolicyNo", map.get("originalPolicyNo")==null?"":map.get("originalPolicyNo").toString());
				result.put("companyId", map.get("companyId")==null?"":map.get("companyId").toString());
				result.put("companyName", map.get("companyName")==null?"":map.get("companyName").toString());
				result.put("currency", map.get("currency")==null?"":map.get("currency").toString());
				result.put("vatPercent", map.get("vatPercent")==null?"":map.get("vatPercent").toString());
				result.put("premium", map.get("premium")==null?"":map.get("premium").toString());
				result.put("vatPremium", map.get("vatPremium")==null?"":map.get("vatPremium").toString());
				result.put("overAllPremium", map.get("overAllPremium")==null?"":map.get("overAllPremium").toString());
				result.put("commissionPercentage", map.get("commissionPercentage")==null?"":map.get("commissionPercentage").toString());
				result.put("commission", map.get("commission")==null?"":map.get("commission").toString());
				result.put("branchName", map.get("branchName")==null?"":map.get("branchName").toString());
				result.put("inceptionDate", map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
				result.put("address", map.get("address")==null?"":map.get("address").toString());
				result.put("email1", map.get("email1")==null?"":map.get("email1").toString());
				result.put("mobileNo1", map.get("mobileNo1")==null?"":map.get("mobileNo1").toString());
				result.put("customerName", map.get("customerName")==null?"":map.get("customerName").toString());
				result.put("vrTinNo", map.get("vrTinNo")==null?"":map.get("vrTinNo").toString());
				result.put("expiryDate", map.get("expiryDate")==null?"":map.get("expiryDate").toString());
				result.put("vehicleList", vehicleList);
			}
				
		}catch(Exception e) {
			log.info("Error in getMotorBrokerQuotation ==> "+e.getMessage());
			e.printStackTrace();
		}
		log.info("Exit into getMotorBrokerQuotation");
		return result;
	}
	
	public Map<String,Object> getEwaySchedule(String QuoteNo){
		log.info("Enter into EwaySchedule.\nArgument ==> "+QuoteNo);
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
			Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
			Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
			Root<InsuranceCompanyMaster> icmRoot = cq.from(InsuranceCompanyMaster.class);
			Root<LoginBranchMaster> lbmRoot= cq.from(LoginBranchMaster.class);
			
			Subquery<Integer> cmAmd = cq.subquery(Integer.class);
			Root<CountryMaster> cmAmdRoot = cmAmd.from(CountryMaster.class);
			cmAmd.select(cb.max(cmAmdRoot.get("amendId"))).where(cb.equal(cmAmdRoot.get("countryId"), piRoot.get("nationality")),cb.equal(cmAmdRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(cmAmdRoot.get("status"), "Y"));
			
			Subquery<String> countryName = cq.subquery(String.class);
			Root<CountryMaster> ScmRoot = countryName.from(CountryMaster.class);
			countryName.select(ScmRoot.get("countryName")).where(cb.equal(ScmRoot.get("countryId"), piRoot.get("nationality")),cb.equal(ScmRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(ScmRoot.get("status"), "Y"),cb.equal(ScmRoot.get("amendId"), cmAmd));
			
			Subquery<Integer> icmAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> icmAmdRoot = icmAmd.from(InsuranceCompanyMaster.class);
			icmAmd.select(cb.max(icmAmdRoot.get("amendId"))).where(cb.equal(hpmRoot.get("companyId"), icmAmdRoot.get("companyId")),cb.equal(icmAmdRoot.get("status"), "Y"),
					cb.between(cb.literal(new Date()), icmAmdRoot.get("effectiveDateStart"), icmAmdRoot.get("effectiveDateEnd")));
			
			cq.multiselect(hpmRoot.get("policyNo").alias("policyNo"),hpmRoot.get("quoteNo").alias("quoteNo"),cb.concat(piRoot.get("titleDesc"), cb.concat(".", piRoot.get("clientName"))).alias("customerName"),
					cb.concat(piRoot.get("address1"), cb.concat(",", cb.concat(cb.coalesce(piRoot.get("pinCode"), ""), cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("pinCode")), "")
							.when(cb.equal(piRoot.get("pinCode"), ""), "").otherwise(",").as(String.class), cb.concat(piRoot.get("stateName"), cb.concat(",", cb.concat(piRoot.get("cityName"),
									cb.concat(",", countryName)))))))).alias("address"),
					hpmRoot.get("inceptionDate").alias("inceptionDate"),hpmRoot.get("expiryDate").alias("expiryDate"),hpmRoot.get("branchName").alias("branchName"),hpmRoot.get("brokerBranchName").alias("brokerBranchName"),
					hpmRoot.get("productName").alias("productName"),piRoot.get("stateName").alias("stateName"),piRoot.get("cityName").alias("cityName"),cb.concat(piRoot.get("mobileCodeDesc1"), cb.concat("-", piRoot.get("mobileNo1"))).alias("mobileNo"),
					piRoot.get("customerId").alias("customerId"),cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Agent","Premia Direct","Premia Broker")), hpmRoot.get("customerName"))
					.otherwise(luiRoot.get("userName")).alias("brokerName"),luiRoot.get("coreAppBrokerCode").alias("coreAppBrokerCode"),hpmRoot.get("currency").alias("currency"),hpmRoot.get("vatPercent").alias("vatPercent"),
					cb.selectCase().when(cb.equal(icmRoot.get("currencyId"), hpmRoot.get("currency")), hpmRoot.get("premiumLc")).otherwise(hpmRoot.get("premiumFc")).alias("premium"),
					cb.selectCase().when(cb.equal(icmRoot.get("currencyId"), hpmRoot.get("currency")), hpmRoot.get("vatPremiumLc")).otherwise(hpmRoot.get("vatPremiumFc")).alias("vatPremium"),
					cb.selectCase().when(cb.equal(icmRoot.get("currencyId"), hpmRoot.get("currency")), hpmRoot.get("overallPremiumLc")).otherwise(hpmRoot.get("overallPremiumFc")).alias("totalPremium"),
					icmRoot.get("signature").alias("signature"),lbmRoot.get("branchName").alias("place"))
			.where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId")),cb.equal(hpmRoot.get("agencyCode"), luiRoot.get("agencyCode")),cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")),
					cb.equal(hpmRoot.get("loginId"), lbmRoot.get("loginId")),cb.equal(hpmRoot.get("companyId"), lbmRoot.get("companyId")),cb.equal(hpmRoot.get("branchCode"), lbmRoot.get("branchCode")),cb.equal(lbmRoot.get("status"), "Y"),
					cb.equal(icmRoot.get("status"), "Y"),cb.between(cb.literal(new Date()), icmRoot.get("effectiveDateStart"), icmRoot.get("effectiveDateEnd")),cb.equal(icmRoot.get("amendId"), icmAmd),cb.equal(hpmRoot.get("quoteNo"), QuoteNo));
			List<Tuple> list = em.createQuery(cq).getResultList();
			if(!CollectionUtils.isEmpty(list)) {
				Tuple map = list.get(0);
				List<BuildingDetails> Blist = buildingDetRepo.findByQuoteNo(map.get("quoteNo").toString());
				List<Map<String,Object>> locationDetails = Blist.stream().map(k ->{
					LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
					lmap.put("locationName", k.getLocationName()==null?"":StringUtils.capitalize(k.getLocationName()));
					lmap.put("buildingAddress", k.getBuildingAddress()==null?"":k.getBuildingAddress());
					lmap.put("buildingSumInsured", k.getBuildingSuminsured());
					return lmap;
				}).collect(Collectors.toList());
				
				CriteriaQuery<Tuple> cq1 = cb.createQuery(Tuple.class);
				Root<PolicyCoverData> pcdRoot = cq1.from(PolicyCoverData.class);
				Root<SectionDataDetails> sddRoot = cq1.from(SectionDataDetails.class);
				List<EserviceCommonDetails> eserviceCommonList = eserviceCommonDetRepo.findByQuoteNo(map.get("quoteNo").toString());
				Selection<Object> eserviceQuote = null;
				
				List<Predicate> predicate = new ArrayList<Predicate>();
				predicate.add(cb.equal(pcdRoot.get("quoteNo"),map.get("quoteNo")));
				predicate.add(cb.equal(pcdRoot.get("quoteNo"),sddRoot.get("quoteNo")));
				predicate.add(cb.equal(pcdRoot.get("sectionId"), sddRoot.get("sectionId")));
				predicate.add(cb.equal(pcdRoot.get("taxId"),"0"));
				predicate.add(cb.equal(pcdRoot.get("discLoadId"), "0"));
				predicate.add(cb.equal(pcdRoot.get("subCoverId"), "0"));
				if(!eserviceCommonList.isEmpty()) {
					Root<EserviceCommonDetails> ecdRoot = cq1.from(EserviceCommonDetails.class);
					eserviceQuote = ecdRoot.get("occupationDesc").alias("occupationDesc");
					predicate.add(cb.equal(pcdRoot.get("quoteNo"), ecdRoot.get("quoteNo")));
					predicate.add(cb.equal(pcdRoot.get("sectionId"), ecdRoot.get("sectionId")));
					predicate.add(cb.equal(pcdRoot.get("vehicleId"), ecdRoot.get("riskId")));
					predicate.add(cb.equal(pcdRoot.get("productId"), ecdRoot.get("productId")));
					predicate.add(cb.equal(pcdRoot.get("companyId"), ecdRoot.get("companyId")));
				}
				Predicate [] predicateArray = new Predicate[predicate.size()];
				predicate.toArray(predicateArray);
				cq1.multiselect(sddRoot.get("sectionId").alias("sectionId"),sddRoot.get("sectionDesc").alias("sectionDesc"),pcdRoot.get("coverDesc").alias("coverDesc"),
						 pcdRoot.get("sumInsured").alias("sumInsured"),pcdRoot.get("rate").alias("rate"),pcdRoot.get("premiumIncludedTaxLc").alias("premiumIncludedTaxLc"),
						 pcdRoot.get("premiumIncludedTaxFc").alias("premiumIncludedTaxFc"),!eserviceCommonList.isEmpty()?eserviceQuote:cb.literal("").alias("occupationDesc"))
				.where(predicateArray).orderBy(cb.asc(sddRoot.get("sectionId")));
						
			List<Tuple> Slist = em.createQuery(cq1).getResultList();
			Map<Object, List<Map<String,Object>>> sectionRes = Slist.stream().collect(Collectors.groupingBy(g -> g.get("sectionDesc"),Collectors.mapping(v ->{
				Map<String,Object> Smap = new HashMap<String,Object>();
				Smap.put("occupationDesc", v.get("occupationDesc"));
				Smap.put("coverDesc", v.get("coverDesc"));
				Smap.put("sumInsured", v.get("sumInsured"));
				Smap.put("rate", v.get("rate"));
				Smap.put("premiumIncludedTaxLc", v.get("premiumIncludedTaxLc"));
				Smap.put("premiumIncludedTaxFc", v.get("premiumIncludedTaxFc"));
				return Smap;
			}, Collectors.toList())));			
			List<Map<String,Object>> sectionList = new ArrayList<Map<String,Object>>();
			for(Map.Entry<Object, List<Map<String,Object>>> entry :sectionRes.entrySet()) {
				Map<String, Object> sectionMap = new HashMap<String, Object>();
				sectionMap.put("sectionKey", entry.getKey());
				sectionMap.put("sectionValue", entry.getValue());
				sectionList.add(sectionMap);
			}
			List<Object> sectionIds = Slist.stream().map(k -> k.get("sectionId")).distinct().collect(Collectors.toList());
			List<Map<String,Object>> coverageList = new ArrayList<Map<String,Object>>();
			for(int i=0;i<sectionIds.size();i++) {
				Map<String,Object> coverMap = new HashMap<String,Object>();
				String sectionId = sectionIds.get(i).toString();
				List<ContentAndRisk> contentInfo = conAndRiskRepo.findByQuoteNoAndSectionId(map.get("quoteNo").toString(),sectionId);
				List<Map<String,Object>> contentList = contentInfo.stream().map(k ->{
					LinkedHashMap<String, Object> contentMap = new LinkedHashMap<String, Object>();
						contentMap.put("itemId", k.getItemId());
						contentMap.put("itemDesc", k.getItemDesc());
						contentMap.put("contentRiskDesc", k.getContentRiskDesc());
						contentMap.put("sumInsured", k.getSumInsured());
						return contentMap;
					}).collect(Collectors.toList());
				
				List<ProductEmployeeDetails> empDetails = productEmpDetRepo.findByQuoteNoAndSectionId(map.get("quoteNo").toString(),sectionId);
				List<Map<String,Object>> employeeList = empDetails.stream().map(e ->{
					LinkedHashMap<String,Object> empMap = new LinkedHashMap<String,Object>();
						empMap.put("employeeId", e.getEmployeeId());
						empMap.put("employeeName", e.getEmployeeName());
						empMap.put("occupationDesc", e.getOccupationDesc());
						empMap.put("salary", e.getSalary());
						return empMap;
					}).collect(Collectors.toList());
				
				// CONDITIONS
				List<Map<String,Object>> conditionList = getConditionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);

				// EXCLUSION
				List<Map<String,Object>> exclusionRes = getExclusionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);
				List<Map<String,Object>> exclusionList = exclusionRes.stream().map(k -> {
					Map<String,Object> eMap = new HashMap<String,Object>();
					eMap.put("conditionTerms", k.get("exclusioTerms"));
					return eMap;
				}).collect(Collectors.toList());
				
				//WARRANTY
				List<Map<String,Object>> warrantyList = getWarrantyDescription(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);
				
				List<Map<String,Object>> termsAndconditions = Stream.of(conditionList,exclusionList,warrantyList).flatMap(Collection::stream).collect(Collectors.toList());
				termsAndconditions = termsAndconditions.stream().distinct().collect(Collectors.toList());
				coverMap.put("sectionDesc", Slist.stream().filter(k -> sectionId.equalsIgnoreCase(k.get("sectionId").toString())).map(e -> e.get("sectionDesc").toString()).findFirst().orElse(""));
				coverMap.put("contentList", contentList);
				coverMap.put("employeeList", employeeList);
				coverMap.put("termsAndconditions", termsAndconditions);
				coverageList.add(coverMap);
			}
			
			Map<Object,List<Map<String,Object>>> groupBycoverageDetails = coverageList.stream().collect(Collectors.groupingBy(k -> k.get("sectionDesc"), Collectors.toList()));
			List<Map<String,Object>> coverageDetails = new ArrayList<Map<String,Object>>();
			for(Map.Entry<Object, List<Map<String,Object>>> CDEntry : groupBycoverageDetails.entrySet()) {
				LinkedHashMap<String, Object> coverMap = new LinkedHashMap<String, Object>();
				coverMap.put("coverId", Slist.stream().filter(f -> f.get("sectionDesc").equals(CDEntry.getKey())).map(m -> m.get("sectionId")).findFirst().orElse(""));
				coverMap.put("coverKey", CDEntry.getKey());
				coverMap.put("coverValue", CDEntry.getValue());
				coverMap.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
				coverMap.put("policyNo", map.get("policyNo")==null?"":map.get("policyNo").toString());
				coverageDetails.add(coverMap);
			}
			
			result.put("policyNo", map.get("policyNo")==null?"":map.get("policyNo").toString());
			result.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
			result.put("customerName", map.get("customerName")==null?"":map.get("customerName").toString());
			result.put("address", map.get("address")==null?"":map.get("address").toString());
			result.put("inceptionDate", map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
			result.put("expiryDate", map.get("expiryDate")==null?"":map.get("expiryDate").toString());
			result.put("branchName", map.get("branchName")==null?"":map.get("branchName").toString());
			result.put("brokerBranchName", map.get("brokerBranchName")==null?"":map.get("brokerBranchName").toString());
			result.put("productName", map.get("productName")==null?"":map.get("productName").toString());
			result.put("stateName", map.get("stateName")==null?"":map.get("stateName").toString());
			result.put("cityName", map.get("cityName")==null?"":map.get("cityName").toString());
			result.put("mobileNo", map.get("mobileNo")==null?"":map.get("mobileNo").toString());
			result.put("customerId", map.get("customerId")==null?"":map.get("customerId").toString());
			result.put("brokerName", map.get("brokerName")==null?"":map.get("brokerName").toString());
			result.put("coreAppBrokerCode", map.get("coreAppBrokerCode")==null?"":map.get("coreAppBrokerCode").toString());
			result.put("currency", map.get("currency")==null?"":map.get("currency").toString());
			result.put("vatPercent", map.get("vatPercent")==null?"":Double.parseDouble(map.get("vatPercent").toString()));
			result.put("premium", map.get("premium")==null?"":map.get("premium").toString());
			result.put("vatPremium", map.get("vatPremium")==null?"":Double.parseDouble(map.get("vatPremium").toString()));
			result.put("totalPremium", map.get("totalPremium")==null?"":map.get("totalPremium").toString());
			result.put("signature", map.get("signature")==null?"":map.get("signature").toString());
			result.put("place", map.get("place")==null?"":map.get("place").toString());
			result.put("sectionDetails", sectionList);
			result.put("locationDetails", locationDetails);
			result.put("coverageDetails", coverageDetails);
			}
		}catch(Exception e) {
			log.info("Error in EwaySchedule ==> "+e.getMessage());
			e.printStackTrace();
		}
		log.info("Exit into EwaySchedule");
		return result;
	}
	
	private List<Map<String,Object>> getConditionList(String policyNo,String QuoteNo, String sectionId){
		List<Map<String,Object>> conditionList = new ArrayList<Map<String,Object>>();
	try {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		List<Tuple> conditionRes = new ArrayList<>();
		for(int i=1;i<=2;i++) {
			CriteriaQuery<Tuple> cq2 = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot2 = cq2.from(HomePositionMaster.class);
			
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			if(StringUtils.isNotBlank(policyNo)) {
				predicates.add(cb.equal(hpmRoot2.get("policyNo"), policyNo));
			}else {
				predicates.add(cb.equal(hpmRoot2.get("quoteNo"), QuoteNo));
			}
			if(i == 1) {
				Subquery<Tuple> CquoteIn = cq2.subquery(Tuple.class);
				Root<TermsAndCondition> StacRoot = CquoteIn.from(TermsAndCondition.class);
				CquoteIn.select(StacRoot.get("quoteNo")).where(cb.equal(StacRoot.get("quoteNo"), QuoteNo));
				
				Root<ClausesMaster> cmRoot2 = cq2.from(ClausesMaster.class);
				if(StringUtils.isNotBlank(sectionId)) {
					predicates.add(cb.or(cb.equal(cmRoot2.get("sectionId"), sectionId), cb.equal(cmRoot2.get("sectionId"), "99999")));
				}else {
					Root<SectionDataDetails> sddRoot2 = cq2.from(SectionDataDetails.class);
					predicates.add(cb.equal(sddRoot2.get("quoteNo"), hpmRoot2.get("quoteNo")));
					predicates.add(cb.or(cb.equal(cmRoot2.get("sectionId"), sddRoot2.get("sectionId")), cb.equal(cmRoot2.get("sectionId"), "99999")));
				}
				cq2.multiselect(cmRoot2.get("clausesDescription").alias("conditionTerms"));
				predicates.add(cb.equal(cmRoot2.get("companyId"), hpmRoot2.get("companyId")));
				predicates.add(cb.equal(cmRoot2.get("productId"), hpmRoot2.get("productId")));
				predicates.add(cb.or(cb.equal(cmRoot2.get("branchCode"), hpmRoot2.get("branchCode")), cb.equal(cmRoot2.get("branchCode"), "99999")));
				predicates.add(cb.between(cb.literal(new Date()), cmRoot2.get("effectiveDateStart"), cmRoot2.get("effectiveDateEnd")));
				predicates.add(cb.equal(cmRoot2.get("status"), "Y"));
				predicates.add(cb.not(cb.in(hpmRoot2.get("quoteNo")).value(CquoteIn)));
			}else {
				Root<TermsAndCondition> tacRoot2 = cq2.from(TermsAndCondition.class);
				if(StringUtils.isNotBlank(sectionId)) {
					predicates.add(cb.or(cb.equal(tacRoot2.get("sectionId"), sectionId), cb.equal(tacRoot2.get("sectionId"), "99999")));
				}else {
					Root<SectionDataDetails> sddRoot2 = cq2.from(SectionDataDetails.class);
					predicates.add(cb.equal(sddRoot2.get("quoteNo"), hpmRoot2.get("quoteNo")));
					predicates.add(cb.equal(tacRoot2.get("sectionId"), sddRoot2.get("sectionId")));
				}
				cq2.multiselect(tacRoot2.get("subIdDesc").alias("conditionTerms"));
				predicates.add(cb.equal(tacRoot2.get("companyId"), hpmRoot2.get("companyId")));
				predicates.add(cb.equal(tacRoot2.get("productId"), hpmRoot2.get("productId")));
				
				predicates.add(cb.in(hpmRoot2.get("quoteNo")).value(tacRoot2.get("quoteNo")));
				predicates.add(cb.equal(tacRoot2.get("status"), "Y"));
				predicates.add(cb.or(cb.equal(tacRoot2.get("branchCode"), hpmRoot2.get("branchCode")), cb.equal(tacRoot2.get("branchCode"), "99999")));
			}
				Predicate [] predicatArray = new Predicate[predicates.size()];
				predicates.toArray(predicatArray);
				conditionRes.addAll(em.createQuery(cq2.where(predicatArray)).getResultList());
		}
		conditionList = conditionRes.stream().distinct().map(c ->{
			LinkedHashMap<String,Object> Cmap = new LinkedHashMap<String,Object>();
			Cmap.put("conditionTerms", c.get("conditionTerms")==null?"":c.get("conditionTerms").toString());
			return Cmap;
		}).collect(Collectors.toList());
	}catch(Exception e) {
		log.info("Error in getConditionList ==> "+e.getMessage());
		e.printStackTrace();
	}
	return conditionList;
	}
	
	private List<Map<String,Object>> getExclusionList(String policyNo,String QuoteNo,String sectionId){
		List<Map<String,Object>> exclusionList = new ArrayList<Map<String,Object>>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> exclusionRes = new ArrayList<>();
			
			for(int i=1;i<=2;i++) {
				CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot3 = cq3.from(HomePositionMaster.class);
				
				List<Predicate> predicates = new ArrayList<Predicate>();
				if(StringUtils.isNotBlank(policyNo)) {
					predicates.add(cb.equal(hpmRoot3.get("policyNo"), policyNo));
				}else {
					predicates.add(cb.equal(hpmRoot3.get("quoteNo"), QuoteNo));
				}
				if(i == 1) {
					
					Subquery<Tuple> EquoteIn = cq3.subquery(Tuple.class);
					Root<TermsAndCondition> SEtacRoot = EquoteIn.from(TermsAndCondition.class);
					EquoteIn.select(SEtacRoot.get("quoteNo")).where(cb.equal(SEtacRoot.get("quoteNo"), QuoteNo));
					
					Root<ExclusionMaster> emRoot3 = cq3.from(ExclusionMaster.class);
					if(StringUtils.isNotBlank(sectionId)) {
						predicates.add(cb.or(cb.equal(emRoot3.get("sectionId"), sectionId), cb.equal(emRoot3.get("sectionId"), "99999")));
					}else {
						Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot3.get("quoteNo"), hpmRoot3.get("quoteNo")));
						predicates.add(cb.or(cb.equal(emRoot3.get("sectionId"), sddRoot3.get("sectionId")), cb.equal(emRoot3.get("sectionId"), "99999")));
					}
					cq3.multiselect(emRoot3.get("exclusionDescription").alias("exclusionTerms"));
					predicates.add(cb.equal(emRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(emRoot3.get("productId"), hpmRoot3.get("productId")));
					predicates.add(cb.or(cb.equal(emRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(emRoot3.get("branchCode"), "99999")));
					predicates.add(cb.between(cb.literal(new Date()), emRoot3.get("effectiveDateStart"), emRoot3.get("effectiveDateEnd")));
					predicates.add(cb.equal(emRoot3.get("status"), "Y"));
					predicates.add(cb.not(cb.in(hpmRoot3.get("quoteNo")).value(EquoteIn)));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				}else {
					Root<TermsAndCondition> tacRoot3 = cq3.from(TermsAndCondition.class);
					if(StringUtils.isNotBlank(sectionId)) {
						predicates.add(cb.equal(tacRoot3.get("sectionId"), sectionId));
						predicates.add(cb.or(cb.equal(tacRoot3.get("sectionId"), sectionId), cb.equal(tacRoot3.get("sectionId"), "99999")));
					}else {
						Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot3.get("quoteNo"), hpmRoot3.get("quoteNo")));
						predicates.add(cb.equal(tacRoot3.get("sectionId"), sddRoot3.get("sectionId")));
					}
					
					cq3.multiselect(tacRoot3.get("subIdDesc").alias("exclusionTerms"));
					predicates.add(cb.equal(tacRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(tacRoot3.get("productId"), hpmRoot3.get("productId")));
					
					predicates.add(cb.in(hpmRoot3.get("quoteNo")).value(tacRoot3.get("quoteNo")));
					predicates.add(cb.equal(tacRoot3.get("status"), "Y"));
					predicates.add(cb.or(cb.equal(tacRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(tacRoot3.get("branchCode"), "99999")));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				}
			}
			exclusionList = exclusionRes.stream().distinct().map(c ->{
				LinkedHashMap<String,Object> Emap = new LinkedHashMap<String,Object>();
				Emap.put("exclusioTerms", c.get("exclusionTerms")==null?"":c.get("exclusionTerms").toString());
				return Emap;
			}).collect(Collectors.toList());
		}catch(Exception e) {
			log.info("Error in getExclusionList ==> "+e.getMessage());
			e.printStackTrace();
		}
		return exclusionList;
	}
	
	private List<Map<String,Object>> getWarrantyDescription(String policyNo,String QuoteNo, String sectionId){
		List<Map<String,Object>> warrantyList = new ArrayList<Map<String,Object>>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> warrantyRes = new ArrayList<>();
			
			for(int i=1;i<=2;i++) {
				CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot3 = cq3.from(HomePositionMaster.class);
				List<Predicate> predicates = new ArrayList<Predicate>();
				if(StringUtils.isNotBlank(policyNo)) {
					predicates.add(cb.equal(hpmRoot3.get("policyNo"), policyNo));
				}else {
					predicates.add(cb.equal(hpmRoot3.get("quoteNo"), QuoteNo));
				}
				if(i == 1) {
					
					Subquery<Tuple> EquoteIn = cq3.subquery(Tuple.class);
					Root<TermsAndCondition> SEtacRoot = EquoteIn.from(TermsAndCondition.class);
					EquoteIn.select(SEtacRoot.get("quoteNo")).where(cb.equal(SEtacRoot.get("quoteNo"), QuoteNo));
					Root<WarrantyMaster> wmRoot3 = cq3.from(WarrantyMaster.class);
					cq3.multiselect(wmRoot3.get("warrantyDescription").alias("warrantyTerms"));
					predicates.add(cb.equal(wmRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(wmRoot3.get("productId"), hpmRoot3.get("productId")));
					predicates.add(cb.or(cb.equal(wmRoot3.get("sectionId"), sectionId), cb.equal(wmRoot3.get("sectionId"), "99999")));
					predicates.add(cb.or(cb.equal(wmRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(wmRoot3.get("branchCode"), "99999")));
					predicates.add(cb.between(cb.literal(new Date()), wmRoot3.get("effectiveDateStart"), wmRoot3.get("effectiveDateEnd")));
					predicates.add(cb.equal(wmRoot3.get("status"), "Y"));
					predicates.add(cb.not(cb.in(hpmRoot3.get("quoteNo")).value(EquoteIn)));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					warrantyRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				}else {
					Root<TermsAndCondition> tacRoot3 = cq3.from(TermsAndCondition.class);
					cq3.multiselect(tacRoot3.get("subIdDesc").alias("warrantyTerms"));
					predicates.add(cb.equal(tacRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(tacRoot3.get("productId"), hpmRoot3.get("productId")));
					predicates.add(cb.or(cb.equal(tacRoot3.get("sectionId"), sectionId), cb.equal(tacRoot3.get("sectionId"), "99999")));
					predicates.add(cb.in(hpmRoot3.get("quoteNo")).value(tacRoot3.get("quoteNo")));
					predicates.add(cb.equal(tacRoot3.get("status"), "Y"));
					predicates.add(cb.or(cb.equal(tacRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(tacRoot3.get("branchCode"), "99999")));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					warrantyRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				}
			}
			warrantyList = warrantyRes.stream().distinct().map(c ->{
				LinkedHashMap<String,Object> Emap = new LinkedHashMap<String,Object>();
				Emap.put("conditionTerms", c.get("warrantyTerms")==null?"":c.get("warrantyTerms").toString());
				return Emap;
			}).collect(Collectors.toList());
		}catch(Exception e) {
			log.info("Error in getWarrantyDescription ==> "+e.getMessage());
			e.printStackTrace();
		}
		return warrantyList;
		
	}

}