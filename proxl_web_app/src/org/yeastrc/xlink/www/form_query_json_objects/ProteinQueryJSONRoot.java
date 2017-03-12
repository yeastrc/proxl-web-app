package org.yeastrc.xlink.www.form_query_json_objects;

/**
 * The Root object for the query for the Search Protein and Merged Protein Pages (Crosslink and Looplink and Coverage)
 *
 */
public class ProteinQueryJSONRoot extends A_QueryBase_JSONRoot {
	//  A_QueryBase_JSONRoot contains the PSM and Peptide cutoffs

	private int[] excludeTaxonomy;
	private boolean filterNonUniquePeptides;
	private boolean filterOnlyOnePSM;
	private boolean filterOnlyOnePeptide;
	
	/**
	 * 
	 */
	private int[] excludeProteinSequenceIds;
	/**
	 * Exclude Protein Encoded
	 */
	private String exclProteinSequenceIdsEncoded;
	/**
	 * Exclude Protein Separator
	 */
	private String exclProteinSequenceIdsEncodedSeparator;
	/**
	 * Base X RADIX encoding for exclProtEnc
	 */
	private int exclProteinSequenceIdsEncodedRadix;
	/**
	 * OLD  Nrseq Protein Ids
	 */
	private int[] excludeProtein;
	
	//  Consider populating this instead as an optimization when many proteins are excluded
//	private int[] includeProtein;
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
	public boolean isFilterOnlyOnePSM() {
		return filterOnlyOnePSM;
	}
	public void setFilterOnlyOnePSM(boolean filterOnlyOnePSM) {
		this.filterOnlyOnePSM = filterOnlyOnePSM;
	}
	public boolean isFilterOnlyOnePeptide() {
		return filterOnlyOnePeptide;
	}
	public void setFilterOnlyOnePeptide(boolean filterOnlyOnePeptide) {
		this.filterOnlyOnePeptide = filterOnlyOnePeptide;
	}
	public String getExclProteinSequenceIdsEncoded() {
		return exclProteinSequenceIdsEncoded;
	}
	public void setExclProteinSequenceIdsEncoded(
			String exclProteinSequenceIdsEncoded) {
		this.exclProteinSequenceIdsEncoded = exclProteinSequenceIdsEncoded;
	}
	public int getExclProteinSequenceIdsEncodedRadix() {
		return exclProteinSequenceIdsEncodedRadix;
	}
	public void setExclProteinSequenceIdsEncodedRadix(
			int exclProteinSequenceIdsEncodedRadix) {
		this.exclProteinSequenceIdsEncodedRadix = exclProteinSequenceIdsEncodedRadix;
	}
	public String getExclProteinSequenceIdsEncodedSeparator() {
		return exclProteinSequenceIdsEncodedSeparator;
	}
	public void setExclProteinSequenceIdsEncodedSeparator(
			String exclProteinSequenceIdsEncodedSeparator) {
		this.exclProteinSequenceIdsEncodedSeparator = exclProteinSequenceIdsEncodedSeparator;
	}
	public int[] getExcludeProteinSequenceIds() {
		return excludeProteinSequenceIds;
	}
	public void setExcludeProteinSequenceIds(int[] excludeProteinSequenceIds) {
		this.excludeProteinSequenceIds = excludeProteinSequenceIds;
	}
}
