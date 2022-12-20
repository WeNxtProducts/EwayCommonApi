package com.maan.eway.common.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.CityMaster;
import com.maan.eway.bean.CountryMaster;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.MsCustomerDetails;
import com.maan.eway.bean.OccupationMaster;
import com.maan.eway.bean.StateMaster;
import com.maan.eway.common.req.EserviceCustomerSaveReq;
import com.maan.eway.common.req.EserviceCustomerSearchVrtinReq;
import com.maan.eway.common.req.GetAllCustomerDetailsReq;
import com.maan.eway.common.req.GetCustomerDetailsReq;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.common.res.MsPersonalInfoGetRes;
import com.maan.eway.common.service.EserviceCustomerDetailsService;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.OccupationMasterGetReq;
import com.maan.eway.master.res.OccupationMasterRes;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.OccupationMasterRepository;
import com.maan.eway.res.SuccessRes;

@Service
@Transactional
public class EserviceCustomerDetailsServiceImpl implements EserviceCustomerDetailsService {

	private Logger log = LogManager.getLogger(EserviceCustomerDetailsServiceImpl.class);

	@Autowired
	private EserviceCustomerDetailsRepository repository;

	@Autowired
	private ListItemValueRepository listRepo;

	@Autowired
	private OccupationMasterRepository occupationRepo;

