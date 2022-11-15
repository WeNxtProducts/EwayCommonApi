package com.maan.eway.bean;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SmsMasterId implements Serializable {

	private static final long serialVersionUID=1L;
	private Integer sNo;
	private String companyId;
	private Date effectiveDateStart;
	private Date effectiveDateEnd;
}
