package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.common.req.MakePaymentRes;
import com.maan.eway.common.req.MakePaymentSaveReq;
import com.maan.eway.common.req.MakePaymentUpdateReq;
import com.maan.eway.common.req.PaymentDetailsGetReq;
import com.maan.eway.common.req.PaymentDetailsGetallReq;
import com.maan.eway.common.req.PaymentDetailsSaveReq;
import com.maan.eway.common.req.PaymentDetailsSaveRes;
import com.maan.eway.common.req.PaymentInfoGetAllReq;
import com.maan.eway.common.req.PaymentInfoGetReq;
import com.maan.eway.common.res.PaymentDetailGetRes;
import com.maan.eway.common.res.PaymentInfoGetRes;
import com.maan.eway.error.Error;
import com.maan.eway.res.SuccessRes;

public interface PaymentService {

	List<Error> validatemakepayment(MakePaymentSaveReq req);

	MakePaymentRes savemakepayment(MakePaymentSaveReq req);

	SuccessRes updatemakepayment(MakePaymentUpdateReq req);

	PaymentDetailGetRes getpaymentdetails(PaymentDetailsGetReq req);

	List<PaymentDetailGetRes> getallpaymentdetails(PaymentDetailsGetallReq req);

	PaymentInfoGetRes getPaymentInfo(PaymentInfoGetReq req);

	List<PaymentInfoGetRes> viewPaymentInfo(PaymentInfoGetAllReq req);

	List<Error> validatePaymentInsert(PaymentDetailsSaveReq req);

	PaymentDetailsSaveRes savePaymentDetails(PaymentDetailsSaveReq req);

}
