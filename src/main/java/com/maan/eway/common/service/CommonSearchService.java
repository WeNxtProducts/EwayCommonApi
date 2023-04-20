package com.maan.eway.common.service;

import java.util.List;

import javax.persistence.Tuple;

import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.SearchReq;
import com.maan.eway.res.CopyQuoteSuccessRes;

public interface CommonSearchService {

//	List<Tuple> searchCommon(CopyQuoteReq req, List<String> branches);
// List<Tuple> commonDetails(String searchKey, String searchValue, String companyId, String loginId,
//			String userType, List<String> branches) ;
	
	List<Tuple> searchCommon(SearchReq req, List<String> branches);
	
	List<Tuple> commonDetails(String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches,String productId) ;


}
