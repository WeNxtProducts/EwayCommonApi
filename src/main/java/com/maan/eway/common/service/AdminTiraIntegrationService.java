package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.common.req.AdminTiraIntegrationGridReq;
import com.maan.eway.common.res.AdminTiraIntegrationGirdRes;
import com.maan.eway.common.res.EserviceCustomerDetailsRes;
import com.maan.eway.common.res.TiraRes;
import com.maan.eway.error.Error;

import com.maan.eway.res.SuccessRes;

public interface AdminTiraIntegrationService {

	List<AdminTiraIntegrationGirdRes> getallTiraSuccess(AdminTiraIntegrationGridReq req);

	List<AdminTiraIntegrationGirdRes> getallTiraFailure(AdminTiraIntegrationGridReq req);

	List<AdminTiraIntegrationGirdRes> getallTiraPending(AdminTiraIntegrationGridReq req);

	List<TiraRes> getallTiraDetails(AdminTiraIntegrationGridReq req);

	



}