	@Autowired
	private LoginMasterRepository loginRepo;

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<Error> validateCustomerDetails(EserviceCustomerSaveReq req) {
		List<Error> errorList = new ArrayList<Error>();
		try {

			if (req.getSaveOrSubmit().equalsIgnoreCase("Submit")) {
				if (StringUtils.isBlank(req.getClientName())) {
					errorList.add(new Error("01", "ClientName", "Please Enter ClientName "));
				} else if (req.getClientName().length() > 100) {
					errorList.add(new Error("01", "ClientName", "Please Enter ClientName within 100 Characters"));
				}
				if (StringUtils.isBlank(req.getAddress1())) {
					errorList.add(new Error("02", "Address1", "Please Enter Address1 "));
				} else if (req.getAddress1().length() > 100) {
					errorList.add(new Error("02", "Address1", "Please Enter Address1 within 100 Characters"));
				}
				if (StringUtils.isBlank(req.getAddress2())) {
					errorList.add(new Error("02", "Address2", "Please Enter Address2 "));
				} else if (req.getAddress2().length() > 100) {
					errorList.add(new Error("03", "Address2", "Please Enter Address2 within 100 Characters"));
				}
				if (StringUtils.isBlank(req.getTitle())) {
					errorList.add(new Error("04", "Title", "Please Select Title"));
				}
				if (StringUtils.isBlank(req.getClientStatus())) {
					errorList.add(new Error("05", "Client Status", "Please Select Client Status"));
				}

				if (StringUtils.isNotBlank(req.getPolicyHolderType())) {

					if (req.getPolicyHolderType().equalsIgnoreCase("2")) {
						if (StringUtils.isBlank(req.getBusinessType())) {
							errorList.add(new Error("16", "BusinessType", "Please Select BusinessType"));
						}
					}
				}
//			if (StringUtils.isBlank(req.getIdType())) {
//				errorList.add(new Error("09", "IdType", "Please Select IdType"));
//			}

				if (StringUtils.isBlank(req.getIdNumber())) {
					errorList.add(new Error("11", "IdNumber", "Please Enter IdNumber"));
				} else if (req.getIdNumber().length() > 100) {
					errorList.add(new Error("11", "IdNumber", "Please Enter IdNumber within 100 Characters"));
				}
				if (StringUtils.isBlank(req.getNationality())) {
					errorList.add(new Error("12", "Nationality", "Please select Natinality"));
				}
				
//				if (StringUtils.isBlank(req.getPreferredNotification())) {
//					errorList.add(new Error("12", "PreferredNotification", "Please select Preferred Notification"));
//				}
				
				Calendar cal = new GregorianCalendar();
//				Date today2 = new Date();
//				cal.setTime(today2);
//				cal.set(Calendar.HOUR_OF_DAY, 1);
//				cal.set(Calendar.MINUTE, 1);
//				today2 =  cal.getTime()	;
//				if (req.getAppointmentDate() == null ) {
//					errorList.add(new Error("12", "AppointmentDate", "Please select AppointmentDate"));
//				} else {
//					cal.setTime(req.getAppointmentDate());
//					cal.set(Calendar.HOUR_OF_DAY, 5);
//					cal.set(Calendar.MINUTE, 5);
//					Date appDate =  cal.getTime()	;
//					if (appDate.before(today2) ) {
//						errorList.add(new Error("12", "AppointmentDate", "Please Enter AppointmentDate As FuturDate"));
//					} 
//				}
				/*
				 * if (StringUtils.isBlank(req.getPlaceOfBirth())) { errorList.add(new
				 * Error("13", "PlaceOfBirth", "Please Enter PlaceOfBirth ")); } else if
				 * (req.getPlaceOfBirth().length() > 100) { errorList.add(new Error("13",
				 * "PlaceOfBirth", "Please Enter PlaceOfBirth within 100 Characters")); } if
				 * (StringUtils.isBlank(req.getGender())) { errorList.add(new Error("14",
				 * "Gender", "Please Select Gender")); }
				 * 
				 * if (StringUtils.isBlank(req.getOccupation())) { errorList.add(new Error("15",
				 * "Occupation", "Please Select Occupation")); }
				 */

//
//			if (req.getVrnGst().length() > 20) {
//				errorList.add(new Error("17", "VrnGst", "Please Enter VrnGst within 20 Characters"));
//			}
				if (StringUtils.isBlank(req.getRegionCode())) {
					errorList.add(new Error("18", "RegionCode", "Please Enter RegionCode"));
				} else if (req.getRegionCode().length() > 20) {
					errorList.add(new Error("18", "RegionCode", "Please Enter RegionCode within 20 Characters"));
				}
				if (StringUtils.isNotBlank(req.getStreet()) && req.getStreet().length() > 100) {
					errorList.add(new Error("19", "Street", "Please Enter Street within 100 Characters"));
				}
				if (StringUtils.isNotBlank(req.getFax()) && req.getFax().length() > 20) {
					errorList.add(new Error("20", "Fax", "Please Enter Fax within 20 Characters"));
				}
				if (StringUtils.isNotBlank(req.getTelephoneNo1()) && req.getTelephoneNo1().length() > 20) {
					errorList.add(new Error("21", "TelephoneNo1", "Please Enter TelephoneNo1 within 20 Characters"));
				} else if (!req.getTelephoneNo1().matches("\\d+")) {
					errorList.add(new Error("21", "TelephoneNo1", "Please Enter TelephoneNo1 only in numbers"));
				}
				if (StringUtils.isNotBlank(req.getTelephoneNo2()) && req.getTelephoneNo2().length() > 20) {

					errorList.add(new Error("22", "TelephoneNo2", "Please Enter TelephoneNo2 within 20 Characters"));
				} else if (StringUtils.isNotBlank(req.getTelephoneNo2()) && !req.getTelephoneNo2().matches("\\d+")) {
					errorList.add(new Error("22", "TelephoneNo2", "Please Enter TelephoneNo2 only in numbers"));
				}

				if (StringUtils.isNotBlank(req.getTelephoneNo3()) && req.getTelephoneNo3().length() > 20) {
					errorList.add(new Error("23", "TelephoneNo3", "Please Enter TelephoneNo3 within 20 Characters"));
				} else if (StringUtils.isNotBlank(req.getTelephoneNo3()) && !req.getTelephoneNo3().matches("\\d+")) {
					errorList.add(new Error("23", "TelephoneNo3", "Please Enter TelephoneNo3 only in numbers"));
				}

				if (StringUtils.isBlank(req.getMobileNo1())) {
					errorList.add(new Error("24", "MobileNo1", "Please Enter MobileNo1"));
				} else if (req.getMobileNo1().length() > 20) {
					errorList.add(new Error("24", "MobileNo1", "Please Enter MobileNo1 within 20 Characters"));
				} else if (!req.getMobileNo1().matches("\\d+")) {
					errorList.add(new Error("24", "MobileNo1", "Please Enter MobileNo1 only in numbers"));
				}

				if (StringUtils.isNotBlank(req.getMobileNo2()) && req.getMobileNo2().length() > 20) {
					errorList.add(new Error("25", "MobileNo2", "Please Enter MobileNo2 within 20 Characters"));
				} else if (StringUtils.isNotBlank(req.getMobileNo2()) && !req.getMobileNo2().matches("\\d+")) {
					errorList.add(new Error("25", "MobileNo2", "Please Enter MobileNo2 only in numbers"));
				}
				if (StringUtils.isNotBlank(req.getMobileNo3()) && req.getMobileNo3().length() > 20) {
					errorList.add(new Error("26", "MobileNo3", "Please Enter MobileNo3 within 20 Characters"));
				} else if (StringUtils.isNotBlank(req.getMobileNo2()) && !req.getMobileNo3().matches("\\d+")) {
					errorList.add(new Error("26", "MobileNo3", "Please Enter MobileNo3 only in numbers"));
				}
				if (StringUtils.isBlank(req.getEmail1())) {
					errorList.add(new Error("27", "Email1", "Please Enter Email1"));
				} else if (req.getEmail1().length() > 20) {
					errorList.add(new Error("27", "Email1", "Please Enter Email1 within 20 Characters"));
				} else {
					boolean b = isValidMail(req.getEmail1());
					if (b == false) {
						errorList.add(new Error("37", "Email", "Please Enter Email in correct format"));
					}
				}

				if (StringUtils.isNotBlank(req.getEmail2()) && req.getEmail2().length() > 20) {
					errorList.add(new Error("28", "Email2", "Please Enter Email2 within 20 Characters"));
				} else if (StringUtils.isNotBlank(req.getEmail2())) {
					boolean b = isValidMail(req.getEmail2());

					if (b == false) {
						errorList.add(new Error("28", "Email2", "Please Enter Email2 in correct format"));
					}
				}
				if (StringUtils.isNotBlank(req.getEmail3()) && req.getEmail3().length() > 20) {
					errorList.add(new Error("29", "Email3", "Please Enter Email3 within 20 Characters"));
				} else if (StringUtils.isNotBlank(req.getEmail3())) {
					boolean b = isValidMail(req.getEmail3());
					if (b == false) {
						errorList.add(new Error("29", "Email3", "Please Enter Email3 in correct format"));
					}
				}
				if (StringUtils.isBlank(req.getLanguage())) {
					errorList.add(new Error("30", "Language", "Please Select Language"));
				}

				if (StringUtils.isNotBlank(req.getEmail1()) && StringUtils.isNotBlank(req.getEmail2())
						&& req.getEmail1().equalsIgnoreCase(req.getEmail2())) {
					errorList.add(new Error("28", "Email2", "Email2 Is Already Available In Email1"));
				}
				if (StringUtils.isNotBlank(req.getEmail1()) && StringUtils.isNotBlank(req.getEmail3())
						&& req.getEmail1().equalsIgnoreCase(req.getEmail3())) {
					errorList.add(new Error("28", "Email2", "Email3 Is Already Available In Email1"));
				}
				if (StringUtils.isNotBlank(req.getEmail2()) && StringUtils.isNotBlank(req.getEmail3())
						&& req.getEmail2().equalsIgnoreCase(req.getEmail3())) {
					errorList.add(new Error("28", "Email3", "Email3 Is Already Available In Email2"));
				}

				if (StringUtils.isNotBlank(req.getTelephoneNo1()) && StringUtils.isNotBlank(req.getTelephoneNo2())
						&& req.getTelephoneNo1().equalsIgnoreCase(req.getTelephoneNo2())) {
					errorList.add(new Error("28", "TelephoneNo2", "TelephoneNo2 Is Already Available In TelephoneNo1"));
				}
				if (StringUtils.isNotBlank(req.getTelephoneNo1()) && StringUtils.isNotBlank(req.getTelephoneNo3())
						&& req.getTelephoneNo1().equalsIgnoreCase(req.getTelephoneNo3())) {
					errorList.add(new Error("28", "TelephoneNo2", "TelephoneNo2 Is Already Available In TelephoneNo1"));
				}
				if (StringUtils.isNotBlank(req.getTelephoneNo2()) && StringUtils.isNotBlank(req.getTelephoneNo3())
						&& req.getTelephoneNo2().equalsIgnoreCase(req.getTelephoneNo3())) {
					errorList.add(new Error("28", "TelephoneNo3", "TelephoneNo3 Is Already Available In TelephoneNo2"));
				}

				if (StringUtils.isNotBlank(req.getMobileNo1()) && StringUtils.isNotBlank(req.getMobileNo2())
						&& req.getMobileNo1().equalsIgnoreCase(req.getMobileNo2())) {
					errorList.add(new Error("28", "MobileNo2", "MobileNo2 Is Already Available In MobileNo1"));
				}
				if (StringUtils.isNotBlank(req.getMobileNo1()) && StringUtils.isNotBlank(req.getMobileNo3())
						&& req.getMobileNo1().equalsIgnoreCase(req.getMobileNo3())) {
					errorList.add(new Error("28", "MobileNo2", "MobileNo3 Is Already Available In MobileNo1"));
				}
				if (StringUtils.isNotBlank(req.getMobileNo2()) && StringUtils.isNotBlank(req.getMobileNo3())
						&& req.getMobileNo2().equalsIgnoreCase(req.getMobileNo3())) {
					errorList.add(new Error("28", "MobileNo3", "MobileNo3 Is Already Available In MobileNo2"));
				}

				if (StringUtils.isNotBlank(req.getAddress1()) && StringUtils.isNotBlank(req.getAddress2())
						&& req.getAddress1().equalsIgnoreCase(req.getAddress2())) {
					errorList.add(new Error("28", "Address2", "Address2 Is Already Available In Address1"));
				}

				if (StringUtils.isBlank(req.getIsTaxExempted())) {
					errorList.add(new Error("31", "IsTaxExempted", "Please Select IsTaxExempted"));

				}

				if (req.getIsTaxExempted().equals("Y")) {
					if (StringUtils.isBlank(req.getTaxExemptedId())) {
						errorList.add(new Error("32", "TaxExemptedId", "Please Enter TaxExemptedId"));
					} else if (req.getTaxExemptedId().length() > 20) {
						errorList.add(
								new Error("33", "TaxExemptedId", "Please Enter TaxExemptedId within 20 Characters"));
					}

				}
				// Status Validation
				if (StringUtils.isBlank(req.getStatus())) {
					errorList.add(new Error("34", "Status", "Please Enter Status"));
				} else if (req.getStatus().length() > 1) {
					errorList.add(new Error("34", "Status", "Enter Status in 1 Character Only"));
				} else if (!("Y".equals(req.getStatus()) || "N".equals(req.getStatus())
						|| "P".equals(req.getStatus()))) {
					errorList.add(new Error("34", "Status", "Plese Enter Status"));
				}
				if (StringUtils.isBlank(req.getCreatedBy())) {
					errorList.add(new Error("35", "CreatedBy", "Please Enter CreatedBy "));
				} else if (req.getCreatedBy().length() > 100) {
					errorList.add(new Error("35", "CreatedBy", "Please Enter CreatedBy within 100 Characters"));
				}

				// Date Validation
				Date today = new Date();
				cal.setTime(today);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 50);
				today = cal.getTime();
				if (req.getPolicyHolderType().equalsIgnoreCase("1")) {

					if (req.getDobOrRegDate() == null) {
						errorList.add(new Error("38", "DobOrRegDate", "Please Enter DobOrRegDate "));

					} else if (req.getDobOrRegDate().after(today)) {
						errorList.add(new Error("38", "DobOrRegDate", "Please Enter DobOrRegDate as Past Date"));

					}

					LocalDate localDate1 = req.getDobOrRegDate().toInstant().atZone(ZoneId.systemDefault())
							.toLocalDate();
					LocalDate localDate2 = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

					Integer years = Period.between(localDate1, localDate2).getYears();
					if (years > 100) {
						errorList.add(new Error("38", "DobOrRegDate", "DobOrRegDate Not Accepted More than 100 Years"));

					}
				}

				if (req.getPolicyHolderType().equalsIgnoreCase("2")) {

					if (req.getDobOrRegDate() == null) {
						errorList.add(new Error("38", "DobOrRegDate", "Please Enter DobOrRegDate "));

					} else if (req.getDobOrRegDate().after(today)) {
						errorList.add(new Error("38", "DobOrRegDate", "Please Enter DobOrRegDate as Past Date"));

					}

					LocalDate localDate1 = req.getDobOrRegDate().toInstant().atZone(ZoneId.systemDefault())
							.toLocalDate();
					LocalDate localDate2 = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

					Integer years = Period.between(localDate1, localDate2).getYears();
					if (years > 100) {
						errorList.add(new Error("38", "DobOrRegDate", "DobOrRegDate Not Accepted More than 100 Years"));

					}
				}

				if (StringUtils.isBlank(req.getBranchCode())) {
					errorList.add(new Error("39", "BranchCode", "Please Enter BranchCode "));
				} else if (req.getBranchCode().length() > 20) {
					errorList.add(new Error("39", "BranchCode", "Please Enter BranchCode within 20 Characters"));
				}
				if (StringUtils.isBlank(req.getProductId())) {
					errorList.add(new Error("40", "ProductId", "Please Enter ProductId "));
				} else if (req.getProductId().length() > 20) {
					errorList.add(new Error("40", "ProductId", "Please Enter ProductId within 20 Characters"));
				}
				if (StringUtils.isBlank(req.getCompanyId())) {
					errorList.add(new Error("41", "CompanyId", "Please Enter CompanyId "));
				} else if (req.getCompanyId().length() > 20) {
					errorList.add(new Error("41", "CompanyId", "Please Enter CompanyId within 20 Characters"));
				}
				if (StringUtils.isBlank(req.getVrTinNo())) {
					errorList.add(new Error("42", "VrTinNo", "Please Enter VrTinNo"));
				} else if (req.getVrTinNo().length() > 20) {
					errorList.add(new Error("42", "VrTinNo", "Please Enter VrTinNo within 20 Characters"));
				}

//			if (StringUtils.isBlank(req.getStateName())) {
//				errorList.add(new Error("43", "StateName", "Please Select StateName"));
//			}
				if (StringUtils.isBlank(req.getCityName())) {
					errorList.add(new Error("43", "CityName", "Please Select CityName "));
				} else if (req.getCityName().length() > 100) {
					errorList.add(new Error("43", "CityName", "Please Enter CityName within 100 Characters"));
				}

				List<EserviceCustomerDetails> list = new ArrayList<EserviceCustomerDetails>();
				if ((StringUtils.isNotBlank(req.getAddress1())) && (StringUtils.isNotBlank(req.getAddress2()))
						&& (StringUtils.isNotBlank(req.getBranchCode()))
						&& (StringUtils.isNotBlank(req.getBusinessType()))
						&& (StringUtils.isNotBlank(req.getCityCode())) && (StringUtils.isNotBlank(req.getCityName()))
						&& (StringUtils.isNotBlank(req.getClientName()))
						&& (StringUtils.isNotBlank(req.getClientStatus()))
						&& (StringUtils.isNotBlank(req.getCompanyId())) && (StringUtils.isNotBlank(req.getCreatedBy()))
						// && (StringUtils.isNotBlank(req.getCustomerReferenceNo()))
						&& (StringUtils.isNotBlank(req.getEmail1())) && (StringUtils.isNotBlank(req.getEmail2()))
						&& (StringUtils.isNotBlank(req.getEmail3())) && (StringUtils.isNotBlank(req.getFax()))
						&& (StringUtils.isNotBlank(req.getGender())) && (StringUtils.isNotBlank(req.getIdNumber()))
						&& (StringUtils.isNotBlank(req.getIsTaxExempted()))
						&& (StringUtils.isNotBlank(req.getLanguage()))
						&& (StringUtils.isNotBlank(req.getLanguageDesc()))
						&& (StringUtils.isNotBlank(req.getMobileNo1())) && (StringUtils.isNotBlank(req.getMobileNo2()))
						&& (StringUtils.isNotBlank(req.getMobileNo3()))
						&& (StringUtils.isNotBlank(req.getNationality()))
						&& (StringUtils.isNotBlank(req.getOccupation()))
						&& (StringUtils.isNotBlank(req.getPlaceOfBirth()))
						&& (StringUtils.isNotBlank(req.getPolicyHolderType()))
						&& (StringUtils.isNotBlank(req.getPolicyHolderTypeid()))
						&& (StringUtils.isNotBlank(req.getProductId())) && (StringUtils.isNotBlank(req.getRegionCode()))
						&& (StringUtils.isNotBlank(req.getStateCode())) && (StringUtils.isNotBlank(req.getStateName()))
						&& (StringUtils.isNotBlank(req.getStatus())) && (StringUtils.isNotBlank(req.getStreet()))
						&& (StringUtils.isNotBlank(req.getTaxExemptedId()))
						&& (StringUtils.isNotBlank(req.getTelephoneNo1()))
						&& (StringUtils.isNotBlank(req.getTelephoneNo2()))
						&& (StringUtils.isNotBlank(req.getTelephoneNo3())) && (StringUtils.isNotBlank(req.getTitle()))
						&& (req.getDobOrRegDate()!=null)
						&& (StringUtils.isNotBlank(req.getIsTaxExempted()))
						&& (StringUtils.isNotBlank(req.getTaxExemptedId()))
						&& (StringUtils.isNotBlank(req.getPreferredNotification()))
						&& (req.getAppointmentDate()!=null)
						
						){

					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<EserviceCustomerDetails> query = cb.createQuery(EserviceCustomerDetails.class);
					// Find all
					Root<EserviceCustomerDetails> b = query.from(EserviceCustomerDetails.class);
					// Select
					query.select(b);
					// Where

					Predicate n1 = (cb.like(cb.lower(b.get("address1")), req.getAddress1().toLowerCase()));
					Predicate n2 = (cb.like(cb.lower(b.get("address2")), req.getAddress2().toLowerCase()));
					Predicate n3 = (cb.like(cb.lower(b.get("branchCode")), req.getBranchCode().toLowerCase()));
					Predicate n4 = (cb.like(cb.lower(b.get("businessType")), req.getBusinessType().toLowerCase()));
					Predicate n5 = (cb.like(cb.lower(b.get("cityCode")), req.getCityCode().toLowerCase()));
					Predicate n6 = (cb.like(cb.lower(b.get("cityName")), req.getCityName().toLowerCase()));
					Predicate n7 = (cb.like(cb.lower(b.get("clientName")), req.getClientName().toLowerCase()));
					Predicate n8 = (cb.like(cb.lower(b.get("clientStatus")), req.getClientStatus().toLowerCase()));
					Predicate n9 = (cb.like(cb.lower(b.get("companyId")), req.getCompanyId().toLowerCase()));
					Predicate n10 = (cb.like(cb.lower(b.get("createdBy")), req.getCreatedBy().toLowerCase()));
					// Predicate n11 =
					// (cb.like(cb.lower(b.get("customerReferenceNo")),req.getCustomerReferenceNo().toLowerCase()));
					Predicate n12 = (cb.equal(b.get("dobOrRegDate"), req.getDobOrRegDate()));
					Predicate n13 = (cb.like(cb.lower(b.get("email1")), req.getEmail1().toLowerCase()));
					Predicate n14 = (cb.like(cb.lower(b.get("email2")), req.getEmail2().toLowerCase()));
					Predicate n15 = (cb.like(cb.lower(b.get("email3")), req.getEmail3().toLowerCase()));
					Predicate n16 = (cb.equal(b.get("fax"), req.getFax().toLowerCase()));
					Predicate n17 = (cb.like(cb.lower(b.get("gender")), req.getGender().toLowerCase()));
					Predicate n18 = (cb.like(cb.lower(b.get("idNumber")), req.getIdNumber().toLowerCase()));
					Predicate n19 = (cb.like(cb.lower(b.get("isTaxExempted")), req.getIsTaxExempted().toLowerCase()));
					Predicate n20 = (cb.like(cb.lower(b.get("language")), req.getLanguage().toLowerCase()));
					Predicate n21 = (cb.like(cb.lower(b.get("languageDesc")), req.getLanguageDesc().toLowerCase()));
					Predicate n22 = (cb.equal(b.get("mobileNo1"), req.getMobileNo1()));
					Predicate n23 = (cb.equal(b.get("mobileNo2"), req.getMobileNo2()));
					Predicate n24 = (cb.equal(b.get("mobileNo3"), req.getMobileNo3()));
					Predicate n25 = (cb.like(cb.lower(b.get("nationality")), req.getNationality().toLowerCase()));
					Predicate n26 = (cb.like(cb.lower(b.get("occupation")), req.getOccupation().toLowerCase()));
					Predicate n27 = (cb.like(cb.lower(b.get("placeOfBirth")), req.getPlaceOfBirth().toLowerCase()));
					Predicate n28 = (cb.like(cb.lower(b.get("policyHolderType")),
							req.getPolicyHolderType().toLowerCase()));
					Predicate n29 = (cb.like(cb.lower(b.get("policyHolderTypeId")),
							req.getPolicyHolderTypeid().toLowerCase()));
					Predicate n30 = (cb.like(cb.lower(b.get("productId")), req.getProductId().toLowerCase()));
					Predicate n31 = (cb.like(cb.lower(b.get("regionCode")), req.getRegionCode().toLowerCase()));
					Predicate n32 = (cb.like(cb.lower(b.get("stateCode")), req.getStateCode().toLowerCase()));
					Predicate n33 = (cb.like(cb.lower(b.get("stateName")), req.getStateName().toLowerCase()));
					Predicate n34 = (cb.like(cb.lower(b.get("status")), req.getStatus().toLowerCase()));
					Predicate n35 = (cb.like(cb.lower(b.get("street")), req.getStreet().toLowerCase()));
					Predicate n36 = (cb.like(cb.lower(b.get("taxExemptedId")), req.getTaxExemptedId().toLowerCase()));
					Predicate n37 = (cb.equal(b.get("telephoneNo1"), req.getTelephoneNo1()));
					Predicate n38 = (cb.equal(b.get("telephoneNo2"), req.getTelephoneNo2()));
					Predicate n39 = (cb.equal(b.get("telephoneNo3"), req.getTelephoneNo3()));
					Predicate n40 = (cb.like(cb.lower(b.get("title")), req.getTitle().toLowerCase()));
					Predicate n41 = (cb.equal(b.get("appointmentDate"), req.getAppointmentDate()));
					Predicate n42 = (cb.like(cb.lower(b.get("preferredNotification")), req.getPreferredNotification().toLowerCase()));

					query.where(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10,
							// n11,
							n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24, n25, n26, n27, n28, n29,
							n30, n31, n32, n33, n34, n35, n36, n37, n38, n39, n40,n41,n42);
					// Get Result
					TypedQuery<EserviceCustomerDetails> result = em.createQuery(query);
					list = result.getResultList();
					if (list.size() > 0) {
						errorList.add(new Error("42", "Already have data for customerReferenceNo",
								list.get(0).getCustomerReferenceNo()));

					}
				}
			}

			else if (req.getSaveOrSubmit().equalsIgnoreCase("Save")) {
				if (StringUtils.isBlank(req.getClientName())) {
					errorList.add(new Error("01", "ClientName", "Please Enter ClientName "));
				} else if (req.getClientName().length() > 100) {
					errorList.add(new Error("01", "ClientName", "Please Enter ClientName within 100 Characters"));
				}
				if (StringUtils.isBlank(req.getPolicyHolderType())) {
					errorList.add(new Error("02", "PolicyHolderType", "Please Select PolicyHolderType "));
				}
/*				if (StringUtils.isNotBlank(req.getPolicyHolderType())) {

					if (req.getPolicyHolderType().equalsIgnoreCase("2")) {
						if (StringUtils.isBlank(req.getBusinessType())) {
							errorList.add(new Error("16", "BusinessType", "Please Select BusinessType"));
						}
					}
				}
*/
				// Date Validation
				Calendar cal = new GregorianCalendar();
				Date today = new Date();
				cal.setTime(today);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 50);
				today = cal.getTime();
				if (req.getPolicyHolderType().equalsIgnoreCase("1")) {

					if (req.getDobOrRegDate() == null) {
						errorList.add(new Error("38", "DobOrRegDate", "Please Enter DobOrRegDate "));

					} else if (req.getDobOrRegDate().after(today)) {
						errorList.add(new Error("38", "DobOrRegDate", "Please Enter DobOrRegDate as Past Date"));

					}

					LocalDate localDate1 = req.getDobOrRegDate().toInstant().atZone(ZoneId.systemDefault())
							.toLocalDate();
					LocalDate localDate2 = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

					Integer years = Period.between(localDate1, localDate2).getYears();
					if (years > 100) {
						errorList.add(new Error("38", "DobOrRegDate", "DobOrRegDate Not Accepted More than 100 Years"));

					}
				}

				if (req.getPolicyHolderType().equalsIgnoreCase("2")) {

					if (req.getDobOrRegDate() == null) {
						errorList.add(new Error("38", "DobOrRegDate", "Please Enter DobOrRegDate "));

					} else if (req.getDobOrRegDate().after(today)) {
						errorList.add(new Error("38", "DobOrRegDate", "Please Enter DobOrRegDate as Past Date"));

					}

					LocalDate localDate1 = req.getDobOrRegDate().toInstant().atZone(ZoneId.systemDefault())
							.toLocalDate();
					LocalDate localDate2 = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

					Integer years = Period.between(localDate1, localDate2).getYears();
					if (years > 100) {
						errorList.add(new Error("38", "DobOrRegDate", "DobOrRegDate Not Accepted More than 100 Years"));

					}
				}
			}
			/*
			if (StringUtils.isBlank(req.getMobileNo1())) {
				errorList.add(new Error("24", "MobileNo1", "Please Enter MobileNo1"));
			} else if (req.getMobileNo1().length() > 20) {
				errorList.add(new Error("24", "MobileNo1", "Please Enter MobileNo1 within 20 Characters"));
			} else if (!req.getMobileNo1().matches("\\d+")) {
				errorList.add(new Error("24", "MobileNo1", "Please Enter MobileNo1 only in numbers"));
			}
			if (StringUtils.isBlank(req.getEmail1())) {
				errorList.add(new Error("27", "Email1", "Please Enter Email1"));
			} else if (req.getEmail1().length() > 20) {
				errorList.add(new Error("27", "Email1", "Please Enter Email1 within 20 Characters"));
			} else {
				boolean b = isValidMail(req.getEmail1());
				if (b == false) {
					errorList.add(new Error("37", "Email1", "Please Enter Email in correct format"));
				}
			}
			*/
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			errorList.add(new Error("01", "Common Error", e.getMessage()));
		}
		return errorList;

	}

	public static boolean datevalid(String date) {
		String regex = "(([0-9]{2})/([0-9]{2})/([0-9]{4}))";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(date);
		return m.matches();

	}

	public static boolean isValidMail(String mail) {
		String regex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(mail);
		return m.matches();

	}

	@Override
	@Transactional
	public SuccessRes saveCustomerDetails(EserviceCustomerSaveReq req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddmmssSSS");
		try {
			EserviceCustomerDetails saveData = new EserviceCustomerDetails();
			Date entryDate = null;
			String createdBy = "";
			String custRefNo = "";

			if (StringUtils.isBlank(req.getCustomerReferenceNo())) {
				// Save
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				Random rand = new Random();
				int random = rand.nextInt(90) + 10;

				custRefNo = "Cust-" + sdf.format(new Date()) + random;
				res.setResponse("Saved Successfully");
				res.setSuccessId(custRefNo);
			} else {
				// Update
				custRefNo = req.getCustomerReferenceNo();
				EserviceCustomerDetails findData = repository.findByCustomerReferenceNo(req.getCustomerReferenceNo());
				entryDate = findData.getEntryDate();
				createdBy = findData.getCreatedBy();
				res.setResponse("Updated Successfully");
				res.setSuccessId(custRefNo);
			}
			dozerMapper.map(req, saveData);
			saveData.setEntryDate(entryDate);
			saveData.setCreatedBy(createdBy);
			saveData.setUpdatedDate(new Date());
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setCustomerReferenceNo(custRefNo);
			saveData.setStatus(req.getStatus());
			saveData.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
			saveData.setGender(StringUtils.isBlank(req.getGender()) ? "M" : req.getGender());
			saveData.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());

			// Age Calculation
			Date dob = req.getDobOrRegDate();
			Date today = new Date();
			int age = today.getYear() - dob.getYear();

			// From List Item Value
			ListItemValue gender = listRepo.findByItemTypeAndItemCode("GENDER", saveData.getGender());
			ListItemValue title = listRepo.findByItemTypeAndItemCode("NAME_TITLE", req.getTitle());
			ListItemValue language = listRepo.findByItemTypeAndItemCode("LANGUAGE", req.getLanguage());
			ListItemValue policyHolderType = listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_TYPE",
					req.getPolicyHolderType());
			ListItemValue policyHolderTypeId = listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_ID_TYPE",
					req.getPolicyHolderTypeid());

			if (StringUtils.isNotBlank(req.getBusinessType())) {
				ListItemValue businessType = listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE", req.getBusinessType());
				saveData.setBusinessTypeDesc(businessType.getItemValue());
			}

			String occupationDesc = getByOccupationId(req.getOccupation(), req.getCompanyId(), req.getBranchCode());
			saveData.setGenderDesc(gender.getItemValue());
			saveData.setTitleDesc(title.getItemValue());
			saveData.setLanguageDesc(language.getItemValue());
			saveData.setOccupationDesc(occupationDesc);
			saveData.setPolicyHolderTypeDesc(policyHolderType.getItemValue());
			saveData.setPolicyHolderTypeIdDesc(policyHolderTypeId.getItemValue());
			saveData.setIdType(req.getPolicyHolderTypeid());
			saveData.setIdTypeDesc(policyHolderTypeId.getItemValue());
			saveData.setAge(age);

			if((StringUtils.isNotBlank(req.getNationality()))&&(StringUtils.isNotBlank(req.getStateCode()))){
			List<StateMaster> stateCityNames = getStateAndCityName(req.getNationality(), req.getStateCode());
			saveData.setStateName(stateCityNames.get(0).getStateName() == null ? "" : stateCityNames.get(0).getStateName().toString());
			saveData.setCityName(req.getCityName());
			}
			repository.save(saveData);

			// Response

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;

	}

	public String getByOccupationId(String occupationId, String insuranceId, String branchCode) {
		String occupationDesc = "";
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<OccupationMaster> list = new ArrayList<OccupationMaster>();

			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<OccupationMaster> query = cb.createQuery(OccupationMaster.class);

			// Find All
			Root<OccupationMaster> b = query.from(OccupationMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<OccupationMaster> ocpm1 = amendId.from(OccupationMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("occupationId"), b.get("occupationId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));

			amendId.where(a1, a2, a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), insuranceId);
			Predicate n3 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n4 = cb.equal(b.get("occupationId"), occupationId);
			Predicate n6 = cb.equal(b.get("branchCode"), "99999");
			Predicate n7 = cb.or(n3, n6);
			query.where(n1, n2, n4, n7).orderBy(orderList);

			// Get Result
			TypedQuery<OccupationMaster> result = em.createQuery(query);

			list = result.getResultList();
			list.sort(Comparator.comparing(OccupationMaster::getOccupationName));
			occupationDesc = list.size() > 0 ? list.get(0).getOccupationName() : "";

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return occupationDesc;
	}

	public List<StateMaster> getStateAndCityName(String countryId, String stateCode) {
		List<StateMaster> list = new ArrayList<StateMaster>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<StateMaster> query = cb.createQuery(StateMaster.class);

			// State Effective Date Max Filter
			Root<StateMaster> s = query.from(StateMaster.class);

			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<StateMaster> ocpm2 = effectiveDate2.from(StateMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateStart")));
			Predicate seff1 = cb.equal(ocpm2.get("stateId"), stateCode);
			Predicate seff2 = cb.equal(ocpm2.get("countryId"), countryId);
			Predicate seff3 = cb.equal(ocpm2.get("status"), s.get("status"));
			Predicate seff4 = cb.lessThanOrEqualTo(ocpm2.get("effectiveDateStart"), today);
			effectiveDate2.where(seff1, seff2, seff3, seff4);

			// State Name Max Filter
			query.select(s.get("stateName"));
			Predicate s1 = cb.equal(s.get("stateId"), stateCode);
			Predicate s2 = cb.equal(s.get("countryId"), countryId);
			Predicate s3 = cb.equal(s.get("status"), s.get("status"));
			Predicate s4 = cb.equal(s.get("effectiveDateStart"), effectiveDate2);
			query.where(s1, s2, s3, s4);

			// Country Effective Date Max Filter
			Subquery<Long> country = query.subquery(Long.class);
			Root<CountryMaster> cm = country.from(CountryMaster.class);

			Subquery<Long> effectiveDate3 = query.subquery(Long.class);
			Root<CountryMaster> ocpm3 = effectiveDate3.from(CountryMaster.class);
			effectiveDate3.select(cb.max(ocpm3.get("effectiveDateStart")));
			Predicate ceff2 = cb.equal(ocpm3.get("countryId"), cm.get("countryId"));
			Predicate ceff3 = cb.equal(ocpm3.get("status"), cm.get("status"));
			Predicate ceff4 = cb.lessThanOrEqualTo(ocpm3.get("effectiveDateStart"), today);
			effectiveDate3.where(ceff2, ceff3, ceff4);

			// Country Name Max Filter
			country.select(cm.get("countryName"));
			Predicate cm2 = cb.equal(cm.get("countryId"), s.get("countryId"));
			Predicate cm3 = cb.equal(cm.get("status"), s.get("status"));
			Predicate cm4 = cb.equal(cm.get("effectiveDateStart"), effectiveDate3);
			country.where(cm2, cm3, cm4);

			// Select
			query.select(s.alias("stateName"));

			query.where(s1, s2, s3, s4);
			// Get Result
			TypedQuery<StateMaster> result = em.createQuery(query);
			list = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
		}
		return list;
	}

	@Override
	public CustomerDetailsGetRes getCustomerDetails(GetCustomerDetailsReq req) {
		CustomerDetailsGetRes res = new CustomerDetailsGetRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			EserviceCustomerDetails data = repository.findByCustomerReferenceNo(req.getCustomerReferenceNo());
			res = dozerMapper.map(data, CustomerDetailsGetRes.class);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public List<CustomerDetailsGetRes> getallCustomerDetails(GetAllCustomerDetailsReq req) {
		List<CustomerDetailsGetRes> resList = new ArrayList<CustomerDetailsGetRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			// Limit , Offset
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 10 : Integer.valueOf(req.getOffset());
			Pageable paging = PageRequest.of(limit, offset, Sort.by("updatedDate").descending());

			LoginMaster loginData = loginRepo.findByLoginId(req.getCreatedBy());
			Page<EserviceCustomerDetails> datas = null;
			if (loginData.getUserType().equalsIgnoreCase("Broker")
					|| loginData.getUserType().equalsIgnoreCase("User")) {
				datas = repository.findByCompanyIdAndBrokerBranchCodeAndProductIdAndCreatedBy(paging,
						req.getComapanyId(), req.getBrokerBranchCode(), Integer.valueOf(req.getProductId()),
						req.getCreatedBy());
			} else {
				datas = repository.findByCompanyIdAndBranchCodeAndProductIdAndCreatedBy(paging, req.getComapanyId(),
						req.getBranchCode(), Integer.valueOf(req.getProductId()), req.getCreatedBy());
			}

			for (EserviceCustomerDetails data : datas) {
				CustomerDetailsGetRes res = new CustomerDetailsGetRes();
				res = dozerMapper.map(data, CustomerDetailsGetRes.class);
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<CustomerDetailsGetRes> getbyvrtinno(EserviceCustomerSearchVrtinReq req) {
		List<CustomerDetailsGetRes> reslist = new ArrayList<CustomerDetailsGetRes>();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {
			String searchValue = req.getSearchValue();
			String companyId = req.getInsuranceId();

			// Search By Tin No
			String searchKey = "TinNumber";
			List<EserviceCustomerDetails> list = searchCustomerData(searchKey, searchValue, companyId);
			if (list.size() <= 0) {
				searchKey = "IdNumber";
				list = searchCustomerData(searchKey, searchValue, companyId);
			}
			if (list.size() <= 0) {
				searchKey = "ClientName";
				list = searchCustomerData(searchKey, searchValue, companyId);
			}

			for (EserviceCustomerDetails data : list) {
				CustomerDetailsGetRes res = new CustomerDetailsGetRes();
				dozermapper.map(data, res);
				reslist.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			;
			return null;
		}
		return reslist;
	}

	public List<EserviceCustomerDetails> searchCustomerData(String searchKey, String searchValue, String companyId) {
		List<EserviceCustomerDetails> list = new ArrayList<EserviceCustomerDetails>();
		try {
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EserviceCustomerDetails> query = cb.createQuery(EserviceCustomerDetails.class);
			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(c.get("updatedDate")));

			Predicate n1 = null;
			// Where
			if (searchKey.equalsIgnoreCase("TinNumber")) {
				n1 = cb.like(cb.lower(c.get("vrTinNo")), "%" + searchValue + "%");
			} else if (searchKey.equalsIgnoreCase("IdNumber")) {
				n1 = cb.like(cb.lower(c.get("idNumber")), "%" + searchValue + "%");
			} else if (searchKey.equalsIgnoreCase("ClientName")) {
				n1 = cb.like(cb.lower(c.get("clientName")), "%" + searchValue + "%");
			}
			Predicate n2 = cb.equal(c.get("companyId"), companyId);

			query.where(n1, n2).orderBy(orderList);
			// Get Result
			TypedQuery<EserviceCustomerDetails> result = em.createQuery(query);
			list = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			;
			return null;
		}
		return list;
	}

	@Override
	public List<CustomerDetailsGetRes> getActiveCustomerDetails(GetAllCustomerDetailsReq req) {
		List<CustomerDetailsGetRes> resList = new ArrayList<CustomerDetailsGetRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			// Limit , Offset
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 10 : Integer.valueOf(req.getOffset());
			Pageable paging = PageRequest.of(limit, offset, Sort.by("updatedDate").descending());
			LoginMaster loginData = loginRepo.findByLoginId(req.getCreatedBy());
			Page<EserviceCustomerDetails> datas = null;
			if (loginData.getUserType().equalsIgnoreCase("Broker")
					|| loginData.getUserType().equalsIgnoreCase("User")) {
				datas = repository.findByCompanyIdAndBrokerBranchCodeAndProductIdAndCreatedByAndStatus(paging,
						req.getComapanyId(), req.getBrokerBranchCode(), Integer.valueOf(req.getProductId()),
						req.getCreatedBy(), "Y");
			} else {
				datas = repository.findByCompanyIdAndBranchCodeAndProductIdAndCreatedByAndStatus(paging,
						req.getComapanyId(), req.getBranchCode(), Integer.valueOf(req.getProductId()),
						req.getCreatedBy(), "Y");
			}

			for (EserviceCustomerDetails data : datas) {
				CustomerDetailsGetRes res = new CustomerDetailsGetRes();
				res = dozerMapper.map(data, CustomerDetailsGetRes.class);
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

}
