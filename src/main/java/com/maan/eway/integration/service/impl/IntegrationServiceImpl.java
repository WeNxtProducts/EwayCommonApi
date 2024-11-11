package com.maan.eway.integration.service.impl;

import java.sql.Timestamp;
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

import com.google.gson.Gson;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.CreditLimitDetail;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotCommDiscountDetail;
import com.maan.eway.bean.MotDriverDetail;
import com.maan.eway.bean.PgithPolRiskAddlInfo;
import com.maan.eway.bean.PremiaConfigDataMaster;
import com.maan.eway.bean.PremiaConfigMaster;
import com.maan.eway.bean.PtIntgFlexTran;
import com.maan.eway.bean.SeqPiftTranId;
import com.maan.eway.bean.YiChargeDetail;
import com.maan.eway.bean.YiCoverDetail;
import com.maan.eway.bean.YiPolicyApproval;
import com.maan.eway.bean.YiPolicyDetail;
import com.maan.eway.bean.YiPremCal;
import com.maan.eway.bean.YiSectionDetail;
import com.maan.eway.bean.YiVatDetail;
import com.maan.eway.integration.req.PremiaListRequest;
import com.maan.eway.integration.req.PremiaRequest;
import com.maan.eway.integration.res.PremiaResponse;
import com.maan.eway.integration.service.FrameReqService;
import com.maan.eway.integration.service.IntegrationService;
import com.maan.eway.repository.CreditLimitDetailRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotDriverDetailRepository;
import com.maan.eway.repository.MotcommDiscountDetailRepository;
import com.maan.eway.repository.PgitPolRiskAddlInfoRepository;
import com.maan.eway.repository.PremiaConfigDataMasterRepository;
import com.maan.eway.repository.PremiaConfigMasterRepository;
import com.maan.eway.repository.PtintgFlexTransRepository;
import com.maan.eway.repository.SeqPiftTranIdRepository;
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
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class IntegrationServiceImpl implements IntegrationService {

	
@Autowired
private PremiaConfigDataMasterRepository pcdatarepo;
@Autowired
private PremiaConfigMasterRepository pcmasterrepo;
@Autowired
private IntegrationService intSer;
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

@Autowired
private SeqPiftTranIdRepository seqPiftTranIdRepo;

@Autowired
private PtintgFlexTransRepository ptTransRepo;

@Autowired
private FrameReqService frameReqService;
@Autowired
private OracleQuery oracle;

@PersistenceContext
private EntityManager em;

Gson json = new Gson(); 
private Logger log=LogManager.getLogger(IntegrationServiceImpl.class);
/*
 * 
 * 
 *
   get Master table : premia_config_master 
   get Data Maste TAble : premia_config_data
   get Frame Insert query
*/

public boolean push(PremiaConfigMaster configMas , List<String> params,String quoteNo) {
	try {
		
		PremiaConfigMaster masterop = configMas  ;
		if(masterop!=null ) {
			PremiaConfigMaster masterdata = masterop ; //col names
			List<PremiaConfigDataMaster> configData = getPremiaConfigData(configMas.getCompanyId() ,configMas.getProductId() ,configMas.getPremiaId()  ) ;
			List<Map<String, Object>> listFromQuery = new ArrayList<Map<String, Object>>();
			
			if(StringUtils.isNotBlank(masterdata.getQueryKey())) {
				String query=oracle.getQuery(masterdata.getQueryKey());
				List<String> asList = fromQuerytoList(query);
				Map<String, String> maps = fromListToMaps(asList);
				
				Map<String,String> avoidd=new HashMap<String,String>();
				
				
				if(configData!=null && !configData.isEmpty()) {
					for (PremiaConfigDataMaster data : configData) {
						if(!"Y".equals(data.getDefaultYn())) {
							Map<String, String> filterdmap=maps.entrySet().stream().filter(m-> data.getInputColumn().equals(m.getKey()) ).collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));  
							// Map<String, String> filered = filterdmap.get(0);
							 String queryvalue = filterdmap.get(data.getInputColumn());
							 
							 if("Date".equals(data.getDataTypeDesc()) && !avoidd.containsKey(data.getInputColumn()) ) {
								// queryvalue=(data.getDataFormatType()==null || StringUtils.isBlank(queryvalue) ) ?queryvalue:data.getDataFormatType().replaceAll("<>",queryvalue );
								// avoidd.put(data.getInputColumn(), queryvalue);
								 maps.put(data.getInputColumn(), queryvalue); 
							 }
							 if("N".equals(data.getDefaultYn()) && "Y".equals(data.getCaseConditionYn()) ) {
									maps.put(data.getInputColumn(), data.getCaseCondition()); 
							 }else							 
								 maps.put(data.getInputColumn(), queryvalue);
							 
						}else if("N".equals(data.getDefaultYn()) && "Y".equals(data.getCaseConditionYn()) ) {
							maps.put(data.getInputColumn(), data.getCaseCondition()); 
						}
					}
				}
			//	Stream combined = Stream.concat(maps.entrySet().stream(), map2.entrySet().stream());
				
				String framedselecquery=frameselectfromMap(maps);
				log.info("framedselecquery :: "+framedselecquery);
				query="SELECT "+framedselecquery+" "+query.substring(query.indexOf(" FROM"), query.length());
				
				log.info("framedselecquery with Select :: "+query);
				/*maps.get(0);
				***********/
				listFromQuery = oracle.getListFromQueryWithoutKey(query, params);
//				if(listFromQuery!=null && listFromQuery.size()>0) {
//					 qdata = listFromQuery.get(0);
//				}
			}
			Boolean result= delete(quoteNo,masterdata.getPremiaTableName());
			
			for (Map<String, Object> qdata  : listFromQuery ) {
				if(configData!=null && !configData.isEmpty() && qdata!=null) {
					Map<String,String> jmap=new HashMap<String,String>();
					List<String> colums=new ArrayList<String>();
					List<String> values=new ArrayList<String>();
					
					for (PremiaConfigDataMaster data : configData) {
						String value="";
						if("Y".equals(data.getDefaultYn())) {
							value= StringUtils.isBlank(data.getDefaultValue())?"":data.getDefaultValue();
							
							if("Date".equals(data.getDataTypeDesc())) { 
							//	String dateformatt=StringUtils.isNotEmpty(data.getDataFormatType())?data.getDataFormatType().toUpperCase().replace("TO_CHAR", "TO_DATE"):null;
								String dateformatt=  StringUtils.isNotEmpty(data.getDataFormatType())?data.getDataFormatType().toUpperCase() : "yyyy-MM-dd hh:mm:ss" ;
								
								if(value.equalsIgnoreCase("SYSDATE") ) {
									SimpleDateFormat dbF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
									value= dbF.format(new Date()) ;
								} else {
								//	value = " STR_TO_DATE(" +"'"+ value + ","+","+ "'"+ dateformatt+") " ;
									value = "'"+ value + "'" ;

								}
							
									
							}
							value=(("String".equals(data.getDataTypeDesc())|| "Date".equals(data.getDataTypeDesc()) )?"'"+value+"'":value );
							
						}/*else if("N".equals(data.getDefaultYn()) &&  "Y".equals(data.getCasecondYn() ) ){
							Object aliazval=qdata.get(data.getQueryAliaz())==null?"":qdata.get(data.getQueryAliaz());
							value=aliazval;
						}*/else {
							Object aliazval=qdata.get(data.getInputColumn())==null?"":qdata.get(data.getInputColumn());
							
							value=String.valueOf(aliazval);
							if("Date".equals(data.getDataTypeDesc())) { 
							//	String dateformatt=StringUtils.isNotEmpty(data.getDataFormatType())?data.getDataFormatType().toUpperCase().replace("TO_CHAR", "TO_DATE"):null;
								String dateformatt=  StringUtils.isNotEmpty(data.getDataFormatType())?data.getDataFormatType().toUpperCase() : "yyyy-MM-dd hh:mm:ss" ;
								if(dateformatt!=null) 
								//	value = " STR_TO_DATE(" +"'"+ value + "'"+","+ "'"+dateformatt+"'"+") " ;
									//value=dateformatt.replaceAll("<>","'"+aliazval.toString()+"'" );
									value = "'"+ value + "'" ;

							}
							
							value=(("String".equals(data.getDataTypeDesc()) )?"'"+String.valueOf(aliazval)+"'":value);
						}
						jmap.put(data.getColumnName(), value);
						colums.add(data.getColumnName());
						values.add(value);
					}
					
					if(result=true) {
					if(!jmap.isEmpty()) {
						//Madison
//						Boolean result1=deleteTable(quoteNo,masterdata.getPremiaTableName(),jmap);
						String insertQuery="INSERT INTO "+masterdata.getPremiaTableName()+" ("+StringUtils.join(colums,",")
						+") VALUES ("+StringUtils.join(values,",")+")";
						log.info("Insert Query::"+insertQuery);
						oracle.insert(insertQuery);
					}
					}
					
				}
			}
			// Framing External Api
			String policyNo = "";
			String reqRefNo = "";
			String companyId="";
			String productId="";
			HomePositionMaster home = homeRepo.findByQuoteNo(quoteNo);
			if (home != null) {
				policyNo = home.getPolicyNo();
				reqRefNo = home.getRequestReferenceNo();
				companyId= home.getCompanyId();
				productId= home.getProductId().toString();
			}
			CompanyProductMaster product =  getCompanyProductMasterDropdown(companyId , productId);
		
			if ("100002".equalsIgnoreCase(companyId)) {
				ewayMotorPremiaPush(policyNo, reqRefNo, configMas);

			}
		}
		
		return true;
	}catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}
	return false;
}

