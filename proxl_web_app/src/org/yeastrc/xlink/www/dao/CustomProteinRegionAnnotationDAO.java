package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.CustomProteinRegionAnnotationDTO;

public class CustomProteinRegionAnnotationDAO {

	private static final Logger log = Logger.getLogger(CustomProteinRegionAnnotationDAO.class);
	private CustomProteinRegionAnnotationDAO() { }
	public static CustomProteinRegionAnnotationDAO getInstance() { return new CustomProteinRegionAnnotationDAO(); }
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<CustomProteinRegionAnnotationDTO> getAllCustomProteinRegionAnnotationDTO( int proteinSequenceVersionId, int projectId ) throws Exception {

		List<CustomProteinRegionAnnotationDTO> dtos = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM custom_protein_region_annotation WHERE protein_sequence_version_id = ? AND project_id = ? ORDER BY start_position";
		
		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, proteinSequenceVersionId );
			pstmt.setInt( 2,  projectId);
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
			
				CustomProteinRegionAnnotationDTO dto = getDTOFromResultSet( rs );
				dtos.add( dto );

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


		return dtos;
	}
	
	/**
	 * Insert into appropriate table the customized protein region annotations.
	 * 
	 * @param proteinSequenceVersionId
	 * @param projectId
	 * @param dtos
	 * @throws Exception 
	 */
	public void insertNewListOfRegionAnnotationsForProteinAndProject( int proteinSequenceVersionId, int projectId, List<CustomProteinRegionAnnotationDTO> dtos ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String deleteSql = "DELETE FROM custom_protein_region_annotation WHERE protein_sequence_version_id = ? AND project_id = ?";

		String insertSql = "INSERT INTO custom_protein_region_annotation (protein_sequence_version_id, project_id, start_position, ";
		insertSql +=       "end_position, annotation_color, annotation_text, created_by) VALUES (?, ?, ?, ?, ?, ?, ? )";
		
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			conn.setAutoCommit(false);
			
			try {
				
				// first do the delete of the existing data
				pstmt = conn.prepareStatement( deleteSql );
				
				pstmt.setInt( 1,  proteinSequenceVersionId );
				pstmt.setInt( 2, projectId );
				
				pstmt.executeUpdate();
				pstmt.close();
				pstmt = null;
				
			} catch ( Exception e ) {
								
				String msg = "Failed to delete existing data, SQL: " + deleteSql;
				log.error( msg, e );
				throw e;
			}

			if( dtos != null && dtos.size() > 0 ) {
				
				try {
					
					// now insert the data
					pstmt = conn.prepareStatement( insertSql );
					
					for( CustomProteinRegionAnnotationDTO dto : dtos ) {
						
						pstmt.setInt( 1, dto.getProteinSequenceVersionId() );
						pstmt.setInt( 2, dto.getProjectId() );
						pstmt.setInt( 3, dto.getStartPosition() );
						pstmt.setInt( 4,  dto.getEndPosition() );
						pstmt.setString( 5, dto.getAnnotationColor() );
						pstmt.setString( 6,  dto.getAnnotationText() );
						pstmt.setInt( 7,  dto.getCreatedBy() );
						
						pstmt.executeUpdate();
					}
				} catch ( Exception e ) {
									
					String msg = "Failed to insert new data, SQL: " + insertSql;
					log.error( msg, e );
					throw e;
				}
			}
			
			conn.commit();

		} catch ( Exception e ) {
			
			conn.rollback();
			
			String msg = "Error updating custom protein region annotation: " + e.getMessage() ;
			log.error( msg, e );
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			try {
				conn.setAutoCommit(true);
			 } catch( Throwable t ) { ; }
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
		
	}
	
	/**
	 * Get a DTO from a ResultSet's current cursor position
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private CustomProteinRegionAnnotationDTO getDTOFromResultSet( ResultSet rs ) throws SQLException {
		
		CustomProteinRegionAnnotationDTO dto = new CustomProteinRegionAnnotationDTO();
		
		dto.setStartPosition( rs.getInt( "start_position" ) );
		dto.setEndPosition( rs.getInt( "end_position" ) );
		dto.setProjectId( rs.getInt( "project_id" ) );
		dto.setProteinSequenceVersionId( rs.getInt( "protein_sequence_version_id" ) );
		dto.setAnnotationText( rs.getString( "annotation_text" ) );
		dto.setAnnotationColor( rs.getString( "annotation_color" ) );
		dto.setCreatedBy( rs.getInt( "created_by" ) );
		
		return dto;
		
	}
}
