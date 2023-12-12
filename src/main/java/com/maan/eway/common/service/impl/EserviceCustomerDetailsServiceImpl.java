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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.CountryMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.OccupationMaster;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.SeqCustrefno;
import com.maan.eway.bean.StateMaster;
import com.maan.eway.common.req.EserviceCustomerSaveReq;
import com.maan.eway.common.req.EserviceCustomerSearchVrtinReq;
import com.maan.eway.common.req.GetAllCustomerDetailsReq;
import com.maan.eway.common.req.GetCustomerDetailsReq;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.service.EserviceCustomerDetailsService;
import com.maan.eway.error.Error;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.OccupationMasterRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.SeqCustrefnoRepository;
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
	
	@Autowired
	private SeqCustrefnoRepository custRefRepo  ; 
	
	@Autowired
	private HomePositionMasterRepository homePosistionRepo;
	
	@Autowired
	private PersonalInfoRepository personalInforepo;
	

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<Error> validateCustomerDetails(EserviceCustomerSaveReq req) {
		List<Error> errorList = new ArrayList<Error>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			Calendar cal = Calendar.getInstance();

			if (req.getSaveOrSubmit().equalsIgnoreCase("Submit")) {
				if (StringUtils.isBlank(req.getClientName())) {
					errorList.add(new Error("01", "ClientName", "Please Enter ClientName "));
				} else if (req.getClientName().length() > 250) {
					errorList.add(new Error("01", "ClientName", "Please Enter ClientName within 250 Characters"));
				} 
//				else if (StringUtils.isNotBlank(req.getClientName())&& !req.getClientName().matches("[a-zA-Z.&() ]+")) {
//					errorList.add(new Error("01", "ClientName", "Please Enter Proper ClientName"));						
//				}
				
				
				if (StringUtils.isBlank(req.getAddress1())) {
					errorList.add(new Error("02", "Address1", "Please Enter Address "));
				} else if (req.getAddress1().length() > 100) {
					errorList.add(new Error("02", "Address1", "Please Enter Address within 100 Characters"));
				}
				
//				if (StringUtils.isBlank(req.getStreet())) {
//					errorList.add(new Error("03", "Street", "Please Enter Street"));
//				} else if (req.getAddress1().length() > 100) {
//					errorList.add(new Error("03", "Street", "Please Enter Street within 100 Characters"));
//				}
				
				/*if (StringUtils.isBlank(req.getAddress2())) {
					errorList.add(new Error("02", "Address2", "Please Enter Address2 "));
				} else if (req.getAddress2().length() > 100) {
					errorList.add(new Error("03", "Address2", "Please Enter Address2 within 100 Characters"));
				}*/
				
				if (StringUtils.isBlank(req.getClientStatus())) {
					errorList.add(new Error("05", "Client Status", "Please Select Client Status"));
				}
				if (StringUtils.isBlank(req.getIdType())) {
					errorList.add(new Error("09", "IdType", "Please Select Personal/Corporate"));
				}
				
				if (StringUtils.isBlank(req.getPolicyHolderTypeid())) {
					errorList.add(new Error("09", " Identity Type", "Please Select Identity Type"));
				}
				
				
				

				if (StringUtils.isBlank(req.getIdNumber())) {
					errorList.add(new Error("11", "IdNumber", "Please Enter IdNumber"));
				} else if (req.getIdNumber().length() > 100) {
					errorList.add(new Error("11", "IdNumber", "Please Enter IdNumber within 100 Characters"));
				} else if (! req.getIdNumber().matches("[A-Za-z0-9]+") ) {
					errorList.add(new Error("11", "IdNumber", "Please Enter Valid IdNumber "));
				}
				
				
//				if (StringUtils.isBlank(req.getPreferredNotification())) {
//					errorList.add(new Error("12", "PreferredNotification", "Please select Preferred Notification"));
//				}
				
//				if(StringUtils.isNotBlank(req.getAppointmentDate().toString())) {
//				cal.add(Calendar.DATE, -1);
//				Date yesterday = cal.getTime();
//				String a1 = sdf.format(req.getAppointmentDate());
//				Date a = sdf.parse(a1);
//
//				if (a.before(yesterday)) {
//					errorList.add(new Error("07", "Appointment Date", "Please Enter Appointment Date as Future Date"));
//					} 
//				}
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
				
				if (StringUtils.isNotBlank(req.getPinCode())) {
//					 if (! req.getPinCode().matches("[0-9a-bA-Z]+") ) {
//						 errorList.add(new Error("18", "PinCode", "Please Enter Valid Number In Po Box"));
//						 
//					 } else
					if (req.getPinCode().length() > 20) {
							errorList.add(new Error("18", "PinCode", "Please Enter Po Box within 20 Characters"));
					}
				} 
				/*if (StringUtils.isBlank(req.getStreet())) {
					errorList.add(new Error("19", "Street", "Please Enter Street"));
				}
				else if (StringUtils.isNotBlank(req.getStreet()) && req.getStreet().length() > 100) {
					errorList.add(new Error("19", "Street", "Please Enter Street within 100 Characters"));
				}*/
				if (StringUtils.isNotBlank(req.getFax()) && req.getFax().length() > 20) {
					errorList.add(new Error("20", "Fax", "Please Enter Fax within 20 Characters"));
				}
				/*if (StringUtils.isBlank(req.getTelephoneNo1())) {
					errorList.add(new Error("21", "TelephoneNo1", "Please Enter TelephoneNo1"));
				}
				else if (StringUtils.isNotBlank(req.getTelephoneNo1()) && req.getTelephoneNo1().length() > 20) {
					errorList.add(new Error("21", "TelephoneNo1", "Please Enter TelephoneNo within 20 Characters"));
				} else if (!req.getTelephoneNo1().matches("\\d+")) {
					errorList.add(new Error("21", "TelephoneNo1", "Please Enter TelephoneNo only in numbers"));
				} */
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
				
				if (StringUtils.isBlank(req.getOccupation()) ) {
					errorList.add(new Error("23", "Occupation", "Please Select Occupation"));
				}

				if(req.getOccupation().equalsIgnoreCase("76")){
					if (StringUtils.isBlank(req.getOtherOccupation()) ) {
						errorList.add(new Error("47", "Other Occupation", "Please Enter Other Occupation"));
					}else if (req.getOtherOccupation().length() > 100){
						errorList.add(new Error("47","Other Occupation", "Please Enter Other Occupation within 100 Characters")); 
				}}
				
				if (StringUtils.isBlank(req.getMobileNo1())) {
					errorList.add(new Error("24", "MobileNo", "Please Enter MobileNo"));
				} else if (req.getMobileNo1().length() > 10||req.getMobileNo1().length() < 8) {
					errorList.add(new Error("24", "MobileNo", "Please Enter Valid MobileNo"));
				} else if (!req.getMobileNo1().matches("\\d+")) {
					errorList.add(new Error("24", "MobileNo", "Please Enter MobileNo only in numbers"));
				}

				
				
				if (StringUtils.isNotBlank(req.getMobileNo3()) &&( req.getMobileNo3().length() > 10||req.getMobileNo3().length() < 10)) {
					errorList.add(new Error("26", "MobileNo3", "Please Enter MobileNo3 must be 10 digts"));
				} else if (StringUtils.isNotBlank(req.getMobileNo3()) && !req.getMobileNo3().matches("\\d+")) {
					errorList.add(new Error("26", "MobileNo3", "Please Enter MobileNo3 only in numbers"));
				}
//				if (StringUtils.isBlank(req.getEmail1())) {
//					errorList.add(new Error("27", "Email1", "Please Enter Email"));
//				} else
				if ( StringUtils.isNotBlank(req.getEmail1()) ) {
					if( req.getEmail1().length() > 100 ) {
						errorList.add(new Error("27", "Email1", "Please Enter Email within 100 Characters"));
					} else if(StringUtils.isNotBlank(req.getEmail1())) {
						boolean b = isValidMail(req.getEmail1());
						if (b == false) {
							errorList.add(new Error("37", "Email", "Please Enter Email in correct format"));
						}
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
					errorList.add(new Error("28", "Email2", "Email2 Is Already Available In Email"));
				}
				if (StringUtils.isNotBlank(req.getEmail1()) && StringUtils.isNotBlank(req.getEmail3())
						&& req.getEmail1().equalsIgnoreCase(req.getEmail3())) {
					errorList.add(new Error("28", "Email2", "Email3 Is Already Available In Email"));
				}
				if (StringUtils.isNotBlank(req.getEmail2()) && StringUtils.isNotBlank(req.getEmail3())
						&& req.getEmail2().equalsIgnoreCase(req.getEmail3())) {
					errorList.add(new Error("28", "Email3", "Email3 Is Already Available In Email2"));
				}

				if (StringUtils.isNotBlank(req.getTelephoneNo1()) && StringUtils.isNotBlank(req.getTelephoneNo2())
						&& req.getTelephoneNo1().equalsIgnoreCase(req.getTelephoneNo2())) {
					errorList.add(new Error("28", "TelephoneNo2", "TelephoneNo2 Is Already Available In TelephoneNo"));
				}
				if (StringUtils.isNotBlank(req.getTelephoneNo1()) && StringUtils.isNotBlank(req.getTelephoneNo3())
						&& req.getTelephoneNo1().equalsIgnoreCase(req.getTelephoneNo3())) {
					errorList.add(new Error("28", "TelephoneNo2", "TelephoneNo2 Is Already Available In TelephoneNo"));
				}
				if (StringUtils.isNotBlank(req.getTelephoneNo2()) && StringUtils.isNotBlank(req.getTelephoneNo3())
						&& req.getTelephoneNo2().equalsIgnoreCase(req.getTelephoneNo3())) {
					errorList.add(new Error("28", "TelephoneNo3", "TelephoneNo3 Is Already Available In TelephoneNo2"));
				}

				if (StringUtils.isNotBlank(req.getMobileNo1()) && StringUtils.isNotBlank(req.getMobileNo2())
						&& req.getMobileNo1().equalsIgnoreCase(req.getMobileNo2())) {
					errorList.add(new Error("28", "MobileNo2", "MobileNo2 Is Already Available In MobileNo"));
				}
				if (StringUtils.isNotBlank(req.getMobileNo1()) && StringUtils.isNotBlank(req.getMobileNo3())
						&& req.getMobileNo1().equalsIgnoreCase(req.getMobileNo3())) {
					errorList.add(new Error("28", "MobileNo2", "MobileNo3 Is Already Available In MobileNo"));
				}
				if (StringUtils.isNotBlank(req.getMobileNo2()) && StringUtils.isNotBlank(req.getMobileNo3())
						&& req.getMobileNo2().equalsIgnoreCase(req.getMobileNo3())) {
					errorList.add(new Error("28", "MobileNo3", "MobileNo3 Is Already Available In MobileNo2"));
				}
//
//				if (StringUtils.isNotBlank(req.getAddress1()) && StringUtils.isNotBlank(req.getAddress2())
//						&& req.getAddress1().equalsIgnoreCase(req.getAddress2())) {
//					errorList.add(new Error("28", "Address2", "Address2 Is Already Available In Address"));
//				}

				
				
				
				// Status Validation
//				if(StringUtils.isNotBlank(req.getCompanyId()) && "100004".equalsIgnoreCase(req.getCompanyId())) {
//					//
//					if(req.getPolicyHolderType().equalsIgnoreCase("2")) {
//					if (StringUtils.isNotBlank(req.getMobileNo2()) && req.getMobileNo2().length() > 20) {
//						errorList.add(new Error("25", "MobileNo2", "Please Enter MobileNo2 within 20 Characters"));
//					} else if (StringUtils.isNotBlank(req.getMobileNo2()) && !req.getMobileNo2().matches("\\d+")) {
//						errorList.add(new Error("25", "MobileNo2", "Please Enter MobileNo2 only in numbers"));
//					}
//					}
//					
//				} else {
//					if (StringUtils.isBlank(req.getTitle()))  {
//						errorList.add(new Error("04", "Title", "Please Select Title"));
//					}
//					if (StringUtils.isBlank(req.getNationality())) {
//						errorList.add(new Error("12", "Country", "Please select Country"));
//					}
//					if (StringUtils.isBlank(req.getPreferredNotification())) {
//						errorList.add(new Error("09", "Preferred Notification", "Please Select Preferred Notification"));
//					}
//					
//					if (StringUtils.isNotBlank(req.getPolicyHolderType())) {
//
//						if (req.getPolicyHolderType().equalsIgnoreCase("2")) {
//							if (StringUtils.isBlank(req.getBusinessType())) {
//								errorList.add(new Error("16", "BusinessType", "Please Select BusinessType"));
//							}
//						}
//					}
//					if( StringUtils.isNotBlank(req.getPolicyHolderType()) && req.getPolicyHolderType().equalsIgnoreCase("2") ) {
//						if (StringUtils.isBlank(req.getVrTinNo())) {
//							errorList.add(new Error("42", "VRN/GST Number", "Please Enter VRN/GST Number"));
//						} else if (req.getVrTinNo().length() > 20) {
//							errorList.add(new Error("42", "VRN/GST Number", "Please Enter VRN/GST Number within 20 Characters"));
//						}
//						
//					}
//					if (StringUtils.isBlank(req.getRegionCode())) {
//						errorList.add(new Error("18", "RegionCode", "Please Enter RegionCode"));
//					} else if (req.getRegionCode().length() > 20) {
//						errorList.add(new Error("18", "RegionCode", "Please Enter RegionCode within 20 Characters"));
//					}
//					
//					if (StringUtils.isBlank(req.getIsTaxExempted())) {
//						errorList.add(new Error("31", "IsTaxExempted", "Please Select IsTaxExempted"));
//
//					}else if (req.getIsTaxExempted().equals("Y")) {
//						if (StringUtils.isBlank(req.getTaxExemptedId())) {
//							errorList.add(new Error("32", "TaxExemptedId", "Please Enter TaxExemptedId"));
//						} else if (req.getTaxExemptedId().length() > 20) {
//							errorList.add(
//									new Error("33", "TaxExemptedId", "Please Enter TaxExemptedId within 20 Characters"));
//						}
//
//					}
//					if (StringUtils.isBlank(req.getStatus())) {
//						errorList.add(new Error("34", "Status", "Please Enter Status"));
//					} else if (req.getStatus().length() > 1) {
//						errorList.add(new Error("34", "Status", "Enter Status in 1 Character Only"));
//					} else if (!("Y".equals(req.getStatus()) || "N".equals(req.getStatus())
//							|| "P".equals(req.getStatus()))) {
//						errorList.add(new Error("34", "Status", "Plese Enter Status"));
//					}
//					if (StringUtils.isBlank(req.getStateCode())) {
//						errorList.add(new Error("45", "RegionCode", "Please Enter RegionCode "));
//					}
//					
//					if (StringUtils.isBlank(req.getMobileCode1())) {
//						errorList.add(new Error("46", "MobileCode", "Please Select MobileCode "));
//					}
//				}
				
				
				if (StringUtils.isBlank(req.getTitle()))  {
					errorList.add(new Error("04", "Title", "Please Select Title"));
				}
				if (StringUtils.isBlank(req.getNationality())) {
					errorList.add(new Error("12", "Country", "Please select Country"));
				}
				if (StringUtils.isBlank(req.getPreferredNotification())) {
					errorList.add(new Error("09", "Preferred Notification", "Please Select Preferred Notification"));
				}
				
				if (StringUtils.isNotBlank(req.getPolicyHolderType())) {

					if (req.getPolicyHolderType().equalsIgnoreCase("2")) {
						if (StringUtils.isBlank(req.getBusinessType())) {
							errorList.add(new Error("16", "BusinessType", "Please Select BusinessType"));
						}
					}
				}
				if( StringUtils.isNotBlank(req.getPolicyHolderType()) && req.getPolicyHolderType().equalsIgnoreCase("2") ) {
					if (StringUtils.isBlank(req.getVrTinNo())) {
						errorList.add(new Error("42", "VRN/GST Number", "Please Enter VRN/GST Number"));
					} else if (req.getVrTinNo().length() > 20) {
						errorList.add(new Error("42", "VRN/GST Number", "Please Enter VRN/GST Number within 20 Characters"));
					}
					
				}
				if (StringUtils.isBlank(req.getRegionCode())) {
					errorList.add(new Error("18", "RegionCode", "Please Enter RegionCode"));
				} else if (req.getRegionCode().length() > 20) {
					errorList.add(new Error("18", "RegionCode", "Please Enter RegionCode within 20 Characters"));
				}
				
				if (StringUtils.isBlank(req.getIsTaxExempted())) {
					errorList.add(new Error("31", "IsTaxExempted", "Please Select IsTaxExempted"));

				}else if (req.getIsTaxExempted().equals("Y")) {
					if (StringUtils.isBlank(req.getTaxExemptedId())) {
						errorList.add(new Error("32", "TaxExemptedId", "Please Enter TaxExemptedId"));
					} else if (req.getTaxExemptedId().length() > 20) {
						errorList.add(
								new Error("33", "TaxExemptedId", "Please Enter TaxExemptedId within 20 Characters"));
					}

				}
				if (StringUtils.isBlank(req.getStatus())) {
					errorList.add(new Error("34", "Status", "Please Enter Status"));
				} else if (req.getStatus().length() > 1) {
					errorList.add(new Error("34", "Status", "Enter Status in 1 Character Only"));
				} else if (!("Y".equals(req.getStatus()) || "N".equals(req.getStatus())
						|| "P".equals(req.getStatus()))) {
					errorList.add(new Error("34", "Status", "Plese Enter Status"));
				}
				if (StringUtils.isBlank(req.getStateCode())) {
					errorList.add(new Error("45", "RegionCode", "Please Enter RegionCode "));
				}
				
				if (StringUtils.isBlank(req.getMobileCode1())) {
					errorList.add(new Error("46", "MobileCode", "Please Select MobileCode "));
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
				
				
				if (StringUtils.isNotBlank(req.getPolicyHolderType()) && req.getPolicyHolderType().equalsIgnoreCase("1")) {
					if( StringUtils.isNotBlank(req.getIdType()) && req.getIdType().equalsIgnoreCase("1")) {
						if (req.getDobOrRegDate() == null) {
							errorList.add(new Error("38", "DobOrRegDate", "Please Select Dob "));
						}
					}
					
					try {
					if (req.getDobOrRegDate() != null) {
						if (req.getDobOrRegDate().after(today)) {
							errorList.add(new Error("38", "DobOrRegDate", "Please Enter Dob as Past Date"));

						} else {
							LocalDate localDate1 = req.getDobOrRegDate().toInstant().atZone(ZoneId.systemDefault())
									.toLocalDate();
							LocalDate localDate2 = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

							Integer years = Period.between(localDate1, localDate2).getYears();
							if (years > 100) {
								errorList.add(new Error("38", "DobOrRegDate", "Dob Not Accepted More than 100 Years"));

							} else if (years < 18) {
								errorList.add(new Error("38", "DobOrRegDate", "Dob Not Accepted Less than 18 Years For Induvidual"));

							}
		
						}

					}else {
						errorList.add(new Error("38", "DobOrRegDate", "DobOrRegDate Not Valid"));
					}
					}catch (Exception e) {
						errorList.add(new Error("38", "DobOrRegDate", "DobOrRegDate Not Valid"));
					}
				}

				if (req.getPolicyHolderType().equalsIgnoreCase("2")) {
					try {
					if (req.getDobOrRegDate() != null) {
						cal.setTime(today);
						cal.add(Calendar.DAY_OF_MONTH, +1);
						cal.set(Calendar.HOUR_OF_DAY, 23);
						cal.set(Calendar.MINUTE, 50);
						Date tomorrow = cal.getTime();
						if (req.getDobOrRegDate().after(tomorrow)) {
							errorList.add(new Error("38", "DobOrRegDate", "Please Enter RegDate as Past Date"));

						} else if(req.getDobOrRegDate()!=null ) {
							LocalDate localDate1 = req.getDobOrRegDate().toInstant().atZone(ZoneId.systemDefault())
									.toLocalDate();
							LocalDate localDate2 = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

							Integer years = Period.between(localDate1, localDate2).getYears();
							if (years > 100) {
								errorList.add(new Error("38", "DobOrRegDate", "RegDate Not Accepted More than 100 Years"));

							}
						}

					}else {
						errorList.add(new Error("38", "DobOrRegDate", "DobOrRegDate Not Valid"));
					}
					}catch (Exception e) {
						errorList.add(new Error("38", "DobOrRegDate", "DobOrRegDate Not Valid"));
					}
				}

				if (StringUtils.isBlank(req.getBranchCode())) {
					errorList.add(new Error("39", "BranchCode", "Please Enter BranchCode "));
				} else if (req.getBranchCode().length() > 20) {
					errorList.add(new Error("39", "BranchCode", "Please Enter BranchCode within 20 Characters"));
				}
				
//				if (StringUtils.isBlank(req.getBranchCode())) {
//					errorList.add(new Error("39", "BranchCode", "Please Enter BranchCode "));
//				}
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
				
				
				

//			if (StringUtils.isBlank(req.getStateName())) {
//				errorList.add(new Error("43", "StateName", "Please Select StateName"));
//			}
				
				if (StringUtils.isBlank(req.getCityName())) {
					errorList.add(new Error("43", "District", "Please Select District "));
				} else if (req.getCityName().length() > 100) {
					errorList.add(new Error("43", "District", "Please Enter District within 100 Characters"));
				}

				/*if (StringUtils.isBlank(req.getStreet())) {
					errorList.add(new Error("44", "Street", "Please Enter Street "));
				} else if (req.getStreet().length() > 100) {
					errorList.add(new Error("44", "Street", "Please Enter Street within 100 Characters"));
				}*/
				
				
				
//				if (StringUtils.isBlank(req.getWhatsappCode())) {
//					errorList.add(new Error("47", "WhatsappCode", "Please Select WhatsappCode "));
//				}
				
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
				//	Predicate n35 = (cb.like(cb.lower(b.get("street")), req.getStreet().toLowerCase()));
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
							n30, n31, n32, n33, n34, /*n35,*/ n36, n37, n38, n39, n40,n41,n42);
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
			//	Calendar cal = new GregorianCalendar();
				Date today = new Date();
				cal.setTime(today);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 50);
				today = cal.getTime();
				if (req.getPolicyHolderType().equalsIgnoreCase("1")) {

					if (req.getDobOrRegDate() != null) {
						if (StringUtils.isBlank(req.getGender()) ) {
							errorList.add(new Error("23", "Gender", "Please Select Gender"));
						}
						if (req.getDobOrRegDate().after(today)) {
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

				if (req.getPolicyHolderType().equalsIgnoreCase("2")) {

					if (req.getDobOrRegDate() != null) {
						 if (req.getDobOrRegDate().after(today)) {
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
	//	SimpleDateFormat sdf = new SimpleDateFormat("yyMMddmmssSSS");
		try {
			EserviceCustomerDetails saveData = new EserviceCustomerDetails();
			Date entryDate = null;	
			String createdBy = "";
			String custRefNo = "";
			Integer productId;
        if (StringUtils.isBlank(req.getCustomerReferenceNo())) {
				// Save
				entryDate = new Date();
				createdBy = req.getCreatedBy();
			//	Random rand = new Random();
			//	int random = rand.nextInt(90) + 10;
				productId=Integer.valueOf(req.getProductId());
				custRefNo = "Cust-" +   generateCustRefNo() ; // idf.format(new Date()) + random ;
				res.setResponse("Saved Successfully");
				res.setSuccessId(custRefNo);
			} else {
				// Update
				custRefNo = req.getCustomerReferenceNo();
				EserviceCustomerDetails findData = repository.findByCustomerReferenceNo(req.getCustomerReferenceNo());
				entryDate = findData.getEntryDate();
				createdBy = findData.getCreatedBy();
				productId=findData.getProductId();
				res.setResponse("Updated Successfully");
				res.setSuccessId(custRefNo);
			}
			dozerMapper.map(req, saveData);
			saveData.setProductId(productId);
			saveData.setEntryDate(entryDate);
			saveData.setCreatedBy(createdBy);
			saveData.setUpdatedDate(new Date());
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setCustomerReferenceNo(custRefNo);
			saveData.setStatus(req.getStatus());
			saveData.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
			saveData.setGender(StringUtils.isBlank(req.getGender()) ? "M" : req.getGender());
			saveData.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());
			saveData.setBrokerBranchCode(req.getBrokerBranchCode());
			// Age Calculation
			int age = 0 ;
			Date dob = null;
			if (req.getDobOrRegDate() !=null) {
				dob = req.getDobOrRegDate();
				Date today = new Date();
				age = today.getYear() - dob.getYear();
			}

			// From List Item Value
			String gender = getListItem (req.getCompanyId() , req.getBranchCode() ,"GENDER",req.getGender());// listRepo.findByItemTypeAndItemCode("GENDER", saveData.getGender());
			String title = getListItem (req.getCompanyId() , req.getBranchCode() ,"NAME_TITLE",req.getTitle());//listRepo.findByItemTypeAndItemCode("NAME_TITLE", req.getTitle());
			String language = getListItem (req.getCompanyId() , req.getBranchCode() ,"LANGUAGE",req.getLanguage());//listRepo.findByItemTypeAndItemCode("LANGUAGE", req.getLanguage());
			String policyHolderType = getListItem ("99999" , req.getBranchCode() ,"POLICY_HOLDER_TYPE",req.getPolicyHolderType());//listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_TYPE",	req.getPolicyHolderType());
			String policyHolderTypeId = getListItem (req.getCompanyId(), req.getBranchCode() ,"POLICY_HOLDER_ID_TYPE",req.getPolicyHolderTypeid());// listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_ID_TYPE", req.getPolicyHolderTypeid());
			
			if(StringUtils.isNotBlank(req.getMobileCode1())){		        
				String mobileCode1 = getListItem (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode1());
			saveData.setMobileCodeDesc1(mobileCode1);

			}
	        if(StringUtils.isNotBlank(req.getMobileCode2())){
	        	String mobileCode2 = getListItem (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode2());
			saveData.setMobileCodeDesc2(mobileCode2);

	        }
	       
	        
	        if(StringUtils.isNotBlank(req.getMobileCode3())){		        
	        	String mobileCode3 = getListItem (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode3());
			saveData.setMobileCodeDesc3(mobileCode3);

	        }
	        if(StringUtils.isNotBlank(req.getWhatsappCode())){		        
	        	String whatsappCode = getListItem (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getWhatsappCode());
			saveData.setWhatsappCodeDesc(whatsappCode);

	        }			
			
			if (StringUtils.isNotBlank(req.getBusinessType())) {
				String businessType =  getListItem ("99999" , req.getBranchCode() ,"BUSINESS_TYPE",req.getBusinessType());//listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE", req.getBusinessType());
				saveData.setBusinessTypeDesc(businessType);
			}
 			String occupationDesc = getByOccupationId(req.getOccupation(), req.getCompanyId(),req.getProductId() , req.getBranchCode());
			
//			if(StringUtils.isNotBlank(req.getCompanyId()) && "100004".equalsIgnoreCase(req.getCompanyId()) ) {
//				saveData.setTitleDesc(null);
//				saveData.setPreferredNotification("Sms");
//				saveData.setIsTaxExempted("N");
//				saveData.setRegionCode(null);
//				saveData.setStatus("Y");
//				saveData.setBusinessType(null);
//				saveData.setVrTinNo(null);
//				saveData.setVrnGst(null);
//			    String mobileCode2 = getListItem1(req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE");
//		        saveData.setMobileCode1(mobileCode2);
//				saveData.setMobileCode2(mobileCode2);
//		     	String country = getByCountry(req.getCompanyId());
//				saveData.setNationality(country);
//			 	
//			}else {
//				saveData.setTitleDesc(title);
//				saveData.setPreferredNotification(req.getPreferredNotification());
//				saveData.setIsTaxExempted(req.getIsTaxExempted());
//				saveData.setRegionCode(req.getRegionCode());
//				saveData.setStatus(req.getStatus());
//				saveData.setBusinessType(req.getBusinessType());
//				saveData.setVrTinNo(req.getVrTinNo());
//				saveData.setVrnGst(req.getVrTinNo());
//				saveData.setMobileCode1(req.getMobileCode1());
//				saveData.setMobileCode2(req.getMobileCode2()==null?"":req.getMobileCode2());
//			}
			saveData.setTitleDesc(title);
			saveData.setPreferredNotification(req.getPreferredNotification());
			saveData.setIsTaxExempted(req.getIsTaxExempted());
			saveData.setRegionCode(req.getRegionCode());
			saveData.setStatus(req.getStatus());
			saveData.setBusinessType(req.getBusinessType());
			saveData.setVrTinNo(req.getVrTinNo());
			saveData.setVrnGst(req.getVrTinNo());
			saveData.setMobileCode1(req.getMobileCode1());
			saveData.setMobileCode2(req.getMobileCode2()==null?"":req.getMobileCode2());
			saveData.setGenderDesc(gender);
			saveData.setTitleDesc(title);
			saveData.setLanguageDesc(language);
			saveData.setOccupationDesc(occupationDesc);
			saveData.setOtherOccupation(req.getOtherOccupation());
			saveData.setPolicyHolderTypeDesc(policyHolderType);
			saveData.setPolicyHolderTypeIdDesc(policyHolderTypeId);
			saveData.setIdType(req.getPolicyHolderTypeid());
			saveData.setIdTypeDesc(policyHolderTypeId);
			saveData.setVrTinNo(req.getVrTinNo());
			saveData.setVrnGst(req.getVrTinNo());
			saveData.setAge(age);
			saveData.setMobileCode1(req.getMobileCode1());
			//saveData.setStreet(req.getStreet());
			saveData.setMobileCode2(req.getMobileCode2()==null?"":req.getMobileCode2());
			saveData.setMobileCode3(req.getMobileCode3()==null?"":req.getMobileCode3());
			saveData.setWhatsappCode(req.getWhatsappCode());
			saveData.setRegionCode(req.getStateCode());
			saveData.setStateCode(StringUtils.isBlank(req.getStateCode()) ?null :Integer.valueOf(req.getStateCode()));
			saveData.setStateName(req.getStateName());
			saveData.setCityCode(StringUtils.isBlank(req.getCityCode())?null :Integer.valueOf(req.getCityCode()));
			saveData.setCityName(req.getCityName());
			saveData.setRegionCode(req.getRegionCode());
			
			
			
			
//			if((StringUtils.isNotBlank(req.getNationality()))&&(StringUtils.isNotBlank(req.getStateCode()))){
//			List<StateMaster> stateCityNames = getStateAndCityName(req.getNationality(), req.getStateCode());
//			saveData.setStateName(stateCityNames.get(0).getStateName() == null ? "" : stateCityNames.get(0).getStateName().toString());
//			saveData.setCityName(req.getCityName());
//			}
			repository.save(saveData);

			//Personal Info Update
			
			//Endorsement flow and B2C Flow
			//Type=B2C
			if(StringUtils.isNotBlank(req.getEndtCategDesc())) {
			if("Non Financial".equalsIgnoreCase(req.getEndtCategDesc().toString())) {
				PersonalInfo savePersonalInfo=new PersonalInfo();
				HomePositionMaster homedata=homePosistionRepo.findByQuoteNo(req.getQuoteNo());
			//	PersonalInfo personalInfodata=personalInforepo.findByCustomerId(homedata.getCustomerId());
				dozerMapper.map(req, saveData);
				savePersonalInfo.setPinCode(req.getPinCode());
				savePersonalInfo.setCustomerId(homedata.getCustomerId());
				savePersonalInfo.setIdNumber(req.getIdNumber());
				savePersonalInfo.setCreatedBy(createdBy);
				savePersonalInfo.setUpdatedDate(new Date());
				savePersonalInfo.setUpdatedBy(req.getCreatedBy());
				savePersonalInfo.setCustomerReferenceNo(custRefNo);
				savePersonalInfo.setAddress1(req.getAddress1());
				savePersonalInfo.setAddress2(req.getAddress2());
				savePersonalInfo.setAge(age);
				savePersonalInfo.setBranchCode(req.getBranchCode());
				savePersonalInfo.setBusinessType(req.getBusinessType());
				savePersonalInfo.setOtherOccupation(req.getOtherOccupation());
				if (StringUtils.isNotBlank(req.getBusinessType())) {
					String businessType =  getListItem ("99999" , req.getBranchCode() ,"BUSINESS_TYPE",req.getBusinessType());//listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE", req.getBusinessType());
					savePersonalInfo.setBusinessTypeDesc(businessType);
						
				}
				
				
				savePersonalInfo.setRegionCode(req.getRegionCode());
				savePersonalInfo.setIsTaxExempted(req.getIsTaxExempted());
				savePersonalInfo.setCityCode(req.getCityCode());
				savePersonalInfo.setCityName(req.getCityName());
				savePersonalInfo.setClientName(req.getClientName());
				savePersonalInfo.setClientStatus(req.getClientStatus());
				savePersonalInfo.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
				savePersonalInfo.setCompanyId(req.getCompanyId());
				savePersonalInfo.setCreatedBy(req.getCreatedBy());
				savePersonalInfo.setCustomerReferenceNo(req.getCustomerReferenceNo());
 				savePersonalInfo.setDobOrRegDate(dob);
  				savePersonalInfo.setEmail1(req.getEmail1());
				savePersonalInfo.setEmail2(req.getEmail2());
				savePersonalInfo.setEmail3(req.getEmail3());
				savePersonalInfo.setEndorsementDate(req.getEndorsementDate());
				savePersonalInfo.setEndorsementEffdate(req.getEndorsementEffdate());
				savePersonalInfo.setEndorsementRemarks(req.getEndorsementRemarks());
				savePersonalInfo.setEndorsementType(req.getEndorsementType());
				savePersonalInfo.setEndorsementTypeDesc(req.getEndorsementTypeDesc());
				savePersonalInfo.setEndtCategDesc(req.getEndtCategDesc());
				savePersonalInfo.setEndtCount(req.getEndtCount());
				savePersonalInfo.setEndtPrevPolicyNo(req.getEndtPrevPolicyNo());
				savePersonalInfo.setEndtPrevQuoteNo(req.getEndtPrevQuoteNo());
				savePersonalInfo.setEndtStatus(req.getEndtStatus());
				savePersonalInfo.setEntryDate(new Date());
				savePersonalInfo.setFax(req.getFax());
				savePersonalInfo.setGender(StringUtils.isBlank(req.getGender()) ? "M" : req.getGender());
				savePersonalInfo.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());
				savePersonalInfo.setGenderDesc(gender);
				savePersonalInfo.setGenderDesc(gender);
				savePersonalInfo.setTitleDesc(title);
				savePersonalInfo.setLanguageDesc(language);
				savePersonalInfo.setOccupationDesc(occupationDesc);
				
						
				// Induvidual / Corporate
				savePersonalInfo.setPolicyHolderType(req.getPolicyHolderType());
				savePersonalInfo.setPolicyHolderTypeDesc(policyHolderType);
				
				// Possport or etc
				savePersonalInfo.setPolicyHolderTypeid(req.getPolicyHolderTypeid());
				savePersonalInfo.setPolicyHolderTypeIdDesc(policyHolderTypeId);
				savePersonalInfo.setIdType(req.getPolicyHolderTypeid());
				savePersonalInfo.setIdTypeDesc(policyHolderTypeId);
				
				savePersonalInfo.setMobileCode1(req.getMobileCode1());
				savePersonalInfo.setMobileCode2(req.getMobileCode2()==null?"":req.getMobileCode2());
				savePersonalInfo.setMobileCode3(req.getMobileCode3()==null?"":req.getMobileCode3());
				savePersonalInfo.setMobileNo1(req.getMobileNo1());
				savePersonalInfo.setMobileNo2(req.getMobileNo2());
				savePersonalInfo.setMobileNo3(req.getMobileNo3());
				savePersonalInfo.setWhatsappCode(req.getWhatsappCode());
				if (StringUtils.isNotBlank(req.getMobileCode1())) {
 					ListItemValue mobiledesc1 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode1(),req.getCompanyId());
					savePersonalInfo.setMobileCodeDesc1(mobiledesc1.getItemValue());

				}
				if (StringUtils.isNotBlank(req.getMobileCode2())) {
					ListItemValue mobiledesc2 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode2(),req.getCompanyId());
					savePersonalInfo.setMobileCodeDesc2(mobiledesc2.getItemValue());

				}
				if (StringUtils.isNotBlank(req.getMobileCode3())) {
					ListItemValue mobiledesc3 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode3(),req.getCompanyId());
					savePersonalInfo.setMobileCodeDesc3(mobiledesc3.getItemValue());

				}
				if (StringUtils.isNotBlank(req.getWhatsappCode())) {
					ListItemValue whatsappCode = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
							req.getWhatsappCode(),req.getCompanyId());
					savePersonalInfo.setWhatsappcodeDesc(whatsappCode.getItemValue());

				}
				savePersonalInfo.setRegionCode(req.getRegionCode());
				savePersonalInfo.setStateCode(req.getStateCode());
				savePersonalInfo.setStateName(req.getStateName());
				savePersonalInfo.setStatus(req.getStatus());
				savePersonalInfo.setNationality(req.getNationality());
				savePersonalInfo.setVrTinNo(req.getVrTinNo());
				savePersonalInfo.setVrnGst(req.getVrTinNo());
				personalInforepo.save(savePersonalInfo);
			}
			}else if(StringUtils.isNotBlank(req.getType())) {
				HomePositionMaster homedata=homePosistionRepo.findByQuoteNo(req.getQuoteNo());
				if("b2c".equalsIgnoreCase(req.getType().toString()) && homedata !=null ) {
					PersonalInfo savePersonalInfo=new PersonalInfo();
					
				//	PersonalInfo personalInfodata=personalInforepo.findByCustomerId(homedata.getCustomerId());
					dozerMapper.map(req, saveData);
					savePersonalInfo.setPinCode(req.getPinCode());
					savePersonalInfo.setCustomerId(homedata.getCustomerId());
					savePersonalInfo.setIdNumber(req.getIdNumber());
					savePersonalInfo.setCreatedBy(createdBy);
					savePersonalInfo.setUpdatedDate(new Date());
					savePersonalInfo.setUpdatedBy(req.getCreatedBy());
					savePersonalInfo.setCustomerReferenceNo(custRefNo);
					savePersonalInfo.setAddress1(req.getAddress1());
					savePersonalInfo.setAddress2(req.getAddress2());
					savePersonalInfo.setAge(age);
					savePersonalInfo.setBranchCode(req.getBranchCode());
					savePersonalInfo.setBusinessType(req.getBusinessType());
					if (StringUtils.isNotBlank(req.getBusinessType())) {
						String businessType =  getListItem ("99999" , req.getBranchCode() ,"BUSINESS_TYPE",req.getBusinessType());//listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE", req.getBusinessType());
						savePersonalInfo.setBusinessTypeDesc(businessType);
					}
					
					savePersonalInfo.setIsTaxExempted(req.getIsTaxExempted());
					savePersonalInfo.setCityCode(req.getCityCode());
					savePersonalInfo.setCityName(req.getCityName());
					savePersonalInfo.setClientName(req.getClientName());
					savePersonalInfo.setClientStatus(req.getClientStatus());
					savePersonalInfo.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
					savePersonalInfo.setCompanyId(req.getCompanyId());
					savePersonalInfo.setCreatedBy(req.getCreatedBy());
					savePersonalInfo.setCustomerReferenceNo(req.getCustomerReferenceNo());
					savePersonalInfo.setDobOrRegDate(req.getDobOrRegDate());
					savePersonalInfo.setEmail1(req.getEmail1());
					savePersonalInfo.setEmail2(req.getEmail2());
					savePersonalInfo.setEmail3(req.getEmail3());
					savePersonalInfo.setEndorsementDate(req.getEndorsementDate());
					savePersonalInfo.setEndorsementEffdate(req.getEndorsementEffdate());
					savePersonalInfo.setEndorsementRemarks(req.getEndorsementRemarks());
					savePersonalInfo.setEndorsementType(req.getEndorsementType());
					savePersonalInfo.setEndorsementTypeDesc(req.getEndorsementTypeDesc());
					savePersonalInfo.setEndtCategDesc(req.getEndtCategDesc());
					savePersonalInfo.setEndtCount(req.getEndtCount());
					savePersonalInfo.setEndtPrevPolicyNo(req.getEndtPrevPolicyNo());
					savePersonalInfo.setEndtPrevQuoteNo(req.getEndtPrevQuoteNo());
					savePersonalInfo.setEndtStatus(req.getEndtStatus());
					savePersonalInfo.setEntryDate(new Date());
					savePersonalInfo.setFax(req.getFax());
					savePersonalInfo.setGender(StringUtils.isBlank(req.getGender()) ? "M" : req.getGender());
					savePersonalInfo.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());
					savePersonalInfo.setGenderDesc(gender);
					savePersonalInfo.setGenderDesc(gender);
					savePersonalInfo.setTitle(req.getTitle());
					savePersonalInfo.setTitleDesc(title);
					savePersonalInfo.setLanguageDesc(language);
					savePersonalInfo.setOccupationDesc(occupationDesc);
					savePersonalInfo.setIdType(req.getIdType()); 
					savePersonalInfo.setPolicyHolderTypeIdDesc(policyHolderTypeId);
					
					// Induvidual / Corporate
					savePersonalInfo.setPolicyHolderType(req.getPolicyHolderType());
					savePersonalInfo.setPolicyHolderTypeDesc(policyHolderType);
					
					// Possport or etc
					savePersonalInfo.setPolicyHolderTypeid(req.getPolicyHolderTypeid());
					savePersonalInfo.setPolicyHolderTypeIdDesc(policyHolderTypeId);
					savePersonalInfo.setIdType(req.getPolicyHolderTypeid());
					savePersonalInfo.setIdTypeDesc(policyHolderTypeId);
					
					savePersonalInfo.setMobileCode1(req.getMobileCode1());
					savePersonalInfo.setMobileCode2(req.getMobileCode2()==null?"":req.getMobileCode2());
					savePersonalInfo.setMobileCode3(req.getMobileCode3()==null?"":req.getMobileCode3());
					savePersonalInfo.setMobileNo1(req.getMobileNo1());
					savePersonalInfo.setMobileNo2(req.getMobileNo2());
					savePersonalInfo.setMobileNo3(req.getMobileNo3());
					savePersonalInfo.setWhatsappCode(req.getWhatsappCode());
					if (StringUtils.isNotBlank(req.getMobileCode1())) {
						ListItemValue mobiledesc1 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode1(),req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc1(mobiledesc1.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getMobileCode2())) {
						ListItemValue mobiledesc2 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode2(),req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc2(mobiledesc2.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getMobileCode3())) {
						ListItemValue mobiledesc3 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode3(),req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc3(mobiledesc3.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getWhatsappCode())) {
						ListItemValue whatsappCode = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getWhatsappCode(),req.getCompanyId());
						savePersonalInfo.setWhatsappcodeDesc(whatsappCode.getItemValue());

					}
					savePersonalInfo.setRegionCode(req.getRegionCode());
					savePersonalInfo.setStateCode(req.getStateCode());
					savePersonalInfo.setStateName(req.getStateName());
					savePersonalInfo.setStatus(req.getStatus());
					savePersonalInfo.setNationality(req.getNationality());
					savePersonalInfo.setVrTinNo(req.getVrTinNo());
					savePersonalInfo.setVrnGst(req.getVrTinNo());
					personalInforepo.save(savePersonalInfo);
				}
			}
			// Response

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;

	}
	
	 

	public synchronized String generateCustRefNo() {
	       try {
	    	   SeqCustrefno entity;
	            entity = custRefRepo.save(new SeqCustrefno());          
	            return String.format("%05d",entity.getCustReferenceNo()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 }
	
	public synchronized String getListItem(String insuranceId , String branchCode, String itemType, String itemCode) {
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
			Predicate b1= cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			effectiveDate.where(a1,a2,b1,b2);
			
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate b3= cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate b4= cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			effectiveDate2.where(a3,a4,b3,b4);
						
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			//Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			//Predicate n8 = cb.or(n4,n5);
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType );
			Predicate n11 = cb.equal(c.get("itemCode"), itemCode);
			query.where(n1,n2,n3,n4,n9,n10,n11).orderBy(orderList);
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

	public synchronized String getListItem1(String insuranceId , String branchCode, String itemType) {
		String countryCode = "" ;
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
			Predicate b1= cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			effectiveDate.where(a1,a2,b1,b2);
			
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate b3= cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate b4= cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			effectiveDate2.where(a3,a4,b3,b4);
						
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			//Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			//Predicate n8 = cb.or(n4,n5);
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType );
			query.where(n1,n2,n3,n4,n9,n10).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			
			countryCode = list.size() > 0 ? list.get(0).getItemValue() : "" ; 
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return countryCode ;
	}

	public String getByOccupationId(String occupationId, String insuranceId, String productId , String branchCode) {
		String occupationDesc = "";
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<OccupationMaster> query=  cb.createQuery(OccupationMaster.class);
			List<OccupationMaster> list = new ArrayList<OccupationMaster>();
			
			// Find All
			Root<OccupationMaster> c = query.from(OccupationMaster.class);
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));
			
			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<OccupationMaster> ocpm1 = effectiveDate.from(OccupationMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("occupationId"),ocpm1.get("occupationId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a5 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a6 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate a9 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			effectiveDate.where(a1,a2,a5,a6,a9);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<OccupationMaster> ocpm2 = effectiveDate2.from(OccupationMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("occupationId"),ocpm2.get("occupationId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a7 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a8 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			Predicate a10 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			effectiveDate2.where(a3,a4,a7,a8,a10);
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"),insuranceId);
			Predicate n5 = cb.equal(c.get("branchCode"),branchCode);
			Predicate n6 = cb.equal(c.get("branchCode"),"99999");
			Predicate n7 = cb.or(n5,n6);
			Predicate n8 = cb.equal(c.get("occupationId"),occupationId);
			Predicate n9 = cb.equal(c.get("productId"),productId );
			Predicate n10 = cb.equal(c.get("productId"),"99999" );
			Predicate n11 =  cb.or(n9, n10);
			query.where(n1,n2,n3,n4,n7,n8,n11).orderBy(orderList);
			TypedQuery<OccupationMaster> result = em.createQuery(query);
			list = result.getResultList();

			if(list.size()>0) {
				list = result.getResultList();
				list.sort(Comparator.comparing(OccupationMaster::getOccupationName));
				occupationDesc = list.size() > 0 ? list.get(0).getOccupationName() : "";
			}
		} catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
		}
			return occupationDesc;
		}

	public String getByCountry(String insuranceId) {
		String country = "";
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<InsuranceCompanyMaster> query=  cb.createQuery(InsuranceCompanyMaster.class);
			List<InsuranceCompanyMaster> list = new ArrayList<InsuranceCompanyMaster>();
			
			// Find All
			Root<InsuranceCompanyMaster> c = query.from(InsuranceCompanyMaster.class);
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("amendId")));
			
			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<InsuranceCompanyMaster> ocpm1 = effectiveDate.from(InsuranceCompanyMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a5 = cb.equal(c.get("amendId"),ocpm1.get("amendId"));
			//Predicate a6 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			//Predicate a9 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			effectiveDate.where(a1,a2,a5);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<InsuranceCompanyMaster> ocpm2 = effectiveDate2.from(InsuranceCompanyMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a7 = cb.equal(c.get("amendId"),ocpm2.get("amendId"));
			//Predicate a8 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			//Predicate a10 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			effectiveDate2.where(a3,a4,a7);
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"),insuranceId);
			//Predicate n5 = cb.equal(c.get("branchCode"),branchCode);
			//Predicate n6 = cb.equal(c.get("branchCode"),"99999");
			//Predicate n7 = cb.or(n5,n6);
			query.where(n1,n2,n3,n4).orderBy(orderList);
			TypedQuery<InsuranceCompanyMaster> result = em.createQuery(query);
			list = result.getResultList();

			if(list.size()>0) {
				list = result.getResultList();
				country = list.size() > 0 ? list.get(0).getCountryId() : "";
			}
		} catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
		}
			return country;
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
			List<EserviceCustomerDetails> data = repository.findByCustomerReferenceNoOrderByEntryDateDesc(req.getCustomerReferenceNo());
	
			res = dozerMapper.map(data.get(0), CustomerDetailsGetRes.class);
			res.setMobileCodeDesc1(data.get(0).getMobileCodeDesc1()==null?"":data.get(0).getMobileCodeDesc1());
			res.setMobileCodeDesc2(data.get(0).getMobileCodeDesc2()==null?"":data.get(0).getMobileCodeDesc2());
			res.setMobileCodeDesc3(data.get(0).getMobileCodeDesc3()==null?"":data.get(0).getMobileCodeDesc3());
			res.setMobileCode1(data.get(0).getMobileCode1()==null?"":data.get(0).getMobileCode1());
			res.setMobileCode2(data.get(0).getMobileCode2()==null?"":data.get(0).getMobileCode2());
			res.setMobileCode3(data.get(0).getMobileCode3()==null?"":data.get(0).getMobileCode3());
	
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
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
			Pageable paging = PageRequest.of(limit, offset, Sort.by("updatedDate").descending());

			LoginMaster loginData = loginRepo.findByLoginId(req.getCreatedBy());
			List<EserviceCustomerDetails> custList = new ArrayList<EserviceCustomerDetails>(); 
			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EserviceCustomerDetails> query = cb.createQuery(EserviceCustomerDetails.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
			Root<PersonalInfo> p = query.from(PersonalInfo.class);
			
			// Select
			query.select(c  ).distinct(true);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(c.get("updatedDate")));

			// Where
			Predicate n1 = cb.equal(p.get("customerId"), h.get("customerId"));
			Predicate n2 = cb.equal(h.get("companyId"), req.getComapanyId());
			Predicate n3 = cb.equal(c.get("customerReferenceNo"), p.get("customerReferenceNo"));
		//	Predicate n3 = cb.equal(h.get("productId"), req.getProductId());
			Predicate n7 = cb.equal(c.get("companyId"), req.getComapanyId());
			Predicate n4 = null ;
			Predicate n5 = null ;
			
			if (loginData.getUserType().equalsIgnoreCase("Broker") || loginData.getUserType().equalsIgnoreCase("User")) {
				
				n4 = cb.equal(  h.get("brokerBranchCode"), req.getBrokerBranchCode());
				n5 = cb.equal(  h.get("loginId"), req.getCreatedBy());
			} else {
				
				n4 = cb.equal(  h.get("branchCode"),  req.getBranchCode());
				n5 = cb.equal(  h.get("applicationId"), req.getCreatedBy());
			}

			query.where(n1, n2,n3, n4, n5,n7).orderBy(orderList);

			// Get Result
			TypedQuery<EserviceCustomerDetails> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			custList = result.getResultList();
			
			
			
			List<EserviceCustomerDetails> totalCustList = new ArrayList<EserviceCustomerDetails>();
			Page<EserviceCustomerDetails> datas = null ;
			if (loginData.getUserType().equalsIgnoreCase("Broker")
					|| loginData.getUserType().equalsIgnoreCase("User")) {
//				datas = repository.findByCompanyIdAndBrokerBranchCodeAndCreatedBy(paging,
//						req.getComapanyId(), req.getBrokerBranchCode(),
//						req.getCreatedBy());
				datas = repository.findByCompanyIdAndBrokerBranchCodeAndCreatedBy(paging,
						req.getComapanyId(), req.getBrokerBranchCode(),req.getCreatedBy());
			} else {
//				datas = repository.findByCompanyIdAndBranchCodeAndCreatedBy(paging, req.getComapanyId(),
//						req.getBranchCode(), req.getCreatedBy());
				datas  = repository.findByCompanyIdAndBranchCodeAndCreatedBy(paging,
						req.getComapanyId(), req.getBranchCode(),req.getCreatedBy());
			}
			
			totalCustList.addAll(datas.getContent());
			totalCustList.addAll(custList);
			
			totalCustList = totalCustList.stream().filter(distinctByKey(o -> Arrays.asList(o.getCustomerReferenceNo()))).collect(Collectors.toList());

			for (EserviceCustomerDetails data : totalCustList) {
				CustomerDetailsGetRes res = new CustomerDetailsGetRes();
				res = dozerMapper.map(data, CustomerDetailsGetRes.class);
				res.setMobileCodeDesc1(data.getMobileCodeDesc1()==null?"":data.getMobileCodeDesc1());
				res.setMobileCodeDesc2(data.getMobileCodeDesc2()==null?"":data.getMobileCodeDesc2());
				res.setMobileCodeDesc3(data.getMobileCodeDesc3()==null?"":data.getMobileCodeDesc3());
				res.setMobileCode1(data.getMobileCode1()==null?"":data.getMobileCode1());
				res.setMobileCode2(data.getMobileCode2()==null?"":data.getMobileCode2());
				res.setMobileCode3(data.getMobileCode3()==null?"":data.getMobileCode3());
				res.setWhatsappCode(data.getWhatsappCode()==null?"":data.getWhatsappCode());
				res.setWhatsappDesc(data.getWhatsappCodeDesc()==null?"":data.getWhatsappCodeDesc());
				res.setWhatsappNo(data.getWhatsappNo()==null?"":data.getWhatsappNo());
				res.setVrTinNo( data.getIdType().equalsIgnoreCase("6") ? data.getIdNumber() : data.getVrTinNo()  );
				
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
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
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
			Pageable paging = PageRequest.of(limit, offset, Sort.by("updatedDate").descending());

			LoginMaster loginData = loginRepo.findByLoginId(req.getCreatedBy());
			List<EserviceCustomerDetails> custList = new ArrayList<EserviceCustomerDetails>(); 
			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EserviceCustomerDetails> query = cb.createQuery(EserviceCustomerDetails.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
			Root<PersonalInfo> p = query.from(PersonalInfo.class);
			
			// Select
			query.select(c  ).distinct(true);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(c.get("updatedDate")));

			// Where
			Predicate n1 = cb.equal(p.get("customerId"), h.get("customerId"));
			Predicate n2 = cb.equal(h.get("companyId"), req.getComapanyId());
			Predicate n3 = cb.equal(c.get("customerReferenceNo"), p.get("customerReferenceNo"));
			Predicate n6 = cb.equal(c.get("status"), "Y");
			Predicate n7 = cb.equal(c.get("companyId"), req.getComapanyId());
		//	Predicate n3 = cb.equal(h.get("productId"), req.getProductId());
			Predicate n4 = null ;
			Predicate n5 = null ;
			
			if (loginData.getUserType().equalsIgnoreCase("Broker") || loginData.getUserType().equalsIgnoreCase("User")) {
				
				n4 = cb.equal(  h.get("brokerBranchCode"), req.getBrokerBranchCode());
				n5 = cb.equal(  h.get("loginId"), req.getCreatedBy());
			} else {
				
				n4 = cb.equal(  h.get("branchCode"),  req.getBranchCode());
				n5 = cb.equal(  h.get("applicationId"), req.getCreatedBy());
			}

			query.where(n1, n2,n3, n4, n5,n6,n7).orderBy(orderList);

			// Get Result
			TypedQuery<EserviceCustomerDetails> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			custList = result.getResultList();
			
			Page<EserviceCustomerDetails> datas = null;
			if (loginData.getUserType().equalsIgnoreCase("Broker")
					|| loginData.getUserType().equalsIgnoreCase("User")) {
				datas = repository.findByCompanyIdAndBrokerBranchCodeAndCreatedByAndStatus(paging,
						req.getComapanyId(), req.getBrokerBranchCode(),
						req.getCreatedBy(), "Y");
			} else {
				datas = repository.findByCompanyIdAndBranchCodeAndCreatedByAndStatus(paging,
						req.getComapanyId(), req.getBranchCode(),
						req.getCreatedBy(), "Y");
			}

			List<EserviceCustomerDetails> totalCustList = new ArrayList<EserviceCustomerDetails>();
			totalCustList.addAll(datas.getContent());
			totalCustList.addAll(custList);
			
			totalCustList = totalCustList.stream().filter(distinctByKey(o -> Arrays.asList(o.getCustomerReferenceNo()))).collect(Collectors.toList());

			for (EserviceCustomerDetails data : totalCustList) {
				CustomerDetailsGetRes res = new CustomerDetailsGetRes();
				res = dozerMapper.map(data, CustomerDetailsGetRes.class);
				res.setMobileCodeDesc1(data.getMobileCodeDesc1()==null?"":data.getMobileCodeDesc1());
				res.setMobileCodeDesc2(data.getMobileCodeDesc2()==null?"":data.getMobileCodeDesc2());
				res.setMobileCodeDesc3(data.getMobileCodeDesc3()==null?"":data.getMobileCodeDesc3());
				res.setMobileCode1(data.getMobileCode1()==null?"":data.getMobileCode1());
				res.setMobileCode2(data.getMobileCode2()==null?"":data.getMobileCode2());
				res.setMobileCode3(data.getMobileCode3()==null?"":data.getMobileCode3());
				res.setWhatsappCode(data.getWhatsappCode()==null?"":data.getWhatsappCode());
				res.setWhatsappDesc(data.getWhatsappCodeDesc()==null?"":data.getWhatsappCodeDesc());
				res.setWhatsappNo(data.getWhatsappNo()==null?"":data.getWhatsappNo());
				res.setVrTinNo( data.getIdType().equalsIgnoreCase("6") ? data.getIdNumber() : data.getVrTinNo()  );				
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
	public List<Error> validateCustomer(EserviceCustomerSaveReq req) {
		List<Error> errorList = new ArrayList<Error>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			Calendar cal = Calendar.getInstance();

			if (req.getSaveOrSubmit().equalsIgnoreCase("Submit")) {
				if (StringUtils.isBlank(req.getClientName())) {
					errorList.add(new Error("01", "ClientName", "Please Enter ClientName "));
				} else if (req.getClientName().length() > 250) {
					errorList.add(new Error("01", "ClientName", "Please Enter ClientName within 250 Characters"));
				} 
//				else if (StringUtils.isNotBlank(req.getClientName())&& !req.getClientName().matches("[a-zA-Z.&() ]+")) {
//					errorList.add(new Error("01", "ClientName", "Please Enter Proper ClientName"));						
//				}
				 
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
//	 

				if (StringUtils.isBlank(req.getIdNumber())) {
					errorList.add(new Error("11", "IdNumber", "Please Enter IdNumber"));
				} else if (req.getIdNumber().length() > 100) {
					errorList.add(new Error("11", "IdNumber", "Please Enter IdNumber within 100 Characters"));
				} else if (! req.getIdNumber().matches("[A-Za-z0-9]+") ) {
					errorList.add(new Error("11", "IdNumber", "Please Enter Valid IdNumber "));
				}
				      

				if (StringUtils.isBlank(req.getMobileNo1())) {
					errorList.add(new Error("24", "MobileNo", "Please Enter MobileNo"));
				} else if (req.getMobileNo1().length() > 20) {
					errorList.add(new Error("24", "MobileNo", "Please Enter MobileNo within 20 Characters"));
				} else if (!req.getMobileNo1().matches("\\d+")) {
					errorList.add(new Error("24", "MobileNo", "Please Enter MobileNo only in numbers"));
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
			/*	if (StringUtils.isBlank(req.getEmail1())) {
					errorList.add(new Error("27", "Email1", "Please Enter Email"));
				} else if (req.getEmail1().length() > 100) {
					errorList.add(new Error("27", "Email1", "Please Enter Email within 100 Characters"));
				} else {
					boolean b = isValidMail(req.getEmail1());
					if (b == false) {
						errorList.add(new Error("37", "Email", "Please Enter Email in correct format"));
					}
				}*/
 
				 
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
				
				if( StringUtils.isNotBlank(req.getPolicyHolderType()) && req.getPolicyHolderType().equalsIgnoreCase("2") ) {
					if (StringUtils.isBlank(req.getVrTinNo())) {
						errorList.add(new Error("42", "VrTinNo", "Please Enter VrTinNo"));
					} else if (req.getVrTinNo().length() > 20) {
						errorList.add(new Error("42", "VrTinNo", "Please Enter VrTinNo within 20 Characters"));
					}
					
				}
				
 
				 
  
				if (StringUtils.isBlank(req.getMobileCode1())) {
					errorList.add(new Error("46", "MobileCode", "Please Select MobileCode "));
				}
				
				if (StringUtils.isBlank(req.getWhatsappCode())) {
					errorList.add(new Error("47", "WhatsappCode", "Please Select WhatsappCode "));
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
			//	Calendar cal = new GregorianCalendar();
				Date today = new Date();
				cal.setTime(today);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 50);
				today = cal.getTime();
				if (req.getPolicyHolderType().equalsIgnoreCase("1")) {

					if (req.getDobOrRegDate() != null) {
						try {
							
						
						if (req.getDobOrRegDate().after(today)) {
							errorList.add(new Error("38", "DobOrRegDate", "Please Enter DobOrRegDate as Past Date"));

						} 
						LocalDate localDate1 = req.getDobOrRegDate().toInstant().atZone(ZoneId.systemDefault())
								.toLocalDate();
						LocalDate localDate2 = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

						Integer years = Period.between(localDate1, localDate2).getYears();
						if (years > 100) {
							errorList.add(new Error("38", "DobOrRegDate", "DobOrRegDate Not Accepted More than 100 Years"));

						}
						}catch (Exception e) {
							errorList.add(new Error("38", "DobOrRegDate", "DobOrRegDate Not Valid"));
						}

					}/*else {
						errorList.add(new Error("38", "DobOrRegDate", "DobOrRegDate Not Valid"));
					}*/

					
				}

				if (req.getPolicyHolderType().equalsIgnoreCase("2")) {

					if (req.getDobOrRegDate() != null) {
						try {

							if (req.getDobOrRegDate().after(today)) {
								errorList.add(new Error("38", "DobOrRegDate", "Please Enter DobOrRegDate as Past Date"));

							}
							LocalDate localDate1 = req.getDobOrRegDate().toInstant().atZone(ZoneId.systemDefault())
									.toLocalDate();
							LocalDate localDate2 = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

							Integer years = Period.between(localDate1, localDate2).getYears();
							if (years > 100) {
								errorList.add(new Error("38", "DobOrRegDate", "DobOrRegDate Not Accepted More than 100 Years"));

							}


						}catch (Exception e) {
							errorList.add(new Error("38", "DobOrRegDate", "DobOrRegDate Not Valid"));
						}
					} /*else {
						errorList.add(new Error("38", "DobOrRegDate", "DobOrRegDate Not Valid"));
					}*/

					
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

}
