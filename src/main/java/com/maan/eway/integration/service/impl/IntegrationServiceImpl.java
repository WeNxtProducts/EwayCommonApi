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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.PremiaConfigDataMaster;
import com.maan.eway.bean.PremiaConfigMaster;
import com.maan.eway.integration.req.PremiaRequest;
import com.maan.eway.integration.res.PremiaResponse;
import com.maan.eway.integration.service.IntegrationService;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PremiaConfigDataMasterRepository;
import com.maan.eway.repository.PremiaConfigMasterRepository;

@Service
public class IntegrationServiceImpl implements IntegrationService {

	
@Autowired
private PremiaConfigDataMasterRepository pcdatarepo;
@Autowired
private PremiaConfigMasterRepository pcmasterrepo;

@Autowired
private HomePositionMasterRepository homeRepo;

@Autowired
private OracleQuery oracle;

@PersistenceContext
private EntityManager em;

private Logger log=LogManager.getLogger(IntegrationServiceImpl.class);
/*
 * 
 * 
 *
   get Master table : premia_config_master 
   get Data Maste TAble : premia_config_data
   get Frame Insert query
*/

public boolean push(PremiaConfigMaster configMas , List<String> params ) {
	try {
		
		PremiaConfigMaster masterop = configMas  ;
		if(masterop!=null ) {
			PremiaConfigMaster masterdata = masterop ;
			List<PremiaConfigDataMaster> configData = getPremiaConfigData(configMas.getCompanyId() ,configMas.getProductId() ,configMas.getPremiaId()  ) ;
			List<Map<String, Object>> listFromQuery = new ArrayList<Map<String, Object>>();
			
			if(StringUtils.isNotBlank(masterdata.getQueryKey())) {
				String query=oracle.getQuery(masterdata.getQueryKey());
				List<String> asList = fromQuerytoList(query);
				Map<String, String> maps = fromListToMaps(asList);
				
				Map<String,String> avoidd=new HashMap<String,String>();
				
				
				if(configData!=null && !configData.isEmpty()) {
					for (PremiaConfigDataMaster data : configData) {
						if(!"Y".equals(data.getDefaultYn())) {
							Map<String, String> filterdmap=maps.entrySet().stream().filter(m-> data.getInputColumn().equals(m.getKey()) ).collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));  
							// Map<String, String> filered = filterdmap.get(0);
							 String queryvalue = filterdmap.get(data.getInputColumn());
							 
							 if("Date".equals(data.getDataTypeDesc()) && !avoidd.containsKey(data.getInputColumn()) ) {
								// queryvalue=(data.getDataFormatType()==null || StringUtils.isBlank(queryvalue) ) ?queryvalue:data.getDataFormatType().replaceAll("<>",queryvalue );
								// avoidd.put(data.getInputColumn(), queryvalue);
								 maps.put(data.getInputColumn(), queryvalue); 
							 }
							 if("N".equals(data.getDefaultYn()) && "Y".equals(data.getCaseConditionYn()) ) {
									maps.put(data.getInputColumn(), data.getCaseCondition()); 
							 }else							 
								 maps.put(data.getInputColumn(), queryvalue);
							 
						}else if("N".equals(data.getDefaultYn()) && "Y".equals(data.getCaseConditionYn()) ) {
							maps.put(data.getInputColumn(), data.getCaseCondition()); 
						}
					}
				}
			//	Stream combined = Stream.concat(maps.entrySet().stream(), map2.entrySet().stream());
				
				String framedselecquery=frameselectfromMap(maps);
				log.info("framedselecquery :: "+framedselecquery);
				query="SELECT "+framedselecquery+" "+query.substring(query.indexOf(" FROM"), query.length());
				
				log.info("framedselecquery with Select :: "+query);
				/*maps.get(0);
				***********/
				listFromQuery = oracle.getListFromQueryWithoutKey(query, params);
//				if(listFromQuery!=null && listFromQuery.size()>0) {
//					 qdata = listFromQuery.get(0);
//				}
			}
			
			for (Map<String, Object> qdata  : listFromQuery ) {
				if(configData!=null && !configData.isEmpty() && qdata!=null) {
					Map<String,String> jmap=new HashMap<String,String>();
					List<String> colums=new ArrayList<String>();
					List<String> values=new ArrayList<String>();
					
					for (PremiaConfigDataMaster data : configData) {
						String value="";
						if("Y".equals(data.getDefaultYn())) {
							value= StringUtils.isBlank(data.getDefaultValue())?"":data.getDefaultValue();
							
							if("Date".equals(data.getDataTypeDesc())) { 
							//	String dateformatt=StringUtils.isNotEmpty(data.getDataFormatType())?data.getDataFormatType().toUpperCase().replace("TO_CHAR", "TO_DATE"):null;
								String dateformatt=  StringUtils.isNotEmpty(data.getDataFormatType())?data.getDataFormatType().toUpperCase() : "yyyy-MM-dd hh:mm:ss" ;
								
								if(value.equalsIgnoreCase("SYSDATE") ) {
									SimpleDateFormat dbF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
									value= dbF.format(new Date()) ;
								} else {
									value = " STR_TO_DATE(" + value + ","+ dateformatt+") " ;
								}
							
									
							}
							value=(("String".equals(data.getDataTypeDesc())|| "Date".equals(data.getDataTypeDesc()) )?"'"+value+"'":value );
							
						}/*else if("N".equals(data.getDefaultYn()) &&  "Y".equals(data.getCasecondYn() ) ){
							Object aliazval=qdata.get(data.getQueryAliaz())==null?"":qdata.get(data.getQueryAliaz());
							value=aliazval;
						}*/else {
							Object aliazval=qdata.get(data.getInputColumn())==null?"":qdata.get(data.getInputColumn());
							
							value=String.valueOf(aliazval);
							if("Date".equals(data.getDataTypeDesc())) { 
							//	String dateformatt=StringUtils.isNotEmpty(data.getDataFormatType())?data.getDataFormatType().toUpperCase().replace("TO_CHAR", "TO_DATE"):null;
								String dateformatt=  StringUtils.isNotEmpty(data.getDataFormatType())?data.getDataFormatType().toUpperCase() : "yyyy-MM-dd hh:mm:ss" ;
								if(dateformatt!=null) 
									value = " STR_TO_DATE(" + value + ","+ dateformatt+") " ;
									//value=dateformatt.replaceAll("<>","'"+aliazval.toString()+"'" );
							}
							
							value=(("String".equals(data.getDataTypeDesc()) )?"'"+String.valueOf(aliazval)+"'":value);
						}
						jmap.put(data.getColumnName(), value);
						colums.add(data.getColumnName());
						values.add(value);
					}
					
					if(!jmap.isEmpty()) {
						
						String insertQuery="INSERT INTO "+masterdata.getPremiaTableName()+" ("+StringUtils.join(colums,",")
						+") VALUES ("+StringUtils.join(values,",")+")";
						log.info("Insert Query::"+insertQuery);
						oracle.insert(insertQuery);
					}
					
					
				}
			}
			
		 
		}
		
