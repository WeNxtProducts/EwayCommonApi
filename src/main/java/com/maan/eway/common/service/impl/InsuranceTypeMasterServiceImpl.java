package com.maan.eway.common.service.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.InsuranceTypeMaster;
import com.maan.eway.common.req.GetProductMasterReq;
import com.maan.eway.common.req.ProductStructureMasterReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.ProductStructureMasterRes;
import com.maan.eway.common.service.InsuranceTypeMasterService;
import com.maan.eway.error.Error;
import com.maan.eway.repository.InsuranceTypeMasterRepository;
@Service
@Transactional
public class InsuranceTypeMasterServiceImpl  implements InsuranceTypeMasterService{

	@Autowired
	InsuranceTypeMasterRepository ProductStructureRepo;

	
	@Override
	public List<Error> validationInsuranceTypeMaster(ProductStructureMasterReq req) {
		// TODO Auto-generated method stub
		List<Error> errors = new ArrayList<Error>();
		try {
		if (StringUtils.isBlank(req.getCompanyid())) {
			 errors.add(new Error("01", "Company Id" , "Please Enter CompanyId"));
		 }
		if (StringUtils.isBlank(req.getIndustryTypeId())) {
			 errors.add(new Error("02", "Insurance Type Id" , "Please select  the insurance type"));
		 }
		if(StringUtils.isBlank(req.getSectionId()))
		{
			 errors.add(new Error("03", "Section Id" , "Please select  the section"));	
		}
		if(StringUtils.isBlank(req.getSectionName()))
		{
			 errors.add(new Error("04", "Section Name" , "Please enter the section name"));	
		}
		if(StringUtils.isBlank(req.getRemarks()))
		{
			 errors.add(new Error("05", "Remark" , "Please enter the remark"));	
		}
		if(StringUtils.isBlank(req.getDisplayOrder()))
		{
			 errors.add(new Error("06", "Display Order" , "Please enter the display order"));	
		}
		if(StringUtils.isBlank(req.getProductId()))
		{
			 errors.add(new Error("06", "Product Id" , "Please enter the product id"));	
		}
		}catch(Exception cc)
		{
			System.out.println("The Exception Occured in ValidationnsuranceTypeMaster ");
			cc.printStackTrace();
		}
		return errors;
	}
	
	
	@Override
	public CommonRes saveproductMaster(ProductStructureMasterReq req) {
		// TODO Auto-generated method stub
		CommonRes res = new CommonRes();
		int AmendId=0;
		try {
		
			//delete Block
			{
				InsuranceTypeMaster Exsisting =ProductStructureRepo.findByIndsutryTypeIdAndSectionId(req.getIndustryTypeId(),Integer.valueOf(req.getSectionId()));	
			if(Exsisting!=null) {
				AmendId=Exsisting.getAmendId()+1;
				ProductStructureRepo.delete(Exsisting);}
			}
			//insert block
		   
			Date entryDate = req.getEntryDate();
			 LocalDate nextYearDate = entryDate.toLocalDate().plusYears(1);
			  Date enddate = java.sql.Date.valueOf(nextYearDate);
			InsuranceTypeMaster newRecords = new InsuranceTypeMaster();
			newRecords.setCompanyId(StringUtils.isBlank(req.getCompanyid())?null:req.getCompanyid());
			newRecords.setProductId(StringUtils.isBlank(req.getProductId())?null:Integer.valueOf(req.getProductId()));
			newRecords.setIndsutryTypeId(StringUtils.isBlank(req.getIndustryTypeId())? "0":req.getIndustryTypeId());
			newRecords.setIndsutryTypeDesc(StringUtils.isBlank(req.getIndustryTypeDesc())?null:req.getIndustryTypeDesc());
			newRecords.setSectionId(StringUtils.isBlank(req.getSectionId())?0:Integer.valueOf(req.getSectionId()));
			newRecords.setSectionName(StringUtils.isBlank(req.getSectionName())?null:req.getSectionName());
			newRecords.setStatus(StringUtils.isBlank(req.getStatus())?null:req.getStatus());
			newRecords.setCreatedBy(StringUtils.isBlank(req.getCreatedBy())?null:req.getCreatedBy());
			newRecords.setDisplayOrder(StringUtils.isBlank(req.getDisplayOrder())?null:Integer.valueOf(req.getDisplayOrder()));
			newRecords.setRemarks(StringUtils.isBlank(req.getRemarks())?null:req.getRemarks());
			newRecords.setEntryDate(entryDate);
			newRecords.setEffectiveDateStart(entryDate);
			newRecords.setEffectiveDateEnd(entryDate);
			newRecords.setEffectiveDateEnd(enddate);
			newRecords.setAmendId(AmendId);
			newRecords.setDisplayOrder(StringUtils.isBlank(req.getDisplayOrder())?null:Integer.valueOf(req.getDisplayOrder()));
			
			ProductStructureRepo.saveAndFlush(newRecords);
		
			res.setCommonResponse("Inserted Successfully......");
			res.setIsError(false);
			res.setMessage("Success");
		}catch(Exception in)
		{
		System.out.println("**********The Exception Occured in   Insurance Type MasterSave****************");
		in.printStackTrace();
		
		res.setCommonResponse("Oops Something Went Wrong......");
		res.setIsError(true);
		res.setMessage("Failed");
		}
		return res;
	}
 
