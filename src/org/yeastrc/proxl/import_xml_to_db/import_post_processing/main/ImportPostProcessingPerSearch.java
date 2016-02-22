package org.yeastrc.proxl.import_xml_to_db.import_post_processing.main;

import org.yeastrc.proxl.import_xml_to_db.import_post_processing.add_psm_generic_lookup_records.main.AddPsmGenericLookupRecordsPerSearchId;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.add_unified_rep_peptide_for_search.main.AddUnifiedReportedPeptideDataForSearchMain;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.populate__search_crosslink_looplink_lookup.PopulateSearchCrosslinkGenericLookupTable;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.populate__search_crosslink_looplink_lookup.PopulateSearchDimerGenericLookupTable;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.populate__search_crosslink_looplink_lookup.PopulateSearchLooplinkGenericLookupTable;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.populate__search_crosslink_looplink_lookup.PopulateSearchMonolinkGenericLookupTable;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.populate__search_crosslink_looplink_lookup.PopulateSearchUnlinkedGenericLookupTable;

/**
 * Processing to complete after a search is imported 
 *
 */
public class ImportPostProcessingPerSearch {
	
	public static void importPostProcessingPerSearch( int searchId ) throws Exception {
		
		

		
		System.out.println( "calling AddPsmGenericLookupRecordsPerSearchId " );
		AddPsmGenericLookupRecordsPerSearchId.getInstance().addPsmGenericLookupRecordsPerSearchId( searchId );

		
	    System.out.println( "Saving search to Unified Reported Peptide table" );
	    AddUnifiedReportedPeptideDataForSearchMain.getInstance().addUnifiedReportedPeptideDataForSearch( searchId );

	    //  Moved to do inside AddUnifiedReportedPeptideDataForSearchMain
//	    System.out.println( "Saving search to search__dynamic_mod_mass_lookup table" );
//		SearchDynamicModMassPopulateForSearchIdDAO.getInstance().searchDynamicModMassPopulateForSearchId( searchId );
	    
	    

//	    System.out.println( "Saving search to protein lookup cache..." );
//	    SearchProteinLookupDAO.getInstance().createEntriesForSearch( searchId );
	    
//	    System.out.println( "Saving search to crosslink lookup cache..." );
//	    PopulateSearchCrosslinkLookupTable.getInstance().populateSearchCrosslinkLookupTable( searchId );
//	    
//	    System.out.println( "Saving search to looplink lookup cache..." );
//	    PopulateSearchLooplinkLookupTable.getInstance().populateSearchLooplinkLookupTable( searchId );
//	    
//	    System.out.println( "Saving search to monolink lookup cache..." );
//	    SearchMonolinkLookupDAO.getInstance().createEntriesForSearch( searchId );
		

	    System.out.println( "Saving search to Crosslink protein lookup cache..." );
		PopulateSearchCrosslinkGenericLookupTable.getInstance().populateSearchCrosslinkGenericLookupTable( searchId );
		

	    System.out.println( "Saving search to Looplink protein lookup cache..." );
	    PopulateSearchLooplinkGenericLookupTable.getInstance().populateSearchLooplinkGenericLookupTable( searchId );

	    System.out.println( "Populating Search Monolink lookup tables" );
	    PopulateSearchMonolinkGenericLookupTable.getInstance().populateSearchMonolinkGenericLookupTable( searchId );

	    System.out.println( "Populating Search Dimer lookup tables" );
	    PopulateSearchDimerGenericLookupTable.getInstance().populateSearchDimerGenericLookupTable( searchId );

	    System.out.println( "Populating Search Unlinked lookup tables" );
	    PopulateSearchUnlinkedGenericLookupTable.getInstance().populateSearchUnlinkedGenericLookupTable( searchId );
	   
	}
}