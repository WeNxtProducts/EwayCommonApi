package com.maan.eway.notification.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.bean.FollowUpDetails;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.error.Error;
import com.maan.eway.notification.repository.FollowUpDetailsRepository;
import com.maan.eway.notification.req.FollowupDetailsGetReq;
import com.maan.eway.notification.req.FollowupDetailsGetallReq;
import com.maan.eway.notification.req.FollowupDetailsSaveReq;
import com.maan.eway.notification.res.FollowUpDetailsListRes;
import com.maan.eway.notification.res.FollowUpDetailsPageRes;
import com.maan.eway.notification.res.FollowUpDetailsRes;
import com.maan.eway.notification.service.FollowupDetailsService;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.res.SuccessRes;

@Service
public class FollowupDetailsServiceImpl  implements FollowupDetailsService{

	private Logger log = LogManager.getLogger(FollowupDetailsServiceImpl.class);
	Gson json = new Gson();

	@Autowired
	private FollowUpDetailsRepository repository;

	@Autowired
	private ListItemValueRepository listrepo;

	
	@PersistenceContext
	private EntityManager em;

	
	@Override
	public List<Error> validateFollowupDetails(FollowupDetailsSaveReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SuccessRes saveFollowupDetails(FollowupDetailsSaveReq req) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		FollowUpDetails saveData = new FollowUpDetails();
		List<FollowUpDetails> list  = new ArrayList<FollowUpDetails>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			Integer followupid = 0;
			
			ListItemValue data = listrepo.findByItemTypeAndItemCode("FOLLOWUP_STATUS",req.getStatus());
			
			//Insert
			if(StringUtils.isBlank(req.getFollowupId().toString())) {
			
				Integer totalCount = getMasterTableCount(req.getCompanyId(),req.getProductId(),"99999");
				followupid = totalCount+1;
				saveData.setEntryDate(new Date());
				saveData.setFollowupId(followupid.toString());
				res.setSuccessId(followupid.toString());
				res.setResponse("Inserted Successfully");
			}
			//Update
			else {
				followupid= Integer.valueOf(req.getFollowupId());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<FollowUpDetails> query = cb.createQuery(FollowUpDetails.class);
				//Find all
				Root<FollowUpDetails> b = query.from(FollowUpDetails.class);
				//Select 
				query.select(b);
				//Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("entryDate")));
				//Where
				Predicate n1 = cb.equal(b.get("followupId"),req.getFollowupId());
				Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
				Predicate n3 = cb.equal(b.get("branchCode"),"99999");
				Predicate n4 = cb.equal(b.get("requestReferenceNo"),req.getRequestReferenceNo());
				Predicate n5 = cb.equal(b.get("loginId"),req.getLoginId());
				Predicate n6 = cb.equal(b.get("productId"),req.getProductId());
			
				query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			
				// Get Result
				TypedQuery<FollowUpDetails> result = em.createQuery(query);
			
				
				
				res.setResponse("Updated Successfully");
				res.setSuccessId(followupid.toString());
			
			}
			dozerMapper.map(req, saveData);			
			saveData.setFollowupId(followupid.toString());
			saveData.setEntryDate(new Date());
			saveData.setBranchCode("99999");
			saveData.setStartDate(req.getStartDate());
			saveData.setUpdatedDate(new Date());
			saveData.setStatus(req.getStatus());
			saveData.setStatusDesc(data.getItemValue());
			saveData.setCompanyId(req.getCompanyId());
			repository.saveAndFlush(saveData);	
			log.info("Saved Details is --> " + json.toJson(saveData));	
			}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
		}
		
	public Integer getMasterTableCount(String companyId, String productId, String branchCode)	{

		Integer data =0;
		try {
			List<FollowUpDetails> list = new ArrayList<FollowUpDetails>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<FollowUpDetails> query = cb.createQuery(FollowUpDetails.class);
			//Find all
			Root<FollowUpDetails> b = query.from(FollowUpDetails.class);
			// Select
			query.select(b);
			
			
			//OrderBy
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("followupId")));
			
			Predicate n1 = cb.equal(b.get("companyId"),companyId);
			Predicate n2 = cb.equal(b.get("productId"),productId);
			Predicate n3 = cb.equal(b.get("branchCode"),"99999");
			
			
			
			query.where(n1,n2,n3).orderBy(orderList);
					
			
			// Get Result
			TypedQuery<FollowUpDetails> result = em.createQuery(query);
			int limit = 0 , offset = 1 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			data = list.size() > 0 ?  Integer.valueOf(list.get(0).getFollowupId()) : 0 ;
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return data;
	}
	
	private Date dateConversion(Date nextfollowupdate, String nextfollowuptime) {
		
		Date date = nextfollowupdate;
		if(nextfollowuptime!=null) {
			String[] split = nextfollowuptime.split(":");
			date.setHours(Integer.valueOf(split[0]));
			date.setMinutes(Integer.valueOf(split[1]));
		}
		return date;
	}

	@Override
	public FollowUpDetailsRes getclientdetailsid(FollowupDetailsGetReq req) {
		// TODO Auto-generated method stub
		FollowUpDetailsRes res = new FollowUpDetailsRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
		
			List<FollowUpDetails> list = new ArrayList<FollowUpDetails>();
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<FollowUpDetails> query = cb.createQuery(FollowUpDetails.class);

			// Find All
			Root<FollowUpDetails> b = query.from(FollowUpDetails.class);

			// Select
			query.select(b);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n2 = cb.equal(b.get("branchCode"),"99999");
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("requestReferenceNo"),req.getRequestReferenceNo());
			Predicate n5 = cb.equal(b.get("followupId"),req.getFollowupId());
			Predicate n6 = cb.equal(b.get("loginId"),req.getLoginId());
			
			query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<FollowUpDetails> result = em.createQuery(query);

			list = result.getResultList();

			res = mapper.map(list.get(0), FollowUpDetailsRes.class);
						
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
		}

	@Override
	public FollowUpDetailsPageRes getfollowupDetails(FollowupDetailsGetallReq req) {
		// TODO Auto-generated method stub
		FollowUpDetailsPageRes res = new FollowUpDetailsPageRes();
		try {
		
			List<FollowUpDetails> list = new ArrayList<FollowUpDetails>();
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<FollowUpDetails> query = cb.createQuery(FollowUpDetails.class);

			// Find All
			Root<FollowUpDetails> b = query.from(FollowUpDetails.class);

			// Select
			query.select(b);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n2 = cb.equal(b.get("branchCode"), "99999");
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());			
			Predicate n4 = cb.equal(b.get("status"),req.getStatus());

			query.where(n1,n2,n3,n4).orderBy(orderList);
			
			// Get Result
			TypedQuery<FollowUpDetails> result = em.createQuery(query);

			list = result.getResultList();
			
			
			List<FollowUpDetailsListRes> reslist = new ArrayList<FollowUpDetailsListRes>();

			for (FollowUpDetails followUpDetails : list) {
				ModelMapper mapper = new ModelMapper();
				FollowUpDetailsListRes res1 = mapper.map(followUpDetails, FollowUpDetailsListRes.class);
				reslist.add(res1);
			}
			
			res.setBranchCode("99999");
			res.setCompanyId(req.getCompanyId());
			res.setProductId(req.getProductId());
			res.setStatus(req.getStatus());
			res.setStatusDesc(list.get(0).getStatusDesc());;
			res.setFollowupDetailsRes(reslist);;
		}catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		
		return res;
	}

}	