private void updateIntegrationStatus(String quoteNo, HomePositionMaster home) {
	log.info("updateIntegrationStatus Enter :: "+home.getPolicyNo());
	boolean status=false,status1=false,status2=false,status3=false,status4=false,status5=false,status6=false,status7=false,status8=false,status9=false;
	String policyNo=home.getPolicyNo();
	try {
		List<YiPolicyDetail> list=yiPolicyReo.findByQuotationPolicyNo(policyNo);
		if(list.size()>0 && list!=null) {
			log.info("status=true;");
			status=true;
		}
	
		List<YiSectionDetail> list1=yisecRepo.findByQuotationPolicyNo(policyNo);
		if(list1.size()>0 && list1!=null) {
			log.info("status1=true;");
			status1=true;
		}
	
		List<PgithPolRiskAddlInfo> list2=pgitPolRiskRepo.findByQuotationPolicyNo(policyNo);
		if(list2.size()>0 && list2!=null) {
			log.info("status2=true;");
			status2=true;
		}
	
		List<MotDriverDetail> list3=motDrivDetailsRepo.findByQuotationPolicyNo(policyNo);
		if(list3.size()>0 && list3!=null) {
			log.info("status3=true;");
			status3=true;
		}
		List<YiCoverDetail> list4=yiCoverDetailRepo.findByQuotationPolicyNo(policyNo);
		if(list4.size()>0 && list4!=null) {
			log.info("status4=true;");
			status4=true;
		}

		List<MotCommDiscountDetail> list5=motComRepo.findByQuotationPolicyNo(policyNo);
		if(list5.size()>0 && list5!=null) {
			log.info("status5=true;");
			status5=true;
		}
	
		List<YiChargeDetail> list6=yiChargeDetailRepo.findByQuotationPolicyNo(policyNo);
		if(list6.size()>0 && list6!=null) {
			log.info("status6=true;");
			status6=true;
		}
	
		List<YiVatDetail> list7=yivatRepo.findByQuotationPolicyNo(policyNo);
		if(list7.size()>0 && list7!=null) {
			log.info("status7=true;");
			status7=true;
		}
		List<YiPremCal> list8=yipremRepo.findByQuotationPolicyNo(policyNo);
		if(list8.size()>0 && list8!=null) {
			log.info("status8=true;");
			status8=true;
		}
	
		List<YiPolicyApproval> list9=yipolicyRepo.findByQuotationPolicyNo(policyNo);
		if(list9.size()>0 && list9!=null) {
			log.info("status9=true;");
			status9=true;
		}
		if(status && status1 && status2 && status3 && status4  && status6 && status7 && status8 && status9) {
			home.setCoreIntgStatus("S");
		}else {
			home.setCoreIntgStatus("F");
		}
		
	}catch (Exception e) {
		e.printStackTrace();
		home.setCoreIntgStatus("F");
	}
	homeRepo.saveAndFlush(home);
	log.info("updateIntegrationStatus Exit :: ");
}

