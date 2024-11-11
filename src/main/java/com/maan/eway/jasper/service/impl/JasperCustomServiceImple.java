 package com.maan.eway.jasper.service.impl;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.Put;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.ClausesMaster;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.ContentAndRisk;
import com.maan.eway.bean.CountryMaster;
import com.maan.eway.bean.DocumentUniqueDetails;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.ExclusionMaster;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.FirstLossPayee;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.MotorMakeMaster;
import com.maan.eway.bean.MotorMakeModelMaster;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PolicyDrcrDetail;
import com.maan.eway.bean.PolicyTypeMaster;
import com.maan.eway.bean.ProductEmployeeDetails;
import com.maan.eway.bean.ProductGroupMaster;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.TermsAndCondition;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.bean.WarrantyMaster;
import com.maan.eway.jasper.req.JasperScheduleReq;
import com.maan.eway.jasper.req.PremiumReportReq;
import com.maan.eway.jasper.res.AttachMentRes;
import com.maan.eway.jasper.res.CoverDetailsRes;
import com.maan.eway.jasper.res.CreditDataSetOne;
import com.maan.eway.jasper.res.CreditDataSetTwo;
import com.maan.eway.jasper.res.CreditNoteRes;
import com.maan.eway.jasper.res.MotorCoverNoteRes;
import com.maan.eway.jasper.res.MotorPrivateAccessoriesDetails;
import com.maan.eway.jasper.res.MotorPrivateDriverDetails;
import com.maan.eway.jasper.res.MotorPrivateRes;
import com.maan.eway.jasper.res.MotorPrivateVehicleDetails;
import com.maan.eway.jasper.res.PremiumReportRes;
import com.maan.eway.jasper.res.TaxDataSetOneRes;
import com.maan.eway.jasper.res.TaxInvoicePremiumDetails;
import com.maan.eway.jasper.res.TaxInvoiceRes;
import com.maan.eway.jasper.res.TearmsAndCondition;
import com.maan.eway.jasper.res.TravelDataSetOneRes;
import com.maan.eway.jasper.res.TravelDataSetTwoRes;
import com.maan.eway.jasper.res.TravelReportRes;
import com.maan.eway.repository.BuildingDetailsRepository;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.ContentAndRiskRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.FirstLossPayeeRepository;
import com.maan.eway.repository.GroupMedicalDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.PolicyDrcrDetailRepository;
import com.maan.eway.repository.ProductEmployeesDetailsRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.Subquery;

@Component
public class JasperCustomServiceImple {
	
	Logger log = LogManager.getLogger(JasperCustomServiceImple.class);

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private MotorDataDetailsRepository motorRepo;
	
	@Autowired
	private ContentAndRiskRepository conAndRiskRepo;
	
	@Autowired
	private BuildingDetailsRepository buildingDetRepo;
	
	@Autowired
	private BuildingRiskDetailsRepository buildingRiskDetailsRepo;
	
	@Autowired
	private ProductEmployeesDetailsRepository productEmpDetRepo;
	
	@Autowired
	private PaymentDetailRepository paymentDetailRepo;
	
	@Autowired
	private ListItemValueRepository listItemValueRepo;
	
	@Autowired
	private GroupMedicalDetailsRepository groupMedicalDetRepo;
	
	@Autowired
	private PolicyDrcrDetailRepository drcrdetail;
	
	@Autowired
	private InsuranceCompanyMasterRepository insuranceComMasRepo;
	
	@Autowired
	private EserviceBuildingDetailsRepository eserviceBuildingDetailsRepo;
	
	@Autowired
	private HomePositionMasterRepository homeRepo;
	
	@Autowired
	private PolicyCoverDataRepository coverDataRepository;
	
	@Autowired
	private FactorRateRequestDetailsRepository factorRateRequestDetailsRepo;
	
	@Autowired
	private FirstLossPayeeRepository firstLossPayeeRepo; 
	
	@Autowired
	private CommonDataDetailsRepository commonDataDetailsRepo;
	
//	@Autowired
//	private MultiplePolicyDrCrDetailRepository multiPolicyDrCrDtlRepo;
	
	@Autowired
	private JasperServiceImpl jasperServiceImpl;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	private String RenewalDate(String Input) {
		DateTimeFormatter inputformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
		LocalDateTime dateTime = LocalDateTime.parse(Input, inputformatter);
		return dateTime.toLocalDate().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}
	
	public Object callReport(JasperScheduleReq req) {
		log.info("Enter into callReport");
		Object response = null;
		try {
			HomePositionMaster hpm = homeRepo.findByQuoteNo(req.getQuoteNo());
			if("1".equalsIgnoreCase(req.getReportId())) { // SCHEDULE
				if(hpm.getProductId() == 5) {
					if("100004".equalsIgnoreCase(hpm.getCompanyId())) {
						List<Map<String,Object>> resportRes = getMadisonMotorSchedule(hpm.getPolicyNo());
						response = resportRes;
					}else {
						MotorPrivateRes reportRes = getMotorPrivate(hpm.getPolicyNo(), req.getQuoteNo(),"");
						response = reportRes;
					}
				}else if(hpm.getProductId() == 4) {
					TravelReportRes reportRes = getTravelReport(hpm.getPolicyNo());
					response = reportRes;
				}else if(hpm.getProductId() == 42) {
					Map<String, Object> reportRes = getCyberInsurance(hpm.getPolicyNo());
					response = reportRes;
				}else {
					Map<String, Object> reportRes = getEwaySchedule(req.getQuoteNo());
					response = reportRes;
				}
			}else if("2".equalsIgnoreCase(req.getReportId())) { // CREDIT NOTE
				CreditNoteRes creditNoteRes = getCreditNoteRes(hpm.getPolicyNo());
				response = creditNoteRes;
			}else if("3".equalsIgnoreCase(req.getReportId())) { // DEBIT NOTE
				TaxInvoiceRes invoiceRes = getTaxInvoiceRes(hpm.getPolicyNo());
				response = invoiceRes;
			}else if("4".equalsIgnoreCase(req.getReportId())) { // BROKER QUOTATION
				Map<String,Object> map = getMotorBrokerQuotation(hpm.getQuoteNo());
				response = map;
			}else if("5".equalsIgnoreCase(req.getReportId())) { //PREMIUM REGISTER
				PremiumReportRes reportRes = (PremiumReportRes) jasperServiceImpl.getPremiumReport(req.getPremiumRegisterReq()).getCommonResponse();
				response = reportRes;
			}else if("6".equalsIgnoreCase(req.getReportId())) { // ENDORSEMENT PDF
				Map<String,Object> map = getMotorEndorsementSchedule(hpm.getPolicyNo());
				response = map;
			}else if("7".equalsIgnoreCase(req.getReportId())) { // STICKER PDF
				List<MotorPrivateVehicleDetails> motPrivateRes = getMotorPrivate(hpm.getPolicyNo(),req.getQuoteNo(),"").getVehicleDetails();
				response = motPrivateRes;
			}else if("8".equalsIgnoreCase(req.getReportId())) { // ILLESTRATION PDF
				Map<String,Object> map = getInalipaSchedule(hpm.getPolicyNo());
				response = map;
			}
			log.info("callReport Response ==> "+new Gson().toJson(response));
		}catch(Exception e) {
			log.info("Error in callReport ==> "+e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	@SuppressWarnings("rawtypes")
	public List<MotorCoverNoteRes> getMotorCoverNote(String policyNo,String vehicleId) {
	  log.info("Enter into getMotorCoverNote.\nArgument ==> PolicyNo :"+policyNo);
	  List<MotorCoverNoteRes> response = new  ArrayList<MotorCoverNoteRes>();
  try {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<MotorDataDetails> mddRoot = cq.from(MotorDataDetails.class);
		Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
		Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
		Root<InsuranceCompanyMaster> icmRoot = cq.from(InsuranceCompanyMaster.class);
		Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
		Root<SectionDataDetails> sddRoot = cq.from(SectionDataDetails.class);
		
		Subquery<String> insureName = cq.subquery(String.class);
		Root<LoginUserInfo> SubluiRoot = insureName.from(LoginUserInfo.class);
		insureName.select(cb.upper(SubluiRoot.get("userName"))).where(cb.equal(SubluiRoot.get("loginId"), hpmRoot.get("loginId")));
		
		// MAKE MASTER TYPE
		Subquery<Integer> makeTypeAmd = cq.subquery(Integer.class);
		Root<MotorMakeMaster> makeAmd = makeTypeAmd.from(MotorMakeMaster.class);
		makeTypeAmd.select(cb.max(makeAmd.get("amendId"))).where(cb.equal(makeAmd.get("makeId").as(String.class), mddRoot.get("vehicleMake")),
				cb.equal(makeAmd.get("status"), "Y"),cb.equal(makeAmd.get("companyId"), hpmRoot.get("companyId")));
		
		/*Subquery<String> makeType = cq.subquery(String.class);
		Root<MotorMakeMaster> makeRoot = makeType.from(MotorMakeMaster.class);
		makeType.select(makeRoot.get("makeNameEn")).where(cb.equal(makeRoot.get("makeId"), mddRoot.get("vehicleMake")),
				cb.equal(makeRoot.get("companyId"), hpmRoot.get("companyId")),cb.equal(makeRoot.get("status"), "Y"),
				cb.equal(makeRoot.get("amendId"), makeTypeAmd));*/
		
		// MODEL MASTER TYPE
		Subquery<Integer> modelTypeAmd = cq.subquery(Integer.class);
		Root<MotorMakeModelMaster> SubmmAmd = modelTypeAmd.from(MotorMakeModelMaster.class);
		modelTypeAmd.select(cb.max(SubmmAmd.get("amendId"))).where(cb.equal(SubmmAmd.get("vehiclemodelcode").as(String.class), mddRoot.get("vehcileModel")),
				cb.equal(SubmmAmd.get("status"), "Y"),cb.equal(SubmmAmd.get("companyId"), hpmRoot.get("companyId")));
		
		Subquery<String> modelType = cq.subquery(String.class);
		Root<MotorMakeModelMaster> Submm = modelType.from(MotorMakeModelMaster.class);
		modelType.select(Submm.get("modelNameEn")).where(cb.equal(Submm.get("vehiclemodelcode").as(String.class), mddRoot.get("vehcileModel")),
				cb.equal(Submm.get("companyId"), hpmRoot.get("companyId")),cb.equal(Submm.get("status"), "Y"),cb.equal(Submm.get("amendId"), modelTypeAmd));
		
		
		Subquery<Integer> icmAmd = cq.subquery(Integer.class);
		Root<InsuranceCompanyMaster> SubicmAmd = icmAmd.from(InsuranceCompanyMaster.class);
		icmAmd.select(cb.max(SubicmAmd.get("amendId"))).where(cb.equal(SubicmAmd.get("companyId"), icmRoot.get("companyId")));
		
			List<Selection> selectionList = 	Arrays.asList(
				mddRoot.get("vehicleId").alias("vehicleId"),
				cb.concat(piRoot.get("titleDesc"), cb.concat(".", piRoot.get("clientName"))).alias("customerName"),
				cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")),hpmRoot.get("customerName"))
						.otherwise(insureName).alias("insurerName"),
				hpmRoot.get("policyCovertedDate").alias("paymentDate"),
				hpmRoot.get("inceptionDate").alias("inceptionDate"),
				hpmRoot.get("expiryDate").alias("expiryDate"),
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
				sddRoot.get("coverNoteReferenceNo").alias("covernoteNo"),sddRoot.get("stickerNumber").alias("stickerNumber"));
			List<Selection> selections = selectionList.stream().collect(Collectors.toList());
		//if(StringUtils.isNotBlank(vehicleId)) {
			selections.add(cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")), mddRoot.get("actualPremiumLc"))
					.otherwise(mddRoot.get("actualPremiumFc")).alias("premium"));
			selections.add(mddRoot.get("vatPremium").alias("vatPremium"));
			selections.add(cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")), mddRoot.get("overallPremiumLc"))
					.otherwise(mddRoot.get("overallPremiumFc")).alias("overallPremium"));
		/*}else {
			selections.add(cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")),hpmRoot.get("premiumLc"))
					.otherwise(hpmRoot.get("vatPremiumFc")).alias("premium"));
			selections.add(cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")), hpmRoot.get("vatPremiumLc"))
					.otherwise(hpmRoot.get("vatPremiumFc")).alias("vatPremium"));
			selections.add(cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")), hpmRoot.get("overallPremiumLc"))
					.otherwise(hpmRoot.get("overallPremiumFc")).alias("overallPremium"));
		}*/
		
		Selection [] selectionArray = new Selection[selections.size()];
		selections.toArray(selectionArray);
				
	  cq.multiselect(selectionArray)
	  .where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId")),
					cb.equal(mddRoot.get("quoteNo"), hpmRoot.get("quoteNo")),
					cb.equal(luiRoot.get("loginId"), hpmRoot.get("loginId")),
					cb.equal(hpmRoot.get("currency"), icmRoot.get("currencyId")),
					cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")),
					cb.equal(icmRoot.get("amendId"), icmAmd),
					cb.equal(hpmRoot.get("productId"), "46"),
					cb.equal(hpmRoot.get("status"), "P"),
					cb.equal(sddRoot.get("quoteNo"), mddRoot.get("quoteNo")),
					cb.equal(sddRoot.get("riskId").as(String.class), mddRoot.get("vehicleId")),
					cb.equal(hpmRoot.get("policyNo"), policyNo),
					StringUtils.isNotBlank(vehicleId)?cb.equal(mddRoot.get("vehicleId"), vehicleId):
						cb.conjunction());
		List<Tuple> list = em.createQuery(cq).getResultList();
		if(!CollectionUtils.isEmpty(list)) {
			list.forEach(map -> {
				MotorCoverNoteRes m = MotorCoverNoteRes.builder()
						.vehicleId(map.get("vehicleId")==null?"":map.get("vehicleId").toString())
						.customerName(map.get("customerName")==null?"":map.get("customerName").toString())
						.insurerName(map.get("insurerName")==null?"":map.get("insurerName").toString())
						.paymentDate(map.get("paymentDate")==null?"":map.get("paymentDate").toString())
						.dateofIssue(map.get("paymentDate")==null?"":map.get("paymentDate").toString())
						.startDate(map.get("inceptionDate")==null?"":map.get("inceptionDate").toString())
						.endDate(map.get("expiryDate")==null?"":map.get("expiryDate").toString())
						.covernoteNo(map.get("covernoteNo")==null?"":map.get("covernoteNo").toString())
						.stickerNumber(map.get("stickerNumber")==null?"":map.get("stickerNumber").toString())
						.registrationNumber(map.get("registrationNumber")==null?"":map.get("registrationNumber").toString())
						.vehicleTypeDesc(map.get("vehicleTypeDesc")==null?"":map.get("vehicleTypeDesc").toString())
						.modelType(map.get("modelType")==null?"":map.get("modelType").toString())
						.colorDesc(map.get("colorDesc")==null?"":map.get("colorDesc").toString())
						.cubicCapacity(map.get("cubicCapacity")==null?"":map.get("cubicCapacity").toString())
						.vehicleMakeDesc(map.get("vehicleMakeDesc")==null?"":map.get("vehicleMakeDesc").toString())
						.chassisNumber(map.get("chassisNumber")==null?"":map.get("chassisNumber").toString())
						.seatingCapacity(map.get("seatingCapacity")==null?"":map.get("seatingCapacity").toString())
						.engineNumber(map.get("engineNumber")==null?"":map.get("engineNumber").toString())
						.fuelType(map.get("fuelType")==null?"":map.get("fuelType").toString())
						.policyTypeDesc(map.get("policyTypeDesc")==null?"":map.get("policyTypeDesc").toString())
						.manufactureYear(map.get("manufactureYear")==null?"":map.get("manufactureYear").toString())
						.agentMobile(map.get("agentMobile")==null?"":map.get("agentMobile").toString())
						.motorUsageDesc(map.get("motorUsageDesc")==null?"":map.get("motorUsageDesc").toString())
						.companyName(map.get("companyName")==null?"":map.get("companyName").toString())
						.branchName(map.get("branchName")==null?"":map.get("branchName").toString())
						.currency(map.get("currency")==null?"":map.get("currency").toString())
						.sectionName(map.get("sectionName")==null?"":map.get("sectionName").toString())
						.modelNumber(map.get("vehcileModel")==null?"":map.get("vehcileModel").toString())
						.premium(map.get("premium")==null?"":map.get("premium").toString())
						.vatPremium(map.get("vatPremium")==null?"":map.get("vatPremium").toString())
						.overallPremium(map.get("overallPremium")==null?"":map.get("overallPremium").toString())
						.build();
				response.add(m);
			});
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
		Double OverAllPremium=0.0;
	try {
		List<TaxDataSetOneRes> dataset1Res = new ArrayList<TaxDataSetOneRes>();
		List<TaxInvoicePremiumDetails> premiumDetailsRes = new ArrayList<>();
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
		
		/*Subquery<String> companyName = cq.subquery(String.class);
		Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
		//AMD MAX
		Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
		Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
		companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
		companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
				cb.equal(companyNameRoot.get("amendId"), companyNameAmd));
		
		Subquery<String> imageURL = cq.subquery(String.class);
		Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
		//AMD MAX
		Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
		Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
		imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
		imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
				cb.equal(imageURLRoot.get("amendId"), imageURLAmd));*/
		
		cq.multiselect(luiRoot.get("userName").alias("userName"),hpmRoot.get("approvedBy").alias("approvedBy"),hpmRoot.get("agencyCode").alias("agencyCode"),
				cb.concat(piRoot.get("titleDesc"), cb.concat(".", piRoot.get("clientName"))).alias("customerName"),cb.concat(piRoot.get("address1"), cb.concat(",",
				cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("pinCode")), "").when(cb.equal(piRoot.get("pinCode"), ""), "").otherwise(cb.concat("P.O.BOX ", piRoot.get("pinCode"))).as(String.class),
				cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("pinCode")), "").when(cb.equal(piRoot.get("pinCode"), ""), "").otherwise(",").as(String.class), cb.concat(piRoot.get("cityName"),cb.concat(",",cb.concat(piRoot.get("stateName"),cb.concat(",\n", countryName)))))))).alias("address"),
				piRoot.get("vrTinNo").alias("vrTinNo"),piRoot.get("idTypeDesc").alias("identificationName"),piRoot.get("idNumber").alias("identificationNo"),hpmRoot.get("brokerCode").alias("intermediaryRefNo"),
				hpmRoot.get("policyNo").alias("policyNo"),hpmRoot.get("quoteNo").alias("quoteNo"),hpmRoot.get("inceptionDate").alias("inceptionDate"),
				hpmRoot.get("expiryDate").alias("expiryDate"),hpmRoot.get("currency").alias("currency"),hpmRoot.get("debitNoteNo").alias("debitNoteNo"),
				vrnNumber.alias("vrnNumber"),tinNumber.alias("tinNumber"),cb.selectCase().when(cb.equal(hpmRoot.get("subUserType"), "b2c"), "DIRECT")
				.when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")),hpmRoot.get("customerName"))
					.otherwise(brokerName).alias("brokerName"),hpmRoot.get("productId").alias("productId"),
//				cb.selectCase().when(cb.isNull(hpmRoot.get("endtTypeId")), cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(currencyId), hpmRoot.get("premiumLc")).otherwise(hpmRoot.get("premiumFc")))
//					.otherwise(hpmRoot.get("endtPremium")).alias("premium"),
//				cb.selectCase().when(cb.isNull(hpmRoot.get("endtTypeId")), cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(currencyId), hpmRoot.get("vatPremiumLc")).otherwise(hpmRoot.get("vatPremiumFc")))
//					.otherwise(cb.quot(cb.prod(hpmRoot.get("endtPremium"), hpmRoot.get("vatPercent")), 100)).alias("vatPremium"),
//				cb.selectCase().when(cb.isNull(hpmRoot.get("endtTypeId")), cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(currencyId), hpmRoot.get("overallPremiumLc")).otherwise(hpmRoot.get("overallPremiumFc")))
//					.otherwise(cb.sum(hpmRoot.get("endtPremium"),cb.quot(cb.prod(hpmRoot.get("endtPremium"), hpmRoot.get("vatPercent")), 100))).alias("overAllPremium"),
				hpmRoot.get("vatPercent").alias("vatPercent"),pdRoot.get("bankName").alias("bankName"),pdRoot.get("accountNumber").alias("accountNumber"),
				sumInsured.alias("totSumInsured"),hpmRoot.get("companyId").alias("companyId"),hpmRoot.get("branchCode").alias("branchCode"),hpmRoot.get("branchName").alias("branchName"))//,companyName.alias("companyName"),imageURL.alias("companyLogo"))
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
					mddRoot.get("motorCategoryDesc").alias("motorCategoryDesc"),mddRoot.get("policyTypeDesc").alias("policyTypeDesc"))
						.where(cb.equal(mddRoot.get("quoteNo"), map.get("quoteNo")));
			List<Tuple> dataset1 = em.createQuery(cq1).getResultList();
			dataset1.forEach(k -> {
				TaxDataSetOneRes p = TaxDataSetOneRes.builder()
					.registrationNumber(k.get("registrationNumber")==null?"":k.get("registrationNumber").toString())
					.motorCategoryDesc(k.get("motorCategoryDesc")==null?"":k.get("motorCategoryDesc").toString())
					.build();
				dataset1Res.add(p);
			});
			String companyId = map.get("companyId")==null?"":map.get("companyId").toString();
			String branchCode = map.get("branchCode")==null?"":map.get("branchCode").toString();
			CriteriaQuery<Tuple> bankdetails = cb.createQuery(Tuple.class);
			Root<ListItemValue> lRoot = bankdetails.from(ListItemValue.class);
			Subquery<Integer> bankAmd = bankdetails.subquery(Integer.class);
			Root<ListItemValue> bankAmdRoot = bankAmd.from(ListItemValue.class);
			Predicate ba1 = cb.equal(bankAmdRoot.get("itemType"), lRoot.get("itemType"));
			Predicate ba2 = cb.equal(bankAmdRoot.get("companyId"), lRoot.get("companyId"));
			Predicate ba3 = cb.equal(bankAmdRoot.get("branchCode"), lRoot.get("branchCode"));
			Predicate ba4 = cb.equal(bankAmdRoot.get("status"), lRoot.get("status"));
			bankAmd.select(cb.max(bankAmdRoot.get("amendId"))).where(ba1,ba2,ba3,ba4);
			
			Predicate b1 = cb.equal(lRoot.get("itemType"), "BANK_DETAILS");
			Predicate b2 = cb.equal(lRoot.get("companyId"), companyId);
			Predicate b3 = cb.equal(lRoot.get("status"), "Y");
			Predicate b4 = cb.equal(lRoot.get("amendId"), bankAmd);
			bankdetails.multiselect(lRoot.get("itemCode").alias("itemCode"),lRoot.get("itemValue").alias("itemValue"));
			if("100019".equalsIgnoreCase(companyId)) {
				Predicate b5 = cb.equal(lRoot.get("branchCode"), branchCode);
				bankdetails.where(b1,b2,b3,b4,b5);
			}else {
				bankdetails.where(b1,b2,b3,b4);
			}
			List<Tuple> bankDetailsList = em.createQuery(bankdetails).getResultList();
			
			List<Map<String,Object>> bankList = new ArrayList<>();
			bankDetailsList.forEach(b -> {
				Map<String,Object> Bmap = new HashMap<>();
				Bmap.put(b.get("itemCode").toString(),b.get("itemValue"));
				bankList.add(Bmap);
			});
			
			for(Map<String,Object>entry : bankList) {
				if(entry.containsKey("ACCOUNT_NUMBER"))
					response.setBankaccountNumber(entry.get("ACCOUNT_NUMBER")==null?"":entry.get("ACCOUNT_NUMBER").toString());
				else if(entry.containsKey("ACCOUNT_NAME"))
					response.setBankaccountName(entry.get("ACCOUNT_NAME")==null?"":entry.get("ACCOUNT_NAME").toString());
				else if(entry.containsKey("ADDRESS"))
					response.setBankaddress(entry.get("ADDRESS")==null?"":entry.get("ADDRESS").toString());
				else if(entry.containsKey("SWIFT CODE"))
					response.setBankswiftCode(entry.get("SWIFT CODE")==null?"":entry.get("SWIFT CODE").toString());
				else if(entry.containsKey("ACCOUNT_NUMBER_USD"))
					response.setBankaccountUSD(entry.get("ACCOUNT_NUMBER_USD")==null?"":entry.get("ACCOUNT_NUMBER_USD").toString());
			}
			
			Double taxAmount=0.0;
			if(Arrays.asList(5,46).contains(map.get("productId"))) {
				List<PolicyDrcrDetail> drcrDetails = drcrdetail.findByQuoteNoAndStatusIn(map.get("quoteNo")==null?"":map.get("quoteNo").toString(),Arrays.asList("Y","CV"));
				List<PolicyDrcrDetail> listByRiskId = drcrDetails.stream().filter(r -> r.getDrcrFlag().equalsIgnoreCase("DR") && !r.getChargeCode().equals(new BigDecimal(1007))
						&& !r.getChargeCode().equals(new BigDecimal(1005))).sorted(Comparator.comparing(PolicyDrcrDetail::getDisplayOrder)).collect(Collectors.toList());
				listByRiskId.forEach(h ->{
					TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
						.amount(h.getAmountFc()==null?"":new BigDecimal(Double.parseDouble(h.getAmountFc().toString())).toPlainString())
						.narration(h.getNarration()==null?"":h.getNarration().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
						.status(h.getStatus())
					.build();
					premiumDetailsRes.add(u);
			});
			}else {
				List<PolicyCoverData> coverData = coverDataRepository.findByQuoteNo(map.get("quoteNo")==null?"":map.get("quoteNo").toString());
				if(coverData!=null && !coverData.isEmpty()) {
					Double taxRate = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T"))
							.map(m -> m.getTaxRate()).map(BigDecimal::doubleValue)
							.findAny().orElse(0.0);
					
					taxAmount = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T") && f.getSectionId()!=99999)
							.map(i -> i.getTaxAmount()).collect(Collectors.summingDouble(BigDecimal::doubleValue));
					
					response.setVatPercent(taxRate.toString());
					response.setVatAmount(taxAmount.toString());
					
					List<Map<String,Object>> sectionPremium = coverData.stream().filter(f ->f.getTaxId()==0 && f.getDiscLoadId()==0 && f.getSectionId()!=99999)
							.collect(Collectors.groupingBy(a -> a.getSectionId(),Collectors.groupingBy(b -> b.getCoverDesc(),Collectors.reducing(
								BigDecimal.ZERO, PolicyCoverData::getPremiumExcludedTaxLc, BigDecimal::add))))
							.entrySet().stream()
							.flatMap((Map.Entry<Integer,Map<String,BigDecimal>> s ) -> {
								Integer sectionId = s.getKey();
								return s.getValue().entrySet().stream()
										.map((Map.Entry<String,BigDecimal> g )-> {
											String coverDesc = g.getKey();
											BigDecimal totPremium = g.getValue();
											Map<String,Object> secMap = new HashMap<String,Object>();
											secMap.put("SectionId", sectionId);
											secMap.put("CoverDesc", coverDesc);
											secMap.put("TotPremium", totPremium);
											return secMap;
										});
							}).sorted(Comparator.comparing(p -> (String) p.get("CoverDesc")))
							.collect(Collectors.toList());
					sectionPremium.forEach(k -> {
						TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
								.amount(new BigDecimal(Double.valueOf(k.get("TotPremium").toString())).toString())
								.narration(k.get("CoverDesc")==null?"":k.get("CoverDesc").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
							.build();
							premiumDetailsRes.add(u);
					});
				}
			}
			
			
			OverAllPremium = premiumDetailsRes.stream().map(k -> new BigDecimal(k.getAmount())).collect(Collectors.summingDouble(BigDecimal::doubleValue))+taxAmount;
			String amtInWords="";
			if(OverAllPremium!=null) {
				amtInWords = motorRepo.getAmountByWords(OverAllPremium);
			}
			
			List<Map<String,Object>> companyDetails = insuranceComMasRepo.getCompanyDetailsById(map.get("companyId")==null?"":map.get("companyId").toString());
			if(!companyDetails.isEmpty()) {
				response.setCompanyName(companyDetails.get(0).get("COMPANY_NAME")==null?"":companyDetails.get(0).get("COMPANY_NAME").toString());
				response.setCompanyLogo(companyDetails.get(0).get("COMPANY_LOGO")==null?"":companyDetails.get(0).get("COMPANY_LOGO").toString());
				response.setCompanyWebsite(companyDetails.get(0).get("COMPANY_WEBSITE")==null?"":companyDetails.get(0).get("COMPANY_WEBSITE").toString());
				response.setCompanyMail(companyDetails.get(0).get("COMPANY_EMAIL")==null?"":companyDetails.get(0).get("COMPANY_EMAIL").toString());
				response.setCompanyPhone(companyDetails.get(0).get("COMPANY_PHONE")==null?"":companyDetails.get(0).get("COMPANY_PHONE").toString());
				response.setCompanyAddress(companyDetails.get(0).get("COMPANY_ADDRESS")==null?"":companyDetails.get(0).get("COMPANY_ADDRESS").toString());
				response.setCompanyPoBox(companyDetails.get(0).get("PO_BOX")==null?"":companyDetails.get(0).get("PO_BOX").toString());
			}
			
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
			response.setTinNumber(map.get("tinNumber")==null?null:map.get("tinNumber").toString());
			response.setBrokerName(map.get("brokerName")==null?"":map.get("brokerName").toString());
			response.setOverAllPremium(new BigDecimal(OverAllPremium).toPlainString());
			response.setTotSumInsured(map.get("totSumInsured")==null?"":new BigDecimal(Double.valueOf(map.get("totSumInsured").toString())).toString());
			response.setIntermediaryRefNo(map.get("intermediaryRefNo")==null?"":map.get("intermediaryRefNo").toString());
			response.setBranchCode(map.get("branchCode")==null?"":map.get("branchCode").toString());
			response.setBranchName(map.get("branchName")==null?"":map.get("branchName").toString());
			response.setPolicyType(!dataset1.isEmpty()?dataset1.get(0).get("policyTypeDesc")==null?"":dataset1.get(0).get("policyTypeDesc").toString():"");
			response.setCompanyId(map.get("companyId")==null?"":map.get("companyId").toString());
			response.setAmountInWords(amtInWords);
			response.setDataset1List(dataset1Res);
			response.setPremiumDetails(premiumDetailsRes);
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
		Double OverAllPremium=0.0;
	try {
		List<CreditDataSetOne> DataSetOneRes = new ArrayList<CreditDataSetOne>();
		List<CreditDataSetTwo> DataSetTwoRes = new ArrayList<CreditDataSetTwo>();
		List<TaxInvoicePremiumDetails> premiumDetailsRes = new ArrayList<>();
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
		
		Subquery<String> companyName = cq.subquery(String.class);
		Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
		//AMD MAX
		Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
		Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
		companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
		companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
				cb.equal(companyNameRoot.get("amendId"), companyNameAmd));
		
		Subquery<String> imageURL = cq.subquery(String.class);
		Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
		//AMD MAX
		Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
		Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
		imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
		imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
				cb.equal(imageURLRoot.get("amendId"), imageURLAmd));
		
		cq.multiselect(cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")), hpmRoot.get("customerName"))
				.otherwise(luiRoot.get("userName")).alias("brokerName"),
			cb.concat(piRoot.get("titleDesc"), cb.concat(".", piRoot.get("clientName"))).alias("customerName"),
			cb.concat(piRoot.get("address1"), cb.concat(",", cb.concat(cb.coalesce(piRoot.get("pinCode"), ""), cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("pinCode")), "")
					.when(cb.equal(piRoot.get("pinCode"), ""), "").otherwise(",").as(String.class), cb.concat(piRoot.get("stateName"), 
					cb.concat(",", cb.concat(piRoot.get("cityName"), cb.concat(",\n", countryName)))))))).alias("address"),
			hpmRoot.get("branchName").alias("branchName"),hpmRoot.get("creditNo").alias("creditNo"),hpmRoot.get("currency").alias("currency"),
			hpmRoot.get("productName").alias("productName"),cb.selectCase().when(cb.equal(hpmRoot.get("endtCount"), "0"), "NEW BUSINESS").otherwise("ENDORSEMENT").alias("business"),
			cb.selectCase().when(cb.isNull(hpmRoot.get("originalPolicyNo")), hpmRoot.get("policyNo")).otherwise(hpmRoot.get("originalPolicyNo")).alias("policyNo"),
			cb.selectCase().when(cb.isNotNull(hpmRoot.get("originalPolicyNo")), hpmRoot.get("policyNo")).alias("endorsementNo"),hpmRoot.get("endtTypeId").alias("endtTypeId"),
			hpmRoot.get("endtTypeDesc").alias("endtTypeDesc"),hpmRoot.get("endorsementRemarks").alias("endorsementRemarks"),hpmRoot.get("inceptionDate").alias("inceptionDate"),
			hpmRoot.get("expiryDate").alias("expiryDate"),hpmRoot.get("agencyCode").alias("agencyCode"),hpmRoot.get("customerId").alias("customerId"),hpmRoot.get("approvedBy").alias("approvedBy"),
