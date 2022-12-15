package com.maan.eway.service;

import java.util.List;

import javax.persistence.Tuple;

import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.common.req.UpdateFactorRateReq;
import com.maan.eway.req.calcengine.CalcEngine;

public interface CalculatorEngine {
	void LoadSection(CalcEngine engine) ;
	List<Tuple> LoadCover(CalcEngine engine) ;
	 
	EserviceMotorDetailsSaveRes  calculator(CalcEngine engine);
	EserviceMotorDetailsSaveRes referalCalculator(CalcEngine request);
}
