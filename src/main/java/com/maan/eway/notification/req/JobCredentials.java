package com.maan.eway.notification.req;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobCredentials {

	private String host; 
	private Long port;
	private Boolean isSSL;
	private String username;
	private String password;
	
	

}
