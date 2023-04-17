package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.maan.eway.common.req.SearchReq;

import com.maan.eway.common.service.BuildingSearchService;

import com.maan.eway.notification.repository.CoverDocumentUploadDetailsRepository;
import com.maan.eway.repository.CoverMasterRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.SeqCustidRepository;
import com.maan.eway.repository.SeqCustrefnoRepository;
import com.maan.eway.repository.SeqQuotenoRepository;
import com.maan.eway.repository.SeqRefnoRepository;
import com.maan.eway.res.CopyQuoteSuccessRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;


@Service
@Transactional
public class BuildingSearchServiceImpl implements BuildingSearchService {
	@PersistenceContext
	private EntityManager em;

	private Logger log = LogManager.getLogger(MotorGridServiceImpl.class);

	@Autowired
	private HomePositionMasterRepository homePosistionRepo;
	
	@Autowired
	private PersonalInfoRepository personalInforepo;
	
	@Autowired
	private PolicyCoverDataRepository policyCoverDataRepo;
	
	@Autowired
	private MotorDataDetailsRepository motorDataDetepo;
	
	@Autowired
	private EServiceMotorDetailsRepository repo;
	
	@Autowired
	private EserviceCustomerDetailsRepository custRepo ;
	
	@Autowired
	private SeqQuotenoRepository quoteNoRepo ;
	
	@Autowired
	private SeqCustidRepository custIdRepo ;

	@Autowired
	private SeqRefnoRepository refNoRepo ;
	
	@Autowired
	private GenerateSeqNoServiceImpl seqNo ;
	
	@Autowired
	private SeqCustrefnoRepository custRefRepo  ;
	
	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;
	
	@Autowired
	private MotorDriverDetailsRepository motordrivDetepo;
	
	@Autowired
	private CoverDocumentUploadDetailsRepository coverDocUploadDetails;

	@Autowired
	private CoverMasterRepository coverMasterRepo;

	@Override
	public List<Tuple> searchBuilding(SearchReq req, List<String> branches) {
		// TODO Auto-generated method stub
		return null;
	}

		}
