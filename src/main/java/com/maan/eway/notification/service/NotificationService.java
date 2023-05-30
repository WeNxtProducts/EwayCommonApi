package com.maan.eway.notification.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.maan.eway.auth.token.EncryDecryService;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.QuoteUpdateRes;
import com.maan.eway.error.Error;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.jasper.res.JasperDocumentRes;
import com.maan.eway.jasper.service.JasperService;
import com.maan.eway.master.service.impl.ClausesMasterServiceImpl;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.notification.repository.NotifTransactionDetailsRepository;
import com.maan.eway.notification.req.Broker;
import com.maan.eway.notification.req.Customer;
import com.maan.eway.notification.req.Notification;
import com.maan.eway.notification.req.UnderWriter;
import com.maan.eway.notification.req.statealgo.NotificationStatus;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
@Service
public class NotificationService {
	@Autowired 
	private NotifTransactionDetailsRepository notifTrans;
	
	@Autowired
	private NotificationValidation vad;
	
	
	@Autowired
	private InsuranceCompanyMasterRepository companyRepo;
	
	@Autowired
	private RatingFactorsUtil ratingutil;
	/*
	@Autowired
	private JobScheduler jobScheduler;
	*/
	private Logger log = LogManager.getLogger(NotificationService.class);
	public CommonRes pushNotification(Notification n) {
		
		
		List<Error> validation = vad.pushValidation(n);
		CommonRes c=new CommonRes();
		
		if(validation.isEmpty()) {
			Calendar calend = Calendar.getInstance();
			calend.setTime(n.getNotifcationDate()); 
			calend.add(Calendar.DATE, 1); 
			List<InsuranceCompanyMaster> coms = companyRepo.findByCompanyIdOrderByAmendIdDesc(n.getCompanyid());
			String filesTobeAttch=null;
			if(n.getAttachments()!=null && n.getAttachments().size()>0) {
				filesTobeAttch = n.getAttachments().stream().collect(Collectors.joining(";"));
			}
				
			NotifTransactionDetails sv = null;
			if(n.getUnderwriters().size()>0) {
				List<NotifTransactionDetails> uws=new ArrayList<NotifTransactionDetails>();
				
				//Tiny URL
				List<Tuple> loadTinyUrl = ratingutil.loadTinyUrl(n.getCompanyid(),n.getProductid(),n.getNotifTemplatename());
				List<Tuple> loadDropdown=null;
				if(!loadTinyUrl.isEmpty()) {
					for(Tuple t: loadTinyUrl) {
						String sno = t.get("sno").toString();
						loadDropdown = ratingutil.loadTinyUrlRequest(n.getCompanyid(),n.getProductid(),n.getNotifTemplatename(),sno);  
					}
				}
				
				for (UnderWriter underWriter : n.getUnderwriters()) {
					NotifTransactionDetails nt = NotifTransactionDetails.builder()
							.brokerCompanyName(n.getBroker().getBrokerCompanyName())
							.brokerMailId(n.getBroker().getBrokerMailId())
							.brokerMessengerCode(n.getBroker().getBrokerMessengerCode())
							.brokerMessengerPhone(n.getBroker().getBrokerMessengerPhone())
							.brokerPhoneCode(n.getBroker().getBrokerPhoneCode())
							.brokerPhoneNo(n.getBroker().getBrokerPhoneNo())
							.brokerName(n.getBroker().getBrokerName())
							.companyName(n.getCompanyName())
							.customerMailid(n.getCustomer().getCustomerMailid())
							.customerPhoneCode(n.getCustomer().getCustomerPhoneCode())
							.customerPhoneNo(n.getCustomer().getCustomerPhoneNo())
							.customerMessengerCode(n.getCustomer().getCustomerMessengerCode())
							.customerMessengerPhone(n.getCustomer().getCustomerMessengerPhone())
							.customerName(n.getCustomer().getCustomerName())
							.entryDate(new Date())
							.notifcationPushDate(n.getNotifcationDate())
							.notifcationEndDate(calend.getTime())
							.notifDescription(n.getNotifDescription())
							//.notifNo(null)
							.notifPriority(n.getNotifPriority())
							.notifPushedStatus("P")
							.notifTemplatename(n.getNotifTemplatename())
							.otp(n.getOtp())
							.policyNo(n.getPolicyNo())
							.quoteNo(n.getQuoteNo()) 
							.uwMailid(underWriter.getUwMailid() )
							.uwMessengerCode(underWriter.getUwMessengerCode())
							.uwMessengerPhone(underWriter.getUwMessengerPhone())
							.uwName(underWriter.getUwName())
							.uwPhonecode(underWriter.getUwPhonecode())
							.uwPhoneNo(underWriter.getUwPhoneNo())
							.uwloginId(underWriter.getUwLoginId())
							.uwUserType(underWriter.getUwuserType())
							.uwSubuserType(underWriter.getUwsubuserType())
							.customerRefno(n.getCustomer().getCustomerRefno())
							.branchCode(n.getBranchCode())
							.refno(n.getRefNo())							
							.productName(n.getProductName())
							.sectionName(n.getSectionName())
							.statusMessage(n.getStatusMessage())
							.tinyUrl(n.getTinyUrl())
							.notifPushedStatus(n.getNotifPushedStatus().toString())
							.companyid(n.getCompanyid())
							.productid(n.getProductid())
							.companyLogo(coms.get(0).getCompanyLogo())
							.companyAddress(coms.get(0).getCompanyAddress())
							.attachFilePath(filesTobeAttch)
							.branchCode(n.getBranchCode())
							.customerRefno(n.getCustomer().getCustomerRefno())
							.refno(n.getRefNo())
							.build();
					
					if(!loadTinyUrl.isEmpty()) {
						List<Map<String,String>> mps=null;
						if(loadDropdown!=null && loadDropdown.size()>0) {
							mps=new ArrayList<Map<String,String>>();	
							for(Tuple l :loadDropdown) {
								Map<String,String> mp=new HashMap<String,String>();
								mp.put("JsonKey", l.get("requestJsonKey")==null?"":l.get("requestJsonKey").toString());
								mp.put("JsonColum", l.get("requestColumn")==null?"":l.get("requestColumn").toString());
								mp.put("JsonTable", l.get("requestTable")==null?"":l.get("requestTable").toString());
								mp.put("dropdownYn",l.get("dropdownYn")==null?"":l.get("dropdownYn").toString());
								mps.add(mp);
							}							

						} 

						List<String> list=new ArrayList<String>();
						for(Map<String, String> map:mps){
							String jsonKey = map.get("JsonKey");
							String jsonColum = map.get("JsonColum");
							String dropdownYn= map.get("dropdownYn");
							Object jsonValue = null;
							try {
								
								if("Y".equals(dropdownYn.trim())) {
									Field field = nt.getClass().getField(jsonColum);								
									jsonValue=field.get(nt);
								}else
									jsonValue=jsonColum;
							}catch(Exception e) {
								e.printStackTrace();
							}
							//String jsonValue = ;

							String value="\""+jsonKey+"\":\""+jsonValue+"\"";
							list.add(value);		

						}
						String json="{"+StringUtils.join(list,',')+"}";
						try {
							String encrData = EncryDecryService.encrypt(json);
							String appUrl=loadTinyUrl.get(0).get("appUrl").toString();
							String shorternURL = getShorternURL(appUrl+encrData);
							nt.setTinyUrl(shorternURL);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					uws.add(nt);
				}
				List<NotifTransactionDetails> saveAll = notifTrans.saveAll(uws);
				sv=saveAll.get(0);
			}else {		
				NotifTransactionDetails nt = NotifTransactionDetails.builder()
						.brokerCompanyName(n.getBroker().getBrokerCompanyName())
						.brokerMailId(n.getBroker().getBrokerMailId())
						.brokerMessengerCode(n.getBroker().getBrokerMessengerCode())
						.brokerMessengerPhone(n.getBroker().getBrokerMessengerPhone())
						.brokerPhoneCode(n.getBroker().getBrokerPhoneCode())
						.brokerPhoneNo(n.getBroker().getBrokerPhoneNo())
						.brokerName(n.getBroker().getBrokerName())
						.companyName(n.getCompanyName())
						.customerMailid(n.getCustomer().getCustomerMailid())
						.customerPhoneCode(n.getCustomer().getCustomerPhoneCode())
						.customerPhoneNo(n.getCustomer().getCustomerPhoneNo())
						.customerMessengerCode(n.getCustomer().getCustomerMessengerCode())
						.customerMessengerPhone(n.getCustomer().getCustomerMessengerPhone())
						.customerName(n.getCustomer().getCustomerName())
						.entryDate(new Date())
						.notifcationPushDate(n.getNotifcationDate())
						.notifcationEndDate(calend.getTime())
						.notifDescription(n.getNotifDescription())
						//.notifNo(null)
						.notifPriority(n.getNotifPriority())
						.notifPushedStatus("P")
						.notifTemplatename(n.getNotifTemplatename())
						.otp(n.getOtp())
						.policyNo(n.getPolicyNo())
						.quoteNo(n.getQuoteNo()) 
						.uwMailid((n.getUnderwriters().size()>5)?n.getUnderwriters().subList(0, 5).stream().map(a -> a.getUwMailid()).collect(Collectors.joining(",")):
							n.getUnderwriters().stream().map(a -> a.getUwMailid()).collect(Collectors.joining(",")))
						.uwMessengerCode(n.getUnderwriters().get(0).getUwMessengerCode())
						.uwMessengerPhone(n.getUnderwriters().get(0).getUwMessengerPhone())
						.uwName(n.getUnderwriters().get(0).getUwName())
						.uwPhonecode(n.getUnderwriters().get(0).getUwPhonecode())
						.uwPhoneNo(n.getUnderwriters().get(0).getUwPhoneNo())
						.productName(n.getProductName())
						.sectionName(n.getSectionName())
						.statusMessage(n.getStatusMessage())
						.tinyUrl(n.getTinyUrl())
						.notifPushedStatus(n.getNotifPushedStatus().toString())
						.companyid(n.getCompanyid())
						.productid(n.getProductid())
						.companyLogo(coms.get(0).getCompanyLogo())
						.companyAddress(coms.get(0).getCompanyAddress())
						.attachFilePath(filesTobeAttch)
						.uwloginId(n.getUnderwriters().get(0).getUwLoginId())
						.uwUserType(n.getUnderwriters().get(0).getUwuserType())
						.uwSubuserType(n.getUnderwriters().get(0).getUwsubuserType())
						.customerRefno(n.getCustomer().getCustomerRefno())
						.branchCode(n.getBranchCode())
						.refno(n.getRefNo())
						
						.build();
				sv = notifTrans.save(nt);
			}
			c.setIsError(Boolean.FALSE);
			c.setErroCode(100);
			c.setIsError(null);
			c.setMessage("Pushed Successfuly");
			c.setCommonResponse(sv);
			
			//jobScheduler.enqueue(()->jobProcess(n,nt));
			
			
		}else {
			c.setErroCode(101);
			c.setErrorMessage(validation);
			c.setIsError(Boolean.TRUE);
			c.setMessage("Have Validation");
		}
		
		return  c;
		
		
 	}
	
	private String getShorternURL(String encryptedURL) {
		BufferedReader reader = null;
		URL url =null;
		URLConnection con =null;
		InputStream openStream =null;
		try {
			final String tinyUrl = "http://tinyurl.com/api-create.php?url=";
			String tinyUrlLookup = tinyUrl + URLEncoder.encode(encryptedURL,"UTF-8");
				url = new URL(tinyUrlLookup);
			  con = url.openConnection();
			 con.setConnectTimeout(8000);
			 con.setReadTimeout(5000);
			 openStream = url.openStream();
			reader = new BufferedReader(new InputStreamReader(openStream));
			
			String result = reader.readLine();
			log.info("Encrypted URL result: " + result + " Encrypted URL " + encryptedURL);
			reader.close();
			return result;
		} catch (Exception e) {
			log.error(e);
		}finally {
			try {
					if(reader!=null)				
						reader.close();
					if(openStream!=null)
						openStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return "";
	}
	@Value(value = "${motor.productId}")
	private String motorProductId;
	
	@Value(value = "${travel.productId}")
	private String travelProductId;
	
	@Value(value = "${building.productId}")
	private String buildingProductId;
	
	@Value(value = "${personalaccident.productId}")
	private String personalAccidentProductId;
	
	@Value(value = "${workmencompensation.productId}")
	private String workmenCompensationProductId;
	
	@Value(value = "${employeesliability.productId}")
	private String employeesliabilityProductId;
	
	@Value(value = "${sme.productId}")
	private String smeProductId;
	

	@Autowired
	private LoginUserInfoRepository loginUserRepo;

	@Autowired
	private EserviceCustomerDetailsRepository eserCustRepo ;
	@Autowired
	private EServiceMotorDetailsRepository eserMotRepo ;

	@Autowired
	private EserviceBuildingDetailsRepository eserBuildRepo  ;
	

	@Autowired
	private EserviceTravelDetailsRepository eserTraRepo ;
	

	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo;
	
	@Autowired
	private JasperService jasperService;
	@Async
	public QuoteUpdateRes motorQuotationNotification(NewQuoteReq req) {
		QuoteUpdateRes updateRes = new QuoteUpdateRes();
		try {
			
			String customerRefNo="";
			String applicationId="";
			String loginId ="";
			String companyId="";
			String companyName="";
			String policyNo="";
			String sectionName="";
			String quoteNo="";
			String productName="";
			
			if (req.getProductId().equalsIgnoreCase(motorProductId) ) {
				List<EserviceMotorDetails> cusRefNo = eserMotRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
				 customerRefNo=cusRefNo.get(0).getCustomerReferenceNo();
				 applicationId=cusRefNo.get(0).getApplicationId();
				 loginId = cusRefNo.get(0).getLoginId();
				 companyId=cusRefNo.get(0).getCompanyId();
				 companyName=cusRefNo.get(0).getCompanyName();
				 policyNo=cusRefNo.get(0).getPolicyNo();
				 sectionName=cusRefNo.get(0).getSectionName();
				 quoteNo=StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())?cusRefNo.get(0).getRequestReferenceNo():cusRefNo.get(0).getQuoteNo().toString();
				 productName= cusRefNo.get(0).getProductName();
			}else if (req.getProductId().equalsIgnoreCase(buildingProductId) || req.getProductId().equalsIgnoreCase(smeProductId) ) {
				List<EserviceBuildingDetails> cusRefNo = eserBuildRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
				
				 customerRefNo=cusRefNo.get(0).getCustomerReferenceNo();
				 applicationId=cusRefNo.get(0).getApplicationId();
				 loginId = cusRefNo.get(0).getLoginId();
				 companyId=cusRefNo.get(0).getCompanyId();
				 companyName=cusRefNo.get(0).getCompanyName();
				 policyNo=cusRefNo.get(0).getPolicyNo();
				 sectionName=cusRefNo.get(0).getSectionDesc();
				 quoteNo=StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())?cusRefNo.get(0).getRequestReferenceNo():cusRefNo.get(0).getQuoteNo().toString();
				 productName= cusRefNo.get(0).getProductDesc();
				 
			}else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
				List<EserviceTravelDetails> cusRefNo = eserTraRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
			     
				 customerRefNo=cusRefNo.get(0).getCustomerReferenceNo();
				 applicationId=cusRefNo.get(0).getApplicationId();
				 loginId = cusRefNo.get(0).getLoginId();
				 companyId=cusRefNo.get(0).getCompanyId();
				 companyName=cusRefNo.get(0).getCompanyName();
				 policyNo=cusRefNo.get(0).getPolicyNo();
				 sectionName=cusRefNo.get(0).getSectionName();
				 quoteNo=StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())?cusRefNo.get(0).getRequestReferenceNo():cusRefNo.get(0).getQuoteNo().toString();
				 productName= cusRefNo.get(0).getProductName();
			}else  {
				List<EserviceCommonDetails> cusRefNo = eserCommonRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
				 customerRefNo=cusRefNo.get(0).getCustomerReferenceNo();
				 applicationId=cusRefNo.get(0).getApplicationId();
				 loginId = cusRefNo.get(0).getLoginId();
				 companyId=cusRefNo.get(0).getCompanyId();
				 companyName=cusRefNo.get(0).getCompanyName();
				 policyNo=cusRefNo.get(0).getPolicyNo();
				 sectionName=cusRefNo.get(0).getSectionName();
				 quoteNo=StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())?cusRefNo.get(0).getRequestReferenceNo():cusRefNo.get(0).getQuoteNo().toString();
				 productName= cusRefNo.get(0).getProductDesc();
			}
			

			
			
			
			
			
			if (!"1".equals(applicationId)) {				 
				loginId =applicationId;
			}
			Notification n = new Notification();
			//Broker Info
			LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
			Broker brokerReq = new Broker();
			if(loginInfo!=null) {
			brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?loginInfo.getUserName(): loginInfo.getCompanyName());
			brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
			brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
			brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
			brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
			brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
			brokerReq.setBrokerName(loginInfo.getUserName());
			}
			// Customer Info
			EserviceCustomerDetails customerData = eserCustRepo.findByCustomerReferenceNo(customerRefNo);
			Customer cusReq = new Customer();
			if(customerData!=null) {
				cusReq.setCustomerMailid(customerData.getEmail1());
				cusReq.setCustomerName(customerData.getClientName());
				cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
				cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
				cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
				cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
			}

