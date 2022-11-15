package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.common.req.NcdDetailsGetReq;
import com.maan.eway.master.dropdown.req.CityDropDownReq;
import com.maan.eway.master.dropdown.req.RegionDropDownReq;
import com.maan.eway.master.dropdown.req.StateDropDownReq;
import com.maan.eway.req.SubUserTypeReq;
import com.maan.eway.res.DropDownRes;

public interface DropDownService {

	
	
	List<DropDownRes> coverNoteType();

	List<DropDownRes> paymentmode();

	List<DropDownRes> endorsementtype();

	List<DropDownRes> discounttypeoffered();

	List<DropDownRes> taxexcempted();

	List<DropDownRes> taxexcemptiontype();

	List<DropDownRes> policyholdertype();

	List<DropDownRes> policyholderidtype();

	List<DropDownRes> policyholdergender();

	List<DropDownRes> nametitle();

	List<DropDownRes> notificationtype();

	List<DropDownRes> getCountryDropdown();

	List<DropDownRes> getRegionDropdown(RegionDropDownReq req);

	List<DropDownRes> getStateDropdown(StateDropDownReq req);

	List<DropDownRes> getCityDropdown(CityDropDownReq req);

	List<DropDownRes> getMotorCategory();

	List<DropDownRes> getMotorType();

	List<DropDownRes> getMotorUsage();

	List<DropDownRes> ownerCategory();

	List<DropDownRes> fleetType();

	List<DropDownRes> reinsuranceCategory();

	List<DropDownRes> participantType();

	List<DropDownRes> reinsuranceForm();

	List<DropDownRes> reinsuranceType();

	List<DropDownRes> claimformdullyfilled();

	List<DropDownRes> lostassessmentoption();

	List<DropDownRes> assessoridtype();

	List<DropDownRes> claimantCategory();

	List<DropDownRes> claimantType();

	List<DropDownRes> claimantIdType();

	List<DropDownRes> isreassessment();

	List<DropDownRes> offerAccepted();

	List<DropDownRes> partiesNotified();

	List<DropDownRes> claimResultedLitigation();

	List<DropDownRes> tonnage();

	List<DropDownRes> getNcdDetails(NcdDetailsGetReq req);

	List<DropDownRes> insuranceType();

	List<DropDownRes> insuranceClass();

	List<DropDownRes> title();

	List<DropDownRes> borrowerType();

	List<DropDownRes> cityLimit();

	List<DropDownRes> getLanguage();

	List<DropDownRes> getFuelType();

}
