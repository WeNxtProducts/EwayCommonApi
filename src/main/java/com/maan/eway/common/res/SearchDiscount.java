package com.maan.eway.common.res;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDiscount implements Serializable {
    @JsonProperty("DiscountId") 
    private String discountId;
    @JsonProperty("DiscountDesc") 
    private String discountDesc;
    @JsonProperty("DiscountRate") 
    private String discountRate;
    @JsonProperty("DiscountAmount") 
    private BigDecimal discountAmount;
   }
