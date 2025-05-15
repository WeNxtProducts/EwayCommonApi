package com.maan.eway.update;

import com.maan.eway.common.req.EserviceCustomerSaveReq;
import com.maan.eway.res.SuccessRes;

public interface UpdateCustomerService {

	SuccessRes updateCustomerDetails(EserviceCustomerSaveReq req);

}
