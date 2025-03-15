package com.maan.eway.bean;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ExcessMasterId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer excessId;
	private String companyId;
	private String productId;
	private String sectionId;
	private Integer amendId;

}
