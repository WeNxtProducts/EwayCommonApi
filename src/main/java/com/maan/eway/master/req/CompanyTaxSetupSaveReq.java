package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyTaxSetupSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

    
    
    @JsonProperty("ProductId")
    private String productId ;

    
    @JsonProperty("InsuranceId")
    private String     companyId;
    
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("EffectiveDateStart")
    private Date       effectiveDateStart ;

    
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("EffectiveDateEnd")
    private Date       effectiveDateEnd ;
    
    //--- ENTITY DATA FIELDS 
 
    @JsonProperty("CreatedBy")
    private String   createdBy ;
    
    
    @JsonProperty("CompanyTaxDetails")
    private List<TaxMultiInsertReq> companyTaxDetails ; 
    
    

}
