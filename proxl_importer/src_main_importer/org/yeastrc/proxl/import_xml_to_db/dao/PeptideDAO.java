package org.yeastrc.proxl.import_xml_to_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dto.PeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 *
 */
public class PeptideDAO {
	
	private static final Logger log = Logger.getLogger(PeptideDAO.class);
	
	private PeptideDAO() { }
	public static PeptideDAO getInstance() { return new PeptideDAO(); }
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
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
	public PeptideDTO getPeptideDTOFromSequenceInsertIfNotInTable( String sequence ) throws Exception {
		PeptideDTO peptide = new PeptideDTO();
		peptide.setSequence( sequence );
		List<Integer> peptideIdList = getPeptideIdForSequence( sequence );
		if ( peptideIdList.size() > 1 ) {
			deleteAllButRecordWithId( peptideIdList.get(0), sequence );
		}
		if ( ! peptideIdList.isEmpty() ) {
			peptide.setId( peptideIdList.get(0) );
			return peptide;
		}
		saveToDatabase( peptide );
		return peptide;
	}
	
	/**
	 * Get the id for the supplied peptide sequence (as it appears in percolator
	 * output) from the database. Returns empty list if not found.
	 * @param sequence
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getPeptideIdForSequence( String sequence ) throws Exception {
		List<Integer> results = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT id FROM peptide WHERE sequence = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, sequence );
			rs = pstmt.executeQuery();
			while ( rs.next() ) {
				results.add( rs.getInt( "id" ) );
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
		Collections.sort( results );
		return results;
	}

	/**
	 * @param peptide
	 * @throws Exception
	 */
	private void saveToDatabase( PeptideDTO peptide ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int insertedPeptideId = 0;
		String sql = "INSERT INTO peptide (sequence) VALUES (?)";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt.setString( 1, peptide.getSequence() );
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				insertedPeptideId = rs.getInt( 1 );
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
		List<Integer> peptideIdList = getPeptideIdForSequence( peptide.getSequence() );
		if ( peptideIdList.size() > 1 ) {
			deleteAllButRecordWithId( peptideIdList.get(0), peptide.getSequence() );
		}
		if ( ! peptideIdList.isEmpty() ) {
			peptide.setId( peptideIdList.get(0) );
			return;
		}
		String msg = "Unable to find peptide record just inserted by sequence.  "
				+ "Inserted peptide id: " + insertedPeptideId + ", sequence: " + peptide.getSequence();
		log.error( msg );
		throw new ProxlImporterInteralException(msg);
	}
	
	/**
	 * Clean up database for sequences inserted more than once
	 * @param id - id to keep
	 * @param sequence
	 * @throws Exception 
	 */
	private void deleteAllButRecordWithId( int id, String sequence ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM peptide WHERE id <> ? AND sequence = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			pstmt.setString( 2, sequence );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
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
