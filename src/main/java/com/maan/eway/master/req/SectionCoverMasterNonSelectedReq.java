package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SectionCoverMasterNonSelectedReq implements Serializable {

    private static final long serialVersionUID = 1L;
    @JsonProperty("InsuranceId")
    private String companyId;
    
    @JsonProperty("ProductId")
    private String productId;
  
    @JsonProperty("SectionId")
    private String sectionId;
  

    @JsonProperty("Limit")
    private String limit;
    
    @JsonProperty("Offset")
    private String offset;
    
    @JsonFormat(pattern ="dd/MM/yyyy")
    @JsonProperty("EffectiveDateStart")
    private Date effectiveDateStart;
  

}
