package com.maan.eway.salesLead.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.salesLead.bean.EnquiryDetails;

@Repository
public interface EnquiryDetailsRepository extends JpaRepository<EnquiryDetails, String> {

	EnquiryDetails findByEnquiryId(String enquiryId);

	List<EnquiryDetails> findLeadId(String leadId);
}
