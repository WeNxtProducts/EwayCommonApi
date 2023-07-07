package com.maan.eway.otp.service;



import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.admin.req.UserCreationReq;
import com.maan.eway.admin.req.UserLoginReq;
import com.maan.eway.admin.req.UserPersonalInfoReq;
import com.maan.eway.admin.res.LoginCreationRes;
import com.maan.eway.admin.service.LoginDetailsService;
import com.maan.eway.admin.service.LoginValidationService;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.OtpDataDetail;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.QuoteUpdateRes;
import com.maan.eway.error.Error;
import com.maan.eway.notification.req.Broker;
import com.maan.eway.notification.req.Customer;
import com.maan.eway.notification.req.Notification;
import com.maan.eway.notification.req.statealgo.NotificationStatus;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.otp.dto.OtpConfirm;
import com.maan.eway.otp.dto.UserOtp;
import com.maan.eway.otp.dto.ValidateOtp;
import com.maan.eway.repository.CompanyProductMasterRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.OtpDataDetailRepository;

@Service
public class OTPService {

	@Autowired
	private OtpDataDetailRepository otpDataRepo;
	@Autowired
	private NotificationService notiService;
	
	@Autowired
	private LoginUserInfoRepository loginUserRepo;
	
	@Autowired
	private LoginMasterRepository loginMasterRepo;
	
