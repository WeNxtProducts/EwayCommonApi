/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2022-11-19 ( Date ISO 2022-11-19 - Time 13:30:12 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2022-11-19 ( 13:30:12 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.bean;


import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import java.util.Date;
import jakarta.persistence.*;




/**
* Domain class for entity "EserviceMotorDetails"
*
* @author Telosys Tools Generator
*
*/
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
//@Builder
@IdClass(EserviceMotorDetailsId.class)
@Table(name="eservice_motor_details")


public class EserviceMotorDetails implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="CUSTOMER_REFERENCE_NO", nullable=false, length=20)
    private String     customerReferenceNo ;

    @Id
    @Column(name="REQUEST_REFERENCE_NO", nullable=false, length=20)
    private String     requestReferenceNo ;

    @Id
    @Column(name="ID_NUMBER", nullable=false, length=20)
    private String     idNumber ;

    @Id
    @Column(name="RISK_ID", nullable=false)
    private Integer    riskId ;
    
    @Column(name="LOCATION_ID")
    private Integer    locationId ;

    @Column(name="INSURANCE_TYPE", length=10)
    private String     insuranceType ;

    //--- ENTITY DATA FIELDS 
    @Column(name="ACCIDENT", length=1)
    private String     accident ;

    @Column(name="GPS_TRACKING_INSTALLED", length=1)
    private String     gpsTrackingInstalled ;

    @Column(name="WIND_SCREEN_COVER_REQUIRED", nullable=false, length=1)
    private String     windScreenCoverRequired ;

    @Column(name="MANUFACTURE_YEAR", length=10)
    private String     manufactureYear ;

    @Column(name="INSURANCE_TYPE_DESC", length=100)
    private String     insuranceTypeDesc ;

    @Column(name="POLICY_NO", length=100)
    private String     policyNo;

    
    
    @Column(name="MOTOR_CATEGORY", length=1)
    private String     motorCategory ;

    @Column(name="MOTOR_CATEGORY_DESC", length=100)
    private String     motorCategoryDesc ;

    @Column(name="MOTOR_USAGE", length=100)
    private String     motorUsage ;

    @Column(name="REGISTRATION_NUMBER", length=20)
    private String     registrationNumber ;

    @Column(name="ACTUAL_PREMIUM_LC")
    private BigDecimal     actualPremiumLc ;

    @Column(name="CHASSIS_NUMBER", length=20)
    private String     chassisNumber ;

    @Column(name="ACTUAL_PREMIUM_FC")
    private BigDecimal     actualPremiumFc ;

    @Column(name="VEHICLE_MAKE", length=20)
    private String     vehicleMake ;

    @Column(name="OVERALL_PREMIUM_LC")
    private BigDecimal     overallPremiumLc ;

    @Column(name="VEHICLE_MAKE_DESC", length=100)
    private String     vehicleMakeDesc ;

    @Column(name="OVERALL_PREMIUM_FC")
    private BigDecimal     overallPremiumFc ;

    @Column(name="VEHCILE_MODEL", length=20)
    private String     vehcileModel ;

    @Column(name="VEHCILE_MODEL_DESC", length=100)
    private String     vehcileModelDesc ;

    @Column(name="VEHICLE_TYPE", length=100)
    private String     vehicleType ;

    @Column(name="VEHICLE_TYPE_DESC", length=100)
    private String     vehicleTypeDesc ;

    @Column(name="MODEL_NUMBER", length=20)
    private String     modelNumber ;

    @Column(name="ENGINE_NUMBER", length=20)
    private String     engineNumber ;

    @Column(name="FUEL_TYPE", length=20)
    private String     fuelType ;

    @Column(name="FUEL_TYPE_DESC", length=100)
    private String     fuelTypeDesc ;

//    @Column(name="OVERRIDE_PERCENTAGE")
//    private BigDecimal     overridePercentage ;

    @Temporal(TemporalType.DATE)
    @Column(name="REGISTRATION_YEAR")
    private Date    registrationYear ;

    @Column(name="SEATING_CAPACITY")
    private Integer    seatingCapacity ;

    @Column(name="CUBIC_CAPACITY")
    private BigDecimal     cubicCapacity ;

    @Column(name="COLOR", length=100)
    private String     color ;

    @Column(name="COLOR_DESC", length=100)
    private String     colorDesc ;

    @Column(name="GROSS_WEIGHT")
    private BigDecimal     grossWeight ;

    @Column(name="TARE_WEIGHT")
    private BigDecimal     tareWeight ;

//    @Column(name="COVERNOTE_NO", length=20)
//    private String     covernoteNo ;
//
//    @Column(name="STICKER_NO", length=20)
//    private String     stickerNo ;

    @Column(name="PERIOD_OF_INSURANCE", length=20)
    private String     periodOfInsurance ;

    @Column(name="WIND_SCREEN_SUM_INSURED")
    private BigDecimal     windScreenSumInsured ;

    @Column(name="ACCCESSORIES_SUM_INSURED")
    private BigDecimal     acccessoriesSumInsured ;

//    @Column(name="ACCESSORIES_INFORMATION", length=200)
//    private String     accessoriesInformation ;

    @Column(name="NUMBER_OF_AXELS")
    private Integer    numberOfAxels ;

    @Column(name="AXEL_DISTANCE")
    private BigDecimal     axelDistance ;

    @Column(name="SUM_INSURED")
    private BigDecimal     sumInsured ;

    @Column(name="ENDORSEMENT_TYPE")
    private Integer    endorsementType ;

    @Column(name="ENDORSEMENT_TYPE_DESC", length=100)
    private String     endorsementTypeDesc ;

    @Column(name="TPPD_FREE_LIMIT")
    private BigDecimal     tppdFreeLimit ;

    @Column(name="TPPD_INCREAE_LIMIT")
    private BigDecimal     tppdIncreaeLimit ;

    @Column(name="SPECIAL_TERMS_OF_PREMIUM", length=5)
    private String     specialTermsOfPremium ;

    @Column(name="BRANCH_CODE", nullable=false, length=20)
    private String     branchCode ;

    @Column(name="AGENCY_CODE", nullable=false, length=20)
    private String     agencyCode ;

    @Column(name="INSURANCE_CLASS", length=20)
    private String     insuranceClass ;

    @Column(name="SECTION_ID", nullable=false, length=20)
    private String     sectionId ;
    
    @Column(name="SECTION_NAME", length=100)
    private String     sectionName ;

    @Column(name="PRODUCT_ID", nullable=false, length=20)
    private String     productId ;
    
    @Column(name="PRODUCT_NAME", length=100)
    private String     productName ;

    @Column(name="INSURANCE_CLASS_DESC", length=100)
    private String     insuranceClassDesc ;

    @Column(name="OWNER_CATEGORY", length=20)
    private String     ownerCategory ;

    @Column(name="COMPANY_ID", nullable=false, length=20)
    private String     companyId ;
    
    @Column(name="COMPANY_NAME",  length=100)
    private String     companyName ;

    @Column(name="MANUFACTURE_AGE")
    private Integer    manufactureAge ;
//
//    @Column(name="INSURER_SETTLEMENT")
//    private BigDecimal     insurerSettlement ;

    @Column(name="REGISTRATION_AGE")
    private Integer    registrationAge ;

    @Column(name="NCD_YEARS")
    private Integer    ncdYears ;

    @Column(name="NCD_YN", length=1)
    private String     ncdYn ;

    @Column(name="POLICY_TYPE", length=5)
    private String     policyType ;

    @Column(name="POLICY_TYPE_DESC", length=100)
    private String     policyTypeDesc ;
//
//    @Column(name="RADIOORCASSETEPLAYER")
//    private BigDecimal     radioorcasseteplayer ;

    @Column(name="STATUS", length=1)
    private String     status ;

    @Column(name="ROOF_RACK")
    private BigDecimal     roofRack ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATED_DATE")
    private Date       updatedDate ;

    @Column(name="CREATED_BY", length=100)
    private String     createdBy ;

    @Column(name="SPOT_FOG_LAMP")
    private BigDecimal     spotFogLamp ;

    @Column(name="UPDATED_BY", length=100)
    private String     updatedBy ;

    @Column(name="TRAILER_DETAILS", length=200)
    private String     trailerDetails ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;

    @Column(name="DRIVEN_BY", length=5)
    private String     drivenBy ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="POLICY_START_DATE")
    private Date       policyStartDate ;

    @Column(name="DRIVEN_BY_DESC", length=100)
    private String     drivenByDesc ;

    @Column(name="DRIVEN_BY_UNDER_AGE", length=5)
    private String     drivenByUnderAge ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="POLICY_END_DATE")
    private Date       policyEndDate ;

    @Column(name="CURRENCY")
    private String    currency ;

    @Column(name="DEFECTIVE_VISION_OR_HEARING", length=5)
    private String     defectiveVisionOrHearing ;

    @Column(name="EXCHANGE_RATE")
    private BigDecimal     exchangeRate ;

    @Column(name="MOTORING_OFFENCE", length=5)
    private String     motoringOffence ;

    @Column(name="FLEET_OWNER_YN", length=1)
    private String     fleetOwnerYn ;

    @Column(name="SUSPENSION_OF_LICENSE", length=5)
    private String     suspensionOfLicense ;

    @Column(name="NO_OF_VEHICLES")
    private Integer    noOfVehicles ;

    @Column(name="IRRESPECTIVE_OF_BLAME", length=5)
    private String     irrespectiveOfBlame ;

    @Column(name="NO_OF_COMPEHENSIVES")
    private Integer    noOfCompehensives ;

    @Column(name="VEHICLE_INTERESTED_COMPANY", length=5)
    private String     vehicleInterestedCompany ;

    @Column(name="CLAIM_RATIO")
    private BigDecimal     claimRatio ;

    @Column(name="INTERESTED_COMPANY_DETAILS", length=200)
    private String     interestedCompanyDetails ;

    @Column(name="COLLATERAL_YN", length=1)
    private String     collateralYn ;

    @Column(name="BORROWER_TYPE", length=1)
    private String     borrowerType ;

    @Column(name="OTHER_VEHICLE", length=5)
    private String     otherVehicle ;

    @Column(name="BORROWER_TYPE_DESC", length=100)
    private String     borrowerTypeDesc ;

    @Column(name="OTHER_VEHICLE_DETAILS", length=200)
    private String     otherVehicleDetails ;

    @Column(name="COLLATERAL_NAME", length=100)
    private String     collateralName ;

    @Column(name="OTHER_INSURANCE", length=5)
    private String     otherInsurance ;

    @Column(name="FIRST_LOSS_PAYEE", length=100)
    private String     firstLossPayee ;

    @Column(name="OTHER_INSURANCE_DETAILS", length=200)
    private String     otherInsuranceDetails ;

    @Column(name="HOLD_INSURANCE_POLICY", length=5)
    private String     holdInsurancePolicy ;

    @Column(name="NO_OF_CLAIMS")
    private Integer    noOfClaims ;

    @Column(name="CITY_LIMIT", length=20)
    private String     cityLimit ;

    @Column(name="ADDITIONAL_CIRCUMSTANCES", length=200)
    private String     additionalCircumstances ;

    @Column(name="SAVED_FROM", length=20)
    private String     savedFrom ;

    @Column(name="AC_EXECUTIVE_ID")
    private Integer    acExecutiveId ;

    @Column(name="APPLICATION_ID", length=100)
    private String     applicationId ;

    @Column(name="BROKER_CODE", length=20)
    private String     brokerCode ;

    @Column(name="SUB_USER_TYPE", length=20)
    private String     subUserType ;

    @Column(name="LOGIN_ID", length=100)
    private String     loginId ;

    @Column(name="CUSTOMER_ID", length=20)
    private String     customerId ;

    @Column(name="QUOTE_NO", length=20)
    private String     quoteNo ;

    @Column(name="ADMIN_LOGIN_ID", length=100)
    private String     adminLoginId ;

    @Column(name="ADMIN_REMARKS", length=1000)
    private String     adminRemarks ;

    @Column(name="REJECT_REASON", length=1000)
    private String     rejectReason ;

    @Column(name="REFERAL_REMARKS", length=1000)
    private String     referalRemarks ;

    @Column(name="BDM_CODE", length=100)
    private String     bdmCode ;

    @Column(name="SOURCE_TYPE", length=100)
    private String     sourceType;

    @Column(name="CUSTOMER_CODE", length=100)
    private String     customerCode;

    @Column(name="BROKER_BRANCH_CODE", length=20)
    private String     brokerBranchCode ;

    @Column(name="BROKER_BRANCH_NAME", length=20)
    private String     brokerBranchName ;
    
    @Column(name="COMMISSION_TYPE", length=20)
    private String     commissionType ;
    
    @Column(name="COMMISSION_TYPE_DESC", length=20)
    private String     commissionTypeDesc ;
    
    @Column(name="OLD_REQ_REF_NO", length=20)
    private String     oldReqRefNo ;
    
    @Column(name="HAVEPROMOCODE", length=10)
    private String     havepromocode ;

    @Column(name="PROMOCODE", length=100)
    private String     promocode ;
    
    @Column(name="DRIVER_YN", length=100)
    private String     driverYn;


    @Column(name="BANK_CODE", length=100)
    private String   bankCode;
    
    @Column(name="MANUAL_REFERAL_YN", length=100)
    private String  manualReferalYn;
    
    

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENDORSEMENT_DATE")
    private Date       endorsementDate ;

    @Column(name="ENDORSEMENT_REMARKS", length=500)
    private String     endorsementRemarks ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENDORSEMENT_EFFDATE")
    private Date       endorsementEffdate ;

    @Column(name="ORIGINAL_POLICY_NO", length=500)
    private String     originalPolicyNo ;

    @Column(name="ENDT_PREV_POLICY_NO", length=500)
    private String     endtPrevPolicyNo ;

    @Column(name="ENDT_PREV_QUOTE_NO", length=500)
    private String     endtPrevQuoteNo ;

    @Column(name="ENDT_COUNT")
    private BigDecimal endtCount ;

    @Column(name="ENDT_STATUS", length=10)
    private String     endtStatus ;
    
    
    
    @Column(name="IS_FINYN", length=10)
    private String     isFinaceYn ;
    
    
    @Column(name="ENDT_CATEG_DESC", length=10)
    private String     endtCategDesc ;
    
    @Column(name="ENDORSEMENT_YN", length=10)
    private String     endorsementYn ;
    
    @Column(name="ENDT_PREMIUM")
    private Double       endtPremium ;
    
    @Column(name="TIRA_COVER_NOTE_NO")
    private String tiraCoverNoteNo;
    
    @Column(name="COMMISSION_PERCENTAGE")
    private BigDecimal commissionPercentage;
    
    @Column(name="VAT_COMMISSION")
    private BigDecimal vatCommission;
    
    @Column(name="WIND_SCREEN_SUM_INSURED_LC")
    private BigDecimal     windScreenSumInsuredLc ;

    @Column(name="SUM_INSURED_LC")
    private BigDecimal     sumInsuredLc ;
    
    @Column(name="TPPD_FREE_LIMIT_LC")
    private BigDecimal     tppdFreeLimitLc ;

    @Column(name="TPPD_INCREAE_LIMIT_LC")
    private BigDecimal     tppdIncreaeLimitLc ;
    
    @Column(name="ACCCESSORIES_SUM_INSURED_LC")
    private BigDecimal     acccessoriesSumInsuredLc ;
    
    @Column(name="VD_REFNO")
    private Integer     vdRefNo ;
    
    @Column(name="CD_REFNO")
    private Integer     cdRefno;
    
    @Column(name="MS_REFNO")
    private Integer     msRefno ;
    
    @Column(name="CUSTOMER_NAME")
    private String     customerName ;
    
    
    @Column(name="BRANCH_NAME")
    private String     branchName ;
    
    @Column(name="MOTOR_USAGE_DESC")
    private String     motorUsageDesc ;
    
    @Column(name="TIRA_BODY_TYPE")
    private String     tiraBodyType ;
    
    @Column(name="TIRA_MOTOR_USAGE")
    private String     tiraMotorUsage ;
    

    @Column(name="SALE_POINT_CODE", length=200)
    private String    salePointCode;

    @Column(name="FINALIZE_YN")
    private String finalizeYn;
    
    @Column(name = "NON_ELEC_ACCESSORIES_SI")
    private Double nonElecAccessoriesSi; 
    
    @Column(name = "NON_ELEC_ACCESSORIES_SI_LC")
    private Double nonElecAccessoriesSiLc;
    
    @Column(name="EXCESS_LIMIT")
    private Double excessLimit;
    
     @Column(name="EXCESS_LIMIT_LC")
     private Double excessLimitLc;
     
     @Column(name="BROKER_TIRA_CODE")
     private String brokerTiraCode;

     @Column(name="SOURCE_TYPE_ID")
     private String sourceTypeId;

    
     @Column(name="EMI_YN", length=20)
     private String     emiYn;

     @Column(name="INSTALLMENT_PERIOD")
     private Integer     installmentPeriod ;
     
     @Column(name="NO_OF_INSTALLMENT")
     private Integer     noOfInstallment ;

     @Column(name="EMI_PREMIUM")
     private BigDecimal     emiPremium ;
     
 
     @Column(name="VAT_PREMIUM")
     private BigDecimal vatPremium;
     
     
     @Column(name="ENDT_VAT_PREMIUM")
     private BigDecimal endtVatPremium;


     @Column(name="MANUFACTURE_COUNTRY")
     private String manufactureCountry;
     
     @Column(name="CUSTOMER_TYPE")
     private String customerType;
     
     @Column(name="VEHICLE_MAKE_ID")
     private String vehicleMakeId;
     
     @Column(name="VEHICLE_MODEL_ID")
     private String vehicleModelId ;
     
     @Column(name="FUEL_TYPE_ID")
     private String fuelTypeId;
     
     @Column(name="OWNER_CATEGORY_ID", length=20)
     private String     ownerCategoryId ;

     @Column(name="OWNER_NAME", length=20)
     private String     ownerName ;
     

     @Column(name="OWN_DAMAGE", length=20)
     private String     ownDamage ;
     
     @Column(name="THEFT", length=20)
     private String     theft ;
     
     @Column(name="WINDSCREEN", length=20)
     private String     windscreen ;
     
     @Column(name="FIRE", length=20)
     private String     fire ;
     
     @Column(name="THIRD_PARTY", length=20)
     private String     thirdParty ;
     
     @Column(name="VEHICLE_CLASS", length=20)
     private String     vehicleClass ;
     
     @Column(name="CLAIM_NUM_12M_0M", length=20)
     private Integer     claimNum12m0m ;
     
     @Column(name="CLAIM_NUM_24M_12M", length=20)
     private Integer     claimNum24m12m ;
     
     @Column(name="CLAIM_NUM_36M_24M", length=20)
     private Integer     claimNum36m24m ;
     
     @Column(name="POWER_KILO_WATTS", length=20)
     private BigDecimal     powerKiloWatts ;
     
     @Column(name="POWER_WATTS", length=20)
     private BigDecimal     powerWatts ;
     

     @Column(name="VEHICLE_GROUP", length=20)
     private String     vehicleGroup ;
     
     @Column(name="CAR_ALARM_YN", length=20)
     private String     carAlarmYn ;
     
     @Column(name="PAYMENT_FREQUENCY", length=20)
     private Integer     paymentFrequency ;
     
     @Column(name="RENEWAL_DATE_YN", length=20)
     private String renewalDateYn;     
     @Column(name = "VEHICLE_VALUE_TYPE", length = 100)
 	private String vehicleValueType;

 	@Column(name = "VEHICLE_VALUE_TYPE_DESC", length = 100)
 	private String vehicleValueTypeDesc;

 	@Column(name = "INFLATION", length = 100)
 	private String inflation;

 	@Column(name = "NCB", length = 100)
 	private String ncb;
 	
 	@Column(name = "DEFENCE_VALUE", length = 100)
 	private String defenceValue;

 	@Temporal(TemporalType.TIMESTAMP)
 	@Column(name = "PURCHASE_DATE")
 	private Date purchaseDate;

 	@Temporal(TemporalType.TIMESTAMP)
 	@Column(name = "REGISTRATION_DATE")
 	private Date registrationDate;
 	
 	@Column(name = "REGISTRATION_STATUS", length = 10)
	private String registrationStatus;

 	@Column(name = "EXCESS", length = 100)
	private String excess;
	
	@Column(name = "EXCESS_DESC", length = 100)
	private String excessDesc;
	
	@Column(name = "DEFENCE_VALUE_DESC ", length = 100)
	private String defenceValueDesc;
	
	@Column(name ="CUST_RENEWAL_YN", length = 5)
	private String custRenewalYn;
 	
	@Column(name = "NON_DEPRECIATED_SI"  )
	private BigDecimal nonDepreciatedSi;
	
	@Column(name = "INFLATION_SI"  )
	private BigDecimal inflationSi;
	
	@Column(name = "MILEAGE")
	private Integer mileage;
	
	@Column(name ="NO_CLAIM_YEARS")
	private Integer noClaimYears;
	
	@Column(name ="NO_OF_TRAILERS")
	private Integer noOfTrailers;

	@Column(name ="NO_OF_PASSENGERS")
	private Integer noOfPassengers;
     
	@Column(name = "LOSS_RATIO")
	private Double previousLossRatio;
	
	@Column(name = "PREVIOUS_INSURANCE_YN")
	private String previousInsuranceYN;
	
	@Column(name = "HORSE_POWER")
	private Integer horsePower;
	
	 @Column(name = "ZONE")
	private Integer zone;
	 
	@Column(name = "CLASS")
	private String classType;
	
	@Column(name = "CLASS_DESC")
    private String classTypeDesc;

	@Column(name = "NO_OF_CARDS")
	private Integer noOfCards;

	@Column(name = "PAYLOAD")
	private BigDecimal payload;

	/// Local Description Columns///

	@Column(name = "EXCESS_DESC_LOCAL")
	private String excessDescLocal;

	@Column(name = "VEHICLE_VALUE_TYPE_DESC_LOCAL")
	private String vehicleValueTypeDescLocal;

	@Column(name = "DEFENCE_VALUE_DESC_LOCAL")
	private String defenceValueDescLocal;

	@Column(name = "VEHICLE_TYPE_DESC_LOCAL")
	private String vehicleTypeDescLocal;

	@Column(name = "MOTOR_CATEGORY_DESC_LOCAL")
	private String motorCategoryDescLocal;

	@Column(name = "MOTOR_USAGE_DESC_LOCAL")
	private String motorUsageDescLocal;

	@Column(name = "BORROWER_TYPE_DESC_LOCAL")
	private String borrowerTypeDescLocal;

	@Column(name = "INSURANCE_TYPE_DESC_LOCAL")
	private String insuranceTypeDescLocal;

	@Column(name = "VEHICLE_MAKE_DESC_LOCAL")
	private String vehicleMakeDescLocal;

	@Column(name = "VEHICILE_MODEL_DESC_LOCAL")
	private String vehcileModelDescLocal;

	@Column(name = "FUEL_TYPE_DESC_LOCAL")
	private String fuelTypeDescLocal;

	@Column(name = "COLOR_DESC_LOCAL")
	private String colorDescLocal;

	@Column(name = "SECTION_NAME_LOCAL")
	private String sectionNameLocal;

	@Column(name = "PRODUCT_NAME_LOCAL")
	private String productNameLocal;

	/// Local Description Columns///
	
