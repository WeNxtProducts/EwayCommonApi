package com.maan.eway.notif.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.MailDetails;
import com.maan.eway.notif.req.MailDetailsSaveReq;
import com.maan.eway.notif.service.MailDetailsService;
import com.maan.eway.repository.MailDetailsRepository;
import com.maan.eway.res.SuccessRes;

@Service
@Transactional
public class MailDetailsServiceImpl implements MailDetailsService {

	private Logger log = LogManager.getLogger(MailDetailsServiceImpl.class);

	@Autowired
	private MailDetailsRepository mailrepo;

	public SuccessRes maildetails(MailDetailsSaveReq req) {
		SuccessRes res = new SuccessRes();
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmssSSS");
		ModelMapper mapper = new ModelMapper();
		try {
			MailDetails mail = new MailDetails();
			Date today = new Date();
			String id = sdf.format(today);
			mapper.map(req, MailDetails.class);
			mail.setId(id);
			res.setResponse("Successfully Inserted");
			res.setSuccessId(id);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return res;
	}

}
