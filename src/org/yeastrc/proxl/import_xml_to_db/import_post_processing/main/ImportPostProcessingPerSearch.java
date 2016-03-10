package org.yeastrc.proxl.import_xml_to_db.import_post_processing.main;

import java.util.Date;

import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
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
		
		

	    ImportDBConnectionFactory.getInstance().commitInsertControlCommitConnection();
		
		System.out.println( "calling AddPsmGenericLookupRecordsPerSearchId " );
		AddPsmGenericLookupRecordsPerSearchId.getInstance().addPsmGenericLookupRecordsPerSearchId( searchId );

	    ImportDBConnectionFactory.getInstance().commitInsertControlCommitConnection();
		
	    System.out.println( "Saving search to Unified Reported Peptide table" );
	    AddUnifiedReportedPeptideDataForSearchMain.getInstance().addUnifiedReportedPeptideDataForSearch( searchId );

	    //  Moved to do inside AddUnifiedReportedPeptideDataForSearchMain
//	    System.out.println( "Saving search to search__dynamic_mod_mass_lookup table" );
//		SearchDynamicModMassPopulateForSearchIdDAO.getInstance().searchDynamicModMassPopulateForSearchId( searchId );
	    
	    
	    ImportDBConnectionFactory.getInstance().commitInsertControlCommitConnection();
	    

	    System.out.println( "Starting populating Search Crosslink lookup tables.  Now: " + new Date() );
		PopulateSearchCrosslinkGenericLookupTable.getInstance().populateSearchCrosslinkGenericLookupTable( searchId );
	    System.out.println( "Finished populating Search Crosslink lookup tables.  Now: " + new Date() );

	    System.out.println( "Starting populating Search Looplink lookup tables.  Now: " + new Date() );
	    PopulateSearchLooplinkGenericLookupTable.getInstance().populateSearchLooplinkGenericLookupTable( searchId );
	    System.out.println( "Finished populating Search Looplink lookup tables.  Now: " + new Date() );

	    System.out.println( "Starting populating Search Monolink lookup tables.  Now: " + new Date() );
	    PopulateSearchMonolinkGenericLookupTable.getInstance().populateSearchMonolinkGenericLookupTable( searchId );
	    System.out.println( "Finished populating Search Monolink lookup tables.  Now: " + new Date() );

	    System.out.println( "Starting populating Search Dimer lookup tables.  Now: " + new Date() );
	    PopulateSearchDimerGenericLookupTable.getInstance().populateSearchDimerGenericLookupTable( searchId );
	    System.out.println( "Finished populating Search Dimer lookup tables.  Now: " + new Date() );

	    System.out.println( "Starting populating Search Unlinked lookup tables.  Now: " + new Date() );
	    PopulateSearchUnlinkedGenericLookupTable.getInstance().populateSearchUnlinkedGenericLookupTable( searchId );
	    System.out.println( "Finished populating Search Unlinked lookup tables.  Now: " + new Date() );

	    System.out.println( "");
	    
	    ImportDBConnectionFactory.getInstance().commitInsertControlCommitConnection();
		
	}
}