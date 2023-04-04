package com.maan.eway.notification.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		List<Error> errorList = new ArrayList<Error>();

		try {
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("01", "Insurance Id", "Please Select Insurance Id"));
			}
			if (StringUtils.isBlank(req.getProductId())) {
				errorList.add(new Error("02", "Product Id", "Please Select Product Id"));
			}
			if (StringUtils.isBlank(req.getRequestReferenceNo())) {
				errorList.add(new Error("03", "RequestReferenceNo", "Please Enter Request Reference No"));
			}
			else if((StringUtils.isNotBlank(req.getRequestReferenceNo())&&req.getRequestReferenceNo().length()>100)){
				errorList.add(new Error("03", "RequestReferenceNo", "Please Enter RequestReferenceNo within 100 Characters"));			
			}
			else if(!req.getRequestReferenceNo().matches("[0-9a-zA-Z-]+")) {
				errorList.add(new Error("03", "RequestReferenceNo", "Please Enter RequestReferenceNo in Correct Format"));										
			}			
			if (StringUtils.isBlank(req.getLoginId())) {
				errorList.add(new Error("04", "Login Id", "Please Enter Login Id"));
			}
			else if((StringUtils.isNotBlank(req.getLoginId())&&req.getLoginId().length()>100)){
				errorList.add(new Error("04", "Login Id", "Please Enter Login Id within 100 Characters"));			
			}
			if(StringUtils.isBlank(req.getFollowupDesc())){
				errorList.add(new Error("05", "FollowupDesc", "Please Enter FollowupDesc"));			
			}
			else if((StringUtils.isNotBlank(req.getFollowupDesc())&&req.getFollowupDesc().length()>1000)){
				errorList.add(new Error("05", "FollowupDesc", "Please Enter FollowupDesc within 1000 Characters"));			
			}
			else if(!req.getFollowupDesc().matches("[a-zA-Z ]+")) {
				errorList.add(new Error("05", "FollowupDesc", "Please Enter FollowupDesc in Alphabets"));										
			}			
			if (StringUtils.isBlank(req.getStatus())) {
				errorList.add(new Error("06", "Status", "Please Select Status"));
			}
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			Date yesterday = cal.getTime();
			Date a = sdf.parse(req.getStartDate());

			if (StringUtils.isBlank(req.getStartDate())) {
				errorList.add(new Error("07", "Start Date", "Please Enter Start Date"));
			} else if (!req.getStartDate().matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")) {
				errorList.add(new Error("07", "Start Date",
						"StartDate format should be dd/MM/yyyy only allowed . Example :- 07/10/2023"));
			}
			else if (a.before(yesterday)) {
				errorList.add(new Error("07", "Start Date", "Please Enter Future Date as Start Date"));
				} 
			Date endDate = sdf.parse(req.getEndDate());
			Date startDate = sdf.parse(req.getStartDate());

			if (StringUtils.isBlank(req.getEndDate())) {
				errorList.add(new Error("08", "End Date", "Please Enter EndDate"));
			} else if (!req.getEndDate().toString().matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")) {
				errorList.add(new Error("08", "End Date",
						"End Date format should be dd/MM/yyyy only allowed . Example :- 07/10/2023"));
			}
			else if (endDate.before(startDate)) {
				errorList.add(new Error("08", "End Date", "End Date not before Start Date"));
				}

			if (StringUtils.isBlank(req.getStartTime())) {
				errorList.add(new Error("09", "Start Time", "Please Enter Start Time"));
			} else if (!req.getStartTime().toString().matches("([0-9]{2}):([0-9]{2})")) {
				errorList.add(new Error("09", "Start Time",
						"Start Time format should be 00:00 only allowed . Example :- 00:00"));
			}

			if (StringUtils.isBlank(req.getEndTime())) {
				errorList.add(new Error("10", "End Time", "Please Enter End Time"));
			} else if (!req.getEndTime().toString().matches("([0-9]{2}):([0-9]{2})")) {
				errorList.add(new Error("10", "End Time",
						"End Time format should be 00:00 only allowed . Example :- 00:00"));
			}
			if (StringUtils.isBlank(req.getRemarks())) {
				errorList.add(new Error("11", "Remarks", "Please Enter Remarks"));
			}
			else if((StringUtils.isNotBlank(req.getRemarks())&&req.getRemarks().length()>100)){
				errorList.add(new Error("11", "Remarks", "Please Enter Remarks within 100 Characters"));			
			}
			
	} catch (Exception e) {
		log.error(e);
		e.printStackTrace();
	}
	return errorList;
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
			//dozerMapper.map(req, saveData);			
			saveData.setFollowupId(followupid.toString());
			saveData.setEntryDate(new Date());
			saveData.setBranchCode("99999");
			saveData.setStartDate(sdf.parse(req.getStartDate()));
			saveData.setUpdatedDate(new Date());
			saveData.setStatus(req.getStatus());
			saveData.setStatusDesc(data.getItemValue());
			saveData.setCompanyId(req.getCompanyId());
			saveData.setEndDate(sdf.parse(req.getEndDate()));
			saveData.setLoginId(req.getLoginId());
			saveData.setRequestReferenceNo(req.getRequestReferenceNo());
			saveData.setProductId(req.getProductId());
			saveData.setFollowupDesc(req.getFollowupDesc()==null?"":req.getFollowupDesc());
			saveData.setStartTime(req.getStartTime());
			saveData.setEndTime(req.getEndTime());
			saveData.setRemarks(req.getRemarks());
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
			if(list!=null&& list.size()>0) {
			res = mapper.map(list.get(0), FollowUpDetailsRes.class);
			}
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
			
			if(list!=null&& list.size()>0) {
					
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
			res.setStatusDesc(list.get(0).getStatusDesc());
			res.setFollowupDetailsRes(reslist);
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		
		return res;
	}

}	
