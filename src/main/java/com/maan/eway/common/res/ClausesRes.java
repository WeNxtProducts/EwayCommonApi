package com.maan.eway.common.res;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClausesRes {

@JsonProperty("ClausesId")
private String clausesId;

@JsonProperty("ClausesDesc")
private String clausesDesc;

@JsonProperty("DocRefNo")
private String docRefNo;

@JsonProperty("DocumentId")
private String documentId;
@JsonProperty("ProductId")
private String productId;


@JsonProperty("SectionId")
private String sectionId;

}
