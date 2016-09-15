package org.yeastrc.proxl.import_xml_to_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
// import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchProteinSequenceAnnotationDTO;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 * table search_protein_sequence_annotation
 */
public class SearchProteinSequenceAnnotationDAO {
	
	private static final Logger log = Logger.getLogger(SearchProteinSequenceAnnotationDAO.class);

	private SearchProteinSequenceAnnotationDAO() { }
	public static SearchProteinSequenceAnnotationDAO getInstance() { return new SearchProteinSequenceAnnotationDAO(); }
	
	
	public void saveToDatabase( SearchProteinSequenceAnnotationDTO searchProteinSequenceAnnotationDTO ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "INSERT IGNORE INTO search_protein_sequence_annotation ( search_id, protein_sequence_id, annotation_id ) VALUES (?,?,?)";


		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			//  Skip get generated key since not populated if record already in DB
			
//			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );

			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, searchProteinSequenceAnnotationDTO.getSearchId());
			pstmt.setInt( 2, searchProteinSequenceAnnotationDTO.getProteinSequenceId() );
			pstmt.setInt( 3, searchProteinSequenceAnnotationDTO.getAnnotationId() );
			
			pstmt.executeUpdate();
			
			//  Skip get generated key since not populated if record already in DB
			
//			rs = pstmt.getGeneratedKeys();
//			if( rs.next() ) {
//				search_protein_sequence_annotation.setId( rs.getInt( 1 ) );
//			} else
//				throw new Exception( "Failed to insert search_protein_sequence_annotation for search_id: " 
//						+ search_protein_sequence_annotation.getSearchId()
//						+ ", protein_sequence_id: " + search_protein_sequence_annotation.getProteinSequenceId()
//						+ ", annotation_id: " + search_protein_sequence_annotation.getAnnotationId() );
			
			
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
