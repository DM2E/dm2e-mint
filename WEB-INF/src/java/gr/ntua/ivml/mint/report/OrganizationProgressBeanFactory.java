package gr.ntua.ivml.mint.report;


import gr.ntua.ivml.mint.actions.UrlApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class OrganizationProgressBeanFactory {
	String organizationId; 
	Date startDate;
	Date endDate;

	PublicationDetailsBeanFactory publicationDetailsBeanFactory ;
	TransformationDetailsBeanFactory transformationDetailsBeanFactory ;
	OaiPublicationDetailsBeanFactory oaipublicationbeanFactory ;
	OrganizationGoalsSummaryBeanFactory orggoalsFactory;

	public OrganizationProgressBeanFactory(String id,Date startdate,Date enddate ) {
		super();

		this.organizationId = id;
		this.startDate = startdate;
		this.endDate = enddate;

		this.transformationDetailsBeanFactory = new TransformationDetailsBeanFactory(id,startDate,endDate);
		this.oaipublicationbeanFactory = new OaiPublicationDetailsBeanFactory(id,startDate,endDate);
		this.publicationDetailsBeanFactory = new PublicationDetailsBeanFactory(id, startDate, endDate);
		this.orggoalsFactory = new OrganizationGoalsSummaryBeanFactory(id, startDate, endDate);
	}




	public String getOrganizationId() {
		return organizationId;
	}



	public void setOrganizationid(String organizationId) {
		this.organizationId = organizationId;
	}







	public Date getStartDate() {
		return startDate;
	}




	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}




	public Date getEndDate() {
		return endDate;
	}




	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}




	public List<OrganizationProgressDetailsBean> getOrgProgressbeans(){		
		UrlApi api = new UrlApi();
		api.setOrganizationId(this.organizationId);		
		JSONObject json = api.listOrganizations();
		JSONArray result = (JSONArray) json.get("result");
		List<OrganizationProgressDetailsBean> all = new ArrayList<OrganizationProgressDetailsBean>();
		Iterator it = result.iterator();
		while (it.hasNext()){
			JSONObject jsonObject = (JSONObject) it.next();
			String organizationId = jsonObject.get("dbID").toString();
			String organizationName = jsonObject.getString("englishName");
			if (organizationName.equals("NTUA")) continue;
			OrganizationProgressBeanFactory factory = new OrganizationProgressBeanFactory(organizationId, startDate, endDate);
			List<OrganizationProgressDetailsBean> progbeans = new ArrayList<OrganizationProgressDetailsBean>();
			progbeans = factory.getOrgProgressbeans(organizationId);
			all.addAll(progbeans);
		}
		
		return all;


	}
	
	public List<OrganizationProgressDetailsBean> getOrgProgressbeans(String orgid){			
		UrlApi api = new UrlApi();
		api.setOrganizationId(organizationId);
		JSONObject json = api.listOrganizations();
		JSONArray result = (JSONArray) json.get("result");
		JSONObject jsonObject = result.getJSONObject(0);
		return this.getOrgProgressbeans(jsonObject);

	}


	public List<OrganizationProgressDetailsBean> getOrgProgressbeans(JSONObject jsonObject) {

		List<OrganizationProgressDetailsBean> organizations = new ArrayList<OrganizationProgressDetailsBean>();

		String name = jsonObject.get("englishName").toString();
		String country = jsonObject.get("country").toString();	


		List<PublicationDetailsBean> publicationsbeanCollection = null;
		List<TransformationDetailsBean> transformationsbeanCollection = null;
		List<OaiPublicationDetailsBean>   oaipublicationbeanCollection = null;

		List<OrganizationGoalsSummaryBean>   goalsbeanColleection = null;


		transformationsbeanCollection = transformationDetailsBeanFactory.getTransformations();
		publicationsbeanCollection = publicationDetailsBeanFactory.getPublications();
		oaipublicationbeanCollection = oaipublicationbeanFactory.getOaiPublicationDetailsBeans();
		goalsbeanColleection = orggoalsFactory.getOrgGoalBeans();

		OrganizationProgressDetailsBean orgBean = new OrganizationProgressDetailsBean(name, country, transformationsbeanCollection, publicationsbeanCollection, oaipublicationbeanCollection, goalsbeanColleection);

		organizations.add(orgBean); 
		return organizations;
	}
	
}



