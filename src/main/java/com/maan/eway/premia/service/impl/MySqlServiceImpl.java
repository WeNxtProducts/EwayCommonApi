package com.maan.eway.premia.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.CreditLimitDetail;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotCommDiscountDetail;
import com.maan.eway.bean.MotDriverDetail;
import com.maan.eway.bean.PgithPolRiskAddlInfo;
import com.maan.eway.bean.PremiaConfigDataMaster;
import com.maan.eway.bean.PremiaConfigMaster;
import com.maan.eway.bean.YiChargeDetail;
import com.maan.eway.bean.YiCoverDetail;
import com.maan.eway.bean.YiPolicyApproval;
import com.maan.eway.bean.YiPolicyDetail;
import com.maan.eway.bean.YiPremCal;
import com.maan.eway.bean.YiSectionDetail;
import com.maan.eway.bean.YiVatDetail;
import com.maan.eway.integration.service.impl.MySqlQuery;
import com.maan.eway.premia.service.MySqlService;
import com.maan.eway.repository.CreditLimitDetailRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotDriverDetailRepository;
import com.maan.eway.repository.MotcommDiscountDetailRepository;
import com.maan.eway.repository.PgitPolRiskAddlInfoRepository;
import com.maan.eway.repository.YiChargeDetailRepository;
import com.maan.eway.repository.YiCoverDetailRepository;
import com.maan.eway.repository.YiPolicyApprovalRepository;
import com.maan.eway.repository.YiPolicyDetailRepository;
import com.maan.eway.repository.YiPremCalRepository;
import com.maan.eway.repository.YiSectionDetailRepository;
import com.maan.eway.repository.YiVatDetailRepository;

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
public class MySqlServiceImpl implements MySqlService {

	private Logger log = LogManager.getLogger(MySqlServiceImpl.class);

	@Autowired
	private HomePositionMasterRepository homeRepo;

	@Autowired
	private YiCoverDetailRepository yiCoverDetailRepo;

	@Autowired
	private PgitPolRiskAddlInfoRepository pgitPolRiskRepo;

	@Autowired
	private YiChargeDetailRepository yiChargeDetailRepo;

	@Autowired
	private MotDriverDetailRepository motDrivDetailsRepo;

	@Autowired
	private MotcommDiscountDetailRepository motComRepo;

	@Autowired
	private YiPolicyDetailRepository yiPolicyReo;

	@Autowired
	private CreditLimitDetailRepository creditRepo;

	@Autowired
	private YiPolicyApprovalRepository yipolicyRepo;

	@Autowired
	private YiPremCalRepository yipremRepo;

	@Autowired
	private YiSectionDetailRepository yisecRepo;

	@Autowired
	private YiVatDetailRepository yivatRepo;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private MySqlQuery mysql;

