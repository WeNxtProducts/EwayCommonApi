package com.maan.eway.common.service;

import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.res.CommonRes;


public interface QuoteService {

	CommonRes generateNewQuote(NewQuoteReq req);

	ViewQuoteRes viewQuoteDetails(ViewQuoteReq req);

}
