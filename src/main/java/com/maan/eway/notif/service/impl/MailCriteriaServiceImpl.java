package com.maan.eway.notif.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.CoverMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ProductMaster;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.bean.SectionMaster;
import com.maan.eway.notif.req.MailFramingReq;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;

@Service
public class MailCriteriaServiceImpl {
	
	@PersistenceContext
	private EntityManager em;
	

	@Autowired
	private InsuranceCompanyMasterRepository insrepo;
	
	private Logger log = LogManager.getLogger(MailCriteriaServiceImpl.class);
	
	
	// Product  Insert Criteria
	public List<Map<String, Object>> productCreatedCreateria(MailFramingReq mReq ) {
		List<Map<String, Object>> queryList = new ArrayList<Map<String, Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
		SimpleDateFormat dbf = new SimpleDateFormat("yyyy-MM-dd"); 
		try {
			String productId = mReq.getKeys().get("PRODUCT_ID")==null?"":mReq.getKeys().get("PRODUCT_ID").toString();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
			List<Tuple> list = new ArrayList<Tuple>();
			
			// Find All
			Root<CompanyProductMaster>    c = query.from(CompanyProductMaster.class);
			Root<InsuranceCompanyMaster> i = query.from(InsuranceCompanyMaster.class);
			Root<BranchMaster> b = query.from(BranchMaster.class);
			Root<ProductMaster> p = query.from(ProductMaster.class);
			// Insurance Company Name
			Subquery<String> insName = query.subquery(String.class);
			insName.select(i.get("companyName") );
			javax.persistence.criteria.Predicate i1 = cb.equal(c.get("companyId"), i.get("companyId"));
			insName.where(i1);
			
			// Branch Name
			Subquery<String> branchName = query.subquery(String.class);
			branchName.select(b.get("branchName") );
			javax.persistence.criteria.Predicate b1 = cb.equal(c.get("companyId"), b.get("companyId"));
			insName.where(b1);
			
			// Select
			query.multiselect(c.get("entryDate").alias("ENTRY_DATE") ,
							  c.get("productId").alias("PRODUCT_ID")      , 
							  c.get("companyId").alias("COMPANY_ID")      , 
							  c.get("productName").alias("PRODUCT_NAME")  ,
							  c.get("status").alias("STATUS")  ,
							  i.get("companyName").alias("COMPANY_NAME")  ,
							  b.get("branchName").alias("BRANCH_NAME"));
							
			// Where
			javax.persistence.criteria.Predicate n1 = cb.equal(c.get("productId"),productId);
			javax.persistence.criteria.Predicate n2 = cb.equal(c.get("productName"), p.get("productName"));
			
			query.where(n1,n2);
				
			TypedQuery<Tuple> result = em.createQuery(query);
			list =  result.getResultList(); 
			Tuple content = list.get(0);
			Map<String, Object> queryRes = new HashMap<String, Object>();
			queryRes.put("PRODUCT_ID", content.get("PRODUCT_ID")==null?"":content.get("PRODUCT_ID") );
			queryRes.put("PRODUCT_NAME", content.get("PRODUCT_NAME")==null?"":content.get("PRODUCT_NAME") );
			queryRes.put("COMPANY_NAME", content.get("COMPANY_NAME")==null?"":content.get("COMPANY_NAME") );
			queryRes.put("BRANCH_NAME", content.get("BRANCH_NAME")==null?"":content.get("BRANCH_NAME") );
			queryRes.put("ENTRY_DATE", content.get("ENTRY_DATE")==null?"":content.get("ENTRY_DATE") );
			queryRes.put("COMPANY_ID", content.get("COMPANY_ID")==null?"":content.get("COMPANY_ID") );
			queryRes.put("STATUS", mReq.getStatus() );
			queryList.add(queryRes);
			
		//	queryList.add(content);
			return queryList;
			
		}catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
		}
		return null;
	}


	// Section Insert Criteria
	
	public List<Map<String, Object>> sectionCreatedCriteria(MailFramingReq mReq) {
	
List<Map<String, Object>> queryList = new ArrayList<Map<String, Object>>();
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
SimpleDateFormat dbf = new SimpleDateFormat("yyyy-MM-dd");
try {
	String sectionId = mReq.getKeys().get("SECTION_ID")==null ?"" : mReq.getKeys().get("SECTION_ID").toString();
	//Criteria
	CriteriaBuilder cb = em.getCriteriaBuilder();
	CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
	List<Tuple> list = new ArrayList<Tuple>(); 
	//Find All
	Root<ProductSectionMaster> c = query.from(ProductSectionMaster.class);
	Root<InsuranceCompanyMaster> i = query.from(InsuranceCompanyMaster.class);
	Root<BranchMaster> b = query.from(BranchMaster.class);
	Root<SectionMaster> p = query.from(SectionMaster.class);
	// Insurace Company Name
	Subquery<String> insName = query.subquery(String.class);
	insName.select(i.get("companyName"));
	javax.persistence.criteria.Predicate i1 = cb.equal(c.get("companyId"),i.get("companyId"));
	insName.where(i1);
	//Branch Name
	Subquery<String> branchName = query.subquery(String.class);
	branchName.select(b.get("branchName"));
	javax.persistence.criteria.Predicate b1 = cb.equal(c.get("companyId"), b.get("companyId"));
	insName.where(b1);
	// Select
	query.multiselect(c.get("entryDate").alias("ENTRY_DATE"),
			c.get("productId").alias("PRODUCT_ID"),
			c.get("sectionId").alias("SECTION_ID"),
			c.get("sectionName").alias("SECTION_NAME"),
			c.get("companyId").alias("COMPANY_ID"),			
			c.get("status").alias("STATUS"),
			i.get("companyName").alias("COMPANY_NAME"),
			b.get("branchName").alias("BRANCH_NAME")
			);
	
	//Where 
	javax.persistence.criteria.Predicate n1 = cb.equal(c.get("sectionName"),p.get("sectionName"));
	javax.persistence.criteria.Predicate n2 = cb.equal(c.get("sectionId"),sectionId);
	query.where(n1,n2);
	
	TypedQuery<Tuple> result = em.createQuery(query);
	list = result.getResultList();
	Tuple content = list.get(0);
	Map<String, Object> queryRes = new HashMap<String, Object>();
	queryRes.put("PRODUCT_ID", content.get("PRODUCT_ID")==null?"":content.get("PRODUCT_ID") );
	queryRes.put("SECTION_ID", content.get("SECTION_ID")==null?"":content.get("SECTION_ID") );
	queryRes.put("SECTION_NAME", content.get("SECTION_NAME")==null?"":content.get("SECTION_NAME") );
	queryRes.put("COMPANY_NAME", content.get("COMPANY_NAME")==null?"":content.get("COMPANY_NAME") );
	queryRes.put("BRANCH_NAME", content.get("BRANCH_NAME")==null?"":content.get("BRANCH_NAME") );
	queryRes.put("ENTRY_DATE", content.get("ENTRY_DATE")==null?"":content.get("ENTRY_DATE") );
	queryRes.put("COMPANY_ID", content.get("COMPANY_ID")==null?"":content.get("COMPANY_ID") );
	queryRes.put("STATUS", mReq.getStatus() );
	queryList.add(queryRes);
	
	return queryList;
}
	catch(Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		}
	return null;
	}


	public List<Map<String, Object>> coverCreatedCriteria(MailFramingReq mReq) {
		List<Map<String, Object>> queryList = new ArrayList<Map<String, Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat dbf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			String coverId = mReq.getKeys().get("COVER_ID")==null ?"" : mReq.getKeys().get("COVER_ID").toString();
			//Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
			List<Tuple> list = new ArrayList<Tuple>(); 
			//Find All
			Root<SectionCoverMaster> c = query.from(SectionCoverMaster.class);
			Root<InsuranceCompanyMaster> i = query.from(InsuranceCompanyMaster.class);
			Root<BranchMaster> b = query.from(BranchMaster.class);
			Root<CoverMaster> p = query.from(CoverMaster.class);
			// Insurace Company Name
			Subquery<String> insName = query.subquery(String.class);
			insName.select(i.get("companyName"));
			javax.persistence.criteria.Predicate i1 = cb.equal(c.get("companyId"),i.get("companyId"));
			insName.where(i1);
			//Branch Name
			Subquery<String> branchName = query.subquery(String.class);
			branchName.select(b.get("branchName"));
			javax.persistence.criteria.Predicate b1 = cb.equal(c.get("companyId"), b.get("companyId"));
			insName.where(b1);
			// Select
			query.multiselect(c.get("entryDate").alias("ENTRY_DATE"),
					c.get("productId").alias("PRODUCT_ID"),
					c.get("sectionId").alias("SECTION_ID"),
					c.get("coverId").alias("COVER_ID"),				
					c.get("coverName").alias("COVER_NAME"),
					c.get("companyId").alias("COMPANY_ID"),			
					c.get("status").alias("STATUS"),
					i.get("companyName").alias("COMPANY_NAME"),
					b.get("branchName").alias("BRANCH_NAME")
					);
			
			//Where 
			javax.persistence.criteria.Predicate n1 = cb.equal(c.get("coverName"),p.get("coverName"));
			javax.persistence.criteria.Predicate n2 = cb.equal(c.get("coverId"),coverId);
			query.where(n1,n2);
			
			TypedQuery<Tuple> result = em.createQuery(query);
			list = result.getResultList();
			Tuple content = list.get(0);
			Map<String, Object> queryRes = new HashMap<String, Object>();
			queryRes.put("PRODUCT_ID", content.get("PRODUCT_ID")==null?"":content.get("PRODUCT_ID") );
			queryRes.put("SECTION_ID", content.get("SECTION_ID")==null?"":content.get("SECTION_ID") );
			queryRes.put("COVER_ID", content.get("COVER_ID")==null?"":content.get("COVER_ID") );	
			queryRes.put("COVER_NAME", content.get("COVER_NAME")==null?"":content.get("COVER_NAME") );
			queryRes.put("COMPANY_NAME", content.get("COMPANY_NAME")==null?"":content.get("COMPANY_NAME") );
			queryRes.put("BRANCH_NAME", content.get("BRANCH_NAME")==null?"":content.get("BRANCH_NAME") );
			queryRes.put("ENTRY_DATE", content.get("ENTRY_DATE")==null?"":content.get("ENTRY_DATE") );
			queryRes.put("COMPANY_ID", content.get("COMPANY_ID")==null?"":content.get("COMPANY_ID") );
			queryRes.put("STATUS", mReq.getStatus() );
			queryList.add(queryRes);
			
			return queryList;
		}
			catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --> " + e.getMessage());
				}
			return null;
			}
	
}
