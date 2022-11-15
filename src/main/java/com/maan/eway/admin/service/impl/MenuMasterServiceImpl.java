package com.maan.eway.admin.service.impl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.admin.req.MenuServiceReq;
import com.maan.eway.admin.res.AdminListRes;
import com.maan.eway.admin.res.MenuServiceRes;
import com.maan.eway.admin.res.UserMenuListRes;
import com.maan.eway.admin.service.MenuMasterService;
import com.maan.eway.auth.dto.Menu;
import com.maan.eway.bean.MenuMaster;
import com.maan.eway.repository.MenuMasterRepository;
import com.maan.eway.res.CustomerDetailsSearchRes;

@Service
@Transactional
public class MenuMasterServiceImpl implements MenuMasterService{

	@Autowired
	private MenuMasterRepository menurepo;
	
	@PersistenceContext
	private EntityManager em;
	
	private Logger log = LogManager.getLogger(MenuMasterService.class); 
	
	@Override
	public MenuServiceRes menudisplay(MenuServiceReq req) {
		MenuServiceRes response = new MenuServiceRes();
		ModelMapper mapper = new ModelMapper();
		try {
			List<Menu> menus=new ArrayList<Menu>();
			
			CriteriaBuilder cb=	em.getCriteriaBuilder();
			CriteriaQuery<MenuMaster> criteriaQuery = cb.createQuery(MenuMaster.class);
			// Find All
			Root<MenuMaster> m =criteriaQuery.from(MenuMaster.class);
			// Select
			criteriaQuery.select(m);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(m.get("menuId")));

			Predicate  p1 = cb.equal(m.get("displayYn") , "Y");
			Predicate  p2 = cb.equal(m.get("status") , "Y");
			Predicate p3 = cb.like(m.get("usertype"), "%" + req.getUserType() + "%" );
			
			
			
			TypedQuery<MenuMaster> query = em.createQuery(criteriaQuery);
			List<MenuMaster> adminMenulist = new ArrayList<MenuMaster>();
			List<MenuMaster> otherMenulist = new ArrayList<MenuMaster>();
			
			if(req.getSubUserType().equalsIgnoreCase("both")    ) {	
				criteriaQuery.where(p1,p2,p3).orderBy(orderList);	
				query = em.createQuery(criteriaQuery);
				otherMenulist = query.getResultList();
				
				p3 =  cb.like(m.get("usertype"), "%" + "admin" + "%" );
				criteriaQuery.where(p1,p2,p3).orderBy(orderList);
				query = em.createQuery(criteriaQuery);
				adminMenulist = query.getResultList();
				
			} else if (req.getSubUserType().equalsIgnoreCase("low")     ) {
				criteriaQuery.where(p1,p2,p3).orderBy(orderList);
				query = em.createQuery(criteriaQuery);
				otherMenulist = query.getResultList();
				
			} else if (req.getSubUserType().equalsIgnoreCase("high")     ) {
				p3 =  cb.like(m.get("usertype"), "%" + "admin" + "%" );
				criteriaQuery.where(p1,p2,p3).orderBy(orderList);
				query = em.createQuery(criteriaQuery);
				adminMenulist = query.getResultList();
			} else {
				criteriaQuery.where(p1,p2,p3).orderBy(orderList);	
				query = em.createQuery(criteriaQuery);
				otherMenulist = query.getResultList();
			}
			
			
			List<Menu> userResList = new ArrayList<Menu>();
			List<Menu> userMenus=new ArrayList<Menu>();
			List<Menu> adminReslist = new ArrayList<Menu>();
			List<Menu> adminMenus = new ArrayList<Menu>();
			
			// User Menu List 
			for (MenuMaster menuMaster : otherMenulist) {
				Menu menu = Menu.builder().title(menuMaster.getMenuName()).link(menuMaster.getMenuUrl()).id(menuMaster.getMenuId().toString()).parent(menuMaster.getParentMenu())
						.icon(menuMaster.getMenuLogo() ).isdesti(false).orderby(menuMaster.getDisplayOrder()==null?0:menuMaster.getDisplayOrder().longValue()).build();
				userMenus.add(menu);
			}
			 List<Menu> collect = userMenus.stream().filter(i-> "99999".equals(i.getParent())).collect(Collectors.toList());
			log.info("collect"+collect);
			 for (Menu men : collect) {
				 Menu menu = men;
				 menu.setChildren(userMenus.stream().filter(i -> (!"99999".equals(i.getParent()) && menu.getId().equals(i.getParent()))).collect(Collectors.toList()));
				 userResList.add(menu);
			}
			 
			// Admin Menu List 
			for (MenuMaster menuMaster : adminMenulist) {
				Menu menu = Menu.builder().title(menuMaster.getMenuName()).link(menuMaster.getMenuUrl()).id(menuMaster.getMenuId().toString()).parent(menuMaster.getParentMenu())
						.icon(menuMaster.getMenuLogo()).isdesti(false).orderby(menuMaster.getDisplayOrder()==null?0:menuMaster.getDisplayOrder().longValue()).build();
				adminMenus.add(menu);
			}
			List<Menu> collect2 = adminMenus.stream().filter(i-> "99999".equals(i.getParent())).collect(Collectors.toList());
			log.info("collect"+collect2);
			for (Menu men : collect2) {
				 Menu menu = men;
				 menu.setChildren(adminMenus.stream().filter(i -> (!"99999".equals(i.getParent()) && menu.getId().equals(i.getParent()))).collect(Collectors.toList()));
				 adminReslist.add(menu);
			}
			 	
			 response.setAdminlist(adminReslist);
			 response.setUserList(userResList);
		}
		catch(Exception e) {
		e.printStackTrace();
		log.info("Log Details"+e.getMessage());
		return null;
		}
		return response;
	}
	
}
