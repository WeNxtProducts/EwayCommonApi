package com.maan.eway.payment.process;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.payment.process.req.SavePaymentProcessReq;

public interface PaymentProcessService {

	CommonRes savePaymentProcess(SavePaymentProcessReq req);

}
