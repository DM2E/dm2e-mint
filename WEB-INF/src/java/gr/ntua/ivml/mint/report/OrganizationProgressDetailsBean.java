package gr.ntua.ivml.mint.report;
import java.util.List;

public class OrganizationProgressDetailsBean {

		
		private String name;
		private String country;
		
		
		private List<TransformationDetailsBean> transformations;
		private List<PublicationDetailsBean> publications;
		private List<OaiPublicationDetailsBean> oaipublications;
		private List<OrganizationGoalsSummaryBean> goals;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
		}
		public List<TransformationDetailsBean> getTransformations() {
			return transformations;
		}
		public void setTransformations(List<TransformationDetailsBean> transformations) {
			this.transformations = transformations;
		}
		public List<PublicationDetailsBean> getPublications() {
			return publications;
		}
		public void setPublications(List<PublicationDetailsBean> publications) {
			this.publications = publications;
		}
		public List<OaiPublicationDetailsBean> getOaipublications() {
			return oaipublications;
		}
		public void setOaipublications(List<OaiPublicationDetailsBean> oaipublications) {
			this.oaipublications = oaipublications;
		}
		public List<OrganizationGoalsSummaryBean> getGoals() {
			return goals;
		}
		public void setGoals(List<OrganizationGoalsSummaryBean> goals) {
			this.goals = goals;
		}
		public OrganizationProgressDetailsBean(String name, String country,
				List<TransformationDetailsBean> transformations,
				List<PublicationDetailsBean> publications,
				List<OaiPublicationDetailsBean> oaipublications,
				List<OrganizationGoalsSummaryBean> goals) {
			super();
			this.name = name;
			this.country = country;
			this.transformations = transformations;
			this.publications = publications;
			this.oaipublications = oaipublications;
			this.goals = goals;
		}

	

	}

