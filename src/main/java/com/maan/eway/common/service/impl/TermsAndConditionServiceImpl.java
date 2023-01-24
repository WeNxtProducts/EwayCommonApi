package com.maan.eway.common.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.ClausesMaster;
import com.maan.eway.bean.ExclusionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.ProductMaster;
import com.maan.eway.bean.SectionMaster;
import com.maan.eway.bean.TermsAndCondition;
import com.maan.eway.bean.WarRateMaster;
import com.maan.eway.bean.WarrantyMaster;
import com.maan.eway.common.req.ClausesTermsReq;
import com.maan.eway.common.req.ExclusionTermsReq;
import com.maan.eway.common.req.TermsAndConditionGetBySubIdReq;
import com.maan.eway.common.req.TermsAndConditionGetReq;
import com.maan.eway.common.req.TermsAndConditionInsertReq;
import com.maan.eway.common.req.TermsAndConditionListReq;
import com.maan.eway.common.req.TermsAndConditionReq;
import com.maan.eway.common.req.WarrantyTermsReq;
import com.maan.eway.common.req.WarrateTermsReq;
import com.maan.eway.common.res.ClausesRes;
import com.maan.eway.common.res.ExclusionRes;
import com.maan.eway.common.res.TermsAndConditionGetBySubIdRes;
import com.maan.eway.common.res.TermsAndConditionGetRes;
import com.maan.eway.common.res.TermsAndConditionListRes;
import com.maan.eway.common.res.TermsAndConditionRes;
import com.maan.eway.common.res.WarrantyRes;
import com.maan.eway.common.res.WarrateRes;
import com.maan.eway.common.service.TermsAndConditionService;
import com.maan.eway.error.Error;
import com.maan.eway.repository.BranchMasterRepository;
import com.maan.eway.repository.ClausesMasterRepository;
import com.maan.eway.repository.ExclusionMasterRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.ProductMasterRepository;
import com.maan.eway.repository.SectionMasterRepository;
import com.maan.eway.repository.TermsAndConditionRepository;
import com.maan.eway.repository.WarRateMasterRepository;
import com.maan.eway.repository.WarrantyMasterRepository;
import com.maan.eway.res.SuccessRes;

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

	@Autowired
	private InsuranceCompanyMasterRepository inuranceRepo;

	@Autowired
	private BranchMasterRepository branchRepo;

	@Autowired
	private ProductMasterRepository productRepo;

	@Autowired
	private SectionMasterRepository sectionRepo;

	@Autowired
	private TermsAndConditionRepository termsRepo;

	@Autowired
	private ListItemValueRepository listRepo;

	@Override
	public TermsAndConditionRes viewTermsAndCondition(TermsAndConditionReq req) {
		TermsAndConditionRes res = new TermsAndConditionRes();

		try {
			List<WarrantyMaster> warrantyList = new ArrayList<WarrantyMaster>();
			List<WarRateMaster> warrateList = new ArrayList<WarRateMaster>();
			List<ExclusionMaster> exclusionList = new ArrayList<ExclusionMaster>();
			List<ClausesMaster> clausesList = new ArrayList<ClausesMaster>();

			if (StringUtils.isNotBlank(req.getBranchCode())) {
				warrantyList = warrantyRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(
						req.getCompanyId(), req.getBranchCode(), req.getProductId(), req.getSectionId());
				warrateList = warRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(
						req.getCompanyId(), req.getBranchCode(), req.getProductId(), req.getSectionId());
				exclusionList = exclusionRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(
						req.getCompanyId(), req.getBranchCode(), req.getProductId(), req.getSectionId());
				clausesList = clausesRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(
						req.getCompanyId(), req.getBranchCode(), req.getProductId(), req.getSectionId());
			} else {
				warrantyList = warrantyRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(
						req.getCompanyId(), "99999", req.getProductId(), req.getSectionId());
				warrateList = warRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(
						req.getCompanyId(), "99999", req.getProductId(), req.getSectionId());
				exclusionList = exclusionRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(
						req.getCompanyId(), "99999", req.getProductId(), req.getSectionId());
				clausesList = clausesRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(
						req.getCompanyId(), "99999", req.getProductId(), req.getSectionId());

			}

			List<WarrantyRes> warrantyresList = new ArrayList<WarrantyRes>();
			List<WarrateRes> warrateresList = new ArrayList<WarrateRes>();
			List<ExclusionRes> exclusionresList = new ArrayList<ExclusionRes>();
			List<ClausesRes> clausesresList = new ArrayList<ClausesRes>();

			warrantyList = warrantyList.stream().filter(distinctByKey(o -> Arrays.asList(o.getWarrantyId())))
					.collect(Collectors.toList());
			warrateList = warrateList.stream().filter(distinctByKey(o -> Arrays.asList(o.getWarRateId())))
					.collect(Collectors.toList());
			exclusionList = exclusionList.stream().filter(distinctByKey(o -> Arrays.asList(o.getExclusionId())))
					.collect(Collectors.toList());
			clausesList = clausesList.stream().filter(distinctByKey(o -> Arrays.asList(o.getClausesId())))
					.collect(Collectors.toList());

			if (warrantyList.size() > 0 && !warrantyList.isEmpty()) {
				res.setProductId(warrantyList.get(0).getProductId());
				res.setSectionId(warrantyList.get(0).getSectionId());
				res.setCompanyId(warrantyList.get(0).getCompanyId());
				res.setBranchCode(warrantyList.get(0).getBranchCode());

				for (WarrantyMaster warranties : warrantyList) {
					WarrantyRes warrantyres = new WarrantyRes();

					warrantyres.setWarrantyId(warranties.getWarrantyId().toString());
					warrantyres.setWarrantyDesc(warranties.getWarrantyDescription());
					warrantyres.setDocRefNo(warranties.getDocRefNo());
					warrantyres.setDocumentId("16");
					warrantyresList.add(warrantyres);
					res.setWarrantyRes(warrantyresList);
				}
			}
			if (warrateresList.size() > 0 && !warrateresList.isEmpty()) {
				for (WarRateMaster warrates : warrateList) {
					WarrateRes warrateres = new WarrateRes();

					warrateres.setWarrateId(warrates.getWarRateId().toString());
					warrateres.setWarrateDesc(warrates.getWarRateDesc());
					warrateres.setDocRefNo(warrates.getDocRefNo());
					warrateres.setDocumentId("17");
					warrateresList.add(warrateres);
					res.setWarrateRes(warrateresList);
					;

				}
			}
			if (clausesList.size() > 0 && !clausesList.isEmpty()) {

				for (ClausesMaster clauses : clausesList) {
					ClausesRes clausesres = new ClausesRes();

					clausesres.setClausesId(clauses.getClausesId().toString());
					clausesres.setClausesDesc(clauses.getClausesDescription());
					clausesres.setDocRefNo(clauses.getDocRefNo());
					clausesres.setDocumentId("18");
					clausesresList.add(clausesres);
					res.setClausesRes(clausesresList);

				}
			}
			if (exclusionList.size() > 0 && !exclusionList.isEmpty()) {
				for (ExclusionMaster exclusions : exclusionList) {
					ExclusionRes exclusionres = new ExclusionRes();

					exclusionres.setExclusionId(exclusions.getExclusionId().toString());
					exclusionres.setExclusionDesc(exclusions.getExclusionDescription());
					exclusionres.setDocRefNo(exclusions.getDocRefNo());
					exclusionres.setDocumentId("19");
					exclusionresList.add(exclusionres);
					res.setExclusionRes(exclusionresList);

				}
			}

		}

		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	}

	// Fiter Details By Key
	private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public List<Error> validateTermsAndCondition(TermsAndConditionInsertReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SuccessRes insertTermsAndCondition(TermsAndConditionInsertReq req) {
		// TODO Auto-generated method stub
		SuccessRes res = new SuccessRes();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			
			List<TermsAndCondition> data = termsRepo.findByQuoteNoAndIdAndRiskIdAndProductIdAndSectionId(req.getQuoteNo(),Integer.valueOf(req.getId()),req.getRiskId(),req.getProductId(),req.getSectionId());
			if(data.size()>0 && data!=null) {
				termsRepo.deleteAll();
			}
			

			TermsAndCondition saveData = new TermsAndCondition();
			Long count = termsRepo.count();
			Integer count1 = count.intValue();
			List<InsuranceCompanyMaster> insurance = inuranceRepo
					.findTopByCompanyIdOrderByAmendIdDesc(req.getCompanyId());
			List<BranchMaster> branch = branchRepo
					.findTopByCompanyIdAndBranchCodeOrderByAmendIdDesc(req.getCompanyId(), req.getBranchCode());
			List<ProductMaster> product = productRepo
					.findTopByProductIdOrderByAmendIdDesc(Integer.valueOf(req.getProductId()));
			List<SectionMaster> section = sectionRepo
					.findTopBySectionIdOrderByAmendIdDesc(Integer.valueOf(req.getSectionId()));
			ListItemValue id = listRepo.findByItemTypeAndItemCode("TERMS_AND_CONDITION", req.getId());

			saveData.setCompanyId(req.getCompanyId());
			saveData.setBranchCode(req.getBranchCode());
			saveData.setProductId(req.getProductId());
			saveData.setSectionId(req.getSectionId());
			saveData.setCompanyName(insurance.get(0).getCompanyName());
			saveData.setBranchName(branch.get(0).getBranchName());
			saveData.setProductName(product.get(0).getProductName());
			saveData.setSectionName(section.get(0).getSectionName());
			saveData.setSno(count1 + 1);
			saveData.setEntryDate(new Date());
			saveData.setStatus("Y");
			saveData.setCreatedBy(req.getCreatedBy());
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setQuoteNo(req.getQuoteNo());
			saveData.setRiskId(req.getRiskId());
			saveData.setAmendId(0);
			saveData.setId(Integer.valueOf(req.getId()));
			saveData.setIdDesc(id.getItemValue());			
			

			//Warranty Save 
			if(req.getId().equalsIgnoreCase("4")) {
			for (TermsAndConditionListReq reqList : req.getTermsAndConditionReq()) {
				WarrantyMaster warrantydata = warrantyRepo.findTopByWarrantyIdOrderByAmendIdDesc(Integer.valueOf(reqList.getSubId()));
				saveData.setSubId(Integer.valueOf(reqList.getSubId()));
				saveData.setSubIdDesc(warrantydata.getWarrantyDescription());
				termsRepo.saveAndFlush(saveData);
			}
			}
			//War Rate Save
			if(req.getId().equalsIgnoreCase("5")) {
				for (TermsAndConditionListReq reqList : req.getTermsAndConditionReq()) {
					WarRateMaster warratedata = warRepo.findTopByWarRateIdOrderByAmendIdDesc(Integer.valueOf(reqList.getSubId()));
					saveData.setSubId(Integer.valueOf(reqList.getSubId()));
					saveData.setSubIdDesc(warratedata.getWarRateDesc());
					termsRepo.saveAndFlush(saveData);
				}
				}
			
			//Exclusion Save
			if(req.getId().equalsIgnoreCase("7")) {
				for (TermsAndConditionListReq reqList : req.getTermsAndConditionReq()) {
					ExclusionMaster exclusiondata = exclusionRepo.findTopByExclusionIdOrderByAmendIdDesc(Integer.valueOf(reqList.getSubId()));
					saveData.setSubId(Integer.valueOf(reqList.getSubId()));
					saveData.setSubIdDesc(exclusiondata.getExclusionDescription());
					termsRepo.saveAndFlush(saveData);
				}
				}
		
			
			//Clauses Save
			if(req.getId().equalsIgnoreCase("6")) {
				for (TermsAndConditionListReq reqList : req.getTermsAndConditionReq()) {
					ClausesMaster clausesdata = clausesRepo.findTopByClausesIdOrderByAmendIdDesc(Integer.valueOf(reqList.getSubId()));
					saveData.setSubId(Integer.valueOf(reqList.getSubId()));
					saveData.setSubIdDesc(clausesdata.getClausesDescription());
					termsRepo.saveAndFlush(saveData);
				}
				}
			
			termsRepo.saveAndFlush(saveData);
			res.setResponse("Saved Successful");
			res.setSuccessId(req.getQuoteNo());
		}

		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public TermsAndConditionGetRes getTermsAndCondition(TermsAndConditionGetReq req) {
		TermsAndConditionGetRes res = new TermsAndConditionGetRes();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {
			TermsAndCondition savedata = new TermsAndCondition();
			List<TermsAndCondition> datas = termsRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndRiskIdAndQuoteNoAndId(req.getCompanyId(),req.getBranchCode(),req.getProductId(),req.getSectionId(),req.getRiskId(),req.getQuoteNo(),Integer.valueOf(req.getId()));
		
				if(datas.size()>0 && datas!=null) {
				res= dozermapper.map(datas.get(0),TermsAndConditionGetRes.class);
				List<TermsAndConditionListRes> resList = new  ArrayList<TermsAndConditionListRes>();
				for(TermsAndCondition data:  datas) {
					TermsAndConditionListRes res1 = new TermsAndConditionListRes();	
					res1.setSubId(data.getSubId().toString());
					res1.setSubIdDesc(data.getSubIdDesc().toString());
					resList.add(res1);
				}
				res.setTermsAndConditionlistRes(resList);
				}
		}
			catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public TermsAndConditionGetBySubIdRes getTermsAndConditionSubId(TermsAndConditionGetBySubIdReq req) {
		TermsAndConditionGetBySubIdRes res = new TermsAndConditionGetBySubIdRes();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {
			TermsAndCondition savedata = new TermsAndCondition();
			TermsAndCondition data = termsRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndRiskIdAndQuoteNoAndIdAndSubId(req.getCompanyId(),req.getBranchCode(),req.getProductId(),req.getSectionId(),req.getRiskId(),req.getQuoteNo(),Integer.valueOf(req.getId()),Integer.valueOf(req.getSubId()));
		
				res= dozermapper.map(data,TermsAndConditionGetBySubIdRes.class);
				res.setId(data.getId().toString());
				res.setSubId(data.getSubId().toString());
				res.setEntryDate(data.getEntryDate());		
		}
			catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	}
	
	

}
