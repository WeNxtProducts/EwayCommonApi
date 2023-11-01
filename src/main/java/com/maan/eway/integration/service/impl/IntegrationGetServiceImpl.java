package com.maan.eway.integration.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.bean.CreditLimitDetail;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotCommDiscountDetail;
import com.maan.eway.bean.MotDriverDetail;
import com.maan.eway.bean.OccupationMaster;
import com.maan.eway.bean.PgithPolRiskAddlInfo;
import com.maan.eway.bean.PremiaConfigDataMaster;
import com.maan.eway.bean.PremiaConfigMaster;
import com.maan.eway.bean.YiChargeDetail;
import com.maan.eway.bean.YiCoverDetail;
import com.maan.eway.bean.YiPolicyApproval;
import com.maan.eway.bean.YiPolicyDetail;
import com.maan.eway.bean.YiPremCal;
import com.maan.eway.bean.YiSectionDetail;
import com.maan.eway.bean.YiVatDetail;
import com.maan.eway.integration.req.PremiaGetReq;
import com.maan.eway.integration.req.PremiaRequest;
import com.maan.eway.integration.req.YiPolicyDetailReq;
import com.maan.eway.integration.res.CreditLimitDetailGetRes;
import com.maan.eway.integration.res.MotCommDiscountDetailGetRes;
import com.maan.eway.integration.res.MotDriverDetailGetRes;
import com.maan.eway.integration.res.PgithPolRiskAddlInfoGetRes;
import com.maan.eway.integration.res.PremiaResponse;
import com.maan.eway.integration.res.YiChargeDetailsGetRes;
import com.maan.eway.integration.res.YiCoverDetailsGetRes;
import com.maan.eway.integration.res.YiPolicyApprovalGetRes;
import com.maan.eway.integration.res.YiPolicyDetailsGetRes;
import com.maan.eway.integration.res.YiPremCalGetRes;
import com.maan.eway.integration.res.YiSectionDetailGetRes;
import com.maan.eway.integration.res.YiVatDetailGetRes;
import com.maan.eway.integration.service.FrameReqService;
import com.maan.eway.integration.service.IntegrationGetService;
import com.maan.eway.integration.service.IntegrationService;
import com.maan.eway.repository.CreditLimitDetailRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotDriverDetailRepository;
import com.maan.eway.repository.MotcommDiscountDetailRepository;
import com.maan.eway.repository.PgitPolRiskAddlInfoRepository;
import com.maan.eway.repository.PremiaConfigDataMasterRepository;
import com.maan.eway.repository.PremiaConfigMasterRepository;
import com.maan.eway.repository.YiChargeDetailRepository;
import com.maan.eway.repository.YiCoverDetailRepository;
import com.maan.eway.repository.YiPolicyApprovalRepository;
import com.maan.eway.repository.YiPolicyDetailRepository;
import com.maan.eway.repository.YiPremCalRepository;
import com.maan.eway.repository.YiSectionDetailRepository;
import com.maan.eway.repository.YiVatDetailRepository;

@Service
public class IntegrationGetServiceImpl implements IntegrationGetService {
	
	@Autowired
	private YiPolicyDetailRepository yiPolicyDetailsRepo;

	@Autowired
	private YiChargeDetailRepository yiChargeDetailsRepo;
	
	@Autowired
	private YiCoverDetailRepository yiCoverDetailsRepo;
	
	@Autowired
	private YiPolicyApprovalRepository yiPolicyApprovalRepo;
	
	@Autowired
	private YiPremCalRepository yiPremCalRepo;
	
	@Autowired
	private YiSectionDetailRepository yiSectionDetailsRepo;
	
	@Autowired
	private YiVatDetailRepository yiVatDetailsRepo;
	
	@Autowired
	private MotDriverDetailRepository motDriverDetailRepo;
	
	@Autowired
	private CreditLimitDetailRepository creditLimitDetailRepo;
	
	@Autowired
	private MotcommDiscountDetailRepository motCommDiscountDetailRepo;
	
	@Autowired
	private PgitPolRiskAddlInfoRepository pgitPolRiskAddlInfoRepo;
	
