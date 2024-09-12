package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.MsAssetDetails;
import com.maan.eway.bean.MsHumanDetails;
import com.maan.eway.bean.MsVehicleDetails;
import com.maan.eway.bean.UwQuestionsDetails;
import com.maan.eway.bean.UwQuestionsDetailsArch;
import com.maan.eway.common.req.UwQuestionsDetailsGetReq;
import com.maan.eway.common.req.UwQuestionsDetailsSaveReq;
import com.maan.eway.common.res.UwQuestionsDetailsRes;
import com.maan.eway.common.service.UwQuestionsDetailsService;
import com.maan.eway.error.Error;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.MsAssetDetailsRepository;
import com.maan.eway.repository.MsHumanDetailsRepository;
import com.maan.eway.repository.MsVehicleDetailsRepository;
import com.maan.eway.repository.UwQuestionsDetailsArchRepository;
import com.maan.eway.repository.UwQuestionsDetailsRepository;
import com.maan.eway.res.SuccessRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
@Transactional
public class UwQuesitonsDetailsServiceImpl implements UwQuestionsDetailsService {

	@Autowired
	private UwQuestionsDetailsRepository uwRepo;

	@Autowired
	private UwQuestionsDetailsArchRepository uwArchRepo;

	@Autowired
	private MsVehicleDetailsRepository msVehRepo;
	
	@Autowired
	private MsAssetDetailsRepository msAssetRepo ;
	
	@Autowired
	private MsHumanDetailsRepository msHumanRepo ;
	
	@Autowired
	private EServiceMotorDetailsRepository eserMotRepo ;
	
	@Autowired
	private EserviceCommonDetailsRepository eserHumanRepo ;
	
	@Autowired
	private EserviceBuildingDetailsRepository eserBuildRepo ;
	
	@Autowired
	private EserviceTravelDetailsRepository eserTraRepo ;
	
	
	
	@PersistenceContext
	private EntityManager em;

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(UwQuesitonsDetailsServiceImpl.class);
	
	
	@Override
	public List<Error> validateUwQuestions(List<UwQuestionsDetailsSaveReq> data) {
		List<Error> error = new ArrayList<Error>();

		try {
			Long row = 0L ;

			for(UwQuestionsDetailsSaveReq req : data) {
				row =row+1;
			if (StringUtils.isBlank(req.getCompanyId())) {
				error.add(new Error("01", "CompanyId", "Please Enter CompanyId" +row));
			} else if (req.getCompanyId().length() > 20) {
				error.add(new Error("01", "CompanyId", "Please Enter CompanyId within 20 Characters"+row));
			}

			if (StringUtils.isBlank(req.getProductId())) {
				error.add(new Error("02", "ProductId", "Please Enter ProductId"+row));
			}
			if (StringUtils.isNotBlank(req.getUwQuestionDesc()) &&req.getUwQuestionDesc().length() > 500) {
				error.add(new Error("03", "UwQuestionDesc", "Please Enter UwQuestionDesc within 100 Characters"+row));
			}
			if (StringUtils.isNotBlank(req.getQuestionType()) &&req.getQuestionType().length() > 100) {
				error.add(new Error("04", "QuestionType", "Please Enter QuestionType within 100 Characters"+row));
			
			if (StringUtils.isNotBlank(req.getQuestionType()) &&req.getQuestionType().equalsIgnoreCase("02") && StringUtils.isBlank(req.getValue())) {
				error.add(new Error("04", "Value", "Please Enter Value"+row));
			}
			else if(StringUtils.isNotBlank(req.getValue()) && req.getValue().length()>100) {
				error.add(new Error("04", "Value", "Please Enter Value within 100 Characters"+row));					
			}
			}
			if (StringUtils.isNotBlank(req.getRemarks()) && req.getRemarks().length() > 100) {
				error.add(new Error("05", "Remarks", "Please Enter Remarks within 100 Characters"+row));
			}
			if (StringUtils.isBlank(req.getRequestReferenceNo())) {
				error.add(new Error("06", "RequestReferenceNo", "Please Enter RequestReferenceNo"+row));
			}
			else if (req.getQuestionType().length() > 20) {
				error.add(new Error("06", "RequestReferenceNo", "Please Enter RequestReferenceNo within 20 Characters"+row));
			}
			if (StringUtils.isBlank(req.getVehicleId())) {
				error.add(new Error("07", "VehicleId", "Please Enter VehicleId"+row));
			}
			if (StringUtils.isBlank(req.getUwQuestionId())) {
				error.add(new Error("08", "UwQuestionId", "Please Enter UwQuestionId"+row));
			}
			if (StringUtils.isNotBlank(req.getValue()) &&req.getValue().length() > 100) {
				error.add(new Error("09", "Value", "Please Enter Value within 100 Characters"+row));
			}
			if (StringUtils.isNotBlank(req.getCreatedBy()) &&req.getCreatedBy().length() > 100) {
				error.add(new Error("10", "CreatedBy", "Please Enter CreatedBy within 100 Characters"+row));
			}
			if (StringUtils.isNotBlank(req.getUpdatedBy()) &&req.getUpdatedBy().length() > 100) {
				error.add(new Error("11", "UpdatedBy", "Please Enter UpdatedBy within 100 Characters"+row));
			}
			if (StringUtils.isBlank(req.getBranchCode())) {
				error.add(new Error("12", "BranchCode", "Please Enter BranchCode"+row));
			}
			else if (req.getBranchCode().length() > 20) {
				error.add(new Error("12", "BranchCode", "Please Enter BranchCode within 20 Characters"+row));
			}
			
			if (StringUtils.isNotBlank(req.getMandatoryYn()) && req.getMandatoryYn().equalsIgnoreCase("Y") && StringUtils.isBlank(req.getValue())) {
				error.add(new Error("13", "UW Question Value", "Please Enter Under Writer Question Value at row no  "+row));
			}
			
			}
		} catch (Exception e) {

			log.error(e);
			e.printStackTrace();
			error.add(new Error("15", "Common Error", e.getMessage()));
		}
		return error;
	}



