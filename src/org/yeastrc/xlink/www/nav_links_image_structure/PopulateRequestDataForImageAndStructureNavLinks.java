package org.yeastrc.xlink.www.nav_links_image_structure;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.ImageStructureQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.PeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.forms.PeptideProteinCommonForm;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.web_utils.AnyPDBFilesForProjectId;
import org.yeastrc.xlink.www.web_utils.URLEncodeDecodeAURL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class PopulateRequestDataForImageAndStructureNavLinks {


	private static final Logger log = Logger.getLogger(PopulateRequestDataForImageAndStructureNavLinks.class);


	/**
	 * Static get instance
	 * @return
	 */
	public static PopulateRequestDataForImageAndStructureNavLinks getInstance() {
		return new PopulateRequestDataForImageAndStructureNavLinks(); 
	}
	
	//  constructor
	private PopulateRequestDataForImageAndStructureNavLinks() { }
	

	public void populateRequestDataForImageAndStructureNavLinksForPeptide(

			PeptideQueryJSONRoot peptideQueryJSONRoot,
			int projectId,
			AuthAccessLevel authAccessLevel,
			PeptideProteinCommonForm form, 
			HttpServletRequest request ) throws ProxlWebappDataException {

		ImageStructureQueryJSONRoot imageStructureQueryJSONRoot = new ImageStructureQueryJSONRoot( peptideQueryJSONRoot );

		populateRequestDataForImageAndStructureNavLinksCommon( imageStructureQueryJSONRoot, form, projectId, authAccessLevel, request );
	}
	
	public void populateRequestDataForImageAndStructureNavLinksForProtein(
			
			ProteinQueryJSONRoot proteinQueryJSONRoot,
			int projectId,
			AuthAccessLevel authAccessLevel,
			PeptideProteinCommonForm form, 
			HttpServletRequest request ) throws ProxlWebappDataException {
		
		ImageStructureQueryJSONRoot imageStructureQueryJSONRoot = new ImageStructureQueryJSONRoot( proteinQueryJSONRoot );
		
		populateRequestDataForImageAndStructureNavLinksCommon( imageStructureQueryJSONRoot, form, projectId, authAccessLevel, request );
	}
	

	private void populateRequestDataForImageAndStructureNavLinksCommon(
			
			ImageStructureQueryJSONRoot imageStructureQueryJSONRoot,
			PeptideProteinCommonForm form, 
			int projectId,
			AuthAccessLevel authAccessLevel,
			HttpServletRequest request ) throws ProxlWebappDataException {
		
		request.setAttribute( "formDataForImageStructure", form );
		
		if ( form.getSearchIds() != null && form.getSearchIds().length == 1 ) {
		
			request.setAttribute( "ImageAndStructureSingleSearchId", form.getSearchIds()[0] );
		}
		
		
		try {

			//  Jackson JSON Mapper object for JSON deserialization and serialization

			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object

			String imageStructureQueryJSONRootToPage = jacksonJSON_Mapper.writeValueAsString( imageStructureQueryJSONRoot );


			//  Create URI Encoded JSON for passing to Image and Structure pages in hash 

			String imageAndStructureQueryJSONRootToPageURIEncoded = URLEncodeDecodeAURL.urlEncodeAURL( imageStructureQueryJSONRootToPage );

			request.setAttribute( "imageAndStructureQueryJSON", imageAndStructureQueryJSONRootToPageURIEncoded );

			

			boolean showStructureLink = true;
			
			if ( authAccessLevel.isAssistantProjectOwnerAllowed()
					|| authAccessLevel.isAssistantProjectOwnerIfProjectNotLockedAllowed() ) {
				
				
			} else {
				
				//  Public access user:
				
				showStructureLink = AnyPDBFilesForProjectId.getInstance().anyPDBFilesForProjectId( projectId );
			}
			
			request.setAttribute( WebConstants.REQUEST_SHOW_STRUCTURE_LINK, showStructureLink );
			

			

		
		} catch ( JsonProcessingException e ) {
			
			String msg = "Failed to write as JSON 'queryJSONToForm', JsonProcessingException.   queryString" +  request.getQueryString();
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );
		
		} catch ( Exception e ) {
			
			String msg = "Failed to write as JSON 'queryJSONToForm', Exception.   queryString" +  request.getQueryString();
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );
		}
	}
}
