package com.maan.eway.chartaccount;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.maan.eway.bean.PolicyCoverData;

@Component
@Transactional
public class JpqlQueryServiceImpl {
	
	
	@PersistenceContext
	private EntityManager em;
	
	Logger logger = LogManager.getLogger(JpqlQueryServiceImpl.class);
	
	
	public List<ChartAccountChildMaster> getChildChartAccountData(Integer companyId, Integer productId, Integer sectionId,
			ChartParentMaster cpmm) {
		try {
			
			String stringQuery ="select c from ChartAccountChildMaster c where c.id.companyId =:companyId and c.id.productId=:productId "
					+ "and c.id.sectionId=:sectionId and c.id.chartId=:chartId and c.status=:status and c.id.amendId=(select max(cc.id.amendId) "
					+ "from ChartAccountChildMaster cc where cc.id.companyId= c.id.companyId and cc.id.productId=c.id.productId and "
					+ "cc.id.sectionId=cc.id.sectionId and cc.id.chartId=c.id.chartId and cc.id.coverId=c.id.coverId and sysdate() between "
					+ "cc.effectiveStartDate and cc.effectiveEndDate)";
			
			@SuppressWarnings("unchecked")
			List<ChartAccountChildMaster> chilldMaster =em.createQuery(stringQuery).setParameter("companyId", companyId).setParameter("productId", productId)
			.setParameter("sectionId", sectionId).setParameter("chartId", cpmm.getChatParentId().getChartId())
			.setParameter("status", "Y").getResultList();
			
			return chilldMaster;
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<PolicyCoverData> getPolicyCoverDataPremium(String quoteNo,List<Integer> coverIds)
	{
		try {
			
			String sqlString ="select pcd from PolicyCoverData pcd where pcd.quoteNo=:quoteNo and pcd.coverId in(:coverId)";
			List<PolicyCoverData> coverDatas =(List<PolicyCoverData>) em.createQuery(sqlString)
					.setParameter("quoteNo", quoteNo).setParameter("coverId", coverIds).getResultList();
			return coverDatas;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<PolicyCoverData> getPolicyCoverDataTax(String quoteNo,List<Integer> coverIds)
	{
		try {
			
			String sqlString ="select pcd from PolicyCoverData pcd where pcd.quoteNo=:quoteNo and pcd.taxId in(:taxId)";
			List<PolicyCoverData> coverDatas =(List<PolicyCoverData>) em.createQuery(sqlString)
					.setParameter("quoteNo", quoteNo).setParameter("taxId", coverIds).getResultList();
			return coverDatas;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Integer updateBrokerCommission(String quoteNo,Double commission)
	{
		try {
			
			String sqlString ="update HomePositionMaster hpm set hpm.commission=:commission where hpm.quoteNo=:quoteNo";
			Integer count =	em.createQuery(sqlString)
					.setParameter("quoteNo", quoteNo).setParameter("commission", new BigDecimal(commission.toString())).executeUpdate();
			
			logger.info("Broker Commission update for this quote No :"+quoteNo+" || Commission : "+commission+"");
			return count;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

}
