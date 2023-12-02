package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminViewQuoteBurglaryRes {
	
	
	@JsonProperty("NatureOfTradeId")
	private Integer natureOfTradeId;
	
	@JsonProperty("InsuranceForId")
	private String insuranceForId;

	@JsonProperty("InternalWallType")
	private Integer internalWallType;
	
	@JsonProperty("OccupiedYear")
	private Integer occupiedYear;
	
	@JsonProperty("BuildingBuildYear")
	private Integer buildingBuildYear;
	
	@JsonProperty("RoofType")
	private String roofType;
	
	@JsonProperty("CeilingType")
	private Integer ceilingType;
	
	@JsonProperty("WallType")
	private String wallType;
	
	@JsonProperty("Address")
	private String address;
	
	@JsonProperty("RegionCode")
	private String regionCode;
	
	@JsonProperty("DistrictCode")
	private String districtCode;
	
	@JsonProperty("WatchmanGuardHours")
	private Integer watchmanGuardHours;
	
	@JsonProperty("AccessibleWindows")
	private Integer accessibleWindows;
	
	@JsonProperty("ShowWindow")
	private Integer showWindow;
	
	@JsonProperty("FrontDoors")
	private Integer frontDoors;
	
	@JsonProperty("BackDoors")
	private Integer backDoors;
	
	@JsonProperty("WindowsMaterialId")
	private Integer windowsMaterialId;
	
	@JsonProperty("DoorsMaterialId")
	private String doorsMaterialId;
	
	@JsonProperty("NightLeftDoor")
	private Integer nightLeftDoor;
	
	@JsonProperty("BuildingOccupied")
	private Integer buildingOccupied;
	
	@JsonProperty("StockInTradeSi")
	private BigDecimal stockInTradeSi;
	
	@JsonProperty("GoodsSi")
	private  BigDecimal goodsSi;
	
	@JsonProperty("FurnitureSi")
	private BigDecimal furnitureSi;
	
	@JsonProperty("ApplianceSi")
	private BigDecimal applianceSi;
	
	@JsonProperty("CashValueablesSi")
	private BigDecimal cashValueablesSi;
	
	@JsonProperty("BurglarySi")
	private BigDecimal burglarySi;
	
	@JsonProperty("StockLossPercent")
	private Integer stockLossPercent;
	
	@JsonProperty("GoodsLossPercent")
	private Integer goodsLossPercent;
	
	@JsonProperty("FurnitureLossPercent")
	private Integer furnitureLossPercent;
	
	@JsonProperty("ApplianceLossPercent")
	private Integer applianceLossPercent;
	
	@JsonProperty("CashValueablesLossPercent")
	private Integer cashValueablesLossPercent;
}
