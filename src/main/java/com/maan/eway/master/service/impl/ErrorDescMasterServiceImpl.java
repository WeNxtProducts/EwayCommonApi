package com.maan.eway.master.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.CityMaster;
import com.maan.eway.bean.ErrorDescMaster;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.ErrorDescMasterGetReq;
import com.maan.eway.master.req.ErrorDescMasterSaveReq;
import com.maan.eway.master.res.CityMasterRes;
import com.maan.eway.master.res.ErrorDescMasterRes;
import com.maan.eway.master.service.ErrorDescMasterService;
import com.maan.eway.repository.ErrorDescMasterRepository;
import com.maan.eway.res.SuccessRes;

@Service
@Transactional
public class ErrorDescMasterServiceImpl implements ErrorDescMasterService {
	
	@PersistenceContext
	private EntityManager em;

	@Autowired
	private GenerateSeqNoServiceImpl seqNo;
	
	@Autowired
	private ErrorDescMasterRepository repo;

	
	@Override
	public List<Error> validateErrorDesc(ErrorDescMasterSaveReq req) {
		List<Error> errorList = new ArrayList<Error>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		
		try
		{
//			if (StringUtils.isBlank(req.getErrorCode()) ) {
//				errorList.add(new Error("01", "Error Code", "Please Select Error Code "));
//			}
			
			if (StringUtils.isBlank(req.getErrorDesc()) ) {
				errorList.add(new Error("01", "Error Desc", "Please Select Error Desc"));
			}
			
			if (StringUtils.isBlank(req.getProductId()) ) {
				errorList.add(new Error("01", "Product Id", "Please Select Product Id"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
		return errorList;
	}

	@Override
	public SuccessRes inserterrordesc(ErrorDescMasterSaveReq req) {
		
		
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/YYYY");
		SuccessRes res = new SuccessRes();
		
		try
		{
		
		   ErrorDescMaster errordesc = new ErrorDescMaster();
		
		   List<ErrorDescMaster> errordescs = new ArrayList<ErrorDescMaster>();
		
		   DozerBeanMapper dozerMapper = new DozerBeanMapper();
		
		   Integer amendId = 0 ;
		
			Date startDate = req.getEffectiveDateStart() ;
			String end = "31/12/2050";
			Date endDate = sdformat.parse(end);
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
			Date entryDate = null ;
			String createdBy = "" ;
			
			String errorCode = "";
			
			if(StringUtils.isBlank(req.getErrorCode()))
			{
				errorCode = seqNo.generateRefNo();
				res.setResponse("Saved Successfully ");
				res.setSuccessId(errorCode);
			}
			else
			{
				errorCode = req.getErrorCode();
				
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ErrorDescMaster> query = cb.createQuery(ErrorDescMaster.class);

				Root<ErrorDescMaster> b = query.from(ErrorDescMaster.class);
				
				query.select(b);
				
				Predicate n1 = cb.equal(b.get("status"), "Y");
				Predicate n3 = cb.equal(b.get("errorCode"), req.getErrorCode());
				Predicate n4 = cb.equal(b.get("productId"), req.getProductId());
				
				query.where(n1, n3, n4);
				
				TypedQuery<ErrorDescMaster> result = em.createQuery(query);
				int limit = 0 , offset = 2 ;
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				errordescs = result.getResultList();
				
				if (errordescs.size() > 0) {
					Date beforeOneDay = new Date(new Date().getTime() - MILLIS_IN_A_DAY);
					
					if ( errordescs.get(0).getEffectiveDateStart().before(beforeOneDay)  ) {
						amendId = errordescs.get(0).getAmendId() + 1 ;
						entryDate = new Date() ;
						createdBy = req.getCreatedBy();
						ErrorDescMaster lastRecord = errordescs.get(0);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						
					} else {
						amendId = errordescs.get(0).getAmendId() ;
						entryDate = errordescs.get(0).getEntryDate() ;
						createdBy = errordescs.get(0).getCreatedBy();
						errordesc = errordescs.get(0) ;
						if (errordescs.size()>1 ) {
							ErrorDescMaster lastRecord = errordescs.get(1);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					
				    }
				}
				
				res.setResponse("Updated Successfully ");
				res.setSuccessId(errorCode);
				
			}
			
			dozerMapper.map(req, errordesc);
			errordesc.setErrorCode(errorCode);
			errordesc.setEntryDate(entryDate);
			errordesc.setEffectiveDateStart(startDate);
			errordesc.setEffectiveDateEnd(endDate);
			errordesc.setStatus(req.getStatus());
			errordesc.setRemarks(req.getRemarks());
			errordesc.setProductId(req.getProductId());
			errordesc.setErrorDesc(req.getErrorDesc());
			errordesc.setAmendId(amendId);
			errordesc.setCreatedBy(req.getCreatedBy());
			errordesc.setUpdatedBy(req.getUpdatedBy());
			errordesc.setUpdatedDate(new Date());
			repo.saveAndFlush(errordesc);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return res;
	}

	@Override
	public List<ErrorDescMasterRes> getallErrorDetails(ErrorDescMasterGetReq req) {
		
		
		List<ErrorDescMasterRes> resList = new ArrayList<ErrorDescMasterRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		
		try {
			List<ErrorDescMaster> list = new ArrayList<ErrorDescMaster>();

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ErrorDescMaster> query = cb.createQuery(ErrorDescMaster.class);
			
			Root<ErrorDescMaster> b = query.from(ErrorDescMaster.class);
		
			
			query.select(b);
		
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ErrorDescMaster> ocpm1 = amendId.from(ErrorDescMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("errorCode"), b.get("errorCode"));

			amendId.where(a1);

			
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("errorCode")));

			
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			

			query.where(n1).orderBy(orderList);

			TypedQuery<ErrorDescMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getErrorCode()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(ErrorDescMaster :: getErrorCode ));
	
			for (ErrorDescMaster data : list) {
				ErrorDescMasterRes res = new ErrorDescMasterRes();

				res = dozerMapper.map(data, ErrorDescMasterRes.class);
				res.setErrorCode(data.getErrorCode().toString());
				resList.add(res);
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
			//log.info(e.getMessage());
			return null;

		}
		return resList;
	}
	
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public ErrorDescMasterRes getbyerrorcodeDetails(ErrorDescMasterGetReq req) {
		//List<ErrorDescMasterRes> resList = new ArrayList<ErrorDescMasterRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		
		ErrorDescMasterRes res = new ErrorDescMasterRes();
		
		try {
			List<ErrorDescMaster> list = new ArrayList<ErrorDescMaster>();

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ErrorDescMaster> query = cb.createQuery(ErrorDescMaster.class);
			
			Root<ErrorDescMaster> b = query.from(ErrorDescMaster.class);
		
			
			query.select(b);
		
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ErrorDescMaster> ocpm1 = amendId.from(ErrorDescMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("errorCode"), b.get("errorCode"));

			amendId.where(a1);

			
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("errorCode")));

			
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			

			query.where(n1).orderBy(orderList);

			TypedQuery<ErrorDescMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getErrorCode()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(ErrorDescMaster :: getErrorCode ));
	
			res = dozerMapper.map(list.get(0), ErrorDescMasterRes.class);
			res.setErrorCode(list.get(0).getErrorCode().toString());
			res.setEntryDate(list.get(0).getEntryDate());
			res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
			res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());
			

		} catch (Exception e) {
			e.printStackTrace();
			
			return null;

		}
		return res;
	}

}
