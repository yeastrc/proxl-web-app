package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.db.AuthLibraryDBConnectionFactory;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;

/**
 * DAO for xl_user table
 *
 */
public class XLinkUserDAO {
	
	private static final Logger log = Logger.getLogger(XLinkUserDAO.class);

	//  private constructor
	private XLinkUserDAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static XLinkUserDAO getInstance() { 
		return new XLinkUserDAO(); 
	}
	
	
//	CREATE TABLE xl_user (
//			  auth_user_id INT UNSIGNED NOT NULL,
//			  first_name VARCHAR(255) NOT NULL,
//			  last_name VARCHAR(255) NOT NULL,
//			  organization VARCHAR(2000) NULL,

	
	/**
	 * @param lastName
	 * @return null if not found
	 * @throws 
	 */
	public List<XLinkUserDTO> getXLinkUserDTOListForLastName( String lastName ) throws Exception {

		
		if ( StringUtils.isEmpty( lastName ) ) {
			
			String msg = "Last name cannot be null or empty";
			
			log.error( msg );

			throw new IllegalArgumentException( msg );
		}
		
		 List<XLinkUserDTO> returnItems = new ArrayList<XLinkUserDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT auth_user_id, first_name, last_name, organization FROM xl_user WHERE last_name = ?";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, lastName );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				XLinkUserDTO item = new XLinkUserDTO();
				
				item.setFirstName( rs.getString( "first_name" ) );
				item.setLastName( rs.getString( "last_name" ) );
				item.setOrganization( rs.getString( "organization" ) );

				int authUserId = rs.getInt( "auth_user_id" );

				AuthUserDTO authUserDTO = AuthUserDAO.getInstance().getAuthUserDTOForId( authUserId );

				if ( authUserDTO == null ) {
					
					String msg = "Unable to retrieve authUserDTO record for auth_user_id: " + authUserId;
					
					log.error( msg );
					
					throw new Exception( msg );
				}
				
				item.setAuthUser(authUserDTO);
				
				returnItems.add( item );
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select XLinkUserDTO, lastName: " + lastName + ", sql: " + sql;
			
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
		
		return returnItems;
	}
	

	
	/**
	 * @param authUserId
	 * @return null if not found
	 * @throws Exception
	 */
	public XLinkUserDTO getXLinkUserDTOForAuthUserId( int authUserId ) throws Exception {

		AuthUserDTO authUserDTO = AuthUserDAO.getInstance().getAuthUserDTOForId( authUserId );
		
		return getXLinkUserDTOForAuthUserIdInternal( authUserDTO );
	}
	
	/**
	 * @param username
	 * @return null if not found
	 * @throws Exception
	 */
	public XLinkUserDTO getXLinkUserDTOForUsername( String username ) throws Exception {
		
		AuthUserDTO authUserDTO = AuthUserDAO.getInstance().getAuthUserDTOForUsername( username );
		
		return getXLinkUserDTOForAuthUserIdInternal( authUserDTO );
	}
	
	/**
	 * @param email
	 * @return null if not found
	 * @throws Exception
	 */
	public XLinkUserDTO getXLinkUserDTOForEmail( String email ) throws Exception {
		
		AuthUserDTO authUserDTO = AuthUserDAO.getInstance().getAuthUserDTOForEmail( email );
		
		return getXLinkUserDTOForAuthUserIdInternal( authUserDTO );
	}
	