public void ewayMotorPremiaPush(String policyNo,String reqRefNo,PremiaConfigMaster configMas) {
	try {
		System.out.println("*********EXTERNAL API CALL STARTS*********");
		System.out.println("*********PolicyNo " + policyNo);

		if (configMas.getPremiaId() == 1) {
			System.out.println("*********1.YiPolicyDetail: ");
			Object list = frameReqService.pushYiPolicyDetail(policyNo);
			System.out.println("List " + json.toJson(list));
			System.out.println("_____________________________________________ ");
		} else if (configMas.getPremiaId() == 2) {
			System.out.println("*********2.YiSectionDetail:");
			Object list = frameReqService.pushYiSectionDetail(policyNo);
			System.out.println("List " + json.toJson(list));
			System.out.println("_____________________________________________ ");
		} else if (configMas.getPremiaId() == 3) {
			System.out.println("*********3.PgitPolRiskAddlInfo:");
			Object list = frameReqService.pushPgitPolRiskAddlInfo(policyNo);
			System.out.println("List " + json.toJson(list));
			System.out.println("_____________________________________________ ");
		} else if (configMas.getPremiaId() == 4) {
			System.out.println("*********4.MotDriverDetail: ");
			Object list = frameReqService.pushMotDriverDetail(policyNo);
			System.out.println("List " + json.toJson(list));
			System.out.println("_____________________________________________ ");
		} else if (configMas.getPremiaId() == 5) {
			System.out.println("*********5.YiCoverDetail: ");
			Object list = frameReqService.pushYiCoverDetail(policyNo);
			System.out.println("List " + json.toJson(list));
			System.out.println("_____________________________________________ ");
		} else if (configMas.getPremiaId() == 6) {
			System.out.println("*********6.MotCommDiscountDetail:");
			Object list = frameReqService.pushMotCommDiscountDetail(policyNo);
			System.out.println("List " + json.toJson(list));
			System.out.println("_____________________________________________ ");
		} else if (configMas.getPremiaId() == 7) {
			System.out.println("*********7.YiChargeDetail: ");
			Object list = frameReqService.pushYiChargeDetail(policyNo);
			System.out.println("List " + json.toJson(list));
			System.out.println("_____________________________________________ ");
		} else if (configMas.getPremiaId() == 8) {
			System.out.println("*********8.YiVatDetail:");
			Object list = frameReqService.pushYiVatDetail(policyNo);
			System.out.println("List " + json.toJson(list));
			System.out.println("_____________________________________________ ");
		} else if (configMas.getPremiaId() == 9) {
			System.out.println("*********9.YiPremCal:");
			Object list = frameReqService.pushYiPremCal(policyNo);
			System.out.println("List " + json.toJson(list));
			System.out.println("_____________________________________________ ");
		} else if (configMas.getPremiaId() == 10) {
			System.out.println("*********10.YiPolicyApproval:");
			Object list = frameReqService.pushYiPolicyApproval(policyNo);
			System.out.println("List " + json.toJson(list));
			System.out.println("_____________________________________________ ");
		} else if (configMas.getPremiaId() == 11) {
			System.out.println("*********11.CreditLimitDetail:");
			Object list = frameReqService.pushCreditLimitDetail(reqRefNo);
			System.out.println("List " + json.toJson(list));
			System.out.println("_____________________________________________ ");
		}

	}catch (Exception e) {
		e.printStackTrace();
	}
}
public void madisonMotorPremiaPush(String policyNo,String reqRefNo) {
	try {
		
		System.out.println("*********EXTERNAL API CALL STARTS*********");
		System.out.println("*********PolicyNo " + policyNo);
		System.out.println("*********PtIntgFlexTran:");
		Object list = frameReqService.pushPtIntgFlexTran(policyNo);
		System.out.println("List " + json.toJson(list));
		System.out.println("_____________________________________________ ");
	}catch (Exception e) {
		e.printStackTrace();
	}
}


