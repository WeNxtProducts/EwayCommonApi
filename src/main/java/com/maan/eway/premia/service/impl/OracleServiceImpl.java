package com.maan.eway.premia.service.impl;

import java.text.ParseException;
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
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.PremiaConfigDataMaster;
import com.maan.eway.bean.PremiaConfigMaster;
import com.maan.eway.integration.service.impl.MySqlQuery;
import com.maan.eway.integration.service.impl.OracleQuery;
import com.maan.eway.oracle.CreditLimitDetailOra;
import com.maan.eway.oracle.MotCommDiscountDetailOra;
import com.maan.eway.oracle.MotDriverDetailOra;
import com.maan.eway.oracle.PgithPolRiskAddlInfoOra;
import com.maan.eway.oracle.YiChargeDetailOra;
import com.maan.eway.oracle.YiCoverDetailOra;
import com.maan.eway.oracle.YiPolicyApprovalOra;
import com.maan.eway.oracle.YiPolicyDetailOra;
import com.maan.eway.oracle.YiPremCalOra;
import com.maan.eway.oracle.YiSectionDetailOra;
import com.maan.eway.oracle.YiVatDetailOra;
import com.maan.eway.oracle.repo.CreditLimitDetailOraRepository;
import com.maan.eway.oracle.repo.MotDriverDetailOraRepository;
import com.maan.eway.oracle.repo.MotcommDiscountDetailOraRepository;
import com.maan.eway.oracle.repo.PgitPolRiskAddlInfoOraRepository;
import com.maan.eway.oracle.repo.YiChargeDetailOraRepository;
import com.maan.eway.oracle.repo.YiCoverDetailOraRepository;
import com.maan.eway.oracle.repo.YiPolicyApprovalOraRepository;
import com.maan.eway.oracle.repo.YiPolicyDetailOraRepository;
import com.maan.eway.oracle.repo.YiPremCalOraRepository;
import com.maan.eway.oracle.repo.YiSectionDetailOraRepository;
import com.maan.eway.oracle.repo.YiVatDetailOraRepository;
import com.maan.eway.premia.service.OracleService;
import com.maan.eway.repository.HomePositionMasterRepository;

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

public class OracleServiceImpl implements OracleService {

	private Logger log = LogManager.getLogger(OracleServiceImpl.class);

	@Autowired
	private YiPolicyDetailOraRepository yiPolicyDetailOraRepo;

	@Autowired
	private PgitPolRiskAddlInfoOraRepository pgitPolRiskAddlInfoOraRepo;

	@Autowired
	private YiCoverDetailOraRepository yiCoverDetailOraRepo;

	@Autowired
	private YiPolicyApprovalOraRepository yiPolicyApprovalOraRepo;

	@Autowired
	private YiPremCalOraRepository yiPremCalOraRepo;

	@Autowired
	private YiVatDetailOraRepository yiVatDetailOraRepo;

	@Autowired
	private YiSectionDetailOraRepository yiSectionDetailOraRepo;

	@Autowired
	private YiChargeDetailOraRepository yiChargeDetailOraRepo;

	@Autowired
	private MotcommDiscountDetailOraRepository motcommDiscountDetailOraRepo;

	@Autowired
	private MotDriverDetailOraRepository motDriverDetailOraRepos;

	@Autowired
	private CreditLimitDetailOraRepository creditLimitDetailOraRepo;

	@Autowired
	private HomePositionMasterRepository homeRepo;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private MySqlQuery mySqlQuery;

	@Autowired
	private OracleQuery oracle;

