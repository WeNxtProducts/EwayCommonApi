package com.maan.eway.upgrade.criteria;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maan.eway.service.PrintReqService;
@Component
public class CriteriaService {

	private CriteriaBuilder cb;
	
	@PersistenceContext
    private EntityManager em;
	
	@Autowired
	private PrintReqService reqPrinter;
	//public static Gson gs=new Gson();	
	
	/*  private static final Gson gs = new GsonBuilder()
	            .excludeFieldsWithoutExposeAnnotation()
	            .excludeFieldsWithModifiers(java.lang.reflect.Modifier.STATIC) // STATIC|TRANSIENT in the default configuration
	            .create();*/
	public List<Tuple> getResult(SpecCriteria cr,Integer limit,Integer offset) throws Exception{
		
		List<Tuple> list =null;
	
		cb=em.getCriteriaBuilder();
		//List<Root> preparedRoot=new ArrayList<Root>();
		CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
		
		Root<? extends Class> root = query.from(cr.getTableName());
		List<Selection<?>> colselct=new ArrayList<Selection<?>>();
		
		Field[] declaredFields = cr.getTableName().getDeclaredFields();
		//declaredFields
		for(Field  field:declaredFields) {
			try {
				if(!"serialVersionUID".equals(field.getName()))
				{
					Selection<Object> alias = root.get(field.getName()).alias(field.getName());
					colselct.add(alias);
				}
			
			}catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		query.multiselect(colselct);
		Predicate predicate = cb.conjunction();
		UserSearchQueryCriteriaConsumer searchConsumer = new UserSearchQueryCriteriaConsumer(predicate, cb, root);
		List<SearchCriteria> params = cr.getWheres();
		params.stream().forEach(searchConsumer);
	    predicate = searchConsumer.getPredicate();
	    
	    
	    // Order By
	 		List<Order> orderList = new ArrayList<Order>();	 
	 		for (String order : cr.getOrderby()) {
	 			if(order.indexOf(".")!=-1) {
	 				String[] embd = order.split("\\.");
	 				orderList.add(cb.desc(root.get(embd[0]).get(embd[1])));
	 			}else
	 				orderList.add(cb.desc(root.get(order)));			}
	 		
	 		query.where(predicate).orderBy(orderList);		
	 		TypedQuery<Tuple> result = em.createQuery(query);
	 		
	 		Map<String, Object> parameters = searchConsumer.getParameters();
	 		if(!parameters.isEmpty()) {
	 			//Set<Entry<String, Object>> entrySet = parameters.entrySet();
	 			for(Entry<String, Object> keyas:parameters.entrySet()) {
	 				//keyas.getValue().getClass()
	 				final Class<?> parameterType = result.getParameter(keyas.getKey()).getParameterType();
	 				if(parameterType.isAssignableFrom(java.util.Date.class) ) {
	 					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	 					DateFormat time=new SimpleDateFormat("HH:mm:ss");
	 					//String format = ;
						 //formatter.format(data.get("inceptiondate"));
	 					result.setParameter(keyas.getKey(),formatter.parse(keyas.getValue().toString()+" "+time.format(new Date())));	
	 				}else
	 				 {
	 					Object value = keyas.getValue();
	 					if(parameterType.isAssignableFrom(Double.class) )
	 						value=Double.parseDouble(keyas.getValue().toString());
	 					else if(parameterType.isAssignableFrom(BigDecimal.class) )
	 						value=new BigDecimal(keyas.getValue().toString());
	 					else if(parameterType.isAssignableFrom(Integer.class) )
	 						value=new Integer(keyas.getValue().toString());
	 					else if(parameterType.isAssignableFrom(Long.class) )
	 						value=new Long(keyas.getValue().toString());
	 					
	 					result.setParameter(keyas.getKey(),value);
	 				 }
	 				
	 			}
	 		}

	 		result.setFirstResult(limit* offset);
			result.setMaxResults(offset);
			//System.out.println("Query Input: "+reqPrinter.reqPrint(cr.getWheres()));
			list =  result.getResultList();
		 
		
		
		return list;
	}
public List<Tuple> getResult(SpecCriteria cr,String amendIdCol,Integer limit,Integer offset) throws Exception{

		
		List<Tuple> list =null;
		cb=em.getCriteriaBuilder();
		//List<Root> preparedRoot=new ArrayList<Root>();
		CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
		
		Root<? extends Class> root = query.from(cr.getTableName());
		List<Selection<?>> colselct=new ArrayList<Selection<?>>();
		
		Field[] declaredFields = cr.getTableName().getDeclaredFields();
		//declaredFields
		for(Field  field:declaredFields) {
			try {
				if(!"serialVersionUID".equals(field.getName()))
				{
					Selection<Object> alias = root.get(field.getName()).alias(field.getName());
					colselct.add(alias);
				}
			}catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		query.multiselect(colselct);
		Predicate predicate = cb.conjunction();
		UserSearchQueryCriteriaConsumer searchConsumer = new UserSearchQueryCriteriaConsumer(predicate, cb, root);
		List<SearchCriteria> params = cr.getWheres();
		params.stream().forEach(searchConsumer);
	    predicate = searchConsumer.getPredicate();
	    
	    
	    

	 // Amend Id Max Filter
	 Subquery<Long> amendId = query.subquery(Long.class);
	 Root<? extends Class> ocpm1 = amendId.from(cr.getTableName());
	 if(amendIdCol.indexOf(".")!=-1) {
		 String[] embd = amendIdCol.split("\\.");
		 amendId.select(cb.max(ocpm1.get(embd[0]).get(embd[1])));
	 }else {
		 amendId.select(cb.max(ocpm1.get(amendIdCol)));
	 }
	 
	 //amendId.select(cb.max(ocpm1.get(amendIdCol)));
	 javax.persistence.criteria.Predicate n3=null;
	 Predicate subPredicate = cb.conjunction();
	 {
		   
			UserSearchQueryCriteriaConsumer amendConsumer = new UserSearchQueryCriteriaConsumer(subPredicate, cb, ocpm1);
			List<SearchCriteria> params_sub = cr.getWheres();
			params_sub.stream().forEach(amendConsumer);
			subPredicate = amendConsumer.getPredicate();
		    amendId.where(subPredicate);
		    
		    if(amendIdCol.indexOf(".")!=-1) {
				 String[] embd = amendIdCol.split("\\.");
				 //amendId.select(cb.max(ocpm1.get(embd[0]).get(embd[1])));
				 n3 = cb.equal(root.get(embd[0]).get(embd[1]), amendId);
				 
			 }else {			 
				 n3 = cb.equal(root.get(amendIdCol), amendId);
			 }
		    
		 
		    
	 }
	 //javax.persistence.criteria.Predicate a1 = cb.equal(c.get("cityId"), ocpm1.get("cityId"));
	 
	    
	     
	 	/*	List<Order> orderList = new ArrayList<Order>();	 
	 		for (String order : cr.getOrderby()) {
	 			orderList.add(cb.desc(root.get(order)));
			}
	 		*/
	 
	// Order By
		List<Order> orderList = new ArrayList<Order>();	 
		for (String order : cr.getOrderby()) {
			if(order.indexOf(".")!=-1) {
				String[] embd = order.split("\\.");
				orderList.add(cb.desc(root.get(embd[0]).get(embd[1])));
			}else
				orderList.add(cb.desc(root.get(order)));
		}
	 		query.where(predicate,n3).orderBy(orderList);		
	 		TypedQuery<Tuple> result = em.createQuery(query);
	 		
	 		Map<String, Object> parameters = searchConsumer.getParameters();
	 		if(!parameters.isEmpty()) {
	 			//Set<Entry<String, Object>> entrySet = parameters.entrySet();
	 			for(Entry<String, Object> keyas:parameters.entrySet()) {
	 				//keyas.getValue().getClass()
	 				final Class<?> parameterType = result.getParameter(keyas.getKey()).getParameterType();
	 				if(parameterType.isAssignableFrom(java.util.Date.class) ) {
	 					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
	 					DateFormat time=new SimpleDateFormat("HH:mm:ss");
						 //formatter.format(data.get("inceptiondate"));
	 					result.setParameter(keyas.getKey(),formatter.parse(keyas.getValue().toString()+" "+time.format(new Date())));	
	 				}else
	 				 {
	 					Object value = keyas.getValue();
	 					if(parameterType.isAssignableFrom(Double.class) )
	 						value=Double.parseDouble(keyas.getValue().toString());
	 					else if(parameterType.isAssignableFrom(BigDecimal.class) )
	 						value=new BigDecimal(keyas.getValue().toString());
	 					else if(parameterType.isAssignableFrom(Integer.class) )
	 						value=new Integer(keyas.getValue().toString());
	 					else if(parameterType.isAssignableFrom(Long.class) )
	 						value=new Long(keyas.getValue().toString());
	 					
	 					result.setParameter(keyas.getKey(),value);
	 				}
	 				
	 			}
	 		}

	 		result.setFirstResult(limit* offset);
			result.setMaxResults(offset);
			//System.out.println("Query Input: "+reqPrinter.reqPrint(cr.getWheres()));
			list =  result.getResultList();
		 
		
		
		return list;
	
	}
	
	public SpecCriteria createCriteria(Class tablename, String search, String orderbya) throws Exception {
		List<String> colms=new ArrayList<String>();
		List<SearchCriteria> params = new ArrayList<SearchCriteria>();
		if (search != null) {
			//Pattern pattern = Pattern.compile("(\\w+?)(:|<|>|~)(\\w+|[a-zA-Z0-9_&.*]+);");
			Pattern pattern = Pattern.compile("(\\w+?||[a-zA-Z0-9_&\\/.*]+)(:|<|>|~)([^%';=?$\\x22]+);");
			Matcher matcher = pattern.matcher(search+ ";");
			while (matcher.find()) {	            	
				if(matcher.group(3).indexOf("&")!=-1) {
					String[] strkey = matcher.group(3).split("&");
					//matcher.group(1)
					params.add(new SearchCriteria(strkey[0], matcher.group(2),matcher.group(1),null,strkey[1]));
				}else if(matcher.group(2).equals(":") && (matcher.group(3)!=null && matcher.group(3).indexOf("{")!=-1 && matcher.group(3).indexOf("}")!=-1)) {
					String inData = matcher.group(3).replaceAll("\\x7B", "").replaceAll("\\x7D", "");//.split(",");
					List<String> data=new LinkedList<String>();
					if(inData.indexOf(",")!=-1) {
						
						data.addAll(Arrays.asList(inData.split(",")));
						params.add(new SearchCriteria(matcher.group(1), matcher.group(2), null,data,null));
					}else {
						data.add(matcher.group(3));
						params.add(new SearchCriteria(matcher.group(1), matcher.group(2), null,data,null));
					}
				}else 
					params.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3),null,null));
			} 
		}
		List<String> orderby=new ArrayList<String>();
		if(orderbya.indexOf(",")!=-1) {
			String[] split = orderbya.split(",");
			for (int i = 0; i < split.length; i++) {
				orderby.add(split[0]);	
			}
		}else {
			orderby.add(orderbya);	
		}
		
		

