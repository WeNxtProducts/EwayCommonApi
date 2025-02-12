/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.workstream.entity.HierarchyManagement;
import com.maan.eway.workstream.repository.HierarchyManagementRepository;
import com.maan.eway.workstream.request.Hierarchy;
import com.maan.eway.workstream.request.HierarchyManagementGetReq;
import com.maan.eway.workstream.request.HierarchyManagementSaveReq;
import com.maan.eway.workstream.response.HierarchyRes;
import com.maan.eway.workstream.service.HierarchyManagementService;
import com.maan.eway.error.Error;


@Service
public class HierarchyManagementServiceImpl implements HierarchyManagementService {
	private static final Logger log = LogManager.getLogger(HierarchyManagementServiceImpl.class);
		
	private HierarchyManagementRepository hierarchyRepo;
	private ModelMapper mapper;	
	
	@Autowired
	public HierarchyManagementServiceImpl(HierarchyManagementRepository hierarchyRepo, ModelMapper mapper) {
		this.hierarchyRepo = hierarchyRepo;
		this.mapper = mapper;
	}



	public List<Error> validateParametersForHierarchySaveReq(HierarchyManagementSaveReq req) {
		List<Error> errors = new ArrayList<>();
		if(req.getCompanyId() == null) {
			errors.add(new Error("11", "CompanyId", "CompanyId Should Not Be Null"));
		}
		if(req.getProductId() == null) {
			errors.add(new Error("12", "ProductId", "ProductId Should Not Be Null"));
		}
		if(req.getHierarchies() == null || req.getHierarchies().isEmpty()) {
			errors.add(new Error("14", "Hierarchies", "Hierarchies Should Not Be Null or Emplty"));
		}
		
		int rowNum = 1;
		for(Hierarchy hierarchy : req.getHierarchies()) {
			
			if(hierarchy.getHierarchyLevel() == null || hierarchy.getHierarchyLevel().isBlank()) {
				errors.add(new Error("51", "HierarchyLevel", "Hierarchy Level Should Not Be Blank for Row "+rowNum));
			}
			if(hierarchy.getHierarchyValue() == null) {
				errors.add(new Error("52", "HierarchyValue", "Hierarchy Value Should Not Be Null for Row "+rowNum));
			}
			rowNum++;
		}

		return errors;
	}

	public List<Error> validateParametersForHierarchyGetReq(HierarchyManagementGetReq req) {
		List<Error> errors = new ArrayList<>();
		if(req.getCompanyId() == null) {
			errors.add(new Error("11", "CompanyId", "CompanyId Should Not Be Null"));
		}
		if(req.getProductId() == null) {
			errors.add(new Error("12", "ProductId", "ProductId Should Not Be Null"));
		}

		return errors;
	}
	
	
	public Boolean saveAllHierarchyManagement(HierarchyManagementSaveReq req) {		
		try {
			List<HierarchyManagement> allHierarchy = hierarchyRepo.findAllByCompanyIdAndProductId(
					req.getCompanyId(), req.getProductId());
			
			if(!allHierarchy.isEmpty()) {
				return false;
			}
			List<HierarchyManagement> list = new ArrayList<>();
			for(Hierarchy hierarchy : req.getHierarchies()) {
				
				HierarchyManagement hierarchyManagement = HierarchyManagement.builder()
						.companyId(req.getCompanyId())
						.productId(req.getProductId())
						.hierarchyLevel(hierarchy.getHierarchyLevel())
						.hierarchyValue(hierarchy.getHierarchyValue())
						.build();
			
				list.add(hierarchyManagement);
			}
			hierarchyRepo.saveAllAndFlush(list);
			return true;
		} catch (Exception e) {
			log.error("Exception occurred: {}", e.getMessage(), e);
			return null;
		}
	}
	
	
	public List<HierarchyRes> getAllHierarchyManagement(Integer companyId, Integer productId) {
		List<HierarchyManagement> allHierarchy = hierarchyRepo.findAllByCompanyIdAndProductId(companyId, productId);
		return allHierarchy.stream()
				.map(level -> mapper.map(level, HierarchyRes.class))
				.toList();		
		}
	

	public List<Integer> retrieveAllLevelsForProduct(Integer companyId, Integer productId) {
		List<HierarchyManagement> allHierarchy = hierarchyRepo.findAllByCompanyIdAndProductId(companyId, productId);
	    
	    if (allHierarchy.isEmpty()) {
	        return List.of();
	    }

	    return allHierarchy.stream()
	            .map(HierarchyManagement::getHierarchyValue)
	            .sorted() 
	            .toList();
	}
	
	
}
