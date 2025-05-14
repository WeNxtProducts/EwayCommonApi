package com.maan.eway.excess.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.bean.ExcessMaster;
import com.maan.eway.bean.ExcessTransactionDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.excess.req.ExcessDetailsReq;
import com.maan.eway.excess.req.ExcessTransactionReq;
import com.maan.eway.excess.res.ExcessTransactionRes;
import com.maan.eway.excess.service.ExcessTransactionDetailsService;
import com.maan.eway.repository.ExcessMasterRepository;
import com.maan.eway.repository.ExcessTransactionDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;

import groovy.transform.EqualsAndHashCode;
import jakarta.persistence.EntityManager;
import lombok.Data;

@Service
public class ExcessTransactionDetailsServiceImpl implements ExcessTransactionDetailsService {

	@Autowired
	private ExcessTransactionDetailsRepository excesstranRepo;

	@Autowired
	private ExcessMasterRepository excessRepo;

	@Autowired
	private FactorRateRequestDetailsRepository factorRepo;

	@Autowired
	private EntityManager em;

	@Data
	@EqualsAndHashCode
	public class EnquiryDetails {
		private String locationId;
		private String sectionId;
		private String coverId;
		private String riskId;
	}

	// Insert Excess Transaction Details
	@Override
	public CommonRes insertExessTransactionDetails(ExcessTransactionReq req) {
		CommonRes res = new CommonRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<ExcessTransactionDetails> excessTranDetailsList = new ArrayList<ExcessTransactionDetails>();
		List<ExcessTransactionDetails> toDelete = new ArrayList<>();
		try {
			List<FactorRateRequestDetails> factorDatas = factorRepo
					.findByRequestReferenceNo(req.getRequestReferenceNo());
			List<FactorRateRequestDetails> optedDatas = factorDatas.stream()
					.filter(f -> !StringUtils.isBlank(f.getUserOpt()) && f.getUserOpt().equalsIgnoreCase("Y")).toList();
			// Create list of EnquiryDetails (with no duplicates)
			List<EnquiryDetails> enquiryDetailsList = optedDatas.stream().distinct().map(data -> {
				EnquiryDetails enquiry = new EnquiryDetails();
				enquiry.setLocationId(String.valueOf(data.getLocationId()));
				enquiry.setSectionId(String.valueOf(data.getSectionId()));
				enquiry.setCoverId(String.valueOf(data.getCoverId()));
				enquiry.setRiskId(String.valueOf(data.getVehicleId()));
				return enquiry;
			}).distinct().collect(Collectors.toList());

			String companyId = factorDatas.get(0).getCompanyId();
			String productId = String.valueOf(factorDatas.get(0).getProductId());
			List<ExcessTransactionDetails> existTranDatas = excesstranRepo
					.findByRequestReferenceNo(req.getRequestReferenceNo());
			if (existTranDatas.isEmpty()) {
				List<ExcessMaster> excessMasterDatas = excessRepo
						.findAllByCompanyIdAndProductIdOrderByExcessId(companyId, productId);
				if (!excessMasterDatas.isEmpty()) {

					for (EnquiryDetails enquiry : enquiryDetailsList) {
						List<ExcessMaster> em = excessMasterDatas.parallelStream()
								.filter(f -> f.getSectionId().equalsIgnoreCase(enquiry.getSectionId())
										&& f.getCoverId().equals(enquiry.getCoverId()))
								.toList();
						if (!em.isEmpty()) {
							ExcessMaster mas = em.get(0);
							ExcessTransactionDetails map = mapper.map(mas, ExcessTransactionDetails.class);
							map.setRiskId(Integer.parseInt(enquiry.getRiskId()));
							map.setLocationId(enquiry.getLocationId());
							map.setRequestReferenceNo(req.getRequestReferenceNo());
							excessTranDetailsList.add(map);
						}
					}

				}
			} else {
				List<ExcessTransactionDetails> copyList= existTranDatas;
				List<ExcessDetailsReq> excessDetails = req.getExcessDetails();
				for (EnquiryDetails enquiry : enquiryDetailsList) {
					List<ExcessTransactionDetails> exist = existTranDatas.stream()
							.filter(f -> f.getSectionId().equalsIgnoreCase(enquiry.getSectionId())
									&& f.getCoverId().equals(enquiry.getCoverId())
									&& f.getLocationId().equalsIgnoreCase(enquiry.getLocationId())
									&& String.valueOf(f.getRiskId()).equals(enquiry.getRiskId()))
							.toList();

					if (exist.isEmpty()) {
						List<ExcessMaster> excessMasterDatas = excessRepo
								.findAllByCompanyIdAndProductIdOrderByExcessId(companyId, productId);
						if (!excessMasterDatas.isEmpty()) {
							List<ExcessMaster> em = excessMasterDatas.parallelStream()
									.filter(f -> f.getSectionId().equalsIgnoreCase(enquiry.getSectionId())
											&& f.getCoverId().equals(enquiry.getCoverId()))
									.toList();
							if (!em.isEmpty()) {
								ExcessMaster mas = em.get(0);
								ExcessTransactionDetails map = mapper.map(mas, ExcessTransactionDetails.class);
								map.setRiskId(Integer.parseInt(enquiry.getRiskId()));
								map.setLocationId(enquiry.getLocationId());
								map.setRequestReferenceNo(req.getRequestReferenceNo());
								excessTranDetailsList.add(map);
							}
						}
					} else {
						ExcessTransactionDetails tran = exist.get(0);
						if (!excessDetails.isEmpty()) {
							List<ExcessDetailsReq> extractDatasFromReq = excessDetails.stream()
									.filter(f -> f.getSectionId().equalsIgnoreCase(enquiry.getSectionId())
											&& f.getCoverId().equals(enquiry.getCoverId())
											&& f.getLocationId().equalsIgnoreCase(enquiry.getLocationId())
											&& String.valueOf(f.getRiskId()).equals(enquiry.getRiskId()))
									.toList();
							if (!extractDatasFromReq.isEmpty()) {
								ExcessDetailsReq extractexcess = extractDatasFromReq.get(0);
								tran.setExcessAmount(
										StringUtils.isBlank(extractexcess.getExcessAmount()) ? tran.getExcessAmount()
												: Double.valueOf(extractexcess.getExcessAmount()));
								tran.setExcessPercentage(StringUtils.isBlank(extractexcess.getExcessPercentage())
										? tran.getExcessPercentage()
										: Integer.parseInt(extractexcess.getExcessPercentage()));
								tran.setExcessDescription(StringUtils.isBlank(extractexcess.getExcessDescription())
										? tran.getExcessDescription()
										: extractexcess.getExcessDescription());
							}

						}
						copyList.remove(tran);
					}
                      
				}
				
				if(!copyList.isEmpty())
					toDelete.addAll(copyList);
			}
			if (!CollectionUtils.isEmpty(toDelete))
				excesstranRepo.deleteAll(toDelete);

			excesstranRepo.saveAllAndFlush(excessTranDetailsList);

			res.setMessage("Success");
			res.setIsError(false);
			res.setCommonResponse("success");

		} catch (Exception e) {
			e.printStackTrace();
			res.setMessage("Failed");
			res.setIsError(true);
		}
		return res;
	}

