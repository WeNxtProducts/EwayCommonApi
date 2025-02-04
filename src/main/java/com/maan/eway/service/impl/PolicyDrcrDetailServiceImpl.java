/*
*  Copyright (c) 2019. All right reserved
* Created on 2023-01-04 ( Date ISO 2023-01-04 - Time 13:04:14 )
* Generated by Telosys Tools Generator ( version 3.3.0 )
*/
package com.maan.eway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.maan.eway.repository.PolicyDrcrDetailRepository;
import com.maan.eway.res.calc.DebitAndCredit;
import com.maan.eway.bean.PolicyDrcrDetail;
import com.maan.eway.service.PolicyDrcrDetailService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
/**
* <h2>PolicyDrcrDetailServiceimpl</h2>
*/
@Service
@Transactional
public class PolicyDrcrDetailServiceImpl implements PolicyDrcrDetailService {

@Autowired
private PolicyDrcrDetailRepository repository;


private Logger log=LogManager.getLogger(PolicyDrcrDetailServiceImpl.class);
/*
public PolicyDrcrDetailServiceImpl(PolicyDrcrDetailRepository repo) {
this.repository = repo;
}

  */
 @Override
    public PolicyDrcrDetail create(PolicyDrcrDetail d) {

       PolicyDrcrDetail entity;

        try {
            entity = repository.save(d);

        } catch (Exception ex) {
			log.error(ex);
            return null;
        }
        return entity;
    }

    
    @Override
    public PolicyDrcrDetail update(PolicyDrcrDetail d) {
        PolicyDrcrDetail c;

        try {
            c = repository.saveAndFlush(d);

        } catch (Exception ex) {
			log.error(ex);
            return null;
        }
        return c;
    }

/*
    @Override
    public PolicyDrcrDetail getOne(long id) {
        PolicyDrcrDetail t;

        try {
            t = repository.findById(id).orElse(null);

        } catch (Exception ex) {
			log.error(ex);
            return null;
        }
        return t;
    }

*/
    @Override
    public List<PolicyDrcrDetail> getAll() {
        List<PolicyDrcrDetail> lst;

        try {
            lst = repository.findAll();

        } catch (Exception ex) {
			log.error(ex);
            return Collections.emptyList();
        }
        return lst;
    }


    @Override
    public long getTotal() {
        long total;

        try {
            total = repository.count();
        } catch (Exception ex) {
            log.error(ex);
			return 0;
        }
        return total;
    }

/*
    @Override
    public boolean delete(long id) {
        try {
            repository.deleteById(id);
            return true;

        } catch (Exception ex) {
			log.error(ex);
            return false;
        }
    }

 */
    
    @Override
    public boolean insertDRCR(List<DebitAndCredit> push,String quoteno ) {
        

        try {
            
        	List<PolicyDrcrDetail> list=repository.findByQuoteNoAndStatusIn(quoteno,Arrays.asList("Y"));
        	if(list.size()>0) {
        		list.stream().forEach(t->t.setStatus("N"));
        		repository.saveAllAndFlush(list);
        	}
        	DozerBeanMapper dozerMappper = new DozerBeanMapper();
        	List<PolicyDrcrDetail> items=new ArrayList<PolicyDrcrDetail>();
        	 for(int i=0;i<push.size();i++) {
        		 DebitAndCredit debitAndCredit = push.get(i);
        		 PolicyDrcrDetail p = dozerMappper.map(debitAndCredit, PolicyDrcrDetail.class);
        		 items.add(p);
        	 }
        	 repository.saveAllAndFlush(items);
        	 
        } catch (Exception ex) {
            log.error(ex);
			return false;
        }
        return true;
    }
    

}
