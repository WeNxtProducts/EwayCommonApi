package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.bean.ListItemValue;
import com.maan.eway.common.req.SearchEservieMotorDetailsViewRatingRes;
import com.maan.eway.common.req.SearchReq;
import com.maan.eway.common.res.AdminViewQuoteRes;
import com.maan.eway.common.res.DocumentRes;
import com.maan.eway.common.res.SearchCustomerDetailsRes;
import com.maan.eway.common.res.SearchPaymentInfoRes;
import com.maan.eway.common.res.SearchPremiumDetailsRes;
import com.maan.eway.common.res.SearchROPDetailsRes;
import com.maan.eway.common.res.SearchROPVehicleDetailsRes;
import com.maan.eway.common.res.SearchRes;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.res.DropDownRes;

public interface SearchService {

	List<SearchRes> adminSearchOrderByEntryDate(SearchReq req);

	AdminViewQuoteRes adminViewQuoteDetails(SearchReq req);

	List<DropDownRes> searchDropdown(CopyQuoteDropDownReq req);

	List<SearchCustomerDetailsRes> adminCustomerSearch(SearchReq req);

	List<SearchEservieMotorDetailsViewRatingRes> adminViewRatingDetails(SearchReq req);

	SearchPremiumDetailsRes adminPremiumSearch(SearchReq req);

	SearchROPDetailsRes adminROPDriverSearch(SearchReq req);

	SearchROPVehicleDetailsRes adminROPVehicleSearch(SearchReq req);

	List<SearchPaymentInfoRes> viewPaymentInfo(SearchReq req);

	List<DocumentRes> viewDocumentDetails(SearchReq req);





}
