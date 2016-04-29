package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PeptideDTO;

public class PeptideDAO {
	
	private static final Logger log = Logger.getLogger(PeptideDAO.class);

	private PeptideDAO() { }
	public static PeptideDAO getInstance() { return new PeptideDAO(); }
	
	public PeptideDTO getPeptideDTOFromDatabase( int id ) throws Exception {
		
		
		PeptideDTO peptide = new PeptideDTO();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT sequence FROM peptide WHERE id = ?";
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();

			if( !rs.next() )
				throw new Exception( "could not find peptide with id " + id );
			
			peptide.setId( id );
			peptide.setSequence( rs.getString( 1 ) );
			
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
	public PeptideDTO getPeptideDTO( String sequence ) throws Exception {
		
		PeptideDTO peptide = new PeptideDTO();
		peptide.setSequence( sequence );
		peptide.setId( getPeptideIdForSequence( sequence ) );
		
		if( peptide.getId() == 0 )
			saveToDatabase( peptide );
		
		return peptide;
	}
	
	/**
	 * Get the id for the supplied peptide sequence (as it appears in percolator
	 * output) from the database. Returns 0 if not found.
	 * @param sequence
	 * @return
	 * @throws Exception
	 */
	public int getPeptideIdForSequence( String sequence ) throws Exception {
		int id = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT id FROM peptide WHERE sequence = ?";
		
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
	public void saveToDatabase( PeptideDTO peptide ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "INSERT INTO peptide (sequence) VALUES (?)";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt.setString( 1, peptide.getSequence() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				peptide.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert peptide for " + peptide.getSequence() );
			
			
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
