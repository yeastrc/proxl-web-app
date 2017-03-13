package org.yeastrc.proxl.import_xml_to_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dto.AnnotationDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 * table annotation
 */
public class AnnotationDAO {
	
	private static final Logger log = Logger.getLogger(AnnotationDAO.class);
	
	private AnnotationDAO() { }
	public static AnnotationDAO getInstance() { return new AnnotationDAO(); }
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public AnnotationDTO getAnnotationDTOFromDatabase( int id ) throws Exception {
		AnnotationDTO annotation = new AnnotationDTO();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM annotation WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( !rs.next() )
				throw new Exception( "could not find annotation with id " + id );
			annotation.setId( id );
			annotation.setTaxonomy( rs.getInt( "taxonomy" ) );
			annotation.setName( rs.getString( "name" ) );
			annotation.setDescription( rs.getString( "description" ) );
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
		return annotation;
	}
	
	/**
	 * Update the id in the annotation DTO object provided from
	 * existing record or inserted record
	 * 
	 * 
	 * @param sequence
	 * @return
	 * @throws Exception
	 */
	public void getAnnotationId_InsertIfNotInDB( AnnotationDTO annotation ) throws Exception {
		List<Integer> idList = getIdForNameTaxDesc( annotation );
		if ( idList.size() > 1 ) {
			deleteAllButRecordWithId( idList.get(0), annotation );
		}
		if ( ! idList.isEmpty() ) {
			annotation.setId( idList.get(0) );
			return;
		}
		saveToDatabase( annotation );
		return;
	}
	
	/**
	 * Get the id for the supplied protein sequence from the database. Returns empty list if not found.
	 * @param sequence
	 * @return
	 * @throws Exception
	 */
	public List<Integer>  getIdForNameTaxDesc( AnnotationDTO annotation ) throws Exception {
		List<Integer> results = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT id FROM annotation WHERE name = ? AND taxonomy = ? AND description = ? ";
		if ( annotation.getDescription() == null ) {
			sql = "SELECT id FROM annotation WHERE name = ? AND taxonomy = ? AND description IS NULL ";
		}
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, annotation.getName() );
			pstmt.setInt( 2, annotation.getTaxonomy() );
			if ( annotation.getDescription() != null ) {
				pstmt.setString( 3, annotation.getDescription() );
			}
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
	 * @param annotation
	 * @throws Exception
	 */
	private void saveToDatabase( AnnotationDTO annotation ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int insertedId = 0;
		String sql = "INSERT INTO annotation ( name, description, taxonomy ) VALUES (?,?,?)";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt.setString( 1, annotation.getName() );
			pstmt.setString( 2, annotation.getDescription() );
			pstmt.setInt( 3, annotation.getTaxonomy() );
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				insertedId = rs.getInt( 1 );
			} else
				throw new Exception( "Failed to insert annotation for name: " + annotation.getName()
						+ ", description: " + annotation.getDescription() );
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
		List<Integer> idList = getIdForNameTaxDesc( annotation );
		if ( idList.size() > 1 ) {
			deleteAllButRecordWithId( idList.get(0), annotation );
		}
		if ( ! idList.isEmpty() ) {
			annotation.setId( idList.get(0) );
			return;
		}
		String msg = "Unable to find annotation record just inserted by sequence.  "
				+ "Inserted id: " + insertedId + ", annotation: " + annotation;
		log.error( msg );
		throw new ProxlImporterInteralException(msg);
	}
	
	/**
	 * Clean up database for annotation records inserted more than once
	 * @param id - id to keep
	 * @param sequence
	 * @throws Exception 
	 */
	private void deleteAllButRecordWithId( int id, AnnotationDTO annotation ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM annotation WHERE id <> ? AND name = ? AND taxonomy = ? AND description = ? ";
		if ( annotation.getDescription() == null ) {
			sql = "DELETE FROM annotation WHERE id <> ? AND name = ? AND taxonomy = ? AND description IS NULL ";
		}
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			pstmt.setString( 2, annotation.getName() );
			pstmt.setInt( 3, annotation.getTaxonomy() );
			if ( annotation.getDescription() != null ) {
				pstmt.setString( 4, annotation.getDescription() );
			}
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