	/**
	 * @param authUserDTO
	 * @return 
	 * @throws Exception if not found
	 */
	private XLinkUserDTO getXLinkUserDTOForAuthUserIdInternal( AuthUserDTO authUserDTO ) throws Exception {

		
		if ( authUserDTO == null ) {

			return null;
		}
		
		XLinkUserDTO returnItem = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT first_name, last_name, organization FROM xl_user WHERE auth_user_id = ?";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, authUserDTO.getId() );
			
			rs = pstmt.executeQuery();
			
			if( !rs.next() ) {
				
				throw new Exception( "xl_user record not found for auth_user_id: " + authUserDTO.getId() );
				
			} else {

				returnItem = new XLinkUserDTO();

				returnItem.setAuthUser( authUserDTO );
				
				returnItem.setFirstName( rs.getString( "first_name" ) );
				returnItem.setLastName( rs.getString( "last_name" ) );
				returnItem.setOrganization( rs.getString( "organization" ) );

			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select XLinkUserDTO, auth_user_id: " + authUserDTO.getId() + ", sql: " + sql;
			
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
	
	
	
	/**
	 * @param item
	 * @param hashedPassword
	 * @throws Exception
	 */
	public void save( XLinkUserDTO item, String hashedPassword ) throws Exception {
		

		Connection dbConnection = null;

		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			save( item, hashedPassword, dbConnection );
			
		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
		
	}

	/**
	 * @param item
	 * @param hashedPassword
	 * @param dbConnection
	 * @throws Exception
	 */
	public void save( XLinkUserDTO item, String hashedPassword, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;

//		CREATE TABLE xl_user (
//		  auth_user_id INT UNSIGNED NOT NULL,
//		  first_name VARCHAR(255) NOT NULL,
//		  last_name VARCHAR(255) NOT NULL,
//		  organization VARCHAR(2000) NULL,

		final String sql = "INSERT INTO xl_user (auth_user_id, first_name, last_name, organization) " +
				"VALUES ( ?, ?, ?, ? )";
		
		try {
			
			AuthUserDAO.getInstance().save( item.getAuthUser(), hashedPassword, dbConnection );
			
			
			pstmt = dbConnection.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getAuthUser().getId() );
			counter++;
			pstmt.setString( counter, item.getFirstName() );
			counter++;
			pstmt.setString( counter, item.getLastName() );
			counter++;
			pstmt.setString( counter, item.getOrganization() );
			
			pstmt.executeUpdate();
			
			
		} catch ( Exception e ) {
			
			String msg = "Failed to insert XLinkUserDTO, sql: " + sql;
			
			log.error( msg, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}

		}
		
	}
	
	/**
	 * @param authUserId
	 * @param lastLoginIP
	 * @throws Exception
	 */
	public void updateLastLogin( int authUserId, String lastLoginIP ) throws Exception {
		
		AuthUserDAO.getInstance().updateLastLogin( authUserId, lastLoginIP );
	}
	

	/**
	 * @param authUserId
	 * @param lastLoginDateTime
	 * @param lastLoginIP
	 * @throws Exception
	 */
	public void updateLastLogin( int authUserId, Date lastLoginDateTime, String lastLoginIP ) throws Exception {
		
		AuthUserDAO.getInstance().updateLastLogin( authUserId, lastLoginDateTime, lastLoginIP );
	}

	
	

	/**
	 * Update first_name = ? 
	 * @param authUserId
	 * @param firstName
	 * @throws Exception
	 */
	public void updateFirstName( int authUserId, String firstName ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "UPDATE xl_user SET first_name = ? WHERE auth_user_id = ?";

		
		try {
			
			conn = AuthLibraryDBConnectionFactory.getConnection(  );


//			CREATE TABLE xl_user (
//					  auth_user_id INT UNSIGNED NOT NULL,
//					  first_name VARCHAR(255) NOT NULL,
//					  last_name VARCHAR(255) NOT NULL,
//					  organization VARCHAR(2000) NULL,

			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
	
			pstmt.setString( counter, firstName );
	
			
			counter++;
			pstmt.setInt( counter, authUserId );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to update first_name, sql: " + sql;
			
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
		
	}
	


	/**
	 * Update last_name = ? 
	 * @param authUserId
	 * @param lastName
	 * @throws Exception
	 */
	public void updateLastName( int authUserId, String lastName ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "UPDATE xl_user SET last_name = ? WHERE auth_user_id = ?";

		
		try {
			
			conn = AuthLibraryDBConnectionFactory.getConnection(  );


//			CREATE TABLE xl_user (
//					  auth_user_id INT UNSIGNED NOT NULL,
//					  first_name VARCHAR(255) NOT NULL,
//					  last_name VARCHAR(255) NOT NULL,
//					  organization VARCHAR(2000) NULL,

			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
	
			pstmt.setString( counter, lastName );
	
			
			counter++;
			pstmt.setInt( counter, authUserId );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to update last_name, sql: " + sql;
			
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
		
	}
	

	/**
	 * Update organization = ? 
	 * @param authUserId
	 * @param organization
	 * @throws Exception
	 */
	public void updateOrganization( int authUserId, String organization ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "UPDATE xl_user SET organization = ? WHERE auth_user_id = ?";

		
		try {
			
			conn = AuthLibraryDBConnectionFactory.getConnection(  );


//			CREATE TABLE xl_user (
//					  auth_user_id INT UNSIGNED NOT NULL,
//					  first_name VARCHAR(255) NOT NULL,
//					  last_name VARCHAR(255) NOT NULL,
//					  organization VARCHAR(2000) NULL,

			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
	
			pstmt.setString( counter, organization );
	
			
			counter++;
			pstmt.setInt( counter, authUserId );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to update organization, sql: " + sql;
			
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
		
	}

}
