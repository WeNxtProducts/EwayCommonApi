package com.maan.eway.salesLead;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.maan.eway.bean.SalesLead;

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

}