//			cb.selectCase().when(cb.equal(hpmRoot.get("endtCount"), "0"), hpmRoot.get("commission")).when(cb.isNotNull(hpmRoot.get("creditNo")), hpmRoot.get("commission")).alias("premium"),
//			cb.selectCase().when(cb.equal(hpmRoot.get("endtCount"), "0"), cb.quot(cb.prod(hpmRoot.get("commission"), hpmRoot.get("vatPercent")), 100)).when(cb.isNotNull(hpmRoot.get("creditNo")), 
//					cb.quot(cb.prod(hpmRoot.get("commission"), hpmRoot.get("vatPercent")), 100)).alias("vatPremiumFc"),
//			cb.selectCase().when(cb.equal(hpmRoot.get("endtCount"), "0"), cb.sum(hpmRoot.get("commission"), cb.quot(cb.prod(hpmRoot.get("commission"), hpmRoot.get("vatPercent")), 100)))
//				.when(cb.isNotNull(hpmRoot.get("creditNo")), cb.sum(hpmRoot.get("commission"), cb.quot(cb.prod(hpmRoot.get("commission"), hpmRoot.get("vatPercent")), 100))).alias("overAllPremiumFc"),
			hpmRoot.get("vatPercent").alias("vatPercent"),hpmRoot.get("quoteNo").alias("quoteNo"),hpmRoot.get("customerCode").alias("customerCode"),companyName.alias("companyName"),
			imageURL.alias("companyLogo"),piRoot.get("vrTinNo").alias("vatRegNo"),hpmRoot.get("companyId").alias("companyId"))
		.where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId")),
				cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")),cb.equal(hpmRoot.get("loginId"), luiRoot.get("loginId")),
				cb.equal(icmRoot.get("amendId"), icmAmd),cb.in(hpmRoot.get("status")).value(Arrays.asList("P","D")),cb.equal(hpmRoot.get("policyNo"), policyNo))
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
			List<PolicyDrcrDetail> drcrDetails = drcrdetail.findByQuoteNoAndStatusIn(map.get("quoteNo")==null?"":map.get("quoteNo").toString(),Arrays.asList("Y","CV"));
			if(!drcrDetails.isEmpty()) {
				if("100019".equalsIgnoreCase(map.get("companyId")==null?"":map.get("companyId").toString())) {
					List<PolicyDrcrDetail> drcrList = drcrDetails.stream().filter(f -> f.getDrcrFlag().equalsIgnoreCase("CR")&& !f.getChargeCode().equals(new BigDecimal(1005))).sorted(Comparator.comparing(PolicyDrcrDetail::getDisplayOrder)).collect(Collectors.toList());
					drcrList.forEach(h -> {
						TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
								.amount(h.getAmountFc()==null?"":new BigDecimal(Double.parseDouble(h.getAmountFc().toString())).toPlainString())
								.narration(h.getNarration()==null?"":h.getNarration().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
								.status(h.getStatus())
							.build();
							premiumDetailsRes.add(u);
					});
					Double WHTLevy = drcrList.stream().filter(f -> f.getChargeCode().equals(new BigDecimal("1008")) || f.getChargeCode().equals(new BigDecimal("1009")))
							.map(h -> h.getAmountFc()).collect(Collectors.summingDouble(BigDecimal::doubleValue));
					Double Commission = drcrList.stream().filter(f -> f.getChargeCode().equals(new BigDecimal("1006"))).map(h -> h.getAmountFc())
							.map(BigDecimal::doubleValue).findFirst().get();
					Double VAT = drcrList.stream().filter(f -> f.getChargeCode().equals(new BigDecimal("1007"))).map(h -> h.getAmountFc())
							.map(BigDecimal::doubleValue).findFirst().get();
					OverAllPremium = (Commission - WHTLevy) + VAT;
				}else {
					List<PolicyDrcrDetail> listByRiskId = drcrDetails.stream().filter(r -> r.getDrcrFlag().equalsIgnoreCase("CR") && !r.getChargeCode().equals(new BigDecimal(1007)) && !r.getChargeCode().equals(new BigDecimal(1005))).sorted(Comparator.comparing(PolicyDrcrDetail::getDisplayOrder)).collect(Collectors.toList());
					listByRiskId.forEach(h ->{
						TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
							.amount(h.getAmountFc()==null?"":new BigDecimal(Double.parseDouble(h.getAmountFc().toString())).toPlainString())
							.narration(h.getNarration()==null?"":h.getNarration().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
						.build();
						premiumDetailsRes.add(u);
				});
					OverAllPremium = premiumDetailsRes.stream().map(k -> new BigDecimal(k.getAmount())).collect(Collectors.summingDouble(BigDecimal::doubleValue));
				}
			}
			
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
			response.setOverAllPremiumFc(new BigDecimal(OverAllPremium).toPlainString());
			response.setQuoteNo(map.get("quoteNo")==null?"":map.get("quoteNo").toString());
			response.setCompanyLogo(map.get("companyLogo")==null?"":map.get("companyLogo").toString());
			response.setCompanyName(map.get("companyName")==null?"":map.get("companyName").toString());
			response.setCustomerCode(map.get("customerCode")==null?"":map.get("customerCode").toString());
			response.setVatRegNo(map.get("vatRegNo")==null?"":map.get("vatRegNo").toString());
			response.setCompanyId(map.get("customerId")==null?"":map.get("customerId").toString());
			response.setSectionDescList(DataSetOneRes);
			response.setRiskCodeList(DataSetTwoRes);
			response.setPremiumDetails(premiumDetailsRes);
		}
	}catch(Exception e) {
		log.info("Error in getCreditNoteRes ==>" + e.getMessage());
		e.printStackTrace();
	}
	log.info("Exit into getCreditNoteRes");
		return response;
	}

	@SuppressWarnings("serial")
	public MotorPrivateRes getMotorPrivate(String policyNo,String quoteNo,String vehicleId) {
		log.info("Enter into getMotorPrivate.\nArgument ==> PolicyNo :"+policyNo+" || \t QuoteNo :"+quoteNo+" || \t VehicleId :"+vehicleId);
		MotorPrivateRes response = new MotorPrivateRes();
		List<TaxInvoicePremiumDetails> premiumDetailsRes = new ArrayList<>();
		Double OverAllPremium = 0d;
	try {
		List<MotorPrivateVehicleDetails> vehicleDetailsRes = new ArrayList<>();
		List<MotorPrivateDriverDetails> driverDetailsRes = new ArrayList<>();
		List<MotorPrivateAccessoriesDetails> accessoriesDetailsRes = new ArrayList<>();
		List<TearmsAndCondition> tearmsAndConditionRes = new ArrayList<>();
		List<AttachMentRes> attachments = new ArrayList<>();
		List<CoverDetailsRes> coverDetailsRes = new ArrayList<>();
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
		MotorCount.select(cb.count(SubMCRoot));
				Predicate mc1 = cb.equal(SubMCRoot.get("policyNo"), hpmRoot.get("policyNo"));
				if(StringUtils.isNotBlank(vehicleId)) {
					Predicate mc2 = cb.equal(SubMCRoot.get("vehicleId"), vehicleId);
					MotorCount.where(mc1,mc2);
				}else {
					MotorCount.where(mc1);
				}
		
		Subquery<String> companyName = cq.subquery(String.class);
		Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
		//AMD MAX
		Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
		Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
		companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
		companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
				cb.equal(companyNameRoot.get("amendId"), companyNameAmd));
		
		
		//COMPANY_LOGO
		Subquery<String> imageURL = cq.subquery(String.class);
		Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
		//AMD MAX
		Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
		Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
		imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
		imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
				cb.equal(imageURLRoot.get("amendId"), imageURLAmd));
		
		//SIGNATURE_IMG
		Subquery<String> signimageURL = cq.subquery(String.class);
		Root<InsuranceCompanyMaster> signimageURLRoot = signimageURL.from(InsuranceCompanyMaster.class);
		//AMD MAX
		Subquery<Integer> signimageURLAmd = cq.subquery(Integer.class);
		Root<InsuranceCompanyMaster> signimageURLAmdRoot = signimageURLAmd.from(InsuranceCompanyMaster.class);
		signimageURLAmd.select(cb.max(signimageURLAmdRoot.get("amendId"))).where(cb.equal(signimageURLAmdRoot.get("companyId"), signimageURLRoot.get("companyId")));
		signimageURL.select(signimageURLRoot.get("signature")).where(cb.equal(signimageURLRoot.get("companyId"), hpmRoot.get("companyId")),
				cb.equal(signimageURLRoot.get("amendId"), signimageURLAmd));
		
		
		Subquery<String> attachment = cq.subquery(String.class);
		Root<InsuranceCompanyMaster> attachmentRoot = attachment.from(InsuranceCompanyMaster.class);
		//AMD MAX
		Subquery<Integer> attachmentAmd = cq.subquery(Integer.class);
		Root<InsuranceCompanyMaster> attachmentAmdRoot = attachmentAmd.from(InsuranceCompanyMaster.class);
		attachmentAmd.select(cb.max(attachmentAmdRoot.get("amendId"))).where(cb.equal(attachmentAmdRoot.get("companyId"), attachmentRoot.get("companyId")));
		attachment.select(attachmentRoot.get("remarks")).where(cb.equal(attachmentRoot.get("companyId"), hpmRoot.get("companyId")),
				cb.equal(attachmentRoot.get("amendId"), attachmentAmd));
				
		cq.multiselect(cpmRoot.get("companyId").alias("companyId"),cpmRoot.get("effectiveDateStart").alias("effectiveDateStart"),cpmRoot.get("effectiveDateEnd").alias("effectiveDateEnd"),
			hpmRoot.get("policyNo").alias("policyNo"),hpmRoot.get("quoteNo").alias("quoteNo"),cb.concat(piRoot.get("titleDesc"), cb.concat(".", piRoot.get("clientName"))).alias("customerName"),
			hpmRoot.get("debitNoteNo").alias("debitNoteNo"),cb.concat(piRoot.get("address1"), cb.concat(",", cb.concat(piRoot.get("cityName"),
				cb.concat(" Street", cb.concat(",", cb.concat(piRoot.get("stateName"), cb.concat(",", countryName))))))).alias("address"),
			cb.selectCase().when(cb.isNotNull(piRoot.get("pinCode")), cb.concat("P.O.BOX ", cb.concat(piRoot.get("pinCode"), cb.concat(",", cb.concat(piRoot.get("cityName"),
				cb.concat(" Street",cb.concat(",", cb.concat(piRoot.get("stateName"), cb.concat(",", countryName))))))))).otherwise("").alias("postalAddress"),hpmRoot.get("inceptionDate").alias("inceptionDate"),
			hpmRoot.get("expiryDate").alias("expiryDate"),hpmRoot.get("currency").alias("currency"),
			mddRoot.get("insuranceTypeDesc").alias("insuranceTypeDesc"),cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")), hpmRoot.get("premiumLc"))
			.otherwise(hpmRoot.get("premiumFc")).alias("premium"),cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")), hpmRoot.get("vatPremiumLc"))
			.otherwise(hpmRoot.get("vatPremiumFc")).alias("vatPremium"),cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")), hpmRoot.get("overallPremiumLc"))
			.otherwise(hpmRoot.get("overallPremiumFc")).alias("totalPremium"),cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")),"LC").otherwise("FC").alias("vehiclePremiumDesc"),
			hpmRoot.get("branchName").alias("branchName"),hpmRoot.get("approvedBy").alias("approvedBy"),
			cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")), hpmRoot.get("customerName"))
			.otherwise(luiRoot.get("userName")).alias("userName"),MotorCount.alias("noOfVehicle"),companyName.alias("companyName"),
			imageURL.alias("companylogo"),hpmRoot.get("coverNoteReferenceNo").alias("coverNoteReferenceNo"),piRoot.get("customerId").alias("customerId"),
			cb.selectCase().when(cb.equal(hpmRoot.get("endtCount"), "0"), "NEW BUSINESS").otherwise("ENDORSEMENT").alias("business"),signimageURL.alias("signImg"),
			attachment.alias("attachment"),hpmRoot.get("loginId").alias("loginId"),luiRoot.get("brokerLogo").alias("brokerLogo"),hpmRoot.get("vatPercent").alias("vatPercent"),
			cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")), mddRoot.get("actualPremiumLc"))
			.otherwise(mddRoot.get("actualPremiumFc")).alias("vehiclePremium"),mddRoot.get("vatPremium").alias("vehicleVatPremium"),cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")), mddRoot.get("overallPremiumLc"))
			.otherwise(mddRoot.get("overallPremiumFc")).alias("vehicelTotalPremium"),hpmRoot.get("subUserType").alias("subUserType"))
		.where(StringUtils.isBlank(policyNo)?cb.equal(mddRoot.get("quoteNo"), hpmRoot.get("quoteNo")):cb.equal(mddRoot.get("policyNo"), hpmRoot.get("policyNo")),
				cb.equal(piRoot.get("customerId"), hpmRoot.get("customerId")),cb.equal(hpmRoot.get("loginId"), luiRoot.get("loginId")),
				cb.equal(cpmRoot.get("companyId"), hpmRoot.get("companyId")),cb.equal(cpmRoot.get("status"), "Y"),cb.equal(hpmRoot.get("productId"), cpmRoot.get("productId")),
				cb.between(cb.literal(new Date()), cpmRoot.get("effectiveDateStart"), cpmRoot.get("effectiveDateEnd")),
				StringUtils.isBlank(policyNo)?cb.equal(hpmRoot.get("quoteNo"), quoteNo):cb.equal(hpmRoot.get("policyNo"), policyNo)).distinct(true);
		
		List<Tuple> list = em.createQuery(cq).getResultList();
		if(!CollectionUtils.isEmpty(list)) {
			Tuple map = list.get(0);
			List<MotorDataDetails> vehicleDetails = motorRepo.findByQuoteNoOrderByVehicleIdAsc(map.get("quoteNo").toString())
						.stream().filter(f -> !f.getStatus().equalsIgnoreCase("D")).collect(Collectors.toList());
			if(StringUtils.isNotBlank(vehicleId)) {
				vehicleDetails = vehicleDetails.stream().filter(f -> f.getVehicleId()!=null && f.getVehicleId().equalsIgnoreCase(vehicleId))
						.collect(Collectors.toList());
			}
			String vehiclePremiumDesc = map.get("vehiclePremiumDesc")==null?"":map.get("vehiclePremiumDesc").toString();
			vehicleDetails.forEach(k -> {
				MotorPrivateVehicleDetails t = MotorPrivateVehicleDetails.builder()
					.vehicleId(k.getVehicleId()==null?"":k.getVehicleId().toString())
					.registrationNumber(k.getRegistrationNumber()==null?"":k.getRegistrationNumber().toString())
					.vehicleMake(k.getVehicleMake()==null?"":k.getVehicleMake().toString())
					.vehcileModel(k.getVehcileModel()==null?"":k.getVehcileModel().toString())
					.vehicleTypeDesc(k.getVehicleTypeDesc()==null?"":k.getVehicleTypeDesc().toString())
					.cubicCapacity(k.getCubicCapacity()==null?"":k.getCubicCapacity().toString())
					.manufactureYear(k.getManufactureYear()==null?"":k.getManufactureYear().toString())
					.seatingCapacity(k.getSeatingCapacity()==null?null:k.getSeatingCapacity().toString())
					.colorDesc(k.getColorDesc()==null?"":k.getColorDesc().toString())
					.policyTypeDesc(k.getPolicyTypeDesc()==null?"":k.getPolicyTypeDesc().toString())
					.policyTypeId(k.getPolicyType()==null?"":k.getPolicyType())
					.windScreenSumInsuredLc(k.getWindScreenSumInsured()==null?null:new BigDecimal(Double.parseDouble(k.getWindScreenSumInsured().toString())).toString())
					.sumInsured(k.getSumInsured()==null?null:new BigDecimal(Double.parseDouble(k.getSumInsured().toString())).toString())
					//.stickerNumber(map.get("stickerNumber")==null?"":map.get("stickerNumber").toString())
					.stickerNumber(getStrickerNo(k.getQuoteNo(),k.getVehicleId()))
					.grossWeight(k.getGrossWeight()==null?null:k.getGrossWeight().toString())
					.insTypeDesc(k.getInsuranceTypeDesc()==null?"":k.getInsuranceTypeDesc())
					.engineNumber(k.getEngineNumber()==null?"":k.getEngineNumber())
					.chassisNumber(k.getChassisNumber()==null?"":k.getChassisNumber())
					.premium("LC".equalsIgnoreCase(vehiclePremiumDesc)?k.getOverallPremiumLc()==null?"":
						new BigDecimal(Double.parseDouble(k.getOverallPremiumLc().toString())).toString():k.getOverallPremiumFc()==null?"":
							new BigDecimal(Double.parseDouble(k.getOverallPremiumFc().toString())).toString())
					.inceptionDate(map.get("inceptionDate")==null?"":sdf.format(map.get("inceptionDate")))
					.expiryDate(map.get("expiryDate")==null?"":sdf.format(map.get("expiryDate")))
					.policyNo(map.get("policyNo")==null?"":map.get("policyNo").toString())
					.customerName(map.get("customerName")==null?"":map.get("customerName").toString())
					.companyName(map.get("companyName")==null?"":map.get("companyName").toString())
					.build();
				vehicleDetailsRes.add(t);
			});
			
			CriteriaBuilder dbuilder = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> driverDtl = dbuilder.createQuery(Tuple.class);
			Root<MotorDriverDetails> dRoot = driverDtl.from(MotorDriverDetails.class);
			Root<MotorDataDetails> dmdRoot = driverDtl.from(MotorDataDetails.class);
			
			driverDtl.multiselect(dRoot.get("driverId").alias("driverId"),dRoot.get("driverName").alias("driverName"),
					dRoot.get("driverTypedesc").alias("driverTypedesc"),dRoot.get("driverDob").alias("driverDob"),
					dRoot.get("idNumber").alias("idNumber"),dmdRoot.get("chassisNumber").alias("chassisNumber"));
			List<Predicate> dPredicate = new ArrayList<Predicate>();
			dPredicate.add(cb.equal(dRoot.get("quoteNo"), map.get("quoteNo").toString()));
			dPredicate.add(cb.equal(dRoot.get("quoteNo"), dmdRoot.get("quoteNo")));
			dPredicate.add(cb.equal(dmdRoot.get("vehicleId"), dRoot.get("riskId").as(String.class)));
			dPredicate.add(cb.equal(dmdRoot.get("companyId"), dRoot.get("companyId")));
			dPredicate.add(cb.equal(dmdRoot.get("productId"), dRoot.get("productId")));
			if(StringUtils.isNotBlank(vehicleId)) {
				dPredicate.add(cb.equal(dRoot.get("riskId"), Integer.parseInt(vehicleId)));
			}
			Predicate dPredicateArray  [] = new Predicate[dPredicate.size()];
			dPredicate.toArray(dPredicateArray);
			driverDtl.where(dPredicateArray);
			List<Tuple> driverDetails = em.createQuery(driverDtl).getResultList();
			if(!driverDetails.isEmpty()) {
				driverDetails.forEach(k -> 
				driverDetailsRes.add(MotorPrivateDriverDetails.builder()
						.driverId(k.get("driverId")==null?"":k.get("driverId").toString())
						.driverName(k.get("driverName")==null?"":k.get("driverName").toString())
						.driverTypeDesc(k.get("driverTypedesc")==null?"":k.get("driverTypedesc").toString())
						.driverDOB(k.get("driverDob")==null?"":sdf.format(k.get("driverDob")))
						.iDNumber(k.get("idNumber")==null?"":k.get("idNumber").toString())
						.chassisNumber(k.get("chassisNumber")==null?"":k.get("chassisNumber").toString())
						.build()));
			}
			if(StringUtils.isNotBlank(vehicleId) && StringUtils.isNotBlank(policyNo)) {
				CriteriaBuilder coverdtl = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> pcd = coverdtl.createQuery(Tuple.class);
				Root<PolicyCoverData> pcdRoot = pcd.from(PolicyCoverData.class);
				Root<HomePositionMaster> phpmRoot = pcd.from(HomePositionMaster.class);
				Root<CompanyProductMaster> pcpmRoot = pcd.from(CompanyProductMaster.class);
				
				pcd.multiselect(pcdRoot.get("coverName").alias("coverName"),pcdRoot.get("sumInsured").alias("sumInsured"),
						cb.selectCase().when(cb.in(phpmRoot.get("currency")).value(pcpmRoot.get("currencyIds")), pcdRoot.get("premiumAfterDiscountLc"))
						.otherwise(pcdRoot.get("premiumAfterDiscountFc")).alias("premiumAfterDiscount"),
						cb.selectCase().when(cb.in(phpmRoot.get("currency")).value(pcpmRoot.get("currencyIds")), pcdRoot.get("premiumIncludedTaxLc"))
						.otherwise(pcdRoot.get("premiumIncludedTaxFc")).alias("premiumIncludedTax"),pcdRoot.get("vehicleId").alias("vehicleId"))
				.where(cb.equal(pcpmRoot.get("companyId"), phpmRoot.get("companyId")),cb.equal(pcpmRoot.get("status"),"Y"),
						cb.equal(phpmRoot.get("productId"), pcpmRoot.get("productId")),cb.between(cb.literal(new Date()), pcpmRoot.get("effectiveDateStart"), pcpmRoot.get("effectiveDateEnd")),
						cb.equal(pcdRoot.get("policyNo"), phpmRoot.get("policyNo")),cb.equal(pcdRoot.get("taxId"), 0),cb.equal(pcdRoot.get("discLoadId"), 0),
						cb.notEqual(pcdRoot.get("coverageType"), "B"),cb.equal(phpmRoot.get("policyNo"), policyNo));
				
				List<Tuple> coverDetailsList = em.createQuery(pcd).getResultList();
				if(!coverDetailsList.isEmpty()) {
					coverDetailsList.forEach(k -> {
						CoverDetailsRes m = CoverDetailsRes.builder()
								.coverName(k.get("coverName")==null?"":k.get("coverName").toString())
								.sumInsured(k.get("sumInsured")==null?null:k.get("sumInsured").toString())
								.premiumAfterDiscount(k.get("premiumAfterDiscount")==null?null:k.get("premiumAfterDiscount").toString())
								.premiumIncludedTax(k.get("premiumIncludedTax")==null?null:k.get("premiumIncludedTax").toString())
								.vehicleId(k.get("vehicleId")==null?"":k.get("vehicleId").toString())
								.build();
						coverDetailsRes.add(m);
					});
				}
			}
			
			List<ContentAndRisk> accessoriesDetails = conAndRiskRepo.findByQuoteNoOrderByRiskIdAsc(map.get("quoteNo").toString());
			accessoriesDetails.forEach(a -> {
				MotorPrivateAccessoriesDetails t = MotorPrivateAccessoriesDetails.builder()
						.itemNo(a.getItemId()==null?"":a.getItemId().toString())
						.itemDesc(a.getItemDesc()==null?"":a.getItemDesc().toString())
						.sumInsured(a.getSumInsured()==null?"":a.getSumInsured().toString())
						.serialNoDesc(a.getSerialNoDesc()==null?"":a.getSerialNoDesc())
						.build();
				accessoriesDetailsRes.add(t);
			});
			
			List<MotorDataDetails> collateralDetails = vehicleDetails.stream().filter(f -> "Y".equalsIgnoreCase(Objects.requireNonNullElse(f.getCollateralYn(),""))).collect(Collectors.toList());
			
			CriteriaQuery<Tuple> cq1 = cb.createQuery(Tuple.class);
			Root<PolicyCoverData> pcdRoot = cq1.from(PolicyCoverData.class);
			Root<SectionDataDetails> sddRoot = cq1.from(SectionDataDetails.class);
			
			List<Predicate> predicate = new ArrayList<Predicate>();
			predicate.add(cb.equal(pcdRoot.get("quoteNo"),map.get("quoteNo")));
			predicate.add(cb.equal(pcdRoot.get("quoteNo"),sddRoot.get("quoteNo")));
			predicate.add(cb.equal(pcdRoot.get("sectionId").as(String.class), sddRoot.get("sectionId")));
			predicate.add(cb.equal(pcdRoot.get("taxId"),"0"));
			predicate.add(cb.equal(pcdRoot.get("discLoadId"), "0"));
			predicate.add(cb.equal(pcdRoot.get("subCoverId"), "0"));
			Predicate [] predicateArray = new Predicate[predicate.size()];
			predicate.toArray(predicateArray);
			
			Subquery<String> occDesc = cq1.subquery(String.class);
			Root<EserviceCommonDetails> ecdRoot = occDesc.from(EserviceCommonDetails.class);
			occDesc.select(ecdRoot.get("occupationDesc")).where(cb.equal(pcdRoot.get("quoteNo"), ecdRoot.get("quoteNo")),cb.equal(pcdRoot.get("sectionId").as(String.class),ecdRoot.get("sectionId")),
					cb.equal(pcdRoot.get("vehicleId"), ecdRoot.get("riskId")),cb.equal(pcdRoot.get("productId").as(String.class), ecdRoot.get("productId")),
					cb.equal(pcdRoot.get("companyId"), ecdRoot.get("companyId")));
			
			cq1.multiselect(sddRoot.get("sectionId").alias("sectionId"),sddRoot.get("sectionDesc").alias("sectionDesc"),pcdRoot.get("coverDesc").alias("coverDesc"),
					pcdRoot.get("coverId").alias("coverId"),pcdRoot.get("coverageType").alias("coverageType"),
					 pcdRoot.get("sumInsured").alias("sumInsured"),pcdRoot.get("rate").alias("rate"),pcdRoot.get("premiumIncludedTaxLc").alias("premiumIncludedTaxLc"),
					 pcdRoot.get("premiumIncludedTaxFc").alias("premiumIncludedTaxFc"),occDesc.alias("occupationDesc"),
					 pcdRoot.get("premiumExcludedTaxLc").alias("premiumExcludedTaxLc"),pcdRoot.get("premiumExcludedTaxFc").alias("premiumExcludedTaxFc"))
			.where(predicateArray).orderBy(cb.asc(sddRoot.get("sectionId")));
					
		List<Tuple> Slist = em.createQuery(cq1).getResultList();
		
		List<Object> sectionIds = Slist.stream().map(k -> k.get("sectionId")).distinct().collect(Collectors.toList());
		for(int i=0;i<sectionIds.size();i++) {
			String sectionId = sectionIds.get(i).toString();
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
			termsAndconditions.stream().distinct().collect(Collectors.toList()).forEach(g ->{
				TearmsAndCondition tearms = new TearmsAndCondition();
				tearms.setAllConditions(g.get("conditionTerms")==null?"":g.get("conditionTerms").toString());
				tearmsAndConditionRes.add(tearms);
			});
		}
		if(StringUtils.isNotBlank(vehicleId)) {
			List<LinkedHashMap<String, Object>> vehiclePremiumList = new ArrayList<LinkedHashMap<String,Object>>(4);
			vehiclePremiumList.add(new LinkedHashMap<String,Object>(){{
				put("Premium",map.get("vehiclePremium")==null?"":new BigDecimal(Double.parseDouble(map.get("vehiclePremium").toString())));
			}});
			vehiclePremiumList.add(new LinkedHashMap<String,Object>(){{
				put("Vat Premium @ "+(map.get("vatPercent")==null?"":map.get("vatPercent").toString())+" %",
						map.get("vehicleVatPremium")==null?"":new BigDecimal(Double.parseDouble(map.get("vehicleVatPremium").toString())));			
			}});
			vehiclePremiumList.add(new LinkedHashMap<String,Object>(){{
				put("Total Premium",map.get("vehicelTotalPremium")==null?"":new BigDecimal(Double.parseDouble(map.get("vehicelTotalPremium").toString())));
			}});
			for (LinkedHashMap<String,Object> k : vehiclePremiumList) {
			    TaxInvoicePremiumDetails v = TaxInvoicePremiumDetails.builder()
			            .amount(k.values().stream().map(m -> {
			            	return BigDecimal.valueOf(Double.parseDouble(m.toString())).toString();
			            }).collect(Collectors.joining(", ")))
			            .narration(String.join(", ", k.keySet()))
			            .build();
			    premiumDetailsRes.add(v);
			}
		/*	List<MultiplePolicyDrCrDetail> vehiclePremiumDetails = multiPolicyDrCrDtlRepo.findByQuoteNoAndRiskId(map.get("quoteNo")==null?"":map.get("quoteNo").toString(),Integer.parseInt(vehicleId));
			if(!vehiclePremiumDetails.isEmpty()) {
				vehiclePremiumDetails = vehiclePremiumDetails.stream().filter(f -> f.getChargeCode().compareTo(new BigDecimal(1003)) !=0 && f.getChargeCode().compareTo(new BigDecimal(1004)) !=0).sorted(Comparator.comparing(MultiplePolicyDrCrDetail::getDisplayOrder)).collect(Collectors.toList());
				vehiclePremiumDetails.forEach(h ->{
					TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
						.amount(h.getAmountFc()==null?"":new BigDecimal(Double.parseDouble(h.getAmountFc().toString())).toPlainString())
						.narration(h.getNarration()==null?"":h.getNarration().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
					.build();
					premiumDetailsRes.add(u);
			});
			}*/
		}else {
			List<PolicyDrcrDetail> drcrDetails = drcrdetail.findByQuoteNoAndStatusIn(map.get("quoteNo")==null?"":map.get("quoteNo").toString(),Arrays.asList("Y","CV"));
			List<PolicyDrcrDetail> listByRiskId = drcrDetails.stream().filter(r -> r.getDrcrFlag().equalsIgnoreCase("DR") && !r.getChargeCode().equals(new BigDecimal(1007))
					&& !r.getChargeCode().equals(new BigDecimal(1005))).sorted(Comparator.comparing(PolicyDrcrDetail::getDisplayOrder)).collect(Collectors.toList());
			listByRiskId.forEach(h ->{
				TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
					.amount(h.getAmountFc()==null?"":new BigDecimal(Double.parseDouble(h.getAmountFc().toString())).toPlainString())
					.narration(h.getNarration()==null?"":h.getNarration().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
					.status(h.getStatus())
				.build();
				premiumDetailsRes.add(u);
		});
	}
		OverAllPremium = premiumDetailsRes.stream().map(k -> new BigDecimal(k.getAmount())).collect(Collectors.summingDouble(BigDecimal::doubleValue));
		
			String loginId = map.get("loginId")==null?"":map.get("loginId").toString();
			if(StringUtils.isNotBlank(loginId)) {
					CriteriaBuilder cb1 = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> doc = cb1.createQuery(Tuple.class);
					Root<DocumentUniqueDetails> dudRoot = doc.from(DocumentUniqueDetails.class);
					Root<ClausesMaster> cmRoot = doc.from(ClausesMaster.class);
					Root<LoginMaster> lmRoot = doc.from(LoginMaster.class);
					Root<LoginUserInfo> dlui = doc.from(LoginUserInfo.class);
					
					Subquery<Integer> cmAmd = cq.subquery(Integer.class);
					Root<ClausesMaster> cmAmdRoot = cmAmd.from(ClausesMaster.class);
					
					cmAmd.select(cb.max(cmAmdRoot.get("amendId"))).where(cb.equal(cmAmdRoot.get("brokerCode"), dlui.get("customerCode")),
							cb.equal(cmAmdRoot.get("clausesId"), cmRoot.get("clausesId")),cb.equal(cmAmdRoot.get("status"), cmRoot.get("status")));
					
					doc.multiselect(cmRoot.get("docRefNo").alias("docRefNo"),dudRoot.get("filePathOrginal").alias("filePathOrginal"))
					.where(cb.equal(lmRoot.get("loginId"), dlui.get("loginId")),cb.equal(dlui.get("customerCode"), cmRoot.get("brokerCode")),
							cb.equal(cmRoot.get("docRefNo").as(String.class), dudRoot.get("uniqueId").as(String.class)),cb.equal(lmRoot.get("loginId").as(String.class), loginId),
							cb.equal(cmRoot.get("status"), "Y"),cb.equal(cmRoot.get("amendId"), cmAmd));
					
					List<Tuple> docList = em.createQuery(doc).getResultList();
					if(!docList.isEmpty()) {
						docList.forEach(e -> {
							AttachMentRes m = AttachMentRes.builder()
									.docRefNo(e.get("docRefNo")==null?"":e.get("docRefNo").toString())
									.docloction(e.get("filePathOrginal")==null?"":e.get("filePathOrginal").toString())
									.build();
							attachments.add(m);
						});
					}
				}
			String polNo = map.get("policyNo")==null?"":map.get("policyNo").toString();
			if(StringUtils.isNotBlank(vehicleId)) {
				polNo+="/"+vehicleId;
			}
			String subUserType = map.get("subUserType")==null?"":map.get("subUserType").toString();
			response.setCustomerId(map.get("customerId")==null?"":map.get("customerId").toString());
			response.setCompanyId(map.get("companyId")==null?"":map.get("companyId").toString());
			response.setEffectiveDateStart(map.get("effectiveDateStart")==null?"":map.get("effectiveDateStart").toString());
			response.setEffectiveDateEnd(map.get("effectiveDateEnd")==null?"":map.get("effectiveDateEnd").toString());
			response.setPolicyNo(polNo);
			response.setQuoteNo(map.get("quoteNo")==null?"":map.get("quoteNo").toString());
			response.setCustomerName(map.get("customerName")==null?"":map.get("customerName").toString());
			response.setDebitNoteNo(map.get("debitNoteNo")==null?"":map.get("debitNoteNo").toString());
			response.setAddress(map.get("address")==null?"":map.get("address").toString());
			response.setInceptionDate(map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
			response.setExpiryDate(map.get("expiryDate")==null?"":map.get("expiryDate").toString());
			response.setRenewalDate(map.get("expiryDate")==null?"":RenewalDate(map.get("expiryDate").toString()));
			response.setCurrency(map.get("currency")==null?"":map.get("currency").toString());
			//response.setStickerNumber(map.get("stickerNumber")==null?"":map.get("stickerNumber").toString());
			response.setInsuranceTypeDesc(map.get("insuranceTypeDesc")==null?"":map.get("insuranceTypeDesc").toString());
			response.setPremium(map.get("premium")==null?"":new BigDecimal(Double.parseDouble(map.get("premium").toString())).toString());
			response.setVatPremium(map.get("vatPremium")==null?"":new BigDecimal(Double.parseDouble(map.get("vatPremium").toString())).toString());
			response.setOverAllPremium(map.get("totalPremium")==null?"":new BigDecimal(Double.parseDouble(map.get("totalPremium").toString())).toString());
			response.setTotalPremium(new BigDecimal(OverAllPremium).toString());
			response.setBranchName(map.get("branchName")==null?"":map.get("branchName").toString());
			response.setApprovedBy(map.get("approvedBy")==null?"":map.get("approvedBy").toString());
			response.setUserName(subUserType.toLowerCase().contains("b2c")?map.get("customerName")==null?"":map.get("customerName").toString():
					map.get("userName")==null?"":map.get("userName").toString());
			response.setNoOfVehicle(map.get("noOfVehicle")==null?"":map.get("noOfVehicle").toString());
			response.setPostalAddress(map.get("postalAddress")==null?"":map.get("postalAddress").toString());
			response.setBorrowerType(collateralDetails.isEmpty()?"":collateralDetails.get(0).getBorrowerTypeDesc());
			response.setCollateralName(collateralDetails.isEmpty()?"":collateralDetails.get(0).getCollateralName());
			response.setFirstLossPayee(collateralDetails.isEmpty()?"":collateralDetails.get(0).getFirstLossPayee());
			response.setCompanylogo(map.get("companylogo")==null?"":map.get("companylogo").toString());
			response.setCompanyName(map.get("companyName")==null?"":map.get("companyName").toString());
			response.setCoverNoteReferenceNo(map.get("coverNoteReferenceNo")==null?"":map.get("coverNoteReferenceNo").toString());
			response.setBusiness(map.get("business")==null?"":map.get("business").toString());
			response.setSignImg(map.get("signImg")==null?"":map.get("signImg").toString());
			response.setBrokerLogo(map.get("brokerLogo")==null?"":map.get("brokerLogo").toString());
			response.setVatPercent(map.get("vatPercent")==null?"":map.get("vatPercent").toString());
			response.setSubUserType(subUserType);
			response.setVehicleDetails(vehicleDetailsRes);
			response.setDriverDetails(driverDetailsRes);
			response.setAccessoriesDetails(accessoriesDetailsRes);
			response.setTearmsAndConditions(tearmsAndConditionRes);
			response.setPremiumDetails(premiumDetailsRes);
			response.setAttachmentList(attachments);
			response.setCoverDetailsList(coverDetailsRes);
		}
	}catch(Exception e) {
		log.info("Error in getMotorPrivate ==>"+e.getMessage());
		e.printStackTrace();
	}
	log.info("Exit into getMotorPrivate");
		return response;
	}
	
	private String getStrickerNo(String quoteNo,String vehicleId) {
		String result = "";
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<SectionDataDetails> stnoRoot = cq.from(SectionDataDetails.class);
			cq.select(stnoRoot.get("stickerNumber")).where(cb.equal(stnoRoot.get("quoteNo"), quoteNo),
					cb.equal(stnoRoot.get("riskId"),vehicleId));
			result = em.createQuery(cq).getSingleResult();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
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
		
		Subquery<String> companyName = cq.subquery(String.class);
		Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
		//AMD MAX
		Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
		Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
		companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
		companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
				cb.equal(companyNameRoot.get("amendId"), companyNameAmd));
		
		Subquery<String> imageURL = cq.subquery(String.class);
		Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
		//AMD MAX
		Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
		Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
		imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
		imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
				cb.equal(imageURLRoot.get("amendId"), imageURLAmd));
		
		cq.multiselect(hpmRoot.get("quoteNo").alias("quoteNo"),hpmRoot.get("policyNo").alias("policyNo"),cb.upper(cb.concat(piRoot.get("titleDesc"),
				cb.concat(".", piRoot.get("clientName")))).alias("customerName"),cb.concat(piRoot.get("address1"), cb.concat(cb.coalesce(piRoot.get("pinCode"), ""),
						cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("pinCode")), "").when(cb.equal(piRoot.get("pinCode"), ""), "").otherwise(",").as(String.class), cb.concat(piRoot.get("stateName"), 
								cb.concat(",", cb.concat(piRoot.get("cityName"), cb.concat(",", countryName))))))).alias("address"),
				piRoot.get("telephoneNo1").alias("telephoneNo1"),lmRoot.get("agencyCode").alias("agencyCode"),hpmRoot.get("inceptionDate").alias("inceptionDate"),hpmRoot.get("expiryDate").alias("expiryDate"),
				hpmRoot.get("currency").alias("currency"),cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(currencyId), overAllPremiumLc).otherwise(overAllPremiumFc).alias("overAllPremium"),
				cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(currencyId), premiumLc).otherwise(premiumFc).alias("premium"),
				noOfPassanger.alias("noOfPassanger"),companyName.alias("companyName"),imageURL.alias("companylogo"))
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
					cb.equal(hpmRoot2.get("productId").as(String.class), "4"),cb.equal(hpmRoot2.get("status"), "P"),cb.equal(hpmRoot2.get("quoteNo"), pcdRoot2.get("quoteNo")),cb.equal(pcdRoot2.get("vehicleId"), tpdRoot2.get("groupId")),
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
			response.setCompanylogo(map.get("companylogo")==null?"":map.get("companylogo").toString());
			response.setCompanyName(map.get("companyName")==null?"":map.get("companyName").toString());
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
		
		Subquery<String> companyName = cq.subquery(String.class);
		Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
		//AMD MAX
		Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
		Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
		companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
		companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
				cb.equal(companyNameRoot.get("amendId"), companyNameAmd));
		
		Subquery<String> imageURL = cq.subquery(String.class);
		Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
		//AMD MAX
		Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
		Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
		imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
		imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
				cb.equal(imageURLRoot.get("amendId"), imageURLAmd));
		
		//Refund or Not
		Subquery<String> payments = cq.subquery(String.class);
		Root<PaymentInfo> paymentsRoot = payments.from(PaymentInfo.class);
		
		Subquery<Integer> paymentAmd = cq.subquery(Integer.class);
		Root<PaymentInfo> paymentAmdRoot = paymentAmd.from(PaymentInfo.class);
		paymentAmd.select(cb.max(paymentAmdRoot.get("merchantReference"))).where(cb.equal(paymentAmdRoot.get("quoteNo"), paymentsRoot.get("quoteNo")),
				cb.equal(paymentAmdRoot.get("paymentStatus"), paymentsRoot.get("paymentStatus")));
		
		payments.select(paymentsRoot.get("payments")).where(cb.equal(paymentsRoot.get("quoteNo"), hpmRoot.get("quoteNo")),cb.equal(paymentsRoot.get("paymentStatus"), "ACCEPTED"),
				cb.equal(paymentsRoot.get("merchantReference"), paymentAmd));
		
		cq.multiselect(cb.concat(piRoot.get("titleDesc"), cb.concat(".", piRoot.get("clientName"))).alias("customerName"),
			hpmRoot.get("policyNo").alias("EndorsementNo"),hpmRoot.get("originalPolicyNo").alias("originalPolicyNo"),hpmRoot.get("effectiveDate").alias("effectiveDate"),
			hpmRoot.get("expiryDate").alias("expiryDate"),hpmRoot.get("inceptionDate").alias("inceptionDate"),hpmRoot.get("currency").alias("currency"),
			hpmRoot.get("endtPremium").alias("endtPremium"),hpmRoot.get("endtTypeDesc").alias("endtTypeDesc"),hpmRoot.get("endorsementRemarks").alias("endorsementRemarks"),
			hpmRoot.get("branchName").alias("branchName"),cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")),hpmRoot.get("customerName"))
			.otherwise(luiRoot.get("userName")).alias("userName"),hpmRoot.get("quoteNo").alias("quoteNo"),companyName.alias("companyName"),imageURL.alias("companylogo"),payments.alias("payments"))
		.where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId")),cb.equal(luiRoot.get("loginId"), hpmRoot.get("loginId")),
				cb.equal(hpmRoot.get("productId"), "5"),cb.in(hpmRoot.get("status")).value(Arrays.asList("P","D","E")),cb.equal(hpmRoot.get("policyNo"), policyNo))
		.orderBy(cb.asc(hpmRoot.get("entryDate")));
		
		List<Tuple> list = em.createQuery(cq).getResultList();
		if(!CollectionUtils.isEmpty(list)) {
			Tuple map = list.get(0);
			CriteriaBuilder cb1 = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq1 = cb1.createQuery(Tuple.class);
			Root<MotorDataDetails> mddRoot = cq1.from(MotorDataDetails.class);
			
			cq1.multiselect(mddRoot.get("insuranceClassDesc").alias("insuranceClassDesc"),mddRoot.get("registrationNumber").alias("registrationNumber"),
					mddRoot.get("chassisNumber").alias("chassisNumber"),mddRoot.get("borrowerTypeDesc").alias("borrowerTypeDesc"),mddRoot.get("collateralName").alias("collateralName"),
					mddRoot.get("firstLossPayee").alias("firstLossPayee"),mddRoot.get("collateralYn").alias("collateralYn"),mddRoot.get("sumInsured").alias("sumInsured"))
			.where(cb.equal(mddRoot.get("quoteNo"), map.get("quoteNo")));
			
			List<Tuple> vehicleInfo = em.createQuery(cq1).getResultList();
			List<Map<String,Object>> vehicleList = vehicleInfo.stream().map(k ->{
				LinkedHashMap<String, Object> vMap = new LinkedHashMap<String,Object>();
				vMap.put("InsuranceClassDesc", k.get("insuranceClassDesc")==null?"":k.get("insuranceClassDesc").toString());
				vMap.put("RegistrationNumber", k.get("registrationNumber")==null?"":k.get("registrationNumber").toString());
				vMap.put("ChassisNumber", k.get("chassisNumber")==null?"":k.get("chassisNumber").toString());
				return vMap;
			}).collect(Collectors.toList());
			
			List<LinkedHashMap<String,Object>> collateralDetails = vehicleInfo.stream().filter(k -> "Y".equals(Objects.requireNonNullElse(k.get("collateralYn"), ""))).map(m ->{
				LinkedHashMap<String,Object> cdMap = new LinkedHashMap<String,Object>();
				cdMap.put("BorrowerType", m.get("borrowerTypeDesc")==null?"":m.get("borrowerTypeDesc").toString());
				cdMap.put("CollateralName", m.get("collateralName")==null?"":m.get("collateralName").toString());
				cdMap.put("CollateralYn", m.get("collateralYn")==null?"":m.get("collateralYn").toString());
				cdMap.put("FirstLossPayee", m.get("firstLossPayee")==null?"":m.get("firstLossPayee").toString());
				return cdMap;
			}).collect(Collectors.toList());
			
			List<PaymentDetail> paymentDetail = paymentDetailRepo.findByQuoteNo(map.get("quoteNo").toString());
			List<LinkedHashMap<String,Object>> refundPaymentDetail = paymentDetail.stream().filter(f -> "REFUND".equalsIgnoreCase(Objects.requireNonNullElse(f.getPayments(), ""))).map(k ->{
				LinkedHashMap<String,Object> pMap = new LinkedHashMap<String,Object>();
				pMap.put("BankName",k.getBankName());
				pMap.put("AccountNumber",k.getAccountNumber());
				pMap.put("IbanNumber",k.getIbanNumber());
				return pMap;
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
			result.put("companyName", map.get("companyName")==null?"":map.get("companyName").toString());
			result.put("companylogo", map.get("companylogo")==null?"":map.get("companylogo").toString());
			result.put("payments", map.get("payments")==null?"":map.get("payments").toString());
			result.put("vehicleList", vehicleList);
			result.put("refundPaymentDetail", refundPaymentDetail);
			result.put("collateralDetails", collateralDetails);
			result.put("vehicleSumInsured", vehicleInfo.get(0).get("sumInsured")==null?null:new BigDecimal(Double.parseDouble(vehicleInfo.get(0).get("sumInsured").toString())).toString());
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
		
		Subquery<String> companyName = cq.subquery(String.class);
		Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
		//AMD MAX
		Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
		Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
		companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
		companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
				cb.equal(companyNameRoot.get("amendId"), companyNameAmd));
		
		Subquery<String> imageURL = cq.subquery(String.class);
		Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
		//AMD MAX
		Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
		Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
		imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
		imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
				cb.equal(imageURLRoot.get("amendId"), imageURLAmd));
		
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
			
			Subquery<String> companyName = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
			companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
			companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(companyNameRoot.get("amendId"), companyNameAmd));
			
			Subquery<String> imageURL = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(imageURLRoot.get("amendId"), imageURLAmd));
			
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
					piRoot.get("vrTinNo").alias("vrTinNo"),piRoot.get("email1").alias("email1"),piRoot.get("mobileNo1").alias("mobileNo1"),imageURL.alias("companyLogo"),luiRoot.get("brokerLogo").alias("brokerLogo"))
			.where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId"))/*,cb.equal(hpmRoot.get("currency"), icmRoot.get("currencyId"))*/,cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")),
					cb.equal(luiRoot.get("loginId"), hpmRoot.get("loginId")),cb.equal(icmRoot.get("amendId"), icmAmd),cb.in(hpmRoot.get("productId")).value(Arrays.asList(5,46)),cb.equal(hpmRoot.get("quoteNo"), QuoteNo))
			.orderBy(cb.desc(hpmRoot.get("entryDate")));
			
			List<Tuple> list = em.createQuery(cq).getResultList();
			if(!CollectionUtils.isEmpty(list)) {
				Tuple map = list.get(0);
				List<MotorDataDetails> list1 = motorRepo.findByQuoteNoOrderByVehicleIdAsc(map.get("quoteNo").toString());
				List<Map<String,Object>> vehicleList = list1.stream().map(k -> {
					LinkedHashMap<String,Object> m = new LinkedHashMap<String,Object>();
					m.put("policyTypeDesc", k.getPolicyTypeDesc()==null?"":StringUtils.capitalize(k.getPolicyTypeDesc()));
					m.put("registrationNumber", k.getRegistrationNumber()==null?"":k.getRegistrationNumber());
					m.put("vehicleMakeDesc", k.getVehicleMake()==null?"":StringUtils.capitalize(k.getVehicleMake()));
					m.put("vehicleTypeDesc", k.getVehicleTypeDesc()==null?"":StringUtils.capitalize(k.getVehicleTypeDesc()));
					m.put("chassisNumber", k.getChassisNumber()==null?"":k.getChassisNumber());
					m.put("colorDesc", k.getColorDesc()==null?"":StringUtils.capitalize(k.getColorDesc()));
					m.put("manufactureYear", k.getManufactureYear());
					m.put("engineNumber", k.getEngineNumber()==null?"":k.getEngineNumber());
					m.put("vehcileModelDesc", k.getVehcileModelDesc()==null?"":StringUtils.capitalize(k.getVehcileModelDesc()));
					m.put("sumInsured", k.getSumInsured());
					m.put("insuranceTypeDesc", k.getInsuranceTypeDesc()==null?"":k.getInsuranceTypeDesc());
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
				result.put("vatPercent", map.get("vatPercent")==null?null:map.get("vatPercent").toString());
				result.put("premium", map.get("premium")==null?"":map.get("premium").toString());
				result.put("vatPremium", map.get("vatPremium")==null?null:map.get("vatPremium").toString());
				result.put("overAllPremium", map.get("overAllPremium")==null?"":map.get("overAllPremium").toString());
				result.put("commissionPercentage", map.get("commissionPercentage")==null?null:map.get("commissionPercentage").toString());
				result.put("commission", map.get("commission")==null?null:map.get("commission").toString());
				result.put("branchName", map.get("branchName")==null?null:map.get("branchName").toString());
				result.put("inceptionDate", map.get("inceptionDate")==null?null:map.get("inceptionDate").toString());
				result.put("address", map.get("address")==null?"":map.get("address").toString());
				result.put("email1", map.get("email1")==null?"":map.get("email1").toString());
				result.put("mobileNo1", map.get("mobileNo1")==null?null:map.get("mobileNo1").toString());
				result.put("customerName", map.get("customerName")==null?"":map.get("customerName").toString());
				result.put("vrTinNo", map.get("vrTinNo")==null?null:map.get("vrTinNo").toString());
				result.put("expiryDate", map.get("expiryDate")==null?null:map.get("expiryDate").toString());
				result.put("companyLogo", map.get("companyLogo")==null?"":map.get("companyLogo").toString());
				result.put("brokerLogo", map.get("brokerLogo")==null?"":map.get("brokerLogo").toString());
				result.put("insuranceTypeDesc", vehicleList.get(0).get("insuranceTypeDesc")==null?"":vehicleList.get(0).get("insuranceTypeDesc").toString());
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
		List<AttachMentRes> attachments = new ArrayList<>();
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
			
			Subquery<String> companyName = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
			companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
			companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(companyNameRoot.get("amendId"), companyNameAmd));
			
			Subquery<String> imageURL = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(imageURLRoot.get("amendId"), imageURLAmd));
			
			cq.multiselect(hpmRoot.get("policyNo").alias("policyNo"),hpmRoot.get("quoteNo").alias("quoteNo"),hpmRoot.get("requestReferenceNo").alias("requestReferenceNo"),cb.concat(piRoot.get("titleDesc"), cb.concat(".", piRoot.get("clientName"))).alias("customerName"),
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
					icmRoot.get("signature").alias("signature"),lbmRoot.get("branchName").alias("place"),companyName.alias("companyName"),imageURL.alias("companylogo"),hpmRoot.get("companyId").alias("companyId"),hpmRoot.get("productId").alias("productId"),
					hpmRoot.get("debitNoteNo").alias("debitNoteNo"))
			.where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId")),cb.equal(hpmRoot.get("agencyCode").as(String.class), luiRoot.get("agencyCode")),cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")),
					cb.equal(hpmRoot.get("loginId"), lbmRoot.get("loginId")),cb.equal(hpmRoot.get("companyId"), lbmRoot.get("companyId")),cb.equal(hpmRoot.get("branchCode"), lbmRoot.get("branchCode")),cb.equal(lbmRoot.get("status"), "Y"),
					cb.equal(icmRoot.get("status"), "Y"),cb.between(cb.literal(new Date()), icmRoot.get("effectiveDateStart"), icmRoot.get("effectiveDateEnd")),cb.equal(icmRoot.get("amendId"), icmAmd),cb.equal(hpmRoot.get("quoteNo"), QuoteNo));
			List<Tuple> list = em.createQuery(cq).getResultList();
			if(!CollectionUtils.isEmpty(list)) {
				Tuple map = list.get(0);
				List<PolicyCoverData> coverData = coverDataRepository.findByQuoteNo(map.get("quoteNo")==null?"":map.get("quoteNo").toString());
				
				CriteriaQuery<Tuple> cq1 = cb.createQuery(Tuple.class);
				Root<PolicyCoverData> pcdRoot = cq1.from(PolicyCoverData.class);
				Root<SectionDataDetails> sddRoot = cq1.from(SectionDataDetails.class);
				//List<EserviceCommonDetails> eserviceCommonList = eserviceCommonDetRepo.findByQuoteNo(map.get("quoteNo").toString());
				
				List<Predicate> predicate = new ArrayList<Predicate>();
				predicate.add(cb.equal(pcdRoot.get("quoteNo"),map.get("quoteNo")));
				predicate.add(cb.equal(pcdRoot.get("quoteNo"),sddRoot.get("quoteNo")));
				predicate.add(cb.equal(pcdRoot.get("sectionId").as(String.class), sddRoot.get("sectionId")));
				predicate.add(cb.equal(pcdRoot.get("taxId"),"0"));
				predicate.add(cb.equal(pcdRoot.get("discLoadId"), "0"));
				predicate.add(cb.equal(pcdRoot.get("subCoverId"), "0"));
				predicate.add(cb.equal(pcdRoot.get("locationId"), sddRoot.get("locationId")));
				/*if(!eserviceCommonList.isEmpty()) {
					Root<EserviceCommonDetails> ecdRoot = cq1.from(EserviceCommonDetails.class);
					eserviceQuote = ecdRoot.get("occupationDesc").alias("occupationDesc");
					predicate.add(cb.equal(pcdRoot.get("quoteNo"), ecdRoot.get("quoteNo")));
					predicate.add(cb.equal(pcdRoot.get("sectionId"), ecdRoot.get("sectionId")));
					predicate.add(cb.equal(pcdRoot.get("vehicleId"), ecdRoot.get("riskId")));
					predicate.add(cb.equal(pcdRoot.get("productId"), ecdRoot.get("productId")));
					predicate.add(cb.equal(pcdRoot.get("companyId"), ecdRoot.get("companyId")));
				}*/
				Predicate [] predicateArray = new Predicate[predicate.size()];
				predicate.toArray(predicateArray);
				
				Subquery<String> occDesc = cq1.subquery(String.class);
				Root<EserviceCommonDetails> ecdRoot = occDesc.from(EserviceCommonDetails.class);
				occDesc.select(ecdRoot.get("occupationDesc")).where(cb.equal(pcdRoot.get("quoteNo"), ecdRoot.get("quoteNo")),cb.equal(pcdRoot.get("sectionId").as(String.class), ecdRoot.get("sectionId")),
						cb.equal(pcdRoot.get("vehicleId"), ecdRoot.get("riskId")),cb.equal(pcdRoot.get("productId").as(String.class), ecdRoot.get("productId")),
						cb.equal(pcdRoot.get("companyId"), ecdRoot.get("companyId")),cb.equal(pcdRoot.get("locationId"), ecdRoot.get("locationId")));
				
				cq1.multiselect(sddRoot.get("sectionId").alias("sectionId"),sddRoot.get("sectionDesc").alias("sectionDesc"),pcdRoot.get("coverDesc").alias("coverDesc"),
						pcdRoot.get("coverId").alias("coverId"),pcdRoot.get("coverageType").alias("coverageType"),sddRoot.get("coverNoteReferenceNo").alias("coverNoteReferenceNo"),
						 pcdRoot.get("sumInsured").alias("sumInsured"),pcdRoot.get("rate").alias("rate"),pcdRoot.get("premiumIncludedTaxLc").alias("premiumIncludedTaxLc"),
						 pcdRoot.get("premiumIncludedTaxFc").alias("premiumIncludedTaxFc"),occDesc.alias("occupationDesc"),
						 pcdRoot.get("premiumExcludedTaxLc").alias("premiumExcludedTaxLc"),pcdRoot.get("premiumExcludedTaxFc").alias("premiumExcludedTaxFc"),
						 sddRoot.get("locationId").alias("locationId"),sddRoot.get("locationName").alias("locationName"))
				.where(predicateArray).orderBy(cb.asc(sddRoot.get("sectionId")));
						
			List<Tuple> Slist = em.createQuery(cq1).getResultList();
						
			List<Map<String,Object>>sectList=new ArrayList<>();
			Double minAdjPrem=0.0,minAdjPremFc=0.0,basePremium=0.0,basePremiumFc=0.0,
					minAdjExPrem=0.0,minAdjExPremFc=0.0,baseExPremium=0.0,baseExPremiumFc=0.0;
				
			for (int i=0;i<Slist.size();i++) {
				Tuple t=Slist.get(i);
				String coverId = t.get("coverId")==null?"":t.get("coverId").toString();
				String coverageType = t.get("coverageType")==null?"":t.get("coverageType").toString();
				if("945".equals(coverId)){
					minAdjPrem=t.get("premiumIncludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxLc").toString());
					minAdjPremFc=t.get("premiumIncludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxFc").toString());
					minAdjExPrem=t.get("premiumExcludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxLc").toString());
					minAdjExPremFc=t.get("premiumExcludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxFc").toString());
				}
				if("B".equals(coverageType)) {
					basePremium=t.get("premiumIncludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxLc").toString());
					basePremiumFc=t.get("premiumIncludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxFc").toString());
					baseExPremium=t.get("premiumExcludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxLc").toString());
					baseExPremiumFc=t.get("premiumExcludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxFc").toString());
					basePremium=basePremium+minAdjPrem;
					basePremiumFc=basePremiumFc+minAdjPremFc;
					baseExPremium=baseExPremium+minAdjExPrem;
					baseExPremiumFc=baseExPremiumFc+minAdjExPremFc;
				}else {
					basePremium=t.get("premiumIncludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxLc").toString());
					basePremiumFc=t.get("premiumIncludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxFc").toString());
					baseExPremium=t.get("premiumExcludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxLc").toString());
					baseExPremiumFc=t.get("premiumExcludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxFc").toString());
				}
				if(!"945".equals(coverId)){
					Map<String,Object> Smap = new HashMap<String,Object>();
					Smap.put("sectionDesc", t.get("sectionDesc"));
					Smap.put("occupationDesc", t.get("occupationDesc"));
					Smap.put("coverDesc", t.get("coverDesc"));
					Smap.put("sumInsured", t.get("sumInsured"));
					Smap.put("rate", t.get("rate"));
					Smap.put("premiumIncludedTaxLc", basePremium);
					Smap.put("premiumIncludedTaxFc", basePremiumFc);
					Smap.put("premiumExcludedTaxLc", baseExPremium);
					Smap.put("premiumExcludedTaxFc", baseExPremiumFc);
					sectList.add(Smap);
				}
			}
			List<Map<String,Object>> sectionList = new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> coverageDetails = new ArrayList<Map<String,Object>>();
			List<TaxInvoicePremiumDetails> premiumDetailsRes = new ArrayList<>();
			Double OverAllPremium=0.0;
			String companyId = map.get("companyId")==null?"":map.get("companyId").toString();
			if("100002".equalsIgnoreCase(companyId)) {
					if(coverData!=null && !coverData.isEmpty()) {
						Double taxRate = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T"))
								.map(m -> m.getTaxRate()).map(BigDecimal::doubleValue)
								.findAny().orElse(0.0);
						
						Double taxAmount = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T") && f.getSectionId()!=99999)
								.map(i -> i.getTaxAmount()).collect(Collectors.summingDouble(BigDecimal::doubleValue));
						
						
						List<Map<String,Object>> sectionPremium = coverData.stream().filter(f ->f.getTaxId()==0 && f.getDiscLoadId()==0 && f.getSectionId()!=99999
								&& (f.getCoverageType().equals("O") && (f.getIsSelected().equalsIgnoreCase("Y")?"Y":"N").equalsIgnoreCase("Y") 
								|| !f.getCoverageType().equalsIgnoreCase("O")))
								.collect(Collectors.groupingBy(a -> a.getSectionId(),Collectors.groupingBy(b -> b.getCoverName(),Collectors.reducing(
									BigDecimal.ZERO, PolicyCoverData::getPremiumExcludedTaxLc, BigDecimal::add))))
								.entrySet().stream()
								.flatMap((Map.Entry<Integer,Map<String,BigDecimal>> s ) -> {
									Integer sectionId = s.getKey();
									return s.getValue().entrySet().stream()
											.map((Map.Entry<String,BigDecimal> g )-> {
												String coverDesc = g.getKey();
												BigDecimal totPremium = g.getValue();
												Map<String,Object> secMap = new HashMap<String,Object>();
												secMap.put("SectionId", sectionId);
												secMap.put("CoverDesc", coverDesc.toUpperCase());
												secMap.put("TotPremium", totPremium);
												return secMap;
											});
								}).sorted(Comparator.comparing(p -> (String) p.get("CoverDesc")))
								.collect(Collectors.toList());
						sectionPremium.forEach(k -> {
							TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
									.amount(new BigDecimal(Double.valueOf(k.get("TotPremium").toString())).toString())
									.narration(k.get("CoverDesc")==null?"":k.get("CoverDesc").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
								.build();
								premiumDetailsRes.add(u);
						});
					
							OverAllPremium = premiumDetailsRes.stream().map(k -> new BigDecimal(k.getAmount())).collect(Collectors.summingDouble(BigDecimal::doubleValue))+taxAmount;
							result.put("vatPercent", taxRate.toString());
							result.put("vatAmount", taxAmount.toString());
					}
				}else if("100004".equalsIgnoreCase(companyId)){
					sectList.forEach(k -> {
						Map<String,Object> Smap = new HashMap<String,Object>();
						Smap.put("sectionDesc", k.get("sectionDesc"));
						Smap.put("coverDesc", k.get("coverDesc"));
						Smap.put("sumInsured", k.get("sumInsured"));
						Smap.put("rate", k.get("rate"));
						Smap.put("premiumIncludedTaxLc", k.get("premiumIncludedTaxLc"));
						Smap.put("premiumIncludedTaxFc", k.get("premiumIncludedTaxFc"));
						Smap.put("premiumExcludedTaxLc", k.get("premiumExcludedTaxLc"));
						Smap.put("premiumExcludedTaxFc", k.get("premiumExcludedTaxFc"));
						Smap.put("vehicleId",k.get("vehicleId"));
						sectionList.add(Smap);
						result.put("occupationDesc", sectList.stream().filter(f -> f.get("occupationDesc") != null).map(m -> m.get("occupationDesc"))
								.map(Object::toString).findAny().orElse(null));
					});
			}else {
				Map<Object, List<Map<String,Object>>> sectionRes = sectList.stream().collect(Collectors.groupingBy(g -> g.get("sectionDesc"),Collectors.mapping(v ->{
					Map<String,Object> Smap = new HashMap<String,Object>();
					Smap.put("occupationDesc", v.get("occupationDesc"));
					Smap.put("coverDesc", v.get("coverDesc"));
					Smap.put("sumInsured", v.get("sumInsured"));
					Smap.put("rate", v.get("rate"));
					Smap.put("premiumIncludedTaxLc", v.get("premiumIncludedTaxLc"));
					Smap.put("premiumIncludedTaxFc", v.get("premiumIncludedTaxFc"));
					Smap.put("premiumExcludedTaxLc", v.get("premiumExcludedTaxLc"));
					Smap.put("premiumExclhitudedTaxFc", v.get("premiumExcludedTaxFc"));
					return Smap;
				}, Collectors.toList())));			
				for(Map.Entry<Object, List<Map<String,Object>>> entry :sectionRes.entrySet()) {
					Map<String, Object> sectionMap = new HashMap<String, Object>();
					sectionMap.put("sectionKey", entry.getKey());
					sectionMap.put("sectionValue", entry.getValue());
					sectionList.add(sectionMap);
				}
			}
			
			List<Object> sectionIds = Slist.stream().map(k -> k.get("sectionId")).distinct().collect(Collectors.toList());
			List<Object> locationIds = Slist.stream().map(k -> k.get("locationId")).distinct().collect(Collectors.toList());
			List<Map<String,Object>> coverageList = new ArrayList<Map<String,Object>>();
				for(int i=0;i<sectionIds.size();i++) {
					Map<String,Object> coverMap = new HashMap<String,Object>();
					List<Map<String,Object>> contentDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> employeeDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> commonDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> locationDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> bonddetils = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> excessConDetails = new ArrayList<Map<String,Object>>();
					String sectionId = sectionIds.get(i).toString();
					for(int x=0;x<locationIds.size();x++) {
						String locationId = locationIds.get(x).toString();
						String locationName = Slist.stream().filter(f -> f.get("locationId").equals(Integer.parseInt(locationId))).map(r -> r.get("locationName").toString()).findFirst().get();
						List<EserviceBuildingDetails> buildingdtl = eserviceBuildingDetailsRepo.findByRequestReferenceNoAndSectionIdAndLocationId(map.get("requestReferenceNo").toString(),sectionId,Integer.parseInt(locationId));
						List<BuildingRiskDetails> buildingRiskData = buildingRiskDetailsRepo.findByRequestReferenceNoAndSectionIdAndLocationId(map.get("requestReferenceNo").toString(),sectionId,Integer.parseInt(locationId));
						List<Map<String,Object>> bond_list = buildingRiskData.stream().map(p -> {
							LinkedHashMap<String, Object> bond_map = new LinkedHashMap<String,Object>();
							bond_map.put("locationName", p.getLocationName());
							bond_map.put("industrydesc", p.getIndustryDesc());
							bond_map.put("sectiondesc", p.getSectionDesc());
							bond_map.put("bondyear", p.getBondYear());
							bond_map.put("coveringdetails", p.getCoveringDetails());
							bond_map.put("descriptionofrisk", p.getDescriptionOfRisk());
							bond_map.put("suminsured", p.getSumInsured());
							bond_map.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
									&& f.getSectionId()==Integer.parseInt(p.getSectionId())
									&& f.getVehicleId()==p.getRiskId()).map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
							return bond_map;
						}).collect(Collectors.toList());
						
						List<Map<String,Object>> locationList = buildingdtl.stream().map(k ->{
							LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
							lmap.put("locationName", k.getLocationName());
							lmap.put("buildingAddress", k.getAddress());
							lmap.put("wallType", k.getWallTypeDesc());
							lmap.put("roofType", k.getRoofTypeDesc());
							lmap.put("firstlosspayee", k.getFirstLossPercent());
							lmap.put("buildingSumInsured", k.getBuildingSuminsured());
							lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
							lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
									&& f.getSectionId()==Integer.parseInt(k.getSectionId())
									&& f.getVehicleId()==k.getRiskId()).map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
							return lmap;
						}).collect(Collectors.toList());
						
						List<ContentAndRisk> contentInfo = conAndRiskRepo.findByRequestReferenceNoAndSectionIdAndLocationId(map.get("requestReferenceNo").toString(),sectionId,Integer.parseInt(locationId));
						List<Map<String,Object>> contentList = contentInfo.stream().collect(
								Collectors.groupingBy(k -> k.getRiskId(), Collectors.mapping(m -> {
									LinkedHashMap<String, Object> contentMap = new LinkedHashMap<String, Object>();
									contentMap.put("locationName", locationName);
									contentMap.put("itemId", m.getItemId());
									if("E".equalsIgnoreCase(m.getType()))
										contentMap.put("contentRiskDesc", m.getSerialNo()+", &nbsp;"+m.getMakeAndModel()/*":&nbsp;&nbsp;<span style=\"font-weight:bold;\">"+new DecimalFormat("##,##0.00").format(m.getSumInsured())+"</span>"*/);
									else
										contentMap.put("contentRiskDesc", m.getContentRiskDesc()+", &nbsp;"+m.getSerialNoDesc() /*":&nbsp;&nbsp;<span style=\"font-weight:bold;\">"+new DecimalFormat("##,##0.00").format(m.getSumInsured())+"</span>"*/);
									contentMap.put("sumInsured", m.getSumInsured());
									contentMap.put("Rate", coverData.stream().filter(f -> f.getCoverageType().equalsIgnoreCase("T")
											&& f.getTaxId()!=0 && f.getSectionId()==Integer.parseInt(m.getSectionId())
											&& f.getVehicleId()==m.getRiskId()).map(u -> u.getRate()).findAny().orElse(BigDecimal.ZERO));
									contentMap.put("Premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(m.getSectionId())
											&& f.getVehicleId()==m.getRiskId()).map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									return contentMap;
								}, Collectors.toList()))).entrySet()
								.stream().map(l -> {
									LinkedHashMap<String, Object> cMap = new LinkedHashMap<String, Object>();
									cMap.put("contentRiskDesc", l.getValue().stream().map(q -> String.valueOf(q.get("contentRiskDesc"))).collect(Collectors.joining("<br>")));
									cMap.put("sumInsured", l.getValue().stream().map(g -> (BigDecimal) g.get("sumInsured")).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									cMap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									cMap.put("premium", l.getValue().stream().map(g -> g.get("Premium")).findFirst().get());
									cMap.put("rate", l.getValue().stream().map(g -> g.get("Rate")).findFirst().get());
									cMap.put("locationName", l.getValue().stream().map(g -> g.get("locationName")).findFirst().get());
									cMap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return cMap;
								}).collect(Collectors.toList());
						
						List<ProductEmployeeDetails> empDetails = productEmpDetRepo.findByRequestReferenceNoAndSectionIdAndLocationId(map.get("requestReferenceNo").toString(),sectionId,Integer.parseInt(locationId));
						List<Map<String,Object>> employeeList = empDetails.stream()
								.collect(Collectors.groupingBy(k -> k.getRiskId(), Collectors.mapping(o -> {
									LinkedHashMap<String,Object> empMap = new LinkedHashMap<String,Object>();
									empMap.put("locationName", locationName);
									empMap.put("employeeId", o.getEmployeeId());
									empMap.put("employeeName", o.getEmployeeName());
									empMap.put("occupationDesc", o.getOccupationDesc()+":&nbsp;&nbsp;<span style=\"font-weight:bold;\">"+new DecimalFormat("##,##0.00").format(o.getSalary())+"</span>");
									empMap.put("Rate", coverData.stream().filter(f -> f.getCoverageType().equalsIgnoreCase("T")
											&& f.getTaxId()!=0 && f.getSectionId()==Integer.parseInt(o.getSectionId())
											&& f.getVehicleId()==o.getRiskId()).map(u -> u.getRate()).findAny().orElse(BigDecimal.ZERO));
									empMap.put("Premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(o.getSectionId())
											&& f.getVehicleId()==o.getRiskId()).map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									empMap.put("salary", o.getSalary());
									return empMap;
								}, Collectors.toList()))).entrySet()
								.stream().map(g -> {
									LinkedHashMap<String,Object> eMap = new LinkedHashMap<String,Object>();
									eMap.put("employeeName", g.getValue().stream().map(t -> t.get("employeeName")).findFirst().orElse(""));
									eMap.put("occupationDesc", g.getValue().stream().map(t -> String.valueOf(t.get("occupationDesc"))).collect(Collectors.joining("<br>")));
									eMap.put("Rate", g.getValue().stream().map(t -> t.get("Rate")).findFirst().get());
									eMap.put("premium", g.getValue().stream().map(h -> h.get("Premium")).findFirst().get());
									eMap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									eMap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									eMap.put("locationName", g.getValue().stream().map(j -> j.get("locationName")).findFirst().get());
									eMap.put("salary", g.getValue().stream().map(h -> (BigDecimal) h.get("salary")).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									return eMap;
								}).collect(Collectors.toList());
						if("35".equalsIgnoreCase(sectionId)) {
							List<CommonDataDetails> comDetails = commonDataDetailsRepo.findByRequestReferenceNoAndSectionIdAndLocationId(map.get("requestReferenceNo").toString(),sectionId,Integer.parseInt(locationId));
							List<Map<String,Object>> commonList = comDetails.stream()
									.collect(Collectors.groupingBy(k -> k.getRiskId(), Collectors.mapping(o -> {
										LinkedHashMap<String,Object> empMap = new LinkedHashMap<String,Object>();
										empMap.put("locationName", locationName);
										empMap.put("occupationDesc", o.getOccupationDesc());
										empMap.put("sumInsured", o.getSumInsured());
										empMap.put("Rate", coverData.stream().filter(f -> f.getCoverageType().equalsIgnoreCase("T")
												&& f.getTaxId()!=0 && f.getSectionId()==Integer.parseInt(o.getSectionId())
												&& f.getVehicleId()==o.getRiskId()).map(u -> u.getRate()).findAny().orElse(BigDecimal.ZERO));
										empMap.put("Premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
												&& f.getSectionId()==Integer.parseInt(o.getSectionId())
												&& f.getVehicleId()==o.getRiskId()).map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
										return empMap;
									}, Collectors.toList()))).entrySet()
									.stream().map(g -> {
										LinkedHashMap<String,Object> eMap = new LinkedHashMap<String,Object>();
										eMap.put("occupationDesc", g.getValue().stream().map(t -> String.valueOf(t.get("occupationDesc"))).collect(Collectors.joining("<br>")));
										eMap.put("Rate", g.getValue().stream().map(t -> t.get("Rate")).findFirst().get());
										eMap.put("sumInsured", g.getValue().stream().map(j -> (BigDecimal) j.get("sumInsured")).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
										eMap.put("premium", g.getValue().stream().map(h -> h.get("Premium")).findFirst().get());
										eMap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
												.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
										eMap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
										eMap.put("locationName", g.getValue().stream().map(j -> j.get("locationName")).findFirst().get());
										return eMap;
									}).collect(Collectors.toList());
							commonDetails.addAll(commonList);
						}
						contentDetails.addAll(contentList);
						employeeDetails.addAll(employeeList);
						locationDetails.addAll(locationList);
						bonddetils.addAll(bond_list);
					}
					// CONDITIONS
					List<Map<String,Object>> conditionList = getConditionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);

					// EXCLUSION
					List<Map<String,Object>> exclusionRes = getExclusionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);
					List<Map<String,Object>> exclusionList = exclusionRes.stream().map(k -> {
						Map<String,Object> eMap = new HashMap<String,Object>();
						eMap.put("conditionTerms", k.get("exclusioTerms"));
						eMap.put("SectionId", k.get("SectionId"));
						return eMap;
					}).collect(Collectors.toList());
					
					//WARRANTY
					List<Map<String,Object>> warrantyList = getWarrantyDescription(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);
					
						List<Map<String,Object>> termsAndconditions = Stream.of(conditionList,exclusionList,warrantyList).flatMap(Collection::stream).distinct()
								.map(u -> {
									return u.entrySet().stream()
											.collect(Collectors.toMap(Map.Entry::getKey, e -> capitalizeFirstLetter(e.getValue())));
								})
								.collect(Collectors.toList());
						int conditionsize = termsAndconditions.size();
						int midIndex = conditionsize / 2;

						List<Map<String, Object>> firstHalf = termsAndconditions.subList(0, midIndex);

						List<Map<String, Object>> secondHalf = termsAndconditions.subList(midIndex, conditionsize);
						
						List<PolicyCoverData> excessCon = coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0 && f.getCoverageType().equalsIgnoreCase("B") && f.getSectionId()==Integer.parseInt(sectionId)).collect(Collectors.toList());
						if(!excessCon.isEmpty()) {
							excessCon.forEach(k -> {
								Map<String,Object> excessMap = new HashMap<String,Object>();
								excessMap.put("excessPercent", k.getExcessPercent());
								excessMap.put("excessAmount", k.getExcessAmount());
								excessMap.put("excessDesc", k.getExcessDesc());
								excessConDetails.add(excessMap);
							});
						}
						
						coverMap.put("sectionDesc", Slist.stream().filter(k -> sectionId.equalsIgnoreCase(k.get("sectionId").toString())).map(e -> e.get("sectionDesc").toString()).findFirst().orElse(""));
						coverMap.put("contentList", contentDetails);
						if("1".equalsIgnoreCase(sectionId))
						coverMap.put("locationDetails", locationDetails);
						coverMap.put("employeeList", employeeDetails);
						coverMap.put("commonDtlList",commonDetails);
						coverMap.put("firstHalfconditions", firstHalf);
						coverMap.put("secondHalfconditions", secondHalf);
						coverMap.put("excessConditions", excessConDetails);
						coverMap.put("sectionId", sectionId);
						coverMap.put("bonddetils", bonddetils);
						coverageList.add(coverMap);
				}
				
			Map<Object,List<Map<String,Object>>> groupBycoverageDetails = coverageList.stream()
					.collect(Collectors.groupingBy(k -> k.get("sectionDesc"), Collectors.toList()));
			for(Map.Entry<Object, List<Map<String,Object>>> CDEntry : groupBycoverageDetails.entrySet()) {
				LinkedHashMap<String, Object> coverMap = new LinkedHashMap<String, Object>();
				coverMap.put("coverId", Slist.stream().filter(f -> f.get("sectionDesc").equals(CDEntry.getKey())).map(m -> m.get("sectionId")).findFirst().orElse(""));
				coverMap.put("coverKey", CDEntry.getValue().stream()
					    .filter(e -> Arrays.asList(108, 109, 114, 115, 33, 111).contains(Integer.parseInt(e.get("sectionId").toString())))
					    .map(e -> "BUSINESS INTERRUPTION (" + CDEntry.getKey().toString() + ")".toUpperCase()+ " " +(map.get("policyNo") == null ? "QUOTE SCHEDULE" : "POLICY SCHEDULE"))
					    .findFirst()
					    .orElse(CDEntry.getKey().toString().toUpperCase() + " " + (map.get("policyNo") == null ? "QUOTE SCHEDULE" : "POLICY SCHEDULE")));
				coverMap.put("coverValue", CDEntry.getValue());
				coverMap.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
				coverMap.put("policyNo", map.get("policyNo")==null?"":map.get("policyNo").toString());
				coverMap.put("productId", map.get("productId")==null?"":map.get("productId").toString());
				coverMap.put("companyId", map.get("companyId")==null?"":map.get("companyId").toString());
				coverMap.put("inceptionDate", map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
				coverMap.put("expiryDate", map.get("expiryDate")==null?"":map.get("expiryDate").toString());
				coverageDetails.add(coverMap);
			}
			
			List<EserviceBuildingDetails> buildingDtl = eserviceBuildingDetailsRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(QuoteNo, "Y");
			String buildingOwnerYn = buildingDtl.isEmpty()?"":buildingDtl.get(0).getBuildingOwnerYn()==null?"":buildingDtl.get(0).getBuildingOwnerYn();
			List<Map<String,Object>> domesticKeyFactor = listItemValueRepo.getDomesticKeyFactor(buildingOwnerYn.equalsIgnoreCase("Y")?"1":"2");
			if(!domesticKeyFactor.isEmpty()) {
				String attachmentloc = this.getClass().getClassLoader().getResource("").getPath().replaceAll("%20", "")+"report/attachments/";
				domesticKeyFactor.forEach(k->{
					AttachMentRes a = AttachMentRes.builder()
							.docRefNo(k.get("ITEM_CODE")==null?"":k.get("ITEM_CODE").toString())
							.docloction(k.get("ITEM_VALUE")==null?"":(attachmentloc+k.get("ITEM_VALUE").toString()))
							.build();
					attachments.add(a);
				});
			}
			
			List<Map<String,Object>> customerList = new ArrayList<Map<String,Object>>();
			List<FirstLossPayee> firstLossPayees = firstLossPayeeRepo.findByRequestReferenceNo(map.get("requestReferenceNo").toString());
			if(!firstLossPayees.isEmpty()) {
				firstLossPayees.forEach(k -> {
					Map<String,Object> custMap = new HashMap<String,Object>();
					custMap.put("customerName", k.getFirstLossPayeeDesc());
					customerList.add(custMap);
				});
				Map<String,Object> custMap = new HashMap<String,Object>();
				custMap.put("customerName", map.get("customerName")==null?"":map.get("customerName").toString());
				customerList.add(custMap);
			}else {
				Map<String,Object> custMap = new HashMap<String,Object>();
				custMap.put("customerName", map.get("customerName")==null?"":map.get("customerName").toString());
				customerList.add(custMap);
			}
			
			result.put("policyNo", map.get("policyNo")==null?"":map.get("policyNo").toString());
			result.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
			result.put("address", map.get("address")==null?"":map.get("address").toString());
			result.put("inceptionDate", map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
			result.put("expiryDate", map.get("expiryDate")==null?"":map.get("expiryDate").toString());
			result.put("branchName", map.get("branchName")==null?"":map.get("branchName").toString());
			result.put("brokerBranchName", map.get("brokerBranchName")==null?"":map.get("brokerBranchName").toString());
			result.put("productName", map.get("productName")==null?"":map.get("productName").toString().toUpperCase()+" "+(map.get("policyNo")==null?"QUOTE SCHEDULE":"POLICY SCHEDULE"));
			result.put("stateName", map.get("stateName")==null?"":map.get("stateName").toString());
			result.put("cityName", map.get("cityName")==null?"":map.get("cityName").toString());
			result.put("mobileNo", map.get("mobileNo")==null?"":map.get("mobileNo").toString());
			result.put("customerId", map.get("customerId")==null?"":map.get("customerId").toString());
			result.put("brokerName", map.get("brokerName")==null?"":map.get("brokerName").toString());
			result.put("coreAppBrokerCode", map.get("coreAppBrokerCode")==null?"":map.get("coreAppBrokerCode").toString());
			result.put("currency", map.get("currency")==null?"":map.get("currency").toString());
			result.put("debitNoteNo", map.get("debitNoteNo")==null?"":map.get("debitNoteNo").toString());
			result.put("premium", map.get("premium")==null?"":new BigDecimal(Double.parseDouble(map.get("premium").toString())).toString());
			result.put("vatPremium", map.get("vatPremium")==null?"":Double.parseDouble(map.get("vatPremium").toString()));
			result.put("totalPremium", map.get("totalPremium")==null?"":new BigDecimal(Double.parseDouble(map.get("totalPremium").toString())).toString());
			result.put("signature", map.get("signature")==null?"":map.get("signature").toString());
			result.put("place", map.get("place")==null?"":map.get("place").toString());
			result.put("companyName", map.get("companyName")==null?"":map.get("companyName").toString());
			result.put("companylogo", map.get("companylogo")==null?"":map.get("companylogo").toString());
			result.put("productId", map.get("productId")==null?"":map.get("productId").toString());
			result.put("companyId", map.get("companyId")==null?"":map.get("companyId").toString());
			result.put("taxName", map.get("companyId")==null?"":map.get("companyId").toString().equalsIgnoreCase("100004")?"Premium":"Vat");
			result.put("overAllPremium", OverAllPremium);
			result.put("premiumDetails", premiumDetailsRes);
			result.put("customerList", customerList);
			//result.put("sectionDetails", sectionList);
			//result.put("locationDetails", locationDetails);
			result.put("coverageDetails", coverageDetails);
			result.put("attachMents", attachments);
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
				CquoteIn.select(StacRoot.get("quoteNo")).where(cb.equal(StacRoot.get("quoteNo"), QuoteNo),cb.equal(StacRoot.get("id"), "6"));
				
				Root<ClausesMaster> cmRoot2 = cq2.from(ClausesMaster.class);
				if(StringUtils.isNotBlank(sectionId)) {
					predicates.add(cb.or(cb.equal(cmRoot2.get("sectionId"), sectionId), cb.equal(cmRoot2.get("sectionId"), "99999")));
				}else {
					Root<SectionDataDetails> sddRoot2 = cq2.from(SectionDataDetails.class);
					predicates.add(cb.equal(sddRoot2.get("quoteNo"), hpmRoot2.get("quoteNo")));
					predicates.add(cb.or(cb.equal(cmRoot2.get("sectionId"), sddRoot2.get("sectionId")), cb.equal(cmRoot2.get("sectionId"), "99999")));
				}
				cq2.multiselect(cmRoot2.get("clausesDescription").alias("conditionTerms"),cmRoot2.get("sectionId").alias("sectionId"));
				predicates.add(cb.equal(cmRoot2.get("companyId"), hpmRoot2.get("companyId")));
				predicates.add(cb.equal(cmRoot2.get("productId").as(String.class), hpmRoot2.get("productId").as(String.class)));
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
				cq2.multiselect(tacRoot2.get("subIdDesc").alias("conditionTerms"),tacRoot2.get("sectionId").alias("sectionId"));
				predicates.add(cb.equal(tacRoot2.get("companyId"), hpmRoot2.get("companyId")));
				predicates.add(cb.equal(tacRoot2.get("productId").as(String.class), hpmRoot2.get("productId").as(String.class)));
				predicates.add(cb.in(hpmRoot2.get("quoteNo")).value(tacRoot2.get("quoteNo")));
				predicates.add(cb.equal(tacRoot2.get("status"), "Y"));
				predicates.add(cb.or(cb.equal(tacRoot2.get("branchCode"), hpmRoot2.get("branchCode")), cb.equal(tacRoot2.get("branchCode"), "99999")));
				predicates.add(cb.equal(tacRoot2.get("id"), "6"));
			}
				Predicate [] predicatArray = new Predicate[predicates.size()];
				predicates.toArray(predicatArray);
				conditionRes.addAll(em.createQuery(cq2.where(predicatArray)).getResultList());
		}
		conditionList = conditionRes.stream().distinct().map(c ->{
			LinkedHashMap<String,Object> Cmap = new LinkedHashMap<String,Object>();
			Cmap.put("conditionTerms", c.get("conditionTerms")==null?"":c.get("conditionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
			Cmap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
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
					EquoteIn.select(SEtacRoot.get("quoteNo")).where(cb.equal(SEtacRoot.get("quoteNo"), QuoteNo),cb.equal(SEtacRoot.get("id"), "7"));
					
					Root<ExclusionMaster> emRoot3 = cq3.from(ExclusionMaster.class);
					if(StringUtils.isNotBlank(sectionId)) {
						predicates.add(cb.or(cb.equal(emRoot3.get("sectionId"), sectionId), cb.equal(emRoot3.get("sectionId"), "99999")));
					}else {
						Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot3.get("quoteNo"), hpmRoot3.get("quoteNo")));
						predicates.add(cb.or(cb.equal(emRoot3.get("sectionId"), sddRoot3.get("sectionId")), cb.equal(emRoot3.get("sectionId"), "99999")));
					}
					cq3.multiselect(emRoot3.get("exclusionDescription").alias("exclusionTerms"),emRoot3.get("sectionId").alias("sectionId"));
					predicates.add(cb.equal(emRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(emRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));
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
						predicates.add(cb.or(cb.equal(tacRoot3.get("sectionId"), sectionId), cb.equal(tacRoot3.get("sectionId"), "99999")));
					}else {
						Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot3.get("quoteNo"), hpmRoot3.get("quoteNo")));
						predicates.add(cb.equal(tacRoot3.get("sectionId"), sddRoot3.get("sectionId")));
					}
					
					cq3.multiselect(tacRoot3.get("subIdDesc").alias("exclusionTerms"),tacRoot3.get("sectionId").alias("sectionId"));
					predicates.add(cb.equal(tacRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(tacRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));
					
					predicates.add(cb.in(hpmRoot3.get("quoteNo")).value(tacRoot3.get("quoteNo")));
					predicates.add(cb.equal(tacRoot3.get("status"), "Y"));
					predicates.add(cb.or(cb.equal(tacRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(tacRoot3.get("branchCode"), "99999")));
					predicates.add(cb.equal(tacRoot3.get("id"), "7"));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				}
			}
			exclusionList = exclusionRes.stream().distinct().map(c ->{
				LinkedHashMap<String,Object> Emap = new LinkedHashMap<String,Object>();
				Emap.put("exclusioTerms", c.get("exclusionTerms")==null?"":c.get("exclusionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
				Emap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
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
					EquoteIn.select(SEtacRoot.get("quoteNo")).where(cb.equal(SEtacRoot.get("quoteNo"), QuoteNo),cb.equal(SEtacRoot.get("id"), "4"));
					Root<WarrantyMaster> wmRoot3 = cq3.from(WarrantyMaster.class);
					cq3.multiselect(wmRoot3.get("warrantyDescription").alias("warrantyTerms"),wmRoot3.get("sectionId").alias("sectionId"));
					predicates.add(cb.equal(wmRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(wmRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));
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
					cq3.multiselect(tacRoot3.get("subIdDesc").alias("warrantyTerms"),tacRoot3.get("sectionId").alias("sectionId"));
					predicates.add(cb.equal(tacRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(tacRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));
					predicates.add(cb.or(cb.equal(tacRoot3.get("sectionId"), sectionId), cb.equal(tacRoot3.get("sectionId"), "99999")));
					predicates.add(cb.in(hpmRoot3.get("quoteNo")).value(tacRoot3.get("quoteNo")));
					predicates.add(cb.equal(tacRoot3.get("status"), "Y"));
					predicates.add(cb.or(cb.equal(tacRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(tacRoot3.get("branchCode"), "99999")));
					predicates.add(cb.equal(tacRoot3.get("id"), "4"));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					warrantyRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				}
			}
			warrantyList = warrantyRes.stream().distinct().map(c ->{
				LinkedHashMap<String,Object> Emap = new LinkedHashMap<String,Object>();
				Emap.put("conditionTerms", c.get("warrantyTerms")==null?"":c.get("warrantyTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
				Emap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
				return Emap;
			}).collect(Collectors.toList());
		}catch(Exception e) {
			log.info("Error in getWarrantyDescription ==> "+e.getMessage());
			e.printStackTrace();
		}
		return warrantyList;
		
	}

	public Map<String, Object> getInalipaSchedule(String policyNo) {
		Map<String,Object> res = new HashMap<String,Object>();
		try {
			List<Map<String,Object>> list = groupMedicalDetRepo.getInalipaScheduleByPolicyNo(policyNo);
			if(!CollectionUtils.isEmpty(list)) {
				Map<String,Object> map = list.get(0);
				res.put("PolicyNo", map.get("POLICY_NO")==null?"":map.get("POLICY_NO").toString());
				res.put("InsuredName", map.get("CUSTOMER_NAME")==null?"":map.get("CUSTOMER_NAME").toString());
				res.put("MobileCode", map.get("MOBILE_CODE")==null?"":map.get("MOBILE_CODE").toString());
				res.put("MobileNo", map.get("MOBILE_NO")==null?"":map.get("MOBILE_NO").toString());
				res.put("TranscationNo", map.get("CLIENT_TRANSACTION_NO")==null?"":map.get("CLIENT_TRANSACTION_NO").toString());
				res.put("TranscationDate", map.get("ENTRY_DATE")==null?"":sdf.format(map.get("ENTRY_DATE")).toString());
				res.put("LoginId", map.get("LOGIN_ID")==null?"":map.get("LOGIN_ID").toString());
				res.put("StartDate", map.get("INCEPTION_DATE")==null?"":sdf.format(map.get("INCEPTION_DATE")).toString());
				res.put("EndDate", map.get("EXPIRY_DATE")==null?"":sdf.format(map.get("EXPIRY_DATE")).toString());
				res.put("AmountPaid", map.get("AMOUNT_PAID")==null?"":map.get("AMOUNT_PAID").toString());
				res.put("Premium", map.get("PREMIUM")==null?"":map.get("PREMIUM").toString());
				res.put("TaxPercent", map.get("TAX_PERCENTAGE")==null?"":map.get("TAX_PERCENTAGE").toString());
				res.put("TaxPremium", map.get("TAX_PREMIUM")==null?"":map.get("TAX_PREMIUM").toString());
				res.put("OverAllPremium", map.get("OVERALL_PREMIUM")==null?"":map.get("OVERALL_PREMIUM").toString());
				res.put("PlanObtained", map.get("PLAN_OBTAINED")==null?"":map.get("PLAN_OBTAINED").toString());
				res.put("Companylogo", map.get("COMPANY_LOGO")==null?"":map.get("COMPANY_LOGO").toString());
				res.put("CompanyName", map.get("COMPANY_NAME")==null?"":map.get("COMPANY_NAME").toString());
			}
		}catch(Exception e) {
			log.info("Error in jasperCustomServiceImple :: getInalipaSchedule ==> "+e.getMessage());
			e.printStackTrace();
		}
		return res;
	}

	public List<Map<String, Object>> getMadisonMotorSchedule(String policyNo) {
		log.info("Enter into getMadisonMotorSchedule. \nArguments ==> "+policyNo);
		List<Map<String,Object>> resultList = new ArrayList<>();
		List<TearmsAndCondition> tearmsAndwarrantesRes = new ArrayList<>();
		List<Map<String,Object>> conditionsRes = new ArrayList<>();
		try {
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
			
			Subquery<String> companyName = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
			companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
			companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(companyNameRoot.get("amendId"), companyNameAmd));
			
			Subquery<String> imageURL = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(imageURLRoot.get("amendId"), imageURLAmd));
			
			cq.multiselect(cpmRoot.get("companyId").alias("companyId"),cpmRoot.get("effectiveDateStart").alias("effectiveDateStart"),cpmRoot.get("effectiveDateEnd").alias("effectiveDateEnd"),
				hpmRoot.get("policyNo").alias("policyNo"),hpmRoot.get("quoteNo").alias("quoteNo"),cb.concat(piRoot.get("titleDesc"), cb.concat(".", piRoot.get("clientName"))).alias("customerName"),
				hpmRoot.get("debitNoteNo").alias("debitNoteNo"),cb.concat(piRoot.get("address1"), cb.concat(",", cb.concat(piRoot.get("cityName"),
					cb.concat(" Street", cb.concat(",", cb.concat(piRoot.get("stateName"), cb.concat(",", countryName))))))).alias("address"),
				cb.selectCase().when(cb.isNotNull(piRoot.get("pinCode")), cb.concat("P.O.BOX ", cb.concat(piRoot.get("pinCode"), cb.concat(",", cb.concat(piRoot.get("cityName"),
					cb.concat(" Street",cb.concat(",", cb.concat(piRoot.get("stateName"), cb.concat(",", countryName))))))))).otherwise("").alias("postalAddress"),hpmRoot.get("inceptionDate").alias("inceptionDate"),
				hpmRoot.get("expiryDate").alias("expiryDate"),hpmRoot.get("currency").alias("currency"),hpmRoot.get("stickerNumber").alias("stickerNumber"),hpmRoot.get("policyPeriod").alias("policyPeriod"),
				mddRoot.get("insuranceTypeDesc").alias("insuranceTypeDesc"),mddRoot.get("vehicleId").alias("vehicleId"),mddRoot.get("registrationNumber").alias("registrationNumber"),
				mddRoot.get("vehicleMake").alias("vehicleMake"),mddRoot.get("vehcileModel").alias("vehcileModel"),mddRoot.get("vehicleTypeDesc").alias("vehicleTypeDesc"),
				mddRoot.get("cubicCapacity").alias("cubicCapacity"),mddRoot.get("manufactureYear").alias("manufactureYear"),mddRoot.get("seatingCapacity").alias("seatingCapacity"),
				mddRoot.get("colorDesc").alias("colorDesc"),mddRoot.get("policyTypeDesc").alias("policyTypeDesc"),mddRoot.get("sumInsured").alias("sumInsured"),
				mddRoot.get("engineNumber").alias("engineNumber"),mddRoot.get("chassisNumber").alias("chassisNumber"),
				cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")), mddRoot.get("overallPremiumLc"))
				.otherwise(hpmRoot.get("overallPremiumFc")).alias("totalPremium"),hpmRoot.get("branchName").alias("branchName"),hpmRoot.get("approvedBy").alias("approvedBy"),
				cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")), hpmRoot.get("customerName"))
				.otherwise(luiRoot.get("userName")).alias("userName"),MotorCount.alias("noOfVehicle"),companyName.alias("companyName"),imageURL.alias("companylogo"),hpmRoot.get("coverNoteReferenceNo").alias("coverNoteReferenceNo"))
			.where(StringUtils.isBlank(policyNo)?cb.equal(mddRoot.get("quoteNo"), hpmRoot.get("quoteNo")):cb.equal(mddRoot.get("policyNo"), hpmRoot.get("policyNo")),
					cb.equal(piRoot.get("customerId"), hpmRoot.get("customerId")),cb.equal(hpmRoot.get("loginId"), luiRoot.get("loginId")),
					cb.equal(cpmRoot.get("companyId"), hpmRoot.get("companyId")),cb.equal(cpmRoot.get("status"), "Y"),cb.equal(hpmRoot.get("productId"), cpmRoot.get("productId")),
					cb.between(cb.literal(new Date()), cpmRoot.get("effectiveDateStart"), cpmRoot.get("effectiveDateEnd")),
					cb.equal(hpmRoot.get("policyNo"), policyNo)).distinct(true).orderBy(cb.asc(mddRoot.get("vehicleId")));
			List<Tuple> list = em.createQuery(cq).getResultList();
			if(!list.isEmpty()) {
				Tuple m = list.get(0);
				CriteriaQuery<Tuple> cq1 = cb.createQuery(Tuple.class);
				Root<PolicyCoverData> pcdRoot = cq1.from(PolicyCoverData.class);
				Root<SectionDataDetails> sddRoot = cq1.from(SectionDataDetails.class);
				
				List<Predicate> predicate = new ArrayList<Predicate>();
				predicate.add(cb.equal(pcdRoot.get("quoteNo"),m.get("quoteNo")));
				predicate.add(cb.equal(pcdRoot.get("quoteNo"),sddRoot.get("quoteNo")));
				predicate.add(cb.equal(pcdRoot.get("sectionId").as(String.class), sddRoot.get("sectionId")));
				predicate.add(cb.equal(pcdRoot.get("taxId"),"0"));
				predicate.add(cb.equal(pcdRoot.get("discLoadId"), "0"));
				predicate.add(cb.equal(pcdRoot.get("subCoverId"), "0"));
				Predicate [] predicateArray = new Predicate[predicate.size()];
				predicate.toArray(predicateArray);
				
				Subquery<String> occDesc = cq1.subquery(String.class);
				Root<EserviceCommonDetails> ecdRoot = occDesc.from(EserviceCommonDetails.class);
				occDesc.select(ecdRoot.get("occupationDesc")).where(cb.equal(pcdRoot.get("quoteNo"), ecdRoot.get("quoteNo")),cb.equal(pcdRoot.get("sectionId").as(String.class), ecdRoot.get("sectionId")),
						cb.equal(pcdRoot.get("vehicleId"), ecdRoot.get("riskId")),cb.equal(pcdRoot.get("productId").as(String.class), ecdRoot.get("productId")),
						cb.equal(pcdRoot.get("companyId"), ecdRoot.get("companyId")));
				
				cq1.multiselect(sddRoot.get("sectionId").alias("sectionId"),sddRoot.get("sectionDesc").alias("sectionDesc"),pcdRoot.get("coverDesc").alias("coverDesc"),
						pcdRoot.get("coverId").alias("coverId"),pcdRoot.get("coverageType").alias("coverageType"),
						 pcdRoot.get("sumInsured").alias("sumInsured"),pcdRoot.get("rate").alias("rate"),pcdRoot.get("premiumIncludedTaxLc").alias("premiumIncludedTaxLc"),
						 pcdRoot.get("premiumIncludedTaxFc").alias("premiumIncludedTaxFc"),occDesc.alias("occupationDesc"),
						 pcdRoot.get("premiumExcludedTaxLc").alias("premiumExcludedTaxLc"),pcdRoot.get("premiumExcludedTaxFc").alias("premiumExcludedTaxFc"))
				.where(predicateArray).orderBy(cb.asc(sddRoot.get("sectionId")));
						
			List<Tuple> Slist = em.createQuery(cq1).getResultList();
			
			List<Object> sectionIds = Slist.stream().map(k -> k.get("sectionId")).distinct().collect(Collectors.toList());
			for(int i=0;i<sectionIds.size();i++) {
				String sectionId = sectionIds.get(i).toString();
				// CONDITIONS
				List<Map<String,Object>> conditionList = getConditionList(m.get("policyNo")==null?"":m.get("policyNo").toString(), m.get("quoteNo")==null?"":m.get("quoteNo").toString(),sectionId);
				conditionsRes.addAll(conditionList);
				
				// EXCLUSION
				List<Map<String,Object>> exclusionRes = getExclusionList(m.get("policyNo")==null?"":m.get("policyNo").toString(), m.get("quoteNo")==null?"":m.get("quoteNo").toString(),sectionId);
				List<Map<String,Object>> exclusionList = exclusionRes.stream().map(k -> {
					Map<String,Object> eMap = new HashMap<String,Object>();
					eMap.put("conditionTerms", k.get("exclusioTerms"));
					return eMap;
				}).collect(Collectors.toList());
				
				//WARRANTY
				List<Map<String,Object>> warrantyList = getWarrantyDescription(m.get("policyNo")==null?"":m.get("policyNo").toString(), m.get("quoteNo")==null?"":m.get("quoteNo").toString(),sectionId);
				
				List<Map<String,Object>> tearmsAndwarrantes = Stream.of(exclusionList,warrantyList).flatMap(Collection::stream).collect(Collectors.toList());
				tearmsAndwarrantes.stream().distinct().collect(Collectors.toList()).forEach(g ->{
					TearmsAndCondition tearms = new TearmsAndCondition();
					tearms.setAllConditions(g.get("conditionTerms")==null?"":g.get("conditionTerms").toString());
					tearmsAndwarrantesRes.add(tearms);
				});
			}
				
				list.forEach(k -> {
					Map<String,Object> map = new HashMap<>();
					map.put("companyId", k.get("companyId")==null?"":k.get("companyId").toString());
					map.put("effectiveDateStart", k.get("effectiveDateStart")==null?"":k.get("effectiveDateStart").toString());
					map.put("effectiveDateEnd", k.get("effectiveDateEnd")==null?"":k.get("effectiveDateEnd").toString());
					map.put("policyNo", k.get("policyNo")==null?"":k.get("policyNo").toString());
					map.put("quoteNo", k.get("quoteNo")==null?"":k.get("quoteNo").toString());
					map.put("customerName", k.get("customerName")==null?"":k.get("customerName").toString());
					map.put("debitNoteNo", k.get("debitNoteNo")==null?"":k.get("debitNoteNo").toString());
					map.put("address", k.get("address")==null?"":k.get("address").toString());
					map.put("postalAddress", k.get("postalAddress")==null?"":k.get("postalAddress").toString());
					map.put("inceptionDate", k.get("inceptionDate")==null?"":k.get("inceptionDate").toString());
					map.put("time", new SimpleDateFormat("HH:mm").format(k.get("inceptionDate")));
					map.put("durationOfCover", "From "+sdf.format(k.get("inceptionDate"))+"  To  "+sdf.format(k.get("expiryDate"))+" 23:59");
					map.put("Quarter", getQuarter(k.get("inceptionDate")));
					map.put("expiryDate", k.get("expiryDate")==null?"":k.get("expiryDate").toString());
					map.put("currency", k.get("currency")==null?"":k.get("currency").toString());
					map.put("stickerNumber", k.get("stickerNumber")==null?"":k.get("stickerNumber").toString());
					map.put("insuranceTypeDesc", k.get("insuranceTypeDesc")==null?"":k.get("insuranceTypeDesc").toString());
					map.put("vehicleId", k.get("vehicleId")==null?"":k.get("vehicleId").toString());
					map.put("registrationNumber", k.get("registrationNumber")==null?"":k.get("registrationNumber").toString());
					map.put("vehicleMake", k.get("vehicleMake")==null?"":k.get("vehicleMake").toString());
					map.put("vehcileModel", k.get("vehcileModel")==null?"":k.get("vehcileModel").toString());
					map.put("vehicleTypeDesc", k.get("vehicleTypeDesc")==null?"":k.get("vehicleTypeDesc").toString());
					map.put("cubicCapacity", k.get("cubicCapacity")==null?"":k.get("cubicCapacity").toString());
					map.put("manufactureYear", k.get("manufactureYear")==null?"":k.get("manufactureYear").toString());
					map.put("seatingCapacity", k.get("seatingCapacity")==null?"":k.get("seatingCapacity").toString());
					map.put("colorDesc", k.get("colorDesc")==null?"":k.get("colorDesc").toString());
					map.put("policyTypeDesc", k.get("policyTypeDesc")==null?"":k.get("policyTypeDesc").toString());
					map.put("sumInsured", k.get("sumInsured")==null?"":k.get("sumInsured").toString());
					map.put("totalPremium", k.get("totalPremium")==null?"":k.get("totalPremium").toString());
					map.put("branchName", k.get("branchName")==null?"":k.get("branchName").toString());
					map.put("approvedBy", k.get("approvedBy")==null?"":k.get("approvedBy").toString());
					map.put("noOfVehicle", k.get("noOfVehicle")==null?"":k.get("noOfVehicle").toString());
					map.put("companyName", k.get("companyName")==null?"":k.get("companyName").toString());
					map.put("companylogo", k.get("companylogo")==null?"":k.get("companylogo").toString());
					map.put("coverNoteReferenceNo", k.get("coverNoteReferenceNo")==null?"":k.get("coverNoteReferenceNo").toString());
					map.put("engineNumber", k.get("engineNumber")==null?"":k.get("engineNumber").toString());
					map.put("chassisNumber", k.get("chassisNumber")==null?"":k.get("chassisNumber").toString());
					map.put("policyPeriod", k.get("policyPeriod")==null?"":k.get("policyPeriod").toString());
					map.put("tearmsAndwarrantes", tearmsAndwarrantesRes);
					map.put("conditions", conditionsRes);
					resultList.add(map);
				});
			}
			log.info("Exit into getMadisonMotorSchedule");
		}catch(Exception e) {
			log.info("Error in getMadisonMotorSchedule ==> "+e.getMessage());
			e.printStackTrace();
		}
		return resultList;
	}

	private String getQuarter(Object object) {
		String result = "";
		try {
			String quarterStr = new SimpleDateFormat("MM").format(object);
			Integer quarter = Integer.parseInt(quarterStr);
			if(quarter <= 3) {
				result = quarter+"/1";
			}else if(quarter > 4 && quarter <=6) {
				result = quarter+"/2";
			}else if(quarter > 6 && quarter <=9) {
				result = quarter+"/3";
			}else if(quarter > 9 && quarter <=12) {
				result = quarter+"/4";
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> GetReportByRequestRefNo(String requestRefNo) {
		log.info("Enter Into GetReportByRequestRefNoImple \n Argument ==> "+requestRefNo);
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map<String,Object>> vehicleList = new ArrayList<Map<String,Object>>();
		List<TaxInvoicePremiumDetails> premiumDetailsRes = new ArrayList<>();
		Double OverAllPremium=0.0;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<EserviceCustomerDetails> ecdRoot = cq.from(EserviceCustomerDetails.class);
			Root<EserviceMotorDetails> emdRoot = cq.from(EserviceMotorDetails.class);
			Root<InsuranceCompanyMaster> icmRoot = cq.from(InsuranceCompanyMaster.class);
			Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
			
			Subquery<Integer> countryNameAmd = cq.subquery(Integer.class);
			Root<CountryMaster> SubCnAd = countryNameAmd.from(CountryMaster.class);
			countryNameAmd.select(cb.max(SubCnAd.get("amendId"))).where(cb.equal(SubCnAd.get("countryId"), ecdRoot.get("nationality")),
					cb.equal(SubCnAd.get("companyId"), emdRoot.get("companyId")),cb.equal(SubCnAd.get("status"), "Y"));
			
			Subquery<String> countryName = cq.subquery(String.class);
			Root<CountryMaster> SubCm = countryName.from(CountryMaster.class);
			countryName.select(SubCm.get("countryName")).where(cb.equal(SubCm.get("countryId"), ecdRoot.get("nationality")),
						cb.equal(SubCm.get("companyId"), emdRoot.get("companyId")),cb.equal(SubCm.get("status"), "Y"),cb.equal(SubCm.get("amendId"), countryNameAmd));
			
			Subquery<Integer> icmAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> icmAmdRoot = icmAmd.from(InsuranceCompanyMaster.class);
			icmAmd.select(cb.max(icmAmdRoot.get("amendId"))).where(cb.equal(icmAmdRoot.get("companyId"), icmRoot.get("companyId")));
			
			Subquery<String> companyName = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
			companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
			companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), emdRoot.get("companyId")),
					cb.equal(companyNameRoot.get("amendId"), companyNameAmd));
			
			Subquery<String> imageURL = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), emdRoot.get("companyId")),
					cb.equal(imageURLRoot.get("amendId"), imageURLAmd));
			
			cq.multiselect(luiRoot.get("userName").alias("userName"),emdRoot.get("requestReferenceNo").alias("requestReferenceNo"),emdRoot.get("companyId").alias("companyId"),
					emdRoot.get("currency").alias("currency"),emdRoot.get("policyStartDate").alias("inceptionDate"),emdRoot.get("branchName").alias("branchName"),
					emdRoot.get("policyEndDate").alias("expiryDate"),
					cb.concat(ecdRoot.get("titleDesc"), cb.concat(".", ecdRoot.get("clientName"))).alias("customerName"),companyName.alias("companyName"),
					cb.concat(ecdRoot.get("address1"), cb.concat(",", cb.concat(cb.coalesce(ecdRoot.get("pinCode"), ""),cb.concat(cb.selectCase().when(cb.isNull(ecdRoot.get("pinCode")), "").when(cb.equal(ecdRoot.get("pinCode"), ""), "")
					.otherwise(",").as(String.class), cb.concat(ecdRoot.get("stateName"), cb.concat(",", cb.concat(ecdRoot.get("cityName"),cb.concat(",", countryName)))))))).alias("address"),
					ecdRoot.get("vrTinNo").alias("vrTinNo"),ecdRoot.get("email1").alias("email1"),ecdRoot.get("mobileNo1").alias("mobileNo1"),imageURL.alias("companyLogo"),luiRoot.get("brokerLogo").alias("brokerLogo"))
			.where(cb.equal(emdRoot.get("customerReferenceNo"), ecdRoot.get("customerReferenceNo")),cb.equal(emdRoot.get("companyId"), icmRoot.get("companyId")),
					cb.equal(luiRoot.get("loginId"), emdRoot.get("loginId")),cb.equal(icmRoot.get("amendId"), icmAmd),cb.in(emdRoot.get("productId")).value(Arrays.asList("5","46")),cb.equal(emdRoot.get("requestReferenceNo"), requestRefNo))
			.orderBy(cb.desc(emdRoot.get("entryDate")));
			
			List<Tuple> list = em.createQuery(cq).getResultList();
			Tuple map = list.get(0);
			String sql = "SELECT MD.RISK_ID, MD.INSURANCE_TYPE_DESC, MD.POLICY_TYPE_DESC, MD.REGISTRATION_NUMBER, MD.VEHICLE_MAKE_DESC, MD.VEHCILE_MODEL_DESC, MD.CHASSIS_NUMBER, MD.VEHICLE_TYPE_DESC, ( SELECT COLOR_DESC FROM MOTOR_COLOR_MASTER WHERE COLOR_ID = MD.COLOR AND COMPANY_ID = MD.COMPANY_ID AND AMEND_ID = ( SELECT MAX(AMEND_ID) FROM MOTOR_COLOR_MASTER WHERE COLOR_ID = MD.COLOR AND COMPANY_ID = MD.COMPANY_ID ) ) AS COLOR_DESC, MD.MANUFACTURE_YEAR, MD.SUM_INSURED FROM ESERVICE_MOTOR_DETAILS MD WHERE MD.REQUEST_REFERENCE_NO = :requestRefNo";
			Query query = em.createNativeQuery(sql);
			query.setParameter("requestRefNo", requestRefNo);
			query.unwrap(NativeQueryImpl.class).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			List<Map<String,Object>> vehicleDetails = query.getResultList();
			if(vehicleDetails!=null && !vehicleDetails.isEmpty()) {
				vehicleDetails.forEach(k -> {
					Map<String,Object> m = new HashMap<String,Object>();
					m.put("vehicleId", k.get("RISK_ID")==null?"":k.get("RISK_ID").toString());
					m.put("InsuranceType", k.get("INSURANCE_TYPE_DESC")==null?"":k.get("INSURANCE_TYPE_DESC").toString());
					m.put("PolicyType", k.get("POLICY_TYPE_DESC")==null?"":k.get("POLICY_TYPE_DESC").toString());
					m.put("RegistrationNumber", k.get("REGISTRATION_NUMBER")==null?"":k.get("REGISTRATION_NUMBER").toString());
					m.put("Make", k.get("VEHICLE_MAKE_DESC")==null?"":k.get("VEHICLE_MAKE_DESC").toString());
					m.put("Model", k.get("VEHCILE_MODEL_DESC")==null?"":k.get("VEHCILE_MODEL_DESC").toString());
					m.put("ChassisNo", k.get("CHASSIS_NUMBER")==null?"":k.get("CHASSIS_NUMBER").toString());
					m.put("BodyType", k.get("VEHICLE_TYPE_DESC")==null?"":k.get("VEHICLE_TYPE_DESC").toString());
					m.put("Color", k.get("COLOR_DESC")==null?"":k.get("COLOR_DESC").toString());
					m.put("Year", k.get("MANUFACTURE_YEAR")==null?"":k.get("MANUFACTURE_YEAR").toString());
					m.put("SumInsured", k.get("SUM_INSURED")==null?null:k.get("SUM_INSURED"));
					vehicleList.add(m);
				});
			}
			

			List<FactorRateRequestDetails> coverData = factorRateRequestDetailsRepo.findByRequestReferenceNo(map.get("requestReferenceNo")==null?"":map.get("requestReferenceNo").toString());
			if(coverData!=null && !coverData.isEmpty()) {
				Double taxRate = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T"))
						.map(m -> m.getTaxRate()).map(BigDecimal::doubleValue)
						.findAny().orElse(0.0);
				
				Double taxAmount = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T") && f.getSectionId()!=99999)
						.map(i -> i.getTaxAmount()).collect(Collectors.summingDouble(BigDecimal::doubleValue));
				
				
				List<Map<String,Object>> sectionPremium = coverData.stream().filter(f ->f.getTaxId()==0 && f.getDiscLoadId()==0 && f.getSectionId()!=99999
						&& (f.getCoverageType().equals("O") && (f.getIsSelected().equalsIgnoreCase("Y")?"Y":"N").equalsIgnoreCase("Y") 
						|| !f.getCoverageType().equalsIgnoreCase("O")))
						.collect(Collectors.groupingBy(a -> a.getSectionId(),Collectors.groupingBy(b -> b.getCoverDesc(),Collectors.reducing(
							BigDecimal.ZERO, FactorRateRequestDetails::getPremiumIncludedTaxLc, BigDecimal::add))))
						.entrySet().stream()
						.flatMap((Map.Entry<Integer,Map<String,BigDecimal>> s ) -> {
							Integer sectionId = s.getKey();
							return s.getValue().entrySet().stream()
									.map((Map.Entry<String,BigDecimal> g )-> {
										String coverDesc = g.getKey();
										BigDecimal totPremium = g.getValue();
										Map<String,Object> secMap = new HashMap<String,Object>();
										secMap.put("SectionId", sectionId);
										secMap.put("CoverDesc", coverDesc);
										secMap.put("TotPremium", totPremium);
										return secMap;
									});
						}).sorted(Comparator.comparing(p -> (String) p.get("CoverDesc")))
						.collect(Collectors.toList());
				sectionPremium.forEach(k -> {
					TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
							.amount(new BigDecimal(Double.valueOf(k.get("TotPremium").toString())).toString())
							.narration(k.get("CoverDesc")==null?"":k.get("CoverDesc").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
						.build();
						premiumDetailsRes.add(u);
				});
				TaxInvoicePremiumDetails h = TaxInvoicePremiumDetails.builder()
						.amount(new BigDecimal(Double.valueOf(taxAmount.toString())).toString())
						.narration("Vat Output (Premium) - "+taxRate+"%")
					.build();
					premiumDetailsRes.add(h);
					
					OverAllPremium = premiumDetailsRes.stream().map(k -> new BigDecimal(k.getAmount())).collect(Collectors.summingDouble(BigDecimal::doubleValue));
			}
		
			
			result.put("userName", map.get("userName")==null?"":map.get("userName").toString());
			result.put("requestReferenceNo", map.get("requestReferenceNo")==null?"":map.get("requestReferenceNo").toString());
			result.put("companyId", map.get("companyId")==null?"":map.get("companyId").toString());
			result.put("companyName", map.get("companyName")==null?"":map.get("companyName").toString());
			result.put("branchName", map.get("branchName")==null?"":map.get("branchName").toString());
			result.put("inceptionDate", map.get("inceptionDate")==null?null:map.get("inceptionDate").toString());
			result.put("expiryDate", map.get("expiryDate")==null?null:map.get("expiryDate").toString());
			result.put("currency", map.get("currency")==null?"":map.get("currency").toString());
			result.put("customerName", map.get("customerName")==null?"":map.get("customerName").toString());
			result.put("address", map.get("address")==null?"":map.get("address").toString());
			result.put("vrTinNo", map.get("vrTinNo")==null?"":map.get("vrTinNo").toString());
			result.put("email1", map.get("email1")==null?"":map.get("email1").toString());
			result.put("mobileNo1", map.get("mobileNo1")==null?"":map.get("mobileNo1").toString());
			result.put("companyLogo", map.get("companyLogo")==null?"":map.get("companyLogo").toString());
			result.put("brokerLogo", map.get("brokerLogo")==null?"":map.get("brokerLogo").toString());
			result.put("vehicleDetails", vehicleList);
			result.put("PremiumDetails", premiumDetailsRes);
			result.put("PremiumTotal", OverAllPremium);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<Map<String,Object>> getEwayPremiumRegister(PremiumReportReq req){
		log.info("Enter into EwayPremiumRegister || "+req.toString());
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpm = cq.from(HomePositionMaster.class);
			Root<PersonalInfo> pif = cq.from(PersonalInfo.class);
			
			//BROKER_NAME
			Subquery<String> brokerName = cq.subquery(String.class);
			Root<LoginUserInfo> lui = brokerName.from(LoginUserInfo.class);
			brokerName.select(cb.upper(lui.get("userName"))).where(cb.equal(lui.get("loginId"), hpm.get("loginId")));
			
		/*	//PAYMENT_ID
			Subquery<Object> paymentIds = cq.subquery(Object.class);
			Root<PaymentInfo> pi = paymentIds.from(PaymentInfo.class);
			paymentIds.select(pi.get("paymentId")).where(cb.equal(pi.get("quoteNo"), hpm.get("quoteNo")),
					cb.equal(pi.get("paymentStatus"), "ACCEPTED"));
			paymentIds.setMaxResults(1); */
			
			//SECTION_NAME
			Subquery<String> sectionName = cq.subquery(String.class);
			Root<TravelPassengerDetails> tpd = sectionName.from(TravelPassengerDetails.class);
			sectionName.select(tpd.get("sectionName")).where(cb.equal(tpd.get("quoteNo"), hpm.get("quoteNo"))).distinct(true);
			
			//POLICY_TYPE_DESC
			Subquery<String> policyTypeDesc = cq.subquery(String.class);
			Root<MotorDataDetails> mdd = policyTypeDesc.from(MotorDataDetails.class);
			policyTypeDesc.select(mdd.get("policyTypeDesc")).where(cb.equal(tpd.get("quoteNo"), hpm.get("quoteNo")),
					cb.equal(mdd.get("vehicleId"), "1"));
			
			//POLICY_TYPE_NAME
			Subquery<String> policyTypeName = cq.subquery(String.class);
			Root<PolicyTypeMaster> ptm = policyTypeName.from(PolicyTypeMaster.class);
			
			Subquery<Integer> policyTypeAmd = policyTypeName.subquery(Integer.class);
			Root<PolicyTypeMaster> policyTypeAmdRoot = policyTypeAmd.from(PolicyTypeMaster.class);
			policyTypeAmd.select(cb.max(policyTypeAmdRoot.get("amendId"))).where(cb.equal(policyTypeAmdRoot.get("status"), ptm.get("status")),
					cb.equal(policyTypeAmdRoot.get("productId"), ptm.get("productId")),cb.equal(policyTypeAmdRoot.get("companyId"), ptm.get("companyId")));
			
			policyTypeName.select(ptm.get("policyTypeName")).where(cb.equal(ptm.get("status"), "Y"),
					cb.equal(ptm.get("productId"), hpm.get("productId")),cb.equal(ptm.get("companyId"), hpm.get("companyId")),
					cb.equal(ptm.get("amendId"), policyTypeAmd));
			
			//SUM_INSURED
			Subquery<BigDecimal> sumInsured = cq.subquery(BigDecimal.class);
			Root<PolicyCoverData> pcd = sumInsured.from(PolicyCoverData.class);
			sumInsured.select(cb.sum(pcd.get("sumInsured"))).where(cb.equal(pcd.get("quoteNo"), hpm.get("quoteNo")),
					cb.equal(pcd.get("taxId"), 0),cb.equal(pcd.get("discLoadId"), 0),cb.equal(pcd.get("coverageType"), "B"));
			
			//CURRENCY_ID
			Subquery<Tuple> currencyId = cq.subquery(Tuple.class);
			Root<InsuranceCompanyMaster> icm = currencyId.from(InsuranceCompanyMaster.class);
			currencyId.select(icm.get("currencyId")).where(cb.equal(hpm.get("companyId"), icm.get("companyId")));
			
			
			cq.multiselect(hpm.get("originalPolicyNo").alias("originalPolicyNo"),hpm.get("sourceType").alias("sourceType"),
					hpm.get("customerCode").alias("customerCode"),hpm.get("loginId").alias("loginId"),hpm.get("quoteNo").alias("quoteNo"),
					hpm.get("policyNo").alias("policyNo"),cb.upper(cb.concat(pif.get("titleDesc"), cb.concat(".", pif.get("clientName")))).alias("customerName"),
					hpm.get("inceptionDate").alias("startDate"),hpm.get("expiryDate").alias("endDate"),hpm.get("entryDate").alias("issueDate"),
					cb.upper(hpm.get("branchName")).alias("branchName"),cb.selectCase().when(cb.in(hpm.get("sourceType")).value(Arrays.asList("Premia Broker",
							"Premia Direct","Premia Agent")), hpm.get("customerName")).otherwise(brokerName).alias("brokerName"),
					hpm.get("userType").alias("userType"),hpm.get("subUserType").alias("subUserType"),hpm.get("currency").alias("currency"),hpm.get("paymentType").alias("paymentType"),
					hpm.get("productName").alias("productName"),cb.selectCase().when(cb.equal(hpm.get("productId"), 4), sectionName).when(cb.equal(hpm.get("productId"), 5), policyTypeDesc)
					.otherwise(policyTypeName).alias("policyTypeDesc"),hpm.get("debitNoteNo").alias("debitNoteNo"),sumInsured.alias("sumInsured"),
					cb.selectCase().when(cb.in(hpm.get("currency")).value(currencyId), cb.selectCase().when(cb.in(hpm.get("productId")).value(Arrays.asList(5,46)),
							icm)));
		log.info("Exit into EwayPremiumRegister");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Object capitalizeFirstLetter(Object obj) {
	    if (obj == null) {
	        return null;
	    }
	    String str = obj.toString();
	    if (str.isEmpty()) {
	        return str;
	    }
	    return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	
}