package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TiraRes {



	@JsonProperty("QuoteNo")
	private String quoteNo;
    
	@JsonProperty("StickerNo")
	private String stickerNo;	
	
	@JsonProperty("CoverNoteNo")
	private String coverNoteNo;	
	
	@JsonProperty("TiraTrackingDetails")
	private List<TiraResList> tiraTrackingDetails;
} 