public Boolean delete(String quoteNo,String tableName) {
	Boolean result=true;
	try {
		String policyNo = "";
		String reqRefNo = "";
		String companyId="";
		String productId="";
		HomePositionMaster home = homeRepo.findByQuoteNo(quoteNo);
		if (home != null) {
			policyNo = home.getPolicyNo();
			reqRefNo = home.getRequestReferenceNo();
			companyId= home.getCompanyId();
			productId= home.getProductId().toString();
		}
		if("Yi_Policy_Detail".equalsIgnoreCase(tableName)) {
			//List<YiPolicyDetail> list=yiPolicyReo.findByQuotationPolicyNo(policyNo);
			List<YiPolicyDetail> list=yiPolicyReo.findByRequestreferenceno(reqRefNo);
			if(list.size()>0 && list!=null) {
				yiPolicyReo.deleteAll(list);
				result=true;
			}
		}else if("Yi_Section_Detail".equalsIgnoreCase(tableName)) {
			//List<YiSectionDetail> list=yisecRepo.findByQuotationPolicyNo(policyNo);
			List<YiSectionDetail> list=yisecRepo.findByRequestreferenceno(reqRefNo);
			if(list.size()>0 && list!=null) {
				yisecRepo.deleteAll(list);
				result=true;
			}
		}else if("PGIT_POL_RISK_ADDL_INFO_01".equalsIgnoreCase(tableName)) {
			//List<PgithPolRiskAddlInfo> list=pgitPolRiskRepo.findByQuotationPolicyNo(policyNo);
			List<PgithPolRiskAddlInfo> list=pgitPolRiskRepo.findByRequestReferenceNo(reqRefNo);
			if(list.size()>0 && list!=null) {
				pgitPolRiskRepo.deleteAll(list);
				result=true;
			}
		}else if("Mot_Driver_Detail".equalsIgnoreCase(tableName)) {
			//List<MotDriverDetail> list=motDrivDetailsRepo.findByQuotationPolicyNo(policyNo);
			List<MotDriverDetail> list=motDrivDetailsRepo.findByRequestreferenceno(reqRefNo);
			if(list.size()>0 && list!=null) {
				motDrivDetailsRepo.deleteAll(list);
				result=true;
			}
		}else if("Yi_Cover_Detail".equalsIgnoreCase(tableName)) {
			//List<YiCoverDetail> list=yiCoverDetailRepo.findByQuotationPolicyNo(policyNo);
			List<YiCoverDetail> list=yiCoverDetailRepo.findByRequestreferenceno(reqRefNo);
			if(list.size()>0 && list!=null) {
				yiCoverDetailRepo.deleteAll(list);
				result=true;
			}
		}else if("Mot_Comm_Discount_Detail".equalsIgnoreCase(tableName)) {
			//List<MotCommDiscountDetail> list=motComRepo.findByQuotationPolicyNo(policyNo);
			List<MotCommDiscountDetail> list=motComRepo.findByRequestreferenceno(reqRefNo);
			if(list.size()>0 && list!=null) {
				motComRepo.deleteAll(list);
				result=true;
			}
		}else if("Yi_Charge_Detail".equalsIgnoreCase(tableName)) {
			//List<YiChargeDetail> list=yiChargeDetailRepo.findByQuotationPolicyNo(policyNo);
			List<YiChargeDetail> list=yiChargeDetailRepo.findByRequestreferenceno(reqRefNo);
			if(list.size()>0 && list!=null) {
				yiChargeDetailRepo.deleteAll(list);
				result=true;
			}
		}else if("Yi_Vat_Detail".equalsIgnoreCase(tableName)) {
			//List<YiVatDetail> list=yivatRepo.findByQuotationPolicyNo(policyNo);
			List<YiVatDetail> list=yivatRepo.findByRequestreferenceno(reqRefNo);
			if(list.size()>0 && list!=null) {
				yivatRepo.deleteAll(list);
				result=true;
			}
		}
		else if("Yi_Prem_Cal".equalsIgnoreCase(tableName)) {
			//List<YiPremCal> list=yipremRepo.findByQuotationPolicyNo(policyNo);
			List<YiPremCal> list=yipremRepo.findByRequestreferenceno(reqRefNo);
			if(list.size()>0 && list!=null) {
				yipremRepo.deleteAll(list);
				result=true;
			}
		}else if("Yi_Policy_Approval".equalsIgnoreCase(tableName)) {
			//List<YiPolicyApproval> list=yipolicyRepo.findByQuotationPolicyNo(policyNo);
			List<YiPolicyApproval> list=yipolicyRepo.findByRequestreferenceno(reqRefNo);
			if(list.size()>0 && list!=null) {
				yipolicyRepo.deleteAll(list);
				result=true;
			}
		}else if("Credit_Limit_Detail".equalsIgnoreCase(tableName)) {
			List<CreditLimitDetail> list=creditRepo.findByRequestreferenceno(reqRefNo);
			if(list.size()>0 && list!=null) {
				creditRepo.deleteAll(list);
				result=true;
			}
		}
	}catch (Exception e) {
		e.printStackTrace();
	}
	return result;
}

