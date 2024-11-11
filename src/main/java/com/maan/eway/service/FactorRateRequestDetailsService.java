/*
*  Copyright (c) 2019. All right reserved
* Created on 2022-11-08 ( Date ISO 2022-11-08 - Time 16:28:23 )
* Generated by Telosys Tools Generator ( version 3.3.0 )
*/
package com.maan.eway.service;
import java.util.List;

import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.common.req.EservieMotorDetailsViewRes;
import com.maan.eway.common.req.FactorRateDetailsList;
import com.maan.eway.common.req.UpdateFactorRateReq;
import com.maan.eway.common.req.ViewPolicyCalc;
import com.maan.eway.common.res.UpdateCoverRes;
import com.maan.eway.error.Error;
import com.maan.eway.req.FactorFdCalcViewReq;
import com.maan.eway.req.FactorRateDetailsGetReq;
import com.maan.eway.res.SuccessRes;
/**
* <h2>FactorRateRequestDetailsServiceimpl</h2>
*/
public interface FactorRateRequestDetailsService  {

FactorRateRequestDetails create(FactorRateRequestDetails d);
FactorRateRequestDetails update(FactorRateRequestDetails d);
//FactorRateRequestDetails getOne(long id) ;
 List<FactorRateRequestDetails> getAll();
long getTotal();
//boolean delete(long id);
List<EservieMotorDetailsViewRes>  getFactorRateRequestDetails(FactorRateDetailsGetReq req,String tokens);
SuccessRes saveFactorRateRequestDetails(EserviceMotorDetailsSaveRes req);
List<Error> validateFoctorPremiumDetails(UpdateFactorRateReq req);
UpdateCoverRes updateFactorRatePremiumDetails(UpdateFactorRateReq req);
List<Error> validateFactorIsSelectedDetails(UpdateFactorRateReq req);
UpdateCoverRes updateFactorIsSelectedDetails(UpdateFactorRateReq req);
FactorRateDetailsList getFactorRateFdDetailsList(FactorFdCalcViewReq req);
ViewPolicyCalc getViewPolicyCalc(FactorRateDetailsGetReq req, String string);
List<Error> validatePolicyCalcRate(UpdateFactorRateReq req);
UpdateCoverRes updatePolicyCalcRate(UpdateFactorRateReq req);


}
