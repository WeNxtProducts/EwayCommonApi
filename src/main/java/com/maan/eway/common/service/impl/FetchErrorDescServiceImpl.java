package com.maan.eway.common.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.maan.eway.bean.ErrorDescMaster;
import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.res.ErrorDescListRes;
import com.maan.eway.common.res.ErrorGroupRes;

@Component
public class FetchErrorDescServiceImpl {
	
	private Logger log=LogManager.getLogger(FetchErrorDescServiceImpl.class);
	
	@PersistenceContext
	private EntityManager em;
	
	List<ErrorGroupRes> errorDescriptionList = new ArrayList<ErrorGroupRes>();

	public String getErrorDesc(String errorCode , CommonErrorModuleReq req ) {
		String errorDesc = "";
		try {
			List<ErrorGroupRes> errorDescList = errorDescriptionList ;// loadErrorModule(req );
			
			// Filter By Primary Key
			List<ErrorGroupRes> filterErrorList = errorDescList.stream().filter( o-> o.getCompanyId().equalsIgnoreCase(req.getInsuranceId())   
					&& o.getProductId().equals(req.getProductId()!=null ?Integer.valueOf(req.getProductId()):99999)
					&&  o.getModuleId().equals(req.getModuleId()!=null ?Integer.valueOf(req.getModuleId()):0) ).collect(Collectors.toList());  
			
			// Filter By Branch  
			List<ErrorDescListRes> filterErrorCodeList = new ArrayList<ErrorDescListRes>();
			if( filterErrorList.size() > 0 && filterErrorList.get(0).getErrorDescList()!=null && filterErrorList.get(0).getErrorDescList().size() > 0   ) {
				filterErrorCodeList = filterErrorList.get(0).getErrorDescList() ;
			}
			List<ErrorDescListRes> filterErrorCode = filterErrorCodeList.stream().filter( o -> o.getErrorCode().equalsIgnoreCase(errorCode) 
					&& (o.getBranchCode().equalsIgnoreCase(req.getBranchCode()) || o.getBranchCode().equalsIgnoreCase("99999") )	).collect(Collectors.toList());
			
			// Response 
			errorDesc = filterErrorCode.size() > 0 ? filterErrorCode.get(0).getErrorDesc() : "No Error Description Available" ;
			
			 
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
			errorDesc = "No Error Description Available" ;
		}
		return errorDesc ;
	}
	
