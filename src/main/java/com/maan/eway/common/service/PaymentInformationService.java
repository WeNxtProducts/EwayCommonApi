package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.common.req.PaymentInformationGetReq;
import com.maan.eway.common.res.PaymentInformationGetRes;

public interface PaymentInformationService {
	

	List<PaymentInformationGetRes> getByPaymentInformationId(PaymentInformationGetReq req);
}
