package org.yeastrc.xlink.searcher_psm_peptide_cutoff_utils;

import java.util.List;

import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;

/**
 * 
 * Create SearcherCutoffValuesSearchLevel with default values from Annotation Type records
 */
public class CreateSearcherCutoffValuesSearchLevelFromDefaultsInTypeRecords {

	private static final Logger log = Logger.getLogger( CreateSearcherCutoffValuesSearchLevelFromDefaultsInTypeRecords.class );
	
	/**
	 * private constructor
	 */
	private CreateSearcherCutoffValuesSearchLevelFromDefaultsInTypeRecords() { }

	public static CreateSearcherCutoffValuesSearchLevelFromDefaultsInTypeRecords getInstance() {
		
		return new CreateSearcherCutoffValuesSearchLevelFromDefaultsInTypeRecords();
	}
	
	/**
	 * Create SearcherCutoffValuesSearchLevel with default values from Annotation Type records
	 * 
	 * @param searchId
	 * @param srchPgm_Filterable_Psm_AnnotationType_DTOList
	 * @param srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList
	 * @return
	 * @throws Exception 
	 */
	public SearcherCutoffValuesSearchLevel createSearcherCutoffValuesSearchLevelFromDefaultsInTypeRecords( 

			int searchId,
			
			List<AnnotationTypeDTO> srchPgm_Filterable_Psm_AnnotationType_DTOList,
			
			List<AnnotationTypeDTO> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList

			) throws Exception {
		

		SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = new SearcherCutoffValuesSearchLevel();
		
		searcherCutoffValuesSearchLevel.setProjectSearchId( searchId );
		
		
		for ( AnnotationTypeDTO item : srchPgm_Filterable_Psm_AnnotationType_DTOList ) {

			if ( item.getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + item.getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			if ( item.getAnnotationTypeFilterableDTO().isDefaultFilter() ) {

				SearcherCutoffValuesAnnotationLevel output = new SearcherCutoffValuesAnnotationLevel();

				output.setAnnotationTypeId( item.getId() );
				output.setAnnotationCutoffValue( item.getAnnotationTypeFilterableDTO().getDefaultFilterValue() );
				output.setAnnotationTypeDTO( item );
				
				searcherCutoffValuesSearchLevel.addPsmPerAnnotationCutoffs( output );
			}
		}

		for ( AnnotationTypeDTO item : srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOList ) {

			if ( item.getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + item.getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			if ( item.getAnnotationTypeFilterableDTO().isDefaultFilter() ) {

				SearcherCutoffValuesAnnotationLevel output = new SearcherCutoffValuesAnnotationLevel();

				output.setAnnotationTypeId( item.getId() );
				output.setAnnotationCutoffValue( item.getAnnotationTypeFilterableDTO().getDefaultFilterValue() );
				output.setAnnotationTypeDTO( item );

				searcherCutoffValuesSearchLevel.addPeptidePerAnnotationCutoffs( output );
			}
		}
		
		
		return searcherCutoffValuesSearchLevel;
	}
}
