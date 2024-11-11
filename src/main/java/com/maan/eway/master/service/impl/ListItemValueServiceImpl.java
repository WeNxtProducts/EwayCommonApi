/*
*  Copyright (c) 2019. All right reserved
* Created on 2022-11-19 ( Date ISO 2022-11-19 - Time 13:30:17 )
* Generated by Telosys Tools Generator ( version 3.3.0 )
*/
package com.maan.eway.master.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.master.req.ListItemValueSaveReq;
import com.maan.eway.master.req.LovChangeStatusReq;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.master.req.LovGetAllReq;
import com.maan.eway.master.req.LovGetReq;
import com.maan.eway.master.res.LovDetailsGetRes;
import com.maan.eway.master.service.ListItemValueService;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.res.TitleType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
/**
* <h2>ListItemValueServiceimpl</h2>
*/
@Service
@Transactional
public class ListItemValueServiceImpl implements ListItemValueService {

@Autowired
private ListItemValueRepository repository;

@PersistenceContext
private EntityManager em;

private Logger log=LogManager.getLogger(ListItemValueServiceImpl.class);

Gson json = new Gson();
/*
public ListItemValueServiceImpl(ListItemValueRepository repo) {
this.repository = repo;
}

  */
 @Override
    public ListItemValue create(ListItemValue d) {

       ListItemValue entity;

        try {
            entity = repository.save(d);

        } catch (Exception ex) {
			log.error(ex);
            return null;
        }
        return entity;
    }

    
    @Override
    public ListItemValue update(ListItemValue d) {
        ListItemValue c;

        try {
            c = repository.saveAndFlush(d);

        } catch (Exception ex) {
			log.error(ex);
            return null;
        }
        return c;
    }

/*
    @Override
    public ListItemValue getOne(long id) {
        ListItemValue t;

        try {
            t = repository.findById(id).orElse(null);

        } catch (Exception ex) {
			log.error(ex);
            return null;
        }
        return t;
    }

*/
    @Override
    public List<ListItemValue> getAll() {
        List<ListItemValue> lst;

        try {
            lst = repository.findAll();

        } catch (Exception ex) {
			log.error(ex);
            return Collections.emptyList();
        }
        return lst;
    }


    @Override
    public long getTotal() {
        long total;

        try {
            total = repository.count();
        } catch (Exception ex) {
            log.error(ex);
			return 0;
        }
        return total;
    }


