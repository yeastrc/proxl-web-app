package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.enum_classes.SearchRecordStatus;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dto.Search_Core_DTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_Search_Core_DTO;

/**
 * Table search
 *
 */
public class SearchDAO {

	private static final Logger log = Logger.getLogger(SearchDAO.class);
	private SearchDAO() { }
	public static SearchDAO getInstance() { return new SearchDAO(); }
	
	/**
	 * Get the given Search from the database
	 * 
	 * This method is left here since it is called from so many places
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public SearchDTO getSearchFromProjectSearchId( int id ) throws Exception {
		Search_Core_DTO search_Core_DTO = 
				Cached_Search_Core_DTO.getInstance().getSearch_Core_DTO( id );
		if ( search_Core_DTO == null ) {
			return null;  // EARLY EXIT
		}
		SearchDTO searchDTO = new SearchDTO( search_Core_DTO );
		//  Update projectId to ensure it is the latest.  Could invalidate cache on move but too important
		Integer projectId = getProjectIdFromProjectSearchId( id );
		if ( projectId == null ) {
			String msg = "getProjectIdFromProjectSearchId(id) returns null after get Search_Core_DTO not null."
					+ "  projectSearchId: " + id;
			log.error( msg );
			throw new ProxlWebappInternalErrorException(msg);
		}
		searchDTO.setProjectId( projectId );
		return searchDTO;
	}
	
	private static final String GET_FROM_PROJECT_SEARCH_ID_SQL =
			"SELECT project_search.search_id, "
			+ " search.path, search.directory_name, "
			+ " search.load_time, search.fasta_filename, search.has_scan_data, "
			+ " project_search.search_name, project_search.project_id, project_search.search_display_order "
			+ " FROM project_search INNER JOIN search ON project_search.search_id = search.id "
			+ " WHERE project_search.id = ? "
			+    " AND project_search.status_id = " + SearchRecordStatus.IMPORT_COMPLETE_VIEW.value();
	/**
	 * Get the given Search from the database
	 * 
	 * uses tables project_search and search
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Search_Core_DTO getSearch_Core_DTO_FromProjectSearchId( int id ) throws Exception {
		Search_Core_DTO search = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = GET_FROM_PROJECT_SEARCH_ID_SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				search = new Search_Core_DTO();
				search.setProjectSearchId( id );
				search.setSearchId( rs.getInt( "search_id" ) );
				search.setFastaFilename( rs.getString( "fasta_filename" ) );
				search.setPath( rs.getString( "path" ) );
				search.setDirectoryName( rs.getString( "directory_name" ) );
				search.setLoad_time( new DateTime( rs.getTimestamp( "load_time" ) ) );
				search.setName( rs.getString( "search_name" ) );
				search.setProjectId( rs.getInt( "project_id" ) );
				int hasScanDataInt = rs.getInt( "has_scan_data" );
				if ( Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE == hasScanDataInt ) {
					search.setHasScanData( false );
				} else {
					search.setHasScanData( true );
				}
				search.setDisplayOrder( rs.getInt( "search_display_order" ) );
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
		return search;
	}
	
	/**
	 * Get the project id for the project_search.id from the database
	 * 
	 * This uses project_search table but left here since called from so many places
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Integer getProjectIdFromProjectSearchId( int id ) throws Exception {
		Integer result = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT project_id FROM project_search WHERE id = ? AND status_id = " + SearchRecordStatus.IMPORT_COMPLETE_VIEW.value();
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				result = rs.getInt( "project_id" );
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

}
