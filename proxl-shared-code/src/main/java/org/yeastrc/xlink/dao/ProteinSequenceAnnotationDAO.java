package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ProteinSequenceAnnotationDTO;

public class ProteinSequenceAnnotationDAO {

	private static final Logger log = Logger.getLogger(NoteDAO.class);

	//  private constructor
	private ProteinSequenceAnnotationDAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static ProteinSequenceAnnotationDAO getInstance() { 
		return new ProteinSequenceAnnotationDAO(); 
	}
	
	/**
	 * For a given annotation id, get a ProteinSequenceAnnotationDTO object
	 * 
	 * @param annoId
	 * @return
	 * @throws Exception
	 */
	public ProteinSequenceAnnotationDTO getProteinSequenceAnnotationDTOForAnnotationId( int annoId ) throws Exception {


		ProteinSequenceAnnotationDTO returnItem = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM annotation WHERE id = ?";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, annoId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				returnItem = new ProteinSequenceAnnotationDTO();
				
				returnItem.setId( annoId );
				returnItem.setName( rs.getString( "name" ) );
				returnItem.setTaxonomy( rs.getInt( "taxonomy" ) );
				returnItem.setDescription( rs.getString( "description" ) );

			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select ProteinSequenceAnnotationDTO, annoId: " + annoId + ", sql: " + sql;
			
			log.error( msg, e );
			
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
		
		return returnItem;
	}
	
}
