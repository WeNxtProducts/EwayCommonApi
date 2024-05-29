package com.maan.eway.salesLead;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.bean.SalesLead;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.repository.SalesLeadRepository;

@Service
public class SalesLeadServiceImpl implements SalesLeadService {

	@Autowired
	SalesLeadCustomRepositry salesLeadCustomRepo;
	
	@Autowired
	private SalesLeadRepository salesLeadRepo;
	
	private Gson gson = new Gson();
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	private final static Logger logger = LogManager.getLogger(SalesLeadServiceImpl.class);
	
	@Override
	public CommonRes insertSales(InsertSalesReq req) {
		logger.info("Enter into insertSales.\n Argument ==> "+gson.toJson(req));
		CommonRes res = new CommonRes();
		try {
			Optional<SalesLead> salesLead = salesLeadRepo.findById(req.getLeadId());
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
	public CommonRes getAllSales() {
		logger.info("Enter into getAllSales.");
		CommonRes res = new CommonRes();
		List<GetAllSalesRes> resList = new ArrayList<GetAllSalesRes>();
		try {
			List<SalesLead> salesList = salesLeadRepo.findAll();
			if(!salesList.isEmpty()) {
				salesList.forEach(k -> {
					GetAllSalesRes m = GetAllSalesRes.builder()
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

}
