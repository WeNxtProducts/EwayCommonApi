/* 
*  Copyright (c) 2019. All right reserved
 * Created on 2022-01-05 ( Date ISO 2022-01-05 - Time 16:11:08 )
 * Generated by Telosys Tools Generator ( version 3.3.0 )
 */

/*
 * Created on 2022-01-05 ( 16:11:08 )
 * Generated by Telosys ( http://www.telosys.org/ ) version 3.3.0
 */


package com.maan.eway.bean;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
//@Builder
@IdClass(PgithPolRiskAddlInfoId.class)
@Table(name="PGIT_POL_RISK_ADDL_INFO_01") 


public class PgithPolRiskAddlInfo implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="QUOTATION_POLICY_NO")  //POLICY NO
    private String     quotationPolicyNo ;

    @Id
    @Column(name="RISK_ID")  //VEHCILE ID
    private String     riskId ;
    
    @Id
    @Column(name="PRAI_CODE_23") //TIRA PROD CODE
    private String     praiCode23;

    @Id
    @Column(name="PRAI_CODE_24")  
    private String     praiCode24;
    
    @Id 
    @Column(name="PRAI_DATA_01") //Chassis No.
    private String     praiData01 ;

    @Id
    @Column(name="PRAI_DATA_03") //ENGINE No.
    private String     praiData03 ;
    

    //--- ENTITY DATA FIELDS 
    @Column(name="PRAIH_SYS_ID")
    private BigDecimal praihSysId ;
    
    @Column(name="PRAIH_POL_SYS_ID")
    private BigDecimal praihPolSysId ;
    
    @Column(name="PRAIH_END_NO_IDX")
    private BigDecimal praihEndNoIdx ;

    @Column(name="PRAI_END_SR_NO")
    private BigDecimal praiEndSrNo ;
    
    @Column(name="PRAI_PSEC_SYS_ID")
    private BigDecimal praiPsecSysId ;

    @Column(name="PRAI_RISK_LVL_NO")
    private BigDecimal praiRiskLvlNo ;

    @Column(name="PRAIH_LVL1_SYS_ID")
    private BigDecimal praihLvl1SysId ;
    
    @Column(name="PRAI_LVL1_SR_NO")
    private BigDecimal     praiLvl1SrNo ;

    @Column(name="PRAIH_LVL2_SYS_ID")
    private BigDecimal     praihLvl2SysId ;

    @Column(name="PRAIH_LVL2_SR_NO")
    private BigDecimal     praihLvl2SrNo ;
    
    @Column(name="PRAI_LVL3_SYS_ID")
    private BigDecimal     praiLvl3SysId ;

    @Column(name="PRAI_LVL3_SR_NO")
    private BigDecimal     praiLvl3SrNo ;
    
    @Column(name="PRAI_LVL4_SYS_ID")
    private BigDecimal     praiLvl4SysId ;

    @Column(name="PRAI_LVL4_SR_NO")
    private BigDecimal     praiLvl4SrNo ;
    
    @Column(name="PRAI_LVL5_SYS_ID")
    private BigDecimal     praiLvl5SysId ;

    @Column(name="PRAI_LVL5_SR_NO")
    private BigDecimal     praiLvl5SrNo ;
    
    @Column(name="PRAI_SI_CURR_CODE")
    private String     praiSiCurrCode ;

    @Column(name="PRAI_PREM_CURR_CODE")
    private String     praiPremCurrCode ;


    @Column(name="PRAI_SI_FC")
    private BigDecimal praiSiFc ;

    @Column(name="PRAI_SI_LC_1")
    private BigDecimal praiSiLc1 ;
    
    @Column(name="PRAI_SI_LC_2")
    private BigDecimal praiSiLc2 ;
    
    @Column(name="PRAI_SI_LC_3")
    private BigDecimal praiSiLc3 ;

    @Column(name="PRAI_ORG_SI_FC")
    private BigDecimal     praiOrgSiFc ;
    
    @Column(name="PRAI_ORG_SI_LC_1")
    private BigDecimal praiOrgSiLc1 ;

    @Column(name="PRAI_ORG_SI_LC_2")
    private BigDecimal praiOrgSiLc2 ;
    
    @Column(name="PRAI_ORG_SI_LC_3")
    private BigDecimal praiOrgSiLc3 ;
    
    @Column(name="PRAI_PREM_FC")
    private BigDecimal praiPremFc ;
    
    
    @Column(name="PRAI_PREM_LC_1")
    private BigDecimal praiPremLc1 ;
    
    @Column(name="PRAI_PREM_LC_2")
    private BigDecimal praiPremLc2 ;
    
    @Column(name="PRAI_PREM_LC_3")
    private BigDecimal praiPremLc3 ;
    
    @Column(name="PRAI_ORG_PREM_FC")
    private BigDecimal praiOrgPremFc ;
    
    @Column(name="PRAI_ORG_PREM_LC_1")
    private BigDecimal praiOrgPremLc1 ;
    
    @Column(name="PRAI_ORG_PREM_LC_2")
    private BigDecimal praiOrgPremLc2 ;
    
    @Column(name="PRAI_ORG_PREM_LC_3")
    private BigDecimal praiOrgPremLc3 ; 
    
    @Column(name="PRAI_CUM_BONUS_PERC")
    private BigDecimal praiCumBonusPerc ;
    
    @Column(name="PRAI_CUM_BONUS_FC")
    private BigDecimal praiCumBonusFc ;
    
    @Column(name="PRAI_CUM_BONUS_LC_1")
    private BigDecimal praiCumBonusLc1 ;
    
    @Column(name="PRAI_CUM_BONUS_LC_2")
    private BigDecimal praiCumBonusLc2 ;
    
    @Column(name="PRAI_CUM_BONUS_LC_3")
    private BigDecimal praiCumBonusLc3 ;
    
    @Column(name="PRAI_SILENT_YN")
    private String praiSilentYn ;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_SILENT_FM_DT")
    private Date praiSilentFmDt ;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_SILENT_TO_DT")
    private Date praiSilentToDt ;
    
    @Column(name="PRAI_SERVICE_TAX_YN")
    private String praiServiceTaxYn ;
    
    @Column(name="PRAI_REC_TYPE")
    private String praiRecType ;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_EFF_FM_DT")
    private Date praiEffFmDt ;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_EFF_TO_DT")
    private Date praiEffToDt ;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_END_EFF_FM_DT")
    private Date praiEndEffFmDt ;
    
    @Column(name="PRAI_CODE_01")
    private String praiCode01 ;
    
    @Column(name="PRAI_CODE_02")
    private String praiCode02 ;
    
    @Column(name="PRAI_CODE_03")
    private String praiCode03 ;
    
    @Column(name="PRAI_CODE_04")
    private String praiCode04 ;
    
    @Column(name="PRAI_CODE_05")
    private String praiCode05 ;
    
    @Column(name="PRAI_CODE_06")
    private String praiCode06 ;
    
    @Column(name="PRAI_CODE_07")
    private String praiCode07 ;
    
    @Column(name="PRAI_CODE_08")
    private String praiCode08 ;
    
    @Column(name="PRAI_CODE_09")
    private String praiCode09 ;
    
    @Column(name="PRAI_CODE_10")
    private String praiCode10 ;
    
    @Column(name="PRAI_CODE_11")
    private String praiCode11 ;
    
    @Column(name="PRAI_CODE_12")
    private String praiCode12 ;
    
    @Column(name="PRAI_CODE_13")
    private String praiCode13 ;
    
    @Column(name="PRAI_CODE_14")
    private String praiCode14 ;
    
    @Column(name="PRAI_CODE_15")
    private String praiCode15 ;
    
    @Column(name="PRAI_CODE_16")
    private String praiCode16 ;
    
    @Column(name="PRAI_CODE_17")
    private String praiCode17 ;
    
    @Column(name="PRAI_CODE_18")
    private String praiCode18 ;
    
    @Column(name="PRAI_CODE_19")
    private String praiCode19 ;
    
    @Column(name="PRAI_CODE_20")
    private String praiCode20 ;
    
    @Column(name="PRAI_YN_01")
    private String praiYn01 ;
    
    @Column(name="PRAI_YN_02")
    private String praiYn02 ;
    
    @Column(name="PRAI_YN_03")
    private String praiYn03 ;
    
    @Column(name="PRAI_YN_04")
    private String praiYn04 ;
    
    @Column(name="PRAI_YN_05")
    private String praiYn05 ;
    
    @Column(name="PRAI_YN_06")
    private String praiYn06 ;
    
    @Column(name="PRAI_YN_07")
    private String praiYn07;
    
    @Column(name="PRAI_YN_08")
    private String praiYn08;
    
    @Column(name="PRAI_YN_09")
    private String praiYn09 ;
    
    @Column(name="PRAI_YN_10")
    private String praiYn10 ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_01")
    private Date praiDate01 ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_02")
    private Date praiDate02 ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_03")
    private Date praiDate03 ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_04")
    private Date praiDate04 ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_05")
    private Date praiDate05 ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_06")
    private Date praiDate06 ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_07")
    private Date praiDate07 ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_08")
    private Date praiDate08 ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_09")
    private Date praiDate09 ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_10")
    private Date praiDate10 ;
    
    @Column(name="PRAI_NUM_01")
    private BigDecimal     praiNum01 ;
    
    @Column(name="PRAI_NUM_02")
    private BigDecimal     praiNum02 ;
    
    @Column(name="PRAI_NUM_03")
    private BigDecimal     praiNum03 ;
    
    @Column(name="PRAI_NUM_04")
    private BigDecimal     praiNum04 ;
    
    @Column(name="PRAI_NUM_05")
    private BigDecimal     praiNum05 ;

    
    @Column(name="PRAI_DATA_02")
    private String     praiData02 ;

  
    
    @Column(name="PRAI_DATA_04")
    private String     praiData04 ;

    @Column(name="PRAI_DATA_05")
    private String     praiData05 ;
    
    @Column(name="PRAI_DATA_06")
    private String     praiData06 ;

    @Column(name="PRAI_DATA_07")
    private String     praiData07 ;

    @Column(name="PRAI_DATA_08")
    private String     praiData08 ;
    
    @Column(name="PRAI_DATA_09")
    private String     praiData09 ;

    @Column(name="PRAI_DATA_10")
    private String     praiData10 ;
    
    @Column(name="PRAI_DATA_11")
    private String     praiData11 ;

    @Column(name="PRAI_DATA_12")
    private String     praiData12 ;

    @Column(name="PRAI_DATA_13")
    private String     praiData13 ;
    
    @Column(name="PRAI_DATA_14")
    private String     praiData14 ;

    @Column(name="PRAI_DATA_15")
    private String     praiData15 ;
    
    @Column(name="PRAI_DATA_16")
    private String     praiData16 ;

    @Column(name="PRAI_DATA_17")
    private String     praiData17 ;

    @Column(name="PRAI_DATA_18")
    private String     praiData18 ;
    
    @Column(name="PRAI_DATA_19")
    private String     praiData19 ;

    @Column(name="PRAI_DATA_20")
    private String     praiData20 ;
    
    @Column(name="PRAI_REMARKS_01")
    private String     praiRemarks01 ;

    @Column(name="PRAI_REMARKS_02")
    private String     praiRemarks02 ;
    
    @Column(name="PRAI_REMARKS_03")
    private String     praiRemarks03 ;

    @Column(name="PRAI_REMARKS_04")
    private String     praiRemarks04 ;
    
    @Column(name="PRAI_REMARKS_05")
    private String     praiRemarks05 ;
    
    @Column(name="PRAI_CR_UID")
    private String     praiCrUid ;
    
    
    
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_CR_DT")
    private Date     praiCrDt ;

    @Column(name="PRAI_UPD_UID")
    private String     praiUpdUid ;
    
    @Column(name="PRAI_RISK_ID")
    private String     praiRiskId ;

    @Column(name="PRAI_PERIOD_UNIT")
    private String     praiPeriodUnit ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_UPD_DT")
    private Date     praiUpdDt ;
    
    @Column(name="PRAI_PML_AMT_FC")
    private BigDecimal     praiPmlAmtFc ;
    
    @Column(name="PRAI_PML_AMT_LC_1")
    private BigDecimal     praiPmlAmtLc1 ;
    
    @Column(name="PRAI_PML_AMT_LC_2")
    private BigDecimal     praiPmlAmtLc2 ;
    
    @Column(name="PRAI_PML_AMT_LC_3")
    private BigDecimal     praiPmlAmtLc3;
    
    @Column(name="PRAI_PML_PERC")
    private BigDecimal     praiPmlPerc;
    
    @Column(name="PRAI_AOA_LIMIT_FC")
    private BigDecimal     praiAoaLimitFc ;

    @Column(name="PRAI_AOA_LIMIT_LC_1")
    private BigDecimal     praiAoaLimitLc1 ;

    @Column(name="PRAI_AOA_LIMIT_LC_2")
    private BigDecimal     praiAoaLimitLc2 ;

    @Column(name="PRAI_AOA_LIMIT_LC_3")
    private BigDecimal     praiAoaLimitLc3 ;
    
    @Column(name="PRAI_ORG_AOA_LIMIT_FC")
    private BigDecimal     praiOrgAoaLimitFc ;
    
    @Column(name="PRAI_ORG_AOA_LIMIT_LC_1")
    private BigDecimal     praiOrgAoaLimitLc1 ;

    @Column(name="PRAI_ORG_AOA_LIMIT_LC_2")
    private BigDecimal     praiOrgAoaLimitLc2 ;

    @Column(name="PRAI_ORG_AOA_LIMIT_LC_3")
    private BigDecimal     praiOrgAoaLimitLc3 ;
    
    @Column(name="PRAI_AOY_LIMIT_FC")
    private BigDecimal     praiAoyLimitFc ;
    
    @Column(name="PRAI_AOY_LIMIT_LC_1")
    private BigDecimal     praiAoyLimitLc1 ;
    
    @Column(name="PRAI_AOY_LIMIT_LC_2")
    private BigDecimal     praiAoyLimitLc2 ;
    
    @Column(name="PRAI_AOY_LIMIT_LC_3")
    private BigDecimal     praiAoyLimitLc3 ;
    
    @Column(name="PRAI_ORG_AOY_LIMIT_FC")
    private BigDecimal     praiOrgAoyLimitFc ;

    @Column(name="PRAI_ORG_AOY_LIMIT_LC_1")
    private BigDecimal     praiOrgAoyLimitLc1 ;

    @Column(name="PRAI_ORG_AOY_LIMIT_LC_2")
    private BigDecimal     praiOrgAoyLimitLc2 ;

    @Column(name="PRAI_ORG_AOY_LIMIT_LC_3")
    private BigDecimal     praiOrgAoyLimitLc3 ;

    @Column(name="PRAI_RISK_SR_NO")
    private BigDecimal     praiRiskSrNo ;
    
    @Column(name="PRAI_COMP_CODE")
    private String     praiCompCode ;

    @Column(name="PRAI_DIVN_CODE")
    private String     praiDivnCode ;

    @Column(name="PRAI_DEPT_CODE")
    private String     praiDeptCode ;

    @Column(name="PRAI_DS_TYPE")
    private String     praiDsType ;

    @Column(name="PRAI_PROD_CODE")
    private String     praiProdCode ;
    
    @Column(name="PRAI_PERIOD")
    private BigDecimal     praiPeriod ;

    @Column(name="PRAI_PI_SYS_ID")
    private BigDecimal     praiPiSysId ;
    
    @Column(name="PRAI_NUM_06")
    private BigDecimal     praiNum06 ;

    @Column(name="PRAI_NUM_07")
    private BigDecimal     praiNum07 ;

    @Column(name="PRAI_NUM_08")
    private BigDecimal     praiNum08 ;

    @Column(name="PRAI_NUM_09")
    private BigDecimal     praiNum09 ;

    @Column(name="PRAI_NUM_10")
    private BigDecimal     praiNum10 ;

    @Column(name="PRAI_NUM_11")
    private BigDecimal     praiNum11 ;

    @Column(name="PRAI_NUM_12")
    private BigDecimal     praiNum12 ;
    
    @Column(name="PRAI_NUM_13")
    private BigDecimal     praiNum13 ;

    @Column(name="PRAI_NUM_14")
    private BigDecimal     praiNum14 ;

    @Column(name="PRAI_NUM_15")
    private BigDecimal     praiNum15 ;

    @Column(name="PRAI_NUM_16")
    private BigDecimal     praiNum16 ;

    @Column(name="PRAI_NUM_17")
    private BigDecimal     praiNum17 ;

    @Column(name="PRAI_NUM_18")
    private BigDecimal     praiNum18 ;

    @Column(name="PRAI_NUM_19")
    private BigDecimal     praiNum19 ;

    @Column(name="PRAI_NUM_20")
    private BigDecimal     praiNum20 ;

    @Column(name="PRAI_NUM_21")
    private BigDecimal     praiNum21 ;

    @Column(name="PRAI_NUM_22")
    private BigDecimal     praiNum22 ;

    @Column(name="PRAI_NUM_23")
    private BigDecimal     praiNum23 ;

    @Column(name="PRAI_NUM_24")
    private BigDecimal     praiNum24 ;

    @Column(name="PRAI_NUM_25")
    private BigDecimal     praiNum25 ;
    
    @Column(name="PRAI_YN_11")
    private BigDecimal     praiYn11 ;
    
    @Column(name="PRAI_YN_12")
    private BigDecimal     praiYn12 ;
    
    @Column(name="PRAI_YN_13")
    private BigDecimal     praiYn13 ;
    
    @Column(name="PRAI_YN_14")
    private BigDecimal     praiYn14 ;
    
    @Column(name="PRAI_YN_15")
    private BigDecimal     praiYn15;
    
    @Column(name="PRAI_YN_16")
    private BigDecimal     praiYn16 ;
    
    @Column(name="PRAI_YN_17")
    private BigDecimal     praiYn17 ;
    
    @Column(name="PRAI_YN_18")
    private BigDecimal     praiYn18;
    
    @Column(name="PRAI_YN_19")
    private BigDecimal     praiYn19;

    @Column(name="PRAI_YN_20")
    private BigDecimal     praiYn20;

    @Column(name="PRAI_YN_21")
    private BigDecimal     praiYn21;

    @Column(name="PRAI_YN_22")
    private BigDecimal     praiYn22;

    @Column(name="PRAI_YN_23")
    private BigDecimal     praiYn23;

    @Column(name="PRAI_YN_24")
    private BigDecimal     praiYn24;

    @Column(name="PRAI_YN_25")
    private BigDecimal     praiYn25;
    
    @Column(name="PRAI_DATA_21")
    private BigDecimal     praiData21;

    @Column(name="PRAI_DATA_22")
    private BigDecimal     praiData22;

    @Column(name="PRAI_DATA_23")
    private BigDecimal     praiData23;

    @Column(name="PRAI_DATA_24")
    private BigDecimal     praiData24;

    @Column(name="PRAI_DATA_25")
    private BigDecimal     praiData25;
    
    @Column(name="PRAI_DATA_26")
    private BigDecimal     praiData26;

    @Column(name="PRAI_DATA_27")
    private BigDecimal     praiData27;

    @Column(name="PRAI_DATA_28")
    private BigDecimal     praiData28;

    @Column(name="PRAI_DATA_29")
    private BigDecimal     praiData29;

    @Column(name="PRAI_DATA_30")
    private BigDecimal     praiData30;
    

    @Column(name="PRAI_DATA_31")
    private BigDecimal     praiData31;

    @Column(name="PRAI_DATA_32")
    private BigDecimal     praiData32;

    @Column(name="PRAI_DATA_33")
    private BigDecimal     praiData33;

    @Column(name="PRAI_DATA_34")
    private BigDecimal     praiData34;

    @Column(name="PRAI_DATA_35")
    private BigDecimal     praiData35;
    
    @Column(name="PRAI_DATA_36")
    private BigDecimal     praiData36;

    @Column(name="PRAI_DATA_37")
    private BigDecimal     praiData37;

    @Column(name="PRAI_DATA_38")
    private BigDecimal     praiData38;

    @Column(name="PRAI_DATA_39")
    private BigDecimal     praiData39;

    @Column(name="PRAI_DATA_40")
    private BigDecimal     praiData40;

    @Column(name="PRAI_CODE_21")
    private String     praiCode21;

    @Column(name="PRAI_CODE_22")
    private BigDecimal     praiCode22;



    @Column(name="PRAI_CODE_25")
    private BigDecimal     praiCode25;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_11")
    private Date     praiDate11;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_12")
    private Date     praiDate12;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_13")
    private Date     praiDate13;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_14")
    private Date     praiDate14;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_15")
    private Date     praiDate15;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_16")
    private Date     praiDate16;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_17")
    private Date     praiDate17;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_18")
    private Date     praiDate18;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_19")
    private Date     praiDate19;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_20")
    private Date     praiDate20;

    @Column(name="PRAI_REMARKS_06")
    private String     praiRemarks06;

    @Column(name="PRAI_REMARKS_07")
    private String     praiRemarks07;

    @Column(name="PRAI_REMARKS_08")
    private String     praiRemarks08;

    @Column(name="PRAI_REMARKS_09")
    private String     praiRemarks09;

    @Column(name="PRAI_REMARKS_10")
    private String     praiRemarks10;
    

    @Column(name="PRAI_ORG_CUM_BONUS_FC")
    private BigDecimal praiOrgCumBonusFc ;

    @Column(name="PRAI_ORG_CUM_BONUS_LC_1")
    private BigDecimal praiOrgCumBonusLc1 ;

    @Column(name="PRAI_ORG_CUM_BONUS_LC_2")
    private BigDecimal praiOrgCumBonusLc2 ;

    @Column(name="PRAI_ORG_CUM_BONUS_LC_3")
    private BigDecimal praiOrgCumBonusLc3 ;

    @Column(name="PRAI_REN_PRAI_SYS_ID")
    private BigDecimal praiRenPraiSysId ;
    
    @Column(name="PRAI_RISK_CLASS_CODE")
    private String     praiRiskClassCode ;

    @Column(name="PRAI_CLM_TOTAL_LOSS_YN")
    private String     praiClmTotalLossYn ;

    @Column(name="PRAI_ORG_PML_AMT_FC")
    private BigDecimal praiOrgPmlAmtFc ;

    @Column(name="PRAI_ORG_PML_AMT_LC_1")
    private BigDecimal praiOrgPmlAmtLc1 ; 

    @Column(name="PRAI_ORG_PML_AMT_LC_2")
    private BigDecimal praiOrgPmlAmtLc2 ; 

    @Column(name="PRAI_ORG_PML_AMT_LC_3")
    private BigDecimal praiOrgPmlAmtLc3 ; 

    @Column(name="PRAI_ORG_PML_PERC")
    private BigDecimal praiOrgPmlPerc ;

    @Column(name="PRAI_REN_SR_NO")
    private BigDecimal praiRenSrNo ; 

    @Column(name="PRAI_TPA_CODE")
    private String     praiTpaCode ;

    @Column(name="PRAI_POL_CLASSIFICATION")
    private String     praiPolClassification ;

    @Column(name="PRAI_COUNTRY_CODE")
    private String     praiCountryCode ;

    @Column(name="PRAI_LOCATION_CODE")
    private String     praiLocationCode ;


    @Column(name="PRAI_NO_REN_CLM")
    private BigDecimal praiNoRenClm ; 

    @Column(name="PRAI_ANNUAL_PREM_FC")
    private BigDecimal praiAnnualPremFc ; 

    @Column(name="PRAI_ANNUAL_PREM_LC_1")
    private BigDecimal praiAnnualPremlc1 ; 

    @Column(name="PRAI_ANNUAL_PREM_LC_2")
    private BigDecimal praiAnnualPremlc2 ; 

    @Column(name="PRAI_ANNUAL_PREM_LC_3")
    private BigDecimal praiAnnualPremlc3 ;
    
    @Column(name="PRAI_CODE_26")
    private String     praiCode26 ;
    
    @Column(name="PRAI_CODE_27")
    private String     praiCode27 ;
    
    @Column(name="PRAI_CODE_28")
    private String     praiCode28 ;
    
    @Column(name="PRAI_CODE_29")
    private String     praiCode29 ;
    
    @Column(name="PRAI_CODE_30")
    private String     praiCode30 ;

    @Column(name="PRAI_NCB_YRS")
    private BigDecimal praiNcbYrs ; 

    @Column(name="PRAI_CNCT_SYS_ID")
    private BigDecimal praiCnctSysId ;

    @Column(name="PRAI_RA_APPL_YN")
    private String     praiRaApplYn ;
    
    @Column(name="PRAI_RISK_REF_NO")
    private String     praiRiskRefNo ;

    @Column(name="PRAI_IDV_FC")
    private BigDecimal praiIdvFc ; 

    @Column(name="PRAI_IDV_LC_1")
    private BigDecimal praiIdvLc1 ;

    @Column(name="PRAI_IDV_LC_2")
    private BigDecimal praiIdvLc2 ;

    @Column(name="PRAI_IDV_LC_3")
    private BigDecimal praiIdvLc3 ;
    

    @Column(name="PRAI_TOT_SI_FC")
    private BigDecimal praiTotSiFc ;

    @Column(name="PRAI_TOT_SI_LC_1")
    private BigDecimal praiTotSiLc1 ;

    @Column(name="PRAI_TOT_SI_LC_2")
    private BigDecimal praiTotSiLc2 ;
    
    @Column(name="PRAI_TOT_SI_LC_3")
    private BigDecimal praiTotSiLc3;

    @Column(name="PRAI_FIRST_LOSS_PERC")
    private BigDecimal praiFirstLossPerc ;
    
    @Column(name="PRAI_ORG_TOT_SI_FC")
    private BigDecimal praiOrgTotSiFc;
    
    @Column(name="PRAI_ORG_TOT_SI_LC_1")
    private BigDecimal praiOrgTotSiLc1;
    
    @Column(name="PRAI_ORG_TOT_SI_LC_2")
    private BigDecimal praiOrgTotSiLc2;
    
    @Column(name="PRAI_ORG_TOT_SI_LC_3")
    private BigDecimal praiOrgTotSiLc3;
    
    @Column(name="PRAI_OUR_SHARE_PERC")
    private BigDecimal praiOurSharePerc;
    
    @Column(name="PRAI_OUR_SHARE_SI_FC")
    private BigDecimal praiOurShareSiFc;

    @Column(name="PRAI_OUR_SHARE_SI_LC_1")
    private BigDecimal praiOurShareSiLc1;

    @Column(name="PRAI_OUR_SHARE_SI_LC_2")
    private BigDecimal praiOurShareSiLc2;

    @Column(name="PRAI_OUR_SHARE_SI_LC_3")
    private BigDecimal praiOurShareSiLc3;

    @Column(name="PRAI_OUR_SHARE_PREM_FC")
    private BigDecimal praiOurSharePremFc;

    @Column(name="PRAI_OUR_SHARE_PREM_LC_1")
    private BigDecimal praiOurSharePremLc1;

    @Column(name="PRAI_OUR_SHARE_PREM_LC_2")
    private BigDecimal praiOurSharePremLc2;

    @Column(name="PRAI_OUR_SHARE_PREM_LC_3")
    private BigDecimal praiOurSharePremLc3;

    @Column(name="PRAI_ORG_OUR_SHARE_PERC")
    private BigDecimal praiOrgOurSharePerc;
    

    @Column(name="PRAI_ORG_OUR_SHARE_SI_FC")
    private BigDecimal praiOrgOurShareSiFc;
    
    @Column(name="PRAI_ORG_OUR_SHARE_SI_LC_1")
    private BigDecimal praiOrgOurShareSiLc1;
    
    @Column(name="PRAI_ORG_OUR_SHARE_SI_LC_2")
    private BigDecimal praiOrgOurShareSiLc2;
    
    @Column(name="PRAI_ORG_OUR_SHARE_SI_LC_3")
    private BigDecimal praiOrgOurShareSiLc3;
    
    @Column(name="PRAI_ORG_OUR_SHARE_PREM_FC")
    private BigDecimal praiOrgOurSharePremFc;
    
    @Column(name="PRAI_ORG_OUR_SHARE_PREM_LC_1")
    private BigDecimal praiOrgOurSharePremLc1;
    
    @Column(name="PRAI_ORG_OUR_SHARE_PREM_LC_2")
    private BigDecimal praiOrgOurSharePremLc2;
    
    @Column(name="PRAI_ORG_OUR_SHARE_PREM_LC_3")
    private BigDecimal praiOrgOurSharePremLc3;
    
    
    @Column(name="PRAI_MAINT_PERIOD_UNIT")
    private String     praiMaintPeriodUnit ;
    
    @Column(name="PRAI_MAINT_PERIOD")
    private BigDecimal praiMaintPeriod;
    
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_MAINT_FM_DT")
    private Date     praiMaintFmDt ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_MAINT_TO_DT")
    private Date praiMaintToDt;
    
    @Column(name="PRAI_TESTING_PERIOD_UNIT")
    private String     praiTestingPeriodUnit ;
    
    @Column(name="PRAI_TESTING_PERIOD")
    private BigDecimal praiTestingPeriod;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_TESTING_FM_DT")
    private Date     praiTestingFmDt ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_TESTING_TO_DT")
    private Date praiTestingToDt;
    
    
    
    
    
    @Column(name="PRAI_NAJM_STS")
    private String     praiNajmSts ;

    @Column(name="PRAI_NAJM_REMARKS")
    private String     praiNajmRemarks ;
    
    @Column(name="PRAI_COINS_POOL_CODE")
    private String     praiCoinsPoolCode ;

    @Column(name="PRAI_CERT_REQ_YN")
    private String     praiCertReqYn ;
    
    @Column(name="PRAI_CERT_TYPE")
    private String     praiCertType ;

    @Column(name="PRAI_CERT_MODE")
    private String     praiCertMode ;
    
    @Column(name="PRAI_CERT_NO")
    private String     praiCertNo ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_CERT_FM_DT")
    private Date     praiCertFmDt ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_CERT_TO_DT")
    private Date     praiCertToDt ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_21")
    private Date     praiDate21 ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_22")
    private Date     praiDate22 ;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_23")
    private Date     praiDate23 ;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_24")
    private Date     praiDate24 ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="PRAI_DATE_25")
    private Date     praiDate25 ;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="REQUEST_TIME")
    private Date     requestTime ;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="RESPONSE_TIME")
    private Date     responseTime ;
    
    
    @Column(name="STATUS")
    private String     status ;

    @Column(name="SERVICE_ID")
    private String     serviceId ;

    @Column(name="P_WS_RESPONSE_TYPE")
    private String     pWsResponseType ;

    @Column(name="P_WS_ERROR")
    private String     pWsError ;
    @Column(name="SERVICE_ACTION")
    private String     serviceAction ;
    @Column(name="REQUEST_REFERENCE_NO")
    private String     requestReferenceNo ;

    @Column(name = "POL_DS_CODE", length = 200)
    private String polDsCode;
  

    @Column(name="PRAI_SYS_ID")
    private String     praiSysId ;
    

    @Column(name = "PRAI_END_NO_IDX", length = 200)
    private String praiEndNoIdx;
}



