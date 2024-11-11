package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PolicyTypeMasterGetReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("PolicyTypeId")
    private String policyTypeId;
    
    @JsonProperty("InsuranceId")
    private String companyId;

   @JsonProperty("ProductId")
   private String productId;

    

}
