package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.SrchRepPeptNrseqIdPosLooplinkDTO;

public class SearchReportedPeptideNrseqPositionLooplinkSearcher {

	private static final Logger log = Logger.getLogger( SearchReportedPeptideNrseqPositionLooplinkSearcher.class );
	
	private SearchReportedPeptideNrseqPositionLooplinkSearcher() { }
	private static final SearchReportedPeptideNrseqPositionLooplinkSearcher _INSTANCE = new SearchReportedPeptideNrseqPositionLooplinkSearcher();
	public static SearchReportedPeptideNrseqPositionLooplinkSearcher getInstance() { return _INSTANCE; }
	
	
	private static final String getSrchRepPeptNrseqIdPosDTOList_SQL = 
			"SELECT * FROM srch_rep_pept__nrseq_id_pos_looplink WHERE search_id = ? AND reported_peptide_id = ?";

	public List<SrchRepPeptNrseqIdPosLooplinkDTO> getSrchRepPeptNrseqIdPosLooplinkDTOList( int searchId, int reportedPeptideId ) throws Exception {
		
		List<SrchRepPeptNrseqIdPosLooplinkDTO> results = new ArrayList<>();

		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = getSrchRepPeptNrseqIdPosDTOList_SQL;

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );

			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );

			paramCounter++;
			pstmt.setInt( paramCounter, reportedPeptideId );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
		
				SrchRepPeptNrseqIdPosLooplinkDTO item = new SrchRepPeptNrseqIdPosLooplinkDTO();
		
				item.setId( rs.getInt( "id" ) );
				item.setSearchId( rs.getInt( "search_id" ) );
				item.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );
				item.setSearchReportedPeptidepeptideId( rs.getInt( "search_reported_peptide_peptide_id" ) );
				item.setNrseqId( rs.getInt( "nrseq_id" ) );
				
				item.setNrseqPosition_1( rs.getInt( "nrseq_position_1" ) );
				item.setNrseqPosition_2( rs.getInt( "nrseq_position_2" ) );

				results.add( item );
			}

		} catch ( Exception e ) {

			String msg = "Exception in getSrchRepPeptNrseqIdPosLooplinkDTOList( SearchDTO search, ... ), sql: " + sql;
			
			log.error( msg, e );
			
			throw e;
			
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
		
		return results;
	}
}
