package org.yeastrc.xlink.www.form_query_json_objects;

/**
 * The Root object for the hash value to pass to Image and Structure and QC pages
 *
 */
public class ImageStructure_QC_QueryJSONRoot extends A_QueryBase_JSONRoot {
	//  A_QueryBase_JSONRoot contains the PSM and Peptide cutoffs
	
	private int[] excludeTaxonomy;
	private boolean filterNonUniquePeptides;
	private boolean filterOnlyOnePeptide;

	//  Moved to base class A_QueryBase_JSONRoot since also used in Peptide Page
//	private boolean filterOnlyOnePSM;
	
	////////
	//   Constuctors
	public ImageStructure_QC_QueryJSONRoot() {}
	/**
	 * @param peptideQueryJSONRoot
	 */
	public ImageStructure_QC_QueryJSONRoot( PeptideQueryJSONRoot peptideQueryJSONRoot ) {
		super( peptideQueryJSONRoot );
	}
	/**
	 * @param proteinQueryJSONRoot
	 */
	public ImageStructure_QC_QueryJSONRoot( ProteinQueryJSONRoot proteinQueryJSONRoot ) {
		super( proteinQueryJSONRoot );
		this.excludeTaxonomy = proteinQueryJSONRoot.getExcludeTaxonomy();
		this.filterNonUniquePeptides = proteinQueryJSONRoot.isFilterNonUniquePeptides();
		this.filterOnlyOnePeptide = proteinQueryJSONRoot.isFilterOnlyOnePeptide();
	}
	
	//////////////////////////////////////////
	///    getters setters
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
}
