package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.master.req.GetOptedSectionAdditionalInfoReq;
import com.maan.eway.master.res.GetOptedSectionAdditionalInfoRes;

public interface ProductSectionAdditionalInfoMasterService {

	List<GetOptedSectionAdditionalInfoRes> getOptedSectionAdditionalInfo(GetOptedSectionAdditionalInfoReq req);


}
