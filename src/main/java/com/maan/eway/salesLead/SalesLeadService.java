package com.maan.eway.salesLead;

import com.maan.eway.common.res.CommonRes;

public interface SalesLeadService {

	CommonRes insertSales(InsertSalesReq req);

	CommonRes getAllSales();

	CommonRes insertEnquiry(EnquiryDetailsDTO req);

	CommonRes getAllEnquiry();

}
