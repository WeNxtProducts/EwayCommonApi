package com.maan.eway.jasper.service.impl;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.CountryMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.MotorMakeModelMaster;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.ProductGroupMaster;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.TravelPassengerDetails;
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
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;

@Component
public class JasperCustomServiceImple {
	
	Logger log = LogManager.getLogger(JasperCustomServiceImple.class);

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private MotorDataDetailsRepository motorRepo;
	
	@Autowired
	private MotorDriverDetailsRepository motordriverRepo;
		
	private String RenewalDate(String Input) {
		DateTimeFormatter inputformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
		LocalDateTime dateTime = LocalDateTime.parse(Input, inputformatter);
		return dateTime.toLocalDate().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}

	public MotorCoverNoteRes getMotorCoverNote(String policyNo) {
		MotorCoverNoteRes response = new  MotorCoverNoteRes();
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
		return response;
	}

	public TaxInvoiceRes getTaxInvoiceRes(String policyNo) {
		TaxInvoiceRes response = new TaxInvoiceRes();
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
				piRoot.get("vrTinNo").alias("vrTinNo"),cb.selectCase().when(cb.equal(piRoot.get("idType"), "6"), piRoot.get("idNumber")).alias("customerTin"),
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
			response.setCustomerTin(map.get("customerTin")==null?"":map.get("customerTin").toString());
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
			response.setDataset1List(dataset1Res);
		}
			return response;
	}
	
	public CreditNoteRes getCreditNoteRes(String policyNo) {
		CreditNoteRes response = new CreditNoteRes();
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
		return response;
	}

	public MotorPrivateRes getMotorPrivate(String policyNo) {
		MotorPrivateRes response = new MotorPrivateRes();
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
		return response;
	}

	public TravelReportRes getTravelReport(String policyNo) {
		TravelReportRes response = new TravelReportRes();
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
						cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("pinCode")), "").when(cb.equal(piRoot.get("pinCode"), ""), "").otherwise(",").as(String.class), cb.coalesce(piRoot.get("stateName"), 
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
		return response;
	}

	public Map<String, Object> getMotorEndorsementSchedule(String policyNo) {
		Map<String, Object> result = new HashMap<String,Object>();
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
		return result;
	}

}