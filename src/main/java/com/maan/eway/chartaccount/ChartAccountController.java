package com.maan.eway.chartaccount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;

@RestController
@RequestMapping("/chartaccount")
public class ChartAccountController {
	
	
	@Autowired
	private ChartAccountService service;
	
	
	@GetMapping("/drcr/entry/{quoteNo}")
	public CommonRes drcrEntry(@PathVariable("quoteNo") String quoteNo) {
		return service.drcrEntry(quoteNo,"");
	}

}