public Boolean deleteTable(String quoteNo, String tableName,Map<String,String> jmap) {
	Boolean result=false; 
	try {
		String companyId="";
		String productId="";
		String policyNo="";
		HomePositionMaster home = homeRepo.findByQuoteNo(quoteNo);
		if (home != null) {
			policyNo = home.getPolicyNo();
			companyId = home.getCompanyId();
			productId = home.getProductId().toString();
		}
		if("100004".equalsIgnoreCase(companyId) && "5".equalsIgnoreCase(productId)) {
        // using for-each loop for iteration over Map.entrySet() 
        for (Map.Entry<String,String> entry : jmap.entrySet())  {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            String key=entry.getKey();
            String value="";
            if("PIFT_LEVEL".equalsIgnoreCase(key)) {
            	value=entry.getValue();
            	if ("Pt_intg_flex_tran".equalsIgnoreCase(tableName)) { 
    				List<PtIntgFlexTran> list = ptTransRepo.findByPiftPolicyNoAndPiftLevel(policyNo,value.replaceAll("\'",""));
    				if (list.size() > 0 && list != null) {
    					ptTransRepo.deleteAll(list);
    					 System.out.println("Record Deteted :: "+key+" "+value);
    					result = true;
    				}
    			}
            }
        }
		}
	}catch (Exception e) {
		e.printStackTrace();
	}
	return result;
}

