package com.maan.eway.renewal.service;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.renewal.req.pullrenewalReq;

public interface RenewalService {

	CommonRes pullrenewal(pullrenewalReq request);

}
