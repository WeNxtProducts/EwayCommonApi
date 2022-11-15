package com.maan.eway.master.dropdown.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.master.dropdown.req.ExchangeChangeStatusReq;
import com.maan.eway.master.dropdown.req.ExchangeMasterGetReq;
import com.maan.eway.master.dropdown.req.ExchangeMasterGetallReq;
import com.maan.eway.master.dropdown.req.ExchangeMasterSaveReq;
import com.maan.eway.master.dropdown.res.ExchangeMasterGetRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

public interface ExchangeMasterService {
	

	List<DropDownRes> getExchangeMasterDropdown();

	/*List<Error> validateInsertExchangeMaster(ExchangeMasterSaveReq req);

	SuccessRes insertExchangeMaster(ExchangeMasterSaveReq req);

	ExchangeMasterGetRes getExchangeMaster(ExchangeMasterGetReq req);

	List<ExchangeMasterGetRes> getallExchangeMaster(ExchangeMasterGetallReq req);

	List<ExchangeMasterGetRes> getActiveExchange(ExchangeMasterGetallReq req);


	SuccessRes changeStatusOfExchange(ExchangeChangeStatusReq req);
*/
}
