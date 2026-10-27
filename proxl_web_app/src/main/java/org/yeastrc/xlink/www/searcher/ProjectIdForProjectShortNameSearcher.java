package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 * 
 *
 */
public class ProjectIdForProjectShortNameSearcher {
	
	private static final Logger log = LoggerFactory.getLogger( ProjectIdForProjectShortNameSearcher.class );
	private ProjectIdForProjectShortNameSearcher() { }
	private static final ProjectIdForProjectShortNameSearcher _INSTANCE = new ProjectIdForProjectShortNameSearcher();
	public static ProjectIdForProjectShortNameSearcher getInstance() { return _INSTANCE; }
	

	private static final String QUERY_SQL = "SELECT id "
			+ " FROM project "
			+ " WHERE "
			+ " short_name = ? ";
			
	/**
	 * @param projectShortName
	 * @return
	 * @throws SQLException
	 */
	public Integer getProjectIdForProjectShortName( String projectShortName ) throws Exception {

		Integer result = null;

		final String querySQL = QUERY_SQL;
		
		try ( Connection connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			     PreparedStatement preparedStatement = connection.prepareStatement( querySQL ) ) {
			
			preparedStatement.setString( 1, projectShortName );
			
			try ( ResultSet rs = preparedStatement.executeQuery() ) {
				if ( rs.next() ) {
					result = rs.getInt( "id" );
				}
			}
		} catch ( Exception e ) {
			log.error( "error running SQL: " + querySQL, e );
			throw e;
		}
		
		return result;
	}

}
