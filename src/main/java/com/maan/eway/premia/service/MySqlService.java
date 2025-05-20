package com.maan.eway.premia.service;

import java.util.List;

import com.maan.eway.bean.PremiaConfigMaster;

public interface MySqlService {
	
	public boolean pushMySql(List<PremiaConfigMaster> configMasterList, List<String> params, String quoteNo);

}
