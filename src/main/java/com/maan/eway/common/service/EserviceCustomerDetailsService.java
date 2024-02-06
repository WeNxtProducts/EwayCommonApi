package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.common.req.EserviceCustomerSaveReq;
import com.maan.eway.common.req.EserviceCustomerSearchVrtinReq;
import com.maan.eway.common.req.GetAllCustomerDetailsReq;
import com.maan.eway.common.req.GetByCustomerRefNoReq;
import com.maan.eway.common.req.GetCustomerDetailsReq;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.error.Error;
import com.maan.eway.res.SuccessRes;

public interface EserviceCustomerDetailsService {

	List<String> validateCustomerDetails(EserviceCustomerSaveReq req);

	SuccessRes saveCustomerDetails(EserviceCustomerSaveReq req);

	CustomerDetailsGetRes getCustomerDetails(GetCustomerDetailsReq req);

	List<CustomerDetailsGetRes> getallCustomerDetails(GetAllCustomerDetailsReq req);

	List<CustomerDetailsGetRes>  getbyvrtinno(EserviceCustomerSearchVrtinReq req);

	List<CustomerDetailsGetRes> getActiveCustomerDetails(GetAllCustomerDetailsReq req);

	List<String> validateCustomer(EserviceCustomerSaveReq req);

	SuccessRes updatebycustrefno(GetByCustomerRefNoReq req);

}
