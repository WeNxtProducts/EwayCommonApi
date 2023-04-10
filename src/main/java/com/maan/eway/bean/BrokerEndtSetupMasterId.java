package com.maan.eway.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BrokerEndtSetupMasterId implements Serializable{

	private static final long serialVersionUID =1L;
	
	private String loginId;
	private String productId;
	private Integer amendId;
	private String endtTypes;
	private String endtSetupId;
	private String companyId;

}
