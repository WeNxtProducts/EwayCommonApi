package com.maan.eway.notif.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.NotifTemplateMaster;

import com.maan.eway.error.Error;
import com.maan.eway.notif.req.NotifTemplateMasterGetReq;


import com.maan.eway.notif.req.NotifTemplateMasterReq;

import com.maan.eway.notif.res.NotifTemplateMasterRes;
import com.maan.eway.notif.service.NotifTemplateMasterService;

import com.maan.eway.repository.NotifTemplateMasterRepository;

import com.maan.eway.res.SuccessRes;

import net.bytebuddy.implementation.bytecode.ByteCodeAppender.Size;

/**
 * <h2>BankMasterServiceimpl</h2>
 */
@Service
@Transactional
public class NotifTemplateMasterServiceImpl implements NotifTemplateMasterService {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private NotifTemplateMasterRepository notifRepo;

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(NotifTemplateMasterServiceImpl.class);

	@Override
	public List<Error> validatenotiftemplatemaster(NotifTemplateMasterReq req) {
		List<Error> errorList = new ArrayList<Error>();

		try {
			if (StringUtils.isBlank(req.getInsId().toString())) {
				errorList.add(new Error("01", "Company Id", "Please Enter Company Id"));
			}else if (req.getInsId().length() > 20) {
				errorList.add(new Error("01", "Company Id", "Please Enter Company Id within 20 Characters"));
			}
		
			// Date Validation
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null) {
				errorList.add(new Error("02", "EffectiveDateStart", "Please Enter Effective Date Start "));

			} else if (req.getEffectiveDateStart().before(today)) {
				errorList.add(new Error("02", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
			} else if (req.getEffectiveDateEnd() == null) {
				errorList.add(new Error("03", "EffectiveDateEnd", "Please Enter Effective Date End "));

			} else if (req.getEffectiveDateEnd().before(req.getEffectiveDateStart())|| req.getEffectiveDateEnd().equals(req.getEffectiveDateStart())) {
				errorList.add(new Error("03", "EffectiveDateEnd","Please Enter Effective Date End  is After Effective Date Start"));
			}

			// SMS
			if ( StringUtils.isBlank(req.getSmsRequired())) {
				errorList.add(new Error("04", "SmsRequired", "Please Enter SmsRequired"));
			} else if (req.getSmsRequired().equals("Y")) {

				if ( StringUtils.isBlank(req.getSmsSubject())) {
					errorList.add(new Error("04", "SmsSubject", "Please Enter SmsSubject"));
				}else if (req.getSmsSubject().length() > 500) {
					errorList.add(new Error("04", "SmsSubject", "Please Enter SmsSubject within 500 Characters"));
				}
				if ( StringUtils.isBlank(req.getSmsBodyEn())) {
					errorList.add(new Error("04", "SmsBody", "Please Enter SmsBody"));
				}else if (req.getSmsBodyEn().length() > 2000) {
					errorList.add(new Error("04", "SmsBody", "Please Enter SmsBody within 2000 Characters"));
				}
				if (StringUtils.isBlank(req.getSmsRegards())) {
					errorList.add(new Error("04", "SmsRegards", "Please Enter SmsRegards"));
				}else if (req.getSmsRegards().length() > 500) {
					errorList.add(new Error("04", "SmsRegards", "Please Enter SmsRegards within 500 Characters"));
				}
			}

			//WhatsApp
			if (StringUtils.isBlank(req.getWhatsappRequired())) {
				errorList.add(new Error("05", "WhatsappRequired", "Please Enter WhatsappRequired"));
			}else if(req.getWhatsappRequired().equals("Y")) {
				
				if ( StringUtils.isBlank(req.getWhatsappSubject())) {
					errorList.add(new Error("05", "WhatsappSubject", "Please Enter WhatsappSubject"));
				}else if (req.getWhatsappSubject().length() > 500) {
					errorList.add(new Error("05", "WhatsappSubject", "Please Enter WhatsappSubject within 500 Characters"));
				}
				if ( StringUtils.isBlank(req.getWhatsappBodyEn())) {
					errorList.add(new Error("05", "WhatsappBodyEn", "Please Enter WhatsappBodyEn"));
				}else if (req.getWhatsappBodyEn().length() > 500) {
					errorList.add(new Error("05", "WhatsappSubject", "Please Enter WhatsappSubject within 500 Characters"));
				}
				if (StringUtils.isBlank(req.getWhatsappRegards())) {
					errorList.add(new Error("05", "WhatsappRegards", "Please Enter WhatsappRegards"));
				}else if (req.getWhatsappRegards().length() > 500) {
					errorList.add(new Error("05", "WhatsappRegards", "Please Enter WhatsappRegards within 500 Characters"));
				}
			}
			
			// Mail
			if (StringUtils.isBlank(req.getMailRequired())) {
				errorList.add(new Error("06", "MailRequired", "Please Enter MailRequired"));
			} else if (req.getMailRequired().equals("Y")) {

				if (StringUtils.isBlank(req.getMailSubject())) {
					errorList.add(new Error("06", "MailSubject", "Please Enter MailSubject"));
				}else if (req.getMailSubject().length() > 500) {
					errorList.add(new Error("06", "MailSubject", "Please Enter MailSubject within 500 Characters"));
				}
				if ( StringUtils.isBlank(req.getMailBody())) {
					errorList.add(new Error("06", "MailBody", "Please Enter MailBody"));
				}else if (req.getMailBody().length() > 2000) {
					errorList.add(new Error("06", "MailSubject", "Please Enter MailSubject within 2000  Characters"));
				}
				if ( StringUtils.isBlank(req.getMailRegards())) {
					errorList.add(new Error("06", "MailRegards", "Please Enter MailRegards"));
				}else if (req.getMailRegards().length() > 500) {
					errorList.add(new Error("06", "MailRegards", "Please Enter MailRegards within 500 Characters"));
				}
			}
			
			// Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
				errorList.add(new Error("07", "Status", "Please Enter Status"));
			} else if (req.getStatus().length() > 1) {
				errorList.add(new Error("07", "Status", "Status 1 Character Only"));
			} else if (!("Y".equals(req.getStatus()) || "N".equals(req.getStatus()))) {
				errorList.add(new Error("07", "Status", "Enter Status Y or N Only"));
			}
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add(new Error("08", "CreatedBy", "Please Enter CreatedBy"));
			} else if (req.getCreatedBy().length() > 100) {
				errorList.add(new Error("08", "CreatedBy", "Please Enter CreatedBy within 100 Characters"));
			}
			if (StringUtils.isBlank(req.getRemarks())) {
				errorList.add(new Error("09", "Remarks", "Please Enter Remarks"));
			} else if (req.getRemarks().length() > 300) {
				errorList.add(new Error("09", "Remarks", "Please Enter Remarks within 100 Characters"));
			}
			if (StringUtils.isBlank(req.getCoreAppCode())) {
				errorList.add(new Error("10", "Core App Code", "Please Enter Core App Code"));
			} else if (req.getCoreAppCode().length() > 20) {
				errorList.add(new Error("10", "Core App Code", "Please Enter CreatedBy within 20 Characters"));
			}
			if (StringUtils.isBlank(req.getRegulatoryCode())) {
				errorList.add(new Error("11", "RegulatoryCode", "Please Enter RegulatoryCode"));
			} else if (req.getRegulatoryCode().length() > 20) {
				errorList.add(new Error("11", "RegulatoryCode", "Please Enter RegulatoryCode within 20 Characters"));
			}
			if (StringUtils.isBlank(req.getQueryKey())) {
				errorList.add(new Error("12", "QueryKey", "Please Enter RegulatoryCode"));
			} else if (req.getQueryKey().length() > 100) {
				errorList.add(new Error("12", "QueryKey", "Please Enter RegulatoryCode within 100 Characters"));
			}
		
	} catch (Exception e) {
		e.printStackTrace();
		errorList.add(new Error("00", "CommonError", "CommonError"));
	}
	return errorList;
}

	@Override
	public SuccessRes insertnotiftemplatemaster(NotifTemplateMasterReq req) {
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		NotifTemplateMaster saveData = new NotifTemplateMaster();
		List<NotifTemplateMaster> list = new ArrayList<NotifTemplateMaster>();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {
			Integer amendId = 0;
			Calendar cal = new GregorianCalendar();
			cal.setTime(req.getEffectiveDateStart());cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
			
			Date startDate = cal.getTime();
			Date today = new Date();
			
			cal.setTime(req.getEffectiveDateStart());cal.set(Calendar.HOUR_OF_DAY, today.getHours());cal.set(Calendar.MINUTE, today.getMinutes());
			
			Date oldEndDate = cal.getTime();
			cal.setTime(req.getEffectiveDateStart());cal.set(Calendar.HOUR_OF_DAY, today.getHours());cal.set(Calendar.MINUTE, today.getMinutes());
			Date effDate = cal.getTime();
			Date endDate = req.getEffectiveDateEnd();

			String companyId = "";

			List<NotifTemplateMaster> checkCompanyId=notifRepo.findByInsIdOrderByEntryDateDesc(req.getInsId());
			if (checkCompanyId.size()==0) {
				// Save
				
				companyId =req.getInsId();
				res.setResponse("Saved Successfully");
				res.setSuccessId(companyId);
			} else {
				// Update
				companyId =req.getInsId();
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<NotifTemplateMaster> query = cb.createQuery(NotifTemplateMaster.class);
				// Find all
				Root<NotifTemplateMaster> b = query.from(NotifTemplateMaster.class);
				// Select
				query.select(b);
				// Effective Date Max Filter
				Subquery<Long> effectiveDate = query.subquery(Long.class);
				Root<NotifTemplateMaster> ocpm1 = effectiveDate.from(NotifTemplateMaster.class);
				effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
				Predicate a1 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), startDate);
				effectiveDate.where(a1);

				// Where
				Predicate n1 = cb.equal(b.get("status"), "Y");
				Predicate n2 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
				Predicate n3 = cb.equal(b.get("insId"), req.getInsId());

				query.where(n1, n2, n3);
				// Get Result
				TypedQuery<NotifTemplateMaster> result = em.createQuery(query);
				list = result.getResultList();
				if (list.size() > 0) {
					notifRepo.delete(list.get(0));
					// Amend Id
					if (list.get(0).getEffectiveDateStart().before(startDate)) {
						String startDatewithoutTime = sdformat.format(startDate);
						String oldDateWithoutTime = sdformat.format(list.get(0).getEffectiveDateStart());
						if (startDatewithoutTime.equalsIgnoreCase(oldDateWithoutTime)) {
							amendId = list.get(0).getAmendId() + 1;
						}
					}
				}
				res.setResponse("Updated Successfully");
				res.setSuccessId(companyId);
			}
			dozermapper.map(req, saveData);
			saveData.setInsId(companyId);
			saveData.setEffectiveDateStart(effDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setEntryDate(new Date());
			saveData.setAmendId(amendId);
			notifRepo.saveAndFlush(saveData);

			if (list.size() > 0) {
				// Update Old Record
				NotifTemplateMaster lastRecord = list.get(0);
				lastRecord.setEffectiveDateEnd(oldEndDate);
				String startDatewithoutTime = sdformat.format(startDate);
				String oldDatewithoutTime = sdformat.format(list.get(0).getEffectiveDateStart());

				if (startDatewithoutTime.equalsIgnoreCase(oldDatewithoutTime)) {
					lastRecord.setStatus("N");	
				}
				notifRepo.saveAndFlush(lastRecord);
			}
			log.info("Saved Details is --> " + json.toJson(saveData));
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is -->" + e.getMessage());
			return null;
		}
		return res;
	}


	@Override
	public NotifTemplateMasterRes getenotiftemplatemaster(NotifTemplateMasterGetReq req) {
		NotifTemplateMasterRes res = new NotifTemplateMasterRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<NotifTemplateMaster> list = new ArrayList<NotifTemplateMaster>();
			
			Date today  = req.getEffectiveDateStart()!=null ?req.getEffectiveDateStart() : new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			
			List<NotifTemplateMaster> notifList = notifRepo.findByInsIdOrderByEntryDateDesc( req.getCompanyId());
			if (notifList.size() > 0) {
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<NotifTemplateMaster> query = cb.createQuery(NotifTemplateMaster.class);
			
			// Find all
			Root<NotifTemplateMaster> b = query.from(NotifTemplateMaster.class);
			
			// Select
			query.select(b);
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<NotifTemplateMaster> ocpm1 = effectiveDate.from(NotifTemplateMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);

			effectiveDate.where(a1);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("sno")));
			
			Predicate n1 = cb.equal(b.get("insId"), req.getCompanyId());
			Predicate n2 = cb.equal(b.get("notificationApplicable"), req.getNotificationApplicable());

			query.where(n1, n2).orderBy(orderList);

			// Get Result
			TypedQuery<NotifTemplateMaster> result = em.createQuery(query);
			list = result.getResultList();

			// Map
			for (NotifTemplateMaster data : list) {
				res = mapper.map(data, NotifTemplateMasterRes.class);
			}
		}else {
			 notifList = notifRepo.findByInsIdAndNotificationApplicableOrderByEntryDateDesc( "Default_Template",req.getNotificationApplicable());
			// Map
			for (NotifTemplateMaster data : notifList) {
				res = mapper.map(data, NotifTemplateMasterRes.class);
			}
		}

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return res;
	}

}