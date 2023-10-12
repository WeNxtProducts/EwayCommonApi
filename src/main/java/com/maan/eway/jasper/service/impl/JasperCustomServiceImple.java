package com.maan.eway.jasper.service.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import org.springframework.stereotype.Component;

import com.maan.eway.bean.CountryMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorMakeModelMaster;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.jasper.res.MotorCoverNoteRes;
import com.maan.eway.jasper.res.TaxDataSetOneRes;
import com.maan.eway.jasper.res.TaxInvoiceRes;

@Component
public class JasperCustomServiceImple {
	
	Logger log = LogManager.getLogger(JasperCustomServiceImple.class);

	@PersistenceContext
	private EntityManager em;

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
				hpmRoot.get("inceptionDate").alias("paymentDate"),
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
			response.setStartDate(map.get("paymentDate")==null?"":map.get("paymentDate").toString());
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
				cb.concat(piRoot.get("titleDesc"), cb.concat(".", piRoot.get("clientName"))).alias("customerName"),cb.concat(piRoot.get("address1"), cb.concat(",", cb.concat(piRoot.get("pinCode"),
				cb.concat(",", cb.concat(piRoot.get("stateName"), cb.concat(",", cb.concat(piRoot.get("cityName"),
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
}
