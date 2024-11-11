package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.admin.res.GetallPortfolioActiveRes;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.ExistingBrokerUserListReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.GetApproverListReq;
import com.maan.eway.common.req.GetExistingBrokerListReq;
import com.maan.eway.common.req.GetPaymentStatusReq;
import com.maan.eway.common.req.GetallPolicyReportsReq;
import com.maan.eway.common.req.GetallReferralPendingDetailsRes;
import com.maan.eway.common.req.IssuerQuoteReq;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.req.PortFolioDashBoardReq;
import com.maan.eway.common.req.PortFolioGridReq;
import com.maan.eway.common.req.RegSearchReq;
import com.maan.eway.common.req.RevertGridReq;
import com.maan.eway.common.req.SearchBrokerPolicyReq;
import com.maan.eway.common.req.UpdateLapsedQuoteReq;
import com.maan.eway.common.res.AdminPendingGridRes;
import com.maan.eway.common.res.GetAllMotorDetailsRes;
import com.maan.eway.common.res.GetApproverListRes;
import com.maan.eway.common.res.GetExistingBrokerListRes;
import com.maan.eway.common.res.GetPaymentStatusRes;
import com.maan.eway.common.res.GetRegNumberQuoteRes;
import com.maan.eway.common.res.GetallExistingRejectedLapsedRes;
import com.maan.eway.common.res.GetallPolicyReportsRes;
import com.maan.eway.common.res.GetallPortfolioPendingRes;
import com.maan.eway.common.res.GetallReferralApprovedDetailsRes;
import com.maan.eway.common.res.GetallReferralDetailsCommonRes;
import com.maan.eway.common.res.GetallReferralRejectedDetailsRes;
import com.maan.eway.common.res.PortFolioDashBoardRes;
import com.maan.eway.common.res.PortfolioCustomerDetailsRes;
import com.maan.eway.common.res.PortfolioGridRes;
import com.maan.eway.common.res.QuoteUpdateRes;
import com.maan.eway.common.res.RegNumberRes;
import com.maan.eway.common.res.RevertGridRes;
import com.maan.eway.common.res.UpdateLapsedQuoteRes;
import com.maan.eway.common.service.impl.PortFolioSearchGridRes;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.res.CopyQuoteSuccessRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.DropDownSourceRes;
import com.maan.eway.res.SuccessRes;

public interface NotificationThreadService {

	QuoteUpdateRes getUpdateReferral(NewQuoteReq req);

	

}
