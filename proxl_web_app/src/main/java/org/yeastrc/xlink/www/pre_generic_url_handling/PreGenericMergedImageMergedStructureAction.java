package org.yeastrc.xlink.www.pre_generic_url_handling;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesAnnotationLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.servlet_context.CurrentContext;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class PreGenericMergedImageMergedStructureAction extends Action {

	private static final Logger log = LoggerFactory.getLogger( PreGenericPeptideRedirectAction.class);

	@Override
	public ActionForward execute( ActionMapping mapping,
			ActionForm actionForm,
			HttpServletRequest request,
			HttpServletResponse response )
					throws Exception {


		try {
			PreGenericProteinForm form = (PreGenericProteinForm) actionForm;

			//  Detect which Struts action mapping was called by examining the value of the "parameter" attribute
			//     accessed by calling mapping.getParameter()

			String strutsActionURLToRedirectTo = mapping.getParameter();

			Set<Integer> searchIdsSet = new HashSet<>();

			
			StringBuilder redirectURLSB = new StringBuilder( 1000 );
			
			redirectURLSB.append( CurrentContext.getCurrentWebAppContext() );
			redirectURLSB.append( strutsActionURLToRedirectTo ); 
			redirectURLSB.append( ".do?" );		
					

			if ( form.getSearchIds() == null ) {

				String msg = "form.getSearchIds() == null for strutsActionURLToRedirectTo: " + strutsActionURLToRedirectTo;
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}


			for ( int index = 0; index < form.getSearchIds().length; index++ ) {

				int searchId = form.getSearchIds()[ index ];

				if ( index > 0 ) {

					redirectURLSB.append( "&" );	
				}

				redirectURLSB.append( "searchIds=" );
				redirectURLSB.append( Integer.toString( searchId ) );

				searchIdsSet.add( searchId );
			}
			
			
			
			String redirectURL = redirectURLSB.toString();
			
			
			request.setAttribute( "redirectURL", redirectURL );
			
			


			CutoffValuesRootLevel cutoffValuesRootLevel = new CutoffValuesRootLevel();

			
			
			//  Populate psm and peptpide cutoffs in the Generic
			


			Map<Integer, Map<Integer, AnnotationTypeDTO>> psmFilterableAnnotationType_DTOMapPerSearchIdMap =
					GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIdsSet );

			Map<Integer, Map<Integer, AnnotationTypeDTO>> peptideFilterableAnnotationType_DTOMapPerSearchIdMap =
					GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIdsSet );


			Map<String, CutoffValuesSearchLevel> searches = cutoffValuesRootLevel.getSearches();

			for ( Integer searchId : searchIdsSet ) {

				CutoffValuesSearchLevel search = new CutoffValuesSearchLevel();

				searches.put( searchId.toString(), search);
				
				search.setSearchId( searchId );
				
				Map<String,CutoffValuesAnnotationLevel> psmCutoffValuesMap = search.getPsmCutoffValues();
				Map<String,CutoffValuesAnnotationLevel> peptideCutoffValuesMap = search.getPeptideCutoffValues();
				
				Map<Integer, AnnotationTypeDTO> psmFilterableAnnotationType_DTOMap =
						psmFilterableAnnotationType_DTOMapPerSearchIdMap.get( searchId );

				Map<Integer, AnnotationTypeDTO> peptideFilterableAnnotationType_DTOMap =
						peptideFilterableAnnotationType_DTOMapPerSearchIdMap.get( searchId );
				
				
				if ( psmFilterableAnnotationType_DTOMap == null ) {
					
					String msg = "No PSM filterable Annotation types for search id: " + searchId
							+ ", queryString: " + request.getQueryString();
					log.error( msg );

					throw new ProxlWebappDataException(msg);
					
				} else {
				
					processPsmOrPeptideAnnotationType(
							"psm",// psmOrPeptideType
							searchId,
							null,
							psmFilterableAnnotationType_DTOMap,
							psmCutoffValuesMap );
				}

				if ( peptideFilterableAnnotationType_DTOMap == null ) {
					
					String msg = "No Peptide filterable Annotation types for search id: " + searchId
							+ ", queryString: " + request.getQueryString();
					log.error( msg );
					
					throw new ProxlWebappDataException(msg);
					
				} else {

					processPsmOrPeptideAnnotationType(
							"peptide", // psmOrPeptideType
							searchId,
							null,
							peptideFilterableAnnotationType_DTOMap,
							peptideCutoffValuesMap );
				}
			}


			//  Jackson JSON Mapper object for JSON deserialization and serialization

			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object

			String cutoffValuesRootLevelJSONRootString = jacksonJSON_Mapper.writeValueAsString( cutoffValuesRootLevel );

			
			request.setAttribute( "cutoffValuesRootLevelJSONRootString", cutoffValuesRootLevelJSONRootString );
			
			
			
			
			
			
			
			
			

			return mapping.findForward( "Success" );


		} catch ( Exception e ) {

			String msg = "Exception caught: " + e.toString() + ", queryString: " + request.getQueryString();

			log.error( msg, e );

			return mapping.findForward( StrutsGlobalForwardNames.GENERAL_ERROR );
		}

	}
	
	


	/**
	 * @param psmOrPeptideType
	 * @param searchId
	 * @param value
	 * @param annotationType_DTOMap
	 * @param cutoffValuesMap
	 * @throws ProxlWebappDataException
	 */
	private void processPsmOrPeptideAnnotationType(
			String psmOrPeptideType,
			Integer searchId,
			String value,
			Map<Integer, AnnotationTypeDTO> annotationType_DTOMap,
			Map<String,CutoffValuesAnnotationLevel> cutoffValuesMap ) throws ProxlWebappDataException {

		boolean foundQValueAnnotationTypeDTO = false;

		for ( Map.Entry<Integer, AnnotationTypeDTO> annotationTypeDTOEntry : annotationType_DTOMap.entrySet() ) {

			AnnotationTypeDTO annotationTypeDTO = annotationTypeDTOEntry.getValue();

			if ( PreGenericURLHandlingConstants.ANNOTATION_TYPE_NAME_Q_VALUE.equals( annotationTypeDTO.getName() ) ) {

				CutoffValuesAnnotationLevel cutoffValuesAnnotationLevel = new CutoffValuesAnnotationLevel();

				cutoffValuesAnnotationLevel.setId( annotationTypeDTO.getId() );
				cutoffValuesAnnotationLevel.setValue( value );
				String annotationTypeIdString = Integer.toString( annotationTypeDTO.getId() );

				cutoffValuesMap.put( annotationTypeIdString, cutoffValuesAnnotationLevel );
				
				foundQValueAnnotationTypeDTO = true;

				break;
			}
		}
		
		if ( ! foundQValueAnnotationTypeDTO ) {
			
			String msg = "Failed to find " + psmOrPeptideType + " annotation type with name '" 
					+ PreGenericURLHandlingConstants.ANNOTATION_TYPE_NAME_Q_VALUE
					+ "' for search id: " + searchId;
			log.error( msg );
			throw new ProxlWebappDataException(msg);
		}
	}
}
