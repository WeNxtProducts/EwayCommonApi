package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PaymentInfoId;
import com.maan.eway.common.req.PaymentInformationGetReq;
import com.maan.eway.common.res.PaymentInformationGetRes;

public interface PaymentInformationRepository  extends JpaRepository<PaymentInfo,PaymentInfoId> , JpaSpecificationExecutor<PaymentInfo> {

	List<PaymentInfo> findByQuoteNoAndProductId(String quoteNo,String productId);

	List<PaymentInfo> findByQuoteNo(String quoteNo);

	List<PaymentInfo> findByProductId(String productId);

//	List<PaymentInfo> findByRequestReferenceNo(String requestReferenceNo);

}
