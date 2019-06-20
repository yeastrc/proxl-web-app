package org.yeastrc.xlink.www.form_utils;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.cutoff_processing_web.GetDefaultPsmPeptideCutoffs;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.forms.ProteinCommonForm;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Get ProteinQueryJSONRoot object with data in form proteinCommonForm
 *
 */
public class GetProteinQueryJSONRootFromFormData {

	private static final Logger log = LoggerFactory.getLogger( GetProteinQueryJSONRootFromFormData.class);
	/**
	 * Static get instance
	 * @return
	 */
	public static GetProteinQueryJSONRootFromFormData getInstance() {
		return new GetProteinQueryJSONRootFromFormData(); 
	}
	//  constructor
	private GetProteinQueryJSONRootFromFormData() { }
	
	/**
	 * Get ProteinQueryJSONRoot object with data in form proteinCommonForm
	 * @param proteinCommonForm
	 * @param projectId TODO
	 * @param proteinQueryJSONRoot
	 * 
	 * @throws ProxlWebappDataException
	 * @throws Exception 
	 */
	public ProteinQueryJSONRoot getProteinQueryJSONRootFromFormData( 
			ProteinCommonForm proteinCommonForm,
			int projectId,
			Collection<Integer> projectSearchIds,
			Collection<Integer> searchIds, Map<Integer,Integer> mapProjectSearchIdToSearchId
			) throws ProxlWebappDataException, Exception {
		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		String queryJSONFromForm = proteinCommonForm.getQueryJSON();
		ProteinQueryJSONRoot proteinQueryJSONRoot = null;
		if ( StringUtils.isNotEmpty( queryJSONFromForm ) ) {
			try {
				proteinQueryJSONRoot = jacksonJSON_Mapper.readValue( queryJSONFromForm, ProteinQueryJSONRoot.class );
			} catch ( JsonParseException e ) {
				String msg = "Failed to parse 'queryJSONFromForm', JsonParseException.  queryJSONFromForm: " + queryJSONFromForm;
				log.error( msg, e );
				throw new ProxlWebappDataException( msg, e );
			} catch ( JsonMappingException e ) {
				String msg = "Failed to parse 'queryJSONFromForm', JsonMappingException.  queryJSONFromForm: " + queryJSONFromForm;
				log.error( msg, e );
				throw new ProxlWebappDataException( msg, e );
			} catch ( IOException e ) {
				String msg = "Failed to parse 'queryJSONFromForm', IOException.  queryJSONFromForm: " + queryJSONFromForm;
				log.error( msg, e );
				throw new ProxlWebappDataException( msg, e );
			}

			//  Update proteinQueryJSONRoot for current search ids and project search ids
			Update__A_QueryBase_JSONRoot__ForCurrentSearchIds.getInstance()
			.update__A_QueryBase_JSONRoot__ForCurrentSearchIds( proteinQueryJSONRoot, mapProjectSearchIdToSearchId, projectId );
			
		} else {
			//  Query JSON in the form is empty so create an empty object that will be populated.
			proteinQueryJSONRoot = new ProteinQueryJSONRoot();
			CutoffValuesRootLevel cutoffValuesRootLevel =
					GetDefaultPsmPeptideCutoffs.getInstance()
					.getDefaultPsmPeptideCutoffs( projectId,projectSearchIds, searchIds, mapProjectSearchIdToSearchId );
			proteinQueryJSONRoot.setCutoffs( cutoffValuesRootLevel );
		}
////////////  An experiment that goes with the function getEncodedExcludeProteins in the JS
//		UpdateProteinQueryJSONRootWithFormData.getInstance().updateProteinQueryJSONRootWithFormData( proteinQueryJSONRoot, proteinCommonForm );
		return proteinQueryJSONRoot;
	}
}
