package com.maan.eway.salesLead;

import com.maan.eway.common.res.CommonRes;

public interface SalesLeadService {

	CommonRes insertSales(InsertSalesReq req);

	CommonRes getSalesLead(String leadId);

	CommonRes insertEnquiry(EnquiryDetailsDTO req);

	CommonRes getEnquirys(String enquiryId);

}
