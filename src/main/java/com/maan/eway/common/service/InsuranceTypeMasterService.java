package com.maan.eway.common.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.maan.eway.common.req.GetProductMasterReq;
import com.maan.eway.common.req.ProductStructureMasterReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.ProductStructureMasterRes;
import com.maan.eway.error.Error;


public interface InsuranceTypeMasterService {

	
	CommonRes saveproductMaster(ProductStructureMasterReq req);
	
	List<ProductStructureMasterReq> getAllProductStructureMaster(GetProductMasterReq req);
	
	CommonRes getInsuranceMaster(GetProductMasterReq req);
	
	List<ProductStructureMasterRes> getByIndustryTypeId(GetProductMasterReq req);
	
	CommonRes DeleteproductStructureMaster(GetProductMasterReq req);
	
	List<Error> validationInsuranceTypeMaster(ProductStructureMasterReq req);
	
}
