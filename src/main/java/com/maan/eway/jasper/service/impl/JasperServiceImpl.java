package com.maan.eway.jasper.service.impl;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.jasper.req.JasperReportDocReq;
import com.maan.eway.jasper.req.PremiumReportReq;
import com.maan.eway.jasper.res.JasperDocumentRes;
import com.maan.eway.jasper.res.PremiumReportRes;
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
					input2.put("pvImagePath", config.getImagePath().substring(1,config.getImagePath().length()-0));
					input2.put("pvPolicyNo", homeData.getPolicyNo());
					input2.put("pvSubReportPath",config.getJasperFilePath() + "report/jasper/");
					String obj ="";
					obj= config.getJasperFilePath() + "report/jasper/EwayTravelSubReport.jrxml";
					
							//String jrxml_path=obj.replace(".jasper", ".jrxml");
							String path = JasperCompileManager.compileReportToFile(obj);
							System.out.println("Jasper compileToReport path" +path);		

					res = getJasperPdfFile("/report/jasper/EwayTravelReport.jrxml", getPdfOutFilePath, input2);
					
				} else if (product.getMotorYn().equalsIgnoreCase("M")) {
					res = getJasperPdfFile("/report/jasper/MotorPrivate.jrxml", getPdfOutFilePath, input);
				}else if(product.getMotorYn().equalsIgnoreCase("A")&& "42".equalsIgnoreCase(homeData.getProductId().toString())) {
					String imagePath = config.getImagePath().substring(1,config.getImagePath().length()-0);
					Map<String,Object> input2 = new HashMap<>();
					input2.put("pvImagePath", imagePath);
					input2.put("pvPolicyNo", homeData.getPolicyNo());
					input2.put("pvFooterImage", imagePath);
					input2.put("pvheaderImage", imagePath);
					String obj = config.getJasperFilePath() + "report/jasper/CyberInsurance.jrxml";
					String path = JasperCompileManager.compileReportToFile(obj);
					System.out.println("Jasper compileToReport path" +path);
					res = getJasperPdfFile("/report/jasper/CyberInsurance.jrxml", getPdfOutFilePath, input2);
				}else {
					Map<String, Object> input2 = new HashMap<String, Object>();
					input2.put("pvQuoteNo", req.getQuoteNo());
					input2.put("pvImagepath", config.getImagePath().substring(1,config.getImagePath().length()-0));
					input2.put("pvSubReportPath",config.getJasperFilePath() + "/report/jasper/");
					String obj[] =new String[2];
					obj[0]= config.getJasperFilePath() + "/report/jasper/CoverageDetails.jrxml";
					obj[1]= config.getJasperFilePath() +"/report/jasper/SectionDetails.jrxml";              // for linux system
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
				String filePath = config.getPolicyPath() + "pdf";
				String getPdfOutFilePath = filePath + "/" + homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "")+ ".pdf";
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("pvImagePath", config.getImagePath().substring(1,config.getImagePath().length()-0));
				map.put("pvPolicyNo", homeData.getPolicyNo());
				/*String obj = config.getJasperFilePath()+"report/jasper/EwayTaxInvoice.jrxml";
				String path = JasperCompileManager.compileReportToFile(obj);
				System.out.println("Jasper compileToReport path" +path);*/
				res = getJasperPdfFile("/report/jasper/EwayTaxInvoice.jrxml", getPdfOutFilePath, map);
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
				String filepath = config.getPolicyPath()+"pdf";
				String getpdfFileOutFilePath = filepath+"/"+homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "")+".pdf";
				Map<String,Object> input = new HashMap<String,Object>();
				input.put("pvImagePath", config.getImagePath().substring(1, config.getImagePath().length()-0));
				input.put("pvPolicyNo", homeData.getPolicyNo());
				/*String obj = config.getJasperFilePath()+"report/jasper/EwayCreditNote.jrxml";
				String path = JasperCompileManager.compileReportToFile(obj);
				System.out.println("Jasper compileToReport Path"+path);*/
				res = getJasperPdfFile("/report/jasper/EwayCreditNote.jrxml", getpdfFileOutFilePath, input);
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
			jasperParameter.put("pvBranch", req.getBranchCode());
			jasperParameter.put("pvImagePath", imagepath);
			jasperParameter.put("pvLoginId", req.getLoginId());
			

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
					// TODO Auto-generated catch block
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
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			LocalDate startDate =LocalDate.parse(req.getStartDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			LocalDate endDate =LocalDate.parse(req.getEndDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            Date date1 = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) ;
            Date date2 = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) ;
			List<Map<String,Object>> list =branchRepo.getPremiumReportDetails(req.getLoginId(), date1, date2, req.getBranchCode());
			if(list.size()>0) {
				List<Map<String,Object>> dataRes =list.parallelStream().map( p->{
					LinkedHashMap<String,Object> map =new LinkedHashMap<String,Object>();
					map.put("LoginId",p.get("LOGIN_ID")==null?"":p.get("LOGIN_ID"));
					map.put("QuoteNo", p.get("QUOTE_NO")==null?"":p.get("QUOTE_NO"));
					map.put("PolicyNo", p.get("POLICY_NO")==null?"":p.get("POLICY_NO"));
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
	
}
