package org.yeastrc.proxl.import_xml_to_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
// import java.sql.Statement;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchProteinSequenceVersionAnnotationDTO;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 * table search__protein_sequence_version__annotation
 */
public class SearchProteinSequenceVersionAnnotationDAO {

	private static final Logger log = Logger.getLogger(SearchProteinSequenceVersionAnnotationDAO.class);
	private SearchProteinSequenceVersionAnnotationDAO() { }
	public static SearchProteinSequenceVersionAnnotationDAO getInstance() { return new SearchProteinSequenceVersionAnnotationDAO(); }
	
	/**
	 * @param searchProteinSequenceVersionAnnotationDTO
	 * @throws Exception
	 */
	public void saveToDatabase( SearchProteinSequenceVersionAnnotationDTO searchProteinSequenceVersionAnnotationDTO ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "INSERT IGNORE INTO search__protein_sequence_version__annotation ( search_id, protein_sequence_version_id, annotation_id ) VALUES (?,?,?)";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			//  Skip get generated key since not populated if record already in DB
//			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchProteinSequenceVersionAnnotationDTO.getSearchId());
			pstmt.setInt( 2, searchProteinSequenceVersionAnnotationDTO.getProteinSequenceVersionId() );
			pstmt.setInt( 3, searchProteinSequenceVersionAnnotationDTO.getAnnotationId() );
			pstmt.executeUpdate();
			//  Skip get generated key since not populated if record already in DB
//			rs = pstmt.getGeneratedKeys();
//			if( rs.next() ) {
//				search__protein_sequence_version__annotation.setId( rs.getInt( 1 ) );
//			} else
//				throw new Exception( "Failed to insert search__protein_sequence_version__annotation for search_id: " 
//						+ search__protein_sequence_version__annotation.getSearchId()
//						+ ", protein_sequence_version_id: " + search__protein_sequence_version__annotation.getProteinSequenceVersionId()
//						+ ", annotation_id: " + search__protein_sequence_version__annotation.getAnnotationId() );
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