////ivory
	@Column(name = "PA_COVERID")
    private String paCoverId;
	

	@Column(name = "PA_COVERID_DESC")
    private String paCoveridDesc;
	
	@Column(name = "ZONE_CIRCULATION_DESC")
    private String zonecirculationDesc;
	
	@Column(name = "ZONE_CIRCULATION")
    private String zonecirculation;
	
	
	@Column(name = "BANKING_DELEGATION")
    private String bankingDelegation; 
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LOAN_START_DATE ")
    private Date loanStartDate ;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LOAN_END_DATE")
    private Date loanEndDate;
	
	@Column(name = "LOAN_AMOUNT")
	private Double loanAmount;
	
	@Column(name = "COLLATERAL_COMPANY_ADDRESS")
    private String collateralCompanyAddress; 
	
	@Column(name = "COLLATERAL_COMPANY_NAME")
    private String collateralCompanyName;
	
	@Column(name = "USAGE_ID")
    private String usageId;
	
	@Column(name = "USAGE_DESC")
    private String usageDesc;
	
	@Column(name = "VEHICLE_TYPE_IVR")
    private String vehicleTypeIvr;
	
	@Column(name = "VEHICLE_TYPE_DESC_IVR")
    private String vehicleTypeDescIvr;
	
	@Column(name = "NO_OF_CYLINDERS")
    private Integer noOfCylinders;
	
	@Column(name = "REFERAL_CODES", length = 500)
	private String referalCodes;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LICENSE_ISSUED_DATE")
	private Date licenseIssuedDate;
	
	@Column(name = "LICESENSE_DURATION")
	private Integer licesenseDuration;
	 
	@Column(name = "CLAIM_TYPE", length = 20)
	private String claimType;
	
	@Column(name = "CLAIM_TYPE_DESC", length = 100)
	private String claimTypeDesc;
	
	@Column(name = "RENEWAL_YN", length = 2)
	private String renewalYn;
	
	@Column(name = "PREVIOUS_INSURED", length = 50)
	private String previousInsured;
	
	@Column(name = "SERIES", length = 50)
	private String series;
	
