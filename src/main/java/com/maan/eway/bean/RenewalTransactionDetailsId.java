package com.maan.eway.bean;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RenewalTransactionDetailsId implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tranId;
}
