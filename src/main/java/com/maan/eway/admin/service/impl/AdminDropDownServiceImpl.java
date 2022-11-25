package com.maan.eway.admin.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.admin.service.AdminDropDownService;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.req.SubUserTypeReq;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SubUserTypeDropDownRes;

@Service
public class AdminDropDownServiceImpl  implements AdminDropDownService{


	private Logger log = LogManager.getLogger(AdminDropDownServiceImpl.class);

	
	@Autowired
	private ListItemValueRepository listRepo;
	
	@Autowired
	private LoginMasterRepository loginRepo ;
	
	@PersistenceContext
	private EntityManager em;
	
	// Gender
		@Override
		public List<DropDownRes> getgender(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
				// List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("GENDER", "Y");
				String itemType = "GENDER" ;
				List<ListItemValue> list  = getListItem(req , itemType);
				
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}
		
		private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
		    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
		}

	

		@Override
		public List<DropDownRes> getConstMaterial(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
			//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("CONST_MATERIAL", "Y");
				String itemType = "CONST_MATERIAL";
				List<ListItemValue> list  = getListItem(req , itemType);
				
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}

		@Override
		public List<DropDownRes> getOutbuildingConst(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
			//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("OUTBUILDING_CONST", "Y");
				String itemType = "OUTBUILDING_CONST";
				List<ListItemValue> list  = getListItem(req , itemType);
				
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;	
			
		}

		@Override
		public List<DropDownRes> getAboutBuilding(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
			//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("ABOUT_BUILDING", "Y");
				String itemType = "ABOUT_BUILDING";
				List<ListItemValue> list  = getListItem(req , itemType);
				
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}

		@Override
		public List<DropDownRes> getStateExtent(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
			//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("STATE_EXTENT", "Y");
				String itemType = "STATE_EXTENT";
				List<ListItemValue> list  = getListItem(req , itemType);
				
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}
		
		@Override
		public List<DropDownRes> getContentName(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
				//List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("CONTENT_NAME", "Y");
				String itemType = "CONTENT_NAME";
				List<ListItemValue> list  = getListItem(req , itemType);
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}
		
		@Override
		public List<DropDownRes> getPropertyName(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
			//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("PROPERTY_NAME", "Y");
				String itemType = "PROPERTY_NAME";
				List<ListItemValue> list  = getListItem(req , itemType);
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}
	

		@Override
		public List<DropDownRes> getMobileCodes( LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
			//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("MOBILE_CODE", "Y");
				String itemType = "MOBILE_CODE";
				List<ListItemValue> list  = getListItem(req , itemType);
				
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}

		@Override
		public List<DropDownRes> getBusinessType(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
			try {
			//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("BUSINESS_TYPE", "Y");
				String itemType = "BUSINESS_TYPE";
				List<ListItemValue> list  = getListItem(req , itemType);
				for (ListItemValue data : list) {
					DropDownRes res = new DropDownRes();
					res.setCode(data.getItemCode());
					res.setCodeDesc(data.getItemValue());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return resList;
		}
		
		
		public synchronized List<ListItemValue> getListItem(LovDropDownReq req , String itemType) {
			List<ListItemValue> list = new ArrayList<ListItemValue>();
			try {
				Date today = new Date();
				Calendar cal = new GregorianCalendar();
				cal.setTime(today);
				today = cal.getTime();
				Date todayEnd = cal.getTime();
				
				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ListItemValue> query=  cb.createQuery(ListItemValue.class);
				// Find All
				Root<ListItemValue> c = query.from(ListItemValue.class);
				
				//Select
				query.select(c);
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(c.get("branchCode")));
				
				
				// Effective Date Start Max Filter
				Subquery<Long> effectiveDate = query.subquery(Long.class);
				Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
				effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
				Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				effectiveDate.where(a1,a2);
				// Effective Date End Max Filter
				Subquery<Long> effectiveDate2 = query.subquery(Long.class);
				Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
				effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
				Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
				Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
				effectiveDate2.where(a3,a4);
							
				// Where
				Predicate n1 = cb.equal(c.get("status"),"Y");
				Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
				Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
				Predicate n4 = cb.equal(c.get("companyId"), req.getInsuranceId());
				Predicate n5 = cb.equal(c.get("companyId"), "99999");
				Predicate n6 = cb.equal(c.get("branchCode"), req.getBranchCode());
				Predicate n7 = cb.equal(c.get("branchCode"), "99999");
				Predicate n8 = cb.or(n4,n5);
				Predicate n9 = cb.or(n6,n7);
				Predicate n10 = cb.equal(c.get("itemType"),itemType);
				query.where(n1,n2,n3,n8,n9,n10).orderBy(orderList);
				// Get Result
				TypedQuery<ListItemValue> result = em.createQuery(query);
				list = result.getResultList();
				
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getItemCode()))).collect(Collectors.toList());
				list.sort(Comparator.comparing(ListItemValue :: getItemValue));
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return list ;
		}

}
