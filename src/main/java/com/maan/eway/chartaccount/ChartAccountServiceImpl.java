package com.maan.eway.chartaccount;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PolicyDrcrDetail;
import com.maan.eway.bean.ProductTaxSetup;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PolicyDrcrDetailRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;

@Service
public class ChartAccountServiceImpl implements ChartAccountService {
	
	
	@Autowired
	private HomePositionMasterRepository hpmRepo;
	
	@Autowired
	private ChartParentMasterRepository chatParentMasterRepo;
	
	@Autowired
	private PolicyDrcrDetailRepository policyDRCRRepo;
	
	@Autowired
	private JpqlQueryServiceImpl jpqlQuery;
	
	@Autowired
	private GenerateSeqNoServiceImpl genNo;
	
	@Autowired
	private SectionDataDetailsRepository secRepo ;
	
	@Autowired
	private EServiceSectionDetailsRepository serviceRepo;


	@Override
	public CommonRes drcrEntry(ChartAccountRequest req) {
		CommonRes response = new CommonRes();
		try {
			
			if("N".equals(req.getDiscountYn())) {
				
				String quoteNo =req.getQuoteNo();
				HomePositionMaster hpm =hpmRepo.findByQuoteNo(quoteNo);
				Integer companyId =Integer.valueOf(hpm.getCompanyId());
				Integer productId =hpm.getProductId();
				//Integer sectionId =hpm.getSectionId();
				List<SectionDataDetails> sections = secRepo.findByQuoteNo(quoteNo);
				List<Integer> sectionIds = new ArrayList<Integer>(); 
				sections.forEach( o -> {
					sectionIds.add(Integer.valueOf(o.getSectionId()));
				} ); 
				
				BigDecimal brokerCommision =hpm.getCommissionPercentage();
				String taxFor ="";
				String endorsmentType =StringUtils.isBlank(hpm.getEndtTypeId())?"":hpm.getEndtTypeId();
				
				BigDecimal endtPremium =StringUtils.isNotBlank(endorsmentType)?hpm.getEndtPremium():new BigDecimal(0);
				
				List<ChartParentMaster> cpm =jpqlQuery.getChartParentMasterDetails(companyId);
				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
				
				// Generate Policy Seq
				SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
			 	generateSeqReq.setInsuranceId(hpm.getCompanyId());  
			 	generateSeqReq.setProductId(hpm.getProductId().toString());
			 	List<String> params = new ArrayList<String>();
			 	params.add(hpm.getQuoteNo());
			 	generateSeqReq.setType("7");
			 	generateSeqReq.setParams(params);
			 	String crnumber =  genNo.generateSeqCall(generateSeqReq);//genNo.generateCreditNo(hpm.getBrokerCode());
				
				generateSeqReq.setType("6");
			 	generateSeqReq.setTypeDesc("DEBIT_NO");
			 	generateSeqReq.setParams(params);
			 	String drnumber =  genNo.generateSeqCall(generateSeqReq);//genNo.generateDebitNo(hpm.getBrokerCode());
				
				int index =1;
				
				String minusSign ="";
				
				for(ChartParentMaster c : cpm) {
					
					List<ChartAccountChildMaster> charAccount =jpqlQuery.getChildChartAccountData(companyId,productId,sectionIds,c);
					
					Double premiumLc =null;
					BigDecimal premiumFc =null;
					String documentType ="";
					String docId ="";
					String  documentNo ="";
					BigDecimal premiumFcWithT=null;
					BigDecimal premiumLcWithT=null;
					String drcrFlag ="";
					String narration="";
					Boolean bokerCommiCheck=false;
				
					List<Integer> coverIds =charAccount.stream().map(c1 -> c1.getId().getCoverId()).collect(Collectors.toList());
										 
					if("DR".equalsIgnoreCase(c.getAccountType()) && "C".equalsIgnoreCase(c.getCharactersticType())) {
								
												
						 List<PolicyCoverData> pcdList =jpqlQuery.getPolicyCoverDataPremium(quoteNo, coverIds);
						
						 
						 if(StringUtils.isBlank(endorsmentType)) {
						 
							 premiumFc =pcdList.stream().filter(p->p.getTaxId()==0 )
									 .filter(p ->p.getDiscLoadId()==0)
									 .map(p ->p.getPremiumExcludedTaxFc())
									 .reduce(new BigDecimal(0), (a,b) ->a.add(b));
									 
							/* premiumLc =pcdList.stream().filter(p->p.getTaxId()==0 )
									 .filter(p ->p.getDiscLoadId()==0)
									 .map(p ->Double.valueOf(p.getPremiumExcludedTaxLc().toPlainString()))
									 .collect(Collectors.summingDouble(p ->p));*/
						 }else {
							 
							/* BigDecimal premiumIncFcWithT =pcdList.stream().filter(p->p.getTaxId()==0 )
									 .filter(p ->p.getDiscLoadId()!=0)
									 .filter(p ->p.getCoverageType().equals("E"))
									 .map(p ->p.getPremiumIncludedTaxFc())
									 .reduce(new BigDecimal(0), (a,b)->a.add(b));
							 
							// minusSign =premiumIncFcWithT.longValue()<0L ?"-" :"";*/
							 						 
							 premiumFc =pcdList.stream().filter(p->p.getTaxId()==0 )
									 .filter(p ->p.getDiscLoadId()!=0)
									 .filter(p ->p.getCoverageType().equals("E"))
									 .map(p ->p.getPremiumExcludedTaxFc())
									 .reduce(new BigDecimal(0), (a,b) ->a.add(b)).abs();
							 
							/* premiumLc =pcdList.stream().filter(p->p.getTaxId()==0 )
									 .filter(p ->p.getDiscLoadId()!=0)
									 .filter(p ->p.getCoverageType().equals("E"))
									 .map(p ->Double.valueOf(p.getPremiumExcludedTaxLc().toPlainString()))
									 .collect(Collectors.summingDouble(p ->p));*/
							 
							 //premiumFc =StringUtils.isBlank(minusSign)?premiumFc:
									// premiumFc.longValue()<0 ? premiumFc : premiumFc.abs();
							
						 }
						 
						 if(endtPremium.doubleValue()<0) {
							 drcrFlag="CR";
							 documentType="C";
							 documentNo =crnumber;
	
						 }else {
							 documentType="C";
							 documentNo =drnumber;
	
						 }
						 	
						 docId=hpm.getCustomerId();
						 narration=c.getChartAccountDesc();
						 bokerCommiCheck=true;
						
					}else if("DR".equalsIgnoreCase(c.getAccountType()) && "T".equalsIgnoreCase(c.getCharactersticType())) {
						
						
						 List<PolicyCoverData> pcdList =jpqlQuery.getPolicyCoverDataTax(quoteNo, coverIds);
						 
						 
						 Optional<PolicyCoverData> pcd =pcdList.stream().filter(p->p.getTaxId()!=0 )
							 .filter(p ->p.getDiscLoadId()==0)
							 .findFirst();
						 
						 narration =pcd.isPresent()?pcd.get().getTaxCalcType().equals("P")?c.getChartAccountDesc() +" @ "+pcd.get().getTaxRate().toString()+"%"
							 	 :c.getChartAccountDesc() :"";
								
						 
						 if(StringUtils.isBlank(endorsmentType)) {
						 
							 premiumFc =pcdList.stream().filter(p ->p.getTaxId()!=0)
									 .filter(p ->p.getDiscLoadId()==0)
									 .map(p ->p.getTaxAmount())
									 .reduce(new BigDecimal(0),(a,b) ->a.add(b));
							 
							 
						 }else {
							 
							 premiumFc=pcdList.stream().filter(p ->p.getTaxId()!=0)
									 .filter(p ->p.getDiscLoadId()!=0)
									 .filter(p ->p.getCoverageType().equals("T"))
									 .map(p ->p.getTaxAmount())
									 .reduce(new BigDecimal(0), (a,b) ->a.add(b)).abs();
									
							 						 
							// premiumFc =StringUtils.isBlank(minusSign)?premiumFc:
								// premiumFc.longValue()<0 ? premiumFc :premiumFc.abs();
						 }
						
						 if(endtPremium.doubleValue()<0) {
							 drcrFlag="CR";
							 documentType="C";
							 documentNo =crnumber;
						 }else {
							 documentType="C";
							documentNo =drnumber;
	
						 }
						 
						docId=hpm.getCustomerId();
						bokerCommiCheck=true;
					}else if("CR".equalsIgnoreCase(c.getAccountType()) && brokerCommision.doubleValue()>0 &&
							"C".equalsIgnoreCase(c.getCharactersticType())) {
						
						
						 List<PolicyCoverData> pcdList =jpqlQuery.getPolicyCoverDataPremium(quoteNo, coverIds);
							
						
						
						 if(StringUtils.isBlank(endorsmentType)) {
							 
							 premiumFcWithT =pcdList.stream().filter(p->p.getTaxId()==0 )
									 .filter(p ->p.getDiscLoadId()==0)
									 .map(p ->p.getPremiumExcludedTaxFc())
									.reduce(new BigDecimal(0),(a,b) -> a.add(b));
									 
							/* premiumLcWithT =pcdList.stream().filter(p->p.getTaxId()==0 )
									 .filter(p ->p.getDiscLoadId()==0)
									 .map(p ->Double.valueOf(p.getPremiumExcludedTaxLc().toPlainString()))
									 .collect(Collectors.summingDouble(p ->p));*/
						 }else {
							 
							 /*Double premiumIncWithT =pcdList.stream().filter(p->p.getTaxId()==0 )
									 .filter(p ->p.getDiscLoadId()!=0)
									 .filter(p ->p.getCoverageType().equals("E"))
									 .map(p ->Double.valueOf(p.getPremiumIncludedTaxFc().toPlainString()))
									 .collect(Collectors.summingDouble(p ->p));
							// minusSign =premiumIncWithT<0 ?"-" :"";*/
							 
							 premiumFcWithT =pcdList.stream().filter(p->p.getTaxId()==0 )
									 .filter(p ->p.getDiscLoadId()!=0)
									 .filter(p ->p.getCoverageType().equals("E"))
									 .map(p ->p.getPremiumExcludedTaxFc())
									 .reduce(new BigDecimal(0), (a,b) -> a.add(b)).abs();
									 
							/* premiumLcWithT =pcdList.stream().filter(p->p.getTaxId()==0 )
									 .filter(p ->p.getDiscLoadId()!=0)
									 .filter(p ->p.getCoverageType().equals("E"))
									 .map(p ->Double.valueOf(p.getPremiumExcludedTaxLc().toPlainString()))
									 .collect(Collectors.summingDouble(p ->p));*/
							 
							// premiumFcWithT =StringUtils.isBlank(minusSign)?premiumFcWithT:Double.valueOf("-"+premiumFcWithT.toString());
							
							// premiumFcWithT =StringUtils.isBlank(minusSign)?premiumFcWithT:
								// premiumFcWithT.longValue()<0 ? premiumFcWithT :premiumFcWithT.abs();
							 // premiumLcWithT =StringUtils.isBlank(minusSign)?premiumLcWithT:Double.valueOf("-"+premiumLcWithT.toString());
	
						 }
						 
						 if(endtPremium.doubleValue()<0) {
							 drcrFlag="DR";
							 documentType="B";
							 documentNo =drnumber;
	
						 }else {
							 documentType="B";
							 documentNo =crnumber;
						 }
						 	 
						premiumFc =premiumFcWithT.multiply(brokerCommision).divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
						hpm.setCommission(premiumFc);
						hpmRepo.saveAndFlush(hpm); 
						docId=hpm.getLoginId();
						narration=c.getChartAccountDesc() +" @ "+new DecimalFormat("#,##0.0").format(brokerCommision.doubleValue())+"%";
						bokerCommiCheck=true;
						
					}else if("CR".equalsIgnoreCase(c.getAccountType()) && brokerCommision.doubleValue()>0
							&& "T".equalsIgnoreCase(c.getCharactersticType())) {
													
						
						
						if(endtPremium.doubleValue()<0) {
							drcrFlag="DR";
							documentType="B";
							documentNo =drnumber;
							taxFor="ER";
						}else if(endtPremium.doubleValue()>0) {
							 documentType="B";
							 documentNo =crnumber;
							 taxFor="EC";
						 } else {
							 taxFor="NB";
							 documentType="B";
							 documentNo =crnumber;
						 }
						
						ProductTaxSetup taxProductTaxSetup =jpqlQuery.getProductTaxSetup(companyId,coverIds,productId,hpm.getBranchCode(),taxFor); 
						 
						BigDecimal taxPer =new BigDecimal(taxProductTaxSetup.getValue());
						premiumFc =hpm.getCommission().multiply(taxPer).divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
						docId=hpm.getLoginId();
						bokerCommiCheck=true;
						narration =c.getChartAccountDesc() +" @ "+new DecimalFormat("#,##0.0").format(taxPer.doubleValue())+"%";
							 	
					}
					
					if(bokerCommiCheck) {
					
						HashMap<String,Object> saveReq =new HashMap<>();
						saveReq.put("PolicyNo", StringUtils.isBlank(req.getPolicyNo())?hpm.getPolicyNo():req.getPolicyNo());
						saveReq.put("QuoteNo", hpm.getQuoteNo());
						saveReq.put("ChargeCode", c.getChartAccountCode());
						saveReq.put("ChargeId", index);
						saveReq.put("DocNo", documentNo);
						saveReq.put("DocType", documentType);
						saveReq.put("DocId", docId);
					//	saveReq.put("AmountLc",premiumLc);
						saveReq.put("CompanyId", companyId);
						saveReq.put("ProductId", productId);
						saveReq.put("BranchCode", hpm.getBranchCode());
						saveReq.put("DRCRFlag", StringUtils.isBlank(drcrFlag)?c.getAccountType():drcrFlag);
						saveReq.put("AmountFC",premiumFc);
						saveReq.put("ChargeAccountDesc", c.getChartAccountDesc());
						saveReq.put("Narration", narration);
						saveReq.put("DisplayOrder",c.getDisplayOrder());
						
						list.add(saveReq);
		
						index++;
					}
					
				}
				
				List<PolicyDrcrDetail> drcrList =saveDRCR(list);
				response.setCommonResponse(drcrList);
				response.setMessage("Success");
			}else if("Y".equals(req.getDiscountYn())) {
				
				
				List<EserviceSectionDetails> sectionList = serviceRepo.findByRequestReferenceNo(req.getRequestRefNo());
				
				List<Integer> sectionId =sectionList.stream().map(p ->Integer.valueOf(p.getSectionId()))
						.collect(Collectors.toList());
				
				
				List<ChartAccountChildMaster> childMaster =jpqlQuery.getChildChartAccountData(req.getCompanyId(), req.getProductId(), sectionId, req.getChartId());
				
				List<Integer> coverIds =childMaster.stream().map(p ->p.getId().getCoverId())
						.collect(Collectors.toList());
				
				BigDecimal premium =jpqlQuery.getPremium(req.getRequestRefNo(),coverIds);
				
				response.setCommonResponse(premium);
				response.setMessage("Success");
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	private List<PolicyDrcrDetail> saveDRCR(List<Map<String, Object>> list) {

		try {
			
			return list.stream().map(p ->{
				
				PolicyDrcrDetail drcrDetail = PolicyDrcrDetail.builder()
						.amountFc(p.get("AmountFC")==null?new BigDecimal(0):new BigDecimal(p.get("AmountFC").toString()))
						.amountLc(p.get("AmountFC")==null?new BigDecimal(0):new BigDecimal(p.get("AmountFC").toString()))
						.branchCode(p.get("BranchCode")==null?null:p.get("BranchCode").toString())
						.chargeAccountDesc(p.get("ChargeAccountDesc")==null?null:p.get("ChargeAccountDesc").toString())
						.chargeCode(p.get("ChargeCode")==null?new BigDecimal(0):BigDecimal.valueOf(Double.valueOf(p.get("ChargeCode").toString())))
						.chgId(p.get("ChargeId")==null?new BigDecimal(0):BigDecimal.valueOf(Double.valueOf(p.get("ChargeId").toString())))
						.companyId(p.get("CompanyId")==null?null:p.get("CompanyId").toString())
						.displayOrder(p.get("DisplayOrder")==null?null:Integer.valueOf(p.get("DisplayOrder").toString()))
						.docId(p.get("DocId")==null?null:p.get("DocId").toString())
						.docType(p.get("DocType")==null?null:p.get("DocType").toString())
						.docNo(p.get("DocNo")==null?null:p.get("DocNo").toString())
						.drcrFlag(p.get("DRCRFlag")==null?null:p.get("DRCRFlag").toString())
						.entryDate(new Date())
						.narration(p.get("Narration")==null?null:p.get("Narration").toString())
						.policyNo(p.get("PolicyNo")==null?null:p.get("PolicyNo").toString())
						.productId(p.get("ProductId")==null?null:p.get("ProductId").toString())
						.quoteNo(p.get("QuoteNo")==null?null:p.get("QuoteNo").toString())
						.status("Y")
						.build();
				
				return policyDRCRRepo.save(drcrDetail);
			}).collect(Collectors.toList());
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
