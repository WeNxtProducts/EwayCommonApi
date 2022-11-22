package com.maan.eway.admin.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.admin.req.AdditionalInfoReq;
import com.maan.eway.admin.req.BrokerPersonalInfoReq;
import com.maan.eway.admin.req.CommonLoginCreationReq;
import com.maan.eway.admin.req.CommonLoginInformationReq;
import com.maan.eway.admin.req.CommonPersonalInforReq;
import com.maan.eway.bean.CityMaster;
import com.maan.eway.bean.CountryMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.StateMaster;
import com.maan.eway.error.Error;
import com.maan.eway.repository.CityMasterRepository;
import com.maan.eway.repository.CountryMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.StateMasterRepository;

@Service
public class BasicLoginValidationService {
	
	private Logger log=LogManager.getLogger(BasicLoginValidationService.class);

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private LoginMasterRepository loginRepo ;
	
	@Autowired
	private CityMasterRepository cityRepo;
	
	@Autowired
	private StateMasterRepository stateRepo;
	
	@Autowired
	private CountryMasterRepository countryRepo;
	
	
	public List<Error>  commonLoginCreationValidation(CommonLoginCreationReq req ) {
		List<Error> errors = new ArrayList<Error>();
		try {
			// Login Validation
			CommonLoginInformationReq loginReq = req.getLoginInformation() ;
			
			if (StringUtils.isBlank(loginReq .getCreatedBy())) {
				errors.add(new Error("01", "Created By", "Please Enter Created By"));
			}
			
			if (StringUtils.isBlank(loginReq.getLoginId())) {
				errors.add(new Error("02", "Login Id", "Please Enter Login Id"));
			} else if (loginReq.getLoginId().length() > 50 || loginReq.getLoginId().length() < 5  ) {
				errors.add(new Error("02", "Login Id", "Logi"
						+ "n Id Under 5 - 50 Characters Only Allowed"));
			} 
			if( StringUtils.isBlank(loginReq.getAgencyCode())) {
				if (StringUtils.isBlank(loginReq.getPassword())) {
					errors.add(new Error("04", "Password", "Please Enter Password"));
				} else if (loginReq.getPassword().length() > 50) {
					errors.add(new Error("03", "PassWord", "Password Must Be Under 50 Characters Only Allowed"));
				}
			}
			
			
			if (StringUtils.isBlank(loginReq.getStatus())) {
				errors.add(new Error("05", "Status", "Please Select Status"));
			} 
			
			if (StringUtils.isBlank(loginReq.getCompanyId())) {
				errors.add(new Error("05", "InsuranceId", "Please Select InsuranceId"));
			}
			
			if( StringUtils.isBlank(loginReq.getUserType()) ) {
				errors.add(new Error("05", "UserType", "Please Select UserType"));
			} else if (loginReq.getUserType().length() > 20 ) {
				errors.add(new Error("05", "UserType", "UserType Under 20 Characters Only Allowed"));
				
			} else if (StringUtils.isBlank(loginReq.getSubUserType())) {
				errors.add(new Error("05", "Sub UserType", "Please Select Sub UserType"));
			} else if (loginReq.getSubUserType().length() > 20 ) {
				errors.add(new Error("05", "Sub UserType", "Sub UserType Under 20 Characters Only Allowed"));
			} else if (loginReq.getSubUserType().equalsIgnoreCase("bank") && StringUtils.isBlank(loginReq.getBankCode()) ) {
				errors.add(new Error("05", "Bank Code", "Please Select Bank Code"));
				
			}else if ( loginReq.getUserType().equalsIgnoreCase("User") ) {
				LoginMaster  loginData = loginRepo.findByLoginId(loginReq.getLoginId());
				if(loginData!=null ) {
					if (StringUtils.isBlank( loginReq.getAgencyCode()) ) {
						errors.add(new Error("02", "Login Id", "Login Id Already Exist"));
					} else if(! loginReq.getAgencyCode().equalsIgnoreCase(loginData.getAgencyCode() ) ) {
						errors.add(new Error("02", "Login Id", "Login Id Already Exist"));
					}
				} 
			} else {
				LoginMaster  loginData = loginRepo.findByLoginId(loginReq.getLoginId());
				if(loginData!=null ) {
					if (StringUtils.isBlank( loginReq.getOaCode()) ) {
						errors.add(new Error("02", "Login Id", "Login Id Already Exist"));
					} else if(! loginReq.getOaCode().equalsIgnoreCase(loginData.getOaCode().toString() ) ) {
						errors.add(new Error("02", "Login Id", "Login Id Already Exist"));
					}
				} 
			}
			
			// Effective Date Validation
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();
			if (loginReq.getEffectiveDateStart().toString() == null ) {
				errors.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start "));

			} else if (loginReq.getEffectiveDateStart().before(today)) {
				errors.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date  "));
			}
			
			if(StringUtils.isNotBlank(loginReq.getSubUserType() ) && ! loginReq.getSubUserType().equalsIgnoreCase("bank") &&   StringUtils.isNotBlank(loginReq.getBankCode())   ) {
				errors.add(new Error("06","BankCode","You Can't Enter BankCode"));
			}
		/*	if( loginReq.getAttachedBranches()==null || loginReq.getAttachedBranches().size() == 0 ) {
				errors.add(new Error("06", "Attached Branch", "Please Choose Atleast One Branch"));
			} 
			if( loginReq.getAttachedRegions()==null || loginReq.getAttachedRegions().size() == 0 ) {
				errors.add(new Error("06", "Attached Region", "Please Choose Atleast One Region"));
			}
			if( loginReq.getAttachedCompanies()==null || loginReq.getAttachedCompanies().size() == 0 ) {
				errors.add(new Error("06", "Attached Branch", "Please Choose Atleast One Branch"));
			} */
			// Personal Info Validation
			CommonPersonalInforReq personalReq = req.getPersonalInformation() ; 
			
		
			
		/*	CityMaster countCity=cityRepo.findByCityIdAndStatus(personalReq.getCityCode(),"Y")
			if( StringUtils.isBlank(personalReq.getCityCode()) ) {
				errors.add(new Error("0", "CityCode", "Please Enter City Code"));
			}else if(countCity < 0) {
				errors.add(new Error("0", "CityCode", "Please Enter Valid City Code"));
			} */
			
			
			if( StringUtils.isBlank(personalReq.getUserMail()) ) {
				errors.add(new Error("06", "User Mail", "Please Select User Mail"));
			} else if (personalReq.getUserMail().length() > 50  ) {
				errors.add(new Error("06", "User Mail", "Mail Under Must Be 50 Characters Only Allowed"));
			}  else if( isNotValidMail(personalReq.getUserMail()) ){
				errors.add(new Error("08", "User Mail", "Please Enter Valid User Mail"));
			}
			
			
			if( StringUtils.isBlank(personalReq.getUserMobile()) ) {
				errors.add(new Error("07", "User Mobile", "Please Enter User Mobile"));
			} else if (personalReq.getUserMobile().length() > 20 ) {
				errors.add(new Error("07", "User Mobile", "User Mobile Must Be Under 20 Number Only Allowed"));
			} else if (! personalReq.getUserMobile().matches("[0-9]+") ) {
				errors.add(new Error("07", "User Mobile", "User Mobile Must Be Under 20 Number Only Allowed"));
			}
			
			if( StringUtils.isBlank(personalReq.getUserName()) ) {
				errors.add(new Error("08", "User Name", "Please Enter User Name"));
			} else if (personalReq.getUserName().length() > 100 ) {
				errors.add(new Error("08", "User Name ", "User Name Must Be Under 100 Characters Only Allowed"));
			} else if (isNotValidName(personalReq.getUserName()) ) {
				errors.add(new Error("08", "User Name ", "Please Enter Valid User Name"));
			} 
			 
		
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			errors.add(new Error("09", "Common Error", e.getMessage() ));
		}
		return errors;
	}
	
