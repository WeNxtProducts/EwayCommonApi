package com.maan.eway.master.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.ProductSectionAdditionalInfoMaster;
import com.maan.eway.master.req.GetOptedSectionAdditionalInfoReq;
import com.maan.eway.master.res.GetOptedSectionAdditionalInfoRes;
import com.maan.eway.master.service.ProductSectionAdditionalInfoMasterService;

@Service
@Transactional
public class ProductSectionAdditionalInfoMasterServiceImple implements ProductSectionAdditionalInfoMasterService{

	@PersistenceContext
	EntityManager em;
	
	DozerBeanMapper mapper = new DozerBeanMapper();
	
	
	@Override
	public List<GetOptedSectionAdditionalInfoRes> getOptedSectionAdditionalInfo(GetOptedSectionAdditionalInfoReq req) {	 //
		List<GetOptedSectionAdditionalInfoRes> resList = new ArrayList<GetOptedSectionAdditionalInfoRes>();
		List<ProductSectionAdditionalInfoMaster> list = new ArrayList<ProductSectionAdditionalInfoMaster>();
	
		try{
			
		if(req.getOptedSectionIds().size()>0) {
			for(Integer secId : req.getOptedSectionIds()) {
			
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ProductSectionAdditionalInfoMaster> query = cb.createQuery(ProductSectionAdditionalInfoMaster.class);

				Root<ProductSectionAdditionalInfoMaster> b = query.from(ProductSectionAdditionalInfoMaster.class);

				query.select(b);

				Subquery<Long> amendId = query.subquery(Long.class);
				Root<ProductSectionAdditionalInfoMaster> ocpm1 = amendId.from(ProductSectionAdditionalInfoMaster.class);
				amendId.select(cb.max(ocpm1.get("amendId")));
				Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
				Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
				Predicate a6 = cb.equal(ocpm1.get("productId"), b.get("productId"));
				Predicate a7 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));

				amendId.where(a1,a2,a6,a7);

				Predicate n1 = cb.equal(b.get("amendId"), amendId);
				Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
				Predicate n3 = cb.equal(b.get("productId"), req.getProductId());
				Predicate n6 = cb.equal(b.get("sectionId"), secId);
				Predicate n7 = cb.equal(b.get("status"), "Y");
				Predicate n4 = cb.lessThanOrEqualTo(b.get("effectiveDateStart"), new Date());
				Predicate n5 = cb.greaterThanOrEqualTo(b.get("effectiveDateEnd"), new Date());
			
				query.where(n1,n2,n3,n4,n5,n6, n7);
			
				TypedQuery<ProductSectionAdditionalInfoMaster> result = em.createQuery(query);
				list = result.getResultList();		
				
				if(list.size()>0) {
					ProductSectionAdditionalInfoMaster data = list.get(0);
					GetOptedSectionAdditionalInfoRes res = new GetOptedSectionAdditionalInfoRes();
					mapper.map(data, res);
					res.setAddDetailYn(data.getAddDetailYn()==null?"":data.getAddDetailYn());
					res.setGetallUrl(data.getGetallUrl()==null?"":data.getGetallUrl());
					res.setGetUrl(data.getGetUrl()==null?"":data.getGetUrl());
					res.setJsonPath(data.getJsonPath()==null?"":data.getJsonPath());
					res.setRemarks(data.getRemarks()==null?"":data.getRemarks());
					res.setSaveUrl(data.getSaveUrl()==null?"":data.getSaveUrl());
					res.setSectionId(data.getSectionId()==null?0:data.getSectionId());
					res.setSectionName(data.getSectionName()==null?"":data.getSectionName());
					res.setStatus(data.getStatus()==null?"":data.getStatus());
					resList.add(res);
				}
				
			}
		}	
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return resList;
	}

	
}
