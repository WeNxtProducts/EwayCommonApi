package com.maan.eway.common.service;

import java.util.List;

import javax.persistence.Tuple;

import com.maan.eway.bean.ListItemValue;
import com.maan.eway.common.req.SearchEservieMotorDetailsViewRatingRes;
import com.maan.eway.common.req.SearchReq;
import com.maan.eway.common.res.AdminViewQuoteRes;
import com.maan.eway.master.req.CopyQuoteDropDownReq;

public interface CommonSearchService {

//	List<Tuple> searchCommon(CopyQuoteReq req, List<String> branches);
// List<Tuple> commonDetails(String searchKey, String searchValue, String companyId, String loginId,
//			String userType, List<String> branches) ;
	
	List<Tuple> searchCommon(SearchReq req, List<String> branches);
	
	List<Tuple> commonDetails(String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches,String productId) ;

	List<ListItemValue> searchDropdownCommon(CopyQuoteDropDownReq req);

	AdminViewQuoteRes getCommonProductDetails(SearchReq req);

	List<SearchEservieMotorDetailsViewRatingRes> commonRating(SearchReq req);


}
