package com.maan.eway.claim;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.common.req.EservieMotorDetailsViewRes;
import com.maan.eway.common.req.SearchReq;
import com.maan.eway.common.req.ViewQuoteDetailsReq;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.SearchCustomerDetailsRes;
import com.maan.eway.common.res.SearchROPVehicleDetailsRes;
import com.maan.eway.common.res.SearchROPVehicleRes;
import com.maan.eway.common.res.ViewQuoteDetailsRes;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.common.service.QuoteService;
import com.maan.eway.common.service.impl.SearchServiceImpl;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.req.FactorRateDetailsGetReq;
import com.maan.eway.service.impl.FactorRateRequestDetailsServiceImpl;

@Service
public class ClaimDetailsServiceImpl implements ClaimDetailsService{
	
	@Autowired
	private SearchServiceImpl searchServiceImpl;
	
	@Autowired
	private FactorRateRequestDetailsServiceImpl factorRateRequestDetailsServiceImpl;
	
	@Autowired
	private HomePositionMasterRepository homePositionMasterRepo;
	
	@Autowired
	private  QuoteService quoteService ;
	
	@Autowired
	private MotorDataDetailsRepository motorDataDetailsRepo;

	@Override
	public List<PolicyDetailsResponseDto> policydetailsbyregno(PolicyDetailsReq req) {
		
		List<PolicyDetailsResponseDto> responseList = new ArrayList<PolicyDetailsResponseDto>();
		
		try {
			
			HomePositionMaster home = homePositionMasterRepo.findByPolicyNoAndStatusAndCompanyIdAndProductId(req.getQuotationPolicyNo(),"P",req.getInsuranceId(),5);
			
			// View Quote details
			ViewQuoteDetailsReq quoteReq = new ViewQuoteDetailsReq();
			quoteReq.setInsuranceId(home.getCompanyId());
			quoteReq.setProductId(home.getProductId() != null ? home.getProductId().toString() : "5");
			quoteReq.setQuoteNo(home.getQuoteNo());
			quoteReq.setRequestReferenceNo(home.getRequestReferenceNo());
			ViewQuoteDetailsRes viewQuoteDetails = searchServiceImpl.viewQuoteDetails(quoteReq);
			
			// Customer Details
			SearchReq customerDetailsReq = new SearchReq();
			customerDetailsReq.setApplicationId(home.getApplicationId());
			customerDetailsReq.setBranchCode(home.getBranchCode());
			customerDetailsReq.setInsuranceId(home.getCompanyId());
			customerDetailsReq.setLoginId(home.getLoginId());
			customerDetailsReq.setProductId(home.getProductId() != null ? home.getProductId().toString() : "5");
			customerDetailsReq.setQuoteNo(home.getQuoteNo());
			customerDetailsReq.setRequestReferenceNo(home.getRequestReferenceNo());
			customerDetailsReq.setSearchValue(home.getQuoteNo());
			List<SearchCustomerDetailsRes> adminCustomerSearch = searchServiceImpl.adminCustomerSearch(customerDetailsReq);
			
			// cover Details
			FactorRateDetailsGetReq coverDetailsReq = new FactorRateDetailsGetReq();
			coverDetailsReq.setProductId(home.getProductId() != null ? home.getProductId().toString() : "5");
			coverDetailsReq.setRequestReferenceNo(home.getRequestReferenceNo());
			List<EservieMotorDetailsViewRes> factorRateRequestDetails = factorRateRequestDetailsServiceImpl.getFactorRateRequestDetails(coverDetailsReq,"token");
			
			// vehicle Details
			SearchReq vehicleDetailsReq = new SearchReq();
			vehicleDetailsReq.setInsuranceId(home.getCompanyId());
			vehicleDetailsReq.setProductId(home.getProductId() != null ? home.getProductId().toString() : "5");
			vehicleDetailsReq.setQuoteNo(home.getQuoteNo());
			vehicleDetailsReq.setRequestReferenceNo(home.getRequestReferenceNo());
			SearchROPVehicleDetailsRes adminROPVehicleSearch = searchServiceImpl.adminROPVehicleSearch(vehicleDetailsReq);
			
			//Mapping eway response to claim response
			responseList = claimResponseMapper(viewQuoteDetails,adminCustomerSearch.get(0),factorRateRequestDetails,adminROPVehicleSearch.getVehDetails(),home);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	    return responseList;
	}
	
	private List<PolicyDetailsResponseDto> claimResponseMapper(ViewQuoteDetailsRes viewQuoteDetails,
			SearchCustomerDetailsRes adminCustomerSearch, List<EservieMotorDetailsViewRes> factorRateRequestDetails, List<SearchROPVehicleRes> vehDetails,
			HomePositionMaster home) {
		List<PolicyDetailsResponseDto> resList = new ArrayList<>();

		for (SearchROPVehicleRes veh : vehDetails) {

			PolicyDetailsResponseDto response = new PolicyDetailsResponseDto();

			PolicyInfoDetailsDto policyInfo = new PolicyInfoDetailsDto();

			policyInfo.setPolicyNo(viewQuoteDetails.getPolicyNo());
			policyInfo.setPolicyFrom(viewQuoteDetails.getInceptionDate());
			policyInfo.setPolicyTo(viewQuoteDetails.getExpiryDate());
			policyInfo.setProductDesc(home.getProductName());
			policyInfo.setProductcode(home.getProductId() != null ? home.getProductId().toString() : "5");
			policyInfo.setCustomerCode(viewQuoteDetails.getCustomerCode());
			policyInfo.setContactPerName(viewQuoteDetails.getCustomerName());
			policyInfo.setCivilId(adminCustomerSearch.getIdNumber());
			policyInfo.setSectionCode(home.getSectionId() != null ? home.getSectionId().toString() : null);
			policyInfo.setCurrencyCode(viewQuoteDetails.getCurrency());

			PremiaRiskDetailDto vehicleInfo = new PremiaRiskDetailDto();

			vehicleInfo.setSeats(veh.getSeatingCapacity() != null ? veh.getSeatingCapacity().toString() : null);
			vehicleInfo.setVehicleMakeModel(veh.getResMake() + " / " + veh.getResModel());
			vehicleInfo.setPlateNoCharacter(veh.getResRegNumber());
			vehicleInfo.setChassisNo(veh.getResRegNumber());
			vehicleInfo.setVehicletypedesc(veh.getResBodyType());
			vehicleInfo.setVehtype(veh.getMotorDesc());
			vehicleInfo.setVehmake(veh.getResMake());
			vehicleInfo.setVehbodytype(veh.getResBodyType());
			vehicleInfo.setManufactureyear(
					veh.getResYearOfManufacture() != null ? veh.getResYearOfManufacture().toString() : null);
			vehicleInfo.setVehiclecc(null);
			vehicleInfo.setSeating(veh.getSeatingCapacity() != null ? veh.getSeatingCapacity().toString() : null);
			vehicleInfo.setTonnage(null);
			vehicleInfo.setVechregno(veh.getResRegNumber());

			response.setPolicyInfo(policyInfo);
			response.setVehicleInfo(vehicleInfo);

			resList.add(response);

		}

		return resList;
	}

	@Override
	public ViewQuoteRes claimViewQuoteDetails(PolicyDetailsReq req) {
		
		String quoteNo = "";
		
		if(StringUtils.isNoneBlank(req.getQuotationPolicyNo())) {
			HomePositionMaster home = homePositionMasterRepo.findByPolicyNoAndStatusAndCompanyIdAndProductId(req.getQuotationPolicyNo(),"P",req.getInsuranceId(),5);
			System.out.println("Requet ==> "+req);
			quoteNo = home.getQuoteNo();
		}else if(StringUtils.isNoneBlank(req.getChassisno())) {
			List<MotorDataDetails> motor = motorDataDetailsRepo.findByRegistrationNumberOrderByEntryDateDesc(req.getChassisno());
			if(motor != null && !motor.isEmpty()) {
				quoteNo = motor.get(0).getQuoteNo();
			}
		}
		
		ViewQuoteReq request = new ViewQuoteReq();
		request.setQuoteNo(quoteNo);
		
		return quoteService.viewQuoteDetails(request);
	}
	
}
