package com.maan.eway.salesLead;

import java.util.List;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.res.DropDownRes;

public interface SalesLeadService {

	CommonRes insertSales(InsertSalesReq req);

	CommonRes getSalesLead(String leadId);

	CommonRes insertEnquiry(EnquiryDetailsDTO req);

	CommonRes getEnquirys(String enquiryId);

	List<DropDownRes> contactType(LovDropDownReq req);

	List<DropDownRes> customerType(LovDropDownReq req);

	List<DropDownRes> sectionType(LovDropDownReq req);

	List<DropDownRes> typeOfBusiness(LovDropDownReq req);

	List<DropDownRes> currentInsurer(LovDropDownReq req);

	List<DropDownRes> lineOfBusiness(LovDropDownReq req);

	List<DropDownRes> product(LovDropDownReq req);

	List<DropDownRes> businessType(LovDropDownReq req);

	List<DropDownRes> probabilityOfSuccess(LovDropDownReq req);

	CommonRes insertPersonalInfo(String enquiryId);

}