	public Long getCountryCount(String countryId) {
		Long countryCount = 0L ;
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<CountryMaster> c = query.from(CountryMaster.class);
			
			// Country Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<CountryMaster> ocpm1 = effectiveDate.from(CountryMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate c1 = cb.equal(ocpm1.get("countryId"), c.get("countryId"));
			Predicate c2 = cb.equal(ocpm1.get("status"),c.get("status"));
			Predicate c3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(c1,c2,c3);
			
			Predicate n1 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(c.get("countryId"), countryId);
			Predicate n3 = cb.equal(c.get("status"), "Y");
			
			// Select
			query.multiselect( cb.count(c) );
			
			query.where(n1,n2,n3);
			// Get Result
			TypedQuery<Long> result = em.createQuery(query);
			List<Long> list = result.getResultList();
			if( list.size()>0) {
				countryCount = list.get(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
		}
		return countryCount;
	}
	
	public Long getStateCount(String countryId, String stateId ) {
		Long stateCount = 0L ;
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<StateMaster> s = query.from(StateMaster.class);
			
			// State Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<StateMaster> ocpm1 = effectiveDate.from(StateMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate c1 = cb.equal(ocpm1.get("countryId"), s.get("countryId"));
			Predicate c2 = cb.equal(ocpm1.get("status"),s.get("status"));
			Predicate c3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate c4 = cb.equal(ocpm1.get("stateId"), s.get("stateId"));
			effectiveDate.where(c1,c2,c3,c4);
			
			Predicate n1 = cb.equal(s.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(s.get("countryId"), countryId);
			Predicate n3 = cb.equal(s.get("stateId"), stateId);
			Predicate n4 = cb.equal(s.get("status"), "Y");
			
			// Select
			query.multiselect( cb.count(s) );
			
			query.where(n1,n2,n3,n4);
			// Get Result
			TypedQuery<Long> result = em.createQuery(query);
			List<Long> list = result.getResultList();
			if( list.size()>0) {
				stateCount = list.get(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
		}
		return stateCount;
	}
	
	public Long getCityCount(String countryId,String cityId) {
		Long cityCount = 0L ;
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<CityMaster> c = query.from(CityMaster.class);
			
			// City Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<CityMaster> ocpm1 = effectiveDate.from(CityMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate c1 = cb.equal(ocpm1.get("countryId"), c.get("countryId"));
			Predicate c2 = cb.equal(ocpm1.get("status"),c.get("status"));
			Predicate c3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate c4 = cb.equal(ocpm1.get("stateId"), c.get("stateId"));
			Predicate c5 = cb.equal(ocpm1.get("cityId"), c.get("cityId"));
			effectiveDate.where(c1,c2,c3,c4,c5);
			
			Predicate n1 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(c.get("countryId"), countryId);
			Predicate n4 = cb.equal(c.get("cityId"), cityId );
			Predicate n5 = cb.equal(c.get("status"), "Y");
			
			// Select
			query.multiselect( cb.count(c) );
			
			query.where(n1,n2,n4,n5);
			// Get Result
			TypedQuery<Long> result = em.createQuery(query);
			List<Long> list = result.getResultList();
			if( list.size()>0) {
				cityCount = list.get(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
		}
		return cityCount;
	}
	
	
	public List<Error>  commonBrokerPersonalValidation(AdditionalInfoReq brokerReq ) {
		List<Error> errors = new ArrayList<Error>();
		try {
			// Login Validation
			if(StringUtils.isBlank(brokerReq.getAcExecutiveId())  ) {
				errors.add(new Error("09", "Ac Excutive Id", "Plese Enter AcExcutiveId" ));
			} else if(! brokerReq.getAcExecutiveId().matches("[0-9]+")  ) {
				errors.add(new Error("09", "Ac Excutive Id", "Plese Enter Valid Number AcExcutiveId" ));
			}
			
			if(StringUtils.isBlank(brokerReq.getAddress1())  ) {
				errors.add(new Error("10", "Address1", "Plese Enter Address1" ));
			} else if(brokerReq.getAddress1().length()>100 ) {
				errors.add(new Error("10", "Address1", "Address1 Must Be Under 100 Character Only Allowed" ));
			}
			
			if(StringUtils.isBlank(brokerReq.getAddress2())  ) {
				errors.add(new Error("11", "Address2", "Plese Enter Address2" ));
			} else if(brokerReq.getAddress2().length()>100 ) {
				errors.add(new Error("11", "Address2", "Address2 Must Be Under 100 Character Only Allowed" ));
			}
			
			if(StringUtils.isBlank(brokerReq.getAddress3())  ) {
				errors.add(new Error("12", "Address3", "Plese Enter Address3" ));
			} else if(brokerReq.getAddress3().length()>100 ) {
				errors.add(new Error("12", "Address3", "Address3 Must Be Under 100 Character Only Allowed" ));
			}
			
			if(StringUtils.isBlank(brokerReq.getApprovedPreparedBy())  ) {
				errors.add(new Error("13", "ApprovedPreparedBy", "Plese Enter Approved Prepared By" ));
			} else if(brokerReq.getApprovedPreparedBy().length()>30 ) {
				errors.add(new Error("13", "ApprovedPreparedBy", "ApprovedPreparedBy Must Be Under 30 Character Only Allowed" ));
			}
			
			if(StringUtils.isBlank(brokerReq.getDesignation())  ) {
				errors.add(new Error("17", "Designation", "Plese Enter Designation" ));
			}else if(brokerReq.getDesignation().length()>100 ) {
				errors.add(new Error("17", "Designation", "Designation Must Be Under 100 Character Only Allowed" ));
			} 
			
			
			if(StringUtils.isBlank(brokerReq.getFax())  ) {
				errors.add(new Error("22", "Fax", "Plese Select Fax" ));
			} else if(brokerReq.getFax().length() > 50  ) {
				errors.add(new Error("22", "Fax", "Fax Must Be Under 50 Character Only Allowed" ));
			}
			
			// Yn Validation
			if (StringUtils.isBlank(brokerReq.getMakerYn())) {
				errors.add(new Error("23", "Maker", "Please Select Maker Y or N"));
			} else if (!("Y".equals(brokerReq.getMakerYn()) || "N".equals(brokerReq.getMakerYn()))) {
				errors.add(new Error("23", "Maker", "Please Select Maker Y or N"));
			}
			
			if (StringUtils.isBlank(brokerReq.getCheckerYn())) {
				errors.add(new Error("24", "CheckerYn", "Please Select Checker Y or N"));
			} else if (!("Y".equals(brokerReq.getCheckerYn()) || "N".equals(brokerReq.getCheckerYn()))) {
				errors.add(new Error("24", "CheckerYn", "Please Select Checker Y or N"));
			}
			
			if (StringUtils.isBlank(brokerReq.getCommissionVatYn())) {
				errors.add(new Error("25", "CommissionVat", "Please Select CommissionVat Y or N"));
			} else if (!("Y".equals(brokerReq.getMakerYn()) || "N".equals(brokerReq.getMakerYn()))) {
				errors.add(new Error("25", "CommissionVat", "Please Select CommissionVat Y or N"));
			}
			
			if (StringUtils.isBlank(brokerReq.getCustConfirmYn())) {
				errors.add(new Error("26", "Customer Confirm ", "Please Select Customer Confirm Y or N"));
			} else if (!("Y".equals(brokerReq.getCustConfirmYn()) || "N".equals(brokerReq.getCustConfirmYn()))) {
				errors.add(new Error("26", "Customer Confirm ", "Please Select Customer Confirm  Y or N"));
			}
			
			
			if(StringUtils.isBlank(brokerReq.getPobox())  ) {
				errors.add(new Error("25", "Post Box No", "Plese Enter Post Box No" ));
			} else if(! brokerReq.getPobox().matches("[0-9]+")  ) {
				errors.add(new Error("25", "Post Box No", "Plese Enter Valid Number In Post Box No" ));
			}
			
			if(StringUtils.isBlank(brokerReq.getRemarks())  ) {
				errors.add(new Error("26", "Remarks", "Plese Enter Remarks" ));
			} else if(brokerReq.getRemarks().length()>100  ) {
				errors.add(new Error("26", "Remarks", "Remarks Must Be Under 100 Characters Only Allowed" ));
			}
			
			if(StringUtils.isBlank(brokerReq.getCoreAppBrokerCode())  ) {
				errors.add(new Error("27", "CoreAppBrokerCode", "Plese Enter CoreAppBrokerCode" ));
			} else if(brokerReq.getCoreAppBrokerCode().length()>100  ) {
				errors.add(new Error("27", "CoreAppBrokerCode", "RsaBrokerCode Must Be Under 100 Characters Only Allowed" ));
			}
			Date today = new Date();
			if(StringUtils.isBlank(brokerReq.getCountryCode())  ) {
				errors.add(new Error("18", "Country", "Plese Select Country" ));
			}
			/*else if(! brokerReq.getCountryCode().matches("[0-9]+")  ) {
				errors.add(new Error("18", "Country", "Plese Enter Valid Number In Country" ));
			} 
			*/else {
				Long countryCount  = getCountryCount(brokerReq.getCountryCode());// countryRepo.countByCountryIdAndStatusAndEffectiveDateStartLessThanEqual(Integer.valueOf(brokerReq.getCountryCode()),"Y", today );
				if(countryCount <=0 ) {
					errors.add(new Error("18", "Country", "Please Select Valid Country" ));
				}
			}
			
		/*	if(StringUtils.isBlank(brokerReq.getStateCode())  ) {
				errors.add(new Error("28", "State", "Plese Select State" ));
			} else if(! brokerReq.getStateCode().matches("[0-9]+")  ) {
				errors.add(new Error("18", "Country", "Plese Enter Valid Number In Country" ));
			}else if(StringUtils.isNotBlank(brokerReq.getCountryCode()) &&   brokerReq.getCountryCode().matches("[0-9]+")  ){
	
				Long stateCount  = getStateCount( brokerReq.getCountryCode(),brokerReq.getStateCode() );//stateRepo.countByStateIdAndCountryIdAndStatusAndEffectiveDateStartLessThanEqual(Integer.valueOf(brokerReq.getStateCode()) , Integer.valueOf(brokerReq.getCountryCode()),"Y", today );
				if(stateCount <=0 ) {
					errors.add(new Error("18", "State", "Please Select Valid State" ));
				}
			} */
			
			if(StringUtils.isBlank(brokerReq.getCityCode())  ) {
				errors.add(new Error("15", "City", "Plese Select City" ));
			} else if(! brokerReq.getCityCode().matches("[0-9]+")  ) {

			}else if(StringUtils.isNotBlank(brokerReq.getCountryCode()) &&   brokerReq.getCountryCode().matches("[0-9]+")  ){
	
				Long cityCount  = getCityCount(brokerReq.getCountryCode() , brokerReq.getCityCode());//cityRepo.countByCityIdAndStateIdAndCountryIdAndStatusAndEffectiveDateStartLessThanEqual(Integer.valueOf(brokerReq.getCityCode()) , Integer.valueOf(brokerReq.getStateCode()) , Integer.valueOf(brokerReq.getCountryCode()),"Y", today );
				if(cityCount  <=0 ) {
					errors.add(new Error("18", "City", "Please Select Valid City" ));
				}
			} 
			
			if(StringUtils.isBlank(brokerReq.getVatRegNo())  ) {
				errors.add(new Error("29", "VatRegNo", "Plese Select VatRegNo" ));
			} else if(brokerReq.getVatRegNo().length()>100  ) {
				errors.add(new Error("29", "VatRegNo", "VatRegNo Must Be Under 100 Characters Only Allowed" ));
			}
			
			
						
			if(StringUtils.isBlank(brokerReq.getContactPersonName())  ) {
				errors.add(new Error("29", "ContactPersonName", "Plese Enter ContactPersonName" ));
			} else if(brokerReq.getContactPersonName().length()>100  ) {
				errors.add(new Error("29", "ContactPersonName", "ContactPersonName Must Be Under 100 Characters Only Allowed" ));
			}
			
			if(StringUtils.isBlank(brokerReq.getMobileCode())  ) {
				errors.add(new Error("29", "MobileCode", "Plese Select MobileCode" ));
			} 
			
			
			if(StringUtils.isBlank(brokerReq.getWhatsappCode())  ) {
				errors.add(new Error("29", "WhatsappCode", "Plese Select WhatsappCode" ));
			} 
			
			if(StringUtils.isBlank(brokerReq.getWhatsappNo())  ) {
				errors.add(new Error("29", "WhatsappNo", "Plese Enter WhatsappNo" ));
			} else if(! brokerReq.getWhatsappNo().matches("[0-9]+")  ) {
				errors.add(new Error("29", "WhatsappNo", "Plese Enter Valid Number in WhatsappNo" ));
			} else if(brokerReq.getWhatsappNo().length()>20  ) {
				errors.add(new Error("29", "WhatsappNo", "WhatsappNo Must Be Under 20 Characters Only Allowed" ));
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			errors.add(new Error("09", "Common Error", e.getMessage() ));
		}
		return errors;
	}
	
	
	public boolean isNotValidName(String name) {
		String s = name;
		String regx = "^[\\p{L} .'-]+$";
		Pattern p = Pattern.compile(regx);
		Matcher m = p.matcher(s);
		try {
			if (m.matches()) {
				return false;
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return true;
		}
		return true;
	}
	
	public boolean isNotValidMail(String mail) {
		String regex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
				+ "A-Z]{2,7}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(mail);
		try {
			if (m.matches()) {
				return false;
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return true;
		}
		return true;
	}
	
	
	
}
