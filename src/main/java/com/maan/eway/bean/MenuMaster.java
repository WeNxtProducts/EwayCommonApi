package com.maan.eway.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.maan.eway.bean.MenuMaster.MenuMasterBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;




/**
* Domain class for entity "MenuMaster"
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
@Table(name="MENU_MASTER")


public class MenuMaster implements Serializable {
 
private static final long serialVersionUID = 1L;
 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="MENU_ID", nullable=false)
    private Integer menuId ;

    //--- ENTITY DATA FIELDS 
    @Column(name="MENU_NAME", length=300)
    private String     menuName ;

    @Column(name="MENU_URL", length=4000)
    private String     menuUrl ;

    @Column(name="PARENT_MENU", length=10)
    private String     parentMenu ;

    @Column(name="BRANCH_CODE", length=10)
    private String     branchCode ;

    @Column(name="PRODUCT_ID")
    private Integer productId ;

    @Column(name="STATUS", nullable=false, length=1)
    private String     status ;

    @Column(name="RSACODE", length=25)
    private String     rsacode ;

    @Column(name="USERTYPE", length=100)
    private String     usertype ;

    @Column(name="ISCLICK", length=100)
    private String     isclick ;

    @Column(name="DISPLAY_ORDER")
    private Integer displayOrder ;
    
    @Column(name="DISPLAY_YN")
    private String  displayYn ;

    @Column(name="MENU_LOGO")
    private String  menuLogo ;

    //--- ENTITY LINKS ( RELATIONSHIP )


}


