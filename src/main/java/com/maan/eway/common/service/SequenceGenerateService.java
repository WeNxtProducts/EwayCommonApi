package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.common.req.EserviceCustomerSaveReq;
import com.maan.eway.common.req.EserviceCustomerSearchVrtinReq;
import com.maan.eway.common.req.GetAllCustomerDetailsReq;
import com.maan.eway.common.req.GetCustomerDetailsReq;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.common.res.SequenceGenerateRes;
import com.maan.eway.error.Error;
import com.maan.eway.res.SuccessRes;

public interface SequenceGenerateService {

	SequenceGenerateRes generateSequence(SequenceGenerateReq req);

	

}
