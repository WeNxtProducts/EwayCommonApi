package com.maan.eway.common.service;

import java.util.List;

import jakarta.persistence.Tuple;

import com.maan.eway.bean.ListItemValue;
import com.maan.eway.common.req.SearchEservieMotorDetailsViewRatingRes;
import com.maan.eway.common.req.SearchReq;
import com.maan.eway.common.req.ViewQuoteDetailsReq;
import com.maan.eway.common.res.AdminViewQuoteRes;
import com.maan.eway.common.res.SearchCustomerDetailsRes;
import com.maan.eway.common.res.ViewQuoteDetailsRes;
import com.maan.eway.master.req.CopyQuoteDropDownReq;

public interface MotorSearchService {
	
	

	List<Tuple> adminSearchMotorQuote(SearchReq req, List<String> branches);
	List<ListItemValue> searchDropdownMotor(CopyQuoteDropDownReq req);
	AdminViewQuoteRes getMotorProductDetails(SearchReq req);
	List<SearchEservieMotorDetailsViewRatingRes> motorRating(SearchReq req);
	List<SearchCustomerDetailsRes> motorCustSearch(SearchReq req);
	ViewQuoteDetailsRes viewQuoteMotor(ViewQuoteDetailsReq req);

	
}
