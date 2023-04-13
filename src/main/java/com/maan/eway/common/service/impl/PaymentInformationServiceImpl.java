package com.maan.eway.common.service.impl;

import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.common.req.PaymentInformationGetReq;
import com.maan.eway.common.res.PaymentInformationGetRes;
import com.maan.eway.common.service.PaymentInformationService;
import com.maan.eway.master.service.impl.ClausesMasterServiceImpl;
import com.maan.eway.repository.NotifTemplateMasterRepository;
import com.maan.eway.repository.PaymentInfoRepository;

@Service
@Transactional
public class PaymentInformationServiceImpl implements PaymentInformationService {
	
	
	@PersistenceContext
	private EntityManager em;
	@Autowired
	 private PaymentInfoRepository paymentrepo;
	
	@Autowired
	 private NotifTemplateMasterRepository notifRepo;
	

	
	
	
	
	private Logger log = LogManager.getLogger(ClausesMasterServiceImpl.class);
//	
//	private String motorProductId;
//		@Value(value = "${motor.productId}")
//
//	@Value(value = "${travel.productId}")
//	private String travelProductId;
//	
//	@Value(value = "${building.productId}")
//	private String buildingProductId;
//	
//	@Value(value = "${sme.productId}")
//	private String smeProductId;

@Override	
public List<PaymentInformationGetRes> getByPaymentInformationId(PaymentInformationGetReq req) {
	PaymentInformationGetRes paymentgetres=new PaymentInformationGetRes();

	List<PaymentInformationGetRes> paylist=new ArrayList<PaymentInformationGetRes> ();

 List<PaymentInfo> getList=paymentrepo.findByQuoteNo(req.getQuoteNo());
List<PaymentInfo> getListProductId=paymentrepo.findByProductId(req.getProductId());
//List<PaymentInfo> getListReferenceNumber=paymentrepo.findByRequestReferenceNo(req.getRequestReferenceNo());

String ProductId=req.getProductId().toString();




if(StringUtils.isNotBlank(req.getQuoteNo())){
//	  itemType = "COPY_dOCUMENT_BY_QUOTENO";
	for(PaymentInfo pi:getList) {
// paymentgetres = new DozerBeanMapper().map(pi, PaymentInformationGetRes.class);
// List<PaymentInfo> qno=paymentrepo.findByQuoteNo(req.getQuoteNo());
 for(PaymentInfo pii:getList) {
	 paymentgetres=new DozerBeanMapper().map(pii, PaymentInformationGetRes.class);
	 paylist.add(paymentgetres);
 }

  }
	
}
else if(StringUtils.isNotEmpty(ProductId)) {
//	for(PaymentInfo pi:getListProductId) {
		
//		List<PaymentInfo> prodId=paymentrepo.findByProductId(req.getProductId());
		
		for(PaymentInfo pii:getListProductId) {
			
			paymentgetres=new DozerBeanMapper().map(pii, PaymentInformationGetRes.class);
			paylist.add(paymentgetres);
		}


//}
}



//else if(StringUtils.isNotBlank(req.getProductId()))
//{
////	  itemType = "COPY_DOCUMENT_BY_PRODUCTID"; 
//for(PaymentInfo pi: getListProductId)
//{
//PaymentInformationGetRes paymentgetres=new PaymentInformationGetRes();
// paymentgetres=new DozerBeanMapper().map(pi,PaymentInformationGetRes.class);
//paylist.add(paymentgetres); 
//}	 	  
//}
//else if(StringUtils.isNotBlank(req.getRequestReferenceNo()))
//{
////	  itemType = "COPY_DOCUMENT_BY_REQUESTREFERECENUMBER";	  
//for(PaymentInfo pi: getListReferenceNumber)
//{
//PaymentInformationGetRes paymentgetres=new PaymentInformationGetRes();
//paymentgetres=new DozerBeanMapper().map(pi,PaymentInformationGetRes.class);
//paylist.add(paymentgetres);
//}	 


return paylist;






		
		
		
		

	}



}
