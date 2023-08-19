package com.maan.eway.master.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OptionsReq {
	
	@JsonProperty("UwQuesOptionId")
    private String   uwQuesOptionId ; //value
	
	@JsonProperty("UwQuesOptionDesc")
    private String   uwQuesOptionDesc ; //DisplayName
    
	@JsonProperty("Status")
    private String   status ;
    
	@JsonProperty("LoadingPercent")
    private String   loadingPercent ; 
    
	@JsonProperty("DependentYn")
    private String   dependentYn ;
    
	@JsonProperty("DependentUnderwriterId")
    private String   dependentUnderwriterId ; 
	
	@JsonProperty("DependentUwAction")
    private String   dependentUwAction ;
  	
	@JsonProperty("ReferralYn")
    private String  referralYn;
	

	
}
