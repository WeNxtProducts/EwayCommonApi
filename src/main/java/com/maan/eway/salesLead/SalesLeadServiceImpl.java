package com.maan.eway.salesLead;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

import com.google.gson.Gson;
import com.maan.eway.bean.EserviceLeadDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.IplcmsListItemValue;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.RegionMaster;
import com.maan.eway.bean.StateMaster;
import com.maan.eway.common.req.EserviceCustomerSaveReq;
import com.maan.eway.common.req.GetAllCustomerDetailsReq;
import com.maan.eway.common.req.GetCustomerDetailsReq;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.common.service.impl.EserviceCustomerDetailsServiceImpl;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.repository.EserviceLeadDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.IplcmsListItemValueRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.RegionMasterRepository;
import com.maan.eway.repository.StateMasterRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.salesLead.Repository.EnquiryDetailsRepository;
import com.maan.eway.salesLead.Repository.LeadContactInfoRepository;
import com.maan.eway.salesLead.bean.EnquiryDetails;
import com.maan.eway.salesLead.bean.LeadContactInfo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;
import com.maan.eway.error.Error;

@Service
public class SalesLeadServiceImpl implements SalesLeadService {

	
	private Logger log = LogManager.getLogger(SalesLeadServiceImpl.class);
	
	@Autowired
	SalesLeadCustomRepositry salesLeadCustomRepo;
	
	@Autowired
	private LeadContactInfoRepository leadContactRepo;
	
	@Autowired
	private EnquiryDetailsRepository enquiryDetailsRepo;
	
	@Autowired
	private IplcmsListItemValueRepository iplcmsListItemValueRepo;
	
	@Autowired
	private GenerateSeqNoServiceImpl genSeqNoService ;
	
	@Autowired
	private EserviceCustomerDetailsServiceImpl custService;
	
	@Autowired
	private EserviceLeadDetailsRepository leadrepo;
	
	@Autowired
	private ListItemValueRepository listRepo;
	
	@Autowired
	private RegionMasterRepository regionMasterRepo;
	
	@Autowired
	private StateMasterRepository stateMasterRepo;
	
	@Autowired
	private HomePositionMasterRepository homePosistionRepo;
	
	@Autowired
	private PersonalInfoRepository personalInforepo;
	
	@Autowired
	private LoginMasterRepository loginRepo;
	
	@PersistenceContext
	private EntityManager em;
	
	private Gson gson = new Gson();
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	private final static Logger logger = LogManager.getLogger(SalesLeadServiceImpl.class);
	
