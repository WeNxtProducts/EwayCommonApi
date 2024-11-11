/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2022-11-18 ( Date ISO 2022-11-18 - Time 11:38:59 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2022-11-18 ( 11:38:59 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.bean;


import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import java.util.Date;
import jakarta.persistence.*;




/**
* Domain class for entity "MsAssetDetails"
*
* @author Telosys Tools Generator
*
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@IdClass(MsAssetDetailsId.class)
@Table(name="ms_asset_details")


public class MsAssetDetails implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="VD_REFNO", nullable=false)
    private Long       vdRefno ;

    @Id
    @Column(name="REQUEST_REFERENCE_NO", nullable=false, length=20)
    private String     requestReferenceNo ;
    
    
    @Id
    @Column(name="LOCATION_ID", nullable=false)
    private Integer    locationId ;
    
    @Id
    @Column(name="RISK_ID", nullable=false)
    private Integer    riskId ;

    @Id
    @Column(name="PRODUCT_ID", nullable=false)
    private Integer    productId ;

    @Id
    @Column(name="SECTION_ID", nullable=false)
    private Integer    sectionId ;

    @Id
    @Column(name="COMPANY_ID", nullable=false, length=20)
    private String     companyId ;

    @Id
    @Column(name="BRANCH_CODE", nullable=false, length=20)
    private String     branchCode ;
    
    @Id
    @Column(name="ENDT_TYPE_ID")
    private Integer    endtTypeId ;
    
    @Id
    @Column(name="ENDT_CATEGORY_ID")
    private String    endtCategoryId ;

    //--- ENTITY DATA FIELDS 
    @Column(name="BUILDING_AGE")
    private Integer    buildingAge ;

    @Column(name="BUILDING_FLOORS")
    private Integer    buildingFloors ;

    @Column(name="BUILDING_USAGE_ID", length=100)
    private String     buildingUsageId;
 
    @Column(name="BUILDING_SUMINSURED")
    private BigDecimal buildingSuminsured ;
    
    @Column(name="ALLRISK_SUMINSURED")
    private BigDecimal     allriskSuminsured ;
    
       
    @Column(name="CONTENT_SUMINSURED")
    private BigDecimal     contentSuminsured ;
   
    @Column(name="PERIOD_OF_INSURANCE", nullable=false, length=10)
    private String     periodOfInsurance ;
    
    
    @Column(name="CURRENCY", length=20)
    private String  currency;
    
    
    @Column(name="EXCHANGE_RATE")
    private BigDecimal exchangeRate;
    
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;

    @Column(name="CREATED_BY", length=100)
    private String     createdBy ;
    
    @Column(name="STATUS", length=2)
    private String     status ;

    @Column(name="GROUP_COUNT")
    private Integer groupCount;
    
    @Column(name="HAVEPROMOCODE", length=20)
    private String     havepromocode;

    @Column(name="PROMOCODE", length=100)
    private String     promocode;
    
    @Column(name="INBUILD_CONSTRUCT_TYPE", length=20)
    private String     inbuildConstructType;
    
    @Column(name="INTERNAL_WALL_TYPE")
    private Integer internalWallType;

    @Column(name="CATEGORY_ID", length=20)
    private String     categoryId;

    @Column(name="ELEC_EQUIP_SUMINSURED")
    private BigDecimal elecEquipSuminsured;

    @Column(name="GOODS_SINGLECARRY_SUMINSURED")
    private BigDecimal goodsSinglecarrySuminsured;

    @Column(name="GOODS_TURNOVER_SUMINSURED")
    private BigDecimal goodsTurnoverSuminsured;

    @Column(name="INDUSTRY_ID")
    private Integer  industryId ;

    @Column(name="STOCK_IN_TRADE_SI")
    private BigDecimal stockInTradeSi ;
    
    @Column(name="GOODS_SI")
    private BigDecimal goodsSi;
    
    @Column(name="FURNITURE_SI")
    private BigDecimal furnitureSi;
    
    @Column(name="APPLIANCE_SI")
    private BigDecimal applianceSi;
    
    @Column(name="CASH_VALUEABLES_SI")
    private BigDecimal cashValueablesSi;
    
    @Column(name="CASH_VALUEABLES_SI_LC")
    private BigDecimal cashValueablesSiLc;
    
    @Column(name="STOCK_LOSS_PERCENT")
	private Integer stockLossPercent ;
  
  @Column(name="GOODS_LOSS_PERCENT")
  private Integer goodsLossPercent;
  
  @Column(name="FURNITURE_LOSS_PERCENT")
  private Integer furnitureLossPercent;
  
  @Column(name="APPLIANCE_LOSS_PERCENT")
  private Integer applianceLossPercent;
  
  @Column(name="CASH_VALUEABLES_LOSS_PERCENT")
  private Integer cashValueablesLossPercent;
    
    @Column(name="MACHINE_EQUIP_SI")
    private BigDecimal machineEquipSi ;

    @Column(name="PLATE_GLASS_SI")
    private BigDecimal plateGlassSi ;
    
    @Column(name="FIRST_LOSS_PERCENT")
    private Long firstLossPercent ;
    
    @Column(name="POWER_PLANT_SI")
    private BigDecimal powerPlantSi ;
    
    @Column(name="ELEC_MACHINES_SI")
    private BigDecimal elecMachinesSi ;
    
    @Column(name="EQUIPMENT_SI")
    private BigDecimal equipmentSi ;
    
    @Column(name="GENERAL_MACHINE_SI")
    private BigDecimal generalMachineSi ;
    
    @Column(name="MANU_UNITS_SI")
    private BigDecimal manuUnitsSi ;
    
    @Column(name="BOILER_PLANTS_SI")
    private BigDecimal boilerPlantsSi ;
    
    @Column(name="MINING_PLANT_SI")
    private BigDecimal miningPlantSi;
    
    @Column(name="NONMINING_PLANT_SI")
    private BigDecimal nonminingPlantSi;
    
    @Column(name="GENSETS_SI")
    private BigDecimal gensetsSi;
    
  // LC Columns
	@Column(name = "ALLRISK_SUMINSURED_LC")
	private BigDecimal allRiskSumInsuredLC;


	@Column(name = "CONTENT_SUMINSURED_LC")
	private BigDecimal contentSumInsuredLC;

	@Column(name = "BUILDING_SUMINSURED_LC")
	private BigDecimal buildingSumInsuredLC;

		@Column(name = "ELEC_EQUIP_SUMINSURED_LC")
	private BigDecimal elecEquipSumInsuredLC;

	
	@Column(name = "GOODS_SINGLECARRY_SUMINSURED_LC")
	private BigDecimal goodsSingleCarrySumInsuredLC;

	@Column(name = "GOODS_TURNOVER_SUMINSURED_LC")
	private BigDecimal goodsTurnoverSumInsuredLC;

	@Column(name = "STOCK_IN_TRADE_SI_LC")
	private BigDecimal stockInTradeSiLC;

	@Column(name = "GOODS_SI_LC")
	private BigDecimal goodsSiLC;

	@Column(name = "FURNITURE_SI_LC")
	private BigDecimal furnitureSiLC;

	@Column(name = "APPLIANCE_SI_LC")
	private BigDecimal applianceSiLC;

	@Column(name = "MACHINE_EQUIP_SI_LC")
	private BigDecimal machineEquipSiLC;

	@Column(name = "PLATE_GLASS_SI_LC")
	private BigDecimal plateGlassSiLC;

	@Column(name = "BURGLARY_SI_LC")
	private BigDecimal burglarySiLC;

	@Column(name = "POWER_PLANT_SI_LC")
	private BigDecimal powerPlantSiLC;

	@Column(name = "ELEC_MACHINES_SI_LC")
	private BigDecimal elecMachinesSiLC;

	@Column(name = "EQUIPMENT_SI_LC")
	private BigDecimal equipmentSiLC;

	@Column(name = "GENERAL_MACHINE_SI_LC")
	private BigDecimal generalMachineSiLC;

	@Column(name = "MANU_UNITS_SI_LC")
	private BigDecimal manuUnitsSiLC;

	@Column(name = "BOILER_PLANTS_SI_LC")
	private BigDecimal boilerPlantsSiLC;

	@Column(name = "MINING_PLANT_SI_LC")
	private BigDecimal miningPlantSiLC;

	@Column(name = "NONMINING_PLANT_SI_LC")
	private BigDecimal nonMiningPlantSiLC;

	@Column(name = "GENSETS_SI_LC")
	private BigDecimal gensetsSiLC;
    
    @Column(name="UW_LOADING")
    private BigDecimal     uwLoading;
    
    @Column(name = "OCCUPATION_TYPE")
	private String occupationType;
    
    @Column(name="MONEY_SAFE_LIMIT")
    private BigDecimal     moneySafeLimit;
    
    @Column(name="MONEY_SAFE_LIMIT_LC")
    private BigDecimal     moneySafeLimitLc;
    
    @Column(name="MONEY_OUTOF_SAFE")
    private BigDecimal     moneyOutofSafe;
    
    @Column(name="MONEY_OUTOF_SAFE_LC")
    private BigDecimal     moneyOutofSafeLc;
    
    @Column(name="MONEY_DIRECTOR_RESIDENCE")
    private BigDecimal     moneyDirectorResidence;
    
    @Column(name="MONEY_DIRECTOR_RESIDENCE_LC")
    private BigDecimal     moneyDirectorResidenceLc;
    
    @Column(name="MONEY_COLLECTOR")
    private BigDecimal     moneyCollector;
    
    @Column(name="MONEY_COLLECTOR_LC")
    private BigDecimal     moneyCollectorLc;
    
    @Column(name="MONEY_ANNUAL_ESTIMATE")
    private BigDecimal     moneyAnnualEstimate;
    
    @Column(name="MONEY_ANNUAL_ESTIMATE_LC")
    private BigDecimal     moneyAnnualEstimateLc;
    
    @Column(name="MONEY_MAJOR_LOSS")
    private BigDecimal     moneyMajorLoss;
    
    @Column(name="MONEY_MAJOR_LOSS_LC")
    private BigDecimal     moneyMajorLossLc;
    
    @Column(name="FIRE_PLANT_SI")
    private BigDecimal firePlantSi  ;
    
    @Column(name="FIRE_PLANT_SI_LC")
    private BigDecimal firePlantSiLc  ;
    
    @Column(name="WATER_TANK_SI")
    private BigDecimal waterTankSi;
    
    @Column(name="WATER_TANK_SI_LC")
    private BigDecimal waterTankSiLc;
    
    @Column(name="ARCHITECTS_SI")
    private BigDecimal architectsSi  ;
    
    @Column(name="ARCHITECTS_SI_LC")
    private BigDecimal architectsSiLc  ;
    
    @Column(name="LOSS_OF_RENT_SI")
    private BigDecimal lossOfRentSi  ;
    
    @Column(name="LOSS_OF_RENT_SI_LC")
    private BigDecimal lossOfRentSiLc  ;
    
    @Column(name="JEWELLERY_SI")
    private BigDecimal jewellerySi  ;
    
    @Column(name="JEWELLERY_SI_LC")
    private BigDecimal jewellerySiLc  ;
    
    @Column(name="PAITINGS_SI")
    private BigDecimal paitingsSi  ;
    
    @Column(name="PAITINGS_SI_LC")
    private BigDecimal paitingsSiLc  ;

    @Column(name="CARPETS_SI")
    private BigDecimal carpetsSi  ;
    
    @Column(name="CARPETS_SI_LC")
    private BigDecimal carpetsSiLc  ;
    
    @Column(name="INSURANCE_CLASS", length=10)

   private String     insuranceClass ;
    
    
    @Column(name = "ON_STOCK_SI")
	private BigDecimal onStockSi;
    

    @Column(name = "ON_STOCK_SI_LC")
	private BigDecimal onStockSiLc;
    

    @Column(name = "ON_ASSETS_SI")
	private BigDecimal onAssetsSi;
    
    @Column(name = "ON_ASSETS_SI_LC")
	private BigDecimal onAssetsSiLc;
    
    @Column(name = "BURGLARY_SI")
	private BigDecimal burglarySi;
    
    @Column(name = "STRONGROOM_SI")
	private BigDecimal strongroomSi;
    
    @Column(name = "STRONGROOM_SI_LC")
	private BigDecimal strongroomSiLc;
    
    @Column(name = "MACHINERY_SI")
	private BigDecimal machinerySi;
    
    @Column(name = "MACHINERY_SI_LC")
	private BigDecimal machinerySiLc;

    //private String     insuranceClass ;    
    
    @Column(name = "GROSS_PROFIT_FC")
    private BigDecimal grossProfitFc;
    
    @Column(name = "GROSS_PROFIT_LC")
    private BigDecimal grossProfitLc;
    
    @Column(name="INDEMNITY_PERIOD_FC")
    private BigDecimal indemnityPeriodFc;
    
    @Column(name="INDEMNITY_PERIOD_LC ")
    private BigDecimal indemnityPeriodLc;
    
    @Column(name="TRANSPORTED_BY") 
    private String transportedBy;
   
   @Column(name="MODE_OF_TRANSPORT")
   private String modeOfTransport;
   
   @Column(name="GEOGRAPHICAL_COVERAGE") 
   private String geographicalCoverage;
   
   @Column(name="SINGLE_ROAD_SI_LC") 
   private Double singleRoadSiLc;
   
   @Column(name="SINGLE_ROAD_SI_FC ") 
   private Double singleRoadSiFc;
   
   @Column(name="EST_ANNUAL_CARRIES_SI_LC ") 
   private Double estAnnualCarriesSiLc; 
   
   @Column(name="EST_ANNUAL_CARRIES_SI_FC ") 
   private Double estAnnualCarriesSiFc;

   @Column(name="GROUND_UNDERGROUND_SI")
   private 	BigDecimal groundUndergroundSi;
   
   @Column(name="WALL_TYPE") 
   private String wallType;
   
   @Column(name="ROOF_TYPE") 
   private String roofType;
   
 //Bond
   @Column(name="BOND_SUMINSURED")
   private BigDecimal bondSuminsured;
   
   @Column(name="BOND_TYPE")
   private String bondType;
   
   @Column(name="BOND_YEAR")
   private String bondYear;
   
   @Column(name="SUM_INSURED")
   private BigDecimal     sumInsured ;
   
   @Column(name="SUM_INSURED_LC")
   private BigDecimal     sumInsuredLc ;
   @Column(name="INDEMITY_PERIOD")
   private String indemityPeriod;

}




