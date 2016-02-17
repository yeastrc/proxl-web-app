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

	private int[] excludeProtein;


	/**
	 * Exclude Protein Encoded
	 */
	private String exclProtEnc;
	
	/**
	 * Base X RADIX encoding for exclProtEnc
	 */
	private int exclProtEncRadix;

	
	
	//  Consider populating this instead as an optimization when many proteins are excluded
//	private int[] includeProtein;
	
	
	

	public int getExclProtEncRadix() {
		return exclProtEncRadix;
	}

	public void setExclProtEncRadix(int exclProtEncRadix) {
		this.exclProtEncRadix = exclProtEncRadix;
	}

	public String getExclProtEnc() {
		return exclProtEnc;
	}

	public void setExclProtEnc(String exclProtEnc) {
		this.exclProtEnc = exclProtEnc;
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

	public int[] getExcludeProtein() {
		return excludeProtein;
	}

	public void setExcludeProtein(int[] excludeProtein) {
		this.excludeProtein = excludeProtein;
	}
	
}
