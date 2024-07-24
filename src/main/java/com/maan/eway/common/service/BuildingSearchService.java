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



public interface BuildingSearchService {

	List<Tuple> searchBuilding(SearchReq req, List<String> branches);
	
	List<Tuple> searchBuildingDetails(SearchReq req,String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches,String productId) ;

	List<ListItemValue> searchDropdownBuilding(CopyQuoteDropDownReq req);

	AdminViewQuoteRes getBuildingProductDetails(SearchReq req);

	List<SearchEservieMotorDetailsViewRatingRes> buildingRating();

	List<SearchCustomerDetailsRes> buildingCustSearch(SearchReq req);

	ViewQuoteDetailsRes viewQuoteBuilding(ViewQuoteDetailsReq req);

	
}
