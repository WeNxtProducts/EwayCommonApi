package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.res.EserviceCustomerDetailsRes;

public interface GridService {

	List<EserviceCustomerDetailsRes> getallExistingQuoteDetails(ExistingQuoteReq req);

}
