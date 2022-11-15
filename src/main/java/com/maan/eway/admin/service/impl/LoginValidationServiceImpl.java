package com.maan.eway.admin.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.admin.req.AdditionalInfoReq;
import com.maan.eway.admin.req.AttachBrokerBranchReq;
import com.maan.eway.admin.req.AttachCompaniesReq;
import com.maan.eway.admin.req.AttachCompnayProductRequest;
import com.maan.eway.admin.req.AttachIssuerBrannchReq;
import com.maan.eway.admin.req.AttachIssuerReferalReq;
import com.maan.eway.admin.req.AttachReferalReq;
import com.maan.eway.admin.req.AttacheIssuerBranchReq;
import com.maan.eway.admin.req.AttachedBranchesReq;
import com.maan.eway.admin.req.AttachedProductReq;
import com.maan.eway.admin.req.BrokerBranchesReq;
import com.maan.eway.admin.req.BrokerCreationReq;
import com.maan.eway.admin.req.CommonLoginCreationReq;
import com.maan.eway.admin.req.CommonPersonalInforReq;
import com.maan.eway.admin.req.IssuerCraeationReq;
import com.maan.eway.admin.req.IssuerLoginReq;
import com.maan.eway.admin.req.IssuerPersonalInfoReq;
import com.maan.eway.admin.req.UserCreationReq;
import com.maan.eway.admin.service.LoginValidationService;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.error.Error;
import com.maan.eway.repository.LoginMasterRepository;

@Service
public class LoginValidationServiceImpl implements LoginValidationService  {

	@Autowired
	private BasicLoginValidationService basicValidation;
	
	private Logger log=LogManager.getLogger(LoginValidationService.class);
	
	@Autowired
	private LoginMasterRepository loginRepo;
	
//*************************************** Login Creation Apis Validations**********************************************************//
	
