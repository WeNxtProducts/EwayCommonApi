package com.maan.eway.common.service;

import java.util.List;

import javax.persistence.Tuple;

import com.maan.eway.common.req.SearchReq;

public interface TravelSearchService {
	
List<Tuple> searchTravel(SearchReq req, List<String> branches);
	
	List<Tuple> searchTravelDetails(String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches,String productId) ;

}
