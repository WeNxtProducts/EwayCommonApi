package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JasperDocumentRes {

	@JsonProperty("PdfOutFilePath")
	private String pdfoutfilepath;
	@JsonProperty("PdfOutFile")
	private String pdfoutfile;
	
}
