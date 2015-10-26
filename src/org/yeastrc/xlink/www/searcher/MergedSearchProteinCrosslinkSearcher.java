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
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;

public class MergedSearchProteinCrosslinkSearcher {

	private MergedSearchProteinCrosslinkSearcher() { }
	private static final MergedSearchProteinCrosslinkSearcher _INSTANCE = new MergedSearchProteinCrosslinkSearcher();
	public static MergedSearchProteinCrosslinkSearcher getInstance() { return _INSTANCE; }
	
	public List<MergedSearchProteinCrosslink> search( Collection<SearchDTO> searches, double psmCutoff, double peptideCutoff ) throws Exception {
		List<MergedSearchProteinCrosslink> links = new ArrayList<MergedSearchProteinCrosslink>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT nrseq_id_1, nrseq_id_2, protein_1_position, protein_2_position, min(bestPSMQValue), min(bestPeptideQValue) "
					+ "FROM search_crosslink_lookup WHERE search_id IN (#SEARCHES#) AND bestPSMQValue <= ? AND ( bestPeptideQValue <= ? OR bestPeptideQValue IS NULL ) "
					+ "GROUP BY nrseq_id_1, protein_1_position, nrseq_id_2, protein_2_position "
					+ "ORDER BY nrseq_id_1, protein_1_position, nrseq_id_2, protein_2_position";
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : searches )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
						
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setDouble( 1, psmCutoff );
			pstmt.setDouble( 2, peptideCutoff );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				MergedSearchProteinCrosslink link = new MergedSearchProteinCrosslink();
				link.setPsmCutoff( psmCutoff );
				link.setPeptideCutoff( peptideCutoff );
				link.setProtein1( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );
				link.setProtein2( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 2 ) ) ) );
				
				link.setProtein1Position( rs.getInt( 3 ) );
				link.setProtein2Position( rs.getInt( 4 ) );
				link.setBestPSMQValue( rs.getDouble( 5 ) );

				link.setBestPeptideQValue( rs.getDouble( 6 ) );
				if ( rs.wasNull() ) {
					link.setBestPeptideQValue( null );
				}
				
				if( link.getProtein1() == null || link.getProtein2() == null )
					throw new Exception( "Got null for one of the proteins in the crosslink..." );
				
				// add search-level info for the protein crosslinks:
				Map<SearchDTO, SearchProteinCrosslink> searchCrosslinks = new TreeMap<SearchDTO, SearchProteinCrosslink>();
				for( SearchDTO search : searches ) {
					SearchProteinCrosslink tlink = SearchProteinCrosslinkSearcher.getInstance().search(search, 
																										 psmCutoff, 
																										 peptideCutoff, 
																										 link.getProtein1().getNrProtein(),
																										 link.getProtein2().getNrProtein(),
																										 link.getProtein1Position(),
																										 link.getProtein2Position()
																										);

					if( tlink != null )
						searchCrosslinks.put( search, tlink );
				}
				
				link.setSearchProteinCrosslinks( searchCrosslinks );
				
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
