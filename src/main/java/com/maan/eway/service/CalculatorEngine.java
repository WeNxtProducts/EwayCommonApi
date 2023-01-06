package com.maan.eway.service;

import java.util.List;

import javax.persistence.Tuple;

import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.req.calcengine.CalcCommission;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.DebitAndCredit;

public interface CalculatorEngine {
	//void LoadSection(CalcEngine engine) ;
	List<Tuple> LoadCover(CalcEngine engine) ;
	 
	EserviceMotorDetailsSaveRes  calculator(CalcEngine engine,String token);
	EserviceMotorDetailsSaveRes referalCalculator(CalcEngine request);
	 List<DebitAndCredit> commissionCalc(CalcCommission request);
}