	//@Cacheable("ErrorModules")
	@Scheduled(fixedRateString = "5000")//5 second
	public List<ErrorGroupRes> loadErrorModule(CommonErrorModuleReq req ) {
		List<ErrorGroupRes>  resList = new ArrayList<ErrorGroupRes>();
		try {
			
			// Fetch all Error Desc
			List<ErrorDescMaster> errorDescList = getErrorList() ;
			
			Map<String, List<ErrorDescMaster>> groupByCompanyId =  errorDescList.stream().filter( o -> o.getCompanyId() !=null && o.getStatus()!=null 
					&& o.getStatus().equalsIgnoreCase("Y") ).collect(Collectors.groupingBy(ErrorDescMaster :: getCompanyId));
			
			// Group By Company Id
			for (String companyId : groupByCompanyId.keySet() ) {
				
				List<ErrorDescMaster> filterCompanyList = groupByCompanyId.get(companyId);
				Map<Integer, List<ErrorDescMaster>> groupByProductId =  filterCompanyList.stream().filter( o -> o.getProductId() !=null ).collect(Collectors.groupingBy(ErrorDescMaster :: getProductId));
				
				// Group By Product Id
				for (Integer productId : groupByProductId.keySet() ) {
					
					List<ErrorDescMaster> filterProductList = groupByProductId.get(productId);
					Map<Integer, List<ErrorDescMaster>> groupByModuleId =  filterProductList.stream().filter( o -> o.getModuleId() !=null ).collect(Collectors.groupingBy(ErrorDescMaster :: getModuleId));
					
					// Group By Module Id
					for (Integer moduleId : groupByModuleId.keySet() ) {
						ErrorGroupRes res = new ErrorGroupRes();
						List<ErrorDescListRes> errorDescResList = new ArrayList<ErrorDescListRes>(); 
						
						List<ErrorDescMaster> filterModuleList = groupByModuleId.get(moduleId);
						
						if(filterModuleList.size() >0 ) {
							ErrorDescMaster firstData = filterModuleList.get(0);
							res.setCompanyId(companyId);
							res.setProductId(productId);
							res.setModuleId(moduleId);
							res.setModuleName(firstData.getModuleName() );
							
							for (ErrorDescMaster data :  filterModuleList) {
								ErrorDescListRes errorDescRes = new ErrorDescListRes();
								errorDescRes.setBranchCode(data.getBranchCode());
								errorDescRes.setErrorCode(data.getErrorCode());
								errorDescRes.setErrorDesc(data.getErrorDesc());
								errorDescResList.add(errorDescRes);
								
							}
							res.setErrorDescList(errorDescResList);
							resList.add(res);
						}
						
					}
					
				}
				
			}
			
			// Group By Some Condition
//			Function<ErrorDescMaster, ErrorGroupRes > compositeKey = errorRecord ->
//			Arrays.asList(errorRecord.getCompanyId(), errorRecord.getProductId() ,errorRecord.getModuleId() , errorRecord.getModuleName()));
			
//			Object [] value = {req.getInsuranceId() ,Integer.valueOf(req.getProductId()) , req.getModuleId() ,req.getModuleName()  };
//			
//			
//			Function<ErrorDescMaster, List<Object>> compositeKey = errorRecord -> 
//			Arrays.<Object>asList(errorRecord.getCompanyId(), errorRecord.getProductId() ,errorRecord.getModuleId() , errorRecord.getModuleName());
////			
//			
//			
//			Map<Object, List<ErrorDescMaster>> map = errorDescList.stream().collect(Collectors.groupingBy(compositeKey, Collectors.toList()));
//			filterErrorList = (List<ErrorDescMaster>) map.get(value) ;
//			
			
			// Filter Error Desc
//			filterErrorList = errorDescList.stream().filter( o-> o.getCompanyId().equalsIgnoreCase(req.getInsuranceId())   
//					&& o.getProductId().equals(StringUtils.isNotBlank(req.getProductId())?Integer.valueOf(req.getProductId()):99999)
//					&&  o.getModuleId().equals(StringUtils.isNotBlank(req.getModuleId())?Integer.valueOf(req.getModuleId()):99999)   
//					&& (o.getBranchCode().equalsIgnoreCase(req.getBranchCode()) || o.getBranchCode().equalsIgnoreCase("99999") ) ).collect(Collectors.toList());
			
			errorDescriptionList = resList ;
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
		}
		return resList ;
	}
	
	
//  @Scheduled(fixedRateString = "36,00,000")//1hour
//	@Scheduled(fixedRateString = "60000")//1min
//	@Scheduled(fixedRateString = "30000")//30Second
//	@Scheduled(fixedRateString = "5000")//5 Second
//	@CacheEvict(value = "ErrorModules", allEntries = true)
//	@Scheduled(fixedRateString = "5000")
//	public void emptyErrorModuleCache() {
//		log.info("emptying Error Modules cache");
//	}
	
	public List<ErrorDescMaster> getErrorList() {
		List<ErrorDescMaster>  list = new ArrayList<ErrorDescMaster>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
//			cal.set(Calendar.HOUR_OF_DAY, 1);;
//			cal.set(Calendar.MINUTE, 1);
//			today = cal.getTime();
//			cal.set(Calendar.HOUR_OF_DAY, 23);
//			cal.set(Calendar.MINUTE, 59);
//			Date todayEnd = cal.getTime();
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ErrorDescMaster> query=  cb.createQuery(ErrorDescMaster.class);
			
			// Find All
			Root<ErrorDescMaster> c = query.from(ErrorDescMaster.class);
			//Select
			query.select(c);
			
			
			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<ErrorDescMaster> ocpm1 = effectiveDate.from(ErrorDescMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("errorCode"),ocpm1.get("errorCode"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a4 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate a5 = cb.equal(c.get("moduleId"),ocpm1.get("moduleId"));
			Predicate a6 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			effectiveDate.where(a1,a2,a3,a4,a5,a6);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<ErrorDescMaster> ocpm2 = effectiveDate2.from(ErrorDescMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a7 = cb.equal(c.get("errorCode"),ocpm2.get("errorCode"));
			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a9 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a10 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			Predicate a11 = cb.equal(c.get("moduleId"),ocpm2.get("moduleId"));
			Predicate a12 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			effectiveDate2.where(a7,a8,a9,a10,a11,a12);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));
			
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n8 = cb.equal(c.get("status"),"R");
			Predicate n9 = cb.or(n1,n8);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);
			query.where(n9,n2,n3).orderBy(orderList);
				
			// Get Result
			
			TypedQuery<ErrorDescMaster> result = em.createQuery(query);
			list = result.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
		}
		return list ;
	}
}
