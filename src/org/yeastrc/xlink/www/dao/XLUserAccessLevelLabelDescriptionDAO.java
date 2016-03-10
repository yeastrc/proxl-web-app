package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.XLUserAccessLevelLabelDescriptionDTO;

/**
 * DAO for xl_user_access_level_label_description table
 *
 */
public class XLUserAccessLevelLabelDescriptionDAO {
	
	private static final Logger log = Logger.getLogger(XLUserAccessLevelLabelDescriptionDAO.class);

	//  private constructor
	private XLUserAccessLevelLabelDescriptionDAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static XLUserAccessLevelLabelDescriptionDAO getInstance() { 
		return new XLUserAccessLevelLabelDescriptionDAO(); 
	}
	
	
	//CREATE TABLE xl_user_access_level_label_description (
//	  xl_user_access_level_numeric_value INT UNSIGNED NOT NULL,
//	  label VARCHAR(255) NOT NULL,
//	  description VARCHAR(255) NULL,

	
	/**
	 * @param accessLevelNumericValue
	 * @return null if not found
	 * @throws Exception
	 */
	public XLUserAccessLevelLabelDescriptionDTO getXLUserAccessLevelLabelDescriptionDTOForAuthUserId( int accessLevelNumericValue ) throws Exception {

		XLUserAccessLevelLabelDescriptionDTO returnItem = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM xl_user_access_level_label_description WHERE xl_user_access_level_numeric_value = ?";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, accessLevelNumericValue );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
		
				returnItem = new XLUserAccessLevelLabelDescriptionDTO();

				returnItem.setAccessLevelNumericValue( rs.getInt( "xl_user_access_level_numeric_value" ) );
				returnItem.setLabel( rs.getString( "label" ) );
				returnItem.setDescription( rs.getString( "description" ) );

			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select XLUserAccessLevelLabelDescriptionDTO, accessLevelNumericValue: " + accessLevelNumericValue + ", sql: " + sql;
			
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
