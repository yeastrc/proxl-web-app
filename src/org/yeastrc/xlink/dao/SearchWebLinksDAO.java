package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchWebLinksDTO;

public class SearchWebLinksDAO {
	
	private static final Logger log = Logger.getLogger(SearchWebLinksDAO.class);

	private SearchWebLinksDAO() { }
	public static SearchWebLinksDAO getInstance() { return new SearchWebLinksDAO(); }

	/**
	 * Save the given webLinks to the database. Assumes it's not already in the database.
	 * @param webLinks
	 * @throws Exception
	 */
	public void save( SearchWebLinksDTO webLinks ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "INSERT INTO search_web_links ( search_id, auth_user_id, link_url, link_label ) VALUES (?,?,?,?)";


//CREATE TABLE search_web_links (
//  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//  search_id INT UNSIGNED NOT NULL,
//  auth_user_id INT UNSIGNED NULL,
//  link_url VARCHAR(600) NOT NULL,
//  link_label VARCHAR(400) NOT NULL,
//  link_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt.setInt( 1, webLinks.getSearchid() );
			pstmt.setInt( 2, webLinks.getAuthUserId() );
			pstmt.setString( 3, webLinks.getLinkUrl() );
			pstmt.setString( 4, webLinks.getLinkLabel() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				webLinks.setId( rs.getInt( 1 ) );
				webLinks.setDateTime( new DateTime() );
			} else
				throw new Exception( "Failed to insert webLinks" );
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
			
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
	 * Delete the webLinks with the supplied id
	 * @param id
	 * @throws Exception
	 */
	public void delete( int id ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;

		String sql = "DELETE FROM search_web_links WHERE id = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
			
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
	
	/**
	 * Delete the supplied webLinks from the database (based on its id)
	 * @param webLinks
	 * @throws Exception
	 */
	public void delete( SearchWebLinksDTO webLinks ) throws Exception {
		delete( webLinks.getId() );
	}
	
	/**
	 * Load a webLinks with the given id from the database, return a webLinks object
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public SearchWebLinksDTO load( int id ) throws Exception {
		SearchWebLinksDTO webLinks = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT search_id, link_url, link_label, link_timestamp FROM search_web_links  WHERE id = ?";

		//CREATE TABLE search_web_links (
	//  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	//  search_id INT UNSIGNED NOT NULL,
	//  auth_user_id INT UNSIGNED NULL,
	//  link_url VARCHAR(600) NOT NULL,
	//  link_label VARCHAR(400) NOT NULL,
	//  link_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				webLinks = new SearchWebLinksDTO();
				
				webLinks.setId( id );
				webLinks.setSearchid( rs.getInt( "search_id" ) );
				webLinks.setLinkUrl( rs.getString( "link_url" ) );
				webLinks.setLinkLabel( rs.getString( "link_label" ) );
				webLinks.setDateTime( new DateTime( rs.getTimestamp( "link_timestamp" ) ) );				
			}
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
			
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
		
		return webLinks;
		
	}
	
	
}