	public OtpConfirm generate(UserOtp otp) {
		try {
			int newOtp = generateOTP();
			long otpId = Instant.now().getEpochSecond();
			Calendar instance = Calendar.getInstance();
			instance.add(Calendar.MINUTE, 2);
			
			OtpDataDetail odd=OtpDataDetail.builder()
								.companyId(otp.getCompanyId())
								.emailId(otp.getUser().getMailId())
								.mobileCode(otp.getUser().getMobileCode())
								.mobileNo(otp.getUser().getMobileNo())
								.whatsappCode(otp.getUser().getWhatsappCode())
								.whatsappNo(otp.getUser().getWhatsappNo())
								.otp(String.valueOf(newOtp))
								.otpId(new BigDecimal(otpId))
								.loginId(otp.getLoginId())
								.expiryDate(instance.getTime())								
								.build();
			OtpDataDetail save = otpDataRepo.save(odd);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					motorPushNotification(save);
					
				}
			}).start();
			OtpConfirm c= OtpConfirm.builder().isError(false).otpToken(otpId).build();
			return c;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int generateOTP() {
		int otp = 0;
		try {
			final TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator();			
			Date now = new Date();
			otp = totp.generateOneTimePassword(totp.TOTP_ALGORITHM_HMAC_SHA1, now);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return otp;

	}

	public OtpConfirm validate(ValidateOtp otp) {
			try {
				List<Error> errorlist=new ArrayList<Error>();
				
				if(otp==null || otp.getUserOtp().toString().length()>6 || !otp.getUserOtp().toString().matches("[0-9]+")) {
					errorlist.add(new Error("09","OTP","Not a Valid OTP"));
				}else {
					 Integer count=	otpDataRepo.countByCompanyIdAndProductIdAndOtpIdAndOtp(otp.getCompanyId(),otp.getProductId(),otp.getOtpToken(),otp.getUserOtp());
					 if(count==0)
						 errorlist.add(new Error("09","OTP","Not a Valid OTP"));
					 else {
						 int i= otpDataRepo.countByCompanyIdAndProductIdAndOtpIdAndOtpAndExpiryDateIsAfter(otp.getCompanyId(),otp.getProductId(),otp.getOtpToken(),otp.getUserOtp(),new Date());
						 if(i>0)
							 errorlist.add(new Error("10","OTP","Otp is Expired"));
					 }
				}
				OtpDataDetail otpData =null;
				if(errorlist.size()==0) {
					 otpData =otpDataRepo.findByCompanyIdAndProductIdAndOtpId(otp.getCompanyId(),otp.getProductId(),otp.getOtpToken());
				}
				if(otp.getCreateUser() && errorlist.size()==0) {					
					 
					 
					errorlist = createUserLogin(otpData,otp);
					
					
				}else if(errorlist.size()==0) {
					String mobileNo = otpData.getMobileCode().concat(otpData.getMobileNo());
					Integer count=loginMasterRepo.countByCompanyIdAndLoginId(otp.getCompanyId(), mobileNo);
					if(count==0) {
						errorlist.add(new Error("10","OTP","User is Not Valid"));
					}
				}
				OtpConfirm c=OtpConfirm.builder()
						.isError(errorlist.size()>0?true:false)
						.errorlist(errorlist)
						.otpToken(otp.getOtpToken())
						.build();
				return c;
			}catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}
	
	@Autowired
	private LoginValidationService validationService ;

	@Autowired
	private  LoginDetailsService entityService;
	
	@Autowired
	private EserviceCustomerDetailsRepository eserviceCustomer;
	private List<Error>  createUserLogin(OtpDataDetail otpData, ValidateOtp otp) {
		List<Error> validation =null;
		try {
			String mobileNo = otpData.getMobileCode().concat(otpData.getMobileNo());
			
			UserCreationReq userCreation=new UserCreationReq();
			UserLoginReq loginInformation = new UserLoginReq();
			loginInformation.setAgencyCode(null);
			loginInformation.setBankCode(null);
			loginInformation.setBrokerCompanyYn("N");
			loginInformation.setCompanyId(otpData.getCompanyId());
			loginInformation.setCreatedBy(otpData.getLoginId());
			loginInformation.setEffectiveDateStart(new Date());
			loginInformation.setLoginId(mobileNo);
			loginInformation.setOaCode(otp.getAgencyCode());
			loginInformation.setPassword("Admin@01");
			loginInformation.setStatus("Y");
			loginInformation.setSubUserType("b2c");
			loginInformation.setUserType("User");
			UserPersonalInfoReq personalInformation = userCreation.getPersonalInformation();
			EserviceCustomerDetails custotmer = eserviceCustomer.findByCustomerReferenceNo(otp.getCustomerId());
			personalInformation.setAcExecutiveId("5");
			personalInformation.setAddress1(custotmer.getAddress1());
			personalInformation.setAddress2(custotmer.getAddress2());
			personalInformation.setAddress3(null);
			personalInformation.setApprovedPreparedBy(otpData.getLoginId());
			personalInformation.setCityCode(custotmer.getCityCode().toString());
			personalInformation.setCityName(custotmer.getCityName());
			personalInformation.setCompanyName(custotmer.getCompanyId());
			personalInformation.setContactPersonName(custotmer.getClientName());
			personalInformation.setCoreAppBrokerCode("NA");
			personalInformation.setCountryCode(custotmer.getNationality());
			personalInformation.setDesignation(custotmer.getOccupationDesc());
			personalInformation.setFax(custotmer.getFax());
			personalInformation.setMissippiId("NA");
			personalInformation.setMobileCode(custotmer.getMobileCode1());
			personalInformation.setPobox(custotmer.getPinCode());
			personalInformation.setRemarks("B2C Customer");
			personalInformation.setStateCode(custotmer.getStateCode().toString());
			personalInformation.setUserMail(custotmer.getEmail1());
			personalInformation.setUserMobile(custotmer.getMobileNo1());
			personalInformation.setUserName(custotmer.getClientName());
			personalInformation.setWhatsappCode(custotmer.getWhatsappCode());
			personalInformation.setWhatsappNo(custotmer.getWhatsappNo());
			userCreation.setLoginInformation(loginInformation);
			userCreation.setPersonalInformation(personalInformation);
			
			 validation = validationService.validateUserCreation(userCreation);
			if(validation.size()==0)
				entityService.createUserLogin(userCreation);
			return validation; 	
		}catch (Exception e) {
			e.printStackTrace();
		}		
		validation=new ArrayList<Error>();
		validation.add(new Error("10","unExpected","Some error while creating user"));
		return validation;
	}

	@Autowired
	private InsuranceCompanyMasterRepository insuranceRepo;
	
	@Autowired
	private CompanyProductMasterRepository cpmRepo;
	
	private QuoteUpdateRes motorPushNotification(OtpDataDetail data) {

		
		try {
			Notification n = new Notification();
			Broker brokerReq = new Broker();
			LoginUserInfo loginInfo = loginUserRepo.findByLoginId(data.getLoginId());
			brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?null: loginInfo.getCompanyName());
			brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
			brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
			brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
			brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
			brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
			brokerReq.setBrokerName(loginInfo.getUserName());
			
			
			Customer cusReq = new Customer();
			
			cusReq.setCustomerMailid(data.getEmailId());
			cusReq.setCustomerName("Customer");
			cusReq.setCustomerPhoneCode(Integer.valueOf(data.getMobileCode()));
			cusReq.setCustomerPhoneNo(new BigDecimal(data.getMobileNo()));
			cusReq.setCustomerMessengerCode(Integer.valueOf(data.getWhatsappCode()));
			cusReq.setCustomerMessengerPhone(new BigDecimal(data.getWhatsappNo()));
			cusReq.setCustomerRefno(data.getCustomerId());
			 
			List<InsuranceCompanyMaster> company = insuranceRepo.findByCompanyIdOrderByAmendIdDesc(data.getCompanyId());

			
			n.setUnderwriters(null);
			//Company Info
			n.setCompanyid(data.getCompanyId());
			n.setCompanyName(company.get(0).getCompanyName());
	
			n.setNotifTemplatename("B2C OTP");
			n.setStatusMessage(null);
			
			//Common Info
			n.setBroker(brokerReq);
			n.setCustomer(cusReq);
			n.setNotifcationDate(new Date());
			n.setNotifDescription("");
			n.setNotifPriority(0);
			n.setNotifPushedStatus(NotificationStatus.PENDING);
			n.setPolicyNo(null);
			List<CompanyProductMaster> products = cpmRepo.findByCompanyIdAndProductIdOrderByAmendIdDesc(data.getCompanyId(), data.getProductId().intValue());
			n.setProductid(data.getProductId().intValue());
			n.setProductName(products.get(0).getProductName());
			n.setQuoteNo(null);
			n.setSectionName(null);
			n.setOtp(Integer.parseInt(data.getOtp()));
			n.setRefNo(data.getReqNo());
			n.setBranchCode(null);
			
 
			// Calling pushNotification
			CommonRes res=notiService.pushNotification(n);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	
	}
	

	
	
}
