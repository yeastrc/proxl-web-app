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
import org.yeastrc.xlink.www.objects.MergedSearchProteinMonolink;
import org.yeastrc.xlink.www.objects.SearchProteinMonolink;

public class MergedSearchProteinMonolinkSearcher {

	private MergedSearchProteinMonolinkSearcher() { }
	private static final MergedSearchProteinMonolinkSearcher _INSTANCE = new MergedSearchProteinMonolinkSearcher();
	public static MergedSearchProteinMonolinkSearcher getInstance() { return _INSTANCE; }
	
	public List<MergedSearchProteinMonolink> search( Collection<SearchDTO> searches, double psmCutoff, double peptideCutoff ) throws Exception {
		List<MergedSearchProteinMonolink> links = new ArrayList<MergedSearchProteinMonolink>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT nrseq_id, protein_position, min(bestPSMQValue), min(bestPeptideQValue) "
					+ "FROM search_monolink_lookup WHERE search_id IN (#SEARCHES#) AND bestPSMQValue <= ? AND  ( bestPeptideQValue <= ? OR bestPeptideQValue IS NULL )  "
					+ "GROUP BY nrseq_id, protein_position "
					+ "ORDER BY nrseq_id, protein_position";
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : searches )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
						
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setDouble( 1, psmCutoff );
			pstmt.setDouble( 2, peptideCutoff );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				MergedSearchProteinMonolink link = new MergedSearchProteinMonolink();
				link.setPsmCutoff( psmCutoff );
				link.setPeptideCutoff( peptideCutoff );
				link.setProtein( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) ) );
				
				link.setProteinPosition( rs.getInt( 2 ) );
				link.setBestPSMQValue( rs.getDouble( 3 ) );
				
				link.setBestPeptideQValue( rs.getDouble( 4 ) );
				if ( rs.wasNull() ) {
					link.setBestPeptideQValue( null );
				}
				
				// add search-level info for the protein monolinks:
				Map<SearchDTO, SearchProteinMonolink> searchMonolinks = new TreeMap<SearchDTO, SearchProteinMonolink>();
				for( SearchDTO search : searches ) {
					SearchProteinMonolink tlink = SearchProteinMonolinkSearcher.getInstance().search(search, 
																										 psmCutoff, 
																										 peptideCutoff, 
																										 link.getProtein().getNrProtein(),
																										 link.getProteinPosition()
																										);

					if( tlink != null )
						searchMonolinks.put( search, tlink );
				}
				
				link.setSearchProteinMonolinks( searchMonolinks );
				
				
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