private String frameselectfromMap(Map<String, String> maps) {
	String result = maps.entrySet().stream().map(map -> (map.getValue()+" "+map.getKey()))
    .collect(Collectors.joining(","));
	
	
	return result;
}


private List<String> fromQuerytoList(String selectquery){
	if(selectquery.indexOf(",")!=-1) {
		selectquery=selectquery.substring(selectquery.indexOf("SELECT")+6, selectquery.indexOf(" FROM"));
		List<String> arrays=new ArrayList<String>();
		String[] col_aliz = selectquery.split(","); //column names
		for(int i=0;i<col_aliz.length;i++) {
			arrays.add(col_aliz[i]);
		}
		return arrays;
	}
	return null;
}

private Map<String,String> fromListToMaps(List<String> arrays){
	Map<String,String> listmaps=new HashMap<String,String>();
	for (String val : arrays) {
		if(val.trim().indexOf(" ")!=1) {
			val=val.trim();
			String[] split = val.split(" ");
			//Map<String,String> hmap=new HashMap<String,String>();
			listmaps.put(split[split.length-1],split[0]);
			//listmaps.add(hmap);
		}
	}
	return listmaps;
}

@Override
public PremiaResponse pushPremiaIntegration(PremiaRequest request) {
	PremiaResponse response = new PremiaResponse();
	try {
		String policyNo = "";
		String reqRefNo = "";
		String companyId="";
		String productId="";
		HomePositionMaster home = homeRepo.findByQuoteNo(request.getQuoteNo()); //get all tables names and details
		if (home != null) {
			policyNo = home.getPolicyNo();
			reqRefNo = home.getRequestReferenceNo();
			companyId= home.getCompanyId();
			productId= home.getProductId().toString();
		}
		CompanyProductMaster product =  getCompanyProductMasterDropdown(companyId , productId);
	
		 List<PremiaConfigMaster> configMasterList =   getPremiaConfigMaster(home.getCompanyId() , home.getProductId() , request.getPremiaIds() );
		
		List<String> param=new ArrayList<String>();
		param.add(request.getQuoteNo());
		 
		for (PremiaConfigMaster configMas :  configMasterList ) {
			boolean push = push(configMas , param,request.getQuoteNo());
			if(push ==true  ) {
				response.setResponse("Success");	
				
			} else {
				response.setResponse("Failed");
			} 
			
		}
		updateIntegrationStatus(request.getQuoteNo(),home);
		if ("100004".equalsIgnoreCase(companyId)) {
			 SeqPiftTranId entity=new SeqPiftTranId();
			 List<SeqPiftTranId> data=seqPiftTranIdRepo.findAllByOrderByTranIdDesc();
			 Long id=data.get(0).getTranId()+1;
			 entity.setTranId(id);
	         seqPiftTranIdRepo.saveAndFlush(entity);  
	         System.out.println(entity);
		}
		if ("100004".equalsIgnoreCase(companyId)) {
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				madisonMotorPremiaPush(policyNo, reqRefNo);
			}

		}
	}catch(Exception e){
		e.printStackTrace();
		return null ;
	}
	return response ;
}

	
	
	public synchronized List<PremiaConfigMaster> getPremiaConfigMaster(String insuraceId , Integer productId , List<String> premiaIds ) {
		List<PremiaConfigMaster> list = new ArrayList<PremiaConfigMaster>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PremiaConfigMaster> query=  cb.createQuery(PremiaConfigMaster.class);
			// Find All
			Root<PremiaConfigMaster> c = query.from(PremiaConfigMaster.class);
			
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("premiaId")));
			
			
			// Effective Date Start Max Filter
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<PremiaConfigMaster> ocpm1 = effectiveDate.from(PremiaConfigMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("premiaId"),ocpm1.get("premiaId"));
			Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a3 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2,a3,a4);
			
			// Effective Date End Max Filter
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<PremiaConfigMaster> ocpm2 = effectiveDate2.from(PremiaConfigMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			Predicate a5 = cb.equal(c.get("premiaId"),ocpm2.get("premiaId"));
			Predicate a6 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a7 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a5,a6,a7,a8);
						
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"), insuraceId);
			Predicate n5 = cb.equal(c.get("productId"), productId);
			Predicate n7 = cb.equal(c.get("productId"), "99999");
			Predicate n8 = cb.or(n5,n7);
			//In 
			//Expression<String>e0= c.get("premiaId");
			//Predicate n6 = e0.in(premiaIds);
			query.where(n1,n2,n3,n4,n8).orderBy(orderList);
			
			// Get Result
			TypedQuery<PremiaConfigMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getPremiaId()))).collect(Collectors.toList());
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list ;
	}

	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	public synchronized List<PremiaConfigDataMaster> getPremiaConfigData(String insuraceId , String productId , Integer premiaId ) {
		List<PremiaConfigDataMaster> list = new ArrayList<PremiaConfigDataMaster>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PremiaConfigDataMaster> query=  cb.createQuery(PremiaConfigDataMaster.class);
			// Find All
			Root<PremiaConfigDataMaster> c = query.from(PremiaConfigDataMaster.class);
			
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("premiaId")));
			
			/*
			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<PremiaConfigDataMaster> ocpm1 = effectiveDate.from(PremiaConfigDataMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("premiaId"),ocpm1.get("premiaId"));
			Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a3 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2,a3,a4);
			
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<PremiaConfigDataMaster> ocpm2 = effectiveDate2.from(PremiaConfigDataMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			Predicate a5 = cb.equal(c.get("premiaId"),ocpm2.get("premiaId"));
			Predicate a6 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a7 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a5,a6,a7,a8);
						
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"), insuraceId);
			Predicate n5 = cb.equal(c.get("productId"), productId);
			Predicate n6 = cb.equal(c.get("premiaId"), premiaId);
			query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			*/
			
			
			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<PremiaConfigDataMaster> ocpm1 = amendId.from(PremiaConfigDataMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("premiaId"), c.get("premiaId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), c.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"),c.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("columnId"),c.get("columnId"));

			amendId.where(a1, a2,a3,a4);

			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("amendId"),amendId);	
			Predicate n3 = cb.equal(c.get("companyId"), insuraceId);
			Predicate n4 = cb.equal(c.get("productId"), productId);
			Predicate n5 = cb.equal(c.get("productId"), "99999");
			Predicate n6 =cb.or(n4,n5);
			Predicate n7 = cb.equal(c.get("premiaId"), premiaId);
			query.where(n1,n2,n3,n6,n7).orderBy(orderList);
			// Get Result
			TypedQuery<PremiaConfigDataMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getPremiaId() , o.getColumnId()))).collect(Collectors.toList());
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list ;
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
			product = list.size() > 0 ? list.get(0) :null;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return product;
	}

	@Override
	public PremiaResponse hitByQuoteNo(PremiaListRequest req) {
		  PremiaResponse response = new PremiaResponse();

	      try {
	         List<HomePositionMaster> data = this.homeRepo.findAllByOrderByEntryDateDesc();
	         List<String> quoteNo=new ArrayList<>();
	         List<String> premiaIds = new ArrayList<>();
	         if (data.size() > 0 && data != null && !data.isEmpty() && req.getQuoteNo() == null) {
	            quoteNo = data.stream().map(HomePositionMaster::getQuoteNo).collect(Collectors.toList());
	            premiaIds.add("1");
	            premiaIds.add("2");
	            premiaIds.add("3");
	            premiaIds.add("4");
	            premiaIds.add("5");
	            premiaIds.add("6");
	            premiaIds.add("7");
	            premiaIds.add("8");
	            premiaIds.add("9");
	            premiaIds.add("10");
	            premiaIds.add("11");
	            premiaIds.add("12");
	         } else if (req.getQuoteNo() != null) {
	            quoteNo = req.getQuoteNo();
	            premiaIds = req.getPremiaIds();
	         }

	         PushIntegrationThread hit = new PushIntegrationThread(this.intSer,quoteNo,premiaIds);
	         Thread push = new Thread(hit);
	         push.start();
	         response.setResponse("Processing.....");
	         return response;
	      } catch (Exception var8) {
	         var8.printStackTrace();
	         return null;
	      }
	}
	
}