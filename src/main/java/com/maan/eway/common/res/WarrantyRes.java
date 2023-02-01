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
public class WarrantyRes {

@JsonProperty("SubId")
private String subId;

@JsonProperty("SubIdDesc")
private String subIdDesc;

@JsonProperty("DocRefNo")
private String docRefNo;

@JsonProperty("DocumentId")
private String documentId;

}
