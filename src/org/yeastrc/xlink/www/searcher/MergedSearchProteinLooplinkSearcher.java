package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.MergedSearchProteinLooplink;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;

public class MergedSearchProteinLooplinkSearcher {

	private MergedSearchProteinLooplinkSearcher() { }
	private static final MergedSearchProteinLooplinkSearcher _INSTANCE = new MergedSearchProteinLooplinkSearcher();
	public static MergedSearchProteinLooplinkSearcher getInstance() { return _INSTANCE; }
	
	public List<MergedSearchProteinLooplink> search( Collection<SearchDTO> searches, double psmCutoff, double peptideCutoff ) throws Exception {
		List<MergedSearchProteinLooplink> links = new ArrayList<MergedSearchProteinLooplink>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT nrseq_id, protein_position_1, protein_position_2, min(bestPSMQValue), min(bestPeptideQValue) "
					+ "FROM search_looplink_lookup WHERE search_id IN (#SEARCHES#) AND bestPSMQValue <= ? AND  ( bestPeptideQValue <= ? OR bestPeptideQValue IS NULL )  "
					+ "GROUP BY nrseq_id, protein_position_1, protein_position_2 "
					+ "ORDER BY nrseq_id, protein_position_1, protein_position_2";
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : searches )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
						
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setDouble( 1, psmCutoff );
			pstmt.setDouble( 2, peptideCutoff );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				MergedSearchProteinLooplink link = new MergedSearchProteinLooplink();
				link.setPsmCutoff( psmCutoff );
				link.setPeptideCutoff( peptideCutoff );
				link.setProtein( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );
				
				link.setProteinPosition1( rs.getInt( 2 ) );
				link.setProteinPosition2( rs.getInt( 3 ) );
				link.setBestPSMQValue( rs.getDouble( 4 ) );
				
				link.setBestPeptideQValue( rs.getDouble( 5 ) );
				if ( rs.wasNull() ) {
					link.setBestPeptideQValue( null );
				}
				
				// add search-level info for the protein looplinks:
				Map<SearchDTO, SearchProteinLooplink> searchLooplinks = new TreeMap<SearchDTO, SearchProteinLooplink>();
				for( SearchDTO search : searches ) {
					SearchProteinLooplink tlink = SearchProteinLooplinkSearcher.getInstance().search(search, 
																										 psmCutoff, 
																										 peptideCutoff, 
																										 link.getProtein().getNrProtein(),
																										 link.getProteinPosition1(),
																										 link.getProteinPosition2()
																										);

					if( tlink != null )
						searchLooplinks.put( search, tlink );
				}
				
				link.setSearchProteinLooplinks( searchLooplinks );
				
				
				links.add( link );
			}
			
		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		return links;
	}
	
}
