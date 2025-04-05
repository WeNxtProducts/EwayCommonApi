package com.maan.eway.salesLead;

import java.util.List;

import com.maan.eway.common.req.EserviceCustomerSaveReq;
import com.maan.eway.common.req.GetAllCustomerDetailsReq;
import com.maan.eway.common.req.GetCustomerDetailsReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

public interface SalesLeadService {

	boolean insertLeadDetails(List<InsertSalesReq> req);

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

	SuccessRes saveLeadDetails(EserviceCustomerSaveReq req);

	List<CustomerDetailsGetRes> getallLeadDetails(GetAllCustomerDetailsReq req);

	CustomerDetailsGetRes getLeadDetails(GetCustomerDetailsReq req);

}