	@Override
	public List<String> validateLovDetails(ListItemValueSaveReq req) {
		List<String> errorList = new ArrayList<String>();

		try {
		
//			if (StringUtils.isBlank(req.getItemType())) {
//				errorList.add(new Error("02", "ItemType", "Please Enter ItemType"));
//			}else if (req.getItemType().length() > 100){
//				errorList.add(new Error("02","ItemType", "Please Enter ItemType Under 100 Characters")); 
//			}
			
			
//	

			if (StringUtils.isBlank(req.getItemValue())) {
			//	errorList.add(new Error("02", "ItemValue", "Please Enter Description"));
				errorList.add("1379");
			}else if (req.getItemCode().length() > 20){
				//errorList.add(new Error("02","ItemValue", "Please Enter Description Under 20 Characters")); 
				errorList.add("1380");
			}
			if (StringUtils.isBlank(req.getItemCode())) {
				//errorList.add(new Error("02", "ItemCode", "Please Enter ItemCode"));
				errorList.add("1381");
			}else if (req.getItemCode().length() > 20){
				//errorList.add(new Error("02","ItemCode", "Please Enter ItemCode Under 20 Characters")); 
				errorList.add("1382");
			}
//			else if (StringUtils.isBlank(req.getItemId())  && StringUtils.isNotBlank(req.getItemCode()) && StringUtils.isNotBlank(req.getItemType()) &&  StringUtils.isNotBlank(req.getInsuranceId()) && StringUtils.isNotBlank(req.getBranchCode())) {
//				List<ListItemValue> lovList = getItemCodeExistDetails( req.getItemCode() ,req.getItemType() , req.getInsuranceId() , req.getBranchCode());
//				if (lovList.size()>0 && (! req.getItemId().equalsIgnoreCase(lovList.get(0).getItemId().toString()))) {
//					errorList.add(new Error("01", "ItemCode", "This ItemCode Already Exist For This L.O.V"));
//				}
//			}else if (StringUtils.isNotBlank(req.getItemId())  &&  StringUtils.isNotBlank(req.getItemCode()) && StringUtils.isNotBlank(req.getItemType()) &&  StringUtils.isNotBlank(req.getInsuranceId()) && StringUtils.isNotBlank(req.getBranchCode())) {
//				List<ListItemValue> lovList = getItemCodeExistDetails( req.getItemCode() ,req.getItemType() , req.getInsuranceId() , req.getBranchCode());
//				if (lovList.size()>0 &&  (! req.getItemId().equalsIgnoreCase(lovList.get(0).getItemId().toString()))  ) {
//					errorList.add(new Error("01", "ItemCode", "This ItemCode Already Exist For This L.O.V"));
//				}
//			}
			
//			else if (StringUtils.isBlank(req.getItemId()) && StringUtils.isNotBlank(req.getItemValue()) && StringUtils.isNotBlank(req.getItemType()) &&  StringUtils.isNotBlank(req.getInsuranceId()) && StringUtils.isNotBlank(req.getBranchCode())) {
//				List<ListItemValue> lovList = getItemValueExistDetails(req.getItemCode() , req.getItemValue() ,req.getItemType() , req.getInsuranceId() , req.getBranchCode());
//				if (lovList.size()>0 && lovList.get(0).getItemCode().equalsIgnoreCase(req.getItemCode())   ) {
//					errorList.add(new Error("01", "ItemCode", "This ItemCode Already Exist For This L.O.V"));
//				}
//			}else if (StringUtils.isNotBlank(req.getItemId()) && StringUtils.isNotBlank(req.getItemValue()) && StringUtils.isNotBlank(req.getItemType()) &&  StringUtils.isNotBlank(req.getInsuranceId()) && StringUtils.isNotBlank(req.getBranchCode())) {
//				List<ListItemValue> lovList = getItemValueExistDetails(req.getItemCode() , req.getItemValue() ,req.getItemType() , req.getInsuranceId() , req.getBranchCode());
//				if (lovList.size()>0 &&  (! req.getItemId().equalsIgnoreCase(lovList.get(0).getItemId().toString()))   ) {
//					errorList.add(new Error("01", "ItemCode", "This ItemCode Already Exist For This L.O.V"));
//				}
//			}
			
			if (StringUtils.isBlank(req.getInsuranceId())) {
			//	errorList.add(new Error("02", "InsuranceId", "Please Enter InsuranceId"));
				errorList.add("1255");
			}
			
			if (StringUtils.isBlank(req.getBranchCode())) {
				//errorList.add(new Error("02", "BranchCode", "Please Select BranchCode"));
				errorList.add("1256");
			}

			if (StringUtils.isBlank(req.getRemarks())) {
				//errorList.add(new Error("04", "Remarks", "Please Enter Remarks "));
				errorList.add("1259");
			}else if (req.getRemarks().length() > 100){
				//errorList.add(new Error("04","Remarks", "Please Enter Remarks within 100 Characters")); 
				errorList.add("1260");
			}
			
			// Date Validation 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);;
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
			//	errorList.add(new Error("05", "EffectiveDateStart", "Please Select Effective Date Start"));
				errorList.add("1261");

			} else if (req.getEffectiveDateStart().before(today)) {
			//	errorList.add(new Error("05", "EffectiveDateStart", "Please Select Effective Date Start as Future Date"));
				errorList.add("1262");
			}
			//Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
				//errorList.add(new Error("05", "Status", "Please Select Status  "));
				errorList.add("1263");
			} else if (req.getStatus().length() > 1) {
			//	errorList.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed"));
				errorList.add("1264");
			}else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
			//	errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
				errorList.add("1265");
			}

			if (StringUtils.isBlank(req.getCoreAppCode())) {
				//errorList.add(new Error("07", "CoreAppCode", "Please Enter CoreAppCode"));
				errorList.add("1266");
			}else if (req.getCoreAppCode().length() > 20){
			//	errorList.add(new Error("07","CoreAppCode", "Please Enter CoreAppCode within 20 Characters")); 
				errorList.add("1267");
			}
			if (StringUtils.isBlank(req.getRegulatoryCode())) {
			//	errorList.add(new Error("08", "RegulatoryCode", "Please Enter RegulatoryCode"));
				errorList.add("1268");
			}else if (req.getRegulatoryCode().length() > 20){
			//	errorList.add(new Error("08","RegulatoryCode", "Please Enter RegulatoryCode within 20 Characters")); 
				errorList.add("1269");
			}
			if (StringUtils.isBlank(req.getCreatedBy())) {
			//	errorList.add(new Error("09", "CreatedBy", "Please Enter CreatedBy"));
				errorList.add("1270");
			}else if (req.getCreatedBy().length() > 100){
			//	errorList.add(new Error("09","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
				errorList.add("1271");
			}		
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}

	 List<ListItemValue> getItemCodeExistDetails(String itemCode ,String itemType , String InsuranceId , String branchCode ) {
			List<ListItemValue> list = new ArrayList<ListItemValue>();
			try {
				Date today = new Date();
				// Find Latest Record
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);

				// Find All
				Root<ListItemValue> b = query.from(ListItemValue.class);

				// Select
				query.select(b);

				// Effective Date Max Filter
				Subquery<Long> amendId = query.subquery(Long.class);
				Root<ListItemValue> ocpm1 = amendId.from(ListItemValue.class);
				amendId.select(cb.max(ocpm1.get("amendId")));
				Predicate a1 = cb.equal(ocpm1.get("itemType"), b.get("itemType"));
				Predicate a2 = cb.equal(b.get("companyId"),ocpm1.get("companyId"));
				Predicate a3 = cb.equal(b.get("branchCode"), ocpm1.get("branchCode"));
				amendId.where(a1,a2,a3);

				Predicate n1 = cb.equal(b.get("amendId"), amendId);
				Predicate n2 = cb.equal( b.get("itemType"),itemType );
				Predicate n3 = cb.equal(b.get("companyId"),InsuranceId);
				Predicate n4 = cb.equal(b.get("companyId"),"99999");
				Predicate n5 = cb.equal(b.get("branchCode"), branchCode);
				Predicate n6 = cb.equal(b.get("branchCode"), "99999");
				Predicate n7 = cb.or(n3,n4);
				Predicate n8 = cb.or(n5,n6);
				Predicate n9 = cb.equal(cb.lower( b.get("itemCode")),itemCode.toLowerCase() );
				query.where(n1,n2,n7,n8,n9);
				
				// Get Result
				TypedQuery<ListItemValue> result = em.createQuery(query);
				list = result.getResultList();		
			
			} catch (Exception e) {
				e.printStackTrace();
				log.info(e.getMessage());

			}
			return list;
		}

	 
	 List<ListItemValue> getItemValueExistDetails(String itemCode , String itemValue ,String itemType , String InsuranceId , String branchCode ) {
			List<ListItemValue> list = new ArrayList<ListItemValue>();
			try {
				Date today = new Date();
				// Find Latest Record
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);

				// Find All
				Root<ListItemValue> b = query.from(ListItemValue.class);

				// Select
				query.select(b);

				// Effective Date Max Filter
				Subquery<Long> amendId = query.subquery(Long.class);
				Root<ListItemValue> ocpm1 = amendId.from(ListItemValue.class);
				amendId.select(cb.max(ocpm1.get("amendId")));
				Predicate a1 = cb.equal(ocpm1.get("itemType"), b.get("itemType"));
				Predicate a2 = cb.equal(b.get("companyId"),ocpm1.get("companyId"));
				Predicate a3 = cb.equal(b.get("branchCode"), ocpm1.get("branchCode"));
				amendId.where(a1,a2,a3);

				Predicate n1 = cb.equal(b.get("amendId"), amendId);
				Predicate n2 = cb.equal( b.get("itemType"),itemType );
				Predicate n3 = cb.equal(b.get("companyId"),InsuranceId);
				Predicate n4 = cb.equal(b.get("companyId"),"99999");
				Predicate n5 = cb.equal(b.get("branchCode"), branchCode);
				Predicate n6 = cb.equal(b.get("branchCode"), "99999");
				Predicate n7 = cb.or(n3,n4);
				Predicate n8 = cb.or(n5,n6);
				Predicate n10 = cb.equal( b.get("itemCode"),itemCode );
				Predicate n9 = cb.equal(cb.lower( b.get("itemValue")),itemValue.toLowerCase() );
				query.where(n1,n2,n7,n8,n9,n10);
				
				// Get Result
				TypedQuery<ListItemValue> result = em.createQuery(query);
				list = result.getResultList();		
			
			} catch (Exception e) {
				e.printStackTrace();
				log.info(e.getMessage());

			}
			return list;
		}
	 
	@Override
	public SuccessRes insertLovDetails(ListItemValueSaveReq req) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		ListItemValue saveData = new ListItemValue();
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Integer amendId=0;
			Date startDate = req.getEffectiveDateStart() ;
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
			Date entryDate = null ;
			String createdBy = "" ;
			
			Integer itemId = 0 ;
			entryDate = new Date();
			createdBy = req.getCreatedBy();
			if(StringUtils.isBlank(req.getItemId())) {
				// Save
				Integer totalCount = getMasterTableCount( req.getInsuranceId() , req.getBranchCode());
				itemId =  totalCount+1 ;
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully");
				res.setSuccessId(itemId.toString());
			}
			else {
				// Update
				itemId = Integer.valueOf(req.getItemId());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
				//Find all
				Root<ListItemValue> b = query.from(ListItemValue.class);
				//Select 
				query.select(b);
//				
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
				
				// Where
			//	Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
				Predicate n2 = cb.equal(b.get("itemId"), req.getItemId());
				Predicate n3 = cb.equal(b.get("companyId"), req.getInsuranceId());
				Predicate n4 = cb.equal(b.get("branchCode"), req.getBranchCode());
				
				query.where(n2,n3,n4).orderBy(orderList);
				
				// Get Result 
				TypedQuery<ListItemValue> result = em.createQuery(query);
				int limit = 0 , offset = 2 ;
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				list = result.getResultList();
				
				if(list.size()>0) {
					Date beforeOneDay = new Date(new Date().getTime() - MILLIS_IN_A_DAY);
				
					if ( list.get(0).getEffectiveDateStart().before(beforeOneDay)  ) {
						amendId = list.get(0).getAmendId() + 1 ;
						entryDate = new Date() ;
						createdBy = req.getCreatedBy();
							ListItemValue lastRecord = list.get(0);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repository.saveAndFlush(lastRecord);
						
					} else {
						amendId = list.get(0).getAmendId() ;
						entryDate = list.get(0).getEntryDate() ;
						createdBy = list.get(0).getCreatedBy();
						saveData = list.get(0) ;
						if (list.size()>1 ) {
							ListItemValue lastRecord = list.get(1);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repository.saveAndFlush(lastRecord);
						}
					
				    }
				}
				res.setResponse("Updated Successfully");
				res.setSuccessId(itemId.toString());
			}
			
			dozerMapper.map(req, saveData);
			saveData.setItemId(itemId);
			saveData.setEffectiveDateStart(startDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setStatus(req.getStatus());
			saveData.setCompanyId(req.getInsuranceId());
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedDate(new Date());
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setAmendId(amendId);
			saveData.setParam1(StringUtils.isBlank(req.getTitleType())?"I":req.getTitleType());
			saveData.setCoreAppCode(req.getCoreAppCode());
			saveData.setItemValueLocal(req.getCodeDescLocal());
			repository.saveAndFlush(saveData);
			log.info("Saved Details is --> " + json.toJson(saveData));
			
			}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> "+ e.getMessage());
			return null;
		}
		return res;
		}

	public Integer getMasterTableCount(String companyId , String branchCode) {
		Integer data =0;
		try {
			List<ListItemValue> list = new ArrayList<ListItemValue>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
		// Find all
			Root<ListItemValue> b = query.from(ListItemValue.class);
			//Select 
			query.select(b);

			//Effective Date Max Filter
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("itemId"), b.get("itemId"));
			effectiveDate.where(a1);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("itemId")));
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"), companyId);
			Predicate n3 = cb.equal(b.get("companyId"), "99999");
			Predicate n4 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n2,n3);
			Predicate n7 = cb.or(n4,n5);
			query.where(n1,n6,n7).orderBy(orderList);
			
			
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			int limit = 0 , offset = 1 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			data = list.size() > 0 ?Integer.valueOf(list.get(0).getItemId().toString()) : 0 ;
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return data;
	}
	
	@Override
	public List<LovDetailsGetRes> getallLovDetails(LovGetAllReq req) {
		List<LovDetailsGetRes> resList = new ArrayList<LovDetailsGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<ListItemValue> list = new ArrayList<ListItemValue>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);

			// Find All
			Root<ListItemValue> b = query.from(ListItemValue.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ListItemValue> ocpm1 = amendId.from(ListItemValue.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("itemId"), b.get("itemId"));
			Predicate a2 = cb.equal(ocpm1.get("itemCode"), b.get("itemCode"));
			Predicate a3 = cb.equal(b.get("companyId"),ocpm1.get("companyId"));
			Predicate a4 = cb.equal(b.get("branchCode"), ocpm1.get("branchCode"));
			amendId.where(a1,a2,a3,a4);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("companyId"), "99999");
			Predicate n4 = cb.equal(b.get("branchCode"), StringUtils.isBlank(req.getBranchCode()) ?"99999" :req.getBranchCode() );
			//Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n2,n3);
			//Predicate n7 = cb.or(n4,n5);
			Predicate n8 = cb.equal(b.get("itemType"), req.getItemType());
			
			query.where(n1,n6,n4,n8).orderBy(orderList);
			
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getItemId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(ListItemValue :: getItemValue ));
			// Map
			for (ListItemValue data : list) {
				LovDetailsGetRes res = new LovDetailsGetRes();

				res = mapper.map(data, LovDetailsGetRes.class);
				res.setCodeDescLocal(data.getItemValueLocal());			
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return resList;
	}
	
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public List<LovDetailsGetRes> getActiveLovDetails(LovGetAllReq req) {
		List<LovDetailsGetRes> resList = new ArrayList<LovDetailsGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<ListItemValue> list = new ArrayList<ListItemValue>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);

			// Find All
			Root<ListItemValue> b = query.from(ListItemValue.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ListItemValue> ocpm1 = amendId.from(ListItemValue.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("itemId"), b.get("itemId"));
			Predicate a2 = cb.equal(ocpm1.get("itemCode"), b.get("itemCode"));
			Predicate a3 = cb.equal(b.get("companyId"),ocpm1.get("companyId"));
			Predicate a4 = cb.equal(b.get("branchCode"), ocpm1.get("branchCode"));
			amendId.where(a1,a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("companyId"), "99999");
			Predicate n4 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n2,n3);
			Predicate n7 = cb.or(n4,n5);
			Predicate n8 = cb.equal(b.get("itemType"), req.getItemType());
			Predicate n9 = cb.equal(b.get("status"), "Y");
			
			query.where(n1,n6,n7,n8,n9).orderBy(orderList);
			
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getItemId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(ListItemValue :: getItemValue ));
			// Map
			for (ListItemValue data : list) {
				LovDetailsGetRes res = new LovDetailsGetRes();

				res = mapper.map(data, LovDetailsGetRes.class);
				
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return resList;
	}


	@Override
	public LovDetailsGetRes getByIdLovDetails(LovGetReq req) {
		LovDetailsGetRes res = new LovDetailsGetRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<ListItemValue> list = new ArrayList<ListItemValue>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);

			// Find All
			Root<ListItemValue> b = query.from(ListItemValue.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ListItemValue> ocpm1 = amendId.from(ListItemValue.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("itemId"), b.get("itemId"));
			Predicate a2 = cb.equal(ocpm1.get("itemCode"), b.get("itemCode"));
			Predicate a3 = cb.equal(b.get("companyId"),ocpm1.get("companyId"));
			Predicate a4 = cb.equal(b.get("branchCode"), ocpm1.get("branchCode"));
			amendId.where(a1,a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("companyId"), "99999");
			Predicate n4 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n2,n3);
			Predicate n7 = cb.or(n4,n5);
			Predicate n8 = cb.equal(b.get("itemType"), req.getItemType());
			Predicate n9 = cb.equal(b.get("itemCode"), req.getItemCode());
			
			query.where(n1,n6,n7,n8,n9).orderBy(orderList);
			
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			// Map
			res = mapper.map(list.get(0), LovDetailsGetRes.class);
			res.setCodeDescLocal(list.get(0).getItemValueLocal());
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return res;
	}


	@Override
	public List<DropDownRes> getLovMasterDropdown(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query=  cb.createQuery(Tuple.class);
			List<Tuple> list = new ArrayList<Tuple>();
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);
			
			//Select
			query.multiselect( c.get("itemId").alias("itemId") ,  c.get("itemType").alias("itemType") ,  c.get("status").alias("status") ,c.get("itemTypeLocal").alias("itemTypeLocal"));
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));
			
			
			// Effective Date Start Max Filter
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a6 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a7 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));
			effectiveDate.where(a1,a2,a6,a7);
			// Effective Date End Max Filter
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a8 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a9 = cb.equal(c.get("branchCode"), ocpm2.get("branchCode"));
			effectiveDate2.where(a3,a4,a8,a9);
			
			// Item Type Filter
			Subquery<Long> itemType = query.subquery(Long.class);
			Root<ListItemValue> ocpm3 = itemType.from(ListItemValue.class);
			itemType.select(cb.max(ocpm3.get("itemType")));
			Predicate a5 = cb.equal(c.get("itemType"),ocpm3.get("itemType"));
			Predicate a10 = cb.equal(c.get("companyId"),ocpm3.get("companyId"));
			Predicate a11 = cb.equal(c.get("branchCode"), ocpm3.get("branchCode"));
			itemType.where(a5,a10,a11);
			
						
			// Where
