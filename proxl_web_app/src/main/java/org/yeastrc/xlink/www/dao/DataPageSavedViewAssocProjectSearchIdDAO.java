package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.www.dto.DataPageSavedViewAssocProjectSearchIdDTO;

/**
 * table data_page_saved_view_assoc_project_search_id_tbl
 *
 */
public class DataPageSavedViewAssocProjectSearchIdDAO {

	private static final Logger log = LoggerFactory.getLogger( DataPageSavedViewAssocProjectSearchIdDAO.class);
	//  private constructor
	private DataPageSavedViewAssocProjectSearchIdDAO() { }
	/**
	 * @return newly created instance
	 */
	public static DataPageSavedViewAssocProjectSearchIdDAO getInstance() { 
		return new DataPageSavedViewAssocProjectSearchIdDAO(); 
	}

	private static final String INSERT_SQL = 
			"INSERT INTO data_page_saved_view_assoc_project_search_id_tbl "
			+ "( assoc_main_id, project_search_id ) "
			+ "VALUES ( ?, ? )";
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( DataPageSavedViewAssocProjectSearchIdDTO item, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = INSERT_SQL;
		try {
//			pstmt = dbConnection.prepareStatement( sql );
			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, item.getAssocMainId() );
			counter++;
			pstmt.setInt( counter, item.getProjectSearchId() );

			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else {
				String msg = "Failed to insert DataPageSavedViewAssocProjectSearchIdDTO, generated key not found.";
				log.error( msg );
				throw new Exception( msg );
			}
		} catch ( Exception e ) {
			String msg = "Failed to insert DataPageSavedViewAssocProjectSearchIdDTO, sql: " + sql;
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
		}
	}
}
