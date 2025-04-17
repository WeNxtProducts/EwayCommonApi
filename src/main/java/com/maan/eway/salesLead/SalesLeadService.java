package com.maan.eway.salesLead;

import java.util.List;

import com.maan.eway.common.req.GetCustomerDetailsReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

public interface SalesLeadService {

	boolean insertLeadDetails(List<InsertSalesReq> req);

	CommonRes getSalesLead(String leadId);

	CommonRes insertEnquiry(EnquiryDetailsDTO req);

	CommonRes getEnquirys(GetEnquiryDetailsReq req);

	List<DropDownRes> contactType(LovDropDownReq req);

	List<DropDownRes> customerType(LovDropDownReq req);

	List<DropDownRes> sectionType(LovDropDownReq req);

	List<DropDownRes> typeOfBusiness(LovDropDownReq req);

	List<DropDownRes> currentInsurer(LovDropDownReq req);

	List<DropDownRes> lineOfBusiness(LovDropDownReq req);

	List<DropDownRes> product(LovDropDownReq req);

	List<DropDownRes> businessType(LovDropDownReq req);

	List<DropDownRes> probabilityOfSuccess(LovDropDownReq req);

	SuccessRes saveLeadDetails(EserviceLeadSaveReq req);

	List<GetLeadDetailsRes> getLeadDetails(GetCustomerDetailsReq req);

}