	public List<ProductStructureMasterReq> getAllProductStructureMaster(GetProductMasterReq req)
	{
		List<ProductStructureMasterReq> result=null;
		try {
			List<ProductStructureMasterReq> result1 = new ArrayList<>();
			List<InsuranceTypeMaster> data =ProductStructureRepo.findByCompanyIdAndProductIdAndStatus(req.getCompanyId(),Integer.valueOf(req.getProductid()),"Y");
			for(InsuranceTypeMaster dd:data)
			{
				ProductStructureMasterReq records=new DozerBeanMapper().map(dd,ProductStructureMasterReq.class);
				records.setIndustryTypeId(dd.getIndsutryTypeId());
				records.setCompanyid(dd.getCompanyId());
				result1.add(records);
			}
			result=result1;
			
		}catch(Exception cc)
		{
		System.out.println("The Exception Occured in get All  get Insurance Type Master");	
		return null;
		}
		return result;
		
	}
	public CommonRes getInsuranceMaster(GetProductMasterReq req)
	{
		CommonRes res = new CommonRes();
	ProductStructureMasterReq result=null;
		try {
			
		    InsuranceTypeMaster data =ProductStructureRepo.findByIndsutryTypeIdAndSectionId(req.getIndsutryTypeId(),Integer.valueOf(req.getSectionId()));
		    if(data!=null)
		    {
		    result=new DozerBeanMapper().map(data, ProductStructureMasterReq.class);
		    result.setCompanyid(data.getCompanyId());
		    result.setIndustryTypeId(data.getIndsutryTypeId());
		    result.setIndustryTypeDesc(data.getIndsutryTypeDesc());
		    res.setCommonResponse(result);
		  
		    res.setIsError(false);
		    res.setMessage("Success");
		    }
		    else {
		    	  res.setCommonResponse(null);
				   res.setIsError(true);
				   res.setMessage("failed");
		    }
		}catch(Exception cc)
		{
		System.out.println("The Exception Occured in get Insurance Type Master");	
		return res;
		}
		return res;
		
	}
	public List<ProductStructureMasterRes> getByIndustryTypeId(GetProductMasterReq sneha)
	{
		List<ProductStructureMasterRes> result=null;
		try {
			List<InsuranceTypeMaster> getdata=	ProductStructureRepo.findByIndsutryTypeIdAndStatus(sneha.getIndsutryTypeId(),"Y");
			List<ProductStructureMasterRes> result1 = new ArrayList<>();
			for(InsuranceTypeMaster dd:getdata)
			{
				ProductStructureMasterRes data = new ProductStructureMasterRes();
				data.setIndustryType(dd.getIndsutryTypeId());
				data.setSectionid(dd.getSectionId());
				data.setStatus(dd.getStatus());
				data.setSectionName(dd.getSectionName());
				result1.add(data);
			}
			result=result1;
		}catch(Exception cc)
		{
		 System.out.println("***************Exception Occured in  get Insurance Type Master****************");
        cc.printStackTrace();
        return null;
		}
		return result;
	}

	@Override
	public CommonRes DeleteproductStructureMaster(GetProductMasterReq req) {
		// TODO Auto-generated method stub
		CommonRes res=new CommonRes();
		String result="Not Data Found";
		try {
			
			
			InsuranceTypeMaster getdata=null;
			if(StringUtils.isNotBlank(req.getIndsutryTypeId()) && StringUtils.isNotBlank(req.getSectionId()))
			{
			 getdata=ProductStructureRepo.findByIndsutryTypeIdAndSectionId(req.getIndsutryTypeId(),Integer.valueOf(req.getSectionId()));
			}
			if(getdata!=null)
			{
				ProductStructureRepo.delete(getdata);
				result="Data Deleted Successfully";
			}
			res.setCommonResponse(result);
			res.setMessage("Success");
			res.setIsError(false);
			
		}catch(Exception dd)
		{
			System.out.println("**************The Exception Occured in Delete  Insurance Type Master*************");
			dd.printStackTrace();
			res.setCommonResponse(result);
			res.setMessage("Success");
			res.setIsError(false);
			
		}
		return res;
	}

	
}
