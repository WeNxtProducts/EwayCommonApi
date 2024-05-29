package com.maan.eway.repository;

import com.maan.eway.bean.EnquiryDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnquiryDetailsRepository extends JpaRepository<EnquiryDetails, String> {

	EnquiryDetails findByEnquiryId(String enquiryId);
}
