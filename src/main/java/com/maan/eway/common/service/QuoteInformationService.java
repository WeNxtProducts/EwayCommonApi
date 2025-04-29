package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.common.req.QuoteInformationDTO;

public interface QuoteInformationService {

	public QuoteInformationDTO saveOrUpdate(QuoteInformationDTO dto);

	public List<QuoteInformationDTO>  findAll();

	public List<QuoteInformationDTO> getQuotationDetails(String enquiryId, String quoteNo);

	public void delete(String enquiryId, String quoteNo);

	

}
