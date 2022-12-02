package com.maan.eway.service.impl.referal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import javax.persistence.Tuple;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.ConstantTableDetails;
import com.maan.eway.bean.DropdownTableDetails;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.req.referal.ReferralRequest;
import com.maan.eway.res.referal.MasterReferal;
import com.maan.eway.thread.MyTaskList;
import com.maan.eway.upgrade.criteria.CriteriaService;
import com.maan.eway.upgrade.criteria.SpecCriteria;

@Service
public class ReferalServiceImpl {
	
	private SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy")  ;
	@Autowired
	private CriteriaService crservice;

	public List<ReferralRequest> LoadConstant(CalcEngine engine) {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:{Y,R};"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(ConstantTableDetails.class, search, "itemId"); 
			result=crservice.getResult(criteria, 0, 50);
			List<ReferralRequest> refreqs=null;
			if(result!=null && result.size()>0) {
				
				refreqs=new ArrayList<ReferralRequest>();
				
				
				
				for(Tuple r :result) {
					String itemId=r.get("itemId")==null?"":r.get("itemId").toString();
					
					ReferralRequest req=ReferralRequest.builder().apiLink(r.get("apiUrl")==null?"":r.get("apiUrl").toString())
							.primaryTable(r.get("keyTable")==null?"":r.get("keyTable").toString())
							.primaryKey(r.get("keyName")==null?"":r.get("keyName").toString())
							.build();
					
					
					List<Tuple> loadDropdown = loadDropdown(engine, itemId);
					List<Map<String,String>> mps=null;
					if(loadDropdown!=null && loadDropdown.size()>0) {
						mps=new ArrayList<Map<String,String>>();	
						for(Tuple l :loadDropdown) {
							Map<String,String> mp=new HashMap<String,String>();
							mp.put("JsonKey", l.get("requestJsonKey")==null?"":l.get("requestJsonKey").toString());
							mp.put("JsonColum", l.get("requestColumn")==null?"":l.get("requestColumn").toString());
							mp.put("JsonTable", l.get("requestTable")==null?"":l.get("requestTable").toString());
							mps.add(mp);
						}
						req.setMp(mps);
					}
					refreqs.add(req);
				}
				
			}
			
			return refreqs;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	public List<Tuple> loadDropdown(CalcEngine engine,String itemId){
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:{Y,R};"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
			List<Tuple> result=null;
			SpecCriteria criteria = crservice.createCriteria(DropdownTableDetails.class, search, "requestId"); 
			result=crservice.getResult(criteria, 0, 50);

			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
		public List<MasterReferal> masterreferral(CalcEngine engine) throws ClassNotFoundException {
			
			List<ReferralRequest> loadConstant = LoadConstant(engine);
			List<Tuple> result =null;
			if(loadConstant!=null && loadConstant.size()>0) {
				String tablename =	loadConstant.get(0).getPrimaryTable();
					Class<?> tableClass = Class.forName("com.maan.eway.bean."+tablename);
					try {
						String search="requestReferenceNo:"+engine.getRequestReferenceNo()+";vehicleId:"+engine.getVehicleId()+";";
						SpecCriteria criteria = crservice.createCriteria(tableClass, search, "requestReferenceNo"); 
						result = crservice.getResult(criteria, 0, 1);
					}catch (Exception e) {
						e.printStackTrace();
					}
			}
			
			List<Callable<Object>> queue = new ArrayList<Callable<Object>>();
			
			for (ReferralRequest r : loadConstant) {
				System.out.println("PrimaryKey  : "+r.getPrimaryKey());
				r.setPrimaryId(result.get(0).get(r.getPrimaryKey())==null?"":result.get(0).get(r.getPrimaryKey()).toString());
				
				List<Map<String, String>> mp = r.getMp();
				List<String> list=new ArrayList<String>();
				for(Map<String, String> map:mp){
					String jsonKey = map.get("JsonKey");
					String jsonColum = map.get("JsonColum");
					String jsonValue = result.get(0).get(jsonColum)==null?"":result.get(0).get(jsonColum).toString();
							
					String value="\""+jsonKey+"\":\""+jsonValue+"\"";
					list.add(value);		
					  
				}
			 
				r.setApiRequest(StringUtils.join(list,','));
				
				queue.add(new ThreadReferralCall(r));
			} 
			
			
			
			
						
			
			
			 MyTaskList taskList = new MyTaskList(queue);		
			 ForkJoinPool forkjoin = new ForkJoinPool((queue.size()>1 ? (int )(queue.size()/2) : 1)); 
	         ConcurrentLinkedQueue<Future<Object>> invoke  = (ConcurrentLinkedQueue<Future<Object>>) forkjoin.invoke(taskList) ;
	         int success=0;
	         List<MasterReferal> list= new ArrayList<MasterReferal>();
				for (Future<Object> callable : invoke) {
					System.out.println(callable.getClass() + "," + callable.isDone());
					if (callable.isDone()) {
						try {
							MasterReferal map = (MasterReferal) callable.get();
							list.add(map);							
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}
						success++;
					}
				}
				return list;
			//ReferralRequest request;//=ReferralRequest.builder();
			
			//ThreadReferralCall th1=new ThreadReferralCall(null)
			
		}
}
