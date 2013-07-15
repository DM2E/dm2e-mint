package gr.ntua.ivml.mint.report;


import gr.ntua.ivml.mint.actions.UrlApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TransformationDetailsBeanFactory {
	
	

	String organizationId = null;
	private Date startDate;
	private Date endDate;
	
	


	public TransformationDetailsBeanFactory(String organizationId, Date startDate, Date endDate) {
		super();
		this.organizationId = organizationId;
		this.startDate = startDate; 
		this.endDate = endDate;
	}




	public List<TransformationDetailsBean> getTransformations(){			
		UrlApi api = new UrlApi();
		api.setOrganizationId(this.organizationId);		
		JSONObject json = api.listOrganizations();
		JSONArray result = (JSONArray) json.get("result");
		List<TransformationDetailsBean> all = new ArrayList<TransformationDetailsBean>();
		Iterator it = result.iterator();
		while (it.hasNext()){
			JSONObject jsonObject = (JSONObject) it.next();
			String orgId = jsonObject.get("dbID").toString();
			String orgname =jsonObject.getString("englishName").toString();
			TransformationDetailsBeanFactory factory = new TransformationDetailsBeanFactory(orgId, startDate, endDate);
			List<TransformationDetailsBean> tforms = new ArrayList<TransformationDetailsBean>();
			tforms = factory.getTransformations(organizationId);
			all.addAll(tforms);
		}
		
		
		Collections.sort(all);
		Collections.synchronizedCollection(all);
		List<TransformationDetailsBean> tlist = filterransformationsofSameParentDataset(all);
		Collections.synchronizedCollection(tlist);
		return tlist;
	
	}
	
	
	
	public List<TransformationDetailsBean> getTransformations(String orgid){			
		UrlApi api = new UrlApi();
		api.setOrganizationId(orgid);
		JSONObject json = api.listTransformations();
		
		return getTransformations(json,orgid);
	
	}
	

	public List<TransformationDetailsBean> getTransformations(JSONObject json,String orgid){
		List<TransformationDetailsBean> transformations = new ArrayList<TransformationDetailsBean>();
		JSONArray result = (JSONArray) json.get("result");
		Iterator<?> it = result.iterator();
		while (it.hasNext()){
			JSONObject jsonObject = (JSONObject) it.next();
			String name = jsonObject.get("name").toString();
			Date created = new Date(0);;
			Date lastModified = new Date(0);
			try {
				String dateCreated = (String) jsonObject.get("created");
//				created = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").parse(dateCreated); 
				created = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(dateCreated);

				String dateModified = (String) jsonObject.get("lastModified");
//				lastModified = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").parse(dateModified);
				lastModified = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(dateModified);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			if (!(created.compareTo(startDate)>0 && created.compareTo(endDate)<0 )&&!(lastModified.compareTo(startDate)>0 &&lastModified.compareTo(endDate)<0 )){
				continue;
			}
			
			Integer  itemCount =  (Integer) jsonObject.get("itemCount");
			if (itemCount==-1) itemCount = 0;
			Integer  validItems =  (Integer) jsonObject.get("validItems");
			if (validItems==-1) validItems = 0;
			Integer  invalidItems =  (Integer) jsonObject.get("invalidItems");
			if (invalidItems==-1) invalidItems = 0;
			
			
			String itemizerStatus = jsonObject.get("itemizerStatus").toString();
		
			
			
			JSONObject creator = (JSONObject) jsonObject.get("creator");
			String creatorId = null;
			String creatorName = null;
			if (creator.has("dbID")) {
				creatorId = creator.getString("dbID").toString();
			}
			if (creator.has("name")) {
				creatorName = creator.getString("name").toString();
			}

			String organizationName = null;
			String organizationId = null;

			
			JSONObject org = (JSONObject) jsonObject.get("organization");
			if (org.has("dbID")) {
				organizationId = org.getString("dbID").toString();
			}
			if (org.has("name")) {
				organizationName = org.getString("name").toString();
			}
			
			String mappingUsed = null;
			String targetSchema = null;
			String parentDataset = null;
			
			
			if (jsonObject.has("mappingUsed")) {
			 mappingUsed = jsonObject.get("mappingUsed").toString();
			}
			else continue;
			if (jsonObject.has("targetSchema")) {
			 targetSchema = jsonObject.get("targetSchema").toString();
			}
			if (jsonObject.has("parentDataset")) {
			 parentDataset = jsonObject.get("parentDataset").toString();
			}
			
			
			
		TransformationDetailsBean transformationDetailsBean = new TransformationDetailsBean(name, created, lastModified, creatorId, creatorName, itemCount,validItems,invalidItems,itemizerStatus, organizationId, organizationName,mappingUsed,targetSchema,parentDataset);
		
		transformations.add(transformationDetailsBean);		
		}
		
		
		return transformations;
		
		

	}
	
	//filters out transformationbeans that have the same parent Dataset name, keeps the latest one.The list must be previously sorted.
	//the list should be  previously sorted by organizationName so jasper will group by organization / page.
	public List<TransformationDetailsBean> filterransformationsofSameParentDataset(List<TransformationDetailsBean> list){
		List<TransformationDetailsBean> tlist = new ArrayList<TransformationDetailsBean>();
		
		String prev_parent = null;
		Iterator<TransformationDetailsBean> t = list.iterator();
		while (t.hasNext()){
			TransformationDetailsBean tbean = (TransformationDetailsBean) t.next();
			 String parent = tbean.getParentDataset();
			
			if  (parent.equals(prev_parent)){
				continue;
			}
			else {
				tlist.add(tbean);
				prev_parent = parent;
			}
		}
		return tlist;

	}
	
	public int gettotalValidItemsTransformed(){
		Integer totalValidTransformed = 0;
		List<TransformationDetailsBean> tlist = getTransformations();
		Iterator<TransformationDetailsBean> t = tlist.iterator();
		while (t.hasNext()){
			TransformationDetailsBean tbean = (TransformationDetailsBean) t.next();
			totalValidTransformed += tbean.getValidItems();
		}
		return totalValidTransformed ;
	}
}
