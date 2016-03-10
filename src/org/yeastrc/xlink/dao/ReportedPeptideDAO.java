package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;

public class ReportedPeptideDAO {
	
	private static final Logger log = Logger.getLogger(ReportedPeptideDAO.class);

	private ReportedPeptideDAO() { }
	public static ReportedPeptideDAO getInstance() { return new ReportedPeptideDAO(); }
	
	public ReportedPeptideDTO getReportedPeptideFromDatabase( int id ) throws Exception {
		
		ReportedPeptideDTO peptide = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT sequence FROM reported_peptide WHERE id = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( !rs.next() )
				throw new Exception( "Could not find reported_peptide for " + id );
			
			peptide = new ReportedPeptideDTO();
			peptide.setSequence( rs.getString( 1 ) );
			peptide.setId( id );
			
			
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
		
		return peptide;
	}
	
	
	/**
	 * Get the peptide DTO corresponding to supplied sequence. If no matching
	 * peptide is found in the database, it is inserted and a populated DTO
	 * returned. If already in the database, DTO is populated from database
	 * and returned.
	 * 
	 * @param sequence
	 * @return
	 * @throws Exception
	 */
	public ReportedPeptideDTO getReportedPeptideDTO( String sequence ) throws Exception {
		
		ReportedPeptideDTO plpeptide = new ReportedPeptideDTO();
		plpeptide.setSequence( sequence );
		
		plpeptide.setId( getReportedPeptideIdForSequence( sequence ) );
		
		if( plpeptide.getId() == 0 )
			saveToDatabase( plpeptide );
		
		return plpeptide;
	}
	
	/**
	 * Get the id for the supplied peptide sequence (as it appears in percolator
	 * output) from the database. Returns 0 if not found.
	 * @param sequence
	 * @return
	 * @throws Exception
	 */
	public int getReportedPeptideIdForSequence( String sequence ) throws Exception {
		int id = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT id FROM reported_peptide WHERE sequence = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, sequence );
			
			rs = pstmt.executeQuery();

			if( rs.next() ) {
				id = rs.getInt( 1 );
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
		
		return id;
	}
	
	
	/*
	CREATE TABLE peptide (
	    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	    sequence VARCHAR(2000) NOT NULL
	);
	 */
	public void saveToDatabase( ReportedPeptideDTO peptide ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "INSERT INTO reported_peptide (sequence ) VALUES (?)";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, peptide.getSequence() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				peptide.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert reported_peptide for " + peptide.getSequence() );
			
			
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
		
		
	}
	
}
