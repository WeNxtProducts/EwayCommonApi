package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.common.req.NcdDetailsGetReq;
import com.maan.eway.integration.req.PremiaRequest;
import com.maan.eway.integration.req.QueryKeyReq;
import com.maan.eway.master.req.BrokerSumInsuredRefReq;
import com.maan.eway.master.req.BuildingUsageDropDownReq;
import com.maan.eway.master.req.CityDropDownReq;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.master.req.LovPolicyDropDownReq;
import com.maan.eway.master.req.RegionDropDownReq;
import com.maan.eway.master.req.RelationDropDownReq;
import com.maan.eway.master.req.StateDropDownReq;
import com.maan.eway.res.ColummnDropRes;
import com.maan.eway.res.DropDownRes;

public interface DropDownService {

	
	
	List<DropDownRes> coverNoteType(LovDropDownReq req);

	List<DropDownRes> paymentmode(LovDropDownReq req);

	List<DropDownRes> endorsementtype(LovDropDownReq req);

	List<DropDownRes> discounttypeoffered(LovDropDownReq req);

	List<DropDownRes> taxexcempted(LovDropDownReq req);

	List<DropDownRes> taxexcemptiontype(LovDropDownReq req);

	List<DropDownRes> policyholdertype(LovDropDownReq req);

	List<DropDownRes> policyholderidtype(LovPolicyDropDownReq req);

	List<DropDownRes> policyholdergender(LovDropDownReq req);

	List<DropDownRes> nametitle(LovDropDownReq req);

	List<DropDownRes> notificationtype(LovDropDownReq req);

	List<DropDownRes> getMotorCategory(LovDropDownReq req);

	List<DropDownRes> getMotorType(LovDropDownReq req);

	//List<DropDownRes> getMotorUsage(LovDropDownReq req);

	List<DropDownRes> ownerCategory(LovDropDownReq req);

	List<DropDownRes> fleetType(LovDropDownReq req);

	List<DropDownRes> reinsuranceCategory(LovDropDownReq req);

	List<DropDownRes> participantType(LovDropDownReq req);

	List<DropDownRes> reinsuranceForm(LovDropDownReq req);

	List<DropDownRes> reinsuranceType(LovDropDownReq req);

	List<DropDownRes> claimformdullyfilled(LovDropDownReq req);

	List<DropDownRes> lostassessmentoption(LovDropDownReq req);

	List<DropDownRes> assessoridtype(LovDropDownReq req);

	List<DropDownRes> claimantCategory(LovDropDownReq req);

	List<DropDownRes> claimantType(LovDropDownReq req);

	List<DropDownRes> claimantIdType(LovDropDownReq req);

	List<DropDownRes> isreassessment(LovDropDownReq req);

	List<DropDownRes> offerAccepted(LovDropDownReq req);

	List<DropDownRes> partiesNotified(LovDropDownReq req);

	List<DropDownRes> claimResultedLitigation(LovDropDownReq req);

	List<DropDownRes> tonnage(LovDropDownReq req);

	List<DropDownRes> getNcdDetails(NcdDetailsGetReq req);

	List<DropDownRes> insuranceType(LovDropDownReq req);

	List<DropDownRes> insuranceClass(LovDropDownReq req);

	List<DropDownRes> title(LovDropDownReq req);

	List<DropDownRes> borrowerType(LovDropDownReq req);

	List<DropDownRes> cityLimit(LovDropDownReq req);

	List<DropDownRes> getLanguage(LovDropDownReq req);

	List<DropDownRes> getFuelType(LovDropDownReq req);

//	List<ColummnDropRes> getTableDetails(LovDropDownReq req);

	List<DropDownRes> constructType(LovDropDownReq req);

	List<DropDownRes> consecutiveDays(LovDropDownReq req);

	List<DropDownRes> buildingType(LovDropDownReq req);

	List<DropDownRes> getPlanType(LovDropDownReq req);

	List<DropDownRes> getSourceType(LovDropDownReq req);

	List<DropDownRes> getRelationType(RelationDropDownReq req);

	List<DropDownRes> getBuildingPurpose(LovDropDownReq req);

	List<DropDownRes> getBuildingUsage(LovDropDownReq req);

	List<DropDownRes> getPaymentType(LovDropDownReq req);

	List<DropDownRes> getContent(LovDropDownReq req);

	List<DropDownRes> getPersonal(LovDropDownReq req);

	List<DropDownRes> getcontentrisk(LovDropDownReq req);

	List<DropDownRes> getallrisk(LovDropDownReq req);

	List<DropDownRes> datatype(LovDropDownReq req);

	List<DropDownRes> termsandcondition(LovDropDownReq req);

	List<DropDownRes> categoryid(LovDropDownReq req);

	List<DropDownRes> benefitcovermonth(LovDropDownReq req);

	List<DropDownRes> quoteStatus(LovDropDownReq req);

	List<DropDownRes> termsType(LovDropDownReq req);

	List<DropDownRes> driverType(LovDropDownReq req);

	List<DropDownRes> getallelectronicItems(LovDropDownReq req);

	List<DropDownRes> getQueryKeyColumns(QueryKeyReq req);

	List<DropDownRes> industryCategory(LovDropDownReq req);

	List<DropDownRes> followupDetailsStatus(LovDropDownReq req);

	List<DropDownRes> endtDependantFields(LovDropDownReq req);

	List<DropDownRes> productBenefitsTypes(LovDropDownReq req);

	List<DropDownRes> brokerSumInsuredRefrral(BrokerSumInsuredRefReq req);

	List<DropDownRes> getWallTypes(LovDropDownReq req);

	List<DropDownRes> getRoofTypes(LovDropDownReq req);

	List<DropDownRes> getAuditentType(LovDropDownReq req);

	List<DropDownRes> getFidelityEmployeeCount(LovDropDownReq req);

	List<DropDownRes> getFidelitySuminsured(LovDropDownReq req);

	List<DropDownRes> getNatureOfTrade(LovDropDownReq req);

	List<DropDownRes> getBurglaryInsuranceFor(LovDropDownReq req);

	List<DropDownRes> getCeilingType(LovDropDownReq req);

	List<DropDownRes> getWindowsMaterials(LovDropDownReq req);

	List<DropDownRes> getDoorsMaterials(LovDropDownReq req);

	List<DropDownRes> getNightLeftDoor(LovDropDownReq req);

	List<DropDownRes> getBuildingOccupied(LovDropDownReq req);

	List<DropDownRes> getOpenoption(LovDropDownReq req);

	List<DropDownRes> getFirstLossPercent(LovDropDownReq req);



}