	@Override
	public List<Error> validateBrokerCreation(BrokerCreationReq req) {
		List<Error> errors = new ArrayList<Error>();
		 DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
			
			// Common Errors
			CommonLoginCreationReq commonReq = new CommonLoginCreationReq();
			dozerMapper.map(req, commonReq);
			List<Error>  commonErrors = basicValidation.commonLoginCreationValidation(commonReq) ;
			if(commonErrors.size()>0  ) {
				errors.addAll(commonErrors);
			}
			
			// Additional Broker Validataion
			AdditionalInfoReq  brokerReq = new AdditionalInfoReq();
			dozerMapper.map(req.getPersonalInformation() , brokerReq); 
			List<Error>  brokerErrors = basicValidation.commonBrokerPersonalValidation(brokerReq) ;
			if(brokerErrors.size()>0  ) {
				errors.addAll(brokerErrors);
			}
			
			if(StringUtils.isBlank(commonReq.getLoginInformation().getBrokerCompanyYn()))  {
				errors.add(new Error("01","BrokerCompanyYn","Please Select BrokerCompany Y or N"));
			} else if( ! (commonReq.getLoginInformation().getBrokerCompanyYn().equalsIgnoreCase("Y") || commonReq.getLoginInformation().getBrokerCompanyYn().equalsIgnoreCase("N")) )  {
				errors.add(new Error("01","BrokerCompanyYn","Please Select BrokerCompany Y or N"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			errors.add(new Error("07", "Common Error", e.getMessage() ));
			return errors;
		}
		return errors;
	}

	
	@Override
	public List<Error> validateIssuerCreation(IssuerCraeationReq req) {
		List<Error> errors = new ArrayList<Error>();
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setAmbiguityIgnored(true);	
		try {
			
			// Common Errors
			CommonLoginCreationReq commonReq = new CommonLoginCreationReq();
			mapper.map(req, commonReq);
			List<Error>  commonErrors = basicValidation.commonLoginCreationValidation(commonReq) ;
			if(commonErrors.size()>0  ) {
				errors.addAll(commonErrors);
			}
			
			IssuerPersonalInfoReq personalReq = req.getPersonalInformation() ;
			IssuerLoginReq loginReq = req.getLoginInformation();
			
			if( loginReq.getAttachedBranches()==null || loginReq.getAttachedBranches().size() == 0 ) {
				errors.add(new Error("06", "Attached Branch", "Please Choose Atleast One Branch"));
			} 
			
			if( loginReq.getProductIds()==null || loginReq.getProductIds().size() == 0 ) {
				errors.add(new Error("06", "ProductIds", "Please Choose Atleast One Product"));
			} 
			
			// Additional Errors
			if(StringUtils.isBlank(personalReq.getAddress1())  ) {
				errors.add(new Error("10", "Address1", "Plese Enter Address1" ));
			} else if(personalReq.getAddress1().length()>100 ) {
				errors.add(new Error("10", "Address1", "Address1 Must Be Under 100 Character Only Allowed" ));
			}
			
			if(StringUtils.isBlank(personalReq.getAddress2())  ) {
				errors.add(new Error("11", "Address2", "Plese Enter Address2" ));
			} else if(personalReq.getAddress2().length()>100 ) {
				errors.add(new Error("11", "Address2", "Address2 Must Be Under 100 Character Only Allowed" ));
			}
			
			if(StringUtils.isBlank(personalReq.getCountryCode())  ) {
				errors.add(new Error("18", "Country", "Plese Select Country" ));
			}
				
			if(StringUtils.isBlank(personalReq.getCityCode())  ) {
				errors.add(new Error("15", "City", "Plese Select City" ));
			} else if(! personalReq.getCityCode().matches("[0-9]+")  ) {

			}
			
			if(StringUtils.isBlank(personalReq.getMobileCode())  ) {
				errors.add(new Error("29", "MobileCode", "Plese Select MobileCode" ));
			} 
			
			if(StringUtils.isBlank(personalReq.getWhatsappCode())  ) {
				errors.add(new Error("29", "WhatsappCode", "Plese Select WhatsappCode" ));
			} 
			
			if(StringUtils.isBlank(personalReq.getWhatsappNo())  ) {
				errors.add(new Error("29", "WhatsappNo", "Plese Enter WhatsappNo" ));
			} else if(! personalReq.getWhatsappNo().matches("[0-9]+")  ) {
				errors.add(new Error("29", "WhatsappNo", "Plese Enter Valid Number in WhatsappNo" ));
			} else if(personalReq.getWhatsappNo().length()>20  ) {
				errors.add(new Error("29", "WhatsappNo", "WhatsappNo Must Be Under 20 Characters Only Allowed" ));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			errors.add(new Error("07", "Common Error", e.getMessage() ));
			return errors;
		}
		return errors;
	}
	
	
	@Override
	public List<Error> validateUserCreation(UserCreationReq req) {
		List<Error> errors = new ArrayList<Error>();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
			
			// Common Errors
			CommonLoginCreationReq commonReq = new CommonLoginCreationReq();
			dozerMapper.map(req, commonReq);
			List<Error>  commonErrors = basicValidation.commonLoginCreationValidation(commonReq) ;
			if(commonErrors.size()>0  ) {
				errors.addAll(commonErrors);
			}
			
			// Additional Broker Validataion
			AdditionalInfoReq  brokerReq = new AdditionalInfoReq();
			dozerMapper.map(req.getPersonalInformation() , brokerReq); 
			List<Error>  brokerErrors = basicValidation.commonBrokerPersonalValidation(brokerReq) ;
			if(brokerErrors.size()>0  ) {
				errors.addAll(brokerErrors);
			}
			
			if(StringUtils.isBlank(req.getLoginInformation().getOaCode())  ) {
				errors.add(new Error("06","Broker","Please Select Broker OaCode"));
			}
			if(StringUtils.isNotBlank(commonReq.getLoginInformation().getUserType())  && commonReq.getLoginInformation().getUserType().equalsIgnoreCase("Broker")  ) {
				if(StringUtils.isBlank(commonReq.getLoginInformation().getBrokerCompanyYn()))  {
					errors.add(new Error("01","BrokerCompanyYn","Please Select BrokerCompany Y or N"));
				} else if( ! (commonReq.getLoginInformation().getBrokerCompanyYn().equalsIgnoreCase("Y") || commonReq.getLoginInformation().getBrokerCompanyYn().equalsIgnoreCase("N")) )  {
					errors.add(new Error("01","BrokerCompanyYn","Please Select BrokerCompany Y or N"));
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			errors.add(new Error("07", "Common Error", e.getMessage() ));
			return errors;
		}
		return errors;
	}
	
//***************************************Add Branch Apis Validations **********************************************************//	
	
	@Override
	public List<Error> validateBrokerBranchReq(AttachCompaniesReq req) {
		List<Error> errors = new ArrayList<Error>();
		try {
			// Branch Validation
			if(StringUtils.isBlank(req.getLoginId()) ) {
				errors.add(new Error("01", "LoginId", "Plese Enter LoginId" ));
			}
			if (StringUtils.isBlank(req.getBrokerCompanyYn())) {
				errors.add(new Error("02", "BrokerCompanyYn", "Plese Enter BrokerCompanyYn"));
			}  
				
			if(req.getAttachedCompanies()==null || req.getAttachedCompanies().size()== 0 ) {
				errors.add(new Error("03", "Attached Companies", "Plese select Atleast One  Company" ));
			} else {
				Long rowNo = 0L ;
				for(AttachedBranchesReq  data : req.getAttachedCompanies() ) {
					rowNo = rowNo + 1L ;
					if(StringUtils.isBlank(data.getInsuranceId()) ) {
						errors.add(new Error("03", "Insurance Id", "Plese Select Atleast One Company" ));
					}
					
					if(data.getAttachedBranches()==null || data.getAttachedBranches().size()== 0 ) {
						errors.add(new Error("03", "Attached Companies", "Plese select Atleast One  Branch in Company Row No : " +  rowNo  ));
					}
					for (BrokerBranchesReq data2 : data.getAttachedBranches()) {
						if (req.getBrokerCompanyYn().equals("N")) {
							if (StringUtils.isBlank(data2.getBranchCode())) {
								errors.add(new Error("04", "Branch Code", "Plese Enter Branch Code"));
							} else if (req.getBrokerCompanyYn().equals("Y")) {
								if (data2.getBrokerBranchCode() == null || data2.getBrokerBranchCode().size() == 0) {
									errors.add(new Error("04", "Broker Branch Code", "Plese select Atleast One  Broker Branch Code"+rowNo));
								}
							}
						}
					}
				}	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			errors.add(new Error("09", "Common Error", e.getMessage() ));
		}
		return errors;
	}


	@Override
	public List<Error> validateIssuerBranchReq(AttachIssuerBrannchReq req) {
		List<Error> errors = new ArrayList<Error>();
		try {
			// Branch Validation
			if(StringUtils.isBlank(req.getLoginId()) ) {
				errors.add(new Error("01", "LoginId", "Plese Enter LoginId" ));
			}
			
			if(req.getAttachedCompanies()==null || req.getAttachedCompanies().size()== 0 ) {
				errors.add(new Error("02", "Attached Companies", "Plese select Atleast One  Company" ));
			} else {
				Long rowNo = 0L ;
				for(AttacheIssuerBranchReq  data : req.getAttachedCompanies() ) {
					rowNo = rowNo + 1L ;
					if(StringUtils.isBlank(data.getInsuranceId()) ) {
						errors.add(new Error("01", "Insurance Id", "Plese Select Atleast One Company" ));
					}
					
					if(data.getAttachedBranches()==null || data.getAttachedBranches().size()== 0 ) {
						errors.add(new Error("02", "Attached Companies", "Plese select Atleast One  Branch in Company Row No : " +  rowNo  ));
					}
				}	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			errors.add(new Error("09", "Common Error", e.getMessage() ));
		}
		return errors;
	}
	
//***************************************Add Broker Products Apis Validations **********************************************************//	

	@Override
	public List<Error> validateBrokerProductReq(AttachCompnayProductRequest req) {
		List<Error> errors = new ArrayList<Error>();
		try {
			//Product Validation
			if(StringUtils.isBlank(req.getLoginId()) ) {
				errors.add(new Error("01", "LoginId", "Plese Enter LoginId" ));
			}
			
			if(StringUtils.isBlank(req.getInsuranceId()) ) {
				errors.add(new Error("01", "InsuranceId", "Plese Enter InsuranceId" ));
			}
			
			if(req.getProductIds()==null || req.getProductIds().size()== 0 ) {
				errors.add(new Error("02", "Product Ids", "Plese select Atleast One  Product" ));
			} 
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			errors.add(new Error("09", "Common Error", e.getMessage() ));
		}
		return errors;
	}
	
//***************************************Add Issuer Referal Apis Validations **********************************************************//	
	
	@Override
	public List<Error> validateIssuerReferalReq(AttachIssuerReferalReq req) {
		List<Error> errors = new ArrayList<Error>();
		try {
			//Referal Validation
			if(StringUtils.isBlank(req.getLoginId()) ) {
				errors.add(new Error("01", "LoginId", "Plese Enter LoginId" ));
			}
			
			if(StringUtils.isBlank(req.getInsuranceId()) ) {
				errors.add(new Error("02", "InsuranceId", "Plese Enter InsuranceId" ));
			}
			if(StringUtils.isBlank(req.getBranchCode()) ) {
				errors.add(new Error("03", "BrnchCode", "Plese Enter BranchCode" ));
			}
			
			if(req.getAttachedReferals()==null || req.getAttachedReferals().size()== 0 ) {
				errors.add(new Error("02", "Attached Referals", "Plese select Atleast One  Referal" ));
			} else {
				Long referalRow  = 0L;
				boolean status = false ;
				for (AttachReferalReq referal :  req.getAttachedReferals() ) {
					referalRow = referalRow + 1L ;
					if(StringUtils.isBlank(referal.getReferalId())) {
						errors.add(new Error("02", "ReferalId", "Plese Enter ReferalId in  Referal Row No : " +  referalRow  ));
					}
					
					if(StringUtils.isBlank(referal.getReferalName())) {
						errors.add(new Error("02", "Referal Name", "Plese Enter Referal Name in  Referal Row No : " +  referalRow  ));
					}
					
					if(referal.getEffectiveDate()==null ) {
						errors.add(new Error("02", "Effective Date ", "Please select Effective Date  in  Referal Row No : " +  referalRow  ));
					} else {
						Calendar cal = new GregorianCalendar();  
						Date today =  new Date(); 
						cal.setTime(today); cal.add(Calendar.DAY_OF_MONTH, -1); cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 50);
						today = cal.getTime();
						if(referal.getEffectiveDate().before(today) ) {
							errors.add(new Error("02", "Effective Date ", "Plese Enter Future Date as Effective Date  in  Referal Row No : " +  referalRow ));
						}
					}
					if(StringUtils.isBlank(referal.getStatus())) {
						errors.add(new Error("02", "Status", "Plese Select Status in  Referal Row No : " +  referal ));
					} else if (referal.getStatus().equalsIgnoreCase("Y") ) {
						status = true ;
					}
					if(StringUtils.isBlank(referal.getSumInsuredStart())) {
						errors.add(new Error("02", "Start Limit", "Plese Enter Sum Insured Start in  Referal Row No : " +  referalRow ));
					} else if (! referal.getSumInsuredStart().matches("[0-9]+") ) {
						errors.add(new Error("02", "Start Limit", "Plese Enter Valid Number Sum Insured Start  in  Referal Row No : " +  referalRow ));
					}
					if(StringUtils.isBlank(referal.getSumInsuredEnd())) {
						errors.add(new Error("02", "End Limit", "Plese Enter Sum Insured End in  Referal Row No : " +  referalRow ));
					} else if (! referal.getSumInsuredEnd().matches("[0-9]+") ) {
						errors.add(new Error("02", "End Limit", "Plese Enter Valid Number Sum Insured End in  Referal Row No : " +  referalRow ));
					} else if (StringUtils.isNotBlank(referal.getSumInsuredEnd()) && StringUtils.isBlank(referal.getSumInsuredEnd())  ) {
						if (Long.valueOf(referal.getSumInsuredEnd()) > Long.valueOf(referal.getSumInsuredStart()) ) {
							errors.add(new Error("02", "End Limit", "Sum Insured Start Greater Than Sum Insured End in  Referal Row No : " +  referalRow ));
						}
					}
				}	
				
				if( status == false   ) {
					errors.add(new Error("02", "Status", "Plese Select Active Status for Alteast One Referal " ));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			errors.add(new Error("09", "Common Error", e.getMessage() ));
		}
		return errors;
	}


@Override
public List<Error> validateBrokerCompanyBranchReq(AttachBrokerBranchReq req) {
	List<Error> errors = new ArrayList<Error>();
	try {
		
		if(StringUtils.isBlank(req.getLoginId()) ) {
			errors.add(new Error("01", "LoginId", "Plese Enter LoginId" ));
		}
		//Login  Data
		LoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());
		if (loginData.getBrokerCompanyYn() != null && !loginData.getBrokerCompanyYn().equals("N")) {
			if (loginData.getBrokerCompanyYn().equals("Y") && loginData.getUserType().equalsIgnoreCase("BROKER")) {
				if (StringUtils.isBlank(req.getBranchCode())) {
					errors.add(new Error("03", "BranchCode", "Plese Enter BranchCode"));
				}
				if (StringUtils.isBlank(req.getAttachedBranch())) {
					errors.add(new Error("03", "AttachedBranchCode", "Plese Enter AttachedBranchCode"));
				}
			}
		}
		
		if (StringUtils.isBlank(req.getBranchCode())) {
			errors.add(new Error("03", "BranchCode", "Plese Enter BranchCode"));
		}
		
		if (StringUtils.isBlank(req.getBranchType())) {
			errors.add(new Error("03", "BranchType", "Plese Enter BranchType"));
		}
		if(StringUtils.isBlank(req.getCompanyId()) ) {
			errors.add(new Error("02", "InsuranceId", "Plese Enter InsuranceId" ));
		}
		
		if(StringUtils.isBlank(req.getAttachedCompany()) ) {
			errors.add(new Error("03", "AttachedComapany", "Plese Enter AttachedComapany" ));
		}
		
		if (StringUtils.isBlank(req.getRemarks())) {
			errors.add(new Error("03", "Remarks", "Plese Enter Remarks"));
		}
		Calendar cal = new GregorianCalendar();
		Date today = new Date();
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 50);
		today = cal.getTime();
		if (req.getEffectiveDateStart() == null) {
			errors.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start "));

		} else if (req.getEffectiveDateStart().before(today)) {
			errors.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
		} 
		
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --->" + e.getMessage());
		errors.add(new Error("09", "Common Error", e.getMessage() ));
	}
	return errors;
}
	


}
