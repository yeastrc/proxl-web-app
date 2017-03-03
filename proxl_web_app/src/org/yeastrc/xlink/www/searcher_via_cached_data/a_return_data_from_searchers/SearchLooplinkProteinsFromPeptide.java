package org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.factories.ProteinSequenceObjectFactory;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinDoublePosition;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_LooplinkProteinPositionsFor_LooplinkPeptide;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.LooplinkProteinPositionsFor_LooplinkPeptide_Request;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.LooplinkProteinPositionsFor_LooplinkPeptide_Result;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.LooplinkProteinPositionsFor_LooplinkPeptide_Result_Entry;

/**
 * From SearchProteinSearcher
 *
 */
public class SearchLooplinkProteinsFromPeptide {

	private static final Logger log = Logger.getLogger(SearchLooplinkProteinsFromPeptide.class);
	
	private SearchLooplinkProteinsFromPeptide() { }
	private static final SearchLooplinkProteinsFromPeptide _INSTANCE = new SearchLooplinkProteinsFromPeptide();
	public static SearchLooplinkProteinsFromPeptide getInstance() { return _INSTANCE; }


	// method name was getProteinDoublePositions

	public List<SearchProteinDoublePosition> getLooplinkProteinPositions( 
			SearchDTO search, 
			int reportedPeptideId, 
			int peptideId, 
			int position1,
			int position2 ) throws Exception {
		
		List<SearchProteinDoublePosition> proteinPositions = new ArrayList<SearchProteinDoublePosition>();
		
		LooplinkProteinPositionsFor_LooplinkPeptide_Request looplinkProteinPositionsFor_LooplinkPeptide_Request =
				new LooplinkProteinPositionsFor_LooplinkPeptide_Request();
		looplinkProteinPositionsFor_LooplinkPeptide_Request.setSearchId( search.getSearchId() );
		looplinkProteinPositionsFor_LooplinkPeptide_Request.setReportedPeptideId( reportedPeptideId );
		looplinkProteinPositionsFor_LooplinkPeptide_Request.setPeptideId( peptideId );
		looplinkProteinPositionsFor_LooplinkPeptide_Request.setPosition_1(position1);
		looplinkProteinPositionsFor_LooplinkPeptide_Request.setPosition_2(position2);
		LooplinkProteinPositionsFor_LooplinkPeptide_Result looplinkProteinPositionsFor_LooplinkPeptide_Result = 
				Cached_LooplinkProteinPositionsFor_LooplinkPeptide.getInstance()
				.getLooplinkProteinPositionsFor_LooplinkPeptide_Result( looplinkProteinPositionsFor_LooplinkPeptide_Request );
		List<LooplinkProteinPositionsFor_LooplinkPeptide_Result_Entry> entryList = looplinkProteinPositionsFor_LooplinkPeptide_Result.getEntryList();

		for ( LooplinkProteinPositionsFor_LooplinkPeptide_Result_Entry entry : entryList ) {
			SearchProteinDoublePosition prpp = new SearchProteinDoublePosition();
			prpp.setPosition1( entry.getProteinSequencePosition_1() );
			prpp.setPosition2( entry.getProteinSequencePosition_2() );
			prpp.setProtein( new SearchProtein( search, ProteinSequenceObjectFactory.getProteinSequenceObject( entry.getProteinSequenceId() ) ) );

			proteinPositions.add( prpp );
		}

		//  Sort on protein sequence id, protein_position_1, protein_position_2
		
		Collections.sort( proteinPositions, new Comparator<SearchProteinDoublePosition>() {

			@Override
			public int compare(SearchProteinDoublePosition o1, SearchProteinDoublePosition o2) {
				
				if ( o1.getProtein().getProteinSequenceObject().getProteinSequenceId() != o2.getProtein().getProteinSequenceObject().getProteinSequenceId() ) {
					
					return o1.getProtein().getProteinSequenceObject().getProteinSequenceId() - o2.getProtein().getProteinSequenceObject().getProteinSequenceId();
				}
				if ( o1.getPosition1() != o2.getPosition1() ) {
					return o1.getPosition1() - o2.getPosition1();
				}
				return o1.getPosition2() - o2.getPosition2();
			}
		});
		
		return proteinPositions;
	}
	
}