		return true;
	}catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}
	return false;
}


private String frameselectfromMap(Map<String, String> maps) {
	String result = maps.entrySet().stream().map(map -> (map.getValue()+" "+map.getKey()))
    .collect(Collectors.joining(","));
	
	
	return result;
}


private List<String> fromQuerytoList(String selectquery){
	if(selectquery.indexOf(",")!=-1) {
		selectquery=selectquery.substring(selectquery.indexOf("SELECT")+6, selectquery.indexOf(" FROM"));
		List<String> arrays=new ArrayList<String>();
		String[] col_aliz = selectquery.split(",");
		for(int i=0;i<col_aliz.length;i++) {
			arrays.add(col_aliz[i]);
		}
		return arrays;
	}
	return null;
}

private Map<String,String> fromListToMaps(List<String> arrays){
	Map<String,String> listmaps=new HashMap<String,String>();
	for (String val : arrays) {
		if(val.trim().indexOf(" ")!=1) {
			val=val.trim();
			String[] split = val.split(" ");
			//Map<String,String> hmap=new HashMap<String,String>();
			listmaps.put(split[split.length-1],split[0]);
			//listmaps.add(hmap);
		}
	}
	return listmaps;
}




@Override
public PremiaResponse pushPremiaIntegration(PremiaRequest request) {
	PremiaResponse response = new PremiaResponse();
	try {
		HomePositionMaster home = homeRepo.findByQuoteNo(request.getQuoteNo());
		 List<PremiaConfigMaster> configMasterList =   getPremiaConfigMaster(home.getCompanyId() , home.getProductId() , request.getPremiaIds() );
		
		List<String> param=new ArrayList<String>();
		param.add(request.getQuoteNo());
		 
		for (PremiaConfigMaster configMas :  configMasterList ) {
			boolean push = push(configMas , param );
			if(push ==true  ) {
				response.setResponse("Success");	
			} else {
				response.setResponse("Failed");
			}
			
		}
	}catch(Exception e){
		e.printStackTrace();
		return null ;
	}
	return response ;
}

	
	
	public synchronized List<PremiaConfigMaster> getPremiaConfigMaster(String insuraceId , Integer productId , List<String> premiaIds ) {
		List<PremiaConfigMaster> list = new ArrayList<PremiaConfigMaster>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PremiaConfigMaster> query=  cb.createQuery(PremiaConfigMaster.class);
			// Find All
			Root<PremiaConfigMaster> c = query.from(PremiaConfigMaster.class);
			
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("premiaId")));
			
			
			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<PremiaConfigMaster> ocpm1 = effectiveDate.from(PremiaConfigMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("premiaId"),ocpm1.get("premiaId"));
			Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a3 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2,a3,a4);
			
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<PremiaConfigMaster> ocpm2 = effectiveDate2.from(PremiaConfigMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a5 = cb.equal(c.get("premiaId"),ocpm2.get("premiaId"));
			Predicate a6 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a7 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a5,a6,a7,a8);
						
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"), insuraceId);
			Predicate n5 = cb.equal(c.get("productId"), productId);
			//In 
			Expression<String>e0= c.get("premiaId");
			Predicate n6 = e0.in(premiaIds);
			query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<PremiaConfigMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getPremiaId()))).collect(Collectors.toList());
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list ;
	}

	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	public synchronized List<PremiaConfigDataMaster> getPremiaConfigData(String insuraceId , String productId , Integer premiaId ) {
		List<PremiaConfigDataMaster> list = new ArrayList<PremiaConfigDataMaster>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PremiaConfigDataMaster> query=  cb.createQuery(PremiaConfigDataMaster.class);
			// Find All
			Root<PremiaConfigDataMaster> c = query.from(PremiaConfigDataMaster.class);
			
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("premiaId")));
			
			
			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<PremiaConfigDataMaster> ocpm1 = effectiveDate.from(PremiaConfigDataMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("premiaId"),ocpm1.get("premiaId"));
			Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a3 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2,a3,a4);
			
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<PremiaConfigDataMaster> ocpm2 = effectiveDate2.from(PremiaConfigDataMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a5 = cb.equal(c.get("premiaId"),ocpm2.get("premiaId"));
			Predicate a6 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a7 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a5,a6,a7,a8);
						
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"), insuraceId);
			Predicate n5 = cb.equal(c.get("productId"), productId);
			Predicate n6 = cb.equal(c.get("premiaId"), premiaId);
			query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<PremiaConfigDataMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getPremiaId() , o.getColumnId()))).collect(Collectors.toList());
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list ;
	}
}