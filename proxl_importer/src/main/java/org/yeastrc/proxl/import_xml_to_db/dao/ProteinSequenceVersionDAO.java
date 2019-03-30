package org.yeastrc.proxl.import_xml_to_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.proxl.import_xml_to_db.dto.ProteinSequenceVersionDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 * table protein_sequence_version
 */
public class ProteinSequenceVersionDAO {

	private static final Logger log = LoggerFactory.getLogger( ProteinSequenceVersionDAO.class);
	private ProteinSequenceVersionDAO() { }
	public static ProteinSequenceVersionDAO getInstance() { return new ProteinSequenceVersionDAO(); }

	/**
	 * Get the protein_sequence DTO corresponding to supplied sequence. If no matching
	 * protein_sequence_v2 is found in the database, it is inserted and a populated DTO
	 * returned. If already in the database, DTO is populated from database
	 * and returned.
	 * 
	 * @param sequence
	 * @return
	 * @throws Exception
	 */
	public ProteinSequenceVersionDTO getProteinSequenceVersionDTO_InsertIfNotInDB( ProteinSequenceVersionDTO searchItem ) throws Exception {
		Integer smallestId = getSmallestIdForData( searchItem );
		if ( smallestId != null ) {
			searchItem.setId( smallestId );
		} else {
			saveToDatabase( searchItem );
		}
		return searchItem;
	}
	
	/**
	 * @param item
	 * @throws Exception
	 */
	private void saveToDatabase( ProteinSequenceVersionDTO item ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "INSERT INTO protein_sequence_version (protein_sequence_id,isotope_label_id) VALUES (?,?)";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt.setInt( 1, item.getProteinSequenceId() );
			pstmt.setInt( 2, item.getIsotopeLabelId() );
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert item: " + item );
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
		Integer smallestId = getSmallestIdForData( item );
		if ( smallestId == null ) {
			String msg = "Unable to find protein_sequence_version record just inserted by data.  "
					+ "Inserted protein_sequence_version id: " + item.getId() + ", item: " + item;
			log.error( msg );
			throw new ProxlImporterInteralException(msg);
		}
		if ( smallestId != item.getId() ) {
			deleteAllButRecordWithId( smallestId, item );
			item.setId( smallestId );
		}
	}

	
	/**
	 * Get the id for the supplied protein_sequence_id AND isotope_label_id from the database. Returns empty list if not found.
	 * @param proteinSequenceId
	 * @param isotopeLabelId
	 * @return
	 * @throws Exception
	 */
	private Integer getSmallestIdForData( ProteinSequenceVersionDTO item ) throws Exception {
		Integer result = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = 
				"SELECT id FROM protein_sequence_version WHERE protein_sequence_id = ? AND isotope_label_id = ? ORDER BY id LIMIT 1";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, item.getProteinSequenceId() );
			pstmt.setInt( 2, item.getIsotopeLabelId() );
			rs = pstmt.executeQuery();
			if ( rs.next() ) {
				result = rs.getInt( "id" );
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
		return result;
	}

	/**
	 * Clean up database for items inserted more than once
	 * @param id - id to keep
	 * @param sequence
	 * @throws Exception 
	 */
	private void deleteAllButRecordWithId( int smallestId, ProteinSequenceVersionDTO item ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM protein_sequence_version WHERE id <> ? AND protein_sequence_id = ? AND isotope_label_id = ? ";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, smallestId );
			pstmt.setInt( 2, item.getProteinSequenceId() );
			pstmt.setInt( 3, item.getIsotopeLabelId() );
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