			// UnderWriter Info
					List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
					UnderWriter underWriterReq = new UnderWriter();
					underWriterReq.setUwMailid(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
					underWriterReq.setUwMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
					underWriterReq.setUwMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
					underWriterReq.setUwPhonecode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
					underWriterReq.setUwPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
					underWriterReq.setUwName(loginInfo.getUserName());
					underWrite.add(underWriterReq);
			n.setUnderwriters(underWrite);
			//Company Info
			n.setCompanyid(companyId);
			n.setCompanyName(companyName);
			
			//Common Info
			n.setBroker(brokerReq);
			n.setCustomer(cusReq);
			n.setNotifcationDate(new Date());
			n.setNotifDescription(req.getReferralRemarks());
			n.setNotifPriority(0);
			n.setNotifPushedStatus(NotificationStatus.PENDING);
			n.setNotifTemplatename("Sent Proposal");
			n.setPolicyNo(policyNo);
			n.setProductid(Integer.valueOf(req.getProductId()));
			n.setProductName(productName);
			n.setQuoteNo(quoteNo);
			n.setSectionName(sectionName);
		 
			JasperDocumentReq r=JasperDocumentReq.builder().quoteNo(quoteNo).productId(req.getProductId()).build();
			JasperDocumentRes rse = jasperService.proposalform(r);
			
			if(StringUtils.isNotBlank(rse.getPdfoutfilepath())) {
				List<String> atact=new ArrayList<String>();
				atact.add(rse.getPdfoutfilepath());
				n.setAttachments(atact);
			}
			// Calling pushNotification
			CommonRes res=pushNotification(n);
 
		} catch (Exception e) {
			e.printStackTrace();
			//log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	}
	
	
}
