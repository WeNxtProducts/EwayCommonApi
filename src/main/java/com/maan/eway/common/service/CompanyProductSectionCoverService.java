package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.bean.ClaimIntimation;
import com.maan.eway.common.req.ClaimIntimationGetAllREq;
import com.maan.eway.common.req.GetSectionReq;
import com.maan.eway.common.res.GetSectionRes;

public interface CompanyProductSectionCoverService {

	GetSectionRes getOptedAndUnoptedSection(GetSectionReq req);

	GetSectionRes getOptedAndUnoptedSectionCover(GetSectionReq req);
}
