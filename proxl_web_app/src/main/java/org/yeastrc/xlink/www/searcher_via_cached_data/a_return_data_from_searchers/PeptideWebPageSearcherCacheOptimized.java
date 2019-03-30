package org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslink;
import org.yeastrc.xlink.www.objects.SearchPeptideDimer;
import org.yeastrc.xlink.www.objects.SearchPeptideLooplink;
import org.yeastrc.xlink.www.objects.SearchPeptideUnlink;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_ReportedPeptideBasicObjectsSearcher_Results;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.ReportedPeptideBasicObjectsSearcherRequestParameters;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.ReportedPeptideBasicObjectsSearcherResult;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.ReportedPeptideBasicObjectsSearcherResultEntry;

/**
 * 
 *
 */
public class PeptideWebPageSearcherCacheOptimized {
	
	private static final Logger log = LoggerFactory.getLogger( PeptideWebPageSearcherCacheOptimized.class);
	private PeptideWebPageSearcherCacheOptimized() { }
	private static final PeptideWebPageSearcherCacheOptimized _INSTANCE = new PeptideWebPageSearcherCacheOptimized();
	public static PeptideWebPageSearcherCacheOptimized getInstance() { return _INSTANCE; }

	public static enum ReturnOnlyReportedPeptidesWithMonolinks {
		YES, NO
	}
	
//	public static class PretendExcForStacktrace extends Exception {
//		
//	}

