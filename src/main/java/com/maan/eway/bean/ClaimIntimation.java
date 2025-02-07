package com.maan.eway.bean;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "claim_intimation")
@IdClass(ClaimIntimationId.class)
@NoArgsConstructor
@Getter
@Setter
public class ClaimIntimation {
	//Entity Primary Key
		@Id
		@Column(name = "CLAIM_REFERENCE_NO")
		private Integer claimReferenceNo;
		
		@Id
		@Column(name = "COMPANY_ID")
		private String companyId;
		
		@Id
		@Column(name = "PRODUCT_ID")
		private String productId;
		
		@Id
		@Column(name = "AMEND_ID")
		private Integer amendId;
		
		@Column(name = "POLICY_NUMBER")
		private String policyNumber; 

		@Column(name = "VEHICLE_REGISTRATION_NUMBER")
		private String vehicleRegistrationNumber;

		@Column(name = "DATE_OF_LOSS")
		private LocalDate dateOfLoss;

		@Column(name = "PLACE_OF_LOSS")
		private String placeOfLoss;

		@Column(name = "DATE_OF_NOTIFICATION")
		private LocalDate dateOfNotification; 

		@Column(name = "NATURE_OF_DAMAGE")
		private String natureOfDamage;

		@Column(name = "RESERVE_AMOUNT")
		private double reserveAmount;

		@Column(name = "CURRENT_LOCATION_OF_VEHICLE")
		private String currentLocationOfVehicle;
		
		@Column(name = "STATUS")
		private String status;

		@Column(name = "EFFECTIVE_START_DATE")
		private LocalDate effectiveStartDate;

		@Column(name = "EFFECTIVE_END_DATE")
		private LocalDate effectiveEndDate;

		@Column(name = "CREATED_BY")
		private String createdBy;


		
}
