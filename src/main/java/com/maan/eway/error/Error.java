
package com.maan.eway.error;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Error {

	@JsonProperty("Code")
    private String code;
	@JsonProperty("Field")
    private String field;
	@JsonProperty("Message")
    private String message;
}