	public boolean pushMySql(List<PremiaConfigMaster> configMasterList, List<String> params, String quoteNo) {
		try {
			configMasterList.stream().forEach(masterdata -> {
				List<PremiaConfigDataMaster> configData = getPremiaConfigData(masterdata.getCompanyId(),
						masterdata.getProductId(), masterdata.getPremiaId());
				List<Map<String, Object>> listFromQuery = new ArrayList<Map<String, Object>>();

				if (StringUtils.isNotBlank(masterdata.getQueryKey())) {
					String query = mysql.getQuery(masterdata.getQueryKey());
					System.out.println("Premia Id :" + masterdata.getPremiaId());
					System.out.println("Premia Table Name :" + masterdata.getPremiaTableName());
					System.out.println("**********************************************************");
					System.out.println("QueryKey : " + masterdata.getQueryKey());
					System.out.println("MySql Main Quey : " + query);
					System.out.println("**********************************************************");
					List<String> asList = fromQuerytoList(query);

					Map<String, String> maps = fromListToMaps(asList);

					Map<String, String> avoidd = new HashMap<String, String>();

					if (configData != null && !configData.isEmpty()) {
						for (PremiaConfigDataMaster data : configData) {

							if (!"Y".equals(data.getDefaultYn())) {
								Map<String, String> filterdmap = maps.entrySet().stream()
										.filter(m -> data.getInputColumn().equalsIgnoreCase(m.getKey()))
										.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
								String queryvalue = filterdmap.get(data.getInputColumn());

								if ("Date".equals(data.getDataTypeDesc())
										&& !avoidd.containsKey(data.getInputColumn())) {

									maps.put(data.getInputColumn(), queryvalue);
								}
								if ("N".equals(data.getDefaultYn()) && "Y".equals(data.getCaseConditionYn())) {
									maps.put(data.getInputColumn(), data.getCaseCondition());
								} else
									maps.put(data.getInputColumn(), queryvalue);

							} else if ("N".equals(data.getDefaultYn()) && "Y".equals(data.getCaseConditionYn())) {
								maps.put(data.getInputColumn(), data.getCaseCondition());
							}
						}
					}

					String framedselecquery = frameselectfromMap(maps);
					log.info("MySql framedselecquery :: " + framedselecquery);
					query = "SELECT " + framedselecquery + " "
							+ query.substring(query.indexOf(" FROM"), query.length());

					log.info("MySql framedselecquery with Select :: " + query);

					System.out.println("**********************************************************");
					System.out.println("MySql Select Qury with case condition");
					listFromQuery = mysql.getListFromQueryWithoutKey(query, params);
					System.out.println("**********************************************************");
				}
				delete1(quoteNo, masterdata.getPremiaTableName());

				for (Map<String, Object> qdata : listFromQuery) {
					System.out.println("MySql Framing Insert Query");
					if (configData != null && !configData.isEmpty() && qdata != null) {
						Map<String, String> jmap = new HashMap<String, String>();
						List<String> colums = new ArrayList<String>();
						List<String> values = new ArrayList<String>();

						for (PremiaConfigDataMaster data : configData) {

							String value = "";
							if ("Y".equals(data.getDefaultYn())) {
								value = StringUtils.isBlank(data.getDefaultValue()) ? "" : data.getDefaultValue();

								if ("Date".equals(data.getDataTypeDesc())) {

									if (value.equalsIgnoreCase("SYSDATE")) {
										SimpleDateFormat dbF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
										value = dbF.format(new Date());
									} else {
										value = "'" + value + "'";

									}

								}
								value = (("String".equals(data.getDataTypeDesc())
										|| "Date".equals(data.getDataTypeDesc())) ? "'" + value + "'" : value);

							} else {
								Object aliazval = qdata.get(data.getInputColumn()) == null ? ""
										: qdata.get(data.getInputColumn());

								value = String.valueOf(aliazval);
								value = (("String".equals(data.getDataTypeDesc())
										|| "Date".equals(data.getDataTypeDesc())) ? value.replace("'", "") : value);
								if ("Date".equals(data.getDataTypeDesc())) {
									String dateformatt = StringUtils.isNotEmpty(data.getDataFormatType())
											? data.getDataFormatType().toUpperCase()
											: "yyyy-MM-dd hh:mm:ss";
									if (dateformatt != null)
										value = "'" + value + "'";

								}

								value = (("Number".equals(data.getDataTypeDesc()))
										? "'" + String.valueOf(aliazval) + "'"
										: value);
								value = (("String".equals(data.getDataTypeDesc())) ? "'" + String.valueOf(value) + "'"
										: value);

							}

							jmap.put(data.getColumnName(), value);
							colums.add(data.getColumnName());
							values.add(value);
						}

						if (!jmap.isEmpty()) {
							insertMySql(masterdata, colums, values);
						}
					}
				}

			});
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}

	private void insertMySql(PremiaConfigMaster masterdata, List<String> colums, List<String> values) {
		System.out.println("***************************************************************");
		System.out.println("MySql " + masterdata.getPremiaTableName() + " Table Inset Query");
		String resultColumn = colums.stream().map(String::toLowerCase).collect(Collectors.joining(","));
		String insertQuery = "INSERT INTO " + masterdata.getPremiaTableName().toLowerCase() + " (" + resultColumn
				+ ") VALUES (" + StringUtils.join(values, ",") + ")";
		log.info("Insert Query::" + insertQuery);
		boolean insert = mysql.insert(insertQuery);
		if (insert) {
			System.out.println(masterdata.getPremiaTableName() + " Success");
		} else {
			System.out.println(masterdata.getPremiaTableName() + " Failure");
		}
		System.out.println("****************************************************************");
	}

	public synchronized List<PremiaConfigDataMaster> getPremiaConfigData(String insuraceId, String productId,
			Integer premiaId) {
		List<PremiaConfigDataMaster> list = new ArrayList<PremiaConfigDataMaster>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PremiaConfigDataMaster> query = cb.createQuery(PremiaConfigDataMaster.class);
			// Find All
			Root<PremiaConfigDataMaster> c = query.from(PremiaConfigDataMaster.class);

			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("premiaId")));

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<PremiaConfigDataMaster> ocpm1 = amendId.from(PremiaConfigDataMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("premiaId"), c.get("premiaId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), c.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), c.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("columnId"), c.get("columnId"));

			amendId.where(a1, a2, a3, a4);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("amendId"), amendId);
			Predicate n3 = cb.equal(c.get("companyId"), insuraceId);
			Predicate n4 = cb.equal(c.get("productId"), productId);
			Predicate n5 = cb.equal(c.get("productId"), "99999");
			Predicate n6 = cb.or(n4, n5);
			Predicate n7 = cb.equal(c.get("premiaId"), premiaId);
			query.where(n1, n2, n3, n6, n7).orderBy(orderList);
			// Get Result
			TypedQuery<PremiaConfigDataMaster> result = em.createQuery(query);
			list = result.getResultList();

			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getPremiaId(), o.getColumnId())))
					.collect(Collectors.toList());

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}

	private static <T> java.util.function.Predicate<T> distinctByKey(
			java.util.function.Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	private String frameselectfromMap(Map<String, String> maps) {
		String result = maps.entrySet().stream().map(map -> (map.getValue() + " " + map.getKey()))
				.collect(Collectors.joining(","));

		return result;
	}

	private List<String> fromQuerytoList(String selectquery) {
		if (selectquery.indexOf(",") != -1) {
			selectquery = selectquery.substring(selectquery.indexOf("SELECT") + 6, selectquery.indexOf(" FROM"));
			List<String> arrays = new ArrayList<String>();
			String[] col_aliz = selectquery.split(","); // column names
			for (int i = 0; i < col_aliz.length; i++) {
				arrays.add(col_aliz[i]);
			}
			return arrays;
		}
		return null;
	}

	private Map<String, String> fromListToMaps(List<String> arrays) {
		Map<String, String> listmaps = new HashMap<String, String>();
		for (String val : arrays) {
			if (val.trim().indexOf(" ") != 1) {
				val = val.trim();
				String[] split = val.split(" ");
				listmaps.put(split[split.length - 1], split[0]);
			}
		}
		return listmaps;
	}

	public void delete1(String quoteNo, String tableName) {
		try {
			String reqRefNo = "";
			HomePositionMaster home = homeRepo.findByQuoteNo(quoteNo);
			if (home != null) {
				reqRefNo = home.getRequestReferenceNo();
			}
			if ("Yi_Policy_Detail".equalsIgnoreCase(tableName)) {
				List<YiPolicyDetail> list = yiPolicyReo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					yiPolicyReo.deleteAll(list);
					System.out.println("Yi_Policy_Detail is deleted successfully");
				}
			} else if ("Yi_Section_Detail".equalsIgnoreCase(tableName)) {
				List<YiSectionDetail> list = yisecRepo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					yisecRepo.deleteAll(list);
					System.out.println("Yi_Section_Detail is deleted successfully");
				}
			} else if ("PGIT_POL_RISK_ADDL_INFO_01".equalsIgnoreCase(tableName)) {
				List<PgithPolRiskAddlInfo> list = pgitPolRiskRepo.findByRequestReferenceNo(reqRefNo);
				if (list.size() > 0 && list != null) {
					pgitPolRiskRepo.deleteAll(list);
					System.out.println("PGIT_POL_RISK_ADDL_INFO_01 is deleted successfully");
				}
			} else if ("Mot_Driver_Detail".equalsIgnoreCase(tableName)) {
				List<MotDriverDetail> list = motDrivDetailsRepo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					motDrivDetailsRepo.deleteAll(list);
					System.out.println("Mot_Driver_Detail is deleted successfully");
				}
			} else if ("Yi_Cover_Detail".equalsIgnoreCase(tableName)) {
				List<YiCoverDetail> list = yiCoverDetailRepo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					yiCoverDetailRepo.deleteAll(list);
					System.out.println("Yi_Cover_Detail is deleted successfully");
				}
			} else if ("Mot_Comm_Discount_Detail".equalsIgnoreCase(tableName)) {
				List<MotCommDiscountDetail> list = motComRepo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					motComRepo.deleteAll(list);
					System.out.println("Mot_Comm_Discount_Detail is deleted successfully");
				}
			} else if ("Yi_Charge_Detail".equalsIgnoreCase(tableName)) {
				List<YiChargeDetail> list = yiChargeDetailRepo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					yiChargeDetailRepo.deleteAll(list);
					System.out.println("Yi_Charge_Detail is deleted successfully");
				}
			} else if ("Yi_Vat_Detail".equalsIgnoreCase(tableName)) {
				List<YiVatDetail> list = yivatRepo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					yivatRepo.deleteAll(list);
					System.out.println("Yi_Vat_Detail is deleted successfully");
				}
			} else if ("Yi_Prem_Cal".equalsIgnoreCase(tableName)) {
				List<YiPremCal> list = yipremRepo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					yipremRepo.deleteAll(list);
					System.out.println("Yi_Prem_Cal is deleted successfully");
				}
			} else if ("Yi_Policy_Approval".equalsIgnoreCase(tableName)) {
				List<YiPolicyApproval> list = yipolicyRepo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					yipolicyRepo.deleteAll(list);
					System.out.println("Yi_Policy_Approval is deleted successfully");
				}
			} else if ("Credit_Limit_Detail".equalsIgnoreCase(tableName)) {
				List<CreditLimitDetail> list = creditRepo.findByRequestreferenceno(reqRefNo);
				if (list.size() > 0 && list != null) {
					creditRepo.deleteAll(list);
					System.out.println("Credit_Limit_Detail is deleted successfully");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
