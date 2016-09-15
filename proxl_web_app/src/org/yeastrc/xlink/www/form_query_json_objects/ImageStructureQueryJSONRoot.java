package org.yeastrc.xlink.www.form_query_json_objects;


/**
 * The Root object for the hash value to pass to Image and Structure pages
 *
 */
public class ImageStructureQueryJSONRoot extends A_QueryBase_JSONRoot {

	
	//  A_QueryBase_JSONRoot contains the PSM and Peptide cutoffs
	
	
	private int[] excludeTaxonomy;
	private boolean filterNonUniquePeptides;
	private boolean filterOnlyOnePSM;
	private boolean filterOnlyOnePeptide;

	
	////////
	
	//   Constuctors
	
	public ImageStructureQueryJSONRoot() {}
	

	/**
	 * @param peptideQueryJSONRoot
	 */
	public ImageStructureQueryJSONRoot( PeptideQueryJSONRoot peptideQueryJSONRoot ) {
		
		super( peptideQueryJSONRoot );
	}
	
	/**
	 * @param proteinQueryJSONRoot
	 */
	public ImageStructureQueryJSONRoot( ProteinQueryJSONRoot proteinQueryJSONRoot ) {
		
		super( proteinQueryJSONRoot );
		
		this.excludeTaxonomy = proteinQueryJSONRoot.getExcludeTaxonomy();
		this.filterNonUniquePeptides = proteinQueryJSONRoot.isFilterNonUniquePeptides();
		this.filterOnlyOnePSM = proteinQueryJSONRoot.isFilterOnlyOnePSM();
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

	
}
