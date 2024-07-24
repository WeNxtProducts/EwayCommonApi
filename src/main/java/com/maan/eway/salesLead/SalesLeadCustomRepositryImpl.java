package com.maan.eway.salesLead;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.maan.eway.salesLead.bean.EnquiryDetails;
import com.maan.eway.salesLead.bean.SalesLead;

@Repository
public class SalesLeadCustomRepositryImpl implements SalesLeadCustomRepositry {

	@PersistenceContext
	private EntityManager em;
	
	@Override
	public String getMaxLeadId() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<SalesLead> slRoot = cq.from(SalesLead.class);
		Expression<Integer> startIndex = cb.literal(3);
		cq.multiselect(cb.coalesce(cb.sum(cb.max(
				cb.substring(slRoot.get("leadId"), startIndex, cb.length(slRoot.get("leadId"))).as(Integer.class)), 1),
				1000));
		TypedQuery<Integer> query = em.createQuery(cq);
		Integer value = query.getSingleResult();
		return "L-" + value;
	}

	@Override
	public String getMaxEnquiryId() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<EnquiryDetails> enRoot = cq.from(EnquiryDetails.class);
		Expression<Integer> startIndex = cb.literal(3);
		cq.multiselect(cb.coalesce(cb.sum(cb.max(
				cb.substring(enRoot.get("enquiryId"), startIndex, cb.length(enRoot.get("enquiryId"))).as(Integer.class)), 1),
				500));
		TypedQuery<Integer> query = em.createQuery(cq);
		Integer value = query.getSingleResult();
		return "E-" + value;
	}

}