	@Autowired
	private HomePositionMasterRepository homerepo;
	

@PersistenceContext
private EntityManager em;

Gson json = new Gson(); 
private Logger log=LogManager.getLogger(IntegrationGetServiceImpl.class);


//YiPolicyDetails Get
@Override
public List<YiPolicyDetailsGetRes> getYiPolicyDetails(PremiaGetReq req) {
	List<YiPolicyDetailsGetRes> resList = new ArrayList<YiPolicyDetailsGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String policyNo="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
			policyNo=homeData.getPolicyNo();
		}
		List<YiPolicyDetail> yipolicyList=yiPolicyDetailsRepo.findByQuotationPolicyNo(policyNo);
		if(yipolicyList!=null && yipolicyList.size()>0) {
		for (YiPolicyDetail data : yipolicyList) {
			YiPolicyDetailsGetRes res = new YiPolicyDetailsGetRes();

		res = mapper.map(data, YiPolicyDetailsGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
}


@Override
public List<YiChargeDetailsGetRes> getYiChargeDetails(PremiaGetReq req) {
	List<YiChargeDetailsGetRes> resList = new  ArrayList<YiChargeDetailsGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
				try {
					
					Date today = new Date();
					Calendar cal = new GregorianCalendar();
					cal.setTime(today);
					cal.set(Calendar.HOUR_OF_DAY, 23);
					cal.set(Calendar.MINUTE, 1);
					today = cal.getTime();
					
					
					String policyNo="";
					HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
					if(homeData!=null) {
					policyNo=homeData.getPolicyNo();
					}
					List<YiChargeDetail>  yichargeList=yiChargeDetailsRepo.findByQuotationPolicyNo(policyNo);
					if(yichargeList!=null && yichargeList.size()>0) {
					for (YiChargeDetail data : yichargeList) {
						YiChargeDetailsGetRes res = new  YiChargeDetailsGetRes();

					res = mapper.map(data, YiChargeDetailsGetRes.class); 
					res.setRequestTime(data.getRequestTime());
					res.setResponseTime(data.getResponseTime());
					
					resList.add(res);
					}
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is --> " + e.getMessage());
					return null;
				}
				return resList;

}


@Override
public List<YiCoverDetailsGetRes> getYiCoverDetails(PremiaGetReq req) {
	List<YiCoverDetailsGetRes> resList = new ArrayList<YiCoverDetailsGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String policyNo="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
		policyNo=homeData.getPolicyNo();
		}
		List<YiCoverDetail> yicoverList=yiCoverDetailsRepo.findByQuotationPolicyNo(policyNo);
		if(yicoverList!=null && yicoverList.size()>0) {
		for (YiCoverDetail data : yicoverList) {
			YiCoverDetailsGetRes res = new YiCoverDetailsGetRes();

		res = mapper.map(data, YiCoverDetailsGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
}


@Override
public List<YiPolicyApprovalGetRes> getYiPolicyApproval(PremiaGetReq req) {
	List<YiPolicyApprovalGetRes> resList = new ArrayList<YiPolicyApprovalGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String policyNo="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
		policyNo=homeData.getPolicyNo();
		}
		List<YiPolicyApproval> yipolicyapprovalList=yiPolicyApprovalRepo.findByQuotationPolicyNo(policyNo);
		if(yipolicyapprovalList!=null && yipolicyapprovalList.size()>0) {
		for (YiPolicyApproval data : yipolicyapprovalList) {
			YiPolicyApprovalGetRes res = new YiPolicyApprovalGetRes();

		res = mapper.map(data, YiPolicyApprovalGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
}


@Override
public List<YiPremCalGetRes> getYiPremCal(PremiaGetReq req) {
	List<YiPremCalGetRes> resList = new ArrayList<YiPremCalGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String policyNo="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
		policyNo=homeData.getPolicyNo();
		}
		List<YiPremCal> yipremcalList=yiPremCalRepo.findByQuotationPolicyNo(policyNo);
		if(yipremcalList!=null && yipremcalList.size()>0) {
		for (YiPremCal data : yipremcalList) {
			YiPremCalGetRes res = new YiPremCalGetRes();

		res = mapper.map(data, YiPremCalGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
}


@Override
public List<YiSectionDetailGetRes> getYiSectionDetail(PremiaGetReq req) {
	List<YiSectionDetailGetRes> resList = new ArrayList<YiSectionDetailGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String policyNo="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
		policyNo=homeData.getPolicyNo();
		}
		List<YiSectionDetail> yisectiondetailList=yiSectionDetailsRepo.findByQuotationPolicyNo(policyNo);
		if(yisectiondetailList!=null && yisectiondetailList.size()>0) {
		for (YiSectionDetail data : yisectiondetailList) {
			YiSectionDetailGetRes res = new YiSectionDetailGetRes();

		res = mapper.map(data, YiSectionDetailGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
}


@Override
public List<YiVatDetailGetRes> getVatDetail(PremiaGetReq req) {
	List<YiVatDetailGetRes> resList = new ArrayList<YiVatDetailGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String policyNo="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
		policyNo=homeData.getPolicyNo();
		}
		List<YiVatDetail> yivatdetailList=yiVatDetailsRepo.findByQuotationPolicyNo(policyNo);
		if(yivatdetailList!=null && yivatdetailList.size()>0) {
		for (YiVatDetail data : yivatdetailList) {
			YiVatDetailGetRes res = new YiVatDetailGetRes();

		res = mapper.map(data, YiVatDetailGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
}


@Override
public List<MotDriverDetailGetRes> getMotDriverDetail(PremiaGetReq req) {
	List<MotDriverDetailGetRes> resList = new ArrayList<MotDriverDetailGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String policyNo="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
		policyNo=homeData.getPolicyNo();
		}
		List<MotDriverDetail> motdriverdetailList=motDriverDetailRepo.findByQuotationPolicyNo(policyNo);
		if(motdriverdetailList!=null && motdriverdetailList.size()>0) {
		for (MotDriverDetail data : motdriverdetailList) {
			MotDriverDetailGetRes res = new MotDriverDetailGetRes();

		res = mapper.map(data, MotDriverDetailGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
}


@Override
public List<CreditLimitDetailGetRes> getCreditLimitDetail(PremiaGetReq req) {
	List<CreditLimitDetailGetRes> resList = new ArrayList<CreditLimitDetailGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String customerId="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
			customerId=homeData.getCustomerId();
		}
		List<CreditLimitDetail> creditlimitdetailList=creditLimitDetailRepo.findByRequestreferenceno(customerId);
		if(creditlimitdetailList!=null && creditlimitdetailList.size()>0) {
		for (CreditLimitDetail data : creditlimitdetailList) {
			CreditLimitDetailGetRes res = new CreditLimitDetailGetRes();

		res = mapper.map(data, CreditLimitDetailGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
}


@Override
public List<MotCommDiscountDetailGetRes> getMotCommDiscountDetail(PremiaGetReq req) {
	List<MotCommDiscountDetailGetRes> resList = new ArrayList<MotCommDiscountDetailGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String policyNo="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
		policyNo=homeData.getPolicyNo();
		
		}
		List<MotCommDiscountDetail> motcommdiscountdetailList=motCommDiscountDetailRepo.findByQuotationPolicyNo(policyNo);
		if(motcommdiscountdetailList!=null && motcommdiscountdetailList.size()>0) {
		for (MotCommDiscountDetail data : motcommdiscountdetailList) {
			MotCommDiscountDetailGetRes res = new MotCommDiscountDetailGetRes();

		res = mapper.map(data, MotCommDiscountDetailGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
}


@Override
public List<PgithPolRiskAddlInfoGetRes> getPgithPolRiskAddlInfo(PremiaGetReq req) {
	List<PgithPolRiskAddlInfoGetRes> resList = new ArrayList<PgithPolRiskAddlInfoGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String policyNo="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
		policyNo=homeData.getPolicyNo();
		
		}
		List<PgithPolRiskAddlInfo> pgithpolriskaddlinfoList=pgitPolRiskAddlInfoRepo.findByQuotationPolicyNo(policyNo);
		if(pgithpolriskaddlinfoList!=null && pgithpolriskaddlinfoList.size()>0) {
		for (PgithPolRiskAddlInfo data : pgithpolriskaddlinfoList) {
			PgithPolRiskAddlInfoGetRes res = new PgithPolRiskAddlInfoGetRes();

		res = mapper.map(data, PgithPolRiskAddlInfoGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
	
}
}