	@Override
	public CommonRes insertLeadContact(List<InsertSalesReq> reqList) {
		logger.info("Enter into insertSales.\n Argument ==> "+gson.toJson(reqList));
		CommonRes res = new CommonRes();
		List<LeadContactInfo> resList = new ArrayList<LeadContactInfo>();
		List<Error> error = new ArrayList<Error>();
		AtomicInteger autoIndex = new AtomicInteger(0);
		try {
			if(reqList!=null && reqList.size()>0) {
				reqList.forEach(req -> {
					int currentIndex = autoIndex.getAndIncrement();
					if(StringUtils.isBlank(req.getLeadId()))
						error.add(new Error(String.valueOf(currentIndex),"LeadId","LeadId Required to Save Information"));
					Optional<LeadContactInfo> salesLead = leadContactRepo.findById(StringUtils.isBlank(req.getLeadId())?"":req.getLeadId());
					LeadContactInfo existingList=null;
					if(salesLead.isPresent()) {
						existingList = salesLead.get();
					}
					LeadContactInfo s = LeadContactInfo.builder()
							.leadId(salesLead.isPresent()?existingList.getLeadId():req.getLeadId())//salesLeadCustomRepo.getMaxLeadId()
							.firstName(req.getFirstName())
							.lastName(req.getLastName())
							.address(req.getAddress())
							.email(req.getEmail())
							.mobile(req.getMobile())
							.branchCode(req.getBranchCode())
							.entryDate(salesLead.isPresent()?existingList.getEntryDate():new Date())
							.createdBy(salesLead.isPresent()?existingList.getCreatedBy():req.getLoginId())
							.updatedDate(salesLead.isPresent()?new Date():null)
							.updatedBy(salesLead.isPresent()?req.getLoginId():null)
							.intermediateId(req.getIntermediateId())
							.intermediateName(req.getIntermediateName())
							.channelId(req.getChannelId())
							.channelDesc(req.getChannelDesc())
							.propobabilityOfSuccessId(req.getPropobabilityOfSuccessId())
							.propobabilityOfSuccess(req.getPropobabilityOfSuccess())
							.typeOfBusinessId(req.getTypeOfBusinessId())
							.typeOfBusiness(req.getTypeOfBusiness())
							.currentInsurer(req.getCurrentInsurer())
							.build();
					leadContactRepo.save(s);
					resList.add(s);
				});
				res.setCommonResponse(resList);
				res.setMessage("SUCCESS");
				res.setIsError(false);
				res.setErrorMessage(Collections.emptyList());
			}else {
				error.add(new Error("01", "Request", "Request Data is Empty"));
				res.setCommonResponse(null);
				res.setMessage("FAILED");
				res.setIsError(false);
				res.setErrorMessage(error);
			}
			
		logger.info("Exist into insertSales");
		return res;
		}catch(Exception e) {
			logger.info("Error in insertSales ==> "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public CommonRes getLeadContact(String leadId) {
		logger.info("Enter into getAllSales.");
		CommonRes res = new CommonRes();
		List<GetSalesLeadRes> resList = new ArrayList<GetSalesLeadRes>();
		try {
			List<LeadContactInfo> salesList = new ArrayList<LeadContactInfo>();
			if(StringUtils.isBlank(leadId)) {
				salesList = leadContactRepo.findAll();
			}else {
				LeadContactInfo salesById = leadContactRepo.findById(leadId).get();
				salesList.add(salesById);
			}
			if(!salesList.isEmpty()) {
				salesList.forEach(k -> {
					GetSalesLeadRes m = GetSalesLeadRes.builder()
							.leadId(k.getLeadId()==null?"":k.getLeadId())
							.firstName(k.getFirstName()==null?"":k.getFirstName())
							.lastName(k.getLastName()==null?"":k.getLastName())
							.address(k.getAddress()==null?"":k.getAddress())
							.email(k.getEmail()==null?"":k.getEmail())
							.mobile(k.getMobile()==null?"":k.getMobile())
							.branchCode(k.getBranchCode()==null?"":k.getBranchCode())
							.entryDate(k.getEntryDate()==null?"":sdf.format(k.getEntryDate()))
							.createdBy(k.getCreatedBy()==null?"":k.getCreatedBy())
							.updatedBy(k.getUpdatedBy()==null?"":k.getUpdatedBy())
							.updatedDate(k.getUpdatedDate()==null?"":sdf.format(k.getUpdatedDate()))
							.intermediateId(k.getIntermediateId()==null?"":k.getIntermediateId())
							.intermediateName(k.getIntermediateName()==null?"":k.getIntermediateName())
							.channelId(k.getChannelId()==null?"":k.getChannelId())
							.channelDesc(k.getChannelDesc()==null?"":k.getChannelDesc())
							.propobabilityOfSuccess(k.getPropobabilityOfSuccess()==null?"":k.getPropobabilityOfSuccess())
							.propobabilityOfSuccessId(k.getPropobabilityOfSuccessId()==null?"":k.getPropobabilityOfSuccessId())
							.typeOfBusinessId(k.getTypeOfBusinessId()==null?"":k.getTypeOfBusinessId())
							.typeOfBusiness(k.getTypeOfBusiness()==null?"":k.getTypeOfBusiness())
							.currentInsurer(k.getCurrentInsurer()==null?"":k.getCurrentInsurer())
							.build();
					resList.add(m);
				});
				res.setMessage("SUCCESS");
				res.setCommonResponse(resList);
				res.setIsError(false);
				res.setErrorMessage(Collections.emptyList());
			}
			logger.info("Exit into getAllSales.");
			return res;
		}catch(Exception e) {
			logger.info("Error in getAllSales ==> "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public CommonRes insertEnquiry(EnquiryDetailsDTO req) {
		logger.info("Enter into insertEnquiry.\n Argument ==> "+gson.toJson(req));
		CommonRes res = new CommonRes();
		try {
			Optional<EnquiryDetails> enquiryData = enquiryDetailsRepo.findById(req.getEnquiryId()==null?"":req.getEnquiryId());
			EnquiryDetails existingList=null;
			if(enquiryData.isPresent()) {
				existingList = enquiryData.get();
			}
			EnquiryDetails e = EnquiryDetails.builder()
					.enquiryId(enquiryData.isPresent()?existingList.getEnquiryId():salesLeadCustomRepo.getMaxEnquiryId())
					.leadId(req.getLeadId())
					.enquiryDescription(req.getEnquiryDescription())
	                .lobId(req.getLobId())
	                .productId(req.getProductId())
	                .sumInsured(req.getSumInsured())
	                .suggestPremium(req.getSuggestPremium())
	                .entryDate(req.getEntryDate())
	                .createdBy(req.getCreatedBy())
	                .updatedDate(req.getUpdatedDate())
	                .updatedBy(req.getUpdatedBy())
	                .rejectedDate(req.getRejectedDate())
	                .rejectedReason(req.getRejectedReason())
	                .status(req.getStatus())
	                .quoteNo(req.getQuoteNo())
					.build();
			enquiryDetailsRepo.save(e);
			res.setCommonResponse(e);
			res.setMessage("SUCCESS");
			res.setIsError(false);
			res.setErrorMessage(Collections.emptyList());
		logger.info("Exist into insertEnquiry");
		return res;
		}catch(Exception e) {
			logger.info("Error in insertEnquiry ==> "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public CommonRes getEnquirys(String enquiryId) {
		logger.info("Enter into getAllEnquiry.");
		CommonRes res = new CommonRes();
		List<EnquiryDetailsDTO> resList = new ArrayList<EnquiryDetailsDTO>();
		try {
			List<EnquiryDetails> enquiryList = new ArrayList<EnquiryDetails>();
			if(StringUtils.isBlank(enquiryId)) {
				enquiryList = enquiryDetailsRepo.findAll();
			}else {
				EnquiryDetails enquiry = enquiryDetailsRepo.findByEnquiryId(enquiryId);
				enquiryList.add(enquiry);
			}
			if(!enquiryList.isEmpty()) {
				enquiryList.forEach(k -> {
					EnquiryDetailsDTO e = EnquiryDetailsDTO.builder()
				            .enquiryId(k.getEnquiryId()==null?"":k.getEnquiryId())
							.leadId(k.getLeadId()==null?"":k.getLeadId())
							.enquiryDescription(k.getEnquiryDescription() == null ? "" : k.getEnquiryDescription())
	                        .lobId(k.getLobId() == null ? "" : k.getLobId())
	                        .productId(k.getProductId() == null ? "" : k.getProductId())
	                        .sumInsured(k.getSumInsured())
	                        .suggestPremium(k.getSuggestPremium())
	                        .entryDate(k.getEntryDate())
	                        .createdBy(k.getCreatedBy() == null ? "" : k.getCreatedBy())
	                        .updatedDate(k.getUpdatedDate())
	                        .updatedBy(k.getUpdatedBy() == null ? "" : k.getUpdatedBy())
	                        .rejectedDate(k.getRejectedDate())
	                        .rejectedReason(k.getRejectedReason() == null ? "" : k.getRejectedReason())
	                        .status(k.getStatus() == null ? "" : k.getStatus())
	                        .quoteNo(k.getQuoteNo() == null ? "" : k.getQuoteNo())
							.build();
					resList.add(e);
				});
				res.setMessage("SUCCESS");
				res.setCommonResponse(resList);
				res.setIsError(false);
				res.setErrorMessage(Collections.emptyList());
			}
			logger.info("Exit into getAllEnquiry.");
			return res;
		}catch(Exception e) {
			logger.info("Error in getAllEnquiry ==> "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<DropDownRes> contactType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "CONTACT_TYPE" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType(itemType);
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getId().toString());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
//				if(!data.getParam1().isEmpty()) {
//				res.setTitletype(data.getParam1());
//				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> customerType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "CUSTOMER_TYPE" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType(itemType);
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getId().toString());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
//				if(!data.getParam1().isEmpty()) {
//				res.setTitletype(data.getParam1());
//				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> sectionType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "SECTION_TYPE" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType(itemType);
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getId().toString());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
//				if(!data.getParam1().isEmpty()) {
//				res.setTitletype(data.getParam1());
//				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> typeOfBusiness(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "TYPE_OF_BUSINESS" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType(itemType);
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getId().toString());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
//				if(!data.getParam1().isEmpty()) {
//				res.setTitletype(data.getParam1());
//				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> currentInsurer(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "INS_COMP" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType(itemType);
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getId().toString());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
//				if(!data.getParam1().isEmpty()) {
//				res.setTitletype(data.getParam1());
//				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> lineOfBusiness(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "LINE_OF_BUSINESS" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType(itemType);
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getId().toString());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
//				if(!data.getParam1().isEmpty()) {
//				res.setTitletype(data.getParam1());
//				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> product(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "PRODUCT" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemTypeAndParam1(itemType,req.getProductId());
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getId().toString());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
//				if(!data.getParam1().isEmpty()) {
//				res.setTitletype(data.getParam1());
//				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> businessType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "BUSINESS_TYPE" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType(itemType);
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getId().toString());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
//				if(!data.getParam1().isEmpty()) {
//				res.setTitletype(data.getParam1());
//				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> probabilityOfSuccess(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType= "POS" ;

			List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType(itemType);
			for (IplcmsListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getId().toString());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
//				if(!data.getParam1().isEmpty()) {
//				res.setTitletype(data.getParam1());
//				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public CommonRes insertPersonalInfo(String enquiryId) {
		logger.info("Enter into InsertPersonalInfo.\nArgument => "+enquiryId);
		try {
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Transactional
	@Override
	public SuccessRes saveLeadDetails(EserviceCustomerSaveReq req) {
			SuccessRes res = new SuccessRes();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
		//	SimpleDateFormat sdf = new SimpleDateFormat("yyMMddmmssSSS");
			try {
				EserviceLeadDetails saveData = new EserviceLeadDetails();
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
				//	custRefNo = "Cust-" +   generateCustRefNo() ; // idf.format(new Date()) + random ;
					// Generate Seq
		 			SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
		 		 	generateSeqReq.setInsuranceId(req.getCompanyId());  
		 		 	generateSeqReq.setProductId(req.getProductId());
		 		 	generateSeqReq.setType("8");
		 		 	generateSeqReq.setTypeDesc("LEAD_REFERENCE_NO");
		 		 	custRefNo =  genSeqNoService.generateSeqCall(generateSeqReq);
					res.setResponse("Saved Successfully");
					res.setSuccessId(custRefNo);
				} else {
					// Update
					custRefNo = req.getCustomerReferenceNo();
					EserviceLeadDetails findData = leadrepo.findByCustomerReferenceNo(req.getCustomerReferenceNo());
					entryDate = findData.getEntryDate();
					createdBy = findData.getCreatedBy();
					productId=findData.getProductId();
					res.setResponse("Updated Successfully");
					res.setSuccessId(custRefNo);
				}
	        
	        	// Dob Condition
		        if(req.getDobOrRegDate() ==null  ) {
					Date   dobOrReg = new Date() ;
					if( req.getPolicyHolderType().equalsIgnoreCase("1") ) {
						// Dob
						Calendar cal = new GregorianCalendar();
						cal.setTime(dobOrReg);
						cal.add(Calendar.YEAR, -18);
						dobOrReg = cal.getTime();
					}
					req.setDobOrRegDate(dobOrReg);
					
				}
	        
				dozerMapper.map(req, saveData);
				saveData.setProductId(productId);
				saveData.setEntryDate(entryDate);
				saveData.setCreatedBy(createdBy);
				saveData.setUpdatedDate(new Date());
				saveData.setUpdatedBy(req.getCreatedBy());
				saveData.setCustomerReferenceNo(custRefNo);
				saveData.setStatus(req.getStatus());
				
				saveData.setZone(StringUtils.isBlank(req.getZone())? 0:Integer.valueOf(req.getZone()));
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
				
				Map<String,String> title = custService.getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"NAME_TITLE",req.getTitle());//listRepo.findByItemTypeAndItemCode("NAME_TITLE", req.getTitle());
				Map<String,String> gender = custService.getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"GENDER",req.getGender());// listRepo.findByItemTypeAndItemCode("GENDER", saveData.getGender());
				Map<String,String> language = custService.getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"LANGUAGE",req.getLanguage());//listRepo.findByItemTypeAndItemCode("LANGUAGE", req.getLanguage());
				Map<String,String> policyHolderType = custService.getListItemLocal ("99999" , req.getBranchCode() ,"POLICY_HOLDER_TYPE",req.getPolicyHolderType());//listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_TYPE",	req.getPolicyHolderType());
				Map<String,String> policyHolderTypeId = custService.getListItemLocal (req.getCompanyId(), req.getBranchCode() ,"POLICY_HOLDER_ID_TYPE",req.getPolicyHolderTypeid());// listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_ID_TYPE", req.getPolicyHolderTypeid());
				
				String genderDesc = Optional.ofNullable(gender).map(map -> map.get("itemDesc")).orElse("");
				String titleDesc = Optional.ofNullable(title).map(map -> map.get("itemDesc")).orElse("");
				String languageDesc = Optional.ofNullable(language).map(map -> map.get("itemDesc")).orElse("");
				String policyHolderTypeDesc = Optional.ofNullable(policyHolderType).map(map -> map.get("itemDesc")).orElse("");
				String policyHolderTypeIdDesc = Optional.ofNullable(policyHolderTypeId).map(map -> map.get("itemDesc")).orElse("");
				
				// From List Item Value (Local)
				String genderLocal = Optional.ofNullable(gender).map(map -> map.get("itemDescLocal")).orElse("");
				String titleLocal = Optional.ofNullable(title).map(map -> map.get("itemDescLocal")).orElse("");
				String languageLocal = Optional.ofNullable(language).map(map -> map.get("itemDescLocal")).orElse("");
				String PolicyHolderTypeLocal = Optional.ofNullable(policyHolderType).map(map -> map.get("itemDescLocal")).orElse("");
				String policyHolderTypeIdLocal = Optional.ofNullable(policyHolderTypeId).map(map -> map.get("itemDescLocal")).orElse("");
				
				// From Region_mater for state name local
				String stateNameLocal = "";
				List<RegionMaster> rgMaster = regionMasterRepo.findByCountryIdAndRegionCode(req.getNationality(),req.getStateCode());
				if(rgMaster!= null  && rgMaster.size()>0) {
					stateNameLocal = rgMaster.get(0).getRegionNameLocal();
				}
				// From State_master for city name local
				String cityNameLocal = "";
				List<StateMaster> stMaster = stateMasterRepo.findByStateIdAndCountryIdAndRegionCode(Integer.valueOf(req.getCityCode()),req.getNationality(),req.getStateCode());
				if(stMaster!= null && stMaster.size()>0) {
					cityNameLocal = stMaster.get(0).getStateNameLocal();
				}
				
				if(StringUtils.isNotBlank(req.getMobileCode1())){		        
					Map<String,String> mobileCode1Desc = custService.getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode1());
					String mobileCode1 = Optional.ofNullable(mobileCode1Desc).map(map -> map.get("itemDesc")).orElse("");
					String mobileCode1Local = Optional.ofNullable(mobileCode1Desc).map(map -> map.get("itemDescLocal")).orElse("");		
				saveData.setMobileCodeDesc1(mobileCode1);

				}
		        if(StringUtils.isNotBlank(req.getMobileCode2())){
		        	Map<String,String> mobileCode2Desc = custService.getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode2());
		        	String mobileCode2 = Optional.ofNullable(mobileCode2Desc).map(map -> map.get("itemDesc")).orElse("");
					String mobileCode2Local = Optional.ofNullable(mobileCode2Desc).map(map -> map.get("itemDescLocal")).orElse("");		
				saveData.setMobileCodeDesc2(mobileCode2);

		        }
		       
		        
		        if(StringUtils.isNotBlank(req.getMobileCode3())){		        
		        	Map<String,String> mobileCode3Desc = custService.getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode3());
		        	String mobileCode3 = Optional.ofNullable(mobileCode3Desc).map(map -> map.get("itemDesc")).orElse("");
					String mobileCode3Local = Optional.ofNullable(mobileCode3Desc).map(map -> map.get("itemDescLocal")).orElse("");
				saveData.setMobileCodeDesc3(mobileCode3);

		        }
		        if(StringUtils.isNotBlank(req.getWhatsappCode())){		        
		        	Map<String,String> whatsappCodeDesc = custService.getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getWhatsappCode());
		        	String whatsappCode = Optional.ofNullable(whatsappCodeDesc).map(map -> map.get("itemDesc")).orElse("");
					String whatsappCodeLocal = Optional.ofNullable(whatsappCodeDesc).map(map -> map.get("itemDescLocal")).orElse("");
				saveData.setWhatsappCodeDesc(whatsappCode);

		        }			
		        String businessTypeLocal = "";
				if (StringUtils.isNotBlank(req.getBusinessType())) {
					Map<String,String> businessTypeDesc =  custService.getListItemLocal ("99999" , req.getBranchCode() ,"BUSINESS_TYPE",req.getBusinessType());//listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE", req.getBusinessType());
					String businessType = Optional.ofNullable(businessTypeDesc).map(map -> map.get("itemDesc")).orElse("");
					businessTypeLocal = Optional.ofNullable(businessTypeDesc).map(map -> map.get("itemDescLocal")).orElse("");
					saveData.setBusinessTypeDesc(businessType);
				}
	 			Map<String,String> occupation = custService.getByOccupationIdDesc(req.getOccupation(), req.getCompanyId(),req.getProductId() , req.getBranchCode());
				String occupationDesc = Optional.ofNullable(occupation).map(map -> map.get("occupationName")).orElse("");
				String occupationDescLocal = Optional.ofNullable(occupation).map(map -> map.get("occupationNameLocal")).orElse("");
				saveData.setTitleDesc(titleDesc);
				saveData.setMiddleName(req.getMiddleName());
				saveData.setLastName(req.getLastName());
				saveData.setPreferredNotification(req.getPreferredNotification());
				saveData.setIsTaxExempted(req.getIsTaxExempted());
				saveData.setRegionCode(req.getRegionCode());
				saveData.setStatus(req.getStatus());
				saveData.setBusinessType(req.getBusinessType());
				saveData.setVrTinNo(req.getVrTinNo());
				saveData.setVrnGst(req.getVrTinNo());
				saveData.setMobileCode1(req.getMobileCode1());
				saveData.setMobileCode2(req.getMobileCode2()==null?"":req.getMobileCode2());
				saveData.setGenderDesc(genderDesc);
				saveData.setLanguageDesc(languageDesc);
				saveData.setOccupationDesc(occupationDesc);
				saveData.setOtherOccupation(req.getOtherOccupation());
				saveData.setPolicyHolderTypeDesc(policyHolderTypeDesc);
				saveData.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc);
				saveData.setIdType(req.getPolicyHolderTypeid());
				saveData.setIdTypeDesc(policyHolderTypeIdDesc);
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
				
				//local desc feilds
				saveData.setGenderDescLocal(genderLocal);
				saveData.setTitleDescLocal(titleLocal);
				saveData.setLanguageDescLocal(languageLocal);
				saveData.setPolicyHolderTypeDescLocal(PolicyHolderTypeLocal);
				saveData.setPolicyHolderTypeIdDescLocal(policyHolderTypeIdLocal);
				saveData.setOccupationDescLocal(occupationDescLocal);
				//saveData.setMaritalStatusDescLocal(maritalStatusDescLocal);
				saveData.setStateNameLocal(stateNameLocal);
				saveData.setCityNameLocal(cityNameLocal);
				saveData.setMobileCodeDesc1Local(req.getMobileCode1());
				saveData.setMobileCodeDesc2Local(req.getMobileCode2());
				saveData.setMobileCodeDesc3Local(req.getMobileCode3());
				saveData.setWhatsappCodeDescLocal(req.getWhatsappCode());
				saveData.setIdTypeDescLocal(policyHolderTypeIdLocal);
				saveData.setSocioProfessionalCategory(req.getSocioProfessionalCategory());
				saveData.setActivities(req.getActivities());
				
				// Kenya Rating Fields
				saveData.setMaritalStatus(StringUtils.isBlank(req.getMaritalStatus()) ?"Single" : req.getMaritalStatus() );
				if (req.getLicenseIssuedDate()!=null ) {
					saveData.setLicenseIssuedDate(req.getLicenseIssuedDate());
					Date licenceIssued = req.getDobOrRegDate();
					Date today = new Date();
					int licenseDuration = today.getYear() - licenceIssued.getYear();
					saveData.setLicenseDuration(licenseDuration);
					
				} else {
					saveData.setLicenseIssuedDate(new Date());
					saveData.setLicenseDuration(20);
				}
				
				
