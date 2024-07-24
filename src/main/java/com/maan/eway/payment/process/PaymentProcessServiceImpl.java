package com.maan.eway.payment.process;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.PaymentProcessDetail;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.payment.process.Repository.PaymentProcessDetailRepository;
import com.maan.eway.payment.process.req.SavePaymentProcessReq;

@Service
public class PaymentProcessServiceImpl implements PaymentProcessService {
	
	@Autowired
	private PaymentProcessDetailRepository detailRepository;
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public CommonRes savePaymentProcess(SavePaymentProcessReq req) {
		CommonRes res = new CommonRes();
		try {
			Timestamp today = Timestamp.from(Instant.now());
			Optional<PaymentProcessDetail> processDetail = detailRepository.findByQuoteNoAndPaymentId(req.getQuoteNo(),req.getPaymentId());
			if(processDetail.isPresent()) {
				PaymentProcessDetail p = processDetail.get();
				if("ccpending".equalsIgnoreCase(req.getType())) {
					if("Y".equalsIgnoreCase(req.getStatus())) {
						p.setCreditControllerStatus("CCA");
						p.setCcUpdatedDate(today);
						p.setSurveyorStatus("SP");
						p.setSurveyorCode(req.getAgencyCode());
						p.setCcRemarks(req.getRemarks());
						p.setType("sspending");
					}else if("P".equalsIgnoreCase(req.getStatus())){
						p.setCreditControllerStatus("CCP");
						p.setCcUpdatedDate(today);
						p.setCcRemarks(req.getRemarks());
					}else {
						p.setCreditControllerStatus("CCR");
						p.setCcUpdatedDate(today);
						p.setCcRemarks(req.getRemarks());
					}
				}else if("sspending".equalsIgnoreCase(req.getType())) {
					if("Y".equalsIgnoreCase(req.getStatus())) {
						p.setSurveyorStatus("SA");
						p.setSurveyorUpdatedDate(today); // here call policy generation block
						p.setUnderWritterStatus("UWP");
						p.setUnderWritterCode(req.getAgencyCode());
						p.setSsRemarks(req.getRemarks());
						p.setType("uwpending");
					}else if("P".equalsIgnoreCase(req.getStatus())) {
						p.setSurveyorStatus("SP");
						p.setSurveyorUpdatedDate(today);
						p.setSsRemarks(req.getRemarks());
					}else if("R".equalsIgnoreCase(req.getStatus())) {
						p.setSurveyorStatus("SR");
						p.setSurveyorUpdatedDate(today);
						p.setCreditControllerStatus("CCP");
						p.setSsRemarks(req.getRemarks());
						p.setType("ccpending");
					}
				}else if("uwpending".equalsIgnoreCase(req.getType())) {
					if("Y".equalsIgnoreCase(req.getStatus())) {
						p.setUnderWritterStatus("UWA");
						p.setUnderwritterUpdatedDate(today);// update status on home position master
						p.setUwRemarks(req.getRemarks());
						p.setType("completed");
					}else if("P".equalsIgnoreCase(req.getStatus())) {
						p.setUnderWritterStatus("UWP");
						p.setUnderwritterUpdatedDate(today);
						p.setUwRemarks(req.getRemarks());
					}else if("R".equalsIgnoreCase(req.getStatus())) {
						p.setUnderWritterStatus("UWR");
						p.setUnderwritterUpdatedDate(today);
						p.setSurveyorStatus("SP");
						p.setUwRemarks(req.getRemarks());
						p.setType("sspending");
					}
				}
				detailRepository.save(p);
				res.setCommonResponse(p);
				res.setMessage("SUCCESS");
				res.setIsError(false);
				return res;
			}else {
				boolean paymentCondition = Arrays.asList("101","102").contains(req.getPaymentId());
				PaymentProcessDetail p = PaymentProcessDetail.builder()
						.quoteNo(req.getQuoteNo())
						.paymentId(req.getPaymentId())
						.paymentType(getPaymentDesc(req.getPaymentId(),req.getInsuranceId(),req.getBranchCode()))
						.entryDate(today)
						.creditControllerStatus(paymentCondition?"CCA":"CCP")
						.ccUpdatedDate(paymentCondition?today:null)
						.surveyorStatus(paymentCondition?"SP":null)
						.companyId(req.getInsuranceId())
						.type(paymentCondition?"sspending":"ccpending")
						.build();
				detailRepository.save(p);
				res.setCommonResponse(p);
				res.setMessage("SUCCESS");
				res.setIsError(false);
				return res;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getPaymentDesc(String paymentId,String insuranceId,String branchCode) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ListItemValue> lRoot = cq.from(ListItemValue.class);
		
		Subquery<Integer> lAmd = cq.subquery(Integer.class);
		Root<ListItemValue> lAmdRoot = lAmd.from(ListItemValue.class);
		lAmd.select(cb.max(lAmdRoot.get("amendId"))).where(cb.equal(lAmdRoot.get("companyId"),lRoot.get("companyId")),
				cb.equal(lAmdRoot.get("status"),lRoot.get("status")),cb.equal(lAmdRoot.get("itemType"),lRoot.get("itemType")),
				cb.equal(lAmdRoot.get("branchCode"),lRoot.get("branchCode")));
		
		cq.select(lRoot.get("itemValue")).where(cb.equal(lRoot.get("companyId"), insuranceId),
				cb.equal(lRoot.get("status"), "Y"),cb.equal(lRoot.get("itemType"), "PAYMENT_MODE"),
				cb.equal(lRoot.get("branchCode"), StringUtils.isBlank(branchCode)?"99999":branchCode),
				cb.equal(lRoot.get("amendId"), lAmd),cb.equal(lRoot.get("itemCode"), paymentId));
		
		return em.createQuery(cq).getSingleResult();
	}

}
