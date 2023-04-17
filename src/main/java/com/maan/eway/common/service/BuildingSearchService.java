package com.maan.eway.common.service;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import com.maan.eway.common.req.SearchReq;



public interface BuildingSearchService {

	List<Tuple> searchBuilding(SearchReq req, List<String> branches);
	
	
}