//	@Column(name = "NO_OF_CYCLINDERS")
//	private Integer noOfCyclinders;
	
	@Column(name = "ENGINE_TYPE", length = 100)
	private String engineType;
	
	@Column(name = "PLATE_COLOR", length = 50)
	private String plateColor;
	
	@Column(name = "NO_OF_DOORS")
	private Integer noOfDoors;
	
	@Column(name = "PLATE_TYPE_ID", length = 5)
	private String plateTypeId;
	
	@Column(name = "PLATE_TYPE_DESC", length = 50)
	private String plateTypeDesc;
	
	@Column(name = "NO_CLAIM_DOCUMENT_ID")
	private Integer noClaimDocumentId;
	
	@Column(name = "NO_CLAIM_DOCUMENT_DESC", length = 100)
	private String noClaimDocumentDesc;
	
	@Column(name = "REGISTERED_AT")
	private Integer registeredAt;
	
	@Column(name = "MODALITY_SELECTION", length = 5)
	private String modalitySelection;
	
	@Column(name = "DEPRECIATION_VEHICLE_VALUE")
	private BigDecimal depreciationVehicleValue;
	
	@Column(name = "DANGEROUS_GOODS_YN", length = 2)
	private String dangerousGoodsYn;
	
	@Column(name = "NEW_VALUE")
	private Integer newValue;
	
	@Column(name = "MARKET_VALUE")
	private Integer marketValue;
	
	@Column(name = "AGGREGATED_VALUE")
	private Integer aggregatedValue;
	
	@Column(name = "MUNICIPALITY_TRAFFIC")
	private String municipalityTraffic;
	
	@Column(name = "TRANSPORT_HYDRO")
	private String transportHydro;
	
	@Column(name = "DISPLACEMENT_CM3")
    private String displacementInCM3;
	
	@Column(name = "PLATE_TYPE")
	private String plateType;
	
	@Column(name = "QUOTE_EXPIRY_DAYS")
	private Integer quoteExpiryDays;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "QUOTE_EXPIRY_DATE")
    private Date quoteExpiryDate;
}



