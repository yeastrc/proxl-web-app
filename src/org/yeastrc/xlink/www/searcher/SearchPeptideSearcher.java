package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.utils.XLinkUtils;

public class SearchPeptideSearcher {

	
	public int getNumDistinctPeptidesForSearch( int type ) throws Exception {
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			String sql = "SELECT COUNT(DISTINCT a.reported_peptide_id) FROM psm AS a " +
					"INNER JOIN search_reported_peptide AS b ON a.reported_peptide_id = b.reported_peptide_id " +
					"WHERE a.search_id = ? AND a.q_value <= ? AND a.type = ? AND ( b.q_value <= ? OR b.q_value IS NULL )  ";
			
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, this.getSearch().getId() );
			pstmt.setDouble( 2, this.getPsmQValueCutoff() );
			pstmt.setString( 3, XLinkUtils.getTypeString( type ) );
			pstmt.setDouble( 4, this.getPeptideQValueCutoff() );

			rs = pstmt.executeQuery();

			if( rs.next() )
				count = rs.getInt( 1 );
			
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
		
		return count;		
	}
	
	
	
	public double getPsmQValueCutoff() {
		return psmQValueCutoff;
	}
	public void setPsmQValueCutoff(double psmQValueCutoff) {
		this.psmQValueCutoff = psmQValueCutoff;
	}
	public double getPeptideQValueCutoff() {
		return peptideQValueCutoff;
	}
	public void setPeptideQValueCutoff(double peptideQValueCutoff) {
		this.peptideQValueCutoff = peptideQValueCutoff;
	}
	public SearchDTO getSearch() {
		return search;
	}
	public void setSearch(SearchDTO search) {
		this.search = search;
	}
	
	private double psmQValueCutoff;
	private double peptideQValueCutoff;
	private SearchDTO search;
	
}
