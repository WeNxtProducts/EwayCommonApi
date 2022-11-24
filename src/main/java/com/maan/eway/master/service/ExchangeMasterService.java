package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.res.DropDownRes;

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
