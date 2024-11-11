package com.maan.eway.salesLead;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.bean.IplcmsListItemValue;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.repository.IplcmsListItemValueRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.salesLead.Repository.EnquiryDetailsRepository;
import com.maan.eway.salesLead.Repository.SalesLeadRepository;
import com.maan.eway.salesLead.bean.EnquiryDetails;
import com.maan.eway.salesLead.bean.SalesLead;

@Service
public class SalesLeadServiceImpl implements SalesLeadService {

	@Autowired
	SalesLeadCustomRepositry salesLeadCustomRepo;
	
	@Autowired
	private SalesLeadRepository salesLeadRepo;
	
	@Autowired
	private EnquiryDetailsRepository enquiryDetailsRepo;
	
	@Autowired
	private IplcmsListItemValueRepository iplcmsListItemValueRepo;
	
	private Gson gson = new Gson();
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	private final static Logger logger = LogManager.getLogger(SalesLeadServiceImpl.class);
	
	@Override
	public CommonRes insertSales(InsertSalesReq req) {
		logger.info("Enter into insertSales.\n Argument ==> "+gson.toJson(req));
		CommonRes res = new CommonRes();
		try {
			Optional<SalesLead> salesLead = salesLeadRepo.findById(StringUtils.isBlank(req.getLeadId())?"":req.getLeadId());
			SalesLead existingList=null;
			if(salesLead.isPresent()) {
				existingList = salesLead.get();
			}
			SalesLead s = SalesLead.builder()
					.leadId(salesLead.isPresent()?existingList.getLeadId():salesLeadCustomRepo.getMaxLeadId())
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
			salesLeadRepo.save(s);
			res.setCommonResponse(s);
			res.setMessage("SUCCESS");
			res.setIsError(false);
			res.setErrorMessage(Collections.emptyList());
		logger.info("Exist into insertSales");
		return res;
		}catch(Exception e) {
			logger.info("Error in insertSales ==> "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public CommonRes getSalesLead(String leadId) {
		logger.info("Enter into getAllSales.");
		CommonRes res = new CommonRes();
		List<GetSalesLeadRes> resList = new ArrayList<GetSalesLeadRes>();
		try {
			List<SalesLead> salesList = new ArrayList<SalesLead>();
			if(StringUtils.isBlank(leadId)) {
				salesList = salesLeadRepo.findAll();
			}else {
				SalesLead salesById = salesLeadRepo.findById(leadId).get();
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


}
