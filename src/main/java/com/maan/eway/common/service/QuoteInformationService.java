package com.maan.eway.common.service;

import java.util.List;
import java.util.Optional;

import com.maan.eway.common.req.QuoteInformationDTO;

public interface QuoteInformationService {

	public QuoteInformationDTO saveOrUpdate(QuoteInformationDTO dto);

	public List<QuoteInformationDTO>  findAll();

	public Optional<QuoteInformationDTO> findById(String enquiryId, String quoteNo);

	public void delete(String enquiryId, String quoteNo);

	

}
