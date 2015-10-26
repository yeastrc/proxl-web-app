package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.UnifiedReportedPeptideLookupDTO;

public class UnifiedReportedPeptideLookupDAO {
	
	private static final Logger log = Logger.getLogger(UnifiedReportedPeptideLookupDAO.class);

	private UnifiedReportedPeptideLookupDAO() { }
	public static UnifiedReportedPeptideLookupDAO getInstance() { return new UnifiedReportedPeptideLookupDAO(); }
	
	
	
	public UnifiedReportedPeptideLookupDTO getUnifiedReportedPeptideFromDatabase( int id ) throws Exception {
		
		UnifiedReportedPeptideLookupDTO peptide = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM unified_reported_peptide_lookup WHERE id = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( !rs.next() )
				throw new Exception( "Could not find unified_reported_peptide_lookup for " + id );
			
			peptide = new UnifiedReportedPeptideLookupDTO();
			peptide.setUnifiedSequence( rs.getString( "unified_sequence" ) );
			peptide.setLinkTypeString( rs.getString( "link_type" ) );
			peptide.setHasMods( rs.getBoolean( "has_dynamic_modifictions" ) );
			peptide.setId( id );
			
			
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
		
		return peptide;
	}
	
	


	/**
	 * Get the id for the supplied peptide unified_sequence (as it appears in percolator
	 * output) from the database. Returns null if not found.
	 * @param unified_sequence
	 * @return null if no record found
	 * @throws Exception
	 */
	public Integer getReportedPeptideIdForSequence( String unified_sequence ) throws Exception {

		Integer id = null;

		Connection conn = null;
	
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			id = getReportedPeptideIdForSequence( unified_sequence, conn );

		} finally {
			
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		return id;
	}
	
	/**
	 * Get the id for the supplied peptide unified_sequence (as it appears in percolator
	 * output) from the database. Returns null if not found.
	 * @param unified_sequence
	 * @return null if no record found
	 * @throws Exception
	 */
	public Integer getReportedPeptideIdForSequence( String unified_sequence, Connection conn ) throws Exception {
		
		Integer id = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT id FROM unified_reported_peptide_lookup WHERE unified_sequence = ? ";

		try {
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, unified_sequence );
			
			rs = pstmt.executeQuery();

			if( rs.next() ) {
				id = rs.getInt( 1 );
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
			
			
		}
		
		return id;
	}
	
	

	public void saveToDatabase( UnifiedReportedPeptideLookupDTO unifiedReportedPeptide ) throws Exception {
		
		Connection conn = null;

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			saveToDatabase( unifiedReportedPeptide, conn );
			
		} catch ( Exception e ) {
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		
	}
	


	public void saveToDatabase( UnifiedReportedPeptideLookupDTO unifiedReportedPeptide, Connection conn ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "INSERT INTO unified_reported_peptide_lookup (unified_sequence, link_type, has_dynamic_modifictions ) VALUES (?,?,?)";

		try {
			
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, unifiedReportedPeptide.getUnifiedSequence() );
			pstmt.setString( 2, unifiedReportedPeptide.getLinkTypeString() );
			pstmt.setBoolean( 3, unifiedReportedPeptide.isHasMods() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				unifiedReportedPeptide.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert unified_reported_peptide_lookup for " + unifiedReportedPeptide.getUnifiedSequence() );
			
			
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
			
		}
		
		
	}
	
}
