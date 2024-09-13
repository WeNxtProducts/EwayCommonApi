package com.maan.eway.renewal.service;

import java.util.List;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.renewal.req.RenewDataRequest;
import com.maan.eway.renewal.req.RenewalCopyQuoteReq;
import com.maan.eway.res.CopyQuoteSuccessRes;
import com.maan.eway.renewal.req.PullrenewalReq;

public interface RenewalService {

	CommonRes pullrenewal(PullrenewalReq request);

	boolean getDbStatus();

	List<RenewDataRequest> getRenewPolicyList(String tranId);

	void sendSmsEmail(RenewDataRequest res);

	List<RenewDataRequest> getNotificationRequestList();

	void InsertNotificationSmsNext(List<RenewDataRequest> list);

	void startExpiredPolicyUpdateData();

	CopyQuoteSuccessRes renewalCopyQuote(RenewalCopyQuoteReq request);

}
