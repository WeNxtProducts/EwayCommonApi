package com.maan.eway.jasper.service;

import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.jasper.req.JasperReportDocReq;
import com.maan.eway.jasper.res.JasperDocumentRes;

public interface JasperService {

	public JasperDocumentRes policyform(JasperDocumentReq req);

	public JasperDocumentRes proposalform(JasperDocumentReq req);

	public JasperDocumentRes policyreportform(JasperReportDocReq req);


}
