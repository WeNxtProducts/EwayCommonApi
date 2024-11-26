package com.maan.eway.renewal.service;

import java.util.List;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.renewal.req.PullrenewalReq;
import com.maan.eway.renewal.req.RenewDataRequest;
import com.maan.eway.renewal.req.RenewalCopyQuoteReq;
import com.maan.eway.renewal.req.RenewalPendingRequest;
import com.maan.eway.renewal.req.RenewalTransDetailReq;
import com.maan.eway.renewal.req.RenewalTransactionReq;
import com.maan.eway.res.CopyQuoteSuccessRes;


public interface RenewalService {

	CommonRes pullrenewal(PullrenewalReq request);

	boolean getDbStatus();

	List<RenewDataRequest> getRenewPolicyList(String tranId);

	void sendSmsEmail(RenewDataRequest res);

	List<RenewDataRequest> getNotificationRequestList();

	void InsertNotificationSmsNext(List<RenewDataRequest> list);

	void startExpiredPolicyUpdateData();

	CopyQuoteSuccessRes renewalCopyQuote(RenewalCopyQuoteReq request);

	CommonRes getRenewalPending(RenewalPendingRequest request);

	CommonRes getRenewalExpired(RenewalPendingRequest request);

	CommonRes getRenewalCompleted(RenewalPendingRequest request);

	CommonRes getRenewalTransaction(RenewalTransactionReq request);

	CommonRes getRenewalTransactionSuccess(RenewalTransDetailReq request);

	CommonRes getRenewalTransactionConverted(RenewalTransDetailReq request);

	CommonRes getRenewalTransactionPending(RenewalTransDetailReq request);

}
