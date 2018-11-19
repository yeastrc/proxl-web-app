package org.yeastrc.xlink.www.form_query_json_objects;

/**
 * The Root object for the query for the Search Protein and Merged Protein Pages (Crosslink and Looplink and Coverage)
 *
 */
public class ProteinQueryJSONRoot extends A_QueryBase_JSONRoot {
	//  A_QueryBase_JSONRoot contains the PSM and Peptide cutoffs

	/**
	 * Only for ViewSearchProteinsAllAction
	 */
	private String[] linkTypes;
	
	private int[] excludeTaxonomy;
	private boolean filterNonUniquePeptides;
	private boolean filterOnlyOnePeptide;
	
	/**
	 * 
	 */
	private int[] excludeproteinSequenceVersionIds;
	/**
	 * Exclude Protein Encoded
	 */
	private String exclproteinSequenceVersionIdsEncoded;
	/**
	 * Exclude Protein Separator
	 */
	private String exclproteinSequenceVersionIdsEncodedSeparator;
	/**
	 * Base X RADIX encoding for exclProtEnc
	 */
	private int exclproteinSequenceVersionIdsEncodedRadix;
	/**
	 * OLD  Nrseq Protein Ids
	 */
	private int[] excludeProtein;
	
	/**
	 * OLD  Nrseq Protein Ids
	 * 
	 * @return
	 */
	public int[] getExcludeProtein() {
		return excludeProtein;
	}
	/**
	 * OLD  Nrseq Protein Ids
	 * 
	 * @param excludeProtein
	 */
	public void setExcludeProtein(int[] excludeProtein) {
		this.excludeProtein = excludeProtein;
	}
	/**
	 * Only for ViewSearchProteinsAllAction
	 * @return
	 */
	public String[] getLinkTypes() {
		return linkTypes;
	}
	/**
	 * Only for ViewSearchProteinsAllAction
	 * @param linkTypes
	 */
	public void setLinkTypes(String[] linkTypes) {
		this.linkTypes = linkTypes;
	}
	
	public int[] getExcludeTaxonomy() {
		return excludeTaxonomy;
	}
	public void setExcludeTaxonomy(int[] excludeTaxonomy) {
		this.excludeTaxonomy = excludeTaxonomy;
	}
	public boolean isFilterNonUniquePeptides() {
		return filterNonUniquePeptides;
	}
	public void setFilterNonUniquePeptides(boolean filterNonUniquePeptides) {
		this.filterNonUniquePeptides = filterNonUniquePeptides;
	}
	public boolean isFilterOnlyOnePeptide() {
		return filterOnlyOnePeptide;
	}
	public void setFilterOnlyOnePeptide(boolean filterOnlyOnePeptide) {
		this.filterOnlyOnePeptide = filterOnlyOnePeptide;
	}
	public String getExclproteinSequenceVersionIdsEncoded() {
		return exclproteinSequenceVersionIdsEncoded;
	}
	public void setExclproteinSequenceVersionIdsEncoded(
			String exclproteinSequenceVersionIdsEncoded) {
		this.exclproteinSequenceVersionIdsEncoded = exclproteinSequenceVersionIdsEncoded;
	}
	public int getExclproteinSequenceVersionIdsEncodedRadix() {
		return exclproteinSequenceVersionIdsEncodedRadix;
	}
	public void setExclproteinSequenceVersionIdsEncodedRadix(
			int exclproteinSequenceVersionIdsEncodedRadix) {
		this.exclproteinSequenceVersionIdsEncodedRadix = exclproteinSequenceVersionIdsEncodedRadix;
	}
	public String getExclproteinSequenceVersionIdsEncodedSeparator() {
		return exclproteinSequenceVersionIdsEncodedSeparator;
	}
	public void setExclproteinSequenceVersionIdsEncodedSeparator(
			String exclproteinSequenceVersionIdsEncodedSeparator) {
		this.exclproteinSequenceVersionIdsEncodedSeparator = exclproteinSequenceVersionIdsEncodedSeparator;
	}
	public int[] getExcludeproteinSequenceVersionIds() {
		return excludeproteinSequenceVersionIds;
	}
	public void setExcludeproteinSequenceVersionIds(int[] excludeproteinSequenceVersionIds) {
		this.excludeproteinSequenceVersionIds = excludeproteinSequenceVersionIds;
	}

}
