package org.yeastrc.xlink.www.protein_coverage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;

/**
 * Hold the search paramters that are the context for a given protein's
 * sequence coverage in the context of a search and filters for that search--
 * in a nice convenient little object used for caching
 * 
 * @author mriffle
 *
 */
public class ProteinSequenceCoverageSearchParams {

	/**
	 * Build and return a ProteinSequenceCoverageSearchParams from the data present in a SearcherCutoffValuesSearchLevel object
	 * 
	 * @param scvslParams
	 * @param proteinId
	 * @return
	 */
	public static ProteinSequenceCoverageSearchParams getProteinSequenceCoverageSearchParams( SearcherCutoffValuesSearchLevel scvslParams, int proteinId  ) {
		
		ProteinSequenceCoverageSearchParams params = new ProteinSequenceCoverageSearchParams();
		
		params.setProjectSearchId( scvslParams.getProjectSearchId() );
		params.setProteinId( proteinId );
		
		Map<Integer, Double> filters = new HashMap<>();
		
		for( SearcherCutoffValuesAnnotationLevel anno : scvslParams.getPsmPerAnnotationCutoffsList() ) {
			filters.put( anno.getAnnotationTypeId(), anno.getAnnotationCutoffValue() );
		}
		
		for( SearcherCutoffValuesAnnotationLevel anno : scvslParams.getPeptidePerAnnotationCutoffsList() ) {
			filters.put( anno.getAnnotationTypeId(), anno.getAnnotationCutoffValue() );
		}
		
		params.setAnnotationFilters( filters );
		
		return params;
	}
	
	
	
	@Override
	public String toString() {
		
		String theString = this.getProjectSearchId() + "-";
		theString += this.getProteinId() + "-";
		
		List<Integer> annoIds = new ArrayList<>( this.getAnnotationFilters().keySet() );
		Collections.sort( annoIds );
		
		for( int id : annoIds ) {
			theString += id + ":" + this.getAnnotationFilters().get( id ) + ",";
		}
		
		return theString;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	@Override
	public boolean equals( Object o ) {
		
		if( o == this ) return true;
		if( !( o instanceof ProteinSequenceCoverageSearchParams ) ) return false;
		
		ProteinSequenceCoverageSearchParams pscp = (ProteinSequenceCoverageSearchParams)o;
		
		if( this.getProjectSearchId() != pscp.getProjectSearchId() ) return false;
		if( this.getProteinId() != pscp.getProteinId() ) return false;
		
		if( this.getAnnotationFilters() == null && pscp.getAnnotationFilters() == null ) return true;
		
		return this.getAnnotationFilters().equals( pscp.getAnnotationFilters() );		
	}
	
	
	public int getProjectSearchId() {
		return projectSearchId;
	}
	public void setProjectSearchId(int projectSearchId) {
		this.projectSearchId = projectSearchId;
	}
	public int getProteinId() {
		return proteinId;
	}
	public void setProteinId(int proteinId) {
		this.proteinId = proteinId;
	}
	public Map<Integer, Double> getAnnotationFilters() {
		return annotationFilters;
	}
	public void setAnnotationFilters(Map<Integer, Double> annotationFilters) {
		this.annotationFilters = annotationFilters;
	}
	
	private int projectSearchId;
	private int proteinId;
	private Map<Integer, Double> annotationFilters;
	
	

	
	
}
