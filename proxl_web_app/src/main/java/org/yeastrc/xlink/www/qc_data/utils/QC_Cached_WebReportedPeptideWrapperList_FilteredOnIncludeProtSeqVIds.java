package org.yeastrc.xlink.www.qc_data.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized.ReturnOnlyReportedPeptidesWithMonolinks;

public class QC_Cached_WebReportedPeptideWrapperList_FilteredOnIncludeProtSeqVIds {

	private static final Logger log = LoggerFactory.getLogger( QC_Cached_WebReportedPeptideWrapperList_FilteredOnIncludeProtSeqVIds.class);

	/**
	 * private constructor
	 */
	private QC_Cached_WebReportedPeptideWrapperList_FilteredOnIncludeProtSeqVIds(){}
	public static QC_Cached_WebReportedPeptideWrapperList_FilteredOnIncludeProtSeqVIds getInstance( ) {
		QC_Cached_WebReportedPeptideWrapperList_FilteredOnIncludeProtSeqVIds instance = new QC_Cached_WebReportedPeptideWrapperList_FilteredOnIncludeProtSeqVIds();
		return instance;
	}
	

	/**
	 * Gets List<WebReportedPeptideWrapper> and then filters on includeProteinSeqVIdsDecodedArray if populated
	 * 
	 * Calls PeptideWebPageSearcherCacheOptimized.getInstance().searchOnSearchIdPsmCutoffPeptideCutoff(...)
	 * 
	 * @param search
	 * @param searcherCutoffValuesSearchLevel
	 * @param linkTypesForDBQuery
	 * @param modsForDBQuery
	 * @param returnOnlyReportedPeptidesWithMonolinks
	 * @param includeProteinSeqVIdsDecodedArray
	 * @return
	 * @throws Exception 
	 */
	public List<WebReportedPeptideWrapper> get_WebReportedPeptideWrapperList_FilteredOnIncludeProtSeqVIds( 
			SearchDTO search, 
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel, 
			String[] linkTypesForDBQuery, 
			String[] modsForDBQuery, 
			ReturnOnlyReportedPeptidesWithMonolinks returnOnlyReportedPeptidesWithMonolinks,
			List<Integer> includeProteinSeqVIdsDecodedArray ) throws Exception {

		///////////////////////////////////////////////
		//  Get peptides for this search from the DATABASE
		List<WebReportedPeptideWrapper> wrappedLinksPerForSearch =
				PeptideWebPageSearcherCacheOptimized.getInstance().searchOnSearchIdPsmCutoffPeptideCutoff(
						search, searcherCutoffValuesSearchLevel, 
						linkTypesForDBQuery,
						modsForDBQuery, 
						PeptideWebPageSearcherCacheOptimized.ReturnOnlyReportedPeptidesWithMonolinks.NO );

		List<WebReportedPeptideWrapper> wrappedLinksOutput = 	
				QC_FilterWebReportedPeptideWrapperList_OnIncludeProtSeqVIds.getInstance()
				.filter_WebReportedPeptideWrapper_OnIncludeProtSeqVIds(wrappedLinksPerForSearch, includeProteinSeqVIdsDecodedArray );
		
		return wrappedLinksOutput;
	}
}
