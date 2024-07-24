package com.maan.eway.common.service;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Tuple;

import org.springframework.http.ResponseEntity;

import com.maan.eway.admin.res.ReferalCriteriaRes;
import com.maan.eway.admin.res.ReferalGridCriteriaRes;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.TermsAndConditionGetBySubIdReq;
import com.maan.eway.common.req.TermsAndConditionGetReq;
import com.maan.eway.common.req.TermsAndConditionInsertReq;
import com.maan.eway.common.req.TermsAndConditionReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.common.res.TermsAndConditionGetBySubIdRes;
import com.maan.eway.common.res.TermsAndConditionGetRes;
import com.maan.eway.common.res.TermsAndConditionRes;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.res.SuccessRes;

public interface TermsAndConditionService {

	TermsAndConditionRes viewTermsAndCondition(TermsAndConditionReq req);

	List<Error> validateTermsAndCondition(TermsAndConditionInsertReq req);

	SuccessRes insertTermsAndCondition(TermsAndConditionInsertReq req);

	TermsAndConditionGetRes getTermsAndCondition(TermsAndConditionGetReq req);

	TermsAndConditionGetBySubIdRes getTermsAndConditionSubId(TermsAndConditionGetBySubIdReq req);

	ResponseEntity<CommonRes> fetchTermsAndCondition(TermsAndConditionReq req);

	ResponseEntity<CommonRes> fetchSectionsBasedOnRisk(String requestReferenceNo, Integer riskId);
	
}