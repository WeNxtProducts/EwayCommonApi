package com.maan.eway.jasper.service.impl;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.jasper.res.JasperDocumentRes;
import com.maan.eway.jasper.service.JasperService;
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

	@Value(value = "${motor.productId}")
	private String motorProductId;

	@Value(value = "${travel.productId}")
	private String travelProductId;

	@Value(value = "${building.productId}")
	private String buildingProductId;

	@Value(value = "${personalaccident.productId}")
	private String personalAccidentProductId;

	@Value(value = "${workmencompensation.productId}")
	private String workmenCompensationProductId;

	@Value(value = "${employeesliability.productId}")
	private String employeesliabilityProductId;

	@Value(value = "${sme.productId}")
	private String smeProductId;

	@Override
	public JasperDocumentRes policyform(JasperDocumentReq req) {
		JasperDocumentRes res = new JasperDocumentRes();
		String getPdfOutFilePath = "";
		try {
			
			HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo());
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

				if (buildingProductId.equals(homeData.getProductId().toString())) {
					res = getJasperPdfFile("/report/jasper/PersonalPlus.jrxml", getPdfOutFilePath, input);
				}

				else if (travelProductId.equals(req.getProductId())) {
					res = getJasperPdfFile("/report/jasper/TravelReport.jrxml", getPdfOutFilePath, input);
					
				} else if (motorProductId.equals(homeData.getProductId().toString())) {
					res = getJasperPdfFile("/report/jasper/MotorPrivate.jrxml", getPdfOutFilePath, input);
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
			Map<String, Object> input = new HashMap<String, Object>();
			input.put("QuoteNo", req.getQuoteNo());
			input.put("imagePath", config.getImagePath());

			// HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo()) ;
			if (travelProductId.equals(req.getProductId())) {
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
			} else if (motorProductId.equals(req.getProductId())) {
				// Temporary
				res = new JasperDocumentRes();
				String filePath = config.getPolicyPath() + "pdf/MOTOR PRIVATE.pdf";
				GetFileFromPath path = new GetFileFromPath(filePath);
				res.setPdfoutfile(path.call().getImgUrl());
				res.setPdfoutfilepath(filePath);
			} else if (buildingProductId.equals(req.getProductId())) {
				res = new JasperDocumentRes();
				String filePath = config.getPolicyPath() + "pdf/PERSONAL PLUS.pdf";
				GetFileFromPath path = new GetFileFromPath(filePath);
				res.setPdfoutfile(path.call().getImgUrl());
				res.setPdfoutfilepath(filePath);

			} else if (personalAccidentProductId.equals(req.getProductId())) {

			} else if (workmenCompensationProductId.equals(req.getProductId())) {

			} else if (employeesliabilityProductId.equals(req.getProductId())) {
				res = new JasperDocumentRes();
				String filePath = config.getPolicyPath() + "pdf/GROUP PERSONAL ACCIDENT.pdf";
				GetFileFromPath path = new GetFileFromPath(filePath);
				res.setPdfoutfile(path.call().getImgUrl());
				res.setPdfoutfilepath(filePath);
			} else if (smeProductId.equals(req.getProductId())) {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

}
