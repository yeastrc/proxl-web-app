package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PercolatorSearchReportedPeptideDTO;



public class PercolatorSearchReportedPeptideDAO {
	
	private static final Logger log = Logger.getLogger(PercolatorSearchReportedPeptideDAO.class);

	private PercolatorSearchReportedPeptideDAO() { }
	public static PercolatorSearchReportedPeptideDAO getInstance() { return new PercolatorSearchReportedPeptideDAO(); }
	
	
//	CREATE TABLE percolator_search_reported_peptide (
//			search_id INT UNSIGNED NOT NULL,
//			reported_peptide_id INT UNSIGNED NOT NULL,
//			svm_score DOUBLE NOT NULL,
//			q_value DOUBLE NOT NULL,
//			pep DOUBLE NOT NULL,
//			calc_mass DOUBLE NOT NULL,
//			p_value DOUBLE NOT NULL
			
	/**
	 * Search on primary key so return the object or null
	 * @param searchId
	 * @param reportedPeptideId
	 * @return
	 * @throws Exception
	 */
	public PercolatorSearchReportedPeptideDTO getPercolatorSearchReportedPeptideDTOListBySearchIdAndReportedPeptideId( String searchId, String reportedPeptideId ) throws Exception {
		
		
		PercolatorSearchReportedPeptideDTO result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		final String sql = "SELECT * FROM percolator_search_reported_peptide WHERE search_id = ? AND reported_peptide_id = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setString( counter, searchId );
			counter++;
			pstmt.setString( counter, reportedPeptideId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				result = populateFromResultSet(rs);
				
			}
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
			
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
		
		
		return result;
	}
	

	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private PercolatorSearchReportedPeptideDTO populateFromResultSet(ResultSet rs) throws SQLException {
		
		PercolatorSearchReportedPeptideDTO result = new PercolatorSearchReportedPeptideDTO();
		
		result.setSearchId( rs.getInt( "search_id" ) );
		result.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );
		result.setSvmScore( rs.getDouble( "svm_score" ) );
		result.setqValue( rs.getDouble( "q_value" ) );
		result.setPep( rs.getDouble( "pep" ) );
		result.setCalcMass( rs.getDouble( "calc_mass" ) );
		result.setpValue( rs.getDouble( "p_value" ) );

		return result;
	}
	
	
	
	/**
	 *
	CREATE TABLE percolator_search_reported_peptide (
		search_id INT UNSIGNED NOT NULL,
		reported_peptide_id INT UNSIGNED NOT NULL,
		svm_score DOUBLE NOT NULL,
		q_value DOUBLE NOT NULL,
		pep DOUBLE NOT NULL,
		calc_mass DOUBLE NOT NULL,
		p_value DOUBLE NOT NULL
	);

	 * @param psm
	 * @throws Exception
	 */
	public void saveToDatabase( PercolatorSearchReportedPeptideDTO item ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		final String sql = "INSERT INTO percolator_search_reported_peptide (search_id, reported_peptide_id, svm_score, q_value, pep, calc_mass, p_value) VALUES (?, ?, ?, ?, ?, ?, ?)";
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
		
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, item.getSearchId() );
			pstmt.setInt( 2, item.getReportedPeptideId() );
			pstmt.setDouble( 3, item.getSvmScore() );
			pstmt.setDouble( 4, item.getqValue() );
			pstmt.setDouble( 5, item.getPep() );
			pstmt.setDouble( 6, item.getCalcMass() );
			pstmt.setDouble( 7, item.getpValue() );
			
			pstmt.executeUpdate();
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "'"
					+ "\n PercolatorSearchReportedPeptideDTO item:  " + item
				+ "\n sql: " + sql, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
	}
	
}
