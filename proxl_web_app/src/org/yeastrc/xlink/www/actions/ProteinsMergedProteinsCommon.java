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
import org.yeastrc.xlink.www.searcher.ProteinSequenceVersionIdForNrseqProteinIdSearcher;
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
	 * Populates the property excludeproteinSequenceVersionIds in the input proteinQueryJSONRoot
	 * 
	 * Process the exclude protein from the JS code
	 * 
	 * This also includes handling the old Nrseq Protein Ids, converting them to Protein Sequence Id values
	 * 
	 * @param proteinQueryJSONRoot
	 * @throws Exception 
	 */
	public void processExcludeproteinSequenceVersionIdsFromJS( ProteinQueryJSONRoot proteinQueryJSONRoot ) throws Exception {
		if ( proteinQueryJSONRoot.getExcludeProtein() != null ) {
			//  Old Nrseq Protein Ids provided, convert to Protein Sequence Ids and populate excludeproteinSequenceVersionIds
			processNrseqProteinIds( proteinQueryJSONRoot );
			return;  //  EARLY RETURN
		}
		/**
		 * Exclude Protein Encoded
		 */
		String exclproteinSequenceVersionIdsEncoded = proteinQueryJSONRoot.getExclproteinSequenceVersionIdsEncoded();
		if ( exclproteinSequenceVersionIdsEncoded == null || "".equals( exclproteinSequenceVersionIdsEncoded ) ) {
			//  No protein sequence ids to exclude
			return;  //  EARLY RETURN
		}
		/**
		 * Exclude Protein Separator
		 */
		String exclproteinSequenceVersionIdsEncodedSeparator = proteinQueryJSONRoot.getExclproteinSequenceVersionIdsEncodedSeparator();
		/**
		 * Base X RADIX encoding for exclProtEnc
		 */
		int exclproteinSequenceVersionIdsEncodedRadix = proteinQueryJSONRoot.getExclproteinSequenceVersionIdsEncodedRadix();
		
		String[] exclproteinSequenceVersionIdsEncodedSplit = exclproteinSequenceVersionIdsEncoded.split( exclproteinSequenceVersionIdsEncodedSeparator );
		
		Set<Integer> excludeproteinSequenceVersionIdsSet = new HashSet<>();
		int exclproteinSequenceVersionIdsEncodedEntryActualPrev = 0;
		for ( String exclproteinSequenceVersionIdsEncodedEntry : exclproteinSequenceVersionIdsEncodedSplit ) {
			try {
				int exclproteinSequenceVersionIdsEncodedEntryInt = 
						Integer.parseInt(exclproteinSequenceVersionIdsEncodedEntry, exclproteinSequenceVersionIdsEncodedRadix);
				//  The actual protein sequence id is the encoded id + the prev encoded id.
				int exclproteinSequenceVersionIdsEncodedEntryActual = exclproteinSequenceVersionIdsEncodedEntryInt + exclproteinSequenceVersionIdsEncodedEntryActualPrev;
				excludeproteinSequenceVersionIdsSet.add( exclproteinSequenceVersionIdsEncodedEntryActual );
				exclproteinSequenceVersionIdsEncodedEntryActualPrev = exclproteinSequenceVersionIdsEncodedEntryActual;
			} catch ( Exception e ) {
				String msg = "Failed to parse exclude protein sequence id using radix '" + exclproteinSequenceVersionIdsEncodedRadix
						+ "'.  exclude protein sequence id string: " + exclproteinSequenceVersionIdsEncodedEntry;
				log.error( msg );
				throw new ProxlBaseDataException(msg);
			}
		}
		int[] excludeproteinSequenceVersionIds = getExcludeproteinSequenceVersionIdsArray( excludeproteinSequenceVersionIdsSet );
		proteinQueryJSONRoot.setExcludeproteinSequenceVersionIds( excludeproteinSequenceVersionIds );
	}
	
	/**
	 * @param excludeproteinSequenceVersionIdsCollection
	 * @return
	 */
	private int[] getExcludeproteinSequenceVersionIdsArray( Collection<Integer> excludeproteinSequenceVersionIdsCollection ) {
		List<Integer> excludeproteinSequenceVersionIdsList = new ArrayList<>( excludeproteinSequenceVersionIdsCollection );
		Collections.sort( excludeproteinSequenceVersionIdsList );
		int[] excludeproteinSequenceVersionIds = new int[ excludeproteinSequenceVersionIdsList.size() ];
		for ( int index = 0; index < excludeproteinSequenceVersionIdsList.size(); index++ ) {
			excludeproteinSequenceVersionIds[ index ] = excludeproteinSequenceVersionIdsList.get(index);
		}
		return excludeproteinSequenceVersionIds;
	}
	
	/**
	 * @param proteinQueryJSONRoot
	 * @throws Exception 
	 */
	private void processNrseqProteinIds( ProteinQueryJSONRoot proteinQueryJSONRoot  ) throws Exception {
		int[] excludeNrseqProteinIds = proteinQueryJSONRoot.getExcludeProtein();
		Set<Integer> excludeproteinSequenceVersionIdsSet = new HashSet<>();
		for ( int nrseqProteinId : excludeNrseqProteinIds ) {
			Integer proteinSequenceVersionId =
					ProteinSequenceVersionIdForNrseqProteinIdSearcher.getInstance()
					.getProteinSequenceVersionIdForNrseqProteinIdSearcher( nrseqProteinId );
			if ( proteinSequenceVersionId == null ) {
				String msg = "No protein sequence id found for nrseqProteinId: " + nrseqProteinId;
				log.error( msg );
			    throw new ProxlWebappDataException( msg );
			}
			excludeproteinSequenceVersionIdsSet.add( proteinSequenceVersionId );
		}
		int[] excludeproteinSequenceVersionIds = getExcludeproteinSequenceVersionIdsArray( excludeproteinSequenceVersionIdsSet );
		proteinQueryJSONRoot.setExcludeproteinSequenceVersionIds( excludeproteinSequenceVersionIds );
	}
}
