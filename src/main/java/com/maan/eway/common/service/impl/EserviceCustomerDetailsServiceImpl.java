package com.maan.eway.common.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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

import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.MsCustomerDetails;
import com.maan.eway.bean.OccupationMaster;
import com.maan.eway.common.req.EserviceCustomerSaveReq;
import com.maan.eway.common.req.EserviceCustomerSearchVrtinReq;
import com.maan.eway.common.req.GetAllCustomerDetailsReq;
import com.maan.eway.common.req.GetCustomerDetailsReq;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.common.res.MsPersonalInfoGetRes;
import com.maan.eway.common.service.EserviceCustomerDetailsService;
import com.maan.eway.error.Error;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.ListItemValueRepository;
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

	
	@PersistenceContext
	private EntityManager em;

	@Override
	public List<Error> validateCustomerDetails(EserviceCustomerSaveReq req) {
		List<Error> errorList = new ArrayList<Error>();
		try {

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
				
				if(req.getPolicyHolderType().equalsIgnoreCase("2")){
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
		/*	if (StringUtils.isBlank(req.getPlaceOfBirth())) {
				errorList.add(new Error("13", "PlaceOfBirth", "Please Enter PlaceOfBirth "));
			} else if (req.getPlaceOfBirth().length() > 100) {
				errorList.add(new Error("13", "PlaceOfBirth", "Please Enter PlaceOfBirth within 100 Characters"));
			}
			if (StringUtils.isBlank(req.getGender())) {
				errorList.add(new Error("14", "Gender", "Please Select Gender"));
			}

			if (StringUtils.isBlank(req.getOccupation())) {
				errorList.add(new Error("15", "Occupation", "Please Select Occupation"));
			} */

			
//
//			if (req.getVrnGst().length() > 20) {
//				errorList.add(new Error("17", "VrnGst", "Please Enter VrnGst within 20 Characters"));
//			}
			if (StringUtils.isBlank(req.getRegionCode())) {
				errorList.add(new Error("18", "RegionCode", "Please Enter RegionCode"));
			} else if (req.getRegionCode().length() > 20) {
				errorList.add(new Error("18", "RegionCode", "Please Enter RegionCode within 20 Characters"));
			}
			if (req.getStreet().length() > 100) {
				errorList.add(new Error("19", "Street", "Please Enter Street within 100 Characters"));
			}
			if (req.getFax().length() > 20) {
				errorList.add(new Error("20", "Fax", "Please Enter Fax within 20 Characters"));
			}
			if (StringUtils.isBlank(req.getTelephoneNo1())) {
				errorList.add(new Error("21", "TelephoneNo1", "Please Enter TelephoneNo1"));
			} else if (req.getTelephoneNo1().length() > 20) {
				errorList.add(new Error("21", "TelephoneNo1", "Please Enter TelephoneNo1 within 20 Characters"));
			} else if (!req.getTelephoneNo1().matches("\\d+")) {
				errorList.add(new Error("21", "TelephoneNo1", "Please Enter TelephoneNo1 only in numbers"));
			} 

			if (StringUtils.isBlank(req.getMobileNo1())) {
				errorList.add(new Error("24", "MobileNo1", "Please Enter MobileNo1"));
			} else if (req.getMobileNo1().length() > 20) {
				errorList.add(new Error("24", "MobileNo1", "Please Enter MobileNo1 within 20 Characters"));
			}
			if (req.getMobileNo2().length() > 20) {
				errorList.add(new Error("25", "MobileNo2", "Please Enter MobileNo2 within 20 Characters"));
			}
			if (req.getMobileNo3().length() > 20) {
				errorList.add(new Error("26", "MobileNo3", "Please Enter MobileNo3 within 20 Characters"));
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
			} else {
				boolean b = isValidMail(req.getEmail2());

				if (b == false) {
					errorList.add(new Error("28", "Email2", "Please Enter Email2 in correct format"));
				}
			}
			if (StringUtils.isNotBlank(req.getEmail3()) && req.getEmail3().length() > 20) {
				errorList.add(new Error("29", "Email3", "Please Enter Email3 within 20 Characters"));
			} else {
				boolean b = isValidMail(req.getEmail3());
				if (b == false) {
					errorList.add(new Error("29", "Email3", "Please Enter Email3 in correct format"));
				}
			}
			if (StringUtils.isBlank(req.getLanguage())) {
				errorList.add(new Error("30", "Language", "Please Select Language"));
			}
			if (StringUtils.isBlank(req.getIsTaxExempted())) {
				errorList.add(new Error("31", "IsTaxExempted", "Please Select IsTaxExempted"));

			}

			if (req.getIsTaxExempted().equals("Y")) {
				if (StringUtils.isBlank(req.getTaxExemptedId())) {
					errorList.add(new Error("32", "TaxExemptedId", "Please Enter TaxExemptedId"));
				} else if (req.getTaxExemptedId().length() > 20) {
					errorList.add(new Error("33", "TaxExemptedId", "Please Enter TaxExemptedId within 20 Characters"));
				}

			}
			// Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
				errorList.add(new Error("34", "Status", "Please Enter Status"));
			} else if (req.getStatus().length() > 1) {
				errorList.add(new Error("34", "Status", "Enter Status in 1 Character Only"));
			} else if (!("Y".equals(req.getStatus()) || "N".equals(req.getStatus()))) {
				errorList.add(new Error("34", "Status", "Enter Status Y or N Only"));
			}
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add(new Error("35", "CreatedBy", "Please Enter CreatedBy "));
			} else if (req.getCreatedBy().length() > 100) {
				errorList.add(new Error("35", "CreatedBy", "Please Enter CreatedBy within 100 Characters"));
			}

			// Date Validation
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();

			if (req.getDobOrRegDate() == null) {
				errorList.add(new Error("38", "DobOrRegDate", "Please Enter DobOrRegDate "));

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
			
			
			
			List<EserviceCustomerDetails> list = new ArrayList<EserviceCustomerDetails>();
			if ((StringUtils.isNotBlank(req.getAddress1())) && (StringUtils.isNotBlank(req.getAddress2()))
					&& (StringUtils.isNotBlank(req.getBranchCode())) && (StringUtils.isNotBlank(req.getBusinessType()))
					&& (StringUtils.isNotBlank(req.getCityCode())) && (StringUtils.isNotBlank(req.getCityName()))
					&& (StringUtils.isNotBlank(req.getClientName())) && (StringUtils.isNotBlank(req.getClientStatus()))
					&& (StringUtils.isNotBlank(req.getCompanyId())) && (StringUtils.isNotBlank(req.getCreatedBy()))
				//	&& (StringUtils.isNotBlank(req.getCustomerReferenceNo()))
					&& (StringUtils.isNotBlank(req.getEmail1())) && (StringUtils.isNotBlank(req.getEmail2()))
					&& (StringUtils.isNotBlank(req.getEmail3())) && (StringUtils.isNotBlank(req.getFax()))
					&& (StringUtils.isNotBlank(req.getGender())) && (StringUtils.isNotBlank(req.getIdNumber()))
					&& (StringUtils.isNotBlank(req.getIsTaxExempted())) && (StringUtils.isNotBlank(req.getLanguage()))
					&& (StringUtils.isNotBlank(req.getLanguageDesc())) && (StringUtils.isNotBlank(req.getMobileNo1()))
					&& (StringUtils.isNotBlank(req.getMobileNo2())) && (StringUtils.isNotBlank(req.getMobileNo3()))
					&& (StringUtils.isNotBlank(req.getNationality())) && (StringUtils.isNotBlank(req.getOccupation()))
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
					&& (StringUtils.isNotBlank(req.getDobOrRegDate().toString()))) {

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
				//Predicate n11 = (cb.like(cb.lower(b.get("customerReferenceNo")),req.getCustomerReferenceNo().toLowerCase()));
				Predicate n12 = (cb.equal(b.get("dobOrRegDate"),req.getDobOrRegDate()));
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
				Predicate n28 = (cb.like(cb.lower(b.get("policyHolderType")), req.getPolicyHolderType().toLowerCase()));
				Predicate n29 = (cb.like(cb.lower(b.get("policyHolderTypeId")),req.getPolicyHolderTypeid().toLowerCase()));
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

				query.where(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10,
						//n11,
						n12, n13, n14, n15, n16, n17, n18, n19, n20,
						n21, n22, n23, n24, n25, n26, n27, n28, n29, n30,					
						n31, n32, n33, n34, n35, n36, n37, n38, n39, n40);
				// Get Result
				TypedQuery<EserviceCustomerDetails> result = em.createQuery(query);
				list = result.getResultList();
				if (list.size() > 0) {
					errorList.add(new Error("42", "Already have data for customerReferenceNo", list.get(0).getCustomerReferenceNo()));

				}
			}

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
			saveData.setGender(StringUtils.isBlank(req.getGender())? "M" :req.getGender() );
			saveData.setOccupation(StringUtils.isBlank(req.getOccupation())? "2" :req.getOccupation() );
			
			// Age Calculation
			Date dob = req.getDobOrRegDate();
			Date today = new Date();
			int age = today.getYear() - dob.getYear();

			// From List Item Value
			ListItemValue gender = listRepo.findByItemTypeAndItemCode("GENDER", saveData.getGender());
			ListItemValue title = listRepo.findByItemTypeAndItemCode("NAME_TITLE", req.getTitle());
			ListItemValue language = listRepo.findByItemTypeAndItemCode("LANGUAGE", req.getLanguage());
			OccupationMaster occupation =occupationRepo.findByOccupationId(saveData.getOccupation());
			saveData.setGenderDesc(gender.getItemValue());
			saveData.setTitleDesc(title.getItemValue());
			saveData.setLanguageDesc(language.getItemValue());
			saveData.setOccupationDesc(occupation.getOccupationName());
			saveData.setAge(age);
			saveData.setIdType(req.getPolicyHolderTypeid());
			repository.save(saveData);

			// Response

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;

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

			Page<EserviceCustomerDetails> datas = repository.findByCompanyIdAndProductIdAndCreatedBy
					(paging, req.getComapanyId(), Integer.valueOf(req.getProductId()) , req.getCreatedBy());
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
	public CustomerDetailsGetRes getbyvrtinno(EserviceCustomerSearchVrtinReq req) {
		CustomerDetailsGetRes res = new CustomerDetailsGetRes();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {
			EserviceCustomerDetails data = repository.findByVrTinNo(req.getVrTinNo());
			dozermapper.map(data,res);
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());;
			return null;
		}
		return res;
	}

}
