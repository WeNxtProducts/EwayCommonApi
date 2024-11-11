package com.maan.eway.jasper.service;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.jasper.req.JasperReportDocReq;
import com.maan.eway.jasper.req.JasperScheduleReq;
import com.maan.eway.jasper.req.PdfJsonReq;
import com.maan.eway.jasper.req.PremiumReportReq;
import com.maan.eway.jasper.res.JasperDocumentRes;

public interface JasperService {

	public JasperDocumentRes policyform(JasperDocumentReq req);

	public JasperDocumentRes proposalform(JasperDocumentReq req);

	public JasperDocumentRes policyreportform(JasperReportDocReq req);

	public JasperDocumentRes taxInvoice(String quoteNo);

	public JasperDocumentRes creditNote(String quoteNo);

	public CommonRes getPremiumReport(PremiumReportReq req);

	public CommonRes getPremiumReportDetails(PremiumReportReq req);

	public JasperDocumentRes illustration(String jsonFile);

	public JasperDocumentRes getInalipaSchedule(String policyNo);

	public CommonRes getSchedule(JasperScheduleReq req);

	public CommonRes PdfJsonResponse(PdfJsonReq req);

	public JasperDocumentRes GetReportByRequestRefNo(String requestRefNo);


}
