package com.maan.eway.service;

import java.util.List;

import javax.persistence.Tuple;

import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;

public interface CalculatorEngine {
	void LoadSection(CalcEngine engine) ;
	List<Tuple> LoadCover(CalcEngine engine) ;
	 
	List<Cover>  calculator(CalcEngine engine);
}
