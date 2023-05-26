package com.maan.eway.common.service.impl;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.bean.BrokerCommissionDetails;
import com.maan.eway.bean.CompanyCityMaster;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.CompanyRegionMaster;
import com.maan.eway.bean.CompanyStateMaster;
import com.maan.eway.bean.CountryMaster;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.common.req.NcdDetailsGetReq;
import com.maan.eway.common.service.DropDownService;
import com.maan.eway.integration.req.PremiaRequest;
import com.maan.eway.integration.req.QueryKeyReq;
import com.maan.eway.integration.service.impl.OracleQuery;
import com.maan.eway.master.req.BrokerSumInsuredRefReq;
import com.maan.eway.master.req.BuildingUsageDropDownReq;
import com.maan.eway.master.req.CityDropDownReq;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.master.req.LovPolicyDropDownReq;
import com.maan.eway.master.req.RegionDropDownReq;
import com.maan.eway.master.req.RelationDropDownReq;
import com.maan.eway.master.req.StateDropDownReq;
import com.maan.eway.master.service.impl.PolicyTypeMasterServiceImpl;
import com.maan.eway.repository.CompanyCityMasterRepository;
import com.maan.eway.repository.CompanyRegionMasterRepository;
import com.maan.eway.repository.CompanyStateMasterRepository;
import com.maan.eway.repository.CountryMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.res.ColummnDropRes;
import com.maan.eway.res.DropDownRes;

@Service
public class DropDownServiceImpl  implements DropDownService{ 
  

	private Logger log = LogManager.getLogger(DropDownServiceImpl.class);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ListItemValueRepository listRepo;
	
	@Autowired
	private CountryMasterRepository countryRepo;
	
	@Autowired
	private CompanyRegionMasterRepository regionrepo;
	
	@Autowired
	private CompanyStateMasterRepository staterepo;
	
	@Autowired
	private CompanyCityMasterRepository cityrepo;
	
	@Autowired
	private PolicyTypeMasterServiceImpl service;
	
	@Autowired
	private OracleQuery oracle;
	
	// Cover Note Type Drop Down

