package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;

/**
 * Table annotation_type_filterable
 *
 */
public class AnnotationTypeFilterableDAO {
	
	private static final Logger log = Logger.getLogger(AnnotationTypeFilterableDAO.class);

	private AnnotationTypeFilterableDAO() { }
	public static AnnotationTypeFilterableDAO getInstance() { return new AnnotationTypeFilterableDAO(); }
	
	/**
	 * Get the given annotation_type_filterable from the database
	 * 
	 * @param annotation_type_id
	 * @return
	 * @throws Exception
	 */
	public AnnotationTypeFilterableDTO getItem( int annotation_type_id ) throws Exception {
		
		AnnotationTypeFilterableDTO item = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM annotation_type_filterable WHERE annotation_type_id = ?";


		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, annotation_type_id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				item = populateFromResultSet( rs );
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
		
		
		return item;
	}
	
	
	/**
	 * Populate object from result set
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public AnnotationTypeFilterableDTO populateFromResultSet(	ResultSet rs) throws SQLException {
		
		
		AnnotationTypeFilterableDTO item;
		item = new AnnotationTypeFilterableDTO();
		
		item.setAnnotationTypeId( rs.getInt( "annotation_type_id" ) );
		
		String filterDirectionString = rs.getString( "filter_direction" );
		FilterDirectionType filterDirectionType = FilterDirectionType.fromValue( filterDirectionString );
		item.setFilterDirectionType( filterDirectionType );

		
		int defaultFilterInt = rs.getInt( "default_filter" );
		
		if ( Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE == defaultFilterInt ) {
			item.setDefaultFilter( false );
		} else {
			item.setDefaultFilter( true );
		}
		

		double defaultFilterValue = rs.getDouble( "default_filter_value" );
		if ( ! rs.wasNull() ) {
			
			item.setDefaultFilterValue( defaultFilterValue );
		}
		
		item.setDefaultFilterValueString( rs.getString( "default_filter_value_string" ) );
		
		

		//  Values when the record was first inserted into the DB

		int defaultFilterAtDatabaseLoadInt = rs.getInt( "default_filter_at_database_load" );
		
		if ( Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE == defaultFilterAtDatabaseLoadInt ) {
			item.setDefaultFilterAtDatabaseLoad( false );
		} else {
			item.setDefaultFilterAtDatabaseLoad( true );
		}


		double defaultFilterValueAtDatabaseLoad = rs.getDouble( "default_filter_value_at_database_load" );
		if ( ! rs.wasNull() ) {
			
			item.setDefaultFilterValueAtDatabaseLoad( defaultFilterValueAtDatabaseLoad );
		}
		
		item.setDefaultFilterValueStringAtDatabaseLoad( rs.getString( "default_filter_value_string_at_database_load" ) );
		
		

		int sortOrder = rs.getInt( "sort_order" );
		if ( ! rs.wasNull() ) {
			
			item.setSortOrder( sortOrder );
		}
		

		return item;
	}


	
	/**
	 * This will INSERT the given AnnotationTypeFilterableDTO into the database.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( AnnotationTypeFilterableDTO item ) throws Exception {
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			saveToDatabase( item, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
	}
		
	

	private final static String INSERT_SQL = 
			"INSERT INTO annotation_type_filterable "
			
			+ "( annotation_type_id, filter_direction, "
			+ 	" default_filter, "
			+ 	" default_filter_value, default_filter_value_string, sort_order,"
			+ 	" default_filter_at_database_load, default_filter_value_at_database_load,"
			+ 	" default_filter_value_string_at_database_load ) "
			
			+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";
	
		
	/**
	 * This will INSERT the given AnnotationTypeFilterableDTO into the database
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( AnnotationTypeFilterableDTO item, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		if ( log.isDebugEnabled() ) {
			
			log.debug( "Saving AnnotationTypeFilterableDTO item: " + item );
		}

		String sql = INSERT_SQL;

		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			

			counter++;
			pstmt.setInt( counter, item.getAnnotationTypeId() );
			
			
			counter++;
			pstmt.setString( counter, item.getFilterDirectionType().value() );
			
			counter++;
			if ( item.isDefaultFilter() ) {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE );
			} else {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE );
			}
			
			counter++;
			if ( item.getDefaultFilterValue() != null ) {
				pstmt.setDouble( counter, item.getDefaultFilterValue() );
			} else {
				pstmt.setNull( counter, java.sql.Types.DOUBLE );
			}

			counter++;
			pstmt.setString( counter, item.getDefaultFilterValueString() );
			

			counter++;
			if ( item.getSortOrder() != null ) {
				pstmt.setInt( counter, item.getSortOrder() );
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			}


			counter++;
			if ( item.isDefaultFilterAtDatabaseLoad() ) {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE );
			} else {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE );
			}
			
			counter++;
			if ( item.getDefaultFilterValueAtDatabaseLoad() != null ) {
				pstmt.setDouble( counter, item.getDefaultFilterValueAtDatabaseLoad() );
			} else {
				pstmt.setNull( counter, java.sql.Types.DOUBLE );
			}

			counter++;
			pstmt.setString( counter, item.getDefaultFilterValueStringAtDatabaseLoad() );
			

			pstmt.executeUpdate();
			
			
			
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
			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
			
		}
		
		
	}
	
}
