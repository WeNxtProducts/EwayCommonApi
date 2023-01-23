package com.maan.eway.common.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.ClausesMaster;
import com.maan.eway.bean.ExclusionMaster;
import com.maan.eway.bean.WarRateMaster;
import com.maan.eway.bean.WarrantyMaster;
import com.maan.eway.common.req.TermsAndConditionReq;
import com.maan.eway.common.res.ClausesRes;
import com.maan.eway.common.res.ExclusionRes;
import com.maan.eway.common.res.TermsAndConditionRes;
import com.maan.eway.common.res.WarrantyRes;
import com.maan.eway.common.res.WarrateRes;
import com.maan.eway.common.service.TermsAndConditionService;
import com.maan.eway.repository.ClausesMasterRepository;
import com.maan.eway.repository.ExclusionMasterRepository;
import com.maan.eway.repository.WarRateMasterRepository;
import com.maan.eway.repository.WarrantyMasterRepository;

@Service
@Transactional
public class TermsAndConditionServiceImpl implements TermsAndConditionService {

	@PersistenceContext
	private EntityManager em;

	private Logger log = LogManager.getLogger(TermsAndConditionServiceImpl.class);

	@Autowired
	private WarrantyMasterRepository warrantyRepo;
	
	@Autowired
	private WarRateMasterRepository warRepo;
	
	@Autowired
	private ExclusionMasterRepository exclusionRepo;
	
	@Autowired
	private ClausesMasterRepository clausesRepo;
	
	@Override
	public List<TermsAndConditionRes> viewTermsAndCondition(TermsAndConditionReq req) {

		List<TermsAndConditionRes> resList = new ArrayList<TermsAndConditionRes>();
		try {
			List<WarrantyMaster> warrantyList =  new ArrayList<WarrantyMaster>();
			List<WarRateMaster> warrateList = new ArrayList<WarRateMaster>();
			List<ExclusionMaster> exclusionList = new ArrayList<ExclusionMaster>();
			List<ClausesMaster> clausesList = new ArrayList<ClausesMaster>();
			
			
			if(!req.getBranchCode().isBlank()) {
			 warrantyList = warrantyRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(req.getCompanyId(),req.getBranchCode(),req.getProductId(),req.getSectionId());
			 warrateList = warRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(req.getCompanyId(),req.getBranchCode(),req.getProductId(),req.getSectionId());
			 exclusionList = exclusionRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(req.getCompanyId(),req.getBranchCode(),req.getProductId(),req.getSectionId());
			 clausesList = clausesRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(req.getCompanyId(),req.getBranchCode(),req.getProductId(),req.getSectionId());
			}
			else {
			 warrantyList = warrantyRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(req.getCompanyId(),"99999",req.getProductId(),req.getSectionId());
			 warrateList = warRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(req.getCompanyId(),"99999",req.getProductId(),req.getSectionId());
			 exclusionList = exclusionRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(req.getCompanyId(),"99999",req.getProductId(),req.getSectionId());
			 clausesList = clausesRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(req.getCompanyId(),"99999",req.getProductId(),req.getSectionId());
				
			}
			
			List<WarrantyRes> warrantyresList = new ArrayList<WarrantyRes>();
			List<WarrateRes> warrateresList =	new ArrayList<WarrateRes>();
			List<ExclusionRes> exclusionresList = new ArrayList<ExclusionRes>();
			List<ClausesRes> clausesresList = new ArrayList<ClausesRes>();			
			
			
			TermsAndConditionRes res = new TermsAndConditionRes();
			for(WarrantyMaster warranties : warrantyList) {
				WarrantyRes warrantyres = new WarrantyRes();
				
				warrantyres.setProductId(warranties.getProductId());
				warrantyres.setSectionId(warranties.getSectionId());
				warrantyres.setWarrantyId(warranties.getWarrantyId().toString());
				warrantyres.setWarrantyDesc(warranties.getWarrantyDescription());
				warrantyres.setDocRefNo(warranties.getDocRefNo());
				warrantyres.setDocumentId("16");
				warrantyresList.add(warrantyres);
				res.setWarrantyRes(warrantyresList);
			}
			
			for(WarRateMaster warrates : warrateList) {
				WarrateRes warrateres =	new WarrateRes();

				warrateres.setProductId(warrates.getProductId());
				warrateres.setSectionId(warrates.getSectionId());
				warrateres.setWarrateId(warrates.getWarRateId().toString());
				warrateres.setWarrateDesc(warrates.getWarRateDesc());
				warrateres.setDocRefNo(warrates.getDocRefNo());
				warrateres.setDocumentId("17");
				warrateresList.add(warrateres);
				res.setWarrateRes(warrateresList);;

			}
			
			for(ClausesMaster clauses : clausesList) {
				ClausesRes clausesres = new ClausesRes();			

				clausesres.setProductId(clauses.getProductId());
				clausesres.setSectionId(clauses.getSectionId());
				clausesres.setClausesId(clauses.getClausesId().toString());
				clausesres.setClausesDesc(clauses.getClausesDescription());
				clausesres.setDocRefNo(clauses.getDocRefNo());
				clausesres.setDocumentId("18");
				clausesresList.add(clausesres);
				res.setClausesRes(clausesresList);

			}
			
			for(ExclusionMaster exclusions : exclusionList) {
				ExclusionRes exclusionres = new ExclusionRes();

				exclusionres.setProductId(exclusions.getProductId());
				exclusionres.setSectionId(exclusions.getSectionId());
				exclusionres.setExclusionId(exclusions.getExclusionId().toString());
				exclusionres.setExclusionDesc(exclusions.getExclusionDescription());
				exclusionres.setDocRefNo(exclusions.getDocRefNo());
				exclusionres.setDocumentId("19");
				exclusionresList.add(exclusionres);
				res.setExclusionRes(exclusionresList);

			}
			
			
			resList.add(res);
		}
		
		catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return resList;
		}


}
