package com.maan.eway.chartaccount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.ProductTaxSetup;
import com.maan.eway.bean.ReportJasperConfigMaster;

@Component
@Transactional
public class JpqlQueryServiceImpl {
	
	
	@PersistenceContext
	private EntityManager em;
	
	Logger logger = LogManager.getLogger(JpqlQueryServiceImpl.class);
	
	
	public List<ChartAccountChildMaster> getChildChartAccountData(Integer companyId, Integer productId, List<String> sectionIds,
			ChartParentMaster cpmm) {
		try {
			
			String stringQuery ="select c from ChartAccountChildMaster c where c.id.companyId =:companyId and c.id.productId=:productId "
					+ "and c.id.sectionId in (:sectionId) and c.id.chartId=:chartId and c.status=:status and c.id.amendId=(select max(cc.id.amendId) "
					+ "from ChartAccountChildMaster cc where cc.id.companyId= c.id.companyId and cc.id.productId=c.id.productId and "
					+ "cc.id.sectionId=c.id.sectionId and cc.id.chartId=c.id.chartId and cc.id.coverId=c.id.coverId and CURRENT_DATE between "
					+ "cc.effectiveStartDate and cc.effectiveEndDate)";
			
			@SuppressWarnings("unchecked")
			List<ChartAccountChildMaster> chilldMaster =em.createQuery(stringQuery).setParameter("companyId", companyId).setParameter("productId", productId)
			.setParameter("sectionId", sectionIds).setParameter("chartId", cpmm.getChatParentId().getChartId())
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
	
	public ProductTaxSetup getProductTaxSetup(Integer companyId, List<Integer> coverIds,Integer productId,String branchCode,String taxFor) {
		try {
			String jpqlQuery="select pts from ProductTaxSetup pts where pts.companyId=:companyId and pts.productId=:productId and "
					+ "(pts.branchCode=:branchCode or branchCode=:commonBranch) and pts.taxFor=:taxFor and pts.taxId in(:taxId) and CURRENT_DATE between pts.effectiveDateStart and "
					+ "pts.effectiveDateEnd";
			
			@SuppressWarnings("unchecked")
			List<ProductTaxSetup> list = (List<ProductTaxSetup>) em.createQuery(jpqlQuery).setParameter("companyId", companyId.toString()).setParameter("productId", productId)
			.setParameter("branchCode", branchCode).setParameter("taxFor", taxFor).setParameter("taxId", coverIds)
			.setParameter("commonBranch", "99999")
			.getResultList();
			
			return list.get(0);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<ChartParentMaster> getChartParentMasterDetails(Integer companyId){
		List<ChartParentMaster> list =null;
		try {
			String sql ="select cpm from ChartParentMaster cpm where cpm.chatParentId.companyId=:companyId and cpm.status=:status "
					+ "and CURRENT_DATE between cpm.effectiveStartDate and cpm.effectiveEndDate order by cpm.displayOrder";
			list =(List<ChartParentMaster>) em.createQuery(sql).setParameter("companyId", companyId).setParameter("status", "Y").getResultList();
			
			List<ChartParentMaster> filterList = new ArrayList<ChartParentMaster>();
			
				list.stream().collect(Collectors.groupingBy(p ->p.getChatParentId().getChartId()))
				.forEach((key,value) ->{
					ChartParentMaster cpm =	value.stream().collect(Collectors.maxBy((a,b) ->a.getChatParentId().getAmendId().compareTo(a.getChatParentId().getAmendId())))
					.get();
					filterList.add(cpm);
					});
				
				filterList.sort(Comparator.comparing(ChartParentMaster :: getDisplayOrder));
				
			return filterList;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<ReportJasperConfigMaster> getJasperReportConfigMaster(String companyId, Integer productId, Integer reportId) {
		try {
			String sql ="select rjm from ReportJasperConfigMaster rjm  where rjm.id.companyId=:companyId and rjm.id.productId=:productId "
					+ "and rjm.id.reportId=:reportId and rjm.status=:status and rjm.id.amendId=(select max(rjmm.id.amendId) from ReportJasperConfigMaster rjmm where "
					+ "rjmm.id.companyId=rjm.id.companyId and rjmm.id.productId=rjm.id.productId and rjmm.id.reportId=rjm.id.reportId and rjmm.status=rjm.status and sysdate() between "
					+ "rjmm.effectiveDateStart and rjmm.effectiveDateEnd)"
					+ "";
			
			@SuppressWarnings("unchecked")
			List<ReportJasperConfigMaster> list =em.createQuery(sql).setParameter("companyId", companyId).setParameter("productId", productId).setParameter("reportId", reportId)
			.setParameter("status", "Y")
			.getResultList();
			
			return list;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

	
	public Object getCompanyLogo(String companyId) {
		Object companyLogo="";
		try {
			
			String sqlquery ="select icm.companyLogo from InsuranceCompanyMaster icm where  icm.companyId=:companyId and icm.status=:status and "
					+ "icm.amendId=(select max(icmm.amendId) from InsuranceCompanyMaster icmm where icmm.companyId=icm.companyId and "
					+ "icmm.status=icm.status)";
			companyLogo = em.createQuery(sqlquery).setParameter("companyId", companyId).setParameter("status", "Y").getSingleResult();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return companyLogo;
	}
	

}
