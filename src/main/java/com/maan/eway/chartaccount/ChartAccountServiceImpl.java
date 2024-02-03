package com.maan.eway.chartaccount;

import java.math.BigDecimal;
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

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PolicyDrcrDetail;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PolicyDrcrDetailRepository;

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


	@Override
	public CommonRes drcrEntry(String quoteNo, String policyNo) {
		CommonRes response = new CommonRes();
		try {
			
			HomePositionMaster hpm =hpmRepo.findByQuoteNo(quoteNo);
			Integer companyId =Integer.valueOf(hpm.getCompanyId());
			Integer productId =hpm.getProductId();
			Integer sectionId =hpm.getSectionId();
			BigDecimal brokerCommision =hpm.getCommissionPercentage();
			
			String endorsmentType =StringUtils.isBlank(hpm.getEndtTypeId())?"":hpm.getEndtTypeId();
			
			BigDecimal endtPremium =StringUtils.isNotBlank(endorsmentType)?hpm.getEndtPremium():new BigDecimal(1);
			
			List<ChartParentMaster> cpm =chatParentMasterRepo.findByChatParentIdCompanyIdAndStatusIgnoreCaseOrderByDisplayOrderAsc(companyId, "Y");
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			
			String crnumber =  genNo.generateCreditNo(hpm.getBrokerCode());
			
			String drnumber =  genNo.generateDebitNo(hpm.getBrokerCode());
			
			int index =1;
			
			String minusSign ="";
			
			for(ChartParentMaster c : cpm) {
				
				List<ChartAccountChildMaster> charAccount =jpqlQuery.getChildChartAccountData(companyId,productId,sectionId,c);
				
				Double premiumLc =null;
				Double premiumFc =null;
				String documentType ="";
				String docId ="";
				String  documentNo ="";
				Double premiumFcWithT=0D;
				Double premiumLcWithT=0D;
				String drcrFlag ="";
				String narration="";
				Boolean bokerCommiCheck=false;
			
				List<Integer> coverIds =charAccount.stream().map(c1 -> c1.getId().getCoverId()).collect(Collectors.toList());
									 
				if("DR".equalsIgnoreCase(c.getAccountType()) && "C".equalsIgnoreCase(c.getCharactersticType())) {
							
											
					 List<PolicyCoverData> pcdList =jpqlQuery.getPolicyCoverDataPremium(quoteNo, coverIds);
					
					 
					 if(StringUtils.isBlank(endorsmentType)) {
					 
						 premiumFc =pcdList.stream().filter(p->p.getTaxId()==0 )
								 .filter(p ->p.getDiscLoadId()==0)
								 .map(p ->Double.valueOf(p.getPremiumExcludedTaxFc().toPlainString()))
								 .collect(Collectors.summingDouble(p ->p));
								 
						 premiumLc =pcdList.stream().filter(p->p.getTaxId()==0 )
								 .filter(p ->p.getDiscLoadId()==0)
								 .map(p ->Double.valueOf(p.getPremiumExcludedTaxLc().toPlainString()))
								 .collect(Collectors.summingDouble(p ->p));
					 }else {
						 
						 Double premiumIncFcWithT =pcdList.stream().filter(p->p.getTaxId()==0 )
								 .filter(p ->p.getDiscLoadId()!=0)
								 .filter(p ->p.getCoverageType().equals("E"))
								 .map(p ->Double.valueOf(p.getPremiumIncludedTaxFc().toPlainString()))
								 .collect(Collectors.summingDouble(p ->p));
						 
						 minusSign =premiumIncFcWithT<0 ?"-" :"";
						 						 
						 premiumFc =pcdList.stream().filter(p->p.getTaxId()==0 )
								 .filter(p ->p.getDiscLoadId()!=0)
								 .filter(p ->p.getCoverageType().equals("E"))
								 .map(p ->Double.valueOf(p.getPremiumExcludedTaxFc().toPlainString()))
								 .collect(Collectors.summingDouble(p ->p));
						 
						/* premiumLc =pcdList.stream().filter(p->p.getTaxId()==0 )
								 .filter(p ->p.getDiscLoadId()!=0)
								 .filter(p ->p.getCoverageType().equals("E"))
								 .map(p ->Double.valueOf(p.getPremiumExcludedTaxLc().toPlainString()))
								 .collect(Collectors.summingDouble(p ->p));*/
						 
						 premiumFc =StringUtils.isBlank(minusSign)?premiumFc:
								 premiumFc<0 ? premiumFc :Double.valueOf( "-"+premiumFc);
						// premiumLc =StringUtils.isBlank(minusSign)?premiumLc:Double.valueOf("-"+premiumLc);
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
					 
					 narration =pcd.isPresent()?pcd.get().getTaxCalcType().equals("P")?c.getChartAccountDesc() +" @ "+pcd.get().getTaxRate().toString()
						 	 :c.getChartAccountDesc() :"";
							
					 
					 if(StringUtils.isBlank(endorsmentType)) {
					 
						 premiumFc=pcdList.stream().filter(p ->p.getTaxId()!=0)
								 .filter(p ->p.getDiscLoadId()==0)
								 .map(p ->Double.valueOf(p.getTaxAmount().toPlainString()))
								 .collect(Collectors.summingDouble(p ->p));
						 
					 }else {
						 
						 premiumFc=pcdList.stream().filter(p ->p.getTaxId()!=0)
								 .filter(p ->p.getDiscLoadId()!=0)
								 .filter(p ->p.getCoverageType().equals("T"))
								 .map(p ->Double.valueOf(p.getTaxAmount().toPlainString()))
								 .collect(Collectors.summingDouble(p ->p));
						 
						// premiumFc =StringUtils.isBlank(minusSign)?premiumFc:Double.valueOf("-"+premiumFc);
						 
						 premiumFc =StringUtils.isBlank(minusSign)?premiumFc:
							 premiumFc<0 ? premiumFc :Double.valueOf( "-"+premiumFc);
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
								 .map(p ->Double.valueOf(p.getPremiumExcludedTaxFc().toPlainString()))
								 .collect(Collectors.summingDouble(p ->p));
								 
						 premiumLcWithT =pcdList.stream().filter(p->p.getTaxId()==0 )
								 .filter(p ->p.getDiscLoadId()==0)
								 .map(p ->Double.valueOf(p.getPremiumExcludedTaxLc().toPlainString()))
								 .collect(Collectors.summingDouble(p ->p));
					 }else {
						 
						 Double premiumIncWithT =pcdList.stream().filter(p->p.getTaxId()==0 )
								 .filter(p ->p.getDiscLoadId()!=0)
								 .filter(p ->p.getCoverageType().equals("E"))
								 .map(p ->Double.valueOf(p.getPremiumIncludedTaxFc().toPlainString()))
								 .collect(Collectors.summingDouble(p ->p));
						 minusSign =premiumIncWithT<0 ?"-" :"";
						 
						 premiumFcWithT =pcdList.stream().filter(p->p.getTaxId()==0 )
								 .filter(p ->p.getDiscLoadId()!=0)
								 .filter(p ->p.getCoverageType().equals("E"))
								 .map(p ->Double.valueOf(p.getPremiumExcludedTaxFc().toPlainString()))
								 .collect(Collectors.summingDouble(p ->p));
								 
						 premiumLcWithT =pcdList.stream().filter(p->p.getTaxId()==0 )
								 .filter(p ->p.getDiscLoadId()!=0)
								 .filter(p ->p.getCoverageType().equals("E"))
								 .map(p ->Double.valueOf(p.getPremiumExcludedTaxLc().toPlainString()))
								 .collect(Collectors.summingDouble(p ->p));
						 
						// premiumFcWithT =StringUtils.isBlank(minusSign)?premiumFcWithT:Double.valueOf("-"+premiumFcWithT.toString());
						
						 premiumFcWithT =StringUtils.isBlank(minusSign)?premiumFcWithT:
							 premiumFcWithT<0 ? premiumFcWithT :Double.valueOf( "-"+premiumFcWithT);
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
					 	 
					 premiumFc =premiumFcWithT * Double.valueOf(brokerCommision.toPlainString()) /100;
					 premiumLc =premiumLcWithT * Double.valueOf(brokerCommision.toPlainString()) /100;
					 
					 jpqlQuery.updateBrokerCommission(quoteNo, premiumFc);
					 
					docId=hpm.getLoginId();
					narration=c.getChartAccountDesc() +" @ "+brokerCommision.toPlainString();
					bokerCommiCheck=true;
					
				}else if("CR".equalsIgnoreCase(c.getAccountType()) && brokerCommision.doubleValue()>0
						&& "T".equalsIgnoreCase(c.getCharactersticType())) {
												
					 List<PolicyCoverData> pcdList =jpqlQuery.getPolicyCoverDataTax(quoteNo, coverIds);
					 
					 Optional<PolicyCoverData> pcd =null;
							 
					 if(StringUtils.isBlank(endorsmentType)) {		 
							 
						 pcd = pcdList.stream().filter(p->p.getTaxId()!=0 )
							 .filter(p ->p.getDiscLoadId()==0)
							 .findFirst();
					 }else {
						 pcd = pcdList.stream().filter(p->p.getTaxId()!=0 )
								 .filter(p ->p.getDiscLoadId()!=0)
								 .filter(p ->p.getCoverageType().equals("T"))
								 .findFirst();
					 }
					 
					 narration =pcd.isPresent()?pcd.get().getTaxCalcType().equals("P")?c.getChartAccountDesc() +" @ "+pcd.get().getTaxRate().toString()
						 	 :c.getChartAccountDesc() :"";
							
					
					if(endtPremium.doubleValue()<0) {
							drcrFlag="DR";
							documentType="B";
							documentNo =drnumber;
					}else {
						 documentType="B";
						 documentNo =crnumber;
					 } 	 
					 
					BigDecimal taxPer =pcd.isPresent()?pcd.get().getTaxRate():new BigDecimal(1);
					 
					HomePositionMaster hpmm = hpmRepo.findByQuoteNo(quoteNo);
					BigDecimal tax =hpmm.getCommission().multiply(taxPer).divide(new BigDecimal(100));
					premiumFc =Double.valueOf(tax.toPlainString());
					docId=hpm.getLoginId();
					bokerCommiCheck=true;
				}
				
				if(bokerCommiCheck) {
				
					HashMap<String,Object> saveReq =new HashMap<>();
					saveReq.put("PolicyNo", StringUtils.isBlank(policyNo)?hpm.getPolicyNo():policyNo);
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
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	private List<PolicyDrcrDetail> saveDRCR(List<Map<String, Object>> list) {

		try {
			
			return list.stream().map(p ->{
				
				PolicyDrcrDetail drcrDetail = PolicyDrcrDetail.builder()
						.amountFc(p.get("AmountFC")==null?new BigDecimal(0):BigDecimal.valueOf(Double.valueOf(p.get("AmountFC").toString())))
						.amountLc(p.get("AmountFC")==null?new BigDecimal(0):BigDecimal.valueOf(Double.valueOf(p.get("AmountFC").toString())))
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
