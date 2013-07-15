package gr.ntua.ivml.mint.report;

import gr.ntua.ivml.mint.actions.UrlApi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class OrgOAIBeanFactory {
	
	String organizationId ;
	String projectName;
	OaijsonFetcher oaijsonFetcher ;
	
	
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	public OaijsonFetcher getOaijsonFetcher() {
		return oaijsonFetcher;
	}
	public void setOaijsonFetcher(OaijsonFetcher oaijsonFetcher) {
		this.oaijsonFetcher = oaijsonFetcher;
	}
	
	public OrgOAIBeanFactory(String organizationId) {
		super();
		this.organizationId = organizationId;
		this.oaijsonFetcher = new OaijsonFetcher(this.organizationId);	
				
	}
	
	public OrgOAIBeanFactory(String organizationId,String projectName) {
		super();
		this.organizationId = organizationId;
		this.projectName = projectName;
		this.oaijsonFetcher = new OaijsonFetcher(this.projectName,this.organizationId);	
		
	}
	
	public ProjectOAIBean getProjectOAIBean(){
		List<String> namespaces = new ArrayList<String>(); 
		JSONObject object = oaijsonFetcher.getProjectJson();
		JSONArray array = object.getJSONArray("namespaces");
		
		Iterator it = array.iterator();
		while (it.hasNext()){
			JSONObject item = (JSONObject) it.next();
			namespaces.add(item.getString("prefix"));		
		}

		Integer unique = Integer.parseInt(object.getString("unique"));
		Integer duplicates = Integer.parseInt(object.getString("duplicates"));
		Integer publications = Integer.parseInt(object.getString("publications"));
		ProjectOAIBean projectBean = new ProjectOAIBean(projectName, unique, duplicates,publications,namespaces);
		return projectBean;
	}
	
	public List<OrgOAIBean> getOrgOAIBeans(){
		ProjectOAIBean projectbean = getProjectOAIBean();
		Iterator<String> it = projectbean.getTypes().iterator();
		List<OrgOAIBean> orgsoaibeans = new ArrayList<OrgOAIBean>();
		while (it.hasNext()){
			String namespace = (String) it.next();
			JSONObject object = oaijsonFetcher.getOrgJson(namespace);
			Iterator iter = object.keys();
			while (iter.hasNext()){
				String orgid = (String) iter.next();
				Integer unique = object.getInt(orgid);
				UrlApi api = new UrlApi();
				api.setOrganizationId(orgid);		
				JSONObject json = api.listOrganizations();
				JSONArray result = (JSONArray) json.get("result");
				Iterator ait = result.iterator();
				String organizationName = null;
				while (ait.hasNext()){
					
					JSONObject jsonObject = (JSONObject) ait.next();
					organizationName =(String) jsonObject.get("englishName");
				}
				OrgOAIBean orgoaiBean = new OrgOAIBean(organizationName, namespace, orgid, unique);
				orgsoaibeans.add(orgoaiBean);
			}
			
		}
		return orgsoaibeans;
		
	}
	
	public List<OrgOAIBean> getOrgOAIBeans(String orgid){
		List<OrgOAIBean> orgsoaibeans = getOrgOAIBeans();
		List<OrgOAIBean> orgoaibeans = new ArrayList<OrgOAIBean>();
		Iterator<OrgOAIBean>  it = orgsoaibeans.iterator();
		while (it.hasNext()){
			OrgOAIBean bean = it.next();
			if (bean.organizationId.equals(orgid)){
				orgoaibeans.add(bean);
			}
		}
		
		return orgoaibeans;
		
	}
	

}
