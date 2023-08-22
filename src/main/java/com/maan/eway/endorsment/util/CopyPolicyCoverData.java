package com.maan.eway.endorsment.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PolicyCoverDataEndt;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PolicyCoverDataEndtRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;

@Service
public class CopyPolicyCoverData {
	
	@Autowired
	private PolicyCoverDataRepository policyCoverRepo;

	@Autowired
	private HomePositionMasterRepository homeRepo;
	
	@Autowired
	private PolicyCoverDataEndtRepository policyCoverEndtRepo;
	
	public Map<String,Object> copy(String policyNo){
		try {
			HomePositionMaster hpmData = homeRepo.findByPolicyNo(policyNo);
			boolean isBasePolicy=false;
			String quoteNo=hpmData.getEndtPrevQuoteNo();
			if(StringUtils.isBlank(hpmData.getEndtTypeId())){
				isBasePolicy=true;
				quoteNo=hpmData.getQuoteNo();
			} 
			List<String> coverageTypes=new ArrayList<String>();			
			List<PolicyCoverData> datas=null;
			if(isBasePolicy) {
				coverageTypes.add("B");
				coverageTypes.add("O");				 
			}else {
				coverageTypes.add("E");
			}
			
			datas= policyCoverRepo.findByQuoteNoAndStatusAndCoverageTypeIn(quoteNo,"Y",coverageTypes);
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			List<PolicyCoverDataEndt> mappedData = datas.stream().map( m->{ 
				PolicyCoverDataEndt map = dozerMapper.map(m, PolicyCoverDataEndt.class);
				map.setPolicyNo(StringUtils.isNotBlank(hpmData.getOriginalPolicyNo())?hpmData.getOriginalPolicyNo():hpmData.getPolicyNo());
				return map;
					
			}  ) .collect(Collectors.toList());
			
			policyCoverEndtRepo.saveAll(mappedData);
		}catch (Exception e) {
			
			e.printStackTrace();
		}
		return null;
		
	}
}
