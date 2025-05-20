package com.maan.eway.premia.service;

import java.util.List;

import com.maan.eway.bean.PremiaConfigMaster;

public interface OracleService {
	
	public boolean pushOracle(List<PremiaConfigMaster> configMasterList, List<String> params, String quoteNo);

}
