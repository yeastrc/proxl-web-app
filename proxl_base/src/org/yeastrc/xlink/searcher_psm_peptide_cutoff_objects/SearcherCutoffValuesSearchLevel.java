package org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Entry per Search
 *
 */
public class SearcherCutoffValuesSearchLevel {

	private int searchId;

	/**
	 * Key is annotation id
	 */
	private Map<Integer, SearcherCutoffValuesAnnotationLevel> psmCutoffValuesPerAnnotationId = new HashMap<>();
	/**
	 * Key is annotation id
	 */
	private Map<Integer, SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesPerAnnotationId = new HashMap<>();
	
	//  Cached list version of data in map
	private List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesPerAnnotationIdList;
	private List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesPerAnnotationIdList;
	
	
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}

	///////////////////////
	
	//  PSM

	/**
	 * Get SearcherCutoffValuesAnnotationLevel object for annotation id
	 * @param annotationId
	 * @return
	 */
	public SearcherCutoffValuesAnnotationLevel getPsmPerAnnotationCutoffs( Integer annotationId ) {
		return psmCutoffValuesPerAnnotationId.get( annotationId );
	}

	/**
	 * @param perAnnotationCutoffs
	 */
	public void addPsmPerAnnotationCutoffs( SearcherCutoffValuesAnnotationLevel perAnnotationCutoffs ) {
		
		this.psmCutoffValuesPerAnnotationId.put( perAnnotationCutoffs.getAnnotationTypeId(), perAnnotationCutoffs);
		
		psmCutoffValuesPerAnnotationIdList = null;  // ensure cached list is null
	}

	/**
	 * Get searches in list form
	 * @return
	 */
	public List<SearcherCutoffValuesAnnotationLevel> getPsmPerAnnotationCutoffsList() {
		
		if ( psmCutoffValuesPerAnnotationIdList == null ) {
			
			psmCutoffValuesPerAnnotationIdList = new ArrayList<>( psmCutoffValuesPerAnnotationId.size() );
			
			for ( Map.Entry<Integer,SearcherCutoffValuesAnnotationLevel> entry : psmCutoffValuesPerAnnotationId.entrySet() ) {
				
				psmCutoffValuesPerAnnotationIdList.add( entry.getValue() );
			}
			
			// sort by name alphabetical
			
			Collections.sort( psmCutoffValuesPerAnnotationIdList, new Comparator<SearcherCutoffValuesAnnotationLevel>() {

				@Override
				public int compare(SearcherCutoffValuesAnnotationLevel o1,
						SearcherCutoffValuesAnnotationLevel o2) {

					return o1.getAnnotationTypeDTO().getName().compareTo( o1.getAnnotationTypeDTO().getName() );
				}
			});
		}
		
		return psmCutoffValuesPerAnnotationIdList;
	}
	
	///////////////////
	
	//  Peptide

	/**
	 * Get SearcherCutoffValuesAnnotationLevel object for annotation id
	 * @param annotationId
	 * @return
	 */
	public SearcherCutoffValuesAnnotationLevel getPeptidePerAnnotationCutoffs( Integer annotationId ) {
		return peptideCutoffValuesPerAnnotationId.get( annotationId );
	}

	/**
	 * @param perAnnotationCutoffs
	 */
	public void addPeptidePerAnnotationCutoffs( SearcherCutoffValuesAnnotationLevel perAnnotationCutoffs ) {
		
		this.peptideCutoffValuesPerAnnotationId.put( perAnnotationCutoffs.getAnnotationTypeId(), perAnnotationCutoffs);
		
		peptideCutoffValuesPerAnnotationIdList = null;  // ensure cached list is null
	}

	/**
	 * Get searches in list form
	 * @return
	 */
	public List<SearcherCutoffValuesAnnotationLevel> getPeptidePerAnnotationCutoffsList() {
		
		if ( peptideCutoffValuesPerAnnotationIdList == null ) {
			
			peptideCutoffValuesPerAnnotationIdList = new ArrayList<>( peptideCutoffValuesPerAnnotationId.size() );
			
			for ( Map.Entry<Integer,SearcherCutoffValuesAnnotationLevel> entry : peptideCutoffValuesPerAnnotationId.entrySet() ) {
				
				peptideCutoffValuesPerAnnotationIdList.add( entry.getValue() );
			}

			// sort by name alphabetical
			
			Collections.sort( peptideCutoffValuesPerAnnotationIdList, new Comparator<SearcherCutoffValuesAnnotationLevel>() {

				@Override
				public int compare(SearcherCutoffValuesAnnotationLevel o1,
						SearcherCutoffValuesAnnotationLevel o2) {

					return o1.getAnnotationTypeDTO().getName().compareTo( o1.getAnnotationTypeDTO().getName() );
				}
			});
		}
		
		return peptideCutoffValuesPerAnnotationIdList;
	}
}
