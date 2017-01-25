package org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.constants.JSONStringCharsetConstants;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class ListProjectsGet {
	

	private static final Logger log = Logger.getLogger(ListProjectsGet.class);


	private static final ListProjectsGet instance = new ListProjectsGet();

	private ListProjectsGet() { }
	public static ListProjectsGet getInstance() { return instance; }


	public static final String SUB_URL = "/project/listForCurrentUser";
	
	
	/**
	 * @param baseURL
	 * @param httpclient
	 * @return
	 * @throws Exception
	 */
	public ProjectListForCurrentUserServiceResult listProjectsGet( String baseURL, HttpClient httpclient ) throws Exception {
		
		
		String url = baseURL + SUB_URL;
		
		HttpGet httpGet = null;
		HttpResponse response = null;

		InputStream responseInputStream = null;
		
		try {

			httpGet = new HttpGet( url );

			response = httpclient.execute(httpGet);
			
			int httpStatusCode = response.getStatusLine().getStatusCode();
						

			if ( log.isDebugEnabled() ) {

				log.debug("Get: Http Response Status code: " + httpStatusCode );
			}

			responseInputStream = response.getEntity().getContent();
			
			//  optional code for viewing response as string
			
			//  responseBytes must be large enough for the whole response, or code something to create larger array and copy to the larger array
			
			byte[] responseBytes = new byte[10000000];
			
			int responseBytesOffset = 0;
			int responseBytesLength = responseBytes.length;
			
			int totalBytesRead = 0;
			
			while (true) {

				int bytesRead = responseInputStream.read(responseBytes, responseBytesOffset, responseBytesLength );
			
				if ( bytesRead == -1 ) {
					
					break;
				}
				
				totalBytesRead += bytesRead;
				responseBytesOffset += bytesRead;
				responseBytesLength -= bytesRead;
			}
			
			byte[] responseBytesJustData = Arrays.copyOf(responseBytes, totalBytesRead);
			
			String responseAsString = new String(responseBytesJustData, JSONStringCharsetConstants.JSON_STRING_CHARSET_UTF_8 );

			if ( log.isDebugEnabled() ) {
				
				System.out.println( "RESPONSE:" );
				System.out.println( responseAsString );
			}
			
			

			
			//  Throws ProxlSubImportServerReponseException if  httpStatusCode != HttpStatus.SC_OK (200)
			A_ProcessHTTP_StatusCode.getInstance().processHTTP_StatusCode( httpStatusCode, url );
			
			

			
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
			
			ProjectListForCurrentUserServiceResponse projectListForCurrentUserServiceResponse = 
					jacksonJSON_Mapper.readValue( responseBytesJustData, ProjectListForCurrentUserServiceResponse.class );
			
			
			
			ProjectListForCurrentUserServiceResult projectListForCurrentUserServiceResult = new ProjectListForCurrentUserServiceResult();

			List<ProjectListForCurrentUserServiceResultItem> projectList = new ArrayList<>();
			projectListForCurrentUserServiceResult.projectList = projectList;
			
			for ( ProjectWithUserAccessLevel projectInReponse : projectListForCurrentUserServiceResponse.projectList ) {
				
				if ( projectInReponse.canUpload ) {

					ProjectListForCurrentUserServiceResultItem resultItem = new ProjectListForCurrentUserServiceResultItem();
					projectList.add(resultItem);

					resultItem.id = projectInReponse.project.id;
					resultItem.title = projectInReponse.project.title;
				}
			}

			return projectListForCurrentUserServiceResult;

		} catch (Exception e) {

			log.error("Failed to list projects.", e );
			throw e;

		} finally { 

			if ( responseInputStream != null ) {
				responseInputStream.close();
			}
		}
		
	}
	

	public static class ProjectListForCurrentUserServiceResult {
		
		private List<ProjectListForCurrentUserServiceResultItem> projectList;

		public List<ProjectListForCurrentUserServiceResultItem> getProjectList() {
			return projectList;
		}

		public void setProjectList(
				List<ProjectListForCurrentUserServiceResultItem> projectList) {
			this.projectList = projectList;
		}
	}
	

	public static class ProjectListForCurrentUserServiceResultItem {
		
	 	private int id;
	 	private String title;
	 	
	 	
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
	}
	

	public static class ProjectListForCurrentUserServiceResponse {
		
		
		List<ProjectWithUserAccessLevel> projectList;

		public List<ProjectWithUserAccessLevel> getProjectList() {
			return projectList;
		}

		public void setProjectList(List<ProjectWithUserAccessLevel> projectList) {
			this.projectList = projectList;
		}
	}
	
	

	public static class ProjectWithUserAccessLevel {

		private ProjectDTO project;
		private boolean canDelete;
		private boolean canUpload;
		
		
		public ProjectDTO getProject() {
			return project;
		}
		public void setProject(ProjectDTO project) {
			this.project = project;
		}
		public boolean isCanDelete() {
			return canDelete;
		}
		public void setCanDelete(boolean canDelete) {
			this.canDelete = canDelete;
		}
		public boolean isCanUpload() {
			return canUpload;
		}
		public void setCanUpload(boolean canUpload) {
			this.canUpload = canUpload;
		}


	}
	
	/**
	 * table project
	 *
	 */
	 public static class ProjectDTO {

	 	private int id;
	 	private int authShareableObjectId;
	 	
	 	private String title;
	 	private String abstractText;
	 	
	 	private boolean enabled;
	 	private boolean markedForDeletion;
	 	private Date markedForDeletionTimstamp;
	 	private Integer markedForDeletionAuthUserId;
	 	
	 	private boolean projectLocked;
	 	
	 	private Integer publicAccessLevel;
	 	private boolean publicAccessLocked;
	 	
	 	
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public int getAuthShareableObjectId() {
			return authShareableObjectId;
		}
		public void setAuthShareableObjectId(int authShareableObjectId) {
			this.authShareableObjectId = authShareableObjectId;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getAbstractText() {
			return abstractText;
		}
		public void setAbstractText(String abstractText) {
			this.abstractText = abstractText;
		}
		public boolean isEnabled() {
			return enabled;
		}
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
		public boolean isMarkedForDeletion() {
			return markedForDeletion;
		}
		public void setMarkedForDeletion(boolean markedForDeletion) {
			this.markedForDeletion = markedForDeletion;
		}
		public Date getMarkedForDeletionTimstamp() {
			return markedForDeletionTimstamp;
		}
		public void setMarkedForDeletionTimstamp(Date markedForDeletionTimstamp) {
			this.markedForDeletionTimstamp = markedForDeletionTimstamp;
		}
		public Integer getMarkedForDeletionAuthUserId() {
			return markedForDeletionAuthUserId;
		}
		public void setMarkedForDeletionAuthUserId(Integer markedForDeletionAuthUserId) {
			this.markedForDeletionAuthUserId = markedForDeletionAuthUserId;
		}
		public boolean isProjectLocked() {
			return projectLocked;
		}
		public void setProjectLocked(boolean projectLocked) {
			this.projectLocked = projectLocked;
		}
		public Integer getPublicAccessLevel() {
			return publicAccessLevel;
		}
		public void setPublicAccessLevel(Integer publicAccessLevel) {
			this.publicAccessLevel = publicAccessLevel;
		}
		public boolean isPublicAccessLocked() {
			return publicAccessLocked;
		}
		public void setPublicAccessLocked(boolean publicAccessLocked) {
			this.publicAccessLocked = publicAccessLocked;
		}
	 	
	 }

}