	@Transactional("mySqlTransactionManager")
	public boolean pushOracle(List<PremiaConfigMaster> configMasterList, List<String> params, String quoteNo) {
		try {
			configMasterList.stream().forEach(masterdata -> {
				List<PremiaConfigDataMaster> configData = getPremiaConfigData(masterdata.getCompanyId(),
						masterdata.getProductId(), masterdata.getPremiaId());
				List<Map<String, Object>> listFromQuery = new ArrayList<Map<String, Object>>();

				if (StringUtils.isNotBlank(masterdata.getQueryKey())) {
					String query = oracle.getQuery(masterdata.getQueryKey());
					System.out.println("Premia Id :" + masterdata.getPremiaId());
					System.out.println("Premia Table Name :" + masterdata.getPremiaTableName());
					System.out.println("**********************************************************");
					System.out.println("QueryKey : " + masterdata.getQueryKey());
					System.out.println("Oracle Main Quey : " + query);
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
					log.info("Oracle framedselecquery :: " + framedselecquery);
					query = "SELECT " + framedselecquery + " "
							+ query.substring(query.indexOf(" FROM"), query.length());

					log.info("Oracle framedselecquery with Select :: " + query);

					System.out.println("**********************************************************");
					System.out.println("Select Oracle  Qury with case condition");
					listFromQuery = mySqlQuery.getListFromQueryWithoutKey(query, params);
					System.out.println("**********************************************************");
				}
				delete1(quoteNo, masterdata.getPremiaTableName());

				for (Map<String, Object> qdata : listFromQuery) {
					System.out.println("Oracle Framing Insert Query");
					if (configData != null && !configData.isEmpty() && qdata != null) {
						Map<String, String> jmap = new HashMap<String, String>();
						List<String> colums = new ArrayList<String>();
						List<String> values = new ArrayList<String>();

						for (PremiaConfigDataMaster data : configData) {

							String value = "";
							if ("Y".equals(data.getDefaultYn())) {
								value = StringUtils.isBlank(data.getDefaultValue()) ? "" : data.getDefaultValue();
								switch (data.getDataTypeDesc()) {
								case "Date":

									if (value.equalsIgnoreCase("SYSDATE")) {
										value = "TO_TIMESTAMP('"
												+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date())
												+ "', 'YYYY-MM-DD HH24:MI:SS.FF3')";
									} else {
										value = normalizeToTimestamp(value);
									}

									break;

								case "String":
									value = "'" + value.replace("'", "") + "'";
									break;

								case "Number":
									break;

								default:
									value = "'" + value.replace("'", "") + "'";
								}

							} else {

								Object aliazval = qdata.get(data.getInputColumn()) == null ? ""
										: qdata.get(data.getInputColumn());
								value = String.valueOf(aliazval).replace("'", "");
								switch (data.getDataTypeDesc()) {
								case "Date":
									value = normalizeToTimestamp(value);
									break;

								case "String":
									value = "'" + value.replace("'", "") + "'";
									break;

								case "Number":
									break;

								default:
									value = "'" + value.replace("'", "") + "'";
								}
							}
							if (!value.startsWith("TO_TIMESTAMP") && !"NULL".equals(value)) {
								value = "'" + value.replace("'", "") + "'";
							}
							jmap.put(data.getColumnName(), value);
							colums.add(data.getColumnName());
							values.add(value);
						}
							if (!jmap.isEmpty()) {
								insertOracle(masterdata, colums, values);
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

	@Transactional("oracleTransactionManager")
	private void insertOracle(PremiaConfigMaster masterdata, List<String> colums, List<String> values) {
		try {
			System.out.println("***************************************************************");
			System.out.println("Oracle " + masterdata.getPremiaTableName() + " Table Inset Query");
			String resultColumn = colums.stream().map(String::toLowerCase).collect(Collectors.joining(","));
			String insertQuery = "INSERT INTO " + masterdata.getPremiaTableName().toLowerCase() + " (" + resultColumn
					+ ") VALUES (" + StringUtils.join(values, ",") + ")";
			log.info("Insert Query::" + insertQuery);
			boolean insert = oracle.insert(insertQuery);
			if (insert) {
				System.out.println(masterdata.getPremiaTableName() + " Success");
			} else {
				System.out.println(masterdata.getPremiaTableName() + " Failure");
			}
			System.out.println("****************************************************************");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("****************************************************************");
		}
	}

	private String normalizeToTimestamp(String rawDateStr) {
		try {
			if (StringUtils.isBlank(rawDateStr))
				return "NULL";

			rawDateStr = rawDateStr.trim();
			if (!rawDateStr.contains(".")) {
				rawDateStr += ".000";
			} else {
				String[] parts = rawDateStr.split("\\.");
				String millis = parts.length > 1 ? parts[1] : "000";
				millis = String.format("%-3s", millis).replace(' ', '0'); // pads or trims to 3 digits
				rawDateStr = parts[0] + "." + millis;
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date parsedDate = sdf.parse(rawDateStr);
			return "TO_TIMESTAMP('" + sdf.format(parsedDate) + "', 'YYYY-MM-DD HH24:MI:SS.FF3')";
		} catch (ParseException e) {
			log.error("Failed to parse date: {}", rawDateStr, e);
			return "NULL";
		}
	}

	@Transactional("mySqlTransactionManager")
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

	@Transactional("oracleTransactionManager")
	public void delete1(String quoteNo, String tableName) {
		try {
			String policyNo = "";
			HomePositionMaster home = homeRepo.findByQuoteNo(quoteNo);
			if (home != null) {
				policyNo = home.getPolicyNo();
			}
			if ("Yi_Policy_Detail".equalsIgnoreCase(tableName)) {
				List<YiPolicyDetailOra> list = yiPolicyDetailOraRepo.findByQuotationPolicyNo(policyNo);
				if (list.size() > 0 && list != null) {
					yiPolicyDetailOraRepo.deleteAll(list);
					System.out.println("Yi_Policy_Detail is deleted successfully" );
				}
			} else if ("Yi_Section_Detail".equalsIgnoreCase(tableName)) {
				List<YiSectionDetailOra> list = yiSectionDetailOraRepo.findByQuotationPolicyNo(policyNo);
				if (list.size() > 0 && list != null) {
					yiSectionDetailOraRepo.deleteAll(list);
					System.out.println("Yi_Section_Detail is deleted successfully" );
				}
			} else if ("PGIT_POL_RISK_ADDL_INFO_01".equalsIgnoreCase(tableName)) {
				List<PgithPolRiskAddlInfoOra> list = pgitPolRiskAddlInfoOraRepo.findByQuotationPolicyNo(policyNo);
				if (list.size() > 0 && list != null) {
					pgitPolRiskAddlInfoOraRepo.deleteAll(list);
					System.out.println("PGIT_POL_RISK_ADDL_INFO_01 is deleted successfully" );
				}
			} else if ("Mot_Driver_Detail".equalsIgnoreCase(tableName)) {
				List<MotDriverDetailOra> list = motDriverDetailOraRepos.findByQuotationPolicyNo(policyNo);
				if (list.size() > 0 && list != null) {
					motDriverDetailOraRepos.deleteAll(list);
					System.out.println("Mot_Driver_Detail is deleted successfully" );
				}
			} else if ("Yi_Cover_Detail".equalsIgnoreCase(tableName)) {
				List<YiCoverDetailOra> list = yiCoverDetailOraRepo.findByQuotationPolicyNo(policyNo);
				if (list.size() > 0 && list != null) {
					yiCoverDetailOraRepo.deleteAll(list);
					System.out.println("Yi_Cover_Detail is deleted successfully" );
				}
			} else if ("Mot_Comm_Discount_Detail".equalsIgnoreCase(tableName)) {
				List<MotCommDiscountDetailOra> list = motcommDiscountDetailOraRepo.findByQuotationPolicyNo(policyNo);
				if (list.size() > 0 && list != null) {
					motcommDiscountDetailOraRepo.deleteAll(list);
					System.out.println("Mot_Comm_Discount_Detail is deleted successfully" );
				}
			} else if ("Yi_Charge_Detail".equalsIgnoreCase(tableName)) {
				List<YiChargeDetailOra> list = yiChargeDetailOraRepo.findByQuotationPolicyNo(policyNo);
				if (list.size() > 0 && list != null) {
					yiChargeDetailOraRepo.deleteAll(list);
					System.out.println("Yi_Charge_Detail is deleted successfully" );
				}
			} else if ("Yi_Vat_Detail".equalsIgnoreCase(tableName)) {
				List<YiVatDetailOra> list = yiVatDetailOraRepo.findByQuotationPolicyNo(policyNo);
				if (list.size() > 0 && list != null) {
					yiVatDetailOraRepo.deleteAll(list);
					System.out.println("Yi_Vat_Detail is deleted successfully" );
				}
			} else if ("Yi_Prem_Cal".equalsIgnoreCase(tableName)) {
				List<YiPremCalOra> list = yiPremCalOraRepo.findByQuotationPolicyNo(policyNo);
				if (list.size() > 0 && list != null) {
					yiPremCalOraRepo.deleteAll(list);
					System.out.println("Yi_Prem_Cal is deleted successfully" );
				}
			} else if ("Yi_Policy_Approval".equalsIgnoreCase(tableName)) {
				List<YiPolicyApprovalOra> list = yiPolicyApprovalOraRepo.findByQuotationPolicyNo(policyNo);
				if (list.size() > 0 && list != null) {
					yiPolicyApprovalOraRepo.deleteAll(list);
					System.out.println("Yi_Policy_Approval is deleted successfully" );
				}
			} else if ("Credit_Limit_Detail".equalsIgnoreCase(tableName)) {
				List<CreditLimitDetailOra> list = creditLimitDetailOraRepo.findByCustomerCode(home.getCustomerCode());
				if (list.size() > 0 && list != null) {
					creditLimitDetailOraRepo.deleteAll(list);
					System.out.println("Credit_Limit_Detail is deleted successfully" );
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