//			Predicate n1 = cb.equal(c.get("status"),"Y");
//			Predicate n11 = cb.equal(c.get("status"),"R");
//			Predicate n12 = cb.or(n1,n11);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n4,n5);
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType.as(String.class));
			query.where(n2,n3,n8,n9,n10).orderBy(orderList);
			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			list = result.getResultList();
			
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("itemType")))).collect(Collectors.toList());
		
			for (Tuple data : list) {
				// Response 
				DropDownRes res = new DropDownRes();
				res.setCode(data.get("itemId").toString());
				res.setCodeDesc(data.get("itemType").toString());
				res.setCodeDescLocal(data.get("itemTypeLocal")!=null ? data.get("itemTypeLocal").toString() : "");
				res.setStatus(data.get("status")==null?"":data.get("status").toString());
				resList.add(res);
			}
			
			resList.sort(Comparator.comparing(DropDownRes :: getCodeDesc)) ;
		
		}	catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
				}
			return resList;
		}


	@Override
	public SuccessRes changeStatusOfLovDetails(LovChangeStatusReq req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<ListItemValue> list = new ArrayList<ListItemValue>();
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);

			// Find All
			Root<ListItemValue> b = query.from(ListItemValue.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ListItemValue> ocpm1 = amendId.from(ListItemValue.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("itemId"), b.get("itemId"));
			Predicate a2 = cb.equal(ocpm1.get("itemCode"), b.get("itemCode"));
			Predicate a3 = cb.equal(b.get("companyId"),ocpm1.get("companyId"));
			Predicate a4 = cb.equal(b.get("branchCode"), ocpm1.get("branchCode"));
			amendId.where(a1, a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("companyId"), "99999");
			Predicate n4 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n2,n3);
			Predicate n7 = cb.or(n4,n5);
			Predicate n8 = cb.equal(b.get("itemType"), req.getItemType());
			Predicate n9 = cb.equal(b.get("itemCode"), req.getItemCode());
			
			query.where(n1,n6,n7,n8,n9).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			ListItemValue updateRecord = list.get(0);
			
			if(  req.getBranchCode().equalsIgnoreCase(updateRecord.getBranchCode())) {
				updateRecord.setStatus(req.getStatus());
				repository.save(updateRecord);
			} else {
				ListItemValue saveNew = new ListItemValue();
				dozerMapper.map(updateRecord,saveNew);
				saveNew.setBranchCode(req.getBranchCode());
				saveNew.setStatus(req.getStatus());
				repository.save(saveNew);
			}
		
			// Perform Update
			res.setResponse("Status Changed");
			res.setSuccessId(req.getItemCode());
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
			}
		return res;
	}

