package gr.ntua.ivml.mint.report;






import gr.ntua.ivml.mint.actions.UrlApi;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class OrganizationStatisticsBeanFactory {


	private DataUploadDetailsBeanFactory datauploadDetailsFactory;
	private TransformationDetailsBeanFactory transformationDetailsBeanFactory;
	private PublicationDetailsBeanFactory publicationDetailsBeanFactory;
	private MappingDetailsBeanFactory mappingDetailsBeanFactory;
	//private OaiPublicationDetailsBeanFactory oaipublicationbeanFactory;
	private OrgOAIBeanFactory orgaiBeanFactory;

	private Date startDate;
	private Date endDate;


	public OrganizationStatisticsBeanFactory(Date startDate,Date endDate) {
		super();
	
		this.startDate = startDate; 
		this.endDate = endDate;
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





	public List<OrganizationStatisticsBean> getOrgStatisticsBeans(){

		
		UrlApi api = new UrlApi();
		JSONObject json = api.listOrganizations();
		return getOrgStatisticsBeans(json);

	}

	public List<OrganizationStatisticsBean> getOrgStatisticsBeans(JSONObject json){
		List<OrganizationStatisticsBean> organizations = new ArrayList<OrganizationStatisticsBean>();
		JSONArray result = (JSONArray) json.get("result");


		Iterator it = result.iterator();
		while (it.hasNext()){
			JSONObject jsonObject = (JSONObject) it.next();
			String organizationId = jsonObject.get("dbID").toString();
			String name = jsonObject.get("englishName").toString();
			String country = jsonObject.get("country").toString();	
			String publishAllowed = jsonObject.get("publishAllowed").toString();

			this.populateFactories(organizationId);

			
			List<DataUploadDetailsBean> uploadsbeanCollection = null;
			uploadsbeanCollection = datauploadDetailsFactory.getUploads();
		


			List<TransformationDetailsBean> transformationsbeanCollection = null;
			transformationsbeanCollection = transformationDetailsBeanFactory.getTransformations();

			List<PublicationDetailsBean> publicationsbeanCollection = null;
			publicationsbeanCollection = publicationDetailsBeanFactory.getPublications();

			List<MappingDetailsBean> mappingsbeanCollection = null;
			mappingsbeanCollection = mappingDetailsBeanFactory.getMappings();


			Iterator ite = uploadsbeanCollection.iterator();
			int uploadedItems = 0  ;
			while (ite.hasNext()){
				DataUploadDetailsBean uploadbean = (DataUploadDetailsBean) ite.next();
				if (uploadbean.getItemCount() == -1){
					continue;
				}
				uploadedItems += uploadbean.getItemCount();
			}
			
			
			ite = transformationsbeanCollection.iterator();
			int transformedItems = 0 ;
			while (ite.hasNext()){
				TransformationDetailsBean transformationbean = (TransformationDetailsBean) ite.next();
				
				if (transformationbean.getValidItems() == -1 ){
					continue;
				}
				transformedItems += transformationbean.getValidItems();
			}
			
			
			ite = publicationsbeanCollection.iterator();
			int publishedItems = 0 ;
			while (ite.hasNext()){
				PublicationDetailsBean publicationbean = (PublicationDetailsBean) ite.next();
				if (publicationbean.getItemCount() == -1){
					continue;
				}
				publishedItems += publicationbean.getItemCount();
			}
			
			
			
			int numberofmappings = mappingsbeanCollection.size();
			int numberofpublications = publicationsbeanCollection.size();
			int numberoftransformations  = transformationsbeanCollection.size();
			int numberofuploads = uploadsbeanCollection.size();
			

			int oaicommited = 0;
			
			//fix this 
			
			List<OrgOAIBean>  orgOAIBeanList  = orgaiBeanFactory.getOrgOAIBeans(organizationId);
			Iterator iterate = orgOAIBeanList.iterator();
			while (iterate.hasNext()){
				OrgOAIBean oaibean = (OrgOAIBean) iterate.next();
				oaicommited += oaibean.getUniqueItems();
			}
			
			/*List<OaiPublicationDetailsBean>   oaipublicationbeanCollection = null;
			oaipublicationbeanCollection = oaipublicationbeanFactory.getOaiPublicationDetailsBeans();
			
			Iterator<OaiPublicationDetailsBean> oait = oaipublicationbeanCollection.iterator();
			int oaipublishedItems = 0 ;
			while (oait.hasNext()){
				OaiPublicationDetailsBean oaibean = oait.next();
				if (oaibean.getInsertedRecords()!= null) oaipublished += oaibean.getInsertedRecords();
				if (oaibean.getConflictedRecords()!= null) oaiconflicts += oaibean.getConflictedRecords();
			}*/
			
		/*	if (!oaipublicationbeanCollection.isEmpty()){
				OaiPublicationDetailsBean latestbean =  oaipublicationbeanCollection.get(oaipublicationbeanCollection.size()-1);
				if (latestbean.getInsertedRecords()!=null){
						oaipublished = latestbean.getInsertedRecords();
				}
				else oaipublished = 0;
				if (latestbean.getInsertedRecords()!=null){
						oaiconflicts = latestbean.getConflictedRecords();
				}
				else oaiconflicts = 0;
			}*/

			OrganizationStatisticsBean organizationStatisticsbean = new OrganizationStatisticsBean(name, country, uploadedItems, transformedItems, publishedItems, numberofmappings, numberofuploads, numberoftransformations, numberofpublications,oaicommited);
			organizations.add(organizationStatisticsbean); 
		}

		Collections.sort(organizations);
		return organizations;


	}

	protected void populateFactories(String organizationId) {
		transformationDetailsBeanFactory = new TransformationDetailsBeanFactory(organizationId, startDate, endDate);
		publicationDetailsBeanFactory = new PublicationDetailsBeanFactory(organizationId, startDate, endDate);
		mappingDetailsBeanFactory  = new MappingDetailsBeanFactory(organizationId, startDate, endDate);	
		//oaipublicationbeanFactory = new OaiPublicationDetailsBeanFactory (organizationId, startDate, endDate);
		datauploadDetailsFactory = new DataUploadDetailsBeanFactory(organizationId, startDate, endDate);
		orgaiBeanFactory = new OrgOAIBeanFactory(organizationId);
	}	

}
