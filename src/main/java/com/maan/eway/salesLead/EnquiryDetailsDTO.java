package com.maan.eway.salesLead;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnquiryDetailsDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @JsonProperty("EnquiryId")
    private String enquiryId;
    
    @JsonProperty("LeadId")
    private String leadId;
    
    @JsonProperty("EnquiryDescription")
    private String enquiryDescription;
    
    @JsonProperty("LobId")
    private String lobId;
    
    @JsonProperty("ProductId")
    private String productId;
    
    @JsonProperty("SumInsured")
    private Double sumInsured;
    
    @JsonProperty("SuggestPremium")
    private Double suggestPremium;
    
    @JsonProperty("EntryDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date entryDate;
    
    @JsonProperty("CreatedBy")
    private String createdBy;
    
    @JsonProperty("UpdatedDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date updatedDate;
    
    @JsonProperty("UpdatedBy")
    private String updatedBy;
    
    @JsonProperty("RejectedDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date rejectedDate;
    
    @JsonProperty("RejectedReason")
    private String rejectedReason;
    
    @JsonProperty("Status")
    private String status;
    
    @JsonProperty("QuoteNo")
    private String quoteNo;
}