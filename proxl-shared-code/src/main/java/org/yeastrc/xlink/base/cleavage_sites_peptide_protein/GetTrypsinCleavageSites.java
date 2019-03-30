package org.yeastrc.xlink.base.cleavage_sites_peptide_protein;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


/**
 * GetTrypsinCleavageSites for Peptide or Protein Sequence
 * 
 * return a "1" based list of positions where each position is immediately before each cleavage site
 */
public class GetTrypsinCleavageSites {
	
	private static final Logger log = LoggerFactory.getLogger(  GetTrypsinCleavageSites.class );
	
	private static final Pattern TRYPSIN_CLEAVAGE_REGEX_PATTERN = Pattern.compile( "[KR][^P]" );
	
	private static final int ADVANCE_INDEX_FOR_NEXT_SEARCH_OFFSET = 1;
	
	/**
	 * private constructor
	 */
	private GetTrypsinCleavageSites() { }
	/**
	 * @return
	 */
	public static GetTrypsinCleavageSites getInstance() {
		GetTrypsinCleavageSites getCleavageSites = new GetTrypsinCleavageSites();
		return getCleavageSites;
	}
	
	/**
	 * return a "1" based list of positions where each position is immediately before each cleavage site
	 * 
	 * @param peptideOrProteinSequence
	 * @return a "1" based list of positions where each position is immediately before each cleavage site
	 */
	public List<Integer> getTrypsinCleavageSites( String peptideOrProteinSequence ) {
		if ( StringUtils.isEmpty( peptideOrProteinSequence ) ) {
			throw new IllegalArgumentException( "param peptideOrProteinSequence is empty or null" );
		}
		List<Integer> cleavageSites = new ArrayList<>();
		Matcher cleavageRegexMatcher = TRYPSIN_CLEAVAGE_REGEX_PATTERN.matcher( peptideOrProteinSequence );
		int findStartIndex = 0;
		
		//  This approach is specific to Trypsin and may not apply to others, specifically size of ADVANCE_INDEX_FOR_NEXT_SEARCH_OFFSET
		while ( cleavageRegexMatcher.find( findStartIndex ) ) {
			int foundMatchIndex = cleavageRegexMatcher.start();  // foundMatchIndex is zero based
			int foundMatchPosition = foundMatchIndex + 1;  //  positions are "1" based, so add "1"
			cleavageSites.add( foundMatchPosition );
			findStartIndex = foundMatchIndex + ADVANCE_INDEX_FOR_NEXT_SEARCH_OFFSET; // change search start to after start of current find 
		}
		
		return cleavageSites;
	}
	
}
