package org.yeastrc.proxl.import_xml_to_db.post_insert_search_processing;

import org.yeastrc.proxl.import_xml_to_db.dto.SearchDTO_Importer;
//import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.process_input.ProcessProxlInput.ReportedPeptideAndPsmFilterableAnnotationTypesOnId;

/**
 * Perform Processing done after the search is inserted
 *
 */
public class PerformPostInsertSearchProcessing {
	
//	private static final Logger log = Logger.getLogger( PerformPostInsertSearchProcessing.class );
	/**
	 * private constructor
	 */
	private PerformPostInsertSearchProcessing(){}
	public static PerformPostInsertSearchProcessing getInstance() {
		return new PerformPostInsertSearchProcessing();
	}

	/**
	 * @param searchId
	 * @throws Exception 
	 */
	public void performPostInsertSearchProcessing( 
			SearchDTO_Importer search,
			ReportedPeptideAndPsmFilterableAnnotationTypesOnId reportedPeptideAndPsmFilterableAnnotationTypesOnId ) throws Exception {
		
		UpdateLookupTablesPostInsertSearch.getInstance()
		.updateLookupTablesPostInsertSearch( search, reportedPeptideAndPsmFilterableAnnotationTypesOnId );
		
	}
}
