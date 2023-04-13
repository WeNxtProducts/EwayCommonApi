package com.maan.eway.common.service;

import java.util.List;
import com.maan.eway.error.Error;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.DocumentReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.IssuerQuoteReq;
import com.maan.eway.common.req.PaymentInformationGetReq;
import com.maan.eway.common.req.SearchEservieMotorDetailsViewRatingRes;
import com.maan.eway.common.req.SearchReq;
import com.maan.eway.common.req.UpdateLapsedQuoteReq;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.AdminViewQuoteRes;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DocumentRes;
import com.maan.eway.common.res.EserviceCustomerDetailsRes;
import com.maan.eway.common.res.GetAllMotorDetailsRes;
import com.maan.eway.common.res.SearchPaymentInfoRes;
import com.maan.eway.common.res.UpdateLapsedQuoteRes;

import com.maan.eway.common.res.PortfolioCustomerDetailsRes;
import com.maan.eway.common.res.SearchCustomerDetailsRes;
import com.maan.eway.common.res.SearchPremiumDetailsRes;
import com.maan.eway.common.res.SearchROPDetailsRes;
import com.maan.eway.common.res.SearchROPVehicleDetailsRes;
import com.maan.eway.common.res.SearchRes;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.res.CopyQuoteSuccessRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

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