	@Override
	public List<DropDownRes> coverNoteType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "COVER_NOTE_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> paymentmode(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("PAYMENT_MODE", "Y");
			String itemType = "PAYMENT_MODE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> endorsementtype(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
	//		List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("ENDROSEMENT_TYPE", "Y");
			String itemType = "ENDROSEMENT_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> discounttypeoffered(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("DISCOUNT_TYPE_OFFERED", "Y");
			String itemType = "DISCOUNT_TYPE_OFFERED" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> taxexcempted(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("IS_TAX_EXEMPTED", "Y");
			String itemType = "IS_TAX_EXEMPTED" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> taxexcemptiontype(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("TAX_EXEMPTION_TYPE", "Y");
			String itemType = "TAX_EXEMPTION_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> policyholdertype(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("POLICY_HOLDER_TYPE", "Y");
			String itemType = "POLICY_HOLDER_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> policyholderidtype(LovPolicyDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
	//		List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("POLICY_HOLDER_ID_TYPE", "Y");
			String itemType = "POLICY_HOLDER_ID_TYPE" ;
			List<ListItemValue> getList  = getListItemPolicy(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> policyholdergender(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("POLICY_HOLDER_GENDER", "Y");
			String itemType = "POLICY_HOLDER_GENDER" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> nametitle(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
	//		List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("NAME_TITLE", "Y");
			String itemType = "NAME_TITLE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> notificationtype(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("NOTIFICATION_TYPE", "Y");
			String itemType = "NOTIFICATION_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> getMotorCategory(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("MOTOR_CATEGORY", "Y");
			String itemType = "MOTOR_CATEGORY" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getMotorType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
	//		List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("MOTOR_TYPE", "Y");
			String itemType = "MOTOR_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


//	@Override
//	public List<DropDownRes> getMotorUsage(LovDropDownReq req) {
//		List<DropDownRes> resList = new ArrayList<DropDownRes>();
//		try {
//		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("MOTOR_USAGE", "Y");
//			String itemType = "MOTOR_USAGE" ;
//			List<ListItemValue> getList  = getListItem(req , itemType);
//			for (ListItemValue data : getList) {
//				DropDownRes res = new DropDownRes();
//				res.setCode(data.getItemCode());
//				res.setCodeDesc(data.getItemValue());
//				res.setStatus(data.getStatus());
//				resList.add(res);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.info("Exception is ---> " + e.getMessage());
//			return null;
//		}
//		return resList;
//	}



	@Override
	public List<DropDownRes> ownerCategory(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("OWNER_CATEGORY", "Y");
			String itemType = "OWNER_CATEGORY" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}



	@Override
	public List<DropDownRes> fleetType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("FLEET_TYPE", "Y");
			String itemType = "FLEET_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}



	@Override
	public List<DropDownRes> reinsuranceCategory(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
	//		List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("REINSURANCE_CATEGORY", "Y");
			String itemType = "REINSURANCE_CATEGORY" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}



	@Override
	public List<DropDownRes> participantType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
	//		List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("PARTICIPANT_TYPE", "Y");
			String itemType = "PARTICIPANT_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}



	@Override
	public List<DropDownRes> reinsuranceForm(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("REINSURANCE_FORM", "Y");
			String itemType = "REINSURANCE_FORM" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}



	@Override
	public List<DropDownRes> reinsuranceType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("REINSURANCE_TYPE", "Y");
			String itemType = "REINSURANCE_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}



	@Override
	public List<DropDownRes> claimformdullyfilled(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeDesc("CLAIM_FORM_DULLY_FILLED", "Y");
			String itemType = "CLAIM_FORM_DULLY_FILLED" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}



	@Override
	public List<DropDownRes> lostassessmentoption(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("LOSS_ASSESSMENT_OPTION", "Y");
			String itemType = "LOSS_ASSESSMENT_OPTION" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}



	@Override
	public List<DropDownRes> assessoridtype(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("ASSESSOR_ID_TYPE", "Y");
			String itemType = "ASSESSOR_ID_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}



	@Override
	public List<DropDownRes> claimantCategory(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("CLAIMANT_CATEGORY", "Y");
			String itemType = "CLAIMANT_CATEGORY" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}



	@Override
	public List<DropDownRes> claimantType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("CLAIMANT_TYPE", "Y");
			String itemType = "CLAIMANT_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}



	@Override
	public List<DropDownRes> claimantIdType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("CLAIMANT_ID_TYPE", "Y");
			String itemType = "CLAIMANT_ID_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}



	@Override
	public List<DropDownRes> isreassessment(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeDesc("IS_REASSESSMENT", "Y");
			String itemType = "IS_REASSESSMENT" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}



	@Override
	public List<DropDownRes> offerAccepted(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeDesc("OFFER_ACCEPTED", "Y");
			String itemType = "OFFER_ACCEPTED" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}



	@Override
	public List<DropDownRes> partiesNotified(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeDesc("PARTIES_NOTIFIED", "Y");
			String itemType = "PARTIES_NOTIFIED" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}



	@Override
	public List<DropDownRes> claimResultedLitigation(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
	//		List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeDesc("CLAIM_RESULTED_LITIGATION", "Y");
			String itemType = "CLAIM_RESULTED_LITIGATION" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> tonnage(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("TONNAGE", "Y");
			String itemType = "TONNAGE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getNcdDetails(NcdDetailsGetReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Integer year = Calendar.getInstance().get(Calendar.YEAR);
			Integer manuyear = Integer.valueOf(req.getManufactureYear());
			Integer ncdyear = year-manuyear;
			//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("NCD", "Y");
				String itemType = "NCD" ;
				LovDropDownReq req2 = new LovDropDownReq();
				req2.setInsuranceId(req.getInsuranceId());
				req2.setBranchCode(req.getBranchCode());
				
				List<ListItemValue> getList  = getListItem(req2 , itemType);
				for(ListItemValue data : getList) {				
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
				if(ncdyear+2==Integer.valueOf(data.getItemCode())) {
				break;			
				}	
				}
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
			}
		return resList;
	}


	@Override
	public List<DropDownRes> insuranceType(LovDropDownReq req) {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {

			//List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("INSURANCE_TYPE", "Y");
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query=  cb.createQuery(Tuple.class);
			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			
			
			// Join Two Field in Same Alias
			Expression<String> productId = cb.concat(c.get("productId"), "~");
			Expression<String> clientName = cb.concat(c.get("clientName"), "~");
			Expression<String> joinProducIdAndClientName = cb.concat(productId, clientName);
			
			// Ref No Sub Query
			Subquery<String> refNo = query.subquery(String.class);
			Root<EserviceMotorDetails> ocpm1 = refNo.from(EserviceMotorDetails.class);
			refNo.select(ocpm1.get("requestReferenceNo"));
			Predicate a1 = cb.equal(c.get("customerReferenceNo"),ocpm1.get("customerReferenceNo"));
			refNo.where(a1);
			
			// Join Sub Query Name In Same Column
			Expression<String> referenceNo = cb.concat(refNo, "~");
			Expression<String> joinProductAmdClientAndRefNo = cb.concat(joinProducIdAndClientName, referenceNo);			
			
			query.multiselect( cb.concat(joinProductAmdClientAndRefNo , c.get("customerReferenceNo")  ).alias("MenuKey")  );
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));		
		
			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"),req.getBranchCode());
			
			query.where(n1).orderBy(orderList);
			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			List<Tuple> list = result.getResultList();
			
			
			String itemType = "INSURANCE_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
		
	

	}


	@Override
	public List<DropDownRes> insuranceClass(LovDropDownReq req) {
/*		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("INSURANCE_CLASS", "Y");

			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				resList.add(res);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	*/	
		return service.getPolicyTypeMasterDropdown();

	}


	@Override
	public List<DropDownRes> title(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("TITLE", "Y");
			String itemType = "TITLE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> borrowerType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("BORROWER_TYPE", "Y");
			String itemType = "BORROWER_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> cityLimit(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("CITY_LIMIT", "Y");
			String itemType = "CITY_LIMIT" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> getLanguage(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("LANGUAGE", "Y");
			String itemType = "LANGUAGE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> getFuelType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("FUEL_TYPE", "Y");
			String itemType = "FUEL_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


//	@Override
//	public List<ColummnDropRes> getTableDetails(LovDropDownReq req) {
//		List<ColummnDropRes> resList = new ArrayList<ColummnDropRes>();
//		try {
//			//String tableName = "Eservice_Customer_Details" ;
//			//List<String> removerUnderScore = new ArrayList<>(Arrays.asList(tableName.split("_")) ) ;
//			
//			/*Object entityName =null;
//			for (String ent : removerUnderScore) {
//				String lowerCase = ent.toLowerCase() ;
//				String firstLetterCaps =  lowerCase.substring(0, 1).toUpperCase() + lowerCase.substring(1) ;
//				entityName = entityName==null ? firstLetterCaps  :entityName +  firstLetterCaps ; 
//				
//			}*/
//			  
//		String entityName = "com.maan.eway.bean."+req.getTableName();//entityName + ".class" ;
//			 Class<?> forName = Class.forName(entityName);//forName(entityName);
//			
//
//		//	Class table = (Class) entityName ;
//			
//			Field[] members = forName.getDeclaredFields();
//			
//			        for(Field member:members){
//			        	if(! member.getName().equalsIgnoreCase("serialVersionUID") ) {
//			        		System.out.println(member.getName());
//			        		String output = member.getName().substring(0, 1).toUpperCase() + member.getName().substring(1);
//		        			String field =output.replaceAll("(.)([A-Z])", "$1_$2");
//		        			System.out.println(field);
//		        			String display =output.replaceAll("(.)([A-Z])", "$1 $2");
//			        			System.out.println(display);
//			        			ColummnDropRes res = new ColummnDropRes();
//			    				res.setColumnName(field);
//			    				res.setDispalyName(display);
//			    				res.setFieldName( member.getName());
//			    				resList.add(res);
//			        			    
//			        	//	customerReferenceNo
//			        	//	Customer Reference No
//			        	//	Customer_Reference_No
//			        	}
////			            System.out.println(member.getClass().getSimpleName());
////			            System.out.println(member.getClass().getCanonicalName());
////			            System.out.println(member.getClass().getTypeName());
////			            System.out.println(member.getClass().getComponentType());
////			            System.out.println(member.getClass().getModifiers());
////			            System.out.println(member.getClass().getAnnotations());
//			        }			
//			
//			
//		/*	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("FUEL_TYPE", "Y");
//
//			for (ListItemValue data : getList) {
//				DropDownRes res = new DropDownRes();
//				res.setCode(data.getItemCode());
//				res.setCodeDesc(data.getItemValue());
//				res.setStatus(data.getStatus());
//				resList.add(res);
//			} */
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.info("Exception is ---> " + e.getMessage());
//			return null;
//		}
//		return resList;
//	}


	@Override
	public List<DropDownRes> constructType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusAndCompanyIdOrderByItemCodeAsc("CONST_MATERIAL", "Y" , req.getInsuranceId());
			String itemType = "CONST_MATERIAL" ;
			  
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> consecutiveDays(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusAndCompanyIdOrderByItemCodeAsc("CONSECUTIVE_DAYS", "Y" , req.getInsuranceId());
			String itemType = "CONSECUTIVE_DAYS" ;  
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> buildingType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusAndCompanyIdOrderByItemCodeAsc("BUILDING_TYPE", "Y" , req.getInsuranceId());
			String itemType = "BUILDING_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}
	
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	public synchronized List<ListItemValue> getListItem(LovDropDownReq req , String itemType) {
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query=  cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);
			
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));
			
			
			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4);
						
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n4,n5);
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType);
			query.where(n1,n2,n3,n8,n9,n10).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getItemCode()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(ListItemValue :: getItemValue));
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list ;
	}

	public synchronized List<ListItemValue> getListItemPolicy(LovPolicyDropDownReq req , String itemType) {
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query=  cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);
			
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));
			
			
			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4);
						
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n4,n5);
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType);

			Predicate n11 = cb.equal(c.get("param1"),req.getParam1());
			query.where(n1,n2,n3,n8,n9,n10,n11).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getItemCode()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(ListItemValue :: getItemValue));
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list ;
	}


	@Override
	public List<DropDownRes> getPlanType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusAndCompanyIdOrderByItemCodeAsc("CONST_MATERIAL", "Y" , req.getInsuranceId());
			String itemType = "PLAN_TYPE" ;
			  
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getSourceType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "SOURCE_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getRelationType(RelationDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "RELATION_TYPE" ;
			LovDropDownReq req2 = new LovDropDownReq();
			req2.setInsuranceId(req.getInsuranceId());
			req2.setBranchCode(req.getBranchCode());			
			List<ListItemValue> getList  = getListItem(req2 , itemType);
			
			getList = getList.stream().filter( o -> o.getParam1()!=null && o.getParam1().equalsIgnoreCase(req.getGender()) ).collect(Collectors.toList());
					
					
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getBuildingPurpose(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "BUILDING_PURPOSE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getBuildingUsage(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "BUILDING_USAGE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getPaymentType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "PAYMENT_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getContent(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "Content" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getPersonal(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "PERSONAL" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getcontentrisk(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "CONTENT_RISK" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getallrisk(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "All Risk" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> datatype(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "DATA_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> termsandcondition(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "TERMS_AND_CONDITION" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> categoryid(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "OCCUPATION_CATEGORY_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> benefitcovermonth(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "BENEFIT_COVER_MONTH" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> quoteStatus(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "QUOTE_STATUS" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> termsType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "TERMS_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> driverType(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "DRIVER_TYPES" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getallelectronicItems(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "Electronic Items" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getQueryKeyColumns(QueryKeyReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String query = oracle.getQuery(req.getQueryKey());
			List<String> params  = new ArrayList<String>();
			params.add("");
			 
		//	List<Map<String, Object>>  listFromQuery = oracle.getListFromQueryWithoutKey(query, params);
			Query nativequery = em.createNativeQuery(query);		
		
			nativequery.unwrap(NativeQueryImpl.class).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			Map<String,Object> list = nativequery.getHints();
		//	String data2 = listFromQuery.getClass().getCanonicalName() ; 
//			List<String> list = map.values().stream().sorted().collect(Collectors.toList());
//			
//			 List<String> collect = listFromQuery.keySet() ;
//		                .stream()
//		                .filter(e -> Objects.isNull(hashMap.get(e)))
//		                .collect(Collectors.toList());
//		        System.out.println(collect);
			List<String> asList = fromQuerytoList(query);
			
			for (String data : asList) {
				DropDownRes res = new DropDownRes();
				String trimValue = data.trim() ;
				int spacePos = trimValue.indexOf(" ")  ;
				String value = trimValue.substring(spacePos + 1 , trimValue.length() );
				res.setCode(value);
				res.setCodeDesc(value);
				res.setStatus("Y");
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	private List<String> fromQuerytoList(String selectquery){
		if(selectquery.indexOf(",")!=-1) {
			selectquery=selectquery.substring(selectquery.indexOf("SELECT")+6, selectquery.indexOf(" FROM"));
			List<String> arrays=new ArrayList<String>();
			String[] col_aliz = selectquery.split(",");
			for(int i=0;i<col_aliz.length;i++) {
				arrays.add(col_aliz[i]);
			}
			return arrays;
		}
		return null;
	}


	@Override
	public List<DropDownRes> industryCategory(LovDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "INDUSTRY_CATEGORY" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> followupDetailsStatus(LovDropDownReq req) {
		// TODO Auto-generated method stub
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "FOLLOWUP_STATUS" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> endtDependantFields(LovDropDownReq req) {
		// TODO Auto-generated method stub
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "ENDT_DEPENDANT_FIELDS" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> productBenefitsTypes(LovDropDownReq req) {
		// TODO Auto-generated method stub
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "POLICY_BENEFITS_TYPES" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> brokerSumInsuredRefrral(BrokerSumInsuredRefReq req) {
		// TODO Auto-generated method stub
				List<DropDownRes> resList = new ArrayList<DropDownRes>();
				try {
					DropDownRes res = new DropDownRes();
				//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
					BrokerCommissionDetails loginProduct  = getBrokerProduct(req.getInsuranceId() ,req.getProductId() ,req.getLoginId(),req.getPolicyTypeId());
					
					if(loginProduct !=null ) {
						BigDecimal suminsured = StringUtils.isNotBlank(req.getSumInsured()) ? new BigDecimal(req.getSumInsured()) : BigDecimal.ZERO ;
						BigDecimal suminsuredStart = loginProduct.getSuminsuredStart();
						BigDecimal suminsuredEnd = loginProduct.getSuminsuredEnd();
						boolean referal = false ;
						String desc = "" ;
						
						// Comparision
						if (suminsured.compareTo(suminsuredStart) < 0 ) {
							referal = true ;
							desc  = "Sum Insured - " +  "Suminsured = " +  suminsured.toPlainString() + " Less Than Broker SumInsured Start"   ; 
							
						} else if (suminsuredEnd.compareTo(suminsured) < 0  ) {
							referal = true ;
							desc  = "Sum Insured - " +  "Suminsured = " +  suminsured.toPlainString() + " Greater Than Broker SumInsured End"   ; 
						}
						
						// Refral block
						if(referal == true  ) {
							res.setCode(suminsured.toString());
							res.setCodeDesc(desc);
							res.setStatus("R");
								
						} else {
							res.setCode(suminsured.toString());
							res.setCodeDesc("Between Broker SumInsured ");
							res.setStatus("Y");
						}
						
					} else {
						res.setCode("Not Available" );
						res.setCodeDesc("Not Available");
						res.setStatus("Y");
					}
					resList.add(res);
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
					return null;
				}
				return resList;
			}

	public synchronized BrokerCommissionDetails getBrokerProduct(String insuranceId , String productId , String loginId, String policyTypeId) {
		BrokerCommissionDetails loginProduct = new BrokerCommissionDetails(); 
		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd   = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);
			List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
			
			// Find All
			Root<BrokerCommissionDetails>    c = query.from(BrokerCommissionDetails.class);		
			
			// Select
			query.select(c );
			
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productId")));
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<BrokerCommissionDetails> ocpm1 = effectiveDate.from(BrokerCommissionDetails.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("productId"),ocpm1.get("productId") );
			Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId") );
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a4 = cb.equal(c.get("loginId"),ocpm1.get("loginId") );
			Predicate a9 = cb.equal(c.get("policyType"),ocpm1.get("policyType") );
			effectiveDate.where(a1,a2,a3,a4,a9);
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<BrokerCommissionDetails> ocpm2 = effectiveDate2.from(BrokerCommissionDetails.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a5 = cb.equal(c.get("productId"),ocpm2.get("productId") );
			Predicate a6 = cb.equal(c.get("companyId"),ocpm2.get("companyId") );
			Predicate a7 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a8 = cb.equal(c.get("loginId"),ocpm2.get("loginId") );
			Predicate a10 = cb.equal(c.get("policyType"),ocpm2.get("policyType") );
			effectiveDate2.where(a5,a6,a7,a8,a10);
			
			
		    // Where	
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n11 = cb.equal(c.get("status"),"R");
			Predicate n12 = cb.or(n1,n11);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			Predicate n5 = cb.equal(c.get("loginId"), loginId );
			Predicate n7 = cb.equal(c.get("productId"), productId );
			Predicate n8 = cb.equal(c.get("policyType"), policyTypeId );
			query.where(n12,n2,n3,n4,n5,n7,n8).orderBy(orderList);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);			
			list =  result.getResultList(); 
			loginProduct = list.size()>0 ? list.get(0) : null ; 
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return loginProduct ;
	}


	@Override
	public List<DropDownRes> getWallTypes(LovDropDownReq req) {
		// TODO Auto-generated method stub
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "WALL_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getRoofTypes(LovDropDownReq req) {
		// TODO Auto-generated method stub
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "ROOF_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getAuditentType(LovDropDownReq req) {
		// TODO Auto-generated method stub
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "AUDITENT_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			getList.sort(Comparator.comparing(ListItemValue :: getItemCode ) );
			
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getFidelityEmployeeCount(LovDropDownReq req) {
	List<DropDownRes> resList = new ArrayList<DropDownRes>();
	try {
	//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");

		String itemType = "FIDELITY_EMPLOYEE_COUNT" ;
		List<ListItemValue> getList  = getListItem(req , itemType);
		getList.sort(Comparator.comparing(ListItemValue :: getItemCode ) );
		for (ListItemValue data : getList) {
			DropDownRes res = new DropDownRes();
			res.setCode(data.getItemCode());
			res.setCodeDesc(data.getItemValue());
			res.setStatus(data.getStatus());
			resList.add(res);
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return resList;
}


	@Override
	public List<DropDownRes> getNatureOfTrade(LovDropDownReq req) {
		// TODO Auto-generated method stub
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");

			String itemType = "NATURE_OF_TRADE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);

			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}
	
	@Override
	public List<DropDownRes> getFidelitySuminsured(LovDropDownReq req) {
		// TODO Auto-generated method stub
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "FIDELITY_SUMINSURED" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			getList.sort(Comparator.comparing(ListItemValue :: getItemCode ) );
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getBurglaryInsuranceFor(LovDropDownReq req) {
		// TODO Auto-generated method stub
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "BURGLARY_INSURANCE_FOR" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getCeilingType(LovDropDownReq req) {
		// TODO Auto-generated method stub
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "CEILING_TYPE" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getWindowsMaterials(LovDropDownReq req) {
		// TODO Auto-generated method stub
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "WINDOWS_MATERIAL" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getDoorsMaterials(LovDropDownReq req) {
		// TODO Auto-generated method stub
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "DOORS_MATERIAL" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getNightLeftDoor(LovDropDownReq req) {
		// TODO Auto-generated method stub
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "NIGHT_LEFT_DOOR" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getBuildingOccupied(LovDropDownReq req) {
		// TODO Auto-generated method stub
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "BUILDING_OCCUPIED" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getOpenoption(LovDropDownReq req) {
		// TODO Auto-generated method stub
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "KEYS_REQUIRED_TO_OPEN" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<DropDownRes> getFirstLossPercent(LovDropDownReq req) {
		// TODO Auto-generated method stub
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
		//	List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");
			String itemType = "FIRST_LOSS_PERCENT" ;
			List<ListItemValue> getList  = getListItem(req , itemType);
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	
}
