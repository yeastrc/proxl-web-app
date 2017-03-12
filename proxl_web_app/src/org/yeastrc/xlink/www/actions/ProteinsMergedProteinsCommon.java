package org.yeastrc.xlink.www.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.exceptions.ProxlBaseDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.searcher.ProteinSequenceIdForNrseqProteinIdSearcher;
/**
 * Shared code for Protein page (single search) and Merged Proteins ( Multiple searches ) 
 *
 */
public class ProteinsMergedProteinsCommon {
	
	private static final Logger log = Logger.getLogger( ProteinsMergedProteinsCommon.class );
	
	private ProteinsMergedProteinsCommon() { }
	public static ProteinsMergedProteinsCommon getInstance() { 
		return new ProteinsMergedProteinsCommon(); 
	}
	
	/**
	 * Populates the property excludeProteinSequenceIds in the input proteinQueryJSONRoot
	 * 
	 * Process the exclude protein from the JS code
	 * 
	 * This also includes handling the old Nrseq Protein Ids, converting them to Protein Sequence Id values
	 * 
	 * @param proteinQueryJSONRoot
	 * @throws Exception 
	 */
	public void processExcludeProteinSequenceIdsFromJS( ProteinQueryJSONRoot proteinQueryJSONRoot ) throws Exception {
		if ( proteinQueryJSONRoot.getExcludeProtein() != null ) {
			//  Old Nrseq Protein Ids provided, convert to Protein Sequence Ids and populate excludeProteinSequenceIds
			processNrseqProteinIds( proteinQueryJSONRoot );
			return;  //  EARLY RETURN
		}
		/**
		 * Exclude Protein Encoded
		 */
		String exclProteinSequenceIdsEncoded = proteinQueryJSONRoot.getExclProteinSequenceIdsEncoded();
		if ( exclProteinSequenceIdsEncoded == null || "".equals( exclProteinSequenceIdsEncoded ) ) {
			//  No protein sequence ids to exclude
			return;  //  EARLY RETURN
		}
		/**
		 * Exclude Protein Separator
		 */
		String exclProteinSequenceIdsEncodedSeparator = proteinQueryJSONRoot.getExclProteinSequenceIdsEncodedSeparator();
		/**
		 * Base X RADIX encoding for exclProtEnc
		 */
		int exclProteinSequenceIdsEncodedRadix = proteinQueryJSONRoot.getExclProteinSequenceIdsEncodedRadix();
		
		String[] exclProteinSequenceIdsEncodedSplit = exclProteinSequenceIdsEncoded.split( exclProteinSequenceIdsEncodedSeparator );
		
		Set<Integer> excludeProteinSequenceIdsSet = new HashSet<>();
		int exclProteinSequenceIdsEncodedEntryActualPrev = 0;
		for ( String exclProteinSequenceIdsEncodedEntry : exclProteinSequenceIdsEncodedSplit ) {
			try {
				int exclProteinSequenceIdsEncodedEntryInt = 
						Integer.parseInt(exclProteinSequenceIdsEncodedEntry, exclProteinSequenceIdsEncodedRadix);
				//  The actual protein sequence id is the encoded id + the prev encoded id.
				int exclProteinSequenceIdsEncodedEntryActual = exclProteinSequenceIdsEncodedEntryInt + exclProteinSequenceIdsEncodedEntryActualPrev;
				excludeProteinSequenceIdsSet.add( exclProteinSequenceIdsEncodedEntryActual );
				exclProteinSequenceIdsEncodedEntryActualPrev = exclProteinSequenceIdsEncodedEntryActual;
			} catch ( Exception e ) {
				String msg = "Failed to parse exclude protein sequence id using radix '" + exclProteinSequenceIdsEncodedRadix
						+ "'.  exclude protein sequence id string: " + exclProteinSequenceIdsEncodedEntry;
				log.error( msg );
				throw new ProxlBaseDataException(msg);
			}
		}
		int[] excludeProteinSequenceIds = getExcludeProteinSequenceIdsArray( excludeProteinSequenceIdsSet );
		proteinQueryJSONRoot.setExcludeProteinSequenceIds( excludeProteinSequenceIds );
	}
	
	/**
	 * @param excludeProteinSequenceIdsCollection
	 * @return
	 */
	private int[] getExcludeProteinSequenceIdsArray( Collection<Integer> excludeProteinSequenceIdsCollection ) {
		List<Integer> excludeProteinSequenceIdsList = new ArrayList<>( excludeProteinSequenceIdsCollection );
		Collections.sort( excludeProteinSequenceIdsList );
		int[] excludeProteinSequenceIds = new int[ excludeProteinSequenceIdsList.size() ];
		for ( int index = 0; index < excludeProteinSequenceIdsList.size(); index++ ) {
			excludeProteinSequenceIds[ index ] = excludeProteinSequenceIdsList.get(index);
		}
		return excludeProteinSequenceIds;
	}
	
	/**
	 * @param proteinQueryJSONRoot
	 * @throws Exception 
	 */
	private void processNrseqProteinIds( ProteinQueryJSONRoot proteinQueryJSONRoot  ) throws Exception {
		int[] excludeNrseqProteinIds = proteinQueryJSONRoot.getExcludeProtein();
		Set<Integer> excludeProteinSequenceIdsSet = new HashSet<>();
		for ( int nrseqProteinId : excludeNrseqProteinIds ) {
			Integer proteinSequenceId =
					ProteinSequenceIdForNrseqProteinIdSearcher.getInstance()
					.getProteinSequenceIdForNrseqProteinIdSearcher( nrseqProteinId );
			if ( proteinSequenceId == null ) {
				String msg = "No protein sequence id found for nrseqProteinId: " + nrseqProteinId;
				log.error( msg );
			    throw new ProxlWebappDataException( msg );
			}
			excludeProteinSequenceIdsSet.add( proteinSequenceId );
		}
		int[] excludeProteinSequenceIds = getExcludeProteinSequenceIdsArray( excludeProteinSequenceIdsSet );
		proteinQueryJSONRoot.setExcludeProteinSequenceIds( excludeProteinSequenceIds );
	}
}
