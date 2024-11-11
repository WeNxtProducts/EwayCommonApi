/*
*  Copyright (c) 2019. All right reserved
* Created on 2023-01-04 ( Date ISO 2023-01-04 - Time 19:00:39 )
* Generated by Telosys Tools Generator ( version 3.3.0 )
*/
package com.maan.eway.common.service.impl;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.SeqQuoteno;
import com.maan.eway.common.service.SeqQuotenoService;
import com.maan.eway.repository.SeqQuotenoRepository;
/**
* <h2>SeqQuotenoServiceimpl</h2>
*/
@Service
@Transactional
public class SeqQuotenoServiceImpl implements SeqQuotenoService {

@Autowired
private SeqQuotenoRepository repository;


private Logger log=LogManager.getLogger(SeqQuotenoServiceImpl.class);
/*
public SeqQuotenoServiceImpl(SeqQuotenoRepository repo) {
this.repository = repo;
}

  */
 @Override
    public String create() {
       try {
        	SeqQuoteno entity;
            entity = repository.save(new SeqQuoteno());          
            return String.format("%05d",entity.getQuoteNo()) ;
        } catch (Exception ex) {
			log.error(ex);
            return null;
        }
       
    }
 

}
