package org.yeastrc.xlink.www.web_utils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class ProjectSearchIdsSearchIds_SetRequestParameter {
	
	private static final Logger log = LoggerFactory.getLogger( ProjectSearchIdsSearchIds_SetRequestParameter.class );

	private static final ProjectSearchIdsSearchIds_SetRequestParameter instance = new ProjectSearchIdsSearchIds_SetRequestParameter();
	private ProjectSearchIdsSearchIds_SetRequestParameter() { }
	public static ProjectSearchIdsSearchIds_SetRequestParameter getSingletonInstance() { return instance; }
	
	public enum SearchesAreUserSorted { YES, NO }
	

	/**
	 * @param search
	 * @param request
	 * @throws Exception
	 */
	public void populateProjectSearchIdsSearchIds_SetRequestParameter( SearchDTO search, SearchesAreUserSorted searchesAreUserSorted, HttpServletRequest request ) throws Exception {
		List<SearchDTO> searches = new ArrayList<>( 1 );
		searches.add(search);
		populateProjectSearchIdsSearchIds_SetRequestParameter( searches, searchesAreUserSorted, request );
	}
	/**
	 * @param searches
	 * @param request
	 * @throws Exception
	 */
	public void populateProjectSearchIdsSearchIds_SetRequestParameter( List<SearchDTO> searches, SearchesAreUserSorted searchesAreUserSorted, HttpServletRequest request ) throws Exception {
		
		List<ProjectSearchIdSearchIdPair> projectSearchIdSearchIdPairList = new ArrayList<>( searches.size() );
		
		for ( SearchDTO search : searches ) {
			ProjectSearchIdSearchIdPair projectSearchIdSearchIdPair = new ProjectSearchIdSearchIdPair();
			projectSearchIdSearchIdPairList.add( projectSearchIdSearchIdPair );
			
			projectSearchIdSearchIdPair.projectSearchId = search.getProjectSearchId();
			projectSearchIdSearchIdPair.searchId = search.getSearchId();
		}
		
		EncodeToJSON encodeToJSON = new EncodeToJSON();
		
		encodeToJSON.projectSearchIdSearchIdPairList = projectSearchIdSearchIdPairList;
		if ( searchesAreUserSorted == SearchesAreUserSorted.YES ) {
			encodeToJSON.searchesUserSorted = true;
		}

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		
		try {
			String json = jacksonJSON_Mapper.writeValueAsString( encodeToJSON );
			//  Set in request attribute 
			request.setAttribute( WebConstants.PARAMETER_PROJECT_SEARCH_ID_SEARCH_ID_PAIRS_DISPLAY_ORDER_LIST_JSON_REQUEST_KEY, json );

		} catch ( JsonProcessingException e ) {
			String msg = "Failed to write as JSON, JsonProcessingException.   ";
			log.error( msg, e );
			throw new ProxlWebappInternalErrorException( msg, e );
		} catch ( Exception e ) {
			String msg = "Failed to write as JSON, Exception.  ";
			log.error( msg, e );
			throw new ProxlWebappInternalErrorException( msg, e );
		}
		
	}
	
	private static class EncodeToJSON {
		List<ProjectSearchIdSearchIdPair> projectSearchIdSearchIdPairList;
		boolean searchesUserSorted;
		public List<ProjectSearchIdSearchIdPair> getProjectSearchIdSearchIdPairList() {
			return projectSearchIdSearchIdPairList;
		}
		public boolean isSearchesUserSorted() {
			return searchesUserSorted;
		}
	}
	
	private static class ProjectSearchIdSearchIdPair {
		int projectSearchId;
		int searchId;
		public int getProjectSearchId() {
			return projectSearchId;
		}
		public int getSearchId() {
			return searchId;
		}
	}
}
