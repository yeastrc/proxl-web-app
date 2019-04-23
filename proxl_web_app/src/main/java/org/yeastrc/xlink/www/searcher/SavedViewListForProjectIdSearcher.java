package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.searcher_results.SavedViewListForProjectIdItem;

public class SavedViewListForProjectIdSearcher {

	private static final Log log = LogFactory.getLog(SavedViewListForProjectIdSearcher.class);
	private SavedViewListForProjectIdSearcher() { }
	private static final SavedViewListForProjectIdSearcher _INSTANCE = new SavedViewListForProjectIdSearcher();
	public static SavedViewListForProjectIdSearcher getInstance() { return _INSTANCE; }

	private static final String QUERY_SQL = 
			"SELECT "
			+ " id, label, url_start_at_page_name, auth_user_id_created_record "
			+ " FROM "
			+ " data_page_saved_view_tbl "
			+ " WHERE project_id = ? ";

	/**
	 * @param projectId
	 * @return
	 * @throws SQLException
	 */
	public List<SavedViewListForProjectIdItem>  getSavedViewListForProjectId( int projectId ) throws Exception {

		List<SavedViewListForProjectIdItem> resultList = new ArrayList<>();

		final String querySQL = QUERY_SQL;
				
		try ( Connection connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			     PreparedStatement preparedStatement = connection.prepareStatement( querySQL ) ) {
			
			preparedStatement.setInt( 1, projectId );
			try ( ResultSet rs = preparedStatement.executeQuery() ) {
				while ( rs.next() ) {
					SavedViewListForProjectIdItem item = new SavedViewListForProjectIdItem();
					item.setId( rs.getInt( "id" ) );
					item.setLabel( rs.getString( "label" ) );
					item.setUrl( rs.getString( "url_start_at_page_name" ) );
					item.setAuthUserIdCreated( rs.getInt( "auth_user_id_created_record" ) );
					resultList.add( item );
				}
			}
		} catch ( Exception e ) {
			log.error( "error running SQL: " + querySQL, e );
			throw e;
		}
		
		return resultList;
	}
	
}
