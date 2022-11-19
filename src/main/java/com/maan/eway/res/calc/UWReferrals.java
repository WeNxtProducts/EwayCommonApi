package com.maan.eway.res.calc;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UWReferrals implements Serializable {
	@JsonProperty("QuestionId")
	private Integer    uwQuestionId ;

    //--- ENTITY DATA FIELDS 
	@JsonProperty("QuestionDesc")
    private String     uwQuestionDesc ;

	@JsonProperty("QuestionType")
    private String     questionType ;

	@JsonProperty("Value")
    private String     value ;
}