	// Get All Transaction Details Based on the RequestReferenceNumber

	@Override
	public CommonRes getExcessTransactionDetails(ExcessTransactionReq req) {
		CommonRes commonRes = new CommonRes();
		List<ExcessTransactionRes> res = new ArrayList<ExcessTransactionRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {

			List<ExcessTransactionDetails> excessTranDetails = excesstranRepo
					.findByRequestReferenceNo(req.getRequestReferenceNo());
			if (!CollectionUtils.isEmpty(excessTranDetails)) {
				for(ExcessTransactionDetails e:excessTranDetails) {
					ExcessTransactionRes et = mapper.map(e, ExcessTransactionRes.class);
					res.add(et);
				}
				commonRes.setCommonResponse(res);
			} else {
				List<FactorRateRequestDetails> factorDatas = factorRepo
						.findByRequestReferenceNo(req.getRequestReferenceNo());
				if (!factorDatas.isEmpty()) {
					String companyId = factorDatas.get(0).getCompanyId();
					String productId = String.valueOf(factorDatas.get(0).getProductId());
					List<ExcessMaster> master = excessRepo.findAllByCompanyIdAndProductIdOrderByExcessId(companyId,
							productId);
					List<EnquiryDetails> enquiryDetailsList = factorDatas.stream().distinct().map(data -> {
						EnquiryDetails enquiry = new EnquiryDetails();
						enquiry.setLocationId(String.valueOf(data.getLocationId()));
						enquiry.setSectionId(String.valueOf(data.getSectionId()));
						enquiry.setCoverId(String.valueOf(data.getCoverId()));
						enquiry.setRiskId(String.valueOf(data.getVehicleId()));
						return enquiry;
					}).distinct().collect(Collectors.toList());
					
					for(EnquiryDetails enquiry:enquiryDetailsList) {
						List<ExcessMaster> matched = master.stream().filter(m-> m.getSectionId().equalsIgnoreCase(enquiry.getSectionId()) && m.getCoverId().equalsIgnoreCase(enquiry.getCoverId())).toList();
						if(!matched.isEmpty()) {
						ExcessMaster mas = matched.get(0);
						ExcessTransactionRes singleRes = mapper.map(mas, ExcessTransactionRes.class);
						res.add(singleRes);
						}
					}
					
				}
				commonRes.setCommonResponse(res);
			}
			if (CollectionUtils.isNotEmpty(res)) {
				commonRes.setMessage("Success");
				commonRes.setIsError(false);
			} else {
				commonRes.setMessage("No data");
				commonRes.setIsError(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			commonRes.setMessage("Failed");
			commonRes.setIsError(true);
		}
				return commonRes;
	}

}
