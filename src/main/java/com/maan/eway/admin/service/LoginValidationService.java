package com.maan.eway.admin.service;

import java.util.List;

import com.maan.eway.admin.req.AttachBrokerBranchReq;
import com.maan.eway.admin.req.AttachCompaniesReq;
import com.maan.eway.admin.req.AttachCompnayProductRequest;
import com.maan.eway.admin.req.AttachIssuerBrannchReq;
import com.maan.eway.admin.req.AttachIssuerReferalReq;
import com.maan.eway.admin.req.BrokerActiveGridReq;
import com.maan.eway.admin.req.BrokerCreationReq;
import com.maan.eway.admin.req.BrokerDetailsGetReq;
import com.maan.eway.admin.req.BrokerLoginGridReq;
import com.maan.eway.admin.req.IssuerActiveGridReq;
import com.maan.eway.admin.req.IssuerCraeationReq;
import com.maan.eway.admin.req.IssuerDetailsGetReq;
import com.maan.eway.admin.req.IssuerLoginGridReq;
import com.maan.eway.admin.req.UserActiveGridReq;
import com.maan.eway.admin.req.UserCreationReq;
import com.maan.eway.admin.req.UserDetailsGetReq;
import com.maan.eway.admin.req.UserLoginGridReq;
import com.maan.eway.error.Error;

public interface LoginValidationService {

List<Error> validateBrokerCreation(BrokerCreationReq req);
List<Error> validateIssuerCreation(IssuerCraeationReq req);
List<Error> validateUserCreation(UserCreationReq req);
List<Error> validateBrokerBranchReq(AttachCompaniesReq req);
List<Error> validateBrokerProductReq(AttachCompnayProductRequest req);
List<Error> validateIssuerBranchReq(AttachIssuerBrannchReq req);
List<Error> validateIssuerReferalReq(AttachIssuerReferalReq req);
List<Error> validateBrokerCompanyBranchReq(AttachBrokerBranchReq req);


}
