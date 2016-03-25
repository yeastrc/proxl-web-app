package org.yeastrc.proxl.import_xml_to_db.import_post_processing.main;

import java.util.Date;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.add_link_per_peptide_generic_lookup_records.main.AddLinkPerPeptideGenericLookupRecordsPerSearchId;
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

	private static final Logger log = Logger.getLogger( ImportPostProcessingPerSearch.class );

	public static void importPostProcessingPerSearch( int searchId ) throws Exception {


		if ( log.isInfoEnabled() ) {

			log.info( "calling AddPsmGenericLookupRecordsPerSearchId   Now: " + new Date() );
		}

		AddPsmGenericLookupRecordsPerSearchId.getInstance().addPsmGenericLookupRecordsPerSearchId( searchId );

		if ( log.isInfoEnabled() ) {

			log.info( "Saving search to Unified Reported Peptide table Now: " + new Date() );
		}
		AddUnifiedReportedPeptideDataForSearchMain.getInstance().addUnifiedReportedPeptideDataForSearch( searchId );

		//  Moved to do inside AddUnifiedReportedPeptideDataForSearchMain
		//	    log.info( "Saving search to search__dynamic_mod_mass_lookup table" );
		//		SearchDynamicModMassPopulateForSearchIdDAO.getInstance().searchDynamicModMassPopulateForSearchId( searchId );

		if ( log.isInfoEnabled() ) {

			log.info( "calling AddLinkPerPeptideGenericLookupRecordsPerSearchId   Now: " + new Date() );
		}
		AddLinkPerPeptideGenericLookupRecordsPerSearchId.getInstance().addLinkPerPeptideGenericLookupRecordsPerSearchId( searchId );


		if ( log.isInfoEnabled() ) {

			log.info( "Starting populating Search Crosslink lookup tables.  Now: " + new Date() );
		}
		PopulateSearchCrosslinkGenericLookupTable.getInstance().populateSearchCrosslinkGenericLookupTable( searchId );
		if ( log.isInfoEnabled() ) {

			log.info( "Finished populating Search Crosslink lookup tables.  Now: " + new Date() );
		}

		if ( log.isInfoEnabled() ) {

			log.info( "Starting populating Search Looplink lookup tables.  Now: " + new Date() );
		}
		PopulateSearchLooplinkGenericLookupTable.getInstance().populateSearchLooplinkGenericLookupTable( searchId );
		if ( log.isInfoEnabled() ) {

			log.info( "Finished populating Search Looplink lookup tables.  Now: " + new Date() );
		}

		if ( log.isInfoEnabled() ) {

			log.info( "Starting populating Search Monolink lookup tables.  Now: " + new Date() );
		}
		PopulateSearchMonolinkGenericLookupTable.getInstance().populateSearchMonolinkGenericLookupTable( searchId );
		if ( log.isInfoEnabled() ) {

			log.info( "Finished populating Search Monolink lookup tables.  Now: " + new Date() );
		}

		if ( log.isInfoEnabled() ) {

			log.info( "Starting populating Search Dimer lookup tables.  Now: " + new Date() );
		}
		PopulateSearchDimerGenericLookupTable.getInstance().populateSearchDimerGenericLookupTable( searchId );
		if ( log.isInfoEnabled() ) {

			log.info( "Finished populating Search Dimer lookup tables.  Now: " + new Date() );
		}

		if ( log.isInfoEnabled() ) {

			log.info( "Starting populating Search Unlinked lookup tables.  Now: " + new Date() );
		}
		PopulateSearchUnlinkedGenericLookupTable.getInstance().populateSearchUnlinkedGenericLookupTable( searchId );
		if ( log.isInfoEnabled() ) {

			log.info( "Finished populating Search Unlinked lookup tables.  Now: " + new Date() );
		}

		if ( log.isInfoEnabled() ) {

			log.info( "");
		}

		ImportDBConnectionFactory.getInstance().commitInsertControlCommitConnection();

	}
}