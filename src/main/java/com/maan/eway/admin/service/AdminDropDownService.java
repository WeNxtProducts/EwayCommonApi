package com.maan.eway.admin.service;

import java.util.List;

import com.maan.eway.req.SubUserTypeReq;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SubUserTypeDropDownRes;

public interface AdminDropDownService {

	List<DropDownRes> getgender();

	List<DropDownRes> getUserType();

	List<SubUserTypeDropDownRes> getSubUserType(SubUserTypeReq req);

	List<DropDownRes> getConstMaterial();

	List<DropDownRes> getOutbuildingConst();

	List<DropDownRes> getAboutBuilding();

	List<DropDownRes> getStateExtent();

	List<DropDownRes> getContentName();

	List<DropDownRes> getPropertyName();
	
	List<DropDownRes> getProductIcons();

	List<DropDownRes> getCalcTypes();

	List<DropDownRes> getCoverageTypes();

	List<DropDownRes> getRangeParams();

	List<DropDownRes> getDiscreteParams();

	List<DropDownRes> getMobileCodes();

	List<DropDownRes> getBusinessType();


}
