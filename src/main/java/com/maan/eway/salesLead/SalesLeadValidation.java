package com.maan.eway.salesLead;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.maan.eway.error.Error;

@Service
public class SalesLeadValidation {

	public List<Error> insertLeadContactVali(List<InsertSalesReq> req) {
		List<Error> errors = new ArrayList<Error>();
		try {
			for(int i=0;i<req.size();i++) {
				if(StringUtils.isBlank(req.get(i).getClientName())) {
					errors.add(new Error("01", "ClientName", "ClientName is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getClientCode())) {
					errors.add(new Error("02", "ClientCode", "ClientCode is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getAddress1())) {
					errors.add(new Error("03", "Address1", "Address1 is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getAddress2())) {
					errors.add(new Error("04", "Address2", "Address2 is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getState())) {
					errors.add(new Error("05", "State", "State is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getCity())) {
					errors.add(new Error("06", "City", "City is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getGstIdentificationNo())) {
					errors.add(new Error("07", "GST Identification Number", "GST Identification Number is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getBranchCode())) {
					errors.add(new Error("08", "RSA Branch", "RSA Branch is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getLeadCreatedOn())) {
					errors.add(new Error("09", "Lead Created On", "Lead Created On is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getIntermediateId())) {
					errors.add(new Error("10", "Intermediary Code", "Intermediary Code is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getIntermediateName())) {
					errors.add(new Error("11", "Intermediary Name", "Intermediary Name is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getChannelId())) {
					errors.add(new Error("12", "Channel", "Channel is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getSectionTypeId())) {
					errors.add(new Error("13", "Section Type", "Section Type is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getPropobabilityOfSuccessId())) {
					errors.add(new Error("14", "Probability Of Success", "Probability Of Success  is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getTypeOfBusinessId())) {
					errors.add(new Error("15", "Type of Business", "Type of Business is Required"));
				}
				if(StringUtils.isBlank(req.get(i).getCurrentInsurer())) {
					errors.add(new Error("16", "Current Insurer", "Current Insurer is Required"));
				}
				
				if(req.get(i).getLeadContactPersonReq()!=null && req.get(i).getLeadContactPersonReq().size()>0) {
					for(int j=0;j<req.get(i).getLeadContactPersonReq().size();j++) {
						LeadContactPersonReq k = req.get(i).getLeadContactPersonReq().get(j);
						if(StringUtils.isBlank(k.getContactType())) {
							errors.add(new Error("01", "ContactType", "ContactType is Required"));
						}
						if(StringUtils.isBlank(k.getContactPersonName())) {
							errors.add(new Error("02", "Contact Person Name", "Contact Person Name is Required"));
						}
						if(StringUtils.isBlank(k.getEmailAddress())) {
							errors.add(new Error("03", "Email Address", "Email Address  is Required"));
						}
						if(StringUtils.isBlank(k.getMobileNo())) {
							errors.add(new Error("04", "Mobile No", "Mobile No is Required"));
						}
						if(StringUtils.isBlank(k.getPhoneNo())) {
							errors.add(new Error("05", "Phone No", "Phone No is Required"));
						}
						if(StringUtils.isBlank(k.getDesignation())) {
							errors.add(new Error("06", "Designation", "Designation is Required"));
						}
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return errors;
	}

}
