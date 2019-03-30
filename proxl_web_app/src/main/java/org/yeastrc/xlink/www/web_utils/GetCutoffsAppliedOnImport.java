package org.yeastrc.xlink.www.web_utils;

import java.util.List;

//import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.objects.CutoffsAppliedOnImportWebDisplay;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_CutoffsAppliedOnImportWebDisplay;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.Cached_CutoffsAppliedOnImportWebDisplay_Result;

/**
 * 
 *
 */
public class GetCutoffsAppliedOnImport {

//	private static final Logger log = LoggerFactory.getLogger(  GetCutoffsAppliedOnImport.class );
	private static final GetCutoffsAppliedOnImport instance = new GetCutoffsAppliedOnImport();
	private GetCutoffsAppliedOnImport() { }
	public static GetCutoffsAppliedOnImport getInstance() { return instance; }
	

	/**
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public List<CutoffsAppliedOnImportWebDisplay> getCutoffsAppliedOnImportList( int searchId ) throws Exception {
		
		Cached_CutoffsAppliedOnImportWebDisplay_Result cached_CutoffsAppliedOnImportWebDisplay_Result =
				Cached_CutoffsAppliedOnImportWebDisplay.getInstance()
				.getCached_CutoffsAppliedOnImportWebDisplay_Result( searchId );
		List<CutoffsAppliedOnImportWebDisplay> cutoffsAppliedOnImportList = 
				cached_CutoffsAppliedOnImportWebDisplay_Result.getCutoffsAppliedOnImportList();

		return cutoffsAppliedOnImportList;
	}

	/**
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public String getCutoffsAppliedOnImportAllAsString( int searchId ) throws Exception {
		
		List<CutoffsAppliedOnImportWebDisplay> cutoffsAppliedOnImportList =
				getCutoffsAppliedOnImportList( searchId );
		return getCutoffsAppliedOnImportAllAsString( cutoffsAppliedOnImportList );
	}
	
	/**
	 * @param cutoffsAppliedOnImportList
	 * @return
	 * @throws Exception
	 */
	public String getCutoffsAppliedOnImportAllAsString( List<CutoffsAppliedOnImportWebDisplay> cutoffsAppliedOnImportList ) throws Exception {

		if ( cutoffsAppliedOnImportList == null || ( cutoffsAppliedOnImportList.isEmpty() ) ) {
			return "";
		}
		StringBuilder peptideCutoffsOnImport = new StringBuilder();
		StringBuilder psmCutoffsOnImport = new StringBuilder();
		boolean firstPeptideCutoffsOnImport = true;
		boolean firstPsmCutoffsOnImport = true;
		for ( CutoffsAppliedOnImportWebDisplay cutoffsAppliedOnImport : cutoffsAppliedOnImportList ) {
			if ( cutoffsAppliedOnImport.isPeptideCutoff() ) {
				if ( peptideCutoffsOnImport.length() == 0 ) {
					peptideCutoffsOnImport.append( "Peptide Cutoffs: " );
				}
				if ( firstPeptideCutoffsOnImport ) {
					firstPeptideCutoffsOnImport = false;
				} else {
					peptideCutoffsOnImport.append( ", " );
				}
				peptideCutoffsOnImport.append( cutoffsAppliedOnImport.getAnnotationName() );
				peptideCutoffsOnImport.append( ": " );
				peptideCutoffsOnImport.append( cutoffsAppliedOnImport.getCutoffValue() );
			} else {
				if ( psmCutoffsOnImport.length() == 0 ) {
					psmCutoffsOnImport.append( "PSM Cutoffs: " );
				}
				if ( firstPsmCutoffsOnImport ) {
					firstPsmCutoffsOnImport = false;
				} else {
					psmCutoffsOnImport.append( ", " );
				}
				psmCutoffsOnImport.append( cutoffsAppliedOnImport.getAnnotationName() );
				psmCutoffsOnImport.append( ": " );
				psmCutoffsOnImport.append( cutoffsAppliedOnImport.getCutoffValue() );
			}
		}
		return peptideCutoffsOnImport + "; " + psmCutoffsOnImport;
	}
	
}
