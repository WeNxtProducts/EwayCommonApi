package com.maan.eway.common.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.BuildingDetails;
import com.maan.eway.common.req.BuildingDetailsGetRequest;
import com.maan.eway.common.res.BuildingDetailsGetResponse;
import com.maan.eway.common.service.BuildingdetailsService;
import com.maan.eway.master.service.impl.ClausesMasterServiceImpl;
import com.maan.eway.repository.BuildingDetailsRepository;

@Service
@Transactional
public class BuildingdetailsServiceImpl implements BuildingdetailsService {
	
	
	@Autowired
	private BuildingDetailsRepository repo;
	private Logger log = LogManager.getLogger(ClausesMasterServiceImpl.class);

	@Override
	public List<BuildingDetailsGetResponse> viewBuilingInfo(BuildingDetailsGetRequest req) {
		List<BuildingDetailsGetResponse> buildinglist=new ArrayList<BuildingDetailsGetResponse>();

		BuildingDetailsGetResponse buildingres=new BuildingDetailsGetResponse();
		
		DozerBeanMapper bean=new DozerBeanMapper();
		try {
			List<BuildingDetails> buildingdetails=null;
			if(StringUtils.isNotBlank(req.getQuoteNo())) {
				buildingdetails=repo.findByQuoteNo(req.getQuoteNo());
			}
			
//			if(StringUtils.isNotBlank(req.getQuoteNo(),req.getRiskId())) {
//				
//			}
			
			
			for(BuildingDetails bd:buildingdetails) {
				buildingres=bean.map(bd, BuildingDetailsGetResponse.class);
				buildinglist.add(buildingres);
			}
			
			
			
			
			
		}catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return buildinglist;
		
		
	}

	

}
