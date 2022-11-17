package com.maan.eway.common.service;

import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.res.CommonRes;

public interface QuoteThreadService {

	CommonRes call_OT_Insert(NewQuoteReq req);

}
