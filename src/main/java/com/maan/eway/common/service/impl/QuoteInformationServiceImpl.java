package com.maan.eway.common.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.QuoteInformationId;
import com.maan.eway.bean.QuoteInformationIpclms;
import com.maan.eway.common.req.QuoteInformationDTO;
import com.maan.eway.common.service.QuoteInformationService;
import com.maan.eway.repository.QuoteInformationRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

@Service
public class QuoteInformationServiceImpl implements QuoteInformationService {
	
	private Logger log = LogManager.getLogger(QuoteInformationServiceImpl.class);

    @Autowired
    private QuoteInformationRepository repository;
    
    @PersistenceContext
    private EntityManager em;

    @Override
    public QuoteInformationDTO saveOrUpdate(QuoteInformationDTO dto) {
        try {
        	QuoteInformationId id = new QuoteInformationId();
            id.setEnquiryId(dto.getEnquiryId());
            id.setQuoteNo(dto.getQuoteNo());
            QuoteInformationIpclms entity = new QuoteInformationIpclms();
        	Optional<QuoteInformationIpclms> exdata = repository.findById(id);
        	if(exdata.isPresent()) {
        		QuoteInformationIpclms u = exdata.get();
        		entity.setEnquiryId(u.getEnquiryId());
                entity.setQuoteNo(u.getQuoteNo());
        	}else {
        		entity.setEnquiryId(dto.getEnquiryId());
                entity.setQuoteNo(getmaxQuoteNo());
                dto.setQuoteNo(entity.getQuoteNo());
        	}
            entity.setQuotationDescription(dto.getQuotationDescription());
            entity.setSumInsured(dto.getSumInsured());
            entity.setPremiumRate(dto.getPremiumRate());
            entity.setPremiumAmount(dto.getPremiumAmount());
            entity.setTechnicalDiscount(dto.getTechnicalDiscount());
            entity.setAdditionalDiscount(dto.getAdditionalDiscount());
            entity.setQuoteStatus(dto.getQuoteStatus());
            entity.setQuoteRemarks(dto.getQuoteRemarks());
            repository.save(entity);
            return dto;

        } catch (Exception e) {
            log.error("Error while saving/updating quote: ", e);
            throw new RuntimeException("Error while saving/updating quote", e);
        }
    }


    @Override
    public List<QuoteInformationDTO> findAll() {
    	try {
    		return repository.findAll()
    	            .stream()
    	            .map(entity -> {
    	            	QuoteInformationDTO dto = new QuoteInformationDTO();
    	                dto.setEnquiryId(entity.getEnquiryId());
    	                dto.setQuoteNo(entity.getQuoteNo());
    	                dto.setQuotationDescription(entity.getQuotationDescription());
    	                dto.setSumInsured(entity.getSumInsured());
    	                dto.setPremiumRate(entity.getPremiumRate());
    	                dto.setPremiumAmount(entity.getPremiumAmount());
    	                dto.setTechnicalDiscount(entity.getTechnicalDiscount());
    	                dto.setAdditionalDiscount(entity.getAdditionalDiscount());
    	                dto.setQuoteStatus(entity.getQuoteStatus());
    	                dto.setQuoteRemarks(entity.getQuoteRemarks());
    	                return dto;
    	            }).collect(Collectors.toList());
    	}catch (Exception e){
    		log.error(e);
			e.printStackTrace();
    	}
    	return null;
    }

    @Override
    public Optional<QuoteInformationDTO> findById(String enquiryId, String quoteNo) {
        try{
        	QuoteInformationId id = new QuoteInformationId();
        id.setEnquiryId(enquiryId);
        id.setQuoteNo(quoteNo);
        return repository.findById(id).map(entity -> {
        	QuoteInformationDTO dto = new QuoteInformationDTO();
            dto.setEnquiryId(entity.getEnquiryId());
            dto.setQuoteNo(entity.getQuoteNo());
            dto.setQuotationDescription(entity.getQuotationDescription());
            dto.setSumInsured(entity.getSumInsured());
            dto.setPremiumRate(entity.getPremiumRate());
            dto.setPremiumAmount(entity.getPremiumAmount());
            dto.setTechnicalDiscount(entity.getTechnicalDiscount());
            dto.setAdditionalDiscount(entity.getAdditionalDiscount());
            dto.setQuoteStatus(entity.getQuoteStatus());
            dto.setQuoteRemarks(entity.getQuoteRemarks());
            return dto;
        });
    }catch (Exception e){
		log.error(e);
		e.printStackTrace();
	}
	return null;
    }

    @Override
    public void delete(String enquiryId, String quoteNo) {
        QuoteInformationId id = new QuoteInformationId();
        id.setEnquiryId(enquiryId);
        id.setQuoteNo(quoteNo);
        repository.deleteById(id);
    }
    
    
    public String getmaxQuoteNo() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<QuoteInformationIpclms> slRoot = cq.from(QuoteInformationIpclms.class);
		Expression<Integer> startIndex = cb.literal(3);
		cq.multiselect(cb.coalesce(cb.sum(cb.max(
				cb.substring(slRoot.get("quoteNo"), startIndex, cb.length(slRoot.get("quoteNo"))).as(Integer.class)), 1),
				1000));
		TypedQuery<Integer> query = em.createQuery(cq);
		Integer value = query.getSingleResult();
		return "Q-" + value;
	}
    
}