//				if((StringUtils.isNotBlank(req.getNationality()))&&(StringUtils.isNotBlank(req.getStateCode()))){
//				List<StateMaster> stateCityNames = getStateAndCityName(req.getNationality(), req.getStateCode());
//				saveData.setStateName(stateCityNames.get(0).getStateName() == null ? "" : stateCityNames.get(0).getStateName().toString());
//				saveData.setCityName(req.getCityName());
//				}
				leadrepo.save(saveData);

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
						String businessType =  custService.getListItem ("99999" , req.getBranchCode() ,"BUSINESS_TYPE",req.getBusinessType());//listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE", req.getBusinessType());
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
					savePersonalInfo.setGenderDesc(genderDesc);
					savePersonalInfo.setTitleDesc(titleDesc);
					savePersonalInfo.setLanguageDesc(languageDesc);
					savePersonalInfo.setOccupationDesc(occupationDesc);
					
							
					// Induvidual / Corporate
					savePersonalInfo.setPolicyHolderType(req.getPolicyHolderType());
					savePersonalInfo.setPolicyHolderTypeDesc(policyHolderTypeDesc);
					
					// Possport or etc
					savePersonalInfo.setPolicyHolderTypeid(req.getPolicyHolderTypeid());
					savePersonalInfo.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc);
					savePersonalInfo.setIdType(req.getPolicyHolderTypeid());
					savePersonalInfo.setIdTypeDesc(policyHolderTypeIdDesc);
					 
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
					
					
					// local desc 
					savePersonalInfo.setTitleDescLocal(titleLocal);
					savePersonalInfo.setGenderDescLocal(genderLocal);
					savePersonalInfo.setOccupationDescLocal(occupationDescLocal);
					savePersonalInfo.setBusinessTypeDescLocal(businessTypeLocal);
					savePersonalInfo.setStateNameLocal(stateNameLocal);
					savePersonalInfo.setCityNameLocal(cityNameLocal);
					savePersonalInfo.setIdTypeDescLocal(policyHolderTypeIdLocal);
					savePersonalInfo.setPolicyHolderTypeDescLocal(PolicyHolderTypeLocal);
					savePersonalInfo.setLanguageDescLocal(languageLocal);
					savePersonalInfo.setPolicyHolderTypeDescLocal(PolicyHolderTypeLocal);
					savePersonalInfo.setPolicyHolderTypeIdDescLocal(policyHolderTypeIdLocal);
					savePersonalInfo.setSocioProfessionalCategory(req.getSocioProfessionalCategory());
					savePersonalInfo.setActivities(req.getActivities());
					
					
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
							String businessType =  custService.getListItem ("99999" , req.getBranchCode() ,"BUSINESS_TYPE",req.getBusinessType());//listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE", req.getBusinessType());
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
						savePersonalInfo.setGenderDesc(genderDesc);
						savePersonalInfo.setTitle(req.getTitle());
						savePersonalInfo.setTitleDesc(titleDesc);
						savePersonalInfo.setLanguageDesc(languageDesc);
						savePersonalInfo.setOccupationDesc(occupationDesc);
						savePersonalInfo.setIdType(req.getIdType()); 
						savePersonalInfo.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc);
						
						// Induvidual / Corporate
						savePersonalInfo.setPolicyHolderType(req.getPolicyHolderType());
						savePersonalInfo.setPolicyHolderTypeDesc(policyHolderTypeDesc);
						
						// Possport or etc
						savePersonalInfo.setPolicyHolderTypeid(req.getPolicyHolderTypeid());
						savePersonalInfo.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc);
						savePersonalInfo.setIdType(req.getPolicyHolderTypeid());
						savePersonalInfo.setIdTypeDesc(policyHolderTypeDesc);
						
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
						
						// local desc 
						savePersonalInfo.setTitleDescLocal(titleLocal);
						savePersonalInfo.setGenderDescLocal(genderLocal);
						savePersonalInfo.setOccupationDescLocal(occupationDescLocal);
						savePersonalInfo.setBusinessTypeDescLocal(businessTypeLocal);
						savePersonalInfo.setStateNameLocal(stateNameLocal);
						savePersonalInfo.setCityNameLocal(cityNameLocal);
						savePersonalInfo.setIdTypeDescLocal(PolicyHolderTypeLocal);
						savePersonalInfo.setLanguageDescLocal(languageLocal);
						savePersonalInfo.setPolicyHolderTypeDescLocal(PolicyHolderTypeLocal);
						savePersonalInfo.setPolicyHolderTypeIdDescLocal(policyHolderTypeIdLocal);
						savePersonalInfo.setSocioProfessionalCategory(req.getSocioProfessionalCategory());
						savePersonalInfo.setActivities(req.getActivities());
						
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
	
	@Override
	public List<CustomerDetailsGetRes> getallLeadDetails(GetAllCustomerDetailsReq req) {
		List<CustomerDetailsGetRes> resList = new ArrayList<CustomerDetailsGetRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			// Limit , Offset
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
			Pageable paging = PageRequest.of(limit, offset, Sort.by("updatedDate").descending());
			
			LoginMaster loginData = loginRepo.findByLoginId(req.getCreatedBy());
			
			LoginMaster Brokerlogin = loginRepo.findByAgencyCodeAndCompanyId(loginData.getOaCode().toString(),req.getComapanyId());
			List<EserviceLeadDetails> custList = new ArrayList<EserviceLeadDetails>(); 
			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EserviceLeadDetails> query = cb.createQuery(EserviceLeadDetails.class);

			// Find All
			Root<EserviceLeadDetails> c = query.from(EserviceLeadDetails.class);
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
				if ("Broker".equalsIgnoreCase(loginData.getUserType())) {
					Subquery<String> loginId = query.subquery(String.class);
					Root<LoginMaster> ocpm1 = loginId.from(LoginMaster.class);
					loginId.select(ocpm1.get("loginId"));
					Predicate a1 = cb.equal(ocpm1.get("agencyCode"), loginData.getOaCode());
					Predicate a2 = cb.equal(ocpm1.get("companyId"),req.getComapanyId());

					loginId.where(a1,a2);
					n5 = cb.equal(c.get("createdBy"), loginId.as(String.class));
				}else 	if ("User".equalsIgnoreCase(loginData.getUserType())&& !"Direct".equalsIgnoreCase(Brokerlogin.getSubUserType())) {
					Subquery<Long> loginId = query.subquery(Long.class);
					Root<LoginMaster> ocpm1 = loginId.from(LoginMaster.class);
					loginId.select(ocpm1.get("loginId"));
					Predicate a1 = cb.equal(ocpm1.get("agencyCode"), loginData.getOaCode());
					Predicate a2 = cb.equal(ocpm1.get("companyId"),req.getComapanyId());
					loginId.where(a1,a2);
					n5 = cb.equal(c.get("createdBy"), loginId.as(String.class));
				}else 	if ("User".equalsIgnoreCase(loginData.getUserType())&& "Direct".equalsIgnoreCase(Brokerlogin.getSubUserType())) {
					n5 = cb.equal(c.get("createdBy"), req.getCreatedBy());
				}
			//	n5 = cb.equal(  h.get("loginId"), req.getCreatedBy());
			} else {
				
				n4 = cb.equal(  h.get("branchCode"),  req.getBranchCode());
				n5 = cb.equal(  h.get("applicationId"), req.getCreatedBy());
			}

			query.where(n1, n2,n3, n4, n5,n7).orderBy(orderList);

			// Get Result
			TypedQuery<EserviceLeadDetails> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			custList = result.getResultList();
			
			
			
			List<EserviceLeadDetails> totalCustList = new ArrayList<EserviceLeadDetails>();
			Page<EserviceLeadDetails> datas = null ;
			if (loginData.getUserType().equalsIgnoreCase("Broker")|| (loginData.getUserType().equalsIgnoreCase("User")&& !"Direct".equalsIgnoreCase(Brokerlogin.getSubUserType()))) {
//				datas = repository.findByCompanyIdAndBrokerBranchCodeAndCreatedBy(paging,
//						req.getComapanyId(), req.getBrokerBranchCode(),
//						req.getCreatedBy());
				List<LoginMaster> loginlist = loginRepo.findByOaCodeAndCompanyId(loginData.getOaCode(),req.getComapanyId());
				List<String> loginIds=loginlist.stream().map(LoginMaster :: getLoginId ).collect(Collectors.toList())  ;
				datas = leadrepo.findByCompanyIdAndBrokerBranchCodeAndCreatedByIn(paging,
						req.getComapanyId(), req.getBrokerBranchCode(),loginIds);
			} else {
//				datas = repository.findByCompanyIdAndBranchCodeAndCreatedBy(paging, req.getComapanyId(),
//						req.getBranchCode(), req.getCreatedBy());
				datas  = leadrepo.findByCompanyIdAndBranchCodeAndCreatedBy(paging,
						req.getComapanyId(), req.getBranchCode(),req.getCreatedBy());
			}
			
			totalCustList.addAll(datas.getContent());
			totalCustList.addAll(custList);
			
			totalCustList = totalCustList.stream().filter(distinctByKey(o -> Arrays.asList(o.getCustomerReferenceNo()))).collect(Collectors.toList());

			for (EserviceLeadDetails data : totalCustList) {
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
				
				res.setSocioProfessionalCategory(data.getSocioProfessionalCategory());	
				res.setActivities(data.getActivities());				
				
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
	public CustomerDetailsGetRes getLeadDetails(GetCustomerDetailsReq req) {
		CustomerDetailsGetRes res = new CustomerDetailsGetRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			List<EserviceLeadDetails> data = leadrepo.findByCustomerReferenceNoOrderByEntryDateDesc(req.getCustomerReferenceNo());
	
			res = dozerMapper.map(data.get(0), CustomerDetailsGetRes.class);
			
			res.setMobileCodeDesc1(data.get(0).getMobileCodeDesc1()==null?"":data.get(0).getMobileCodeDesc1());
			res.setMobileCodeDesc2(data.get(0).getMobileCodeDesc2()==null?"":data.get(0).getMobileCodeDesc2());
			res.setMobileCodeDesc3(data.get(0).getMobileCodeDesc3()==null?"":data.get(0).getMobileCodeDesc3());
			res.setMobileCode1(data.get(0).getMobileCode1()==null?"":data.get(0).getMobileCode1());
			res.setMobileCode2(data.get(0).getMobileCode2()==null?"":data.get(0).getMobileCode2());
			res.setMobileCode3(data.get(0).getMobileCode3()==null?"":data.get(0).getMobileCode3());
			res.setSocioProfessionalCategory(data.get(0).getSocioProfessionalCategory());
			res.setActivities(data.get(0).getActivities());
			
					
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}


}
