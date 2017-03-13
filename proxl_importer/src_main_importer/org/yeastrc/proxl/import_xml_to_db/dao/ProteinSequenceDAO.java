package org.yeastrc.proxl.import_xml_to_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dto.ProteinSequenceDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 * table protein_sequence
 */
public class ProteinSequenceDAO {

	private static final Logger log = Logger.getLogger(ProteinSequenceDAO.class);
	private ProteinSequenceDAO() { }
	public static ProteinSequenceDAO getInstance() { return new ProteinSequenceDAO(); }
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ProteinSequenceDTO getProteinSequenceDTOFromDatabase( int id ) throws Exception {
		ProteinSequenceDTO protein_sequence = new ProteinSequenceDTO();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT sequence FROM protein_sequence WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( !rs.next() )
				throw new Exception( "could not find protein_sequence with id " + id );
			protein_sequence.setId( id );
			protein_sequence.setSequence( rs.getString( 1 ) );
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
		return protein_sequence;
	}
	
	/**
	 * Get the protein_sequence DTO corresponding to supplied sequence. If no matching
	 * protein_sequence is found in the database, it is inserted and a populated DTO
	 * returned. If already in the database, DTO is populated from database
	 * and returned.
	 * 
	 * @param sequence
	 * @return
	 * @throws Exception
	 */
	public ProteinSequenceDTO getProteinSequenceDTO_InsertIfNotInDB( String sequence ) throws Exception {
		ProteinSequenceDTO proteinSequenceDTO = new ProteinSequenceDTO();
		proteinSequenceDTO.setSequence( sequence );
		List<Integer> proteinIdList = getProteinIdForSequence( sequence );
		if ( proteinIdList.size() > 1 ) {
			deleteAllButRecordWithId( proteinIdList.get(0), sequence );
		}
		if ( ! proteinIdList.isEmpty() ) {
			proteinSequenceDTO.setId( proteinIdList.get(0) );
			return proteinSequenceDTO;
		}
		saveToDatabase( proteinSequenceDTO );
		return proteinSequenceDTO;
	}
	
	/**
	 * Get the id for the supplied protein sequence from the database. Returns empty list if not found.
	 * @param sequence
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getProteinIdForSequence( String sequence ) throws Exception {
		List<Integer> results = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT id FROM protein_sequence WHERE sequence = ?";
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
	 * @param item
	 * @throws Exception
	 */
	private void saveToDatabase( ProteinSequenceDTO item ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int insertedProteinId = 0;
		String sql = "INSERT INTO protein_sequence (sequence) VALUES (?)";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt.setString( 1, item.getSequence() );
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				insertedProteinId = rs.getInt( 1 );
			} else
				throw new Exception( "Failed to insert protein sequence for " + item.getSequence() );
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
		List<Integer> proteinIdList = getProteinIdForSequence( item.getSequence() );
		if ( proteinIdList.size() > 1 ) {
			deleteAllButRecordWithId( proteinIdList.get(0), item.getSequence() );
		}
		if ( ! proteinIdList.isEmpty() ) {
			item.setId( proteinIdList.get(0) );
			return;
		}
		String msg = "Unable to find protein record just inserted by sequence.  "
				+ "Inserted protein id: " + insertedProteinId + ", sequence: " + item.getSequence();
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
		String sql = "DELETE FROM protein_sequence WHERE id <> ? AND sequence = ?";
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
