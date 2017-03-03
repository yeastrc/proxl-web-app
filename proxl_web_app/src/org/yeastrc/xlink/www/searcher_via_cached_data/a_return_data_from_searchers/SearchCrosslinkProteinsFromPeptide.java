package org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.factories.ProteinSequenceObjectFactory;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinPosition;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_CrosslinkProteinPositionsFor_CrosslinkPeptide;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.CrosslinkProteinPositionsFor_CrosslinkPeptide_Request;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.CrosslinkProteinPositionsFor_CrosslinkPeptide_Result;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.CrosslinkProteinPositionsFor_CrosslinkPeptide_Result_Entry;

/**
 * From SearchProteinSearcher
 *
 */
public class SearchCrosslinkProteinsFromPeptide {

	private static final Logger log = Logger.getLogger(SearchCrosslinkProteinsFromPeptide.class);
	
	private SearchCrosslinkProteinsFromPeptide() { }
	private static final SearchCrosslinkProteinsFromPeptide _INSTANCE = new SearchCrosslinkProteinsFromPeptide();
	public static SearchCrosslinkProteinsFromPeptide getInstance() { return _INSTANCE; }


	
	public List<SearchProteinPosition> getProteinPositions( SearchDTO search, int reportedPeptideId, int peptideId, int position ) throws Exception {
		
		List<SearchProteinPosition> proteinPositions = new ArrayList<SearchProteinPosition>();
		
		CrosslinkProteinPositionsFor_CrosslinkPeptide_Request crosslinkProteinPositionsFor_CrosslinkPeptide_Request =
				new CrosslinkProteinPositionsFor_CrosslinkPeptide_Request();
		crosslinkProteinPositionsFor_CrosslinkPeptide_Request.setSearchId( search.getSearchId() );
		crosslinkProteinPositionsFor_CrosslinkPeptide_Request.setReportedPeptideId( reportedPeptideId );
		crosslinkProteinPositionsFor_CrosslinkPeptide_Request.setPeptideId( peptideId );
		crosslinkProteinPositionsFor_CrosslinkPeptide_Request.setPosition( position );
		CrosslinkProteinPositionsFor_CrosslinkPeptide_Result crosslinkProteinPositionsFor_CrosslinkPeptide_Result = 
				Cached_CrosslinkProteinPositionsFor_CrosslinkPeptide.getInstance()
				.getCrosslinkProteinPositionsFor_CrosslinkPeptide_Result( crosslinkProteinPositionsFor_CrosslinkPeptide_Request );
		List<CrosslinkProteinPositionsFor_CrosslinkPeptide_Result_Entry> entryList = crosslinkProteinPositionsFor_CrosslinkPeptide_Result.getEntryList();

		for ( CrosslinkProteinPositionsFor_CrosslinkPeptide_Result_Entry entry : entryList ) {
			SearchProteinPosition prpp = new SearchProteinPosition();
			prpp.setPosition( entry.getProteinSequencePosition() );
			prpp.setProtein( new SearchProtein( search, ProteinSequenceObjectFactory.getProteinSequenceObject( entry.getProteinSequenceId() ) ) );

			proteinPositions.add( prpp );
		}

		//  Sort on protein sequence id, position

		Collections.sort( proteinPositions, new Comparator<SearchProteinPosition>() {

			@Override
			public int compare(SearchProteinPosition o1, SearchProteinPosition o2) {

				if ( o1.getProtein().getProteinSequenceObject().getProteinSequenceId() != o2.getProtein().getProteinSequenceObject().getProteinSequenceId() ) {

					return o1.getProtein().getProteinSequenceObject().getProteinSequenceId() - o2.getProtein().getProteinSequenceObject().getProteinSequenceId();
				}
				return o1.getPosition() - o2.getPosition();
			}
		});

		
		return proteinPositions;
	}
	
}
