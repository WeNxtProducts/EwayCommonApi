package com.maan.eway.integration.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.ValuationIntegration;
import com.maan.eway.integration.req.ValuationDetailsReq;
import com.maan.eway.integration.req.ValuationListReq;
import com.maan.eway.integration.req.ValuationReq;
import com.maan.eway.integration.req.ValuationStatusReq;
import com.maan.eway.integration.res.PremiaResponse;
import com.maan.eway.integration.res.ValuationListRes;
import com.maan.eway.integration.service.ValuationService;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ValuationServiceImpl implements ValuationService {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private MotorDataDetailsRepository repo;
	@Autowired
	private SolvitValuation solvit;
	@Override
	public PremiaResponse pushValuation(ValuationReq req) {
		PremiaResponse resp=new PremiaResponse();
		String valCompanyId="";
		try {
			List<MotorDataDetails>list=repo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			valCompanyId=list.get(0).getValCompanyId();
			if(!CollectionUtils.isEmpty(list)) {
			if("1".equals(valCompanyId))
				resp=solvit.pushValuation(req);
			else if("2".equals(valCompanyId))
				resp=solvit.pushValuation(req);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return resp;
	}

	@Override
	public PremiaResponse getStatus(ValuationStatusReq req) {
		PremiaResponse resp=new PremiaResponse();
		try {
			if("1".equals(req.getValCompanyId()))
				resp=solvit.getStatus(req);
			else if("2".equals(req.getValCompanyId()))
				resp=solvit.getStatus(req);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	@Override
	public PremiaResponse getDetails(ValuationDetailsReq req) {
		PremiaResponse resp=new PremiaResponse();
		try {
			if("1".equals(req.getValCompanyId()))
				resp=solvit.getDetails(req);
			else if("2".equals(req.getValCompanyId()))
				resp=solvit.getDetails(req);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	@Override
	public List<ValuationListRes> getValuationList(ValuationListReq req) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		List<ValuationListRes>list=null;
		try {
		// Criteria
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ValuationListRes> query = cb.createQuery(ValuationListRes.class);

		// Find All
		Root<ValuationIntegration> a = query.from(ValuationIntegration.class);

		// Select
		query.multiselect(a.get("quoteNo").alias("quoteNo"),
				a.get("vehicleId").alias("vehicleId"),a.get("vehicleRegNo").alias("vehicleRegNo"),a.get("firstName").alias("firstName"),
				a.get("email").alias("email"),a.get("customerMobile").alias("customerMobile"),a.get("policyNo").alias("policyNo"),
				a.get("createRequest").alias("createRequest"),a.get("createResponse").alias("createResponse"),a.get("idrequest").alias("idrequest"),
				a.get("idresponse").alias("statusrequest"),a.get("statusrequest").alias("statusresponse"),a.get("statusresponse").alias("idresponse"),
				a.get("recordId").alias("recordId"),a.get("status").alias("status"),a.get("valCompanyId").alias("valCompanyId"));

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(a.get("vehicleId")));

		
		List<Predicate> predics1 = new ArrayList<Predicate>();
		predics1.add(cb.equal(a.get("companyId"), req.getCompanyId()));
		if("quoteNo".equals(req.getSearchBy())) {
			predics1.add(cb.equal(a.get("quoteNo"), req.getSearchValue()));
		}else if("policyNo".equals(req.getSearchBy())) {
			predics1.add(cb.equal(a.get("policyNo"), req.getSearchValue()));
		}else if("vehicleRegNo".equals(req.getSearchBy())) {
			predics1.add(cb.equal(a.get("vehicleRegNo"), req.getSearchValue()));
		}else {
			predics1.add(cb.between(a.get("entryDate"), sdf.parse(req.getStartDate()), sdf.parse(req.getEndDate())));
		}
		
		

		query.where(predics1.toArray(new Predicate[0])).orderBy(orderList);

		// Get Result
		TypedQuery<ValuationListRes> result = em.createQuery(query);
		list = result.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
}