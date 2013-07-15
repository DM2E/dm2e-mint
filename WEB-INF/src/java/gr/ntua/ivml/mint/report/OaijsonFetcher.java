package gr.ntua.ivml.mint.report;

import gr.ntua.ivml.mint.util.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class OaijsonFetcher {
	
	private String url;
	private String projectName;		
	private String orgId;
	
	public OaijsonFetcher(String orgId) {
		String customName = Config.get("custom.name");
		this.projectName = customName;
		this.orgId = orgId;
		this.url = "http://panic.image.ntua.gr:9000/manager/projects/"+this.projectName+"/organizations/"+orgId+"/reports";
		
	}
	
	public OaijsonFetcher(String projectName,String orgId) {
		this.projectName = projectName;	
		this.orgId = orgId;
		this.url = "http://panic.image.ntua.gr:9000/manager/projects/"+this.projectName+"/organizations/"+orgId+"/reports";
	}
	
	 
	
	static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }
	
	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd).trim();
	      JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonText);
	      return json;
	    } finally {
	      is.close();
	    }
	  }
	
	
	
	public  JSONObject getJson(){
		
		JSONObject json = null;
		try {
			json = readJsonFromUrl(this.url);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
	public JSONObject getOrgJson(String type){
		JSONObject json = null; 
		this.url = "http://panic.image.ntua.gr:9000/manager/projects/"+this.projectName+"recordsPerOrganization/"+type;
		
		// http://panic.image.ntua.gr:9000/manager/projects/partage/recordsPerOrganization/rdf
		try {
			json = readJsonFromUrl(this.url);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
	public JSONObject getProjectJson(){
		JSONObject json = null; 
		this.url = "http://panic.image.ntua.gr:9000/manager/projects/"+this.projectName+"/overall";
		
		// http://panic.image.ntua.gr:9000/manager/projects/partage/recordsPerOrganization/rdf
		//http://panic.image.ntua.gr:9000/manager/projects/partage/overall
		try {
			json = readJsonFromUrl(this.url);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
	
	public Integer getPublishedItems(){
		JSONObject jsonObject = getJson();
		int inserted = 0   ;
		
		JSONArray pubreports = (JSONArray) jsonObject.get("reports");
		if (!pubreports.isEmpty()){
			JSONObject publication = (JSONObject) pubreports.get(pubreports.size()-1);
			inserted =  (Integer) publication.get("insertedRecords");
			
		}
		
		return inserted;
	}
	
	public Integer getConflictedItems(){
		JSONObject jsonObject = getJson();
		
		int conflicted = 0  ;
		JSONArray pubreports = (JSONArray) jsonObject.get("reports");
		if (!pubreports.isEmpty()){
			JSONObject publication = (JSONObject) pubreports.get(pubreports.size()-1);
			conflicted =  (Integer) publication.get("conflictedRecords");
		}
		
		return conflicted;
	}

}
