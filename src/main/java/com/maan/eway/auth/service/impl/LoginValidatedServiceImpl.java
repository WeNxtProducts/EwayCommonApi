package com.maan.eway.auth.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.maan.eway.auth.dto.ChangePasswordReq;
import com.maan.eway.auth.dto.CommonLoginRes;
import com.maan.eway.auth.dto.LoginRequest;
import com.maan.eway.auth.service.LoginCriteriaQueryService;
import com.maan.eway.auth.service.LoginValidatedService;
import com.maan.eway.auth.token.passwordEnc;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.SessionMaster;
import com.maan.eway.error.Error;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.SessionMasterRepository;


@Component
public class LoginValidatedServiceImpl implements LoginValidatedService {

	@Autowired
	private LoginCriteriaQueryService criteriaQuery;
	@Autowired
	private LoginMasterRepository loginRepo;
	@Autowired
	private SessionMasterRepository sessionRep;

	private Logger log = LogManager.getLogger(LoginValidatedServiceImpl.class);

	public CommonLoginRes loginInputValidation(LoginRequest req) {
		CommonLoginRes commonRes = new CommonLoginRes();
		List<Error> list = new ArrayList<Error>();

		log.info(req);
		String changePwd = "N";
		try {
			
			List<SessionMaster> sessionlist = new ArrayList<SessionMaster>();
			List<LoginMaster> data  = new ArrayList<LoginMaster>();
			
			String loginId = "" ;
			if (req.getLoginId() == null || StringUtils.isBlank(req.getLoginId())) {
				list.add(new Error("", "UserId", "Please enter Login Id"));	
				
			} else {
				loginId = req.getLoginId() ;
			}
			
			/*	else if (req.getCompanyId() == null || StringUtils.isBlank(req.getCompanyId())) {
				list.add(new Error("", "CompanyId", "Please enter CompanyId"));
				}
			if (req.getUserType() == null || StringUtils.isBlank(req.getUserType())) {
				list.add(new Error("", "UserType", "Please enter UserType"));
			}
			
		 */
		   if (req.getPassword() == null || StringUtils.isBlank(req.getPassword())) {
				list.add(new Error("", "Password", "Please enter password"));
			}
			
		   // Guest Login Checking
			if( loginId.equalsIgnoreCase("guest")  ) {
				if (StringUtils.isNotBlank(req.getLoginId()) && StringUtils.isNotBlank(req.getPassword())) {
					LoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());
					if (loginData ==null ) {
						list.add(new Error("", "UserId", "Please enter Valid Login Id"));
					} 
					
					if (loginData != null  ) {
						data = criteriaQuery.isvalidUser(req);
						if (CollectionUtils.isEmpty(data)) {
							list.add(new Error("", "User", "Please enter valid username/password"));
						}
					}
				} 
			}
			
			// Other Login Checking
			if(! loginId.equalsIgnoreCase("guest")  ) {
//				if (req.getPassword() == null || StringUtils.isBlank(req.getPassword())) {
//					list.add(new Error("", "Password", "Please enter password"));
//				}
				
				if (StringUtils.isNotBlank(req.getLoginId()) && StringUtils.isNotBlank(req.getPassword())) {
					LoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());
					if (loginData ==null ) {
						list.add(new Error("", "UserId", "Please enter Valid Login Id"));
					} else {
						sessionlist = sessionRep.findByLoginIdOrderByEntryDateDesc(req.getLoginId());
					} 
					
					if (loginData != null  ) {
						if (! loginData.getStatus().equalsIgnoreCase("Y") ) {
							list.add(new Error("", "UserId", "This Login Id is Deactivated"));
						} else {
							data = criteriaQuery.isvalidUser(req);
							if (CollectionUtils.isEmpty(data)) {
								list.add(new Error("", "User", "Please enter valid username/password"));
							}else if(isExpired(data.get(0).getLpassDate())) {
								list.add(new Error("", "User", "Password Expired Please Change Your Password"));
								changePwd = "Y";
							}
						}
						
					}
				} 
				
				if(req.getReLoginKey()!=null && req.getReLoginKey().equalsIgnoreCase("Y") &&  sessionlist.size()>0 ) {
					SessionMaster updatelogout = sessionlist.get(0);
						updatelogout.setLogoutDate(new Date());
						updatelogout.setStatus("DE-ACTIVE");
						sessionRep.save(updatelogout);
				}else if(sessionlist.size()!=0) {
				
						if(sessionlist.get(0).getLogoutDate()==null) {
							list.add(new Error("", "SessionError", "You already have an active logged in session on another device or window Do you want to start new session and terminate that session?"));
							list.add(new Error("", "SessionError", "User :" + sessionlist.get(0).getUserName() + " : logged in at " +sessionlist.get(0).getEntryDate().toString()));
						}
				}
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			list.add(new Error("", "CommonError", e.getMessage() ));
		}
		if(list!=null && list.size()>0  ) {
			commonRes.setErrorMessage(list);
			commonRes.setChangePasswordYn(changePwd);
			commonRes.setCommonResponse(null);
			commonRes.setIsError(true);
			commonRes.setMessage("Failed");
		}
		return commonRes;
	}

	private boolean isExpired(Date date) {

		Date d = date;
		Date d1 = new Date();

		// not expired
		if (d1.compareTo(d) < 0) {
			return false;
		} 
		// both date are same
		else if (d.compareTo(d1) == 0) {
			if (d.getTime() < d1.getTime()) {// not expired
				return false;
			} else if (d.getTime() == d1.getTime()) {// expired
				return true;
			} else {// expired
				return true;
			}			
		} 
		// expired
		else {
			return true;
		}
	}


	  public static String getToday(String format){
	     Date date = new Date();
	     return new SimpleDateFormat(format).format(date);
	 }

	  /*
	@Override
	public List<Error> LoginChangePwdValidation(ChangePasswordReq req) {

		log.info(req);

		List<Error> list = new ArrayList<Error>();

		if (req.getUserId() == null || StringUtils.isBlank(req.getUserId())) {
			
			list.add(new Error("", "UserId", "Please enter username"));
			list.add(new Error("", "ChangePassword", "You are not authorized user..!"));
			
		}else {
		
			ClaimLoginMasterId id = new ClaimLoginMasterId();
			id.setLoginId(req.getUserId());
			id.setCompanyId(req.getCompanyId());
			
			Optional<ClaimLoginMaster> model = loginRepo.findById(id);
			if (model.isPresent()) {

				String epass = "";
				passwordEnc passEnc = new passwordEnc();
				
				if (req.getNewPassword() == null || StringUtils.isBlank(req.getNewPassword())) {
					list.add(new Error("", "New password", "Please enter New password"));
				}else if (!validPassword(req.getNewPassword())) {
					list.add(new Error("", "NewPassword", "Please enter the valid password"));					
				}
				else {
					epass = passEnc.crypt(req.getNewPassword().trim());
				}	
				
				if (req.getOldpassword() == null || StringUtils.isBlank(req.getOldpassword())) {
					list.add(new Error("", "Old password", "Please enter Oldpassword"));
				} else if (model.get().getPassword().equals(epass)) {
					list.add(new Error("", "ChangePassword", "Oldpassword  and Newpassword should not match"));
				} else if(model.get().getLpass1().equals(epass) || model.get().getLpass2().equals(epass) || model.get().getLpass3().equals(epass) || model.get().getLpass4().equals(epass) || model.get().getLpass5().equals(epass)) {
					list.add(new Error("", "ChangePassword", "Newpassword should not be last 5 Password"));
				} 
			} else {
				list.add(new Error("", "ChangePassword", "You are not authorized user..!"));
			}
			
		}

 
		return list;
	}

	
	@Override
	public List<Error> InsertLoginValidation(InsertLoginMasterReq req) {

		List<Error> list = new ArrayList<Error>();
		try { 
			
	
		if ( StringUtils.isBlank(req.getLoginId())) {
			list.add(new Error("01", "LoginId", "Please enter LoginId"));
		} else if (req.getLoginId().length() <5 ) {
			list.add(new Error("01", "LoginId", "LoginId Under 5 Charecter Not Allowed"));
		} else {
			ClaimLoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());
			if(loginData !=null  && StringUtils.isBlank(req.getAgencyCode()) ) {
				list.add(new Error("01", "LoginId", "This Login Id Already Available"));
			} else if (loginData !=null  && (! loginData.getAgencyCode().equalsIgnoreCase(req.getAgencyCode())  )) {
				list.add(new Error("01", "LoginId", "This Login Id Already Available"));
			}
		}
		
		if ( StringUtils.isBlank(req.getCreatedBy())) {
			list.add(new Error("03", "CreatedBy", "Please enter CreatedBy"));
		}

		if ( StringUtils.isBlank(req.getMobileNumber())) {
			list.add(new Error("04", "MobileNumber", "Please enter MobileNumber"));
		} else if (req.getMobileNumber().length() <5 ) {
			list.add(new Error("04", "LoginId", "Please Enter Valid Mobile Number"));
		} else if (! req.getMobileNumber().matches("[0-9]+") ) {
			list.add(new Error("04", "LoginId", "Please Enter Valid Mobile Number"));
		}

		if ( StringUtils.isBlank(req.getPassword())) {
			list.add(new Error("05", "Password", "Please enter Password"));
		} else if (req.getMobileNumber().length() <5 ) {
			list.add(new Error("05", "Password", "Password Under 5 Charecter Not Allowed"));
		} 

		if (StringUtils.isBlank(req.getRemarks())) {
			list.add(new Error("06", "Remarks", "Please enter Remarks"));
		}

		if (StringUtils.isBlank(req.getStatus())) {
			list.add(new Error("07", "Status", "Please Select Status"));
		}

		if (StringUtils.isBlank(req.getUserMail())) {
			list.add(new Error("08", "UserMail", "Please enter UserMail"));
		} else {
			String regex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
					+ "A-Z]{2,7}$";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(req.getUserMail());
			 if (!matcher.matches()) {
				 list.add(new Error("101", "email", "Please enter vaild email"));
			}
		}
		
		if (StringUtils.isBlank(req.getUsername())) {
			list.add(new Error("09", "Username", "Please enter Username"));
		} else if(req.getUsername().length() < 5 ||   isValidName(req.getUsername() )) {
			list.add(new Error("101", "Username Name", "Please Enter Valid Username"));
		} 
		
		if ( StringUtils.isBlank(req.getCompanyId())) {
			list.add(new Error("02", "CompanyId", "Please enter CompanyId"));
		} 

		if(req.getBranchCode().size() == 0  ) {
			list.add(new Error("10", "BranchCode", "Please Select Branches"));
		}
		
		boolean status = false ;
		
		if( StringUtils.isBlank(req.getUserType()) ) {
			list.add(new Error("11", "UserType", "Please Select UserType"));
		} else {
			if(req.getUserType().equalsIgnoreCase("UnderWritter")  ) {
				if(req.getUnderWritterList().size() ==0  ) {
					list.add(new Error("11", "UserType", "Please Add Atleast One Under Witter List with Status Y"));
				} else {
					List<Long>   ClassTypes = new ArrayList<Long>(); 
					List<Long>   PolicyTypes = new ArrayList<Long>();
					
					Long rowCount = 0L ;
					for (UnderWritterDetailsReq uw :   req.getUnderWritterList() ) {
						rowCount = rowCount + 1L;
						if ( StringUtils.isBlank(uw.getClassId())) {
							list.add(new Error("01", "ClassId", "Please Select Class In Row No : " + rowCount ));
						} else {
							List<Long> ClassTypes2 = ClassTypes.stream().filter( o ->  Long.valueOf(o).equals(Long.valueOf(uw.getClassId()))  ).collect(Collectors.toList());
							if(ClassTypes2.size() > 0  ) {
								list.add(new Error("01", "ClassId", "Class Duplicate Selected In Row No : "  + rowCount));
							}
						}
						if( StringUtils.isBlank(uw.getStatus()) ) {
							list.add(new Error("02", "Stauts", "Please Select Status In Row No :  "  + rowCount));
						} else if (uw.getStatus().equalsIgnoreCase("Y") ) {
							status = true ;
						}
					/*	if ( StringUtils.isBlank(uw.getClassDesc())) {
							list.add(new Error("02", "ClassDesc", "Please Select Class"));
						}
						if ( StringUtils.isBlank(uw.getPolicyType())) {
							list.add(new Error("02", "PolicyType", "Please Select PolicyType"));
						}
						 */
	/*					if ( StringUtils.isBlank(uw.getPolicyTypeId())) {
							list.add(new Error("02", "PolicyTypeId", "Please Select PolicyTypeId In Row No :  "  + rowCount));
						} else {
							List<Long> PolicyTypes2 = PolicyTypes.stream().filter( o ->  Long.valueOf(o).equals(Long.valueOf(uw.getPolicyTypeId()))  ).collect(Collectors.toList());
							if(PolicyTypes2.size() > 0  ) {
								list.add(new Error("01", "PolicyTypeId", "PolicyTypes Duplicate Selected In Row No : "  + rowCount));
							}
						}
						
						if ( StringUtils.isBlank(uw.getSumInsuredStart())) {
							list.add(new Error("02", "SumInsuredStart", "Please Enter SumInsuredStart In Row No :  " + rowCount ));
						
						} else if (!  uw.getSumInsuredStart().matches("[0-9.]+") ) {
							list.add(new Error("02", "SumInsuredStart", "Please Enter Valid SumInsuredStart In Row No :  " + rowCount));
						
						} else if ( StringUtils.isBlank(uw.getSumInsuredEnd())) {
							list.add(new Error("02", "SumInsuredEnd", "Please Enter SumInsuredEnd In Row No :  " + rowCount));
						
						} else if (!  uw.getSumInsuredEnd().matches("[0-9.]+") ) {
							list.add(new Error("02", "SumInsuredEnd", "Please Enter Valid SumInsuredEnd In Row No :  " + rowCount));
						
						} else if (Long.valueOf(uw.getSumInsuredEnd())< Long.valueOf(uw.getSumInsuredEnd()) ) {
							list.add(new Error("02", "SumInsuredEnd", "SumInsuredStart Greater Than SumInsuredEnd Not Allowed In Row No : "  + rowCount));
						}
						
						
					
					}
					
					if (status == false ) {
						list.add(new Error("11", "Status", "Please Add Atleast One Under Witter List with Status Y"));
					}
				}
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
			list.add(new Error("01", "Common Error", e.getMessage()));
		}
		return list;
	}  */


	public boolean isValidName(String name) {
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
	
	@Override
	public List<Error> LoginChangePasswordValidation(ChangePasswordReq req) {
		log.info(req);
		
		List<Error> list = new ArrayList<Error>();
		
		if(StringUtils.isBlank(req.getLoginId())) {
			list.add(new Error("","Login Id", "Please Enter Login Id"));
		}
		else {
			passwordEnc passEnc = new passwordEnc();
			String epass = passEnc.crypt(req.getOldpassword().trim());
			log.info("Encrypted password"+ epass);
			LoginMaster model = loginRepo.findByLoginIdAndPassword(req.getLoginId(), epass);
			if(model !=null ) {
				if(req.getNewPassword()==null || StringUtils.isBlank(req.getNewPassword())) {
					list.add(new Error ("", "New Password", "Please Enter New Password"));
				} else if (!validPassword(req.getNewPassword())) {
					list.add(new Error("","New Password","Please Enter Valid Password"));
				}
				else {
					epass = passEnc.crypt(req.getNewPassword().trim());
				}	
				
				if (req.getOldpassword() == null || StringUtils.isBlank(req.getOldpassword())) {
					list.add(new Error("", "Old password", "Please enter Oldpassword"));
				} else if (model.getPassword().equalsIgnoreCase(epass)) {
					list.add(new Error("", "ChangePassword", "Oldpassword  and Newpassword should not match"));
				}
				/*else if(model.getLpass1().equals(epass) || model.getLpass2().equals(epass) || model.getLpass3().equals(epass) || model.getLpass4().equals(epass) || model.getLpass5().equals(epass)) {
					list.add(new Error("", "ChangePassword", "Newpassword should not be last 5 Password"));
				} */
			} else {
				list.add(new Error("", "ChangePassword", "You are not authorized user..!"));
			}
			
		}

 
		return list;
	}
	
	private boolean validPassword(String newPassword) {
		Pattern pattern=Pattern.compile("(?=\\S+$).{7,20}");
    	Matcher matcher = pattern.matcher(newPassword);
    	return matcher.matches();
	}

		

}