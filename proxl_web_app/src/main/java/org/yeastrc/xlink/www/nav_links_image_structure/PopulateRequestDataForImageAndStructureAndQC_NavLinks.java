package org.yeastrc.xlink.www.nav_links_image_structure;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.ImageStructure_QC_QueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.PeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.forms.PeptideProteinCommonForm;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.web_utils.AnyPDBFilesForProjectId;
import org.yeastrc.xlink.www.web_utils.URLEncodeDecodeAURL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Put objects in the request scope to pass to imageAndStructureNavLinks.jsp and qc_NavLinks.jsp
 *
 */
public class PopulateRequestDataForImageAndStructureAndQC_NavLinks {

	private static final Logger log = LoggerFactory.getLogger( PopulateRequestDataForImageAndStructureAndQC_NavLinks.class);

	/**
	 * Static get instance
	 * @return
	 */
	public static PopulateRequestDataForImageAndStructureAndQC_NavLinks getInstance() {
		return new PopulateRequestDataForImageAndStructureAndQC_NavLinks(); 
	}
	
	//  constructor
	private PopulateRequestDataForImageAndStructureAndQC_NavLinks() { }
	
	/**
	 * @param peptideQueryJSONRoot
	 * @param projectId
	 * @param authAccessLevel
	 * @param form
	 * @param request
	 * @throws ProxlWebappDataException
	 */
	public void populateRequestDataForImageAndStructureAndQC_NavLinksForPeptide(

			PeptideQueryJSONRoot peptideQueryJSONRoot,
			int projectId,
			AuthAccessLevel authAccessLevel,
			PeptideProteinCommonForm form, 
			HttpServletRequest request ) throws ProxlWebappDataException {

		ImageStructure_QC_QueryJSONRoot imageStructureQueryJSONRoot = new ImageStructure_QC_QueryJSONRoot( peptideQueryJSONRoot );

		populateRequestDataForImageAndStructureNavLinksCommon( imageStructureQueryJSONRoot, form, projectId, authAccessLevel, request );
	}
	
	/**
	 * @param proteinQueryJSONRoot
	 * @param projectId
	 * @param authAccessLevel
	 * @param form
	 * @param request
	 * @throws ProxlWebappDataException
	 */
	public void populateRequestDataForImageAndStructureNavLinksForProtein(
			
			ProteinQueryJSONRoot proteinQueryJSONRoot,
			int projectId,
			AuthAccessLevel authAccessLevel,
			PeptideProteinCommonForm form, 
			HttpServletRequest request ) throws ProxlWebappDataException {
		
		ImageStructure_QC_QueryJSONRoot imageStructureQueryJSONRoot = new ImageStructure_QC_QueryJSONRoot( proteinQueryJSONRoot );
		
		populateRequestDataForImageAndStructureNavLinksCommon( imageStructureQueryJSONRoot, form, projectId, authAccessLevel, request );
	}
	

	/**
	 * @param imageStructure_QC_QueryJSONRoot
	 * @param form
	 * @param projectId
	 * @param authAccessLevel
	 * @param request
	 * @throws ProxlWebappDataException
	 */
	private void populateRequestDataForImageAndStructureNavLinksCommon(
			
			ImageStructure_QC_QueryJSONRoot imageStructure_QC_QueryJSONRoot,
			PeptideProteinCommonForm form, 
			int projectId,
			AuthAccessLevel authAccessLevel,
			HttpServletRequest request ) throws ProxlWebappDataException {
		
		request.setAttribute( "formDataForImageStructure_QC", form );
		
		if ( form.getProjectSearchId() != null && form.getProjectSearchId().length == 1 ) {
			request.setAttribute( "ImageAndStructure_QC_SingleProjectSearchId", form.getProjectSearchId()[0] );
		}

		if ( PeptideProteinCommonForm.DO_NOT_SORT_PROJECT_SEARCH_IDS_YES.equals( form.getDs() ) ) {
			request.setAttribute( "ImageAndStructure_QC_DoNotSortValue", PeptideProteinCommonForm.DO_NOT_SORT_PROJECT_SEARCH_IDS_YES );
		}
		
		try {
			//  Jackson JSON Mapper object for JSON deserialization and serialization

			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object

			String imageStructure_QC_QueryJSONRootToPage = jacksonJSON_Mapper.writeValueAsString( imageStructure_QC_QueryJSONRoot );

			//  Create URI Encoded JSON for passing to Image and Structure pages in hash 
			String imageAndStructureAndQC_QueryJSONRootToPageURIEncoded = URLEncodeDecodeAURL.urlEncodeAURL( imageStructure_QC_QueryJSONRootToPage );
			request.setAttribute( "imageAndStructureAndQC_QueryJSON", imageAndStructureAndQC_QueryJSONRootToPageURIEncoded );

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
