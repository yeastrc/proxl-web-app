package org.yeastrc.xlink.www.cutoff_processing_web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.SearchProgramsPerSearchDAO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchProgramsPerSearchDTO;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDBDataOutOfSyncException;
import org.yeastrc.xlink.www.form_page_objects.CutoffPageDisplayAnnotationLevel;
import org.yeastrc.xlink.www.form_page_objects.CutoffPageDisplayRoot;
import org.yeastrc.xlink.www.form_page_objects.CutoffPageDisplaySearchLevel;

/**
 * 
 *  Get Page display of all filterable type annotations provided
 */
public class GetCutoffPageDisplayRoot {

	private static final Logger log = Logger.getLogger( GetCutoffPageDisplayRoot.class );


	//  private constructor
	private GetCutoffPageDisplayRoot() { }

	/**
	 * @return newly created instance
	 */
	public static GetCutoffPageDisplayRoot getInstance() { 
		return new GetCutoffPageDisplayRoot(); 
	}	


	/**
	 * Get Cutoff Display data and put in request scope
	 * 
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public CutoffPageDisplayRoot getCutoffPageDisplayRootSingleSearchId(

			int searchId,
			HttpServletRequest request
			
			) throws Exception {


		Collection<Integer> searchIdsCollection = new HashSet<>();

		searchIdsCollection.add( searchId );

		return getCutoffPageDisplayRoot( searchIdsCollection, request );
	}


	/**
	 * @param searchIdsCollection
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public CutoffPageDisplayRoot getCutoffPageDisplayRoot(

			Collection<Integer> searchIdsCollection,
			HttpServletRequest request

			) throws Exception {

		
		Set<Integer> searchIdsSet = new HashSet<>( searchIdsCollection );  // put in set to remove dups;
		
		List<Integer> searchIdsList= new ArrayList<>( searchIdsSet );  // put in list so can sort
		
		Collections.sort( searchIdsList );

		//  Cutoff  Values,  Match with Annotation Type records from DB to get more info for display on the page

		//  CutoffPageDisplayRoot used for building the display of the cutoffs on the page 

		CutoffPageDisplayRoot cutoffPageDisplayRoot = new CutoffPageDisplayRoot();



		//  Get Annotation Type records for PSM and Peptide
		
		
		//  Get  Annotation Type records for PSM
		
		Map<Integer, Map<Integer, AnnotationTypeDTO>> 
		srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIdsList );

		
		//  Get  Annotation Type records for Reported Peptides
		
		Map<Integer, Map<Integer, AnnotationTypeDTO>> 
		srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIdsList );
		
		
		
		
		
		//  Get search program DTO records
		
		Map<Integer,SearchProgramsPerSearchDTO> searchProgramsPerSearchDTOMap = new HashMap<>();
		
		for ( Map.Entry<Integer, Map<Integer, AnnotationTypeDTO>> perSearchEntry : srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap.entrySet() ) {
			
			for ( Map.Entry<Integer, AnnotationTypeDTO> perAnnTypeEntry : perSearchEntry.getValue().entrySet() ) {
				
				Integer searchProgramsPerSearchId = perAnnTypeEntry.getValue().getSearchProgramsPerSearchId();
				
				SearchProgramsPerSearchDTO searchProgramsPerSearchDTO = searchProgramsPerSearchDTOMap.get( searchProgramsPerSearchId );
				
				if ( searchProgramsPerSearchDTO == null ) {
					
					searchProgramsPerSearchDTO = SearchProgramsPerSearchDAO.getInstance().getSearchProgramDTOForId( searchProgramsPerSearchId ) ;
					
					if ( searchProgramsPerSearchDTO == null ) {
						
						String msg = "No searchProgramsPerSearchDTO record found for searchProgramsPerSearchId: " + searchProgramsPerSearchId;
						log.error( msg );
						
						throw new ProxlWebappDBDataOutOfSyncException( msg );
					}
					
					searchProgramsPerSearchDTOMap.put( searchProgramsPerSearchId, searchProgramsPerSearchDTO );
				}
			}
		}
		
		
		
		
		
		
		// Process Per SearchId


		for ( Integer searchId : searchIdsList ) {


			CutoffPageDisplaySearchLevel cutoffPageDisplaySearchLevel = new CutoffPageDisplaySearchLevel();
			cutoffPageDisplayRoot.addCutoffPageDisplaySearchLevel( cutoffPageDisplaySearchLevel );

			cutoffPageDisplaySearchLevel.setSearchId( searchId );

			Map<Integer, AnnotationTypeDTO> srchPgm_Filterable_Psm_AnnotationType_DTOMap = srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap.get( searchId );
			Map<Integer, AnnotationTypeDTO> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap = srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap.get( searchId );

			processPSMs( srchPgm_Filterable_Psm_AnnotationType_DTOMap, cutoffPageDisplaySearchLevel, searchProgramsPerSearchDTOMap );

			processPeptides( srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap, cutoffPageDisplaySearchLevel, searchProgramsPerSearchDTOMap );
		}


		request.setAttribute( WebConstants.PARAMETER_CUTOFF_PAGE_DISPLAY_ROOT_REQUEST_ENTRY, cutoffPageDisplayRoot );

		

		return cutoffPageDisplayRoot;

	}





	/**
	 * @param srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap
	 * @param cutoffPageDisplaySearchLevel
	 * @throws Exception
	 */
	private void processPeptides(
			Map<Integer, AnnotationTypeDTO> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap,
			CutoffPageDisplaySearchLevel cutoffPageDisplaySearchLevel,
			Map<Integer,SearchProgramsPerSearchDTO> searchProgramsPerSearchDTOMap )
					throws Exception {

		for ( Map.Entry<Integer, AnnotationTypeDTO> entry : srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap.entrySet() ) {

			AnnotationTypeDTO annotationTypeDTO = entry.getValue();

			if ( annotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {

				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + annotationTypeDTO.getId();
				log.error( msg );
				throw new Exception(msg);
			}

			//  Add Display object

			CutoffPageDisplayAnnotationLevel cutoffPageDisplayAnnotationLevel = new CutoffPageDisplayAnnotationLevel();

			cutoffPageDisplaySearchLevel.addPeptideCutoffPageDisplayAnnotationLevel( cutoffPageDisplayAnnotationLevel );

			cutoffPageDisplayAnnotationLevel.setAnnotationTypeId( annotationTypeDTO.getId() );
			cutoffPageDisplayAnnotationLevel.setAnnotationName( annotationTypeDTO.getName() );
			cutoffPageDisplayAnnotationLevel.setAnnotationDescription( annotationTypeDTO.getDescription() );

			if ( annotationTypeDTO.getAnnotationTypeFilterableDTO().isDefaultFilter() ) {

				cutoffPageDisplayAnnotationLevel.setAnnotationDefaultValue( annotationTypeDTO.getAnnotationTypeFilterableDTO().getDefaultFilterValueString() );
			}

			cutoffPageDisplayAnnotationLevel.setSortOrder( annotationTypeDTO.getAnnotationTypeFilterableDTO().getSortOrder() );
			cutoffPageDisplayAnnotationLevel.setDisplayOrder( annotationTypeDTO.getDisplayOrder() );


			Integer searchProgramsPerSearchId = annotationTypeDTO.getSearchProgramsPerSearchId();
			
			SearchProgramsPerSearchDTO searchProgramsPerSearchDTO = searchProgramsPerSearchDTOMap.get( searchProgramsPerSearchId );
			
			if ( searchProgramsPerSearchDTO == null ) {
					
				String msg = "No searchProgramsPerSearchDTO record found for searchProgramsPerSearchId: " + searchProgramsPerSearchId;
				log.error( msg );

				throw new ProxlWebappDBDataOutOfSyncException( msg );
			}
			
			cutoffPageDisplayAnnotationLevel.setSearchProgramDisplayName( searchProgramsPerSearchDTO.getDisplayName() );

			//  Determine if this annotation name is duplicate across search programs
			
			//  True/False: This annotation name is duplicate across search programs
//			cutoffPageDisplayAnnotationLevel.setAnnNameDupsAcrossSrchPgms( true );
		}
	}





	/**
	 * @param srchPgmFilterablePsmAnnotationTypeDTOMap
	 * @param cutoffPageDisplaySearchLevel
	 * @param psmCutoffValuesMap
	 * @throws Exception
	 */
	public void processPSMs(
			Map<Integer, AnnotationTypeDTO> srchPgmFilterablePsmAnnotationTypeDTOMap,
			CutoffPageDisplaySearchLevel cutoffPageDisplaySearchLevel,
			Map<Integer,SearchProgramsPerSearchDTO> searchProgramsPerSearchDTOMap )
					throws Exception {


		for ( Map.Entry<Integer, AnnotationTypeDTO> entry :  srchPgmFilterablePsmAnnotationTypeDTOMap.entrySet() ) {

			AnnotationTypeDTO annotationTypeDTO = entry.getValue();

			if ( annotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {

				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + annotationTypeDTO.getId();
				log.error( msg );
				throw new Exception(msg);
			}

			//  Add Display object

			CutoffPageDisplayAnnotationLevel cutoffPageDisplayAnnotationLevel = new CutoffPageDisplayAnnotationLevel();

			cutoffPageDisplaySearchLevel.addPsmCutoffPageDisplayAnnotationLevel( cutoffPageDisplayAnnotationLevel );

			cutoffPageDisplayAnnotationLevel.setAnnotationTypeId( annotationTypeDTO.getId() );
			cutoffPageDisplayAnnotationLevel.setAnnotationName( annotationTypeDTO.getName() );
			cutoffPageDisplayAnnotationLevel.setAnnotationDescription( annotationTypeDTO.getDescription() );

			if ( annotationTypeDTO.getAnnotationTypeFilterableDTO().isDefaultFilter() ) {

				cutoffPageDisplayAnnotationLevel.setAnnotationDefaultValue( annotationTypeDTO.getAnnotationTypeFilterableDTO().getDefaultFilterValueString() );
			}

			cutoffPageDisplayAnnotationLevel.setSortOrder( annotationTypeDTO.getAnnotationTypeFilterableDTO().getSortOrder() );
			cutoffPageDisplayAnnotationLevel.setDisplayOrder( annotationTypeDTO.getDisplayOrder() );



			Integer searchProgramsPerSearchId = annotationTypeDTO.getSearchProgramsPerSearchId();
			
			SearchProgramsPerSearchDTO searchProgramsPerSearchDTO = searchProgramsPerSearchDTOMap.get( searchProgramsPerSearchId );
			
			if ( searchProgramsPerSearchDTO == null ) {
					
				String msg = "No searchProgramsPerSearchDTO record found for searchProgramsPerSearchId: " + searchProgramsPerSearchId;
				log.error( msg );

				throw new ProxlWebappDBDataOutOfSyncException( msg );
			}
			

			cutoffPageDisplayAnnotationLevel.setSearchProgramDisplayName( searchProgramsPerSearchDTO.getDisplayName() );

			//  Determine if this annotation name is duplicate across search programs
			
			//  True/False: This annotation name is duplicate across search programs
//			cutoffPageDisplayAnnotationLevel.setAnnNameDupsAcrossSrchPgms( true );
		}

	}
}



