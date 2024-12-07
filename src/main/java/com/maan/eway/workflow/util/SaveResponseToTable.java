package com.maan.eway.workflow.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.referal.MasterReferal;
import com.maan.eway.service.FactorRateRequestDetailsService;
import com.maan.eway.workflow.dto.WorkEngine;

import io.jsonwebtoken.lang.Collections;

@Service
public class SaveResponseToTable {
	@Autowired
	private FactorRateRequestDetailsService fservice;

	public void saveIntoFactorRequestTable(Map<String, Object> response, WorkEngine engine, Map<String, Object> request) {
		try {
			
			List<MasterReferal> masterreferral=null;
			
			Map<String, Object> data=(Map<String, Object>) response.get("data");
			
			if(data.get("summaryDetails")!=null || 	data.get("errorDetailsList")!=null) {
				masterreferral=new ArrayList<MasterReferal>();
				if(data.get("summaryDetails")!=null) {
					if(data.get("brResults")!=null) {
						List<Map<String, Object>> brResults=(List<Map<String, Object>>) data.get("brResults");
						for (Map<String, Object> map : brResults) {
							MasterReferal master = MasterReferal.builder().isreferral(true).referralDesc(map.get("message").toString()).build();
							masterreferral.add(master);
						}
					}
				}
				if(data.get("errorDetailsList")!=null) {
					List<Map<String, Object>> errorDetailsList=(List<Map<String, Object>>) data.get("errorDetailsList");
					for (Map<String, Object> map : errorDetailsList) {
						MasterReferal master = MasterReferal.builder().isreferral(true).referralDesc(map.get("errorDescription").toString()).build();
						masterreferral.add(master);
					}
				}
				
				
			}
			
			Map<String, Object> object5 =null;
			try {

				Map<String, Object> object1 = (Map<String, Object>) response.get("data");
				Map<String, Object> object2 = (Map<String, Object>) object1.get("riskInfo");
				Map<String, Object> object3 = (Map<String, Object>) object2.get("riskDetails");
				Map<String, Object> object4 = (Map<String, Object>) object3.get("riskDetailsArray");
				object5=(Map<String, Object>) object4.get("coverages");
			}catch (Exception e) {
				e.printStackTrace();
			}
			List<Cover> retc=new ArrayList<Cover>();
			
			if(object5!=null && object5.get("mandatoryCoveragesArray")!=null) {
				List<Map<String, Object>> mandatory=(List<Map<String, Object>>) object5.get("mandatoryCoveragesArray");
				if(mandatory.size()>0) {
					CoverFromAzentoResponse c=new CoverFromAzentoResponse("B");
					List<Cover> mainCover = mandatory.stream().map(c).collect(Collectors.toList());
					retc.addAll(mainCover);
				}
			}
			if(object5!=null && object5.get("selectedOptionalCoveragesArray")!=null) {
				List<Map<String, Object>> optional=(List<Map<String, Object>>) object5.get("selectedOptionalCoveragesArray");
				if(optional.size()>0) {
					CoverFromAzentoResponse c=new CoverFromAzentoResponse("O");
					List<Cover> optinalC = optional.stream().map(c).collect(Collectors.toList());
					retc.addAll(optinalC);
				}
			}
			
			
			
			
			EserviceMotorDetailsSaveRes resp = new EserviceMotorDetailsSaveRes();
			
			resp.setCoverList(retc);
			resp.setResponse("Saved Successfully");
			resp.setRequestReferenceNo(engine.getRequestReferenceNo());
			// response.setCustomerReferenceNo(req.getCustomerReferenceNo());
			resp.setVehicleId(engine.getVehicleId());
			resp.setVdRefNo(engine.getVdRefNo());
			resp.setCdRefNo(engine.getCdRefNo());
			resp.setInsuranceId(engine.getCompanyId());
			resp.setSectionId(engine.getSectionId());
			resp.setCreatedBy(engine.getCreatedBy());
			resp.setProductId(engine.getProductId());
			resp.setLocationId(engine.getLocationId());
			resp.setMsrefno(engine.getMsrefno());
			resp.setUpdateas(null);
			resp.setUwList(null);
			resp.setReferals(masterreferral);
			fservice.saveFactorRateRequestDetails(resp);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
