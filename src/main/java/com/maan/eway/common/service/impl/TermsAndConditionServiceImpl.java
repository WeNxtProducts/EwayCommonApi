package com.maan.eway.common.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.ClausesMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.ExclusionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.ProductMaster;
import com.maan.eway.bean.SectionMaster;
import com.maan.eway.bean.TermsAndCondition;
import com.maan.eway.bean.WarrantyMaster;
import com.maan.eway.common.req.SectionDataRes;
import com.maan.eway.common.req.TermsAndConditionGetBySubIdReq;
import com.maan.eway.common.req.TermsAndConditionGetReq;
import com.maan.eway.common.req.TermsAndConditionInsertReq;
import com.maan.eway.common.req.TermsAndConditionListReq;
import com.maan.eway.common.req.TermsAndConditionReq;
import com.maan.eway.common.res.ClausesRes;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.ExclusionRes;
import com.maan.eway.common.res.TermsAndConditionGetBySubIdRes;
import com.maan.eway.common.res.TermsAndConditionGetRes;
import com.maan.eway.common.res.TermsAndConditionListRes;
import com.maan.eway.common.res.TermsAndConditionRes;
import com.maan.eway.common.res.WarrantyRes;
import com.maan.eway.common.service.TermsAndConditionService;
import com.maan.eway.error.Error;
import com.maan.eway.repository.BranchMasterRepository;
import com.maan.eway.repository.ClausesMasterRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.ExclusionMasterRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.ProductMasterRepository;
import com.maan.eway.repository.SectionMasterRepository;
import com.maan.eway.repository.TermsAndConditionRepository;
import com.maan.eway.repository.WarRateMasterRepository;
import com.maan.eway.repository.WarrantyMasterRepository;
import com.maan.eway.res.SuccessRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
@Transactional
public class TermsAndConditionServiceImpl implements TermsAndConditionService {

	private Logger log = LogManager.getLogger(TermsAndConditionServiceImpl.class);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private WarrantyMasterRepository warrantyRepo;

	@Autowired
	private WarRateMasterRepository warRepo;

	@Autowired
	private ExclusionMasterRepository exclusionRepo;

	@Autowired
	private ClausesMasterRepository clausesRepo;

	@Autowired
	private InsuranceCompanyMasterRepository inuranceRepo;

	@Autowired
	private BranchMasterRepository branchRepo;

	@Autowired
	private ProductMasterRepository productRepo;

	@Autowired
	private SectionMasterRepository sectionRepo;

	@Autowired
	private TermsAndConditionRepository termsRepo;

	@Autowired
	private ListItemValueRepository listRepo;
	
	@Autowired
	private EserviceBuildingDetailsRepository buildRepo;
	
	@Autowired
	private EserviceCommonDetailsRepository commonRepo;
	
	@Autowired
	private EServiceMotorDetailsRepository EServiceMotorDetailsRepo;