/*
    @Override
    public boolean delete(long id) {
        try {
            repository.deleteById(id);
            return true;

        } catch (Exception ex) {
			log.error(ex);
            return false;
        }
    }

 */
	@Override
	public List<DropDownRes> getByItemValue(LovGetReq req) {
		
	
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<ListItemValue> list = new ArrayList<ListItemValue>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);

			// Find All
			Root<ListItemValue> b = query.from(ListItemValue.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ListItemValue> ocpm1 = amendId.from(ListItemValue.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("itemId"), b.get("itemId"));
//			Predicate a2 = cb.equal(ocpm1.get("itemCode"), b.get("itemCode"));
			Predicate a3 = cb.equal(b.get("companyId"),ocpm1.get("companyId"));
			Predicate a4 = cb.equal(b.get("branchCode"), ocpm1.get("branchCode"));
			amendId.where(a1,a3,a4);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n4 = cb.equal(b.get("branchCode"), StringUtils.isBlank(req.getBranchCode()) ?"99999" :req.getBranchCode() );
			Predicate n8 = cb.equal(b.get("itemType"), req.getItemType());
			/*
			 * if(!StringUtils.isBlank(req.getTitletype())) { Predicate
			 * n9=cb.equal(b.get("param1"),req.getTitletype());
			 * query.where(n1,n2,n4,n8,n9).orderBy(orderList); }
			 */
			
			query.where(n1,n2,n4,n8).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getItemId()))).collect(Collectors.toList());
			if (StringUtils.isNotBlank(req.getItemType()) && ("BOND_YEAR".equalsIgnoreCase(req.getItemType())
					|| "BURGLARY_FIRST_LOSS".equalsIgnoreCase(req.getItemType())
					|| "FIDELITY_SI".equalsIgnoreCase(req.getItemType()))) {
				list = list.stream().sorted((o1, o2)->Long.valueOf(o1.getItemCode()).compareTo(Long.valueOf(o2.getItemCode()))).collect(Collectors.toList());
			}else {
			list.sort(Comparator.comparing(ListItemValue :: getItemValue ));
			}
			// Map
			if(!StringUtils.isBlank(req.getTitletype()))
			{
		
					list = list.stream()
	                .filter(item -> req.getTitletype().equals(item.getParam1()))
	                .collect(Collectors.toList());
			}
			
			
			for (ListItemValue data : list) {
				DropDownRes res = new DropDownRes();
               res.setTitletype(data.getParam1());
			res.setCode(data.getItemCode().toString());
			res.setCodeDesc(data.getItemValue().toString());
			res.setCodeDescLocal(data.getItemValueLocal());
				res.setStatus(data.getStatus()==null?"":data.getStatus().toString());
			resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return resList;
	}

	@Override
	public TitleType getByTitleType(LovGetReq req) {
		
		//List<DropDownRes> individual = new ArrayList<DropDownRes>();
		//List<DropDownRes> corporate = new ArrayList<DropDownRes>();
	    TitleType resList = new TitleType();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<ListItemValue> list = new ArrayList<ListItemValue>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);

			// Find All
			Root<ListItemValue> b = query.from(ListItemValue.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ListItemValue> ocpm1 = amendId.from(ListItemValue.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("itemId"), b.get("itemId"));
//			Predicate a2 = cb.equal(ocpm1.get("itemCode"), b.get("itemCode"));
			Predicate a3 = cb.equal(b.get("companyId"),ocpm1.get("companyId"));
			Predicate a4 = cb.equal(b.get("branchCode"), ocpm1.get("branchCode"));
			amendId.where(a1,a3,a4);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n4 = cb.equal(b.get("branchCode"), StringUtils.isBlank(req.getBranchCode()) ?"99999" :req.getBranchCode() );
			Predicate n8 = cb.equal(b.get("itemType"), req.getItemType());
			query.where(n1,n2,n4,n8).orderBy(orderList);
			
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			
			
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getItemId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(ListItemValue :: getItemValue ));
			
			List<DropDownRes> individual = list.stream().filter(data -> data.getParam1().equals("I")) .map(data -> {
			            DropDownRes res = new DropDownRes();
			            res.setTitletype(data.getParam1());
			            res.setCode(data.getItemCode().toString());
			            res.setCodeDesc(data.getItemValue().toString());
			            res.setStatus(data.getStatus() == null ? "" : data.getStatus().toString());
			            return res;
			        })
			        .collect(Collectors.toList());

			List<DropDownRes> corporate = list.stream().filter(data -> data.getParam1().equals("C")).map(data -> {
			            DropDownRes res = new DropDownRes();
			            res.setTitletype(data.getParam1());
			            res.setCode(data.getItemCode().toString());
			            res.setCodeDesc(data.getItemValue().toString());
			            res.setStatus(data.getStatus() == null ? "" : data.getStatus().toString());
			            return res;
			        })
			        .collect(Collectors.toList());

			resList.setIndividual(individual);
			resList.setCorporate(corporate);

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return resList;
	}

}
