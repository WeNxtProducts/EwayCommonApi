package com.maan.eway.admin.service;

import java.util.List;

import com.maan.eway.admin.req.AttachIssuerReferalReq;
import com.maan.eway.admin.req.IssuerCompanyReferalGetReq;
import com.maan.eway.admin.req.IssuerReferalGetReq;
import com.maan.eway.admin.res.IssuerReferalCompanyGetRes;
import com.maan.eway.admin.res.IssuerReferalCompniesRes;
import com.maan.eway.admin.res.LoginCreationRes;

public interface LoginReferalService {

	LoginCreationRes attachIssuerReferal(AttachIssuerReferalReq req);

	List<IssuerReferalCompniesRes> getIssuerReferals(IssuerReferalGetReq req);

	List<IssuerReferalCompanyGetRes> getIssuerCompanyReferal(IssuerCompanyReferalGetReq req);
}