	@Override
	public TermsAndConditionRes viewTermsAndCondition(TermsAndConditionReq req) {
		TermsAndConditionRes res = new TermsAndConditionRes();

		try {
			
			List<WarrantyRes> warrantyresList = new ArrayList<WarrantyRes>();
			List<ExclusionRes> exclusionresList = new ArrayList<ExclusionRes>();
			List<ClausesRes> clausesresList = new ArrayList<ClausesRes>();

			String refNO = req.getRequestReferenceNo() ;
			
			List<TermsAndCondition> datas1 = termsRepo
					.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndRequestReferenceNoOrderBySnoAsc(req.getCompanyId(),
							req.getBranchCode(), req.getProductId(), req.getSectionId(), refNO );
			
			List<TermsAndCondition> filterWarrantyList = datas1.stream().filter( o -> o.getId().equals(4) ).collect(Collectors.toList());
			List<TermsAndCondition> filterClausesList = datas1.stream().filter( o -> o.getId().equals(6) ).collect(Collectors.toList());
			List<TermsAndCondition> filterExclusionList = datas1.stream().filter( o -> o.getId().equals(7) ).collect(Collectors.toList());
			
			// Warranty 
			if (filterWarrantyList.size() > 0) {
				for (TermsAndCondition data : filterWarrantyList) {
						WarrantyRes warrantyres = new WarrantyRes();
						warrantyres.setId(data.getId().toString());
						warrantyres.setSubId(data.getSubId().toString());
						warrantyres.setSubIdDesc(data.getSubIdDesc());
						warrantyres.setDocRefNo(data.getDocRefNo());
						warrantyres.setDocumentId("16");
						warrantyres.setTypeId(data.getTypeId() );
						warrantyres.setSectionId(data.getSectionId() != null ? data.getSectionId() : ""  );
						warrantyresList.add(warrantyres);
						res.setWarrantyRes(warrantyresList);
					 
				}
				
			} else {
				List<WarrantyMaster> list3 = getWarrantiesMaster(req);
				if (list3.size() > 0  ) {
					
					for (WarrantyMaster warranties : list3) {
						WarrantyRes warrantyres = new WarrantyRes();
						warrantyres.setId("4");

						warrantyres.setSubId(warranties.getWarrantyId().toString());
						warrantyres.setSubIdDesc(warranties.getWarrantyDescription());
						warrantyres.setDocRefNo(warranties.getDocRefNo());
						warrantyres.setDocumentId("16");
						warrantyres.setTypeId(warranties.getTypeId());
						warrantyres.setSectionId(warranties.getSectionId() != null ? warranties.getSectionId() : ""  );

						warrantyresList.add(warrantyres);
						res.setWarrantyRes(warrantyresList);
					}
				}
			}
			
			// Clauses
			if (filterClausesList.size() > 0) {
				for (TermsAndCondition data : filterClausesList) {
						ClausesRes clausesres = new ClausesRes();
						clausesres.setId(data.getId().toString());
						clausesres.setSubId(data.getSubId().toString());
						clausesres.setSubIdDesc(data.getSubIdDesc());
						clausesres.setDocRefNo(data.getDocRefNo());
						clausesres.setDocumentId("18");
						clausesres.setTypeId(data.getTypeId());
						clausesres.setSectionId(data.getSectionId() != null ?  data.getSectionId() :  "" );
						clausesresList.add(clausesres);
						res.setClausesRes(clausesresList);
					 
				}
				
			} else {
				List<ClausesMaster>  list = getClausesMaster(req);
				for (ClausesMaster clauses : list) {
					ClausesRes clausesres = new ClausesRes();
					clausesres.setId("6");

					clausesres.setSubId(clauses.getClausesId().toString());
					clausesres.setSubIdDesc(clauses.getClausesDescription());
					clausesres.setDocRefNo(clauses.getDocRefNo());
					clausesres.setDocumentId("18");
					clausesres.setSectionId(clauses.getSectionId() != null ?  clauses.getSectionId() :  "" );
					clausesres.setTypeId(clauses.getTypeId());
					clausesresList.add(clausesres);
					res.setClausesRes(clausesresList);

				}
			}
			
			// Exclusion
			if (filterExclusionList.size() > 0) {
				for (TermsAndCondition data : filterExclusionList) {
						ExclusionRes exclusionres = new ExclusionRes();
						exclusionres.setId(data.getId().toString());
	
						exclusionres.setSubId(data.getSubId().toString());
						exclusionres.setSubIdDesc(data.getSubIdDesc());
						exclusionres.setDocRefNo(data.getDocRefNo());
						exclusionres.setDocumentId("19");
						exclusionres.setTypeId(data.getTypeId());
						exclusionres.setSectionId(data.getSectionId() != null ? data.getSectionId() : "" );
						exclusionresList.add(exclusionres);
						res.setExclusionRes(exclusionresList);
					 
				}
				
			} else {
				List<ExclusionMaster> list2 = getExclusionMaster(req);
				if (list2.size() > 0  ) {
					
					for (ExclusionMaster exclusions : list2) {
						ExclusionRes exclusionres = new ExclusionRes();
						exclusionres.setId("7");

						exclusionres.setSubId(exclusions.getExclusionId().toString());
						exclusionres.setSubIdDesc(exclusions.getExclusionDescription());
						exclusionres.setDocRefNo(exclusions.getDocRefNo());
						exclusionres.setDocumentId("19");
						exclusionres.setTypeId(exclusions.getTypeId());
						exclusionres.setSectionId(exclusions.getSectionId() != null ? exclusions.getSectionId() : "" );

						exclusionresList.add(exclusionres);
						res.setExclusionRes(exclusionresList);

					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	}

	
	public List<ClausesMaster> getClausesMaster(TermsAndConditionReq req) {
		List<ClausesMaster> list = new ArrayList<ClausesMaster>();

		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd   = cal.getTime();

			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ClausesMaster> query = cb.createQuery(ClausesMaster.class);

			// Find All
			Root<ClausesMaster> b = query.from(ClausesMaster.class);

			// Select
			query.select(b);

			// Effective Date Start Max Filter
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<ClausesMaster> ocpm1 = effectiveDate.from(ClausesMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(b.get("clausesId"), ocpm1.get("clausesId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a4 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a5 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a6 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));

			effectiveDate.where(a1, a2, a3, a4, a5, a6);
			// Effective Date End Max Filter
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<ClausesMaster> ocpm2 = effectiveDate2.from(ClausesMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			Predicate a7 = cb.equal(b.get("clausesId"), ocpm2.get("clausesId"));
			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a9 = cb.equal(ocpm2.get("companyId"), b.get("companyId"));
			Predicate a10 = cb.equal(ocpm2.get("branchCode"), b.get("branchCode"));
			Predicate a11 = cb.equal(ocpm2.get("productId"), b.get("productId"));
			Predicate a12 = cb.equal(ocpm2.get("sectionId"), b.get("sectionId"));

			effectiveDate2.where(a7, a8, a9, a10, a11, a12);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("sectionId")));

			// Where
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3, n4);
			Predicate n6 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n9 = cb.equal(b.get("sectionId"), req.getSectionId());
			Predicate n10 = cb.equal(b.get("sectionId"), "99999");
			Predicate n11 = cb.or(n9, n10);
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n12 = cb.equal(b.get("effectiveDateEnd"), effectiveDate2);
			Predicate n13 = cb.equal(b.get("status"), "Y");
			Predicate n14 = cb.equal(b.get("status"), "R");
			Predicate n15 = cb.or(n13, n14);
	
			query.where(n1, n12, n2, n5, n6, n11, n15).orderBy(orderList);
			// Get Result
			TypedQuery<ClausesMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getClausesId())))
					.filter(distinctByKey(o -> Arrays.asList(o.getClausesDescription())))					
					.collect(Collectors.toList());
			list.sort(Comparator.comparing(ClausesMaster::getClausesDescription));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return list;
	}
	
	public List<ExclusionMaster> getExclusionMaster(TermsAndConditionReq req) {
		List<ExclusionMaster> list2 = new ArrayList<ExclusionMaster>();


		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd   = cal.getTime();
			
			// Find Latest Record
			CriteriaBuilder cb1 = em.getCriteriaBuilder();
			CriteriaQuery<ExclusionMaster> query2 = cb1.createQuery(ExclusionMaster.class);

			// Find All
			Root<ExclusionMaster> b2 = query2.from(ExclusionMaster.class);

			// Select
			query2.select(b2);

			// Effective Date Start Max Filter
			Subquery<Timestamp> effectiveDate3 = query2.subquery(Timestamp.class);
			Root<ExclusionMaster> ocpm3 = effectiveDate3.from(ExclusionMaster.class);
			effectiveDate3.select(cb1.greatest(ocpm3.get("effectiveDateStart")));
			Predicate a21 = cb1.equal(b2.get("exclusionId"), ocpm3.get("exclusionId"));
			Predicate a22 = cb1.lessThanOrEqualTo(ocpm3.get("effectiveDateStart"), today);
			Predicate a23 = cb1.equal(ocpm3.get("companyId"), b2.get("companyId"));
			Predicate a24 = cb1.equal(ocpm3.get("branchCode"), b2.get("branchCode"));
			Predicate a25 = cb1.equal(ocpm3.get("productId"), b2.get("productId"));
			Predicate a26 = cb1.equal(ocpm3.get("sectionId"), b2.get("sectionId"));

			effectiveDate3.where(a21, a22, a23, a24, a25, a26);
			// Effective Date End Max Filter
			Subquery<Timestamp> effectiveDate4 = query2.subquery(Timestamp.class);
			Root<ExclusionMaster> ocpm4 = effectiveDate4.from(ExclusionMaster.class);
			effectiveDate4.select(cb1.greatest(ocpm4.get("effectiveDateEnd")));
			Predicate a27 = cb1.equal(b2.get("exclusionId"), ocpm4.get("exclusionId"));
			Predicate a28 = cb1.greaterThanOrEqualTo(ocpm4.get("effectiveDateEnd"), todayEnd);
			Predicate a29 = cb1.equal(ocpm4.get("companyId"), b2.get("companyId"));
			Predicate a30 = cb1.equal(ocpm4.get("branchCode"), b2.get("branchCode"));
			Predicate a31 = cb1.equal(ocpm4.get("productId"), b2.get("productId"));
			Predicate a32 = cb1.equal(ocpm4.get("sectionId"), b2.get("sectionId"));

			effectiveDate4.where(a27, a28, a29, a30, a31, a32);
			// Order By
			List<Order> orderList2 = new ArrayList<Order>();
			orderList2.add(cb1.asc(b2.get("sectionId")));

			// Where
			Predicate n22 = cb1.equal(b2.get("companyId"), req.getCompanyId());
			Predicate n23 = cb1.equal(b2.get("branchCode"), req.getBranchCode());
			Predicate n24 = cb1.equal(b2.get("branchCode"), "99999");
			Predicate n25 = cb1.or(n23, n24);
			Predicate n26 = cb1.equal(b2.get("productId"), req.getProductId());
			Predicate n27 = cb1.equal(b2.get("sectionId"), req.getSectionId());
			Predicate n28 = cb1.equal(b2.get("sectionId"), "99999");
			Predicate n29 = cb1.or(n27, n28);
			Predicate n21 = cb1.equal(b2.get("effectiveDateStart"), effectiveDate3);
			Predicate n30 = cb1.equal(b2.get("effectiveDateEnd"), effectiveDate4);
			Predicate n31 = cb1.equal(b2.get("status"), "Y");
			Predicate n32 = cb1.equal(b2.get("status"), "R");
			Predicate n33 = cb1.or(n31, n32);
	
			query2.where(n21, n22, n25, n26, n29, n30, n33).orderBy(orderList2);

			// Get Result
			TypedQuery<ExclusionMaster> result2 = em.createQuery(query2);
			list2 = result2.getResultList();
			list2 = list2.stream().filter(distinctByKey(o -> Arrays.asList(o.getExclusionId())))
					.filter(distinctByKey(o -> Arrays.asList(o.getExclusionDescription())))					
					.collect(Collectors.toList());
			list2.sort(Comparator.comparing(ExclusionMaster::getExclusionDescription));
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return list2;
	}
	
	public List<WarrantyMaster> getWarrantiesMaster(TermsAndConditionReq req) {
		List<WarrantyMaster> list3 = new ArrayList<WarrantyMaster>();
		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd   = cal.getTime();

			// Find Latest Record
			CriteriaBuilder cb3 = em.getCriteriaBuilder();
			CriteriaQuery<WarrantyMaster> query3 = cb3.createQuery(WarrantyMaster.class);

			// Find All
			Root<WarrantyMaster> b3 = query3.from(WarrantyMaster.class);

			// Select
			query3.select(b3);

			// Effective Date Start Max Filter
			Subquery<Timestamp> effectiveDate5 = query3.subquery(Timestamp.class);
			Root<WarrantyMaster> ocpm5 = effectiveDate5.from(WarrantyMaster.class);
			effectiveDate5.select(cb3.greatest(ocpm5.get("effectiveDateStart")));
			Predicate a33 = cb3.equal(b3.get("warrantyId"), ocpm5.get("warrantyId"));
			Predicate a34 = cb3.lessThanOrEqualTo(ocpm5.get("effectiveDateStart"), today);
			Predicate a35 = cb3.equal(ocpm5.get("companyId"), b3.get("companyId"));
			Predicate a36 = cb3.equal(ocpm5.get("branchCode"), b3.get("branchCode"));
			Predicate a37 = cb3.equal(ocpm5.get("productId"), b3.get("productId"));
			Predicate a38 = cb3.equal(ocpm5.get("sectionId"), b3.get("sectionId"));

			effectiveDate5.where(a33, a34, a35, a36, a37, a38);
			// Effective Date End Max Filter
			Subquery<Timestamp> effectiveDate6 = query3.subquery(Timestamp.class);
			Root<WarrantyMaster> ocpm6 = effectiveDate6.from(WarrantyMaster.class);
			effectiveDate6.select(cb3.greatest(ocpm6.get("effectiveDateEnd")));
			Predicate a39 = cb3.equal(b3.get("warrantyId"), ocpm6.get("warrantyId"));
			Predicate a40 = cb3.greaterThanOrEqualTo(ocpm6.get("effectiveDateEnd"), todayEnd);
			Predicate a41 = cb3.equal(ocpm6.get("companyId"), b3.get("companyId"));
			Predicate a42 = cb3.equal(ocpm6.get("branchCode"), b3.get("branchCode"));
			Predicate a43 = cb3.equal(ocpm6.get("productId"), b3.get("productId"));
			Predicate a44 = cb3.equal(ocpm6.get("sectionId"), b3.get("sectionId"));
			effectiveDate6.where(a39, a40, a41, a42, a43, a44);

			// Order By
			List<Order> orderList3 = new ArrayList<Order>();
			orderList3.add(cb3.asc(b3.get("sectionId")));

			// Where
			Predicate n40 = cb3.equal(b3.get("effectiveDateStart"), effectiveDate5);
			Predicate n43 = cb3.equal(b3.get("companyId"), req.getCompanyId());
			Predicate n44 = cb3.equal(b3.get("branchCode"), req.getBranchCode());
			Predicate n34 = cb3.equal(b3.get("branchCode"), "99999");
			Predicate n35 = cb3.or(n44, n34);
			Predicate n36 = cb3.equal(b3.get("productId"), req.getProductId());
			Predicate n37 = cb3.equal(b3.get("sectionId"), req.getSectionId());
			Predicate n38 = cb3.equal(b3.get("sectionId"), "99999");
			Predicate n39 = cb3.or(n37, n38);
			Predicate n41 = cb3.equal(b3.get("effectiveDateEnd"), effectiveDate6);
			Predicate n42 = cb3.equal(b3.get("status"), "Y");
			Predicate n45 = cb3.equal(b3.get("status"), "R");
			Predicate n46 = cb3.or(n42, n45);
	
			query3.where(n43, n35, n36, n39, n40, n41, n46).orderBy(orderList3);

			// Get Result
			TypedQuery<WarrantyMaster> result3 = em.createQuery(query3);
			list3 = result3.getResultList();
			list3 = list3.stream().filter(distinctByKey(o -> Arrays.asList(o.getWarrantyId())))
					.filter(distinctByKey(o -> Arrays.asList(o.getWarrantyDescription())))					
					.collect(Collectors.toList());
			list3.sort(Comparator.comparing(WarrantyMaster::getWarrantyDescription));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return list3;
	}
	
	private static <T> java.util.function.Predicate<T> distinctByKey(
			java.util.function.Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public List<Error> validateTermsAndCondition(TermsAndConditionInsertReq req) {
		List<Error> errorList = new ArrayList<Error>();

		try {

			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
			}

			if (StringUtils.isBlank(req.getBranchCode())) {
				errorList.add(new Error("02", "BranchCode", "Please Select BranchCode"));
			}
			if (StringUtils.isBlank(req.getProductId())) {
				errorList.add(new Error("03", "ProductId", "Please Select ProductId"));
			}

			if (StringUtils.isBlank(req.getSectionId())) {
				errorList.add(new Error("04", "SectionId", "Please Select SectionId"));
			}

			if (StringUtils.isBlank(req.getRequestReferenceNo())) {
				errorList.add(new Error("05", "RequestReferenceNo", "Please Enter RequestReferenceNo"));
			}
			if (StringUtils.isBlank(req.getRiskId())) {
				errorList.add(new Error("06", "RiskId", "Please Enter RiskId"));
			}
			
			List<TermsAndConditionListReq> req1 = req.getTermsAndConditionReq();
			
			if(req1.size()>0 ) {
				for (TermsAndConditionListReq re : req1) {
					
					if(re.getId().equalsIgnoreCase("6") && StringUtils.isBlank(re.getSubIdDesc()))
						errorList.add(new Error("06", "Description", "Please Enter Clauses Description"));
					
					if(re.getId().equalsIgnoreCase("7") && StringUtils.isBlank(re.getSubIdDesc()))
						errorList.add(new Error("06", "Description", "Please Enter Exclusion Description"));
					
					if(re.getId().equalsIgnoreCase("4") && StringUtils.isBlank(re.getSubIdDesc()))
						errorList.add(new Error("06", "Description", "Please Enter Warrranty Description"));
				}
			}

		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}

	@Override
	public SuccessRes insertTermsAndCondition(TermsAndConditionInsertReq req) {
	
    	SuccessRes res = new SuccessRes();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		DozerBeanMapper dozermapper = new DozerBeanMapper();
       try {
			// delete 
            Integer idValue = Integer.valueOf(req.getTermsAndConditionReq().size() >0  ?  req.getTermsAndConditionReq().get(0).getId() : "0" );
			
			List<TermsAndCondition> data = termsRepo.findByRequestReferenceNoAndRiskIdAndProductIdAndSectionIdAndId(req.getRequestReferenceNo(),
					req.getRiskId(), req.getProductId(), req.getSectionId(),  idValue);

			if (data.size() > 0 && data != null) {
				termsRepo.deleteAll(data);
			}
			
			if( req.getTermsAndConditionReq()!=null && req.getTermsAndConditionReq().size() > 0   ) {
				Long count = termsRepo.count();
				Integer count1 = count.intValue();
				Integer a = 1000;
				TermsAndCondition saveData = new TermsAndCondition();
				List<TermsAndCondition> savelist  = new ArrayList<>();
				List<InsuranceCompanyMaster> insurance = inuranceRepo
						.findTopByCompanyIdOrderByAmendIdDesc(req.getCompanyId());
				List<BranchMaster> branch = branchRepo.findTopByCompanyIdAndBranchCodeOrderByAmendIdDesc(req.getCompanyId(),
						req.getBranchCode());
				List<ProductMaster> product = productRepo
						.findTopByProductIdOrderByAmendIdDesc(Integer.valueOf(req.getProductId()));
				List<SectionMaster> section = sectionRepo
						.findTopBySectionIdOrderByAmendIdDesc(Integer.valueOf(req.getSectionId()));

				
				saveData.setCompanyId(req.getCompanyId());
				saveData.setBranchCode(req.getBranchCode());
				saveData.setProductId(req.getProductId());
				saveData.setSectionId(req.getSectionId());
				saveData.setCompanyName(insurance.get(0).getCompanyName());
				saveData.setBranchName(branch.get(0).getBranchName());
				saveData.setProductName(product.get(0).getProductName());
				saveData.setSectionName(section.size() > 0 ? section.get(0).getSectionName() : "All") ;
				saveData.setEntryDate(new Date());
				saveData.setStatus("Y");
				saveData.setCreatedBy(req.getCreatedBy());
				saveData.setUpdatedBy(req.getCreatedBy());
				saveData.setUpdatedDate(new Date());
				saveData.setQuoteNo(req.getQuoteNo());
				saveData.setRiskId(req.getRiskId());
				saveData.setAmendId(0);
				saveData.setRequestReferenceNo(req.getRequestReferenceNo());
				
				// Remove D type 
			    // Insert O TYpe
				
			for(TermsAndConditionListReq req1: req.getTermsAndConditionReq())
			{
				//if(req1.getTypeId().equalsIgnoreCase("O"))
				{
                    ListItemValue id = listRepo.findByItemTypeAndItemCode("TERMS_AND_CONDITION", req1.getId());
    				TermsAndCondition saveDatas = new TermsAndCondition();
    				saveDatas = dozermapper.map(saveData,TermsAndCondition.class );
    				saveDatas.setSno(count1 + 1);
    				saveDatas.setId(Integer.valueOf(req1.getId()));
    				saveDatas.setIdDesc(id.getItemValue());
    				saveDatas.setDocRefNo(req1.getDocRefNo());
					
    				saveDatas.setTypeId(req1.getTypeId());

					if (StringUtils.isNotBlank(req1.getSubId())) {
						saveDatas.setSubId(Integer.valueOf(req1.getSubId()));
						saveDatas.setSubIdDesc(req1.getSubIdDesc());
					}

					else {
						saveDatas.setSubId(a++);
						saveDatas.setSubIdDesc(req1.getSubIdDesc());
					}
					
					count1++;
					savelist.add(saveDatas);
				}
			}
			if(savelist!=null &&!savelist.isEmpty())
			{
			termsRepo.saveAllAndFlush(savelist);
			
			
			}
			res.setResponse("Saved Successful"); res.setSuccessId(req.getQuoteNo()); 
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
			
			}
    	
    	return res;
    	
    
	}

	@Override
	public TermsAndConditionGetRes getTermsAndCondition(TermsAndConditionGetReq req) {
		TermsAndConditionGetRes res = new TermsAndConditionGetRes();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {
			TermsAndCondition savedata = new TermsAndCondition();
			List<TermsAndCondition> datas = new ArrayList<TermsAndCondition>();
			if (req.getQuoteNo() == null && StringUtils.isNotBlank(req.getQuoteNo())) {
				datas = termsRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndRiskIdAndQuoteNoAndId(
						req.getCompanyId(), req.getBranchCode(), req.getProductId(), req.getSectionId(),
						req.getRiskId(), req.getQuoteNo(), Integer.valueOf(req.getId()));
			}

			else {

				datas = termsRepo
						.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndRiskIdAndRequestReferenceNoAndId(
								req.getCompanyId(), req.getBranchCode(), req.getProductId(), req.getSectionId(),
								req.getRiskId(), req.getRequestReferenceNo(), Integer.valueOf(req.getId()));

			}

			if (datas.size() > 0 && datas != null) {
				res = dozermapper.map(datas.get(0), TermsAndConditionGetRes.class);
				List<TermsAndConditionListRes> resList = new ArrayList<TermsAndConditionListRes>();
				for (TermsAndCondition data : datas) {
					TermsAndConditionListRes res1 = new TermsAndConditionListRes();
					res1.setSubId(data.getSubId().toString());
					res1.setSubIdDesc(data.getSubIdDesc().toString());
					resList.add(res1);
				}
				res.setTermsAndConditionlistRes(resList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public TermsAndConditionGetBySubIdRes getTermsAndConditionSubId(TermsAndConditionGetBySubIdReq req) {
		TermsAndConditionGetBySubIdRes res = new TermsAndConditionGetBySubIdRes();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {
			TermsAndCondition savedata = new TermsAndCondition();
			TermsAndCondition data = new TermsAndCondition();
			if (req.getQuoteNo() == null && StringUtils.isNotBlank(req.getQuoteNo())) {
				data = termsRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndRiskIdAndQuoteNoAndIdAndSubId(
						req.getCompanyId(), req.getBranchCode(), req.getProductId(), req.getSectionId(),
						req.getRiskId(), req.getQuoteNo(), Integer.valueOf(req.getId()),
						Integer.valueOf(req.getSubId()));
			} else {
				data = termsRepo
						.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndRiskIdAndRequestReferenceNoAndIdAndSubId(
								req.getCompanyId(), req.getBranchCode(), req.getProductId(), req.getSectionId(),
								req.getRiskId(), req.getRequestReferenceNo(), Integer.valueOf(req.getId()),
								Integer.valueOf(req.getSubId()));

			}
			res = dozermapper.map(data, TermsAndConditionGetBySubIdRes.class);
			res.setId(data.getId().toString());
			res.setSubId(data.getSubId().toString());
			res.setEntryDate(data.getEntryDate());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	}
	
	
	@Override
	public ResponseEntity<CommonRes> fetchTermsAndCondition(TermsAndConditionReq req){
		
		
		CommonRes data = new CommonRes();		
		TermsAndConditionRes res = new TermsAndConditionRes();

		try {
			
			if(req == null ||  StringUtils.isBlank(req.getCompanyId()) ||  StringUtils.isBlank(req.getProductId()) 
					||  StringUtils.isBlank(req.getSectionId()) ||   StringUtils.isBlank(req.getRequestReferenceNo())  ||   StringUtils.isBlank(req.getBranchCode())  ) {
				
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			
			
			List<WarrantyRes> warrantyresList = new ArrayList<WarrantyRes>();
			List<ExclusionRes> exclusionresList = new ArrayList<ExclusionRes>();
			List<ClausesRes> clausesresList = new ArrayList<ClausesRes>();
			 

			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd   = cal.getTime();

			  
			// Warranty 
						 			 
			 List<ClausesMaster>  list = null;
			 List<ExclusionMaster> list2 = null ;
			 List<WarrantyMaster> list3 = null ;
			 
				
				List<TermsAndCondition> datas1 = termsRepo
						.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndRequestReferenceNoOrderBySnoAsc(req.getCompanyId(),
								req.getBranchCode(), req.getProductId(), req.getSectionId(), req.getRequestReferenceNo() );
				
				if(null != datas1 &&  !datas1.isEmpty()) {
				
					
					List<TermsAndCondition> filterWarrantyList = datas1.stream().filter( o -> o.getId().equals(4) ).collect(Collectors.toList());
					List<TermsAndCondition> filterClausesList = datas1.stream().filter( o -> o.getId().equals(6) ).collect(Collectors.toList());
					List<TermsAndCondition> filterExclusionList = datas1.stream().filter( o -> o.getId().equals(7) ).collect(Collectors.toList());
	
								
					if (null != filterWarrantyList && !filterWarrantyList.isEmpty() ) {
						
						for (TermsAndCondition warranties : filterWarrantyList) {
							WarrantyRes warrantyres = new WarrantyRes();
							warrantyres.setId(null != warranties.getId() ? warranties.getId().toString() : "");
							warrantyres.setSubId(null != warranties.getSubId() ? warranties.getSubId().toString() : "");
							warrantyres.setSubIdDesc(warranties.getSubIdDesc());
							warrantyres.setDocRefNo(warranties.getDocRefNo());
							warrantyres.setDocumentId("16");
							warrantyres.setTypeId(warranties.getTypeId());
							warrantyres.setSectionId(warranties.getSectionId() != null ? warranties.getSectionId() : ""  );
							warrantyres.setCoverId(null);

							warrantyresList.add(warrantyres);
							
						}
						
						res.setWarrantyRes(warrantyresList);
					}
				
				
					if (null != filterClausesList && !filterClausesList.isEmpty() ) {
					
					for (TermsAndCondition clauses : filterClausesList) {
						ClausesRes clausesres = new ClausesRes();
						clausesres.setId(clauses.getId() != null ? clauses.getId().toString() : "" );

						clausesres.setSubId(null != clauses.getId() ? clauses.getId().toString() : "");
						clausesres.setSubIdDesc(clauses.getSubIdDesc());
						clausesres.setDocRefNo(clauses.getDocRefNo());
						clausesres.setDocumentId("18");
						clausesres.setSectionId(clauses.getSectionId() != null ?  clauses.getSectionId() :  "" );
						clausesres.setTypeId(clauses.getTypeId());
						clausesres.setCoverId(null );
						clausesresList.add(clausesres);
						
					
					
				}res.setClausesRes(clausesresList);
				
					}
				
					if (null != filterExclusionList && !filterExclusionList.isEmpty() ) {
						
						for (TermsAndCondition exclusions : filterExclusionList) {
							ExclusionRes exclusionres = new ExclusionRes();
							exclusionres.setId(exclusions.getId() != null ? exclusions.getId().toString() : "" );
							exclusionres.setSubId(exclusions.getSubId() != null ?  exclusions.getSubId().toString() : "");
							exclusionres.setSubIdDesc(exclusions.getSubIdDesc());
							exclusionres.setDocRefNo(exclusions.getDocRefNo());
							exclusionres.setDocumentId("19");
							exclusionres.setTypeId(exclusions.getTypeId());
							exclusionres.setSectionId(exclusions.getSectionId() != null ? exclusions.getSectionId() : "" );
							exclusionres.setCoverId(null);
							exclusionresList.add(exclusionres);
							

						
					}
						
						res.setExclusionRes(exclusionresList);
				}
		
			
				}else {
			
			   if(null != req && null != req.getCoverIds() && !req.getCoverIds().isEmpty() ) {
				   
				  // warranty and Exclusion Not based On cover Ids 
					   
				   if("99999".equals( req.getSectionId())  ) {             // Hold 				 
						
					 }
				   
				   
				   list3 = warrantyRepo
							.findAllByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
									req.getCompanyId(), "99999", req.getProductId(), req.getSectionId(),
									today, todayEnd, "Y");

					list = clausesRepo
							.findAllByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndCoverIdInAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
									req.getCompanyId(),  "99999", req.getProductId(), req.getSectionId(), req.getCoverIds() , 
									today, todayEnd, "Y");

					list2 = exclusionRepo
							.findAllByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
									req.getCompanyId(),  "99999", req.getProductId(), req.getSectionId(),
									today, todayEnd, "Y");
				
					   
				 
				   
				} else {
					
					
					
					 if("99999".equals( req.getSectionId())  ) {
						 
						 list3 = warrantyRepo
									.findAllByCompanyIdAndBranchCodeAndProductIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
											req.getCompanyId(),  "99999", req.getProductId(), 
											today, todayEnd, "Y");

							list = clausesRepo
									.findAllByCompanyIdAndBranchCodeAndProductIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
											req.getCompanyId(),  "99999", req.getProductId(),
											today, todayEnd, "Y");

							list2 = exclusionRepo
									.findAllByCompanyIdAndBranchCodeAndProductIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
											req.getCompanyId(),  "99999", req.getProductId(), 
											today, todayEnd, "Y"); 
							
					 }else {

					list3 = warrantyRepo
							.findAllByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
									req.getCompanyId(),  "99999", req.getProductId(), req.getSectionId(),
									today, todayEnd, "Y");

					list = clausesRepo
							.findAllByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
									req.getCompanyId(),  "99999", req.getProductId(), req.getSectionId(),
									today, todayEnd, "Y");

					list2 = exclusionRepo
							.findAllByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
									req.getCompanyId(),  "99999", req.getProductId(), req.getSectionId(),
									today, todayEnd, "Y");
					
					 }

				}
						
							if (null != list3 && !list3.isEmpty() ) {
								
								for (WarrantyMaster warranties : list3) {
									WarrantyRes warrantyres = new WarrantyRes();
									warrantyres.setId("4");

									warrantyres.setSubId(warranties.getWarrantyId().toString());
									warrantyres.setSubIdDesc(warranties.getWarrantyDescription());
									warrantyres.setDocRefNo(warranties.getDocRefNo());
									warrantyres.setDocumentId("16");
									warrantyres.setTypeId(warranties.getTypeId());
									warrantyres.setSectionId(warranties.getSectionId() != null ? warranties.getSectionId() : ""  );
									warrantyres.setCoverId(null);

									warrantyresList.add(warrantyres);
									
								}
								
								res.setWarrantyRes(warrantyresList);
							}
						
						
							if (null != list && !list.isEmpty() ) {
							
							for (ClausesMaster clauses : list) {
								ClausesRes clausesres = new ClausesRes();
								clausesres.setId("6");

								clausesres.setSubId(clauses.getClausesId().toString());
								clausesres.setSubIdDesc(clauses.getClausesDescription());
								clausesres.setDocRefNo(clauses.getDocRefNo());
								clausesres.setDocumentId("18");
								clausesres.setSectionId(clauses.getSectionId() != null ?  clauses.getSectionId() :  "" );
								clausesres.setTypeId(clauses.getTypeId());
								clausesres.setCoverId(clauses.getCoverId() != null ? clauses.getCoverId().toString() : "" );
								clausesresList.add(clausesres);
								
							
							
						}res.setClausesRes(clausesresList);
						
							}
						
							if (null != list2 && !list2.isEmpty() ) {
								
								for (ExclusionMaster exclusions : list2) {
									ExclusionRes exclusionres = new ExclusionRes();
									exclusionres.setId("7");

									exclusionres.setSubId(exclusions.getExclusionId().toString());
									exclusionres.setSubIdDesc(exclusions.getExclusionDescription());
									exclusionres.setDocRefNo(exclusions.getDocRefNo());
									exclusionres.setDocumentId("19");
									exclusionres.setTypeId(exclusions.getTypeId());
									exclusionres.setSectionId(exclusions.getSectionId() != null ? exclusions.getSectionId() : "" );
									exclusionres.setCoverId(null);
									exclusionresList.add(exclusionres);
									

								
							}
								
								res.setExclusionRes(exclusionresList);
						}
							
				}	
							
							data.setCommonResponse(res);
							data.setErrorMessage(Collections.emptyList());
							data.setIsError(false);
							data.setMessage("Success");
							
							return new ResponseEntity<CommonRes> (data, HttpStatus.CREATED);

					} catch (Exception e) {
			
			log.error("Exception Occurs When User Fetch The Records From Terms and Conditions Master Table **** "  + e.getMessage() );
			e.printStackTrace();
		//	thorw new 			
			return new ResponseEntity<> (null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	
	@Override
	public ResponseEntity<CommonRes> fetchSectionsBasedOnRisk(String requestReferenceNo  , Integer riskId){
		

		
		CommonRes res = new CommonRes();
		List<SectionDataRes> sectionDataList = new ArrayList<>();
		
		try {
			
			List<EserviceBuildingDetails> list = buildRepo
					.findAllByRequestReferenceNoAndRiskId(requestReferenceNo, riskId);
			

			if (riskId != null && riskId == 1) {
							
				List<EserviceCommonDetails> commonList = commonRepo.findByRequestReferenceNo(requestReferenceNo);
			
				if (null != commonList && !commonList.isEmpty()) {
					
					Set<String> sectionList = new HashSet<String>();

					DozerBeanMapper mapper = new DozerBeanMapper();					

					for (EserviceCommonDetails data : commonList) {
												
						if (StringUtils.isNotBlank(data.getSectionId())) {
							
							
							
						     if ((data.getSectionId().equals("35") && data.getSumInsured() == null)
									|| (data.getSectionId().equals("36") && data.getSumInsured() == null)) {

								continue;
							}
						
						
					if (sectionList.add(data.getSectionId())) {

						SectionDataRes secRes = new SectionDataRes();

						mapper.map(data, secRes);
						secRes.setSectionName(data.getSectionName() != null ? data.getSectionName() : "" );

						sectionDataList.add(secRes);
					}
				}
			}

		}
			}
			
			

			if (null != list && !list.isEmpty()) {

				DozerBeanMapper mapper = new DozerBeanMapper();
				
				Set<String> sectionList = new HashSet<String>();

				for (EserviceBuildingDetails data : list) {

					if (StringUtils.isNotBlank(data.getSectionId())) {
						

						if (data.getSectionId().equals("0")) {
							continue;
						} else if ((data.getSectionId().equals("1") && data.getBuildingSuminsured() == null)
								|| (data.getSectionId().equals("3") && data.getAllriskSuminsured() == null)
								|| data.getSectionId().equals("47") && data.getContentSuminsured() == null) {

							continue;

						}
					
					if (sectionList.add(data.getSectionId())) {

					SectionDataRes secRes = new SectionDataRes();

					mapper.map(data, secRes);
					secRes.setSectionName(data.getSectionDesc() != null ? data.getSectionDesc() : "" );

					sectionDataList.add(secRes);

				}
					}
			}
				
				res.setCommonResponse(sectionDataList);
				res.setErrorMessage(null);
				res.setIsError(false);
				res.setMessage("Success");
				
				return new ResponseEntity<CommonRes>(res, HttpStatus.CREATED );

			}else {
				EserviceMotorDetails data = EServiceMotorDetailsRepo.findByRequestReferenceNoAndRiskId(requestReferenceNo, riskId);
				
				if(data != null) {
					SectionDataRes secRes = new SectionDataRes();
					secRes.setCompanyId(data.getCompanyId());
					secRes.setProductId(data.getProductId());
					secRes.setRequestReferenceNo(requestReferenceNo);
					secRes.setRiskId(String.valueOf(riskId));
					secRes.setSectionId(data.getSectionId());
					secRes.setSectionName(data.getSectionName());
					
					sectionDataList.add(secRes);
					
					res.setCommonResponse(sectionDataList);
					res.setErrorMessage(null);
					res.setIsError(false);
					res.setMessage("Success");
					
					return new ResponseEntity<CommonRes>(res, HttpStatus.CREATED );
				}
				
			}
			res.setCommonResponse(sectionDataList);
			res.setErrorMessage(null);
			res.setIsError(false);
			res.setMessage("Failure-Data Not found In Table");
		
		
			return new ResponseEntity<CommonRes>(res, HttpStatus.OK );

		
		}catch(Exception e) {
			
			log.error("Exception Occurs When Fetch Section Data Based On Section Id *******" +  e.getMessage() );
			e.printStackTrace();
		//	 throw new UnexpectedException("");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
