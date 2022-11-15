//StateMasterId
package com.maan.eway.bean;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MotorColorMasterId implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

    private Integer colorId; 
	private Integer amendId;
    private Date effectiveDateStart;
    private Date effectiveDateEnd;

}