		SpecCriteria criteria=SpecCriteria.builder().tableName(tablename).columns(colms).orderby(orderby).wheres(params).build();
		return criteria;
	}
	
	
	
	

	public List<Long> getCount(SpecCriteria cr,Integer limit,Integer offset) throws Exception{
	
		List<Long> list =null;
		cb=em.getCriteriaBuilder();
		//List<Root> preparedRoot=new ArrayList<Root>();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		
		Root<? extends Class> root = query.from(cr.getTableName());
		List<Selection<?>> colselct=new ArrayList<Selection<?>>();
		
		Field[] declaredFields = cr.getTableName().getDeclaredFields();
		//declaredFields
		for(Field  field:declaredFields) {
			try {
				if(!"serialVersionUID".equals(field.getName()))
				{
					Selection<Object> alias = root.get(field.getName()).alias(field.getName());
					colselct.add(alias);
				}
			
			}catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		
		
		///******
		query.multiselect(cb.count(root));
		
		
		
		Predicate predicate = cb.conjunction();
		UserSearchQueryCriteriaConsumer searchConsumer = new UserSearchQueryCriteriaConsumer(predicate, cb, root);
		List<SearchCriteria> params = cr.getWheres();
		params.stream().forEach(searchConsumer);
	    predicate = searchConsumer.getPredicate();
	    
	    
	    // Order By
	 		List<Order> orderList = new ArrayList<Order>();	 
	 		for (String order : cr.getOrderby()) {
	 			if(order.indexOf(".")!=-1) {
	 				String[] embd = order.split("\\.");
	 				orderList.add(cb.desc(root.get(embd[0]).get(embd[1])));
	 			}else
	 				orderList.add(cb.desc(root.get(order)));			}
	 		
	 		query.where(predicate).orderBy(orderList);		
	 		TypedQuery<Long> result = em.createQuery(query);
	 		
	 		Map<String, Object> parameters = searchConsumer.getParameters();
	 		if(!parameters.isEmpty()) {
	 			//Set<Entry<String, Object>> entrySet = parameters.entrySet();
	 			for(Entry<String, Object> keyas:parameters.entrySet()) {
	 				//keyas.getValue().getClass()
	 				final Class<?> parameterType = result.getParameter(keyas.getKey()).getParameterType();
	 				if(parameterType.isAssignableFrom(java.util.Date.class) ) {
	 					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	 					DateFormat time=new SimpleDateFormat("HH:mm:ss");
						 //formatter.format(data.get("inceptiondate"));
	 					result.setParameter(keyas.getKey(),formatter.parse(keyas.getValue().toString()+" "+time.format(new Date())));	
	 				}else {
	 					Object value = keyas.getValue();
	 					if(parameterType.isAssignableFrom(Double.class) )
	 						value=Double.parseDouble(keyas.getValue().toString());
	 					else if(parameterType.isAssignableFrom(BigDecimal.class) )
	 						value=new BigDecimal(keyas.getValue().toString());
	 					else if(parameterType.isAssignableFrom(Integer.class) )
	 						value=new Integer(keyas.getValue().toString());
	 					else if(parameterType.isAssignableFrom(Long.class) )
	 						value=new Long(keyas.getValue().toString());
	 					
	 					result.setParameter(keyas.getKey(),value);
	 				}
	 			}
	 		}

	 		result.setFirstResult(limit* offset);
			result.setMaxResults(offset);
			//System.out.println("Query Input: "+reqPrinter.reqPrint(cr.getWheres()));
			list =  result.getResultList();
		 
			 
		
		
		return list;
	}
	
	
	
public List<Long> getCount(SpecCriteria cr,String amendIdCol,Integer limit,Integer offset) throws Exception{


		List<Long> list =null;
		cb=em.getCriteriaBuilder();
		//List<Root> preparedRoot=new ArrayList<Root>();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		
		Root<? extends Class> root = query.from(cr.getTableName());
		List<Selection<?>> colselct=new ArrayList<Selection<?>>();
		
		Field[] declaredFields = cr.getTableName().getDeclaredFields();
		//declaredFields
		for(Field  field:declaredFields) {
			try {
				if(!"serialVersionUID".equals(field.getName()))
				{
					Selection<Object> alias = root.get(field.getName()).alias(field.getName());
					colselct.add(alias);
				}
			}catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		//query.multiselect(colselct);
		
		query.multiselect(cb.count(root));
		
		
		Predicate predicate = cb.conjunction();
		UserSearchQueryCriteriaConsumer searchConsumer = new UserSearchQueryCriteriaConsumer(predicate, cb, root);
		List<SearchCriteria> params = cr.getWheres();
		params.stream().forEach(searchConsumer);
	    predicate = searchConsumer.getPredicate();
	    
	    
	    

	 // Amend Id Max Filter
	 Subquery<Long> amendId = query.subquery(Long.class);
	 Root<? extends Class> ocpm1 = amendId.from(cr.getTableName());
	 if(amendIdCol.indexOf(".")!=-1) {
		 String[] embd = amendIdCol.split("\\.");
		 amendId.select(cb.max(ocpm1.get(embd[0]).get(embd[1])));
	 }else {
		 amendId.select(cb.max(ocpm1.get(amendIdCol)));
	 }
	 
	 //amendId.select(cb.max(ocpm1.get(amendIdCol)));
	 javax.persistence.criteria.Predicate n3=null;
	 Predicate subPredicate = cb.conjunction();
	 {
		   
			UserSearchQueryCriteriaConsumer amendConsumer = new UserSearchQueryCriteriaConsumer(subPredicate, cb, ocpm1);
			List<SearchCriteria> params_sub = cr.getWheres();
			params_sub.stream().forEach(amendConsumer);
			subPredicate = amendConsumer.getPredicate();
		    amendId.where(subPredicate);
		    
		    if(amendIdCol.indexOf(".")!=-1) {
				 String[] embd = amendIdCol.split("\\.");
				 //amendId.select(cb.max(ocpm1.get(embd[0]).get(embd[1])));
				 n3 = cb.equal(root.get(embd[0]).get(embd[1]), amendId);
				 
			 }else {			 
				 n3 = cb.equal(root.get(amendIdCol), amendId);
			 }
		    
		 
		    
	 }
	 //javax.persistence.criteria.Predicate a1 = cb.equal(c.get("cityId"), ocpm1.get("cityId"));
	 
	    
	     
	 	/*	List<Order> orderList = new ArrayList<Order>();	 
	 		for (String order : cr.getOrderby()) {
	 			orderList.add(cb.desc(root.get(order)));
			}
	 		*/
	 
	// Order By
		List<Order> orderList = new ArrayList<Order>();	 
		for (String order : cr.getOrderby()) {
			if(order.indexOf(".")!=-1) {
				String[] embd = order.split("\\.");
				orderList.add(cb.desc(root.get(embd[0]).get(embd[1])));
			}else
				orderList.add(cb.desc(root.get(order)));
		}
	 		query.where(predicate,n3).orderBy(orderList);		
	 		TypedQuery<Long> result = em.createQuery(query);
	 		
	 		Map<String, Object> parameters = searchConsumer.getParameters();
	 		if(!parameters.isEmpty()) {
	 			//Set<Entry<String, Object>> entrySet = parameters.entrySet();
	 			for(Entry<String, Object> keyas:parameters.entrySet()) {
	 				//keyas.getValue().getClass()
	 				final Class<?> parameterType = result.getParameter(keyas.getKey()).getParameterType();
	 				if(parameterType.isAssignableFrom(java.util.Date.class) ) {
	 					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	 					DateFormat time=new SimpleDateFormat("HH:mm:ss");
						 //formatter.format(data.get("inceptiondate"));
	 					result.setParameter(keyas.getKey(),formatter.parse(keyas.getValue().toString()+" "+time.format(new Date())));	
	 				}else
	 				 {
	 					Object value = keyas.getValue();
	 					if(parameterType.isAssignableFrom(Double.class) )
	 						value=Double.parseDouble(keyas.getValue().toString());
	 					else if(parameterType.isAssignableFrom(BigDecimal.class) )
	 						value=new BigDecimal(keyas.getValue().toString());
	 					else if(parameterType.isAssignableFrom(Integer.class) )
	 						value=new Integer(keyas.getValue().toString());
	 					else if(parameterType.isAssignableFrom(Long.class) )
	 						value=new Long(keyas.getValue().toString());
	 					
	 					result.setParameter(keyas.getKey(),value);
	 				}
	 				
	 			}
	 		}

	 		result.setFirstResult(limit* offset);
			result.setMaxResults(offset);
			//System.out.println("Query Input: "+reqPrinter.reqPrint(cr.getWheres()));
			list =  result.getResultList();
		 
		
		
		return list;
	
	}
}
