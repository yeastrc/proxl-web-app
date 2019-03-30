package org.yeastrc.xlink.base.cleavage_missed;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.base.cleavage_sites_peptide_protein.GetTrypsinCleavageSites;

/**
 * 
 *
 */
public class GetMissedCleavageSites {

	private static final Logger log = LoggerFactory.getLogger(  GetMissedCleavageSites.class );

	/**
	 * private constructor
	 */
	private GetMissedCleavageSites() { }
	/**
	 * @return
	 */
	public static GetMissedCleavageSites getInstance() {
		GetMissedCleavageSites getCleavageSites = new GetMissedCleavageSites();
		return getCleavageSites;
	}
	
	/**
	 * result object
	 *
	 */
	public static class GetMissedCleavageSitesResult {
		private List<Integer> cleavageSiteList;
		private List<Integer> missedCleavageSiteList;
		
		public List<Integer> getCleavageSiteList() {
			return cleavageSiteList;
		}
		public void setCleavageSiteList(List<Integer> cleavageSiteList) {
			this.cleavageSiteList = cleavageSiteList;
		}
		public List<Integer> getMissedCleavageSiteList() {
			return missedCleavageSiteList;
		}
		public void setMissedCleavageSiteList(List<Integer> missedCleavageSiteList) {
			this.missedCleavageSiteList = missedCleavageSiteList;
		}
	}

	/**
	 * @param peptideOrProteinSequence
	 * @param positionsOfLinks - on peptideOrProteinSequence
	 * @return
	 */
	public GetMissedCleavageSitesResult getMissedTrypsinCleavageSites( String peptideOrProteinSequence, Set<Integer> positionsOfLinks ) {
		List<Integer> cleavageSiteList = GetTrypsinCleavageSites.getInstance().getTrypsinCleavageSites( peptideOrProteinSequence );
		return getMissedCleavageSites( peptideOrProteinSequence, cleavageSiteList, positionsOfLinks );
	}
	
	/**
	 * @param peptideOrProteinSequence
	 * @param cleavageSiteList
	 * @param positionsOfLinks - on peptideOrProteinSequence
	 * @return
	 */
	public GetMissedCleavageSitesResult getMissedCleavageSites( String peptideOrProteinSequence, List<Integer> cleavageSiteList, Set<Integer> positionsOfLinks ) {
		List<Integer> missedCleavageSiteList = new ArrayList<>( cleavageSiteList.size() );
		for ( Integer cleavageSite : cleavageSiteList ) {
			if ( ! positionsOfLinks.contains( cleavageSite ) ) {
				missedCleavageSiteList.add(cleavageSite);
			}
		}
		GetMissedCleavageSitesResult getMissedCleavageSitesResult = new GetMissedCleavageSitesResult();
		getMissedCleavageSitesResult.setCleavageSiteList( cleavageSiteList );
		getMissedCleavageSitesResult.setMissedCleavageSiteList( missedCleavageSiteList );
		return getMissedCleavageSitesResult;
	}
}
