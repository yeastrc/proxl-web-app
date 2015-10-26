package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinMonolink;

public class SearchProteinMonolinkSearcher {

	private SearchProteinMonolinkSearcher() { }
	private static final SearchProteinMonolinkSearcher _INSTANCE = new SearchProteinMonolinkSearcher();
	public static SearchProteinMonolinkSearcher getInstance() { return _INSTANCE; }
	
	public List<SearchProteinMonolink> search( SearchDTO search, double psmCutoff, double peptideCutoff ) throws Exception {
		List<SearchProteinMonolink> links = new ArrayList<SearchProteinMonolink>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT nrseq_id, protein_position, bestPSMQValue, bestPeptideQValue " +
					"FROM search_monolink_lookup WHERE search_id = ? AND bestPSMQValue <= ? AND  ( bestPeptideQValue <= ? OR bestPeptideQValue IS NULL ) "
					+ "ORDER BY nrseq_id, protein_position";			
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setDouble( 1, search.getId() );
			pstmt.setDouble( 2, psmCutoff );
			pstmt.setDouble( 3, peptideCutoff );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				SearchProteinMonolink link = new SearchProteinMonolink();
				link.setPsmCutoff( psmCutoff );
				link.setPeptideCutoff( peptideCutoff );
				link.setProtein( new SearchProtein( search, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );
				
				link.setProteinPosition( rs.getInt( 2 ) );
				link.setBestPSMQValue( rs.getDouble( 3 ) );
				
				link.setBestPeptideQValue( rs.getDouble( 4 ) );
				if ( rs.wasNull() ) {
					link.setBestPeptideQValue( null );
				}
				
				link.setSearch( search );
				
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
	
	public SearchProteinMonolink search( SearchDTO search, double psmCutoff, double peptideCutoff, NRProteinDTO protein, int position ) throws Exception {
		SearchProteinMonolink link = null;
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT bestPSMQValue, bestPeptideQValue " +
					"FROM search_monolink_lookup WHERE search_id = ? AND bestPSMQValue <= ? AND  ( bestPeptideQValue <= ? OR bestPeptideQValue IS NULL )  AND "
					+ "nrseq_id = ? AND protein_position = ?";	
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setDouble( 1, search.getId() );
			pstmt.setDouble( 2, psmCutoff );
			pstmt.setDouble( 3, peptideCutoff );
			pstmt.setInt( 4, protein.getNrseqId() );
			pstmt.setInt( 5, position );
			
			rs = pstmt.executeQuery();

			if( rs.next() ) {
				link = new SearchProteinMonolink();

				link.setPsmCutoff( psmCutoff );
				link.setPeptideCutoff( peptideCutoff );
				link.setProtein( new SearchProtein( search, protein ) );
				
				link.setProteinPosition( position );
				link.setBestPSMQValue( rs.getDouble( 1 ) );
				
				link.setBestPeptideQValue( rs.getDouble( 2 ) );
				if ( rs.wasNull() ) {
					link.setBestPeptideQValue( null );
				}
				
				link.setSearch( search );
				
				if( rs.next() )
					throw new Exception( "Should only have gotten one row..." );
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
		
		return link;
	}
	
}
