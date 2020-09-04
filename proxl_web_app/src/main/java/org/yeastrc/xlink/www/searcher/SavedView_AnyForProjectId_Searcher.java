package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;

public class SavedView_AnyForProjectId_Searcher {

	private static final Log log = LogFactory.getLog(SavedView_AnyForProjectId_Searcher.class);
	private SavedView_AnyForProjectId_Searcher() { }
	private static final SavedView_AnyForProjectId_Searcher _INSTANCE = new SavedView_AnyForProjectId_Searcher();
	public static SavedView_AnyForProjectId_Searcher getInstance() { return _INSTANCE; }

	private static final String QUERY_SQL = 
			"SELECT "
			+ " id "
			+ " FROM "
			+ " data_page_saved_view_tbl "
			+ " WHERE project_id = ? "
			+ " LIMIT 1 ";

	/**
	 * @param projectId
	 * @return
	 * @throws SQLException
	 */
	public boolean savedView_AnyForProjectId( int projectId ) throws Exception {

		boolean result = false;

		final String querySQL = QUERY_SQL;
				
		try ( Connection connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			     PreparedStatement preparedStatement = connection.prepareStatement( querySQL ) ) {
			
			preparedStatement.setInt( 1, projectId );
			try ( ResultSet rs = preparedStatement.executeQuery() ) {
				if ( rs.next() ) {
					result = true;
				}
			}
		} catch ( Exception e ) {
			log.error( "error running SQL: " + querySQL, e );
			throw e;
		}
		
		return result;
	}
	
}
