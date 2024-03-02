package com.maan.eway.jasper.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ReportJasperConfigMaster;
import com.maan.eway.chartaccount.JpqlQueryServiceImpl;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.jasper.req.JasperReportDocReq;
import com.maan.eway.jasper.req.JasperScheduleReq;
import com.maan.eway.jasper.req.PdfJsonReq;
import com.maan.eway.jasper.req.PremiumReportReq;
import com.maan.eway.jasper.res.CreditNoteRes;
import com.maan.eway.jasper.res.JasperDocumentRes;
import com.maan.eway.jasper.res.MotorCoverNoteRes;
import com.maan.eway.jasper.res.MotorPrivateRes;
import com.maan.eway.jasper.res.PremiumReportRes;
import com.maan.eway.jasper.res.TaxInvoiceRes;
import com.maan.eway.jasper.res.TravelReportRes;
import com.maan.eway.jasper.service.JasperService;
import com.maan.eway.repository.BranchMasterRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.thread.GetFileFromPath;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;

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
	
	@Value(value = "${report.file.path}")
	private String policyReportPath;
	
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private JasperCustomServiceImple jasperCustomeImple;
	
	@Autowired
	private JpqlQueryServiceImpl jpqlQueryServiceImpl;
	
	@Autowired
	private Gson gson;

	Logger log = LogManager.getLogger(JasperServiceImpl.class);

	@Override
	public JasperDocumentRes policyform(JasperDocumentReq req) {
		JasperDocumentRes res = new JasperDocumentRes();
		try {
			
			HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo());
			CompanyProductMaster product =  getCompanyProductMasterDropdown(homeData.getCompanyId() , homeData.getProductId().toString());

			Map<String, Object> input = new HashMap<String, Object>();
			String filePath = null;String Imagepath;
			log.info("OS Using ==> "+System.getProperty("os.name").toLowerCase());
			if(System.getProperty("os.name").toLowerCase().contains("windows")) {
				Imagepath = config.getImagePath().substring(1, config.getImagePath().length()-0);
			}else {
				Imagepath = config.getImagePath().replaceAll("\\\\", "/");
			}
			

			if (StringUtils.isNotBlank(homeData.getPolicyNo())) {
				input.put("pvPolicyNo", homeData.getPolicyNo());
				input.put("pvImagepath", Imagepath);
				filePath = config.getPolicyPath() + "pdf";

			} else {
				input.put("QuoteNo", req.getQuoteNo());
				input.put("pvImagepath", Imagepath);
				filePath = config.getDraftPath() + "pdf";
			}
			
			if (null != input && input.size() > 0) {
				File theDir = new File(filePath);
				if (!theDir.exists()) {
					theDir.mkdirs();
				}
				if(StringUtils.isBlank(homeData.getPolicyNo()) && homeData.getProductId()==5) {
					if("Y".equalsIgnoreCase(req.getBrokerQuoteYn()) && !"100019".equalsIgnoreCase(homeData.getCompanyId())) {
						Map<String,Object> brokerQuotation = jasperCustomeImple.getMotorBrokerQuotation(homeData.getQuoteNo());
						String jsonString = gson.toJson(brokerQuotation);
						String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile")+homeData.getQuoteNo().replaceAll("[\\/:*?\"<>|]*", "");
						res = getCommonJasperPdfFileByJson("/report/jasper/EwayBrokerQuotation.jrxml", jasperSaveLocation, jsonString, input, "- BrokerQuotation.json");
					}else {
						MotorPrivateRes motPrivateRes = jasperCustomeImple.getMotorPrivate(homeData.getPolicyNo(),homeData.getQuoteNo());
						String JsonString = gson.toJson(motPrivateRes);
						String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile")+homeData.getQuoteNo().replaceAll("[\\/:*?\"<>|]*", "");
						res = getCommonJasperPdfFileByJson("/report/jasper/MotorPrivate.jrxml", jasperSaveLocation, JsonString, input, "- MotorPrivate.json");
					}
				}else if (product.getMotorYn().equalsIgnoreCase("H") && travelProductId.equals(homeData.getProductId().toString())) {
						Map<String, Object> input2 = new HashMap<String, Object>();
						input2.put("pvImagePath", config.getImagePath().substring(1,config.getImagePath().length()-0));
						input2.put("pvSubReportPath",config.getJasperFilePath().replaceAll("%20", " ")+ "report/jasper/");
						String obj= config.getJasperFilePath().replaceAll("%20", " ") + "report/jasper/EwayTravelSubReport.jrxml";
								String jrxml_path=obj.replace(".jasper", ".jrxml");
								String path = JasperCompileManager.compileReportToFile(jrxml_path);
								System.out.println("Jasper compileToReport path" +path);		
						TravelReportRes travelRes = jasperCustomeImple.getTravelReport(homeData.getPolicyNo());
						String jsonString = gson.toJson(travelRes);
						String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile")+homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "");
						res = getCommonJasperPdfFileByJson("/report/jasper/EwayTravelReport.jrxml", jasperSaveLocation, jsonString, input2, "- TravelReport.json");
				} else if (product.getMotorYn().equalsIgnoreCase("M") && !"46".equalsIgnoreCase(homeData.getProductId().toString())) {
					String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile")+homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "");
					if(homeData.getEndtCount() != 0 && !homeData.getPolicyNo().equalsIgnoreCase(homeData.getOriginalPolicyNo()) && "E".equalsIgnoreCase(req.getEndorsementType())) {
						Map<String,Object> MotorEndorsementScheduleRes = jasperCustomeImple.getMotorEndorsementSchedule(homeData.getPolicyNo());
						String jsonString = gson.toJson(MotorEndorsementScheduleRes);
						res = getCommonJasperPdfFileByJson("/report/jasper/MotorEndorsementSchedule.jrxml", jasperSaveLocation, jsonString, input, "- MotorEndorsementSchedule.json");
					}else if("100004".equalsIgnoreCase(homeData.getCompanyId())){ // MADISON MOTOR
						List<Map<String,Object>> MadisonMotorSchedule = jasperCustomeImple.getMadisonMotorSchedule(homeData.getPolicyNo());
						String jsonString = gson.toJson(MadisonMotorSchedule);
						res = getCommonJasperPdfFileByJson("/report/jasper/EwayMadisonMotorSchedule.jrxml", jasperSaveLocation, jsonString, input, "- EwayMadisonMotorSchedule.json");
					}else {
						MotorPrivateRes motPrivateRes = jasperCustomeImple.getMotorPrivate(homeData.getPolicyNo(),"");
						String JsonString="";
						String JasperName = "MotorPrivate";
							if("100019".equalsIgnoreCase(homeData.getCompanyId())){		// UIA
								if("Y".equalsIgnoreCase(req.getStrickerYn())) {
									JsonString = gson.toJson(motPrivateRes.getVehicleDetails());
									JasperName = "Schedule_UIA";
								}else {
									JsonString = gson.toJson(motPrivateRes);
									JasperName = "UgandaMotorSchedule";
								}
							}
						res = getCommonJasperPdfFileByJson("/report/jasper/"+JasperName+".jrxml", jasperSaveLocation, JsonString, input, "- MotorPrivate.json");
					}
				}else if(product.getMotorYn().equalsIgnoreCase("A")&& "42".equalsIgnoreCase(homeData.getProductId().toString())) {
					Map<String,Object> input2 = new HashMap<>();
					input2.put("pvImagePath", Imagepath);
					Map<String,Object> cyberInsurance = jasperCustomeImple.getCyberInsurance(homeData.getPolicyNo());
					String jsonString = gson.toJson(cyberInsurance);
					String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile")+homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "");
					res = getCommonJasperPdfFileByJson("/report/jasper/CyberInsurance.jrxml", jasperSaveLocation, jsonString, input2, "- CyberInsurance.json");
				}else if(product.getMotorYn().equalsIgnoreCase("M") && "46".equalsIgnoreCase(homeData.getProductId().toString())){
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("pvImagePath", config.getImagePath().substring(1,config.getImagePath().length()-0));
					String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile")+homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "");
					MotorCoverNoteRes MotorCoverNote = jasperCustomeImple.getMotorCoverNote(homeData.getPolicyNo());
					String jsonString = gson.toJson(MotorCoverNote);
					res = getCommonJasperPdfFileByJson("/report/jasper/EwayMotorCoverNote.jrxml",jasperSaveLocation,jsonString,map,"- MotorCoveNote.json");
				}else {
					Map<String, Object> input2 = new HashMap<String, Object>();
					input2.put("pvImagepath", Imagepath);
					input2.put("pvSubReportPath",config.getJasperFilePath().replaceAll("%20", " ")  + "report/jasper/");
					String obj[] =new String[2];
					obj[0] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/CoverageDetails.jrxml";
					obj[1] = config.getJasperFilePath().replaceAll("%20", " ")+"report/jasper/SectionDetails.jrxml";		 // for linux system
                for(String s :obj) {
					String jrxml_path=s.replace(".jasper", ".jrxml");
					String path = JasperCompileManager.compileReportToFile(jrxml_path);
					System.out.println("Jasper compileToReport path" +path);
					}
	                Map<String,Object> EwaySchedule = jasperCustomeImple.getEwaySchedule(homeData.getQuoteNo());
					String jsonString = gson.toJson(EwaySchedule);
					String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile")+homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "");
					res = getCommonJasperPdfFileByJson("/report/jasper/EwaySchedule.jrxml", jasperSaveLocation, jsonString, input2, "- EwaySchedule.json");
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
					e.printStackTrace();
				}
		}
		return res;
	}
	private JasperDocumentRes getJasperPdfFileFromJson(String jasperPath, String filePath, Map<String, Object> input, String jsonFile) {
		JasperDocumentRes res = new JasperDocumentRes();
		InputStream inputStream=null;
		try {
			
			File file = new File(policyReportPath.replaceAll("PolicyReport", "IllustrationFile")+jsonFile);
			
			JsonDataSource ds=new JsonDataSource(file);
			inputStream = this.getClass().getResourceAsStream(jasperPath);
			JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, input, ds);
			System.out.println("filePath name is ====> " + filePath);
			JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
			GetFileFromPath path = new GetFileFromPath(filePath);
			res.setPdfoutfile(path.call().getImgUrl());
			res.setPdfoutfilepath(filePath);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(inputStream!=null)
				try {
					inputStream.close();
				} catch (IOException e) {
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

	@Override
	public JasperDocumentRes taxInvoice(String quoteNo) {
		JasperDocumentRes res = new JasperDocumentRes();
		HomePositionMaster homeData = homeRepo.findByQuoteNo(quoteNo);
		try {
			if(StringUtils.isNotBlank(homeData.getPolicyNo())) {
				Map<String,Object> map = new HashMap<String,Object>();
				log.info("OS Using ==> "+System.getProperty("os.name").toLowerCase());
				if(System.getProperty("os.name").toLowerCase().contains("windows")) {
					map.put("pvImagepath",  config.getImagePath().substring(1, config.getImagePath().length()-0));
				}else {
					map.put("pvImagepath",config.getImagePath().replaceAll("\\\\", "/"));
				}
				TaxInvoiceRes taxRes = jasperCustomeImple.getTaxInvoiceRes(homeData.getPolicyNo());
				String JsonString = gson.toJson(taxRes);
				String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile")+homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "");
				String jasperName="";
				if(homeData.getCompanyId().equalsIgnoreCase("100004")) {
					jasperName = "/report/jasper/EwayMadisonTaxInvoice.jrxml";
				}else {
					jasperName = "/report/jasper/EwayTaxInvoice.jrxml";
				}
				res = getCommonJasperPdfFileByJson(jasperName, jasperSaveLocation, JsonString, map, "- TaxInvoice.json");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public JasperDocumentRes creditNote(String quoteNo) {
		JasperDocumentRes res = new JasperDocumentRes();
		HomePositionMaster homeData = homeRepo.findByQuoteNo(quoteNo);
		try {
			if(StringUtils.isNotBlank(homeData.getPolicyNo())) {
				Map<String,Object> input = new HashMap<String,Object>();
				log.info("OS Using ==> "+System.getProperty("os.name").toLowerCase());
				if(System.getProperty("os.name").toLowerCase().contains("windows")) {
					input.put("pvImagepath",  config.getImagePath().substring(1, config.getImagePath().length()-0));
				}else {
					input.put("pvImagepath",config.getImagePath().replaceAll("\\\\", "/"));
				}
				CreditNoteRes creditRes = jasperCustomeImple.getCreditNoteRes(homeData.getPolicyNo());
				String JsonString = gson.toJson(creditRes);
				String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile")+homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "");
				res = getCommonJasperPdfFileByJson("/report/jasper/EwayCreditNote.jrxml", jasperSaveLocation, JsonString, input, "- CreditNote.json");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public CommonRes getPremiumReport(PremiumReportReq req) {
		CommonRes response = new CommonRes();
		Connection connection=null;
		try {
			String classpath = this.getClass().getClassLoader().getResource("").getPath();
			classpath = classpath.replaceAll("%20", " ");
			classpath = classpath.substring(1, classpath.length());
			
			String imagepath = classpath + "report/images/"; //windows system path
			
			String jasperPath = policyReportPath+req.getLoginId()+System.currentTimeMillis()+ ".pdf";

			HashMap<String, Object> jasperParameter = new HashMap<String, Object>();
			jasperParameter.put("pvStartDate", getFormattedDate(req.getStartDate()));
			jasperParameter.put("pvEndDate", getFormattedDate(req.getEndDate()));
			jasperParameter.put("pvBranch", StringUtils.isBlank(req.getBranchCode())?"99999":req.getBranchCode());
			jasperParameter.put("pvImagePath", imagepath);
			jasperParameter.put("pvLoginId", req.getLoginId());
			jasperParameter.put("pvProductId", req.getProductId());
			jasperParameter.put("pvCode", StringUtils.isBlank(req.getCode())?"99999":req.getCode());
			jasperParameter.put("pvUserType", StringUtils.isBlank(req.getUserType())?"99999":req.getUserType());

			

			connection=config.getDataSourceForJasper().getConnection();
			
			InputStream is = this.getClass().getResourceAsStream("/report/jasper/EwayPremiumReport.jrxml");
						
			JasperReport jr = JasperCompileManager.compileReport(is);

			JasperPrint jp = JasperFillManager.fillReport(jr, jasperParameter, connection);

			JasperExportManager.exportReportToPdfFile(jp, jasperPath);
			
			File file = new File(jasperPath);
			
			
			byte[] bytes = FileUtils.readFileToByteArray(file);

			String encodeToString = Base64.getEncoder().encodeToString(bytes);
			
            PremiumReportRes preRes = PremiumReportRes.builder()
            		.base64("data:application/pdf;base64,"+encodeToString)
            		.fileName("PremiumReport.pdf")
            		.filePath(jasperPath)
            		.build();		
            response.setCommonResponse(preRes);
            response.setIsError(false);
            response.setErrorMessage(Collections.emptyList());
            response.setMessage("Success");
		}catch (Exception e) {
			response.setCommonResponse(null);
            response.setIsError(true);
            response.setErrorMessage(Collections.emptyList());
            response.setMessage("Failed");
			e.printStackTrace();
		}finally {
			if(connection!=null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			
		}
		return response;
	}
	
	private static String getFormattedDate(String input) {
		String output ="";
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date =sdf1.parse(input);
			output=sdf2.format(date);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}

	@Override
	public CommonRes getPremiumReportDetails(PremiumReportReq req) {
		CommonRes response = new CommonRes();
		System.out.println("Enter Into getPremiumReportDetails");
		try {
			/*int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
			int start =  limit * offset + 1 ; 
			int end =  limit * offset + offset ;
//			int start =  limit; */
//			int end = offset ;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String date1 = new SimpleDateFormat("yyyy-MM-dd").format(sdf.parse(req.getStartDate()));
			String date2 = new SimpleDateFormat("yyyy-MM-dd").format(sdf.parse(req.getEndDate()));
//			LocalDate startDate =LocalDate.parse(req.getStartDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//			LocalDate endDate =LocalDate.parse(req.getEndDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//            Date date1 = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) ;
//            Date date2 = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) ;
            String branchCode =StringUtils.isBlank(req.getBranchCode())?"99999":req.getBranchCode();
			List<Map<String,Object>> list =branchRepo.getPremiumReportDetails(req.getProductId(), branchCode, date1, date2, req.getLoginId(),req.getUserType(),req.getCode());
			//List<Map<String,Object>> listcount =branchRepo.getPremiumReportDetailsCount(req.getProductId(), branchCode, date1, date2, req.getLoginId(),req.getUserType(),req.getCode());
			//ReportRes res=new ReportRes();
			
			//Integer count=listcount.size();
			if(list.size()>0) {
				List<Map<String,Object>> dataRes =list.parallelStream().map( p->{
					LinkedHashMap<String,Object> map =new LinkedHashMap<String,Object>();
					map.put("LoginId",p.get("LOGIN_ID")==null?"":p.get("LOGIN_ID"));
					map.put("QuoteNo", p.get("QUOTE_NO")==null?"":p.get("QUOTE_NO"));
					map.put("PolicyNo", p.get("POLICY_NO")==null?"":p.get("POLICY_NO"));
					map.put("OriginalPolicyNo", p.get("ORIGINAL_POLICY_NO")==null?"":p.get("ORIGINAL_POLICY_NO"));
					map.put("CustomerName", p.get("CUSTOMER_NAME")==null?"":p.get("CUSTOMER_NAME"));
					map.put("StartDate", p.get("START_DATE")==null?"":sdf.format(p.get("START_DATE")));
					map.put("EndDate", p.get("END_DATE")==null?"":sdf.format(p.get("END_DATE")));
					map.put("IssueDate",p.get("ISSUED_DATE")==null?"":p.get("ISSUED_DATE"));
					map.put("BranchName",p.get("BRANCH_NAME")==null?"":p.get("BRANCH_NAME"));
					map.put("BrokerName", p.get("BROKER_NAME")==null?"":p.get("BROKER_NAME"));
					map.put("SumInured", p.get("SUM_INSURED")==null?"":p.get("SUM_INSURED"));
					map.put("Premium", p.get("PERMIUM")==null?"":p.get("PERMIUM"));
					map.put("PaymentType", p.get("PAYMENT_TYPE")==null?"":p.get("PAYMENT_TYPE"));
					map.put("Currency", p.get("CURRENCY")==null?"":p.get("CURRENCY"));
					map.put("PolicyDesc", p.get("POLICY_TYPE_DESC")==null?"":p.get("POLICY_TYPE_DESC"));
					map.put("CommisionAmt", p.get("COMMISSION_AMOUNT")==null?"":p.get("COMMISSION_AMOUNT"));
					map.put("ProductName", p.get("PRODUCT_NAME")==null?"":p.get("PRODUCT_NAME"));
					map.put("CreditLimit", p.get("CREDIT_LIMIT")==null?"":p.get("CREDIT_LIMIT"));
					return map;
				}).collect(Collectors.toList());
				
				 response.setCommonResponse(dataRes);
		         response.setIsError(false);
		         response.setErrorMessage(Collections.emptyList());
		         response.setMessage("Success");
				
			}else{
				 response.setCommonResponse(null);
		         response.setIsError(true);
		         response.setErrorMessage(Collections.emptyList());
		         response.setMessage("Failed");
			}
		}catch (Exception e) {
			e.printStackTrace();
			 response.setCommonResponse(null);
	         response.setIsError(true);
	         response.setErrorMessage(Collections.emptyList());
	         response.setMessage("Failed");
		}
		return response;
	}

	@Override
	public JasperDocumentRes illustration(String jsonFile) {
		 try {
			 	//String filePath = config.getPolicyPath() + "pdf";
			// 	String filePath="d:\\"+Instant.now().toEpochMilli();
			 	String filePath = config.getPolicyPath() + Instant.now().toEpochMilli();
			 	//String filePath="d:\\"+Instant.now().toEpochMilli();
			 	String getPdfOutFilePath = filePath + ".pdf";
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pvImagePath", config.getImagePath().substring(1,config.getImagePath().length()-0));
				//map.put("pvPolicyNo", homeData.getPolicyNo());
				
				JasperDocumentRes	res = getJasperPdfFileFromJson("/report/jasper/Illestration.jrxml", getPdfOutFilePath, map,jsonFile);
				return res;
		 }catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}
	
	private JasperDocumentRes getCommonJasperPdfFileByJson(String jrxmlPath,String jasperSaveLocation, String jsonString, Map<String, Object> map,String fileNameEnd) {
		log.info("Enter into getCommonJasperPdfFileByJson");
		JasperDocumentRes res = new JasperDocumentRes();
		InputStream inputStream=null;
		log.info(fileNameEnd.substring(2).replaceAll(".json", " ")+"JsonResponse ==> "+jsonString);
		try {
			FileWriter fileWriter = new FileWriter(jasperSaveLocation+fileNameEnd, false);
			fileWriter.write(jsonString);
			fileWriter.close();
			File file = new File(jasperSaveLocation+fileNameEnd);
			JsonDataSource dataSource = new JsonDataSource(file);
			inputStream = this.getClass().getResourceAsStream(jrxmlPath);
			JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map,dataSource);
			JasperExportManager.exportReportToPdfFile(jasperPrint, jasperSaveLocation+fileNameEnd.replace(".json", ".pdf"));
			GetFileFromPath filePath = new GetFileFromPath(jasperSaveLocation+fileNameEnd.replace(".json", ".pdf"));
			res.setPdfoutfile(filePath.call().getImgUrl());
			res.setPdfoutfilepath(jasperSaveLocation+fileNameEnd.replace(".json", ".pdf"));
		}catch(Exception e) {
			log.info("Error in getCommonJasperPdfFileByJson ==> "+e.getMessage());
			e.printStackTrace();
		}finally {
			if(inputStream!=null)
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		log.info("Exit into getCommonJasperPdfFileByJson");
		return res;
	}

	@Override
	public JasperDocumentRes getInalipaSchedule(String policyNo) {
		log.info("Enter into getInalipaSchedule \n Argument ==> "+policyNo);
		JasperDocumentRes res = new JasperDocumentRes();
		try {
			Map<String,Object> response = jasperCustomeImple.getInalipaSchedule(policyNo);
			String jsonString = gson.toJson(response);
			String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile")+policyNo.replaceAll("[\\/:*?\"<>|]*", "");
			res = getCommonJasperPdfFileByJson("/report/jasper/InalipaSchedule.jrxml", jasperSaveLocation, jsonString, null, "- InalipaSchedule.json");
			log.info("Exit into getInalipaSchedule");
		}catch(Exception e) {
			log.info("Error in getInalipaSchedule ==> "+e.getMessage());
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public CommonRes getSchedule(JasperScheduleReq req) {
		CommonRes response = new CommonRes();
		try {
			HomePositionMaster hpm=homeRepo.findByQuoteNo(req.getQuoteNo());
			String companyId =hpm.getCompanyId();
			Integer productId =hpm.getProductId();
			String quoteNo =hpm.getQuoteNo();
			String policyNo =hpm.getPolicyNo();
			
			List<ReportJasperConfigMaster> list =jpqlQueryServiceImpl.getJasperReportConfigMaster(companyId,productId,Integer.valueOf(req.getReportId()));
			
			if(!list.isEmpty()) {
				ReportJasperConfigMaster report =list.get(0);
				String jasperReportJrxml =report.getJasperPath().trim();
				String jasperName =report.getJasperName();		
				
				String classpath = this.getClass().getClassLoader().getResource("").getPath();
				classpath = classpath.replaceAll("%20", " ");
				classpath = classpath.substring(1, classpath.length());
				String imagepath = classpath + "report/images/"; 
				
					
				if("Y".equals(report.getSubJasperYn())) { // for if subjasper yes 
				
					String [] subJasperArray =report.getSubJasperName().split(",");
				
				for(String subJasperJrxml :subJasperArray) {
						String jrxmlPath=classpath +subJasperJrxml.replace(".jasper", ".jrxml");
						String path = JasperCompileManager.compileReportToFile(jrxmlPath);
						log.info("Jasper compileToReport path" +path);
					}
				}
				
				HashMap<String, Object> jasperParameter = new HashMap<String, Object>();
				jasperParameter.put("pvImagepath",imagepath);
				jasperParameter.put("pvSubReportPath", classpath+"jasper/");
								
				Map<String,Object> map = new HashMap<>();
				
				JasperDocumentRes reponse = new JasperDocumentRes();
				
				if("1".equals(req.getReportId())) { //PolicySchedule pdf
					Object result = null;
					if(report.getId().getProductId()==5) {
						if("100004".equalsIgnoreCase(report.getId().getCompanyId())) {
							List<Map<String,Object>> reportRes = jasperCustomeImple.getMadisonMotorSchedule(policyNo);
							result = reportRes;
						}else {
							MotorPrivateRes reportRes =jasperCustomeImple.getMotorPrivate(policyNo, req.getQuoteNo());
							result = reportRes;
						}
					}else if(report.getId().getProductId()==4) {
						TravelReportRes reportRes = jasperCustomeImple.getTravelReport(policyNo);
						result = reportRes;
					}else if(report.getId().getProductId()==42) {
						Map<String, Object> reportRes = jasperCustomeImple.getCyberInsurance(policyNo);
						result = reportRes;
					}else {
						Map<String, Object> reportRes = jasperCustomeImple.getEwaySchedule(policyNo);
						result = reportRes;
					}
					String jsonString = gson.toJson(result);
					String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile")+quoteNo.replaceAll("[\\/:*?\"<>|]*", "");
					reponse= getCommonJasperPdfFileByJson(jasperReportJrxml, jasperSaveLocation, jsonString, jasperParameter, "- "+jasperName+".json");

				}else if("2".equals(req.getReportId())) {  //CreditNote pdf
					
					CreditNoteRes creditNoteRes=jasperCustomeImple.getCreditNoteRes(hpm.getPolicyNo());
					
					String jsonString = gson.toJson(creditNoteRes);
					String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile")+quoteNo.replaceAll("[\\/:*?\"<>|]*", "");
					reponse= getCommonJasperPdfFileByJson(jasperReportJrxml, jasperSaveLocation, jsonString, jasperParameter, "- "+jasperName+".json");


				}else if("3".equals(req.getReportId())) {  // DebitNote pdf
					
					TaxInvoiceRes invoiceRes= jasperCustomeImple.getTaxInvoiceRes(hpm.getPolicyNo());
					
					String jsonString = gson.toJson(invoiceRes);
					String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile")+quoteNo.replaceAll("[\\/:*?\"<>|]*", "");
					reponse= getCommonJasperPdfFileByJson(jasperReportJrxml, jasperSaveLocation, jsonString, jasperParameter, "- "+jasperName+".json");

					
				}else if("4".equals(req.getReportId())) {  // Broker Quotation pdf
					
					map =jasperCustomeImple.getMotorBrokerQuotation(hpm.getPolicyNo());
					String jsonString = gson.toJson(map);
					String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile")+quoteNo.replaceAll("[\\/:*?\"<>|]*", "");
					reponse= getCommonJasperPdfFileByJson(jasperReportJrxml, jasperSaveLocation, jsonString, jasperParameter, "- "+jasperName+".json");

				}else if("5".equals(req.getReportId())) {  // Premium register pdf
					
					PremiumReportRes reportRes = (PremiumReportRes)getPremiumReport(req.getPremiumRegisterReq()).getCommonResponse();
					reponse.setPdfoutfile(reportRes.getBase64());
					reponse.setPdfoutfilepath(reportRes.getFilePath());
					
				}else if("6".equals(req.getReportId())) {  // EndorseMent pdf
					
					map =jasperCustomeImple.getMotorEndorsementSchedule(hpm.getPolicyNo());
					String jsonString = gson.toJson(map);
					String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile")+quoteNo.replaceAll("[\\/:*?\"<>|]*", "");
					reponse= getCommonJasperPdfFileByJson(jasperReportJrxml, jasperSaveLocation, jsonString, jasperParameter, "- "+jasperName+".json");

				}else if("7".equals(req.getReportId())) {  // Sticker  pdf
					
					MotorPrivateRes motPrivateRes = jasperCustomeImple.getMotorPrivate(hpm.getPolicyNo(),quoteNo);
					String JsonString = gson.toJson(motPrivateRes);
					String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile")+quoteNo.replaceAll("[\\/:*?\"<>|]*", "");
					reponse = getCommonJasperPdfFileByJson(jasperReportJrxml, jasperSaveLocation, JsonString, jasperParameter, "- "+jasperName+".json");
					
				}else if("8".equals(req.getReportId())) { // Illestration Pdf
					map = jasperCustomeImple.getInalipaSchedule(hpm.getPolicyNo());
					String JsonString = gson.toJson(map);
					String jasperSaveLocation = policyReportPath.replaceAll("PolicyReport", "JsonFile")+quoteNo.replaceAll("[\\/:*?\"<>|]*", "");
					reponse = getCommonJasperPdfFileByJson(jasperReportJrxml, jasperSaveLocation, JsonString, jasperParameter, "- "+jasperName+".json");
				}			
				response.setCommonResponse(reponse);	
			
			}
			
		}catch (Exception e) {
			log.info("Error in getSchedule ==> "+e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public CommonRes PdfJsonResponse(PdfJsonReq req) {
		log.info("Enter in PdfJsonResponse => "+gson.toJson(req));
		CommonRes response = new CommonRes();
		Object Result = null;
		try {
			HomePositionMaster hpmData = homeRepo.findByQuoteNo(req.getQuoteNo());
			if(StringUtils.isNotBlank(hpmData.getQuoteNo())) {
				if(StringUtils.isBlank(req.getTaxInvoiceYn()) && StringUtils.isBlank(req.getCreditYn()) && StringUtils.isBlank(req.getEndtSchedule())) {
					if(hpmData.getProductId() == 4){
						TravelReportRes res = jasperCustomeImple.getTravelReport(hpmData.getPolicyNo());
						Result = res;
					}else if(hpmData.getProductId() == 42) {
						Result = jasperCustomeImple.getCyberInsurance(hpmData.getPolicyNo());
					}else if(hpmData.getProductId() == 46) {
						MotorCoverNoteRes res = jasperCustomeImple.getMotorCoverNote(hpmData.getPolicyNo());
						Result = res;
					}else if(hpmData.getProductId() == 5){
						if("100004".equalsIgnoreCase(hpmData.getCompanyId())) {
							Result = jasperCustomeImple.getMadisonMotorSchedule(hpmData.getPolicyNo());
						}else if("100015".equalsIgnoreCase(hpmData.getCompanyId())) {
							Result = jasperCustomeImple.getInalipaSchedule(hpmData.getPolicyNo());
						}else {
							MotorPrivateRes res = jasperCustomeImple.getMotorPrivate(hpmData.getPolicyNo(),hpmData.getQuoteNo());
							Result = res;
						}
					}else {
						Result = jasperCustomeImple.getEwaySchedule(hpmData.getQuoteNo());
					}
				}else if(StringUtils.isNotBlank(hpmData.getPolicyNo())){
					if("Y".equalsIgnoreCase(req.getTaxInvoiceYn())) {
						TaxInvoiceRes res = jasperCustomeImple.getTaxInvoiceRes(hpmData.getPolicyNo());
						Result = res;
					}else if("Y".equalsIgnoreCase(req.getCreditYn())) {
						CreditNoteRes res = jasperCustomeImple.getCreditNoteRes(hpmData.getPolicyNo());
						Result = res;
					}else if("Y".equalsIgnoreCase(req.getEndtSchedule())) {
						Result = jasperCustomeImple.getMotorEndorsementSchedule(hpmData.getPolicyNo());
					}
				}
				response.setCommonResponse(Result);
				response.setMessage("SUCCESS");
				response.setErrorMessage(null);
				response.setIsError(false);
			}else {
				response.setCommonResponse(null);
				response.setMessage("FAILED");
				response.setIsError(true);
			}
			log.info("Exit into PdfJsonResponse");
		}catch(Exception e) {
			log.info("Error in PdfJsonResponse ==> "+e.getMessage());
			e.printStackTrace();
		}
		return response;
	}
	
}
