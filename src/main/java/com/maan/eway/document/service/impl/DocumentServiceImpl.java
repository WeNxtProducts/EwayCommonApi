package com.maan.eway.document.service.impl;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.maan.eway.bean.BuildingDetails;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.ContentAndRisk;
import com.maan.eway.bean.CoverDocumentMaster;
import com.maan.eway.bean.DocumentTransactionDetails;
import com.maan.eway.bean.DocumentUniqueDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.ProductEmployeeDetails;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.SeqDocuniqueid;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.document.req.DocGetReq;
import com.maan.eway.document.req.DocTypeDropDownReq;
import com.maan.eway.document.req.DocTypeReq;
import com.maan.eway.document.req.DocumentDeleteReq;
import com.maan.eway.document.req.DocumentUploadOCRReq;
import com.maan.eway.document.req.DocumentUploadReq;
import com.maan.eway.document.req.FilePathReq;
import com.maan.eway.document.req.GetDocListReq;
import com.maan.eway.document.req.GetEmiDocReq;
import com.maan.eway.document.req.TermsDocUploadReq;
import com.maan.eway.document.req.UpdateVerifiedYnReq;
import com.maan.eway.document.res.ClientDocListRes;
import com.maan.eway.document.res.CommonDoumentRes;
import com.maan.eway.document.res.DocTypeRes;
import com.maan.eway.document.res.DocumentDropdownRes;
import com.maan.eway.document.res.DocumentListRes;
import com.maan.eway.document.res.DocumentSectionList;
import com.maan.eway.document.res.DocumentTypeDetails;
import com.maan.eway.document.res.FilePathRes;
import com.maan.eway.document.res.LocationWiseSections;
import com.maan.eway.document.res.OCRRecogisation;
import com.maan.eway.document.res.TermsDocRes;
import com.maan.eway.document.service.DocumentService;
import com.maan.eway.error.Error;
import com.maan.eway.repository.BuildingDetailsRepository;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.ContentAndRiskRepository;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.DocumentUniqueDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.ProductEmployeesDetailsRepository;
import com.maan.eway.repository.ProductSectionMasterRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.SeqDocuniqueidRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.res.SuccessRes;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;

	@Autowired
	private HomePositionMasterRepository homeRepo;

	@Autowired
	private SectionDataDetailsRepository secRepo;

	@Autowired
	private TravelPassengerDetailsRepository passengerRepo;

	@Autowired
	private CommonDataDetailsRepository humanRepo;

	@Autowired
	private MotorDataDetailsRepository motorRepo;

	@Autowired
	private BuildingDetailsRepository buildingRepo;

	@Autowired
	private BuildingRiskDetailsRepository buildingRiskRepo;

	@Autowired
	private ProductEmployeesDetailsRepository employeeRepo;

	@Autowired
	private SeqDocuniqueidRepository seqDocUniqueRepo;

	@Autowired
	private DocumentUniqueDetailsRepository docUniqueRepo;

	@Autowired
	private DocumentTransactionDetailsRepository docTranRepo;

	@Autowired
	private ContentAndRiskRepository contentRepo;

	private Logger log = LogManager.getLogger(DocumentServiceImpl.class);

	@Value("${file.directoryPath}")
	private String directoryPath;

	@Value("${file.compressedImg}")
	private String compressedImg;

	@Value(value = "${travel.productId}")
	private String travelProductId;

	@Value(value = "${ocr.tesseract.path}")
	private String tesseractPath;

	@Value(value = "${ocr.imageMagic.path}")
	private String magickPath;

	@Value(value = "${ocr.image.path}")
	private String ocrFolder;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ProductEmployeesDetailsRepository paccRepo;
	
	@Autowired
	private ProductSectionMasterRepository productSectionMasterRepo;
	
	