	@Override
	public SuccessRes saveUwQuestions(List<UwQuestionsDetailsSaveReq> req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		
		try {
			Date entryDate = new Date();
			String refNo = req.get(0).getRequestReferenceNo();
			Integer vehId = Integer.valueOf(req.get(0).getVehicleId());
			String companyId = req.get(0).getCompanyId();
			String productId = req.get(0).getProductId();
			String sectionId = req.get(0).getProductId();
			
			// Save Old Datas
			List<UwQuestionsDetails> oldDatas = uwRepo.findByRequestReferenceNo(refNo);
			List<UwQuestionsDetailsArch> saveArchs = new ArrayList<UwQuestionsDetailsArch>();
			Long count = uwArchRepo.countByRequestReferenceNo(refNo);
			//Integer sno = Integer.valueOf(count.toString())+1;
			if(count > 0 ) {
//				uwArchRepo.deleteByRequestReferenceNoAndVehicleId(refNo , vehId);

				uwArchRepo.deleteByRequestReferenceNo(refNo);
			}
			if ( oldDatas.size() > 0 ) {
				entryDate = oldDatas.get(0).getEntryDate() !=null ? oldDatas.get(0).getEntryDate() : new Date()  ;
			}
			
			oldDatas.forEach( o -> {
				UwQuestionsDetailsArch arch = new UwQuestionsDetailsArch();
				dozerMapper.map(o, arch);
				arch.setArchId(o.getVehicleId());
				saveArchs.add(arch);
					
			});
			uwArchRepo.saveAllAndFlush(saveArchs);
			uwRepo.deleteAll(oldDatas);	
			
			List<UwQuestionsDetails> saveList = new ArrayList<UwQuestionsDetails>();
			BigDecimal totalUwLoading = BigDecimal.ZERO ;
			for(UwQuestionsDetailsSaveReq data : req) {
				UwQuestionsDetails saveData = new UwQuestionsDetails();
				
				saveData = dozerMapper.map(data,UwQuestionsDetails.class);
				saveData.setEntryDate(entryDate);		
				saveData.setUpdatedDate(new Date());			
				saveData.setStatus(data.getStatus());
				saveData.setTextValue(data.getTextValue());
				saveData.setStatus(data.getStatus());
				saveData.setQuestionCategory(data.getQuestionCategory());
				saveData.setQuestionCategoryDesc(data.getQuestionCategoryDesc());
				if((StringUtils.isNotBlank(data.getStatus())) && (data.getStatus().equalsIgnoreCase("R")) ){
					saveData.setIsReferral("Y");
				}
				else {
					saveData.setIsReferral("N");							
				}
				saveData.setLoading(data.getLoadingPercent()==null? BigDecimal.ZERO : new BigDecimal (data.getLoadingPercent()));
				totalUwLoading = totalUwLoading.add(saveData.getLoading());
				saveList.add(saveData);
			
			}
						
			res.setSuccessId(vehId.toString());			
			CompanyProductMaster product = getCompanyProductMasterDropdown(companyId , productId); 
			Integer cdRefNo = null;
			Integer vdRefNo = null;
			Integer msRefNo = null;
			
			if(product.getMotorYn().equalsIgnoreCase("M") ) {
				EserviceMotorDetails motData = eserMotRepo.findByRequestReferenceNoAndRiskId(refNo, vehId)	;	
				MsVehicleDetails vehicleData = msVehRepo.findByVdRefno(Long.valueOf(motData.getVdRefNo()) );
				if(vehicleData!=null) {
					cdRefNo = motData.getCdRefno() ;
					vdRefNo = motData.getVdRefNo();
					msRefNo = motData.getMsRefno();
					
					vehicleData.setUwLoading(totalUwLoading);
					msVehRepo.saveAndFlush(vehicleData);
					
				}
			} else if(product.getMotorYn().equalsIgnoreCase("H") && "4".equalsIgnoreCase(productId) ) {
				EserviceTravelDetails traData = eserTraRepo.findByRequestReferenceNo(refNo)	;
				MsHumanDetails humanData = msHumanRepo.findByVdRefno(Long.valueOf(traData.getVdRefNo()) );
				if(humanData!=null) {
					cdRefNo = traData.getCdRefno() ;
					vdRefNo = traData.getVdRefNo();
					msRefNo = traData.getMsRefno();
					
					humanData.setUwLoading(totalUwLoading);
					msHumanRepo.saveAndFlush(humanData);
					
				}
			} else if(product.getMotorYn().equalsIgnoreCase("H") ) {
				List<EserviceCommonDetails> comDatas = eserHumanRepo.findByRequestReferenceNo(refNo )	;
				for (EserviceCommonDetails comData : comDatas) {
					MsHumanDetails humanData = msHumanRepo.findByVdRefno(Long.valueOf(comData.getVdRefNo()) );
					if(humanData !=null) {
						cdRefNo = comData.getCdRefno() ;
						vdRefNo = comData.getVdRefNo();
						msRefNo = comData.getMsRefno();
						
						humanData.setUwLoading(totalUwLoading);
						msHumanRepo.saveAndFlush(humanData);
						
					}
					
			}
			}else  {
					List<EserviceBuildingDetails> buildings = eserBuildRepo.findByRequestReferenceNo(refNo )	;
					for (EserviceBuildingDetails buildData : buildings) {
						MsAssetDetails assetData = msAssetRepo.findByVdRefno(Long.valueOf(buildData.getVdRefNo()) );
						if(assetData !=null) {
							cdRefNo = buildData.getCdRefno() ;
							vdRefNo = buildData.getVdRefNo();
							msRefNo = buildData.getMsRefno();
							
							assetData.setUwLoading(totalUwLoading);
							msAssetRepo.saveAndFlush(assetData);
						}
					}
					
					
			}
			
			
			for (UwQuestionsDetails  o : saveList )  {
				o.setVdRefNo(vdRefNo);
				o.setCdRefno(cdRefNo);
				o.setMsRefno(msRefNo);
			}
			uwRepo.saveAll(saveList);	
			res.setResponse("Updated Successfully");
				
			} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
	
	public CompanyProductMaster getCompanyProductMasterDropdown(String companyId, String productId) {
		CompanyProductMaster product = new CompanyProductMaster();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			;
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);
			List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
			// Find All
			Root<CompanyProductMaster> c = query.from(CompanyProductMaster.class);
			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productName")));

			// Effective Date Start Max Filter
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, a3);
			// Effective Date End Max Filter
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			Predicate a4 = cb.equal(c.get("productId"), ocpm2.get("productId"));
			Predicate a5 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a4, a5, a6);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), companyId);
			Predicate n5 = cb.equal(c.get("productId"), productId);
			query.where(n1, n2, n3, n4, n5).orderBy(orderList);
			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			product = list.size() > 0 ? list.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return product;
	}
	@Override
	public List<UwQuestionsDetailsRes> getUwQuestionsDetails(UwQuestionsDetailsGetReq req) {
		List<UwQuestionsDetailsRes> resList = new ArrayList<UwQuestionsDetailsRes>();
		
		try {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		List<UwQuestionsDetails> datas = uwRepo.findByCompanyIdAndProductIdAndRequestReferenceNo(req.getCompanyId(),Integer.valueOf(req.getProductId()),req.getRequestReferenceNo());
		for(UwQuestionsDetails data : datas) {
			UwQuestionsDetailsRes res = new UwQuestionsDetailsRes();
			res=dozerMapper.map(data,UwQuestionsDetailsRes.class);
			res.setValue(data.getValue()==null?"":data.getValue());
			resList.add(res);
		}
		}
		catch(Exception e)
		{
		e.printStackTrace();
		log.info("Exception is --->" + e.getMessage());
		return null;
	}
	return resList;
}

	
}
