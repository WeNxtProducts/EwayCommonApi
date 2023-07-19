package com.maan.eway.jasper.service.impl;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.jasper.req.JasperReportDocReq;
import com.maan.eway.jasper.res.JasperDocumentRes;
import com.maan.eway.jasper.service.JasperService;
import com.maan.eway.repository.BranchMasterRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.thread.GetFileFromPath;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class JasperServiceImpl implements JasperService {

	@Autowired
	private JasperConfiguration config;

	@Autowired
	private HomePositionMasterRepository homeRepo;

	@Autowired
	private BranchMasterRepository branchRepo ;
	
	@Value(value = "${travel.productId}")
	private String travelProductId;
	
	@Value(value = "${jasper.compile.path}")
	private String jasperCompilePath;
	
	@Value(value = "${report.file.path}")
	private String policyReportPath;
	
	
	@PersistenceContext
	private EntityManager em;


	@Override
	public JasperDocumentRes policyform(JasperDocumentReq req) {
		JasperDocumentRes res = new JasperDocumentRes();
		String getPdfOutFilePath = "";
		try {
			
			HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo());
			CompanyProductMaster product =  getCompanyProductMasterDropdown(homeData.getCompanyId() , homeData.getProductId().toString());

			Map<String, Object> input = new HashMap<String, Object>();
			
			// String directoryname=null ;
			// File Save Path
			String filePath = null;

			if (StringUtils.isNotBlank(homeData.getPolicyNo())) {
				input.put("pvPolicyNo", homeData.getPolicyNo());
				input.put("pvImagepath", config.getImagePath());

				// directoryname=homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "");
				filePath = config.getPolicyPath() + "pdf";
				getPdfOutFilePath = filePath + "/" + homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "")
						+ ".pdf";

			} else {
				input.put("QuoteNo", req.getQuoteNo());
				input.put("imagePath", config.getImagePath());

				// directoryname=req.getQuoteNo().replaceAll("[\\/:*?\"<>|]*", "");
				filePath = config.getDraftPath() + "pdf";
				getPdfOutFilePath = filePath + "/" + req.getQuoteNo() + ".pdf";
			}
			
			if (null != input && input.size() > 0) {
				
				File theDir = new File(filePath);
				if (!theDir.exists()) {
					theDir.mkdirs();
				}

//				if (product.getMotorYn().equalsIgnoreCase("A")) {
//					res = getJasperPdfFile("/report/jasper/PersonalPlus.jrxml", getPdfOutFilePath, input);
//				}
//
//				else 
				if (product.getMotorYn().equalsIgnoreCase("H") && travelProductId.equals(homeData.getProductId().toString())) {
					Map<String, Object> input2 = new HashMap<String, Object>();
					input2.put("pvImagePath", config.getImagePath());
					input2.put("pvPolicyNo", homeData.getPolicyNo());
					res = getJasperPdfFile("/report/jasper/TravelReport.jrxml", getPdfOutFilePath, input2);
					
				} else if (product.getMotorYn().equalsIgnoreCase("M")) {
					res = getJasperPdfFile("/report/jasper/MotorPrivate.jrxml", getPdfOutFilePath, input);
				} else {
					Map<String, Object> input2 = new HashMap<String, Object>();
					input2.put("pvQuoteNo", req.getQuoteNo());
					input2.put("pvImagepath", config.getImagePath());
					input2.put("pvSubReportPath",jasperCompilePath + "/report/jasper/");
					String obj[] =new String[2];
					obj[0]= jasperCompilePath + "/report/jasper/CoverageDetails.jrxml";
					obj[1]= jasperCompilePath +"/report/jasper/SectionDetails.jrxml";              // for linux system
				//	obj[0]=class_path +"/report/jasper/CoverageDetails.jrxml";
				//	obj[1]=class_path +"/report/jasper/SectionDetails.jrxml";              // for linux system
				//	obj[2]=class_path +"/report/jasper/VehicleDetails.jrxml";
                for(String s :obj) {
					// String jrxml_path=s.replace(".jasper", ".jrxml");
					String path = JasperCompileManager.compileReportToFile(s);
					System.out.println("Jasper compileToReport path" +path);
					}
					
					res = getJasperPdfFile("/report/jasper/EwaySchedule.jrxml", getPdfOutFilePath, input2);
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	private JasperDocumentRes getJasperPdfFile(String jasperPath, String filePath, Map<String, Object> input) {
		JasperDocumentRes res = new JasperDocumentRes();
		Connection connection = null;
		try {
			connection = config.getDataSourceForJasper().getConnection();
			InputStream inputStream = this.getClass().getResourceAsStream(jasperPath);
			JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, input, connection);
			// JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,input);

			/* servletRequest.getRealPath(getPdfOutFilePath) */;
			// filePath=filePath.replaceAll("%20", " ");
			System.out.println("filePath name is ====> " + filePath);
			JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
			// res.setPdfoutfilepath(commonPath+"/"+getPdfOutFilePath);

			GetFileFromPath path = new GetFileFromPath(filePath);
			res.setPdfoutfile(path.call().getImgUrl());
			res.setPdfoutfilepath(filePath);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return res;
	}

	@Override
	public JasperDocumentRes proposalform(JasperDocumentReq req) {
		JasperDocumentRes res = null;
		String getPdfOutFilePath = "";
		try {
			HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo());
			CompanyProductMaster product =  getCompanyProductMasterDropdown(homeData.getCompanyId() , homeData.getProductId().toString());

			Map<String, Object> input = new HashMap<String, Object>();
			input.put("QuoteNo", req.getQuoteNo());
			input.put("imagePath", config.getImagePath());

			// HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo()) ;
			if (product.getMotorYn().equalsIgnoreCase("H")  && travelProductId.equals(req.getProductId())) {
				if (null != input && input.size() > 0) {

					// String directoryname=null ;
					// File Save Path
					String filePath = null;

					// directoryname=req.getQuoteNo().replaceAll("[\\/:*?\"<>|]*", "");
					filePath = config.getProposalPath() + "pdf";
					getPdfOutFilePath = filePath + "/" + req.getQuoteNo() + ".pdf";

					File theDir = new File(filePath);
					if (!theDir.exists()) {
						theDir.mkdirs();
					}
					res = getJasperPdfFile("/report/jasper/TravelReport.jrxml", getPdfOutFilePath, input);
				}
			} else if (product.getMotorYn().equalsIgnoreCase("M") ) {
				// Temporary
				res = getJasperPdfFile("/report/jasper/MotorPrivate.jrxml", getPdfOutFilePath, input);
				String filePath = config.getPolicyPath() + "pdf/MOTOR PRIVATE.pdf";
				GetFileFromPath path = new GetFileFromPath(filePath);
				res.setPdfoutfile(path.call().getImgUrl());
				res.setPdfoutfilepath(filePath);
			} else {
				
				input.put("pvSubReportPath", "/report/jasper/");
				res = getJasperPdfFile("/report/jasper/EwaySchedule.jrxml", getPdfOutFilePath, input);
				String filePath = config.getPolicyPath() + "pdf/EWAY SCHEDULE.pdf";
				GetFileFromPath path = new GetFileFromPath(filePath);
				res.setPdfoutfile(path.call().getImgUrl());
				res.setPdfoutfilepath(filePath);
             }
				 
//				res = new JasperDocumentRes();
//				String filePath = config.getPolicyPath() + "pdf/PERSONAL PLUS.pdf";
//			    GetFileFromPath path = new GetFileFromPath(filePath);
//				res.setPdfoutfile(path.call().getImgUrl());
//				res.setPdfoutfilepath(filePath);

//			} else {
//				res = new JasperDocumentRes();
//				String filePath = config.getPolicyPath() + "pdf/GROUP PERSONAL ACCIDENT.pdf";
//				GetFileFromPath path = new GetFileFromPath(filePath);
//				res.setPdfoutfile(path.call().getImgUrl());
//				res.setPdfoutfilepath(filePath);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public synchronized CompanyProductMaster getCompanyProductMasterDropdown(String companyId, String productId) {
		CompanyProductMaster product = new CompanyProductMaster();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			;
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);
			List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
			// Find All
			Root<CompanyProductMaster> c = query.from(CompanyProductMaster.class);
			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productName")));

			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, a3);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a4 = cb.equal(c.get("productId"), ocpm2.get("productId"));
			Predicate a5 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a4, a5, a6);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), companyId);
			Predicate n5 = cb.equal(c.get("productId"), productId);
			query.where(n1, n2, n3, n4, n5).orderBy(orderList);
			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			product = list.size() > 0 ? list.get(0) :null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return product;
	}

	@Override
	public JasperDocumentRes policyreportform(JasperReportDocReq req) {
		JasperDocumentRes res = null;
		String getPdfOutFilePath = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy"); 
		try {
			CompanyProductMaster product =  getCompanyProductMasterDropdown(req.getInsuranceId() , req.getProductId());
			List<BranchMaster> branchList = branchRepo.findTopByCompanyIdAndBranchCodeOrderByAmendIdDesc(req.getInsuranceId() , req.getBranchCode());
 			String branchName = branchList.size() > 0  ?  branchList.get(0).getBranchName() : req.getBranchCode() ;
 			
			Map<String, Object> input = new HashMap<String, Object>();
			input.put("pvStartDate", sdf.format(req.getStartDate()));
			input.put("pvImagePath", config.getImagePath());
			input.put("pvEndDate", sdf.format(req.getEndDate()));
			input.put("pvBranch", req.getBranchCode());
			input.put("pvLoginId", req.getLoginId());
			
			getPdfOutFilePath =  policyReportPath + "pdf/" + req.getLoginId() +":"+"Branch-" + branchName   +"(" + sdf2.format(req.getStartDate()) + "To" + sdf2.format(req.getEndDate())  + ") Policy Report.pdf";
			
			if (product.getMotorYn().equalsIgnoreCase("H")  && travelProductId.equals(req.getProductId())) {
				res = getJasperPdfFile("/report/jasper/EwayPremiumReport.jrxml", getPdfOutFilePath, input);
				
				
			} else if (product.getMotorYn().equalsIgnoreCase("M") ) {
				
				res = getJasperPdfFile("/report/jasper/EwayPremiumReport.jrxml", getPdfOutFilePath, input);
				
			} else {
				
				res = getJasperPdfFile("/report/jasper/EwayPremiumReport.jrxml", getPdfOutFilePath, input);
				
             }
				 

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
}
