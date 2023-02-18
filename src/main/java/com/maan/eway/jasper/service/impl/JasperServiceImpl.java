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
public class JasperServiceImpl implements JasperService{


	@Autowired
	private JasperConfiguration config;
	
	@Autowired
	private HomePositionMasterRepository homeRepo;
	

	@Override
	public JasperDocumentRes policyform(JasperDocumentReq req) {
		JasperDocumentRes res = new JasperDocumentRes();
		String getPdfOutFilePath="";
		try {
			Map<String,Object> input = new HashMap<String,Object>();
			input.put("QuoteNo",req.getQuoteNo());
			input.put("imagePath",config.getImagePath());
			
			HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo()) ;
			
			if(null!=input && input.size()>0) {
				
				//String directoryname=null ;
				// File Save Path
				String filePath=null;
				 
				if(StringUtils.isNotBlank(homeData.getPolicyNo()) ) {
					
					//directoryname=homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "");
					filePath = 	config.getPolicyPath()+"pdf";
					getPdfOutFilePath = filePath+"/"+homeData.getPolicyNo().replaceAll("[\\/:*?\"<>|]*", "")+".pdf";
					
				} else {
					
					//directoryname=req.getQuoteNo().replaceAll("[\\/:*?\"<>|]*", "");
					filePath = 	config.getDraftPath()+"pdf";
					getPdfOutFilePath = filePath+"/"+req.getQuoteNo()+".pdf";
				}
				
				File theDir = new File(filePath);
				if (!theDir.exists()){
				    theDir.mkdirs();
				}			
				
				
				res = getJasperPdfFile("/report/jasper/SampleReport.jrxml",getPdfOutFilePath,input);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	private JasperDocumentRes getJasperPdfFile(String jasperPath, String filePath,Map<String, Object> input) {
		JasperDocumentRes res = new JasperDocumentRes();
		Connection connection=null;
		try {
			connection=config.getDataSourceForJasper().getConnection();
			InputStream inputStream = this.getClass().getResourceAsStream(jasperPath);
			JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,input, connection);
			//JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,input);
			
					/*servletRequest.getRealPath(getPdfOutFilePath)*/;
			//filePath=filePath.replaceAll("%20", " ");
			System.out.println("filePath name is ====> "+filePath);
			JasperExportManager.exportReportToPdfFile(jasperPrint,filePath);
			//res.setPdfoutfilepath(commonPath+"/"+getPdfOutFilePath);
			
			GetFileFromPath path=new GetFileFromPath(filePath);
			res.setPdfoutfile(path.call().getImgUrl());
			res.setPdfoutfilepath(filePath);
			
		}catch(Exception e) {
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
		return res;
	}

}
