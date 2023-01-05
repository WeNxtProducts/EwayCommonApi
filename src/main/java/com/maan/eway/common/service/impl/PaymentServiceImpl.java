package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.common.req.MakePaymentSaveReq;
import com.maan.eway.common.req.MakePaymentUpdateReq;
import com.maan.eway.common.req.PaymentDetailsGetReq;
import com.maan.eway.common.req.PaymentDetailsGetallReq;
import com.maan.eway.common.res.PaymentDetailGetRes;
import com.maan.eway.common.service.PaymentService;
import com.maan.eway.error.Error;
import com.maan.eway.master.service.impl.ClausesMasterServiceImpl;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PaymentInfoRepository;
import com.maan.eway.res.SuccessRes;

@Service
@Transactional

public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private PaymentDetailRepository paymentdetailrepo;
	
	@Autowired
	private PaymentInfoRepository paymentinforepo;
	
	@Autowired
	private HomePositionMasterRepository homerepo;
	
	@Autowired
	private ListItemValueRepository listrepo;
	
	
	private Logger log = LogManager.getLogger(ClausesMasterServiceImpl.class);

	
	@Override
	public List<Error> validatemakepayment(MakePaymentSaveReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized SuccessRes savemakepayment(MakePaymentSaveReq req) {
		// TODO Auto-generated method stub
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozermappper = new DozerBeanMapper();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddmmssSSS");
		PaymentDetail paymentdetail = new PaymentDetail();
		PaymentInfo paymentinfo = new PaymentInfo();
		String refNo = "";
		
		try {
			Date today = new Date();
			
			//List Item 
			ListItemValue paymenttype = listrepo.findByItemTypeAndItemCode("PAYMENT_TYPE",req.getPaymentTypeId());
			//Reference No Generation
			Random rand = new Random();
			int random = rand.nextInt(90) + 10;
			refNo = "EWAY" +"-" + idf.format(new Date()) + random ; 
			
			//Payment Id Count
			Long count  = paymentdetailrepo.count();
			Long paymentid = 200001+count;
			
			//Find data from home Position Master
			HomePositionMaster data = homerepo.findByQuoteNo(req.getQuoteNo());
			
			
			//Payment Detail Save
			paymentdetail=dozermappper.map(data, PaymentDetail.class);
			paymentdetail.setPaymentStatus("PENDING");
			paymentdetail.setPaymentId(Double.valueOf(paymentid));
			paymentdetail.setPaymentReferenceNo(refNo);
			paymentdetail.setPaymentTypeId(Integer.valueOf(req.getPaymentTypeId()));
			paymentdetail.setPaymentTypeDesc(paymenttype.getItemValue());
			paymentdetail.setEntryDate(today);
			paymentdetail.setPremium(data.getPremiumLc());
			paymentdetail.setCustomerid(data.getCustomerId());
			paymentdetail.setOthProductId(data.getProductId().toString());
			paymentdetail.setChequeNo(data.getChqInvNo());

			paymentdetailrepo.save(paymentdetail);

			//Payment Info Save
			paymentinfo = dozermappper.map(data, PaymentInfo.class);			
			paymentinfo.setPaymentStatus("PENDING");;
			paymentinfo.setPaymentId(Double.valueOf(paymentid));
			paymentinfo.setPaymentReferenceNo(refNo);
			paymentinfo.setEntryDate(today);
			paymentinfo.setPremium(new BigDecimal(data.getPremiumLc()));
			paymentinfo.setOthProductId(data.getProductId().toString());
			paymentinfo.setOthPaymentMode(paymenttype.getItemValue());
			
			
			paymentinforepo.save(paymentinfo);
			
			res.setSuccessId(paymentid.toString());
			res.setResponse("Saved Successful");
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public SuccessRes updatemakepayment(MakePaymentUpdateReq req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozermappper = new DozerBeanMapper();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddmmssSSS");
		PaymentDetail paymentdetail = new PaymentDetail();
		PaymentInfo paymentinfo = new PaymentInfo();
		String refNo = "";

		try {
			Date today = new Date();

			
			//Reference No Generation
			Random rand = new Random();
			int random = rand.nextInt(90) + 10;
			refNo = "EWAY" +"-" + idf.format(new Date()) + random ; 
			
			//Payment Id Count
			Long count  = paymentdetailrepo.count();
			Long paymentid = 200001+count;
			
			//Find data from home Position Master
			List<PaymentDetail> data = paymentdetailrepo.findByQuoteNoOrderByEntryDateDesc(req.getQuoteNo());

			//For Status Expired
			if(req.getStatus().equalsIgnoreCase("EXPIRED"))
			{
			//Payment Detail Save
			paymentdetail=dozermappper.map(data.get(0), PaymentDetail.class);
			paymentdetail.setPaymentStatus(req.getStatus());
			paymentdetail.setPaymentId(data.get(0).getPaymentId());
			paymentdetail.setPaymentReferenceNo(refNo);
			paymentdetail.setEntryDate(data.get(0).getEntryDate());
			paymentdetail.setUpdatedDate(today);

			paymentdetailrepo.save(paymentdetail);

			//Payment Info Save
			paymentinfo = dozermappper.map(data.get(0), PaymentInfo.class);			
			paymentinfo.setPaymentStatus(req.getStatus());
			paymentinfo.setPaymentId(data.get(0).getPaymentId());
			paymentinfo.setPaymentReferenceNo(refNo);
			paymentinfo.setEntryDate(data.get(0).getEntryDate());
			paymentinfo.setUpdatedDate(today);
			paymentinfo.setOthPaymentMode(data.get(0).getPaymentTypeDesc());
			
			paymentinforepo.save(paymentinfo);
			}

			
			//For Status Rejected
			else if(req.getStatus().equalsIgnoreCase("REJECTED"))
			{
			//Payment Detail Save
			paymentdetail=dozermappper.map(data.get(0), PaymentDetail.class);
			paymentdetail.setPaymentStatus(req.getStatus());
			paymentdetail.setPaymentId(data.get(0).getPaymentId());
			paymentdetail.setPaymentReferenceNo(refNo);
			paymentdetail.setEntryDate(data.get(0).getEntryDate());
			paymentdetail.setUpdatedDate(today);

			paymentdetailrepo.save(paymentdetail);

			//Payment Info Save
			paymentinfo = dozermappper.map(data.get(0), PaymentInfo.class);			
			paymentinfo.setPaymentStatus(req.getStatus());
			paymentinfo.setPaymentId(data.get(0).getPaymentId());
			paymentinfo.setPaymentReferenceNo(refNo);
			paymentinfo.setEntryDate(data.get(0).getEntryDate());
			paymentinfo.setUpdatedDate(today);
			paymentinfo.setOthPaymentMode(data.get(0).getPaymentTypeDesc());

			paymentinforepo.save(paymentinfo);
			}

			
			//For Status Accept
			if(req.getStatus().equalsIgnoreCase("ACCEPTED"))
			{
			List<PaymentDetail> datas = paymentdetailrepo.findByQuoteNoAndPaymentStatusOrderByEntryDateDesc(req.getQuoteNo(),"PENDING");
					
			if(datas.size()>0) {
			//Payment Detail Save
			paymentdetail=dozermappper.map(datas.get(0), PaymentDetail.class);
			paymentdetail.setPaymentStatus(req.getStatus());
			paymentdetail.setPaymentId(datas.get(0).getPaymentId());
			paymentdetail.setPaymentReferenceNo(datas.get(0).getPaymentReferenceNo());
			paymentdetail.setEntryDate(datas.get(0).getEntryDate());
			paymentdetail.setUpdatedDate(today);

			paymentdetailrepo.save(paymentdetail);

			//Payment Info Save
			paymentinfo = dozermappper.map(datas.get(0), PaymentInfo.class);			
			paymentinfo.setPaymentStatus(req.getStatus());
			paymentinfo.setPaymentId(datas.get(0).getPaymentId());
			paymentinfo.setPaymentReferenceNo(datas.get(0).getPaymentReferenceNo());
			paymentinfo.setEntryDate(datas.get(0).getEntryDate());
			paymentinfo.setUpdatedDate(today);
			paymentinfo.setOthPaymentMode(datas.get(0).getPaymentTypeDesc());
			
			paymentinforepo.save(paymentinfo);
			}
			
			}
			else {
				return res;
			}
			
			res.setSuccessId(req.getQuoteNo());
			res.setResponse("Updated Successful");
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public PaymentDetailGetRes getpaymentdetails(PaymentDetailsGetReq req) {
		// TODO Auto-generated method stub
		DozerBeanMapper dozermappper = new DozerBeanMapper();
		PaymentDetailGetRes res = new PaymentDetailGetRes();
		try {
		
			PaymentDetail data = paymentdetailrepo.findByQuoteNoAndPaymentIdAndPaymentReferenceNo(req.getQuoteNo(),Double.valueOf(req.getPaymentId()),req.getPaymentReferenceNo());
			
			res = dozermappper.map(data, PaymentDetailGetRes.class);
			res.setPaymentId(String.valueOf(Math.round(data.getPaymentId())));				

		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return res;
	}
	

	@Override
	public List<PaymentDetailGetRes> getallpaymentdetails(PaymentDetailsGetallReq req) {
		// TODO Auto-generated method stub
		DozerBeanMapper dozermappper = new DozerBeanMapper();
		List<PaymentDetailGetRes> resList = new ArrayList<PaymentDetailGetRes>();
		try {
			List<PaymentDetail> datas = paymentdetailrepo.findByQuoteNo(req.getQuoteNo());
			for(PaymentDetail data : datas) {
				PaymentDetailGetRes res = new PaymentDetailGetRes();
				res = dozermappper.map(data, PaymentDetailGetRes.class);
				res.setPaymentId(String.valueOf(Math.round(data.getPaymentId())));				
				resList.add(res);
				}
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return resList;
	}
	
	
	
}