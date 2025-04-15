package com.maan.eway.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class QuoteInformationId implements Serializable {
	
	 private static final long serialVersionUID = 1L;	
    private String enquiryId;
    private String quoteNo;

    // Constructors, equals, and hashCode methods
}
