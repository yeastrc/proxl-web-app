package org.yeastrc.proxl.import_xml_to_db.post_insert_search_processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yeastrc.proxl.import_xml_to_db.dto.SearchDTO_Importer;
// import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.process_input.ProcessProxlInput.ReportedPeptideAndPsmFilterableAnnotationTypesOnId;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_utils.CreateSearcherCutoffValuesSearchLevelFromDefaultsInTypeRecords;

/**
 * Update Lookup tables after search is inserted
 *
 */
public class UpdateLookupTablesPostInsertSearch {

//	private static final Logger log = Logger.getLogger( UpdateLookupTablesPostInsertSearch.class );
	/**
	 * private constructor
	 */
	private UpdateLookupTablesPostInsertSearch(){}
	public static UpdateLookupTablesPostInsertSearch getInstance() {
		return new UpdateLookupTablesPostInsertSearch();
	}

	/**
	 * @param searchId
	 * @throws Exception 
	 */
	public void updateLookupTablesPostInsertSearch( 
			SearchDTO_Importer search,
			ReportedPeptideAndPsmFilterableAnnotationTypesOnId reportedPeptideAndPsmFilterableAnnotationTypesOnId ) throws Exception {

		int searchId = search.getId();
		
		SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = 
				buildSearcherCutoffValuesSearchLevel( searchId, reportedPeptideAndPsmFilterableAnnotationTypesOnId );
		
		Update_unified_rp__search__rep_pept__generic_lookup_table.getInstance()
		.update_unified_rp__search__rep_pept__generic_lookup_table( search, searcherCutoffValuesSearchLevel );

	}
	
	/**
	 * Create SearcherCutoffValuesSearchLevel object with default cutoffs
	 * 
	 * Produce a list of SearcherCutoffValuesAnnotationLevel for default cutoffs in filterableAnnotationTypesOnId
	 * @param filterableAnnotationTypesOnId
	 * @return SearcherCutoffValuesSearchLevel
	 * @throws Exception 
	 */
	public SearcherCutoffValuesSearchLevel buildSearcherCutoffValuesSearchLevel( 
			int searchId,
			ReportedPeptideAndPsmFilterableAnnotationTypesOnId reportedPeptideAndPsmFilterableAnnotationTypesOnId ) throws Exception {
		
		Map<Integer, AnnotationTypeDTO> reportedPeptideFilterableAnnotationTypesOnId = 
				reportedPeptideAndPsmFilterableAnnotationTypesOnId.getFilterableReportedPeptideAnnotationTypesOnId();
		Map<Integer, AnnotationTypeDTO> psmFilterableAnnotationTypesOnId = 
				reportedPeptideAndPsmFilterableAnnotationTypesOnId.getFilterablePsmAnnotationTypesOnId();

		//  Build lists of AnnotationTypeDTO for reported peptide and psm
		List<AnnotationTypeDTO> reportedPeptideAnnotationTypeList = null;
		if ( reportedPeptideFilterableAnnotationTypesOnId != null ) {
			reportedPeptideAnnotationTypeList = new ArrayList<>( reportedPeptideFilterableAnnotationTypesOnId.size() );
			for ( Map.Entry<Integer, AnnotationTypeDTO> annotationTypeEntry : reportedPeptideFilterableAnnotationTypesOnId.entrySet() ) {
				reportedPeptideAnnotationTypeList.add( annotationTypeEntry.getValue() );
			}
		} else {
			reportedPeptideAnnotationTypeList = new ArrayList<>();
		}
		List<AnnotationTypeDTO> psmAnnotationTypeList = null;
		if ( psmFilterableAnnotationTypesOnId != null ) {
			psmAnnotationTypeList = new ArrayList<>( psmFilterableAnnotationTypesOnId.size() );
			for ( Map.Entry<Integer, AnnotationTypeDTO> annotationTypeEntry : psmFilterableAnnotationTypesOnId.entrySet() ) {
				psmAnnotationTypeList.add( annotationTypeEntry.getValue() );
			}
		} else {
			psmAnnotationTypeList = new ArrayList<>();
		}
		
		SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
				CreateSearcherCutoffValuesSearchLevelFromDefaultsInTypeRecords.getInstance()
				.createSearcherCutoffValuesSearchLevelFromDefaultsInTypeRecords( 
						searchId, psmAnnotationTypeList, reportedPeptideAnnotationTypeList );
		
		return searcherCutoffValuesSearchLevel;
	}


}
