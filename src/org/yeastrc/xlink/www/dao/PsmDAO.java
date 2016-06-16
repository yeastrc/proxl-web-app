package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PsmDTO;

/**
 * Table psm
 *
 */
public class PsmDAO {

	private static final Logger log = Logger.getLogger(PsmDAO.class);
			
	private PsmDAO() { }
	public static PsmDAO getInstance() { return new PsmDAO(); }
	
	public PsmDTO getPsmDTO( int id ) throws Exception {
		
		PsmDTO psm = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM psm WHERE id = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				psm = new PsmDTO();
				
				psm.setId( id );
				psm.setSearchId( rs.getInt( "search_id" ) );
				
				int scanId = rs.getInt( "scan_id" );
				if ( ! rs.wasNull() ) {
					
					psm.setScanId( scanId );
				}
				

				int charge = rs.getInt( "charge" );
				if ( ! rs.wasNull() ) {
					
					psm.setCharge( charge );
				}

				psm.setLinkerMass( rs.getBigDecimal( "linker_mass" ) );  //  Can be NULL
				
				
				psm.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );
				
			}
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
			
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
		
		
		return psm;
	}
	

	/**
	 * Query on the 2 foreign keys
	 * 
	 * @param reportedPeptideId
	 * @param searchId
	 * @return psm.id if found, null otherwise
	 * @throws Exception
	 */
	public Integer getOnePsmIdForSearchIdAndReportedPeptideId( int reportedPeptideId,  int searchId ) throws Exception {
		
		Integer psmId = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT id FROM psm WHERE reported_peptide_id = ? AND search_id = ? LIMIT 1";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, reportedPeptideId );
			pstmt.setInt( 2, searchId );
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				psmId = rs.getInt( "id" );
			}
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
			
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
		
		
		return psmId;
	}
	
	

	/**
	 * Query on the 2 foreign keys
	 * 
	 * @param reportedPeptideId
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public PsmDTO getOnePsmDTOForSearchIdAndReportedPeptideId( int reportedPeptideId,  int searchId ) throws Exception {
		
		PsmDTO psm = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM psm WHERE reported_peptide_id = ? AND search_id = ? LIMIT 1";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, reportedPeptideId );
			pstmt.setInt( 2, searchId );
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				psm = new PsmDTO();
				
				psm.setId( rs.getInt( "id" ) );
				psm.setSearchId( rs.getInt( "search_id" ) );
				
				int scanId = rs.getInt( "scan_id" );
				if ( ! rs.wasNull() ) {
					
					psm.setScanId( scanId );
				}
				

				int charge = rs.getInt( "charge" );
				if ( ! rs.wasNull() ) {
					
					psm.setCharge( charge );
				}
				
				
				psm.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );
				
			}
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
			
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
		
		
		return psm;
	}
	
	
	
}
