package com.maan.eway.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;




/**
* Domain class for entity "UwQuestionsDetails"
*
* @author Telosys Tools Generator
*
*/
@Entity
@DynamicInsert
@DynamicUpdate
@Data
@IdClass(UwQuestionsDetailsId.class)
@Table(name="uw_questions_details")


public class UwQuestionsDetails implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="COMPANY_ID", nullable=false, length=20)
    private String     companyId ;

    @Id
    @Column(name="PRODUCT_ID", nullable=false)
    private Integer    productId ;

    @Id
    @Column(name="BRANCH_CODE",length=20, nullable=false)
    private  String branchCode ;

    
    @Id
    @Column(name="REQUEST_REFERENCE_NO", nullable=false, length=20)
    private String     requestReferenceNo ;

    @Id
    @Column(name="VEHICLE_ID", nullable=false)
    private Integer    vehicleId ;

    @Id
    @Column(name="UW_QUESTION_ID", nullable=false)
    private Integer    uwQuestionId ;

    //--- ENTITY DATA FIELDS 
    @Column(name="UW_QUESTION_DESC", length=100)
    private String     uwQuestionDesc ;

    @Column(name="QUESTION_TYPE", length=100)
    private String     questionType ;

    @Column(name="VALUE", length=100)
    private String     value ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ENTRY_DATE")
    private Date       entryDate ;

    @Column(name="STATUS", length=2)
    private String     status ;

    @Column(name="REMARKS", length=100)
    private String     remarks ;

    @Column(name="CREATED_BY", length=100)
    private String     createdBy;

    @Column(name="UPDATED_BY", length=100)
    private String     updatedBy;
    
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UPDATED_DATE")
    private Date    updatedDate ;
	
    @Column(name="IS_REFERRAL", length=5)
    private String    isReferral;


}