//	@Autowired
//	private CoverDocumentUploadDetailsRepository  documentuploaddetailsrepository;
//	@Autowired
//	private CoverDocumentMasterRepository docRepo;
//	
//	public static  Map<String,String> ALLOWED_CONTENTTYPE;
//	  static {
//		  ALLOWED_CONTENTTYPE = new HashMap<String, String>();
//		  //Image
//		  ALLOWED_CONTENTTYPE.put(".bmp","image/bmp");
//		  ALLOWED_CONTENTTYPE.put(".jpg","image/jpeg");
//		  ALLOWED_CONTENTTYPE.put(".png","image/png");
//		  ALLOWED_CONTENTTYPE.put(".jpeg","image/jpeg");
//		  //Doc
//		  ALLOWED_CONTENTTYPE.put(".doc","application/msword");
//		  ALLOWED_CONTENTTYPE.put(".docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document");
//		  ALLOWED_CONTENTTYPE.put(".pdf","application/pdf");		  
//		  ALLOWED_CONTENTTYPE.put(".xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//		  ALLOWED_CONTENTTYPE.put(".xls","application/vnd.ms-excel");
//		  //Vid
//		  ALLOWED_CONTENTTYPE.put(".avi","video/x-msvideo");
//		  ALLOWED_CONTENTTYPE.put(".3gp","video/3gpp"); 
//		  ALLOWED_CONTENTTYPE.put(".mpeg","video/mpeg");
//		  ALLOWED_CONTENTTYPE.put(".mp4","video/mp4");
//		  ALLOWED_CONTENTTYPE.put(".m4v","video/m4v");
//		  ALLOWED_CONTENTTYPE.put(".flv","video/x-flv");
//		  ALLOWED_CONTENTTYPE.put(".mp4","video/mp4");
//		  ALLOWED_CONTENTTYPE.put(".m3u8","application/x-mpegURL");
//		  ALLOWED_CONTENTTYPE.put(".ts","video/MP2T");
//		  ALLOWED_CONTENTTYPE.put(".3gp","video/3gpp");
//		  ALLOWED_CONTENTTYPE.put(".mov","video/quicktime");
//		  ALLOWED_CONTENTTYPE.put(".avi","video/x-msvideo");
//		  ALLOWED_CONTENTTYPE.put(".wmv","video/x-ms-wmv");
//
//	  }

	public List<CoverDocumentMaster> getDocMasterDropdown(String companyId, String productId, String sectionId) {
		List<CoverDocumentMaster> resList = new ArrayList<CoverDocumentMaster>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CoverDocumentMaster> query = cb.createQuery(CoverDocumentMaster.class);

			Root<CoverDocumentMaster> c = query.from(CoverDocumentMaster.class);

			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("documentDesc")));

			// Effective Date Max Filter
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<CoverDocumentMaster> ocpm1 = effectiveDate.from(CoverDocumentMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));

			Predicate a10 = cb.equal(c.get("documentId"), ocpm1.get("documentId"));
			Predicate a1 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			Predicate a3 = cb.equal(c.get("sectionId"), ocpm1.get("sectionId"));
			Predicate a4 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a5 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a3, a4, a5, a10);

			// Effective Date Max Filter
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<CoverDocumentMaster> ocpm2 = effectiveDate2.from(CoverDocumentMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			Predicate a11 = cb.equal(c.get("documentId"), ocpm2.get("documentId"));
			Predicate a6 = cb.equal(c.get("productId"), ocpm2.get("productId"));
			Predicate a7 = cb.equal(c.get("sectionId"), ocpm2.get("sectionId"));
			Predicate a8 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a9 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a6, a7, a8, a9, a11);

			// Where
			Predicate p1 = cb.equal(c.get("status"), "Y");
			Predicate p2 = cb.equal(c.get("productId"), productId);
			Predicate p3 = cb.equal(c.get("companyId"), companyId);
			Predicate p4 = cb.equal(c.get("sectionId"), sectionId);
			Predicate p5 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate p6 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			query.where(p1, p2, p3, p4, p6, p5).orderBy(orderList);

			TypedQuery<CoverDocumentMaster> result = em.createQuery(query);
			resList = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
		}
		return resList;
	}

	@Override
	public DocumentTypeDetails getLocationWiseSections(DocTypeDropDownReq req) {
		DocumentTypeDetails res = new DocumentTypeDetails();
		try {

			// Common Document
			CommonDoumentRes commonDocuments = getCommonDocument(req);

			// Induvudal Documents
			List<LocationWiseSections> induvidualDocuments = getInduvidualDocument(req);

			res.setCommonDocuments(commonDocuments);
			res.setInduvidualDocuments(induvidualDocuments);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
		}
		return res;
	}

	public CommonDoumentRes getCommonDocument(DocTypeDropDownReq req) {
		CommonDoumentRes res = new CommonDoumentRes();
		try {
			// Common Document
			res.setLocationId("99999");
			res.setLocationName("Common");
			res.setSectionId("99999");
			res.setSectionName("All");
			res.setRiskId("99999");
			res.setId("99999");
			res.setIdType("Common");
			res.setCodeDescLocal("Todos");

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
		}
		return res;
	}

	public List<LocationWiseSections> getInduvidualDocument(DocTypeDropDownReq req) {
		List<LocationWiseSections> resList = new ArrayList<LocationWiseSections>();
		try {
			String quoteNo = req.getQuoteNo();
			HomePositionMaster homeData = homeRepo.findByQuoteNo(quoteNo);

			CompanyProductMaster product = getCompanyProductMasterDropdown(homeData.getCompanyId(),
					homeData.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("H")
					&& homeData.getProductId().equals(Integer.valueOf(travelProductId))) { 
				// Travel Product Details
				resList = getTravelDocument(homeData);

			} else if (product.getMotorYn().equalsIgnoreCase("M")) {
				// Motor Product Details
				resList = getMotorDocument(homeData);

			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				// Asset Product Details
				resList = getAssetDocument(homeData);

			} else {
				// Human Product Details
				resList = getHumanDocument(homeData);

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
		}
		return resList;
	}

	public List<LocationWiseSections> getTravelDocument(HomePositionMaster homeData) {
		List<LocationWiseSections> resList = new ArrayList<LocationWiseSections>();
		try {
			List<ListItemValue> docTypeList = getListItem(homeData.getCompanyId(), homeData.getBranchCode(),
					"DOC_ID_TYPE");

			List<TravelPassengerDetails> passengerList = passengerRepo.findByQuoteNoAndStatusNot(homeData.getQuoteNo(),
					"D");
			List<SectionDataDetails> sectionDatas = secRepo.findByQuoteNoOrderByRiskIdAsc(homeData.getQuoteNo());

			List<DocumentSectionList> sectionList = new ArrayList<DocumentSectionList>();
			for (SectionDataDetails sec : sectionDatas) {

				List<DocumentDropdownRes> idList = new ArrayList<DocumentDropdownRes>();

				for (TravelPassengerDetails passenger : passengerList) {

					// Passnger
					DocumentDropdownRes doc = new DocumentDropdownRes();
					doc.setRiskId(passenger.getPassengerId() == null ? "1" : passenger.getPassengerId().toString());
					doc.setId(passenger.getPassportNo());
					String idType = docTypeList.stream().filter(o -> o.getItemCode().equalsIgnoreCase("T"))
							.collect(Collectors.toList()).get(0).getItemValue();
					doc.setIdType(idType);
					doc.setCodeDescLocal(passenger.getPassportNo());
					idList.add(doc);

				}
				//get Section name Local from session master 
				List<ProductSectionMaster> PSM = productSectionMasterRepo.findBySectionName(sec.getSectionDesc()!=null ? sec.getSectionDesc().toString() : " ");
				
				// Section
				DocumentSectionList sectionRes = new DocumentSectionList();
				sectionRes.setSectionId(sec.getSectionId());
				sectionRes.setSectionName(sec.getSectionDesc());
				sectionRes.setIdList(idList);
				sectionRes.setCodeDescLocal((PSM!=null && PSM.size()>0) ? PSM.get(0).getSectionNameLocal() : " ");
				sectionList.add(sectionRes);
			}

			// Location
			LocationWiseSections loc = new LocationWiseSections();
			loc.setLocationId("1");
			loc.setLocationName(homeData.getProductName());
			loc.setSectionList(sectionList);
			resList.add(loc);
			;

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
		}
		return resList;
	}

	public List<LocationWiseSections> getMotorDocument(HomePositionMaster homeData) {
		List<LocationWiseSections> resList = new ArrayList<LocationWiseSections>();
		try {
			List<ListItemValue> docTypeList = getListItem(homeData.getCompanyId(), homeData.getBranchCode(),
					"DOC_ID_TYPE");

			List<MotorDataDetails> motorList = motorRepo.findByQuoteNo(homeData.getQuoteNo());
			List<SectionDataDetails> sectionDatas = secRepo.findByQuoteNoOrderByRiskIdAsc(homeData.getQuoteNo());

			List<DocumentSectionList> sectionList = new ArrayList<DocumentSectionList>();
			sectionDatas = sectionDatas.stream().filter(distinctByKey(o -> Arrays.asList(o.getSectionId())))
					.collect(Collectors.toList());

			for (SectionDataDetails sec : sectionDatas) {
				List<MotorDataDetails> filtermotList = motorList.stream()
						.filter(o -> o.getSectionId().toString().equals(sec.getSectionId().toString()))
						.collect(Collectors.toList());
				
				//get Section name Local from session master 
				List<ProductSectionMaster> PSM = productSectionMasterRepo.findBySectionNameAndCompanyIdOrderByAmendIdDesc(sec.getSectionDesc()!=null ? sec.getSectionDesc().toString() : " ",homeData.getCompanyId());
				
				DocumentSectionList sectionRes = new DocumentSectionList();
				sectionRes.setSectionId(sec.getSectionId());
				sectionRes.setSectionName(sec.getSectionDesc());
				sectionRes.setCodeDescLocal((PSM!=null && PSM.size()>0) ? PSM.get(0).getSectionNameLocal() : " ");

				List<DocumentDropdownRes> idList = new ArrayList<DocumentDropdownRes>();
				for (MotorDataDetails mot : filtermotList) {

					// Vehicles
					DocumentDropdownRes doc = new DocumentDropdownRes();
					doc.setRiskId(mot.getVehicleId());
					doc.setId(mot.getRegistrationNumber());
					String idType = docTypeList.stream().filter(o -> o.getItemCode().equalsIgnoreCase("M"))
							.collect(Collectors.toList()).get(0).getItemValue();
					doc.setIdType(idType);
					doc.setCodeDescLocal(mot.getRegistrationNumber());
					idList.add(doc);
				}
				sectionRes.setIdList(idList);
				sectionList.add(sectionRes);
			}

			// Location
			LocationWiseSections loc = new LocationWiseSections();
			loc.setLocationId("1");
			loc.setLocationName(homeData.getProductName());
			loc.setSectionList(sectionList);
			resList.add(loc);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
		}
		return resList;
	}

	private static <T> java.util.function.Predicate<T> distinctByKey(
			java.util.function.Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	public List<LocationWiseSections> getAssetDocument(HomePositionMaster homeData) {
		List<LocationWiseSections> resList = new ArrayList<LocationWiseSections>();
		try {
			List<ListItemValue> docTypeList = getListItem(homeData.getCompanyId(), homeData.getBranchCode(),
					"DOC_ID_TYPE");

			List<BuildingRiskDetails> buildingRisk = buildingRiskRepo.findByQuoteNo(homeData.getQuoteNo());
			List<BuildingDetails> buildingList = buildingRepo.findByQuoteNo(homeData.getQuoteNo());
			List<SectionDataDetails> sectionDatas = secRepo.findByQuoteNoOrderByRiskIdAsc(homeData.getQuoteNo());
			List<ProductEmployeeDetails> employeeList = employeeRepo.findByQuoteNo(homeData.getQuoteNo());
			// List<CommonDataDetails> humanList =
			// humanRepo.findByQuoteNo(homeData.getQuoteNo());

			if (buildingList.size() > 0) {
				for (BuildingDetails building : buildingList) {

					List<DocumentSectionList> sectionList = new ArrayList<DocumentSectionList>();
					for (SectionDataDetails sec : sectionDatas) {

						List<DocumentDropdownRes> idList = new ArrayList<DocumentDropdownRes>();

						if (sec.getProductType().equalsIgnoreCase("H")) {

							List<ProductEmployeeDetails> filterEmpList = employeeList.stream()
									.filter(o -> (!o.getStatus().equalsIgnoreCase("D"))
											&& building.getRiskId().equals(o.getRiskId())
											&& o.getSectionId().equalsIgnoreCase(sec.getSectionId()))
									.collect(Collectors.toList());

							if (filterEmpList.size() > 0) {
								for (ProductEmployeeDetails emp : filterEmpList) {
									// Employees Documents
									DocumentDropdownRes doc = new DocumentDropdownRes();
									doc.setRiskId(emp.getEmployeeId() == null ? "1" : emp.getEmployeeId().toString());
//									doc.setId(emp.getNationalityId());
									doc.setId(emp.getLocationName().toString());
									String idType = docTypeList.stream()
											.filter(o -> o.getItemCode().equalsIgnoreCase("H"))
											.collect(Collectors.toList()).get(0).getItemValue();
									doc.setIdType(idType);
									doc.setCodeDescLocal(emp.getNationalityId());
									idList.add(doc);
								}

							}

						} else {
							// Asset Documents
//								if( "3".equalsIgnoreCase(sec.getSectionId()) ||  "2".equalsIgnoreCase(sec.getSectionId()) ||  "47".equalsIgnoreCase(sec.getSectionId()) ||
//										"53".equalsIgnoreCase(sec.getSectionId())  || "39".equalsIgnoreCase(sec.getSectionId())   || "41".equalsIgnoreCase(sec.getSectionId())  ) {
							// Content , All Risk , PLate Glass
							List<ContentAndRisk> contents = contentRepo.findByQuoteNoAndRiskIdAndSectionId(
									homeData.getQuoteNo(), building.getRiskId(), sec.getSectionId());
							// Long count =
							// contentRepo.countByQuoteNoAndRiskIdAndSectionId(homeData.getQuoteNo()
							// ,building.getRiskId() , sec.getSectionId());
							if (contents.size() > 0) {
								for (ContentAndRisk c : contents) {
									DocumentDropdownRes doc = new DocumentDropdownRes();
									doc.setRiskId(
											buildingRisk.size() > 0 ? buildingRisk.get(0).getRiskId().toString() : "1");
//									doc.setId(c.getSerialNoDesc() == null ? "1" : c.getSerialNoDesc());
									doc.setId(c.getLocationName().toString());
									String idType = docTypeList.stream()
											.filter(o -> o.getItemCode().equalsIgnoreCase("A"))
											.collect(Collectors.toList()).get(0).getItemValue();
									doc.setIdType(idType);
									doc.setCodeDescLocal(c.getSerialNoDesc() == null ? "1" : c.getSerialNoDesc());
									idList.add(doc);
								}

							} else {
								DocumentDropdownRes doc = new DocumentDropdownRes();
								doc.setRiskId(
										buildingRisk.size() > 0 ? buildingRisk.get(0).getRiskId().toString() : "1");
//								doc.setId(buildingRisk.size() > 0 ? buildingRisk.get(0).getRiskId().toString() : "1");
								doc.setId(buildingRisk.size() > 0 ? buildingRisk.get(0).getLocationName().toString() : "UNKNOWN");
								String idType = docTypeList.stream().filter(o -> o.getItemCode().equalsIgnoreCase("A"))
										.collect(Collectors.toList()).get(0).getItemValue();
								doc.setIdType(idType);
								doc.setCodeDescLocal(buildingRisk.size() > 0 ? buildingRisk.get(0).getRiskId().toString() : "1");
								idList.add(doc);
							}
//								} else {
//									DocumentDropdownRes doc = new DocumentDropdownRes();
//									doc.setRiskId(buildingRisk.getRiskId()==null ? "1" : buildingRisk.getRiskId().toString());
//									doc.setId(buildingRisk.getRiskId()==null ? "1" : buildingRisk.getRiskId().toString());
//									String idType = docTypeList.stream().filter( o -> o.getItemCode().equalsIgnoreCase("A") ).collect(Collectors.toList()).get(0).getItemValue() ;					
//									doc.setIdType(idType);
//									idList.add(doc);
//								}

						}
						//get Section name Local from session master 
						List<ProductSectionMaster> PSM = productSectionMasterRepo.findBySectionName(sec.getSectionDesc()!=null ? sec.getSectionDesc().toString() : " ");
						
						// Section
						if (idList.size() > 0) {
							DocumentSectionList sectionRes = new DocumentSectionList();
							sectionRes.setSectionId(sec.getSectionId());
							sectionRes.setSectionName(sec.getSectionDesc());
							sectionRes.setIdList(idList);
							sectionRes.setCodeDescLocal((PSM!=null && PSM.size()>0) ? PSM.get(0).getSectionNameLocal() : " ");
							sectionList.add(sectionRes);
						}

					}
					// Location
					if (sectionList.size() > 0) {
						LocationWiseSections loc = new LocationWiseSections();
						loc.setLocationId(building.getRiskId() == null ? "1" : building.getRiskId().toString());
						loc.setLocationName(building.getLocationName());
						loc.setSectionList(sectionList);
						resList.add(loc);
					}

				}
			} else {
				List<DocumentSectionList> sectionList = new ArrayList<DocumentSectionList>();
				for (SectionDataDetails sec : sectionDatas) {

					// Asset Documents
					List<DocumentDropdownRes> idList = new ArrayList<DocumentDropdownRes>();
					DocumentDropdownRes doc = new DocumentDropdownRes();
					doc.setRiskId(buildingRisk.size() > 0 ? buildingRisk.get(0).getRiskId().toString() : "1");
//					doc.setId(buildingRisk.size() > 0 ? buildingRisk.get(0).getRiskId().toString() : "1");
					doc.setId(buildingRisk.size() > 0 ? buildingRisk.get(0).getLocationName().toString() : "UNKNOWN");
					String idType = docTypeList.stream().filter(o -> o.getItemCode().equalsIgnoreCase("A"))
							.collect(Collectors.toList()).get(0).getItemValue();
					doc.setIdType(idType);
					doc.setCodeDescLocal(buildingRisk.size() > 0 ? buildingRisk.get(0).getRiskId().toString() : "1");
					idList.add(doc);
					
					//get Section name Local from session master 
					List<ProductSectionMaster> PSM = productSectionMasterRepo.findBySectionName(sec.getSectionDesc()!=null ? sec.getSectionDesc().toString() : " ");
					// Section
					if (idList.size() > 0) {
						DocumentSectionList sectionRes = new DocumentSectionList();
						sectionRes.setSectionId(sec.getSectionId());
						sectionRes.setSectionName(sec.getSectionDesc());
						sectionRes.setIdList(idList);
						sectionRes.setCodeDescLocal((PSM!=null && PSM.size()>0) ? PSM.get(0).getSectionNameLocal() : " ");
						sectionList.add(sectionRes);
					}

				}
				// Location
				if (sectionList.size() > 0) {
					LocationWiseSections loc = new LocationWiseSections();
					loc.setLocationId(buildingRisk.size() > 0 ? buildingRisk.get(0).getRiskId().toString() : "1");
					loc.setLocationName(buildingRisk.size() > 0 ? buildingRisk.get(0).getProductDesc() : "");
					loc.setSectionList(sectionList);
					resList.add(loc);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
		}
		return resList;
	}

	public List<LocationWiseSections> getHumanDocument(HomePositionMaster homeData) {
		List<LocationWiseSections> resList = new ArrayList<LocationWiseSections>();
		try {
			List<ListItemValue> docTypeList = getListItem(homeData.getCompanyId(), homeData.getBranchCode(),
					"DOC_ID_TYPE");
			List<BuildingDetails> buildingList = buildingRepo.findByQuoteNo(homeData.getQuoteNo());
			List<SectionDataDetails> sectionDatas = secRepo.findByQuoteNoOrderByRiskIdAsc(homeData.getQuoteNo());
			List<ProductEmployeeDetails> employeeList = employeeRepo.findByQuoteNo(homeData.getQuoteNo());

			if (buildingList.size() > 0) {
				for (BuildingDetails building : buildingList) {

					List<DocumentSectionList> sectionList = new ArrayList<DocumentSectionList>();
					for (SectionDataDetails sec : sectionDatas) {

						List<DocumentDropdownRes> idList = new ArrayList<DocumentDropdownRes>();

						List<ProductEmployeeDetails> filterEmpList = employeeList.stream()
								.filter(o -> building.getRiskId().equals(o.getRiskId())
										&& o.getSectionId().equalsIgnoreCase(sec.getSectionId()))
								.collect(Collectors.toList());

						for (ProductEmployeeDetails emp : filterEmpList) {
							// Employees Documents
							DocumentDropdownRes doc = new DocumentDropdownRes();
							doc.setRiskId(emp.getEmployeeId() == null ? "1" : emp.getEmployeeId().toString());
//							doc.setId(StringUtils.isBlank(emp.getEmployeeName()) ? "UNKNOWN" : emp.getEmployeeName().toString());
							doc.setId(emp.getLocationName());
							String idType = docTypeList.stream().filter(o -> o.getItemCode().equalsIgnoreCase("H"))
									.collect(Collectors.toList()).get(0).getItemValue();
							doc.setIdType(idType);
							doc.setCodeDescLocal(emp.getNationalityId());
							idList.add(doc);
						}
						
						//get Section name Local from session master 
						List<ProductSectionMaster> PSM = productSectionMasterRepo.findBySectionName(sec.getSectionDesc()!=null ? sec.getSectionDesc().toString() : " ");
						
						// Section
						DocumentSectionList sectionRes = new DocumentSectionList();
						sectionRes.setSectionId(sec.getSectionId());
						sectionRes.setSectionName(sec.getSectionDesc());
						sectionRes.setIdList(idList);
						sectionRes.setCodeDescLocal((PSM!=null && PSM.size()>0) ? PSM.get(0).getSectionNameLocal() : " ");
						sectionList.add(sectionRes);

					}

					// Location
					if (sectionList.size() > 0) {
						LocationWiseSections loc = new LocationWiseSections();
						loc.setLocationId(building.getRiskId() == null ? "1" : building.getRiskId().toString());
						loc.setLocationName(building.getLocationName());
						loc.setSectionList(sectionList);
						resList.add(loc);
					}
				}

			} else {
				List<DocumentSectionList> sectionList = new ArrayList<DocumentSectionList>();
				for (SectionDataDetails sec : sectionDatas) {

					List<DocumentDropdownRes> idList = new ArrayList<DocumentDropdownRes>();

					List<ProductEmployeeDetails> filterEmpList = employeeList.stream()
							.filter(o -> o.getSectionId().equalsIgnoreCase(sec.getSectionId()))
							.collect(Collectors.toList());

					for (ProductEmployeeDetails emp : filterEmpList) {
						// Employees Documents
						DocumentDropdownRes doc = new DocumentDropdownRes();
						doc.setRiskId(emp.getEmployeeId() == null ? "1" : emp.getEmployeeId().toString());
//						doc.setId(emp.getNationalityId());
//						doc.setId(StringUtils.isBlank(emp.getEmployeeName()) ? "UNKNOWN" : emp.getEmployeeName().toString());
						doc.setId(emp.getLocationName().toString());
						String idType = docTypeList.stream().filter(o -> o.getItemCode().equalsIgnoreCase("H"))
								.collect(Collectors.toList()).get(0).getItemValue();
						doc.setIdType(idType);
						doc.setCodeDescLocal(emp.getNationalityId());
						idList.add(doc);
					}
					//get Section name Local from session master 
					List<ProductSectionMaster> PSM = productSectionMasterRepo.findBySectionName(sec.getSectionDesc()!=null ? sec.getSectionDesc().toString() : " ");
					
					// Section
					DocumentSectionList sectionRes = new DocumentSectionList();
					sectionRes.setSectionId(sec.getSectionId());
					sectionRes.setSectionName(sec.getSectionDesc());
					sectionRes.setIdList(idList);
					sectionRes.setCodeDescLocal((PSM!=null && PSM.size()>0) ? PSM.get(0).getSectionNameLocal() : " ");
					sectionList.add(sectionRes);
				}

				// Location
				LocationWiseSections loc = new LocationWiseSections();
				loc.setLocationId("1");
				loc.setLocationName(homeData.getProductName());
				loc.setSectionList(sectionList);
				resList.add(loc);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
		}
		return resList;
	}

	public synchronized CompanyProductMaster getCompanyProductMasterDropdown(String companyId, String productId) {
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

	public synchronized List<ListItemValue> getListItem(String insuranceId, String branchCode, String itemType) {
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);

			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			// Effective Date Start Max Filter
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("itemId"), ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2);
			// Effective Date End Max Filter
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3, a4);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n12 = cb.equal(c.get("status"), "R");
			Predicate n13 = cb.or(n1, n12);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n4, n5);
			Predicate n9 = cb.or(n6, n7);
			Predicate n10 = cb.equal(c.get("itemType"), itemType);
			// Predicate n11 = cb.equal(c.get("itemCode"), itemCode);
			query.where(n13, n2, n3, n8, n9, n10).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}

	@Override
	public List<Error> docvalidation(DocumentUploadReq req, MultipartFile file) {

		List<Error> errorList = new ArrayList<Error>();

		log.info(req);

		/*
		 * if(StringUtils.isNotBlank(req.getUploadedBy()) ) { boolean containsValue =
		 * ALLOWED_CONTENTTYPE.containsValue(file.getContentType()); if (!containsValue)
		 * { errorList.add(new Error("01", "ReferenceNo", file.getContentType() +
		 * " is Not Allowed for " + req.getOrginalFileName())); }
		 */
		long fileSizeInBytes = file.getSize();
		double size_kb = fileSizeInBytes / 1024;
		double size_mb = size_kb / 1024;

		if (size_mb > 25) {
			errorList.add(new Error("01", "FileSize",
					"File Size Must be 25Mb Current file value is" + size_mb + "MB for " + req.getOriginalFileName()));
		}

		/*
		 * CoverDocumentUploadDetails data =
		 * documentuploaddetailsrepository.findByQuoteNoAndSectionIdAndIdAndDocumentId(
		 * req.getQuoteNo() ,Integer.valueOf(req.getSectionId())
		 * ,Integer.valueOf(req.getId()) , Integer.valueOf(req.getDocumentId()));
		 * 
		 * // if(data!=null ) { // errorList.add(new Error("01", "Document Type",
		 * "This Document Type Already Uploaded" )); // // }
		 */

		if (StringUtils.isBlank(req.getLocationId())) {
			errorList.add(new Error("01", "LocationId", "Please Select Location Id"));
		}
		if (StringUtils.isBlank(req.getLocationName())) {
			errorList.add(new Error("01", "LocationName", "Please Select Location Name"));
		}
		if (StringUtils.isBlank(req.getProductId())) {
			errorList.add(new Error("01", "ProductId", "Please Select Product"));
		}
		if (StringUtils.isBlank(req.getSectionId())) {
			errorList.add(new Error("01", "SectionId", "Please Select Section"));
		}
		if (StringUtils.isBlank(req.getSectionId())) {
			errorList.add(new Error("01", "SectionId", "Please Select Section"));
		}

		if (StringUtils.isBlank(req.getRiskId())) {
			errorList.add(new Error("01", "RiskId", "Please Select RiskId"));
		}

		if (StringUtils.isBlank(req.getId())) {
			errorList.add(new Error("01", "Id", "Please Select Id"));
		}

		if (StringUtils.isBlank(req.getIdType())) {
			errorList.add(new Error("01", "IdType", "Please Select IdType"));
		}

		if (StringUtils.isBlank(req.getDocumentId())) {
			errorList.add(new Error("01", "DocumentId", "Please Select Document Type"));
		}

		if (StringUtils.isBlank(req.getUploadedBy())) {
			errorList.add(new Error("01", "UploadedBy", "Please Select Uploaded By"));
		}
		return errorList;

	}

	@Override
	@Transactional
	public CommonRes fileupload(DocumentUploadReq req, MultipartFile file) {
		CommonRes res = new CommonRes();

		try {
			HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo());
			CompanyProductMaster product = getCompanyProductMasterDropdown(homeData.getCompanyId(),
					homeData.getProductId().toString());
			ProductSectionMaster secData = getProductSectionDropdown(req.getInsuranceId(), req.getProductId(),
					req.getSectionId());

			CoverDocumentMaster docDetails = new CoverDocumentMaster();
			if (StringUtils.isNotBlank(req.getTermsAndCondtionYn())
					&& req.getTermsAndCondtionYn().equalsIgnoreCase("Y")) {
				docDetails = getByDocumentId(homeData.getCompanyId(), homeData.getProductId(), "99999",
						req.getDocumentId());
			} else {
				docDetails = getByDocumentId(homeData.getCompanyId(), homeData.getProductId(), req.getSectionId(),
						req.getDocumentId());
			}

			// Copy File
			Random random = new Random();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());

			String newfilename = "";
			String newfilename1 = "";
			// OrginalFile
			Path destination = Paths.get(directoryPath);
			newfilename = random.nextInt(100)
					+ timestamp.toString().replace(":", "T").replace(" ", "S").replace("-", "H").replace(".", "D") + "."
					+ FilenameUtils.getExtension(file.getOriginalFilename());
			Files.copy(file.getInputStream(), destination.resolve(newfilename));

			Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
			// BackupFile
			Path destination1 = Paths.get(compressedImg);
			newfilename1 = random.nextInt(100)
					+ timestamp1.toString().replace(":", "T").replace(" ", "S").replace("-", "H").replace(".", "D")
					+ "." + FilenameUtils.getExtension(file.getOriginalFilename());
			Files.copy(file.getInputStream(), destination1.resolve(newfilename1));

			// Save Document Unique Details
			DocumentUniqueDetails uniqDoc = new DocumentUniqueDetails();
			String uniqueId = genDocUniqueId();

			{
				uniqDoc.setUniqueId(Integer.valueOf(uniqueId));
				uniqDoc.setUploadedBy(req.getUploadedBy());
				uniqDoc.setDocApplicable(docDetails.getDocApplicable());
				uniqDoc.setDocApplicableId(
						docDetails.getDocApplicableId() == null ? null : docDetails.getDocApplicableId().toString());
				uniqDoc.setFileName(newfilename);
				uniqDoc.setFilePathOrginal(directoryPath + newfilename);
				uniqDoc.setFilePathBackup(compressedImg + newfilename1);
				uniqDoc.setOrginalFileName(file.getOriginalFilename());
				uniqDoc.setUploadedTime(new Date());
				uniqDoc.setDocumentId(Integer.valueOf(req.getDocumentId()));
				uniqDoc.setDocumentType(docDetails.getDocumentType());
				uniqDoc.setDocumentTypeDesc(docDetails.getDocumentTypeDesc());
				uniqDoc.setDocumentDesc(docDetails.getDocumentDesc());
				uniqDoc.setDocumentName(docDetails.getDocumentName());
				uniqDoc.setEntryDate(new Date());
				uniqDoc.setUploadedTime(new Date());
				uniqDoc.setStatus("Y");
				uniqDoc.setId(req.getId());
				uniqDoc.setIdType(req.getIdType());
				uniqDoc.setProductType(secData == null ? product.getMotorYn() : secData.getMotorYn());
				uniqDoc.setVerifiedYn(StringUtils.isBlank(req.getVerifiedYn())?"N":req.getVerifiedYn());
				
				if ("Y".equalsIgnoreCase(req.getEmiYn())) {
					uniqDoc.setEmiYn(req.getEmiYn() == null ? null : req.getEmiYn());
					uniqDoc.setInstallmentPeriod(
							req.getInstallmentPeriod() == null ? null : req.getInstallmentPeriod());
					uniqDoc.setNoOfInstallment(req.getNoOfInstallment() == null ? null : req.getNoOfInstallment());
				}
				
				//adding local description feilds 
				uniqDoc.setIdTypeLocal(req.getIdType());
				//uniqDoc.setDocumentApplicableLocal(docDetails.getDocApplicableLocal());
				uniqDoc.setDocumentNameLocal(docDetails.getDocumentNameLocal());
				uniqDoc.setDocumentDescLocal(docDetails.getDocumentDescLocal());
				uniqDoc.setDocumentTypeDescLocal(docDetails.getDocumentTypeDescLocal());
				docUniqueRepo.saveAndFlush(uniqDoc);
			}

			// Save Document Transaction Details
			DocumentTransactionDetails docTran = new DocumentTransactionDetails();
			{
				docTran.setUniqueId(Integer.valueOf(uniqueId));
				docTran.setId(req.getId());
				docTran.setIdType(req.getIdType());
				docTran.setRequestReferenceNo(homeData.getRequestReferenceNo());
				docTran.setQuoteNo(req.getQuoteNo());
				docTran.setCompanyId(homeData.getCompanyId());
				docTran.setCompanyName(homeData.getCompanyName());
				docTran.setProductId(homeData.getProductId());
				docTran.setProductName(homeData.getProductName());
				docTran.setSectionId(Integer.valueOf(req.getSectionId()));
				docTran.setSectionName(secData == null ? "All" : secData.getSectionName());
				docTran.setProductType(secData == null ? product.getMotorYn() : secData.getMotorYn());
				docTran.setLocationId(Integer.valueOf(req.getLocationId()));
				docTran.setLocationName(req.getLocationName());
				docTran.setRiskId(Integer.valueOf(req.getRiskId()));
				docTran.setCreatedBy(req.getUploadedBy());
				docTran.setEntryDate(timestamp1);
				docTran.setStatus("Y");
				docTran.setEntryDate(new Date());

				if (StringUtils.isNotBlank(req.getEndorsementType())) {
					EndtTypeMaster entMaster = endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeId(
							req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
							Integer.valueOf(req.getEndorsementType()));
					if (entMaster != null) {
						docTran.setEndorsementDate(req.getEndorsementDate() == null ? null : new Date());
						docTran.setEndorsementEffdate(
								req.getEndorsementEffdate() == null ? null : req.getEndorsementEffdate());
						docTran.setEndorsementRemarks(
								req.getEndorsementRemarks() == null ? "" : req.getEndorsementRemarks());
						docTran.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
						docTran.setIsFinaceYn(entMaster.getEndtTypeCategoryId() == 2 ? "Y" : "N");
						docTran.setEndtCategDesc(entMaster.getEndtTypeCategory());
						docTran.setEndtStatus("P");
						docTran.setEndtCount(new BigDecimal(req.getEndtCount()));
						docTran.setEndtPrevPolicyNo(req.getEndtPrevPolicyNo());
						docTran.setEndtPrevQuoteNo(req.getEndtPrevQuoteNo());
						
						//insert local description
						docTran.setEndorsementTypeDescLocal(entMaster.getEndtTypeCategory());
					}
				}
				if ("Y".equalsIgnoreCase(req.getEmiYn())) {
					docTran.setEmiYn(req.getEmiYn() == null ? null : req.getEmiYn());
					docTran.setInstallmentPeriod(
							req.getInstallmentPeriod() == null ? null : req.getInstallmentPeriod());
					docTran.setNoOfInstallment(req.getNoOfInstallment() == null ? null : req.getNoOfInstallment());
				}
				//insert local description
				docTran.setProductNameLocal(homeData.getProductName());
				
				docTran.setSectionNameLocal(secData == null ? "All" : secData.getSectionName());
				
				docTranRepo.saveAndFlush(docTran);
			}

			res.setCommonResponse("File Upload Sucessfully");
			res.setIsError(false);
		} catch (Exception e) {
			e.printStackTrace();
			res.setCommonResponse(null);
			List<Error> error = new ArrayList<Error>();
			error.add(new Error("01", "Upload Error", e.getMessage()));
			res.setErrorMessage(error);
			res.setIsError(true);
		}
		return res;
	}

	public ProductSectionMaster getProductSectionDropdown(String companyId, String productId, String sectionId) {
		ProductSectionMaster section = new ProductSectionMaster();
		try {
			List<ProductSectionMaster> sectionList = new ArrayList<ProductSectionMaster>();

			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductSectionMaster> query = cb.createQuery(ProductSectionMaster.class);

			// Find All
			Root<ProductSectionMaster> c = query.from(ProductSectionMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("sectionName")));

			// Effective Date Max Filter
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<ProductSectionMaster> ocpm1 = effectiveDate.from(ProductSectionMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("sectionId"), ocpm1.get("sectionId"));
			Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a3 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, a3, a4);

			// Effective Date End
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<ProductSectionMaster> ocpm2 = effectiveDate2.from(ProductSectionMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			Predicate a5 = cb.equal(c.get("sectionId"), ocpm2.get("sectionId"));
			Predicate a7 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a8 = cb.equal(c.get("productId"), ocpm2.get("productId"));

			Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a5, a6, a7, a8);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), companyId);
			Predicate n5 = cb.equal(c.get("productId"), productId);
			Predicate n6 = cb.equal(c.get("sectionId"), sectionId);
			query.where(n1, n2, n3, n4, n5, n6).orderBy(orderList);
			// Get Result
			TypedQuery<ProductSectionMaster> result = em.createQuery(query);
			sectionList = result.getResultList();
			section = sectionList.size() > 0 ? sectionList.get(0) : null;

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return section;
	}

	@Override
	public CommonRes deleteFile(DocumentDeleteReq req) {
		CommonRes commonRes = new CommonRes();
		SuccessRes res = new SuccessRes();
		try {

			docTranRepo.deleteByQuoteNoAndUniqueIdAndId(req.getQuoteNo(), Integer.valueOf(req.getUniqueId()),
					req.getId());

			res.setResponse("Document Deleted Sucessfully");
			res.setSuccessId(req.getUniqueId());

			commonRes.setCommonResponse(res);
			commonRes.setIsError(false);
			commonRes.setErrorMessage(Collections.emptyList());
			commonRes.setMessage("File Upload Faild");

		} catch (Exception e) {
			e.printStackTrace();
			List<Error> errors = new ArrayList<Error>();
			errors.add(new Error("01", "Failed", "Document Deleted Failed"));
			commonRes.setCommonResponse(res);
			commonRes.setIsError(true);
			commonRes.setErrorMessage(errors);
			commonRes.setMessage("File Upload Faild");
		}
		return commonRes;
	}

	public synchronized String genDocUniqueId() {
		try {
			SeqDocuniqueid entity;
			entity = seqDocUniqueRepo.save(new SeqDocuniqueid());
			return String.format("%05d", entity.getDocUniqueId());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}

	}

	@Override
	public List<DocTypeRes> getDocTypeDropDowns(DocTypeReq req) {
		List<DocTypeRes> resList = new ArrayList<DocTypeRes>();
		try {

			List<CoverDocumentMaster> docList = getDocMasterDropdown(req.getCompanyId(), req.getProductId(),
					req.getSectionId());

			String documentType = req.getSectionId().equalsIgnoreCase("99999") ? "1" : "2";

			List<CoverDocumentMaster> filterDocList = docList.stream()
					.filter(o -> o.getDocumentType().equalsIgnoreCase(documentType)).collect(Collectors.toList());

			for (CoverDocumentMaster data : filterDocList) {

				DocTypeRes res = new DocTypeRes();
				res.setCode(data.getDocumentId().toString());
				res.setCodeDesc(data.getDocumentDesc());
				res.setCodeDescLocal(data.getDocumentDescLocal());
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
		}
		return resList;
	}

	@Override
	public DocumentListRes getTotalDocList(GetDocListReq req) {
		DocumentListRes docRes = new DocumentListRes();
		try {
			List<DocumentTransactionDetails> docList = docTranRepo.findByQuoteNo(req.getQuoteNo());

			List<Integer> uniqueIds = docList.stream().map(DocumentTransactionDetails::getUniqueId)
					.collect(Collectors.toList());

			List<DocumentUniqueDetails> docUniqueList = docUniqueRepo.findByUniqueIdIn(uniqueIds);

			List<ClientDocListRes> totalDocList = new ArrayList<ClientDocListRes>();
			for (DocumentTransactionDetails doc : docList) {
				ClientDocListRes res = new ClientDocListRes();

				// Transaction Related
				res.setInsuranceId(doc.getCompanyId());
				res.setCompanyName(doc.getCompanyName());
				res.setId(doc.getId());
				res.setIdType(doc.getIdType());
				res.setLocationId(doc.getLocationId() == null ? "" : doc.getLocationId().toString());
				res.setLocationName(doc.getLocationName());
				res.setProductId(doc.getProductId() == null ? "" : doc.getProductId().toString());
				res.setProductType(doc.getProductType());
				res.setProducttName(doc.getProductName());
				res.setQuoteNo(doc.getQuoteNo());
				res.setRequestReferenceNo(doc.getRequestReferenceNo());
				res.setRiskId(doc.getRiskId() == null ? "" : doc.getRiskId().toString());
				res.setSectionId(doc.getSectionId() == null ? "" : doc.getSectionId().toString());
				res.setSectionName(doc.getSectionName());
				res.setUniqueId(doc.getUniqueId() == null ? "" : doc.getUniqueId().toString());

				List<DocumentUniqueDetails> filterUnique = docUniqueList.stream()
						.filter(o -> o.getUniqueId().equals(doc.getUniqueId())).collect(Collectors.toList());

				if (filterUnique.size() > 0) {
					DocumentUniqueDetails unique = filterUnique.get(0);
					// Document Related
					res.setDocApplicable(unique.getDocApplicable());
					res.setDocApplicableId(unique.getDocApplicableId());
					res.setDocumentDesc(unique.getDocumentDesc());
					res.setDocumentId(unique.getDocumentId() == null ? "" : unique.getDocumentId().toString());
					res.setDocumentName(unique.getDocumentName());
					res.setDocumentType(unique.getDocumentType());
					res.setDocumentTypeDesc(unique.getDocumentTypeDesc());
					res.setFileName(unique.getFileName());
					res.setFilePathOriginal(unique.getFilePathBackup());
					res.setOriginalFileName(unique.getOrginalFileName());
					res.setStatus(unique.getStatus());
					res.setUploadedBy(unique.getUploadedBy());
					res.setUploadedTime(unique.getUploadedTime());
					res.setVerifiedYn(unique.getVerifiedYn()==null?"N":unique.getVerifiedYn());
					
				}

				// Endorsement Related
				if (doc.getEndorsementType() != null) {
					res.setEndorsementDate(doc.getEndorsementDate() == null ? null : new Date());
					res.setEndorsementEffdate(doc.getEndorsementEffdate() == null ? null : doc.getEndorsementEffdate());
					res.setEndorsementRemarks(doc.getEndorsementRemarks() == null ? "" : doc.getEndorsementRemarks());
					res.setEndorsementTypeDesc(doc.getEndorsementTypeDesc());
					res.setIsFinaceYn(doc.getIsFinaceYn());
					res.setEndtCategDesc(doc.getEndtCategDesc());
					res.setEndtStatus(doc.getEndtStatus());
					res.setEndtCount(doc.getEndtCount());
					res.setEndtPrevPolicyNo(doc.getEndtPrevPolicyNo());
					res.setEndtPrevQuoteNo(doc.getEndtPrevQuoteNo());
					res.setOriginalPolicyNo(doc.getOriginalPolicyNo());

				}

				totalDocList.add(res);

			}

			List<ClientDocListRes> filterCommonDocList = totalDocList.stream()
					.filter(o -> o.getSectionId().equalsIgnoreCase("99999")).collect(Collectors.toList());
			List<ClientDocListRes> filterInduvidualDocList = totalDocList.stream()
					.filter(o -> !o.getSectionId().equalsIgnoreCase("99999")).collect(Collectors.toList());
			filterInduvidualDocList.sort(Comparator.comparing(ClientDocListRes::getLocationName)
					.thenComparing(ClientDocListRes::getSectionName));
			docRes.setCommmonDocument(filterCommonDocList);
			docRes.setInduvidualDocument(filterInduvidualDocList);

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
		}
		return docRes;
	}

	@Override
	public FilePathRes getFilePath(FilePathReq req) {
		FilePathRes res = new FilePathRes();
		try {
			DocumentTransactionDetails doc = docTranRepo.findByQuoteNoAndUniqueIdAndId(req.getQuoteNo(),
					Integer.valueOf(req.getUniqueId()), req.getId());

			// Transaction Related
			res.setInsuranceId(doc.getCompanyId());
			res.setCompanyName(doc.getCompanyName());
			res.setId(doc.getId());
			res.setIdType(doc.getIdType());
			res.setLocationId(doc.getLocationId() == null ? "" : doc.getLocationId().toString());
			res.setLocationName(doc.getLocationName());
			res.setProductId(doc.getProductId() == null ? "" : doc.getProductId().toString());
			res.setProductType(doc.getProductType());
			res.setProducttName(doc.getProductName());
			res.setQuoteNo(doc.getQuoteNo());
			res.setRequestReferenceNo(doc.getRequestReferenceNo());
			res.setRiskId(doc.getRiskId() == null ? "" : doc.getRiskId().toString());
			res.setSectionId(doc.getSectionId() == null ? "" : doc.getSectionId().toString());
			res.setSectionName(doc.getSectionName());
			res.setUniqueId(doc.getUniqueId() == null ? "" : doc.getUniqueId().toString());

			// Document Details
			DocumentUniqueDetails unique = docUniqueRepo.findByUniqueId(Integer.valueOf(req.getUniqueId()));

			if (unique != null) {
				// Document Related
				res.setDocApplicable(unique.getDocApplicable());
				res.setDocApplicableId(unique.getDocApplicableId());
				res.setDocumentDesc(unique.getDocumentDesc());
				res.setDocumentId(unique.getDocumentId() == null ? "" : unique.getDocumentId().toString());
				res.setDocumentName(unique.getDocumentName());
				res.setDocumentType(unique.getDocumentType());
				res.setDocumentTypeDesc(unique.getDocumentTypeDesc());
				res.setFileName(unique.getFileName());
				res.setFilePathOriginal(unique.getFilePathOrginal());
				res.setFilepathname(unique.getFilePathOrginal());
				res.setOriginalFileName(unique.getOrginalFileName());
				res.setStatus(unique.getStatus());
				res.setUploadedBy(unique.getUploadedBy());
				res.setUploadedTime(unique.getUploadedTime());

				if (StringUtils.isNotBlank(res.getFilepathname()) && new File(res.getFilepathname()).exists()) {
					res.setImgurl(new GetFileFromPath(res.getFilepathname()).call().getImgUrl());
				} else
					System.out.println("File is Not found!!" + res.getFilepathname());
			}

		} catch (Exception e) {
			e.printStackTrace();
			// Log.info("Exception Is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public FilePathRes getCompressedImages(FilePathReq req) {
		FilePathRes res = new FilePathRes();
		try {
			DocumentTransactionDetails doc = docTranRepo.findByQuoteNoAndUniqueIdAndId(req.getQuoteNo(),
					Integer.valueOf(req.getUniqueId()), req.getId());

			// Transaction Related
			res.setInsuranceId(doc.getCompanyId());
			res.setCompanyName(doc.getCompanyName());
			res.setId(doc.getId());
			res.setIdType(doc.getIdType());
			res.setLocationId(doc.getLocationId() == null ? "" : doc.getLocationId().toString());
			res.setLocationName(doc.getLocationName());
			res.setProductId(doc.getProductId() == null ? "" : doc.getProductId().toString());
			res.setProductType(doc.getProductType());
			res.setProducttName(doc.getProductName());
			res.setQuoteNo(doc.getQuoteNo());
			res.setRequestReferenceNo(doc.getRequestReferenceNo());
			res.setRiskId(doc.getRiskId() == null ? "" : doc.getRiskId().toString());
			res.setSectionId(doc.getSectionId() == null ? "" : doc.getSectionId().toString());
			res.setSectionName(doc.getSectionName());
			res.setUniqueId(doc.getUniqueId() == null ? "" : doc.getUniqueId().toString());

			// Document Details
			DocumentUniqueDetails unique = docUniqueRepo.findByUniqueId(Integer.valueOf(req.getUniqueId()));

			if (unique != null) {
				// Document Related
				res.setDocApplicable(unique.getDocApplicable());
				res.setDocApplicableId(unique.getDocApplicableId());
				res.setDocumentDesc(unique.getDocumentDesc());
				res.setDocumentId(unique.getDocumentId() == null ? "" : unique.getDocumentId().toString());
				res.setDocumentName(unique.getDocumentName());
				res.setDocumentType(unique.getDocumentType());
				res.setDocumentTypeDesc(unique.getDocumentTypeDesc());
				res.setFileName(unique.getFileName());
				res.setFilePathOriginal(unique.getFilePathBackup());
				res.setFilepathname(unique.getFilePathBackup());
				res.setOriginalFileName(unique.getOrginalFileName());
				res.setStatus(unique.getStatus());
				res.setUploadedBy(unique.getUploadedBy());
				res.setUploadedTime(unique.getUploadedTime());

				if (StringUtils.isNotBlank(res.getFilepathname()) && new File(res.getFilepathname()).exists()) {
					res.setImgurl(new GetFileFromPath(res.getFilepathname()).call().getImgUrl());
				} else
					System.out.println("File is Not found!!" + res.getFilepathname());
			}

		} catch (Exception e) {
			e.printStackTrace();
			// Log.info("Exception Is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public List<DocTypeRes> getDocTypes(DocTypeDropDownReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	public CoverDocumentMaster getByDocumentId(String insId, Integer productId, String sectionId, String documentId) {
		CoverDocumentMaster res = new CoverDocumentMaster();

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CoverDocumentMaster> query = cb.createQuery(CoverDocumentMaster.class);
			List<CoverDocumentMaster> list = new ArrayList<CoverDocumentMaster>();

			// Find All
			Root<CoverDocumentMaster> c = query.from(CoverDocumentMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(c.get("effectiveDateStart")));

			// Where
			jakarta.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			jakarta.persistence.criteria.Predicate n11 = cb.equal(c.get("status"), "R");
			Predicate n12 = cb.or(n1, n11);
			jakarta.persistence.criteria.Predicate n3 = cb.equal(c.get("companyId"), insId);
			jakarta.persistence.criteria.Predicate n4 = cb.equal(c.get("productId"), productId);
			jakarta.persistence.criteria.Predicate n5 = cb.equal(c.get("sectionId"), sectionId);
			jakarta.persistence.criteria.Predicate n7 = cb.equal(c.get("documentId"), documentId);
			query.where(n12, n3, n4, n5, n7).orderBy(orderList);

			// Get Result
			TypedQuery<CoverDocumentMaster> result = em.createQuery(query);
			list = result.getResultList();
			res = list.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public List<Error> doctermsvalidation(TermsDocUploadReq req, MultipartFile file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommonRes termsfileupload(TermsDocUploadReq req, MultipartFile file) {
		CommonRes res = new CommonRes();

		try {

			// Copy File
			Random random = new Random();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());

			String newfilename = "";
			String newfilename1 = "";
			// OrginalFile
			Path destination = Paths.get(directoryPath);
			newfilename = random.nextInt(100)
					+ timestamp.toString().replace(":", "T").replace(" ", "S").replace("-", "H").replace(".", "D") + "."
					+ FilenameUtils.getExtension(file.getOriginalFilename());
			Files.copy(file.getInputStream(), destination.resolve(newfilename));

			Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
			// BackupFile
			Path destination1 = Paths.get(compressedImg);
			newfilename1 = random.nextInt(100)
					+ timestamp1.toString().replace(":", "T").replace(" ", "S").replace("-", "H").replace(".", "D")
					+ "." + FilenameUtils.getExtension(file.getOriginalFilename());
			Files.copy(file.getInputStream(), destination1.resolve(newfilename1));

			// Save Document Unique Details
			DocumentUniqueDetails uniqDoc = new DocumentUniqueDetails();
			String uniqueId = genDocUniqueId();

			uniqDoc.setUniqueId(Integer.valueOf(uniqueId));
			uniqDoc.setUploadedBy(req.getUploadedBy());
			uniqDoc.setDocApplicable(req.getTermsAndCondtionDesc());
			uniqDoc.setDocApplicableId(req.getTermsAndCondtionId());
			uniqDoc.setFileName(newfilename);
			uniqDoc.setFilePathOrginal(directoryPath + newfilename);
			uniqDoc.setFilePathBackup(compressedImg + newfilename1);
			uniqDoc.setOrginalFileName(file.getOriginalFilename());
			uniqDoc.setUploadedTime(new Date());
			uniqDoc.setDocumentId(Integer.valueOf(req.getTermsAndCondtionId()));
			uniqDoc.setDocumentType(req.getType());
			uniqDoc.setDocumentTypeDesc(req.getTermsAndCondtionDesc());
			uniqDoc.setDocumentDesc(req.getTermsAndCondtionDesc());
			uniqDoc.setDocumentName(req.getTermsAndCondtionDesc());
			uniqDoc.setEntryDate(new Date());
			uniqDoc.setUploadedTime(new Date());
			uniqDoc.setStatus("Y");
			uniqDoc.setId(req.getTermsAndCondtionId());
			uniqDoc.setIdType(req.getTermsAndCondtionDesc());
			uniqDoc.setProductType(req.getType());

			docUniqueRepo.saveAndFlush(uniqDoc);
			SuccessRes sucRes = new SuccessRes();
			sucRes.setResponse("File Upload Sucessfully");
			sucRes.setSuccessId(uniqueId);

			res.setCommonResponse(sucRes);
			res.setIsError(false);
		} catch (Exception e) {
			e.printStackTrace();
			res.setCommonResponse(null);
			List<Error> error = new ArrayList<Error>();
			error.add(new Error("01", "Upload Error", e.getMessage()));
			res.setErrorMessage(error);
			res.setIsError(true);
		}
		return res;
	}

	@Override
	public TermsDocRes getTermsFilePath(DocGetReq req) {
		TermsDocRes res = new TermsDocRes();
		try {

			// Document Details
			DocumentUniqueDetails unique = docUniqueRepo.findByUniqueId(Integer.valueOf(req.getUniqueId()));

			if (unique != null) {
				// Document Related
				res.setDocApplicable(unique.getDocApplicable());
				res.setDocApplicableId(unique.getDocApplicableId());
				res.setDocumentDesc(unique.getDocumentDesc());
				res.setDocumentId(unique.getDocumentId() == null ? "" : unique.getDocumentId().toString());
				res.setDocumentName(unique.getDocumentName());
				res.setDocumentType(unique.getDocumentType());
				res.setDocumentTypeDesc(unique.getDocumentTypeDesc());
				res.setFileName(unique.getFileName());
				res.setFilePathOriginal(unique.getFilePathBackup());
				res.setFilepathname(unique.getFilePathBackup());
				res.setOriginalFileName(unique.getOrginalFileName());
				res.setStatus(unique.getStatus());
				res.setUploadedBy(unique.getUploadedBy());
				res.setUploadedTime(unique.getUploadedTime());
				res.setProductType(unique.getProductType());
				res.setUniqueId(req.getUniqueId());

				if (StringUtils.isNotBlank(res.getFilepathname()) && new File(res.getFilepathname()).exists()) {
					res.setImgurl(new GetFileFromPath(res.getFilepathname()).call().getImgUrl());
				} else
					System.out.println("File is Not found!!" + res.getFilepathname());
			}

		} catch (Exception e) {
			e.printStackTrace();
			// Log.info("Exception Is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public DocumentListRes getEmiDoc(GetEmiDocReq req) {
		DocumentListRes docRes = new DocumentListRes();
		try {
			List<DocumentTransactionDetails> docList = docTranRepo.findByQuoteNoAndInstallmentPeriodAndNoOfInstallment(
					req.getQuoteNo(), req.getInstallmentPeriod(), req.getNoOfInstallment());

			List<Integer> uniqueIds = docList.stream().map(DocumentTransactionDetails::getUniqueId)
					.collect(Collectors.toList());

			List<DocumentUniqueDetails> docUniqueList = docUniqueRepo.findByUniqueIdIn(uniqueIds);

			List<ClientDocListRes> totalDocList = new ArrayList<ClientDocListRes>();
			for (DocumentTransactionDetails doc : docList) {
				ClientDocListRes res = new ClientDocListRes();

				// Transaction Related
				res.setInsuranceId(doc.getCompanyId());
				res.setCompanyName(doc.getCompanyName());
				res.setId(doc.getId());
				res.setIdType(doc.getIdType());
				res.setLocationId(doc.getLocationId() == null ? "" : doc.getLocationId().toString());
				res.setLocationName(doc.getLocationName());
				res.setProductId(doc.getProductId() == null ? "" : doc.getProductId().toString());
				res.setProductType(doc.getProductType());
				res.setProducttName(doc.getProductName());
				res.setQuoteNo(doc.getQuoteNo());
				res.setRequestReferenceNo(doc.getRequestReferenceNo());
				res.setRiskId(doc.getRiskId() == null ? "" : doc.getRiskId().toString());
				res.setSectionId(doc.getSectionId() == null ? "" : doc.getSectionId().toString());
				res.setSectionName(doc.getSectionName());
				res.setUniqueId(doc.getUniqueId() == null ? "" : doc.getUniqueId().toString());

				List<DocumentUniqueDetails> filterUnique = docUniqueList.stream()
						.filter(o -> o.getUniqueId().equals(doc.getUniqueId())).collect(Collectors.toList());

				if (filterUnique.size() > 0) {
					DocumentUniqueDetails unique = filterUnique.get(0);
					// Document Related
					res.setDocApplicable(unique.getDocApplicable());
					res.setDocApplicableId(unique.getDocApplicableId());
					res.setDocumentDesc(unique.getDocumentDesc());
					res.setDocumentId(unique.getDocumentId() == null ? "" : unique.getDocumentId().toString());
					res.setDocumentName(unique.getDocumentName());
					res.setDocumentType(unique.getDocumentType());
					res.setDocumentTypeDesc(unique.getDocumentTypeDesc());
					res.setFileName(unique.getFileName());
					res.setFilePathOriginal(unique.getFilePathBackup());
					res.setOriginalFileName(unique.getOrginalFileName());
					res.setStatus(unique.getStatus());
					res.setUploadedBy(unique.getUploadedBy());
					res.setUploadedTime(unique.getUploadedTime());
				}

				// Endorsement Related
				if (doc.getEndorsementType() != null) {
					res.setEndorsementDate(doc.getEndorsementDate() == null ? null : new Date());
					res.setEndorsementEffdate(doc.getEndorsementEffdate() == null ? null : doc.getEndorsementEffdate());
					res.setEndorsementRemarks(doc.getEndorsementRemarks() == null ? "" : doc.getEndorsementRemarks());
					res.setEndorsementTypeDesc(doc.getEndorsementTypeDesc());
					res.setIsFinaceYn(doc.getIsFinaceYn());
					res.setEndtCategDesc(doc.getEndtCategDesc());
					res.setEndtStatus(doc.getEndtStatus());
					res.setEndtCount(doc.getEndtCount());
					res.setEndtPrevPolicyNo(doc.getEndtPrevPolicyNo());
					res.setEndtPrevQuoteNo(doc.getEndtPrevQuoteNo());
					res.setOriginalPolicyNo(doc.getOriginalPolicyNo());

				}

				totalDocList.add(res);

			}

			List<ClientDocListRes> filterCommonDocList = totalDocList.stream()
					.filter(o -> o.getSectionId().equalsIgnoreCase("99999")).collect(Collectors.toList());
			List<ClientDocListRes> filterInduvidualDocList = totalDocList.stream()
					.filter(o -> !o.getSectionId().equalsIgnoreCase("99999")).collect(Collectors.toList());
			filterInduvidualDocList.sort(Comparator.comparing(ClientDocListRes::getLocationName)
					.thenComparing(ClientDocListRes::getSectionName));
			docRes.setCommmonDocument(filterCommonDocList);
			docRes.setInduvidualDocument(filterInduvidualDocList);

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
		}
		return docRes;
	}

	@Override
	public CommonRes fileuploadOCR(DocumentUploadOCRReq req) {
		CommonRes res = new CommonRes();
		OCRRecogisation ocrResponse = new OCRRecogisation();

		log.info("Input Image Path ----------------------------->" + req.getFilePath());

		if (isPDF(req.getFilePath())) {
			try {

				// Set the color management system (Option 2 from above)
				System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");

				ocrResponse = convertPdfToImages(req, ocrFolder);

				System.out.println("PDF converted to image successfully.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			
			String folderPath = ocrFolder + "\\"+System.currentTimeMillis();
			
			File file = new File(folderPath);
			
			if(!file.exists()) {
				file.mkdirs();
			}
			
			String imagePath = file.getAbsolutePath()+"\\"+1;
			String textPath = file.getAbsolutePath()+"\\"+2;
			
			ocrResponse = commandLineArgsForTesseract(req, imagePath, textPath);
		}

		if (ocrResponse != null) {

			Double accuracy = ocrResponse.getPercentage() == null ? 0d : ocrResponse.getPercentage();

			if (accuracy >= 80 && accuracy <= 100) {
				res.setCommonResponse(ocrResponse);
				res.setMessage("Success");
				res.setIsError(false);
				res.setErroCode(0);
			} else {
				res.setCommonResponse(ocrResponse);
				res.setMessage("Failure");
				res.setIsError(true);
				res.setErroCode(0);
			}
		}

		return res;
	}

	public OCRRecogisation commandLineArgsForTesseract(DocumentUploadOCRReq req, String imagePath, String outputText) {

		OCRRecogisation response = new OCRRecogisation();
		

		String[] command = { "cmd", };

		Process p;
		try {

			p = Runtime.getRuntime().exec(command);
			new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
			new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
			PrintWriter stdin = new PrintWriter(p.getOutputStream());

			if (isPDF(req.getFilePath())) {

				stdin.println("\"" + magickPath + "\" mogrify -density 600 -units PixelsPerInch \"" + imagePath + "\"");
				// Extracting Text In an Image
				stdin.println("\"" + tesseractPath + "\" \"" + imagePath + "\" \"" + outputText + "\"");

			} else {

				String blackAndWhite = imagePath+".jpeg";
				
				imagePath = req.getFilePath();
				
				
				
				// Sharpen the Edges Of image
				stdin.println("\"" + magickPath + "\" convert \"" + imagePath
						+ "\" -resize 2000x1200 -sharpen 0x2 -contrast-stretch 0x10% -colorspace Gray -normalize  \""
						+ blackAndWhite + "\"");

//				//Deskewing
				stdin.println("\"" + magickPath + "\" convert \"" + blackAndWhite + "\" -deskew 50% \"" + blackAndWhite
						+ "\"");
//				//Improve the DPI
				stdin.println(
						"\"" + magickPath + "\" mogrify -density 600 -units PixelsPerInch \"" + blackAndWhite + "\"");
				// Extracting Text In an Image
				stdin.println("\"" + tesseractPath + "\" \"" + blackAndWhite + "\" \"" + outputText
						+ "\" -l eng+ara  --oem 3 --psm 3 \"");

			}
			stdin.close();
			p.waitFor();
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();

			response = recognisation(outputText + ".txt", req.getValue());

			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public boolean isPDF(String filePath) {
		// Get the file extension
		String extension = getFileExtension(filePath);

		// Check if the extension is "pdf" (case-insensitive)
		return extension != null && extension.equalsIgnoreCase("pdf");
	}

	public boolean isValidFileExtension(String filePath) {
		
		String fileExtension = getFileExtension(filePath);
		
		String[] validfileExtensions = {"jpeg","jpg","png","pdf"};
		
		for(String validExtensions : validfileExtensions) {
			if(fileExtension.equalsIgnoreCase(validExtensions))
				return true;
		}
		return false;
	}
	public OCRRecogisation convertPdfToImages(DocumentUploadOCRReq req, String outputFolder) throws IOException {
		
		OCRRecogisation res = new OCRRecogisation();
		
		String filePath = req.getFilePath();
		
		try (PDDocument document = PDDocument.load(new File(filePath))) {
			PDFRenderer pdfRenderer = new PDFRenderer(document);

			// Create a folder for the images

			String folderPath = ocrFolder+"\\"+System.currentTimeMillis();
			File folder = new File(folderPath);

			if (!folder.exists()) {
				folder.mkdirs();
			}

			for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
				BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 600, ImageType.RGB);

				// Save the image to a file inside the folder
				String imagePath = folderPath + "/page_" + (pageIndex + 1) + ".png";
				File outputFile = new File(imagePath);

				ImageIO.write(image, "png", outputFile);

				System.out.println("File Path---------------------------->" + outputFile.getAbsolutePath());

				// Assuming this method is used to process each image
				res = commandLineArgsForTesseract(req, imagePath, folderPath + "/text_" + (pageIndex + 1));
			}

			return res;
		}
	}

	public  String getFileExtension(String filePath) {
		
		Path path = FileSystems.getDefault().getPath(filePath);
		String fileName = path.getFileName().toString();
		
		int dotIndex = fileName.lastIndexOf('.');
		
		if (dotIndex > 0 && dotIndex < filePath.length() - 1) {
			return fileName.substring(dotIndex + 1);
		}
		return null; // No extension found
	}

	public  OCRRecogisation recognisation(String filePath, String expectedString) {

		OCRRecogisation recognisation = new OCRRecogisation();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line, text = "";
			Double result = 0d;			
			// Read each line from the file until the end of the file is reached

			Map<Double, String> similarWordPercentMap = new LinkedHashMap<>();
			
			Double detper = 0d;
			while ((line = reader.readLine()) != null) {
				text += line;
				// Perform your operation on each line
				String trimmedLine = line.trim();

				String[] words = trimmedLine.split(" ");

				// Print the words for demonstration purposes
				for (String word : words) {

					// Remove Invisible Characters
					word = word.replaceAll("\\p{C}", "");

					@SuppressWarnings("deprecation")
					Double detectperc2d = (double) StringUtils.getLevenshteinDistance(expectedString, word.trim());

					detper = 100 - ((detectperc2d / expectedString.length()) * 100);


					if (result < detper) {
					    // Use Double.compare for robust comparisons
					    result = detper;
					    similarWordPercentMap.put(result, word);
					}

				}
				// Add your logic here to process the content of each line
				// For example, you can perform text analysis, manipulation, etc.

			}

			 Optional<Double> map = similarWordPercentMap.keySet().stream().max(Double::compare);
			 
			 if(map.isPresent()) {
					recognisation.setPercentage(result);
					recognisation.setValue(similarWordPercentMap.get(result));
					recognisation.setId(expectedString);
				} else {
					recognisation.setPercentage(result);
					recognisation.setValue(similarWordPercentMap.get(result));
					recognisation.setId(expectedString);
				}

			System.out.println("MAP   ----------------------> " + similarWordPercentMap);
			
			
			System.out.println("Recognisation Parameters ------------> " + recognisation);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return recognisation;
	}

	@Override
	public List<Error> ocrFileValidation(DocumentUploadOCRReq req) {

		List<Error> errorList = new ArrayList<>();
		
		String filePath = req.getFilePath();
		
		if(!isValidFileExtension(filePath)) {
			
			errorList.add(new Error("01", "File", "."+getFileExtension(filePath)+" is Invalid .please Upload jpeg,jpg,png,pdf File Formats"));
			
		}
		return errorList;
	}

	@Override
	public SuccessRes updateVerifiedYn(UpdateVerifiedYnReq req) {
		SuccessRes res = new SuccessRes();
		try {
			DocumentUniqueDetails save = new DocumentUniqueDetails();
			List<DocumentUniqueDetails> docUniqueList = docUniqueRepo.findByUniqueIdAndIdAndDocumentId(Integer.valueOf(req.getUniqueId()), req.getId(), Integer.valueOf(req.getDocumentId()));		
			if(docUniqueList.size()>0) {
				save = docUniqueList.get(0);
				save.setVerifiedYn(req.getVerifiedYN());
				docUniqueRepo.saveAndFlush(save);				
			}			
			
			res.setResponse("VerifiedYN Updated Successfully");	
			res.setSuccessId(req.getUniqueId() + ": " + req.getVerifiedYN());	
	
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
		}
		return res;
	}

}