	/**
	 * Get the peptides corresponding to the given parameters
	 * @param search The search we're searching
	 * @param searcherCutoffValuesSearchLevel - PSM and Peptide cutoffs for a search id
	 * @param linkTypes Which link types to include in the results
	 * @param modMassSelections Which modified masses to include.  Null if include all. element "" means no modifications
	 * @param returnOnlyReportedPeptidesWithMonolinks - Only return Reported Peptides with Monolinks
	 * @return
	 * @throws Exception
	 */
	public List<WebReportedPeptideWrapper> searchOnSearchIdPsmCutoffPeptideCutoff( 
			SearchDTO search, 
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel, 
			String[] linkTypes, 
			String[] modMassSelections, 
			ReturnOnlyReportedPeptidesWithMonolinks returnOnlyReportedPeptidesWithMonolinks ) throws Exception {
		
		int searchId = search.getSearchId();

		if ( log.isDebugEnabled() ) {
			StringBuilder stackTraceSB = new StringBuilder( 3000 );
			stackTraceSB.append( "Partial Stack Trace: \n" );
			StackTraceElement[] elements = Thread.currentThread().getStackTrace();
			int elementCount = elements.length;
			if ( elementCount > 5 ) {
				elementCount = 5;
			}
			for (int i = 1; i < elementCount; i++) {
				StackTraceElement s = elements[i];
				stackTraceSB.append("\tat " + s.getClassName() + "." + s.getMethodName()
				+ "(" + s.getFileName() + ":" + s.getLineNumber() + ")\n");
			}
			String stackTrace = stackTraceSB.toString();
			log.debug( "searchOnSearchIdPsmCutoffPeptideCutoff(...) called, searchId: " + searchId
					+ "\n" + stackTrace );
//			PretendExcForStacktrace e = new PretendExcForStacktrace();
//			log.debug( "searchOnSearchIdPsmCutoffPeptideCutoff(...) called, searchId: " + searchId, e );
		}

		//  Translate returnOnlyReportedPeptidesWithMonolinks to cache version
		org.yeastrc.xlink.www.searcher.ReportedPeptideBasicObjectsSearcher.ReturnOnlyReportedPeptidesWithMonolinks
		returnOnlyReportedPeptidesWithMonolinks_ForCache = null;
		if ( returnOnlyReportedPeptidesWithMonolinks == ReturnOnlyReportedPeptidesWithMonolinks.YES ) {
			returnOnlyReportedPeptidesWithMonolinks_ForCache =
					org.yeastrc.xlink.www.searcher.ReportedPeptideBasicObjectsSearcher.ReturnOnlyReportedPeptidesWithMonolinks.YES;
		} else if ( returnOnlyReportedPeptidesWithMonolinks == ReturnOnlyReportedPeptidesWithMonolinks.NO ) {
			returnOnlyReportedPeptidesWithMonolinks_ForCache =
					org.yeastrc.xlink.www.searcher.ReportedPeptideBasicObjectsSearcher.ReturnOnlyReportedPeptidesWithMonolinks.NO;
		} else {
			String msg = "Unexpected value for returnOnlyReportedPeptidesWithMonolinks: " + returnOnlyReportedPeptidesWithMonolinks.toString();
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}
		
		ReportedPeptideBasicObjectsSearcherRequestParameters reportedPeptideBasicObjectsSearcherRequestParameters =
				new ReportedPeptideBasicObjectsSearcherRequestParameters();
		
		reportedPeptideBasicObjectsSearcherRequestParameters.setSearchId( searchId );
		reportedPeptideBasicObjectsSearcherRequestParameters.setSearcherCutoffValuesSearchLevel( searcherCutoffValuesSearchLevel );
		reportedPeptideBasicObjectsSearcherRequestParameters.setLinkTypes( linkTypes );
		reportedPeptideBasicObjectsSearcherRequestParameters.setModMassSelections( modMassSelections );
		reportedPeptideBasicObjectsSearcherRequestParameters.setReturnOnlyReportedPeptidesWithMonolinks( returnOnlyReportedPeptidesWithMonolinks_ForCache );

		ReportedPeptideBasicObjectsSearcherResult reportedPeptideBasicObjectsSearcherResult =
				Cached_ReportedPeptideBasicObjectsSearcher_Results.getInstance()
				.getReportedPeptideBasicObjectsSearcherResult( reportedPeptideBasicObjectsSearcherRequestParameters );
		
		List<ReportedPeptideBasicObjectsSearcherResultEntry> entryList =
				reportedPeptideBasicObjectsSearcherResult.getEntryList();

		List<WebReportedPeptideWrapper> wrappedLinks = new ArrayList<>( entryList.size() );

		for ( ReportedPeptideBasicObjectsSearcherResultEntry reportedPeptideData : entryList ) {
			WebReportedPeptide item = new WebReportedPeptide();
			WebReportedPeptideWrapper wrappedItem = new WebReportedPeptideWrapper();
			wrappedItem.setWebReportedPeptide( item );
			
			item.setSearch( search );
			item.setSearchId( search.getSearchId() );
			item.setSearcherCutoffValuesSearchLevel( searcherCutoffValuesSearchLevel );
			
			int reportedPeptideId = reportedPeptideData.getReportedPeptideId();
			item.setReportedPeptideId( reportedPeptideId );
			item.setUnifiedReportedPeptideId( reportedPeptideData.getUnifiedReportedPeptideId() );
			
			if ( reportedPeptideData.getNumPsms() != null ) {
				item.setNumPsms( reportedPeptideData.getNumPsms() );
			}
			if ( reportedPeptideData.getNumUniquePsms() != null ) {
				item.setNumUniquePsms( reportedPeptideData.getNumUniquePsms() );
			}
			if ( reportedPeptideData.getPeptideAnnotationDTOMap() != null ) {
				wrappedItem.setPeptideAnnotationDTOMap( reportedPeptideData.getPeptideAnnotationDTOMap() );
			}
			if ( reportedPeptideData.getPsmAnnotationDTOMap() != null ) {
				wrappedItem.setPsmAnnotationDTOMap( reportedPeptideData.getPsmAnnotationDTOMap() );
			}
			if ( XLinkUtils.TYPE_CROSSLINK == reportedPeptideData.getLinkType() ) {
				SearchPeptideCrosslink link = new SearchPeptideCrosslink();
				link.setSearch( search );
				link.setReportedPeptideId( reportedPeptideId );
				item.setSearchPeptideCrosslink(link);
			} else if ( XLinkUtils.TYPE_LOOPLINK == reportedPeptideData.getLinkType() ) {
				SearchPeptideLooplink link = new SearchPeptideLooplink();
				link.setSearch( search );
				link.setReportedPeptideId ( reportedPeptideId );
				item.setSearchPeptideLooplink(link);
			} else if ( XLinkUtils.TYPE_UNLINKED == reportedPeptideData.getLinkType() ) {
				SearchPeptideUnlink link = new SearchPeptideUnlink();
				link.setSearch( search );
				link.setReportedPeptideId ( reportedPeptideId );
				item.setSearchPeptideUnlinked(link);
			} else if ( XLinkUtils.TYPE_DIMER == reportedPeptideData.getLinkType() ) {
				SearchPeptideDimer link = new SearchPeptideDimer();
				link.setSearch( search );
				link.setReportedPeptideId ( reportedPeptideId );
				item.setSearchPeptideDimer(link);
			} else {
				String msg = "Unknown link type, linkType: " 
						+  reportedPeptideData.getLinkType()  
						+ ", SEARCH: " + searchId
						+ ", SEARCH: " + searchId
						+ ", SEARCH: " + searchId
						;
				log.error( msg );
				continue;  //  EARLY SKIP TO NEXT RECORD:    skip over other types for now
			}
			
			wrappedLinks.add( wrappedItem );
		}
		
		
		return wrappedLinks;
	}
	
}
